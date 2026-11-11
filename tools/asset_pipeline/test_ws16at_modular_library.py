"""WS16AT QA contract: cabinet, shelf, book and plaque stay independent."""
import unittest
from pathlib import Path

ROOT = Path(__file__).resolve().parents[2]
APP = ROOT / "app/src/main/java/com/playereden/gridmates/wordsearch"
PIPE = ROOT / "tools/asset_pipeline"

def geometry(width, height, font_scale, real_books):
    hit = 54 if font_scale >= 1.65 else 48
    left = width * (126 / 602)
    right = width * (120 / 602)
    slots = max(1, min(12, int((width - left - right) / hit)))
    tiers = (1 if height < 480 else
             3 if height >= 830 and real_books > 2 * slots else 2)
    capacity = slots * tiers
    return slots, tiers, max(1, (real_books + capacity - 1) // capacity)

class ModularLibraryTests(unittest.TestCase):
    def test_catalog_only_paging_and_adaptive_rows(self):
        for width,height in ((320,610),(360,760),(412,860),(720,390)):
            for scale in (1.0, 2.0):
                for real in (0,1,3,6,7,12,13,24,48):
                    with self.subTest(width=width,height=height,scale=scale,real=real):
                        slots,tiers,pages = geometry(width,height,scale,real)
                        self.assertGreaterEqual(slots,1)
                        self.assertIn(tiers,(1,2,3))
                        self.assertEqual(pages, max(1, (real + slots*tiers-1)//(slots*tiers)))
                        self.assertGreaterEqual(pages * slots*tiers, real)

    def test_five_part_art_gate_and_architecture(self):
        code=(APP/"WS16ATModularLibraryScene.kt").read_text()
        for name in ("top_crown","left_pilaster","right_pilaster",
                     "recessed_backboard","bottom_base"):
            self.assertIn('"' + name + '"',code)
        self.assertIn("internal fun hasWs16atCabinetArt(context: Context)",code)
        self.assertIn("WS16AMSpineBay(",code)
        self.assertIn("WS16ATCabinetLayers(foreground = false",code)
        self.assertIn("WS16ATCabinetLayers(foreground = true",code)
        self.assertIn(".padding(start = leftWidth, end = rightWidth, top = top, bottom = bottom)",code)
        self.assertIn(".coerceAtMost(maxHeight * 0.25f)",code)
        self.assertIn(".coerceAtMost(maxHeight * 0.15f)",code)
        self.assertLess(code.index("WS16ATCabinetLayers(foreground = false, modifier = Modifier.fillMaxSize())"),
                        code.index("HorizontalPager("))
        self.assertLess(code.index("HorizontalPager("),
                        code.index("WS16ATCabinetLayers(foreground = true, modifier = Modifier.fillMaxSize())"))
        self.assertLess(code.index("WS16ATCabinetLayers(foreground = true, modifier = Modifier.fillMaxSize())"),
                        code.index('Text("Your Library"'))
        self.assertIn("Ws16EStorybookShelf()",code)
        self.assertIn("books.drop(page * capacity).take(capacity)",code)
        self.assertIn("slot = hitSlot",code)
        self.assertIn('testTag("ws16at-real-count")',code)
        self.assertIn('testTag("ws16at-pagination")',code)
        self.assertNotIn("BookFactoryBlankSpine(",code)

    def test_existing_details_stay_in_charge_and_no_qa_without_art(self):
        live=(APP/"BookcaseBookshelfScreen.kt").read_text()
        old=(APP/"WS16AMInteractiveBookcaseScene.kt").read_text()
        self.assertIn("modularPreview && modularAssetsAvailable",live)
        self.assertIn("hasWs16atCabinetArt(context)",live)
        self.assertIn("modularPreview = false",live)
        self.assertIn("onModularCase = if (modularAssetsAvailable)",live)
        self.assertIn("library-pulled-book-dialog",live)
        self.assertIn("library-view-pages",live)
        self.assertIn("onModularCase: (() -> Unit)? = null",old)
        self.assertIn("onBookSelected(book.id)",old)
        self.assertIn("internal fun WS16AMSpineBay(",old)

    def test_importer_is_debug_only_and_rejects_baked_shelves(self):
        importer=(PIPE/"import_ws16as_modular_library.py").read_text()
        self.assertIn('app/src/debug/assets/ws16as/shell',importer)
        self.assertIn("shelves_baked_into_shell",importer)
        self.assertIn("plaques_baked_into_shell",importer)
        self.assertIn("composite.tobytes()",importer)
        self.assertIn("release_approved",importer)
        self.assertNotIn("app/src/main/assets/ws16as",importer)

if __name__=="__main__":
    unittest.main()
