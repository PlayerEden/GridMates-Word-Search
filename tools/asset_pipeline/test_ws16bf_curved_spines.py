"""WS16BF clean rounded spine assets: no edge flecks or second panels."""
import json
import unittest
from pathlib import Path
from PIL import Image
from build_ws16bf_curved_spines import ROOT, SRC, DEST, REAL, W, H, sha, make

class CurvedSpineTests(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.manifest = json.loads((DEST / "manifest.json").read_text())

    def test_pinned_source_and_exact_three_debug_only_artworks(self):
        info = self.manifest
        self.assertTrue(info["qa_only"])
        self.assertFalse(info["owner_approved"])
        self.assertFalse(info["release_approved"])
        self.assertFalse(info["new_backing_panel"])
        self.assertEqual(set(REAL), {a["name"] for a in info["assets"]})
        for a in info["assets"]:
            with self.subTest(a=a["name"]):
                source = ROOT / a["source"]
                output = ROOT / a["output"]
                self.assertEqual(sha(source.read_bytes()), a["source_sha256"])
                self.assertEqual(sha(output.read_bytes()), a["sha256"])
                with Image.open(output) as im:
                    self.assertEqual(im.mode, "RGBA")
                    self.assertEqual(im.size, (W, H))
                    alpha = im.getchannel("A")
                    self.assertEqual(alpha.getbbox(), (3, 2, 101, 540))
                    # No colored one-pixel fringe outside the physical binding.
                    for x in (0, 1, 2, 101, 102, 103):
                        self.assertEqual(alpha.crop((x, 0, x+1, H)).getbbox(), None)
                    for y in (0, 1, 540, 541):
                        self.assertEqual(alpha.crop((0, y, W, y+1)).getbbox(), None)
                    self.assertGreater(alpha.getpixel((50, 200)), 240)
                    self.assertGreater(alpha.getpixel((50, 350)), 240)

    def test_repeatable_single_face_and_print_is_not_stretched(self):
        for name in REAL:
            with self.subTest(name=name):
                original = Image.open(SRC / (name + ".png")).convert("RGBA")
                product = Image.open(DEST / "spines" / (name + ".png")).convert("RGBA")
                self.assertEqual(make(name, original).tobytes(), product.tobytes())
                # Same source pixel coordinates: no scaling of icons/lettering.
                self.assertEqual(original.size, product.size)
                # Art mid-field is kept light enough to read the original words.
                x, y = 51, 130
                self.assertNotEqual(product.getpixel((x, y))[3], 0)

    def test_single_binding_runtime_and_workflow_coverage(self):
        art = (ROOT / "app/src/main/java/com/playereden/gridmates/wordsearch/BookcaseWoodArtwork.kt").read_text()
        bay = (ROOT / "app/src/main/java/com/playereden/gridmates/wordsearch/WS16AMInteractiveBookcaseScene.kt").read_text()
        scene = (ROOT / "app/src/main/java/com/playereden/gridmates/wordsearch/WS16ATModularLibraryScene.kt").read_text()
        self.assertIn('"curved" -> loadBookcaseAsset(context, "ws16bf/spines/$name.png")', art)
        self.assertIn('"curved" -> 104f / 542f', bay)
        self.assertIn("useCurvedSpines = cabinetPortrait", scene)
        self.assertIn("drawContactShadow = cabinetPortrait", scene)
        for f in ("ws01-android-qa.yml", "ws12g-qa-art-lab.yml"):
            workflow = (ROOT / ".github/workflows" / f).read_text()
            self.assertIn("build_ws16bf_curved_spines.py", workflow)
            self.assertIn("verify_ws16bf_apk.py", workflow)

if __name__ == "__main__":
    unittest.main()
