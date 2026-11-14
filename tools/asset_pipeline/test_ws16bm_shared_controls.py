"""WS16BM — the Library controls reuse Home's actual GridMates visual system.

No synthetic button artwork is allowed here: compare code-level draw calls to the
identical shared paper/gold renderer used by Home. Icon controls remain native
and accessible; bookcases and saved-game logic must be entirely untouched.
"""
import re
import unittest
from pathlib import Path

ROOT = Path(__file__).resolve().parents[2]
SRC = ROOT / "app/src/main/java/com/playereden/gridmates/wordsearch"

class SharedControlsTests(unittest.TestCase):
    def test_library_and_home_use_the_same_frame_renderer(self):
        shared = (SRC/"GridMatesIllustratedFrames.kt").read_text()
        cabinet = (SRC/"WS16BICabinetChrome.kt").read_text()
        self.assertIn("internal fun DrawScope.drawGridMatesPaperFrame(", shared)
        self.assertIn("drawGridMatesPaperFrame(variant, enabled, pressed)", shared)
        self.assertIn("drawGridMatesPaperFrame(", cabinet)
        self.assertIn("variant = GridMatesFrameVariant.MEDIUM", cabinet)
        self.assertIn("enabled = enabled,", cabinet)
        self.assertIn("pressed = inset > .5f", cabinet)
        self.assertIn("Brush.verticalGradient", shared)
        self.assertIn("Color(0xFFFFFDF4)", shared)
        self.assertIn("Color(0xFFBC944A)", shared)
        # Not an alternate dark-brown button surface.
        self.assertNotIn("Color(0xFFA66D32)", cabinet)
        self.assertNotIn("Color(0xFF28170D)", cabinet)

    def test_forest_home_glyphs_on_shared_24dp_icon_viewport(self):
        cabinet = (SRC/"WS16BICabinetChrome.kt").read_text()
        home = (SRC/"Ws16HomeHeader.kt").read_text()
        self.assertIn("val ink = Forest.copy(", cabinet)
        self.assertIn("val icon = 24f*unit", cabinet)
        self.assertIn("val stroke = 2.3f*unit", cabinet)
        self.assertIn("listOf(.25f,.50f,.75f)", cabinet)
        self.assertIn("listOf(.25f, .50f, .75f)", home)
        self.assertIn("drawLine(Forest", home)
        self.assertIn("pt(.16f,fraction),pt(.84f,fraction)", cabinet)
        self.assertIn("glyph == Ws16AZCabinetGlyph.ART_OPTIONS", cabinet)
        self.assertIn("glyph != Ws16AZCabinetGlyph.NEXT", cabinet)

    def test_taps_and_reduced_motion_survive_uniform_style(self):
        cabinet = (SRC/"WS16BICabinetChrome.kt").read_text()
        self.assertIn("modifier.size(54.dp)", cabinet)
        self.assertIn("role = Role.Button", cabinet)
        self.assertIn("contentDescription = description", cabinet)
        self.assertIn("enabled = enabled", cabinet)
        self.assertIn("onClick = onClick", cabinet)
        self.assertIn("ValueAnimator.areAnimatorsEnabled()", cabinet)
        self.assertIn("durationMillis = if (motionAllowed) 115 else 0", cabinet)
        self.assertIn("collectIsPressedAsState()", cabinet)
        self.assertIn("scaleX = 1f - .052f * inset", cabinet)
        self.assertIn("translationY = 1.5.dp.toPx() * inset", cabinet)
        self.assertIn("WS16BICabinetControl(glyph, description, onClick, modifier, enabled)", cabinet)
        self.assertNotIn("else Ws16AZCabinetControl(", cabinet)

    def test_pager_dots_use_storybook_palette_but_keep_navigation(self):
        pager = (SRC/"WS16BKBookcasePager.kt").read_text()
        self.assertIn("StorybookTokens.warmPaper", pager)
        self.assertIn("color = Gold", pager)
        self.assertIn("Color(0xFF9EAA95)", pager)
        self.assertIn("durationMillis = if (motionAllowed) 165 else 0", pager)
        self.assertIn("enabled = selected > 0", pager)
        self.assertIn("enabled = selected < pageCount - 1", pager)
        self.assertIn("onClick = onPrevious", pager)
        self.assertIn("onClick = onNext", pager)
        self.assertIn('testTag("ws16bk-page-dots")', pager)
        self.assertIn('testTag("ws16bk-bookcase-pager")', pager)

    def test_no_changes_to_bookcase_geometry_art_or_persistence(self):
        scene = (SRC/"WS16ATModularLibraryScene.kt").read_text()
        for part in (
            "val pages = realPageCount + futureBookcaseCount",
            "WS16BHDenseSpineBay(",
            "Ws16BIMountedCabinetShelf()",
            "fullPlaceholderShelf = isFutureBookcase",
            "onBookSelected = onBookSelected",
            "books.drop(page * capacity).take(capacity)",
            "WS16BKBookcasePager("
        ):
            self.assertIn(part, scene)
        gradle = (ROOT/"app/build.gradle.kts").read_text()
        ver = re.search(r"versionCode\s*=.*\?:\s*(\d+)", gradle)
        self.assertIsNotNone(ver)
        self.assertGreaterEqual(int(ver.group(1)), 72)
        self.assertIn('applicationIdSuffix = ".qa"', gradle)
        self.assertIn('qa-signing/wordsearch-qa.p12', gradle)
        for workflow in (".github/workflows/ws01-android-qa.yml",
                         ".github/workflows/ws12g-qa-art-lab.yml"):
            w=(ROOT/workflow).read_text()
            self.assertIn("test_ws16bm_shared_controls.py",w)
            self.assertIn("verify_ws16bk_apk.py",w)

if __name__ == "__main__":
    unittest.main()
