# WS16AT — Modular Library native integration, reversible QA gate
**Date:** November 10, 2026 Kentucky / November 11 UTC · **PR:** #131 draft · **Status:** code integrated, first Android and art tests currently repeating after two narrowly scoped corrections; new artwork **not yet** in GitHub or APK.

## Owner direction
- The little permanent crown emblem may stay for now, since later independent placards could cover it.
- Cabinet uses ONLY five architectural sprites (recessed back, left/right pilasters, crown, base). **No permanent horizontal shelves, text, plaques, book spines, or buttons** in the cabinet bitmap.
- Reuse the **existing real Home shelf sprite** and real interactive book spines; the app chooses position, count and book capacity from the display. Future standalone placards and page-arrow/dot/button sprites are separate assets, reusable across GridMates menus.
- Immersive wood extends to available edges. Keep UI controls accessible with minimum 48dp tap targets.

## Source work actually committed
- `app/src/main/java/com/playereden/gridmates/wordsearch/WS16ATModularLibraryScene.kt`: independently composited five-part painted shell loader + a catalog-driven, responsive one/two/three-tier real-book scene. Native title, count, Back, page arrows and QA return control. Each tier calls existing `WS16AMSpineBay` and existing `Ws16EStorybookShelf`; no new fake book ID. A full-screen cabinet is available only when all five assets are packaged.
- `BookcaseBookshelfScreen.kt`: non-destructive modular art flag; existing selection dialog and `View pages` action remain source-of-truth; current WS16AM scene is still default. Asset availability is checked before any modular QA entry is shown.
- `WS16AMInteractiveBookcaseScene.kt`: exposes existing book-spine row compositor to be reused, and adds an optional "Try modular Library" entry in the existing QA visual options menu. It cannot appear when art is missing.
- `tools/asset_pipeline/import_ws16as_modular_library.py`: strict import command for the original conversation ZIP; verifies actual SHA256s, no baked books/shelves/plaques/text, exact five-crop reconstruction and writes ONLY to `app/src/debug/assets/ws16as/shell`. No automatically inferred approval or release asset copy.
- `tools/asset_pipeline/test_ws16at_modular_library.py`: unit checks layout capacity across widths/large fonts/real book counts, QA gate, real book routing and the strict source importer.
- Both major GitHub Actions workflows run the new tests, and asset-pipeline changes now trigger them.

## Exact first-build issues and repairs
1. Initial Android/art compilation failed solely on invalid Kotlin `2 * sideInset` (Int×Dp, unsupported). Replaced with `sideInset * 2` in commit `b6973bb`.
2. Second art test failed because the new test expected a **function call** in the declaration source; changed it to assert the actual function declaration in `test_ws16at_modular_library.py` (commit `5dd06f1`).
3. Both workflows must pass same-current SHA before declaring a build green. Latest workflow-source commit includes pipeline path filter update. Inspect live results rather than relying on past failure/cancellation logs.

## No false claims or destructive changes
- The 5 actual illustrated WS16AS source PNGs and separate plaque candidates still exist **only in the conversation artifact ZIP**; they are NOT yet committed or packaged in Android. The import script is ready for an authorized art-binary transfer and has not run on GitHub CI because the original ZIP is not yet in source control.
- The local source ZIP has been independently hash-checked; all five parts pixel-perfectly reassemble the source frame with no shelves or plaques. This does NOT establish Android visual approval.
- No new immersive artwork APK, no on-phone QA, no new published UI, no app release and no merge claim.
- Preserve existing standard WS16AM / WS16AO/WS16AK paths and all saves, IDs, puzzles, achievements, LAN code and rollback.

## Remaining milestones
1. Import high-resolution candidate art binaries to a durable GitHub master folder and run the exact importer in Actions.
2. Verify packaged manifest and debug-vs-release art isolation; source matched checks must pass.
3. Inspect real assembled screen at 320/360/412dp, tall and landscape, large text, with 0/3/7/13/24+ catalog fixtures and actual taps.
4. Then produce reusable independent plaque, icon, arrow and page-dot assets as a separate GridMates UI-kit milestone.
