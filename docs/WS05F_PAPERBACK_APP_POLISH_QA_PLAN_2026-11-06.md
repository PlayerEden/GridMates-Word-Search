# WS-05F — Paperback App Experience & Visual Polish

**Date:** November 6, 2026  
**Target QA:** v0.5.5, versionCode 17, same Word Search QA package  
**Stack:** this branch is based on unmerged WS-05E PR #35, which includes B5 + WS-05A/B/C/D/E. No merges to main until the owner completes physical acceptance.

## Product design intent

**Look and feel like a genuine published puzzle app**, not a proof-of-concept or developer demo. Preserve the existing warm, nostalgic paperback aesthetic and clean mobile UX. Keep controls obvious for older and less technical users. No fake accounts, paid features, streak pressures, unrelated minigames, or distracting animation.

### App-wide visual language

- Warm off-white paper (#FFFDF8), soft book-cream background, forest-green ink and muted brass/gold accent.
- Real serif title hierarchy and readable modern sans-serif body. Preserve existing licensed board fonts separately; typography changes apply to interface headings, not the game's letter grid.
- Reusable bordered/elevated `PaperPanel` panels with consistent internal spacing and touch-friendly full-width primary buttons.
- Home masthead with a small **decorative letter grid**, GridMates identity, tagline, and actual local player name. Grid is not clickable or exposed as a false puzzle to accessibility services.
- Current Solo puzzle progress bar based on actual found/total words. Daily card shows real daily completion count; Nearby card describes true local Wi-Fi co-op, labeled **Early access** rather than a raw “QA preview.”
- Native Android adaptive launcher icon for API26+, matching the paperback/grid motif; installed app label remains **Word Search**.

### Navigation and feedback

- Back gesture on ordinary secondary screens returns to Play (Daily Puzzle returns to Daily) instead of unexpectedly exiting the app. Existing Solo puzzle state is flushed on exit.
- **Leaving an active Nearby session now asks for confirmation**, explicitly warning hosts that their room and temporary shared chat will close. Leaving inactive setup requires no dialog.
- Nearby room HUD shows a real shared-word progress track, clearer host/PIN/fallback information, and preserves existing chat delivery statuses, rejoin and partner-found indicators.
- Settings and Collection copy no longer refers to unfinished “planned” persistence or internal asset-approval history. Saved feedback settings are already functional; profile dialog accurately says that a local display name **is visible to the nearby partner** and caps input at the documented 24 characters.

## What this pass deliberately does not change

- Generator, word placement, letter-grid drawing, zoom/pan/cell hitboxes, puzzle timing, hints or Solo/Daily scoring/persistence schema.
- Co-op LAN protocol remains **v4**. WS-05A through WS-05E all stay included in the cumulative installable QA APK.
- No extra Woodlands, icons requiring purchase, ads, cloud/online rooms or optional matchmaking.
- No assertions that APK updates preserve installed data until physical signing continuity is tested.
- **No claim of visually approved final UI** solely from build/emulator tests.

## QA plan

1. JVM, Android unit tests, lint, APK assembly, original 12 Woodland artworks, four bundled B5 fonts/OFL notices, native package metadata. Verify v0.5.5 / versionCode 17 / `com.playereden.gridmates.wordsearch.qa`.
2. API35 emulator instrumentation, including navigation from new Home, Play Together/Records/Collection/Settings, native adaptive icon and back/leave controls; preserve all previous multiplayer/Solo/Daily regressions.
3. In the same source-matched CI run, capture home reference screenshots at normal and enlarged Android font scales and attach for visual review. Capturing screenshots does not prove every target phone, accessibility preference or animation path.
4. Give each fully verified source a rollback ref and its exact APK checksum and GitHub Actions artifact links. Do not merge stacked draft PRs pending owner physical playtest.
5. Later owner test: 320–400dp portrait, landscape, Android 200% text, TalkBack targets, keyboard open/close, Solo/Collection navigation and nearby quit confirmation while host and guest connected. Confirm room/reconnect, gestures, typography and signed in-place updates on both phones.

## Known risk to watch

The new home header's decorative 5x5 motif must not crowd the text at 200% scale. Material typography uses only system serif/sans families (no extra asset downloads). The Nearby exit confirmation must never silently end a host's room, and system Back must not bypass it.

Keep WS-05F in a draft until hardware visual/functional approval.
