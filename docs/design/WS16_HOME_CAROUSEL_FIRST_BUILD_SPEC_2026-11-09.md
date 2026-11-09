# WS-16 — GridMates Word Search: Carousel-First Home Build Sheet

**Date:** November 9, 2026  
**Decision:** User-approved direction; **specification only, not an implemented screen**.  
**Objective:** one visually cohesive Android test milestone, not an APK per tiny change. Make books visible immediately, cut unnecessary taps, and preserve solo, daily, same-Wi-Fi co-op, progress, profile and collections.
**Visual principle:** illustrated puzzle book charm plus simple, modern navigation. The selected book is the primary visual focus.

## 1. Portrait layout wireframe (conceptual, not finished art)

    ┌───────────────────────────────────────┐
    │ GRIDMATES                [Profile] [≡] │
    │ WORD SEARCH                           │
    │                                       │
    │           Your puzzle books           │
    │       ◀   [WOODLAND COVER]    ▶        │
    │             ━ wood shelf ━           │
    │          Woodland · 1 of 3            │
    │             [Full shelf]              │
    │                                       │
    │    [Continue · Solo 15×15]            │
    │    [ Daily Puzzle ][ Play Together ]  │
    │                                       │
    │ [Play] [Records] [Collection] [Setup] │
    └───────────────────────────────────────┘

The wireframe establishes hierarchy only. Use actual existing covers, a restrained shelf edge, small themed utility assets, native text and responsive Compose layout. No static full-screen composition should replace interactive controls.

## 2. Top-to-bottom sections and exact behavior

### A. Brand + player header (first component to build)
- **Top left:** new editable shared GRIDMATES brand mark, plus a separate Word Search wordmark stacked below. The GridMates mark must not contain Word Search-specific art, so the same approved family mark can later serve Sudoku, Crossword, and other titles. Provide compact and full brand variants. Use native styled text when a logo fails to load.
- **Top right:** two actual 48dp actions: **Profile** and **Menu**. Leave a third leaderboard/stats icon out of v1. At 320dp or large font scale, compact the brand before shrinking either touch target.
- **Profile button:** open existing local identity, equipped portrait/icon, achievements, stats and collection pathways. Initially this can route into the already working Collection/Records pages, with profile details prominent. No new online account or required sign-in.
- **Menu button:** open a functioning sheet/dialog containing Settings & backup, How to Play, Records, My Puzzles and Full Shelf, plus developer artwork only in QA builds. Do not create decorative controls without actions. Android Back closes the sheet.
- **Native semantic text:** "Open player profile" and "Open menu". Dynamic information never lives inside raster wordmarks.

### B. Featured book carousel: the primary Home action
- Title: **Your puzzle books**. Reuse the existing illustrated Woodland, Space and Garden covers with the native swipeable HorizontalPager, neighbor-book peeks, selection animation and wooden shelf edge. The full illustrated cover stays the dominant focal point.
- **Tap centered cover = start or resume that selected book directly**, using the existing catalog IDs, safe local progress store, and BookProgressRules.continuePage resolution. No separate Open book or Start solving confirmation step on this direct-play path.
- **Tap side-peek cover = focus the neighboring cover** (animate pager), not accidental play. Swipe horizontally = change focused book. Preserve focused book when returning from puzzle.
- Display **one compact progress indicator**, e.g. "Woodland · 1/3 pages complete" and current position "1 of 3 books". Avoid repeating the same counters across several extra cards.
- Newly started book: open first eligible page. In-progress: continue existing book's next eligible page; do not invent last-played metadata. Fully complete: don't silently reset; present a transparent Replay / Choose page option or route to book details for replay selection. Missing/corrupt/blocked data: show a truthful non-destructive error while preserving save files.
- Book cover titles remain part of the real art; selected-book name and tappable semantics should still be native for accessibility. Minimum 48dp touch regions.
- Current code caution: HorizontalBookBrowserScreen already handles book art and focus, but focused-cover clicks are *not yet wired to direct gameplay*. BookLibraryFlow currently starts at shelf → detail → page. A new validated start-book/start-page route is required; this plan does not claim that is already built.
- Honor the QA content gate: prototype book pages currently live behind QaBookshelfRuntime; do not surface unfinished test books as approved release content.

### C. Full Shelf: worthwhile secondary destination
- Small **Full shelf** illustrated book-stack icon/link near carousel, not a giant fourth Home card.
- Unlike the Home carousel, Full Shelf should offer **all books** and eventually an illustrated tall bookcase using actual matching spine assets, with chapter/page selection, bookmark and progress management.
- Keep the existing BookLibraryFlow and PuzzleBookDetailScreen pathways for users choosing a specific page. Preserve progress and existing chapter logic. No artificial standalone spine pasted beside a cover that already includes a book binding.

### D. Compact quick actions
- **Continue:** a small bookmark/ribbon-style card with live native sublabel. The current Solo save and book resume are *different stores*: do not mix them. For first pass choose an honest "Continue Solo · Woodland 15×15" action for existing Solo progress, and use the centered book itself to resume book pages. An optional selected-book "Resume Page 2" badge can be added if sourced from book progress. Do not guess which type of save is most recent.
- **Daily:** calendar illustration, native "Daily" label. Open existing Daily landing.
- **Together:** paired people/link illustration, native "Together" label. Open existing same-Wi-Fi Nearby co-op, not remote matchmaking.
- Keep **New Solo** and **My Puzzles** discoverable via a compact secondary link or Menu; don't accidentally remove Free Play when the bulky Solo card disappears.
- Two small asset buttons side-by-side for Daily and Together on common widths; if not readable on 320dp or at 200% font size, stack vertically. Artwork is decorative; all action labels and sublabels are native and accessibility-readable.

### E. Bottom navigation and scrolling
- **Keep current Play/Records/Collection/Settings tabs for the first WS-16 QA build**. Profile/Menu may eventually replace some, but their discoverability must be validated before removing known navigation.
- Scroll Home content naturally on short screens, landscape, and larger fonts. No tap targets obscured behind Android navigation or bottom tabs. Keep the real cover fully visible when practical before optional flourishes.
- The home should feel like a cozy illustrated front page, not a heavy 3D bookshelf, ad dashboard, or text-heavy catalog.

## 3. Asset packages to make while implementing

| Pack | Exact original source family | Use | Acceptance |
| --- | --- | --- | --- |
| WS16-A Shared GridMates | Family mark + full/compact wordmark SVG/vector masters | Every future GridMates game's header | Brand-only, reusable; legible at 24–48dp; transparent export / fallback |
| WS16-B Word Search | Word Search wordmark source, subtle page flourish | This title's stacked header | Never merged irreversibly with family logo; fits 320dp |
| WS16-C Header controls | Profile/avatar frame, hamburger icon and selected-state treatment | Real profile/menu 48dp native buttons | Named destinations, TalkBack, no fake click behavior |
| WS16-D Book shelf | Existing Woodland/Space/Garden covers, verified corresponding spines, restrained shelf edging | Home carousel / Full Shelf | Existing art identity; no mismatched bindings or duplicated image assets |
| WS16-E Compact actions | Continue bookmark, Daily calendar, Together linked people, Full Shelf symbol | Short illustrated Home shortcuts | Small-size readability; native dynamic text |
| WS16-F Decoration | Paper corners, tiny dividers, noninteractive leaf/star/floral accents | Secondary polish only | Never reduces essential play space |

Create **individual editable asset masters**, stable IDs, reproducible output paths, source/export SHA-256 manifest, alpha-safe PNGs if needed, and native fallback for each. Do not bake labels or progress values into pictures. Brand source should be independently portable to Sudoku/Crossword, but do not automatically alter their repos.

## 4. Implementation sequence: one substantial owner test build

1. **WS-16A Header and identity:** Create actual shared logo + separate Word Search title, compact Profile/Menu icons with real routes and 48dp targets. Test on 320/360/411dp and enlarged font sizes.
2. **WS-16B Reusable book carousel:** Extract shareable pager/content from HorizontalBookBrowserScreen, preserve existing cover art, focus behavior and bookshelf layout. Embed inside Home rather than copy/paste an independent book browser.
3. **WS-16C Direct-play contract:** Add optional validated initial-book/initial-page intent to BookLibraryFlow or a neighboring route holder. Use BookProgressRules.continuePage, existing save guards, Android Back and return-focus; no save format migration.
4. **WS-16D Quick actions:** Replace giant duplicated Home book/solo cards only once the selected-cover play, Solo continue, New Solo, My Puzzles, Daily, Nearby, and Full Shelf actions all remain reachable. Add compact original illustrations.
5. **WS-16E Visual polish and QA:** Include one coherent set of brand/action assets, paper/shelf details only if room permits. Regression-test 12/15/20 book boards, hints/notice history, resume/save, Solo, Daily, LAN route, Settings, Profile/Collection, landscape, large fonts and TalkBack.
6. **One source-matched WS-16 Android QA APK:** Full Android + artwork/release isolation workflows; actual artifact link, checksum, changelog, rollback. Keep PR draft until owner screenshot review.

## 5. Owner acceptance / QA phone checklist

- App opens to compact **GridMates / Word Search** + recognizable Profile and Menu actions.
- Woodland cover is visible without navigating to Library; neighboring Space/Garden books peek in. Swipe switches books, side tap focuses, centered tap starts/resumes **without Open Book intermediary**.
- Full Shelf lets user manage specific volumes and pages separately.
- Continue Solo, New Solo, My Puzzles, Daily and same-Wi-Fi Together are still accessible and functional.
- Larger text does not truncate the logo into the action buttons; all controls remain >=48dp; Home scrolls without clipped content.
- Player name, profile, achievements, records, backup and Settings work locally and do not require external accounts.
- No direct-play path bypasses blocked save safeguards, loses progress, silently resets a finished volume, or skips the book content QA gate.
- Owner can submit screenshot feedback from one *meaningful combined* test, not a string of one-icon APK patches.

## 6. Excluded for this first milestone
No remote multiplayer, live global leaderboard, forced cloud accounts, monetization, new unrelated game modes, cross-app automatic sync, wholesale bottom-nav removal, or approval of unreviewed production art.

## 7. Verified current-code anchors (to keep this build practical)
- **GridMatesShell.kt:** Home currently hosts stacked cards; bottom tabs and all existing app routes live here.
- **HorizontalBookBrowserScreen.kt:** native HorizontalPager, illustrated covers, side peeks, focus state, shelf edge; center cover currently doesn't direct-play.
- **BookLibraryFlow.kt:** currently owns shelf → detail → page state and QA save guards; must add validated direct-entry intent.
- **PuzzleBookshelfScreen.kt:** already calls BookProgressRules.continuePage and displays book chapters/pages; reuse instead of duplicating selection.
- **CollectionScreen.kt / SoloRecordsScreen.kt:** existing local identity/portraits/achievements and stats; first Profile icon should route to working content.
- **Ws15VisualKit.kt:** existing original vector mode assets and accessible utility patterns to keep consistent.

**Build status:** this document is the implementation contract; no WS-16 UI APK or new finished logo assets are claimed by this documentation commit.
