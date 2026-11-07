# GridMates: Word Search

*Good puzzles. Good company.*

GridMates: Word Search is a standalone, offline-first native Android word-search game in the GridMates puzzle family. It emphasizes comfortable, paperback-inspired puzzles and easy nearby play with someone on the same Wi-Fi.

## Current development status — November 7, 2026

**WS-06B v0.6.1 · automated Android QA passed (52 emulator tests) · real-device acceptance pending.** The working Android app is on **cumulative stacked draft PRs**, not the small default main branch.

- [Latest cumulative draft PR #43 — Versus Journal](https://github.com/PlayerEden/GridMates-Word-Search/pull/43) builds on Shared Board Race [#42](https://github.com/PlayerEden/GridMates-Word-Search/pull/42), stable QA updates [#41](https://github.com/PlayerEden/GridMates-Word-Search/pull/41), screenshot/UI polish [#40](https://github.com/PlayerEden/GridMates-Word-Search/pull/40), then #39 and older drafts.
- [Exact WS-06B Android QA receipt](docs/WS06B_BUILD_QA_2026-11-07.md): real API35 emulator **52 tests passed**, verified stable-signed v0.6.0→v0.6.1 in-place APK upgrade preserving existing saved profile data, SHA-256 and rollback checkpoint.
- [Cumulative v0.6.1 QA APK ZIP](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42348032724/artifacts/13564657182) (GitHub Actions artifact expires November 14, 2026).
- **Do not mark physical two-phone LAN or real handset update QA passed yet.** All PRs remain draft/unmerged until owner testing.

## Playable in the cumulative QA app

- Offline Solo and Daily word searches with different themes, book-size grids, challenges, assisted hints and local results.
- Profiles, achievements, free achievement-earned Woodland portraits and collectible board fonts. No cosmetic purchases or ad-to-unlock requirements.
- Manual backup/restore of local Solo/Daily/profile history, with protective validation; separate QA application ID and pinned public **test-only** signer. Future Play signing will be private and different.
- Nearby Co-op on the same Wi-Fi: open rooms or optional codes, discovery/manual join, shared found words, partner presence, chat and reconnect scaffolding.
- **Shared Board Race:** one shared board, first valid claim scores each word, real-time points, and winner/tie. Completed results now persist in a separate Versus stats section with Played/Wins/Losses/Ties and a word-by-word post-match comparison; no duplicate awards on reconnect. Co-op stays noncompetitive.
- Readable paper/ink UI, compact navigation, larger text and landscape support. Emulator checks complement, not replace, real physical touch/accessibility review.

**Coming next:** WS-06C **Speed Duel** (identical private boards with coordinated starts). Speed Duel is not yet playable.

## Source of truth

- [START HERE — current coding handoff](docs/START_HERE.md)
- [Development Roadmap](docs/DEVELOPMENT_ROADMAP.md)
- [Master Game Design](docs/MASTER_GAME_DESIGN.md)
- [WS-06A Race rules and acceptance plan](docs/WS06A_SHARED_BOARD_RACE_QA_PLAN_2026-11-07.md)

**Installing safely:** Stable-signed WS-05J v0.5.9 from PR #41 or WS-06A v0.6.0 can update over the top to v0.6.1. Old ephemeral-signed v0.5.8 and the separate PR #40 screenshot v0.5.9 APK may require **Export backup → verify JSON → uninstall Word Search QA only → install v0.6.1 → Restore backup**. Never uninstall or clear app storage without verifying a backup. Read the [full QA receipt](docs/WS06B_BUILD_QA_2026-11-07.md).

**Sister project:** [GridMates: Sudoku](https://github.com/PlayerEden/SudokuTogether). Word Search has its own package, saves, engine, LAN mode namespace, signing and repository.
