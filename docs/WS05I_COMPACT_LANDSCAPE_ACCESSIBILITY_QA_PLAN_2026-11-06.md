# WS-05I — Compact Phones, Landscape and Enlarged Text

**Date:** November 6, 2026 · Android QA v0.5.8 / versionCode 20.  
**Stack parent:** WS-05H draft PR #38, containing all B5 and WS-05A through WS-05H work. Keep the complete stack draft/unmerged until the owner performs real two-phone QA.

## Scope

This is a responsiveness and accessibility milestone, not a feature or puzzle-generation expansion.

- **Word Search gameplay HUD:** the small/large-text variant separates theme/challenge metadata from Home/Options and shows found words + Hint on a clear row with elapsed time beneath. The previous three competing items (found count/timer/hint) could crowd a 320dp phone when fonts are doubled. The zoomed board viewport reserves extra vertical height for expanded text in portrait and landscape; original coordinates, word-hit logic, scoring, hint allowance and stored puzzle data are unmodified.
- **Bottom navigation:** at width below 360dp or Android font scale 1.5+, visible labels use concise Play / Stats / Items / Setup. Full semantic labels remain **Open Play, Open Records, Open Collection, Open Settings** for TalkBack, with the same four destinations and icons; no icon-only navigation.
- **Nearby gameplay:** shared board appears before chat rather than after a tall chat card. Room network info is still prominent while waiting but collapses into **Room details** once both players are connected; invite code/IP are still available with a tap. Chat messages, delivery confirmation and retry remain available beneath the board. Word-list rows switch from three cramped columns to two normally and one when under 360dp or fontScale 1.3+. Landscape board size is capped by available device height instead of screen width alone.
- **No change** to same-Wi-Fi discovery/admission, the free achievement-based cosmetics, original Woodland artwork, licensed board fonts, Solo/Daily history, backup JSON, signed-package identity or LAN protocol v4.

## Automated acceptance

1. Run Core/app JVM tests, Android lint, package and art/font license audits; confirm `com.playereden.gridmates.wordsearch.qa`, versionCode **20**, v0.5.8.
2. Run actual Google APIs API35 Android emulator instrumentation including all 44 previous regressions **plus** three Compose tests for a simulated **320dp width / 200% font** gameplay/results viewport, compact bottom-nav semantics and Nearby host/join controls.
3. Capture current actual game/results, profile, fonts, achievements, settings and normal/enlarged home images, plus two additional **emulator window** references: a **320dp-wide simulated Android portrait at fontScale 2.0** and an emulator **wide/landscape display**. These screenshots check rendering but do not establish all real-phone window-inset/notch behaviors.
4. Inspect images for clipping, overflow, undersized controls and access to important actions. Fix problems and rerun if needed.
5. Save exact source SHA checkpoint, test count, artifact download, APK SHA256 and QA receipt. Keep all PRs DRAFT / UNMERGED.

## Physical owner gates still open

- Actual 320–400dp phones at normal/1.6/2.0 text, screen-reader TalkBack, landscape and rotation while hosting/joining on two phones; Android keyboard chat/input inset; gesture precision on 12×12/15×15/20×20; actual Wi-Fi/PIN/reconnect; actual Android shares and file chooser.
- Validate actual sideload **in-place APK signature continuity and saved progress**, not merely versionCode increments. Before uninstalling/replacing an early QA build, use Settings → Export backup.
- App process termination ends LAN hosting; no cloud or encrypted chat is added in this phase.

**No new advertisements, purchases, shop, internet service or portrait designs in WS-05I.**
