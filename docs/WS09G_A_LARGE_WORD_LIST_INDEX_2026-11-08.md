# WS-09G-A — A–Z navigation for future 70/105-word lists

**November 8, 2026 · draft UI preflight · no 40×40 / 50×50 public gameplay.**

This independent paperback-UX feature branch starts from [fully Android-QA-verified v0.9.4/code48 bookmark PR #74](https://github.com/PlayerEden/GridMates-Word-Search/pull/74) and does **not** integrate the still-unapproved v4 generator and content PRs [#78–#81](https://github.com/PlayerEden/GridMates-Word-Search/pull/81).

## Implemented, gated to future large lists

- `core/src/main/kotlin/com/playereden/gridmates/wordsearch/core/PaperbackWordIndex.kt` provides a simple alphabetically sorted **view copy** of the targets, available starting letters, first page for a chosen letter and exact one-based visible page ranges. This helper is **not a puzzle generator**, and never changes saved target order, IDs, placement, score, found-state or hints.
- `WordSearchScreen.WordList` displays **A–Z index** and `Words X–Y of Z` for **70 or more target words only**. The index opens a compact dialog with 48dp minimum touch-height buttons for only the letters represented; choosing a letter jumps directly to its first matching page. Normal Previous/Next still work. `Remaining` updates which letters exist and excludes found words.
- Existing released/QA board sizes are 8, 10, 12, 15, 20, 25 and 30, with **no more than 44 target words**. Their old visible ordering, word-list layout and controls are intentionally unchanged.
- Page budget reserves additional room for the A–Z/range row on a small or enlarged-font screen so the pager is not pushed beneath the game board.
- No new Android package, versionCode, production signing, saved-schema field, Solo selection, LAN protocol or V4 dictionary was introduced.

## Tests and exact verification

- `PaperbackWordIndexTest.kt` exercises synthetic 105-word lists (A–U, five words per letter), multiple page sizes, Remaining exclusions, 70-target threshold, no mutation and invalid page capacity.
- `GiantWordListIndexAndroidTest.kt` uses **synthetic test-only** `WordSearchPuzzle` data, not a public V4 puzzle: 105 words at **300dp** and jump to T/U (including range and page count), Remaining drops found A/B, and current 44-word list has **no giant index**.
- Dedicated [WS-09G-A Android QA run #42506333574](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42506333574) compiles core/app JUnit, lint, Android tests, and executes the **full historic 114 plus 3 new = 117** instrumentation suite with a **real same-signer v0.9.3/code47→private v0.9.4/code48** schema5 partial-Solo/paused/Daily/Versus install-over check.
- **Run status was pending at initial documentation creation.** No evidence is counted as passing until GitHub jobs and full emulator logs prove it. This workflow intentionally **does not upload a new APK**; artifacts, if any, contain test reports only.

## Still required before 50×50 is a genuinely comfortable game

- [ ] Test the word-index dialog and page controls manually at **320dp, 360dp, landscape, 200% Android font scale**, with TalkBack/keyboard and edge-to-edge safe areas; capture screenshots. UI automation on a synthetic 300dp layout does not prove all accessibility/readability goals.
- [ ] Decide whether to preserve current list page and Remaining filter during rotation/app restore (currently those transient Compose states reset on new activity). No disk schema change without a separately versioned source-matched safety review.
- [ ] Improve alphabet navigation if human testers find alphabet-only jumping insufficient for crowded letters; avoid huge scrolling lists or costly 100-page tap chains.
- [ ] Review and approve all V4 familiar/rare content and **player-facing display labels** (e.g., display *Brown dwarf* even if the grid answer is `BROWNDWARF`). Provisional Space familiar pool is only 121 against a 125-word initial safety buffer, with ten additional unapproved suggestions in [C3 PR #80](https://github.com/PlayerEden/GridMates-Word-Search/pull/80).
- [ ] Integrate a separately versioned immutable V4 generator into production `core` only after content editorial QA, full V1/V2/V3 goldens, historical profile reconstruction, backup/restore and device performance. Preserve QA-only versionCode48 baseline, never blind-install over unknown family signer.
- [ ] Physical 2-phone Co-op issue #53, release signing and main branch integration remain separate WS-09H gates.

**No 40×40/50×50 game mode is exposed or ready for general installation by this UI work.**
