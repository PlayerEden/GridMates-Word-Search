# WS-02B — Puzzle Feel, Marker Highlights, and Mobile UI

**Date:** November 5, 2026  
**Status:** Owner playtest feedback accepted; detailed values below are implementation hypotheses to test, not finalized release controls.  
**References:** [Master Design](MASTER_GAME_DESIGN.md) · [Roadmap](DEVELOPMENT_ROADMAP.md) · [First phone UX feedback issue #5](https://github.com/PlayerEden/GridMates-Word-Search/issues/5) · [WS-02 prototype PR #4](https://github.com/PlayerEden/GridMates-Word-Search/pull/4)

## Player feedback and goals

First phone playtest of the WS-02 APK showed that the grid renders, words highlight when found, and the GridMates family colors translate well. The user specifically reports **word dragging did not feel smooth or satisfying**, wants better tactile feedback and our previously planned highlight visuals, and wants generated sprites/art considered **throughout development**, not bolted on at the end.

**Priority order:** core touch accuracy → visible feedback → board/list ergonomics → visual polish → extensible art slot system. Avoid spending on future gacha/rewards before finding words feels great.

## Functional plan

### 1. Smooth selection model (WS-02B-A)

- A drag starts with a clear single-cell marker. Don't demand instant perfect diagonal alignment.
- After moving about **0.55 cell width**, resolve one of eight directions from the gesture angle (horizontal/vertical/diagonal).
- **Lock that direction for the gesture** so finger wobble cannot make the highlight turn randomly.
- Project finger movement along that direction, snapping the endpoint to whole cell centers; allow natural slight off-axis movement.
- If finger returns near start, clear locked direction and let it be reselected.
- Clamp the endpoint to the board boundaries; no out-of-range or bent paths.
- Invalid drags fade/cancel gently. A short aborted gesture shouldn't scold the player as a wrong word.
- Keep tap-first/tap-last selection as a fully functioning alternative.
- Record direction/snap tests in pure Kotlin core, with tiny/large grids, boundaries and backwards diagonal cases.
- **Real-phone QA:** compare slow drags, rapid swipes, diagonals/backwards, thumb offset, crowded Hard cells, parent scrolling conflict and tap fallback.

### 2. Highlight language (WS-02B-B)

Use a **cozy paperback highlighter** language—procedurally drawn first for precise scaling, with a future asset-replacement seam.

| State | Appearance | Feedback |
| --- | --- | --- |
| Idle | Cream paper and lightly ruled letter cells | No sound/haptic |
| Touch start | Soft gold circular dab at starting cell | Light optional haptic |
| Active drag | Translucent gold rounded marker ribbon centered behind letters; continuous path | Very light tick only when advancing to another cell; no vibration storm |
| Valid word | Short confirmation haptic / optional system click; line settles into a permanent muted ink-friendly marker | Cross off target and announce word found |
| Already found | Existing line stays stable; quiet “Already found” response | No duplicate award |
| Invalid | No permanent mark; short neutral status then clear | Avoid buzzy error sound |
| Finished | Clear “Puzzle Complete” plus one optional tiny celebratory reaction | Never blocks next action |

**Render constraints:** marker strokes behind text, rounded ends, opacity about 0.25–0.4 subject to contrast checks, preserve letters, support crossing words, never obscure ownership in future multiplayer. **No animation-heavy full-screen effects** in the touch release path.

**Future cosmetics**: soft marker, pencil underline, ribbon, dotted/stamped trail, page edge. Stable semantic style IDs, preview in Collection, color-blind alternatives. A procedural renderer remains the legible fallback when generated assets are not available.

### 3. Compact screen composition (WS-02B-C)

The user's screenshots show theme/difficulty chips pushing the board below the fold, while Hard requires scrolling far down to the target list. Fix this immediately:

1. **Playing mode:** one compact top row with Home, theme/difficulty label, and a **Change** action; slim progress.
2. **Change Puzzle dialog** owns theme and difficulty controls; no clipped horizontal chip lists during active play. Explicit new-puzzle action prevents accidental resets.
3. **Square board** fits within available visible height rather than always consuming full screen width on short phones.
4. **Word list in a dedicated panel directly beneath board**, independently scrollable; a visible 1–2 row preview, no need to scroll the entire page away from the board.
5. Word list keeps two columns when legible, one when large-font/short/narrow testing requires it; no invalid clipped labels.
6. Preserve status/success feedback near the board. Completion card not a blank dead end.
7. The active puzzle's column must **not** have a full-page verticalScroll parent, so the board owns its gesture.
8. Fix system-bar foreground visibility on light paper background and remove unintentional lavender default Material3 colors.
9. Replace placeholder navigation dots with coherent icons and reduce inflated Home presentation spacing without degrading thumb targets.

**QA:** screenshot standard + narrow portrait, landscape, 200% font; crosscheck tapping Word List/Change controls. Do not claim all these configurations pass until actually tested.

### 4. Feedback options (WS-02B-D)

- Start with Android OS-respecting haptics: mild step tick, stronger valid-word confirmation. Provide an on/off switch in the temporary puzzle controls.
- Optional system UI sound effect only on successful find; default restrained; separate toggle. Reuse a future short original sound asset once produced.
- Respect reduced motion and sound-off system preferences where possible. Stop effects when app backgrounds, and avoid sound spam while tracing.
- Counter and status text remains useful **without** audio/vibration.
- Multiplayer feedback must be separated later: **local claim pending** vs **host confirmed**. Do not award network score based on animation.

## Original / generated asset pipeline

**Decided direction:** Gradually create cohesive original sprites and art as meaningful elements enter the app, while preserving the real approved original Woodland portraits from Sudoku when we adopt that profile system. Don't regenerate near-duplicates of the existing portraits.

### Immediate WS-02B asset slots

- Draw marker strokes procedurally in Compose Canvas today—resolution independent and directly driven by validated selection paths. No external PNG required.
- Make native/vector-based small navigation glyphs and checkmarks now, designed to match GridMates Forest/Paper/Gold.
- Define stable highlight style interface / IDs before shipping user-owned cosmetic inventory; keep original colors and styles as defaults.

### Next art batches and destinations

| Batch | Candidate asset | Timing | Guideline |
| --- | --- | --- | --- |
| UI baseline | Marker nibs, soft highlight grain/texture, check seals | After actual finger-feedback approval | Small transparent alpha sprites / vector if possible |
| Profile/collection | Actual 12 Woodland portraits and provenance | WS-04 | Copy original files and IDs from Sudoku verified v0.11C source, not AI-regenerate |
| Puzzle packs | Woodland, Ocean, Garden, Space and Bugs illustrated headers | WS-04/07 after art brief | One coordinated series, not individually mismatched generated images |
| Rewards | Stamp/daily gift icons, collection framing | WS-04/07 only if economy approved | No fake scarcity, remain legible |
| Sharing | Results postcard backgrounds/frames | WS-07 | Locally rendered and spoiler/privacy safe |
| Audio | Subtle original marker whoosh/word-find tick/completion | After UX pass | Owner-directed sound assets with mute settings; no licensed third-party material |

### Art production contract

- Design an **asset family at once** to enforce consistent silhouette, shading, line weight, color palette, scale and padding. Generate a coherent set or sprite sheet, then inspect at real mobile sizes.
- Keep editable working source/art direction under \`design/\`, packaged Android \`drawable-nodpi\`/vector assets under \`app/src/main/res/\`. Use WebP/PNG with transparent alpha for raster; VectorDrawable for small path icons. Avoid raw huge 4K textures for tiny UI.
- Each asset has a stable semantic ID, purpose, ownership/source, usage permissions, packaging size, test screenshot and SHA-256 (as in Sudoku Woodland art manifest).
- Do not put unreleased original artwork, external copyrighted images, non-redistributable fonts or binary signing keys in public repos without verifying permission. Never share font binaries in chat.
- Create feature-specific art review issue when needed; unapproved generated art is prototype-only and should not silently replace approved player character assets.
- On underpowered devices, static procedural fallback is mandatory—effects shouldn't cause frame drops or stall drag.

## Acceptance and stop gates

**Implementation/testable source:**
- [ ] Pure Kotlin snap logic, explicit direction lock and endpoint validation; edge/diagonal/reverse/JVM coverage.
- [ ] Drag visual and found paths align with exact target positions (no false words).
- [ ] No parent scroll interference during board drag; tap endpoint input still works.
- [ ] Board prioritized on screen; theme/difficulty moved out of active board layout.
- [ ] Target list directly beneath board, usable without scrolling board off-screen.
- [ ] Light status bar icons replaced by dark-on-light system presentation; brand colors replace stray lavender.
- [ ] Haptic/sound settings exposed without claiming durable persistence.
- [ ] Basic owned-free procedural highlight design ready for future cosmetics.
- [ ] Build, tests, lint, UI-test compilation, APK identity and checksum captured.

**Physical acceptance remains separate:**
- [ ] Owner verifies slow/fast horizontal, vertical, backward and diagonal finger drags and confirms improvement.
- [ ] Test tap-first/tap-last, crossing targets, overlapping marker colors, invalid/canceled drag.
- [ ] Compare Easy/Hard, narrow/landscape/large text, screen-reader fallback needs.
- [ ] If haptics too strong/noisy, reduce before moving to monetization or further polish.
- [ ] Confirm no regression to Sudoku, saved data or Android installation identity.
- [ ] Keep source-backed QA receipt and rollback checkpoint; don't merge stacked PR early.

## Roadmap integration

This is **WS-02B**, placed between WS-02 CI-green prototype and WS-03 save/progression. It does not replace WS-03 through WS-08. Prioritize tactile and layout fixes in this pass; move unneeded art work and full reward economy to WS-04/07. Open issue [#5](https://github.com/PlayerEden/GridMates-Word-Search/issues/5) remains a source of concrete phone observations.

**Open choices:** exact marker opacity/colors, future collectible highlight style lineups, whether haptics click every cell or only direction lock/word completion, exact board/list height split across devices. All require real-device feedback.
