# WS-09F-D — Hidden v4 placement engine in the real Kotlin core

**November 8, 2026 · internal preview stage; giant boards are NOT playable in the APK.**

This stage is stacked on [WS-09G-A UI navigation draft PR #82](https://github.com/PlayerEden/GridMates-Word-Search/pull/82), whose dedicated emulator QA [#42506712015](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42506712015) passed **117/117** tests and a source-matched same-signer schema5 upgrade. It draws algorithmic evidence from separate [V4 Kotlin C4 research #81](https://github.com/PlayerEden/GridMates-Word-Search/pull/81), with its [1,800-seed generator stress #42472796378](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42472796378). **Neither research branch is merged into this UI branch.**

## Implemented in this milestone

- `core/src/main/kotlin/com/playereden/gridmates/wordsearch/core/V4PreviewPlacement.kt` is an **internal, preview-only** placement engine for 40×40/50×50 grids and 70/105 targets. It reuses the previously exercised bounded Kotlin lab algorithm, with **24 attempts and 160 candidate placements per word**, eight-direction duplicate prevention, and Relaxed/Classic/Expert placements.
- It accepts **explicitly injected candidate words** for previews and wraps results in the **actual core `WordSearchPuzzle`, `WordTarget`, `GridPoint` model**. No V4 word pack is hardcoded or bundled as a shipping dictionary. The unapproved candidate vocabulary exists **only** in `core/src/test/resources/ws09fc_v4_test_only_candidates.txt`.
- Every result uses an unmistakable `wordsearch:v4:PREVIEW:...` ID with a **SHA-256-derived fingerprint of the exact candidate pool and its order**. This prevents a modified test dictionary from reusing the same preview ID for a seed. **These preview IDs must never be persisted or used as future shipping V4 puzzle IDs.** A final approved, immutable V4 corpus/version must have its own permanent identity and replay requirements.
- Neither `PuzzleBoardSize`, `SoloRun`, `SoloProgressRules`, `DailyChallenge`, `WordSearchScreen`, the app/launcher, LAN protocol, backup schema, versionCode nor signing is changed. Users cannot start V4 from New Solo, and no stored v1/v2/v3 seed is routed through this engine.
- `V4PreviewPlacementCoreTest.kt` covers **180 research previews** across all 3 themes × 2 sizes × 3 direction modes × 10 seeds, including **15,750 real forward/reverse claim checks**, exact deterministic replay and all **18 literal pre-integration giant-grid fingerprints** (independently frozen in C2). Negative tests reject invalid size/ambiguous word pools; a reordered corpus cannot alias the same preview ID; legacy enum positions, V1/V2/V3 version identities and V3 Solo reconstruction remain unchanged.
- The custom `ws09f-d-v4-preview-core.yml` CI also checks that the preview engine stays out of Solo, that no unapproved V4 library is present under core main, and that the test fixture matches the frozen lab bytes `sha256:16969acf1ff1ab700f23201cb1fe6273d99c6e7cd29c7909e8b891440b3a46fe`. It then runs the **actual Gradle `:core:test`**; no unnecessary Android emulator/QA APK is generated in this research gate.

## Verification

- **Exact-source V4 preview core CI SUCCESS:** [dedicated workflow #42510018590](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42510018590) at source SHA `5a9efbf93e29cd3cc9f514bba6f0bc1f7547ca82`. Python safety/content-hash gate PASSED; Gradle `:core:test` **BUILD SUCCESSFUL** (1m 35s). This full core suite includes the new 180-puzzle/18-literal-grid-golden/15,750-real-claim regression and all historical v1/v2/v3 core tests. The JUnit assertions are not summarized as individual counts by Gradle's console output; those counts are the exact expectations in checked-in tests, and the overall task passed. [Immutable tested-code checkpoint](https://github.com/PlayerEden/GridMates-Word-Search/tree/checkpoint/ws09f-d-v4-core-preview-180-grids-historic-tests-green-2026-11-08). Documentation-only commits later than this SHA do not change the tested engine.
- [Previous source-verified 117-case Android test of the WS09G-A base](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42506712015) remains unaffected. Do not attribute that run to this new source.
- No new APK is published; owner/family devices and saves are untouched.

## Next release gates

1. **Editorial:** genuinely review theme-appropriate vocabulary and human-readable labels. Reserve specialized words for Expert; keep Relaxed approachable. The current provisional Space Relaxed pool only has 121 possible entries against a tentative 125-word planning buffer after holds, and the extra ten Space suggestions remain unapproved.
2. **Immutable V4:** freeze a reviewed source-controlled corpus, strict challenge tier selections, stable seed/fingerprint scheme and exact grid/path golden manifest. Production puzzle IDs must not contain `PREVIEW` or `LAB`.
3. **Version routing and ordinals:** a production `PuzzleBoardSize` append requires care because current `v2Presets = entries.filterNot { it in largePresets }` would inadvertently admit new V4 enum cases as V2 unless changed to an explicit frozen V2 list. Preserve ordinals and archived V1/V2/V3 grids, then add V4-specific reconstruction to Solo plus schema5-safe backup/paused-game tests.
4. **Actual phone QA:** 40/50 live board rendering, 105-word index accessibility at 320dp/200%-font/landscape, map/pinch selections, save/bookmark long sessions, memory/frame profiling and historical same-signer install-over.
5. **Real family installation:** installed version/signer and independent backup unknown; no uninstall/clear-data shortcuts. Two-phone Co-op P0 #53 still requires physical testing, and stacked main merges/release signing remain WS-09H tasks.

**Do not distribute or merge a version-4 giant-board APK from this milestone.**
