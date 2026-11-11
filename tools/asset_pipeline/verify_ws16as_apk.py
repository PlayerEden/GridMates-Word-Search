#!/usr/bin/env python3
"""WS16AS exact-source visual QA: verify five packaged shell PNGs and release isolation."""
from pathlib import Path
import hashlib
import json
import sys
from zipfile import ZipFile

ROOT=Path(__file__).resolve().parents[2]
ZIP=ROOT/"design/visual-system/masters/ws16as/WS16AS_Modular_Library_Art_Kit.zip"
DEBUG=ROOT/"app/src/debug/assets/ws16as/shell"
EXPECTED={
    "top_crown.png": "c4365f20bf839e504cd5828fdacf2f89187a4770c72911e955de1a7b3ccebc63",
    "left_pilaster.png": "caea09cae3e4114b99fa6fd7c8b9a3d736471b389864b9efb28b8c2f58d21dd9",
    "recessed_backboard.png": "6d63a53556cbc8ae7059418d8210ca79796becfa7e8642b217debca696c17c64",
    "right_pilaster.png": "843346dca3c6f711bd5d2d55076e664bcece16131a17efbffb27d996434a9e98",
    "bottom_base.png": "1192023c1f02efd042a4e75fc5d3ec56a45bdff6f573c741a0a0f0b0112578a2",
}
def sha(data:bytes)->str:
    return hashlib.sha256(data).hexdigest()

def verify(debug_apk:Path,release_apk:Path|None=None)->None:
    imported=ZIP.exists()
    expected_files=set(EXPECTED) if imported else set()
    source_files={p.name for p in DEBUG.glob("*.png")}
    assert source_files==expected_files, (source_files,expected_files)
    for name in expected_files:
        assert sha((DEBUG/name).read_bytes())==EXPECTED[name], ("source asset changed",name)
    if imported:
        manifest=json.loads((DEBUG/"manifest.json").read_text())
        assert manifest["qa_only"] is True and manifest["release_approved"] is False
        assert manifest["assets"]==EXPECTED, "Imported manifest differs from pinned original files"
    prefix="assets/ws16as/shell/"
    with ZipFile(debug_apk) as z:
        packaged={name.removeprefix(prefix) for name in z.namelist()
                  if name.startswith(prefix) and name.endswith(".png")}
        assert packaged==expected_files, (packaged,expected_files)
        for name in expected_files:
            assert sha(z.read(prefix+name))==EXPECTED[name], ("APK PNG hash changed",name)
        if imported:
            assert prefix+"manifest.json" in z.namelist(), "Debug APK missing source manifest"
    if release_apk is not None:
        with ZipFile(release_apk) as z:
            leaked=[n for n in z.namelist() if n.startswith("assets/ws16as/")]
            assert not leaked, ("WS16AS test-only artwork leaked to release",leaked)
    print("WS16AS:", len(expected_files), "original carved cabinet PNGs",
          "EXACT in debug; release clear" if release_apk else "EXACT in debug",
          ";" ,"original source ZIP present" if imported else "original source ZIP not yet imported")

if __name__=="__main__":
    if len(sys.argv) not in (2,3):
        raise SystemExit("usage: verify_ws16as_apk.py DEBUG.apk [RELEASE.apk]")
    verify(Path(sys.argv[1]),Path(sys.argv[2]) if len(sys.argv)>2 else None)
