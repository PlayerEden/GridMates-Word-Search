#!/usr/bin/env python3
"""WS-12B fail-closed preview/approved art audit.

The 3 generated cover concepts currently live in a user-delivered ZIP, not in
the repository or Android assets. SHA fingerprints are kept for later import.
A concept register is NOT an approved asset registry.

Production entries are only safe after exact binary SHA, human review, license
notice, source/edit provenance and explicit Android app resource verification.
"""
import argparse
from hashlib import sha256
import json
from pathlib import Path, PurePosixPath
import re
import sys

HASH = re.compile(r"[0-9a-f]{64}\Z")
ID = re.compile(r"[a-z][a-z0-9_-]{5,80}\Z")
THEMES = {"woodland", "space", "garden"}
STATES = {"concept", "shortlisted", "awaiting-rights",
          "awaiting-visual-review", "approved", "rejected", "retired"}
KINDS = {"book-cover", "book-spine", "chapter-marker", "theme-emblem",
         "completion-stamp", "bookmark", "shelf-decor", "profile-portrait",
         "collection-badge", "pencil-highlight", "page-border"}


def safe_relative(value: str, label: str) -> Path:
    if not value or value.startswith("/") or "\\" in value or ":" in value:
        raise ValueError(f"{label}: must be a normal repository-relative path")
    path = PurePosixPath(value)
    if path.is_absolute() or ".." in path.parts or "." in path.parts:
        raise ValueError(f"{label}: path traversal or ambiguous path")
    return Path(*path.parts)


def matching_file(root: Path, path: str, expected: str, label: str) -> None:
    relative = safe_relative(path, label)
    if not HASH.fullmatch(expected):
        raise ValueError(f"{label}: missing valid SHA-256")
    file = root / relative
    if not file.is_file():
        raise ValueError(f"{label}: referenced file isn't source-controlled")
    actual = sha256(file.read_bytes()).hexdigest()
    if actual != expected:
        raise ValueError(f"{label}: file content hash mismatch")


def check_concepts(concepts: dict, root: Path,
                   concept_dir: Path | None = None) -> dict:
    if concepts.get("schema_version") != 1:
        raise ValueError("Unsupported concept register version")
    if concepts.get("content_status") != "CONCEPTS_ONLY_UNAPPROVED_NOT_IN_ANDROID":
        raise ValueError("Concept register falsely claims game integration/approval")
    if concepts.get("concept_bundle_location") != "conversation-attachment-only":
        raise ValueError("Concept bundle location needs new reviewed proof")
    if not concepts.get("concept_bundle_filename", "").endswith(".zip"):
        raise ValueError("Missing concept ZIP filename")
    if len(concepts.get("source_concept_options", [])) != 2:
        raise ValueError("Expected 2 frozen creative directions")
    source_names = set()
    for x in concepts["source_concept_options"]:
        if x.get("status") != "concept" or not HASH.fullmatch(x.get("sha256", "")):
            raise ValueError("Source board needs unapproved status and hash")
        if x["filename"] in source_names:
            raise ValueError("Duplicate generated source board name")
        source_names.add(x["filename"])
        if concept_dir is not None:
            matching_file(concept_dir, x["filename"], x["sha256"], x["id"])
    books = concepts.get("book_concept_candidates", [])
    if len(books) != 3 or {x.get("theme") for x in books} != THEMES:
        raise ValueError("Pilot must contain exactly 3 distinct theme candidates")
    used_ids = set()
    for x in books:
        identity = x.get("asset_id", "")
        if not ID.fullmatch(identity) or identity in used_ids:
            raise ValueError("Duplicate or invalid concept ID")
        used_ids.add(identity)
        if (x.get("review_status") != "concept" or x.get("adobe_refined") is not False or
            x.get("owner_approved") is not False or
            x.get("exported_to_android") is not False):
            raise ValueError("Concept may not claim Adobe completion, owner approval or Android")
        if (x.get("display_text_baked_into_pixels") is not True or
            x.get("native_ui_text_required") is not True):
            raise ValueError("Generated concept lettering must not be approved as live UI")
        if x.get("volume") != "I" or not x.get("book_title_proposal"):
            raise ValueError("Missing proposed Vol I book title")
        b = x.get("crop_bbox")
        if not isinstance(b, list) or len(b) != 4 or not all(
                isinstance(v, int) and v >= 0 for v in b) or b[0] >= b[2] or b[1] >= b[3]:
            raise ValueError("Invalid concept crop box")
        for name, h in (("crop_filename", "crop_sha256"),
                        ("thumbnail_filename", "thumbnail_sha256")):
            if not HASH.fullmatch(x.get(h, "")):
                raise ValueError(f"{identity}: invalid concept output hash")
            if concept_dir is not None:
                matching_file(concept_dir, x[name], x[h], identity + "/" + name)
    return {"creative_directions": 2,
            "pilot_concepts": len(books),
            "concepts_approved": 0,
            "adobe_refinements": 0,
            "android_assets": 0,
            "binary_sources_verified": concept_dir is not None}


def check_approved(manifest: dict, root: Path) -> dict:
    if manifest.get("schema_version") != 1 or manifest.get("library") != "gridmates-word-search":
        raise ValueError("Unsupported approved asset manifest")
    entries = manifest.get("assets")
    if not isinstance(entries, list):
        raise ValueError("Approved manifest must contain assets array")
    ids = set()
    for asset in entries:
        identity = asset.get("asset_id", "")
        if not ID.fullmatch(identity) or identity in ids:
            raise ValueError("Duplicate or invalid approved asset ID")
        ids.add(identity)
        if asset.get("review_status") != "approved":
            raise ValueError(f"{identity}: unapproved art cannot be in production registry")
        if asset.get("kind") not in KINDS:
            raise ValueError(f"{identity}: unrecognized asset type")
        if not asset.get("theme_id") or not asset.get("accessible_label"):
            raise ValueError(f"{identity}: missing theme or accessible description")
        if not asset.get("editor_name") or not asset.get("approval_date") or not asset.get("approval_evidence"):
            raise ValueError(f"{identity}: human signoff and evidence required")
        if not re.fullmatch(r"\d{4}-\d{2}-\d{2}", asset["approval_date"]):
            raise ValueError(f"{identity}: invalid approval date")
        dimensions = asset.get("dimensions_px")
        if not isinstance(dimensions, dict) or not all(
            isinstance(dimensions.get(k), int) and dimensions[k] > 0 for k in ("width", "height")
        ):
            raise ValueError(f"{identity}: missing positive export dimensions")
        if not asset.get("license_id") or not asset.get("source_type"):
            raise ValueError(f"{identity}: missing use-rights source")
        if not asset.get("fallback_asset_id"):
            raise ValueError(f"{identity}: no missing-art fallback")
        history = asset.get("history")
        if not isinstance(history, list) or not history or any(
            not step.get("operation") or not step.get("tool") for step in history
        ):
            raise ValueError(f"{identity}: missing reproducible editing/tool provenance")
        source_rel = safe_relative(asset.get("source_file",""), identity+" source")
        export_rel = safe_relative(asset.get("export_file",""), identity+" export")
        if source_rel.parts[0] != "design":
            raise ValueError(f"{identity}: approved master must be in design source")
        if export_rel.parts[:3] != ("app", "src", "main"):
            raise ValueError(f"{identity}: approved export must live in Android app tree")
        matching_file(root, str(source_rel), asset.get("source_sha256", ""), identity+" source")
        matching_file(root, str(export_rel), asset.get("export_sha256", ""), identity+" export")
        if asset.get("source_type") == "licensed-third-party" and not asset.get("license_notice_path"):
            raise ValueError(f"{identity}: third-party asset requires license notice")
        if asset.get("license_notice_path"):
            notice = safe_relative(asset["license_notice_path"], identity+" notice")
            if not (root / notice).is_file():
                raise ValueError(f"{identity}: missing third-party notice")
    return {"production_approved_assets": len(entries)}


def audit(root: Path, concept_dir: Path | None = None) -> dict:
    pipeline = root / "design" / "asset-pipeline"
    concepts = json.loads((pipeline / "pilot-concept-register.json").read_text(encoding="utf8"))
    production = json.loads((pipeline / "approved-assets.json").read_text(encoding="utf8"))
    result = {"stage": "WS12A_B_PREVIEW_AND_APPROVED_REGISTRY_VALIDATION"}
    result.update(check_concepts(concepts, root, concept_dir))
    result.update(check_approved(production, root))
    return result


def main():
    p = argparse.ArgumentParser(description=__doc__)
    p.add_argument("--repo-root", type=Path, default=Path("."))
    p.add_argument("--concept-dir", type=Path,
                   help="Optional extracted concept ZIP folder for exact PNG/WebP SHA checks")
    a = p.parse_args()
    try:
        print(json.dumps(audit(a.repo_root, a.concept_dir), indent=2, sort_keys=True))
    except (OSError, ValueError, KeyError, json.JSONDecodeError) as exc:
        print(f"FAIL WS-12 asset audit: {exc}", file=sys.stderr)
        raise SystemExit(1)
    print("PASS: concepts cannot masquerade as approved, Adobe-refined or Android art.")


if __name__ == "__main__":
    main()
