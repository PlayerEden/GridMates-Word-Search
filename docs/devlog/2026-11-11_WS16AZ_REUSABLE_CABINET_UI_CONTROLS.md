# WS16AZ — Reusable illustrated cabinet controls in the actual Android Library
**Date:** November 11, 2026 · **Status:** source committed; build verification and owner phone acceptance pending · **PR:** #131 draft.

## Prior checkpoint
The owner's WS16AX screenshot confirmed the original five-piece cabinet and actual spines, but showed stretched wood. WS16AY source f5baa42f fixes middle-layer proportions; Android #42985730453 and independent art #42985730447 both passed. WS16AY APK was obtained and its SHA-256 signing certificate confirmed as 75cc3c896247d71fbd51f7a3ed41f90c8218a8a5a70df8c0f6b494b5cc23d419, exactly the established QA signer. VersionCode 58.

## Work actually done
- Added reusable `Ws16AZCabinetUiKit.kt` with gold/walnut native Canvas icon plates and native-text carved plaques, designed for reuse in Bookcase, Collection and later menus. This uses the existing GridMates illustration language, not a static screenshot or baked labels. No replacement original PNGs are generated.
- Integrated controls into the working `WS16ATModularLibraryScene`: title placard, accessible 48dp Back and old-art controls, live book-count plaque, native interactive page arrows with illustrated page number for multi-page catalogs.
- Preserved all real books, touch targets, five original cabinet sprites, shelves, Back route, old-art comparison, saved games, Nearby/LAN, and release artwork isolation. No fake catalog data. Existing source SVG masters and shared Home frame system stay unchanged.
- Extended `test_ws16at_modular_library.py` with integration assertions, dynamic live count, and the protected art path.
- Increased default QA versionCode 58→59, preserving the same `com.playereden.gridmates.wordsearch.qa` package and repo QA signing file. No production signing changes.

## Build gates and acceptance
WS16AY was successful, but **new WS16AZ exact-source QA is pending**, not an on-phone approval. GitHub Actions standard Android and independent artwork/release jobs must both succeed before distributing the new install-over APK. Older owner-installed WS16AX artifact: https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42982189161/artifacts/13838735135 ; WS16AY is the newer dual-green cabinet correction fallback.
When the new APK is ready, install over WS16AX/WS16AY without uninstalling. Judge title plaque over the crown, physical cabinet seams, 48dp controls and accessibility, book count, pagination, real book taps and Back. If the placards overwhelm the carved background, refine or reduce ornament rather than regenerate all cabinet master art. Keep PR draft until handset review.
