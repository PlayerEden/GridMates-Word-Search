#!/usr/bin/env python3
"""WS-14A: fail-closed asset source/export integrity and approval check.

This validates the **actual** manifest and files. It never treats a concept
image, an empty registry or a passing Android build as owner art approval.
No network, model call or third-party Python packages are required.
"""
from __future__ import annotations
import hashlib
import json
from pathlib import Path
import re
import sys

DEFAULT = "design/visual-system/ws14-art-register.json"
STATES = {
    "PLANNED", "GENERATED_UNREVIEWED", "QA_INTEGRATED",
    "OWNER_APPROVED", "RELEASE_APPROVED",
}
SHA = re.compile(r"^[a-f0-9]{64}$")
ID = re.compile(r"^[A-Z][A-Z0-9_]{4,95}$")


def _file(root: Path, name: str) -> Path:
    if not isinstance(name, str) or not name:
        raise ValueError("asset path missing")
    path = Path(name)
    if path.is_absolute() or ".." in path.parts or "\\" in name:
        raise ValueError("asset path escapes repository: " + name)
    resolved = (root / path).resolve()
    if not resolved.is_relative_to(root.resolve()):
        raise ValueError("asset path leaves repository")
    return resolved


def _verify(root: Path, item: dict, field: str) -> None:
    path = _file(root, item.get("path"))
    expected = item.get("sha256", "")
    if not isinstance(expected, str) or not SHA.fullmatch(expected):
        raise ValueError(field + ": actual SHA-256 receipt required")
    if not path.is_file():
        raise ValueError(field + ": referenced file does not exist: " + str(path))
    actual = hashlib.sha256(path.read_bytes()).hexdigest()
    if actual != expected:
        raise ValueError(field + ": source/export SHA-256 mismatch")


def verify_registry(root: Path, manifest: Path) -> int:
    data = json.loads(manifest.read_text(encoding="utf-8"))
    if data.get("schema_version") != 1 or data.get("visual_system") != (
        "gridmates-word-search-cozy-paperback"
    ):
        raise ValueError("unrecognized version or visual system")
    assets = data.get("assets")
    if not isinstance(assets, list):
        raise ValueError("assets must be an array")
    seen = set()
    for asset in assets:
        if not isinstance(asset, dict):
            raise ValueError("asset entry must be an object")
        aid = asset.get("asset_id")
        if not isinstance(aid, str) or not ID.fullmatch(aid) or aid in seen:
            raise ValueError("invalid or duplicate asset ID")
        seen.add(aid)
        status = asset.get("status")
        if status not in STATES:
            raise ValueError(aid + ": invalid approval state")
        if not asset.get("family") or not asset.get("purpose"):
            raise ValueError(aid + ": missing family/purpose")
        source = asset.get("source")
        exports = asset.get("exports", [])
        if source is not None:
            _verify(root, source, aid + " source")
        if status in ("QA_INTEGRATED", "OWNER_APPROVED", "RELEASE_APPROVED"):
            if not source or not exports:
                raise ValueError(aid + ": integrated art requires source and exports")
        if not isinstance(exports, list):
            raise ValueError(aid + ": exports must be a list")
        if status in ("OWNER_APPROVED", "RELEASE_APPROVED"):
            review = asset.get("owner_review")
            if not isinstance(review, dict) or not all(
                review.get(k) for k in ("reviewed_by", "reviewed_date", "approval_reference")
            ):
                raise ValueError(aid + ": owner approval evidence missing")
        for entry in exports:
            if not isinstance(entry, dict):
                raise ValueError(aid + ": malformed export")
            _verify(root, entry, aid + " export")
            if entry.get("android_source_set") not in ("debug", "release"):
                raise ValueError(aid + ": unknown Android source set")
            if entry.get("pixel_width", 0) <= 0 or entry.get("pixel_height", 0) <= 0:
                raise ValueError(aid + ": export image dimensions missing")
            if entry.get("format") not in ("png", "webp", "xml-vector", "9-patch-png"):
                raise ValueError(aid + ": unsupported Android export format")
            if entry["android_source_set"] == "release":
                if status != "RELEASE_APPROVED":
                    raise ValueError(aid + ": unapproved art must never enter release")
                if source.get("rights_status") != "DOCUMENTED":
                    raise ValueError(aid + ": release art rights not documented")
    return len(assets)


def main():
    root = Path(__file__).resolve().parents[2]
    manifest = Path(sys.argv[1]) if len(sys.argv) > 1 else root / DEFAULT
    n = verify_registry(root, manifest)
    print(f"WS-14 asset register OK: {n} actual source-backed entries "
          "(0 is intentional until illustration review)")


if __name__ == "__main__":
    main()
