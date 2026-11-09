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
