"""WS16AK approved bookcase geometry, source authenticity and debug integration."""
import json
import unittest
from pathlib import Path
from PIL import Image
from build_ws16ak_ci import ROOT, MASTER, DEBUG, MASTER_SHA256, sha256
from build_ws16ak_background import CUTS, ROW_NAMES, W, H


class BookcaseSourceTests(unittest.TestCase):
    def test_exact_master_and_six_valid_modules(self):
        self.assertEqual(MASTER_SHA256, sha256(MASTER.read_bytes()))
        data = json.loads((DEBUG / "manifest.json").read_text())
        self.assertEqual(MASTER_SHA256, data["source_sha256"])
        self.assertTrue(data["qa_only"])
        self.assertFalse(data["release_approved"])
        self.assertEqual(6, len(data["bands"]))
        self.assertEqual(ROW_NAMES, [r["name"] for r in data["bands"]])
        original = Image.open(MASTER).convert("RGBA")
        assembled = Image.new("RGBA", (W, H), (0, 0, 0, 0))
        for row in data["bands"]:
            part = DEBUG / row["file"]
            self.assertEqual(sha256(part.read_bytes()), row["sha256"])
            with Image.open(part) as piece:
                self.assertEqual(list(piece.size), row["dimensions"])
                assembled.alpha_composite(piece.convert("RGBA"), (0, row["master_y_range"][0]))
        self.assertEqual(original.tobytes(), assembled.tobytes())

    def test_qa_preview_is_separate_from_live_library(self):
        root = ROOT / "app/src/main/java/com/playereden/gridmates/wordsearch"
        cabinet = (root / "WS16AKBookcaseBackground.kt").read_text()
        screen = (root / "BookcaseBookshelfScreen.kt").read_text()
        self.assertIn('ws16ak/${band.name}.png', cabinet)
        self.assertIn("WS16AKEmptyBookcaseBackground", screen)
        self.assertIn("bookcase-empty-background-preview", screen)
        self.assertIn("onSelect(book.id)", screen)
        self.assertIn("library-pulled-book-dialog", screen)
        self.assertIn("bookcase-lazy-shelves", screen)

if __name__ == "__main__":
    unittest.main()
