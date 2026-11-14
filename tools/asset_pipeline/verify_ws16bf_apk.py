#!/usr/bin/env python3
"""Pin all three WS16BF debug PNG bytes and exclude them from release."""
import hashlib
import json
import sys
import zipfile
from pathlib import Path

ROOT = Path(__file__).resolve().parents[2]
folder = ROOT / "app/src/debug/assets/ws16bf"
manifest = json.loads((folder / "manifest.json").read_text())
assert manifest["qa_only"] and not manifest["release_approved"]
expected = {
    "assets/ws16bf/spines/" + a["name"] + ".png": a["sha256"]
    for a in manifest["assets"]
}
expected["assets/ws16bf/manifest.json"] = hashlib.sha256(
    (folder / "manifest.json").read_bytes()
).hexdigest()
assert len(expected) == 4, expected
with zipfile.ZipFile(sys.argv[1]) as apk:
    actual = set(n for n in apk.namelist() if n.startswith("assets/ws16bf/"))
    assert actual == set(expected), (actual, set(expected))
    for name, digest in expected.items():
        assert hashlib.sha256(apk.read(name)).hexdigest() == digest, name
if len(sys.argv) > 2:
    with zipfile.ZipFile(sys.argv[2]) as release:
        assert not any(
            n.startswith("assets/ws16bf/") for n in release.namelist()
        ), "UNAPPROVED NEW SPINE ART IN RELEASE"
print("PASS WS16BF: 3 clean source-pinned spines; release isolation checked")
