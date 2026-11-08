# WS-09F-C4 — Hidden V4 core-model compatibility gate

**November 8, 2026 · verified research-only staging · not a playable APK.**

This branch stacks on [draft C3 PR #80](https://github.com/PlayerEden/GridMates-Word-Search/pull/80), on [draft Kotlin lab PR #79](https://github.com/PlayerEden/GridMates-Word-Search/pull/79), and [draft V4 vocabulary PR #78](https://github.com/PlayerEden/GridMates-Word-Search/pull/78). The separately verified v0.9.4/code48 large-board bookmark [PR #74](https://github.com/PlayerEden/GridMates-Word-Search/pull/74) is **not merged into this research branch**.

## What was implemented and validated

1. **Independent real-core compatibility harness** `labs/ws09f_c_kotlin/CoreModelCompatibility.kt` compiles the unchanged frozen `WordSearch.kt`, `WordSearchV2.kt` and `WordSearchV3.kt` against the independently verified V4 experimental generator. It converts lab data into the **real** `WordSearchPuzzle` / `WordTarget` / `GridPoint` model. It calls the actual `WordSearchRules.line`, `claim`, `complete` with each word's forward and reverse paths.
2. **New test-only core fixture**: byte-exact copies of the frozen lab engine and review-pending candidate words are stored at `core/src/test/kotlin/ws09fc/lab/V4EngineLab.kt` and `core/src/test/resources/ws09fc_v4_test_only_candidates.txt`. There is **no V4 engine or unapproved vocabulary in core/src/main or app/src/main**. Existing user-facing `PuzzleBoardSize` entries and ordinals are unchanged (8,10,12,12,15,20,25,30).
3. `core/src/test/kotlin/com/playereden/gridmates/wordsearch/core/V4ResearchCoreCompatibilityTest.kt` adds a regular **JUnit test in the actual Gradle `:core:test` suite**, validating 54 giant-board research combinations (3 themes × 2 sizes × 3 challenge sets × 3 seeds): 4,725 real WordSearchRules find/reverse path checks, completion, out-of-bounds rejection and public-size isolation. A standalone scenario checks 180 boards and 15,750 path checks, plus **6 independent historical v1/v2/v3 literal SHA-256 golden fingerprints**.
4. `tools/ws09f_c4_core_test_mirror_audit.py` verifies both lab/core-test copies are byte-identical and blocks V4-named engine files being introduced into production source. The QA workflow uses exact source files and **does not** modify `SoloRun`, save schema5, Daily, Nearby, signing, UI or Android package.
5. The earlier [C3 full 1,800-seed generator stress](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42472796378) remains separately green. It validates experimental generator paths for 1,800 seeds but does not establish phone usability, all possible seeds, or memory/frame-rate on Android.

## Exact QA receipt

- **Verified source SHA:** `d8194c7f2fc0d5a5d52b964b9bc4df46200f51ac`.
- [GitHub Actions core compatibility workflow #42504573655](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42504573655) **SUCCESS**:
  - Python source/fixture parity audit passed: test-only engine bytes SHA-256 `63d078a389723d3d5285d76dd5b99e874e18405a7534d6fd0a7672d3e970b0e2`; content bytes SHA-256 `16969acf1ff1ab700f23201cb1fe6273d99c6e7cd29c7909e8b891440b3a46fe`.
  - Actual Kotlin compiler 2.4.21/JRE17 compiled frozen real core v1/v2/v3 source plus V4 experimental adapter; **6 independent historical goldens passed, 180/180 conversions, 15,750 forward/reverse claim assertions passed**.
  - Actual Gradle 8.11.1 `gradle --no-daemon :core:test --console=plain` **BUILD SUCCESSFUL**. This includes the newly added JUnit regression tests in the real core test source set and the original core tests.
- [Immutable exact tested research checkpoint](https://github.com/PlayerEden/GridMates-Word-Search/tree/checkpoint/ws09f-c4-v4-core-test-only-gradle-junit-green-2026-11-08). Later docs-only commits do not change which executable source was tested.

## Discovered giant-board UI blocker

The existing `WordSearchScreen.kt` `WordList` component (v0.9.4 source) pages `1–5` rows per page and `1–2` columns based on screen width/font scale, yielding **1–10 words/page**. At 105 targets that can mean **11–105 pages**; at enlarged font (≥1.3) the UI forces one column. This is unacceptable without dedicated older-user-friendly navigation: alphabetical sections, visible remaining words, efficient jumping without many taps, TalkBack, 200%-font and narrow-phone acceptance. Recorded as a release blocker in [WS-09G #76](https://github.com/PlayerEden/GridMates-Word-Search/issues/76).

## Next acceptance gates (uncompleted)

- [ ] Human editorial review: approve normalized answer vs displayed label, sort familiar/rare vocabulary. Provisional Space Relaxed candidates have **121** after proposed holds, four short of an initial 125-word buffer (105+20); the ten proposed extra Space terms are not included in approved/verified V4 content. [C3 research #80](https://github.com/PlayerEden/GridMates-Word-Search/pull/80).
- [ ] Only after editorial signoff, create a new versioned production-content/engine branch in `core/src/main` with **distinct immutable production V4 IDs** (do not ship `wordsearch:v4:LAB`). Preserve frozen legacy ordinals and v1/v2/v3 golden SHA-256 values and provide stable version-specific routing through saved `SoloRun`. No retrofit of old seeds.
- [ ] Explicit versioned save reconstructability, schema5 backup/restore, resume/paused library, hint and score policy for 70/105 words. Verify actual source-matched old→new same-signer APK install while preserving all existing saves. Do not uninstall any tester's older app to circumvent a signing mismatch.
- [ ] On-device performance: benchmark 40/50 viewport windowing, hit accuracy, multiple region boundaries, real frame-time p50/p95/jank, battery/memory, 320dp and enlarged text, accessibility, readable word list, long-session restore and real two-phone Nearby Co-op #53.
- [ ] Full stacked merge/integration audit across B2 [#74](https://github.com/PlayerEden/GridMates-Word-Search/pull/74) and v4 research [#78–#80](https://github.com/PlayerEden/GridMates-Word-Search/pull/80). Maintain rollback checkpoints and do not claim Play readiness or family APK compatibility from lab tests.

**Current actual APK checkpoint:** separately verified [v0.9.4/code48, 114/114 Android instrumentation tests, same-signer v0.9.3→v0.9.4 upgrade](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42501935166). It has **no 40/50 gameplay**, no changes from this C4 branch, and the existing installed family app signing/version remains unverified.
