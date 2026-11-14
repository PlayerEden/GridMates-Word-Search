"""Verify source-matched modular bookcase art is in QA APK, absent in release."""
from pathlib import Path
import hashlib, json, sys, zipfile
ROOT = Path(__file__).resolve().parents[2]
OUT = ROOT / "app/src/debug/assets/ws16ai"
m = json.loads((OUT / "manifest.json").read_text())
expected = {"assets/ws16ai/" + name: v["sha256"]
            for name, v in m["parts"].items()}
expected.update({"assets/ws16ai/spines/" + name + ".png": v["sha256"]
                 for name, v in m["spines"].items()})
expected["assets/ws16ai/manifest.json"] = hashlib.sha256(
    (OUT / "manifest.json").read_bytes()).hexdigest()
with zipfile.ZipFile(sys.argv[1]) as qa:
    actual = {n for n in qa.namelist() if n.startswith("assets/ws16ai/")}
    assert actual == set(expected), (actual, set(expected))
    for name, sha in expected.items():
        assert hashlib.sha256(qa.read(name)).hexdigest() == sha, name
if len(sys.argv) > 2:
    with zipfile.ZipFile(sys.argv[2]) as release:
        assert not any(n.startswith("assets/ws16ai/")
                       for n in release.namelist()), "QA bookcase art in release"
print("PASS WS16AI:",len(expected),"matched sprites/manifest; no release leakage")
