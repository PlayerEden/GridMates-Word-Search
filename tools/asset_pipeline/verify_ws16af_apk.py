#!/usr/bin/env python3
"""WS16AF real APK byte verification and release artwork isolation."""
import hashlib,json,sys,zipfile
from pathlib import Path
ROOT=Path(__file__).resolve().parents[2]
ART=ROOT/"app/src/debug/assets/ws16af"
manifest=json.loads((ART/"manifest.json").read_text())
expected={"assets/ws16af/spines/"+entry["theme"]+".png":entry["output_sha256"]
          for entry in manifest["assets"]}
expected["assets/ws16af/manifest.json"]=hashlib.sha256((ART/"manifest.json").read_bytes()).hexdigest()
with zipfile.ZipFile(sys.argv[1]) as debug:
    actual={p for p in debug.namelist() if p.startswith("assets/ws16af/")}
    assert actual==set(expected),(actual,set(expected))
    for path,digest in expected.items():
        assert hashlib.sha256(debug.read(path)).hexdigest()==digest,path
if len(sys.argv)>2:
    with zipfile.ZipFile(sys.argv[2]) as release:
        assert not any(p.startswith("assets/ws16af/") for p in release.namelist()),"Unapproved painted spines in release!"
print("PASS: all three corrected spines and manifest matched source; optional release isolation passed")
