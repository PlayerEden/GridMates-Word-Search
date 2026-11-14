# WS-16AB — Actual single-image painted shelf integration (2026-11-10)

## Goal and owner evidence
Owner's **real WS16AA phone screenshots** show a stable Woodland/Space/Garden carousel and successful saved Solo label, but the six-piece vector shelf still looks too flat. Owner explicitly requested to use the **actual generated rich painted shelf image** instead of approximating it with gradients. Previous V1 original reference shelf was generated this conversation; verified same generated source file in local work context.

## Source and new art
- Generated source: `ornate_walnut_shelf_with_golden_oak_motif.png` originally 2172×724 with transparent alpha; SHA256 `d381a6fdbb21f4d1929a6cab275f5191de29d2692e62c42ef4350bcbb090a1eb`.
- Optimized cutout for mobile QA: `app/src/debug/assets/ws16ab/painted-shelf-v1.webp`, 720×149, 16,310 bytes, alpha preserved, SHA256 `c7680df007c7fa9bab3d2ccbeefd5b12a727229db64c41ddb7dee7bc529d7895` / Git object `cc08af2e2773d75f7bd840bc7c665982c10874df`.
- This is the actual generated painting, compressed to a transparent WebP for direct Android decoding; NOT a recreation from SVG and not a whole-screen screenshot. The GitHub repo includes the optimized review asset; full-resolution original remains a separate conversation asset, not versioned into repo.
- `design/visual-system/ws16ab-painted-shelf-v1.json` pins hashes, dimensions and non-release approval.
- A single QA-only image is displayed in `Ws16GShelfArtwork.kt` before the previous WS16AA/WS16G/Canvas fallback. New illustrated shelf rendered 50dp deep with +8dp placement compensation to retain the same top/book resting position as existing 42dp shelf. No carousel, book, saved game, menu routing or font asset changes.
- Existing subtle parchment tooth visibility is increased from .72 to .86 alpha; prior source and release fallback unchanged.
- `verify_ws16ab_apk.py` checks immutable image bytes, WebP alpha and dimensions, APK debug packaging with exact hash, and zero WS16AB assets in release. `test_ws16ab_shelf.py` guards fallback, carousel, paper and visual scope.
- Both workflows are modified to validate and package the candidate; owner_approved=false, release_approved=false.

## Build and QA receipt
**FULLY VERIFIED for exact compiled source commit [`1b1bf49c`](https://github.com/PlayerEden/GridMates-Word-Search/commit/1b1bf49c033db5e02a4f5a68194e41d82ea1ab92).** Both [Android QA #42906649312](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42906649312) and independent [art/release QA #42906649069](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42906649069) **SUCCESS**. [Preferred GitHub Android artifact #13805212782](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42906649312/artifacts/13805212782) ZIP includes `app-debug.apk` (15,775,431 bytes, SHA-256 `0ebde7e7a04e897ffe636d1fa35f10e3d85565c92b31142cafb6ed9f51f3bb52`). Independent [art artifact #13806440061](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42906649069/artifacts/13806440061) APK SHA-256 `fe518cfddbbd0425ea6fe1ebb4cb601737bcff90a03247d29fd24e3b78747ef2`. **All 268 unpacked APK entries match byte for byte** across the two builds; outer ZIP container signatures differ, which is harmless. The packaged shelf has exact 16,310-byte source hash `c7680df007c7fa9bab3d2ccbeefd5b12a727229db64c41ddb7dee7bc529d7895`; candidate art is absent in release. QA phone acceptance still pending. Original source SHA/rollback preserved.

Build reference after this receipt: [PR #131](https://github.com/PlayerEden/GridMates-Word-Search/pull/131) Android unit/lint/AndroidTest compilation & independent debug/release art-isolation, retrieve artifact SHA256, and verify owner-phone appearance. Previous WS16AA dual-green source `b8adce02`, [Android #42898700114](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42898700114) and [art #42898700175](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42898700175) remain fallback. Do not merge or tell owner to clear app data.

## Owner acceptance
- Phone screenshots overlay OFF Woodland/Space/Garden, then optional overlay ON to confirm bounds.
- Does the real-painted top, walnut fascia, carvings, corbels, and natural contact shadow finally feel three-dimensional? Compare directly with owner's reference.
- Look for transparency, dark halos, edge fringe, stretch at different device sizes, paper noise, footer overlap or book baseline shifts.
- Test carousel swipes, book tapping, whole Continue Solo card, Back and progress. Approve art only after real phone evidence; if needed, refine image/render dimensions or future nine-slice, not another Canvas mockup.
