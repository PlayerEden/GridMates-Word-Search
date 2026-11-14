#!/usr/bin/env python3
"""Build the owner-approved illustrated EMPTY WS16AK cabinet from its pinned source.

Only six real image bands plus the manifest are shipped to debug; all original
book spines, clickable regions and library game data remain native Kotlin.
"""
from __future__ import annotations
import hashlib
import json
import tempfile
from pathlib import Path
from shutil import copyfile
from PIL import Image
from build_ws16ak_background import W, H, CUTS, ROW_NAMES, make_assets

ROOT = Path(__file__).resolve().parents[2]
MASTER = ROOT / "design/visual-system/masters/ws16ak/approved_bookcase_master_1024x1536.png"
DEBUG = ROOT / "app/src/debug/assets/ws16ak"
MASTER_SHA256 = "e2f8fcf3732698f4657a9e01a0996a34be72f71d5387b14e3d5be4b0d6d0f606"


def sha256(data: bytes) -> str:
    return hashlib.sha256(data).hexdigest()


def build() -> dict:
    source_bytes = MASTER.read_bytes()
    assert sha256(source_bytes) == MASTER_SHA256, "WS16AK approved source painting changed!"
    DEBUG.mkdir(parents=True, exist_ok=True)
    with tempfile.TemporaryDirectory(prefix="ws16ak_") as tmp:
        folder = Path(tmp)
        generated = make_assets(MASTER, folder)
        assert generated["pixel_identical_reconstruction"] is True
        assert len(generated["modules"]) == 13
        shipped = []
        for i, name in enumerate(ROW_NAMES):
            expected_height = CUTS[i + 1] - CUTS[i]
            inp = folder / (name + ".png")
            out = DEBUG / (name + ".png")
            copyfile(inp, out)
            with Image.open(out) as image:
                assert image.size == (W, expected_height)
            shipped.append({
                "name": name,
                "file": name + ".png",
                "sha256": sha256(out.read_bytes()),
                "dimensions": [W, expected_height],
                "master_y_range": [CUTS[i], CUTS[i + 1]],
            })
    manifest = {
        "id": "WS16AK-owner-selected-empty-bookcase",
        "source_sha256": MASTER_SHA256,
        "source_file": str(MASTER.relative_to(ROOT)),
        "source_dimensions": [W, H],
        "pixel_identical_reconstruction_at_source_dimensions": True,
        "qa_only": True,
        "release_approved": False,
        "book_art_and_click_areas": "live Compose overlays; not painted into cabinet",
        "bands": shipped,
    }
    (DEBUG / "manifest.json").write_text(json.dumps(manifest, indent=2, sort_keys=True) + "\n")
    return manifest


if __name__ == "__main__":
    m = build()
    print("WS16AK actual painted bookcase:", len(m["bands"]), "bands; source SHA verified")
