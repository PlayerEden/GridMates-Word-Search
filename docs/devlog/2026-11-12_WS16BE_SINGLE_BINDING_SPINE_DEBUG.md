# WS16BE — Remove faux backing leather behind real book spines

> **Status (Nov 12, 2026):** SOURCE COMMITTED / NEW EXACT-SOURCE CI PENDING / OWNER PHONE TEST PENDING. A passed automated build does not approve appearance.

## Who / where / why

- **Owner:** After installing WS16BD v63 and sending a portrait Library screenshot, owner said the spines looked bad and noticed it appeared that something else was **behind the generated spine assets**. Requested development continuation.
- **Implementation:** ChatGPT on existing draft [PR #131](https://github.com/PlayerEden/GridMates-Word-Search/pull/131), branch `feature/ws14b-style-anchor-and-art-direction-2026-11-08`.
- **Goal:** Establish whether the apparent double spine comes from two stacked Compose elements, source image transparency, or the generated artwork. Fix the actual cause without another speculative Library overhaul.
- **Relevant files:** `tools/asset_pipeline/build_ws16ao_spine_family.py`, `BookcaseWoodArtwork.kt`, `WS16AMInteractiveBookcaseScene.kt`, `WS16ATModularLibraryScene.kt`, `QaActualBookcaseAndroidTest.kt`, source tests and `app/build.gradle.kts`.

## Investigation (verified from the actual v63 APK)

The source-pinned Android v63 APK (SHA256 `53fb4dc9823e72e9b3fcfa78adf6837c22afc396243577739cfd7e8a7846ea5c`) contains both:

- `assets/ws16ai/spines/{woodland,space,garden}.png` — original 104×542 **single rounded book binding**; alpha bounds x=2..102, no faux square panel.
- `assets/ws16ao/spines/tall/{woodland,space,garden}.png` — 166×662 and standard 166×542 **generated wider** images. `build_ws16ao_spine_family.py` draws a full-width, squared leather backing from x=5..161, adds extra horizontal gold lines on the exposed shoulders, then alpha-composites a feathered original 96px binding at x=35. The central binding and backing have visibly separate outlines, creating a **spine-on-top-of-another-spine** appearance in the actual asset pixels. It is **not** evidence of two Compose book components.

`WS16AMSpineBay` renders one `BookcaseSpineArt` per real book and only optionally a 4dp background contact shadow. No future placeholder visual is drawn in WS16BD's modular real-book path. Thus the square backing and duplicated edge lines are **intrinsic to the generated WS16AO asset**, not from a hidden placeholder. A tiny additional shadow exists in Compose but does not account for the large backing panel.

The v63 user screenshot also shows upper book tops running beneath the `Your Library` plaque. The existing tall profile height and title clearance need attention.

## WS16BE implementation

1. `WS16AMSpineBay`: add optional `useCleanSpines` and `drawContactShadow` toggles (default **false / true** so legacy routes are untouched). When clean is enabled, select profile `clean` and true source ratio **104/542**, not the 166-wide enlarged book. Keep exact book IDs and click callbacks. Disable the 4dp contact ellipse when requested.
2. `BookcaseWoodArtwork.kt`: make clean profile explicitly select the **original** `ws16ai/spines/$name.png`; keep existing `tall`/`standard` WS16AO images and fallback behavior intact for other paths. `ContentScale.Fit` preserves print proportions; no stretching or new asset rights.
3. `WS16ATModularLibraryScene.kt`: only the tall portrait modular Library opts into the clean path (`useCleanSpines = cabinetPortrait`) and disables extra contact shadows (`drawContactShadow = !cabinetPortrait`). For sparse 2-shelf portrait display, reserve a further **24dp** of image-only headroom (34dp total; 10dp baseline) to keep art below the title plaque **without moving its bottom off the shelf**. Continue 2+1 real-book layout, no future placeholders, count bay and safe overlay.
4. `QaActualBookcaseAndroidTest.kt`: additionally assert that top real image bounds do not overlap the native Library title plaque. Existing selection, book placement and count checks remain.
5. `test_ws16at_modular_library.py` and `test_ws16ao_spine_family.py`: preserve source-pinned original/derived files and fallback, assert the correct clean profile, optional shadow and reserved header clearance. The originally pinned 12 WS16AO images are **not changed or reapproved**.
6. `app/build.gradle.kts`: debug QA `versionCode=64`, `versionName=0.9.20-clean-single-spines`; same application ID `com.playereden.gridmates.wordsearch.qa` and same existing QA key/certificate. Intended install-over v63 without uninstall.

**Latest feature/test code source:** [`c82bf18e8e99b60baf9a3233b79baf3c1d4c1278`](https://github.com/PlayerEden/GridMates-Word-Search/commit/c82bf18e8e99b60baf9a3233b79baf3c1d4c1278). New CI runs must be read by commit SHA, not guessed.

## Pre-build geometric correction

An initial implementation reduced `WS16AMSpineBay`'s overall height by 24dp, which would also lift the bottom-aligned image away from the wooden contact plane. This was caught during code review **before sharing a v64 APK**. The corrected implementation keeps `height = (tierHeight - shelfContactDepth)` and introduces `headClearance: Dp = 10.dp` for image width/height calculation only; sparse portrait opts into 34dp total (24dp additional), so image tops clear the title while book feet remain shelf-anchored. Adjusted static tests accordingly, no art/source change.

**Current corrected source:** [`8d8942dff9e50e966346fdbaef4c79c3d3ded504`](https://github.com/PlayerEden/GridMates-Word-Search/commit/8d8942dff9e50e966346fdbaef4c79c3d3ded504). First runs [Android #43173065798](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/43173065798) and [art #43173065828](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/43173065828) were cancelled/superseded. Corrected exact-source [Android QA #43173531241](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/43173531241) and [art/release #43173531335](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/43173531335) were pending at journal update. These newer runs are the ones to verify.

## Build, rollback and release gates

- **Initial v64 code source** [`71449c1e`](https://github.com/PlayerEden/GridMates-Word-Search/commit/71449c1ef7cf371f43a06d8745d92d8e000784c1) triggered first Android/art workflows; following `c82bf18e` repairs an inherited source test that was hard-coded to the 166-wide binding ratio.
- **Both exact-source workflows:** PENDING at this log's creation. Do **not** claim an APK exists for v64 or that any phone check passed. After CI green, extract actual GitHub APK, verify signing/package/version/checksum, then share direct APK and GitHub ZIP.
- **Preserved proven predecessor:** v63 [Android ZIP artifact #13910520915](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/43141513582/artifacts/13910520915), SHA `53fb4dc9823e72e9b3fcfa78adf6837c22afc396243577739cfd7e8a7846ea5c` — **build-verified and owner-tested visually but artwork NOT approved**. v62 is the previous working visual variant. Avoid uninstalling or resetting player data.
- **Unchanged:** all book IDs, progress and saves, puzzle rules, LAN, Home/Profile/Collection, illustrated cabinet, original PNG files, release-art isolation/rights; draft PR stays unmerged.

## Next exact action

Check CI on latest `c82bf18e`. If failure, inspect job logs and repair precisely. If dual-green, verify APK and share v64 for one phone screenshot of Library, top title and real-book tap/save test. If the single-binding art is clean but still visually underwhelming, then consider front-facing covers and a structural Library rethink rather than more faux-wide spines.
