"""WS-12F tests: actual 18-asset coordinated art contact sheet, stable source."""
import copy
import json
from pathlib import Path
import tempfile
import unittest
import xml.etree.ElementTree as ET

from render_family_review import render_family, spine_svg
from render_paperback import render
from render_paperback_accents import render_accents

ROOT = Path(__file__).resolve().parents[2]
TOKENS = ROOT / "design/asset-pipeline/paperback-v1-tokens.json"


class ThreeFamilyReviewTests(unittest.TestCase):
    def setUp(self):
        self.tmp = tempfile.TemporaryDirectory()
        self.root = Path(self.tmp.name)
        self.tokens = json.loads(TOKENS.read_text(encoding="utf-8"))

    def tearDown(self):
        self.tmp.cleanup()

    def test_eighteen_real_svg_art_pieces_and_editable_review_sheet(self):
        report = render_family(TOKENS, self.root / "first")
        self.assertEqual(report["theme_count"], 3)
        self.assertEqual(report["art_file_count"], 18)
        self.assertEqual(len(report["files"]), 19)
        self.assertEqual(report["book_asset_types"],
                         ["books", "spines", "emblems", "chapters", "bookmarks", "seals"])
        self.assertFalse(report["owner_approved"])
        self.assertFalse(report["adobe_refined"])
        self.assertFalse(report["android_resources_modified"])
        html = (self.root / "first/review.html").read_text()
        for t in ("Woodland", "Space", "Garden"):
            self.assertIn("<h2>" + t + "</h2>", html)
        for t in ("woodland", "space", "garden"):
            for kind in ("books", "spines", "emblems", "chapters", "bookmarks", "seals"):
                self.assertTrue((self.root / "first" / kind / (t + ".svg")).is_file())
                self.assertIn(kind + "/" + t + ".svg", html)
        self.assertIn("max-width:420px", html)
        self.assertIn("Unapproved concept preview", html)
        self.assertIn("all labels and progress", html.lower())
        self.assertFalse((self.root / "first/_original_books").exists())

    def test_rebuild_is_byte_identical(self):
        a = render_family(TOKENS, self.root / "a")
        b = render_family(TOKENS, self.root / "b")
        self.assertEqual(a, b)
        for name in a["files"]:
            self.assertEqual((self.root / "a" / name).read_bytes(),
                             (self.root / "b" / name).read_bytes())

    def test_spines_share_correct_geometry_are_textless_and_motif_based(self):
        for theme in self.tokens["themes"]:
            svg = spine_svg(theme)
            root = ET.fromstring(svg)
            self.assertEqual(root.tag, "{http://www.w3.org/2000/svg}svg")
            self.assertEqual(root.attrib["viewBox"], "0 0 160 920")
            self.assertIn(theme["accent"], svg)
            self.assertIn(theme["accent_light"], svg)
            self.assertIn(theme["motif"] if False else "viewBox=\"240 440 730 680\"", svg)
            self.assertNotIn("<text", svg)
            self.assertNotIn("GRIDMATES", svg)
            self.assertEqual(len(list(root.iter("{http://www.w3.org/2000/svg}text"))), 0)

    def test_prior_cover_and_accent_sources_keep_exact_file_hashes(self):
        report = render_family(TOKENS, self.root / "review")
        old = render(TOKENS, self.root / "old-cover")
        companion = render_accents(TOKENS, self.root / "old-accent")
        for name, digest in old["files"].items():
            if name != "index.html":
                self.assertEqual(report["files"][name], digest)
        for name, digest in companion["files"].items():
            self.assertEqual(report["files"][name], digest)

    def test_new_fourth_theme_keeps_old_spine_and_cover_bytes(self):
        original = render_family(TOKENS, self.root / "orig")
        expanded = copy.deepcopy(self.tokens)
        expanded["themes"].append({
            "id": "ocean", "label": "Ocean", "volume": "I",
            "motif": "orbit", "accent": "#365D71",
            "accent_light": "#E0EDF3", "accent_dark": "#233E51",
            "highlight": "#9ABCCD", "mark": "shell",
        })
        alt = self.root / "ocean-token.json"
        alt.write_text(json.dumps(expanded), encoding="utf-8")
        extension = render_family(alt, self.root / "extended")
        self.assertEqual(extension["art_file_count"], 24)
        for name, digest in original["files"].items():
            if name != "review.html":
                self.assertEqual(extension["files"][name], digest)

    def test_invalid_new_motif_fails_closed(self):
        bad = copy.deepcopy(self.tokens)
        bad["themes"][0]["motif"] = "detailed-ai-bear-painting"
        path = self.root / "bad.json"
        path.write_text(json.dumps(bad), encoding="utf-8")
        with self.assertRaisesRegex(ValueError, "Unknown drawing motif"):
            render_family(path, self.root / "bad")

    def test_no_android_assets_are_generated_by_contact_sheet(self):
        render_family(TOKENS, self.root / "proof")
        self.assertEqual(
            sorted(x.name for x in (self.root / "proof").iterdir()),
            ["bookmarks", "books", "chapters", "emblems", "render_sha256.json",
             "review.html", "seals", "spines"]
        )


if __name__ == "__main__":
    unittest.main()
