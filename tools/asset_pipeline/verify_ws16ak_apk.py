#!/usr/bin/env python3
"""Check packaged source-matched WS16AK background, absent in release APK."""
from pathlib import Path
import hashlib
import json
import sys
import zipfile
ROOT = Path(__file__).resolve().parents[2]
ART = ROOT / "app/src/debug/assets/ws16ak"
data = json.loads((ART / "manifest.json").read_text())
assert data["qa_only"] and not data["release_approved"]
expected = {"assets/ws16ak/" + rec["file"]: rec["sha256"] for rec in data["bands"]}
expected["assets/ws16ak/manifest.json"] = hashlib.sha256((ART / "manifest.json").read_bytes()).hexdigest()
with zipfile.ZipFile(sys.argv[1]) as debug:
    actual = {p for p in debug.namelist() if p.startswith("assets/ws16ak/")}
    assert actual == set(expected), (actual, set(expected))
    for p, digest in expected.items():
        assert hashlib.sha256(debug.read(p)).hexdigest() == digest, p
if len(sys.argv) > 2:
    with zipfile.ZipFile(sys.argv[2]) as release:
        assert not any(p.startswith("assets/ws16ak/") for p in release.namelist()), "Unapproved background leaked into release"
print("WS16AK verified", len(expected), "debug-only real artwork entries")
