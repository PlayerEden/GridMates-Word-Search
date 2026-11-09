# November 9, 2026 — WS-14J2: First Artwork Wired Into QA-Only Book Page

> **Status: SOURCE COMMITTED / Android and release-art CI running.** Do not treat queued workflows as passed. This pass deliberately changes no gameplay, save data, board sizes, hint logic, signing, or release artwork.

## Who, what, where, why
- **Owner request:** “Let's continue” after WS-14J1 Woodland standalone artwork creation and GitHub journal recovery.
- **Work performed:** assistant, on draft PR #131 (`feature/ws14b-style-anchor-and-art-direction-2026-11-08`).
- **Goal:** integrate a real, small, unlettered Woodland page accent in the existing book-page header, without crowding the puzzle grid or changing native controls.
- **Previous milestone:** [WS-14J1 actual source/export and receipt](2026-11-09_WS14J1_FIRST_WOODLAND_TRANSPARENT_ASSET.md), successful exact local SHA-256 `747af7c99b76b7ed38c91460409fd9d32a24decd1c1412991d9c364a3f3b331b` on 128×128 PNG, and editable SVG source.
- **Known baseline:** previous WS-14I Android source 3a3d0bc passed Android #42664221672 and art #42664221664; follow-up candidate-source Android #42673911715 finished SUCCESS, art #42673911820 failed with no job-level details. No Woodland candidate was in the prior APK.

## What actually changed (code commit `01c20de43f4ee157cf9aa7a5b6cac7ea09d830e3`)
- Copied the **identical PNG blob** `e0e6dbed8aa89a7eda22bae6bd67d8f7bfcf43b3` from `design/visual-system/exports/ws14j-page-accent-woodland-v1-128.png` to `app/src/debug/assets/ws14j/page-accents/woodland.png`. Both paths point to the *same Git blob*, not a screenshot or text placeholder.
- `BookArt.kt`: added `ws14jQaPageAccentPath(theme, kind, enabledForPageHeader)` and optional `pageAccentCandidate` parameter. Only the **Woodland EMBLEM explicitly enabled for page header** selects this sprite; decode failures fall back to the current native/WS-12 source-backed art. Other screens do not request it.
- `BookPageGameplayScreen.kt`: opted in inside the existing 32dp header-only emblem slot; preserved 48dp Hint lightbulb, existing font-scale/320dp/status/landscape gating, native title and grid untouched.
- `Ws14JPageAccentRoutingTest.kt`: Kotlin unit checks confirm Woodland eligible and Space/Garden/cover/non-header ineligible.
- `tools/asset_pipeline/test_ws14j_page_accent.py`: extends hash/transparency checks to require actual debug PNG copied byte-for-byte from registered export.
- `ws01-android-qa.yml`: checks the source/PNG pixels and tests, plus requires the exact `assets/ws14j/page-accents/woodland.png` byte hash inside the debug APK.
- `ws12g-qa-art-lab.yml`: checks exact PNG in debug and **zero** `assets/ws14j/` paths inside the compiled release APK.
- `ws14j-page-accent-candidates.json`: records debug asset path, flags `android_debug_integrated=true`, `android_release_integrated=false` as intended source layout, leaves `state=GENERATED_UNREVIEWED`, `owner_approved=false` and `release_approved=false`.
- No changes to Woodland profile portraits, illustrated cover/spine binaries, book catalogs/content IDs, save schemas, LAN, theme font/token systems or installed-app identity.

## Builds and QA (at time of writing)
- Exact source commit: [01c20de](https://github.com/PlayerEden/GridMates-Word-Search/commit/01c20de43f4ee157cf9aa7a5b6cac7ea09d830e3).
- [Standard Android QA #42675610221](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42675610221): **queued** at first inspection.
- [Art debug/release-isolation #42675610184](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42675610184): **queued** at first inspection.
- Local previously verified PNG: 128×128, full transparency outside drawing, alpha bounds (13,10,113,123), 2745 bytes, SHA-256 `747af7c99b76b7ed38c91460409fd9d32a24decd1c1412991d9c364a3f3b331b`.
- **Not yet performed:** source-matched Android+release CI acceptance, emulator instrumentation execution, owner handset 24/32/36dp visual review, real 20×20 word tracing/fifth hint/save reopen, owner artwork or commercial release approval.
- **Rollback:** original WS-12 EMBLEM when candidate missing; prior Android success #42673911715 (source-only), and earlier observed physical v56 fallback. Do not uninstall an existing QA app without exporting saves and confirming signing.

## Next one action
Read BOTH final workflow conclusions for commit 01c20de. If either fails, inspect real job logs and repair the code or environment before any install claim. After dual green, inspect artifact SHA/APK entries and hand over GitHub ZIP with a small physical QA checklist: Woodland header visible, 320/360dp, large font fallback, Space/Garden unchanged, hints and 20×20 touch safe, saved progress intact. Only then make Space and Garden matching transparent source-backed assets.

## Final exact-code GitHub Actions receipt — resolved after original entry

Both workflows reached **SUCCESS** on the **same gameplay source commit** `01c20de43f4ee157cf9aa7a5b6cac7ea09d830e3`:

- [Android QA #42675610221](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42675610221): successful Android compile, unit tests, lint, Android instrumentation source *compilation*, debug package and exact candidate hash check inside APK.
- [Art/release isolation #42675610184](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42675610184): successful debug + release compilation, verified one exact Woodland `assets/ws14j/page-accents/woodland.png` candidate in debug and **zero** `assets/ws14j/` assets in release.
- **Owner test artifact:** [Android QA ZIP #13699334694](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42675610221/artifacts/13699334694). ZIP contains `app-debug.apk`; artifact was unexpired when checked. ZIP SHA-256 in GitHub logs `56732923808b5b1f64cf810bb9ff0aac1a82c05c8753ce8e3e96241f866f2d81`.
- **Actual APK checksum (from logged `sha256sum`):** `1df127b0fb655f5c9786729e10ca29440e5f1f40e8607c505fa956aa9260f8c5`.
- **Package/build metadata in Android logs:** `com.playereden.gridmates.wordsearch.qa`, versionCode `57`, versionName `0.9.13-visual-kit-foundation-qa`.
- **Still pending:** installation on owner's phone, 32dp aesthetic acceptance, 20×20 tracing, five hint taps and persistence; ZIP artifact verification by CI is *not* physical Android test.
- **New deferred roadmap:** [WS-14K screenshot-led compact icon UI and asset kit](../WS14K_GAMEPLAY_UI_MINIMIZATION_AFTER_PHONE_QA_2026-11-09.md). Owner expressly prioritizes getting screenshots from a real install before implementing more icon-heavy UI; no additional UI code or images were changed in this documentation pass.

**Next single action:** owner downloads the exact WS-14J2 QA artifact ZIP, safely installs compatible APK, plays Woodland/Space/Garden and sends real screenshots/feedback. Maintain backup/signing safeguards and draft PR; use the screenshots to decide whether WS-14K header consolidation increases board room without accessibility regressions.
