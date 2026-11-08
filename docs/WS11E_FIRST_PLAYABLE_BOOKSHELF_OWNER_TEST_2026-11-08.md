# WS-11E — First Hands-on Puzzle Bookshelf Playtest APK

**November 8, 2026 | QA-only preview, NOT editorially approved or public production content**

## Purpose

Stop making a Bookshelf that exists only as source. Give the game owner a real Android APK with an obvious Home entry and immediately usable, offline, chapter-based puzzle books to test and report back.

## What changed

- Added **debug source-set-only** `BookQaPilot.kt` with three clearly labeled **TEST BOOKS**: Woodland, Space, Garden.
- Each test book has two visible chapters and three real playable seed-fixed existing V2 word-search puzzles: 12×12 Relaxed, 15×15 Classic, 20×20 Classic. Nine puzzles total.
- These are **not approved/published book pages**. The QA-only builder constructs each board and calculates its full SHA-256 from the existing frozen V2 generator, with all source IDs containing `qa`. These test page IDs must NEVER be reused as production IDs.
- A **separate release-source** `BookQaPilot.kt` returns `BookCatalog()` with `enabled=false` so production/release builds have **zero** preview pages and no QA entry. Do not merge these QA books into the approved production catalog.
- Added a **TEST ONLY** Home entry, just below Solo/Daily/Nearby (not a crowded new bottom tab), leading to `BookLibraryFlow` → real book → chapter/page → `BookPageGameplayScreen`.
- Reuses the real word tracing, precise-select, zoom, current board fonts, three-step hints, chapter bookmarks, page completion and no-reward practice replay already implemented in WS-11A–D2B.
- The test launcher label is **Word Search TEST** and debug package is `com.playereden.gridmates.wordsearch.qa`. It installs **alongside an eventual production app**. If a prior QA install was signed by the same checked-in public test signer, this will update that QA install and preserve its existing Solo/Daily test data.
- Separate storage filenames `gridmates_wordsearch_ws11_qa_progress_v1.dat` (book-complete file) and `gridmates_wordsearch_ws11_qa_sessions_v1.dat` (partially-found words) avoid modifying original Solo schema5, Daily, Nearby, current profiles and any future real production book progress.
- QA runtime is lazily initialized on first test book access rather than slowing Home; the book timer is paused via Activity onStart/onStop foreground state.

## Owner playtest script (on Android)

1. Download the GitHub Actions QA **artifact ZIP**, extract its APK and install/upgrade **Word Search TEST**. Do not uninstall your existing working APK first; uninstalling loses that app's local test progress.
2. Open **Word Search TEST**. Confirm Solo, Daily, and Nearby still show normally. Scroll below them to **Test Puzzle Books**.
3. Tap it, verify **Woodland · TEST BOOK**, **Space · TEST BOOK**, **Garden · TEST BOOK** appear, then open Woodland.
4. Open Chapter 1 Page 1. Find one or two words by dragging (and also use **Select** for no-drag selection). Use **Fit**, zoom in/out, and **Hint**. The word count/colored highlights should update correctly.
5. Return **Back to book**, close the app or force-stop it, then reopen it and return to the same page. Found words, hint progress and bookmark should still be there. Background elapsed time should **not** count as play time.
6. Finish a page to verify **Completed** in the book list, **Continue Reading** advances to another page, and reopening that page offers **Practice again** without changing completion totals.
7. Try Chapter 1 Page 2 (15×15) and Chapter 2 Page 3 (20×20), and one page in Space or Garden. Confirm no words from another book appear as found.
8. Verify ordinary Solo still resumes, Daily and Nearby are unaffected, and rotate between portrait/landscape if comfortable. Report small-phone clipping, tiny touch regions, unreadable hints, back-navigation oddities or poor chapter flow.

## Please send back

- Whether the APK **installed/updated** and opened successfully.
- Any screen/photo where book/title/button/text is clipped or awkward.
- Whether the found words/hints stayed after a full app restart.
- Whether page completion and practice replay were correct.
- What feels confusing or unattractive about the new book navigation.

## Release gates still open

The QA-only existing V2 words have not been promoted to approved product volumes. WS-10 editorial provenance review, fixed published-board hash manifests, WS-12 owner-approved art, permanent production Home integration, actual physical phone test and real backup/restore are required before a public Bookshelf release. The build process must pass `:core:test :app:testDebugUnitTest :app:lintDebug :app:compileDebugAndroidTestKotlin :app:assembleDebug` and original packaging identity/asset checks.

**Do not publish the QA APK to Play Store or describe test fixtures as officially curated book chapters.**
