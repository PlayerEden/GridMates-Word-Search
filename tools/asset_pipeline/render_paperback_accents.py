#!/usr/bin/env python3
"""WS-12D: deterministic, text-free companion accents for Quiet Paperbacks v1.

Generates chapter tiles, bookmark ribbons, and completion seals from the SAME
reviewed geometric motifs and exact four-color per-theme tokens as WS-12C.
This is a design proof; nothing is approved for, or copied into, Android.
"""
import argparse
import hashlib
import json
from pathlib import Path
import xml.etree.ElementTree as ET

from render_paperback import PALETTE, motif_markup, validate


def _motif(theme):
    return motif_markup(
        theme["motif"], theme["accent"], theme["accent_light"],
        theme["accent_dark"], theme["highlight"]
    )


def _symbol(theme, x, y, width, height):
    # Same source geometry and same crop for every theme; no letters/numbers.
    return (
        f'<svg x="{x}" y="{y}" width="{width}" height="{height}" '
        'viewBox="240 440 730 680" preserveAspectRatio="xMidYMid meet">'
        f'<g>{_motif(theme)}</g></svg>'
    )


def chapter_svg(theme):
    a = theme["accent"]
    light = theme["accent_light"]
    return (
        '<svg xmlns="http://www.w3.org/2000/svg" width="240" height="240" '
        'viewBox="0 0 240 240" aria-hidden="true">'
        f'<rect x="8" y="8" width="224" height="224" rx="30" fill="{PALETTE["paper"]}" '
        f'stroke="{PALETTE["rule"]}" stroke-width="6"/>'
        f'<rect x="24" y="24" width="192" height="192" rx="22" fill="{light}"/>'
        + _symbol(theme, 30, 34, 180, 174)
        + f'<path d="M48 202H192" stroke="{a}" stroke-width="6" stroke-linecap="round"/>'
        '</svg>'
    )


def bookmark_svg(theme):
    a = theme["accent"]
    light = theme["accent_light"]
    dark = theme["accent_dark"]
    return (
        '<svg xmlns="http://www.w3.org/2000/svg" width="112" height="360" '
        'viewBox="0 0 112 360" aria-hidden="true">'
        f'<path d="M8 10H104V338L56 298L8 338Z" fill="{PALETTE["paper"]}" '
        f'stroke="{a}" stroke-width="8" stroke-linejoin="round"/>'
        f'<path d="M18 20H94V296L56 268L18 296Z" fill="{light}"/>'
        + _symbol(theme, 16, 48, 80, 115)
        + f'<path d="M27 208H85" stroke="{dark}" stroke-width="5" stroke-linecap="round"/>'
        + f'<circle cx="56" cy="231" r="6" fill="{a}"/>'
        '</svg>'
    )


def seal_svg(theme):
    a = theme["accent"]
    light = theme["accent_light"]
    dark = theme["accent_dark"]
    return (
        '<svg xmlns="http://www.w3.org/2000/svg" width="300" height="300" '
        'viewBox="0 0 300 300" aria-hidden="true">'
        f'<circle cx="150" cy="150" r="143" fill="{PALETTE["paper"]}" '
        f'stroke="{a}" stroke-width="10"/>'
        f'<circle cx="150" cy="150" r="122" fill="{light}" '
        f'stroke="{PALETTE["gold"]}" stroke-width="4"/>'
        + _symbol(theme, 55, 51, 190, 185)
        + f'<path d="M92 244Q150 263 208 244" fill="none" '
        f'stroke="{dark}" stroke-width="6" stroke-linecap="round"/>'
        '</svg>'
    )


def render_accents(tokens, output):
    recipe = validate(json.loads(Path(tokens).read_text(encoding="utf-8")))
    output = Path(output)
    files = {}
    types = (
        ("chapters", chapter_svg),
        ("bookmarks", bookmark_svg),
        ("seals", seal_svg),
    )
    for theme in recipe["themes"]:
        for folder, painter in types:
            target = output / folder / (theme["id"] + ".svg")
            target.parent.mkdir(parents=True, exist_ok=True)
            svg = painter(theme)
            if any(bad in svg for bad in ("<text", "<script", "<foreignObject", "href=", "®", "™")):
                raise ValueError("Decorative assets cannot embed UI text, scripts or external resources")
            root = ET.fromstring(svg)
            if root.tag != "{http://www.w3.org/2000/svg}svg":
                raise ValueError("Expected valid SVG asset")
            target.write_text(svg, encoding="utf-8")
            files[target.relative_to(output).as_posix()] = hashlib.sha256(
                svg.encode("utf-8")
            ).hexdigest()
    receipt = {
        "system_id": recipe["system_id"],
        "status": recipe["status"],
        "theme_count": len(recipe["themes"]),
        "art_types": [name for name, _ in types],
        "text_baked_into_art": False,
        "owner_approved": False,
        "adobe_refined": False,
        "android_resources_modified": False,
        "files": dict(sorted(files.items())),
    }
    output.mkdir(parents=True, exist_ok=True)
    (output / "render_sha256.json").write_text(
        json.dumps(receipt, sort_keys=True, indent=2) + "\n", encoding="utf-8"
    )
    return receipt


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--tokens", default="design/asset-pipeline/paperback-v1-tokens.json")
    parser.add_argument("--output-dir", required=True)
    args = parser.parse_args()
    print(json.dumps(render_accents(args.tokens, args.output_dir), indent=2, sort_keys=True))


if __name__ == "__main__":
    main()
