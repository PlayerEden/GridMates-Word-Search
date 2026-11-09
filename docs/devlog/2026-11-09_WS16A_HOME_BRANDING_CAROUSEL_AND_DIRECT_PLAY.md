# WS-16A — Real carousel-first Home, cross-title brand marks, and guarded one-tap book play

**Date:** 2026-11-09 · **State:** IMPLEMENTED ON DRAFT BRANCH; exact Android/art CI was running at creation. **Not** yet phone-approved, production-art-approved or merged.

## Owner direction
Continue from the approved WS-16 [top-to-bottom Home build sheet](../design/WS16_HOME_CAROUSEL_FIRST_BUILD_SPEC_2026-11-09.md). Build assets as needed, reuse actual illustrated book covers, keep the screens intuitive, tap a centered book to play with fewer intermediary screens, preserve Full Shelf for chapters and page management, and combine functional changes into one meaningful test APK rather than numerous one-icon builds.

## Actual new assets and UI
- **Shared series family branding:** editable original `design/visual-system/masters/gridmates-family-mark-ws16-v1.svg` (two interlocking paper tiles). Designed independently of game-specific text so it can later be adopted deliberately by Sudoku/Crossword. The actual Android header draws a matching **native Compose Canvas vector** with GridMates title as native text.
- **Separate game identity:** editable `design/visual-system/masters/wordsearch-title-mark-ws16-v1.svg` (mini letter grid with answer trace), with an independently rendered native vector and **Word Search** title. These are individual real source-backed mark assets, not generated fake application screenshots. SVGs are references/design masters; the runtime rendering is the documented Compose equivalent and **does not read SVG files at runtime**.
- **Functional header:** `Ws16HomeHeader.kt`: native paired-brand stack and real 48dp **Profile** and **Menu** buttons with accessible labels. Profile routes to existing local Collection/identity, portrait and achievement screen; menu opens a native modal to existing Settings/backup, How to Play, Records, My Puzzles, Full Shelf (only where playable books exist) and New Solo. No dead leaderboard/global-account control or forced sign-in.
- **New actual Home:** `Ws16HomeScreen.kt` replaces the stacked WS-15 launcher cards in `GridMatesShell.kt`. It features illustrated books, native index and progress label, real Full Shelf action, compact Continue Solo/New Solo, Daily, Together and My Puzzles, while retaining existing bottom tabs and scroll behavior. On small/large-font layouts action row can stack. No hard-coded progress baked into art.
- **No production book leaks:** when QA books are disabled/blocked/empty, the app shows truthful "books are being prepared" copy and still provides Solo/Daily/Together; no empty Full Shelf menu item.

## Actual shared book carousel
- `Ws16BookCoverPager.kt`: a single composable shared between **Home and existing full-library horizontal browser**, using the exact existing `BookArt` original illustrated covers, native `HorizontalPager`, side peeks, lift animation and wooden shelf edge. We did not create a second, divergent UI-only carousel. The full library retains preview A/B, swiping, headings and detailed book entry.
- On Home **tap current focused cover** → validate book against real catalog and enter book progress flow; **tap neighboring book** → focus/animate only. On Full Shelf, focused cover selects book detail, side peek focuses. Preserve book IDs and selected focus while navigating back.
- `BookLibraryFlow.kt` accepts an **optional validated `initialBookId`** and chooses a page via the *existing* `BookProgressRules.continuePage` / original local progress. If all pages are complete, it opens **book details** for explicit replay/page choice without resetting completion. Back from direct-entered page or detail returns Home; ordinary Full Shelf route continues shelf → detail → page. Blocked/corrupt saves remain protected by existing no-write guard.
- `GridMatesShell.kt` uses in-memory/saveable Home selected book and direct-entry intent. QA book runtime remains debug-only and keeps existing `.qa` package and local save files separate from Solo.

## Actual navigation regression coverage
- `Ws16CarouselHomeAndroidTest.kt` source-compiles checks for header branding/menu/profile, Full Shelf action, and one-tap focused Woodland page with Back returning Home.
- Existing real-activity/small-display tests updated for new Full Shelf button and compact Solo/Daily/Together actions: `QaBookshelfLauncherAndroidTest.kt`, `ShellSmokeTest.kt`, `CompactHomeNearbyEntryAndroidTest.kt`. Existing classic-layout QA tests stay in place.
- **CI success is not actual Android instrumentation execution**. Only owner on-phone tests can confirm layout, tap feel and save resume under real conditions.

## Exact code commit and build gate
- Last substantive code commit: `5a5949a40bbff87a99874eeec388520869304793` (release-safe Full Shelf menu visibility). Earlier source commits include `7327b19` (brand assets/header), `c5a2be0` (shared pager), `3554ce4` (library refactor), `cd8590e` (guarded direct route), `48877dd` (real Home layout), `375e375` (Shell wiring), `4a95ee1` (Android smoke test updates).
- Exact Android source run: [#42703745184](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42703745184), and art/release isolation [#42703744656](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42703744656), both **in progress at this entry**. Superseded intermediate runs cancelled by PR concurrency are not evidence of failing gameplay.
- Previous fully green fallback: WS-15 Pass 2 [APK ZIP #13707642014](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42692044545/artifacts/13707642014); existing app saves should be backed up before any uninstall/signature change.
- **Do not give user an invented new APK link** or claim release readiness until BOTH workflow conclusions, real artifact, checksum, package identity and QA-only release isolation are verified.

## Next owner phone validation
1. Check GridMates/Word Search stacked identity at normal and enlarged font, Profile/Menu navigation and back.
2. Swipe Woodland, Space and Garden on **Home**. Side-peek tap changes focus, focused cover tap goes **directly into playable page**; Back returns Home.
3. Full Shelf remains available for browsing chapters, bookmarks and completed pages. No forced `Open book` intermediary on the direct route.
4. Confirm Solo resume/New Solo, Daily, Nearby Together, My Puzzles, Records, Collection and Settings paths.
5. Confirm 12/15/20 book pages, hint overlays/history and zoom dialog still behave correctly, local saves survive reopen, and nobody loses progress during QA installation.
6. Only after screenshots evaluate logo aesthetics, book size and action spacing as one combined pass. Keep PR #131 draft/unmerged until acceptance.

## Follow-up refinement before the first owner build
- **360dp responsiveness:** `Ws16HomeScreen.kt` uses the available content width rather than the physical device width; the action-stack breakpoint was corrected from 328dp to **300dp** content width. This keeps Daily/Together side-by-side on a normal 360dp phone while stacking on a 320dp display.
- **Full Shelf's distinct purpose:** `BookLibraryFlow.kt` now defaults `horizontalLibrary=false` on the full-shelf entry, so Home owns the swipeable cover showpiece and the Full Shelf opens the **actual standing-book bookcase** with chapters/pages/progress. The existing overflow retains "Try swipe library" for QA A/B comparison, with no content/save-format changes.
- **Regression test changes:** `QaBookshelfLauncherAndroidTest.kt` now asserts the Full Shelf's real bookcase; `Ws16CarouselHomeAndroidTest.kt` asserts the secondary swipe option remains available. The real full-shelf route remains `Home → Full Shelf → Book Details → Page`, while **Home cover tap** is the one-step direct play path.
- Last substantive source commit is `1a6b64254606aa399b227f8dc415503c6da5b95b`. Exact Android [#42704245235](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42704245235) and art/release [#42704244937](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42704244937) were pending at first observation. Earlier branch runs were cancelled by concurrency as the source was adjusted; check only these exact latest-source final results.
