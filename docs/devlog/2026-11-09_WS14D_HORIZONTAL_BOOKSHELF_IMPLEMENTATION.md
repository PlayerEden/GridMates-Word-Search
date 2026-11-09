# 2026-11-09 — WS-14D Implementation: swipeable bookshelf QA vertical slice

**Status:** **IMPLEMENTED IN SOURCE; exact-head Android checks pending.** No owner installation or screenshot review of the new horizontal design has occurred. This is an implementation log, not a release approval.

## Who, what, where, why

- **Owner direction:** "Yeah we don't need anything crazy. Let's log, plan, start getting things together, and begin." This followed screenshots showing that Woodland/Space/Garden covers looked good but detached spines and two vertically stacked shelves felt awkward. The owner wants grounded, unified GridMates visuals and subtle, not excessive animation.
- **Assistant work:** Implemented the first **native horizontal book browser** as a reversible QA choice in Word Search; preserved existing classic shelf and content; adapted Android tests and the standard QA image import.
- **Where:** `PlayerEden/GridMates-Word-Search`, draft [PR #131](https://github.com/PlayerEden/GridMates-Word-Search/pull/131), branch `feature/ws14b-style-anchor-and-art-direction-2026-11-08`, code under `app/src/main/java/com/playereden/gridmates/wordsearch/`.
- **Why:** Compare **real interactive swipe navigation** against the old vertical shelves before generating new art or changing the broader Play/Collection screens.

## Where we came from

- Previous pass: [WS-14E brand/series art and restrained motion design](2026-11-09_WS14E_GRIDMATES_IDENTITY_AND_MOTION_DESIGN.md), itself following [WS-14D owner screenshot review](2026-11-09_WS14D_FIRST_PHONE_VISUAL_REVIEW_AND_LIBRARY_DIRECTION.md).
- Starting branch SHA: `e5e4cd8f4b66ddda3858bd05c346f1165e2f86df`, same stacked draft PR #131 (base #130). Earlier WS-14 source `00d2a36` passed [Android QA run #42576785096](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42576785096) and [Art/Release isolation #42576785170](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42576785170); the owner **did** install and screenshot that earlier artwork build and bookmark behavior.
- Last known broader owner-verified rollback before this visual arc was **v56**. The earlier illustrated candidate has *partial on-phone visual evidence* but not complete saved-game/landscape/two-phone regression approval.

## Implementation decisions and changed files

1. **`HorizontalBookBrowserScreen.kt` (new):** Compose `HorizontalPager` with real catalog-defined books, peeking adjacent covers, native `n of N books` indicator, visible previous/next controls, and a single selected-book title/progress/Open or Continue action. Uses real BookArt cover PNG from the existing WS-14 QA importer, with WS-12 fallback if absent. The cover already contains a left binding so the new layout does **not** render a detached free-floating spine. A very restrained `animateFloatAsState` ~220ms scale change highlights the selected cover. UI motion remains localized to library browsing, not puzzle word-tracing.
2. **`BookLibraryFlow.kt`:** QA-only in-memory choice `horizontalLibrary` defaults to the swipe experiment; stores the selected book ID in memory while opening and returning from detail. **No on-disk save schema, book IDs, page-progress or multiplayer protocol changes.**
3. **`PuzzleBookshelfScreen.kt`:** routes QA books to the new swipe UI or unmodified classic bookshelf via `showHorizontalBrowser`; old text-first production/empty/unreadable state still follows existing logic. In the **book detail hero only**, dropped the awkward independently drawn right-hand spine, using a single cover silhouette.
4. **`BookcaseBookshelfScreen.kt`:** native **Try swipe library** control while preserving existing A/B artwork toggle and stable old shelf behavior.
5. **`Ws14DHorizontalBrowserAndroidTest.kt` (new) and adjusted `QaBookshelfLauncherAndroidTest.kt`, `QaBookArtworkIntegrationAndroidTest.kt`:** source tests for stable book IDs after arrow selection, Classic-vs-Swipe rollback, one Open action, 320dp/200% font and original A/B art regression. CI **compiles** Android instrumentation code; these tests require an emulator/device run to claim actual execution.
6. **`.github/workflows/ws01-android-qa.yml`:** standard Android QA now conditionally imports the hash-pinned **six actual WS-14 covers/spines** into the debug app when the source sheet exists, runs the existing asset validator, and inspects all six exact export hashes inside its built APK. This makes the standard QA APK useful for art evaluation even if the separate artwork workflow's Gradle download fails; it does **not** replace the art workflow's debug/release-isolation check.
7. **Accessibility pass:** at widths under 300dp after gutters or font scale >=1.4x, library comparison controls stack instead of colliding; previous/next buttons retain minimum 48dp touch heights. Motion respects Compose/device animation-scale behavior.

**Source implementation commits:** `2646f0c` (new UI), `f04a80f` (flow/detail integration), `9849552` (tests), `10b565c` (test fixture corrections), **`8bbb911948dd7561d9b4e2add658ec7ae39302d3`** (large-font controls and standard image QA). No new art, profiles, achievements, released APK or cross-app Sudoku changes.

## Actual build evidence and blockers at documentation time

- On `10b565c`, standard Android QA [run #42582505330](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42582505330) reached **core/app compilation**. Its final conclusion must be checked live and does not verify newer commit `8bbb911948dd7561d9b4e2add658ec7ae39302d3`.
- On `10b565c`, separate art QA [run #42582505308](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42582505308) **failed at Gradle setup**, caused by **external HTTP 504 downloading Gradle 8.11.1**, before Android compilation. This is **not a proven source failure**; don't repeatedly rerun and waste Actions minutes.
- **No exact-head `8bbb911948dd7561d9b4e2add658ec7ae39302d3` APK has been verified as part of this journal entry yet.** Confirm the newest PR checks and artifact before giving the owner a replacement download link.
- For any build, prefer the APK from the workflow that explicitly verifies `assets/ws14/books/{woodland,space,garden}.png` and `assets/ws14/spines/{woodland,space,garden}.png` inside the actual debug APK. Download GitHub Actions ZIP and extract `app-debug.apk` when a chat sandbox link returns "Library file not found."
- No physical Android swipe/browser screenshots or test observations have been reported; the owner should compare the new swipe layout with **Classic shelves** after a source-matched build passes.

## Next exact action

1. Inspect the **latest** PR #131 SHA and GitHub Android/art workflow run conclusions. If the build fails **source compilation**, fix the exact error; if art workflow alone is blocked by Gradle 504, avoid blind retries and use the standard image-verified QA artifact if green.
2. Obtain/offer one **actual APK** built from `8bbb911948dd7561d9b4e2add658ec7ae39302d3` or a newer source-matched commit; verify app identity and six assets, and record its artifact/checksum.
3. Owner tests **Play → Test Puzzle Books**: swipe Woodland/Space/Garden; tap arrows; open the selected book and return; switch **Classic shelves** / **Try swipe library**, and separately **Original art (A)** / illustrated art (B). Inspect native progress, page/bookmarks, 320dp/large text and preservation of installed app data.
4. Save screenshots/feedback into a new development-journal entry, then refine one thing at a time: library proportions/spacing first, book detail compactness second, restrained cross-screen artwork third. **Don't generate new sprite families or alter ®-bearing master V1 until owner reviews the test.**

**New-chat instruction:** Read `AGENTS.md`, `docs/DEVELOPMENT_JOURNAL.md`, this log, and `docs/WS14_ACTIVE_HANDOFF_2026-11-09.md`; inspect current code and exact-head workflow. Continue with build verification + the real interactive swipe-library APK test, not more concept mockups.
