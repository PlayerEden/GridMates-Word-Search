#!/usr/bin/env python3
"""WS16M: deterministic raster of two ORIGINAL independent brand emblems.

Text remains native Compose for accessibility, localization, and scaling.
The emblem art is strictly unreviewed, debug-only and release-isolated.
"""
import hashlib
import json
import struct
from pathlib import Path
from xml.etree import ElementTree as ET
import cairosvg

ROOT = Path(__file__).resolve().parents[2]
SOURCE = ROOT / "design/visual-system/masters/ws16m-brand"
OUT = ROOT / "app/src/debug/assets/ws16m/brand"
NAMES = ("gridmates", "wordsearch")
BANNED = {"script", "image", "text", "foreignObject", "use"}

def sha(data):
    return hashlib.sha256(data).hexdigest()

def render():
    OUT.mkdir(parents=True, exist_ok=True)
    sprites = {}
    for name in NAMES:
        src = SOURCE / (name + ".svg")
        raw = src.read_bytes()
        document = ET.fromstring(raw)
        assert document.tag == "{http://www.w3.org/2000/svg}svg"
        assert document.get("viewBox") == "0 0 256 256"
        for tag in document.iter():
            assert tag.tag.rsplit("}", 1)[-1] not in BANNED, name
            assert not any(attr.rsplit("}", 1)[-1] in ("href","src")
                           for attr in tag.attrib), name
        png = cairosvg.svg2png(bytestring=raw, output_width=256, output_height=256)
        assert png.startswith(b"\x89PNG\r\n\x1a\n")
        assert struct.unpack(">IIBB", png[16:26]) == (256, 256, 8, 6)
        dest = OUT / (name + ".png")
        dest.write_bytes(png)
        sprites[name] = {
            "master": src.relative_to(ROOT).as_posix(),
            "source_sha256": sha(raw),
            "apk_path": "assets/ws16m/brand/" + name + ".png",
            "export_sha256": sha(png),
            "dimensions": [256,256],
            "bytes":len(png),
        }
    record = {
        "schema_version": 1,
        "milestone":"WS16M",
        "renderer":"CairoSVG==2.8.2",
        "source_art":"ORIGINAL_VECTOR_MASTERS",
        "debug_only":True,
        "owner_approved":False,
        "release_approved":False,
        "sprites":sprites,
    }
    (OUT/"manifest.json").write_text(json.dumps(record, indent=2, sort_keys=True)+"\n")
    print("WS16M: two independent brand PNGs rendered with source hashes.")
    return record

if __name__ == "__main__":
    render()
