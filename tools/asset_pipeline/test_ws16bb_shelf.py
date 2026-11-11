"""WS16BB shelf art, library spacing and reusable illustrated controls."""
import re
import unittest
from pathlib import Path
from import_ws16bb_matched_shelf import ROOT, EXPECTED_SHA, EXPECTED_SIZE, verify

APP=ROOT/"app/src/main/java/com/playereden/gridmates/wordsearch"
class CabinetRefinementTests(unittest.TestCase):
    def test_independent_transparent_shelf_source(self):
        meta=verify()
        self.assertEqual(meta["source_sha256"],EXPECTED_SHA)
        self.assertEqual((meta["width"],meta["height"]),EXPECTED_SIZE)
        self.assertTrue(meta["qa_only"])
        self.assertFalse(meta["release_approved"])
        self.assertFalse(meta["books_or_text_baked_in"])

    def test_matched_shelf_is_used_with_portrait_cabinet(self):
        scene=(APP/"WS16ATModularLibraryScene.kt").read_text()
        shelf=(APP/"Ws16BBMatchedCabinetShelf.kt").read_text()
        self.assertIn("Ws16BBMatchedCabinetShelf()",scene)
        self.assertIn("Ws16EStorybookShelf()",scene)
        self.assertIn("hasWs16baCabinetArt",scene)
        self.assertIn("ws16bb/shelf.png",shelf)
        self.assertIn("125f / 586f",shelf)
        self.assertIn("paintedHeight - 42.dp",shelf)
        self.assertIn("ws16bb-matched-cabinet-shelf",shelf)

    def test_spaced_library_and_separate_count(self):
        scene=(APP/"WS16ATModularLibraryScene.kt").read_text()
        self.assertIn("val countBayHeight = shelfOverhang + countGap + countPlaqueBudget",scene)
        self.assertIn("shelfTierSpace * 0.54f",scene)
        self.assertIn("shelfTierSpace * 0.46f",scene)
        self.assertIn("ws16bc-count-bay",scene)
        self.assertIn("showFutureVolumes = tier > 0",scene)
        self.assertIn('label = "Coming soon"',scene)
        self.assertIn("futureTreatment = true",scene)
        self.assertIn("if (tierCount == 2)",scene)
        self.assertIn("ws16at-real-count",scene)
        self.assertIn("onBookSelected = onBookSelected",scene)

    def test_future_books_are_not_real_catalog_items(self):
        src=(APP/"WS16AMInteractiveBookcaseScene.kt").read_text()
        self.assertIn("futureTreatment: Boolean = false",src)
        self.assertIn("isFutureVisible",src)
        self.assertIn("alpha(if (futureTreatment) .34f else 1f)",src)
        self.assertIn("onBookSelected(book.id)",src)
        self.assertNotIn("onBookSelected(style)",src)

    def test_shared_frames_extend_into_profile_and_collection(self):
        profile=(APP/"Ws16CProfileHub.kt").read_text()
        collection=(APP/"CollectionScreen.kt").read_text()
        self.assertIn("variant = GridMatesFrameVariant.FEATURE",profile)
        self.assertIn("variant = GridMatesFrameVariant.MEDIUM",profile)
        for tag in ("ws16c-profile-collection","ws16c-profile-records","ws16c-profile-my-puzzles"):
            self.assertIn(tag,profile)
        self.assertIn("ws16bb-collection-heading",collection)
        self.assertIn("variant = GridMatesFrameVariant.PANEL",collection)
        self.assertIn("StorybookHeading(",collection)

    def test_install_over_stable_signing(self):
        gradle=(ROOT/"app/build.gradle.kts").read_text()
        match = re.search(r'versionCode\s*=\s*providers\.gradleProperty\("qaVersionCode"\)\.orNull\?\.toInt\(\)\s*\?:\s*(\d+)', gradle)
        self.assertIsNotNone(match)
        self.assertGreaterEqual(int(match.group(1)), 61)
        self.assertIn("qa-signing/wordsearch-qa.p12",gradle)
        self.assertIn('applicationIdSuffix = ".qa"',gradle)
        for wf in ("ws01-android-qa.yml","ws12g-qa-art-lab.yml"):
            content=(ROOT/".github/workflows"/wf).read_text()
            self.assertIn("import_ws16bb_matched_shelf.py",content)
            self.assertIn("verify_ws16bb_apk.py",content)
            self.assertIn("75cc3c896247d71fbd51f7a3ed41f90c8218a8a5a70df8c0f6b494b5cc23d419",content)

if __name__=="__main__":
    unittest.main()
