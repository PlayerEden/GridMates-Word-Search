# GridMates: Word Search

*A word-search puzzle is more fun with good company.*

**GridMates: Word Search** is a planned standalone native Android game in the **GridMates** puzzle family, featuring classic offline Solo and Daily puzzles, same-Wi-Fi two-phone Co-op, and two Versus modes: **Shared Board Race** and **Speed Duel**.

**Status:** **WS-03 Solo Beta is CI-built and ready for physical QA; first playable Solo word-search prototype was CI-built** in [stacked draft PR #4](https://github.com/PlayerEden/GridMates-Word-Search/pull/4), with a [verified QA APK from run #42138730965](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42138730965/artifacts/13467085867). The generator and native letter grid are implemented for testing, but physical input testing, saved progress, Daily, profiles and multiplayer are not complete. This is not a Google Play release.

**Latest Daily Alpha:** [WS-04A draft PR #9](https://github.com/PlayerEden/GridMates-Word-Search/pull/9) has CI-verified offline Daily puzzles, dated completion history and migration-friendly Solo + Daily backups. [Android QA APK](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42152990119/artifacts/13473941256) · [QA report and phone checklist](https://github.com/PlayerEden/GridMates-Word-Search/blob/feature/ws04a-offline-daily-2026-11-05/docs/WS04A_BUILD_QA_2026-11-05.md). **The build is not yet phone-tested or publicly released; export local progress before any QA uninstall.**

**Latest WS-04B Collections Alpha:** Offline profile name, a 12-entry Woodland catalogue, completion-based portrait unlocks and eight achievements now build cleanly on [draft PR #12](https://github.com/PlayerEden/GridMates-Word-Search/pull/12) in [CI #42159209553](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42159209553). This test APK uses temporary monogram previews; **the approved Woodland PNG art has not yet been imported**. See [QA and checksum receipt](https://github.com/PlayerEden/GridMates-Word-Search/blob/feature/ws04b-profile-woodland-achievements-2026-11-05/docs/WS04B_BUILD_QA_2026-11-05.md). Physical phone testing is still outstanding; protect saved data before QA reinstalls.

## Current playable checkpoint

**WS-03 Solo Beta** is CI-green on [draft PR #7](https://github.com/PlayerEden/GridMates-Word-Search/pull/7), stacked on the independent native foundation and the WS-02B touch-feel work. Its verified Android QA APK has offline Solo save/resume, a three-step hint ladder, a Records journal, progress backup/restore and smoother marker-style gameplay. **Physical on-phone acceptance is still open; this is not a production Google Play release.**

- [Get the latest Solo Beta QA APK from GitHub](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42149214204/artifacts/13471921695)
- [Build evidence and SHA-256](https://github.com/PlayerEden/GridMates-Word-Search/blob/feature/ws03-solo-save-results-2026-11-05/docs/WS03_BUILD_QA_2026-11-05.md)
- [Phone test playbook](https://github.com/PlayerEden/GridMates-Word-Search/blob/feature/ws03-solo-save-results-2026-11-05/docs/WS03_SOLO_BETA_PLAYTEST.md)

**Caution:** QA APK signing can differ between GitHub runs. Before uninstalling any build that contains saved progress, use **Settings → Export** to preserve your local Word Search JSON backup, then use **Settings → Import** after reinstalling.

## Start here

- **[Master Game Design](docs/MASTER_GAME_DESIGN.md)** — full vision, mode rules, gameplay, art and UX, reusable Sudoku systems, data safety, monetization boundaries and open design questions.
- **[Development Roadmap](docs/DEVELOPMENT_ROADMAP.md)** — staged execution plan and verification gates.
- **[START HERE — Development handoff](docs/START_HERE.md)** — current stage and safe next action for a new coding session.

## GridMates principles

- Simple for first-time and older players; polished enough to feel like a complete modern game.
- Offline-first, optional deliberate nearby play. No mandatory online account.
- Reuse the original GridMates: Sudoku animal portraits and proven UX patterns, while giving Word Search its own engine, app identity, saves, tests and releases.
- Respectful help, long-term progress, optional cosmetic rewards, and no ads interrupting puzzles.
- The design remains iterative: proposals should change when real phone playtests show a better answer.

**Next:** The [WS-02B feel-polish build](https://github.com/PlayerEden/GridMates-Word-Search/pull/6) has passed Android CI but needs hands-on review: smoother word dragging, new marker highlights, compact word-list layout and haptic feedback. [Download QA build from GitHub](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42146261876/artifacts/13469863578). Then address remaining phone feedback and begin WS-03 durable Solo saves/update-signing. The underlying WS-01 foundation is [draft PR #3](https://github.com/PlayerEden/GridMates-Word-Search/pull/3).

**Sister project:** [GridMates: Sudoku](https://github.com/PlayerEden/SudokuTogether) — do not treat its default main as the latest Android gameplay source; see the handoff for current references.

## First development tickets

- [WS-01 — Native Android foundation and safe Sudoku reuse](https://github.com/PlayerEden/GridMates-Word-Search/issues/1)
- [WS-02 — Deterministic word-search engine and touch prototype](https://github.com/PlayerEden/GridMates-Word-Search/issues/2)
