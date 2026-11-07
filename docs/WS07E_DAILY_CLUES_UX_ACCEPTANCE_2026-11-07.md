# WS-07E — Compact Daily Journal & Saved Clue Review

**Date:** November 7, 2026  
**Implementation:** [draft PR #58](https://github.com/PlayerEden/GridMates-Word-Search/pull/58), stacked on WS-07D #57.  
**QA candidate:** v0.8.1, Android code35, unchanged package/QA signer, schema4, and LAN v7.  
**Owner physical testing:** **deferred** by owner; none requested, implied or claimed.

## Why this bounded pass

Earlier owner screenshots showed long, cumbersome pages and needlessly repeated interactions, especially before reaching a puzzle. The previous arc moved gameplay to fixed-size boards; this pass eliminates endless **Daily Journal** cards and solves a smaller but real clue problem. Hints already survive save/restart, but their descriptive text was transient, leaving players unable to reread a previously purchased clue.

These changes favor the app's paperback puzzle-book identity: readable date rows, a clear primary play action, calm progress and optional deeper information.

## Daily landing contract

- **Today first:** Compact title and short offline/no-deadline explanation, theme, current progress, and one primary action (Play today / Continue / Review). No extra confirmation or sign-in.
- **Earlier dates:** Chronologically newest-first, **paged** instead of unbounded cards; 3 dates/page at standard font, 2 with large fonts, clear Previous/Next and page count.
- Every stored date remains in the profile and is accessible on later pages. **Paging changes no saves, date identity, completion count, theme or seeded board.** Finite screen space should be spent on the current puzzle, not a history wall.
- Additional history rows do not require nested scrolling during regular play. The outer settings/launcher shell may still scroll at extreme font/device sizes as needed for accessible reachability.

## Saved clue contract

- Existing finite hint allowances are **unchanged**: Daily 2 steps, small Solo 2, 15×15 Solo 3, 20×20 Solo 4. This is **not** unlimited free hints.
- Previously purchased clue levels remain accessible after save and restart. Options offers **Review saved clues (N)** when any clue was used.
- After the remaining hint budget reaches zero on an unfinished puzzle, the visible Hint control becomes **Review clues** instead of a useless disabled button. It does not grant new information or spend a hint.
- Each entry shows only the **highest previously purchased level** for its specific word: row at 1, starting row/column at 2, existing gold-highlighted path at 3. The view cannot auto-find a word, give away a future clue level, change hints, mark found, alter time penalties or award completion.
- The in-game hint ladder remains the same seeded puzzle, so old saves, result assistance labels and backups retain their meaning. Daily and Solo use exactly the same recap.

## Automated acceptance

- [ ] Standard Kotlin build, core/app unit tests, lint, Android test compilation and QA APK succeeded.
- [ ] Simulated phone viewport (360×720dp) Daily page shows today's action, journal, and next/previous page controls without an endless historical card stack.
- [ ] Eight saved past dates are reachable across three pages; none are removed or completed by page navigation.
- [ ] UI can reread an exhausted 3-step hint without changing found IDs, hint counts, or result journal (puzzle time continues normally).
- [ ] A partial-budget clue is revisitable through Options without spending another step.
- [ ] Full API35 emulator instrumentation passes **all prior 87 tests plus 3 new WS-07E UI cases**, including prior 15×15/20×20 gestures, LAN host receipts and saved-data regressions.
- [ ] Real QA-signed v0.8.0 code34→v0.8.1 code35 APK install-over preserves schema4, Solo, Daily, identity, Race, Duel and settings. Record exact executable SHA, APK SHA-256, signer, artifact/expiry, rollback.
- [ ] The cumulative PR remains **draft/unmerged**; no production Play signing or cloud data changes.

## Later owner physical acceptance (not requested yet)

When owner opts in, compare Daily on their actual device(s), including 320dp screens and 200% text; check Today's button remains easy to find and all earlier dates remain reachable without endless scrolling. Verify the saved-clue recap in an in-progress puzzle, after exhausting hints, and across an in-place APK update. Try TalkBack focus/reading order and landscape. Real two-phone Co-op/Race/Duel and physical word selection remain separate **open P0 #53**; do not mark either as accepted on the basis of emulator tests.

## Follow-on limits

This is a **bounded first WS-07E UX pass**, not whole-game user acceptance. Further independent passes can audit profile/records/collection hierarchy, hints on the last unresolved word, results/share, and visual/touch ergonomics, but each must preserve approved save semantics and stay testable.

[Roadmap](DEVELOPMENT_ROADMAP.md) · [UX research](WS07_PLAYTEST_RESEARCH_AND_UX_SPEC_2026-11-07.md) · [WS-07D verified checkpoint](WS07D_BUILD_QA_2026-11-07.md).
