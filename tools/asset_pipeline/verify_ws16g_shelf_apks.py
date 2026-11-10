#!/usr/bin/env python3
"""Verify WS16G shelf sprite bytes in debug and never in optional release."""
import sys,json,hashlib,zipfile
from pathlib import Path
root=Path(__file__).resolve().parents[2]
source=root/"app/src/debug/assets/ws16g/shelf"
manifest=json.loads((source/"manifest.json").read_text())
expected={"assets/ws16g/shelf/"+name:row["export_sha256"] for name,row in manifest["items"].items()}
expected["assets/ws16g/shelf/manifest.json"]=hashlib.sha256((source/"manifest.json").read_bytes()).hexdigest()
with zipfile.ZipFile(sys.argv[1]) as apk:
    actual={n for n in apk.namelist() if n.startswith("assets/ws16g/")}
    assert actual==set(expected),(actual,expected)
    for name,digest in expected.items():
        assert hashlib.sha256(apk.read(name)).hexdigest()==digest,name
if len(sys.argv)>2:
    with zipfile.ZipFile(sys.argv[2]) as apk:
        assert not any(n.startswith("assets/ws16g/") for n in apk.namelist()),"Candidate art leaked into release"
print("WS16G debug art SHA verified",len(expected),"files; release checked",len(sys.argv)>2)
