# WS-14I — Progressive Book Hints and 20×20 Word-Trace Test Receipt

**Date:** November 9, 2026. **Result:** Android and art/release-isolation CI **SUCCESS** on identical source `3a3d0bcdde21de12a5d132c8ad85cc6b00997270`; new native phone/emulator gesture regression **COMPILED, NOT EXECUTED**. Owner has not yet tested this code. No art master/generated asset changes, no production merge.

## Who, what, where, why

- **Who / evidence:** Owner photographed WS-14H compact bulb screen on Woodland 12×12, 15×15 and 20×20 and liked the uncluttered layout. The 20×20 screenshot showed **"No hints available. Keep looking!" at 0/24 found**. Owner confirmed illustrated Puzzle Books should cap at **20×20**, leaving 25×25 and 30×30 as separate opt-in Free Play sizes. Assistant inspected source and implemented a small gameplay-quality patch, with GitHub Actions verifying its build.
- **Why:** The prior book-page hint rule allowed only **four total hint stages on 20×20**, regardless of 24 remaining words; a partially played page could exhaust the budget despite many unsolved words. In addition, `BookPageSessionStore.hint` may return null when a write fails. Both cases were shown as the same generic message.
- **Where:** [Draft PR #131](https://github.com/PlayerEden/GridMates-Word-Search/pull/131), core `BookPageSession.kt`, `BookPageGameplayScreen.kt`, core unit tests, and Android `Ws14IRealTwentyCellWordTraceAndroidTest.kt`.

## Previous state and source guardrails

- [WS-14H2 on-phone size/layout review](2026-11-09_WS14H2_PHONE_SPACING_AND_BOOK_SIZE_CEILING.md). Last owner visually reviewed QA APK: [WS-14H verified artifact #13693073000](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42658174525/artifacts/13693073000), code `7856282`, both Android/art green. User did not demonstrate successful actual word claims or reopen behavior on 20×20.
- Starting draft branch head `223b2d91d168d513c0e6dc83f8990325d97e1f8c`. The commit was documentation-only on top of the visually tested version. Previous code and eight profile/board save families remain separate; no book manifest, generator, format, page IDs, signing or installed saves were changed.
- Preserve old Classic shelves toggle and illustrated art source checks, 12 approved Woodland portraits, offline behavior, LAN state and the historical v56 broader fallback. **Do not uninstall or erase app data just to change a QA APK.**

## What changed — measured scope

1. Removed the **arbitrary per-page total-hint quota** (previous 2/3/4/6/8 depending on grid size) from `BookPageSessionRules.hint`. There are still precisely **three progressive persisted steps per unfound word**: word length, identified word/row, and visible highlighted path. The map is intrinsically bounded by the number of original published word targets × 3; no generator/solution state is mutated. A request after all remaining words have their three clues returns no new state, not a phantom word award.
2. User-facing light-bulb feedback now distinguishes **all remaining words fully hinted**, **save/blocked failure** and **temporary unavailability**, instead of showing the same "No hints available" sentence regardless of why. The existing `sessionStore.hint` AtomicFile save path persists the actual hint level before success is shown.
3. Updated older core expectation that a 12×12 page stops after two hint taps. Added `BookPageLargeHintTest.kt`: 20×20 24-target fixture; verifies seventh hint still available, 3-stage target progression, canonical GMWS encode/decode, genuine FOUND claim, max 3 stored levels per target and no automatic finds.
4. New `Ws14IRealTwentyCellWordTraceAndroidTest.kt`: programmatic **real Compose gesture injection** at grid cell centers, five bulb clicks, 20×20 found-word UI check and reopening a second `BookPageSessionStore` to validate saved find and hints. **Its Kotlin test code compiled, but the CI job does not execute tests on an emulator/device.** The owner's real-finger test remains a separate acceptance gate.
5. **No** new sprite images, extra UI rows, word-grid art, board style, book size changes, score changes, save schema migration, Solo/Daily hint rule changes or release approval.

## Exact CI and APK evidence

- **Source SHA:** `3a3d0bcdde21de12a5d132c8ad85cc6b00997270` (code + tests). Source commits in sequence:
  - `71c478402d7de2eec9560a08c20f1adf052b55ae`: core hint policy, UI copy, JVM tests;
  - `d6b15c959c2b95ceeac4ced55904f144a735413d`: 20×20 touch-injection instrumentation test;
  - `93eacd63551c4c87f3ffe89ede14eb6f5936d0d2`: import correction;
  - `3a3d0bcdde21de12a5d132c8ad85cc6b00997270`: correct touch-injection helper: `width`, `height`, `swipe` extension.
- [Standard Android QA #42664221672](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42664221672) **SUCCESS**: core/app unit tests, lint, Android test **compilation**, Android debug APK assembly, package identity + asset-hash checks, 6 WS-14 illustrated cover/spine sprites in APK.
- [Art/debug-release isolation #42664221664](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42664221664) **SUCCESS**: Android debug/release compilation, no QA-only unapproved artwork leaked to release.
- [GitHub APK QA ZIP artifact #13696490440](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42664221672/artifacts/13696490440) name `GridMates-WordSearch-Woodland-Verified-QA`, **13,750,251 ZIP bytes**, SHA-256 **`a12830ed1eeaca927583b4585f7615d123f2fd848498ffdc7c29ef1eedbd4c7e`**, as reported by GitHub upload. Extract `app-debug.apk`; **APK SHA-256 `74598f4824fd1a3e74e98f96a566d0806d0375aa40b1ecaa8ac91ac83219f17a`**, reported by Android QA run after `aapt` identity check. Package `com.playereden.gridmates.wordsearch.qa`, versionCode **57**, versionName `0.9.13-visual-kit-foundation-qa`. This is a QA test APK, not a public signed Play release.
- **Earlier source failure:** [Android #42663347503](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42663347503) failed during `:app:compileDebugAndroidTestKotlin`, because the newly authored Compose test used unsupported `size` and didn't import `swipe`. The core test and production Android code were not the reported source error. Fixed at `3a3d0bcdde21de12a5d132c8ad85cc6b00997270`. We distinguished this actual test API failure from an external Gradle 504.
- **Not proven:** actual device/emulator instrumentation execution, owner physically claiming a word at 20×20, multi-device co-op, full saved-install migration, or rights approval of new art. Do not label those as tested or green.

## Next single actionable handoff

Owner downloads [QA ZIP #13696490440](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42664221672/artifacts/13696490440), extracts `app-debug.apk`, backs up existing progress, and install-over only if signature and package are compatible (no mandatory uninstall). On Woodland Page 3 (20×20) with 0/24 found:
1. Tap the **light bulb five times** and observe real successive hints (it must **not** stop at four). Reopening the page should retain revealed/highlighted hints.
2. Find a visible word by actual finger drag; try precise **Select**, +/−, Fit and pinch/pan. Confirm the count increments and remains after returning/reopening.
3. Check page 1 (12×12) and at least one Space/Garden page for tint, icon and readable title; optionally test landscape and larger font.

Record owner outcomes in a **new dated journal**, and only after functional page approval proceed to the planned **three small, matching standalone Woodland/Space/Garden gameplay accents** via the hashed QA-only sprite pipeline. **20×20 regular book ceiling remains locked as a product decision.** Preserve broader optional 25×25/30×30 Free Play modes and the last owner-reviewed fallback.

**New-chat restart:** Read `AGENTS.md`, `docs/DEVELOPMENT_JOURNAL.md`, this exact receipt and `docs/WS14_ACTIVE_HANDOFF_2026-11-09.md`; inspect live draft PR and source before changing anything.
