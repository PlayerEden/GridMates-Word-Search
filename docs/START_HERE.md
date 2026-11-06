# START HERE — GridMates: Word Search

**Repository:** https://github.com/PlayerEden/GridMates-Word-Search  
**As of:** November 5, 2026  
**Status:** WS-02 **first playable Word Search** is committed to [stacked draft PR #4](https://github.com/PlayerEden/GridMates-Word-Search/pull/4), with [CI green #42138730965](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42138730965), reproducible word generation, touch/tap selection and an APK artifact. **Real-phone drag/layout testing and full Solo saving are still pending.** [WS-02 QA receipt](https://github.com/PlayerEden/GridMates-Word-Search/blob/feature/ws02-word-engine-touch-prototype-2026-11-05/docs/WS02_FIRST_PLAYABLE_QA_2026-11-05.md).

**Earlier foundation:** WS-01 native Android app scaffold is committed on [draft PR #3](https://github.com/PlayerEden/GridMates-Word-Search/pull/3). [Run #42131133443](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42131133443) passed core tests, Android lint, Android UI-test compilation and QA APK assembly with package-ID verification. **Not yet a playable Word Search:** no puzzle engine or real-phone QA. [Full branch QA receipt](https://github.com/PlayerEden/GridMates-Word-Search/blob/feature/ws01-native-foundation-2026-11-05/docs/WS01_BUILD_QA_2026-11-05.md).

**Latest development (WS-02B):** [Draft PR #6](https://github.com/PlayerEden/GridMates-Word-Search/pull/6) is CI-green in [run #42146261876](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42146261876) for touch snapping, marker highlights, compact board/list layout, sound/haptic options and custom nav icons. The [WS-02B QA receipt](https://github.com/PlayerEden/GridMates-Word-Search/blob/feature/ws02b-touch-highlights-layout-2026-11-05/docs/WS02B_BUILD_QA_2026-11-05.md) captures artifact checksum and phone acceptance. **Next:** the owner must test drag feel and layouts on an actual Android phone; only after this should we finalize UX and pursue WS-03 durable saves/update signing. Stable source checkpoint `checkpoint/ws02b-feel-polish-ci-green-2026-11-05`.

**Latest CI-green checkpoint — WS-03 Solo beta:** [Draft PR #7](https://github.com/PlayerEden/GridMates-Word-Search/pull/7) introduces private offline saves, resume, a three-step hint ladder, real Records, and manual JSON export/import. See [WS-03 phone-test playbook](https://github.com/PlayerEden/GridMates-Word-Search/blob/feature/ws03-solo-save-results-2026-11-05/docs/WS03_SOLO_BETA_PLAYTEST.md). This is stacked on WS-02B and [passed exact-source CI #42149214204](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42149214204), source b7ff74037e5615fd840c0a16609b7f2229ec416b; the verified [APK](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42149214204/artifacts/13471921695) is available to test. [QA receipt](https://github.com/PlayerEden/GridMates-Word-Search/blob/feature/ws03-solo-save-results-2026-11-05/docs/WS03_BUILD_QA_2026-11-05.md). **Do not confuse test compilation with on-device execution; physical QA remains open.** Debug QA signing is not stable across all runs; **export before uninstall** once WS-03 progress exists.

## Three documents that control the project

1. [Master Game Design](MASTER_GAME_DESIGN.md) — the long-term vision, modes, UX, puzzle rules, planned architecture, QA and decisions.
2. [Development Roadmap](DEVELOPMENT_ROADMAP.md) — what to build next, in order, with acceptance gates.
3. [README](../README.md) — concise public entry point and repo navigation.

**Next actionable gate: validate the WS-03 Solo beta on Android: dragging/markers, save on Home and force-stop/reopen, hint ladder, Records and export/import. Protect Sudoku. After owner feedback, fix blockers and move toward Daily/profiles/collections. Stable QA signing remains separate.** The source is in draft PR #4 stacked on PR #3, not on main. Do not treat either as a verified Play release.

## Locked player decisions

- **GridMates: Word Search** is a standalone Android app with its own repository; Sudoku and future Crossword remain separate apps.
- Share familiar GridMates experience/systems and reuse the **real** generated animal icons where possible.
- Main experiences: Solo, Daily, nearby Co-op, Shared Board Race Versus, Speed Duel Versus.
- Accessible, warm/paperback-inspired, modern native UX; older/less technical players should be comfortable.
- Nearby Wi-Fi multiplayer with low-friction open rooms, optional protected codes, no mandatory online accounts.
- This is a living plan; treat suggested size, balance, cosmetics and rewards as proposals until approved or playtested.

## Crucial Sudoku reference warning

The source app is not on Sudoku's tiny default main tree. Read the actual Android source and current verified feature branch:

- [Sudoku repo](https://github.com/PlayerEden/SudokuTogether)
- [Latest relevant v0.11C draft PR #28](https://github.com/PlayerEden/SudokuTogether/pull/28), branch feat/gridmates-nearby-animals; as checked November 5, 2026 PR head SHA 975e31a1e8cbfebad1fbbc7602b3340c357da7ef.
- [v0.11C QA](https://github.com/PlayerEden/SudokuTogether/blob/feat/gridmates-nearby-animals/docs/V0.11C_NEARBY_WOODLAND_QA.md)
- [Reusable puzzle foundation design](https://github.com/PlayerEden/SudokuTogether/blob/development/docs/REUSABLE_ANDROID_PUZZLE_FOUNDATION.md)
- [GridMates series identity](https://github.com/PlayerEden/SudokuTogether/blob/development/docs/GRIDMATES_BRAND_AND_SERIES.md)

Relevant reusable candidates: GameTheme, GameWidgets, Home/Join/Journal, LocalPlayerStore patterns, RoomDiscovery, profile/achievement patterns, AnimalPortraits and 12 Woodland PNG assets. Do **not** reuse Sudoku's 9×9 rules or wire Snapshot models verbatim. Distinct Word Search protocol, package, saves, source and signing identity are mandatory.

The full original portrait provenance lives in the Sudoku branch's design/woodland/packaged-assets.json. The new Word Search repo does **not** yet contain those assets; source them carefully during WS-01/WS-04, with their original IDs and provenance.

## Workflow and safety checklist

- Read master/roadmap **and** current repo/code/PR state before editing.
- Mark things proposed vs implemented; no fake “green build” or Play release claims.
- Prefer small self-contained feature PRs and checkpoints, explicit tests and verified Android APK installs.
- All actual new files, tests, docs and QA receipts belong in **this** repo. Update this handoff and roadmap after each stage.
- Reproduce existing behavior with source-backed test evidence before refactoring.
- Do not touch PlayerEden/SudokuTogether or change its package/signing/saves as part of Word Search work without explicit authorization.
- No release keys, passwords, room tokens or user save data committed to this public repository.
- Manual or limited GitHub Actions during development to avoid wasted minutes. Save source and commit SHA; Actions downloadable artifacts expire.
- Real two-phone multiplayer is a required QA gate for LAN stages; emulator/socket tests alone are insufficient.
- Do not implement live ads, purchases, global leaderboard or cloud identity simply because they appear as future options in the design.

## Starter message for a fresh coding chat

> Continue GridMates: Word Search in https://github.com/PlayerEden/GridMates-Word-Search. Read README, docs/START_HERE.md, docs/MASTER_GAME_DESIGN.md and docs/DEVELOPMENT_ROADMAP.md. Current state: WS-02 playable prototype exists in draft PR #4 stacked on WS-01 PR #3, with verified Android QA build at run 42138730965 and real-device gesture QA still pending. Before changes, read the branch QA receipt and latest Sudoku v0.11C source/QA PR #28; complete WS-02 physical puzzle input/layout testing, then plan WS-03 durable save/resume and results. Keep source, package, saves, LAN identity, QA signing and repository independent of Sudoku. Preserve the green source checkpoint, then document every additional verified test and update the roadmap/handoff. Do not claim the game or LAN is already implemented. The product includes both Shared Board Race and Speed Duel as future Versus modes, but build progressively.

## Design changes log

- **2026-11-05:** Master design established. WS-01 native source scaffold created in draft PR #3; complete source-build/Android UI-test compile/AAPT-verified QA build #42131133443. Physical UI/device co-install QA and gameplay engine are pending.

## Live first-step work tracking

- [GitHub issue #1 — WS-01 Native foundation and safe reuse](https://github.com/PlayerEden/GridMates-Word-Search/issues/1) — **CI green; on-device tests next**.
- [GitHub issue #2 — WS-02 Word Search generator and input](https://github.com/PlayerEden/GridMates-Word-Search/issues/2) — CI-green on draft PR #4; physical UX QA open.
