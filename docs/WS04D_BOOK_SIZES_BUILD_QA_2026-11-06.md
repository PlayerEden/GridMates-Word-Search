# WS-04D — Paperback Board Sizes, Zoom/Move: Final Android QA Receipt

**Recorded:** November 6, 2026  
**Status:** **CI GREEN** for new 12×12 / 15×15 / 20×20 book sizes and Android QA APK. Actual Android device interaction, large-board comfort and installation/update acceptance **NOT YET VERIFIED**.

## Exact passing source & artifact

- **Draft stacked PR #22**: [Paperback board sizes](https://github.com/PlayerEden/GridMates-Word-Search/pull/22), based on WS-04D-A draft PR #20. The original Sudoku app and its data are unchanged. Keep stacked Word Search PRs draft/unmerged pending physical acceptance.
- **Built source SHA**: `f74b91e27fe1abd029d89072a4eae0ac8152f7cf` (following changes to documentation are NOT a new tested code version).
- **[Passing GitHub Actions #42174823751](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42174823751)**: `BUILD SUCCESSFUL in 2m 28s`; 59 Gradle tasks, 57 executed and 2 cached.
- CI checks `:core:test`, `:app:testDebugUnitTest`, `:app:lintDebug`, `:app:compileDebugAndroidTestKotlin`, `:app:assembleDebug`, the QA package/version, original checksum-pinned Woodland art import, and all 12 portrait resources in APK.
- **Package** `com.playereden.gridmates.wordsearch.qa`; **versionCode 7**, **versionName `0.4.4-book-sizes-alpha-qa`**.
- **APK file** `app-debug.apk`, **11,448,672 bytes**, **SHA-256 `6c6450d79fc63fb88714616aa6785fbb3e174c56d68399a257250160045eac2b`**. Independently downloaded ZIP, extracted APK, recomputed SHA-256 matching CI and found all 12 `woodland_*.png` resources.
- [GitHub artifact #13482927260](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42174823751/artifacts/13482927260), ZIP digest `079a1b285274f0bb5611b4530b09581e0e2a9b4273284483c7d357dccb220f9a`; artifact expires approximately November 13, 2026 UTC.
- [Exact tested source checkpoint](https://github.com/PlayerEden/GridMates-Word-Search/tree/checkpoint/ws04d-book-board-presets-v044-ci-green-2026-11-06) points to the built code SHA above.

## Verified source-level changes

- Main **new Solo** board choices: Short **12×12 / 12 words**, Standard **15×15 / 16 words** (default), Extended **20×20 / 24 words**. Board size remains independent from Relaxed/Classic/Expert challenge.
- Native Android Zoom and Move/Find: 100%–250% zoom, enlarged grid/glyphs, draggable pan in Move mode, standard word drag and endpoint taps in Find mode; compact controls for smaller landscape viewports. The visual behavior itself needs phone QA.
- Existing v2 COMPACT (8×8), STANDARD (10×10), LARGE (12×12) enum names and ordinals remain; new `BOOK_SHORT`, `BOOK_CLASSIC`, `BOOK_BIG` appended. Existing IDs, seeds and unfinished v2 puzzles are not deliberately rewritten.
- English v1 Solo and offline Daily generator not changed; current schema4 format handles old and new board enum names, no destructive migration.
- Kotlin unit tests exercise preset sizes, all theme/challenge combinations, determinism, target path acceptance and old v1/v2 identity.
- Android instrumentation **test sources** for 20×20 saved claims/backup/next puzzle and older schema4 v2 8×8 loading compile successfully. **They have not been executed on an emulator/device by CI.**

## Real-device QA / deferred interactions

- [ ] Test 12×12 Relaxed, 15×15 Classic, 20×20 Expert, including all directions and real hand feel.
- [ ] On 15/20 zoomed boards, confirm Move pans accurately and Find does not pan; verify tap-first/tap-last with long target spans and word highlights.
- [ ] 20×20 long words can extend outside the visible zoom viewport. Zoom-out/endpoint taps are current workarounds; evaluate native pinch-to-zoom, edge-autopan and/or mini-map before declaring polished.
- [ ] Test 100/145/185/250% zoom, narrow portrait, short landscape, accessibility font 200%, screen reader/large touch targets.
- [ ] Export current v0.4.3 QA JSON before uninstalling if debug signing differs; confirm real old 8×8/10×10/12×12 v2 save import resumes exact board.
- [ ] Verify new 15×15/20×20 saves, clue paths, stats and records remain after process kill, Android device restart and JSON import.
- [ ] Stable QA signing issue #11 still open: higher versionCode does **not** prove Android in-place upgrade compatibility.
- [ ] No approved new Ocean/Bugs/Spanish art or separate LAN networking has been shipped here.

## Product direction

A 12×12 board is now our **shortest conventional book-size** choice. Difficulty is directions and word-search policy, not simply making letters microscopic; longer boards are expected to be *longer experiences*, not automatically Expert.

[Book-size design and acceptance plan](WS04D_BOOK_SIZES_QA_PLAN.md) · [Physical QA issue #23](https://github.com/PlayerEden/GridMates-Word-Search/issues/23).

**Decision:** CI-green build can be provided for user testing, but keep PR #22 draft until native usability and state preservation checks are confirmed.
