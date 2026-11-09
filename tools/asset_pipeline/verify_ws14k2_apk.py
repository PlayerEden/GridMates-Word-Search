#!/usr/bin/env python3
"""Verify WS-14K2 exact assets in debug APK and strict release absence.

Usage:
  python3 tools/asset_pipeline/verify_ws14k2_apk.py debug.apk [release.apk]
"""
import hashlib
import json
import sys
from pathlib import Path
from zipfile import ZipFile

ROOT = Path(__file__).resolve().parents[2]
ASSET_PATH = Path("app/src/debug/assets/ws14k2/motifs")
PREFIX = "assets/ws14k2/motifs/"


def sha(data: bytes) -> str:
    return hashlib.sha256(data).hexdigest()


def verify(debug_apk: Path, release_apk: Path | None = None) -> None:
    manifest_bytes = (ROOT / ASSET_PATH / "manifest.json").read_bytes()
    manifest = json.loads(manifest_bytes)
    assets = manifest["assets"]
    assert set(assets) == {"woodland", "space", "garden"}, assets.keys()
    assert manifest["debug_only"] and not manifest["release_approved"]
    expected = {PREFIX + theme + ".png" for theme in assets}
    expected.add(PREFIX + "manifest.json")
    with ZipFile(debug_apk) as apk:
        actual = {path for path in apk.namelist() if path.startswith("assets/ws14k2/")}
        assert actual == expected, ("WS-14K2 debug asset mismatch", actual, expected)
        assert apk.read(PREFIX + "manifest.json") == manifest_bytes
        for theme, entry in assets.items():
            name = PREFIX + theme + ".png"
            actual_bytes = apk.read(name)
            assert sha(actual_bytes) == entry["export_sha256"], name
            assert actual_bytes == (ROOT / ASSET_PATH / (theme + ".png")).read_bytes(), name
            source = (ROOT / entry["source_path"]).read_bytes()
            assert sha(source) == entry["source_sha256"], entry["source_path"]
            assert entry["approval"] == "QA_INTEGRATED_UNREVIEWED"
    if release_apk:
        with ZipFile(release_apk) as apk:
            assert not any(
                name.startswith("assets/ws14k2/") for name in apk.namelist()
            ), "WS-14K2 unapproved artwork leaked into release APK"
    print("PASS: 3 original theme motifs with exact hashes in debug, release isolated"
          if release_apk else "PASS: 3 original theme motifs with exact hashes in debug")


if __name__ == "__main__":
    if len(sys.argv) not in (2, 3):
        raise SystemExit("Usage: verify_ws14k2_apk.py debug.apk [release.apk]")
    verify(Path(sys.argv[1]), Path(sys.argv[2]) if len(sys.argv) == 3 else None)
