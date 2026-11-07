# WS-05J — Real-Phone Screenshot Review & UI Repairs

**November 6, 2026 · Android QA v0.5.9 / versionCode 21.**
Parent is WS-05I draft PR #39; all earlier font, Solo/Daily, Nearby, profile, collection, accessibility changes remain stacked and unmerged.

## Real observations from owner-provided screenshots

1. The 5x5 decorative letter motif on the home cover has its fifth row clipped at the bottom. The font inherited line heights that can exceed its fixed 98dp container.
2. The 15x15 board at its default 145% zoom and the 20x20 at 185% deliberately show only part of the board, but the existing hint stays "Drag a word" even when the player selects **Move**, making it unclear how to reach the offscreen letters. At 100%, the full 20x20 fits but letters are naturally smaller.
3. The otherwise clean paperback styling, 0% progress track, native bottom navigation, Save-on-device message, zoom indicator and selected Find/Move chips should remain unchanged.

## Corrections

- Use explicit 13dp-high rows with a restrained 9sp/11sp line-height monospace font for the **decorative** 5x5 grid. Keep the existing >=1.3 fontScale rule that hides art to prioritize readable text.
- Larger boards start with player-facing directions to **tap Move to explore, then Find to select words**. When the user switches mode, the status strip immediately displays correct, concise instructions for pan/pinch or drag/tap word selection. This also fixes the stale Find directions when Move is active.
- No change to real letter-grid glyph size, 15x15/20x20 zoom defaults, panning physics, word hit-test, saved progress, hint allowance, scoring, local multiplayer, sync protocol, font licensing or unlock rules. The 145%/185% viewport cropping is intentional; help is improved rather than eliminating helpful large letters.

## QA gates

- Core/app JVM + Android lint, font/portrait import checks and source-matched debug APK.
- Unit tests for contextual guide; actual Android UI test taps Move and Find, checking the help text updates; retain 47 prior tests.
- Capture actual home screenshots at normal and **fontScale 1.2**, so the fifth row is inspected, plus game and results screenshots and the prior 320dp/200% and landscape screenshots.
- Review screenshots for grid clipping; record test count, exact source SHA, APK artifact/checksum, and a rollback checkpoint.
- **Owner hardware QA is still deferred:** two-phone LAN with chat/reconnect, physical gestures and zoom, 200%-scale real devices, Play Store signing/in-place upgrade continuity, backup restore.
