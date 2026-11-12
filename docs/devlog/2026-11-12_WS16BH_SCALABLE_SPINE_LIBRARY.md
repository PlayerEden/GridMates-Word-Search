# WS16BH — Spine-first, densely packed and scalable physical Library

> **Status (November 12, 2026):** SOURCE COMMITTED / EXACT-SOURCE ANDROID + ART CI IN PROGRESS / OWNER PHONE APPROVAL PENDING. Do not mark v67 install-ready until both workflows, package identity, SHA, signer and exact artifact are verified.

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

- **Android QA run [#43192422437](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/43192422437): PENDING** at log creation.
- **Independent art/release isolation [#43192423542](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/43192423542): IN PROGRESS** at log creation.
- **Actual v67 APK:** Not yet verified or delivered. If any test or compile fails, diagnose exact job first and document corrected source/runs; don't label queued or previous artifacts v67.
- **Last owner-installed build:** v66 [artifact #13929551769](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/43187644879/artifacts/13929551769) (visual comparison and safe signed fallback; do not downgrade via direct install if Android disallows lower versionCode).
- **Next exact action:** Check both workflows on current source; if dual-green, extract actual Android QA APK, verify versionCode 67, package, signer, SHA and packed clean spines; share direct APK and GitHub ZIP. Ask for on-phone Library screenshot, opening Woodland/Space/Garden and install-over progress retention. Record owner acceptance or precise next visual objection before any more art changes.

## Product decision for future volumes

The Library is **spine-first, always**, and expansion adds catalog entries rather than fake locked books. Illustrated front covers remain valuable for Home and book pull-out previews, and should be built alongside each future theme's distinct spine asset. This pass does **not** create the future themes or their artworks; it prepares capacity/paging for them.
