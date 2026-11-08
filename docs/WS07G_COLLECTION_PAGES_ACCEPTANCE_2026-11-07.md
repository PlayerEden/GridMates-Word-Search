# WS-07G Collection gallery paging — acceptance and source of truth

**Date:** November 7, 2026. **Status:** [draft PR #61](https://github.com/PlayerEden/GridMates-Word-Search/pull/61), stacked on verified Records UI PR #60. **v0.8.3/code37** is an unapproved QA candidate until full exact-source API35 verification; no two-phone or owner device test has been performed.

## Purpose

The user's first playtest highlighted inconvenient page scrolling. The Collection already has dedicated Portraits, Fonts and Achievements tabs, but all twelve Woodland portrait cards and all eight achievements could create long walls of content. A previous unmerged sibling experiment had implemented pagination; this PR **reuses only its Collection source**, without mixing in its alternate Records implementation. Existing local profile, Edit display name, free unlock rules, bundled fonts and open-license notices are retained.

## Product contract

- Portrait gallery: at most **4 cards per standard-font page**, **2** on narrow or enlarged-font screens. Previous / Page n of m / Next lets the user find all 12 portraits; equipped/unlocked values are unchanged.
- Achievements: at most **4 per standard page**, **2** for narrow/large font. The existing progress sorting and all eight achievements remain available.
- Pager buttons have at least **48dp** targets, specific UI test tags and are now placed **above** the portrait/achievement card stack so Next/Previous are discoverable without scrolling through the whole gallery. The independent page states survive ordinary tab switches.
- Fonts, profile naming, achievements and portrait unlocks do not write any new save fields; **schema4, LAN wire v7, QA package and signing remain unchanged**. No paid cosmetics, gacha or cloud dependency introduced.

## QA gates

- [ ] Standard Android CI: Kotlin core/app tests, lint, Android instrumented-test compile and QA APK.
- [ ] Real Compose 360×720dp scrolling page: portrait **Next** reveals page 2; Achievements **Next** is **visibly displayed immediately** after switching tabs and reveals page 2; Fonts still accessible.
- [ ] Existing Collection/settings navigation test reaches a later achievement through the pager.
- [ ] All twelve portraits and eight achievement definitions remain present in the underlying progress model after browsing; no progress mutation.
- [ ] Full API35 emulator: **93/93 expected tests** (92 prior + 1 new) green, no failure/skip.
- [ ] Exact same-QA-signer **v0.8.2/code36 → v0.8.3/code37** real APK installation-over, preserving schema4 Solo/Daily/identity/Race/Duel journals; capture source SHA, APK SHA-256, cert, artifact and rollback.
- [ ] Remain draft/unmerged, no production key/Play release. Original two-phone Co-op word selection issue #53 stays **OPEN** until owner physical testing.

## Deferred owner acceptance

When the owner is ready: compare portrait, font and achievement readability in portrait and landscape at 200% font size, verify a previously equipped portrait persists, check every page and back-navigation, and test TalkBack. **Do not request a phone test before the owner volunteers readiness.**

**Numbering note:** This PR's WS-07G gallery pass is *not* the master roadmap WS-07G integration/beta-readiness milestone, which remains blocked. The original WS-07F durability/performance audit is also incomplete.

[Roadmap](DEVELOPMENT_ROADMAP.md) · [previous WS-07F Records 92-test QA](WS07F_RECORDS_BUILD_QA_2026-11-07.md) · [P0 issue #53](https://github.com/PlayerEden/GridMates-Word-Search/issues/53).


## First API35 run and focused repair

[Initial WS-07G full emulator run #42433946720](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42433946720) confirmed actual signed v0.8.2→v0.8.3 install-over preserved Solo/Daily/identity/Race/Duel saves, but **1/93 Compose UI tests failed**: the achievements paging button was absent from the accessible tree after switching tabs from a scrolled portrait gallery. The other 92 tests passed. This is **NOT** a passing checkpoint. The UX fix relocates page controls above the card stack and strengthens the test to require the Achievements Next button to be immediately visible after selecting the tab. Re-run standard and complete emulator tests on the updated source before marking automated QA passed.
