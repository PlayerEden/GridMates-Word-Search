#!/usr/bin/env python3
"""Exact-source WS16BI art gate: debug contains new assets, release contains none."""
from __future__ import annotations
import hashlib
import json
import sys
import zipfile
from pathlib import Path

ROOT = Path(__file__).resolve().parents[2]
DIR = ROOT / "app/src/debug/assets/ws16bi"
manifest = json.loads((DIR / "manifest.json").read_text())
assert manifest["qa_only"] is True and manifest["release_approved"] is False
expected = {
    "assets/" + a["path"]: a["sha256"]
    for a in manifest["assets"]
}
expected["assets/ws16bi/manifest.json"] = hashlib.sha256(
    (DIR / "manifest.json").read_bytes()
).hexdigest()
assert len(expected) == 5
with zipfile.ZipFile(sys.argv[1]) as apk:
    actual = set(n for n in apk.namelist() if n.startswith("assets/ws16bi/"))
    assert actual == set(expected), (actual, set(expected))
    for name, digest in expected.items():
        actual_digest = hashlib.sha256(apk.read(name)).hexdigest()
        assert actual_digest == digest, name
if len(sys.argv) > 2:
    with zipfile.ZipFile(sys.argv[2]) as release:
        assert not any(
            n.startswith("assets/ws16bi/") for n in release.namelist()
        ), "UNAPPROVED WS16BI SHELVES/DECOR IN RELEASE"
print("WS16BI PASS: one high-res mounted shelf, three wordless bindings, QA-only")
