#!/usr/bin/env python3
"""Byte-verify six ws16h actions inside debug and prevent their release leakage."""
import hashlib,json,sys,zipfile
from pathlib import Path
R=Path(__file__).resolve().parents[2]
SOURCE=R/"app/src/debug/assets/ws16h/actions"
m=json.loads((SOURCE/"manifest.json").read_text())
assert len(m["sprites"])==6 and not m["release_approved"]
expected={"assets/ws16h/actions/"+name+".png":r["export_sha256"] for name,r in m["sprites"].items()}
expected["assets/ws16h/actions/manifest.json"]=hashlib.sha256((SOURCE/"manifest.json").read_bytes()).hexdigest()
with zipfile.ZipFile(sys.argv[1]) as apk:
    actual={n for n in apk.namelist() if n.startswith("assets/ws16h/")}
    assert actual==set(expected),(actual,set(expected))
    for path,sha in expected.items():
        assert hashlib.sha256(apk.read(path)).hexdigest()==sha,path
if len(sys.argv)>2:
    with zipfile.ZipFile(sys.argv[2]) as apk:
        assert not any(n.startswith("assets/ws16h/") for n in apk.namelist()),"WS16H QA art leaked into release"
print("WS16H: verified six illustrated QA Home sprites, release clean:",len(sys.argv)>2)
