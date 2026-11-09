# November 9, 2026 — WS-14K1: First owner screenshots and compact gameplay header

> **Status: OWNER VISUAL REVIEW RECEIVED / NARROW UI SOURCE PASS COMMITTED / NEW CI PENDING.** Six genuine owner handset screenshots arrived after the exact-source dual-green WS-14J2 QA APK became available. The screenshots are evidence of visual layout, not of completed 20×20 drag, 5-tap hints, or persistence tests. The exact installed APK checksum has not been established from screenshots. Screenshots are intentionally not reposted to a public repository because their device status bar is visible; record observations without uploading personal device imagery.

## Who / what / where / why

- **Owner:** Sent six Android screenshots: Woodland 12×12 puzzle page; Woodland book-detail screen; swipeable illustrated bookshelf; Play main menu in two scroll positions; QA art lab.
- **Developer:** Assistant reviewed visible layout and GitHub source, then made a **small reversible WS-14K1 header/control pass** on existing draft [PR #131](https://github.com/PlayerEden/GridMates-Word-Search/pull/131).
- **Purpose:** The owner asked to maximize usable puzzle/word-list space using familiar compact symbols and avoid decorative/introductory text that competes with gameplay. This pass intentionally avoids new artwork or large redesign until the real screenshots have been evaluated.

## What the pictures actually show

1. **Puzzle screen:** An extra full row says **Back to book** and **Progress saved on device**; a second oversized row says **Woodland · Page 1** beside a tall bookmark and the icon-only Hint bulb. Then **0 of 12 words found · 0m 25s**, a progress line, and a separate persistent **Try selecting a word from the list.** hint/feedback line appear before an instruction/zoom toolbar (**Trace words · pinch to zoom**, Select, −, Fit, +) and the 12×12 board. The grid itself fills most horizontal width; only the first several word-list entries fit in remaining height.
2. **Source-linked observation:** The text `Try selecting a word from the list.` is produced by `ClaimStatus.INVALID` with no session-store warning in `BookPageGameplayScreen`. This alone does **not** establish a broken gesture engine; owner did not report a confirmed successful drag or persisted word claim.
3. **Woodland accent NOT visibly accepted:** The screen shows a tall **BOOKMARK** state illustration. Existing code gives bookmarked pages priority over `EMBLEM`, so the newly committed transparent `PAGE_ACCENT_WOODLAND_V1` cannot be visually judged from that screenshot. Do not call that new sprite reviewed or accepted.
4. **Book detail:** Illustrated Woodland owl cover integrates well with styling, but extra back text, progress card, **Start reading** and a second **Play** action increase vertical consumption. Chapter/page cards show large spacing and a **Saved** bookmark state. Explore consolidating *after* the first gameplay pass.
5. **Swipeable bookshelf:** Book artwork and adjacent Space peek look visually coherent; unnecessary pre-release/QA instructional labels and A/B comparison actions take space above the actual books. Maintain reversible QA comparisons for development but they should not dominate normal gameplay/release UI.
6. **Main menu:** Solo, Daily, Together and Puzzle Books navigation is discoverable, but a large `04 · PUZZLE LIBRARY · QA ONLY` card wraps awkwardly and the test-only **View Library Artwork** entry is prominent. Future polish should separate the QA Art Lab from a clean player-first default.
7. **Art lab:** Shows older minimalist WS-12 study assets. It is a developer comparison screen, **not** a contradiction of the illustrated owl on the actual bookshelf. Lab labels/status are not normal player-facing design.
8. **No functionality confirmation yet:** 20×20 trace, hint taps 1–5, zoom/Fit/Select and reopen/save are still unreported; screenshot 12×12 with 0/12 words confirms appearance only.

## Exact source change

- **Gameplay source commit:** [794f096e387e7d7b65b4de170e41480ec5e5d448](https://github.com/PlayerEden/GridMates-Word-Search/commit/794f096e387e7d7b65b4de170e41480ec5e5d448).
- `BookPageGameplayScreen.kt`: combines separate Back/Save text and oversized title into a **single** accessible 48dp back-arrow + compact native title/Page sublabel + optional small theme/status graphic + existing 48dp hint icon. Normal active sessions no longer waste a top row on redundant **Progress saved on device** text. Important Practice (unsaved) and Completed states stay visible. Save implementation and identifiers stay unchanged.
- `BookPageBackButton.kt`: new native Canvas vector arrow, accessible **Back to book** label, 48dp touch target, no image generation dependency.
- `ZoomableWordGrid` in `WordSearchScreen.kt`: adds default-off `compactControls` option. Book pages opt in, hiding the permanent **Trace words · pinch to zoom** instructional copy; **Select/−/Fit/+** still work and keep native/48dp hit targets. Solo/Daily behavior unchanged.
- Book-page low-priority invalid-tap and found-word acknowledgements now clear after 2400ms, preventing the persistent **Try selecting a word from the list** message from taking space for the rest of the session. Hints and save failure messages still remain.
- Updated existing `BookPageGameplayScreenAndroidTest` to expect title + page separately and confirm no persistent trace instruction; added source-compiled `Ws14KCompactBackButtonAndroidTest` for the icon action.
- Book covers, sprite masters, QA-only release isolation, font assets, original animal portraits, puzzle catalog, game board geometry, save format, hint quota, LAN and package identity are **untouched**.
- Actual heading icon artwork only appears in the unbookmarked, uncompleted WS-14 eligible state, just as before. This pass only sizes the old bookmark illustration down; later QA should compare the original Woodland fern on a page where no bookmark supersedes it.

## Build and QA evidence

- **Previously verified source:** WS-14J2 `01c20de`; Android [#42675610221](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42675610221) SUCCESS and Art [#42675610184](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42675610184) SUCCESS. [Artifact #13699334694](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42675610221/artifacts/13699334694), app-debug.apk SHA-256 `1df127b0fb655f5c9786729e10ca29440e5f1f40e8607c505fa956aa9260f8c5`.
- **Current candidate source:** `794f096e387e7d7b65b4de170e41480ec5e5d448`.
- **Current new CI:** [Standard Android #42679021745](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42679021745) and [Art/Release isolation #42679021766](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42679021766) **in progress** when first observed. Must recheck before calling it successful or offering the new APK.
- **No owner phone test** of the changed UI is claimed. Android instrumentation compile (when green) is not emulator/phone execution. Maintain older verified QA APK as comparison/rollback; do not uninstall a save-bearing installation without exporting data and confirming signing.

## Next exact action

Check both WS-14K1 workflow conclusions for the **same source SHA**; repair any Kotlin/compiler/test failure, confirm debug and zero-release-art package checks, record actual APK checksum and give owner a link to a reproducible QA artifact. Then owner can compare new 12×12 and 20×20 gameplay screenshots and report whether actual word tracing/hint/save worked. **After those results**, decide if more vertical space and icon controls are warranted, especially word-list/header layout, and separately tidy the library/main menu QA text. Preserve 48dp accessible targets and the existing responsive board. No full UI rewrite.
