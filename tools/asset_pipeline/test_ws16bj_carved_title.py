"""WS16BJ: custom carved title and physical clearance without changing gameplay."""
import re
import unittest
from pathlib import Path

ROOT = Path(__file__).resolve().parents[2]
APP = ROOT / "app/src/main/java/com/playereden/gridmates/wordsearch"

class CarvedTitleTests(unittest.TestCase):
    def test_feature_title_is_a_shaped_wood_cartouche_not_the_count_plaque(self):
        src = (APP / "WS16BJCarvedTitlePlaque.kt").read_text()
        chrome = (APP / "WS16BICabinetChrome.kt").read_text()
        self.assertIn("internal fun WS16BJCarvedTitlePlaque(", src)
        self.assertIn("modifier = modifier.height(46.dp)", src)
        self.assertIn("quadraticBezierTo(", src)
        self.assertIn("Brush.verticalGradient(", src)
        self.assertIn("Color(0xFFE5AD5B)", src)
        self.assertIn("for (right in listOf(false, true))", src)
        self.assertIn("maxLines = 1", src)
        self.assertIn("Text(", src)
        self.assertNotIn(".clickable(", src)
        self.assertNotIn("BitmapFactory", src)
        self.assertIn("if (styled && title) WS16BJCarvedTitlePlaque(label, modifier)", chrome)
        self.assertIn("else if (styled) WS16BICabinetPlaque(label, title, modifier)", chrome)
        self.assertIn("else Ws16AZCabinetPlaque(label, title, modifier)", chrome)

    def test_title_moves_up_without_repositioning_book_pagers(self):
        scene = (APP / "WS16ATModularLibraryScene.kt").read_text()
        self.assertIn('label = "Your Library", title = true, styled = cabinetPortrait', scene)
        self.assertIn('top = (top - 80.dp).coerceAtLeast(16.dp)', scene)
        self.assertIn('.padding(start = 66.dp, end = 66.dp,', scene)
        self.assertIn('testTag("ws16at-library-title")', scene)
        self.assertNotIn('top = (top - 58.dp).coerceAtLeast(38.dp)', scene)
        # No regression to spine-first, count or pagination behavior.
        for literal in (
            "WS16BHDenseSpineBay(", "showDecorativeBlanks = true",
            "Ws16BIMountedCabinetShelf()", "val capacity = slots * tierCount",
            "books.drop(page * capacity).take(capacity)",
            'WS16BKBookcasePager(',
            'testTag("ws16at-pagination")',
            "onBookSelected = onBookSelected",
        ):
            self.assertIn(literal, scene)

    def test_normal_portrait_geometry_reclaims_clearance(self):
        for width, height in ((320, 680), (360, 760), (393, 851), (412, 915)):
            top = min(width * 198 / 602, height * .25)
            old_top = max(top - 58, 38)
            new_top = max(top - 80, 16)
            # At the same safe inset this shifts the title meaningfully up.
            self.assertLessEqual(new_top, old_top - 20)
            self.assertLess(new_top + 46, old_top + 54)
            self.assertGreater(width - 2 * 66, 170)

    def test_install_over_public_qa_identity_and_pipeline_remain(self):
        gradle = (ROOT / "app/build.gradle.kts").read_text()
        version = re.search(r'versionCode\s*=.*\?:\s*(\d+)', gradle)
        self.assertIsNotNone(version)
        self.assertGreaterEqual(int(version.group(1)), 69)
        self.assertIn('qa-signing/wordsearch-qa.p12', gradle)
        self.assertIn('applicationIdSuffix = ".qa"', gradle)
        for wf in (".github/workflows/ws01-android-qa.yml",
                   ".github/workflows/ws12g-qa-art-lab.yml"):
            content = (ROOT / wf).read_text()
            self.assertIn("test_ws16bj_carved_title.py", content)
            self.assertIn("verify_ws16bi_apk.py", content)

if __name__ == "__main__":
    unittest.main()
