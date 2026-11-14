# WS-16G1 — Owner handset Home screenshot and centered Together action

> **Quick status: CODE COMMITTED / CI PENDING / OWNER FOLLOW-UP PENDING.** This document does not assert a new verified APK or device execution.

## Who, what, where, why

- **Owner feedback/evidence:** Owner sent a real portrait screenshot of the WS-16F Home screen on November 9, 2026 after installing the QA build. It visibly contains the shared warm parchment, Space book with Woodland/Garden side peeks, walnut shelf, Full Library, Collection, Continue, Daily, Together, Quick Play preview, Custom and My Puzzles.
- **Review by:** ChatGPT assistant, visually inspecting the screenshot. No screenshot file has been added to GitHub and no tap/save checks were performed in this pass.
- **Issue discovered:** Compact **Together** button places its icon toward the upper-left and label toward the bottom-left; unlike the adjacent Daily Puzzle row, its intrinsic-width Column is left-aligned inside its illustrated frame. Owner has not signed off on visuals or interactions.
- **Goal:** Make the narrow Together control visually coherent without shrinking book art, changing shelf spacing or touching gameplay.

## From previous checkpoint

- **Previous implementation:** [WS-16F layered parchment and Full Library](2026-11-09_WS16F_LAYERED_PARCHMENT_FULL_LIBRARY.md).
- **Previous code:** `83a06cbf22adcfef5aa20bde87b01033ec04ba0f` (followed by docs-only commits); WS-16F exact-source Android / art CI both passed.
- **Previous verified APK:** [WS-16F QA ZIP #13727073915](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42737788114/artifacts/13727073915), `app-debug.apk` SHA-256 `88075143e0a82a1fb59e96c3519de96043bbf734fa4f8b75b1b213b31f76dec8`.
- **Branch:** `feature/ws14b-style-anchor-and-art-direction-2026-11-08`; [draft PR #131](https://github.com/PlayerEden/GridMates-Word-Search/pull/131). No merge.

## Actual code change

- **File:** `app/src/main/java/com/playereden/gridmates/wordsearch/Ws16BHomeActions.kt`.
- **Code commit:** [`2ff2918e`](https://github.com/PlayerEden/GridMates-Word-Search/commit/2ff2918e5b11d4c61ad3bbca16a6ac0b71993230).
- Compact (side-by-side) Together content now takes the illustrated frame's full width with a 62dp min-height content group, centers icon and label, uses a smaller 30dp icon instead of 36dp, and explicitly single-lines/ellipsizes the centered live text.
- The wide stacked/large-font layout is unchanged. Accessible content description and clickable route are unchanged.
- **Not touched:** Home carousel, book cover scale, woodworking, page/solo saves, local Wi-Fi, controller/gameplay, Quick Play coming-soon state, art masters, package/version, release approval gates.

## Build/QA receipt

- **Automated Android/art workflows:** triggered by the source commit on the existing draft branch; **final status not yet recorded in this document**. This documentation commit uses `[skip ci]` to avoid duplicate build cycles.
- No new APK artifact or checksum is claimed.
- Existing Compose Android test sources already assert that the Together action is present and semantically accessible; their compilation alone is not proof of real-device execution.
- **Last dual-green fallback:** WS-16F ZIP #13727073915 (above); if a signing conflict forces uninstall, export puzzle progress first.

## Outcome and next exact action

The limited layout correction is committed. Verify the exact source commit in both Android QA and release-art-isolation runs, fix failures before linking any new APK, and ask owner for **Your Library** multi-shelf screenshot. With the full library evidence, judge whether tighter book contact, warmer carved rail, backboards or optional botanical accents truly improve the existing composition. Keep the current functional Home and original covers.
