"""WS16BL v71: static safeguards for accessible tactile cabinet chrome."""
import re
import unittest
from pathlib import Path

ROOT = Path(__file__).resolve().parents[2]
APP = ROOT / "app/src/main/java/com/playereden/gridmates/wordsearch"

class MotionDepthTests(unittest.TestCase):
    def test_cabinet_buttons_press_the_face_not_hit_target(self):
        s = (APP/"WS16BICabinetChrome.kt").read_text()
        self.assertIn("MutableInteractionSource()", s)
        self.assertIn("collectIsPressedAsState()", s)
        self.assertIn("ValueAnimator.areAnimatorsEnabled()", s)
        self.assertIn("animateFloatAsState(", s)
        self.assertIn("durationMillis = if (motionAllowed) 115 else 0", s)
        self.assertIn('label = "ws16bl-cabinet-press"', s)
        self.assertIn("modifier.size(54.dp)", s)
        self.assertIn("role = Role.Button", s)
        self.assertIn("enabled = enabled", s)
        self.assertIn("onClick = onClick", s)
        self.assertIn("indication = null", s)
        self.assertIn('testTag("ws16bl-animated-face")', s)
        self.assertIn("scaleX = 1f - .052f * inset", s)
        self.assertIn("translationY = 1.5.dp.toPx() * inset", s)
        self.assertIn("strokeWidth=(.8f+1.0f*inset)*u", s)
        self.assertNotIn("while (true)", s)
        self.assertNotIn("rememberInfiniteTransition", s)

    def test_pager_motion_is_state_driven_and_mounted_rail_slimmed(self):
        s = (APP/"WS16BKBookcasePager.kt").read_text()
        self.assertIn("modifier.width(242.dp).height(62.dp)", s)
        self.assertIn("ValueAnimator.areAnimatorsEnabled()", s)
        self.assertIn("durationMillis = if (motionAllowed) 165 else 0", s)
        self.assertIn('label = "ws16bl-active-page-dot"', s)
        self.assertIn("minOf(pageCount, 7)", s)
        self.assertIn("Modifier.size(16.dp)", s)
        self.assertIn("enabled = selected > 0", s)
        self.assertIn("enabled = selected < pageCount - 1", s)
        self.assertIn("onClick = onPrevious", s)
        self.assertIn("onClick = onNext", s)
        self.assertIn('description = "Previous bookcase"', s)
        self.assertIn('description = "Next bookcase"', s)
        self.assertIn('contentDescription = "Page', s)
        self.assertNotIn("rememberInfiniteTransition", s)

    def test_book_top_gap_and_subdued_decorative_spines(self):
        scene = (APP/"WS16ATModularLibraryScene.kt").read_text()
        blank = (APP/"WS16BIUnletteredBlankSpine.kt").read_text()
        self.assertIn("top = (top - 88.dp).coerceAtLeast(12.dp)", scene)
        self.assertIn("alpha(0.75f)", blank)
        self.assertIn("clearAndSetSemantics {}", blank)
        self.assertNotIn(".clickable(", blank)
        for snippet in (
            "val pages = realPageCount + futureBookcaseCount",
            "WS16BKBookcasePager(", "fullPlaceholderShelf = isFutureBookcase",
            "books.drop(page * capacity).take(capacity)",
            "onBookSelected = onBookSelected"
        ):
            self.assertIn(snippet, scene)

    def test_existing_ci_and_install_over_version(self):
        for wf in (".github/workflows/ws01-android-qa.yml",
                   ".github/workflows/ws12g-qa-art-lab.yml"):
            data = (ROOT/wf).read_text()
            self.assertIn("test_ws16bl_motion_depth.py", data)
            self.assertIn("verify_ws16bk_apk.py", data)
        gradle = (ROOT/"app/build.gradle.kts").read_text()
        version = re.search(r"versionCode\s*=.*\?:\s*(\d+)", gradle)
        self.assertIsNotNone(version)
        self.assertGreaterEqual(int(version.group(1)), 71)
        self.assertIn("qa-signing/wordsearch-qa.p12",gradle)
        self.assertIn('applicationIdSuffix = ".qa"',gradle)

if __name__=="__main__":
    unittest.main()
