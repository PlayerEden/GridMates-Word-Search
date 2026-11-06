# WS-02 — First Playable Word Search: Build and QA Receipt

**Date:** November 5, 2026 (US Eastern evening; CI completed November 6 UTC)
**Status:** **JVM engine tests + Android app build/lint + instrumentation source compilation + package verification PASSED.** Actual Android UI-test execution and real-device gesture/visual review are **OPEN**. Draft stacked PR remains unmerged.

## Source and checkpoint

- Working [draft PR #4](https://github.com/PlayerEden/GridMates-Word-Search/pull/4) on `feature/ws02-word-engine-touch-prototype-2026-11-05`, stacked on draft [WS-01 PR #3](https://github.com/PlayerEden/GridMates-Word-Search/pull/3).
- Verified source SHA: `37a8b84e0be7428e13f010ff31ee4923954c2684`.
- Durable [checkpoint/ws02-first-playable-ci-green-2026-11-05](https://github.com/PlayerEden/GridMates-Word-Search/tree/checkpoint/ws02-first-playable-ci-green-2026-11-05) points to that exact SHA.
- Neither Word Search `main` nor the Sudoku repository was changed by the gameplay source commits.

## What the code now does

1. Deterministic Kotlin generator with versioned puzzle IDs, three hand-curated prototype categories (Woodland, Outer Space, Garden), and Easy 8×8 (6 words), Medium 10×10 (8 words), Hard 12×12 (10 words) candidate boards.
2. Word placements and overlaps use legal straight paths; medium/hard allow all eight directions and backward words; generated filler is scanned so every listed target appears exactly once, otherwise a bounded retry occurs.
3. Exact placement-path validation accepts either endpoint direction, rejects invalid/bent/out-of-bounds paths and prevents a target from being counted twice.
4. Compose Solo prototype has an interactive square grid, live highlighted drag, optional tap first/last letters, found-word checklist, 3 themed packs, difficulty switching, New Puzzle and a completion card.
5. A small Android UI smoke-test source exercises opening Solo, visible target list, New Puzzle and Home. It has compiled but **was not executed on an emulator/device**.

## CI history

| Run | Result | Evidence |
| --- | --- | --- |
| [42138338021](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42138338021) | FAILED | App compiler caught unresolved Compose pointer `consume` import and an unsafe cross-module nullable `result.target` access. Core test task executed before app compilation failed. |
| [42138730965](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42138730965) | **PASS** | Removed invalid import; bound the target to a nonnull local; used `rememberUpdatedState(onSelect)` so drag callbacks don't restart on recomposition. Core/unit-test tasks, lint, Android UI test compilation, APK assembly and AAPT package check succeeded. |

### Verified final build

- Source commit: `37a8b84e0be7428e13f010ff31ee4923954c2684`.
- `BUILD SUCCESSFUL in 2m 26s`; 59 actionable Gradle tasks (57 executed, 2 from cache).
- Tasks included `:core:test`, `:app:testDebugUnitTest`, `:app:lintDebug`, `:app:compileDebugAndroidTestKotlin`, `:app:assembleDebug`.
- Core JVM tests cover forward/reverse paths, diagonal/horizontal/vertical legality, bad endpoints and bends, seed reproducibility, multiple themes/difficulties and target completion. **Note:** the Android app's own JVM test task currently has no test sources; the core module is where gameplay rules are tested.
- QA package (AAPT verified): `com.playereden.gridmates.wordsearch.qa`.
- Version code `1`, version name `0.1.0-prealpha-qa` (unchanged from WS-01 shell; release versioning needed later).
- Final APK filename inside artifact: `app-debug.apk`; **10,147,373 bytes**.
- APK SHA-256: `fd3ab0d9830003611cc34b9635035be125618d0f451c269ffe29592d60bf588f`.
- GitHub [QA artifact ID 13467085867](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42138730965/artifacts/13467085867); zipped artifact still carries legacy CI display name `GridMates-WordSearch-WS01-QA` because workflow is shared.
- Artifact ZIP SHA-256: `e2f97473e7115080bfbd8abaf2fbab0c35dc3fceeb242a8ba43535291fb90b19`; this is the ZIP digest, **not** the APK digest.
- Artifact retention: seven days; expiration scheduled around November 13, 2026 UTC. The source/checkpoint remains durable.
- The GitHub artifact was independently downloaded, extracted and its APK rehashed; it matched the build log.

## Important prototype limits

- **No disk save or historical record yet**. Found words persist during rotation where Compose state is saved but not across app restarts; opening another Solo session starts fresh. Durable saving is WS-03.
- **No Daily, profiles, owned animal portraits, LAN, co-op, versus, chat, rewards, online ranking, ads, paid purchases, signing guarantee or Play release**.
- UI test source compilation is **not** physical touch testing or Android instrumentation execution; actual drag/tap behavior, app lifecycle, tiny phones, large text and landscape need acceptance.
- The APK uses ephemeral CI debug keys. A later QA package with the **same application ID** may not update in place if signed by a different run's debug key. For this prototype, uninstall/reinstall may be necessary; no durable Word Search progress exists yet. **Do not depend on this behavior once saving is introduced**—fix stable signing before upgrade QA.
- The word themes are development prototype content. Word lists, licensing review and difficulty calibration still need owner review before release.

## Practical user playtest checklist

- [ ] Install the QA APK alongside GridMates: Sudoku and confirm Sudoku is not replaced.
- [ ] Tap `Try Solo Word Search`: a readable 8×8 board and six-word list appear.
- [ ] Drag across horizontal and vertical target words; ensure correct matches highlight and checklist updates.
- [ ] Try tap first/last letters for a word; verify it works even if dragging is inconvenient.
- [ ] Switch Medium/Hard; find backward or diagonal targets and check hit accuracy on a narrow screen.
- [ ] Trace an incorrect or bent selection; ensure it does not award an unrelated word.
- [ ] Find every listed word and verify the completion card and Play Another button.
- [ ] Change theme/difficulty; rotate device; check any clipping, letter size and controls.
- [ ] Verify large system font does not make letter selection impossible.
- [ ] Check actual UI smoke test on emulator or phone; screenshots needed for visual acceptance.

## Follow-up gates

- Correct real-phone touch and visual problems before treating WS-02 as playtested.
- Revisit accessible selection at 12×12 and true screen-reader alternative, not only per-cell labels.
- Add saved progress / versioned state / safe upgrade path and later stable QA signing in WS-03.
- Preserve both draft PRs and checkpoint branches until reviewed. WS-02 builds on WS-01; do not merge a stacked child before resolving its base.

**Status conclusion:** first testable gameplay APK and deterministic core **verified by CI**, but WS-02 product acceptance remains **open** until on-device interaction is checked.
