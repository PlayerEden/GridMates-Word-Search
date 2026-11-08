# WS-11D1 — Word Search Puzzle Bookshelf UI, empty-safe Compose prototype

**November 8, 2026 | source-ready, not player-visible until approved chapters are published**

## Goal

Take WS-11A/B's actual book/page model and WS-11C's isolated offline-progress store and create the first native Android Bookshelf interfaces, not static art or unrelated mockups. Be a practical, older-player-friendly paperback puzzle library that looks like the actual Word Search app.

## What is implemented

- **PuzzleBookshelfScreen**: one-column scrollable Compose Library, header, clear back button, readable book titles, chapter/page count, progress and an actual Open/Continue Book callback using the stable book ID.
- **PuzzleBookDetailScreen**: visible volume completion, prominent Start/Continue Reading when a real host play callback exists, ordered chapters, individual numbered pages with real board size/challenge, completed/replay status, optional bookmark callback.
- **Large library performance**: Compose LazyColumn keys use immutable book/chapter/page IDs rather than precomposing thousands of pages in one giant column.
- **Fail-closed state**: unknown progress/catalog IDs show an explicit recovery message instead of quietly rendering a clean empty save.
- **Safe no-play condition**: when the host has no page-play callback, no misleading Play buttons are rendered. Content stays descriptive until actual playable chapters have been approved and connected.
- **Paper styling**: reuse native PaperPanel/PaperEyebrow/PaperProgress, actual Compose cream/paper/forest/ink/serif styles and minimum 48dp touch areas. No unapproved AI-generated artwork, fake cover title images or unrelated ornamentation.
- **Instrumentation source tests**: empty catalog, 320dp one-column book card, open by immutable ID, bookmark-defined Continue, completed replay, absent game handler, incompatible-save warning. All test metadata is synthetic and must never be packaged as player content.

## Product principles

**Bookshelf != Collection.** The existing Collection screen still holds portraits/fonts/other earned cosmetics. Bookshelf is curated, fixed, offline-playable puzzle content once approved. Home continues to provide Free Play, My Puzzles, Daily and Nearby.

A future real app flow should be:

Home → **Puzzle Books** (single entry only when real content available) → Book → Chapter/Page → real puzzle board → results/verified completion → Continue Reading.

Do not clutter the existing four bottom tabs with a new one; use a single Home launcher. Page title/progress/buttons always stay native UI text. Chapter decorative images may eventually reuse the WS-12C/D vector motif system *only after owner licensing/visual approval*.

## Important unfinished pieces

1. WS-10 human editorial approval and immutable, shipped page manifests (the current V4 preview and review queue are NOT approved).
2. A real host book-play screen that loads the approved exact page, saves in-progress found words and timers in an independent book session (not as a Solo run), and calls WS-11C AtomicFile storage only after proof of completed words.
3. A release-gated published BookCatalog provider; it must be empty when no approved chapters exist. The existing MainActivity/GridMatesShell remains **unchanged** in this milestone so no fake Library action appears.
4. Implement actual local BookLibraryStore state observation and safe manual backup/restoration UX.
5. Validate physical-device 320dp, 200% font scale, landscape, screen-reader and save/update behavior. Android instrumentation test source/compilation is not the same as executing tests on an emulator.
6. Merge/rebase the separate WS-12 paperback art stack, after verifying layout/asset rights without changing old Woodland portraits.
7. Reconcile stacked PR order: gameplay #85 → book metadata #95 → book progress #96 → this UI branch. Do not merge this draft directly to main.

**Source UI is real and reusable; no actual book is published and no new playable Bookshelf APK is claimed.**
