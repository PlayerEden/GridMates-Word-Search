# WS-06C — Speed Duel, private boards and ready/go

**November 7, 2026 · Implemented in draft, **57 API35 Android tests and real previous-APK update PASS**; physical two-phone acceptance still open. See [exact-source QA receipt](WS06C_BUILD_QA_2026-11-07.md).**

## Separate mode
Speed Duel is a third distinct Nearby game alongside Co-op and Shared Board Race. Both players get the identical theme, board, challenge, deterministic seed, words and word positions, but play privately:
- Player must explicitly press **I'm ready**. The two boards and word lists remain **hidden in the UI until both seats are ready and the host broadcasts GO**.
- The host owns independent per-seat found-word sets; each player can discover the **same word** on their own board without stealing the other's progress.
- Each phone gets only its **own found-word IDs** and the opponent's aggregate progress **count**. It never receives the opponent's claimed word IDs/paths in the game-state snapshot. Opponent chat and online/presence signals remain.
- First player to complete every target, as **serialized and accepted by the host**, wins. Near-simultaneous finishes have a deterministic host arrival-order winner (not an arbitrary tie). There is no idle cutoff, forced time limit or countdown.
- Both progress bars (You / Partner) show live counts. Results remain visible on both phones while the room exists. Solo/Daily and the existing Shared Board Race journal are unchanged.

## Protocol v6
- An entirely separate core SpeedDuelSession is host authority for Duel; legacy NearbyCoopSession is still the authoritative Co-op and Shared Board Race rule engine.
- Extended recipient-scoped DuelView inside state packets; room mode and private progress validated against the board fingerprint and mode. Host explicitly constructs HOST and GUEST views; **host-private word IDs are never serialized into guest room-state messages**.
- Ready is a separate authenticated guest operation over the admitted socket. Guest claims go through existing host-controlled connection, then independently validate against that seat's own set.
- Once GO is sent, a disconnection pauses acceptance of further claims; progress survives a reconnect to the still-open host. A guest dropping **before GO** must Ready again; host readiness is retained. A finished result is replayed when a guest reconnects.
- LAN + NSD wire version increases **5 to 6**. Older versions cannot connect as protocol-compatible peers, even to Co-op or Race rooms, until both phones update.
- Signer unchanged from pinned QA certificate. v0.7.0 versionCode 24 is an intended in-place install over actual stable-signed v0.6.1 code23; incompatible pre-pinned-signer APKs still require JSON backup/export before uninstall/reinstall.
- **Not anti-cheat secure**: puzzle seed/spec is shared for local deterministic generation and can be inspected by a modified app. LAN traffic is not encrypted. Play on trusted Wi-Fi.

## QA gates
1. Core deterministic tests: both ready needed, no early claims, independent same-word claims, private IDs per recipient, replay dedup, reconnect re-ready, host first-finish winner, near-simultaneous finishes.
2. Android codec: v6 recipient-specific state roundtrip, host word ID omission, reject forged counts, fake finish, unknown or mismatched mode, guest join privacy.
3. Android Compose: host selector offers Co-op, Shared Board Race and Speed Duel; readiness and completed Duel result are understandable. Normal Solo/Co-op/Race regression test suite remains enabled.
4. Emulator: actual prior v0.6.1 APK installed; code24 v0.7.0 installed **over**; verify previous schema-4 Solo/Daily/profile/Versus records remain and v6 is correctly packaged. Then run the full Android on-device test suite.
5. Finalize a source-matched signed APK artifact, checksum, exact SHA, rollback checkpoint and QA receipt.
6. **Actual two-phone LAN acceptance is owner-deferred**: ready latency/go, simultaneous final claims, reconnect while running, 320dp/200% font, rotate and chat; do not imply this is passed merely from an emulator.

## Deliberately deferred
- Separate long-term **Speed Duel** win/loss records and share cards: later WS-06D. Existing Shared Board Race stats must not accidentally count Speed Duel.
- Time-trial countdowns, rankings, remote play, paid cosmetics, cloud accounts or production Play signing.
- All cumulative PRs stay draft/unmerged until owner physical acceptance.

## Verified automation result

[GitHub Android CI #42350347969](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42350347969) PASSED. [Full API35 emulator #42350506917](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42350506917) PASSED **57/57 tests**, including host/guest Duel privacy and result UI, and verified signed v0.6.1→v0.7.0 actual install-over preserving all previous profile fields and Race results. [Full APK/QA/checkpoint receipt](WS06C_BUILD_QA_2026-11-07.md). Physical two-device session remains deferred.
