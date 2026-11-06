# WS-05A — Nearby Co-op functional alpha & QA plan

**Started:** November 6, 2026. **Branch:** `feature/ws05a-coop-session-engine-2026-11-06` stacked on unmerged B5.  
**Owner testing:** deferred until the owner is ready. Green CI or an emulator does **not** establish two-phone LAN functionality.

## Player-facing slice

- Home → **Play Together (QA preview)**. Hosting picks one of three themes, Short/Standard/Extended board and Relaxed/Classic/Expert challenge. It creates a **separate, fresh V2 session**; it does not overwrite unfinished Solo or Daily.
- Host defaults to **open/no code** on trusted same-Wi-Fi; a remembered **Require a room code** toggle optionally adds a six-digit PIN. The PIN is required for the first guest only; an authenticated reconnect presents the saved random session credential.
- Joining searches nearby Wi-Fi via Android NSD (DNS-SD `_gridmatesws._tcp.`). A named room is directly tappable. Host Wi-Fi address and port appear in the host screen, with manual entry if discovery is blocked by a guest network.
- Host holds the authoritative board; each valid word path is committed once with monotonically increasing revision. The same accepted board, shared found list, individual contributions and **distinct green/blue path colors** reach both screens. Each phone can use its separately equipped offline font.
- A dropped guest retains progress on the host, and the same guest may rejoin using its local credential while the host room stays open. The original guest owns its reserved seat; another phone cannot steal it. An inactive game never auto-completes or times out as a *game rule*.
- LAN uses a bounded, versioned JSON-over-TCP message format with exact puzzle ID and SHA-256 board fingerprint verification; the guest validates host snapshots and rejects regressive revisions. All networking is optional. **No cloud account, internet backend or purchase feature**.

## Technical implementation

- `core/.../NearbyCoopSession.kt`: immutable snapshot specification, regenerated-content digest, host-serialized first-claim logic, replay detection, guest reservation/presence, complete-only-on-all-words, malformed-snapshot guard.
- `app/.../NearbyCoopRuntime.kt`: Wi-Fi-restricted local socket, NSD advertisement and discovery, optional PIN, bounded wire reads, single guest, reconnect secret, heartbeat and callback-to-main UI state.
- `app/.../NearbyCoopScreen.kt` and `GridMatesShell.kt`: discover / host / join / reconnect controls and shared board with clear partner presence and contribution counters.
- `WordSearchScreen.kt`: optional partner-owner highlight overlay; existing Solo and Daily call sites keep the original default rendering and pointer logic.
- `AndroidManifest.xml`: network permissions for local Wi-Fi multiplayer, with existing offline Solo unaffected.
- Tests: core near-tie, replay, invalid, idle and reconnection; Android socket protocol and UI entry smoke.

## Required acceptance — NOT YET COMPLETE

1. **CI:** Kotlin/JVM core tests, Android unit tests, lint, instrumentation-source compilation and QA APK on exact source branch. Confirm original Woodland PNGs, all bundled fonts/attribution, package signing identity and QA version.
2. **Emulator runtime:** execute all existing and new Android instrumentation tests on API 35, record real test count/failures; specifically ensure Solo and Daily still launch and resume.
3. **Device A/B networking:** actual two Android phones on same Wi-Fi: one taps Host, other sees the named room without typing an address; open room joins with no PIN; protected room asks only once for its six-digit code.
4. **Shared board:** both phones independently verify same letter layout, seeds, words, current claims, source-colored highlights and touch-cell alignment at 12×12, 15×15, 20×20 and zoom. Both players find words. Final word completion only after actual last valid claim.
5. **Conflict handling:** simultaneous same-word claims must award it exactly once. Replayed/invalid/wrong-puzzle claims must never alter the board. Check that only the true source device owns the first claim.
6. **Reconnect:** toggle Wi-Fi or background the guest; see the disconnected message and Reconnect. Existing words/contribution ownership should return. The host must not silently replace a reserved partner. Shutting the host is an honest room-end, **not** cloud-hosted preservation.
7. **Lifecycle and usability:** portrait/landscape/small phone/200% font; NSD unavailable/manual fallback; network isolation/guest Wi-Fi failure; cancel/back/host close behavior. No hidden unbounded retries or intrusive permissions.
8. **Privacy/security:** only local network, no addresses or session tokens added to cloud saves; reject invalid frame sizes and incompatible versions; restrict current scope to trusted Wi-Fi, not public or remotely exposed ports.

## Explicitly deferred after WS-05A

- **Chat**, richer partner cursor/active-highlighter presence, on-screen host connection help, more polished mode selection, friendly newcomer/existing-peer messaging.
- Stronger lifecycle handling when the host process is killed, better flow when peer has no Wi-Fi, and further real-socket fuzz/load tests.
- Both **Versus** modes (WS-06), any internet matchmaking, public lobbies, global stats or Google Play release.
- B5 visual letter-font acceptance remains pending separately; do not merge its draft PR because WS-05A source builds.

## Stop conditions

Do not claim physical two-phone verification or merge this stacked draft until the owner explicitly approves the LAN behavior. If CI fails, diagnose the exact error and fix it, retaining the original B5 rollback checkpoint. Never change Sudoku's repo, Android signing keys or Word Search's existing Solo/Daily save format.
