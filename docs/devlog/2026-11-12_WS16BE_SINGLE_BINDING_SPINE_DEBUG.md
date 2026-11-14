# WS16BE — Remove faux backing leather behind real book spines

> **Status (Nov 12, 2026):** DUAL-GREEN ANDROID/ART QA / EXTRACTED APK HASH, PACKAGE AND SIGNER VERIFIED / OWNER PHONE VISUAL ACCEPTANCE PENDING. Automated build success does not approve appearance.

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

## Final v64 verification and delivery (Nov 12)

- **Exact application/test source:** [`8d8942dff9e50e966346fdbaef4c79c3d3ded504`](https://github.com/PlayerEden/GridMates-Word-Search/commit/8d8942dff9e50e966346fdbaef4c79c3d3ded504) (later journal-only commits are `[skip ci]`).
- **Android [#43173531241](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/43173531241): SUCCESS.** Core/app tests, lint, compilation, signed QA APK assembly, package checks, unchanged QA certificate and all source-pinned book/cabinet artwork checks passed.
- **Independent art/release [#43173531335](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/43173531335): SUCCESS.** Both debug and release compiled, original QA-only generated assets remain out of release, signing check passed.
- **GitHub Android ZIP [artifact #13924000090](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/43173531241/artifacts/13924000090)** contains `app-debug.apk`, **29,054,896 bytes**; extracted APK SHA-256 `654ef1af1b7ba54f323c37dc0103c82b5d80e370ee5d300b764812b9b45acf44`; ZIP SHA-256 `2ad94f13f7ca896e8b21ad6fad31e79cbf9c905447a495668a5c6b6c0e26ef71`. Direct APK and GitHub ZIP were provided to the owner in chat. Source of truth remains the GitHub artifact.
- **Android identity (confirmed by CI `aapt` and `apksigner`):** `com.playereden.gridmates.wordsearch.qa`, versionCode **64**, versionName `0.9.20-clean-single-spines-qa`. Same public QA signing certificate SHA-256 `75cc3c896247d71fbd51f7a3ed41f90c8218a8a5a70df8c0f6b494b5cc23d419` as v63, so install over without uninstall/data loss expected.
- **Independent [art artifact #13922349849](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/43173531335/artifacts/13922349849)** ZIP SHA-256 `df17d3de029b3a99746cb0c59b114e5eb730dcae17a6e5e3714659c006c5b6a6`; its APK SHA-256 `32cb53e7a49d593d6d5950e9609848b9b98c4a3589a8562e9ee420bbd3104406` differs in archive bytes from standard Android APK, but direct comparison found the **same 339 names and identical uncompressed content bytes** in both. Do not call the signed APK archives byte-identical.
- **No asset changes:** All packed `assets/` images in v64 are byte-identical to v63, including original clean `ws16ai/spines`, generated `ws16ao`, and cabinet. Runtime source selection and Compose shadow/headroom, not a new image, are the actual modification.
- **Not tested yet:** Installation on owner's phone, physical visual quality, real spine taps, preserved saves and portrait clipping remain open. PR #131 stays draft; no generated illustration release approval.

## Next exact action

Owner installs supplied v64 APK directly over v63 (no uninstall) and sends one full Library screenshot. Verify that the rounded spine is now a single shape, touches each shelf without overlapping the title, and that real Woodland/Space/Garden books still open with saved progress intact. If technically clean but aesthetically weak, pivot to front-facing illustrated book covers rather than regenerating faux-wide leather shoulders. Keep PR #131 draft.
