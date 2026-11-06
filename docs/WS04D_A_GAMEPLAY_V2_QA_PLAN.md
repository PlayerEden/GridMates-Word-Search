# WS-04D-A — Puzzle Setup V2 / First Real-App Pass

**Stage:** Draft PR #20 on top of WS-04C art PR #15. **Not merged.** CI and owner phone acceptance are separate gates.
**Target QA:** Android v0.4.3-puzzle-v2-alpha (versionCode 6).

## Owner decisions carried forward

- **Board size and challenge are independent.** Compact 8×8 / six targets, Standard 10×10 / eight, Large 12×12 / ten.
- **Challenge primarily controls directions:** Relaxed forward across/down, Classic diagonals plus reverse orthogonal, Expert all eight and a preference for crossings.
- Difficulty also has a limited word-familiarity contribution, not an unverified claim about actual corpus frequency. Bigger does not simply mean harder.
- The new theme packs are *curated starter content*, roughly 32–36 words per theme. The huge researched lexicon, multilingual/Spanish packs and puzzle books remain deliberate later milestones, not rushed into this build.
- Shortening Home focuses players on Play Solo and Daily; unfinished multiplayer modes are clearly described rather than shown as full game features.

## Source changes (WS-04D-A)

- `core/WordSearchV2.kt`: new **English only** generator v2 with explicit versioned ID (`wordsearch:v2:en:THEME:BOARD:CHALLENGE:SEED`); seed-deterministic selection and placement; rejects accidental second occurrences of targets; avoids unbounded search.
- `core/SoloProgress.kt`: run and completion records optionally contain board size/challenge; a single dispatcher reconstructs either v1 or v2 by saved version fields.
- `app/SoloStore.kt`: save schema **4**, reads old schema 1/2/3, preserves identity/Daily/records, stores v2 setup and new results; legacy English v1 remains unmodified for old Solo/Daily. New Solo play uses v2. Changing a board requires an explicit user action.
- `app/WordSearchScreen.kt`: Theme + Board Size + Challenge are independently configurable in a plain-language setup dialog.
- `app/GridMatesShell.kt`: direct Solo and Daily entry points and honest brief explanation of upcoming nearby play.
- `app/SoloRecordsScreen.kt`: version-aware display of actual board size and challenge.
- Core JVM tests: all themes × boards × challenges × fixed seeds, deterministic results, actual target paths, v1 immutability, real v2 completion idempotence.
- Android instrumentation **test source**: v2 settings survive export/import/restart, older schema-3 puzzle remains exact, Daily remains v1.

## Safety gates / QA checklist

- [ ] Core JVM tests pass for all 27 theme/board/challenge combinations and checked seeds, with no unplaceable words or accidental duplicate target paths.
- [ ] Android compile, lint, instrumentation **source compilation**, APK package/version/hash.
- [ ] Execute instrumentation on Android emulator/device (not covered by standard compile workflow yet).
- [ ] Verify actual phone layouts, small screen and 200% font, especially setup dialog scrolling.
- [ ] Existing v1 8×8 Solo and dated Daily puzzle **must reconstruct unchanged** with restored hints, target IDs and claims. No surprise new board after upgrading.
- [ ] New v2 English puzzles persist exact seed, selected words, size/challenge and completion records; no duplicate completions.
- [ ] Verify taps/drags feel accurate, two-column word list legible, finished state obvious, user can return Home, start new, or view Records.
- [ ] Android full screen UI, back gestures, landscape and long portrait labels tested physically.
- [ ] Stable QA signing for actual in-place app upgrades remains issue #11, not solved by versionCode increment. **Export old Word Search QA profile before uninstalling an incompatible debug build.** Sudoku app must stay installed.
- [ ] Keep final source SHA, CI run, APK SHA-256, expiring artifact and rollback branch. Do not promote this draft before QA.

## Known constraints / next stage

- This is a **first** gameplay overhaul slice, not the complete WS-04D overhaul: no network multiplayer, no Spanish, no gigantic word library, no official scored difficulty calibration, no new portraits or purchasable content.
- Next WS-04D-B should focus on a compact in-game header, WordList/board visual polish, success feedback, thoughtful results, hint clarity, input/drag touch QA and executed Android runtime tests.
- Score balancing and official frequency labels should be calibrated against real player sessions, not guessed from word length.
- Puzzle v2 content is immutable once distributed to external testers with saved progress; future lexicon or generator changes must become v3 or use a pinned pack revision.

**Implementation principle:** Good puzzles first, then company—polished offline play and safe updates before adding nearby Co-op and Versus.
