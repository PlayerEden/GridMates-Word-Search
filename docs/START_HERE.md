# START HERE — GridMates: Word Search

**Current implementation stack:** WS-06B Versus Journal v0.6.1 **automated QA PASS (52 emulator tests)**, physical two-phone QA OPEN. [Draft PR #43](https://github.com/PlayerEden/GridMates-Word-Search/pull/43) builds on [Race PR #42](https://github.com/PlayerEden/GridMates-Word-Search/pull/42), then [stable signing PR #41](https://github.com/PlayerEden/GridMates-Word-Search/pull/41), [screenshot polish PR #40](https://github.com/PlayerEden/GridMates-Word-Search/pull/40), then #39 and earlier drafts. The Android app is developed in cumulative, unmerged feature PRs, **not** on the repository's minimal main branch.

## Current state and documents

- [Master game design](MASTER_GAME_DESIGN.md): the full accessible paperback-inspired Android puzzle game vision.
- [Development roadmap](DEVELOPMENT_ROADMAP.md): phased implementation and acceptance requirements.
- [WS-05I Android QA](WS05I_BUILD_QA_2026-11-06.md): v0.5.8, 47 API35 tests, compact-screen and 200% Android text captures; real devices still outstanding.
- [WS-05J QA APK updates](WS05J_STABLE_QA_UPDATES_2026-11-07.md): stable test-only signer, v0.5.9, **verified emulator update preservation**, and **required one-time backup / reinstall / restore** from earlier APKs.
- [WS-05J build and QA receipt](WS05J_BUILD_QA_2026-11-07.md): exact source SHA, successful standard/emulator CI, downloadable cumulative APK and rollback checkpoint.
- [WS-06A first Versus mode and QA plan](WS06A_SHARED_BOARD_RACE_QA_PLAN_2026-11-07.md): Shared Board Race host/guest scoring, winner/tie, protocol v5 and testing.
- [WS-06A exact-source build and QA receipt](WS06A_BUILD_QA_2026-11-07.md): 49 passing Android emulator tests, prior v0.5.9→v0.6.0 update.
- [WS-06B Versus journal plan](WS06B_VERSUS_JOURNAL_QA_PLAN_2026-11-07.md) and [verified QA receipt](WS06B_BUILD_QA_2026-11-07.md): 52 emulator tests, v0.6.0→v0.6.1 saved-progress migration, updated artifact and rollback checkpoint. Speed Duel is later.
- [Word Search GitHub](https://github.com/PlayerEden/GridMates-Word-Search): the only source of truth for this game. Work stays separate from the SudokuTogether repository.

## Implemented across the cumulative stack

Real Solo and offline Daily, word-search touch selection and multiple book sizes, hints and results, local profiles/achievements, free Woodland portrait collections and board fonts, manual JSON backup/restore, same-Wi-Fi co-op with chat/presence and reconnection scaffolding, polished paperback UI and compact/large-text improvements. Shared Board Race builds on co-op ownership and now has separate durable on-device win/loss/tie records, results comparison and backup/restore. Co-op never awards a victory. WS-06A LAN protocol is v5; older v4 clients cannot join v5 rooms. Do not claim physical multiplayer QA passes until two phones are tested.

## Locked choices and guardrails

- Friendly to older and less technical players: readable, warm paper/ink visuals and low-friction nearby hosting/joining; optional room code.
- Cosmetics are earned for free with achievements and progress. No paid cosmetics, ad-watched cosmetics or cosmetic store.
- No ads, online login, cloud backend or purchases unless explicitly approved later.
- Separate Word Search Android package and save file from Sudoku; do not modify Sudoku in this project.
- The QA signing key in qa-signing is **public and test-only**. Never sign production releases with it, and never commit actual production signing keys or other secrets.
- Old pre-stable QA APKs (including the separately signed screenshot-only v0.5.9 PR #40 APK) cannot update to v0.6.0 in place. Export and verify a JSON backup before uninstalling; restore it afterward. Stable-signed PR #41 v0.5.9 **can update in place** to PR #42 v0.6.0; that exact path passed emulator QA, but not physical handsets.
- Draft PRs remain unmerged until owner hardware testing and review. Capture exact SHA, CI evidence and rollback checkpoint at major milestones; conserve GitHub Actions emulator minutes.

## Next

WS-06B race records, post-match comparison, exact-source 52-test Android QA and saved-progress migration are complete. WS-06C will implement distinct Speed Duel private-board play with a coordinated ready/go and authoritative finish. Physical two-phone multiplayer acceptance remains deferred private-board gameplay. Keep drafts unmerged.
