#!/usr/bin/env python3
"""WS16AB generated paint: verify source asset bytes and Android debug/release isolation.

Usage: python3 tools/asset_pipeline/verify_ws16ab_apk.py [debug.apk] [release.apk]
With no APKs it validates immutable source, provenance, dimensions and alpha.
"""
from pathlib import Path
import hashlib, json, struct, sys, zipfile
ROOT = Path(__file__).resolve().parents[2]
manifest = json.loads((ROOT/"design/visual-system/ws16ab-painted-shelf-v1.json").read_text())
path = ROOT / manifest["android_path"]
raw = path.read_bytes()
assert not manifest["owner_approved"] and not manifest["release_approved"]
assert manifest["qa_only"] and manifest["android_source_set"] == "debug"
assert hashlib.sha256(raw).hexdigest() == manifest["export_sha256"], "Painted shelf bytes changed"
assert hashlib.sha1(b"blob " + str(len(raw)).encode() + b"\0" + raw).hexdigest() == manifest["git_blob_sha1"]
assert len(raw) == manifest["export_bytes"]
assert raw[:4] == b"RIFF" and raw[8:16] == b"WEBPVP8X", "Not a VP8X WebP"
# VP8X image width and height are 24-bit unsigned little-endian values +1.
width = int.from_bytes(raw[24:27],"little") + 1
height = int.from_bytes(raw[27:30],"little") + 1
assert [width,height] == manifest["export_dimensions"], (width,height)
assert raw[20] & 0x10, "WebP must have transparent alpha"
if len(sys.argv) > 1:
    with zipfile.ZipFile(sys.argv[1]) as apk:
        art = {s for s in apk.namelist() if s.startswith("assets/ws16ab/")}
        assert art == {manifest["apk_path"]}, art
        assert hashlib.sha256(apk.read(manifest["apk_path"])).hexdigest() == manifest["export_sha256"]
if len(sys.argv) > 2:
    with zipfile.ZipFile(sys.argv[2]) as apk:
        assert not any(s.startswith("assets/ws16ab/") for s in apk.namelist()), "WS16AB art leaked into release"
print("WS16AB painting exact source, WebP alpha/dimensions, and APK isolation PASS")
