#!/usr/bin/env python3
"""Render eight independently authored WS16G SVG shelf parts into debug-only Android PNGs."""
import hashlib
import json
import struct
from pathlib import Path
import xml.etree.ElementTree as ET
import cairosvg

ROOT = Path(__file__).resolve().parents[2]
SOURCE = ROOT / "design/visual-system/masters/ws16g-batch1"
DEST = ROOT / "app/src/debug/assets/ws16g/shelf"
REGISTER = ROOT / "design/visual-system/modular/ws16g-batch1-production-register.json"
ITEMS = {
    "WS_SHELF_TOP_V1": ("ws16g-shelf-top-v1.svg", "top.png", (800, 72)),
    "WS_SHELF_FRONT_V1": ("ws16g-shelf-front-v1.svg", "front.png", (800, 136)),
    "WS_SHELF_APRON_CARVED_V1": ("ws16g-shelf-apron-v1.svg", "apron.png", (800, 152)),
    "WS_SHELF_BRACKET_LEFT_V1": ("ws16g-shelf-bracket-left-v1.svg", "bracket-left.png", (160, 230)),
    "WS_SHELF_BRACKET_RIGHT_V1": ("ws16g-shelf-bracket-right-v1.svg", "bracket-right.png", (160, 230)),
    "WS_SHELF_CENTER_ORNAMENT_V1": ("ws16g-shelf-medallion-v1.svg", "medallion.png", (400, 108)),
    "WS_SHELF_CONTACT_SHADOW_V1": ("ws16g-shelf-contact-shadow-v1.svg", "contact-shadow.png", (640, 64)),
    "WS_BOOKCASE_BACKBOARD_V1": ("ws16g-bookcase-backboard-v1.svg", "backboard.png", (480, 360)),
}
FORBIDDEN = {"text", "image", "foreignObject", "script", "use"}
def sha(b):
    return hashlib.sha256(b).hexdigest()

def render():
    DEST.mkdir(parents=True, exist_ok=True)
    refs = {r["id"]: r for r in json.loads(REGISTER.read_text())["items"]}
    records = {}
    for ident, (filename, outname, (width, height)) in ITEMS.items():
        source = SOURCE / filename
        raw = source.read_bytes()
        expected = refs[ident]["new_svg_sha256"]
        assert sha(raw) == expected, ("Unexpected source modification", filename)
        root = ET.fromstring(raw)
        assert root.tag == "{http://www.w3.org/2000/svg}svg"
        assert not any(n.tag.rsplit("}",1)[-1] in FORBIDDEN for n in root.iter())
        png = cairosvg.svg2png(bytestring=raw, output_width=width, output_height=height)
        assert png.startswith(b"\x89PNG\r\n\x1a\n")
        assert struct.unpack(">II",png[16:24]) == (width, height)
        (DEST / outname).write_bytes(png)
        records[outname] = dict(id=ident, source_sha256=expected,
            export_sha256=sha(png), width=width, height=height, bytes=len(png))
    result = dict(schema_version=1, debug_only=True, owner_approved=False,
        release_approved=False, renderer="CairoSVG==2.8.2", items=records)
    (DEST / "manifest.json").write_text(json.dumps(result,sort_keys=True,indent=2)+"\n")
    print("WS16G shelf renders validated:",len(records))
    return result

if __name__ == "__main__":
    render()
