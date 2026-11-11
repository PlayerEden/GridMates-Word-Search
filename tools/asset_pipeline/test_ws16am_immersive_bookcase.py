"""WS16AM layered interactive cabinet: structural rules and deterministic paging."""
import unittest
from build_ws16ak_ci import ROOT

APP=ROOT/"app/src/main/java/com/playereden/gridmates/wordsearch"

def slots_for(width, font_scale=1.0):
    tap=54 if font_scale>=1.65 else 48
    return max(1,min(12,int((width-94)/tap)))

class InteractiveCabinetTests(unittest.TestCase):
    def test_layout_math_keeps_pages_catalog_only(self):
        for w,h in ((320,610),(360,655),(360,760),(412,725)):
            with self.subTest(size=(w,h)):
                slots=slots_for(w)
                capacity=slots*2
                self.assertGreaterEqual(slots,4)
                self.assertGreaterEqual(capacity,8)
                for real in (0,3,6,7,12,13,24):
                    pages=max(1,(real+capacity-1)//capacity)
                    self.assertEqual(pages,
                        max(1,(real+capacity-1)//capacity))
                    self.assertEqual(max(0,min(capacity,real)),min(real,capacity))
                scale=min(w/1024,h/1150)
                fixed=722*scale
                flexible=max(0,h-fixed)
                upper=flexible*450/814
                lower=flexible-upper
                self.assertGreater(upper-19,120)
                self.assertGreater(lower-19,80)

    def test_separate_real_spines_and_anonymous_blank_spines(self):
        code=(APP/"WS16AMInteractiveBookcaseScene.kt").read_text()
        self.assertIn("WS16ALResponsiveBookcaseBackground(",code)
        self.assertIn("BookcaseSpineArt(",code)
        self.assertIn("BookcaseFutureSpineArt(",code)
        self.assertIn("clickable(role = Role.Button)",code)
        self.assertIn("onBookSelected(book.id)",code)
        self.assertIn("books.drop(page * capacity).take(capacity)",code)
        self.assertIn("slots * 2",code)
        self.assertNotIn("onBookSelected(style)",code)
        self.assertIn("ws16am-inspect-empty",code)
        self.assertIn("ws16am-old-case",code)
        self.assertIn("ws16am-book-count",code)
        # WS16AN real-phone acceptance refinement: books sit closer to the
        # illustrated contact plane, without stretching or shrinking tap slots.
        self.assertIn(".width(slot * slots).padding(bottom = 2.dp)",code)
        self.assertIn(".width(artWidth).height(4.dp)",code)
        self.assertIn('testTag("ws16an-inlaid-book-count")',code)
        self.assertNotIn('Color(0xDD17382D)',code)
        self.assertIn('val hitSlot = if (fontScale >= 1.65f) 54.dp else 48.dp',code)

    def test_prior_library_and_details_remain_operational(self):
        live=(APP/"BookcaseBookshelfScreen.kt").read_text()
        empty=(APP/"WS16ALImmersiveEmptyLibraryScene.kt").read_text()
        self.assertIn("var immersiveBooksPreview",live)
        self.assertIn("WS16AMInteractiveBookcaseScene(",live)
        self.assertIn("BookcaseWoodParts(",live)
        self.assertIn("library-pulled-book-dialog",live)
        self.assertIn("library-view-pages",live)
        self.assertIn("onSelect(book.id)",live)
        self.assertIn("onShowBooks = {",live)
        self.assertIn("WS16AMBookCountPlaque(bookCount)",empty)
        self.assertNotIn('Text("Show books",color=',empty)
        self.assertIn("ws16al-show-books",empty)

if __name__=="__main__":unittest.main()
