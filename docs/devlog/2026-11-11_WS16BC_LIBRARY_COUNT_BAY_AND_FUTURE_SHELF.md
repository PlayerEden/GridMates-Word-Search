# WS16BC — Shelf-anchored book count and honest upcoming volumes

> **Quick status:** DUAL-GREEN CI / EXACT APK VERIFIED / OWNER PHONE VISUAL AND INTERACTION TEST OPEN (Nov 11, 2026).

## Who, what, where, why

- **Owner:** Continued the Word Search project after reviewing real WS16BB Library and Home screenshots, requesting another focused polish pass rather than a redesign.
- **Work:** ChatGPT implemented a source-level Kotlin/Compose adjustment in draft [PR #131](https://github.com/PlayerEden/GridMates-Word-Search/pull/131) on `feature/ws14b-style-anchor-and-art-direction-2026-11-08`. CI verifies builds separately; only the owner can accept actual phone feel.
- **Objective:** Remove the visually wasted lower-cabinet gap and ensure the book-count plaque always clears the wooden shelf carving while keeping unlocked volumes distinct from decorative placeholders.

## Where we came from

- Previous [WS16BB entry](2026-11-11_WS16BB_MATCHED_SHELVES_AND_LIBRARY_COHESION.md): version 61, 586×125 matched shelf with 42dp contact plane, two shelf tiers, faded future volumes, matching Profile/Collection frames. Both previous workflows reported green; [v61 QA ZIP #13888564089](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/43092038677/artifacts/13888564089) is the last supplied APK fallback.
- Owner screenshots show a very large wood area under the lower shelf and a book-count placard that looks attached to/too close to the lower shelf ornament.
- Keep the existing offline-first behavior, real book IDs, cover/modal routing, local saves, LAN, original art files and debug-vs-release art isolation intact. Do **not** merge the draft PR.

## Actual code changes

1. `WS16ATModularLibraryScene.kt`: derive the cabinet shelf's bottom overhang from the actual portrait source ratio (125/586), clamp to renderer dimensions (42–72dp), subtract the existing 42dp contact-plane depth, then reserve measured clearance + a 12dp gap (8dp for enlarged text) + a 38dp count budget (52dp enlarged text). For normal two-tier layouts, divide the remaining space 54%/46% for actual shelf/book areas. Place native `3 books` count inside a dedicated `ws16bc-count-bay` following the lower shelf, rather than positioning an overlay by absolute distance from the screen bottom. One-/three-tier fallbacks retain the existing bottom count.
2. `WS16AMInteractiveBookcaseScene.kt`: optional `showFutureVolumes` keeps catalog click zones unchanged while hiding upper-shelf dummy bindings in the new modular layout. Two subdued, unclickable future spines stay on the lower labeled upcoming shelf. Existing older Library routes preserve prior behavior. Tag actual rendered future assets for explicit test coverage.
3. `QaActualBookcaseAndroidTest.kt`: test that the count sits below the lower tier, there are no rendered top-shelf ghosts, the lower shelf retains upcoming visuals, and a real book still selects by its stable ID.
4. `app/build.gradle.kts`: QA versionCode 62 / versionName `0.9.18-library-count-bay-and-future-shelf`, same QA application ID and existing public test signer. This is an install-over **candidate**, not proof of an install.
5. No artwork files, assets, personal data, save schemas, network protocol, gameplay, release signing or puzzle-generation code changed.

**Implemented source commit:** [`a9b9d98c`](https://github.com/PlayerEden/GridMates-Word-Search/commit/a9b9d98c0618010eedbbb3a3d8a40e615f3d09e6) (preceded by focused Kotlin/test commits on this same branch).

## Build and QA receipt

- **Build requested:** automatic GitHub Actions on code commit `a9b9d98c`.
- **Android QA:** [run #43101804838](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/43101804838) — pending at journal creation.
- **Art/release isolation:** [run #43101804933](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/43101804933) — pending at journal creation.
- **APK:** **NOT YET VERIFIED.** No artifact, SHA-256, installed-on-phone result, or dual-green claim at journal creation. CI may catch further issues.
- **Fallback:** previously supplied WS16BB version 61 [artifact #13888564089](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/43092038677/artifacts/13888564089). Do not uninstall or clear saved data to switch visual builds.
- **Owner testing still needed:** check lower shelf/plaque on actual portrait phone, choose each themed spine, verify View pages and saved progress, compare Collection/Profile, inspect narrow/large-font and landscape fallback. Production art rights and release gate remain separate.

## CI failure and regression-test repair (same pass)

The first v62 source `a9b9d98c` failed both pipelines **before Android compilation**, not because of a crash or broken art: inherited source-only tests in `test_ws16at_modular_library.py` and `test_ws16bb_shelf.py` still expected the literal `?: 61` and fixed 46%/40%/14% old geometry. [Android #43101804838](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/43101804838) and [art #43101804933](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/43101804933) both reported FAILURE at this stage. Repaired both tests to check `versionCode >= 61` using the actual gradleProperty version expression, assert the new source-backed count/shelf contract and the unchanged signer. Corrected Python regex escaping before resubmission.

**Latest Android/code source:** [`0f99722e`](https://github.com/PlayerEden/GridMates-Word-Search/commit/0f99722e00cd4ebcf0d30e826c6d90c5676d21c0). New Android QA [#43102415463](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/43102415463) and independent art/release [#43102415476](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/43102415476) started; at correction journal update, their outcome is **still pending**. These are the runs to trust for v62, not the failed earlier runs. No APK is verified until both pass and its actual contents and certificate are checked.

## Final verified v62 QA receipt — November 11

- **Compiled code:** [`0f99722e00cd4ebcf0d30e826c6d90c5676d21c0`](https://github.com/PlayerEden/GridMates-Word-Search/commit/0f99722e00cd4ebcf0d30e826c6d90c5676d21c0); later commits are documentation-only `[skip ci]`.
- **Android QA:** [run #43102415463](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/43102415463) **SUCCESS** including Gradle unit tests, lint, build, signing certificate check, package identity, and exact packaged art verifications.
- **Independent art/release:** [run #43102415476](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/43102415476) **SUCCESS**, including compile and release-art exclusion.
- **GitHub owner artifact:** [ZIP #13893295859](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/43102415463/artifacts/13893295859) (expires December 11, 2026), extracted `app-debug.apk` length **29,054,920 bytes**, APK SHA-256 `de09063fd9ba65fa279167486ee094ddf27d2afab4b45e8bc1840e6d7be5fa50`; artifact ZIP SHA-256 `18f714a7b1a9188dc8f8aedfe788478cd0c356bfc22871762dc093215f83809f`. Direct APK and ZIP were also delivered as ChatGPT conversation files; GitHub is the durable source.
- **Identity:** `com.playereden.gridmates.wordsearch.qa` versionCode **62**, versionName `0.9.18-library-count-bay-and-future-shelf-qa`, unchanged QA signing cert SHA-256 `75cc3c896247d71fbd51f7a3ed41f90c8218a8a5a70df8c0f6b494b5cc23d419`, supports install-over v61 without an uninstall/data reset.
- **Second artifact:** [art-QA ZIP #13891329462](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/43102415476/artifacts/13891329462) contains a different APK SHA-256 `b10814e0f39974ae91d8a40e20202d947598685d4b0bb18a37f761050b5f90de`; however, direct inspection confirms all **339 uncompressed package entries have identical content bytes and names** to the Android QA APK. Do not claim the APK archives are byte-identical. Use the standard Android QA artifact for the owner.
- **Limitations:** Automated compilation/tests are not a physical touchscreen test. Real handset visual approval, book selection > View pages, resume/saves, Profile/Collection, portrait and accessibility checks are OPEN. Keep PR #131 draft and debug art unreleased; previous WS16BB v61 [artifact #13888564089](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/43092038677/artifacts/13888564089) is the rollback.

## Next exact action

**Owner physical v62 QA:** install directly over v61 (no uninstall), inspect cabinet shelf/plaque/Coming soon arrangement, tap real Woodland/Space/Garden bindings, View pages, Back and save/resume, check Collection/Profile. Request screenshots and regressions before another decorative iteration. When approved, proceed to a project-wide playability/readiness audit instead of more visual-only tweaks. PR #131 stays draft.

**New-chat one-liner:** WS16BC v62 dual-green source `0f99722e`; Android #43102415463 / art #43102415476; owner APK #13893295859, SHA `de09063f...`, same QA signer. Actual phone visual and interaction acceptance pending. Draft PR #131, v61 fallback #13888564089.
