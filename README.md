# GridMates: Word Search

*Good puzzles. Good company.*

GridMates: Word Search is a standalone, offline-first native Android word-search game in the GridMates puzzle family. It emphasizes comfortable, paperback-inspired puzzles and easy nearby play with someone on the same Wi-Fi.

## Current development status — November 7, 2026

**WS-06C v0.7.0 · automated Android QA passed (57 emulator tests) · real-device acceptance pending.** The working Android app is on **cumulative stacked draft PRs**, not the small default main branch.

- [Latest cumulative draft PR #44 — Speed Duel](https://github.com/PlayerEden/GridMates-Word-Search/pull/44) builds on Versus Journal [#43](https://github.com/PlayerEden/GridMates-Word-Search/pull/43), Shared Board Race [#42](https://github.com/PlayerEden/GridMates-Word-Search/pull/42), stable QA updates [#41](https://github.com/PlayerEden/GridMates-Word-Search/pull/41), screenshot/UI polish [#40](https://github.com/PlayerEden/GridMates-Word-Search/pull/40), then #39 and older drafts.
- [Exact WS-06C Android QA receipt](docs/WS06C_BUILD_QA_2026-11-07.md): real API35 emulator **57 tests passed**, verified stable-signed v0.6.1→v0.7.0 in-place APK upgrade preserving Solo/Daily/profile and Race journal, SHA-256 and rollback checkpoint.
- [Cumulative v0.7.0 QA APK ZIP](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42350506917/artifacts/13565352015) (GitHub Actions artifact expires November 14, 2026).
- **Do not mark physical two-phone LAN or real handset update QA passed yet.** All PRs remain draft/unmerged until owner testing.

## Playable in the cumulative QA app

- Offline Solo and Daily word searches with different themes, book-size grids, challenges, assisted hints and local results.
- Profiles, achievements, free achievement-earned Woodland portraits and collectible board fonts. No cosmetic purchases or ad-to-unlock requirements.
- Manual backup/restore of local Solo/Daily/profile history, with protective validation; separate QA application ID and pinned public **test-only** signer. Future Play signing will be private and different.
- Nearby Co-op on the same Wi-Fi: open rooms or optional codes, discovery/manual join, shared found words, partner presence, chat and reconnect scaffolding.
- **Shared Board Race:** one shared board, first valid claim scores each word, real-time points, and winner/tie. Completed results persist in separate Versus stats with Played/Wins/Losses/Ties and word-by-word post-match comparison; no duplicate awards on reconnect. Co-op stays noncompetitive.
- **Speed Duel:** independent, identical private boards on two phones; both tap Ready before GO. Only per-seat counts are public, while each phone sees its own discovered words. The host resolves the first accepted complete board; reconnection recovers a living host's state. Duel match history is a **later** feature.
- Readable paper/ink UI, compact navigation, larger text and landscape support. Emulator checks complement, not replace, real physical touch/accessibility review.

**Coming next:** WS-06D persistent **Speed Duel** journal and richer results; physical two-phone LAN acceptance remains deferred. Nearby protocol v6 means both phones must run compatible v0.7.0+ builds.

## Source of truth

- [START HERE — current coding handoff](docs/START_HERE.md)
- [Development Roadmap](docs/DEVELOPMENT_ROADMAP.md)
- [Master Game Design](docs/MASTER_GAME_DESIGN.md)
- [WS-06C Speed Duel rules and acceptance plan](docs/WS06C_SPEED_DUEL_QA_PLAN_2026-11-07.md)

**Installing safely:** Stable-signed WS-05J v0.5.9 from PR #41, WS-06A v0.6.0, or WS-06B v0.6.1 can update over the top to v0.7.0 (exact v0.6.1→v0.7.0 path emulator-verified). Old ephemeral-signed v0.5.8 and the separate PR #40 screenshot v0.5.9 APK may require **Export backup → verify JSON → uninstall Word Search QA only → install v0.7.0 → Restore backup**. Never uninstall or clear app storage without verifying a backup. Read the [full QA receipt](docs/WS06C_BUILD_QA_2026-11-07.md).

**Sister project:** [GridMates: Sudoku](https://github.com/PlayerEden/SudokuTogether). Word Search has its own package, saves, engine, LAN mode namespace, signing and repository.
