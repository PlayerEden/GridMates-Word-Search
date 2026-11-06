# WS-05D — First-Guest Admission & Room Lifecycle QA Plan

**Date:** November 6, 2026 · **Target QA version:** 0.5.3 · **LAN protocol:** v4
**Source:** stacked on unmerged WS-05C PR #33, which is stacked on WS-05B, WS-05A and B5. Keep all drafts unmerged until owner physical testing.

## Problem and change

Previously, the host could mark a first guest as admitted and reserve a unique reconnect token **before** the guest had safely received and stored that token. If the first welcome packet failed, the room could be permanently reserved for a guest who could not reconnect.

The join now follows explicit phases:

1. **Hello:** a joining phone supplies the room ID if discovered, optional PIN for protected rooms, and a saved token when rejoining. Host checks admission and holds a single **temporary offer** slot; it does not yet reserve the guest or mark them connected.
2. **Welcome (tentative):** host sends the exact puzzle fingerprint and word-claim snapshot, its own identity, temporary token and recent chat. Guest validates both puzzle and chat state.
3. **Credential durability:** guest **synchronously commits** its token to local preferences. If saving fails, it never acknowledges joining.
4. **Accept:** guest acknowledges the exact room ID and token. Only then does the host commit the reservation and admit the guest.
5. **Joined:** host sends final authoritative confirmed state. Guest must validate room, puzzle, prior claims and presence before showing Connected.
6. **Abort / failure:** if welcome or acceptance fails, host releases the temporary offer without reserving an inaccessible guest slot. If Joined is lost **after** admission, guest already has its credential and can rejoin. In all cases host remains the owner of the current shared board.

This is a **LAN protocol v4** change: older WS-05A/B/C alpha builds cannot silently pair with this build. Both phones need the same version; NSD advertisements identify the matching protocol.

## Android lifecycle

Nearby transport is now owned by an Activity-scoped **ViewModel** using application context. Normal orientation/configuration recreation should reuse the same live room, TCP sockets, chat and claims instead of closing them on Compose view disposal. A deliberate Back to Play or system Back action closes the room. A genuinely terminated app process, user exit, or a force stop still closes the hosted room; no cloud persistence or foreground network service is claimed.

Solo/Daily preferences, earned portrait/font cosmetics, deterministic puzzle versions, original art, package identity and signing paths remain untouched.

## Required automated QA

- Core JVM, Android unit, lint and debug APK build with exact SHA, versionCode **15**, `0.5.3-admission-lifecycle-d-alpha-qa`, same `com.playereden.gridmates.wordsearch.qa`.
- Actual API35 emulator instrumentation: local TCP hello/welcome/accept/joined handshake sequence, bad token, wrong room or puzzle, missing-ack nonreservation path, Activity recreation identity continuity, and all previous Word Search regression tests.
- All 12 approved Woodland portrait assets, bundled B5 fonts, original license notices, app label, existing solo/daily data migrations remain intact.
- Protected checkpoint at exact tested source commit, GitHub draft PR, QA report, build output and evidence artifacts.

## Owner physical two-phone acceptance remains OPEN

1. Open and protected room hosting/joining over same Wi-Fi; first connection aborted during welcome, return to host and successfully join again.
2. Reconnect after losing final Joined message; guest retains credential, host retains claims and reserved-seat identity; another device must not steal its seat.
3. Rotate both devices (including while hosting): room code, board, word ownership, chat and socket should remain live.
4. Background/return, screen lock/unlock, connection loss, network isolation, small/landscape/200% font; verify correct status and no invisible stale connection.
5. Run actual two-phone partner chat receipt/retry and simultaneous first-word claim.
6. Repeat offline Solo/Daily save and B5 letter alignment across original font choices and 12/15/20 board sizes.
7. Verify in-place sideload updates with real signing before promising real-world progress preservation.

**Known boundaries:** A process killed by Android still drops the host room, a first-time protected/manual join interrupted after offer may require retrying the code, and local LAN messages are unencrypted. Future work can implement longer-lived hosting and richer reconnection UX once hardware tests establish the baseline.

**Do not merge** before owner confirmation; no new purchases, currencies, extra portrait art or remote multiplayer in this pass.
