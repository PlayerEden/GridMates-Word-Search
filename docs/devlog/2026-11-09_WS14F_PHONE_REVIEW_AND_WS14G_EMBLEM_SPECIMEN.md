# WS-14F owner screen review → WS-14G existing-emblem QA specimen

**Date:** 2026-11-09 · **Status:** five actual Android screenshots reviewed, code specimen committed; new source CI and physical review **pending at this log**. No new sprite files were generated.

## Who, what, where, and why

- **Owner evidence:** Five actual-phone screenshots of Word Search: illustrated Puzzle Books with **Woodland** and **Space** selected; cleaned-up **Space** book detail; **Space Page 1 (12×12)** and **Woodland Page 3 (20×20)**. The images were sent after the WS-14F APK test request. No explicit word-tracing or zoom-test outcomes were written, so these screenshots are *visual*, not gameplay pass claims.
- **Assistant decision:** Treat the horizontal book-browser and chapter/detail layout as a **promising baseline to retain**, not a completely locked/released layout. Start carrying the selected book's visual identity into the actual puzzle via **existing theme emblems**, not a premature large set of decorative images.
- **Where:** Draft [PR #131](https://github.com/PlayerEden/GridMates-Word-Search/pull/131), `BookPageGameplayScreen.kt`, new `Ws14GPageEmblemLayoutTest.kt`; [WS-14G layout and three-theme accent plan](../WS14G_LAYOUT_LOCK_AND_IN_GAME_THEME_ACCENT_KIT_2026-11-09.md).
- **Why:** Book covers are rich and book detail is cleaner, but the puzzle page is still almost entirely unthemed. The game must feel like a cohesive book series without visual effects obscuring the letter grid.

## Previous baseline / evidence

- Latest verified pre-change branch SHA: `12977f393cdbba42f4eafe96ae8501aef880a903`, [Android QA #42652363486](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42652363486) **success** and [Art debug/release isolation #42652363702](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42652363702) **success**. This is CI of the WS-14F build plus WS-14G planning docs, **not** the header emblem code.
- Owner screen details:
  - Library: **1/3 Woodland** and **2/3 Space**, centered cover and side-peek, arrows, and one `Open book` action are visible. Screenshots alone do not prove the swipe gesture route or persisted selection.
  - Space detail: native `Reading progress`, chapter subtitle `CHAPTER 1 · 0/2 PAGES`, `Getting Started` and **single-line Play/Bookmark** labels are clear. The earlier repeated `TEST BOOK` wording is absent on this screenshot.
  - Space Page 1: 12×12 grid and 12-word list rendered, with `Fit`, +/- and `Select`. 0/12 words found; no claim of completed tracing.
  - Woodland Page 3: 20×20 grid and 24-word list rendered, dense zoom helper visible; helper wraps to two lines, and letter cells are quite small at fit-to-width size. 0/24 words found; actual tracing and zoom ergonomics remain **OPEN**.
- The game header already receives `book.theme` and `BookArtKind.EMBLEM` (existing 3-theme, locally bundled WS-12 family) exists; no need to introduce new theme persistence, image downloads or gameplay data.

## Changes this pass

**Feature commit:** `dd2fb8a2b72236a0b9a731b560c0d38d9043e1d7`.

1. On QA artwork-enabled, active (not completed or bookmarked) book pages, display a **32dp native-composed theme emblem** next to `Theme · Page N`. The symbol is chosen from the actual opened book's `WordTheme` through existing `BookArt.kt`; no new art is generated.
2. Hide the decorative emblem when font scale is >=1.5, the content width is under 320dp, QA art isn't enabled, or an existing completion/bookmark image should take priority. The required native page title and game controls win; status artwork and native labels remain.
3. Added pure `shouldShowBookPageHeaderEmblem` guard and `Ws14GPageEmblemLayoutTest.kt` covering normal, narrow, enlarged-text, completed, bookmarked and no-QA cases. No image replaces letters, touches, word lists, progress, page IDs or any saved data.
4. No other gameplay logic modified. No new generated art, no production asset import, no change to book/portrait collections or Series art identity.

## QA and gates

- **At log creation:** source commit `dd2fb8a` is committed; exact-source Android/art CI statuses must be inspected **live**. No on-phone screenshot of the **new emblem** exists yet.
- **Visual sign-off still open:** emblem usefulness at 320–430dp, compact title and 20×20 page; whether WS-12 emblem style matches the lush WS-14 covers; effect on scroll/found-word visibility. This is a QA specimen, **not** final in-game art.
- **Functional sign-off still open:** actual finger word tracing/claim, +/- zoom, Fit and pinch/pan on 20×20; bookmark persistence and safe install-over; reduced motion/accessibility.
- **Release sign-off not implied:** approved original cover and profile ID/source unchanged, debug/release asset isolation still required; draft PR remains draft.

## Next exact action

1. Confirm source-matched Android QA, art-isolation and APK artifact for the new emblem code; fix actual compiler/test errors if found.
2. Send that **GitHub Actions artifact** (ZIP containing `app-debug.apk`), not an invented chat attachment. Owner checks header emblem on Woodland, Space and Garden gameplay pages and reports how it feels at actual size. Verify word tracing on the dense page.
3. If the emblem placement works, create a **cohesive small three-theme V1 accent family** from independently archived transparent masters + hashes using the existing GitHub asset pipeline, integrate in QA only, and compare against existing emblems. No bulk art collection before layout sign-off.
4. Record the verdict and build receipts in a new dated log. Keep prior known-good swipe QA APK and v56 fallback.

**New-chat restart:** Read `AGENTS.md`, `docs/DEVELOPMENT_JOURNAL.md`, this pass, `docs/WS14G_LAYOUT_LOCK_AND_IN_GAME_THEME_ACCENT_KIT_2026-11-09.md` and live draft PR #131. Verify new source workflow, then phone-test the native book page emblems and dense-board word claims.
