# WS16AR — True shelf-free modular Library architecture
**Owner correction:** November 10, 2026 · **Status:** real five-piece original art candidate generated and validated outside repository; no binary image import, Android activation, CI APK, or phone approval.

## Decision — supersedes WS16AQ scenic-screen approach
The Library bookcase must be **architectural scaffolding**, not a complete painted set with fixed shelves. The owner wants the existing painted **Home shelf** sprite(s) independently positioned at arbitrary vertical intervals based on book rows and phone dimensions. **Do not bake any shelf, row, book, spine, text or real button into the architectural painting.** There are exactly five separately replaceable architectural art assets:
1. `top_crown.png` — crown with an **empty dark-wood title plaque** and recessed warm lighting.
2. `left_pilaster.png` — full-height ornate left frame with ivy.
3. `right_pilaster.png` — full-height ornate right frame with ivy.
4. `recessed_backboard.png` — **continuous empty** paneled wood where bookcase shelves can be freely positioned.
5. `bottom_base.png` — carved wood plinth with an **empty parchment-like count/page plaque**.

The bookcase should extend to every visible edge, including behind OS bars if platform insets permit. No detached parchment header/footer or side margins. Leave space above/below actual books for text, navigation and later art buttons; this must be responsive, not screenshot-positioned.

## Cohesion with the real Home artwork
Existing GitHub source `app/src/debug/assets/ws16ab/painted-shelf-v1.webp` is **the authoritative shelf sprite**. It is 720×149, source-pinned in `build_ws16ai_bookcase.py` (SHA256 `c7680df007c7fa9bab3d2ccbeefd5b12a727229db64c41ddb7dee7bc529d7895`). Inspect real Home-on-phone for material/lighting/color before approving the new five-part walnut frame. Existing painterly gold-leaf shelf ornament is the style anchor. New art must not replace or repaint it by default; it must compose convincingly with it.

## Actual original art candidate created during chat
Local archive: `WS16AR_Shelf_Free_Bookcase_Kit.zip`. Source master `shelf_free_bookcase_master.png`, 1017×1547 RGBA output derived from new original 1017×1547 generated RGB image, source file SHA256 `f2a0a037d2c4c6ad70ad20294d963cdccedd3a82014010ad8b20a93582c2dddc`. The empty illustrated master has no horizontal ledges, books, lettering or control glyphs. The top blank wood plaque and bottom empty parchment plaque are decoration only—real text is native.

The ZIP includes `assets/` (the five independent PNG crops), `manifest.json` with per-part SHA256/source rectangles, `README.md`, `test_kit.py`, original master and full-resolution/phone-shaped previews. **Five independent crops reassemble the master pixel-exactly**; local Python hash/dimension/assembly test passed. Exact crops: top y=0..325, base y=1300..1547, left x=0..145, back x=145..872, right x=872..1017 for middle y=325..1300. A separate preview contains shelf guide lines only and is explicitly NOT a shipping asset.

**BINARY STATUS:** The image archive is available in the ChatGPT conversation only and does NOT exist in GitHub; do not claim it as pinned CI source until actual binaries are imported. Do not treat images in this branch as proof of this new pass. No APK has been made.

## Native layering and acceptance contract
- z-order: backboard → existing independently positionable `ws16ab` Home shelf art at live measured y-positions → genuine active/future spine sprites → side pilasters → crown/base → native interactive UI in protected plaque zones.
- Preserve existing real catalog page count, correct navigation, spine → book detail → View pages, ≥48dp per-book tap areas, filler-spine noninteractivity, local saves, progress, game logic, Home layout, LAN and source/build rollback.
- On 320/360/412dp, tall, landscape and large-font layouts, the dynamically selected number of shelves and spacing should fit the number of books. Use measured geometry, not artist fixed bookshelf levels. Keep books physically contacting the movable ledges; no floating gaps.
- Preserve crown/base relief and side-carved capitals; do not nonuniformly stretch into blurry ornaments. The middle backboard can expand/crop or repeat as necessary. Ensure side pilasters don't expose joins.
- Reserve actual safe tappable areas in top crown for Back/Menu and game title, and base for live catalog count/pagination. No baked text.
- **Later separate UI asset family**: small gold-on-dark triangular arrows, overflow/back controls, selected/unselected page dots, plaque chrome and maybe micro-icons, inspired by owner supplied screenshot. They should be scalable native/vector or hash-pinned transparent images with real native 48dp touch targets. **Do not expand this milestone into button production yet.**
- Source-control the master, all five art images, provenance/test and manifest, then integrate as QA-only, run Android and independent debug/release art isolation workflows, verify APK asset hashes, and obtain owner phone screenshots + functional taps before any release approval or merge.

## Scope of this documented pass
Genuinely generated a **new, shelf-free architectural art master** (correcting WS16AQ's ambiguity) and a locally validated five-part candidate archive, inspected the real Home shelf sprite from GitHub, and recorded the future controls direction. **No app code / GitHub PNG / APK work is implied.** PR #131 remains draft; preserve previously verified source and QA fallback.
