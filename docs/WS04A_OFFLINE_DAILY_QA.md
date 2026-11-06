# WS-04A — Offline Daily Puzzle: Design and QA Handoff

**Milestone:** WS-04A, staged after CI-green WS-03 Solo Beta. **Draft PR:** [#9](https://github.com/PlayerEden/GridMates-Word-Search/pull/9).
**Status:** Daily feature source committed; GitHub CI and physical acceptance are separate gates. Neither Word Search main nor Sudoku is modified by this implementation branch.

## Product behavior

- From Home, select **Daily Puzzle**. Today's local-calendar date always generates the same deterministic Easy 8×8 puzzle for all devices using explicit generator version 1, one of the three small starter themes, and a stable SHA-256-derived seed.
- Daily uses the same tactile Compose board, direction snap, marker effects, hint ladder and feedback as Solo. There is no alternate, weaker input implementation.
- Daily owns **separate** found words, hints, elapsed active time and one-time date completion status. Solo puzzles and Solo Results remain independent.
- Today's Daily can be resumed or reviewed if completed. Older dated Daily puzzles (completed or unfinished) remain in a Daily Journal. There is no streak timer that wipes prior play.
- A completion for a date is recorded only once; there is currently **no currency, random gift, exclusive unlock, or paid reward**. Such an economy is a separate owner decision in WS-04B.
- The app uses device-local calendar date at **entry**. Midnight does not replace a puzzle while solving it. Later visiting Daily shows the next day's new board while old date progress remains available.
- Android app foreground status gates the existing Solo and new Daily play timers; navigating Home and backgrounding the app should not count idle elapsed time.

## Save compatibility and backup

- Prior WS-03 schema 1 local progress migrates to schema 2 on the next successful save; old Solo active progress and Records are not intentionally reset or replaced.
- Schema 2 includes all dated Daily entries. The existing Android Settings Export/Import JSON file now includes **both Solo and Daily** history and active progress. Import previews daily totals and requires explicit overwrite confirmation.
- Unknown schema and invalid date/puzzle mismatches are rejected; an unreadable local save is preserved rather than silently overwritten.
- A date's puzzle seed uses the generator content version. Do **not** change previously published Daily word lists or seed rules without migration/versioning.
- **Signing warning:** GitHub QA APKs use temporary debug keys. Installing over an existing `.qa` app may fail. Export progress in Settings **before uninstalling any WS-03/WS-04A build that contains player data**. Reinstall only Word Search QA, then Import. Never uninstall Sudoku.

## Android test plan

### Core Solo regression
- [ ] The same Solo board and previously found words resume after Home, Android background, app force-stop and fresh launch.
- [ ] Solo hint stages and completed Records are unchanged; schema-v1 backup imports into WS-04A without losing Solo results.
- [ ] Haptic/audio toggles, theme change confirmation, Easy/Hard drag snapping and hard grid marker visuals still function.

### Daily acceptance
- [ ] Daily button appears on Home; opening it shows today, theme, six-word Easy puzzle and Daily Journal count.
- [ ] Find two Daily words; Home; open again; force-stop and reopen; exact Daily seed/words remain.
- [ ] Daily hints progress row -> start position -> faint full path without auto-solving; hints tracked independently from Solo.
- [ ] Finish today's Daily. Reopen and review; Daily completion count stays 1, and Solo completion count doesn't increase.
- [ ] Prior dated incomplete Daily remains available through the Journal; reopening it doesn't reset today's puzzle.
- [ ] Navigate away while actively playing and background app; timer doesn't count background-only time.
- [ ] Change local date on a controlled test emulator (optional) or use date-injection unit tests to verify tomorrow gets a different board while yesterday remains.

### Backup and install identity
- [ ] Settings > Export creates a JSON file **including daily entries**. Settings > Import shows Solo and Daily totals and requires explicit replacement confirmation.
- [ ] Invalid JSON or a future schema is rejected without changing current progress.
- [ ] Package remains `com.playereden.gridmates.wordsearch.qa` and co-installs separately from Sudoku.
- [ ] Before uninstalling any app with data, manually export; stable secure QA signing remains open.

### Visual/device checks
- [ ] Daily game on narrow portrait, Hard Solo portrait, landscape and larger fonts: clear Back/Home and near-grid word list.
- [ ] Marker highlights, touch drag and tap two endpoints remain accurate; no accidental page scrolling.
- [ ] The Daily Journal stays readable with many entries, including old unfinished dates.

## Automation required

- JVM tests: date parsing and canonicalization; deterministic identical date across reopens; different days differ; target completion idempotence; mismatched date seeds rejected; 7-day generator checks.
- Android instrumentation test **source**: loading a raw old schema-v1 save, migrating to schema 2, daily claims and two dates, force-stop-equivalent reload, backup/export/import round trip; one completed date remains counted once.
- GitHub workflow verifies core tests, app lint, Android test SOURCE compilation, APK assembly and AAPT package ID. UI instrumentation EXECUTION and actual physical gameplay must be recorded separately.
- Record run URL, source SHA, artifact ID and APK SHA-256, plus a known-good rollback checkpoint before calling the branch CI-green.

## Next owner decisions (not assumed)

- Whether a Daily puzzle should eventually offer small earnable stamps, capped weekly rewards, recycled exclusives or just an achievement; never force a streak penalty.
- Whether profile portraits/collections inherit the exact original 12 Woodland PNGs from Sudoku, with their existing stable IDs and source hash manifest.
- Whether a same-day challenge should remain Easy or offer optional difficulty variants; accessibility testing comes first.

## Safety/limitations

- **Not yet a physical QA-certified milestone.** A GitHub green check is compilation/testing evidence, not proof of thumb feel, process persistence, local time switch or landscape legibility.
- No cloud, network, daily monetization, collection economy, real sprites, social sharing, LAN Co-op, Shared Board Race or Speed Duel in WS-04A.

**Links:** [Master design](MASTER_GAME_DESIGN.md) · [Roadmap](DEVELOPMENT_ROADMAP.md) · [WS-03 source QA](WS03_BUILD_QA_2026-11-05.md).
