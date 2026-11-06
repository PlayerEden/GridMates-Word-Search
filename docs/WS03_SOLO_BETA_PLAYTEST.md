# WS-03 — Solo Beta Playtest and Source QA

**Owner goal:** complete one substantial Solo milestone, then test gameplay and progress persistence thoroughly on Android.
**Branch:** feature/ws03-solo-save-results-2026-11-05; **draft PR:** [#7](https://github.com/PlayerEden/GridMates-Word-Search/pull/7).
**State:** First implementation committed; latest source CI and real-device acceptance must be confirmed separately.

## What's included

- Existing WS-02B tactile dragging, marker highlight trails, compact board/list layout, adjustable haptics and optional sound.
- Offline Solo progress: same theme, difficulty, seeded grid, found targets, hint levels and elapsed active time restored from Android private storage.
- AtomicFile-backed versioned schema with validation; malformed existing saves are preserved rather than silently overwritten.
- Three-stage hints for one unfinished word (row/area, first-letter location, full guide path); hints never automatically solve a word.
- One completion record per puzzle ID, with elapsed time, word count, hint use and assisted/no-hints status.
- Records page showing total completions, words found, assisted completions and history.
- Settings: manual export to a user-selected JSON document, import with schema checks and an explicit overwrite confirmation.
- Core JVM tests for state sanitizing, progress correctness, hint progression and result idempotence. Android instrumentation source includes isolated real save/reopen/import cases.

## Very important: QA signing and updates

GitHub Actions currently generates a fresh temporary Android debug signing identity. Different QA builds with the same app ID may **not** install over one another. The Word Search QA package is separate from Sudoku's.

1. Initial installation: If the old WS-02B Word Search test APK refuses an update, uninstall only **Word Search QA** first, then install WS-03. Do not uninstall Sudoku.
2. Once you begin using **WS-03**, do not uninstall it without saving a backup. Android uninstallation removes its private game progress.
3. Before any future version/reinstall, open Settings > Export and save a Word Search JSON backup to Documents or another location outside the app.
4. If Android later requires uninstall, reinstall the new QA APK then Settings > Import; review the preview and confirm. Reopen Solo and Records to confirm the data.
5. Stable protected QA signing remains a separate gate before promising seamless install-over-install updates. No Play release or cloud backup is claimed.

## Phone test sequence

### 1. Core puzzle input and feel
- [ ] Start Woodland Easy. Find words with finger drag (horizontal/vertical) and two-endpoint tap.
- [ ] Check active and permanent rounded highlighter trails, overlapping words, invalid drag cancellation and subtle haptics.
- [ ] Switch to Medium/Hard and find diagonal/backward words; compare compact word list and letter sizes.
- [ ] Toggle Feel vibration and sound, then try drag and successful word again.

### 2. Save and resume
- [ ] Find exactly two words. Tap Home, reopen Solo. Confirm identical grid and those two words still highlighted.
- [ ] Press Android Home, force-stop only the Word Search QA app from Android Settings, reopen and confirm same board and progress.
- [ ] Rotate orientation, revisit Home and Records and reenter. Confirm no missing/highlighted phantom words.
- [ ] Ensure the play timer continues only while actively inside the puzzle, not while the app is backgrounded.

### 3. Help and assisted results
- [ ] Tap Hint once: nudge identifies an unfinished target and nearby row.
- [ ] Tap Hint a second time: the SAME target's starting letter row/column.
- [ ] Tap Hint a third time: faint guide path, but word remains unchecked until you select it.
- [ ] Finish the puzzle, verify one result with time/word count/hint steps and assisted status.
- [ ] Reopen the completed puzzle several times; results count should remain one, not double-award.
- [ ] Start another puzzle, finish without hints and verify a separate No-hints result.

### 4. Backup/restore
- [ ] Settings > Export: choose a folder, confirm JSON backup file exists outside the app.
- [ ] Settings > Import that file: inspect preview, choose Cancel once, ensure nothing changes.
- [ ] Import again and confirm Restore; check result counts and active puzzle are preserved.
- [ ] Deliberately choose an unrelated JSON/text file and verify the app rejects it without overwriting progress.
- [ ] If you need to test an APK reinstall, export BEFORE uninstalling Word Search QA; import after reinstallation.

### 5. Layout and stability
- [ ] Check portrait, landscape, hard grid and large system text: buttons and word list remain visible.
- [ ] Check status bar readability, bottom navigation, Records, Settings and Change Puzzle dialogs.
- [ ] Play a longer session and background/resume repeatedly; observe any pause or crash.

## Automated evidence gates

- [ ] Green CI for **exact feature-source commit**, including :core:test, app build/lint, instrumented-test **compilation**, and AAPT QA package identity.
- [ ] APK SHA-256 and uploaded artifact recorded.
- [ ] Instrumentation **execution on Android** separately confirmed if performed; compilation alone does not count.
- [ ] Known-good source checkpoint and rollback preserved.
- [ ] Owner confirms touch feel, save/restore and Records on physical phone before calling WS-03 fully accepted.

## Intentionally deferred

Daily challenges, animal portraits, profiles beyond local Solo records, collections, LAN Co-op, Shared Board Race, Speed Duel, ads, purchases, Google Play signing, original sprite packs and cloud saves remain planned future milestones. The established generated-art registry and WS-02B procedural marker visuals remain intact.
