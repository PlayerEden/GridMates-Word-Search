# WS-16O — Fully visible carousel neighbors, cleaner launcher and honest loading feedback

**Owner evidence (Nov 10):** Four real phone screenshots after WS-16N. The new launcher is in the phone's top row beside GridMates: Sudoku, Eden Comms and Sector Rebound. It has a promising grid/pencil illustration but appears visually crowded at launcher scale. On fullscreen Home the large illustrated book and artwork are working; neighboring covers are abruptly clipped by the phone's left/right edges, and no directional indicator explicitly tells new users to swipe. Owner also clarified that by "loading screen" they mean **a delayed indicator during actual slow operations**, not a splash held for a set time.

## Implemented in this pass

- [Code source `a16f0b70800d3d7b1e36e860e4d56e86cdca2e81`](https://github.com/PlayerEden/GridMates-Word-Search/commit/a16f0b70800d3d7b1e36e860e4d56e86cdca2e81), in open draft [PR #131](https://github.com/PlayerEden/GridMates-Word-Search/pull/131).
- `Ws16BookCoverPager.kt` uses the same **full-size centered book** (186dp max width; 218dp illustration) but restricts the gesture pager to the center column. Adjacent volumes, if present, are displayed as **small complete covers contained inside the screen**, not cropped slivers. Each has a small, native dark-green direction chevron below it, an accessible action label `Next book: Space` / `Previous book: Woodland`, and a genuine tap route to focus that book using the existing pager callback. The full-sized center book is still horizontally swipeable and tapping it still opens the real book. The wooden shelf, title/progress rules and save state are unchanged.
- `ic_wordsearch_foreground.xml` and `ic_wordsearch_monochrome.xml` were redrawn as simplified **native vector** adaptive-icon layers. A smaller cream tile reveals more dark forest-green launcher background, with clear grid and single readable gold pencil and fewer tiny glints, while the one-color themed version uses the matching silhouette. These are source-controlled vectors, not a raster rendering or a change to app's identity/label. The Android system may still truncate `Word Search` under a 4-icon row; that is launcher-controlled.
- `Ws16CarouselHomeAndroidTest.kt` now asserts an accessible Next-book control for Space on the default Woodland-focused Home. Actual on-phone gesture, thumb size and adaptive mask must still be owner-tested.

## Delayed loading feedback policy (future real operations, **not a fake spinner today**)

1. Preserve the existing **native splash**, which exits when the Activity draws; never add a second fixed-time loading screen or a fake percentage.
2. For actual asynchronous jobs such as opening/importing a large local puzzle, generating a custom board, loading a long book catalog, or starting Nearby pairing, expose a real `busy`/progress state from that operation. Only display a compact `CircularProgressIndicator` and a meaningful live label (e.g. `Preparing puzzle…`, `Connecting to nearby player…`) **if the same work remains incomplete for roughly 700 ms**; immediately dismiss it on success, cancellation or error. Avoid flashing an indicator for ordinary instant navigation. Never claim a known percent unless the operation exposes actual countable progress.
3. Preserve accessibility (content descriptions, reduced-motion accommodation), cancel/dispose on navigation away, and never block Android Back without an actual non-cancelable reason.
4. If cold-start specifically feels frozen, instrument/measure real Activity/first-frame and local data initialization rather than adding a time-based fake splash. If data loading truly blocks the main thread, offload it first so a spinner can animate and touches remain responsive.
5. The owner describes this as **future-proofing**, not a current demonstrated loading bug; don't add ongoing overhead or an inert spinner to the main menu prematurely.

## Acceptance criteria
- Woodland featured at rest, Space thumbnail fully visible inside right edge with a small right chevron. Swipe center book toward Space; after settling, Woodland and Garden are visible as complete small side previews, left/right chevrons included. Tap preview to move focus and tap current center book to open pages.
- First/last book displays only available directions; no fake locked books or blank dummy placeholders.
- Fullscreen screen height and book/shelf area remain unchanged, bottom links can still be reached, and the new arrows do not overlap native text or touch navigation.
- Icon looks clear as a squircle/circle and Android 13+ themed home icon, particularly next to Sudoku. No blurry bitmap, ugly double pencil, or overfull cream square.
- Standard and Fullscreen preferences retained. Native splash appears without forced waiting.
- Android source, CI and art-isolation tests must pass; don't call it on-device approved before owner screenshots.

## QA and rollback
- Latest source `a16f0b70800d3d7b1e36e860e4d56e86cdca2e81`. [Standard Android QA #42769471322](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42769471322) / [art/release #42769471595](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42769471595) **in progress at initial log**.
- Previous dual-green accepted candidate WS-16N: corrected source `e9460ea`, [Android artifact #13740072635](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42767125319/artifacts/13740072635). Source commits are durable even if 3-day QA artifacts expire.
- No new extra APKs needed until dual-green; one consolidated owner build, then evaluate actual phone images.

## Final exact-source build / artifact receipt (Nov 10 UTC)

- **Compiled application source:** [`a16f0b70800d3d7b1e36e860e4d56e86cdca2e81`](https://github.com/PlayerEden/GridMates-Word-Search/commit/a16f0b70800d3d7b1e36e860e4d56e86cdca2e81). Later documentation-only commits use `[skip ci]`.
- [Android QA #42769471322](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42769471322) **SUCCESS**. Source-level tests, lint, Android instrumentation compilation and QA APK. Real device/emulator instrumentation **not executed**.
- [QA art vs release isolation #42769471595](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42769471595) **SUCCESS**, preserving prior release-art gates.
- **Preferred standard artifact:** [#13741496198](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42769471322/artifacts/13741496198), zip contains `app-debug.apk`, exact 15,006,816-byte APK SHA-256 `1cc99fbd1f48f1093de0094c15d5992ab5a252f1f0c4b3975873ad1872070f27`. Also attached in conversation as verified standalone `WS16O_Contained_Books_Clean_Icon_QA.apk`.
- Alternate art artifact [#13741895319](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42769471595/artifacts/13741895319), APK external SHA-256 `b0a285d67224c03b0712b2cdc2b5049c505abcf55ab77e1f6381e7c7e0899c70`. **All 258 internal paths/decompressed contents match** the standard APK exactly despite different ZIP container metadata.
- Independently opened real APK and verified PNG SHA-256 from internal manifests: **8 shelf + 6 illustrated Home actions + 5 botanicals + 2 separate GridMates brand sprites = 21/21 correct**. Launcher vectors are compiled Android resources (not bitmap PNG assets). The same native splash and puzzle saves are preserved.
- No new content, networking, production merge, release approval, or Android signing changes. Three-day artifact retention remains; old QA builds may expire.
- **Owner handset still needed:** new complete side previews (not offscreen-clipped), tap arrows and swipe, full cover remains prominent, proper center book navigation, new smaller pencil/grid icon at real launcher size and in themed mode.

**Follow-up for graphics review:** If side mini books look too small, tune thumbnail width/vertical alignment without reverting to cropped offscreen full-size pages. If icon still appears cluttered, improve silhouette and legibility at 48dp rather than adding more fine illustration detail. Loading indicator is **intentionally not activated** without an actual slow async task.
