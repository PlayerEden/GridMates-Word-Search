"""WS16AT QA contract: cabinet, shelf, book and plaque stay independent."""
import re
import unittest
from pathlib import Path

ROOT = Path(__file__).resolve().parents[2]
APP = ROOT / "app/src/main/java/com/playereden/gridmates/wordsearch"
PIPE = ROOT / "tools/asset_pipeline"

def geometry(width, height, font_scale, real_books):
    hit = 54 if font_scale >= 1.65 else 48
    left = width * (126 / 602)
    right = width * (120 / 602)
    slots = max(1, min(12, int((width - left - right) / hit)))
    tiers = (1 if height < 480 else
             3 if height >= 830 and real_books > 2 * slots else 2)
    capacity = slots * tiers
    return slots, tiers, max(1, (real_books + capacity - 1) // capacity)

class ModularLibraryTests(unittest.TestCase):
    def test_catalog_only_paging_and_adaptive_rows(self):
        for width,height in ((320,610),(360,760),(412,860),(720,390)):
            for scale in (1.0, 2.0):
                for real in (0,1,3,6,7,12,13,24,48):
                    with self.subTest(width=width,height=height,scale=scale,real=real):
                        slots,tiers,pages = geometry(width,height,scale,real)
                        self.assertGreaterEqual(slots,1)
                        self.assertIn(tiers,(1,2,3))
                        self.assertEqual(pages, max(1, (real + slots*tiers-1)//(slots*tiers)))
                        self.assertGreaterEqual(pages * slots*tiers, real)

    def test_five_part_art_gate_and_architecture(self):
        code=(APP/"WS16ATModularLibraryScene.kt").read_text()
        for name in ("top_crown","left_pilaster","right_pilaster",
                     "recessed_backboard","bottom_base"):
            self.assertIn('"' + name + '"',code)
        self.assertIn("internal fun hasWs16atCabinetArt(context: Context)",code)
        self.assertIn("WS16AMSpineBay(",code)
        self.assertIn("WS16ATCabinetLayers(foreground = false",code)
        self.assertIn("WS16ATCabinetLayers(foreground = true",code)
        self.assertIn(".padding(start = leftWidth, end = rightWidth, top = top, bottom = bottom)",code)
        self.assertIn(".coerceAtMost(maxHeight * 0.25f)",code)
        self.assertIn(".coerceAtMost(maxHeight * 0.15f)",code)
        self.assertLess(code.index("WS16ATCabinetLayers(foreground = false, modifier = Modifier.fillMaxSize())"),
                        code.index("HorizontalPager("))
        self.assertLess(code.index("HorizontalPager("),
                        code.index("WS16ATCabinetLayers(foreground = true, modifier = Modifier.fillMaxSize())"))
        self.assertLess(code.index("WS16ATCabinetLayers(foreground = true, modifier = Modifier.fillMaxSize())"),
                        code.index('label = "Your Library"'))
        self.assertIn("Ws16BIMountedCabinetShelf()",code)
        self.assertIn("Ws16EStorybookShelf()",code)
        self.assertIn("val shelfContactDepth = 42.dp",code)
        self.assertIn("futureTreatment = true",code)
        self.assertIn("height = (tierHeight - shelfContactDepth)",code)
        self.assertIn("headClearance = if (balancedSparseLayout) 34.dp else 10.dp",code)
        self.assertIn("books.drop(page * capacity).take(capacity)",code)
        # Default layout keeps 48/54dp hit slots; the sparse 2-tier showcase
        # explicitly uses larger 60dp catalog-only hit slots.
        self.assertIn("slot = if (balancedSparseLayout) 60.dp else hitSlot", code)
        self.assertNotIn('testTag("ws16at-real-count")',code)
        self.assertIn("WS16BKBookcasePager(",code)
        self.assertIn('testTag("ws16at-pagination")',code)
        self.assertNotIn("BookFactoryBlankSpine(",code)

    def test_cabinet_proportion_and_install_over_signing_contract(self):
        scene = (APP / 'WS16ATModularLibraryScene.kt').read_text()
        self.assertEqual(scene.count('contentScale = middleScale'), 3)
        self.assertEqual(scene.count('contentScale = ContentScale.FillBounds'), 2)
        self.assertIn('ContentScale.FillBounds else ContentScale.Crop', scene)
        gradle = (ROOT / 'app/build.gradle.kts').read_text()
        self.assertIn('qa-signing/wordsearch-qa.p12', gradle)
        self.assertIn('signingConfig = signingConfigs.getByName("wordSearchQa")', gradle)
        self.assertIn('applicationIdSuffix = ".qa"', gradle)
        version = re.search(r'versionCode\s*=\s*providers\.gradleProperty\("qaVersionCode"\)\.orNull\?\.toInt\(\)\s*\?:\s*(\d+)', gradle)
        self.assertIsNotNone(version, "QA default versionCode must remain explicit")
        self.assertGreaterEqual(int(version.group(1)), 61)
        for name in ('ws01-android-qa.yml', 'ws12g-qa-art-lab.yml'):
            workflow = (ROOT / '.github/workflows' / name).read_text()
            self.assertIn('75cc3c896247d71fbd51f7a3ed41f90c8218a8a5a70df8c0f6b494b5cc23d419', workflow)
            self.assertIn('apksigner', workflow)

    def test_ws16bc_shelf_anchored_count_and_future_separation(self):
        scene = (APP / "WS16ATModularLibraryScene.kt").read_text()
        spines = (APP / "WS16AMInteractiveBookcaseScene.kt").read_text()
        self.assertIn("val shelfOverhang = if (cabinetPortrait)", scene)
        self.assertIn("val pagerBayHeight = shelfOverhang + pagerGap + pagerRailBudget", scene)
        self.assertIn('testTag("ws16bk-navigation-bay")', scene)
        self.assertIn("showFutureVolumes = false", scene)
        self.assertIn("val densePortrait = cabinetPortrait", scene)
        self.assertIn("val packedSlot = if (density.fontScale >= 1.65f) 52.dp else 48.dp", scene)
        self.assertIn("densePortrait && maxHeight >= 960.dp && books.size > slots -> 3", scene)
        self.assertIn("val capacity = slots * tierCount", scene)
        self.assertIn("List(tierCount) { shelfTierSpace / tierCount }", scene)
        self.assertIn("val balancedSparseLayout = !densePortrait && tierCount == 2", scene)
        self.assertIn("WS16BHDenseSpineBay(", scene)
        self.assertIn("slots = if (balancedSparseLayout) shelfBooks.size else slots", scene)
        self.assertIn("slot = if (balancedSparseLayout) 60.dp else hitSlot", scene)
        self.assertIn("if (tier == 0) pageBooks.take(topBooks) else pageBooks.drop(topBooks)", scene)
        self.assertIn("showFutureVolumes: Boolean = true", spines)

    def test_ws16be_single_binding_and_no_stacked_shadow(self):
        scene = (APP / "WS16ATModularLibraryScene.kt").read_text()
        bay = (APP / "WS16AMInteractiveBookcaseScene.kt").read_text()
        art = (APP / "BookcaseWoodArtwork.kt").read_text()
        self.assertIn("useCleanSpines = cabinetPortrait", scene)
        self.assertIn("useCurvedSpines = cabinetPortrait", scene)
        self.assertIn("drawContactShadow = cabinetPortrait", scene)
        self.assertIn("useCleanSpines: Boolean = false", bay)
        self.assertIn("drawContactShadow: Boolean = true", bay)
        self.assertIn("headClearance: Dp = 10.dp", bay)
        self.assertIn("(maxHeight - headClearance)", bay)
        self.assertIn('val profile = if (useCurvedSpines) "curved" else if (useCleanSpines) "clean" else', bay)
        self.assertIn('"clean" -> 104f / 542f', bay)
        self.assertIn('if (drawContactShadow && (book != null || isFutureVisible))', bay)
        self.assertIn('"clean" -> loadBookcaseAsset(context, "ws16ai/spines/$name.png")', art)
        self.assertIn('"tall", "standard" ->', art)
        # Never silently replace the pinned 12-image wider family: fallback
        # stays source-pinned, while only the modular portrait asks for clean.
        self.assertIn('"ws16ao/spines/$profile/$name.png"', art)

    def test_ws16az_reusable_library_controls(self):
        scene=(APP/"WS16ATModularLibraryScene.kt").read_text()
        kit=(APP/"Ws16AZCabinetUiKit.kt").read_text()
        self.assertIn("internal fun Ws16AZCabinetPlaque(", kit)
        self.assertIn("internal fun Ws16AZCabinetControl(", kit)
        self.assertIn("size(48.dp)", kit)
        self.assertIn("Role.Button", kit)
        self.assertIn("contentDescription = description", kit)
        self.assertIn('label = "Your Library"', scene)
        self.assertNotIn('testTag("ws16at-real-count")', scene)
        pager=(APP/"WS16BKBookcasePager.kt").read_text()
        for part in ("BACK", "ART_OPTIONS"):
            self.assertIn("Ws16AZCabinetGlyph."+part, scene)
        for part in ("PREVIOUS", "NEXT"):
            self.assertIn("Ws16AZCabinetGlyph."+part,pager)
        self.assertNotIn('Text("‹"', scene)
        self.assertNotIn('Text("⋯"', scene)

    def test_existing_details_stay_in_charge_and_no_qa_without_art(self):
        live=(APP/"BookcaseBookshelfScreen.kt").read_text()
        old=(APP/"WS16AMInteractiveBookcaseScene.kt").read_text()
        self.assertIn("modularPreview && modularAssetsAvailable",live)
        self.assertIn("hasWs16atCabinetArt(context)",live)
        self.assertIn("modularPreview = false",live)
        self.assertIn("onModularCase = if (modularAssetsAvailable)",live)
        self.assertIn("library-pulled-book-dialog",live)
        self.assertIn("library-view-pages",live)
        self.assertIn("onModularCase: (() -> Unit)? = null",old)
        self.assertIn("onBookSelected(book.id)",old)
        self.assertIn("internal fun WS16AMSpineBay(",old)

    def test_ws16aw_full_bleed_shell_and_safe_native_overlays(self):
        shell=(APP/"GridMatesShell.kt").read_text()
        flow=(APP/"BookLibraryFlow.kt").read_text()
        browse=(APP/"PuzzleBookshelfScreen.kt").read_text()
        case=(APP/"BookcaseBookshelfScreen.kt").read_text()
        modular=(APP/"WS16ATModularLibraryScene.kt").read_text()

        # The router passes insets instead of cropping the entire book route.
        self.assertIn("Box(Modifier.fillMaxSize()) {\n                    BookLibraryFlow(", shell)
        self.assertIn("safeContentPadding = contentPadding", shell)
        # Book gameplay, detail, legacy browsing and unavailable-state UI
        # still stay above Android system gesture/navigation areas.
        self.assertIn("Box(Modifier.fillMaxSize().padding(safeContentPadding)) {", flow)
        self.assertIn("Column(Modifier.fillMaxSize().padding(safeContentPadding))", flow)
        self.assertIn("modifier = modifier.padding(safeContentPadding)", browse)
        self.assertIn("safeContentPadding = safeContentPadding", case)
        self.assertIn("Modifier.fillMaxSize().padding(safeContentPadding)", case)

        # Native controls are inset; the five painterly wood pieces and
        # independent shelf/book compositor use the full parent geometry.
        self.assertIn('testTag("ws16aw-safe-overlays")', modular)
        self.assertIn("Box(Modifier.fillMaxSize().padding(safeContentPadding)", modular)
        self.assertLess(modular.index('WS16ATCabinetLayers(foreground = false'),
                        modular.index('testTag("ws16aw-safe-overlays")'))
        self.assertLess(modular.index('WS16ATCabinetLayers(foreground = true'),
                        modular.index('testTag("ws16aw-safe-overlays")'))
        self.assertIn('testTag("ws16at-real-count")', modular)
        self.assertIn('testTag("ws16at-back")', modular)
        self.assertIn('testTag("ws16at-pagination")', modular)

    def test_real_original_github_source_sheet_and_five_parts(self):
        source = ROOT / "design/visual-system/masters/ws16as"
        if not (source / "manifest.json").is_file():
            self.skipTest("Original five-piece art not in this checkpoint")
        from assemble_ws16as_github_kit import verify_original_sources
        manifest, sheet, pieces = verify_original_sources()
        self.assertEqual(len(pieces), 5)
        self.assertIn(b"ws16as.modular-art.v1", manifest)
        self.assertGreater(len(sheet), 1000000)

    def test_importer_is_debug_only_and_rejects_baked_shelves(self):
        importer=(PIPE/"import_ws16as_modular_library.py").read_text()
        self.assertIn('app/src/debug/assets/ws16as/shell',importer)
        self.assertIn("shelves_baked_into_shell",importer)
        self.assertIn("plaques_baked_into_shell",importer)
        self.assertIn("composite.tobytes()",importer)
        self.assertIn("release_approved",importer)
        self.assertNotIn("app/src/main/assets/ws16as",importer)

if __name__=="__main__":
    unittest.main()
