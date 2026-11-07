# WS-07 — Physical Playtest, Touch/Gesture Research and Usability Specification

**Date:** November 7, 2026  
**Purpose:** Research-backed requirements and design decisions for the *entire* Word Search refinement arc.  
**Repository source:** [WS-07 audit issue #52](https://github.com/PlayerEden/GridMates-Word-Search/issues/52), [physical P0 issue #53](https://github.com/PlayerEden/GridMates-Word-Search/issues/53), [WS-07A draft PR #54](https://github.com/PlayerEden/GridMates-Word-Search/pull/54).  
**Physical evidence:** Nine owner-supplied Android screenshots of Home, Solo 15×15, zoomed Solo, Expert 20×20, Nearby setup, waiting and connected match. The supplied images are conversation attachments, **not** public repo assets. Do not upload screenshots with IP addresses or chat without redacting.  
**Status:** Research/requirements only; a proposed feature is not proven just because it appears in this document. The owner's two-phone failure is **OPEN** until repeated physical acceptance.

## A. Direct observations (owner evidence, not inferred test results)

- Both phones could connect to a nearby room and show a connected partner, yet **neither phone could successfully mark a found word**. P0. This is not proof that the network protocol itself is failing: distinguish touch input, claim serialization, authoritative validation, UI feedback and redisplay.
- On 15×15, the board was fitted at 100% in one screenshot and cropped at 150% in another; default code used automatic 145% startup. On 20×20, the default code opened at about 185%; the owner had to use Fit/zoom and Move to explore. Cropped letters and selecting an exact start letter were frustrating.
- Nearby displayed a very long information/setup page before gameplay; an active match could show room text, status, large presence, board, chat and full word list in one scrolling surface. This buried the play loop.
- Expert 20×20 is extremely dense on a single phone. Fit is helpful for overview but not automatically readable. Both full-board view and a low-friction precision method are needed.
- Portrait is the only orientation shown in these nine captures; do not infer that landscape, TalkBack, or 200%-font physical gameplay passed.

## B. Source-backed external research

### B1. Compose pointer delivery & tests
- [Android — Understand gestures](https://developer.android.com/develop/ui/compose/touch-input/pointer-input/understand-gestures): input events flow through Initial/Main/Final passes; parent and child recognizers can compete, and custom handlers need explicit consumption. Use `awaitEachGesture` for an entire pointer lifecycle. `awaitFirstDown`, drag helpers, `calculateZoom`/`calculatePan`, and `performTouchInput` support deterministic gesture implementation/testing.
- **Implication:** track raw down/move/up cells and active pointer IDs, cancel one-pointer selection when additional fingers arrive, consume relevant movement before scroll can steal a word, and test release on the last target cell. Handle cancel/out-of-bounds and touch slop. Compose input event handling must not be dependent on the display font or glyph padding.
- **Critical:** a passing `NearbyCoopSession.submit` unit test proves host rules, not the actual tap/drag-to-claim path. End-to-end Android input injection through the rendered board and *physical* two-phone tests are both required.

### B2. Accessibility and alternate inputs
- [Android — Make apps more accessible](https://developer.android.com/guide/topics/ui/accessibility/apps): ordinary touch controls should have **at least 48dp** targets; custom controls must be sized deliberately.
- [Android — Compose semantics](https://developer.android.com/develop/ui/compose/accessibility/semantics), [merging/clearing](https://developer.android.com/develop/ui/compose/accessibility/merging-clearing): descriptive labels, actions, states and efficient focus grouping matter. Hundreds of raw cell nodes may create an exhausting screen-reader experience.
- [Android — Test accessibility](https://developer.android.com/guide/topics/ui/accessibility/testing): combine manual TalkBack, Switch Access, automation and feedback from people using assistive technology. No test-suite count establishes accessibility by itself.
- [W3C WCAG 2.2 — Dragging Movements](https://www.w3.org/WAI/WCAG22/Understanding/dragging-movements), [Pointer Gestures](https://www.w3.org/WAI/WCAG22/Understanding/pointer-gestures): provide non-drag and non-multipoint alternatives. These web guidelines inform our native UX **as design guidance**, not a claim that this Android game is WCAG-certified.
- [W3C WCAG 2.2 — Target Size](https://www.w3.org/WAI/WCAG22/Understanding/target-size-minimum): pointer size/spacing matters. A spatial grid has practical exceptions, but accessible alternate methods are still necessary. Do not pretend 20×20 tiny cell hit regions meet Android's ordinary **48dp** recommendation.

### B3. Adaptive layout
- [Android — Adapt layouts](https://developer.android.com/design/ui/mobile/guides/layout-and-content/adapt-layout), [window size classes](https://developer.android.com/develop/adaptive-apps/guides/use-window-size-classes): respond to available width **and height**, rotation, font scaling, split-screen and device sizes. Avoid forcing portrait.
- **Implication:** no whole-page scroll in the *active play scene*, but genuine settings/history/word-list content may scroll inside clearly invoked sheets or separate screens. The board must not disappear offscreen or compete with scrolling for word gestures. Compact-height landscape uses a side panel or segmented page rather than squeezing every control under the board.

## C. UX rules locked for the refinement arc

1. **Play first** — Home: obvious Continue Solo, Daily, Play Together; mode choices and customization secondary, never hiding primary actions. A novice should understand the next step without reading instructions.
2. **One finger finds** — straight drag from start to end; also two discrete taps (start then end) for people who cannot drag; invalid path visibly announced, never silently swallowed; reverse, diagonal and edge selection work.
3. **Two fingers navigate** — pinch/translate an enlarged board, with a visible **Fit** reset and accessible + / − controls. Additional finger cancels in-progress selection; zoom/pan must never submit an accidental claim.
4. **Fit is a starting point, not a forced precision ceiling** — start with entire board visible (no implicit 145/185% crop). On 20×20, use an optional precision alternative (zoom, magnifier/crosshair, or accessible cell navigator, subject to playtesting) that never needs continuous mode toggling.
5. **A board owns its touch gestures** — choose one well-tested input arbiter and coordinate transform shared by drawing, semantic selection and touch. Handle touch DOWN, UP, CANCEL, slop, out-of-bounds, rapid repeat, drag ownership and pointer ID changes. Avoid per-cell clickable overlays competing with drag recognizer.
6. **Live games stay put** — fixed active-game scene with back, actual connection/partner, progress, grid, readable brief word preview and direct sheet actions. No stacked intro, room instructions, 24-line chat, decorative card and full word list above or below the board during gameplay.
7. **Optional details are secondary** — Room Info, Chat, all words, cosmetic look settings and post-match comparison open on demand (sheet, dialog or proper screen). Preserve partner privacy and no forced scrolling of the playing surface. Sheets can scroll where needed and must not steal a gesture outside their bounds.
8. **Accessibility is a *separate* interaction path** — explore grouped board semantics, targeted row/column or cursor navigation, explicit Select Start/End and spoken progress; 400 tiny individual focus stops is not an acceptable end state. Controls need clear semantics, appropriate large targets, 200% font compatibility, keyboard/Voice/Switch/TalkBack where supported, and contrast not reliant on green/blue alone.
9. **Do not lose competitive game identity** — Co-op collaborative shared board, Race per-word ownership and fair winner/tie, Duel private paths and ready/GO, chat only within same Wi-Fi, host authoritative; no visible but bogus scores.
10. **No silent failures** — show "waiting for partner", "checking claim", "invalid direction/not a target", "already claimed", "accepted", "disconnected" at appropriate points; don't reveal private Duel answers. If host rejects a path, display meaningful feedback without leaking sensitive network details.
11. **Preserve data and rollback** — no saved puzzle ID changes, malformed migration, app/package/signing changes, or LAN protocol change for visual-only work; existing stable-signer QA installs over, retain saved Solo/Daily/Race/Duel. Preserve verified v0.7.6 fallback and source/checksum.
12. **Treat preferences as hypotheses until physical proof** — final zoom limits, whether to include a transient magnifier, number of pinned words in the HUD, paging vs a word drawer, and advanced navigation are to be measured with users, not advertised as completed features.

## D. Acceptance metrics (proposed targets; no measurements claimed)

| Dimension | Trial / measure | Proposed release threshold |
| --- | --- | --- |
| Real two-phone Co-op P0 | A and B join, each claim target using finger drag and two taps; both screens converge | **100% valid claims delivered/represented exactly once** in acceptance matrix; no physical failures |
| First-time basic 12×12/15×15 | 5+ novice trials on actual devices; correct start/end, no prompting | Target **≥95% first-attempt word selection** across clearly visible targets; collect failures |
| 20×20 precision | 5+ trials with horizontal/vertical/diagonal/backward and edge targets | Target **≥90% first-attempt with selected precision assist**; adjust assist based on misses |
| Multipointer safety | Zoom, two-finger pan, second-finger interrupt, release and cancel | **0 accidental valid claims** in test suite |
| UI comfort | Home → Solo; Home → Host; Home → Join, screens at 320/360/400dp plus portrait/landscape | Main CTA immediately available; **no full-page vertical scrolling while active match is running**; no overlap/crop |
| Accessibility | TalkBack + Switch Access + 200% text + larger font + keyboard/navigation path | Main gameplay and navigation can be completed via non-drag, non-multipoint path; no inaccessible dead end |
| Competitive safety | Race simultaneous claims, Duel privacy, disconnect/reconnect, final recorded results | Same authoritative result on both phones; no duplicate scores; no leaked opponent paths |
| Save/update | v0.7.6 → candidate same signer, off-device exports, background kill/restart | Schema4, profile, fonts, Solo/Daily and both result journals preserved |
| Performance | old/new low-end device cold-start, 20×20 board, repeated trace/zoom, share and journal stress | Establish numeric frame/memory budgets from measured baseline; zero ANRs/crashes in scripted runs |

Thresholds are *project goals*, not Android/W3C mandatory criteria, and not measured successes.

## E. Design alternatives and guardrails

- **Option 1 (default):** always show entire board at start, 1-finger word trace, 2-finger pinch/pan, Fit/zoom controls, 2-tap endpoints, compact list. Minimal learning overhead.
- **Option 2 (if 20×20 stays difficult):** accessible precision aid that previews the selected letter/cell at touch-down (loupe or crosshair), supports small corrective movement before commit, and explicitly shows start/end. Keep it optional and transparent; no auto-completion/auto-correction to a different word without a player choice.
- **Option 3 (accessibility fallback):** word target selection + board position navigation with larger controls or row/column choices, to select endpoints without needing tiny raw cells. This must **still validate the same path** and respect host rules, not grant an unearned word.
- Reject: mandatory Find/Move switching, automatic cropped zoom as default, zoom-only text independent from cells, scrolling the entire gameplay page, 400 TalkBack steps as sole navigation, silent invalid selection, and cosmetics above basic controls.

## F. Required QA matrix before changing status

- **Boards:** Daily 8×8 where applicable, 12×12, 15×15, 20×20; each available theme, challenge and font/look; solved/unfound/highlight paths.
- **Gestures:** tap-start/end, long drag, short drag, diagonal/reversed, edge/corner, multi-touch interrupts, pinch near edge, pan clamp, Fit, rapid tap, finger-up final cell, Android back/rotation.
- **App surfaces:** Home, Solo, Daily, Nearby setup, host/guest active Co-op, Race, Duel Ready/GO, chat, word sheet, room info, results, records, collection, settings/backup, shares.
- **Devices:** at least two independent Android phones and distinct Android/OEM variants if practical; emulator 320dp compact and 200%-font, 360–430dp portrait, compact-height landscape, TalkBack and Switch Access.
- **Network:** host/guest role swap, NSD discovery/manual join, optional code, Wi-Fi/hotspot/guest isolation, delayed claims, race collisions, interrupted/deferred guest, reconnect, host departure.
- **Durability:** same signer install-over; malformed backup and atomic restore; 1/3/5 years Daily/long journal; the resulting exported file must remain re-importable, not merely saveable.
- **Evidence:** run and SHA, exact APK checksum, pass/fail/error counts, private device screenshots/videos (redact IP/code/chat), user feedback and tracked issue. A first green build does not close P0 unless both physical phones pass.

## G. Work sequencing and ownership

Refer to [Development Roadmap](DEVELOPMENT_ROADMAP.md) for the full WS-07A through WS-07G sequence. All new code flows through **new draft PRs**, each with CI, emulator and a safe rollback. Never auto-merge into `main` or publish a build labeled stable without actual acceptance.
