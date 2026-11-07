# WS-06D — Speed Duel records, results and protected update

**November 7, 2026 · Implemented; **60/60 Android API35 emulator tests PASS**, actual signed v0.7.0→v0.7.1 data-preserving update PASS. Real two-phone hardware acceptance remains open. See [verified receipt](WS06D_BUILD_QA_2026-11-07.md).**

Speed Duel final, host-validated recipient-scoped snapshots now create separate local win/loss records. Each result records room, puzzle spec/digest, winner, player-relative word counts and completion timestamp, never opponent private word IDs. Incomplete rounds and other modes cannot generate Duel records; one room can be recorded only once across reconnect, duplicate packets or rotation.

Stats has a distinct **Speed Duel** section with Played, Wins, Losses, total words found and recent results. Shared Board Race keeps its independent tie-capable journal; Co-op and Solo/Daily remain unchanged. The final Duel panel now says whether the result is saved and summarizes remaining words.

The existing schema-4 AtomicFile profile and JSON Export/preview/Restore add optional `duels`. Older saves without this field are valid with empty Duel history. Impossible scores/winner, duplicate rooms and malformed import arrays are rejected before overwrite. The 1,000-record cap preserves existing data rather than silently pruning history.

Android QA build **v0.7.1 / versionCode25**, same public **test-only** QA signing identity and LAN v6 as v0.7.0; production/Play signing remains separate.

**QA requirements:** JVM tests cover host/guest results, partial/forged rejection, replay and separated stats. Instrumentation covers real AtomicFile reload, safe backup export/restore and final Duel UI. API35 workflow installs **actual prior v0.7.0 signed APK code24**, seeds a legacy profile plus Race history (no Duel field), updates in place with code25, checks previous fields and empty new Duel journal, then runs all Android tests. Only the matching green workflow earns an APK/checkpoint claim.

**Hardware remains open:** real two-phone LAN finish delivery, retries, 320dp/200% accessibility, real backup and app update. All stacked PRs remain draft and unmerged until owner physical testing.
