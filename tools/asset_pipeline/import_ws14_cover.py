#!/usr/bin/env python3
"""Import owner-preferred original art into QA only with exact SHA receipts.

Requires local original master and 3:4 PNG export. Never downloads previews,
creates images, approves commercial rights or writes release assets.
"""
from __future__ import annotations
import argparse
import hashlib
import json
from pathlib import Path
import re
import shutil
import struct
import zlib

THEMES = ("woodland", "space", "garden")
SHA = re.compile(r"^[0-9a-f]{64}$")
PNG = b"\x89PNG\r\n\x1a\n"


def checked_path(root: Path, supplied: str, expected_prefix: str) -> Path:
    if not supplied or "\\" in supplied:
        raise ValueError("invalid path")
    rel = Path(supplied)
    if rel.is_absolute() or ".." in rel.parts or not rel.as_posix().startswith(expected_prefix + "/"):
        raise ValueError("source/export must be in " + expected_prefix)
    if any(word in rel.as_posix().lower() for word in ("watermark", "thumbnail", "preview")):
        raise ValueError("watermarked or thumbnail preview is not a valid source")
    path = (root / rel).resolve()
    if not path.is_relative_to(root.resolve()) or not path.is_file():
        raise ValueError("file missing or escapes repository")
    return path


def sha(path: Path) -> str:
    return hashlib.sha256(path.read_bytes()).hexdigest()


def checked_hash(path: Path, expected: str) -> str:
    if not SHA.fullmatch(expected or "") or sha(path) != expected:
        raise ValueError("actual SHA-256 receipt mismatch")
    return expected


def png_size(path: Path) -> tuple[int, int, bool]:
    image = path.read_bytes()
    if not image.startswith(PNG):
        raise ValueError("export must be a real PNG image")
    position, dimensions, alpha, seen_end = 8, None, False, False
    while position + 12 <= len(image):
        length = struct.unpack(">I", image[position:position + 4])[0]
        if length > 50_000_000 or position + 12 + length > len(image):
            raise ValueError("truncated or excessive PNG chunk")
        tag = image[position + 4:position + 8]
        data = image[position + 8:position + 8 + length]
        crc = struct.unpack(">I", image[position + 8 + length:position + 12 + length])[0]
        if zlib.crc32(tag + data) != crc:
            raise ValueError("corrupt PNG chunk CRC")
        if tag == b"IHDR":
            if dimensions is not None or length != 13:
                raise ValueError("invalid PNG header")
            width, height = struct.unpack(">II", data[:8])
            dimensions = (width, height)
            alpha = data[9] in (4, 6)
        if tag == b"IEND":
            seen_end = True
            position += 12 + length
            break
        position += 12 + length
    if not seen_end or dimensions is None or position != len(image):
        raise ValueError("incomplete PNG structure")
    width, height = dimensions
    if width < 300 or height < 400 or width > 4096 or height > 4096:
        raise ValueError("export pixel dimensions outside book cover limits")
    if abs(width / height - .75) > .025:
        raise ValueError("cover must be near 3:4 portrait ratio")
    return width, height, alpha


def prepare(root: Path, theme: str, master_rel: str, export_rel: str,
            master_sha: str, export_sha: str, provenance: str) -> dict:
    if theme not in THEMES:
        raise ValueError("unsupported book theme")
    if not provenance.strip() or len(provenance.strip()) < 12:
        raise ValueError("specific original file provenance/rights review note required")
    master = checked_path(root, master_rel, "design/visual-system/masters")
    export = checked_path(root, export_rel, "design/visual-system/exports")
    checked_hash(master, master_sha)
    checked_hash(export, export_sha)
    width, height, alpha = png_size(export)
    manifest = root / "design/visual-system/ws14-art-register.json"
    doc = json.loads(manifest.read_text(encoding="utf-8"))
    if doc.get("schema_version") != 1 or doc.get("visual_system") != "gridmates-word-search-cozy-paperback":
        raise ValueError("unexpected registry schema")
    identifier = f"BOOK_{theme.upper()}_COVER_V1"
    if any(entry.get("asset_id") == identifier for entry in doc["assets"]):
        raise ValueError("asset ID already registered: no silent replacement")
    destination_rel = f"app/src/debug/assets/ws14/books/{theme}.png"
    destination = root / destination_rel
    if destination.exists():
        raise ValueError("QA cover already exists; no silent replacement")
    import sys
    sys.path.insert(0, str(root / "tools/asset_pipeline"))
    from validate_ws14_assets import verify_registry
    verify_registry(root, manifest)
    destination.parent.mkdir(parents=True, exist_ok=True)
    shutil.copyfile(export, destination)
    if sha(destination) != export_sha:
        destination.unlink(missing_ok=True)
        raise ValueError("copied export checksum mismatch")
    entry = {
        "asset_id": identifier, "family": theme, "purpose": "front-cover-art",
        "status": "QA_INTEGRATED",
        "source": {"path": master_rel, "sha256": master_sha,
                   "rights_status": "PENDING_REVIEW", "provenance_note": provenance.strip()},
        "exports": [{"path": destination_rel, "sha256": export_sha,
                     "android_source_set": "debug", "format": "png",
                     "pixel_width": width, "pixel_height": height, "alpha": alpha}],
    }
    doc["assets"].append(entry)
    try:
        manifest.write_text(json.dumps(doc, indent=2) + "\n", encoding="utf-8")
        verify_registry(root, manifest)
    except Exception:
        doc["assets"].pop()
        manifest.write_text(json.dumps(doc, indent=2) + "\n", encoding="utf-8")
        destination.unlink(missing_ok=True)
        raise
    return entry


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--root", type=Path, default=Path(__file__).resolve().parents[2])
    parser.add_argument("--theme", choices=THEMES, required=True)
    parser.add_argument("--master", required=True)
    parser.add_argument("--export", required=True)
    parser.add_argument("--master-sha", required=True)
    parser.add_argument("--export-sha", required=True)
    parser.add_argument("--provenance", required=True)
    args = parser.parse_args()
    result = prepare(args.root, args.theme, args.master, args.export,
                     args.master_sha, args.export_sha, args.provenance)
    print("QA only (not owner/release approved):", result["asset_id"])


if __name__ == "__main__":
    main()
