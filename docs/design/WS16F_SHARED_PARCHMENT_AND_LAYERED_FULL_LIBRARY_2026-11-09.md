# WS-16F — GridMates parchment, layered bookshelf and Full Library

**Date:** November 9, 2026 · **Branch:** `feature/ws14b-style-anchor-and-art-direction-2026-11-08` · **Draft PR:** #131.  
**Owner direction:** keep the richly illustrated reference as the target, but build it from **independent layered assets**; shared, simple yet cozy parchment across GridMates; move books into contact with the walnut shelf to reclaim unnecessary spacing; more carved bookshelf art; rename **Full Shelf → Full Library**, with a genuinely multi-shelf bookcase destination.

**Status at implementation:** source committed (app/art SHA `83a06cbf22adcfef5aa20bde87b01033ec04ba0f`), exact-source Android and release-art checks underway. This is not a merged production release, not automatically added to Sudoku/Crossword, and not yet accepted on the owner's phone.

## Design contracts and ownership

| Asset/component | Scope | Behavior |
| --- | --- | --- |
| GM shared parchment tile | `GM_SHARED` | A tiny, low-contrast reusable source tile; native drawing uses mathematically repeated faint flecks on a quiet cream base, not a full-screen UI screenshot |
| GM edge-warmth wash | `GM_SHARED` | Extremely subtle radial warmth; rendered responsively to any viewport without clipped edges, stretched objects or bitmap seams |
| WS-16F walnut apron v2 | `WS_THEME` | A separate organically carved shelf-front/underside and antique-gold ornament, alongside existing independent top/ends/shadow |
| WS bookcase backing + vertical uprights | `WS_THEME` | One repeatable back panel per actual populated book row, layered under books; no fabricated future books |
| Button, profile, book art and labels | Separate native components | All current click/focus/Back, book ID, word counts, links and accessibility text stay native, never baked into the background |

Editable SVG sources and **true source SHA-256 hashes**: [WS-16F asset register](../../design/visual-system/modular/ws16f-source-asset-register.json). The SVGs are independent editable masters; the Android renderer draws analogous native Compose art and is **not** automatically rasterizing/importing those SVGs. Owner approval of individual illustrations is still required before calling them final.

## Implemented layering

### Shared background — no edge gaps on tablets

`GridMatesParchmentBackground.kt` draws a continuous warm paper base, very faint gradient/vignette and deterministic, nearly invisible paper flecks. `GridMatesShell.kt` hosts it underneath a transparent Scaffold; ordinary screens retain their functional content and their own panel treatments. This first Android implementation is **local to Word Search** but intentionally extractable into other GridMates apps later. No account, internet dependency or 4K wallpaper.

The simple background remains legible with all optional edge decorations absent. Botanical corners, lanterns, mushrooms and lower flora remain **optional future transparent overlays**, not baked into a permanent background.

### Home carousel — real physical book contact

`Ws16BookCoverPager.kt` previously placed a 234dp-tall pager **above** a separate 34dp-tall wooden shelf, for roughly 268dp of layout. WS-16F composites the shelf **behind** a 226dp pager inside a 252dp overall Box. The enlarged carved shelf is about 42dp tall and overlaps the lowermost part of the books' drawn region rather than adding a separate gap. The illustrated cover itself remains **218dp** high; neighbors still peek, focused-cover tap still plays, neighbor-cover tap still focuses.

**Expected layout effect:** approximately 16dp recovered in this composition, plus a physical contact/shadow impression, without diminishing cover scale. The exact visual edge must be judged in owner screenshots because the original book PNGs have intrinsic transparent pixels. Tuning the overlap is low risk and should be done before shrinking covers. Shelf has no input handlers, so it cannot be an invisible touch blocker.

`Ws16EStorybookShelf.kt` retains the original tagged shelf with its separate top/caps/woodgrain and central golden leaf and now adds a softly scalloped walnut apron behind those layers. It stays responsive to width without scaling an ornate full-width bitmap.

### Full Library — real bookcase rows, not one shelf

`Ws16DLibraryShortcuts.kt` now displays **Full Library** beside Collection; accessibility says `Open Full Library puzzle books`. `Ws16HomeScreen.kt` hamburger menu uses the same label. Existing stable navigation test tag `ws16-full-shelf` intentionally stays unchanged for tooling and compatibility.

The destination heading is **Your Library**. `BookcaseBookshelfScreen.kt` was already a *real lazy, multi-shelf catalog* with two books per row on ordinary phones, one per shelf at large fonts, and three per row on wider layouts. WS-16F reuses that behavior and improves its **visual identity**: each actual row receives a softly painted wooden backboard, narrow left/right uprights, and the same carved shelf used on Home, layered beneath the individual clickable covers/spines. No fake locked books, chapter/page behavior change or save migration. Preview swipe comparison remains available in QA.

Book details and fallback library headings use **Your Library**; Back labels to the library are updated, while the direct Home→book and library→book flows keep their existing actual destinations.

## Mobile/tablet and performance acceptance plan

- **320 / 360 / 393 / 411dp phones:** top Home, first action row and bottom utility links. No text clipping, no double horizontal scrollbar, no interfering decorative overlay.
- **Landscape and 700–1200dp tablets:** background completely fills the content surface; radial wash/flecks stay subtle and never look like stretched wallpaper. Library rows automatically choose 2–3 columns as before.
- **Font sizes 1.0 / 1.5 / 2.0:** buttons expand/stack when needed; back and menu stay touchable. Don't demand zero scroll at 200% text.
- **Book visual QA:** Woodland/Space/Garden should actually touch wood, no clipping of title plaques or cover bottoms, neighbor peeks remain visible, shelf medallion unobstructed.
- **Routes and data:** Home focused cover opens real puzzle and Back returns; Full Library shows 3 real QA books across 2 rows; tap cover, tap Open Book, chapter/page, Home, Gallery, Daily, Together, Custom, My Puzzles remain working. No fake Quick Play generator (coming soon QA-only).
- **Build gates:** core/app unit tests, lint, instrumentation test source compilation, debug Android APK; separate debug/release asset-isolation checks for QA-only books/portraits. Only a final exact-source dual-green result and artifact checksum justifies sharing a new QA APK.
- **Owner review:** send whole Home, scrolled Home, and multi-shelf Your Library screenshots; approve/refine paper warmth, shelf overlap, ornament amount and bookcase backing before further flourishes. Keep PR #131 draft; rollback to [verified Pass 3 artifact #13726026545](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42733181348/artifacts/13726026545).

## Planned next phases after handset acceptance (not yet implemented)

1. **Polish actual illustrated shelf** if too flat or if books still float; better book-contact shadow and individually authored carved endcaps. Keep the pager/IDs untouched.
2. **Optional decorative edge library:** simple transparent corner foliage, small gold header divider, bottom flora and optional removable lantern. Each independent; use sparingly and disable on small screens as needed.
3. **More coherent Full Library:** add tasteful bookcase top/side continuity and richer spines when there are more published volumes. Do not pre-fill shelves with pretend books.
4. **General GridMates reuse:** port only the family-level parchment, scalable frame, CTA pill, typography/icon plate templates into Sudoku/Crossword after title-specific integration and tests. No uncontrolled cross-repo dependencies.
5. **Separate gameplay roadmaps:** true offline mixed-word Quick Play (WS-17), book-specific unlock catalog and enhanced Collection album UI, production publication approval for actual book content. These are not secretly enabled by this visual pass.

**Product principle:** good puzzles, good company. Cozy, understated home/library material design without needing to paint an entire room on every screen.
