# GridMates: Word Search

*Good puzzles. Good company.*

GridMates: Word Search is a standalone, offline-first native Android word-search game in the GridMates puzzle family. It emphasizes comfortable, paperback-inspired puzzles and easy nearby play with someone on the same Wi-Fi.

## Current development status — November 7, 2026

**Latest: WS-07C v0.7.9/code33, automated 83/83 API35 Android tests PASS**, with actual same-QA-signed v0.7.8→v0.7.9 saved-data upgrade PASS. [Source-matched QA receipt, APK ZIP, checksums and rollback](docs/WS07C_BUILD_QA_2026-11-07.md). [Draft PR #56](https://github.com/PlayerEden/GridMates-Word-Search/pull/56) adds accessible non-drag row/column word selection, 48dp zoom controls and an accessible board action. **Real two-phone word marking and TalkBack acceptance remain open; owner has deferred phone testing.** This app is in stacked draft branches, no `main` merge or Play release.
- [Latest cumulative draft PR #48 — Shared Co-op Board Looks](https://github.com/PlayerEden/GridMates-Word-Search/pull/48) builds on Postcards [#47](https://github.com/PlayerEden/GridMates-Word-Search/pull/47), Nearby Presence [#46](https://github.com/PlayerEden/GridMates-Word-Search/pull/46), Speed Duel journal [#45](https://github.com/PlayerEden/GridMates-Word-Search/pull/45), Speed Duel [#44](https://github.com/PlayerEden/GridMates-Word-Search/pull/44), Versus Journal [#43](https://github.com/PlayerEden/GridMates-Word-Search/pull/43), Shared Board Race [#42](https://github.com/PlayerEden/GridMates-Word-Search/pull/42), stable QA updates [#41](https://github.com/PlayerEden/GridMates-Word-Search/pull/41), screenshot/UI polish [#40](https://github.com/PlayerEden/GridMates-Word-Search/pull/40), then #39 and older drafts.
- [Exact WS-06G Android QA receipt](docs/WS06G_BUILD_QA_2026-11-07.md): real API35 emulator **71 tests passed**, signed v0.7.3→v0.7.4 install-over preserving Solo/Daily/profile, Race and Duel journals; host-authoritative Co-op style protocol v7 tests, checksum and rollback checkpoint.
- [Cumulative v0.7.4 QA APK ZIP](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42360155148/artifacts/13569895376) (GitHub Actions artifact expires November 14, 2026).
- **Do not mark physical two-phone LAN or real handset update QA passed yet.** All PRs remain draft/unmerged until owner testing.

## Playable in the cumulative QA app

- Offline Solo and Daily word searches with different themes, book-size grids, challenges, assisted hints and local results.
- Profiles, achievements, free achievement-earned Woodland portraits and collectible board fonts. No cosmetic purchases or ad-to-unlock requirements.
- Manual backup/restore of local Solo/Daily/profile and separate Race/Duel history, with protective validation; separate QA application ID and pinned public **test-only** signer. Future Play signing will be private and different.
- Nearby Co-op on the same Wi-Fi: open rooms or optional codes, discovery/manual join, shared found words, partner presence, chat, reconnect and **temporary shared room board looks** (free Classic Paper / Linen Sketch / Blueprint Study previews, round-frozen, with a Classic-on-my-phone readability override). Neither player's permanent font or collection changes.
- **Shared Board Race:** one shared board, first valid claim scores each word, real-time points, and winner/tie. Completed results persist in separate Versus stats with Played/Wins/Losses/Ties and word-by-word post-match comparison; no duplicate awards on reconnect. Co-op stays noncompetitive.
- **Speed Duel:** independent, identical private boards on two phones; both tap Ready before GO. Only per-seat counts are public, while each phone sees its own discovered words. The host resolves the first accepted complete board; reconnection recovers a living host's state. Completed Duel results now have a **separate saved journal**, local wins/losses and a final-screen save confirmation.
- Friend-presence panel showing true local Wi-Fi connection and contributions, with counts-only opponent progress in Speed Duel. Completed Solo/Daily/Nearby puzzles can optionally preview an **offline 4:5 PNG result postcard** or share the existing privacy-safe text through Android Sharesheet; no remote names, chat, hidden solutions or room details are shared.
- Readable paper/ink UI, compact navigation, larger text and landscape support. Emulator checks complement, not replace, real physical touch/accessibility review.

**Coming next:** [WS-07A–G quality milestones](docs/DEVELOPMENT_ROADMAP.md#live-development-priority--ws-07-refinement-arc-november-7-2026): fix touch coordinate mapping/direction locking, no-scroll adaptive gameplay, accessible 20×20 precision, physical two-phone Co-op/Race/Duel, hint/menus/content, save/performance regression and beta gate. Do not represent preview looks as rare/earned cosmetics. Both phones must use LAN protocol **v7 compatible** builds; v0.7.7 is an emulator-verified QA candidate requiring real-phone acceptance.

## Source of truth

- [START HERE — current coding handoff](docs/START_HERE.md)
- [Development Roadmap — active WS-07A–G](docs/DEVELOPMENT_ROADMAP.md)
- [WS-07: Playtest evidence, input and accessibility research, UX specification](docs/WS07_PLAYTEST_RESEARCH_AND_UX_SPEC_2026-11-07.md)
- [Master Game Design](docs/MASTER_GAME_DESIGN.md)
- [WS-06G shared Co-op look rules and QA plan](docs/WS06G_SHARED_COOP_BOARD_LOOKS_QA_PLAN_2026-11-07.md)

**Installing safely:** Stable-signed WS-05J v0.5.9 from PR #41 and later builds (including WS-06D v0.7.1) can normally update over the top to v0.7.4. The **actual v0.7.3→v0.7.4 path passed Android emulator QA**. Old ephemeral-signed v0.5.8 and the separate PR #40 screenshot v0.5.9 APK may require **Export backup → verify JSON → uninstall Word Search QA only → install v0.7.4 → Restore backup**. Never uninstall or clear app storage without verifying a backup. Read the [full QA receipt](docs/WS06G_BUILD_QA_2026-11-07.md).

**Sister project:** [GridMates: Sudoku](https://github.com/PlayerEden/SudokuTogether). Word Search has its own package, saves, engine, LAN mode namespace, signing and repository.
