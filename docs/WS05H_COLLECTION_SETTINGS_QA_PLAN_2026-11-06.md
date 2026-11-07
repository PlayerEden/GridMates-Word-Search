# WS-05H — Profile, Collections, Achievements & Settings Polish

**Date:** November 6, 2026.  
**QA target:** v0.5.7 `com.playereden.gridmates.wordsearch.qa`, versionCode 19.  
**Parent:** [WS-05G draft PR #37](https://github.com/PlayerEden/GridMates-Word-Search/pull/37) → WS-05F/E/D/C/B/A and B5. All cumulative work stays unmerged and locally testable in one Android QA APK.

## Product intention

Give every non-gameplay menu a consistent, believable paperback puzzle-app feel. Users should see **their real progress and available unlocks**, understand that the app is local/offline, and be able to protect saved puzzles without risking accidental data replacement. Controls must remain readable for smaller phones and larger Android system fonts.

## Collection / profile

- Clear player-profile panel with the **actual equipped Woodland portrait**, saved local display name, real Solo/Daily completion counts, and full-width Edit display name control. The name dialog explicitly states the name is visible to a local Wi-Fi co-op partner; no account exists.
- New collection-progress panel: **real earned portraits and achievements**, honest 0%-to-earned progress, and the **next available Woodland portrait** plus number of completed puzzles remaining. After all unlocks, it says the collection is complete, rather than fabricating a new goal.
- Earned portraits and board fonts are outlined when selected. Locked portraits show **remaining puzzles** and the actual milestone progress track, without relying on opacity alone. Original 12 approved Woodland illustrations remain untouched.
- Achievements list unearned items first (closest to completion among unearned), then earned ones. Each entry has its full description, earned/counter status and truthful progress without a decorative 0% marker. Exact achievement conditions and free font requirements are unchanged.
- Portrait/font grids automatically change from two columns to **one column below 350dp available width or at fontScale >=1.3**. Font licensing notices remain available from every font card, even when locked.

## Settings / save safety

- Consistent paperback panels for **Backup & restore**, Gameplay preferences and About.
- **Export backup** and **Restore backup** are explicit actions. They stack vertically at narrow widths and large text; otherwise align side by side. The export filename and SAF document picker remain intact.
- Restore-preview dialog is scrollable and summarizes actual local identity, records and active puzzle; prominently warns that restoring **replaces current Word Search progress**, while offering **Keep current**. Preview and confirmation still call the existing validated restore implementation; no silent replacement.
- Success/errors use a polite live region and user-controlled Dismiss. About screen lists actual package version and accurately describes Offline Solo/Daily, same-Wi-Fi Nearby Play, local storage and absence of cloud backup.
- Corrected a navigation bug: the main vertical-scroll offset previously survived tab changes. The scroll state now resets **per page**, so switching from deeply scrolled Collection to Settings starts at Settings' title.

## Preservation constraints

- NO new cosmetics, monetization, puzzle packs or achievements.
- NO changes to puzzle generator, scores, hint allowances, Solo/Daily save schema, backup JSON protocol, stored achievements, signing, package identity, or Nearby LAN protocol v4.
- All earned unlocks remain free and derived from previously saved completions and achievements.
- Physical two-phone Wi-Fi/Nearby, Android fontScale 2.0, narrow/landscape screenshots and signing/in-place upgrade continuity remain owner-deferred; emulator tests do **not** prove those behaviors.

## Automated gate

1. Standard Android CI: core/app JVM, lint, APK assembly, original artwork and font/OFL import verification; package versionCode 19.
2. Actual API35 emulator instrumentation, including old 41-test suite and **three new tests** for profile/collection/achievement/settings UI, switching tabs after deep scroll, and cancelable local-name edit dialog.
3. Capture four real screenshots via instrumentation: `profile.png`, `fonts.png`, `achievements.png`, `settings.png`; also keep home normal/enlarged screenshots. Review them for clipped text, crowded controls, bad tab scroll and correct unlock counts.
4. Preserve exact tested code SHA on a new rollback branch; publish source-matched APK artifact and SHA256; document verified results and open owner hardware gate.
5. **Draft PR, no merge** until owner physically accepts the cumulative APK.
