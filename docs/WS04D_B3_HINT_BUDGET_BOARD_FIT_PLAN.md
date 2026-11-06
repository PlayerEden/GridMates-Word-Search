# WS-04D-B3 — Hint Budget & Board Cell Fit Implementation Plan

**Owner playtest:** November 6, 2026, five Android screenshots of v0.4.5 (15×15 and 20×20).  
**Status:** Draft implementation; code CI, Android runtime test, physical acceptance separately gated.  
**Branch:** `feature/ws04d-b3-hints-budget-board-cell-fit-2026-11-06`, stacked on [WS-04D-B2 PR #25](https://github.com/PlayerEden/GridMates-Word-Search/pull/25).

## Evidence and current direction

- **Confirmed visible progress:** 15×15 at 100/145/150% and 20×20 at 185% show the grid and letters changing size together, unlike the prior build's broken independent glyph enlargement. Compact header improves word-list visibility.
- **Remaining issue:** In 20×20's 100% overview, the final row appears partially cropped, likely from rounding each independently measured square row. Magnified boards still need readable navigation and offscreen long-word UX. Do not mark this fully solved until actual device verification.
- **Additional owner design decision:** Word Search hints should be less permissive than Sudoku's hints; endless path revealing would undercut the core search experience.

## Approved-for-prototype hint policy (easy to revise after playtesting)

| Puzzle | Hint steps |
|---|---:|
| Legacy 8×8, 10×10, 12×12 and new 12×12 Short | **2** |
| 15×15 Standard | **3** |
| 20×20 Extended | **4** |
| Daily (English v1 8×8, unchanged) | **2** |

**Rules:**
1. Hints are **per-board consumables**, automatically refreshed only on an explicitly new board; there is no global coin currency, cooldown timer, paywall, advertisement, streak pressure or real-money offer.
2. One tap advances the existing three-level ladder for an unfinished target: row hint → exact start → visible path. Each advancement costs one step; a hint never auto-completes the word.
3. HUD shows `Hint (N)` remaining, disabled at N=0 or after solving. Users can still finish normally with zero hints.
4. Consuming any hint continues to mark the eventual result **Assisted**; current scoring has no numeric penalty system, so don't invent one.
5. Existing saved hint stage entries and result history **remain untouched**, even if a previous player used more than the newly imposed limit. Such saves may have zero further hints; do not revoke old clues or invalidate backups.
6. Daily is restricted separately by the same core policy and cannot spend more by calling Solo store methods.

## Rendering fix

Existing `WordGrid` allocates a square outer viewport, but draws every row as 20 separately rounded square child cells. Integer layout rounding can make 20 accumulated rows slightly taller than the containing viewport, cropping the last glyph/border. Allocate height to every row using Compose `weight(1f)` and make each cell `fillMaxHeight()` with equal column weights. The available square viewport then determines total row height and all rows must fit exactly.

**Keep unchanged:** Entire-board `graphicsLayer` zoom, pan and hit alignment; word generator content/seed IDs, v1 English Daily, all native save IDs, schema4 JSON, achievements, original 12 Woodland PNGs, Sudoku's app and signing identity.

## Tests / acceptance

- [ ] Core JVM tests verify every allowance; first 2/3/4 taps advance hints, subsequent taps do not; no completion or word claim awarded.
- [ ] Existing three-step ladder tested on Standard 15×15; older save with third-tier hint remains readable without new allowance.
- [ ] Android source test checks 15×15 hint spending, restart, JSON export/import, Daily 2-step limit, and fixed Daily identity. CI **compiles** Android instrumentation; device **execution** required separately.
- [ ] User screenshot at 20×20 **100%** shows all 20 complete rows and border; 150–250% remains a true zoom, not scaled-only text.
- [ ] Hint (3) button count decrements, does not grant free steps via app restart, excludes already found word and disables when exhausted. Test with Short, Standard, Extended and Daily.
- [ ] Real Android typography/controls remain readable on narrow portrait, landscape and 200% font; 20×20 still requires larger-board navigation exploration.
- [ ] Keep stable signing issue #11 open and warn tester to export QA backup before uninstall if direct APK update is blocked.
- [ ] PR stays draft until Android screenshots and owner touch feedback; no new cosmetics/languages/multiplayer.

**Related:** [P0 zoom and layout issue #24](https://github.com/PlayerEden/GridMates-Word-Search/issues/24) · [hint/crop issue #26](https://github.com/PlayerEden/GridMates-Word-Search/issues/26) · [full v0.4.5 QA receipt](https://github.com/PlayerEden/GridMates-Word-Search/blob/feature/ws04d-b2-true-zoom-compact-workspace-2026-11-06/docs/WS04D_B2_BUILD_QA_2026-11-06.md).
