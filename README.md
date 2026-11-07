# GridMates: Word Search

*Good puzzles. Good company.*

GridMates: Word Search is a standalone, offline-first native Android word-search game in the GridMates puzzle family. It emphasizes comfortable, paperback-inspired puzzles and easy nearby play with someone on the same Wi-Fi.

## Current development status — November 7, 2026

**WS-06F v0.7.3 · automated Android QA passed (67 emulator tests) · physical two-phone acceptance pending.** The working Android app is on **cumulative stacked draft PRs**, not the small default main branch.

- [Latest cumulative draft PR #47 — Shareable Result Postcards](https://github.com/PlayerEden/GridMates-Word-Search/pull/47) builds on Nearby Presence [#46](https://github.com/PlayerEden/GridMates-Word-Search/pull/46), Speed Duel journal [#45](https://github.com/PlayerEden/GridMates-Word-Search/pull/45), Speed Duel [#44](https://github.com/PlayerEden/GridMates-Word-Search/pull/44), Versus Journal [#43](https://github.com/PlayerEden/GridMates-Word-Search/pull/43), Shared Board Race [#42](https://github.com/PlayerEden/GridMates-Word-Search/pull/42), stable QA updates [#41](https://github.com/PlayerEden/GridMates-Word-Search/pull/41), screenshot/UI polish [#40](https://github.com/PlayerEden/GridMates-Word-Search/pull/40), then #39 and older drafts.
- [Exact WS-06F Android QA receipt](docs/WS06F_BUILD_QA_2026-11-07.md): real API35 emulator **67 tests passed**, verified signed v0.7.2→v0.7.3 install-over preserving Solo/Daily/profile, Race and Duel journals; postcard PNG/URI QA, checksum and rollback checkpoint.
- [Cumulative v0.7.3 QA APK ZIP](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42358477314/artifacts/13569051862) (GitHub Actions artifact expires November 14, 2026).
- **Do not mark physical two-phone LAN or real handset update QA passed yet.** All PRs remain draft/unmerged until owner testing.

## Playable in the cumulative QA app

- Offline Solo and Daily word searches with different themes, book-size grids, challenges, assisted hints and local results.
- Profiles, achievements, free achievement-earned Woodland portraits and collectible board fonts. No cosmetic purchases or ad-to-unlock requirements.
- Manual backup/restore of local Solo/Daily/profile and separate Race/Duel history, with protective validation; separate QA application ID and pinned public **test-only** signer. Future Play signing will be private and different.
- Nearby Co-op on the same Wi-Fi: open rooms or optional codes, discovery/manual join, shared found words, partner presence, chat and reconnect scaffolding.
- **Shared Board Race:** one shared board, first valid claim scores each word, real-time points, and winner/tie. Completed results persist in separate Versus stats with Played/Wins/Losses/Ties and word-by-word post-match comparison; no duplicate awards on reconnect. Co-op stays noncompetitive.
- **Speed Duel:** independent, identical private boards on two phones; both tap Ready before GO. Only per-seat counts are public, while each phone sees its own discovered words. The host resolves the first accepted complete board; reconnection recovers a living host's state. Completed Duel results now have a **separate saved journal**, local wins/losses and a final-screen save confirmation.
- Friend-presence panel showing true local Wi-Fi connection and contributions, with counts-only opponent progress in Speed Duel. Completed Solo/Daily/Nearby puzzles can optionally preview an **offline 4:5 PNG result postcard** or share the existing privacy-safe text through Android Sharesheet; no remote names, chat, hidden solutions or room details are shared.
- Readable paper/ink UI, compact navigation, larger text and landscape support. Emulator checks complement, not replace, real physical touch/accessibility review.

**Coming next:** WS-06G plan a small, owner-reviewable **shared Co-op board appearance** milestone inspired by Sudoku. Word Search currently lacks actual owned board-skin tiers; do not invent ranks or grants, change private Duel appearance or disturb accessibility. Physical two-phone LAN acceptance remains deferred; protocol v6 means both phones must use compatible v0.7.0+ builds.

## Source of truth

- [START HERE — current coding handoff](docs/START_HERE.md)
- [Development Roadmap](docs/DEVELOPMENT_ROADMAP.md)
- [Master Game Design](docs/MASTER_GAME_DESIGN.md)
- [WS-06F PNG postcard design and QA plan](docs/WS06F_POSTCARD_DESIGN_AND_QA_2026-11-07.md)

**Installing safely:** Stable-signed WS-05J v0.5.9 from PR #41 and later builds (including WS-06D v0.7.1) can normally update over the top to v0.7.3. The **actual v0.7.2→v0.7.3 path passed Android emulator QA**. Old ephemeral-signed v0.5.8 and the separate PR #40 screenshot v0.5.9 APK may require **Export backup → verify JSON → uninstall Word Search QA only → install v0.7.3 → Restore backup**. Never uninstall or clear app storage without verifying a backup. Read the [full QA receipt](docs/WS06F_BUILD_QA_2026-11-07.md).

**Sister project:** [GridMates: Sudoku](https://github.com/PlayerEden/SudokuTogether). Word Search has its own package, saves, engine, LAN mode namespace, signing and repository.
