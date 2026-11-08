# WS-11A/B — Playable Puzzle Bookshelf UX + Immutable Chapter Metadata

**November 8, 2026 | Draft implementation foundation | No published books or APK from this pass**

## Product intention

The user's desired Bookshelf is a new playable section: curated, fixed, replayable sets of word searches with chapters, real bookmarks, visible progress and an obvious Continue button. It is not a cosmetic collection, and it does not replace existing **Free Play**, **Daily** or **Nearby**.

The eventual library should feel like opening a familiar paperback puzzle book. Use the actual Word Search app's cream/paper/forest/ink/gold palette and its serif headings/native controls. The WS-12C/D Quiet Illustrated Paperbacks motif system provides decorative covers and accents *after* art approval; all title/chapter/progress/status labels remain real Compose text.

## Navigation on a real phone

Home should continue to prioritize its current usable Solo/Free Play, Daily and Nearby actions. When the first real books are approved, add ONE distinct **Library / Puzzle Books** launcher card; avoid a crowded fifth bottom tab. Suggested route:

Home -> **Puzzle Books** -> a short, readable Library -> selected book -> chapters/pages -> actual word-search board -> results -> **Continue Reading** (next unfinished page).

- Returning players should see **Continue Reading** prominently for an unfinished book.
- First-time players see a limited list of real available books and a simple **Open Book** action, not a settings form.
- Book list shows a small approved thematic cover/decorative token, native title, chapters/pages, total completed and optional compact progress bar. No tiny fake spine lettering.
- Inside a book, chapter names are plain text; page numbers and states are readable even if images are absent. Completed pages remain replayable with no duplicate completion credit.
- **Free Play** still means endless standalone randomized puzzles. **My Puzzles** still means saved Solo sessions. **Collection** still means earned avatars/fonts/cosmetics. Those names must not be interchanged.
- Default phone layout is ONE column with at least 48dp primary actions. At 320dp width, landscape and 200% font scale, text wraps, cards grow vertically, and artwork yields space to labels.
- Avoid accounts, forced internet, a shop, ads between pages or punitive streaks in this core Library. Add theme collections only when editorial content and artwork are approved.

## Real implementation completed in WS-11A/B source

In core, BookCatalog declares BookPage, BookChapter and PuzzleBook. An **empty** catalog is valid. Only test fixtures include example pages; no unapproved words or fake chapter data are bundled as production books.

Each page entry pins:

- Stable book/chapter/page IDs, chapter order and display names.
- Actual existing Word Search puzzle ID, generator version, board preset, challenge, seed and lexicon version.
- A canonical SHA-256 of exact grid rows **and** full ordered target answer paths (BookBoardDigest v1). A matching seed does not substitute for exact bytes.

BookCatalog rejects duplicate IDs/puzzles, malformed hashes, unapproved PREVIEW puzzle IDs and invalid legacy board presets for new chapters. It checks actual puzzle contents before any completion. Upgrades may add new books and change display labels, but cannot silently remove/reorder/change previously published content behind old IDs.

BookProgressRules stores **separate** completed-page IDs and an optional next-page bookmark. Continue resolves the bookmark or first unfinished page; completion requires the exact registered board and every real found target, is idempotent, and never overwrites another game's result. Unknown book/page keys fail closed instead of silently disappearing.

## Deliberately not implied by this implementation

- No production Woodland/Space/Garden book pages have been approved or written into an Android catalog.
- No Android Library navigation is turned on; showing a non-playable mock shelf would confuse players.
- No new saved-profile field/schema migration or import/export adapter yet; a pure progress model exists, **not persistent** game records.
- No claim of a finished page-content editor, lexicon V4 approval or SDK/runtime release.
- No Adobe refinement or approved cover art was performed. WS-12C/D art remains review-only and a separate branch stack.

## Next linked milestones

1. **WS-10C/D actual editorial approval and immutable content loader**. Do not promote candidate-word rows or provisional V4 preview puzzles as book pages.
2. **WS-11C pilot publisher:** approve three small themed volumes, serialize canonical chapter/page payloads, regenerate/check SHA-256, verify no repeats and sign off text and placements.
3. **WS-11D Android:** use the new model to render the actual Library/Book/Chapter views, wire Book Puzzle Play and Continue, persist book progress locally with crash-safe atomic saving and backup/import protection. Preserve old schema5 and install-over, no fake book view before the first pilot exists.
4. **WS-12 final artwork:** review repeatable paperback covers/markers at actual phone sizes; only then copy hash-verified resources into Android.
5. **WS-11E test:** full source JUnit, 320dp/200% accessibility screenshots, physical phone gestures, force-stop/resume, upgrade/export/import/rollback, offline/no-network, long-chapter content novelty and completion idempotence.

## Evidence / acceptance checklist

- [x] A pure Kotlin metadata model exists in the actual Android game's core module.
- [x] Stable puzzle hash and explicit matching/upgrade invariants implemented.
- [x] Book progress/continue/verified completion implemented separately from schema-5 Solo.
- [x] Test fixtures cover tampering, duplicates, empty catalog, no-preview, progress, bookmarks and preexisting V2 IDs.
- [ ] Exact-commit CI green including all earlier core regression tests.
- [ ] Approved editorial book pages and visual assets.
- [ ] Android UI, local persistence, backup and real-phone QA.

**Keep this work a DRAFT stacked on WS-09F-F. It is a real tested foundation, not a claim that playable puzzle books have shipped.**
