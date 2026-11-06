# WS-05C — Chat delivery confirmation & LAN lifecycle audit

**Date:** November 6, 2026  
**Branch:** `feature/ws05c-chat-delivery-and-connection-reliability-2026-11-06`  
**Parent:** [WS-05B PR #32](https://github.com/PlayerEden/GridMates-Word-Search/pull/32) (draft, stacked on [WS-05A #31](https://github.com/PlayerEden/GridMates-Word-Search/pull/31) and B5 #30). **No merges to main.**

## Why this pass matters

In WS-05B a guest's chat text was cleared after the socket write, even though the host may not have accepted it; conversely, the host could accept it but lose the guest connection before the guest sees an acknowledgment. Naively resending creates duplicate messages.

WS-05C adds a **host-confirmed chat receipt** keyed to the original sender request ID:

- Every accepted chat line has a **stable requestId** and host sequence.
- Guest keeps **one outstanding chat** marked `Sending · awaiting host confirmation…`. A second message cannot be queued until the first is verified or explicitly retried.
- When the guest receives the host's exact accepted line (matching seat and request ID), the outstanding item is cleared as **delivered**. Socket-write success alone no longer counts as delivery.
- If no matching confirmation arrives, after nine seconds the message becomes `Not confirmed`. The player can **Retry message** using the **same** ID. A host receipt, even for a duplicate request, echoes the previously accepted line, rather than accepting a second copy.
- If Wi-Fi disconnects, outstanding text is preserved in that running screen as `needs retry`; after reconnect, the host's bounded chat history is checked for the original request ID. If found, it is marked confirmed without resending. Otherwise, the player can retry safely.
- Rejected/rapid messages keep a retryable state and a clear notice. Draft input is only cleared after the app accepts it for sending, rather than on a failed local submission.

## Other reliability improvements

- Serializes host chat acceptance/publication to prevent host-and-guest concurrent messages from arriving out of sequence.
- Validates chat history does not contain duplicate (seat, requestId) pairs, malformed request IDs or out-of-order host sequence.
- Stops ongoing DNS-SD room discovery after successful join to avoid unnecessary discovery activity and stale room entries. A failed discovery may be retried.
- Prevents a disconnected guest from using the reserved room's pending messages on a different room.
- **Protocol version 3** and NSD `proto=3` deliberately do not interoperate with 0.5.0/0.5.1 QA alpha peers. Both phones need the same build.
- QA package remains **`com.playereden.gridmates.wordsearch.qa`**, versionCode **14**, versionName **`0.5.2-chat-confirmation-c-alpha-qa`**.
- Solo, Daily, font collection, Woodland portraits, seeded puzzle generator and personal data format unchanged. Nearby chat is still **unencrypted, ephemeral, local Wi-Fi only**.

## Quality gates

1. Run **core JVM** suite including repeated-message same-ID receipt, guest pending acknowledgment and reconnect-history cases, invalid IDs and chat flood limit; retain all previous claim/saving tests.
2. Run standard **Android** CI, lint, source compilation and APK build. Verify bundled original 12 Woodland images and all four licensed font assets/notices. Record exact SHA, APK checksum and download artifact.
3. Execute actual **API35 emulator instrumentation**, including on-device loopback socket chat roundtrip, malformed chat IDs, existing Solo/Daily/B5 and WS05A/B regressions. Record count/skip/fail.
4. Preserve exact known-good checkpoint in a new protected branch; retain parent B5, WS05A and WS05B checkpoints and unmerged PRs.
5. **Owner's later physical two-phone tests remain required:** host/join, open/optional PIN, near-simultaneous word ownership, two-way chat, data interruption after host receives request but before guest gets receipt, safe retry, guest reconnect history, real gestures/zoom/large fonts and Android 16 local-network behavior.
6. Do **not** claim app-wide lifecycle completion: a host room is still in memory and closes if its screen/process is destroyed (including some configuration changes). Host-process persistence and complete background/rotation handling remain future work. No internet service, full network encryption or real phone LAN evidence yet.

## Player-facing scope

No busy new menus or chat spam. Chat panel remains secondary; confirmed word contributions and most recent partner find stay visible. Main play should remain calm and simple, especially for older or less technical players. Next iterations should respond to actual two-phone feedback and harden host lifecycle before considering WS-06 Versus.
