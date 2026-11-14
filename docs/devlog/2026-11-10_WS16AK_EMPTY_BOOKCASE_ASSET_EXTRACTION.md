# WS-16AK — Approved Painted Empty Bookcase: Art Extraction and Build Scaffold
**Date:** 2026-11-10 · **Status:** modular image extraction completed locally, QA source renderer scaffold committed; **art files not yet committed or compiled into the Android app**. No new APK claimed.

## Owner direction
Owner selected the illustrated **empty** walnut bookcase (1024×1536) with carved crown and open-book emblem, green-inset pilasters, deep wood backing, two broad shelf bays, matching gold ornamental ledges and ivy. The explicit priority is **background first**, then add independent live book sprites. Replaces the flawed WS16AI use of flipped Home shelf pixels as a crown. Do not composite a flattened screenshot containing text, tappable books or controls into the background.

## Genuine completed asset work (offline master, no GitHub binary transfer yet)
- Full-quality **immutable original painting** `approved_bookcase_master_1024x1536.png` SHA256 `e2f8fcf3732698f4657a9e01a0996a34be72f71d5387b14e3d5be4b0d6d0f606`. It is **not yet in the GitHub repository**; avoid suggesting CI can rebuild it until imported.
- Six **real raster bands**, cropped from the approved master with no flipped/rotated shelf replacements: `crown.png`, `upper_bay.png`, `middle_shelf.png`, `lower_bay.png`, `lower_shelf.png`, `base.png`.
- Six further **real image subcomponents**: left pilaster, recessed backboard and right pilaster for **each** upper/lower bay. An extra backboard sample is reference-only; not claimed to be seamless.
- Geometry: width 1024px; row cut coordinates [0,246,696,824,1188,1300,1536], bay pillar cut x=122 and x=901.
- Exact-pixel reconstruction from all six bands has been verified: 1024×1536 RGBA pixel bytes identical to source. Manifest independently hashes **13 image modules** and original master. Local standalone test passed.
- Empty shelf responsive previews rendered at compact **320×610**, typical **360×655**, large **412×725**, and tall **360×760**. Fixed ornamental bands preserve width-relative proportions; less detailed bays absorb variable device height. This remains *off-device image QA*, not proof of physical Android layout.
- Local deliverable: `WS16AK_Empty_Bookcase_Modular_Assets.zip` (includes master, modules, source generation script, native reference, phone previews, HTML interactive offline preview, manifest, test, and Compose renderer example). Copy its contents into project art sources before integrating the images.

## GitHub implementation status
- Added `app/src/main/java/com/playereden/gridmates/wordsearch/WS16AKBookcaseBackground.kt`, a self-contained **not-yet-invoked** renderer for the six expected future `assets/ws16ak/*.png` files.
- The currently running WS16AI `BookcaseWoodParts` remains unchanged, and the current real-book tap previews, saves, paging, Home shelf, game code and fallback APK are untouched.
- After importing actual high-resolution PNG files under `app/src/debug/assets/ws16ak/` and the master into `design/visual-system/masters/ws16ak/`, connect this composable *only in debug QA*. Add manifest-hash checks, unit tests, Android compiled UI checks, release art isolation, and a fresh APK for owner empty-case phone preview.
- Avoid enabling the renderer against missing source assets; it would render blank by design.
- No extra bookcase pages, placeholders, book sprites, menus or progress are baked into the artwork.

## Remaining delivery gate
Actual PNG binary transfer into GitHub + exact-source dual-green CI + actual Android device review. Until then the local ZIP is the source-asset handoff, and this commit is an implementation scaffold, **not a shippable visual change**. Keep original WS16AI build for fallback.
