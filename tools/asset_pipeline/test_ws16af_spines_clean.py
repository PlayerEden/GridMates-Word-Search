"""WS16AF source-pinned image repair, library QA placeholders and tap contract."""
import json, unittest
from PIL import Image
from build_ws16af_spines_clean import ROOT, THEMES, SRC, OUT, MARK_PIXEL_ROWS, clean_spine_symbol, digest

class SpineArtTests(unittest.TestCase):
    def test_all_three_source_outputs_verified(self):
        m=json.loads((OUT.parent/"manifest.json").read_text())
        self.assertTrue(m["qa_only"])
        self.assertFalse(m["release_approved"])
        self.assertEqual(set(THEMES),{a["theme"] for a in m["assets"]})
        for a in m["assets"]:
            self.assertEqual([128,640],a["dimensions"])
            self.assertEqual(digest((ROOT/a["source"]).read_bytes()),a["source_sha256"])
            self.assertEqual(digest((ROOT/a["output"]).read_bytes()),a["output_sha256"])
            with Image.open(ROOT/a["output"]) as im:
                self.assertEqual("RGBA",im.mode)
                self.assertEqual((128,640),im.size)
                self.assertEqual((14,51,114,589),im.getchannel("A").point(lambda v:255 if v>=40 else 0).getbbox())

    def test_only_superscript_pixels_changed_in_source(self):
        for theme in THEMES:
            with self.subTest(theme=theme):
                orig=Image.open(SRC/(theme+".png")).convert("RGBA")
                edited,count=clean_spine_symbol(orig,theme)
                allowed={(x,y) for y,(lo,hi) in MARK_PIXEL_ROWS[theme].items() for x in range(lo,hi+1)}
                actual={(x,y) for y in range(orig.height) for x in range(orig.width)
                        if orig.getpixel((x,y))!=edited.getpixel((x,y))}
                self.assertEqual(allowed,actual)
                self.assertEqual(len(allowed),count)
                self.assertEqual(orig.getchannel("A").tobytes(),edited.getchannel("A").tobytes())

    def test_debug_only_loader_and_nonplayable_placeholders(self):
        book=(ROOT/"app/src/main/java/com/playereden/gridmates/wordsearch/BookArt.kt").read_text()
        lib=(ROOT/"app/src/main/java/com/playereden/gridmates/wordsearch/BookcaseBookshelfScreen.kt").read_text()
        self.assertIn('ws16af/spines/',book)
        self.assertIn('preferredCorrectedSpine',book)
        self.assertIn('preferredPaintedV2',book)
        self.assertIn('ws16ad/placeholders/',book)
        self.assertIn('BookFactoryBlankSpine',lib)
        self.assertIn('if (fieldNotesArt && books.isEmpty() && index == 1)',lib)
        self.assertIn('onSelect(book.id)',lib)
        self.assertIn('library-pulled-book-dialog',lib)
        self.assertIn('Modifier.width(slotWidth)',lib)
        self.assertIn('More books coming soon',lib)

if __name__=="__main__":unittest.main()
