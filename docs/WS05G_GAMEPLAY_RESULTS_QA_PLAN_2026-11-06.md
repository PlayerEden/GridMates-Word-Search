# WS-05G — Game Feel, Puzzle Journal & Result Sharing

**November 6, 2026 · v0.5.6 QA, Android versionCode 18, same LAN protocol v4**
**Parent:** WS-05F draft PR #36, with B5 and WS-05A/B/C/D/E stacked below. Do **not** merge until user physical testing.

## Why this pass

The paperback-style home and launcher are in place. Gameplay and the solved-puzzle screen must match that level of visual clarity without affecting generator fairness, word recognition, saved puzzles or high-font touch behavior.

### Playing a puzzle

- A discreet **truthful progress track** sits below the existing two-row play HUD, using actual found/target counts and the same zero-progress rendering as the home screen.
- Word-found messages and hint guidance use a visually distinct, compact feedback strip; **TalkBack receives polite live announcements** instead of relying on changes in color or ephemeral toasts.
- Portrait feedback can occupy **two lines**, while landscape remains one line to avoid overwhelming an already short board area. Landscape board height reserve accounts for the extra bar.
- The word list becomes a proper **paperback puzzle index** with subtle outlined cells, crossed-out found words and consistent spacing.
- At narrow available width (less than 350 dp) or Android fontScale 1.3+, the word index uses **one readable column**, rather than squeezing/truncating two columns. Larger normal-text layouts keep two columns. All entries and the save-state note remain in a scrollable area.
- The existing zoom / Move / Find letter grid, actual hit mapping, hint ladder, score/records, saving and board glyph licensing **are not changed**.

### Finishing a puzzle

- **Results-first completion view:** after the final word, the large zoomed letter board is replaced by the finished journal with its actions visible. `Review completed board` opens the actual highlighted grid and word list; its `Results` action returns to the journal. This was chosen after reviewing real API35 screenshots of the initial split-board completion, where Play another and Share were hidden below the visible area.
- Paper-style completion journal with **accurate** words found, elapsed time and hint steps taken. Shows `Unassisted solve` only when zero hints were used, and labels other results `Assisted solve`.
- Primary **Play another puzzle** button for Solo, plus **Review completed board**, Records, Home and a newly added **Share results** button. Daily still follows its one-board-per-day rules and does not offer a new Daily puzzle.
- Sharing launches **Android's native text share sheet**. Summary includes mode, theme, board size, difficulty/challenge, word count, time and hint use, plus GridMates branding.
- **No data leak in share payload**: omit player display name, exact seed, room details, puzzle ID, placed words, game board, individual answers and local-device identifiers. No fabricated scores or streaks.
- Failure to open the platform share UI results in a user-facing error, not a crash; no server or internet request is required to compose the text.

## QA before checkpoint

- Core + app unit tests, Android lint, real APK and original Woodland portraits + all four B5 board fonts/OFL notices.
- App JVM tests for safe sharing with clean hint arithmetic, no private data, Solo vs Daily, and elapsed time.
- Actual **API35 emulator UI**: existing Solo/Daily/achievement/nearby tests, finished puzzle can review without reset; results controls accessible.
- Instrumentation captures **real playing board and completed-result screenshots** through emulator shell while Compose screen is active. Workflow additionally captures the home screen at fontScale 1.0 and 1.6, yielding four independent visuals.
- Review screenshot content for clipped labels, cramped buttons, hidden actions, board overflow and response at enlarged fonts. Build a follow-up fix on the branch if visual issues appear.
- Commit QA receipt and saved checkpoint at exact verified source SHA, record ZIP artifact, package version, checksum and 0-skip/0-failure emulator test tally.

## Still deferred for owner

Two-phone same-Wi-Fi onboarding, discovery, optional PIN, live chat, room rotation, host/guest reconnect, actual touch precision on 12×12/15×15/20×20 grids, 200% text at real phone sizes, screen reader comfort, actual share targets, Android package signing continuity and data preservation across sideload updates.

All stacked PRs remain **draft/unmerged**. No new purchases, advertisements, cloud rooms, extra artwork packs or speculative mechanics in WS-05G.
