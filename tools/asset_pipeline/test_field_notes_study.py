"""WS-12K regression: the original A artwork is immutable, and Study B is coherent."""
import hashlib
import json
from pathlib import Path
import tempfile
import unittest
import xml.etree.ElementTree as ET

from render_field_notes_study import (
    candidate_svg, expressive_details, render_study, THEMES, SHAPES
)
from render_family_review import render_family
from render_paperback import validate

ROOT = Path(__file__).resolve().parents[2]
TOKENS = ROOT / "design/asset-pipeline/paperback-v1-tokens.json"
SVG_NS = "{http://www.w3.org/2000/svg}"


class FieldNotesStudyTests(unittest.TestCase):
    def setUp(self):
        self.tmp = tempfile.TemporaryDirectory()
        self.root = Path(self.tmp.name)
        self.recipe = validate(json.loads(TOKENS.read_text(encoding="utf-8")))

    def tearDown(self):
        self.tmp.cleanup()

    def build(self, original, alternate):
        original_receipt = render_family(TOKENS, original)
        before_hashes = dict(original_receipt["files"])
        report = render_study(TOKENS, original, alternate)
        after = json.loads((original / "render_sha256.json").read_text())
        self.assertEqual(before_hashes, after["files"])
        return report, before_hashes

    def test_all_three_themes_have_complete_six_piece_alternate_families(self):
        report, originals = self.build(self.root / "A", self.root / "B")
        self.assertEqual(report["art_file_count"], 18)
        self.assertEqual(report["theme_count"], 3)
        self.assertFalse(report["owner_approved"])
        self.assertFalse(report["production_assets_modified"])
        self.assertTrue(report["source_original_files_unchanged"])
        self.assertEqual(set(THEMES), {t["id"] for t in self.recipe["themes"]})
        for theme in THEMES:
            for folder in SHAPES:
                name = f"{folder}/{theme}.svg"
                content = (self.root / "B" / name).read_bytes()
                old = (self.root / "A" / name).read_bytes()
                self.assertNotEqual(content, old)
                self.assertEqual(hashlib.sha256(old).hexdigest(), originals[name])
                self.assertEqual(hashlib.sha256(content).hexdigest(), report["files"][name])
                self.assertEqual(report["original_source_hashes"][name], originals[name])
                node = ET.fromstring(content)
                self.assertEqual(node.tag, SVG_NS + "svg")
                self.assertFalse(list(node.iter(SVG_NS + "text")))
                self.assertIn(b'field-notes-foreground', content)
                self.assertIn(b'field-notes-back', content)
                self.assertNotIn(b'<script', content)

    def test_comparison_sheet_is_semantic_not_a_faux_playable_game(self):
        self.build(self.root / "A", self.root / "B")
        page = (self.root / "A" / "compare-studies.html").read_text()
        self.assertEqual(page.count("<article"), 6)
        self.assertEqual(page.count("<figure"), 36)
        self.assertIn("Original · Quiet", page)
        self.assertIn("Study B · Field Notes", page)
        self.assertIn("Unapproved", page)
        self.assertNotIn(">Open book<", page)
        for theme in THEMES:
            self.assertIn("alt/field-notes/books/" + theme + ".svg", page)
            self.assertIn("books/" + theme + ".svg", page)

    def test_repeated_builds_are_bit_identical(self):
        one, originals = self.build(self.root / "A", self.root / "B")
        two = render_study(TOKENS, self.root / "A", self.root / "C")
        self.assertEqual(one, two)
        for name in one["files"]:
            self.assertEqual(
                (self.root / "B" / name).read_bytes(),
                (self.root / "C" / name).read_bytes()
            )

    def test_rejects_unverified_source_mutations_without_removing_old_art(self):
        self.build(self.root / "A", self.root / "B")
        original = self.root / "A" / "books" / "woodland.svg"
        original.write_text(original.read_text() + "<!-- altered -->")
        with self.assertRaisesRegex(ValueError, "Original image SHA changed"):
            render_study(TOKENS, self.root / "A", self.root / "C")

    def test_unregistered_art_family_is_not_guessed_or_silently_invented(self):
        bad = dict(self.recipe["themes"][0], id="rainforest")
        with self.assertRaisesRegex(ValueError, "No deliberately approved"):
            expressive_details(bad)


if __name__ == "__main__":
    unittest.main()
