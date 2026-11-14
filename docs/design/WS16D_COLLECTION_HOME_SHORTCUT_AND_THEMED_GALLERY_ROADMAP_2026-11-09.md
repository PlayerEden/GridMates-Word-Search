# WS-16D — Collection Home shortcut and future themed unlock gallery

**Date:** November 9, 2026. **Status:** Home shortcut implemented for QA; larger Collection progression redesign proposed, NOT implemented. **PR:** #131 stays draft.

## Owner motivation and direction
After reviewing the actual WS-16C Home and Profile screenshots, the owner identified Collection as a major reason people keep playing—unlocks, artwork, theme exploration and personal expression. It should not be buried inside Profile. Proposed home arrangement places a dedicated **Collection** action alongside **Full shelf** directly beneath the illustrated book carousel, without reintroducing the bottom navigation, shrinking the covers or squeezing a third button into the top-right header.

## First implementation (WS-16D UI only)
- Two equal, small original illustrated action surfaces, Full shelf + Collection. On narrow viewports or 150%+ fonts they stack, with 56dp minimum action height.
- Home Collection opens the existing working CollectionScreen and returns Home with the current WS-16C Back behavior. Profile still links to Collection too. Existing Full Shelf route and test tag are preserved.
- A framed gallery picture is an original SVG master under design/visual-system/masters/ws16d-collection-gallery-shortcut.svg, with a matching lightweight native Canvas icon. Native text is not baked into image files.
- Collection stays available even if no editorially approved production books are installed. No placeholder reward system or fake unlocks.
- Current source touchpoints: Ws16DLibraryShortcuts.kt, Ws16HomeScreen.kt, GridMatesShell.kt and Ws16CarouselHomeAndroidTest.kt.

## Existing progress architecture verified in GitHub
- core CollectionProgress: WoodlandCollection has 12 animal portraits with stable IDs. Unlocks currently depend on Solo + Daily completion counts, from free starters up to 30 completions. WordSearchAchievements also derive from current player records.
- BoardFontCollection: font unlocks from general achievement IDs.
- CollectionScreen: Portraits, Fonts, Achievements and local player customization already work; not yet a per-book thematic gallery.
- BookCatalog and BookProgress: unique stable book/chapter/page IDs and completed-page records are separate from Solo/Daily. Production BookCatalog is currently empty. Woodland, Space and Garden illustrated QA books are test books, not an approved source of production rewards.

## Future gallery design — direction to review, not yet locked
The Collection should feel like a delightful art gallery, not just a list of achievements. Suggested categories: All, General, Woodland, Space, Garden, and future published books. Each book gets an illustrated album card matching its cover art; entering it reveals themed portraits, frames, paper/board looks, decorative trail styles, stamps or other intentional art collectibles. Present larger artwork, accurate unlocked/total counts, meaningful locked-item silhouettes with a clear earning explanation, and item preview/equip when relevant.

Two complementary reward lanes:
- **General rewards:** existing portraits and fonts, achievements, basic cosmetics and rewards from general activity (Custom, Daily and future Quick Play).
- **Book rewards:** new items earned from actual verified published book-page, chapter and volume milestones. Example: completing a chapter unlocks a signature stamp; completing a volume unlocks a premium themed frame or portrait. A book can be replayed, but repeating the same milestone must not grant duplicate progress.

**Preserve previous unlocks.** Do not secretly convert already-earned general Woodland animal portraits to book-only locks. Their current IDs, thresholds, ownership, equipped status and backups must remain valid. They may be displayed in a Woodland gallery with a General source label, with entirely new IDs assigned to any book-specific items.

Use stable, versioned collectible IDs and a definition catalog with source General or Book(bookId). Derive book awards from verified published progress, never QA-only prototype pages; an idempotent grant/migration scheme should backfill legitimate older rewards and not delete earned ones. Asset manifests, hash/approval gates and accessibility labels remain mandatory. No internet or account required.

## Phasing
1. WS-16D: Collection Home access and responsive art, tested on Android.
2. Later: gallery information architecture and original album previews, owner screenshot review.
3. Later: tested book-specific unlock rules, legacy compatibility, local persistence and backup migration.
4. Later: meaningful earned notifications and a modest real New unlocks indicator on Home when the underlying event/history tracking exists.

No new reward types, money, gacha, unlocking resets or release art promotion in WS-16D. PR remains draft pending user phone test.
