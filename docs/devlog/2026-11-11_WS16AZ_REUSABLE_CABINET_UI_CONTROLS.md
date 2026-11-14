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


## Final exact-source dual-green APK receipt (November 11)
- Tested code commit: [`3865179d77d6a834104916d5b20f14005aef3599`](https://github.com/PlayerEden/GridMates-Word-Search/commit/3865179d77d6a834104916d5b20f14005aef3599).
- [Android QA #42986889097](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42986889097) — SUCCESS, including unit tests, lint and installable debug APK.
- [Independent artwork/release isolation #42986889060](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42986889060) — SUCCESS on the same commit.
- [GitHub APK ZIP artifact #13840371565](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42986889097/artifacts/13840371565) includes `app-debug.apk`. ZIP SHA256 `f6c820d93a497cc6996626268726a7a41f531abcc856cef306bf48f421e513a9`.
- **Actual extracted APK SHA256:** `b0cee43afe87f597bc624b940ac10a7c85428ba998aca31ee5a382655f33d0c5`, size **26,001,164 bytes**; APK ZIP CRC verified, all five original `ws16as/shell` images present. Independently extracted APK v2 certificate SHA256 `75cc3c896247d71fbd51f7a3ed41f90c8218a8a5a70df8c0f6b494b5cc23d419`, identical to prior owner-installed WS16AX and verified WS16AY. App config preserves `com.playereden.gridmates.wordsearch.qa` with default versionCode 59.
- Direct, owner-downloadable extracted APK was made in the current ChatGPT conversation: `GridMates_WordSearch_WS16AZ_Update.apk`. It is **not** a GitHub-hosted standalone APK link; the GitHub artifact is a ZIP. Do not invent download URLs.
- **Owner handset screenshot/interaction approval still pending**. The owner should install the new APK over the existing QA app without uninstalling, then review crown/title plaque overlap, changed cabinet columns, bottom count, navigation plates and actual book taps. PR #131 remains draft.
