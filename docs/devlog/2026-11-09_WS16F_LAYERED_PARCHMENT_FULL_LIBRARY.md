# WS-16F — Source implementation and QA handoff

**Game:** GridMates: Word Search. **Owner-approved direction:** November 9, 2026 reference screenshot, warm understated paper, rich illustrated walnut shelf, books resting *on* it, Full Library instead of Full Shelf, reusable assets across GridMates.  
**Branch:** `feature/ws14b-style-anchor-and-art-direction-2026-11-08`. **Draft PR:** #131, deliberately unmerged.  
**Exact gameplay source to verify:** `83a06cbf22adcfef5aa20bde87b01033ec04ba0f`.

## Owner requirements resolved

Owner explicitly asked to **plan and deploy** the next asset pass, not just talk about the screenshot. Assets should be layered: quiet shared parchment background; independently drawn book cover, wood, decorations and live Compose labels; no massive single full-screen click-map. Fit very tall phones, tablets, landscape without ugly gaps. Shelf and book should visually contact, and the full library should resemble **multiple real bookshelves/bookcase**. Keep existing swipe/focus/one-tap routes and progress safe.

## Actual code/art produced

- **Shared background:** new `GridMatesParchmentBackground.kt`, installed behind the Android `GridMatesShell` Scaffold. Responsive ivory base, subtle radial warm vignette and deterministic ultra-low-opacity flecks; fills available viewport dynamically with no repeating bitmap seams. Editable counterpart masters `gm-parchment-tile-v1.svg` and `gm-parchment-edge-wash-v1.svg`. No literal room wallpaper, big PNG, baked text or interactions; conceptually GM_SHARED, but not yet copied to Sudoku/Crossword.
- **Layered Home bookshelf:** `Ws16BookCoverPager.kt` now composes actual covers above the shared native carved shelf *inside one overlapping Box*. Previously cover pager 234dp + shelf 34dp = 268dp tall. Now pager 226dp + carved shelf 42dp share a **252dp total region**, saving about **16dp** while keeping the actual illustrated cover **218dp** tall, its neighbor peeks, and its book selection/play actions. The shelf is drawn first and non-interactive; clickable covers are on top. Book IDs, pages and saves unchanged.
- **More sculpted woodwork:** `Ws16EStorybookShelf.kt` adds a scalloped walnut carved apron and warm highlights, still responsive to width and preserving gold medallion. Independent new source master `ws16f-carved-shelf-apron-v2.svg`; existing original independent top/supports/leaf/shadow retained as source references.
- **Actual multi-shelf Library:** `BookcaseBookshelfScreen.kt` already used a lazy real multi-row catalog. It now layers a soft walnut backboard + side uprights + the **same carved shelf art as Home** behind each actual row's clickable covers/spines. Independent new editable reference `ws16f-bookcase-backboard-v1.svg`. No invented future books, no store changes, and 1/2/3-column layout based on width/text scaling still works. Keep QA-only test books isolated from production.
- **Clear names:** visible Home and Menu `Full Library`; destination `Your Library`, subtitle `Browse your puzzle books`, details/back/fallback updated accordingly. Stable test tag `ws16-full-shelf` deliberately retained to avoid test harness breakages. Collection remains a separate, equal Home shortcut.
- **Regression test sources updated:** `Ws16CarouselHomeAndroidTest.kt`, `QaActualBookcaseAndroidTest.kt`, `QaBookshelfLauncherAndroidTest.kt`, `PuzzleBookshelfScreenAndroidTest.kt`, `QaBookArtworkIntegrationAndroidTest.kt`, preserving route and navigation checks while updating visible names. Test source compile is **not execution on an Android device**.

## Source/rights/QA details

The four **independent** SVG source masters have true SHA256 hashes and source-only/pending-art-review status in `design/visual-system/modular/ws16f-source-asset-register.json`. These SVGs are not bundled as direct Android drawables; the Android UI uses analogous scalable **Compose Canvas** artwork. Do not imply the native render is pixel-identical to the reference screenshot or that painterly illustration art is final. The reference screenshot is a *direction*, not a fake fullscreen game UI texture.

**Exact-source CI on SHA `83a06cbf` (initially in progress at log creation):**
- [Android #42737788114](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42737788114) — core/app unit tests, lint, Android test source compilation and debug APK assembly.
- [Art / debug-release isolation #42737788236](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42737788236) — debug/release art gates, QA-only books and approved font/portrait hashes.
No new APK link should be called **verified** until both conclude SUCCESS and the exact ZIP/APK hashes are read.

**Last dual-green rollback:** WS-16E Pass 3 [QA ZIP #13726026545](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42733181348/artifacts/13726026545), APK SHA256 `5d8cb4c3cc0e1dcc0bfe9ab029ad851eb18720b562eba81f5a82c2ef31bde5c7`. Current Android QA package remains a debug suffix `com.playereden.gridmates.wordsearch.qa` versionCode 57 unless built with override; export backup before uninstall to resolve a signing conflict. PR #131 remains draft.

## Owner phone acceptance tasks

Compare the approved reference and real rendered app:
- Full Home initial and scrolled: paper warmth restrained, top-right Profile/Menu, no blank edges, covers seem planted on shelf; no clipping of ornament or book title plaques; still can reach Full Library, Collection, Continue, Daily, Together, Custom/My Puzzles.
- Full Library: real first/second wooden shelf rows, book covers and spines clickable, name `Your Library`, open book/detail/play/back. Test portrait/landscape and large fonts where possible.
- Confirm dynamic counts/click semantics, Back to Home, no unintended scroll jumps or huge gaps. Note if the shelf still looks too flat or if paper grain is perceptible/noisy; those are next limited polish tasks.

## Next after owner handset review

Adjust book/shelf overlap carefully without shrinking covers. Consider truly painterly modular shelf PNG/endcap exports and light removable foliage/lantern edge art **only after** measuring the real handset result, keeping shared GM_BACKGROUND distinct from title-specific WS decorations. Avoid adding a background scene to Sudoku; only reusable shared UI materials go there later. WS-17 generator and per-book collection unlock milestones are separate roadmaps.

## Final VERIFIED source-matched QA receipts

**Both workflows COMPLETED SUCCESS on code and art SHA `83a06cbf22adcfef5aa20bde87b01033ec04ba0f`**. Subsequent [skip ci] documentation updates do not change the APK.

- Standard Android [#42737788114](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42737788114): **SUCCESS** — unit tests, lint, Android instrumentation test SOURCE compilation and `assembleDebug`; no claim of emulator/handset execution.
- QA artwork and release-isolation [#42737788236](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42737788236): **SUCCESS** — six QA-only book cover/spine sprites absent from release; 18 original + 18 alternative portrait SVG sources/36 SHA-matched native QA PNGs; 3 exact original motif hashes; all release isolated.
- Downloadable owner [QA ZIP artifact #13727073915](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42737788114/artifacts/13727073915) contains `app-debug.apk`. Artifact title mentions Woodland historically but build is complete WS-16F.
- **APK SHA-256:** `88075143e0a82a1fb59e96c3519de96043bbf734fa4f8b75b1b213b31f76dec8`.
- **Artifact ZIP SHA-256:** `55c52865b3192b7159e2303fd3622a35b0fc12c1200b8ad3061ea2530b4504d5`; GitHub reported 13,976,910 bytes.
- **QA package:** `com.playereden.gridmates.wordsearch.qa`, versionCode `57`, versionName `0.9.13-visual-kit-foundation-qa`. Same versionCode as previous QA APK; back up saves before uninstall when troubleshooting signing conflicts.
- **Owner real Android visual / tap acceptance PENDING.** The exact source compiles and release-art isolation passes; book/shelf contact, background warmth and Library bookcase appearance still need screenshot review. PR #131 remains DRAFT, unmerged; rollback is previous verified WS-16E Pass 3 APK.
