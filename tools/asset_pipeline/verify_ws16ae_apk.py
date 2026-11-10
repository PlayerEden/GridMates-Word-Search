#!/usr/bin/env python3
"""Verify real V2 cover/spine export bytes in APK, and zero in release."""
from pathlib import Path
import hashlib, json, sys, zipfile

ROOT = Path(__file__).resolve().parents[2]
ART = ROOT / "app/src/debug/assets/ws16ae"
manifest = json.loads((ART / "manifest.json").read_text())
expected = {
    "assets/ws16ae/" + Path(entry["output"]).relative_to(
        Path("app/src/debug/assets/ws16ae")).as_posix(): entry["output_sha256"]
    for entry in manifest["assets"]
}
expected["assets/ws16ae/manifest.json"] = hashlib.sha256(
    (ART / "manifest.json").read_bytes()).hexdigest()
with zipfile.ZipFile(sys.argv[1]) as debug:
    actual = {name for name in debug.namelist() if name.startswith("assets/ws16ae/")}
    assert actual == set(expected), (actual, set(expected))
    for name, sha in expected.items():
        assert hashlib.sha256(debug.read(name)).hexdigest() == sha, name
if len(sys.argv) > 2:
    with zipfile.ZipFile(sys.argv[2]) as release:
        assert not any(name.startswith("assets/ws16ae/")
                       for name in release.namelist()), "WS16AE unapproved images leaked"
print("WS16AE book family verified:", len(expected), "files")
