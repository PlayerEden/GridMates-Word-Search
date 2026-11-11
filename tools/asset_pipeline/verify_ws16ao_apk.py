#!/usr/bin/env python3
"""Verify 12 generated WS16AO images against SHA manifest in debug and zero in release."""
import hashlib,json,sys,zipfile
from pathlib import Path
ROOT=Path(__file__).resolve().parents[2]
OUT=ROOT/"app/src/debug/assets/ws16ao"
manifest=json.loads((OUT/"manifest.json").read_text())
expected={"assets/ws16ao/spines/"+a["profile"]+"/"+a["name"]+".png":a["sha256"] for a in manifest["assets"]}
expected["assets/ws16ao/manifest.json"]=hashlib.sha256((OUT/"manifest.json").read_bytes()).hexdigest()
assert len(expected)==13
with zipfile.ZipFile(sys.argv[1]) as qa:
    actual={n for n in qa.namelist() if n.startswith("assets/ws16ao/")}
    assert actual==set(expected),(sorted(actual),sorted(expected))
    for name,digest in expected.items():
        assert hashlib.sha256(qa.read(name)).hexdigest()==digest,name
if len(sys.argv)>2:
    with zipfile.ZipFile(sys.argv[2]) as release:
        assert not any(n.startswith("assets/ws16ao/") for n in release.namelist()),"Unapproved spine family in release!"
print("PASS WS16AO:",len(expected),"sha-pinned debug assets; no release leakage")
