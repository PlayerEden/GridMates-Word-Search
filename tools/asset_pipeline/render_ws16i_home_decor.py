#!/usr/bin/env python3
"""WS16I: render five independent, text-free botanical Home accents to debug-only PNG."""
import hashlib
import json
import struct
from pathlib import Path
import xml.etree.ElementTree as ET
import cairosvg

ROOT = Path(__file__).resolve().parents[2]
SOURCE = ROOT / "design/visual-system/masters/ws16i-home-decor"
DEST = ROOT / "app/src/debug/assets/ws16i/decor"
SIZES = {
    "top-left": (512, 350),
    "top-right": (512, 350),
    "bottom-left": (512, 280),
    "bottom-right": (512, 280),
    "divider": (1024, 86),
}
BAD = {"script", "image", "text", "foreignObject", "use"}
def digest(content):
    return hashlib.sha256(content).hexdigest()

def render():
    DEST.mkdir(parents=True, exist_ok=True)
    manifest = {}
    for name, dims in SIZES.items():
        path = SOURCE / (name + ".svg")
        raw = path.read_bytes()
        root = ET.fromstring(raw)
        if root.tag != "{http://www.w3.org/2000/svg}svg":
            raise ValueError("Bad SVG " + name)
        for node in root.iter():
            if node.tag.rsplit("}",1)[-1] in BAD:
                raise ValueError("Unapproved SVG element in " + name)
            if any(k.rsplit("}",1)[-1] in {"href", "src"} for k in node.attrib):
                raise ValueError("External SVG asset " + name)
        png = cairosvg.svg2png(bytestring=raw,
            output_width=dims[0],output_height=dims[1])
        if png[:8] != b"\x89PNG\r\n\x1a\n":
            raise ValueError("PNG signature failed " + name)
        width,height,depth,colour=struct.unpack(">IIBB",png[16:26])
        if (width,height,depth,colour) != (*dims,8,6):
            raise ValueError("Not RGBA at correct dimensions: " + name)
        (DEST / (name + ".png")).write_bytes(png)
        manifest[name] = {
            "source": path.relative_to(ROOT).as_posix(),
            "source_sha256": digest(raw),
            "export_sha256":digest(png),
            "width":width,"height":height,"format":"PNG RGBA",
            "bytes":len(png)
        }
    result = {"schema_version":1,"scope":"WS16I_HOME_DECOR_ONLY",
        "debug_only":True,"owner_approved":False,"release_approved":False,
        "renderer":"CairoSVG==2.8.2","sprites":manifest}
    (DEST/"manifest.json").write_text(json.dumps(result,sort_keys=True,indent=2)+"\n")
    print("WS16I exported alpha ornaments",len(manifest))
    return result

if __name__=="__main__":
    render()
