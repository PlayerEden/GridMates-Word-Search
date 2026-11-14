#!/usr/bin/env python3
"""WS16AA: pin original illustration masters and rasterize into QA-only Android sprites."""
import hashlib, json, struct
from pathlib import Path
import xml.etree.ElementTree as ET
import cairosvg

ROOT = Path(__file__).resolve().parents[2]
REGISTER = ROOT / "design/visual-system/ws16aa-art-register.json"
OUTPUT = ROOT / "app/src/debug/assets/ws16aa"
PROHIBITED = {"image", "text", "foreignObject", "script", "use"}

def sha256(raw):
    return hashlib.sha256(raw).hexdigest()

def git_blob_sha1(raw):
    return hashlib.sha1(b"blob " + str(len(raw)).encode("ascii") + b"\x00" + raw).hexdigest()

def render():
    manifest = json.loads(REGISTER.read_text(encoding="utf-8"))
    assert manifest["debug_only"] and not manifest["release_approved"]
    OUTPUT.mkdir(parents=True, exist_ok=True)
    receipt = {"schema_version": 1, "debug_only": True,
               "owner_approved": False, "release_approved": False, "items": {}}
    for item in manifest["assets"]:
        path = ROOT / item["path"]
        raw = path.read_bytes()
        assert git_blob_sha1(raw) == item["source_git_blob_sha1"], (
            "Versioned asset source bytes changed", path)
        root = ET.fromstring(raw)
        assert root.tag == "{http://www.w3.org/2000/svg}svg"
        assert not any(node.tag.rsplit("}", 1)[-1] in PROHIBITED
                       for node in root.iter())
        width, height = item["export_width"], item["export_height"]
        png = cairosvg.svg2png(bytestring=raw,
                              output_width=width, output_height=height)
        assert png.startswith(b"\x89PNG\r\n\x1a\n")
        assert struct.unpack(">II", png[16:24]) == (width, height)
        target = OUTPUT / item["export_filename"]
        target.write_bytes(png)
        receipt["items"][item["export_filename"]] = {
            "id": item["id"],
            "source_path": item["path"],
            "source_git_blob_sha1": item["source_git_blob_sha1"],
            "source_sha256": sha256(raw),
            "export_sha256": sha256(png),
            "width": width,
            "height": height,
            "bytes": len(png)}
    (OUTPUT / "manifest.json").write_text(
        json.dumps(receipt, indent=2, sort_keys=True) + "\n", encoding="utf-8")
    print("WS16AA source-pinned painted assets rendered:", len(receipt["items"]))
    return receipt

if __name__ == "__main__":
    render()
