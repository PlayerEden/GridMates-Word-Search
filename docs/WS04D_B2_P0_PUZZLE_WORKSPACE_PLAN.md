# WS-04D-B2 — Puzzle Workspace Repair & Competitive UX Implementation Plan

**Date:** November 6, 2026 · **Priority:** P0 · **Status:** In implementation, not yet phone-accepted.  
**Owner evidence:** Eight Android v0.4.4 screenshots showing 15×15 and 20×20 layouts at 100%–250% zoom.  
**External research:** [Competitor Word Search UX Benchmark](COMPETITOR_WORD_SEARCH_UX_RESEARCH_2026-11-06.md) · [P0 issue #24](https://github.com/PlayerEden/GridMates-Word-Search/issues/24) · [book board issue #23](https://github.com/PlayerEden/GridMates-Word-Search/issues/23).  
**Current source baseline:** [WS-04D book presets draft PR #22](https://github.com/PlayerEden/GridMates-Word-Search/pull/22) at protected CI-green checkpoint [v0.4.4](https://github.com/PlayerEden/GridMates-Word-Search/tree/checkpoint/ws04d-book-board-presets-v044-ci-green-2026-11-06).

## Decision: match competitor usability, not appearance

Havos Word Search Ultimate has independent grid/word settings, first/last letter input, and, in its Plus edition, a movable grid/word-list divider. LR Studios emphasizes fonts and device adaptation. Pink Pointer differentiates directional challenge. PlaySimple emphasizes progression. Treat these as **developer-advertised reference features**, not actual hands-on competitor verification. Do not copy competitor art, interface layouts, or proprietary code.

GridMates should offer a **real book-sized search** with readable cells and word list visible during play, a warm-paper interface, quick resume, low-pressure timing, clear modes and honest puzzle results. Ship no new cosmetics, ads, content currencies or multiplayer until this P0 usability blocker is repaired.

## Owner-visible defects and prioritized fixes

| Gate | Defect | Required repair | Acceptance |
| --- | --- | --- | --- |
| **P0-A** | Zoom 125–250% enlarges letters but grid cells/border/hit-targets don't scale equally | One transform for the **entire measured board**, including lettering, rules, highlights and pointer hit geometry. No independent font scaling | At 100/125/145/150/185/250%, cell bounds, letters, highlight ribbons and taps remain aligned and never overlap |
| **P0-B** | 3+ tall header rows + toolbar waste vertical space | Two-row compact heading; full theme/size/challenge readable; put Customize/New Puzzle/feedback in Options, with confirmation for destructive new puzzle | Meaningfully more board/list room with no clipped title |
| **P0-C** | Only first ~4 words visible below the board | Keep words in a continuously visible independently scrolling target panel, compress chip padding/spacing, show progress and possibly a remaining-only filter | A typical portrait screen shows several words **at the same time** as the board, with independent scrolling |
| **P1-D** | “Move” and “Find” button labels indicate opposite of active mode | Show explicitly **Mode: Find** or **Mode: Move**; make toggle action clear and accessible | Current touch mode recognizable at a glance |
| **P1-E** | 20×20 long offscreen words and tiny 100% overview | Preserve zoom-out and first/end tap input; test all four corners and long diagonals, consider future edge-pan or native pinch later | No impossible paths or inadvertent claims while panning |
| **P2-F** | Landscape/200% font and screen-reader risk | Responsive side-by-side viewport/list where space allows, row compaction and accessible mode descriptions | Narrow portrait, landscape, 200% text remain usable |
| **P2-G** | Gameplay feedback/completion still prototype | After P0 repair, improve found animation, results and preferences | Clear, meaningful feedback without disruptive ads or timers |

## Implementation stages (sequenced)

### B2.1 — Measured viewport, transform and regression unit tests (NOW)
- Retain the existing `WordGrid` cell-based painter and selection logic, but **avoid simultaneously enlarging glyph font and constraining the underlying grid's measured size**.
- Apply a common scale and pan transform to the whole board, with top-left transform origin and independently clipped viewport; same transform for drawing and hit testing.
- On zoom change, preserve viewport-center focus where possible and clamp panning by `contentExtent - viewportExtent`. At 100% return pan to zero.
- Extract pan/zoom math into independently tested pure Kotlin core helper: exact center anchor, bounds, screen-to-grid geometry, zoom-out behavior.
- No generator, seed, content pack or save-schema changes. Leave v1/v2 puzzles and dated Daily untouched.

### B2.2 — Compact play HUD and word-list workspace (NOW)
- Two concise rows: Back + theme/size/challenge + Options; found count/time + Hint.
- Options popup contains Customize, New Puzzle and Feedback, rather than wasting an entire large row on these actions.
- Word-grid toolbar shows zoom and **current active touch mode**, not a mode that might be chosen next.
- Grid viewport uses the available screen thoughtfully, with a **persistently visible, independently scrollable target list**; adapt to landscape.
- Keep word-list chips concise; don't require returning to another page to remember target words.

### B2.3 — Native/emulator and owner playtest
- First pass CI: Kotlin JVM tests, Android lint/compile/test-source compile, 12 original Woodland PNG check, package/version/sha verification.
- Next gate: real Android instrumentation **execution**, screenshots across narrow/large landscape and 200% font. Compilation alone is insufficient.
- On phone: confirm 15×15 at 100/145/250%, 20×20 at 100/150/185/250%, all four pan corners, selecting offscreen long words via zoom-out/endpoints, selection line staying on letters.
- Test pause/resume/backup of old saved 8×8–20×20 boards and Daily; preserve original work and separate Sudoku package.
- Stable debug signing (issue #11) remains open; export old Word Search data before removing any differently signed build.

### B2.4 — Second usability pass (only after B2.1–B2.3)
- Decide whether pinch-to-zoom + two-finger pan should replace optional Mode: Move; avoid gesture conflicts with one-finger word tracing.
- Consider player-resizable board/word-list divider inspired by *documented* Havos Ultimate+ feature, after testing a fixed persistent split.
- Add larger-contrast/font options, clearer completion, optional remaining-only filter and more readable results based on player observations.
- Move into full nearby co-op only after Word Search feels satisfying in Solo and passes genuine native QA.

## Nonnegotiable engineering rules

1. **Freeze old puzzle IDs and generation behavior.** No edits to English v1 seeds/word lists/Daily or previously released v2 books just to fix UI.
2. Keep existing player profile, earned portraits, backup schema and local data intact. Do not touch Sudoku package or installation.
3. Keep all stacked PRs **draft**, with exact source SHA, CI run, APK hash, asset checks and rollback checkpoint once verified.
4. When useful, test both low-power hardware and a typical phone. Measure—not guess—visible words, top-bar height, letter size, selection error rate and elapsed time navigating the board.
5. Do not equate a green Android compile with a passed physical UI test. Mark both states separately.

**Success definition:** On a real 15×15 or 20×20 puzzle, magnification makes the entire grid truly larger, word selection remains accurate, the title and controls are immediately understandable, and the words to find remain accessible while playing.
