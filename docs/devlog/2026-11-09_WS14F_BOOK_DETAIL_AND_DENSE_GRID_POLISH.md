# 2026-11-09 — WS-14F: Book-detail Polish and Dense-Grid Gameplay QA

**Status at authoring:** code/tests committed; source-matched Android and artwork workflows **pending**. Do not claim device QA or release sign-off. Owner approved proceeding after positive WS-14D swipe-library review.

## Who, what, where, why

- **Who:** Owner asked to continue after the WS-14D swipe-library phone test; assistant implemented the focused UI improvements and tests. GitHub Actions independently compiles/validates. The owner has **not yet seen these WS-14F changes on phone**.
- **What:** Tighten the Play → Test Puzzle Books → Book Details experience, and make the existing 20×20 Word Search zoom UI more discoverable, before creating more art.
- **Where:** `PuzzleBookshelfScreen.kt`, `WordSearchScreen.kt`, existing QA instrumentation tests, new instrumentation and JVM zoom-guidance tests; [draft PR #131](https://github.com/PlayerEden/GridMates-Word-Search/pull/131), branch `feature/ws14b-style-anchor-and-art-direction-2026-11-08`.
- **Why:** The owner's real-device Space book screenshots showed repeated TEST/chapter labels, a large amount of vertical reading space, and `Play page 1`/`Bookmark page 1` wrapping to two lines. The 20×20 grid looked dense, though the photos alone do **not** prove touch tracing is faulty.

## Where we came from

- Prior pass: [Owner positive physical WS-14D library review](2026-11-09_WS14D_OWNER_PHONE_VISUAL_REVIEW.md).
- Started from GitHub source `3df9ecd9802ce352f10514408153e008db0c0b00`, with **both** the [Android QA run #42646324240](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42646324240) and [art-release isolation #42646324645](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42646324645) **success** on that preceding source. New pass is stacked in existing draft PR; no merge to main.
- Keep existing swipe library, three illustration families, the 12 original Woodland portraits, and previous physical QA rollback; never clear/upgrade save schema or change multi-device logic for visual polish.

## Exact changes and design trade-offs

1. In QA-only book details, remove duplicate `TEST BOOK` badge from the illustrated hero while retaining honest QA status in the library/launcher. Replace redundant `Outer Space · Volume 1` progress-header text with `Reading progress`. The book catalog itself is unchanged.
2. Condense QA chapter headings to a single chapter/number-of-pages eyebrow plus a smaller native chapter title. Strip only the **display** suffix ` · TEST` from QA chapter titles, preserving exact underlying book/chapter IDs and immutable content; normal non-QA book display retains its existing text.
3. Keep each page number and state in its native card heading; for QA page actions show concise **Play / Bookmark / Saved** labels rather than repeating `page N` on narrow buttons. Accessible button content descriptions retain **Play page N / Bookmark page N / Bookmark saved for page N**. At narrow widths or large system text the actions still stack; the long labels are unchanged in standard/non-QA mode.
4. For 20×20 (and denser) grids, the existing board toolbar now advises `Dense board · use + or pinch`, and the +/- buttons zoom in **0.5×** steps instead of **0.25×** on 12×12 and 15×15. The initial view stays fit-to-screen; `Fit`, two-finger pan, tap selection, coordinate transforms, hint policy, puzzle IDs and word claims remain untouched. Word tracing is still a **separate phone QA gate**, not declared resolved.
5. Existing QA instrumentation expectations updated; added `Ws14FBookDetailPolishAndroidTest.kt` (native page targeting, 360dp and 320dp/200% font) and `Ws14FGridZoomGuidanceTest.kt` (12/15 vs 20/25 grid controls). Instrumentation code compiled in CI is **not** equivalent to execution on a device.

**Feature commits:** `f8c66ebec95b4bc56f473a5022506ed9796eff98` for source UI/zoom, and `201695250cf9cb1578d5e318e73ebd41e8f78469` for accompanying tests and legacy QA assertion updates.

## Build/QA receipt — pending at journal creation

- Android standard workflow: [run #42650533366](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42650533366), source `201695250cf9cb1578d5e318e73ebd41e8f78469`, **pending**. Previous UI-only source run #42650367927 started on f8c66 but is NOT the final source receipt.
- Artwork/debug-release isolation: [run #42650533373](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42650533373), source `201695250cf9cb1578d5e318e73ebd41e8f78469`, **pending**. Previous UI-only source run #42650367925 is not final.
- No new WS-14F APK or artifact is claimed until an exact-source run **passes** and its six WS-14 book sprites are verified in the debug APK; art workflow must continue verifying zero unapproved WS-14 artwork in release.
- On-phone test not yet performed on WS-14F. Keep previous owner-inspected WS-14D APK and earlier v56 broad fallback. Never suggest uninstalling/clearing local progress to remedy a failed download.

## Next specific action

1. Inspect actual latest run statuses; fix any source compiler/test failures, distinguishing them from external Gradle/Kotlin HTTP 504. Do not burn Actions minutes on repeated identical infrastructure retries.
2. Once a matching build passes, share **GitHub artifact ZIP → `app-debug.apk`**, not an unreliable chat sandbox link. Record APK size, hash, identity, workflow conclusions in a follow-up **build receipt** journal entry.
3. Owner review: open Space (and Garden) book details; verify compact chapter headings and readable Play/Bookmark controls; exercise 12×12 and **20×20** grids with actual **swipes, Fit, +, pinch, and precise Select**. Confirm found-word claims, save/reopen, bookmarks, and no loss of earlier progress. Share screenshots and any finger-input problems before further gameplay changes.
4. Only then plan modest Play/Collection art cohesion, following the shared GridMates asset/motion guide. No unnecessary artwork generation in this pass.

**New-chat restart:** Read `AGENTS.md`, `docs/DEVELOPMENT_JOURNAL.md`, this entry and `docs/WS14_ACTIVE_HANDOFF_2026-11-09.md`, check live PR #131 SHA/CI, then finish source-matched Android build and owner 20×20 playtest.
