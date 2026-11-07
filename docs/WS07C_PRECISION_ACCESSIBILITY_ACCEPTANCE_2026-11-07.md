# WS-07C — Accessible Precision Word Selection: Development & Acceptance

**Date:** November 7, 2026  
**Status:** source implementation in [draft PR #56](https://github.com/PlayerEden/GridMates-Word-Search/pull/56); physical testing **DEFERRED by owner**. This is a QA candidate, not a user-accepted release.  
**Version:** Android QA v0.7.9 / versionCode33, stacked on WS-07B v0.7.8 / code32 verified checkpoint.

## Design purpose

A dense 20×20 board can be readable as an overview but extremely difficult to tap precisely. Some players cannot drag consistently or pinch with two fingers, and 400 individual screen-reader focus stops make play impractical. The game should still resemble an accessible paperback puzzle book rather than a technical editor.

### Added interaction

- The normal board starts fitted and keeps one-finger drag, two-tap endpoints and two-finger pan/pinch, unchanged.
- A clearly labeled **Select** button opens optional **Select letters precisely** controls. The player selects **row** and **column** with explicit previous/next controls (each 48dp), hears the selected letter and a spoken text sequence of the current row, sets the first letter, then chooses the last letter and presses **Mark word**.
- The picker prevents empty or crooked lines *before submission*, but **never searches for a target or grants credit**. Its end coordinates flow through the identical Solo/Daily/host-authoritative Nearby claim callback used for direct gestures. Invalid target words still fail in normal gameplay logic.
- The board now exposes a single descriptive accessibility action to open this picker instead of 400 tiny letter semantics nodes; direct finger tracing remains on the parent board, with no intermediate 400 empty composable boxes.
- Zoom out, Fit and Zoom in controls now use explicit 48dp targets; they remain visible alternatives to the two-finger gesture. The new picker is optional and not a new mandatory Find/Move mode.

## Authoritative research

- Android: [Compose accessibility/semantics](https://developer.android.com/develop/ui/compose/accessibility/semantics) and [accessibility testing](https://developer.android.com/guide/topics/ui/accessibility/testing) — avoid excessively long focus traversal, use descriptive actions, verify with a screen reader and actual assistive input.
- Android: [Touch target size](https://developer.android.com/guide/topics/ui/accessibility/apps) recommends at least 48dp for ordinary targets.
- W3C: [WCAG 2.2 Dragging Movements](https://www.w3.org/WAI/WCAG22/Understanding/dragging-movements) and [Pointer Gestures](https://www.w3.org/WAI/WCAG22/Understanding/pointer-gestures) support non-drag and non-multipoint alternatives. This is design guidance, not a native Android conformance claim.

## Automated gates

- [x] Standard core/app/unit/lint/Android test Kotlin compilation passed on exact code commit.
- [x] Actual Compose: select a placed target start and end with only row/col stepper buttons; resulting callback receives exactly the target endpoints.
- [x] Compose: non-straight and zero-length endpoints do not enable Mark word.
- [ ] Compose: 20×20 board has a single descriptive accessible action; no 400 individual Row/Column focus targets; tapping Select opens the picker.
- [x] Existing 15×15 and 20×20 single-finger injected gestures, two-tap, zoom/pan, Solo/Daily and three Nearby modes remain unaffected.
- [x] Full API35 instrumentation + actual same-QA-signed v0.7.8→v0.7.9 install-over preserving schema4 Solo/Daily/profile/Race/Duel history.
- [x] Source-matched APK SHA-256, signer, run, reports and rollback pinned. No app ID/signing/schema4/LAN v7 changes, no merge or Play production release.

## Owner physical acceptance — not requested yet

**Do not ask the owner to test until they say they are ready.** At the next planned playtest, confirm on both phones:
- 15×15 and 20×20 words can still be traced naturally and selected by two taps; Select permits accurate placement without drag.
- Try a row and column move, pick first and last letter, mark a target in Solo and Co-op. Both phones must see the host-approved result; no unearned words or leaked Duel solution.
- Fit, zoom and two-finger pan do not accidentally claim words. Controls remain reachable at 320dp width, landscape, 200% Android system font and with TalkBack/Switch Access.
- For speech navigation, a user can find a row, hear its letter sequence, choose a column, select endpoints, and return focus to the board afterward. No reliance on 400 repeated cell announcements.
- Include private screenshots/video with IP addresses, room codes and chat redacted.

**Future improvements remain open:** a touch loupe/crosshair prototype, keyboard/d-pad navigation, zoomed-pan gesture stress, TalkBack focus restoration, large-text adjustments and human usability trials. Do not claim full WS-07C release acceptance before those are measured.

[Project roadmap](DEVELOPMENT_ROADMAP.md) · [Earlier WS-07 research](WS07_PLAYTEST_RESEARCH_AND_UX_SPEC_2026-11-07.md) · [physical Co-op blocker #53](https://github.com/PlayerEden/GridMates-Word-Search/issues/53).


## Green automated receipt

Source-matched [WS-07C report](WS07C_BUILD_QA_2026-11-07.md): **83/83 tests PASS**, exact commit `113e5e51d44f77c2a4e1bd2dffe3cc0c9b0a8eb6`, QA APK SHA-256 `95be321feee5ef55abd40c0ac2af51670215e50f993a393fdd6006b362804d43`, same-signed saved-data-preserving v0.7.8→v0.7.9 upgrade PASS. Human/phone checks remain unperformed as instructed.
