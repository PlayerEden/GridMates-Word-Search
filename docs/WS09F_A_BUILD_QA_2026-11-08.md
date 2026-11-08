# WS-09F-A — Continuous large-board region navigation / exact-source QA

**Date:** November 8, 2026 ET.  
**Draft stacked PR:** [#72](https://github.com/PlayerEden/GridMates-Word-Search/pull/72), based on [WS-09E #71](https://github.com/PlayerEden/GridMates-Word-Search/pull/71). **Not merged into main.**  
**Exactly tested executable source:** `d77a5a97c5017780da88d63ae1ec74ba0670eb0c`, QA-only **v0.9.2/code46**, same `com.playereden.gridmates.wordsearch.qa` package/test signer, **save schema5 and LAN protocol7 unchanged**.  
**Standard Android CI:** [#42462320253](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42462320253) **SUCCESS**.  
**Signed upgrade plus API35 emulator:** [#42462316597](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42462316597) **SUCCESS**, **110/110 on-device tests completed, 0 failed / 0 skipped**, Gradle successful.

## Product change — no new puzzle generation

- Larger **25×25 and 30×30 Solo** grids have a new **Board map** control. It opens a miniature overview with a rectangle highlighting the currently visible portion; tapping the map or choosing an accessible **Row A–C / Column 1–3** and pressing Jump moves to that section. Words continue across section boundaries: **one continuous puzzle, not nine subpuzzles**. Map navigation does NOT start a new game, award words, reset hints, or mutate a save.
- The larger-board viewport now supports **1×–12×** continuous zoom with clamped two-finger pan and focus-preserving pinch; jump centers a 10-cell section and zooms to an approximately ten-cell viewing window. The toolbar shows visible row/column numbers and current zoom. **Fit** resets to the entire square grid instantly.
- Existing 8×8 to 20×20 legacy/paperback boards retain the frozen **1×–2.5×** `BoardViewportTransform`, original touch gestures and toolbar, with no extra Map button. The underlying row/column word selection is unchanged on all sizes.
- Pure Kotlin `LargeBoardViewport` supports 25/30/40/50/75/100-cell geometry (clamping, corner access, 10-cell regions including partial final regions, normalized viewport overview, visible cell ranges, exact hit coordinate mapping). **This does not generate or enable 40–100-cell boards**.
- Real Android regression includes the 320dp **30×30 Map → Row C/Column 3 → Jump → 3× zoom → Fit** flow and verifies 15×15 retains no extra Map controls. The existing seeded-word swipe, 320dp paperback margins, v1/v2/v3 historical goldens, profile backups and library tests remain in the suite.

## Upgrade and compatibility proof

The emulator installed the *actual previously verified* [v0.9.1/code45 QA APK](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42460291830/artifacts/13610080051), then launched/flushed a schema5 fixture with:
- One partially solved active Woodland 15×15 game (found/hints/timer).
- **Two paused partially solved Solo games**, with independent found IDs/hints/timers.
- Two Daily entries, profile identity and populated Race/Duel journals.

It installed v0.9.2/code46 in place with the matching QA certificate, launched/backgrounded the app and compared decoded schema5 `active`, `pausedSolo`, `results`, `daily`, `identity`, `races` and `duels` fields. **Every field remained unchanged: PASS.** The [same emulator run #42462316597](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42462316597) completed **110/110 Android tests with no skips/failures**.

- **Raw QA APK SHA-256:** `a250b8fcafc36c0fcf083d1a03d59106e3c281caadbfe0584fca2f01018774e5`.
- **Same public test-only signer SHA-256:** `75cc3c896247d71fbd51f7a3ed41f90c8218a8a5a70df8c0f6b494b5cc23d419`.
- [v0.9.2 QA APK artifact #13610059177](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42462316597/artifacts/13610059177).
- [110-test reports #13610139078](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42462316597/artifacts/13610139078).
- Artifacts expire **November 15, 2026, ~08:26 UTC**.
- [Exact executable source checkpoint](https://github.com/PlayerEden/GridMates-Word-Search/tree/checkpoint/ws09f-a-v092-region-map-110-tests-upgrade-green-2026-11-08).
- The initial PR run [#42461932425](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42461932425) failed **Android test compilation** due to invalid two Compose assertion imports. Corrected before the source-matched fully green run; the first candidate is not considered verified.

## Still open: continuous 100×100 is not playable

- **WS-09F-B next:** viewport-aware rendering (cull off-screen letters/lines/selection decorations rather than drawing 10,000 cells every redraw), stable zoom/pan UX, test frame time/memory on Android hardware. Existing large canvas still draws all 25×25 or 30×30 cells; a pure visible-cell range computation alone is NOT virtualization.
- Then **WS-09F-C** 40×40/50×50 *isolated laboratory* content generator with versioned new word library and literal seeds; **WS-09F-D** 75×75 and ultimately 100×100 after benchmarks and owner testing, not simply by changing an enum value.
- **Viewport position is currently ephemeral**: when the board is reopened it starts at Fit. Persisting zoom/center/bookmarks per puzzle with safe recovery/export remains a future gated task. The **actual found words/hints/timers already persist via schema5**.
- The owner's physical narrow-phone/landscape/200%-font and long-session review remains OPEN. The specific family member's old installed APK version/signature is UNKNOWN; do not uninstall or clear the only copy. Two-phone Co-op P0 [issue #53](https://github.com/PlayerEden/GridMates-Word-Search/issues/53) OPEN; no real two-device acceptance, no production signing, no Play submission or main merges.
