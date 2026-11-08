#!/usr/bin/env python3
"""WS-12E art-production queue gate.

This queue is planning and source-prototype tracking only. It must never turn
an art concept into an approved Android asset. Approved images use the separate
existing SHA-validated approved-assets.json registry and require human evidence.
"""
import argparse
import json
from pathlib import Path, PurePosixPath
import re
import sys

ID = re.compile(r"[a-z][a-z0-9-]{2,80}\Z")
SLOTS = {
    "cover", "spine", "theme-emblem", "chapter-marker",
    "bookmark", "completion-seal"
}
STATES = {"planned", "prototype", "human-review", "approved"}
COLLECTION_STATES = {"planned", "prototype", "human-review", "approved", "preserve-originals"}
LEGACY_PORTRAITS = "woodland-original-portraits"
REQUIRED_THEMES = {"woodland", "space", "garden"}


def load_json(root: Path, relative: str) -> dict:
    candidate = PurePosixPath(relative)
    if candidate.is_absolute() or any(p in {"..", "."} for p in candidate.parts):
        raise ValueError("Invalid reference path")
    return json.loads((root / Path(*candidate.parts)).read_text(encoding="utf-8"))


def audit_queue(root: Path) -> dict:
    q = load_json(root, "design/asset-pipeline/production-queue-v1.json")
    if q.get("schema_version") != 1 or q.get("library") != "gridmates-word-search":
        raise ValueError("Unsupported queue schema/library")
    if q.get("status") != "PLANNED_AND_PROTOTYPE_ONLY_NO_RELEASE_APPROVAL":
        raise ValueError("Queue may not claim release/owner approval")
    for field in ("token_source", "approved_registry", "style_bible", "production_playbook"):
        value = q.get(field)
        if not isinstance(value, str) or not value:
            raise ValueError(f"Missing {field}")
        relative = PurePosixPath(value)
        if relative.is_absolute() or any(p in {"..", "."} for p in relative.parts):
            raise ValueError("Unsafe source path")
        if not (root / Path(*relative.parts)).is_file():
            raise ValueError(f"Missing referenced {field}")
    tokens = load_json(root, q["token_source"])
    if q.get("art_system_id") != tokens.get("system_id"):
        raise ValueError("Queue disagrees with versioned art recipe")
    approved = load_json(root, q["approved_registry"])
    if approved.get("library") != "gridmates-word-search" or approved.get("schema_version") != 1:
        raise ValueError("Invalid approved-assets registry")
    approved_ids = {
        a["asset_id"] for a in approved.get("assets", [])
        if a.get("review_status") == "approved"
    }
    token_motifs = {t["id"]: t["motif"] for t in tokens.get("themes", [])}
    books = q.get("books", [])
    if not isinstance(books, list) or len(books) < 3:
        raise ValueError("Missing initial three book families")
    ids = set()
    asset_ids = set()
    slots_count = 0
    status_counts = {s: 0 for s in STATES}
    for book in books:
        theme_id = book.get("theme_id")
        if not isinstance(theme_id, str) or not ID.fullmatch(theme_id) or theme_id in ids:
            raise ValueError("Duplicate or invalid theme")
        ids.add(theme_id)
        if token_motifs.get(theme_id) != book.get("motif"):
            raise ValueError(f"{theme_id}: style motif doesn't match token source")
        if book.get("review_status") != "prototype-family-unapproved":
            raise ValueError(f"{theme_id}: no family may claim final approval")
        if book.get("approval_evidence") is not None:
            raise ValueError(f"{theme_id}: fabricated approval evidence")
        if not isinstance(book.get("priority"), int) or book["priority"] < 1:
            raise ValueError(f"{theme_id}: invalid review priority")
        assets = book.get("assets", [])
        if not isinstance(assets, list) or len(assets) != len(SLOTS):
            raise ValueError(f"{theme_id}: missing required coherent asset slots")
        kinds = set()
        for asset in assets:
            kind = asset.get("kind")
            identity = asset.get("asset_id")
            if kind not in SLOTS or kind in kinds:
                raise ValueError(f"{theme_id}: missing or duplicate family item")
            kinds.add(kind)
            if identity != f"gmws-{theme_id}-{kind}-v1" or identity in asset_ids:
                raise ValueError("Asset identity changed or duplicated")
            asset_ids.add(identity)
            state = asset.get("status")
            if state not in STATES:
                raise ValueError(f"{identity}: invalid status")
            status_counts[state] += 1
            if not isinstance(asset.get("target_preview_dp"), int) or asset["target_preview_dp"] < 24:
                raise ValueError(f"{identity}: invalid display size")
            if asset.get("native_text_required") is not True:
                raise ValueError(f"{identity}: product text must stay native UI")
            if state == "approved":
                if asset.get("owner_approved") is not True or identity not in approved_ids:
                    raise ValueError(f"{identity}: can't approve without real signed manifest")
            else:
                if asset.get("owner_approved") is not False or identity in approved_ids:
                    raise ValueError(f"{identity}: queue/approved registry disagree")
            renderer = asset.get("source_renderer")
            if state == "prototype":
                if not isinstance(renderer, str) or not renderer.startswith("tools/asset_pipeline/"):
                    raise ValueError(f"{identity}: prototype must link to an existing renderer")
                reference = PurePosixPath(renderer)
                if ".." in reference.parts or not (root / Path(*reference.parts)).is_file():
                    raise ValueError(f"{identity}: missing prototype source")
            elif state == "planned" and renderer is not None:
                raise ValueError(f"{identity}: planned item cannot pretend a prototype exists")
            if not isinstance(asset.get("next_gate"), str) or not asset["next_gate"].strip():
                raise ValueError(f"{identity}: missing next practical review step")
        if kinds != SLOTS:
            raise ValueError(f"{theme_id}: incomplete asset family")
        slots_count += len(assets)

    if not REQUIRED_THEMES.issubset(ids):
        raise ValueError("Woodland, Space and Garden pilot families required")

    collections = q.get("collections", [])
    if not isinstance(collections, list) or not collections:
        raise ValueError("Missing cosmetic collection workstreams")
    cids = set()
    for item in collections:
        cid = item.get("family_id")
        if not isinstance(cid, str) or not ID.fullmatch(cid) or cid in cids:
            raise ValueError("Duplicate or invalid collection workstream")
        cids.add(cid)
        if item.get("status") not in COLLECTION_STATES or not item.get("policy"):
            raise ValueError(f"{cid}: missing review status or design rule")
        if item.get("status") == "approved":
            raise ValueError(f"{cid}: collection family cannot self-approve")
    if LEGACY_PORTRAITS not in cids or next(
        i["status"] for i in collections if i["family_id"] == LEGACY_PORTRAITS
    ) != "preserve-originals":
        raise ValueError("Original Woodland portraits must remain frozen")

    gates = q.get("release_gates", [])
    if not isinstance(gates, list) or len(set(gates)) != len(gates) or len(gates) < 6:
        raise ValueError("Missing distinct human/technical release gates")
    if not {"owner-visual-approval", "source-and-rights", "android-real-device",
            "old-portraits-and-saves-unchanged"}.issubset(gates):
        raise ValueError("Owner, legal, device and legacy protection gates are mandatory")

    return {
        "status": "QUEUE_ONLY_NO_OWNER_APPROVAL",
        "art_system": tokens["system_id"],
        "book_families": len(books),
        "asset_slots": slots_count,
        "collections": len(collections),
        "slot_statuses": status_counts,
        "approved_ids_reference_count": len(approved_ids),
    }


def main() -> None:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--repo-root", type=Path, default=Path("."))
    args = parser.parse_args()
    try:
        print(json.dumps(audit_queue(args.repo_root), sort_keys=True, indent=2))
    except (OSError, ValueError, KeyError, json.JSONDecodeError) as exc:
        print(f"FAIL WS-12E art queue: {exc}", file=sys.stderr)
        raise SystemExit(1)
    print("PASS: coherent planned families, unchanged art recipes, no silent approvals.")


if __name__ == "__main__":
    main()
