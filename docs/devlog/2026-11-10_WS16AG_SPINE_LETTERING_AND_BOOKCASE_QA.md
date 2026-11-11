# WS16AG — Corrected physical spines + bookcase QA layout experiment
**Date:** November 10, 2026 · **Status:** SOURCE IMPLEMENTED, exact-commit Android/art CI PENDING. This is not production or owner visual approval.

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
