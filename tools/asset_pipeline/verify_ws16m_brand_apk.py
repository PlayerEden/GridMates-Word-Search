#!/usr/bin/env python3
"""Check source-derived WS16M brand sprites in debug APK, never release."""
import sys,zipfile,hashlib,json
from pathlib import Path
ROOT=Path(__file__).resolve().parents[2]
OUT=ROOT/"app/src/debug/assets/ws16m/brand"
rec=json.loads((OUT/"manifest.json").read_text())
assert len(rec["sprites"])==2 and rec["debug_only"] and not rec["release_approved"]
expect={item["apk_path"]:item["export_sha256"] for item in rec["sprites"].values()}
expect["assets/ws16m/brand/manifest.json"]=hashlib.sha256((OUT/"manifest.json").read_bytes()).hexdigest()
with zipfile.ZipFile(sys.argv[1]) as apk:
    found={x for x in apk.namelist() if x.startswith("assets/ws16m/")}
    assert found==set(expect), (found,set(expect))
    for name,checksum in expect.items():
        assert hashlib.sha256(apk.read(name)).hexdigest()==checksum,name
if len(sys.argv)>2:
    with zipfile.ZipFile(sys.argv[2]) as apk:
        assert not any(x.startswith("assets/ws16m/") for x in apk.namelist()),"WS16M unapproved art leaked into release"
print("WS16M: 2 independent hash-pinned QA emblems verified; release isolated:",len(sys.argv)>2)
