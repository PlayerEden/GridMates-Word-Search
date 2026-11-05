# WS-01 — Native foundation and Sudoku reuse audit

**Created:** November 5, 2026  
**Status:** Initial source scaffold / **not yet build or physical-device verified**.
**Work branch:** feature/ws01-native-foundation-2026-11-05

## Checked reference point

- Sudoku repository: https://github.com/PlayerEden/SudokuTogether
- Latest relevant draft PR: https://github.com/PlayerEden/SudokuTogether/pull/28
- Source branch: feat/gridmates-nearby-animals
- PR head observed: 975e31a1e8cbfebad1fbbc7602b3340c357da7ef
- QA receipt: https://github.com/PlayerEden/SudokuTogether/blob/feat/gridmates-nearby-animals/docs/V0.11C_NEARBY_WOODLAND_QA.md

The Sudoku default main is mostly documentation and workflow scaffolding. Do **not** use it as the current gameplay source.

## Reuse inventory / current disposition

| Sudoku source | Reuse value | First Word Search action |
| --- | --- | --- |
| GameTheme.kt | Accepted cream/forest/paper styling | **Adapted palette** into GridMatesTheme.kt |
| GameWidgets.kt | Navigation, typography, native UI patterns | **Reference only**; no brittle direct port yet |
| HomeScreen.kt / JournalScreen.kt | Game mode menu, profile/records/collections | **Adapted structure** with clearly labeled unfinished modes |
| JoinScreen.kt / RoomDiscovery.kt | Open nearby room, optional code, Wi-Fi discovery | **Deferred to WS-05**, requires distinct service name/version |
| LocalPlayerStore.kt / SessionStore.kt | Atomic private saving, recovery and schema migration | **Deferred to WS-03/04**; no Sudoku save files copied |
| Achievements.kt / Cosmetics.kt | Durable earned progress, stable IDs, metadata validation | **Deferred to WS-04**, Word Search-specific records |
| AnimalPortraits.kt + design/woodland | **12 original portraits**, stable ID/resource/manifest | **Reference only for now**; source PNG copy and provenance in WS-04 |
| DailyChallenge.kt | Offline stable daily puzzle and saved completion | **Deferred to WS-04**, new seeds/content pack |
| Protocol.kt / HostServer.kt / GameSession.kt | Socket, authoritative claims and reconnect | **Architectural reference**, do not import Sudoku snapshot schema |
| Sudoku.kt / HumanSudoku.kt | Sudoku rules / solver | **Not reusable**; new Word Search engine in WS-02 |
| Android/Compose Gradle stack | Proven AGP/Kotlin/Compose dependency versions | **Adapted**: AGP 8.9.2 / Kotlin 2.1.20 / Gradle 8.11.1 / compile 35 / Java 17 |
| CI and QA patterns | Build checks, APK artifacts, version and device QA | **Adapted** narrowly; not evidence of successful Word Search tests |

## What the initial scaffold actually does

- Independent native Android application skeleton with **proposed** package com.playereden.gridmates.wordsearch; QA suffix .qa.
- Native Compose Home/Records/Collection/Settings/Help navigation, familiar palette, and a temporary vector launcher icon.
- All five planned modes appear as **planned**, not as fake playable buttons.
- Tiny pure-JVM core enum recording approved mode IDs, with unit tests.
- Manual/pull-request Android QA workflow that uses pinned Gradle and Android SDK on GitHub Actions.
- No audio, LAN networking, purchases, advertising, user data storage, portraits or gameplay are claimed to be implemented.

## Important caveats and next evidence

- The source tree currently **does not include the Gradle wrapper binary**. The CI workflow installs Gradle 8.11.1 using the official setup-gradle action; local builds require that Gradle version or a properly generated project wrapper. Add a verified wrapper in a follow-up pass.
- No downloaded original Woodland PNG resources yet. Do not fabricate versions of existing assets.
- No actual APK exists until a successful CI build, and no physical install test has been completed.
- APK signing defaults to local debug/QA test signing. Future production signing is a separate protected flow.
- When CI runs, record URL, test results, lint output, artifact SHA-256, install package, screenshot evidence, and any errors. **Never mark WS-01 COMPLETE until evidence and safe co-installation with Sudoku exist.**
- Target/compile SDK versions come from the current Sudoku prototype and are not an assertion of current Google Play policy; verify again before release.

## Quality gates

1. Compile native app and core unit tests; run Android lint.
2. Build QA APK; verify actual install package and launch name.
3. Install alongside Sudoku on Android without replacing/clearing Sudoku.
4. Verify Home, Collection, Settings, Help and long-text/landscape navigation.
5. Record stable commit and checkpoint after acceptance.

**Next stage:** WS-02 deterministic Word Search engine and tactile grid input.

## Review checkpoint — CI registration

- [Draft PR #3](https://github.com/PlayerEden/GridMates-Word-Search/pull/3) now tracks the independent source branch.
- The same pull-request QA workflow was registered on the default `main` branch so the new repository can discover it. This is CI configuration only; **the Word Search app source remains in the draft feature branch**.
- Build/installation evidence is still outstanding. Check actual workflow runs and keep this document honest if Actions is disabled or fails to start.
