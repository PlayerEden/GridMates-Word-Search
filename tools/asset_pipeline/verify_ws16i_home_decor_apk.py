#!/usr/bin/env python3
"""Assert Home flora sprites match debug APK bytes; absent from release APK."""
import sys,json,zipfile,hashlib
from pathlib import Path
root=Path(__file__).resolve().parents[2]
dest=root/"app/src/debug/assets/ws16i/decor"
m=json.loads((dest/"manifest.json").read_text())
assert m["debug_only"] and not m["release_approved"] and len(m["sprites"])==5
expect={"assets/ws16i/decor/"+name+".png":meta["export_sha256"] for name,meta in m["sprites"].items()}
expect["assets/ws16i/decor/manifest.json"]=hashlib.sha256((dest/"manifest.json").read_bytes()).hexdigest()
with zipfile.ZipFile(sys.argv[1]) as z:
    present={x for x in z.namelist() if x.startswith("assets/ws16i/")}
    assert present==set(expect),(present,expect)
    for name,hash in expect.items():
        assert hashlib.sha256(z.read(name)).hexdigest()==hash,name
if len(sys.argv)>2:
    with zipfile.ZipFile(sys.argv[2]) as z:
        assert not any(x.startswith("assets/ws16i/") for x in z.namelist()),"QA botanical art leaked into release"
print("WS16I Home decor matched:",len(expect),"files, release checked",len(sys.argv)>2)
