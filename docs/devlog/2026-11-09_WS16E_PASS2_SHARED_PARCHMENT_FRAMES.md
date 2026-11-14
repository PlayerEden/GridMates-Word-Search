# WS-16E Pass 2 — Reusable GridMates parchment frames on Word Search Home

**Date:** November 9, 2026. **Branch:** feature/ws14b-style-anchor-and-art-direction-2026-11-08. **PR:** #131 (draft).  
**Owner decision:** Keep the home background plain or nearly flat. Cozy library/home atmosphere should come from reusable parchment controls, book/shelf objects, light edging and shared GridMates identity, not a different painted-room background for every game.

## Before this pass

The owner reviewed WS-16E first shelf photos: the newly carved walnut plank and original Woodland/Space covers read well. The app already had a working book carousel, Full Shelf + Collection, Continue, Daily Puzzle, Together, QA-only Quick Play preview, Custom and My Puzzles; Profile/Menu in top-right and **no bottom nav**. Most controls were still very plain white Material3 shapes beside the rich books. The owner wants illustrated **templates with live native text**, reusable in Sudoku and future Crossword when those games explicitly opt in.

## Exact gameplay/art source

- `0880275ebabc98cc325677efc5cf3880e3dfc2bc`: adds `GridMatesIllustratedFrames.kt`, the shared modular native Compose renderer with **MEDIUM**, **FEATURE**, **PANEL** and **PRIMARY_PILL** frame variants; a native-text `GridMatesIllustratedPill` and native-link `GridMatesIllustratedLink`. This has real click/focus semantics, pressed feedback, enabled state, independent icon/text slots, and decorative Canvas frames (quiet parchment gradient, subtle antique-gold border and corner engraving; green/gold primary pill). No full-screen texture.
- `47ae4f479af0e8f754a55204bcc1b89d1da0585a`: integrates that common renderer in `Ws16DLibraryShortcuts.kt` (Full Shelf + Collection), `Ws16BHomeActions.kt` (Daily/Together, Quick Play QA-only nonclickable preview, compact Menu and native-text Custom/My Puzzles links), and `Ws16HomeScreen.kt` (Continue card with green Play/New pill, existing dynamic size/word-count text). Original `Ws16BActionArt` and gallery/shelf subject icons remain separate, swapping icons/labels does not require remaking a button.
- Adds **six independent original editable SVG source masters** under `design/visual-system/modular/masters/shared/`: `gm-frame-medium-v1.svg`, `gm-frame-feature-v1.svg`, `gm-frame-panel-v1.svg`, `gm-pill-primary-v1.svg`, `gm-ornament-corner-v1.svg`, `gm-icon-plate-v1.svg`. The master SVGs are **not** bitmap button overlays or compiled Android SVG resources; Canvas directly renders corresponding geometry. They are reusable family concepts, not yet copied to Sudoku or Crossword.
- Adds `Ws16CarouselHomeAndroidTest.kt` source assertions for Home destinations and text while framed. No new puzzle generator, no change to Solo/book/Collection save IDs, no QA-only books promoted to production. Existing no-footer navigation stays.

## Distinctions and QA gates

- **Status at log creation:** new frames committed in source; Android and art/release exact-source CI initially in progress. Not yet owner phone-reviewed or release-approved. The two initial CI runs from the *first file-only commit* were superseded/cancelled by the consolidated source commit, so use only the corrected **`47ae4f47`** run evidence.
- Verify standard Android QA (core/app unit tests, lint, Android instrumentation **source** compilation, debug APK) and QA-art/release-isolation **both success** on `47ae4f47`. Do not claim the compiled Android test classes were actually executed on the user's phone or emulator.
- Owner QA: visually compare with WS-16D/WS-16E shelf baseline at 320/360/393/411dp and large font; check ornate frames are pleasing without excessive gold, Daily/Together titles don't clip, Continue doesn't become too tall, Quick Play is visibly **coming soon** and not active, buttons still act, Profile/Menu/Collection/Back work; note how far Custom/My Puzzles sit below the fold.
- Current proven fallback: [WS-16E shelf-only Android ZIP #13720434658](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42724381974/artifacts/13720434658) (APK SHA256 `a71680c1c3b476f25727a33efcf2ff657d0bcacf5377b897666b7b549edc7d53`).
- Keep PR #131 draft until owner reviews. Back up any saved progress before uninstalling an older APK after signing conflicts.

## Next after phone review

Refine actual frame artwork and spacing based on handset screenshots; then update Word Search-specific icon subjects and minimal optional corner/edge accents. Avoid creating a giant one-piece illustrated background. The separate WS-17 Quick Play mixed-word generator and per-book collection reward catalog remain independently planned work, not part of this asset pass.


## Corrected first compile issue

First consolidated Android source `47ae4f479af0e8f754a55204bcc1b89d1da0585a` had a Kotlin compile failure in `GridMatesIllustratedFrames.kt`: an unnecessary invalid top-level import of the `BoxScope.matchParentSize` extension. This was removed in correction commit `227b55e6a0c8a97010a591deee59b0e034141d8a`, without changing functionality or artwork. The earlier [Android run #42730780471](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42730780471) failed and is **not a usable APK**. The initial separate art run was superseded/cancelled. Corrected exact-source runs are [Android #42731226177](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42731226177) and [Art #42731226186](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42731226186), to be verified before handoff.

## Final verified WS-16E Pass 2 QA receipt

**Both exact-source checks passed at corrected source `227b55e6a0c8a97010a591deee59b0e034141d8a`**, independent of subsequent documentation-only commits.

- [Android QA #42731226177](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42731226177): **SUCCESS** (unit tests, lint, Android test *source compilation*, APK assembly; not an on-phone test).
- [Art/release #42731226186](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42731226186): **SUCCESS** (six QA-only book cover/spine images absent from release, 18+18 portrait source images/36 matched native PNGs and three verified motif assets release isolated).
- [Owner QA APK ZIP #13724199339](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42731226177/artifacts/13724199339), contains `app-debug.apk`. Historic artifact name includes Woodland; app contains complete WS-16E Pass 2.
- APK SHA-256: `d4535ecbc312afac18ddd23b53c0f80d03ee3f0f14847fb0bf40185e9ccf6a6e`.
- Artifact ZIP SHA-256: `462b362d966c863a320cd6d4db32226b233e66fb412b19f8bb72f812c3f66e7c`; 13,969,819 bytes.
- QA applicationId `com.playereden.gridmates.wordsearch.qa`, versionCode 57, versionName `0.9.13-visual-kit-foundation-qa`. Export backup before uninstall or signing-conflict fixes.
- Master SVG hashes: [GM_SHARED source register](../../design/visual-system/modular/gm-shared-frame-source-register.json), **owner art review pending**. No SVG direct Android drawable claim; runtime uses analogous Canvas rendering.
- PR #131 remains draft. New framed Home not yet phone-accepted. Obtain top/bottom/Menu screenshots and test tap/Back before merging.
