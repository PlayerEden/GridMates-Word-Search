# WS-16AD — Master Book Factory, Stage 1 (November 10, 2026)
> **Status:** ENGINEERING SOURCE COMMITTED IN DRAFT; EXACT-SOURCE ANDROID/ART CI PENDING, owner handset art approval PENDING. No finalized corrected artwork or new published books claimed.

## Who, what, why and previous checkpoint
- Owner intends many repeatable physical puzzle books, requests clean straight edges, independent substantial spines, a few unplayable future-volume placeholders and a reusable template. Painted WS16AB shelf visually accepted on owner's actual handset.
- Live start of pass was PR #131 `d4495f5dc815864acebcf2e651601959bea280f1`. Source-only first commit `788fed3e46b1a7d7bee9f8fe9c71af44de5c2c27` added the reproducible factory/masters; this follow-up commits actual Android and QA integration.
- Previous verified install [WS16AB Android #42906649312](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42906649312) + [art #42906649069](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42906649069), [GitHub Android APK artifact #13805212782](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42906649312/artifacts/13805212782). No new app installs or signers changed by this pass.

## Actual implementation
- `tools/asset_pipeline/build_ws16ad_book_factory.py` produces **12 real original-image-derived or editable-master-based QA exports**, **not a flat screenshot**: 3 normalized original illustrated covers, 3 independent spines, plus 3 blank books each with cover and separate spine.
- Publisher geometry: visual book shape locked inside **480×640 transparent cover** at x12–468/y65–574 (±2); spine **128×640** at x14–114/y51–589 (±2). Normalizes varying original alpha margins, performs slight alpha exterior matte cleanup and retains all theme artwork/content/progress IDs.
- Reuses the verified existing source `design/visual-system/masters/ws14-generated-asset-sheet-original.png` (SHA256 `9c452ba81352b9a3bd08496de5e8cf8d8ea9ed188af540d4db2b2d558c41808e`) and its six generated WS14 inputs; no V1 overwrites. Six new separate editable SVG masters under `design/visual-system/masters/ws16ad/` define linen, night and burgundy blank covers/spines. No placeholders added to playable catalog or displayed as fake available books.
- Each exported file and source receives SHA256 in `app/src/debug/assets/ws16ad/manifest.json`. `BookArt.kt` reads new geometry-proof sprites first only when `fieldNotes` on debug, and falls back to original WS14 and WS12 assets. Gameplay, library IDs, profile, scores, saves, LAN, approved shelf unchanged.
- Both standard Android and independent art/release CI run factory/test, then validate actual packaged SHA byte checks. Release art isolation assertion forbids new candidate assets in the release APK.
- **Local isolated test:** 4/4 tests passed on six real WS14 image bytes extracted from verified prior APK; standalone output manifest hash-check passed for 13 files. This is *not* an Android build or real phone test.
- [Versioned book factory contract](../design/visual-system/WS16AD_BOOK_FACTORY_CONTRACT.md).

## Remaining art and product gates
- Existing baked-in **®** in old WS14 cover illustrations is **NOT corrected** by geometry normalizing; new owner-reviewed truly edited V2 title/cover artwork is required. Don't claim rights/label correctness or call this publishable.
- Need compare white edge/fringe and consistent book pose against light parchment **and** dark background, confirm native contact on accepted walnut shelf and use at other device widths.
- Reuse blank assets when Full Library is later redesigned with clearly non-playable Coming Soon states, substantial tap-able spine regions and controlled height classes. No fake catalog IDs.
- This stage's first pushed factory commit caused a preliminary source-only push; final compiled source is the **follow-up integration commit**; fill its exact SHA and CI IDs below after green tests, not before.

## CI and APK evidence
- **CI**: waiting for final integration commit. Do not refer users to unfinished APK.
- **Rollback:** same WS16AB dual-green build above, no uninstall or data clear.
- **Next single action:** verify exact-source CI + new QA APK; then owner phone geometry comparison. Next visual art pass must use genuine regenerated/edited Woodland illustration to remove ®, followed by Space and Garden using same locked factory shape.
