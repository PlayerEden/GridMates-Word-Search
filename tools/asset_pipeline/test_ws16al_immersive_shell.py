"""WS16AL immersive empty cabinet: original art, fit geometry, safe QA fallback."""
import unittest
from PIL import Image
from build_ws16ak_ci import ROOT, DEBUG, sha256

BANDS = [("crown",246,0,0), ("upper_bay",450,200,100),
         ("middle_shelf",128,0,0), ("lower_bay",364,210,96),
         ("lower_shelf",112,0,0), ("base",236,0,0)]

class ImmersiveCabinetTests(unittest.TestCase):
    def test_painted_source_modules_still_exact(self):
        for name, height, cap_top, cap_bottom in BANDS:
            with self.subTest(name=name):
                path = DEBUG / (name + ".png")
                with Image.open(path) as art:
                    self.assertEqual((1024,height),art.size)
                    if cap_top:
                        self.assertGreater(height-cap_top-cap_bottom,0)
                        before=art.tobytes()
                        top=art.crop((0,0,1024,cap_top))
                        middle=art.crop((0,cap_top,1024,height-cap_bottom))
                        bottom=art.crop((0,height-cap_bottom,1024,height))
                        restored=Image.new("RGBA",art.size)
                        restored.paste(top,(0,0))
                        restored.paste(middle,(0,cap_top))
                        restored.paste(bottom,(0,height-cap_bottom))
                        self.assertEqual(before,restored.tobytes())
                self.assertEqual(64,len(sha256(path.read_bytes())))

    def test_responsive_geometry_for_supported_portrait_screens(self):
        for w,h in ((320,610),(360,655),(360,760),(412,725)):
            with self.subTest(size=(w,h)):
                scale=min(w/1024,h/1150)
                fixed=722*scale
                flexible=max(h-fixed,0)
                upper=flexible*450/814
                lower=flexible-upper
                self.assertAlmostEqual(fixed+upper+lower,h,places=4)
                self.assertGreater(upper,300*scale)
                self.assertGreater(lower,306*scale)

    def test_debug_only_ui_keeps_working_bookcase(self):
        base=ROOT/"app/src/main/java/com/playereden/gridmates/wordsearch"
        scene=(base/"WS16ALImmersiveEmptyLibraryScene.kt").read_text()
        live=(base/"BookcaseBookshelfScreen.kt").read_text()
        self.assertIn("WS16ALResponsiveBookcaseBackground(Modifier.fillMaxSize())",scene)
        self.assertIn('ws16ak/"+band.name+".png"',scene)
        self.assertIn('ws16al-immersive-empty-library',scene)
        self.assertIn('ws16al-show-books',scene)
        self.assertNotIn("BookcaseSpineArt(",scene)
        self.assertIn("if (fieldNotesArt && emptyCabinetPreview)",live)
        self.assertIn("WS16ALImmersiveEmptyLibraryScene(",live)
        self.assertIn("onShowBooks = { emptyCabinetPreview = false }",live)
        self.assertIn("BookcaseWoodParts(",live)
        self.assertIn("onSelect(book.id)",live)
        self.assertIn("library-pulled-book-dialog",live)
        self.assertIn("library-view-pages",live)

if __name__ == "__main__":
    unittest.main()
