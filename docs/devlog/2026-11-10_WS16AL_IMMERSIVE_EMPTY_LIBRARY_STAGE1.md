# WS16AL Stage 1 — Immersive Empty Library Screen, Portrait QA

**Date:** 2026-11-10 · **Status:** SOURCE INTEGRATED, exact-source CI pending at initial commit; owner phone approval pending. Draft PR #131, no production release.

## Owner feedback being addressed
The physical WS16AK screenshot showed the beautiful empty painted bookcase inserted suddenly below a large parchment header, a two-line QA control and a detached `3 books` count. The cabinet took only the remaining fragment of screen height. The owner authorized a screen-level redesign, possibly replacing assets, and asked for a more natural near-full-screen environment.

## This pass really changes
- `WS16ALImmersiveEmptyLibraryScene.kt`: *new actual Compose UI*. In QA **empty-art mode only**, render `Your Library` in a single compact accessible native header **without subtitle**, followed directly by the richly painted bookcase occupying all remaining height at roughly full screen width. No persistent two-line developer instruction row.
- The genuine `3 books` native label and tappable **Show books** comparison control now appear as two small inlaid dark-green/gold plaques near the bottom of the cabinet, not floating underneath its frame. Their exact visual placement remains a phone-review item; do not claim final.
- The new responsive painterly-band renderer reads the **original six ws16ak PNGs already source-pinned and packaged in debug**. It leaves the crown, middle/low shelf ornaments and base at their width-relative vertical scale. The upper/lower carved pillar ends are split into top/middle/bottom slices; only visually quieter middle wood segments take the extra height. No fake procedural wood, new image-generation shortcut, or flipped Home shelf.
- `BookcaseBookshelfScreen.kt` now chooses this immersive empty QA view when `fieldNotesArt && emptyCabinetPreview`, and **Show books** returns to the existing tested WS16AI live-book/paging implementation unchanged. This is still an **empty-cabinet composition approval gate**; live spines are NOT yet layered onto the new shelf contact planes. 
- Existing preview options, Back, real catalog progress, swipe pages, old wooden cabinet, and spine → full-cover details → View pages remain reachable on the fallback branch. No changes to content IDs, page generation, saved puzzle data, Home or payments.

## CI / acceptance
- New `tools/asset_pipeline/test_ws16al_immersive_shell.py` tests all six actual source-derived wood bands, 1:1 crop+reconstruction of carved end caps, responsive height budgets for 320×610 / 360×655 / 360×760 / 412×725, and checks the QA-only scene vs existing live bookcase fallback.
- Both GitHub Actions jobs regenerate WS16AK from its pinned 1024×1536 source, execute these tests, assemble Android debug and check zero debug-only artwork in release. **Exact-commit Android/art success and packed APK audit not yet available at document creation**.
- Screen QA: real Android 320/360/412/tall portrait, no warped gold carving, cream header flows naturally into crown, base plaques readable and don't hide leaf carving, native safe areas intact, Show books and Back functional, gallery modal after fallback preserved.
- Limit: This is a **layout integration of real existing painted parts**, not a claim that the entire cabinet has been repainted. If user still dislikes edges/overall visual, proceed to WS16AM genuine re-illustrated frame/backing assets using actual full-screen composition evidence. Do not blindly enlarge WS16AK image into a stretched PNG or prematurely force live book overlay.

## Rollback
Prior WS16AK dual-green Android source `ac807009` and APK, original WS16AI live bookcase art, and Home shelf retained. New files only loaded in debug art mode and PR #131 remains draft.
