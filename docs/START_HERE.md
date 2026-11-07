# START HERE — GridMates: Word Search

**Current implementation stack:** WS-06A Shared Board Race development (real-device QA open), drafted after [WS-05I #39](https://github.com/PlayerEden/GridMates-Word-Search/pull/39). The Android app is developed in cumulative, unmerged feature PRs, **not** on the repository's minimal main branch.

## Current state and documents

- [Master game design](MASTER_GAME_DESIGN.md): the full accessible paperback-inspired Android puzzle game vision.
- [Development roadmap](DEVELOPMENT_ROADMAP.md): phased implementation and acceptance requirements.
- [WS-05I Android QA](WS05I_BUILD_QA_2026-11-06.md): v0.5.8, 47 API35 tests, compact-screen and 200% Android text captures; real devices still outstanding.
- [WS-05J QA APK updates](WS05J_STABLE_QA_UPDATES_2026-11-07.md): stable test-only signer, v0.5.9, **verified emulator update preservation**, and **required one-time backup / reinstall / restore** from earlier APKs.
- [WS-05J build and QA receipt](WS05J_BUILD_QA_2026-11-07.md): exact source SHA, successful standard/emulator CI, downloadable cumulative APK and rollback checkpoint.
- [WS-06A first Versus mode and QA plan](WS06A_SHARED_BOARD_RACE_QA_PLAN_2026-11-07.md): Shared Board Race host/guest scoring, winner/tie, protocol v5 and testing. Speed Duel is later.
- [Word Search GitHub](https://github.com/PlayerEden/GridMates-Word-Search): the only source of truth for this game. Work stays separate from the SudokuTogether repository.

## Implemented across the cumulative stack

Real Solo and offline Daily, word-search touch selection and multiple book sizes, hints and results, local profiles/achievements, free Woodland portrait collections and board fonts, manual JSON backup/restore, same-Wi-Fi co-op with chat/presence and reconnection scaffolding, polished paperback UI and compact/large-text improvements. Shared Board Race builds on co-op ownership but has separate win/tie rules. WS-06A LAN protocol is v5; older v4 clients cannot join v5 rooms. Do not claim physical multiplayer QA passes until two phones are tested.

## Locked choices and guardrails

- Friendly to older and less technical players: readable, warm paper/ink visuals and low-friction nearby hosting/joining; optional room code.
- Cosmetics are earned for free with achievements and progress. No paid cosmetics, ad-watched cosmetics or cosmetic store.
- No ads, online login, cloud backend or purchases unless explicitly approved later.
- Separate Word Search Android package and save file from Sudoku; do not modify Sudoku in this project.
- The QA signing key in qa-signing is **public and test-only**. Never sign production releases with it, and never commit actual production signing keys or other secrets.
- Older v0.5.8 and prior debug APKs have different ephemeral signing identity. Export backup before uninstalling and switching to v0.5.9. Future stable-signed QA updates should install over an existing app, with validation still required on real devices.
- Draft PRs remain unmerged until owner hardware testing and review. Capture exact SHA, CI evidence and rollback checkpoint at major milestones; conserve GitHub Actions emulator minutes.

## Next

WS-06A adds Shared Board Race and keeps Co-op as the default. Complete automated tests and the stable-signed 0.5.9→0.6.0 APK upgrade check. Follow with WS-06B race result persistence and WS-06C separate Speed Duel private-board gameplay. Keep drafts unmerged.
