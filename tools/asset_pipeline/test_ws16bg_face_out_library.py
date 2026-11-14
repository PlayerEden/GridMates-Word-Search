"""WS16BG front-facing physical cover gallery contract and real catalog safety."""
import json
import re
import unittest
from pathlib import Path
from PIL import Image

ROOT = Path(__file__).resolve().parents[2]
APP = ROOT / "app/src/main/java/com/playereden/gridmates/wordsearch"
COVERS = ROOT / "app/src/debug/assets/ws16ae/covers"
MANIFEST = ROOT / "app/src/debug/assets/ws16ae/manifest.json"

class FaceOutLibraryTests(unittest.TestCase):
    def test_existing_painted_cover_family_and_safe_visible_bounds(self):
        info = json.loads(MANIFEST.read_text())
        self.assertTrue(info["qa_only"])
        self.assertFalse(info["release_approved"])
        self.assertFalse(info["owner_approved"])
        cover_entries = [x for x in info["assets"] if x["role"] == "cover"]
        self.assertEqual({"woodland", "space", "garden"},
                         {x["theme"] for x in cover_entries})
        for entry in cover_entries:
            path = ROOT / entry["output"]
            with self.subTest(theme=entry["theme"]), Image.open(path) as cover:
                self.assertEqual(cover.size, (480, 640))
                self.assertEqual(cover.mode, "RGBA")
                a = cover.getchannel("A").getbbox()
                self.assertGreaterEqual(a[0], 10)
                self.assertGreaterEqual(a[1], 63)
                self.assertLessEqual(a[2], 470)
                self.assertLessEqual(a[3], 576)
                cropped = cover.crop((10, 63, 470, 576))
                self.assertEqual(cropped.size, (460, 513))
                self.assertGreater(cropped.getchannel("A").getbbox()[2], 450)
                self.assertGreater(cropped.getchannel("A").getbbox()[3], 500)

    def test_real_cover_art_has_one_depth_edge_and_no_fake_inventory(self):
        art = (APP / "WS16BGFaceOutBookBay.kt").read_text()
        self.assertIn('context.assets.open("ws16ae/covers/$slug.png")', art)
        self.assertIn("Bitmap.createBitmap(original, 10, 63, 460, 513)", art)
        self.assertIn("val physicalRatio = 460f / 513f", art)
        self.assertIn("BookArtKind.COVER", art)  # release/missing asset fallback
        self.assertIn("onBookSelected(book.id)", art)
        self.assertIn('testTag("ws16bg-cover-" + book.id)', art)
        self.assertIn('testTag("ws16bg-art-" + book.id)', art)
        self.assertIn("width(6.dp).fillMaxHeight()", art)
        self.assertIn("rotationZ = lean", art)
        self.assertIn("rotationY = if (index == 0) -5f else 5f", art)
        self.assertNotIn("BookcaseFutureSpineArt", art)
        self.assertNotIn("BookFactoryBlankSpine", art)
        self.assertNotIn("onBookSelected(style)", art)

    def test_face_out_preview_is_preserved_but_not_the_default_library(self):
        # WS16BG was a useful real-cover A/B study. Owner explicitly
        # selected scalable densely packed spines for the actual Library.
        archived = (APP / "WS16BGFaceOutBookBay.kt").read_text()
        scene = (APP / "WS16ATModularLibraryScene.kt").read_text()
        self.assertIn("internal fun WS16BGFaceOutBookBay(", archived)
        self.assertIn("WS16BHDenseSpineBay(", scene)
        self.assertNotIn("WS16BGFaceOutBookBay(", scene)
        self.assertIn("WS16AMSpineBay(", scene)
        self.assertIn("books.drop(page * capacity).take(capacity)", scene)
        self.assertIn("onBookSelected = onBookSelected", scene)
        gradle = (ROOT / "app/build.gradle.kts").read_text()
        match = re.search(r"versionCode\s*=.*\?:\s*(\d+)", gradle)
        self.assertIsNotNone(match)
        self.assertGreaterEqual(int(match.group(1)), 67)
        self.assertIn("qa-signing/wordsearch-qa.p12", gradle)

if __name__ == "__main__":
    unittest.main()
