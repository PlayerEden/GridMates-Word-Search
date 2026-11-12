"""WS16BH real spine-only scalable library: geometry and code integration."""
import re
import unittest
from pathlib import Path

ROOT = Path(__file__).resolve().parents[2]
APP = ROOT / "app/src/main/java/com/playereden/gridmates/wordsearch"

def layout(width: float, height: float, font_scale: float, count: int, highres: bool):
    left = width * 126 / 602
    right = width * 120 / 602
    dense = highres and height >= width * 1.7
    hit = 54 if font_scale >= 1.65 else 48
    packed = 42 if font_scale >= 1.65 else 36
    slot = packed if dense else hit
    per_shelf = max(1, min(12, int((width-left-right) / slot)))
    if height < 480:
        tier_count = 1
    elif dense and height >= 740 and count > per_shelf:
        tier_count = 3
    elif height >= 830 and count > 2 * per_shelf:
        tier_count = 3
    else:
        tier_count = 2
    capacity = per_shelf * tier_count
    pages = max(1, (count + capacity - 1) // capacity)
    return dense, per_shelf, tier_count, capacity, pages

class DenseLibraryTests(unittest.TestCase):
    def test_real_only_row_major_page_math_many_catalog_sizes(self):
        for width, height, scale, highres in (
            (320, 640, 1, True), (360, 760, 1, True),
            (412, 892, 1, True), (360, 760, 2, True),
            (412, 950, 2, True), (360, 620, 1, True),
            (720, 390, 1, False), (320, 420, 2, False)
        ):
            for count in (0, 1, 2, 3, 5, 6, 10, 14, 15, 16, 30, 60, 100):
                with self.subTest(width=width,height=height,count=count,scale=scale):
                    dense, slots, tiers, capacity, pages = layout(
                        width, height, scale, count, highres)
                    self.assertGreaterEqual(slots, 1)
                    self.assertIn(tiers, (1, 2, 3))
                    self.assertEqual(capacity, slots * tiers)
                    self.assertEqual(pages, max(1, (count + capacity - 1)//capacity))
                    seen = []
                    for p in range(pages):
                        page = list(range(count))[p*capacity:(p+1)*capacity]
                        for row in range(tiers):
                            seen.extend(page[row*slots:(row+1)*slots])
                    self.assertEqual(seen, list(range(count)))

    def test_woodland_space_garden_pack_first_shelf(self):
        dense, slots, tiers, capacity, pages = layout(360, 760, 1, 3, True)
        self.assertTrue(dense)
        self.assertEqual((slots, tiers, pages), (5, 2, 1))
        self.assertEqual(list(range(3))[:slots], [0, 1, 2])

    def test_tall_screen_three_shelves_and_actual_pagination(self):
        dense, slots, tiers, capacity, pages = layout(360, 760, 1, 30, True)
        self.assertTrue(dense)
        self.assertEqual((slots, tiers, capacity, pages), (5, 3, 15, 2))
        # Font scaling remains safe: fewer volumes visible per real shelf.
        _, slots_large, tier_large, _, _ = layout(360, 760, 2, 30, True)
        self.assertEqual((slots_large, tier_large), (5, 3))

    def test_spines_not_covers_are_portrait_default(self):
        scene = (APP / "WS16ATModularLibraryScene.kt").read_text()
        art = (APP / "WS16BHDenseSpineBay.kt").read_text()
        self.assertIn("val densePortrait = cabinetPortrait", scene)
        self.assertIn("val packedSlot = if (density.fontScale >= 1.65f) 42.dp else 36.dp", scene)
        self.assertIn("densePortrait && maxHeight >= 740.dp && books.size > slots -> 3", scene)
        self.assertIn("val capacity = slots * tierCount", scene)
        self.assertIn("pageBooks.drop(tier * slots).take(slots)", scene)
        self.assertIn("List(tierCount) { shelfTierSpace / tierCount }", scene)
        self.assertIn("WS16BHDenseSpineBay(", scene)
        self.assertNotIn("WS16BGFaceOutBookBay(", scene)
        self.assertIn("if (tierCount >= 2)", scene)
        self.assertIn('testTag("ws16bh-spine-" + book.id)', art)
        self.assertIn('testTag("ws16bh-art-" + book.id)', art)
        self.assertIn("profile = \"curved\"", art)
        self.assertIn("onBookSelected(book.id)", art)
        self.assertNotIn("BookFactoryBlankSpine", art)
        self.assertNotIn("BookcaseFutureSpineArt", art)
        self.assertNotIn("BookArtKind.COVER", art)
        self.assertIn("RoundedCornerShape(50)", art)
        self.assertIn(".width(slot * books.size)", art)
        gradle=(ROOT / "app/build.gradle.kts").read_text()
        self.assertIn("qa-signing/wordsearch-qa.p12",gradle)
        match = re.search(r"versionCode\s*=.*\?:\s*(\d+)", gradle)
        self.assertIsNotNone(match)
        self.assertGreaterEqual(int(match.group(1)), 67)

if __name__ == "__main__":
    unittest.main()
