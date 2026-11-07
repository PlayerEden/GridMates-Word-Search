# WS-06B — Versus journal and post-match comparison

**November 7, 2026 · Status: implemented and **automated QA PASS (52 API35 tests)**; physical two-phone verification remains open. See [exact QA receipt](WS06B_BUILD_QA_2026-11-07.md).**

## Experience
- After Shared Board Race ends, both phones display a comparison panel showing the local player's result, two readable scores/progress bars, the score difference, and a fold-out list of which words each person found.
- The exact same completed, host-validated snapshot drives this card and the result journal. Live/unfinished matches never count as wins or losses. Co-op stays noncompetitive.
- Stats → Nearby Versus displays Played, Wins, Losses and Ties in two readable rows, plus the recent match outcomes, theme/board/challenge and score. Solo/Daily journal stats remain untouched.
- Results are private/offline. No public leaderboard, cloud backend, paid feature or opponent identity tracking.

## Save and compatibility design
- Strictly validated RaceResult model and RaceSummary in pure Kotlin core. The score is derived from claims in a canonical completed Shared Board Race snapshot; guests cannot send a forged result packet.
- Each match has a distinct host-created room ID; the first saved result for that room is immutable. Repeated result broadcasts, Compose recomposition, device rotation, retry/reconnect and process reload cannot increment the same result twice.
- The Race ledger lives in the existing AtomicFile local profile, under an optional schema-4 JSON field called races. Normal APK updates, manual Export backup, Import preview and confirmed Restore include Race history; no loose second save file or storage permission.
- Old schema-1 through schema-4 backups without races remain readable as zero Versus results. Restore preview tells the player the number of Race entries before replacing saved progress.
- Invalid/duplicate race entries and impossible totals cause backup preview to fail. Existing corrupt-on-disk protection preserves unreadable files without overwriting.
- Journal maximum 1,000 completed races, protecting portable backup size. When full or when writing fails, earlier records remain intact and UI warns; no silent pruning.
- Same public test-only signer inherited from WS-05J; Android QA package com.playereden.gridmates.wordsearch.qa, v0.6.1 versionCode 23. Never reuse QA key for production/Google Play release.
- Nearby LAN protocol stays v5. No changed Co-op or Race rule logic or new wire packet; each phone calculates its own record from already-authenticated host state.

## Verification
- Core JVM tests: results for wins/losses/ties, incomplete/co-op rejection, repeated and rejoined room, invalid duplicate imported results, Solo/Daily isolation.
- Android instrumentation: atomic save/restart, backup export/preview/restore, forged backup rejection, actual comparison UI.
- CI: Kotlin/JVM, app unit, Android lint/test compile, approved portraits and fonts/licenses.
- API35 emulator: install **actual v0.6.0 stable-signed APK**, seed previous schema-4 profile without races, update in-place to v0.6.1, validate all previous profile fields remain and races initializes empty; run all instrumented regressions.
- Record exact verified commit, artifact, checksum and checkpoint. No claim of physical two-phone acceptance.

## Next milestones
- WS-06C: Speed Duel requires distinct identical private boards, coordinated ready/go and host-authoritative completion.
- Future real-device testing: two phone race finish/rejoin/dedup, 200%-font and 320dp layout, backup/import and stable in-place update.
- Keep stacked drafts unmerged until owner hardware testing.
