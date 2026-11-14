#!/usr/bin/env python3
"""WS16H: reproducible 6-sprite QA rendering from original editable SVG art."""
import hashlib
import json
import struct
from pathlib import Path
import xml.etree.ElementTree as ET
import cairosvg

ROOT = Path(__file__).resolve().parents[2]
SOURCE = ROOT / "design/visual-system/masters/ws16h-home"
DEST = ROOT / "app/src/debug/assets/ws16h/actions"
NAMES = ("continue", "daily", "together", "quick", "full-library", "collection")
FORBIDDEN = {"text", "image", "foreignObject", "script", "use"}
def sha256(data):
    return hashlib.sha256(data).hexdigest()

def render():
    DEST.mkdir(parents=True, exist_ok=True)
    records = {}
    for name in NAMES:
        path = SOURCE / (name + ".svg")
        raw = path.read_bytes()
        root = ET.fromstring(raw)
        if root.tag != "{http://www.w3.org/2000/svg}svg":
            raise ValueError("Not a standalone SVG: " + name)
        for node in root.iter():
            local = node.tag.rsplit("}", 1)[-1]
            if local in FORBIDDEN:
                raise ValueError("Baked text/external asset in SVG: " + name)
            if any(k.rsplit("}",1)[-1] in ("href", "src") for k in node.attrib):
                raise ValueError("External references forbidden: " + name)
        if root.get("viewBox") != "0 0 512 512":
            raise ValueError("Unexpected artwork canvas: " + name)
        png = cairosvg.svg2png(bytestring=raw, output_width=256, output_height=256)
        if not png.startswith(b"\x89PNG\r\n\x1a\n"):
            raise ValueError("Invalid PNG: " + name)
        width, height, depth, colour = struct.unpack(">IIBB",png[16:26])
        if (width,height,depth,colour) != (256,256,8,6):
            raise ValueError("Not 256px RGBA for " + name)
        out = DEST / (name + ".png")
        out.write_bytes(png)
        records[name] = {
            "source": path.relative_to(ROOT).as_posix(),
            "source_sha256": sha256(raw),
            "debug_asset": out.relative_to(ROOT).as_posix(),
            "export_sha256": sha256(png), "bytes": len(png),
            "width": width, "height": height,
            "status": "QA_ONLY_OWNER_UNREVIEWED",
        }
    result = {"schema_version":1, "renderer":"CairoSVG==2.8.2",
        "debug_only":True,"owner_approved":False,"release_approved":False,
        "sprites":records}
    (DEST / "manifest.json").write_text(json.dumps(result,indent=2,sort_keys=True)+"\n")
    print("WS16H illustrated QA action icons rendered:",len(records))
    return result

if __name__ == "__main__":
    render()
