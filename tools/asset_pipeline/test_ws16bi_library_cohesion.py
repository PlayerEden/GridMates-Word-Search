"""WS16BI combined art, layout, no-lettering and furniture control QA."""
import hashlib
import json
import re
import unittest
from pathlib import Path
from PIL import Image

ROOT=Path(__file__).resolve().parents[2]
APP=ROOT/"app/src/main/java/com/playereden/gridmates/wordsearch"
DIR=ROOT/"app/src/debug/assets/ws16bi"

class CohesiveCabinetTests(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.data=json.loads((DIR/"manifest.json").read_text())

    def test_shelf_and_wordless_blank_asset_manifest(self):
        m=self.data
        self.assertTrue(m["qa_only"])
        self.assertFalse(m["owner_approved"])
        self.assertFalse(m["release_approved"])
        self.assertFalse(m["text_on_blanks"])
        self.assertFalse(m["books_or_text_on_shelf"])
        self.assertFalse(m["real_book_ids_in_blanks"])
        self.assertEqual(set(x["name"] for x in m["assets"]),
                         {"shelf","fern","dusk","clay"})
        for a in m["assets"]:
            path=DIR/("shelf.png" if a["name"]=="shelf"
                      else "blanks/"+a["name"]+".png")
            self.assertEqual(hashlib.sha256(path.read_bytes()).hexdigest(),
                             a["sha256"])
            with Image.open(path) as im:
                self.assertEqual(im.size,tuple(a["size"]))
                self.assertEqual(im.mode,"RGBA")
                self.assertEqual(im.getpixel((0,0))[3],0)
                self.assertGreater(im.getchannel("A").getbbox()[2],80)
                if a["kind"]=="unlettered-blank":
                    self.assertEqual(a["size"],[104,542])
                    self.assertLessEqual(im.getchannel("A").getbbox()[2],101)
                else:
                    self.assertEqual(a["size"],[1172,250])

    def test_generator_is_pinned_and_does_not_print_titles(self):
        src=(ROOT/"tools/asset_pipeline/build_ws16bi_library_art.py").read_text()
        self.assertIn("assert sha(WOOD)==WOOD_SHA and sha(OLD)==OLD_SHA",src)
        self.assertIn("VARIANTS",src)
        self.assertNotIn("d.text(",src)
        self.assertNotIn("draw.text(",src)
        self.assertNotIn("GridMates Word Search",src)

    def test_portrait_uses_shelf_and_native_hardware(self):
        src=(APP/"WS16ATModularLibraryScene.kt").read_text()
        shelf=(APP/"Ws16BIMountedCabinetShelf.kt").read_text()
        chrome=(APP/"WS16BICabinetChrome.kt").read_text()
        self.assertIn("Ws16BIMountedCabinetShelf()",src)
        self.assertIn("ws16bi/shelf.png",shelf)
        self.assertIn("250f / 1172f",shelf)
        self.assertIn("paintedHeight - 42.dp",shelf)
        self.assertIn("WS16BIAdaptiveControl(",src)
        self.assertIn("WS16BIAdaptivePlaque(",src)
        self.assertIn("styled = cabinetPortrait",src)
        self.assertIn("size(54.dp)",chrome)
        self.assertIn("role = Role.Button",chrome)
        self.assertIn("if (styled) WS16BICabinetPlaque(",chrome)
        self.assertIn("else Ws16AZCabinetPlaque(",chrome)

    def test_actual_spines_keep_real_ids_decorations_have_none(self):
        src=(APP/"WS16BHDenseSpineBay.kt").read_text()
        blanks=(APP/"WS16BIUnletteredBlankSpine.kt").read_text()
        screen=(APP/"WS16ATModularLibraryScene.kt").read_text()
        self.assertIn("maxSlots: Int = 12",src)
        self.assertIn("showDecorativeBlanks: Boolean = false",src)
        self.assertIn("if (books.isEmpty()) 4 else 2",src)
        self.assertIn("onBookSelected(book.id)",src)
        self.assertIn('testTag("ws16bi-blank-cell-$shelfIndex-$i")',src)
        self.assertIn("WS16BIUnletteredBlankSpine(",src)
        self.assertIn("clearAndSetSemantics {}",blanks)
        self.assertNotIn(".clickable(",blanks)
        self.assertNotIn("onBookSelected(",blanks)
        self.assertIn("showDecorativeBlanks = true",screen)
        self.assertIn("val capacity = slots * tierCount",screen)
        self.assertIn("val packedSlot = if (density.fontScale >= 1.65f) 52.dp else 48.dp",screen)
        self.assertIn("densePortrait && maxHeight >= 960.dp && books.size > slots -> 3",screen)
        self.assertIn("books.drop(page * capacity).take(capacity)",screen)

    def test_debug_only_pipeline_and_v68_install_over(self):
        for wf in (".github/workflows/ws01-android-qa.yml",
                   ".github/workflows/ws12g-qa-art-lab.yml"):
            text=(ROOT/wf).read_text()
            self.assertIn("build_ws16bi_library_art.py",text)
            self.assertIn("verify_ws16bi_apk.py",text)
        gradle=(ROOT/"app/build.gradle.kts").read_text()
        match=re.search(r"versionCode\s*=.*\?:\s*(\d+)",gradle)
        self.assertIsNotNone(match)
        self.assertGreaterEqual(int(match.group(1)),68)
        self.assertIn("qa-signing/wordsearch-qa.p12",gradle)
        self.assertIn('applicationIdSuffix = ".qa"',gradle)

if __name__=="__main__":
    unittest.main()
