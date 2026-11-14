"""WS16AI modular painted bookcase, 2-tier paging and honest blank volumes."""
import json
import unittest
from pathlib import Path
from PIL import Image
from build_ws16ai_bookcase import ROOT, OUT, digest

class BookcaseArtTests(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.m = json.loads((OUT / "manifest.json").read_text())

    def test_separate_painted_wood_and_six_narrow_spines(self):
        m = self.m
        self.assertTrue(m["qa_only"])
        self.assertFalse(m["release_approved"])
        self.assertFalse(m["books_baked_into_art"])
        self.assertFalse(m["labels_baked_into_art"])
        self.assertEqual(5, len(m["parts"]))
        self.assertEqual(6, len(m["spines"]))
        for name, item in m["parts"].items():
            path = OUT / name
            self.assertEqual(item["sha256"], digest(path.read_bytes()))
            with Image.open(path) as artwork:
                self.assertEqual("RGBA", artwork.mode)
                self.assertEqual((item["width"], item["height"]), artwork.size)
        for name, item in m["spines"].items():
            path = OUT / "spines" / (name + ".png")
            self.assertEqual(item["sha256"], digest(path.read_bytes()))
            with Image.open(path) as artwork:
                self.assertEqual((104,542), artwork.size)
            self.assertEqual(name in ("woodland","space","garden"), item["playable"])

    def test_actual_library_ui_is_paged_not_fake_content(self):
        code = (ROOT / "app/src/main/java/com/playereden/gridmates/wordsearch"
                / "BookcaseBookshelfScreen.kt").read_text()
        self.assertIn("HorizontalPager(", code)
        self.assertIn("pageCount = maxOf(1,", code)
        self.assertIn("catalog.books.drop(first).take(capacity)", code)
        self.assertIn("for (slot in 0 until slotsPerRow)", code)
        self.assertIn("onSelect(book.id)", code)
        self.assertIn("BookcaseFutureSpineArt", code)
        self.assertNotIn("onSelect(style", code)
        self.assertIn("library-view-pages", code)
        self.assertIn("library-pulled-book-dialog", code)
        self.assertIn("BookcaseWoodParts", code)

if __name__ == "__main__":
    unittest.main()
