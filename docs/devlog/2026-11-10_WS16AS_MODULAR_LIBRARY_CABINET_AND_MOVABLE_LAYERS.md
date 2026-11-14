# WS16AS — Real five-part shelf-free, plaque-free Library concept and movable-layer sandbox
**Date:** 2026-11-10 (Kentucky local) · **Owner direction:** keep all physical shelves and plaques separate from the cabinet art · **State:** generated and tested as downloadable conversation artifacts; NOT imported as GitHub image binaries, Android code or APK.

## What was actually created
An independent, rich amber/walnut **painterly UI asset sheet** was generated, with a left-hand empty architectural cabinet and distinct separate reusable plaque concepts on the right. The actual versioned **WS16AS_Modular_Library_Art_Kit.zip** exists in the current ChatGPT conversation (SHA256 `db91d99ed5e2f628dcd1e7244f64f1c30e891f57b891324a5021d797a258d8ee`). **This sandbox archive is NOT a GitHub Actions artifact, GitHub image upload, or Android APK.**

The source painting `original_candidate_sheet.png` is 1017×1547 (SHA256 `c1e42abaaef55b05aba8493637e3bc702376fd4078bd07699c504f1a21941264`). Exactly five image crops taken ONLY from the left architectural area reconstruct the original 602×1120-pixel empty cabinet **pixel-for-pixel**, tested with Pillow byte comparison:
- `assets/shell/top_crown.png` — 602×198; no title plaque.
- `assets/shell/left_pilaster.png` — 126×814.
- `assets/shell/recessed_backboard.png` — 356×814, continuous wood.
- `assets/shell/right_pilaster.png` — 120×814.
- `assets/shell/bottom_base.png` — 602×108; no parchment/count plaque.

Five independently extracted textless alpha-cut plaque **candidates**, not attached to the frame, live under `assets/separate_placard_candidates/`: parchment_arch, carved_walnut, parchment_leaf, forest_green and parchment_small. Their edges and alpha need production review, and no permanent positioning is assumed.

All source/asset SHA256 references, crop geometry and QA flags are recorded in the local `manifest.json`. `test_kit.py` verified source/extracted hashes, dimensions, exact 5-part assembly, nontransparent/transparent alpha on each separate plaque, and the absence of embedded books, shelves, labels and plaques in the architectural shell.

## Genuine movable-layer demonstration
`web_preview/index.html` is a local stand-alone, offline prototype with draggable shelves, add/remove/hide shelf controls, and draggable on/off separate top/bottom plaques, demonstrating that bookcase architecture need not change if shelf positions or plaque locations change. `previews/` contains empty phone-format cabinet examples (320×650, 360×760, 412×860), a one-, two- and three-shelf comparison, and a separate-placard overlay comparison.

**Crucial:** The shelf used in the prototype is only a crop from the concept asset sheet and is under `assets/proof_only/`. It is NOT the game's approved real Home shelf. The actual game must reuse `app/src/debug/assets/ws16ab/painted-shelf-v1.webp` and the interactive per-book spines, page logic and 48dp touch slots.

## Explicit acceptance constraints
1. Full-bleed cabinet art, no detached parchment margins/header/footer; preserve Android safe insets for tappable overlay controls.
2. All horizontal shelf ledges from existing Home (or separate successor sprites), independently laid out from measured book rows. Real books stay independent, tappable, and sized appropriately; nonplayable filler books are not counted or clickable.
3. Title placards, status placards, arrows and pagination dots become **separate reusable UI sprites** with live native text/accessibility, not painted into the cabinet. User explicitly wants all layout/position choices changeable later, not fixed into the art. Five plaques here are exploratory crops, not a final reusable button kit.
4. Keep the painterly amber/brown/gold/green style coherent with the actual Home on phone. The generated crest includes a small decorative four-square emblem; confirm with owner whether this permanent ornament is desired before finalizing the shell.
5. To package for Android: copy actual source master + five shell assets + optional approved plaque sprites into versioned GitHub paths; regenerate and validate checksums; integrate as QA-only optional native Compose scene with fallback; test 320/360/412dp and high font scales; run same-SHA Android QA + independent debug/release art isolation and inspect APK; then owner physical-phone screenshots/taps.
6. Keep the current PR #131 **draft**, don't change Home/saves/LAN/book catalog, and don't call the art Play-ready or claim an APK.

## Changes in GitHub for this pass
This entry documents the genuinely created local bundle and integration requirements. **No PNGs have been uploaded to GitHub and no new Android build was run.** The user can inspect the conversation kit and interactive concept locally. Next implementation action: import real binary art and hook a reversible native renderer to the existing working Shelf code, then run dual CI before phone testing.
