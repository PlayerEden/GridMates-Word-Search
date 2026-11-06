# WS-05B — Nearby Co-op chat, partner activity and connection hardening

**Date:** November 6, 2026  
**Branch:** `feature/ws05b-nearby-chat-and-presence-2026-11-06`  
**Parent:** WS-05A draft [PR #31](https://github.com/PlayerEden/GridMates-Word-Search/pull/31) (based on B5 [PR #30](https://github.com/PlayerEden/GridMates-Word-Search/pull/30)).

## Scope and user experience

The host and one nearby guest now have an **optional, collapsible Partner chat** on the shared Word Search screen. The closed panel displays the latest message and an Open chat control; the open panel shows the newest five messages and a simple 140-character composer. Messages show **You** or the actual partner name, and the board HUD also says which word the partner most recently found. Existing partner-colored word highlights and contribution counts remain.

Chat runs locally on the host's same-Wi-Fi TCP room (no account, cloud chat log or ad requirement). **It is not encrypted**; do not use untrusted public Wi-Fi or send sensitive information. The host stores only the newest **24** chat messages in memory. On reconnect, the reserved partner sees that bounded recent history. Ending or killing the host app discards this chat and the temporary room.

The host validates and sequences chat, normalizes whitespace/control characters, caps messages at 140 characters, deduplicates accepted message requests and rate-limits messages per seat to once every 1.5 seconds. A failed/rate-limited guest message is not falsely shown as delivered.

LAN protocol is upgraded to **v2** and NSD advertisements identify `proto=2`. Old 0.5.0 rooms are hidden from newer discovery and incompatible manual joins reject gracefully. QA app versionCode increments from 12 to **13**, versionName `0.5.1-nearby-chat-b-alpha-qa`. Same separate Word Search QA package identifier; no change to the production app or Sudoku.

## Reliability

- Limit simultaneous pre-admission socket handlers to four; fail excess connection attempts closed rather than creating unbounded worker threads.
- Do not alter authoritative puzzle claims, room ownership, saved Solo/Daily identity, seed generation, B5 font IDs or Woodland portraits.
- Room history and claims stay host authoritative. Interrupted guest sockets do not overwrite accepted state; accepted guests reconnect with their previously issued room credential.
- Partner activity is based on **confirmed host claims**, not a fictional real-time cursor. A live finger trace and richer user presence are deferred until the basic transport passes real two-phone use.

## Required verification

1. **Core JVM tests:** chat input cleaning, Unicode/whitespace handling, 140-char cap, 24-message cap, request replay, rate limits per seat, history order and invalid history. Preserve WS-05A simultaneous first-claim, reconnect and Solo-safety tests.
2. **Android:** compile and lint; 26 preexisting Android tests and new chat codec tests execute on a real API-35 emulator. Invalid/unversioned data is rejected. Confirm chat protocol v2 and stable app resources.
3. **APK:** same `com.playereden.gridmates.wordsearch.qa` package, versionCode 13, bundled 12 original Woodland portraits, 4 licensed fonts and full notices. Record SHA256, exact source SHA and rollback.
4. **Physical two-phone acceptance (owner testing deferred):** join open/protected rooms, exchange chat in both directions, names/keyboard and send button are readable at 200% text, message input handles poor connection, spam delay has a clear outcome, reconnect displays recent chat and claims, no duplicate messages.
5. **Network interruption:** Wi-Fi loss, guest app background/return and mismatched 0.5.0/0.5.1 APK versions show honest connection status, no bogus results. Host process ending still closes and discards the room.
6. **No unreviewed merge:** WS-05A and B5 remain draft pending real-device visual/multiplayer acceptance. This new branch stays a separate stacked draft.

## Deferred

Live partner selection trace, typing indicators, more detailed chat notifications, keeping a hosted room alive across process death, long-term chat records, cloud/remote matchmaking, full Versus modes (WS-06), cosmetics and purchases. Prioritize usability and trust ahead of additional features.
