#!/usr/bin/env python3
"""Copy the owner-review WS16BB independent shelf PNG into debug assets.
No release source files or UI/book text is changed. Source is hash pinned.
"""
import hashlib
import json
import shutil
from pathlib import Path
from PIL import Image

ROOT = Path(__file__).resolve().parents[2]
SOURCE = ROOT / "design/visual-system/masters/ws16bb/shelf-matched-qa.png"
OUTPUT = ROOT / "app/src/debug/assets/ws16bb"
EXPECTED_SHA = "691496fae9340fa4131d235abd11589cd2e9155384773d0887848bfde11c4c1e"
EXPECTED_SIZE = (586, 125)

def verify():
    raw = SOURCE.read_bytes()
    digest = hashlib.sha256(raw).hexdigest()
    if digest != EXPECTED_SHA:
        raise ValueError("WS16BB shelf source SHA mismatch")
    with Image.open(SOURCE) as im:
        if im.size != EXPECTED_SIZE or im.mode != "RGBA":
            raise ValueError("WS16BB shelf must be transparent 586x125 RGBA")
        alpha = im.getchannel("A")
        if alpha.getextrema()[0] > 0 or alpha.getextrema()[1] != 255:
            raise ValueError("WS16BB independent shelf lost its transparent contour")
    return {"source_sha256":digest, "width":586, "height":125,
            "qa_only":True, "release_approved":False,
            "on_device_approved":False, "books_or_text_baked_in":False}

def build():
    result = verify()
    OUTPUT.mkdir(parents=True, exist_ok=True)
    shutil.copyfile(SOURCE, OUTPUT / "shelf.png")
    (OUTPUT / "manifest.json").write_text(
        json.dumps(result, indent=2,sort_keys=True) + "\n")
    print("WS16BB matched transparent shelf imported for debug QA only")

if __name__ == "__main__":
    build()
