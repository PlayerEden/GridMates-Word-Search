# WS16AM — Owner Real-Phone Review and Interactive Painted Library Integration

**Date:** November 10, 2026 · **Status at initial source commit:** implementation and QA tests added, **Android/art CI pending**, owner phone review pending. Draft [PR #131](https://github.com/PlayerEden/GridMates-Word-Search/pull/131).

## Owner's new phone result: what actually improved

The owner provided a real Android screenshot of the WS16AL empty-cabinet layout. The compact Back / Your Library / overflow title immediately above the painted crown is now cohesive and almost full-screen. The carved cabinet fills the stage, the full pillar family is visible and the two empty recessed bays have working lighting and material depth. This is a favorable **screen-composition direction**, **not yet final owner art approval**.

Remaining visible flaw: **Show books** and **3 books** were separate large rectangular green/gold badges overlapping the base carving. The test was still an entirely **empty** cabinet and did not demonstrate that any real spine aligned to the new wooden shelf surfaces.

## Implemented WS16AM — real interactive painted cabinet, not another detached menu

- Added `WS16AMInteractiveBookcaseScene.kt` overlaying independent `BookcaseSpineArt` and anonymous, nonclickable `BookcaseFutureSpineArt` sprites **on top of the accepted WS16AK painted background** through `WS16ALResponsiveBookcaseBackground`. No flattening of the actual book cover or spine into the wood background. All previously approved physical book illustrations, identifiers and saved progress preserved.
- Real books remain clickable through `onBookSelected(book.id)` and use the **existing** full-cover chapter/progress modal and `View pages` route in `BookcaseBookshelfScreen.kt`. 48dp independent hit targets (54dp at high font scale), separate from physical artwork width, retained; blank volumes never get content IDs/actions.
- Geometry shared with the renderer: cabinet crown `246 * scale`, shelf lip `128 * scale`; upper/lower bay free space split `450/814` and `364/814` exactly as drawn. Books rest about 5dp above the boundaries between recess and carved shelf surfaces; small ~3dp contact shade added. Upper and lower spines keep their original 104:542 physical binding aspect, so there is a known risk of slightly larger visible gaps on lower row. **Evaluate on real device before approval.**
- Horizontally paged complete cabinets (not fake content): capacity derived from measured width, **safe interior width = screen width minus 94dp** and 48/54dp tap slot. Real books fill first; anonymous future spines occupy unused positions. Second page only when real catalog overflows, page counter shown only for actual multi-page catalogs. Need real multi-page fixture testing.
- QA startup now shows **interactive WS16AM painted cabinet**; the three-dot header menu allows returning to **Empty woodwork** or the known-good **Previous bookcase**. Older WS16AI art and selection flow are intact as independent fallback. Empty QA comparison now returns to the new interactive view.
- Removed the large `Show books` button from the carved base; kept only a single small live `N books` native plaque on the left. QA switches are in the menu, not drawn as fake cabinet controls. No changes to Home or actual player book data.
- Both GitHub Actions pipelines now run `test_ws16am_immersive_bookcase.py` alongside the WS16AL/WS16AK source geometry tests, and still verify original art/release isolation.

## Current QA gates and follow-up

- **Not yet verified at source commit:** Android compile/lint, second independent art/release build, APK integrity, owner physical selection after install. No APK offered without success.
- Must verify 320/360/412dp and tall portrait composition, physical book contact with exact shelf ledge, no spine cutoffs/overlays, base book count unobtrusive, 3-dot QA options, onSelect→full cover→View pages/Back, new version vs Previous bookcase fallback. No claims of full device validation until user captures screenshots.
- Remaining visual work: placeholder art is still repetitive; naturally varied leather and consistent publisher-grade binding artwork deserves separate next asset revision after new cabinet placement is reviewed. Do not silently add new real theme books.

## Safeguard

The working WS16AL candidate APK and earlier WS16AI are retained. Development branch PR #131 remains draft. Source/CI green alone never means final publication or author approval.

## Final exact-source QA receipt

- **Compiled source:** [`f40605247dff5d50d32d7b8bc3c1a068c01b0309`](https://github.com/PlayerEden/GridMates-Word-Search/commit/f40605247dff5d50d32d7b8bc3c1a068c01b0309).
- **Android QA SUCCESS:** [run #42951462605](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42951462605), [APK ZIP artifact #13826020052](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42951462605/artifacts/13826020052). Sign into GitHub to download; retention through **December 11, 2026**.
- **Independent artwork/debug/release QA SUCCESS:** [run #42951462688](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42951462688), [independent artifact #13825351172](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42951462688/artifacts/13825351172), same 30-day retention.
- Extracted normal Android `app-debug.apk` is **23,827,752 bytes**, SHA256 **`18ce273b39879542ca49dc538fdcbd7d9317e8478543ab3048815ea2c85aa740`**. Two ZIP artifacts integrity-tested, two extracted APK archives integrity-tested.
- **All 311 internal APK entries have the exact same uncompressed bytes** between standard and independent Android builds. This is stronger than comparing their outer archive hashes and establishes exact packed contents.
- `assets/ws16ak/manifest.json` and all six real painted source bands are present; SHA256 of each image independently matched its manifest entry. Independent release-art isolation passed. 
- Python source-geometry/overlay structural tests passed in both workflows, plus Android full compilation, unit tests and lint. **No real-device tap or layout acceptance claimed** for this new composite build.
- QA phone review: check actual shelf contact and gaps of first and second rows, no lower shelf clipping, 5 visible spine slots per row on typical 360dp device, real book selection → full cover → View pages and Back, blanks non-tappable, header 3-dot Empty woodwork and Previous bookcase switching, count as real books only, swipe page behavior when catalog grows. Previous WS16AL APK remains rollback; do not uninstall/delete save data.
- Exact-source QA build is ready for owner test, but draft PR remains unmerged, production artwork remains unapproved.

