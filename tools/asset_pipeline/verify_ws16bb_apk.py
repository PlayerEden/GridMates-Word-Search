#!/usr/bin/env python3
"""Ensure exact independent shelf in debug APK and zero QA art in release."""
import hashlib
import sys
from pathlib import Path
from zipfile import ZipFile
from import_ws16bb_matched_shelf import SOURCE, OUTPUT, EXPECTED_SHA, verify

def sha(data): return hashlib.sha256(data).hexdigest()

def check(debug_apk:Path, release_apk:Path|None=None):
    verify()
    assert sha((OUTPUT/"shelf.png").read_bytes())==EXPECTED_SHA
    with ZipFile(debug_apk) as z:
        assert sha(z.read("assets/ws16bb/shelf.png"))==EXPECTED_SHA
        assert "assets/ws16bb/manifest.json" in z.namelist()
    if release_apk is not None:
        with ZipFile(release_apk) as z:
            assert not any(x.startswith("assets/ws16bb/") for x in z.namelist()), "WS16BB leaked to release"
    print("WS16BB: source-matched shelf verified in QA; release isolated" if release_apk else "WS16BB shelf verified in QA")

if __name__=="__main__":
    if len(sys.argv) not in (2,3):
        raise SystemExit("usage: verify_ws16bb_apk.py DEBUG.apk [RELEASE.apk]")
    check(Path(sys.argv[1]),Path(sys.argv[2]) if len(sys.argv)==3 else None)
