"""Proof that all three V2 physical covers/spines share geometry and provenance."""
import hashlib
import json
from pathlib import Path
import unittest
from PIL import Image
from build_ws16ae_books_v2 import ROOT, THEMES, REGISTERED_MARK_BOX, repair_banner

OUT = ROOT / "app/src/debug/assets/ws16ae"

class V2PaintedBookTests(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.info = json.loads((OUT / "manifest.json").read_text())

    def test_complete_fixed_family_and_no_release(self):
        info = self.info
        self.assertFalse(info["release_approved"])
        self.assertTrue(info["qa_only"])
        self.assertFalse(info["includes_inaccurate_registered_mark"])
        self.assertEqual(6, len(info["assets"]))
        self.assertEqual(set(THEMES), set(info["themes"]))
        self.assertEqual({"woodland","garden","space"},
                         {r["theme"] for r in info["assets"] if r["role"] == "cover"})
        for entry in info["assets"]:
            self.assertEqual(hashlib.sha256((ROOT / entry["input"]).read_bytes()).hexdigest(),
                             entry["source_sha256"])
            self.assertEqual(hashlib.sha256((ROOT / entry["output"]).read_bytes()).hexdigest(),
                             entry["output_sha256"])
            with Image.open(ROOT / entry["output"]) as art:
                self.assertEqual("RGBA", art.mode)
                self.assertEqual([480,640] if entry["role"] == "cover" else [128,640],
                                 list(art.size))
                self.assertTrue(all(0 <= x for x in entry["visual_bounds"]))

    def test_localized_registered_mark_edited_in_all_three(self):
        for theme in THEMES:
            with self.subTest(theme=theme):
                source = Image.open(ROOT / "app/src/debug/assets/ws14/books" /
                                    (theme + ".png")).convert("RGBA")
                edited = repair_banner(source, REGISTERED_MARK_BOX[theme])
                x0,y0,x1,y1 = REGISTERED_MARK_BOX[theme]
                self.assertNotEqual(source.crop((x0,y0,x1,y1)).tobytes(),
                                    edited.crop((x0,y0,x1,y1)).tobytes())
                # This is a tiny hand-localized art correction, not a blur of letters.
                before = source.copy()
                before.paste(edited.crop((x0,y0,x1,y1)), (x0,y0))
                self.assertEqual(edited.tobytes(), before.tobytes())

    def test_old_paintings_and_library_content_not_changed(self):
        source = (ROOT / "app/src/main/java/com/playereden/gridmates/wordsearch/BookArt.kt").read_text()
        self.assertIn('ws16ae/covers/', source)
        self.assertIn('ws16ae/spines/', source)
        self.assertIn('preferredPaintedV2', source)
        self.assertIn('preferredBookFactory', source)
        self.assertNotIn('ws16ae/placeholders/', source)

if __name__ == "__main__":
    unittest.main()
