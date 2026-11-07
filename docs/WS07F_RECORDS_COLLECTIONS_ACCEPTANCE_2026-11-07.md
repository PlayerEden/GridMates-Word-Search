# WS-07F — Compact Records & Collections: UX acceptance

**Date:** November 7, 2026  
**Implementation:** [draft PR #59](https://github.com/PlayerEden/GridMates-Word-Search/pull/59), stacked on verified WS-07E #58.  
**Candidate:** QA-only v0.8.2 / Android versionCode36, unchanged `com.playereden.gridmates.wordsearch.qa`, same QA test signer, save schema4, LAN v7.  
**Owner phone tests:** **deferred until the owner says ready**. This document is an acceptance plan, not a claim of physical approval.

## Why this pass exists

The owner's nine screenshots and feedback exposed excessive scrolling, hard-to-reach controls, and a puzzle app that should feel calm, familiar and accessible on small phones. Previous WS-07A–E passes improved board tracing, progress, near-Wi-Fi feedback, Home/Daily and clue review. Records previously combined multiple game modes, stats and up to 15 Race, 12 Duel and 25 Solo cards in one long screen, silently hiding older history.

Collection already grouped Portraits, Fonts and Achievements, but showed the whole 12-item portrait gallery and 8 achievement panels at once. This makes players scroll past many locked items before they can understand what is available.

## New Records contract

- Enter **Records → Overview** first, with saved Solo, Daily, Race, Duel counts and existing word/assistance stats visible in compact metrics.
- Select **Solo**, **Race**, or **Duel** tab to view only that mode's local summary and past results. Races and Duels do **not** mix, and Co-op does not become a win/loss.
- Historical list pages show up to **four entries per page**, with Previous/Next and page count. All saved entries are browseable, not a `take(15)`/`take(25)` preview cutoff.
- Daily progress stays in the existing Daily Journal; the Overview reports its totals. No timer, score, win/loss, or data generation/migration is triggered by switching tabs or pages.
- Default empty records use understandable, specific play suggestions rather than blank or deceptive statistics.

## New Collection contract

- Existing Portraits tab retains the local profile display and **Edit display name**, with the current portrait clearly indicated. Fonts remain available as before.
- Portrait gallery is paged **4 items/page at normal width** and **2 items/page at narrow widths or enlarged text**, with complete access to all **12** Woodland portraits, their equipped/unlocked state and progress.
- Achievements are similarly paged at 4 or 2 items/page. All eight definitions and their earned/progress states remain reachable; sorting is still by unearned/remaining progress.
- No unlocks are purchased, removed, randomized or re-awarded. Changing pages is view-only and must not alter the profile or bundled assets/licenses.

## Automated acceptance

- [ ] Standard Kotlin/core/app unit tests, lint, instrumented-test compilation and QA APK succeed.
- [ ] Actual Compose: first Records view shows Overview and separate Solo/Race/Duel actions.
- [ ] Actual Compose: 13 historical Solo results yield 4 pages and page navigation, not a silently truncated list; Race/Duel tabs remain distinct with accurate empty states.
- [ ] Actual Compose: portrait 12 items accessible across three pages and achievement eight items across two pages at 360dp, including older entries (Badger, Three Chapters). Existing Fonts and Edit display name remain available.
- [ ] Existing settings/collection/portrait/achievements tests updated for pagination; all prior **90** Android cases remain green.
- [ ] Full API35 emulator test suite **92/92** (90 historical + 2 new) with zero failed/skipped, not just source compilation.
- [ ] Actual same-QA-signer **v0.8.1 code35 → v0.8.2 code36** APK install-over retains schema4 active Solo, results, Daily, identity, Race/Duel and settings. Pin exact tested executable source, APK SHA, signer and rollback checkpoint.
- [ ] Keep draft PR stacked/unmerged; `main` and production Play release unaffected.

## Later owner phone acceptance (NOT requested yet)

Check Records and Collections on the actual phone(s) at ordinary and 200% text size, narrow widths, landscape and TalkBack. Verify every tab and gallery pager can be reached without accidental swipe or infinite scrolling. Switch between tabs after unlocking/equipping a portrait and confirm the chosen item stays equipped. Check date labels and previous/next order on a profile with real completed history. Separately, the physical two-phone Co-op word-marking P0 [#53](https://github.com/PlayerEden/GridMates-Word-Search/issues/53) remains open and is **not** solved merely by UI/emulator success.

## Next independent polish opportunities

Share result previews, profile-card sharing, achievement hierarchy, accessibility-focused image semantics and reduced scroll in Settings can be assessed in later passes. None is assumed complete by this milestone.

[Development roadmap](DEVELOPMENT_ROADMAP.md) · [WS-07E verified QA](WS07E_BUILD_QA_2026-11-07.md).
