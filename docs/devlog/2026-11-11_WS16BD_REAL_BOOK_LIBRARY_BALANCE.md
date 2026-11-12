# WS16BD — Real-book-first Library composition

> **Quick status:** SOURCE COMMITTED / AUTOMATED QA IN PROGRESS / OWNER VISUAL APPROVAL PENDING (November 11, 2026). v63 is an experimental alternative to v62, not a confirmed improvement.

## Who, what, where, why

- **Owner feedback:** After installing verified WS16BC v62 and sharing portrait screenshots of the Word Search Home and Library, owner replied **"Meh"**. No individual element was explicitly approved or specified as the cause. The screenshots show three real book spines crowded into one upper tier, an equally prominent "Coming soon" tier with faded decorative books, and a highly ornate cabinet compared with the calm, readable paperback Home.
- **Work:** ChatGPT's proposed design response, implemented as a reversible source pass on draft [PR #131](https://github.com/PlayerEden/GridMates-Word-Search/pull/131) / `feature/ws14b-style-anchor-and-art-direction-2026-11-08`.
- **Objective:** Make a small real catalog occupy the actual cabinet meaningfully, remove ghosts from default display and make real spines larger rather than endlessly shifting plaques.

## Starting point and fallback

- Previously verified source [`0f99722e`](https://github.com/PlayerEden/GridMates-Word-Search/commit/0f99722e00cd4ebcf0d30e826c6d90c5676d21c0); both v62 workflows SUCCESS: [Android #43102415463](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/43102415463) and [art/release #43102415476](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/43102415476). v62 GitHub [APK ZIP #13893295859](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/43102415463/artifacts/13893295859), APK SHA `de09063fd9ba65fa279167486ee094ddf27d2afab4b45e8bc1840e6d7be5fa50`, signed with existing public QA test key, installed on owner's phone based on owner screenshots.
- v62 remains safe last source-verifiable rollback. Do NOT recommend uninstalling, downgrading without proper support or clearing player progress. Keep draft unmerged and all QA-only art out of release.

## Implemented changes

1. `WS16ATModularLibraryScene.kt`: New sparse-collection portrait logic activates when there are 2 through `slots` real books, one catalog page, and two tiers. Split real books in catalog order across both real shelves (3 books = 2 up, 1 down; 4 books = 2+2). Make both tiers 50/50 of the space remaining after count clearance. Set spine slots to 60dp and center each tier by actual book count, not by an invisible full-row alignment. This changes only presentation, not catalog order, book IDs, tap callbacks, pagination or progress.
2. Modular Library no longer displays future fake spine bindings. `WS16AMSpineBay` and original artwork sources remain unchanged for other/legacy paths. The standalone `Coming soon` plaque is now only shown if the current paged cabinet has **zero** real books; ordinary small real collections never devote half the experience to future placeholders.
3. `QaActualBookcaseAndroidTest.kt`: Rework portrait UI checks to assert real Woodland/Space on upper shelf, Garden on lower, absence of fake books and "Coming soon" sign, count below lower carving, and stable real-book tap.
4. `test_ws16at_modular_library.py`, `test_ws16bb_shelf.py`: Source-contract coverage of sparse split/centering and no ghost volumes; preserve original independent wood/art, page count, scene layering, debug-only split and QA signer.
5. `app/build.gradle.kts`: versionCode **63**, versionName `0.9.19-balanced-real-book-library`, same `com.playereden.gridmates.wordsearch.qa` and existing public QA signing config for install-over v62.

**Intentionally untouched:** Actual book cover/spine PNGs and SHA pins, original matched cabinet, Home carousel and illustration, Profile/Collection, local progress, puzzle generation/touch/hints, LAN, user profiles, achievements, release signing, backend protocols. No image rights or production-art approval implied.

**Exact candidate source:** [`2cad9c478362f7e49353be03140349ba33f7463f`](https://github.com/PlayerEden/GridMates-Word-Search/commit/2cad9c478362f7e49353be03140349ba33f7463f).

## Build and QA

- New [Android QA #43141264381](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/43141264381) **IN PROGRESS** on source `2cad9c47` at record creation.
- Independent [art/release isolation #43141264469](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/43141264469) **QUEUED** on same source.
- v63 APK **NOT YET VERIFIED**; do not claim availability, signing/hash, success or any on-device preview before actual evidence.
- CI tests build and compile Android tests but are not a real owner handset visual review; note final workflows/artifact and any failures when available.

## CI correction — adaptive slot contract

First v63 code source `2cad9c47` failed [Android #43141264381](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/43141264381) and [art #43141264469](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/43141264469) **before Android compilation** because one inherited Python source test still asserted the literal `slot = hitSlot`. The new sparse catalog correctly uses `slot = if (balancedSparseLayout) 60.dp else hitSlot` while preserving the standard 48/54dp hit slots for larger catalogs. Fixed the assertion rather than reverting the implementation.

**Latest candidate code/test commit:** [`c54b4b18afbe9e2525238f47521fe770daea75f7`](https://github.com/PlayerEden/GridMates-Word-Search/commit/c54b4b18afbe9e2525238f47521fe770daea75f7). Corrected [Android QA #43141513582](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/43141513582) and independent [art/release #43141513574](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/43141513574) **IN PROGRESS** at log update. No APK/owner handset approval yet; must verify exact source and package before delivery.

## Outcome and next step

**Completed:** implementation, source/Android test changes, QA version bump, initiated independent dual-CI runs. The direction is a candidate, not an approved replacement for v62.

**Next action:** Inspect both workflows and repair genuine failures; if both pass, extract Android QA APK, verify package/version/signer, inspect binary contents, and give owner a direct v63 APK and GitHub artifact for comparative screenshots. Verify tapping upper/lower spines and save/resume on the same installed QA app. If v63 looks worse, switch to a more structural Library UX proposal rather than another small spacing tweak.
