#!/usr/bin/env python3
"""WS-12I — Debug-only Android PNG sprites generated from exact WS-12F SVG masters.

No runtime SVG/WebView renderer or generated model calls in playable screens.
All 18 images are bundled offline. These are reversible QA placeholders, not
approved artwork or new reward/profile assets.
"""
import argparse
import hashlib
import json
from pathlib import Path

import cairosvg

SIZES = {
    "books": (144, 192),
    "spines": (40, 230),
    "emblems": (96, 96),
    "chapters": (96, 96),
    "bookmarks": (42, 135),
    "seals": (96, 96),
}
THEMES = ("woodland", "space", "garden")


def rasterize(source_dir: Path, output_dir: Path) -> dict:
    source_dir, output_dir = Path(source_dir), Path(output_dir)
    source_receipt = json.loads((source_dir / "render_sha256.json").read_text())
    if (source_receipt.get("art_file_count") != 18 or
            source_receipt.get("status") != "PROTOTYPE_NOT_OWNER_APPROVED" or
            source_receipt.get("owner_approved") is not False):
        raise ValueError("Only the 18-piece unapproved source art family is permitted")
    hashes = {}
    for theme in THEMES:
        for kind, (w, h) in SIZES.items():
            relative_svg = f"{kind}/{theme}.svg"
            original = source_dir / relative_svg
            if hashlib.sha256(original.read_bytes()).hexdigest() != (
                source_receipt["files"].get(relative_svg)
            ):
                raise ValueError(f"Unverified source sprite: {relative_svg}")
            dest = output_dir / kind / f"{theme}.png"
            dest.parent.mkdir(parents=True, exist_ok=True)
            cairosvg.svg2png(
                url=str(original), write_to=str(dest),
                output_width=w, output_height=h
            )
            png = dest.read_bytes()
            if png[:8] != b"\x89PNG\r\n\x1a\n" or len(png) < 100:
                raise ValueError(f"Raster output invalid: {dest}")
            hashes[f"{kind}/{theme}.png"] = {
                "sha256": hashlib.sha256(png).hexdigest(),
                "source_sha256": source_receipt["files"][relative_svg],
                "width": w, "height": h,
            }
    result = {
        "schema_version": 1,
        "purpose": "DEBUG_ONLY_PLAYABLE_BOOK_ART_PREVIEW",
        "owner_approved": False,
        "art_count": len(hashes),
        "art_system": source_receipt["style_system"],
        "art": dict(sorted(hashes.items())),
    }
    (output_dir / "manifest.json").write_text(
        json.dumps(result, sort_keys=True, indent=2) + "\n", encoding="utf-8"
    )
    return result


def main():
    p = argparse.ArgumentParser(description=__doc__)
    p.add_argument("--input-dir", required=True, type=Path)
    p.add_argument("--output-dir", required=True, type=Path)
    a = p.parse_args()
    r = rasterize(a.input_dir, a.output_dir)
    print(f"WS-12I: {r['art_count']} verified Android PNG prototypes, release unaffected")


if __name__ == "__main__":
    main()
