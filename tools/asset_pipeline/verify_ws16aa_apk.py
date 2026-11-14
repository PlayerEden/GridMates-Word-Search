#!/usr/bin/env python3
"""Compare exact SHA256 sprite bytes in debug APK and assert zero in release APK."""
import hashlib, json, sys, zipfile
from pathlib import Path
root = Path(__file__).resolve().parents[2]
dir_ = root / "app/src/debug/assets/ws16aa"
manifest = json.loads((dir_ / "manifest.json").read_text())
expected = {"assets/ws16aa/" + name: detail["export_sha256"]
            for name, detail in manifest["items"].items()}
expected["assets/ws16aa/manifest.json"] = hashlib.sha256(
    (dir_ / "manifest.json").read_bytes()).hexdigest()
with zipfile.ZipFile(sys.argv[1]) as apk:
    actual = {name for name in apk.namelist() if name.startswith("assets/ws16aa/")}
    assert actual == set(expected), (sorted(actual), sorted(expected))
    for name, sha in expected.items():
        assert hashlib.sha256(apk.read(name)).hexdigest() == sha, name
if len(sys.argv) > 2:
    with zipfile.ZipFile(sys.argv[2]) as release:
        assert not any(name.startswith("assets/ws16aa/")
                       for name in release.namelist()), "WS16AA QA artwork leaked into release"
print("WS16AA source-pinned QA assets verified:", len(expected),
      "; release isolation:", len(sys.argv) > 2)
