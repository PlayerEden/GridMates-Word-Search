# 2026-11-09 — WS-14E: GridMates-wide visual identity, trademark correction & motion ideas

**Status:** Design/standards pass. **No new sprites created, no game code altered, and no APK built in this pass.** Owner's ideas recorded as forward direction; no motion implementation is approved/tested yet.

## Who / what / where / why

- **Owner:** Noted that the GridMates name is **not registered**, so ® alongside GridMates is misleading. Wants a cohesive, growing, cross-game sprite family potentially reusable in GridMates Sudoku, Word Search, Crossword and future series. Asked about subtle motion and liveliness without making the app static or flashy.
- **Assistant:** Reviewed the existing visual QA direction and current GitHub state, checked USPTO and Android Compose documentation, and designed a proposed GridMates-wide art/motion contract. This is advice/documentation, not a claim of code already shipped.
- **Where:** WS-14 current Word Search draft [PR #131](https://github.com/PlayerEden/GridMates-Word-Search/pull/131), with possible future versioned cross-game asset library. This pass **does not change** SudokuTogether.
- **Why:** Actual illustrated book screenshots show that the book art works, but the application needs a single shared style grammar and restrained animations, not random decorative additions.

## Previous checkpoint and decisions

- Prior pass: [WS-14D owner screenshots and grounded horizontal library recommendation](2026-11-09_WS14D_FIRST_PHONE_VISUAL_REVIEW_AND_LIBRARY_DIRECTION.md).
- Starting branch HEAD: `264b0d37d10c0f54dbf33029552e8219c0f18491` on `feature/ws14b-style-anchor-and-art-direction-2026-11-08`.
- Last source-matched successful art QA APK: `00d2a36`; [Android QA](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42576785096) success, [asset isolation](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42576785170) success, six cover/spine PNGs in debug, zero in release. Owner has now viewed the art on a real handset and shown working book detail/bookmark, **not full device regression**.
- Safe prior fallback: v56 before this visual round; current WS-14 art candidate has proven a subset of real phone behaviors but remains a draft-only test build.

## What this pass records

- **Wordmark:** plain **GridMates** by default; optionally **™** for unregistered trademark use; **no ®** without actual verified registration covering the relevant goods/services. Audit image pixels as well as native strings. The current artwork sheet is immutable/hash-pinned; do not silently replace V1. Correct artwork in a new V2 before public release.
- **Shared visual DNA:** parchment and ink palette, restrained green/gold accents, readable serif heading + pragmatic body text, book/paper touch, compatible shadow/stroke/scale, stable accessible UI, with game-specific themes layered on top.
- **Reusable asset bank:** shared neutral UI materials, small generic motifs and presentation frames vs game-specific covers/number faces/clues. Pinned source hashes/IDs, commercial provenance review, separate per-game integration—nothing auto-syncing between apps.
- **Motion hierarchy:** primary touch response (lift/spring/page selection), one-shot transitions (page open, achievement/stamp), very rare optional ambient life (blink, star twinkle, butterfly) **only from new independently layered sprites** where required. Existing one-piece PNG cannot independently blink its owl. Reduced-motion and low-power alternatives mandatory.
- **Performance/QA:** retain Compose native controls, fast input, bounded image decode; don't override tracing gestures with pager motion. Physical 320dp, 200% text, TalkBack and save regressions remain gates.
- Full reusable specification: [GridMates shared visual and motion contract](../GRIDMATES_SHARED_VISUAL_IDENTITY_AND_MOTION_2026-11-09.md).

## Build / QA evidence and current status

- **Build this pass:** None (documentation/decision only).
- **Automated CI:** not invoked specifically for the design. Last image-source-matched verified runs above.
- **Owner motion test:** none; speculative features deliberately not presented as implemented.
- **Legal and final art gate:** ® removal and source V2 inspection required prior to production; commercial/trademark matters not inferred from a model-generated logo.

## Next

**Immediate:** build the previously planned WS-14D horizontal bookshelf **QA A/B**, using existing images and only one understated cover-lift/selection transition, honoring reduce-motion and visible navigation. Remove the detached spine from the QA alternative, not from historical master sources. Preserve prior shelf for direct comparison.

**Later:** owner handset feedback → decide if subtle layered ambient sprites are actually useful → version reusable GridMates core asset vocabulary → separate Sudoku integration if desired. Do not start by producing a large batch of blinking/animated art.

**New-chat instruction:** Read [journal](../DEVELOPMENT_JOURNAL.md), this pass, [WS-14D plan](../WS14D_GROUNDED_LIBRARY_AND_HORIZONTAL_BROWSER_DESIGN_2026-11-09.md), [shared motion/brand specification](../GRIDMATES_SHARED_VISUAL_IDENTITY_AND_MOTION_2026-11-09.md), current handoff, and live PR status before working.
