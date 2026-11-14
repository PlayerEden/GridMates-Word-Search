# WS-16AO — Publisher Spine Master Rework and Dense Shelf Geometry

**Date:** November 10, 2026 · **Status:** Owner phone screenshot review, upcoming art-production pass; NOT implemented, NO APK in this stage.

## Why this pass

After WS16AN, the owner submitted two real Android screenshots of the illustrated Full Library: one with developer spacing/safe-area overlay enabled and one clean. They show the near-full-screen cabinet with three real themes (Woodland, Space, Garden), two anonymous blanks on the upper shelf, five blanks on the lower shelf, and `3 books` in small live text on the base.

**Confirmed by screenshots:** the previously opaque green book-count badge is gone; the count is now less intrusive and the gold inlay is visible. Book bottoms are closer to the shelves and have localized dark contact shadows. Crown, bay paneling and pillars remain visually cohesive and inside the portrait safe-area overlay. These screenshots establish rendered geometry, not confirmation of book taps, page navigation, responsive scaling on other devices or release acceptance.

**Still visibly not at the owner's bookcase goal:** the real spines and placeholders have substantial horizontal separations, especially the smaller lower-row placeholders; blank volumes are thin-looking and artistically much flatter than actual theme spines. Both rows use the 48dp/54dp touch-slot system, but actual art aspect and height make lower row narrow. The cabinet is good enough to freeze for this next art pass rather than spending another cycle on its dimensions.

## Root cause in the live code

`WS16AMInteractiveBookcaseScene.kt` derives rendered width via `artHeight * (104f / 542f)`, capped at `slot - 3.dp`, within independent 48dp (54dp high-font) touch slots. Shorter lower bay → shorter artHeight → markedly thinner rendered spine. Increasing the bitmap width by `FillBounds` alone would stretch letters, animals, emblems and leather grain. Dropping tap-slot width or letting clickable targets overlap is explicitly **not** acceptable.

## WS16AO production contract

1. Create a **new canonical thicker physical-spine master family** with intentionally designed cover depth and binding curvature (not a simple horizontal stretch). Version the new source masters, dimensions, transparent alpha silhouette and reproducible editor/export pipeline.
2. Build **all three current real books** (Woodland, Space, Garden) to that geometry. Keep their identity, existing approved cover illustration, theme icon, clear vertical title, readable `GridMates Word Search` lettering **without any unsupported ®**, and original content IDs.
3. Create/rebuild at least **three anonymous physical placeholder spine masters** (linen/green, night/navy, burgundy/plum), with the same construction/lighting as real books: natural leather grain, rounded caps, raised bands, subtle gilding, bevel, contact edges. Blank means **no fake theme title, pages, status, progress or clickable catalog ID**.
4. Prefer source masters that render visually **~43–46dp wide inside 48dp independent slots** on a typical screen (small ~2–5dp gap), but only when natural at its intended physical height. Avoid hardcoded pixel scaling that breaks on smaller/larger phones; retain `>=48dp` nonoverlapping interactive targets, `>=54dp` at high font setting. Different rows can use controlled standard/tall height classes; variation must not arbitrarily distort geometry.
5. Fix remaining edge alpha/white specks by testing transparent source against **cream, walnut and near-black** mats. Verify 1:1 physical silhoutte, top/bottom caps, posterization, and absence of halo before Android.
6. Keep actual book covers separate from spine sprites; Home full-cover carousel and all real-book selection/modal/progress logic unchanged. Any future Home coming-soon cover teaser needs its own **matching blank cover**, not a forced crop of a spine, and separate owner review.
7. Maintain catalog-based page count and automatically replace blanks with real new themes as actual books are added; no phantom book count or empty swipe pages.

## Visual acceptance and release gates

- Compare old vs new art in the **actual full-screen cabinet** (not isolated PNGs alone), on both shelves, including realistic contact and visible gaps.
- Inspect standard/tall and future volumes on 320dp, 360dp, 412dp, tall portrait and large fonts; verify independent touch regions, three real spine→full-cover dialogs, `View pages`, Back, and noninteractivity of all blanks.
- Commit source masters, full provenance manifest, deterministic generating script, alpha/edge/registration unit tests. Android QA and **independent art/release-isolation** workflows must pass exact same source commit. Match packaged APK source hashes and retain WS16AN proven APK for rollback.
- **No visual screenshot alone can confirm all interactions or production acceptance.**

## Scope control

Do not redo the WS16AK/AL cabinet, header or progress screen in this pass. Do not auto-publish future themes or change saved data. Leave current Home as fallback. Owner's latest screenshot is accepted as a *positive cabinet integration checkpoint*, **not** acceptance of current spine-family art.
