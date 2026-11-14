# WS-16C — Remove duplicate bottom navigation; add functional Profile hub

**Date:** November 9, 2026. **Branch:** feature/ws14b-style-anchor-and-art-direction-2026-11-08. **Draft PR:** #131.
**Source checkpoint:** 3d3a6ded4786645637ca46e1f182997e3e153aef. **CI initially running; do not claim green or link a new APK until verified.**

## Owner phone feedback and instruction
After reviewing actual WS-16B Home/menu screenshots, the owner agreed to remove the Play/Records/Collection/Settings bottom bar because Profile and Menu at the top already provide access. The footer took valuable height and obscured the bottom of the illustrated Home actions. Owner approved continuing with that navigation simplification.

## Actual implementation
- Removed the old always-visible NavigationBar/NavigationBarItem footer from GridMatesShell.kt; Scaffold now uses whole available Home viewport. Deleted obsolete private bottom-nav glyph implementation. The existing illustrated carousel, Home controls, QA-only honest Quick Play preview, Custom, Daily, Together and saved puzzle/backup implementations stay intact.
- Introduced Page.PROFILE and new real Ws16CProfileHub.kt. The Home avatar opens a local player summary with stored name, portrait when asset available, puzzles/words totals, working Collection (portraits/fonts/achievements), Records and My Puzzles buttons. No forced Google account, global leaderboard, network or new save schema.
- Profile-origin Records and Collection return to Profile; Menu-origin Records and direct Settings return Home. Preserved Android system Back for all non-home and non-nearby routes and added visible native Ws15BackHeader controls to Records, Collection and Settings. Profile has its own Back to Home arrow. Existing Book/Help/Daily/My Puzzles/Solo setup back controls are retained. Nearby owns its own exit/confirmation flow as before.
- Updated actual Android test sources for new navigation: Ws16CarouselHomeAndroidTest, IntegratedNavigationAndroidTest, ShellSmokeTest, CompactNavigationNearbyAndroidTest (200% font), CollectionSettingsPolishAndroidTest, PaperbackAppPolishTest, SoloPreGameNavigationAndroidTest and NearbyCoopUiEntryTest. Removed assumptions of old footer accessibility labels and verified intended new Profile/Menu routes.
- No book content gating changes, no release-art promotion, no changes to Solo IDs, books, daily, LAN save or other persistent data.

## QA / acceptance
Exact source workflows should be checked on **3d3a6ded**: Android QA (core unit tests, app unit tests, lint, instrumentation SOURCE compilation, debug APK build) and original-art/release isolation. Instrumentation compile is not on-device execution.
On owner's phone: confirm Home has no bottom nav, Quick Play/Custom/My Puzzles are reachable sooner, Profile portrait/counts, profile → Collection → Back to Profile, profile → Records → Back, Menu → Settings → Back to Home, Menu → Records → Home, book focused-cover direct-play/Back, Daily and Together entry, and no 320dp or large-font clipping. No new production content approval inferred.
Last dual-green physical fallback: WS-16B Android QA ZIP https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42714316623/artifacts/13717317690; APK SHA-256 6e4ff68635727ec3a60a5a0f88ab4c1207cc26c13e75918b5614361ff5bd3f39.
Keep draft PR unmerged until owner confirms on-phone navigation and layout.

## Next
If exact CI fails, diagnose and fix. Then give one verified WS-16C APK ZIP and checksum to owner. After phone screenshots, tune book/quick-action proportions only if the freed footer space proves insufficient; separate WS-17 mixed-vocabulary Quick Play remains unimplemented.

## Final exact-source CI / handoff receipt

**Both exact-source pipelines finished SUCCESS on source SHA 3d3a6ded4786645637ca46e1f182997e3e153aef** (subsequent [skip ci] docs commits are not new Android builds).

- [Android QA run #42717003929](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42717003929): **SUCCESS** — unit tests, lint, instrumentation test source compiled, debug APK assembled. Android instrumentation source compilation is **not actual physical-device execution**.
- [Art + debug/release isolation #42717003902](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42717003902): **SUCCESS** — six QA-only book cover/spine sprites absent from release; verified 18 original + 18 alt portraits and 36 native PNGs; theme motif checks passed; no debug artwork leak.
- **Owner test artifact:** [WS-16C Android ZIP #13717803408](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42717003929/artifacts/13717803408), contains `app-debug.apk`. Historic GitHub ZIP name still mentions Woodland but actual code is complete WS-16C.
- **APK SHA-256:** `42bebe4ddcfef4056d25ddb562f1ef2abab2df857dd0bef8d5437bf97a9a4bbc`.
- **Artifact ZIP SHA-256:** `d66292a29d196587ab008872eed303bcf588d9fbe590bf320ebb7cb5bbabfebf`.
- App package `com.playereden.gridmates.wordsearch.qa`, versionCode `57`, versionName `0.9.13-visual-kit-foundation-qa`. VersionCode same as prior QA; don't assume automatic update/signing compatibility. Backup/export saves before any uninstall or signing-conflict fix.
- **Owner phone review still pending for WS-16C.** Keep PR #131 DRAFT/unmerged and old WS-16B artifact as fallback. Do not claim new branding or book pages production-approved.
