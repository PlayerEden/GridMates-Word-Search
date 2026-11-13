"""WS16BK: bookcase page model, assets, accessibility and ownership separation."""
import hashlib
import json
import re
import unittest
from pathlib import Path
from PIL import Image

ROOT=Path(__file__).resolve().parents[2]
APP=ROOT/"app/src/main/java/com/playereden/gridmates/wordsearch"
FOLDER=ROOT/"app/src/debug/assets/ws16bk"

def page_model(owned,slots,tiers,portrait):
    cap=slots*tiers
    real=max(1,(owned+cap-1)//cap)
    future=2 if portrait else 0
    pages=real+future
    index=list(range(owned))
    real_ids=[]
    for page in range(pages):
        owned_page=index[page*cap:(page+1)*cap] if page<real else []
        for shelf in range(tiers):
            real_ids.extend(owned_page[shelf*slots:(shelf+1)*slots])
    return pages,real,real_ids

class FutureBookcaseTests(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.manifest=json.loads((FOLDER/"manifest.json").read_text())

    def test_eight_blanks_rail_manifest_and_perimeter(self):
        info=self.manifest
        self.assertTrue(info["qa_only"])
        self.assertFalse(info["release_approved"])
        self.assertFalse(info["owner_approved"])
        self.assertTrue(info["no_words_on_blanks"])
        assets=info["assets"]
        names={a["name"] for a in assets}
        self.assertEqual(names,{"fern","dusk","clay","garnet",
             "ochre","teal","violet","slate","pager_rail"})
        for asset in assets:
            with self.subTest(name=asset["name"]):
                path=ROOT/"app/src/debug/assets"/asset["path"]
                self.assertEqual(hashlib.sha256(path.read_bytes()).hexdigest(),
                                 asset["sha256"])
                with Image.open(path) as im:
                    self.assertEqual(im.size,tuple(asset["dimensions"]))
                    self.assertEqual(im.mode,"RGBA")
                    self.assertEqual(im.getpixel((0,0))[3],0)
                    self.assertGreater(im.getchannel("A").getbbox()[2],80)
                    if asset["kind"]=="unlettered-spine":
                        self.assertEqual(im.size,(104,542))
                        self.assertLessEqual(im.getchannel("A").getbbox()[2],101)

    def test_wordless_generator_and_old_source_pins(self):
        script=(ROOT/"tools/asset_pipeline/build_ws16bk_future_bookcase_assets.py").read_text()
        self.assertIn("build_ws16bi_library_art import ROOT, blank, WOOD, WOOD_SHA",script)
        self.assertIn("assert sha(WOOD)==WOOD_SHA",script)
        self.assertIn("VARIANTS",script)
        self.assertNotIn("d.text(",script)
        self.assertNotIn("draw.text(",script)
        self.assertNotIn("GridMates Word Search",script)

    def test_future_pages_never_replace_real_books(self):
        for slots,tiers in ((2,2),(4,2),(5,3),(8,2)):
            for owned in (0,1,3,6,8,10,15,30,100,201):
                for portrait in (True,False):
                    total,real,selected=page_model(owned,slots,tiers,portrait)
                    self.assertEqual(selected,list(range(owned)))
                    self.assertEqual(total,real+(2 if portrait else 0))
        self.assertEqual(page_model(3,4,2,True)[:2],(3,1))
        self.assertEqual(page_model(9,4,2,True)[:2],(4,2))

    def test_pager_controls_and_two_future_page_contract(self):
        src=(APP/"WS16ATModularLibraryScene.kt").read_text()
        chrome=(APP/"WS16BKBookcasePager.kt").read_text()
        blank=(APP/"WS16BIUnletteredBlankSpine.kt").read_text()
        bay=(APP/"WS16BHDenseSpineBay.kt").read_text()
        self.assertIn("val realPageCount = maxOf(1,",src)
        self.assertIn("val futureBookcaseCount = if (densePortrait) 2 else 0",src)
        self.assertIn("val pages = realPageCount + futureBookcaseCount",src)
        self.assertIn("val isFutureBookcase = densePortrait && page >= realPageCount",src)
        self.assertIn("books.drop(page * capacity).take(capacity)",src)
        self.assertIn("fullPlaceholderShelf = isFutureBookcase",src)
        self.assertIn('testTag(if (isFutureBookcase) "ws16bk-future-bookcase-$page"',src)
        self.assertIn("WS16BKBookcasePager(",src)
        self.assertNotIn('testTag("ws16at-real-count")',src)
        self.assertNotIn('label = "${books.size}',src)
        self.assertIn("WS16BIAdaptiveControl(",chrome)
        self.assertIn('testTag("ws16bk-page-dots")',chrome)
        self.assertIn('testTag("ws16bk-bookcase-pager")',chrome)
        self.assertIn("minOf(pageCount, 7)",chrome)
        self.assertIn("ws16bk/pager_rail.png",chrome)
        self.assertIn("fullPlaceholderShelf: Boolean = false",bay)
        self.assertIn("if (fullPlaceholderShelf && books.isEmpty()) room",bay)
        self.assertIn("onBookSelected(book.id)",bay)
        self.assertIn('clearAndSetSemantics {}',blank)
        self.assertNotIn('.clickable(',blank)
        for name in ("fern","dusk","clay","garnet",
                     "ochre","teal","violet","slate"):
            self.assertIn('"'+name+'"',blank)

    def test_reproducible_pipeline_and_same_signer_v70(self):
        for workflow in (".github/workflows/ws01-android-qa.yml",
                         ".github/workflows/ws12g-qa-art-lab.yml"):
            s=(ROOT/workflow).read_text()
            self.assertIn("build_ws16bk_future_bookcase_assets.py",s)
            self.assertIn("test_ws16bk_future_bookcases.py",s)
            self.assertIn("verify_ws16bk_apk.py",s)
        gradle=(ROOT/"app/build.gradle.kts").read_text()
        version=re.search(r"versionCode\s*=.*\?:\s*(\d+)",gradle)
        self.assertIsNotNone(version)
        self.assertGreaterEqual(int(version.group(1)),70)
        self.assertIn('applicationIdSuffix = ".qa"',gradle)
        self.assertIn('qa-signing/wordsearch-qa.p12',gradle)

if __name__=="__main__":
    unittest.main()
