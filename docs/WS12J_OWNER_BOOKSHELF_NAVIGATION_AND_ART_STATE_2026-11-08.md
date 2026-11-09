# WS-12J — Owner v53 Screenshots → Book-First QA UX v54

**November 8, 2026 — Draft implementation, NOT final art approval.** Follow-up to owner visual feedback “Not bad... I guess” and [issue #114](https://github.com/PlayerEden/GridMates-Word-Search/issues/114).

## What the actual phone screenshots established

Woodland and Space **covers plus spines** render in the real Puzzle Bookshelf; Woodland's **emblem and chapter marks** render inside the book. Navigation and primary buttons are readable and consistent with the cream-paper style. But the book covers look small beside repeated oversized green buttons, and the book detail repeats large Play/Bookmark pairs. More importantly, **Chapter 2 restarts with “Page 1”** even though Chapter 1 already has a Page 1. These are verified screen observations, not evidence that saving/completion art has been exercised.

**Owner art direction remains open:** book illustrations are consistent but do not yet feel collectible or eye-catching. This milestone improves hierarchy and usability without pretending those prototypes are approved; a controlled, richer central-motif experiment follows after behavioral acceptance.

## Changes implemented on the QA/playable branch

1. **Book-wide page ordinals**: use the canonical published `catalog.pages(bookId)` flattened sequence in the book-detail listing, matching the preexisting gameplay header. Chapter 2's first page in the three-page pilot is **Page 3**, not another Page 1. All callbacks still use immutable book/page IDs; no journals change.
2. **Bigger book cover**: for QA/prototype-only book cards, source-verified cover display increases to **102×136dp** and matching spine to **16×104dp**. The source image, colors, SHA, asset version, and existing book progress are unchanged. Outside QA, existing native book-card presentation is untouched.
3. **Clear test-content title hierarchy**: the QA book title is visually **Woodland**, with “TEST BOOK · 2 chapters” secondary instead of a giant split “Woodland · TEST BOOK”. Actual `PuzzleBook.title`, immutable book IDs and per-book save data remain unchanged. Same in book-detail header.
4. **Less oversized CTA**: QA Bookshelf opens with a right-aligned native **Open book** button (minimum 160dp wide, minimum 48dp high), leaving the cover as the visual focus.
5. **Faster chapter browsing**: on ordinary-width phones (`BoxWithConstraints` content at least **270dp** and system `fontScale <= 1.25f`), a page's **Play page 3** and **Bookmark page 3** actions appear in a single row. On narrow layouts, high font scale and other callers, accessible 48dp controls remain stacked. All existing callbacks and meaningful text remain.
6. **State-checkable decorative art**: actual Compose images now get stable QA-only semantics test tags for cover, spine, emblem, chapter, bookmarked page, completed page and completed book, without visual-only progress.
7. **State regression source tests**: new Android test uses two test-only chapters to ensure **Page 3** numbering is stable, bookmarks resume the exact Page 3 and never show completion art prematurely; full completion shows the seal and allows replay but no false bookmark/next state. Existing home/Book test assertions updated for the new native QA label. The Android tests are *compiled* in CI, not automatically executed on a physical device.

### Explicitly untouched

- Core `BookCatalog` identifiers/board hashes and `BookProgressRules`
- AtomicFile/GMWB/GMWS journal formats and save filenames
- Original 12 approved Woodland portraits, profile IDs/unlocks, font files and licenses
- Word grid, select/trace, hint/score, Free Play/Daily/Nearby rules
- Actual source art SVG/PNG hashes and public empty book catalog
- Release art isolation; all 18+18 prototype visuals remain QA-only

### Install-over and release QA

Signed test package stays `com.playereden.gridmates.wordsearch.qa`, using its existing public test signer. VersionCode **54**, versionName **0.9.10-bookshelf-navigation-qa**. **Install over v53; do not uninstall** if you want to preserve local test progress.

The standard Android workflow must pass core/app unit tests, lint, Android test-source compilation, stable portraits/fonts/signing/package identity and assembleDebug. The separate debug/release workflow must verify **18 SVG+18 source-matched PNG prototype images in debug and zero in release** before sharing the APK. A green compile is not a physical test of bookmarks and page completion.

## Owner acceptance test (focused)

1. Open Bookshelf: confirm the bigger covers, smaller native **Open book** button, and clean “Woodland” title. Check that Space/Garden are still accessible.
2. Open Woodland: confirm the emblem, chapter marks and all **three** pages are represented as **Page 1**, **Page 2** and **Page 3** across the two chapters.
3. Select **Bookmark page 3**. Confirm the ribbon appears only beside Page 3, and **Continue reading** returns to the exact same puzzle.
4. Leave/force-stop/reopen: confirm the bookmark and any found words/hints remain without affecting Solo.
5. Solve a page: confirm only the genuinely completed page shows a completion seal, the progress counters change, and **Replay** does not award completion twice.
6. At 200% font size or on a narrow phone, the Play/Bookmark actions should stack rather than become too cramped. In landscape, no game-board touch targets should be covered.
7. Share screenshots of the title/cover layout, Chapter 2 Page 3, and bookmark/completion art. If the cover still feels flat, we'll next compare controlled illustration-motif variations **across all six art roles** rather than generating unrelated pictures.

**Release gates still pending:** physical owner QA, content editorial provenance, final art approval/licensing, native production catalog/art migration and real Android instrumentation execution.
