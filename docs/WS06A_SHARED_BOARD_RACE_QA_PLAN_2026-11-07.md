# WS-06A — Shared Board Race (first Versus milestone)

**Date:** November 7, 2026. **Status:** implementation and automated Android QA PASS; physical two-phone acceptance deferred. [49-test build receipt](WS06A_BUILD_QA_2026-11-07.md).

## Scope and rules

Shared Board Race uses the same visible puzzle and word list for host and guest. Each word is worth **one point** to the first player whose valid selection reaches the authoritative host. Words cannot be stolen or scored twice. The match ends **only after all target words have been found**. Highest score wins; equal scores yield a tie. Idle time never ends the match.

**Speed Duel remains separate and unimplemented** at this milestone: coordinated ready/start, private claims and independent completion need distinct authority. Do not conflate the two modes.

## Player experience

- Nearby hosting offers Co-op (default) or Shared Board Race, existing theme, size, challenge and optional room code.
- Nearby discovery lists game mode before joining. No forced extra steps for the guest.
- Race header displays shared progress, You and Partner live scores, presence and last opponent word.
- After completion both phones render You win, Partner wins or It's a tie with final points. The room stays available to review.
- Same green/blue ownership highlights, word list and optional chat. Co-op remains noncompetitive. Solo and Daily saves untouched.
- No win/loss/tie persistence yet; avoiding double-counted completed matches during reconnection requires a follow-up milestone.
- No online accounts, stores, AI opponent, or time-based win conditions.

## Data integrity and compatibility

- Existing host-authoritative NearbyCoopSession handles every claim, authenticates seat by joined socket and dedupes requests; RaceStanding is derived only from host-accepted word ownership after completion.
- Mode is included in host snapshots and protected by handshake and update-mode consistency.
- LAN and NSD protocol bumped from v4 to **v5** so an outdated Co-op client cannot silently interpret a Race as Co-op. Both phones must use the same compatible QA build.
- Stable QA signing from WS-05J is unchanged. In-place updates from v0.5.9 are intended; physical update verification remains open.
- Local Wi-Fi traffic is not encrypted; use trusted networks only. The host room ends when its app process is killed.

## Tests and acceptance

- SharedBoardRaceTest covers 8–4 win, 6–6 tie, idle/no automatic end, concurrent same-word race, replay deduplication, reconnect, and Co-op isolation.
- Android NearbyCoopAndroidTest covers race packet roundtrip, rejection of missing/unknown mode, and mode consistency in join handshake.
- NearbyCoopUiEntryTest checks default Co-op, Race selector and return to Co-op.
- **Automated PASS:** [standard Android CI](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42343700819), [API35 49-test emulator and real app upgrade](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42343699693), exact-source [QA receipt](WS06A_BUILD_QA_2026-11-07.md) and rollback checkpoint. Physical acceptance remains open.

**Physical gate open:** two phones on same Wi-Fi; Race host/join, no duplicate score on near-simultaneous finger drags, both winners/ties, app background and reconnect, 200% system font, session exit and successful APK upgrade while preserving Solo/Daily.

## Planned continuation

WS-06B: separate race records and robust one-time result acknowledgements. WS-06C: truly separate Speed Duel private-board authority. No PR merges until owner hardware acceptance.
