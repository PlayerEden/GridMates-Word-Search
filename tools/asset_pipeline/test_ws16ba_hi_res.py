"""WS16BA higher-resolution art source, cropping and runtime integration."""
import unittest
from pathlib import Path
from PIL import Image
from build_ws16ba_hi_res import ROOT,SOURCE,OUTPUT,SOURCE_SHA,DIMENSIONS,CROPS,sha,build
class HighResCabinetTests(unittest.TestCase):
 def test_source_and_pixel_perfect_five_part_assembly(self):
  self.assertEqual(sha(SOURCE.read_bytes()),SOURCE_SHA)
  meta=build()
  self.assertEqual(len(meta["assets"]),5)
  self.assertTrue(meta["qa_only"]);self.assertFalse(meta["release_approved"])
  self.assertFalse(meta["on_device_approved"])
  self.assertFalse(meta["books_shelves_text_plaques_baked_in"])
  original=Image.open(SOURCE).convert("RGB")
  reconstructed=Image.new("RGB",DIMENSIONS)
  for name,bounds in CROPS.items():
   filename=name+".png";path=OUTPUT/filename
   self.assertEqual(sha(path.read_bytes()),meta["assets"][filename]["sha256"])
   piece=Image.open(path).convert("RGB")
   self.assertEqual(piece.size,(bounds[2]-bounds[0],bounds[3]-bounds[1]))
   reconstructed.paste(piece,(bounds[0],bounds[1]))
  self.assertEqual(original.tobytes(),reconstructed.tobytes())
  self.assertGreater(DIMENSIONS[0],674)
  self.assertGreater(DIMENSIONS[1],1536)
 def test_portrait_uses_highres_with_old_art_fallback(self):
  scene=(ROOT/"app/src/main/java/com/playereden/gridmates/wordsearch/WS16ATModularLibraryScene.kt").read_text()
  self.assertIn("hasWs16baCabinetArt",scene)
  self.assertIn("ws16ba/shell",scene)
  self.assertIn("ws16as/shell",scene)
  self.assertIn("maxHeight >= maxWidth * 1.7f",scene)
  self.assertIn("ContentScale.FillBounds else ContentScale.Crop",scene)
  gradle=(ROOT/"app/build.gradle.kts").read_text()
  # Version must advance for install-over; avoid freezing this to v60.
  version_line = next(x for x in gradle.splitlines() if "versionCode = " in x)
  self.assertGreaterEqual(int(version_line.split("?:")[-1].strip()), 60)
  self.assertIn('applicationIdSuffix = ".qa"',gradle)
  self.assertIn("qa-signing/wordsearch-qa.p12",gradle)
if __name__=="__main__":unittest.main()
