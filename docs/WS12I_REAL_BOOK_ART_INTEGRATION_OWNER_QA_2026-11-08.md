# WS-12I — Real Bookshelf Artwork Wiring (QA v53)

**November 8, 2026 · User priority: get every art piece working first, then improve how eye-catching it looks.**

This milestone does **not** declare the Quiet Illustrated Paperbacks art final. The currently source-backed minimal illustrations are deliberately **replaceable placeholders**. It takes artwork out of the static Art Lab and puts it into actual playable book UI, while preserving all game state and the future right to redesign art.

## Now actually placed in the app

The three existing offline QA books (Woodland, Space, Garden) now use their six related graphical components in real Compose views:

| Asset | Real usage (QA mode only) | Unchanged native behavior |
| --- | --- | --- |
| Cover | Leading thumbnail on each Bookshelf book card | Title, theme, chapter count and page progress remain real text |
| Spine | Slim matching decoration next to each actual cover | Actual Open/Continue action stays a native 48dp button |
| Emblem | Beside the book title on Book Detail | Book identity and progress unaffected |
| Chapter marker | Beside each real chapter heading | Real book chapters/pages/callbacks |
| Bookmark ribbon | On bookmarked page row, and active saved page header | GMWB bookmarks still written by existing BookLibraryStore |
| Completion seal | Beside a truly completed page or whole book | Never shown for unsolved page; only existing validated page-completion state |

The actual Word Search grid has **no illustrated background** and no changes to touch gesture handling, hint count, word target list, scoring, persistence or existing puzzle-board rendering. If artwork is missing/corrupted, it simply won't render; text and buttons remain fully functional.

## Technique

- `render_family_review.py` regenerates the **identical** 18 original source-vector SVGs and SHA receipts already used in Art Lab v52.
- New `rasterize_android_qa.py` verifies every source file's SHA against the vector manifest, then rasterizes 18 small **PNG bitmap assets** at specific view sizes using pinned CairoSVG **at build time**, never at runtime. Raster checksum + source hash + dimensions are recorded in `ws12/raster/manifest.json`.
- New `BookArt.kt` is an intentionally tiny native Compose decorative Image component reading the PNGs from local QA assets; **no WebView in scrolling Bookshelf cards** or internet. Actual Art Lab continues to show the full source SVG sheet separately.
- `showPrototypeArt` is a reversible presentation-only flag, passed from the debug-only Home Bookshelf route through `BookLibraryFlow` to Bookshelf, Book Detail, and Book Page. Default false in independent callers/tests; release app never activates it. The same static art catalog can later use new approved hashes without changing book or reward IDs.
- Build packaging: exactly **18 source SVGs + 18 source-verified PNGs in the QA APK**, **zero** WS-12 preview assets in release APK. Both source and raster hashes checked inside the final packaged APK. No source ZIP imports or new image-generation model installed on phones.
- Signed QA app bumped from versionCode 52 → **53**, versionName `0.9.9-book-art-integration-qa`. Same `com.playereden.gridmates.wordsearch.qa` application ID and same public test signer allow compatible install-over. The separate QA-specific GMWB and GMWS book journals keep their filename/schema/IDs; Solo/Daily/Collection files and approved original 12 Woodland portraits/old font files are untouched.

## Next real owner test

1. Install the verified v53 **over v52**; don't uninstall. Confirm your prior book progress is still there.
2. Home → **Test Puzzle Books**. See the actual book covers/spines beside Woodland, Space and Garden; buttons still open the real books.
3. Open Woodland: look for the **emblem and chapter markers**. Bookmark Page 1 and see a **bookmark ribbon**, then enter the playable board and verify selected words still work.
4. Complete Page 1 and confirm the **completion seal appears only on a completed page**, and the progress counters still match. Continue/replay as normal.
5. Test Space/Garden. Check narrow-phone readability, scrolling, bigger system text, rotation and any decorative piece that makes a button difficult to find.
6. Art Lab still exists for comparing the six full size art pieces. Later we can improve the **motif richness and illustration impact** without changing the data model or rewriting screens.

## Gates remaining

Physical-phone acceptance and true on-device instrumented execution; approved art source/licensing/human final design review; production art resource packaging; full publication-ready book catalog; portrait/achievement/Collection expansion using a *separate* compatible family style pass. No production/unreviewed artwork has been approved automatically.

**Art direction feedback to retain:** the owner says the first graphics are *not as eye-catching as expected* but wants the pieces **functionally integrated first** and the final look refined afterward. Don't mistake this QA placeholder implementation for permanent aesthetic signoff.
