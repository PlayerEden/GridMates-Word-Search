# WS-09F-B2 — Saved large-board viewport: implementation + QA handoff

**November 8, 2026 | Draft PR [#74](https://github.com/PlayerEden/GridMates-Word-Search/pull/74) | v0.9.4/code48 candidate, NOT source-verified for installation.**
Parent [WS-09F-B #73](https://github.com/PlayerEden/GridMates-Word-Search/pull/73) **v0.9.3/code47** is the last fully verified build: [111/111 Android tests, real signed install-over, rollback and APK hash](WS09F_B_BUILD_QA_2026-11-08.md).

## Implemented — still unverified by complete Android CI
- Optional per-puzzle `LargeBoardBookmark` saves 1×–12× zoom, normalized center, and exact puzzle ID; restores across board viewport sizes/rotation, and ignores stale/invalid data instead of corrupting gameplay.
- On **25×25/30×30 Solo** boards the viewport is captured on zoom-button/Map actions and once after a two-finger gesture, *not on every animation frame*; reopened games restore it. Existing 8–20 boards, Daily and Nearby are unchanged.
- Bookmark is an optional field on each `SoloRun`, including paused sessions, in the **unchanged schema-5 AtomicFile** and manual JSON export/import. It is not stored separately in preferences. Conflicting sidecar code was removed. Historical v1/v2/v3 generator output, saved found IDs/hints/timers, LAN v7 and QA-only signing identity must remain unchanged.
- JVM/Android candidates cover normalization, map edges, stale metadata, multi-session resume and backup, Compose Map/Fit, 24-slot compatibility. New test runner targets **114 Android tests** and a real v0.9.3/code47 → v0.9.4/code48 same-signer upgrade.

## Verification is blocked by external HTTP 504 dependencies
- [Dedicated emulator run #42467956540](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42467956540) got through Gradle setup but `:core:compileKotlin` could not obtain `kotlin-compiler-embeddable-2.1.20.jar` due GitHub-release redirect **HTTP 504**, before compiler or tests ran.
- [Standard QA #42467961149](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42467961149) was blocked earlier provisioning Gradle 8.11.1 (**HTTP 504**). Neither is evidence that Kotlin source compiles, fails, or that the signed upgrade/114 tests pass.
- **Do not recommend installing code48 yet.** Preserve verified v0.9.3/code47 as fallback. Avoid repeated costly Actions re-runs until dependency downloads recover. Next exact-source pass: Gradle core+app tests/lint/Android-test compile/APK, signer and code47→48 upgrade, 114/114 API35 instrumentation tests, raw APK checksum, immutable checkpoint and updated PR receipt.

## Still open
Owner handset portrait/landscape/200% fonts, genuine word claims, multi-hour resume and frame/memory profiling; older family APK signer and independently verified restorable backup; real two-phone Co-op claim issue [#53](https://github.com/PlayerEden/GridMates-Word-Search/issues/53); separate 40×40/50×50 generator laboratory [#75](https://github.com/PlayerEden/GridMates-Word-Search/issues/75), compact UI [#76](https://github.com/PlayerEden/GridMates-Word-Search/issues/76), integration QA [#77](https://github.com/PlayerEden/GridMates-Word-Search/issues/77). No merges, new production signer or Play launch.
