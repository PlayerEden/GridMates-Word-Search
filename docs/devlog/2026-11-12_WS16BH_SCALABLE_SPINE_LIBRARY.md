# WS16BH — Spine-first, densely packed and scalable physical Library

> **Status (November 12, 2026):** DUAL-GREEN ANDROID & ART/RELEASE QA / EXACT-SOURCE v67 APK VERIFIED AND DELIVERED / OWNER PHONE VISUAL & INTERACTION ACCEPTANCE PENDING.

## Who, what, where, why

- **Owner's explicit decision:** After installing WS16BG v66 and submitting a portrait Library screenshot, the owner explained that the front-facing covers do not scale to their planned large Word Search catalog: **many future themes and puzzle books should be packed tightly side-by-side on two or three shelves**, and the covers are intentionally paired assets for book preview and the Home feature. The request to continue means the Library must be spine-first, not a front-cover carousel/grid.
- **Work:** ChatGPT implemented an alternative presentation in existing [draft PR #131](https://github.com/PlayerEden/GridMates-Word-Search/pull/131) on `feature/ws14b-style-anchor-and-art-direction-2026-11-08`. This is reversible, no merge or user-data migration.
- **Goal:** A real expanding book collection, compact clean bindings, deterministic shelves/page capacity, true physical contact, accessible book ID click path, 2/3 adaptive shelves; no fake future volumes or second leather layers.

## Where we started

- Previous [WS16BG journal](2026-11-12_WS16BG_FACE_OUT_COLLECTIBLE_LIBRARY.md) and [exact dual-green v66 Android artifact #13929551769](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/43187644879/artifacts/13929551769). v66 APK SHA-256 `d7f31098e9bdd587a0ebbea4d2e0a652080a322c071059817731c63bed73a1ad`. Owner installed v66 and shared its portrait screenshot, establishing physical visual review; no claim that they accepted that layout.
- Keep high-resolution cabinet, ivy, carved shelf assets, source-pinned one-piece rounded `ws16bf` clean spines, actual catalog order, stable IDs, puzzle pages/preview, local saves, LAN, Profile/Collection and Home intact.
- Original spine PNG/cover art remain QA-only; no new art binary, license, or release rights cleared. Retain old WS16BG face-out component as an unused/reversible design study, not the Library default.

## WS16BH actual source changes

1. `WS16BHDenseSpineBay.kt` (new Compose component): render **only actual PuzzleBook bindings** using `BookcaseSpineArt(..., profile="curved")` backed by existing 104×542 source-pinned alpha-clean `ws16bf` PNGs. Compact slots, nearly touching, small 2dp contact shadow, no invented volumes, no second rectangular leather backing. Row shrinks to exactly the number of real books (no empty click target). Native tap/accessibility semantics keep `onBookSelected(book.id)` and existing book-preview cover/dialog/View pages.
2. `WS16ATModularLibraryScene.kt`: enable tight portrait binding slots **36dp normal / 42dp enlarged font** within actual cabinet interior. Row-major fill: first physical shelf, then second, then third when present. Default 2 live shelves on a normal portrait; on a high-res tall portrait (height >= 740dp) with more books than one shelf can hold, expose **3 physical shelves**. Short / landscape screens retain the previous responsive spine route. For all two/three-shelf portrait layouts, reserve the lower carved shelf extension and book-count plaque space *before* dividing actual tier heights, preventing collision with the base. No future placeholders. Capacity = computed slots per shelf × actual shelf count, and pages contain only real book IDs. Gallery-cover path is no longer invoked from portrait Library.
3. `QaActualBookcaseAndroidTest.kt`: verifies three owned volumes are packed side-by-side on the first shelf and clear the title, 2 shelves when sparse, no fake volumes, count clears wood, Garden tap uses stable ID. Adds 14-real-volume fixture checking expansion to three physical shelves and stable selected ID.
4. `test_ws16bh_dense_library.py`: exercises page partitioning, unique ordered IDs, catalog sizes 0–100, tall/short, narrow/wide and enlarged font sizes; asserts no cover-first default, no fake books and the exact curved spine route. Existing WS16BG tests now preserve the archived face-out candidate without falsely requiring it as the default; older WS16AT/WS16BB tests updated for new source contract.
5. `app/build.gradle.kts`: debug QA `versionCode=67`, versionName `0.9.23-scalable-spine-library`, same application ID `com.playereden.gridmates.wordsearch.qa` and existing public QA signer. Install-over v66 intended; do NOT uninstall/clear saved game.
6. Both Android and art/release workflows gained the new geometry/real catalog test. All previous source art checks and release-art isolation remain required. No new generated PNGs are made.

**Current exact feature/test/workflow code at QA start:** [`c75287c5a8a03e968208337bb68226629c69b9ad`](https://github.com/PlayerEden/GridMates-Word-Search/commit/c75287c5a8a03e968208337bb68226629c69b9ad). Documentation-only updates `[skip ci]` do not alter the exact compiled source.

## Verification and rollback

- **Android QA run [#43192422437](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/43192422437): SUCCESS** on exact feature/test SHA.
- **Independent art/release isolation [#43192423542](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/43192423542): SUCCESS** on the same SHA.
- **Actual v67 APK:** Verified and delivered; see exact APK SHA, signer and artifact receipt below.
- **Last owner-installed build:** v66 [artifact #13929551769](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/43187644879/artifacts/13929551769) (visual comparison and safe signed fallback; do not downgrade via direct install if Android disallows lower versionCode).
- **Next exact action:** Check both workflows on current source; if dual-green, extract actual Android QA APK, verify versionCode 67, package, signer, SHA and packed clean spines; share direct APK and GitHub ZIP. Ask for on-phone Library screenshot, opening Woodland/Space/Garden and install-over progress retention. Record owner acceptance or precise next visual objection before any more art changes.

## Final v67 source-matched APK receipt (November 12)

- **Actual code/test source:** [`c75287c5a8a03e968208337bb68226629c69b9ad`](https://github.com/PlayerEden/GridMates-Word-Search/commit/c75287c5a8a03e968208337bb68226629c69b9ad), unchanged by subsequent `[skip ci]` documentation updates.
- **Standard Android [#43192422437](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/43192422437): SUCCESS**, core/app tests, lint, Kotlin/Compose Android compilation, asset/source checks, signed debug APK, package ID and version verified.
- **Independent art/release [#43192423542](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/43192423542): SUCCESS**, debug and release compilation, source-pinned artwork, zero QA-art promotion into release.
- **Actual owner ZIP download:** [GitHub Android artifact #13930584152](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/43192422437/artifacts/13930584152), `GridMates-WordSearch-Android-QA`. Extracted `app-debug.apk` from this exact artifact, renamed in chat to `GridMates_WordSearch_WS16BH_v67.apk`; **29,383,061 bytes**, APK SHA-256 `01189576df4d0d977ebd74410a53911d2b58b42f538ff2831cdb39fc3bc48dcd`. ZIP size **28,914,855 bytes**, ZIP SHA-256 `f2903e037bfd19c5d3140d06edbbd2a4dce1fe76048649382f439d7fbbe3d078`.
- **Android package/signing from actual CI:** `com.playereden.gridmates.wordsearch.qa`; versionCode **67**, versionName `0.9.23-scalable-spine-library-qa`, original QA signing cert SHA256 `75cc3c896247d71fbd51f7a3ed41f90c8218a8a5a70df8c0f6b494b5cc23d419`, unchanged from v66. Supports install-over. No uninstall or data reset.
- **Independent artifact:** [art-QA #13931387628](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/43192423542/artifacts/13931387628), ZIP SHA256 `6e381ea8a17439c10cecc4e53ae3f9c453a9e126f420727563c6cbd31601cc70`. Its APK archive SHA256 `92926bae83c0683b667fc8443c942684958886f63c8adaa164f41341359877b1` differs, but direct comparison of ZIP-extracted APKs confirms **all 343 unpacked entries exactly match** in names and decompressed bytes. The Android QA artifact above is the installation reference.
- **Packed assets:** Original cleaned `ws16bf` binding family (manifest and 3 real PNGs) and the 3 `ws16ae` covers remain byte-identical. No new asset files or release-art rights are implied.
- **Open:** No owner phone screen/tap/resume verification of v67 yet. Do not claim visual acceptance or merge PR #131. v66 [artifact #13929551769](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/43187644879/artifacts/13929551769) is the last owner-installed baseline. Reverse-version install may need a deliberate QA rollback; do not instruct uninstalling or clearing app data.

**Next exact action:** Owner installs v67 over v66 and sends full Library screenshot; check smaller packed bindings, two/three shelf choice, legibility and touch selection, then taps Garden/Space/Woodland through cover preview and verifies saves. If needed make only owner-evidenced adjustments; spine-first architecture stays selected.

## Product decision for future volumes

The Library is **spine-first, always**, and expansion adds catalog entries rather than fake locked books. Illustrated front covers remain valuable for Home and book pull-out previews, and should be built alongside each future theme's distinct spine asset. This pass does **not** create the future themes or their artworks; it prepares capacity/paging for them.
