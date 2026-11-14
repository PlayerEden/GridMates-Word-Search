# WS16AE — All three physical book covers and spines, corrected V2
**Date:** November 10, 2026 · **Source milestone:** dual-CI GREEN, owner phone review pending · **PR:** [#131 draft](https://github.com/PlayerEden/GridMates-Word-Search/pull/131)

## Owner decision
Owner specifically requested that **all three** existing books be overhauled as necessary, not merely Woodland. The accepted Home shelf, art style, actual playable book IDs and current controls must remain stable. The original source illustrations were deliberately preserved: woodland owl, ringed planet, garden flowers/watering can. An alternate fully redesigned three-cover direction was generated for visual comparison but **NOT silently substituted** into this build because it changes the Space centerpiece to a rocket.

## Implementation: artwork that actually ships in QA
- **Six real edited/re-exported image assets**: `app/src/debug/assets/ws16ae/covers/{woodland,space,garden}.png` and `spines/{woodland,space,garden}.png`, plus generated manifest. All derive from six verified, existing, hash-pinned PNG sources in `ws14/` and repeatably normalize through `build_ws16ad_book_factory.normalize`. The original WS14 master sheet is never overwritten.
- `tools/asset_pipeline/build_ws16ae_books_v2.py`: for each original printed GridMates banner, fixes the *actual tiny registered symbol* at individually identified source-image coordinates using surrounding **unprinted parchment samples** while preserving letterforms and foliage; then normalizes dimensions to **480×640** cover and **128×640** independent spine, including shared alpha bounds and an exterior-RGB decontamination so semitransparent edges do not pick up white matte specks.
- The existing original labels (Woodland/Space/Garden) and illustrated front scenes remain; theme names and stable content IDs are unchanged. No fake chapters or progress. The **three blank coming-soon cover/spine pairs from WS16AD remain source-controlled** but intentionally are not fake selectable library volumes.
- `BookArt.kt` now reads `ws16ae/` first in debug for both FRONT COVER and SPINE, falls back to `ws16ad`, then WS14/WS12 on missing candidate; the public/release image isolation remains unaffected.
- `test_ws16ae_books_v2.py`: confirms six real source-derived image exports with 1:1 source hashes, fixed dimensions, localized ® edits (no nearby glyph change), and unchanged fallback/library status. `verify_ws16ae_apk.py` verifies the seven real debug APK asset files/manifest with SHA-256 and refuses their appearance in release. Both Android and independent art workflows run these checks.

## Quality, source honesty and remaining gates
- First local proof (all six on cream and dark mats) shows unified silhouettes, original theme illustrations and removal of tiny ® pixels. This is **carefully edited source artwork**, not a claim that the covers were repainted entirely from scratch.
- QA-only. Do **not** claim publish rights, owner final approval, physical Android gameplay tested, or an APK exists until both exact-source CI workflows pass. The old paintings and installed WS16AD candidate remain fallback.
- **Future scope kept separate:** the full shared **GridMates** family wordmark and separate **Word Search** title art; full library height/touch layout A/B; 3 coming-soon placeholder placements; book gameplay & two-phone LAN testing.

## Exact-source CI verification and Android test artifact

- **Compiled source:** [`8d4a49170c9eab43afff74b23e8fd42eb72340a4`](https://github.com/PlayerEden/GridMates-Word-Search/commit/8d4a49170c9eab43afff74b23e8fd42eb72340a4).
- **Android QA:** [run #42920066449](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42920066449) **SUCCESS**, [GitHub ZIP artifact #13811856052](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42920066449/artifacts/13811856052). Its `app-debug.apk` is **20,044,271 bytes**, SHA-256 **`b5b7125bd83f3164d14381aabff62a34ed2ae70803524fe1acce0a5e8a54e1f1`**.
- **Independent art/release QA:** [run #42920066338](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42920066338) **SUCCESS**, alternate [artifact #13811237311](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42920066338/artifacts/13811237311). Outer APK SHA-256 differs (`ccb5615f41c5721a53928150f48005da6e5deb44420679a297a3f7b8a606c704`) due to APK packaging, but **all 288 internal file names and uncompressed bytes match** the standard QA build exactly.
- Independent inspection found **six corrected PNGs** plus `assets/ws16ae/manifest.json` in the actual debug APK. All six image SHA-256 hashes were rechecked against the packaged manifest, **zero mismatches**. The independent art/release build verifies the new `ws16ae` candidate assets are **absent in release**.
- The corrected registered-symbol areas, normalized book-body silhouettes and all three separate spines were exercised by the reproducible source-generation QA checks; this is NOT proof of a physical phone visual acceptance or full gameplay regression.
- **Owner action:** install this QA APK over the existing debug app if signing permits; **do not uninstall or clear saved data**. With the developer overlay OFF, compare Woodland, Space and Garden Home covers; open Full Library and check all three spines, tap/Back, save/Resume and visible edges. Send phone screenshot for final art adjustment. 
- The **three blank coming-soon volume pairs** remain debug-only source art but deliberately not catalog entries. The old WS16AD/WS14 inputs and the accepted WS16AB shelf remain fallback. PR #131 is still draft/unmerged.

## Release/checklist
- [x] Android QA build success, source SHA and downloadable artifact
- [x] Independent debug/release art isolation success
- [x] All 6 PNG sources/outputs match manifest and generated bytes, 7 debug APK items present
- [ ] Real phone: Woodland, Space, Garden carousel; tap each; library spine tap/Back; preserve old Woodland save; compare edge pixels and book grounding
- [ ] Owner visual acceptance of all six edited book assets before release promotion

**Next exact action:** validate latest GitHub Actions, hand off one confirmed GitHub APK artifact, and ask for a single on-phone Home + Full Library screenshot with debug overlay OFF. If any letter/fringe artifact remains, revise the original retouch coordinates and source mask before adding any new themes.
