# WS-16AK B — Real illustrated empty-bookcase Android QA integration

**November 10, 2026. Status on source commit: real master image and six-band generator committed, Android and art/release CI SUCCESS; owner phone review pending.**

## Owner intent
After the owner selected an actual painted empty walnut cabinet, the immediate priority is to test the **empty cabinet itself** before adding book spines into it. Keep the playable library/Book Factory and saved progress intact; never substitute an invented procedural gradient or an upside-down Home shelf.

## Actual integrated source
- **Original owner-selected 1024×1536 RGBA PNG master**, checked into GitHub under `design/visual-system/masters/ws16ak/approved_bookcase_master_1024x1536.png`, exact SHA256 `e2f8fcf3732698f4657a9e01a0996a34be72f71d5387b14e3d5be4b0d6d0f606` and Git blob SHA `d7af11384a36b0867fe879ea055329590b19d989`. No lossy recompression.
- `tools/asset_pipeline/build_ws16ak_background.py` extracts 13 actual separate image crops from this master; 6 horizontal wood bands perfectly reconstruct the original pixel for pixel. The `build_ws16ak_ci.py` adapter repeats those cuts at each CI build and packages just 6 painted wood PNGs + SHA256 manifest into **debug-only** `assets/ws16ak/`. Original artwork stays immutable. These are real image pixels, not gradient stand-ins.
- `WS16AKBookcaseBackground.kt` renders those 6 bands; detailed ornamental crown, divider/shelf lips and base preserve their proportions; the recessed bay regions absorb vertical device-height differences. The original book assets and interactive native text remain separate.
- `BookcaseBookshelfScreen.kt` has an explicit **QA-only** comparison switch under the heading when `fieldNotesArt` is enabled. It starts in **empty cabinet** mode with no book hit targets or fake books; **Show books** returns to the existing WS16AI dense live-book layout and tap-to-cover dialog. The original Home shelf and puzzle gameplay are completely unchanged.
- Both Android and independent debug/release CI regenerate source art, run `test_ws16ak_ci.py` (source + pixel exact reconstruction + source-integrity and UI wiring assertions), and verify actual packaged `assets/ws16ak/` image hashes. Independent art CI additionally asserts **zero** WS16AK assets in the release APK.

## Honest gates
- Local script extracted six parts from the original master and verified source SHA256; earlier local image reconstruction and off-device four-size preview also passed. No claim is made that it has compiled successfully into Android **until the new exact-source GitHub Actions complete**.
- Production current user flows still use the previous vetted WS16AI code when the user taps Show books; no permission to merge PR #131 or promote new art until on-phone visual acceptance.
- On-device checks: navigate Full Library, enable QA artwork (if necessary), view **empty cabinet**, inspect its actual carved crown, pilasters, two bays, shelf and base, then tap Show books, verify real spine/detail/Back/View pages works. Inspect Android 320dp/360dp/412dp/tall device geometry. Empty cabinet should NOT contain book spines or clickable books.
- **Next** only after owner approves background: align book-contact planes and add books as separate overlays on this new background; then revisit spacings/placeholders and expand future bookcase paging.

**Fallback:** existing WS16AI source renderer and successful APK; do not uninstall or clear QA app data.

## Exact-source Android and artwork QA receipt

- **Compiled source:** [`ac807009a1b2440a2f126b9dde44458831dd2bfb`](https://github.com/PlayerEden/GridMates-Word-Search/commit/ac807009a1b2440a2f126b9dde44458831dd2bfb).
- **Android QA: SUCCESS** — [run #42944866452](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42944866452), [ZIP artifact #13822112791](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42944866452/artifacts/13822112791). GitHub sign-in required; expires **December 11, 2026**. Download ZIP SHA256 `1f99a1c16f23a0f47688a8ede00979b564670a6631b17ad213389cd16c84ceb5`.
- Its `app-debug.apk` is **23,794,984 bytes**, SHA256 `d7a30ad72930c470bbf10db7c9391f873f64a6d1118f9995727a9a4de9218df5`, independently ZIP-tested with no damaged entries.
- **Independent artwork/release isolation: SUCCESS** — [run #42944866350](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42944866350), [artifact #13822367097](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42944866350/artifacts/13822367097). Outer alternate APK SHA256 `6233787870070c6be67c1aad0d56c992c6efc0d9f4e59579a86049e7cfda179c`; both APKs have **311/311 identically named internal files and identical extracted file bytes**.
- The actual Android package contains 6 PNG bands plus `assets/ws16ak/manifest.json`. Recomputed **all 6** image SHA256 hashes against packaged manifest: **6/6 match**. The manifest cites the source master SHA `e2f8fcf3732698f4657a9e01a0996a34be72f71d5387b14e3d5be4b0d6d0f606`; source hash and per-band exact reconstruction validated in both CI runs. Release APK holds **zero** WS16AK assets (independent art QA check).
- The new Android build is **technically verified but not physically visually accepted**. Actual phone screenshot must judge the native cabinet: crown, side posts, backing, both shelf planes, base, cropping and safe-area display at device resolution. **Empty art / Show books** toggles only with field-notes artwork enabled; existing book sprites/screens remain reachable. Do not uninstall or clear saved puzzles.
- Existing WS16AI full bookcase and older accepted Home walnut shelf remain the rollback; no new published book, save data, or gameplay change.

