# WS-05E — Reliable Nearby Discovery & Interrupted Join Recovery

**November 6, 2026 · Target QA v0.5.4 · LAN protocol v4 unchanged.**
Parent: WS-05D draft [PR #34](https://github.com/PlayerEden/GridMates-Word-Search/pull/34). All earlier multiplayer/font changes remain stacked. No PR merge before the owner completes two-phone QA.

## What players get

- A **Refresh** control beside Nearby rooms. It clears outdated results and restarts scanning cleanly, rather than needing to leave the entire mode.
- More dependable room lists when multiple hosts advertise or when a nearby host stops. A room that vanished while Android was still resolving it must not reappear as a stale selectable entry.
- If the first join is interrupted **after the guest saves the host-issued reconnect credential**, the guest gets a prominent **Retry joining this game** button. This also covers manual address joins that initially lacked a known room ID.
- Retry carries the actual accepted **room ID**, host IP, port and the earlier optional room code *only in the active memory session*, so a protected room can be retried whether or not the host has committed the seat yet.
- Repeated Join taps are gated to one connection attempt. Failed joins without a saved credential stay in the ordinary browse/manual flow.

## Engineering scope

1. New pure Kotlin `NearbyDiscoveryQueue`: dedupe service-name discoveries, **one in-flight legacy Android NSD resolution**, cap registered room services at 32, skip lost queued rooms, preserve in-flight callback ownership and reset cleanly.
2. Every discovered service gets a unique advertisement ticket. A delayed resolve for a lost service cannot apply its result to a later advertisement with the **same name**. Previous discovery-session callbacks cannot mutate a newer session.
3. Android runtime connects the queue to `NsdManager` sequential resolve callbacks, handles failure and stop callbacks, and provides an explicit refresh path that waits for the previous discovery session to stop.
4. Extra UI state `NearbyJoinRecovery` holds only a temporary follow-up option after the credential has **actually committed to SharedPreferences**. A successful `Joined` clears it; deliberate Back closes the ViewModel-owned room.
5. No new wire operations. **LAN protocol stays v4** and should match WS-05D; both devices should nonetheless run the same latest v0.5.4 APK during owner testing.
6. VersionCode **16**, versionName **`0.5.4-discovery-rejoin-e-alpha-qa`**, same independent `com.playereden.gridmates.wordsearch.qa` debug package; no release signing in this pass.

## Automated verification

- Run new pure JVM queue tests: duplicates/serial execution, lost-while-resolving, stale callbacks, re-advertisement with same service name, 32-service limit, reset and invalid name guards.
- Run new API-35 Android emulator tests for service queue state transitions and data preserved by the temporary manual-join retry.
- Keep all previously passing core Sudoku-independent Word Search tests, Solo/Daily persistence, shared puzzle claims, two-phase admission, chat delivery, rotation ViewModel retention, fonts, icons and original Woodland portraits.
- **Standard CI:** core/unit, lint, packaging, asset and license checks; record exact commit SHA and APK SHA-256.
- **Emulator:** run actual API35 instrumentation and record total, failures and skipped count; save its evidence artifact.
- Protect a rollback branch at the exact verified code commit and record it in a QA receipt.

## Unverified until owner two-phone playtest

- Actual multicast DNS-SD timing/discovery on both phones, repeat refresh on different Wi-Fi routers, isolation/guest networks and Android 16 networking restrictions.
- True loss of host Wi-Fi during **Welcome / Accept / Joined**, recovery button use (open and protected rooms), live chat, chat retries, touch/zoom, simultaneous word claim, rotation/background.
- Real-device user experience at high font scaling, portrait/landscape and actual side-loaded APK-in-place update continuity.
- Real NSD device concurrency and multicast network behavior are **not** established by synthetic queue tests. The host room still ends if Android kills the app process. Chat traffic remains ephemeral, unencrypted on trusted Wi-Fi only.

**Policy:** No new currency, ads, portraits, paid unlocks, cloud accounts or backend work. Keep PR drafts unmerged until physical acceptance.
