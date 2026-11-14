# WS16BM — Main-menu visual controls reused inside the Library

**Date:** 2026-11-14. **Status at first write:** v72 source committed / Android QA & independent art/release jobs running / on-phone visual approval OPEN. This is a visual unification pass, not a gameplay update.

## Why

After installing WS16BL v71 on Android and sharing all three bookcase screenshots, the owner said the button **animations feel good** but the dark walnut/brass control art looks unrelated to Home's illustrated cream, green and gold UI. Direction: the Library remains a visually special carved-walnut scene, but **functional UI uses the shared GridMates visual language across the whole app**. Keep mounted walnut pager rail, title and all book art. Reuse established UI components rather than paint a third bespoke style.

## Actual source of truth

We inspected both source families before editing:
- `GridMatesIllustratedFrames.kt` owns the actual Home `GridMatesIllustratedFrame` recipe: cream-paper gradient, antique-gold double outlines, small gilded corner flourishes, and native semantics/click content. Its previously private `DrawScope.drawGridMatesPaperFrame` painter is now `internal` and reusable as-is from the Library, so future shared styling refinements propagate without duplicate drawing code.
- `GridMatesTheme.kt`: `Forest` dark green, `Paper` off-white, `Gold` antique gold, `StorybookTokens.warmPaper` and bundled menu/UI font roles.
- `Ws16HomeHeader.kt` and `Ws15BackHeader.kt`: native 24dp forest-green navigation/menu/options icon vocabulary. Important semantic distinction: the Library's upper-right button invokes **artwork options**, not the Home's global menu. Its icon remains an **ellipsis** rather than a misleading three-line global-menu glyph.
- `WS16BICabinetChrome.kt` before this pass: a separate dark wood mini-hardware renderer with a brass icon, not the shared Home component. Preserved its proven v71 press/release behavior and 54dp native hit target.

## WS16BM implementation

1. `GridMatesIllustratedFrames.kt`: change the **same** paper/gold DrawScope painter from private to internal. No changes to Home visuals or API behavior.
2. `WS16BICabinetChrome.kt`: existing 54dp click/semantics and 115ms press animation are unchanged. Its 44dp moving face draws the **actual shared `drawGridMatesPaperFrame(GridMatesFrameVariant.MEDIUM)`**, plus a soft walnut-mounted contact shadow (background only). All icons are 24dp native, dark-`Forest` green; Back/Prev/Next use the existing stroke style, and Art Options uses the shared green three-dot ellipsis. Disabled behavior and reduced-motion remain. Both portrait and compact/landscape Library now route to the same paper control family; old AZ hardware code is left untouched for archived QA/fallback work.
3. `WS16BKBookcasePager.kt`: walnut rail source bitmap, navigation semantics, real pages, future volumes and 165ms active-dot response unchanged. Page indicators now use soft sage inactive, cream-paper selected, and `Gold` ring from the shared app palette. Still a sliding 7-dot viewport with left/right disabled at ends.
4. `test_ws16bm_shared_controls.py`: source-accurate tests assert the **same function** renders Home and Library, green native 24dp icons, correct options semantic distinction, shared dot palette, existing 54dp hit/115ms press, 165ms dot animation, exact real-book paging and unchanged QA package/signature. Existing WS16BL motion tests now verify reuse instead of dark-brass glints. Both CI workflows invoke the new suite.
5. QA version **72**, `0.9.28-shared-gridmates-controls`, same existing `com.playereden.gridmates.wordsearch.qa` package and public QA signing certificate. Intended install directly over v71 without uninstalling or clearing app progress.

**No new image files** are generated for these control changes. All existing cabinet/shelves/real book covers and decorative bindings are unchanged, and prior QA-only art must stay out of release. No changes to catalog, saved state, puzzle play, profile, collection, LAN, or future page count.

## GitHub QA and gate

- Draft [PR #131](https://github.com/PlayerEden/GridMates-Word-Search/pull/131), branch `feature/ws14b-style-anchor-and-art-direction-2026-11-08`, remains unmerged.
- Latest code/test source at first QA: [`72745e88ea944a195c696dd7366b52df2644a64c`](https://github.com/PlayerEden/GridMates-Word-Search/commit/72745e88ea944a195c696dd7366b52df2644a64c).
- Android QA [#43582240449](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/43582240449): IN PROGRESS at initial journal write.
- Independent art/release [#43582240576](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/43582240576): IN PROGRESS at initial journal write.
- **No verified v72 APK yet**. Verify both runs conclude SUCCESS on same code SHA; obtain the actual standard GitHub Android artifact, check versionCode 72, package and unchanged signer, inspect APK SHA and compare independently built artifact's decompressed entry content. Do not invent a direct APK or claim visual approval.
- Last owner-installed/tested v71: [standard GitHub APK ZIP #14006713081](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/43357902693/artifacts/14006713081), QA APK SHA `148513827e892e9e9b80f5c8b1cfbdf31d8a32c46e4922011a622b4d3d9a0eb1`. Preserve as comparison; do not uninstall/lose saves for rollback.

## Acceptance

Owner installs verified v72 over v71 and sends full first Library screenshot and one future bookcase. Check parchment/forest/gold controls match Home while still sitting naturally on walnut, both top and rail buttons have their v71 press/pop feel, options ellipsis remains distinct from the global menu, disabled arrows and three-dot paging work, real book previews/saved progress remain intact. Only then consider a broader app-wide audit for remaining legacy inconsistent controls. This source change itself does **not** claim to restyle every screen.
