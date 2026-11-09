# November 9, 2026 — WS-14K2 unified gameplay visuals, transient notices and tools

> **Status: IMPLEMENTED IN DRAFT SOURCE / EXACT-HEAD CI RUNNING / ON-PHONE UNVERIFIED.** This milestone intentionally groups the requested gameplay chrome + real three-theme artwork into **one** substantial owner APK, rather than repeatedly delivering tiny patch builds.

## Why this pass happened

The owner supplied six actual phone screenshots (see [WS-14K1](2026-11-09_WS14K1_OWNER_SCREENSHOTS_AND_COMPACT_BOOK_HEADER.md)) and said: hint popups should not reserve space, players should be able to revisit vanished game messages, zoom controls should be accessible on demand instead of permanently present, and **real Woodland/Space/Garden theme assets should appear in actual gameplay**. These are clear UI directions. We did not wait for another screenshot cycle before implementing this substantial but scoped pass.

## Starting point

- Draft [PR #131](https://github.com/PlayerEden/GridMates-Word-Search/pull/131); preceding WS-14K1 corrected source `212b900b593921afe965c32d5b59cbe327b08186`.
- Prior exact-source Android [#42679630809](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42679630809) **SUCCESS**, art/release [#42679630526](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42679630526) **SUCCESS**. Previous owner-visible WS-14J2 screenshots reviewed, but 20×20 word trace/hints/save not explicitly confirmed.
- Existing WS-14 original illustrated book covers, original 12 Woodland profile portraits, old WS-14J Woodland sprite and rollback paths remain retained. Existing save schemas are **unchanged**.

## Real implemented code

### 1. On-demand notice overlay and history (book puzzle pages)
- `BookPageGameplayScreen.kt`: replaces in-column feedback with a **short-lived floating overlay** drawn over, not laid out within, the puzzle screen. Both hint progress messages and word feedback are retained in a bounded **30-message, in-session history**. Hints and save errors show for about 6.5 seconds, other feedback about 3.5 seconds, then disappear without consuming layout height.
- New accessible 48dp **Notice history** native icon opens a simple Android dialog showing recent messages, newest first, including hints after their overlay vanishes. The history is **ephemeral per open page** to avoid save-format changes, so it does not currently survive closing and reopening the book. Stored gameplay hints/saves remain durable as before; the *notification history* is not a new save feature.
- New `BookPageGameChrome.kt`: native utility icons, notice dialog, overlay and compact word-list filter. No auto-playing animations, generated text images, or click overlays on the grid.

### 2. Zoom controls available only when needed
- `ZoomableWordGrid` has new optional `zoomConsoleVisible`/`onDismissZoomConsole` parameters. **Book pages only:** the persistent Select/−/Fit/+ row is gone; the toolbar height is removed from board sizing and the existing 48dp **Puzzle tools** icon in the header opens an actual dialog with zoom out, Fit, zoom in, and Select by row/column.
- The same existing viewport-transform functions and `PreciseWordPicker` are invoked; pinch, pan, single-finger touch/drag, calculated grid coordinates, Solo/Daily regular toolbar and 25+ map behavior are not rewritten.
- The dialog is intentionally on-demand, not a permanent translucent bar obscuring letters.

### 3. First matching gameplay sprite family: all three book themes
- Three **individual, unlettered, original transparent SVG masters**, no fake sliced concept sheet:
  - `design/visual-system/masters/ws14k2-woodland-gameplay-motif-v1.svg` (fern, oak leaf, mushroom/acorn)
  - `design/visual-system/masters/ws14k2-space-gameplay-motif-v1.svg` (ringed planet and stars)
  - `design/visual-system/masters/ws14k2-garden-gameplay-motif-v1.svg` (flower, leaves and butterfly)
- Build-only `tools/asset_pipeline/render_ws14k2_gameplay_motifs.py` creates **actual 256×256 RGBA PNGs** plus a manifest containing source and exported SHA-256. Output is generated **only into `app/src/debug/assets/ws14k2/motifs/`**, not into production assets.
- `BookArt.kt` now checks `ws14k2/motifs/<woodland|space|garden>.png` first in the specific QA page-emblem slot, with fallback to the WS-14J Woodland candidate and then WS-12 local studies.
- Actual book puzzle **word-list heading** can now show its theme's small new motif on the left; `Remaining` full-width filter chip becomes an accessible icon toggle. The header also shows the same motif where title spacing allows and no active bookmark/completion illustration takes precedence. This is **actual native UI integration**, not just an art laboratory page.
- `test_ws14k2_gameplay_motifs.py`, `verify_ws14k2_apk.py`, `Ws14K2MotifSelectionTest.kt` and updated Compose Android tests verify individual theme mapping, source/export hashes, debug package bytes, icon actions and release isolation.
- Android and art workflow files render images from source **before Gradle** with pinned CairoSVG 2.8.2, test them and require exact PNGs in debug; the art workflow additionally asserts **zero `assets/ws14k2/` in release**. The original art remains owner-unreviewed, not release-authorized.

## Exact implementation commits

- `682acfbd` — native tool/history/filter icon components and overlay surface
- `d6ec3d04` — real book-page notice/toolbar integration
- `9a2eeb66` — collapsible tool console, actual page-space recovery and compact word-list heading
- `77e0fcdb` — three distinct theme SVG source masters in Git
- `09040943` — deterministic source→PNG generator/manifest and package verifier
- `929b1080` — book-art fallback routing and theme source test
- `19a348ea` — Compose Android test import correction
- `11f7bbf3` / `ba596f98` — CI pipeline and release-APK discovery.

## Exact CI checkpoint (initial inspection)

- Standard Android QA [#42682621963](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42682621963) for exact source `ba596f98c09d0ebe58c2bdd64f56fa9946dcbc85` entered **Core + app unit tests, lint and QA APK** with no failed step at initial inspection.
- Release-art isolation [#42682622439](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42682622439) entered **Compile Android DEBUG and RELEASE** with no failed step at initial inspection.
- **Do not claim this newest Android APK is verified until both runs finish SUCCESS.** Superseded intermediate runs were automatically cancelled by PR concurrency; not evidence of regressions.
- No new owner device screenshot, 20×20 drag success, five-hint usability, or UI acceptance yet. Previous dual-green [WS-14K1 artifact #13701932678](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42679630809/artifacts/13701932678) remains available for fallback.

## Next single action

Check **both final CI conclusions**, inspect failed job-level logs if needed, fix and rerun until exact source green. Then extract successful artifact receipt (actual QA package metadata, APK hash, artifact ID) and supply **one unified Android QA build** for owner to inspect. The next phone review should compare the now-visible actual Woodland/Space/Garden motifs, notification history/overlay, popup tools and word-list/board clearance—plus explicit 20×20 tracing, repeated hint behavior and save reopen. Keep PR draft, preserve rollback/saves, and do not expand art into production before visual acceptance.
