# November 9, 2026 — WS-15 Pass 1: Real menu cohesion, native icon family and book-detail polish

> **STATUS: CODE AND REUSABLE VECTOR SOURCE COMMITTED ON DRAFT PR #131.** Both exact-source Android/art GitHub Actions are running; no new WS-15 APK is called green until their final results and artifact checks are independently verified.

## Why this pass

The owner tested the WS-14K2 consolidated Android QA build and shared real screenshots of the Space illustrated swipe library, detail page, and the Play home screen. They want the **whole app** to feel like one approachable, coherent illustrated paperback puzzle game: all menus, cards, navigation, consistent graphics and purposeful assets. They expressly approved moving into our proposed **WS-15 UI cohesion plan**. Continue delivering larger meaningful batches rather than one-icon APK updates.

## Evidence from real screenshots, without publishing device screenshots to GitHub

- Space and Woodland illustrated covers are visually coherent and should remain the central artwork.
- Swipeable library works visually but old top-heavy `Illustrated cover preview · release review pending`, `WS-14 covers`, `Field Notes supporting art`, `Original art (A)` and `Classic shelves` controls compete with the book illustrations.
- Book detail has a strong cover/title hero, but bulky `Back to Puzzle Books` and duplicated headings make the page look less cohesive.
- Play home has long numbered section labels, a prominent `Puzzle library · QA only` section, and a separate `View Library Artwork` developer entry that makes it feel like a testing workspace.
- Records/Settings have different heading structures. The current Collection screen already uses `StorybookHeading`.
- The word-grid, successful word tracing, progressive hints, transient notifications, and debug theme motifs were established during WS-14K2. **This pass does not rewrite them.**

## Actual source changes

### Native icon family / shared navigation asset kit (new file)
- `app/src/main/java/com/playereden/gridmates/wordsearch/Ws15VisualKit.kt`: original, **editable Kotlin Canvas vector artwork** for **Solo** (word grid), **Daily** (calendar), **Together** (two players), and **Books** (open paperback). No flattened raster labels. Tiny icons are decorative only and screen-reader hidden; source-backed paths render at any density.
- Reusable `Ws15BackHeader`: 48dp accessible Back arrow, native short title, optional subtitle and 48dp three-dot preview options, all in one compact row. This replaces verbose navigation on the *real swipe library*, *classic shelf alternative*, and *book details*.
- `Ws15LibraryPreviewDialog`: A/B original-vs-illustrated artwork and swipe-vs-classic comparison actions are preserved **on demand** behind the real Preview options icon, not removed or permanently occupying the cover stage. Keeps chosen modes in existing in-memory state without touching progress files.

### Play/Home
- `StorybookQuickEntry` now accepts an optional `modeGlyph`; existing callers and raster fallback behavior are unchanged.
- `GridMatesShell.kt`: injects real native Solo/Daily/Together/Books icons into primary cards, shortens numbered section headings, changes the QA books launcher to player-facing **Explore books**, and removes the developer-art-lab shortcut from the player-facing Home stack.
- `SoloSettingsScreen.kt` now accepts an optional QA art-preview callback and includes an **Artwork comparison** card near the end of Settings only when the QA feature route is available. Existing `Settings & backup`, local privacy and progress backup workflows remain intact.

### Illustrated library and details
- `HorizontalBookBrowserScreen.kt`: replaces the long QA review header with `Ws15BackHeader(Puzzle Books, Swipe to explore)` and a small preview overflow. The actual three illustrated covers, swipe pager, adjacent cover peeks, arrows, shelf illustration, selection animation, focused progress and Open CTA are unchanged.
- `BookcaseBookshelfScreen.kt`: applies the same header and overflow preview menu for the reversible classic layout, preserving original standing books, shelf count and selection callbacks.
- `PuzzleBookshelfScreen.kt` detail hero now uses the compact `Ws15BackHeader`; its **real illustrated original cover** and reading progress remain. It requests the **existing three WS-14K2 debug-only gameplay motif assets** in book and chapter emblems (with original legacy fallback), rather than creating an unrelated miniature icon family. The bookmarked page marker is reduced to a quieter 17×48dp. Chapter/page actions, catalog IDs, page labels, local saving, completion logic and progression are untouched.
- `SoloRecordsScreen.kt` and `SoloSettingsScreen.kt`: use the same shared `StorybookHeading` hierarchy as Collection. No record data is rewritten or removed.

### Test maintenance
- Existing Android instrumentation-source navigation checks updated for **Explore books**, the new preview overflow, the Classic shelves/Return-to-swipe dialog paths and compact accessible back arrow.
- Existing tests retain stable IDs and actual semantic action assertions; no invented on-phone execution claimed.
- No puzzle generator, board touch mapping, hints, Wi-Fi co-op, profile/collections, save format, package signer, release art approvals, external font assets, original book covers or portraits changed.

## Exact code snapshot and verification

- Last code commit for this implementation at journal creation: `f8a575a5c2c82a6a73413c5f732b85b258c5ed9d`.
- Standard Android QA [#42687169679](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42687169679) and art/release isolation [#42687169579](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42687169579) on the SAME source SHA were **in progress** at initial inspection; neither is preemptively marked success.
- Previous tested/dual-green **WS-14K2** source `ba596f98` remains a recoverable QA artifact: [ZIP #13703662259](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42682621963/artifacts/13703662259), app-debug.apk checksum `30bcd610b3a7ca3576cd2cbebf6bc78166f6fcf8aa4ae68a29ce9cbf9a651d63`.
- All new WS-15 assets are **native Kotlin vectors**, not generated PNGs, no binary replacement or new approval claims. Existing QA-only WS14 and WS14K2 unapproved PNGs remain isolated from release.

## Next action

1. Verify **both** exact-source CI results; if either fails, inspect the actual failing job log, repair only the failure and rerun exact commit. Do not hand out a guessed APK link.
2. When dual green, capture actual APK artifact ID and SHA-256, document it and give the owner **one WS-15 larger test build**.
3. Ask owner to compare Play/Home, illustrated swipe library, overflow QA controls, classic rollback, Space/Woodland/Garden detail and chapter imagery; also check Records/Collection/Settings typography at 320dp/large text if available.
4. After screenshots, WS-15 Pass 2 should refine native menu card density/spacing, Collection & Records asset support, information hierarchy, and residual word-gameplay notification placement as one batch. Keep draft PR #131 and preserve saved data.

## Exact first CI failure, focused correction and repeat

- Both original exact-code runs `f8a575a5c2c82a6a73413c5f732b85b258c5ed9d` **FAILED** during Android compilation: [standard #42687169679](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42687169679) and [art/release #42687169579](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42687169579). Their actual logs agreed: `GridMatesShell.kt:332:30 Unresolved reference 'testTag'`; the one source file had a new WS-15 Solo icon QA testTag call but lacked the Compose `androidx.compose.ui.platform.testTag` import. There is no evidence of a deeper gameplay or artwork fault.
- **Fixed in code commit [80636c85](https://github.com/PlayerEden/GridMates-Word-Search/commit/80636c85c0bcf26696d2828414ada1cf02d94860)** by adding precisely that import. No other game logic or save behavior changed.
- The corrected exact-source workflows are [Android QA #42687644991](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42687644991) and [art/release #42687645295](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42687645295), both **in progress** as of the latest inspection. These supersede the initial failed runs. Do not offer the new APK until both conclusions, APK metadata, release isolation and artifact identity are confirmed.
