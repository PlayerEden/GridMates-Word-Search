"""Integrity checks for the WS-14J Woodland page-accent candidate.

Run: python3 -m unittest discover -s tools/asset_pipeline -p 'test_ws14j_page_accent.py' -v
This checks a source and export in Git; it does not claim Android APK inclusion.
"""
import hashlib
import json
import unittest
from pathlib import Path
from PIL import Image

ROOT = Path(__file__).resolve().parents[2]
MANIFEST = ROOT / "design/visual-system/ws14j-page-accent-candidates.json"


class WS14JWoodlandCandidateTests(unittest.TestCase):
    def test_original_and_export_hashes(self):
        data = json.loads(MANIFEST.read_text(encoding="utf-8"))
        self.assertEqual(data["schema_version"], 1)
        candidate = data["assets"][0]
        self.assertEqual(candidate["state"], "GENERATED_UNREVIEWED")
        self.assertFalse(candidate["release_approved"])
        for prefix in ("master", "export"):
            file = ROOT / candidate[f"{prefix}_path"]
            self.assertTrue(file.is_file(), str(file))
            self.assertEqual(hashlib.sha256(file.read_bytes()).hexdigest(), candidate[f"{prefix}_sha256"])
            self.assertEqual(file.stat().st_size, candidate[f"{prefix}_bytes"])

    def test_debug_app_copy_is_exact_export(self):
        asset = json.loads(MANIFEST.read_text(encoding="utf-8"))["assets"][0]
        self.assertEqual(asset["debug_asset_path"], "app/src/debug/assets/ws14j/page-accents/woodland.png")
        local = ROOT / asset["debug_asset_path"]
        export = ROOT / asset["export_path"]
        self.assertTrue(local.is_file())
        self.assertEqual(local.read_bytes(), export.read_bytes())
        self.assertEqual(hashlib.sha256(local.read_bytes()).hexdigest(), asset["export_sha256"])
        self.assertFalse(asset["android_release_integrated"])

    def test_png_has_real_transparency_and_safe_size(self):
        asset = json.loads(MANIFEST.read_text(encoding="utf-8"))["assets"][0]
        with Image.open(ROOT / asset["export_path"]) as source:
            rgba = source.convert("RGBA")
            self.assertEqual(list(rgba.size), asset["export_dimensions_px"])
            alpha = rgba.getchannel("A")
            self.assertEqual(alpha.getextrema(), (0, 255))
            self.assertEqual(list(alpha.getbbox()), asset["alpha_bbox_xyxy"])
            self.assertEqual(rgba.getpixel((0, 0))[3], 0)
            self.assertGreater(sum(1 for a in alpha.getdata() if a > 0), 1000)


if __name__ == "__main__":
    unittest.main()
