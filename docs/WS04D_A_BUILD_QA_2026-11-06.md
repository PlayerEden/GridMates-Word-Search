# WS-04D-A — Final Build/QA Receipt: Independent Board Size and Challenge

**Result:** Kotlin JVM/core unit tests, Android compile, lint, instrumentation-test **source compilation**, original-portrait import and QA APK assembly passed. **Physical Android screen interaction and instrumented test execution remain open.**

## Exact successful build
- **Feature branch:** `feature/ws04d-v2-puzzle-setup-2026-11-06`, stacked draft [PR #20](https://github.com/PlayerEden/GridMates-Word-Search/pull/20) on WS-04C [PR #15](https://github.com/PlayerEden/GridMates-Word-Search/pull/15).
- **Compiled source SHA:** `15401a7b3644c1b14a262335d5416db2bd0095ae`.
- **[CI run #42173169314](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42173169314): SUCCESS**. `BUILD SUCCESSFUL in 2m 21s` / 59 actionable Gradle tasks, 57 executed, 2 cached.
- Pipeline includes `:core:test :app:testDebugUnitTest :app:lintDebug :app:compileDebugAndroidTestKotlin :app:assembleDebug`; all **12 original Woodland portraits checksum-imported and packaged**.
- **QA Android package:** `com.playereden.gridmates.wordsearch.qa`; versionCode **6**; versionName **`0.4.3-puzzle-v2-alpha-qa`**.
- **APK size:** 11,448,676 bytes. **APK SHA-256:** `cad9168641c1a73210bcc14faffd8b93e977c2f862cc33d2a442e0c68b7ede40`.
- [GitHub artifact ID 13481938907](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42173169314/artifacts/13481938907), name `GridMates-WordSearch-Woodland-Verified-QA`; ZIP digest `efddc204c31f6547d93d469ac7daa66f0cdc2ad2a50b4ba1d7f202b28b5a44c0`. Expires ~November 13, 2026 UTC.
- Artifact ZIP downloaded and unpacked independently; **APK SHA exactly matches CI**, and original Woodland assets counted (**12**).
- [Known-green source checkpoint](https://github.com/PlayerEden/GridMates-Word-Search/tree/checkpoint/ws04d-a-v2-setup-ci-green-2026-11-06) points to exact `15401a7b...` build source (later docs-only commits excluded).
- Previous first attempt CI [#42172844686](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42172844686) initially failed: missing `TextButton` import and an unplaceable v2 word set. Both fixed, deterministic alternate word-set retries added; final run green. Preserve the failure as honest QA evidence.

## Built changes
1. **Independent English v2 puzzle setup:** board sizes Compact 8×8, Standard 10×10, Large 12×12; Relaxed, Classic, Expert challenge policy, with permitted directions as the primary distinction.
2. **Curated starter vocabulary:** approximately 32–36 words per English theme. Editorial familiarity hints are qualitative, not claimed corpus frequency; huge researched library is future.
3. **Deterministic generator v2:** namespaced `wordsearch:v2:en` identity; all theme/size/challenge combinations exercised against fixed seeds and genuine reversible target-path validation. Rejected duplicates and bounded placement attempts.
4. **Legacy preservation:** unmodified English WordSearch v1 source/list; v1 saved Solo and dated Daily replay via explicit version dispatcher. Existing backup schemas 1–3 read by schema 4.
5. **New Android UI:** Theme, Board Size, Challenge independently selectable in an explanatory setup dialog; new play stored with matching versioned settings.
6. **Cleaner Home screen:** direct Solo/Daily activities, honest small preview of upcoming LAN rather than a long inactive menu.
7. **Records:** show true v2 board size and challenge; v1 records still display legacy information.
8. **Safety:** complete-once saved results, no 1Hz board regeneration in timer, v2 data rejected in legacy Daily entries; tests for legacy identity and v2 progress.
9. **Approved original Woodland art preserved** byte-for-byte by pinned Sudoku import.

## What this QA has NOT proved
- Real Android instrumentation **execution** (test sources compiled only), emulator screenshots, accessibility and physical touch behavior.
- Install over the existing QA app: **debug signing may differ across GitHub runner builds**; upgrading may yield app-not-installed. Always export old Word Search progress via Settings before uninstall/reinstall; NEVER uninstall Sudoku as part of Word Search QA.
- User-approved difficulty fairness across age groups and word-familiarity levels. The selection weights and vocabulary need genuine playtests.
- Full WS-04D tactile feel, finished-game polish, high-quality results and stronger Home visuals; these are **WS-04D-B** goals.
- Any LAN/multiplayer, Spanish, giant word library, ads, purchases or additional character collections.

## Consolidated acceptance for phone/emulator
- [ ] Save previous v0.4.2 JSON export, then verify schema4 import and identical old seeded v1 board, target/hints/results, and dated Daily completion.
- [ ] Select each board size with a different challenge; confirm board remains readable and directions differ appropriately.
- [ ] Force-stop and resume a partial v2 puzzle; exact letters, word list, elapsed time, selected difficulty and finds remain.
- [ ] New Puzzle and Customize give explicit warning before replacing unfinished boards; existing completed records remain.
- [ ] Compact/narrow and landscape orientation, large text, TalkBack and two-tap/drag behavior.
- [ ] Completed results display actual options in Records with correct assisted flags.
- [ ] Confirm original 12 Woodland images and independently installable package unchanged.
- [ ] Stable QA signing and actual upgrade-in-place acceptance; track separately in issue #11.

See [WS04D-A implementation plan](WS04D_A_GAMEPLAY_V2_QA_PLAN.md) for detailed scope. Keep draft PR #20 **unmerged** until user/device acceptance.
