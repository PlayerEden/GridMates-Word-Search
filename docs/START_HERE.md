# START HERE — GridMates: Word Search

**Repository:** https://github.com/PlayerEden/GridMates-Word-Search  
**As of:** November 5, 2026  
**Status:** Design baseline only. No Word Search Android app, APK, game engine, content pack, or multiplayer implementation has been verified in this repository.

## Three documents that control the project

1. [Master Game Design](MASTER_GAME_DESIGN.md) — the long-term vision, modes, UX, puzzle rules, planned architecture, QA and decisions.
2. [Development Roadmap](DEVELOPMENT_ROADMAP.md) — what to build next, in order, with acceptance gates.
3. [README](../README.md) — concise public entry point and repo navigation.

**Next actionable milestone: WS-01 — create an independent native Android foundation and safe Sudoku reuse inventory.** Do not start by writing a giant Word Search engine and all five modes at once. Make an installable, testable project shell first.

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

> Continue GridMates: Word Search in https://github.com/PlayerEden/GridMates-Word-Search. Read README, docs/START_HERE.md, docs/MASTER_GAME_DESIGN.md and docs/DEVELOPMENT_ROADMAP.md. Current state is a planning-only baseline. First pursue WS-01: inspect the latest Sudoku v0.11C source/QA PR #28 and its actual reusable assets, then create a distinct native Kotlin/Jetpack Compose Word Search Android foundation. Keep source, package, saves, LAN identity, QA signing and repository independent of Sudoku. Build and test a small native UI/asset proof, preserve SHA/checkpoint/QA evidence, and update the roadmap and handoff. Do not claim the game or LAN is already implemented. The product includes both Shared Board Race and Speed Duel as future Versus modes, but build progressively.

## Design changes log

- **2026-11-05:** First planning baseline created; next source development is WS-01.
