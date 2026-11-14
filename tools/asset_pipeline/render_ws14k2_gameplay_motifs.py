#!/usr/bin/env python3
"""WS-14K2: render three original separate SVG motifs to QA-only Android PNGs.

CairoSVG 2.8.2 is pinned by the two CI workflows. Nothing is committed into
release resources; all rendered bytes go under app/src/debug/assets.
"""
import hashlib
import json
import struct
from pathlib import Path
import xml.etree.ElementTree as ET

import cairosvg

ROOT = Path(__file__).resolve().parents[2]
SOURCE = ROOT / "design" / "visual-system" / "masters"
DEST = ROOT / "app" / "src" / "debug" / "assets" / "ws14k2" / "motifs"
THEMES = ("woodland", "space", "garden")
PNG_SIGNATURE = b"\x89PNG\r\n\x1a\n"


def digest(data: bytes) -> str:
    return hashlib.sha256(data).hexdigest()


def render() -> dict:
    DEST.mkdir(parents=True, exist_ok=True)
    records = {}
    for theme in THEMES:
        source = SOURCE / f"ws14k2-{theme}-gameplay-motif-v1.svg"
        raw = source.read_bytes()
        root = ET.fromstring(raw)
        if root.tag != "{http://www.w3.org/2000/svg}svg":
            raise ValueError(f"Not an SVG root: {source}")
        if root.attrib.get("viewBox") != "0 0 256 256":
            raise ValueError(f"Wrong canvas: {source}")
        prohibited = {"text", "image", "foreignObject", "script", "use"}
        for node in root.iter():
            local = node.tag.rsplit("}", 1)[-1]
            if local in prohibited:
                raise ValueError(f"Unsafe or text-bearing SVG node {local}: {source}")
        png = cairosvg.svg2png(bytestring=raw, output_width=256, output_height=256)
        if not png.startswith(PNG_SIGNATURE):
            raise ValueError(f"Not PNG: {source}")
        width, height, depth, colour = struct.unpack(">IIBB", png[16:26])
        if (width, height, depth, colour) != (256, 256, 8, 6):
            raise ValueError(f"Expected 256x256 RGBA: {(width, height, depth, colour)}")
        name = f"{theme}.png"
        (DEST / name).write_bytes(png)
        records[theme] = {
            "id": f"GM_WS14K2_{theme.upper()}_MOTIF_V1",
            "source_path": source.relative_to(ROOT).as_posix(),
            "source_sha256": digest(raw),
            "export_path": (DEST / name).relative_to(ROOT).as_posix(),
            "export_sha256": digest(png),
            "bytes": len(png),
            "width": 256, "height": 256,
            "transparency": True,
            "source_type": "original individually authored SVG",
            "approval": "QA_INTEGRATED_UNREVIEWED",
        }
    manifest = {
        "schema_version": 1,
        "build_source": "independent WS-14K2 SVG masters",
        "renderer": "CairoSVG==2.8.2",
        "debug_only": True,
        "release_approved": False,
        "assets": records,
    }
    (DEST / "manifest.json").write_text(
        json.dumps(manifest, indent=2, sort_keys=True) + "\n", encoding="utf-8"
    )
    print("Rendered WS-14K2 independent gameplay motifs:", {
        theme: (entry["bytes"], entry["export_sha256"][:12])
        for theme, entry in records.items()
    })
    return manifest


if __name__ == "__main__":
    render()
