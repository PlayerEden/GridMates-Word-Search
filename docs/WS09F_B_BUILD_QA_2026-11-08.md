# WS-09F-B — Visible-area rendering / source-matched Android QA

**Date:** November 8, 2026 ET.  
**Draft stacked PR:** [#73](https://github.com/PlayerEden/GridMates-Word-Search/pull/73), based on [WS-09F-A #72](https://github.com/PlayerEden/GridMates-Word-Search/pull/72), not merged into `main`.  
**Exact tested executable commit:** `57cd756a7811fb94da477367a51de70a8dd6a50c`. Subsequent documentation commits are not a newly compiled executable.  
**QA APK:** `0.9.3-visible-cell-culling-qa-alpha-qa` / **versionCode 47**, `com.playereden.gridmates.wordsearch.qa`. **Save schema 5 and LAN protocol v7 unchanged.**  
**Dedicated real Android API35 emulator + signed historical upgrade:** [workflow #42464409118](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42464409118) **SUCCESS**; **111/111 instrumentation tests, 0 failed, 0 skipped**. The job also ran core tests, app unit tests, lint, Android-test compilation and APK assembly successfully.  
**Standard Android CI:** [workflow #42464414276](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42464414276) initially **failed before compilation**, at `gradle/actions/setup-gradle@v4`: Gradle 8.11.1 download received two HTTP **504** responses. This is not evidence of an app source/test failure. A failed-job re-run was requested; check the linked workflow for its final attempt status rather than treating this receipt as a second standard-CI pass.

## Exactly what is implemented

- Existing **25×25 and 30×30** Solo boards preserve the full legacy draw path at **Fit (1×)**. Only when zoomed above 1×, a pure `BoardRenderWindow` computes the visible row/column intersection plus one-cell overscan, bounds letter text calls and interior grid lines, and skips completely off-screen hint, found and active marker strokes.
- Drawing stays on one **continuous** puzzle: a word crossing a 10-cell map/region boundary remains eligible for a visible highlight, even if its endpoints are elsewhere. No clipping/splitting of generated puzzle content.
- The new `BoardGlyphPainter.drawLetters` accepts an optional window; a missing window maintains the historical complete board draw. Smaller **8–20 cell** boards, collection previews and older font behavior remain unchanged.
- Pure Kotlin tests cover culling bounds and intersections on **25/30/40/50/75/100-cell geometry**, corners, full-Fit equivalence, and the reduced visible cell budget. This is a draw-call reduction assertion, **not a measured FPS or memory benchmark**.
- New Android image parity test compares full-rendered versus culled visible letter ink at zoom and pan, using the actual Android canvas. Existing 110 on-device regressions continue to pass.

## Real signed progress-preservation test

The emulator downloaded the exact previously accepted **v0.9.2/code46** QA APK and verified its SHA-256. The old executable launched, read and flushed a valid schema-5 profile containing:
- A partly completed active Solo run with a real found-word ID, hint steps and elapsed time;
- Two independently partly completed paused Solo puzzles;
- Two Daily entries with progress, plus profile identity and populated Race/Duel history.

The job verified old and new APK have identical pinned test signing certificate, installed new **v0.9.3/code47 over the old app with `adb install -r`**, launched/paused the new executable, and checked decoded `active`, `pausedSolo`, `results`, `daily`, `identity`, `races` and `duels` for **exact equality**. **PASS: no saved progress loss in this tested historical fixture.** This does not prove compatibility with an uninspected family member's APK/signing identity.

## Auditable artifacts and rollback

- **Raw v0.9.3 APK SHA-256:** `a81f37daa6b63244e9612476c5925ca4b2074fa67378db8bbef8aae054c6b129` (not the artifact ZIP hash).
- **Public TEST-ONLY signing certificate SHA-256:** `75cc3c896247d71fbd51f7a3ed41f90c8218a8a5a70df8c0f6b494b5cc23d419`. Never use this signer for the Play production app.
- [Cumulative WS-09F-B QA APK artifact #13611208024](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42464409118/artifacts/13611208024) (artifact ZIP contains APK).
- [Full WS-09F-B Android test reports #13611287910](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42464409118/artifacts/13611287910).
- Both artifacts expire approximately **November 15, 2026, 08:58 UTC**; retain the exact SHA and reproducible branch/runner recipe.
- [Immutable exact-source rollback checkpoint](https://github.com/PlayerEden/GridMates-Word-Search/tree/checkpoint/ws09f-b-v093-visible-culling-111-tests-upgrade-green-2026-11-08). Prior [v0.9.2 rollback](WS09F_A_BUILD_QA_2026-11-08.md) remains intact.

## Not yet accepted — WS-09F-C and launch gates

1. **Real-device performance:** capture median/p95 frame times, jank/dropped frames, CPU time and memory/allocation at Fit, 3×, 6×, 12×; compare full-vs-window renderer at 25/30; then prototype 40/50 on devices before offering them. Emulator cell-budget proof alone does not satisfy this.
2. **Viewport persistence:** resumed 25/30 game currently returns to Fit; design backward-compatible per-puzzle normalized zoom/center/bookmarks (with validation, backup/restore and no change to historical IDs). Do not let a stale viewport state destroy an unfinished puzzle.
3. **40×40/50×50:** isolated versioned content/generator laboratory, curated pack density and unique-occurrence verification; not yet generated or user-selectable. **75×75/100×100** remain aspirational lab sizes with no device performance sign-off.
4. **Physical acceptance:** owner narrow phone, landscape, large/200% font, precise swipe/two-finger pan, border glyph readability, long-session resume, older family APK version/signer and independently tested backup remain OPEN. Actual two-phone Nearby Co-op word marking [P0 #53](https://github.com/PlayerEden/GridMates-Word-Search/issues/53) remains OPEN.
5. **Release:** no production signing, no Play submission, no merges of the stacked draft PR chain. Do not direct anyone to uninstall an existing saved-progress app without verifying the older build and a restorable backup first.
