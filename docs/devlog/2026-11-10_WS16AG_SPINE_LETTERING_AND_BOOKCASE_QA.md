# WS16AG — Corrected physical spines + bookcase QA layout experiment
**Date:** November 10, 2026 · **Status:** DUAL-GREEN ANDROID + ART/RELEASE QA VERIFIED; owner handset approval pending. This is not production or owner visual approval.

## Why this pass exists
Owner's real-phone screenshots confirmed Garden Home, the physical spine-out library, and a working Space spine → full cover modal. The previous book cover/art workflow passes Android CI but leaves a faint unverified superscript next to "GridMates" on **all three separate spines**; the wide wooden library display also leaves the spines visually hovering above the shelf and has a second empty row.

## Book source work
- `tools/asset_pipeline/build_ws16af_spines_clean.py` reads hash-pinned original WS14 spine PNGs for **Woodland, Space, Garden**, edits **only 13 / 22 / 15 individually mapped printed-mark source pixels**, preserving the adjacent real 's' letterform, each source alpha channel and every book illustration. The fix samples nearby unlettered leather, not a blur or opaque rectangular patch.
- Uses the same previously tested WS16AD `normalize()` canonical **128×640** spine geometry and WS16AE edge-decontamination; generates 3 independent new transparent PNGs + SHA256 manifest under **debug-only** `assets/ws16af/spines/`. Existing `assets/ws16ae/covers/` remain the front covers; old spines remain fallback.
- `BookArt.kt` selects these corrected spines first only in `fieldNotes` QA mode. No saves, content IDs, page-generation rules, icon collections, Home book shelf, or multiplayer code changed.

## Full Library layout QA (NOT published content)
- Corrected spine alignment: move standing books toward the actual plank surface using a small bottom inset change (36dp to **28dp**). Do not introduce a large rectangular artificial shadow.
- Slightly more substantial **proportionally scaled** spine artwork with two width/height classes: standard **31×154dp**, long one-word title **32×160dp**, with 170dp container; no forced crop, stretching or automated rename. Original clickable independent slot remains **48dp** (54dp at large font scale).
- Fill the existing *second empty decorative row* only in the debug artwork mode with the previously source-controlled linen, night, burgundy **blank physical spine** candidates. These have **no PuzzleBook ID, onClick, pages, rewards or progress**, and are accompanied by actual UI text "More books coming soon" and non-button accessible semantics. The real library count remains **3 books**.
- Library tap-to-preview still uses the same original `onSelect(book.id)` and full cover dialog with actual chapter/progress; no animation introduced until full interaction QA is verified.
- Existing wide decorated backboard, Home cover carousel, parchment and walnut shelf are preserved.

## Evidence / tests
- Precise original source pixels were inspected at native resolution; old/new comparisons were checked on cream and dark. The local WS16AF isolated script produces normalized outputs with identical silhouette bounds and alpha geometry for all three.
- CI steps generate assets, run `test_ws16af_spines_clean.py` (source hash, only allowed original mark pixels edited, fixed dimensions, native UI fallback/non-playable-only checks); `verify_ws16af_apk.py` verifies four actual debug APK asset bytes against manifest and zero in release, in addition to previous WS16AD/WS16AE validation.
- **At commit time, actual GitHub CI and handset APK review are pending.** Do not claim the physical shelf contact or empty-row scale looks approved before inspecting real device screenshots.
- Previous confirmed WS16AE working APK/backup and draft PR #131 retained.

## Next
1. Verify Android and independent art/release CI on exact source commit; compare packaged PNG checksums, confirm no release-leak.
2. Provide authenticated GitHub APK or verified direct APK. Owner phone: Home all themes, Library standard/tall V3 spines, blank second shelf, taps, Back and View pages; check blank spines do **not** act like actual books.
3. Adjust tiny shelf offset if necessary, then move on to separate two-part full wordmark art and deeper book gameplay QA.

## Exact-source dual-green compiled Android QA — verified

- **Source commit tested:** [`1e3704422838c030a3f4caf94e4b02d7e418ab3c`](https://github.com/PlayerEden/GridMates-Word-Search/commit/1e3704422838c030a3f4caf94e4b02d7e418ab3c). First WS16AG commit `f96ac392` failed an *outdated WS16AD test* which forbade even debug-only placeholders in BookArt; this was corrected to demand clearly **nonplayable, uncataloged** fillers and both workflows reran green. No gameplay break or content change.
- **Android QA SUCCESS:** [run #42929200549](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42929200549), [artifact #13815806776](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42929200549/artifacts/13815806776) (GitHub sign-in required), **30-day retention until December 11, 2026**. Archive `WS16AG_Bookcase_AndroidQA.zip` SHA256 `80e289dab19768f0dfa2d3754f9c288e357541154d884aae1eb6e4b00f54249c`, 19,925,846 bytes. Embedded `app-debug.apk` is **20,390,460 bytes**, SHA256 `ce7e92bae3f2acabbf5364e66023d4138f4d1b45a53be98a62ff51921744c3b1`.
- **Independent debug/release art QA SUCCESS:** [run #42929200572](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42929200572), [artifact #13816280471](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42929200572/artifacts/13816280471), expires December 11, 2026. Its independently compiled debug APK SHA256 `120590405a94e469b39e9edcc059e9f3f6a6941d375d8f188c134b0be5cf5655`; **all 292 filenames and uncompressed file contents match byte-for-byte** with the standard Android QA build despite differing APK containers.
- **Actually unpacked and verified**: `assets/ws16af/manifest.json` and all three new physical spine PNGs included. Manifest SHA256 matches all three source images: Woodland `53ff797e42dd78f380c988d5213152502b974faf443edd2a3cea96842bbeefb7`, Space `c05f8e9577dadf31af6a5d3df84d1b65e621a64ec1982e681e96ef194b18abaf`, Garden `41259cc2ec1a99e3cd8d2b904e0cfa81fe93dcc932c482e2adf532e31685a27a`. Their alpha bounds remain `(14,51,114,589)`; older `ws16ae/covers` and distinct `ws16ad/placeholders` PNG assets are present as expected.
- New source-generated artwork remains **debug-only**; independent release-isolation workflow passed. This does **not** establish real Android touch function/visual owner acceptance; must test phone after upgrade without uninstalling or clearing saved data.
- **QA phone acceptance targets:** corrected GridMates spine mark, proportional Standard/Tall sizes/physical contact on walnut plank, clarity of 3 noninteractive blank books and More books coming soon, existing 3-book count, Space and other real spine taps/open/book details, View pages and Back, other Home screens unaffected. Owner can decline/adjust changes; draft PR #131 unmerged.

