"""WS-12D artwork consistency and no-regression tests; no Android files altered."""
import copy
import hashlib
import json
from pathlib import Path
import tempfile
import unittest
import xml.etree.ElementTree as ET

from render_paperback import render
from render_paperback_accents import (
    render_accents, chapter_svg, bookmark_svg, seal_svg
)

ROOT = Path(__file__).resolve().parents[2]
TOKENS = ROOT / "design/asset-pipeline/paperback-v1-tokens.json"
NS = "{http://www.w3.org/2000/svg}"


class PaperbackAccentTests(unittest.TestCase):
    def setUp(self):
        self.recipe = json.loads(TOKENS.read_text(encoding="utf-8"))
        self.temp = tempfile.TemporaryDirectory()
        self.folder = Path(self.temp.name)

    def tearDown(self):
        self.temp.cleanup()

    def test_nine_exact_companion_art_pieces(self):
        report = render_accents(TOKENS, self.folder / "a")
        self.assertEqual(report["theme_count"], 3)
        self.assertEqual(len(report["files"]), 9)
        self.assertEqual(report["art_types"], ["chapters", "bookmarks", "seals"])
        self.assertFalse(report["owner_approved"])
        self.assertFalse(report["adobe_refined"])
        self.assertFalse(report["android_resources_modified"])
        self.assertFalse(report["text_baked_into_art"])

    def test_identical_sha256_on_clean_rebuild(self):
        a = render_accents(TOKENS, self.folder / "a")
        b = render_accents(TOKENS, self.folder / "b")
        self.assertEqual(a, b)
        for name, sha in a["files"].items():
            binary = (self.folder / "a" / name).read_bytes()
            self.assertEqual(hashlib.sha256(binary).hexdigest(), sha)
            self.assertEqual(binary, (self.folder / "b" / name).read_bytes())

    def test_valid_vector_shapes_and_zero_embedded_ui_text(self):
        for theme in self.recipe["themes"]:
            for maker, size in (
                (chapter_svg, 'width="240" height="240"'),
                (bookmark_svg, 'width="112" height="360"'),
                (seal_svg, 'width="300" height="300"'),
            ):
                svg = maker(theme)
                root = ET.fromstring(svg)
                self.assertEqual(root.tag, NS + "svg")
                self.assertIn(size, svg)
                self.assertEqual(len(list(root.iter(NS + "text"))), 0)
                for forbidden in ("GridMates", "VOLUME", "®", "™", "href=", "<script"):
                    self.assertNotIn(forbidden, svg)
                self.assertIn("aria-hidden", svg)

    def test_same_pilot_theme_geometry_for_all_three_assets(self):
        for painter in (chapter_svg, bookmark_svg, seal_svg):
            for theme in self.recipe["themes"]:
                svg = painter(theme)
                self.assertIn(theme["accent"], svg)
                self.assertIn(theme["accent_light"], svg)
                self.assertIn("#FFFDF8", svg)
                self.assertIn('viewBox="240 440 730 680"', svg)

    def test_existing_paperback_cover_and_emblem_bytes_are_untouched(self):
        old = render(TOKENS, self.folder / "old-books")
        render_accents(TOKENS, self.folder / "new-accents")
        rerun = render(TOKENS, self.folder / "unchanged-books")
        self.assertEqual(old["files"], rerun["files"])
        self.assertEqual(len(old["files"]), 7)

    def test_new_ocean_theme_does_not_regenerate_pilot_accent_bytes(self):
        old = render_accents(TOKENS, self.folder / "base")
        more = copy.deepcopy(self.recipe)
        more["themes"].append({
            "id": "ocean", "label": "Ocean", "volume": "I",
            "motif": "orbit", "accent": "#365D71",
            "accent_light": "#E0EDF3", "accent_dark": "#233E51",
            "highlight": "#9ABCCD", "mark": "shell"
        })
        path = self.folder / "ocean-tokens.json"
        path.write_text(json.dumps(more), encoding="utf-8")
        expanded = render_accents(path, self.folder / "extended")
        self.assertEqual(expanded["theme_count"], 4)
        self.assertEqual(len(expanded["files"]), 12)
        for key, digest in old["files"].items():
            self.assertEqual(digest, expanded["files"][key])
        for t in ("chapters", "bookmarks", "seals"):
            self.assertIn(t + "/ocean.svg", expanded["files"])

    def test_invalid_recipe_is_blocked_before_any_asset_export(self):
        bad = copy.deepcopy(self.recipe)
        bad["themes"][0]["motif"] = "unknown-generated-art"
        path = self.folder / "bad.json"
        path.write_text(json.dumps(bad), encoding="utf-8")
        with self.assertRaisesRegex(ValueError, "Unknown drawing motif"):
            render_accents(path, self.folder / "invalid")
        self.assertFalse((self.folder / "invalid").exists())

    def test_artifacts_do_not_touch_android_or_original_avatars(self):
        render_accents(TOKENS, self.folder / "only-proof")
        self.assertEqual(
            sorted(x.name for x in (self.folder / "only-proof").iterdir()),
            ["bookmarks", "chapters", "render_sha256.json", "seals"]
        )
        for path in (self.folder / "only-proof").rglob("*"):
            self.assertNotIn("app/src", str(path))
            self.assertNotIn("woodland/portraits", str(path))


if __name__ == "__main__":
    unittest.main()
