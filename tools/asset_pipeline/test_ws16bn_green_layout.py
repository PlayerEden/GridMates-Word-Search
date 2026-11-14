"""WS16BN: actual Play-green control skin and downward cabinet composition."""
import re
import unittest
from pathlib import Path

ROOT = Path(__file__).resolve().parents[2]
APP = ROOT / "app/src/main/java/com/playereden/gridmates/wordsearch"


def portrait_geometry(width, height):
    top = min(width * 198 / 602, height * .25)
    bottom = min(width * 108 / 602, height * .15)
    portrait = height >= width * 1.7
    stage_drop = 18 if portrait else 0
    pager_drop = min(22, max(0, bottom - 32)) if portrait else 0
    stage_top = top + stage_drop
    stage_bottom = bottom - pager_drop
    return top, bottom, stage_top, stage_bottom, pager_drop


class GreenNavigationTests(unittest.TestCase):
    def test_green_icon_is_same_emblematic_gradient_as_home_play(self):
        src = (APP / "GridMatesIllustratedFrames.kt").read_text()
        chrome = (APP / "WS16BICabinetChrome.kt").read_text()
        home = (APP / "Ws16HomeScreen.kt").read_text()
        self.assertIn("internal enum class GridMatesFrameVariant", src)
        self.assertIn("PRIMARY_PILL, GREEN_ICON", src)
        self.assertIn("val greenSurface = pill || greenIcon", src)
        self.assertIn("brush = if (greenSurface)", src)
        for color in ("0xFF416C49", "0xFF28583F", "0xFF173F32"):
            self.assertEqual(src.count("Color(" + color + ")"), 1)
        self.assertIn("GridMatesIllustratedPill(", home)
        self.assertIn("variant = GridMatesFrameVariant.PRIMARY_PILL", src)
        self.assertIn("variant = GridMatesFrameVariant.GREEN_ICON", chrome)
        self.assertIn("drawGridMatesPaperFrame(", chrome)
        self.assertIn("val ink = Color(0xFFFFF8E8).copy(", chrome)

    def test_disabled_arrow_has_opaque_green_surface_and_readable_dim_ink(self):
        paint = (APP / "GridMatesIllustratedFrames.kt").read_text()
        chrome = (APP / "WS16BICabinetChrome.kt").read_text()
        pager = (APP / "WS16BKBookcasePager.kt").read_text()
        self.assertIn("val opacity = if (greenIcon) 1f else", paint)
        self.assertIn("if (!enabled) {", paint)
        self.assertIn("Color(0x2A0A2118)", paint)
        self.assertIn("alpha = if (enabled) 1f else .38f", chrome)
        self.assertIn("enabled = selected > 0", pager)
        self.assertIn("enabled = selected < pageCount - 1", pager)
        self.assertIn("role = Role.Button", chrome)
        self.assertIn("modifier.size(54.dp)", chrome)
        self.assertIn("onClick = onClick", chrome)
        self.assertIn("ValueAnimator.areAnimatorsEnabled()", chrome)
        self.assertIn("durationMillis = if (motionAllowed) 115 else 0", chrome)
        self.assertNotIn("rememberInfiniteTransition", chrome)

    def test_stage_and_pager_move_together_without_compressing_shelves(self):
        scene = (APP / "WS16ATModularLibraryScene.kt").read_text()
        self.assertIn("val stageDrop = if (densePortrait) 18.dp else 0.dp", scene)
        self.assertIn("22.dp.coerceAtMost((bottom - 32.dp).coerceAtLeast(0.dp))", scene)
        self.assertIn("val stageTop = top + stageDrop", scene)
        self.assertIn("val stageBottom = bottom - pagerDrop", scene)
        self.assertIn("val available = (maxHeight - stageTop - stageBottom)", scene)
        self.assertIn("top = stageTop, bottom = stageBottom)", scene)
        self.assertIn("bottom = (bottom - pagerDrop - 12.dp).coerceAtLeast(12.dp)", scene)
        self.assertIn("val pagerBayHeight = shelfOverhang + pagerGap + pagerRailBudget", scene)
        self.assertIn("WS16BKBookcasePager(", scene)
        for width, height in (
            (320, 570), (320, 680), (360, 640), (360, 760),
            (393, 852), (412, 915), (360, 980), (720, 390)
        ):
            with self.subTest(width=width, height=height):
                top, bottom, new_top, new_bottom, drop = portrait_geometry(width, height)
                self.assertGreaterEqual(new_top, top)
                self.assertLessEqual(new_bottom, bottom)
                self.assertGreaterEqual(new_bottom, 32 if drop else 0)
                old_available = height - top - bottom
                new_available = height - new_top - new_bottom
                self.assertGreater(new_available, 190)
                # In the main handset layout the book content shifts down
                # 18dp while the lower wooden shelf + pager shift ~22dp.
                if height >= width * 1.7:
                    self.assertEqual(new_top - top, 18)
                    self.assertGreater(drop, 0)
                    self.assertGreaterEqual(new_available, old_available - 18)
                else:
                    self.assertEqual(drop, 0)
                    self.assertEqual(new_available, old_available)

    def test_real_books_future_pages_and_artwork_unchanged(self):
        scene = (APP / "WS16ATModularLibraryScene.kt").read_text()
        for required in (
            "val pages = realPageCount + futureBookcaseCount",
            "books.drop(page * capacity).take(capacity)",
            "fullPlaceholderShelf = isFutureBookcase",
            "onBookSelected = onBookSelected",
            "WS16BHDenseSpineBay(",
            "Ws16BIMountedCabinetShelf()",
            'testTag("ws16at-library-title")',
            "showDecorativeBlanks = true",
        ):
            self.assertIn(required, scene)
        for workflow in (".github/workflows/ws01-android-qa.yml",
                         ".github/workflows/ws12g-qa-art-lab.yml"):
            src = (ROOT/workflow).read_text()
            self.assertIn("test_ws16bn_green_layout.py", src)
            self.assertIn("verify_ws16bk_apk.py", src)
        gradle = (ROOT/"app/build.gradle.kts").read_text()
        version = re.search(r"versionCode\s*=.*\?:\s*(\d+)", gradle)
        self.assertIsNotNone(version)
        self.assertGreaterEqual(int(version.group(1)), 73)
        self.assertIn('applicationIdSuffix = ".qa"', gradle)
        self.assertIn("qa-signing/wordsearch-qa.p12", gradle)


if __name__ == "__main__":
    unittest.main()
