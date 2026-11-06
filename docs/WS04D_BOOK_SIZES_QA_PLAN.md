# WS-04D Book-size Board Expansion — Design and QA

**Decision:** The current 8×8 / 10×10 / 12×12 preset choices make the app feel more like a starter mobile prototype than a proper paperback word search. A 12×12 board should be the **smallest normal Solo preset**, not the biggest.

## New book-style Solo presets

| Preset | Board | Target words | Intent |
| --- | --- | --- | --- |
| Short | **12×12** | **12** | Approachable paperback-sized puzzle, including Relaxed challenge |
| Standard | **15×15** | **16** | Default, satisfying conventional puzzle session |
| Extended | **20×20** | **24** | Long-form puzzle for players who want a larger search |

**Challenge remains independent:** Relaxed, Classic, Expert change direction permissions, reverse-word use, crossing preference and selected vocabulary; board size no longer determines difficulty. The larger sizes are *session-length choices*, not an indication that the solver is smarter.

## Android navigation strategy

- Zoomable board: 100%–250%, initial zoom 100% for 12x12, 145% for 15x15, 185% for 20x20.
- Separate **Move** (drag to pan the viewport) and **Find** (drag/tap to select letters) modes to prevent accidental word claims during panning.
- Larger glyph sizes are calculated for zoomed-in views; full-board fit is still possible at 100%, though tiny on a phone.
- Board remains a true square; underlying grid coordinates and word paths are not altered by zoom/pan.
- **Important usability limitation:** at high zoom, a long word may extend beyond the viewport. For now, players can zoom out or use first/end tap selection while repositioning. Smooth two-finger pinch zoom and edge-autopan during tracing are **not yet implemented**, and must be designed/tested before declaring 20x20 a polished phone experience.
- A phone screen cannot reproduce a paperback page's physical dimensions. Confirm UX on narrow phones and landscape before promoting large boards from beta.

## Save compatibility: no changes to existing puzzles

- Keep the existing enum names and **ordinals 0/1/2** for v2 `COMPACT` 8x8/6, `STANDARD` 10x10/8, and `LARGE` 12x12/10. Never reinterpret those IDs.
- Append `BOOK_SHORT`, `BOOK_CLASSIC`, `BOOK_BIG` to the end; new games use these enum IDs, with their own seeded word selections.
- The v1 generator and dated v1 Daily remain byte-for-byte stable. Existing v1/v2 active Solo puzzles **must not change automatically** when a new version is installed.
- New installs start with Standard 15×15; restarting a saved older puzzle preserves the historical board. Pressing **New puzzle** after an older saved puzzle begins a normal book-sized board.
- Save schema remains v4 because existing optional board/challenge fields already encode the distinct new enum values. Never reset progress on an unknown/future schema.
- Future content growth should use a new content version if it would modify seed outputs, including established new book-sized boards.

## QA acceptance

- [ ] Kotlin tests for original enum ordinals/board sizes plus all new 12/15/20 options, every theme/challenge, target validity, seed determinism, completion claims and bounded generation.
- [ ] Android compile, lint, instrumentation-test source compilation and actual APK package/version/art preservation.
- [ ] Device execution: v2 Compact 8x8 save reload unchanged and Book Big 20x20 active claims survive force-stop/export/import.
- [ ] Explicit **Move** mode must intercept drag without claiming words; **Find** restores tracing and tap-to-select.
- [ ] Test at 100%, 145%, 185%, 250% with 12/15/20 on small portrait/landscape devices; check edge positioning, return to Find, highlights and hint alignment.
- [ ] Font scaling 200%, screen reader labels and board movement affordances, no clipped buttons.
- [ ] Test long words that span beyond the visible zoom viewport, via zoom-out and endpoint taps. Evaluate edge-autopan or pinch-only future control.
- [ ] Character choice/achievements, results, Daily puzzle, backups, package identity and original 12 Woodland PNGs unaffected.
- [ ] Stable QA signing and actual update-in-place remains issue #11; export old Word Search data before uninstalling temporary-debug-signed APKs.

**Status:** Book-size change is a draft checkpoint until CI and Android physical UX gates. No claim of complete 20×20 user acceptance. Keep stacked PRs unmerged.
