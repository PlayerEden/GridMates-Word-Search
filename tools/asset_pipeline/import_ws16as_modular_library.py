#!/usr/bin/env python3
"""WS16AS/AT: import only approved candidate ARCHITECTURE from external art kit.

This tool deliberately does not fabricate art, infer phone approval, import
shelves, or write into the release source set. Every source PNG is hash pinned
to its own manifest, and all five parts must reconstruct the empty original.
Run: python tools/asset_pipeline/import_ws16as_modular_library.py \
    --kit /path/to/WS16AS_Modular_Library_Art_Kit.zip [--verify-only]
"""
import argparse
import hashlib
import io
import json
from pathlib import Path
from zipfile import ZipFile

from PIL import Image

ROOT = Path(__file__).resolve().parents[2]
OUTPUT = ROOT / "app/src/debug/assets/ws16as/shell"
EXPECTED = {
    "top_crown": (602, 198),
    "left_pilaster": (126, 814),
    "recessed_backboard": (356, 814),
    "right_pilaster": (120, 814),
    "bottom_base": (602, 108),
}
OFFSETS = {
    "top_crown": (0, 0),
    "left_pilaster": (0, 198),
    "recessed_backboard": (126, 198),
    "right_pilaster": (482, 198),
    "bottom_base": (0, 1012),
}
REQUIRED_FLAGS = (
    "books_baked_into_shell",
    "shelves_baked_into_shell",
    "plaques_baked_into_shell",
    "ui_text_baked_into_shell",
)

def sha(data: bytes) -> str:
    return hashlib.sha256(data).hexdigest()

def read_one(z: ZipFile, name: str) -> bytes:
    possible = [n for n in z.namelist() if n == name or n.endswith("/" + name)]
    if len(possible) != 1:
        raise ValueError("Expected exactly one " + name + " in art kit")
    return z.read(possible[0])

def import_kit(kit: Path, verify_only: bool = False) -> dict:
    with ZipFile(kit) as z:
        meta = json.loads(read_one(z, "manifest.json"))
        if meta.get("schema") != "ws16as.modular-art.v1":
            raise ValueError("Unsupported candidate schema")
        if meta.get("on_device_tested") is not False:
            raise ValueError("Candidate must not claim on-device approval")
        if any(meta.get(key) is not False for key in REQUIRED_FLAGS):
            raise ValueError("Bookcase master is not empty and modular")
        raw = read_one(z, "original_candidate_sheet.png")
        if sha(raw) != meta["source_sheet_sha256"]:
            raise ValueError("Original sheet SHA mismatch")
        source = Image.open(io.BytesIO(raw)).convert("RGBA")
        if source.size != (1017, 1547):
            raise ValueError("Unexpected master dimensions")
        composite = Image.new("RGBA", (602, 1120))
        exports = {}
        for name, dims in EXPECTED.items():
            item = meta["shell_parts"][name]
            path = item["filename"]
            if path != "assets/shell/" + name + ".png":
                raise ValueError("Mismatched shell asset path: " + path)
            data = read_one(z, path)
            if sha(data) != item["sha256"]:
                raise ValueError("Asset SHA mismatch: " + name)
            im = Image.open(io.BytesIO(data)).convert("RGBA")
            if im.size != dims:
                raise ValueError("Incorrect pixel bounds: " + name)
            composite.paste(im, OFFSETS[name])
            exports[name + ".png"] = data
        if composite.tobytes() != source.crop((0, 0, 602, 1120)).tobytes():
            raise ValueError("Five parts do not reconstruct original empty cabinet")
        manifest = {
            "schema": "ws16at.qa-only.v1",
            "qa_only": True,
            "release_approved": False,
            "approved_for_phone_testing": False,
            "source_sheet_sha256": meta["source_sheet_sha256"],
            "assets": {k: sha(v) for k, v in sorted(exports.items())},
        }
        if not verify_only:
            OUTPUT.mkdir(parents=True, exist_ok=True)
            for name, data in exports.items():
                (OUTPUT / name).write_bytes(data)
            (OUTPUT / "manifest.json").write_text(
                json.dumps(manifest, indent=2, sort_keys=True) + "\n"
            )
        return manifest

if __name__ == "__main__":
    p = argparse.ArgumentParser()
    p.add_argument("--kit", type=Path, required=True,
                   help="Real conversation-produced WS16AS ZIP")
    p.add_argument("--verify-only", action="store_true")
    args = p.parse_args()
    result = import_kit(args.kit, args.verify_only)
    print("WS16AS art:", len(result["assets"]),
          "five-piece empty cabinet verified",
          "(verify-only)" if args.verify_only else "(debug-only import)")
