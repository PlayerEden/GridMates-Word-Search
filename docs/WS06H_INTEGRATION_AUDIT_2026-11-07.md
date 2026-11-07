# WS-06H — Cumulative Integration and Release-Readiness Audit

**November 7, 2026 · Base: exact-green WS-06G v0.7.4 / [draft PR #48](https://github.com/PlayerEden/GridMates-Word-Search/pull/48), [71-test receipt](WS06G_BUILD_QA_2026-11-07.md) · Candidate v0.7.5 / versionCode29 · automated acceptance PASS on exact executable SHA `0c958508468e987d1668da7feeaca104d7087c1d` (74/74 emulator tests and signed update); physical acceptance OPEN.**

## Scope and method

Audit the **actual cumulative Word Search branch** (not minimal `main`) across Android entry/navigation, generated Solo/Daily puzzles, achievements/collections/fonts, Nearby Co-op/Shared Board Race/Speed Duel, local LAN v7, host/guest state and reconnect, result previews/images, local saves/backups/upgrades, compact accessibility, licensed assets, and release readiness.

Evidence types must not be conflated:

1. **Source-backed**: reviewed Kotlin, manifest, Gradle, workflow, tests and human-facing UX.
2. **Automated**: existing verified WS-06G 71 Android API35 tests / standard Kotlin-lint CI; new WS-06H checks listed below. Loopback traffic is **not** two actual phones.
3. **Owner/hardware**: Wi-Fi discovery, inter-device connection, delay/fairness, screenshot legibility, TalkBack, native installed sharesheet target apps, actual APK update/backup on existing handset. **OPEN; do not invent results.**
4. **Publishing**: public QA signer is non-production; production signing and Google Play account/testing/enrollment remain separate user-owned steps.

## Integration matrix / evidence

| Area | Status at audit opening | Evidence and guardrails |
|---|---|---|
| Home, Solo, Daily, Records, Collection, Settings, Nearby navigation | Prior automated coverage; **new integrated flow test added** | `GridMatesShell`, `WordSearchScreen`, `DailyLandingScreen`, previous Shell/Compact tests and new `IntegratedNavigationAndroidTest` |
| Solo puzzle generation, drag/tap, zoom/move, hints, scoring, resume | Covered by previous Kotlin and emulator tests; no rules changes | `WordSearchGeneratorV2Test`, `WordDragSnapTest`, `SoloPersistenceTest`, `WordSearchUiSmokeTest`, `CompactGameplayAndroidTest` |
| Daily calendar and saved journal | Covered by prior deterministic/persistence tests; no changes | `DailyChallengeTest`, `DailyPersistenceTest`. Device-local date, no online synchronized daily challenge |
| Co-op, Race, Duel, Ready/GO, claims and privacy | Host/guest core and **same-emulator loopback** automated; physical acceptance still blocked | `NearbyCoopSessionTest`, `SharedBoardRaceTest`, `SpeedDuelSessionTest`, `NearbyAdmissionAndroidTest`, `SpeedDuelPacketAndroidTest`. No anti-cheat/remote play guarantee |
| Shared temporary Co-op palettes | v0.7.4 automation pass; **real two-device visual convergence unproven** | `CoopBoardLookTest`, `SharedCoopBoardLookAndroidTest`; v7 canonical look, local Classic override; no earned cosmetic grant |
| Profiles, portrait/board-font collections | Existing Android tests and asset/license pinning | `IdentityPersistenceTest`, `CollectionSettingsPolishAndroidTest`, `BoardFontAndroidTest`, `ApprovedWoodlandAssetTest`. Fonts remain personal |
| Share preview/PNG | Previous tests passed; **stale cache hygiene added** | `PuzzleResultShareTest`, `NearbyMatchShareTest`, `ResultPostcardAndroidTest`. No remote IDs, chat, answers, or arbitrary files; real receiving app open still untested |
| Atomic local save / backup / migration | Previous signed v0.7.3→v0.7.4 upgrade passed; **two backup safety findings addressed** | `SoloStore`, `RaceJournalPersistenceAndroidTest`, `BackupDocumentReaderTest`; new real signed v0.7.4→v0.7.5 migration required |
| 320dp / 200%-font | Compose emulator coverage; hardware/landscape/TalkBack not cleared | `CompactNavigationNearbyAndroidTest`, `CompactGameplayAndroidTest` |
| APK / release | Public QA-only signature and distinct `.qa` package; **Play release NOT ready** | `app/build.gradle.kts`, `AndroidManifest.xml`, old/new same cert script. Production signing, app listing, account, Android Vitals and physical QA needed |

## Source-backed findings and focused repairs

### H-01 (High) — Backup import file-size check occurred *after reading entire stream*

`SoloSettingsScreen` used `bufferedReader().readText()` and checked `raw.length <= 2_000_000` after allocation. A malicious/accidentally huge SAF document could exhaust device memory. **Fix:** bounded incremental UTF-8 byte reader (maximum **2,000,000 bytes**) that stops after max+1 bytes, closes stream, and rejects before JSON parse. JVM tests exercise Unicode, exact boundary, >boundary, and a simulated 22 MB provider read count. No broad storage permission.

### H-02 (High) — Malformed present Race journal silently became empty

`SoloStore.decode` accepted non-array `races` as if missing; a later successful export/save could erase Race history. **Fix:** mirror existing Duel policy: optional **missing** key remains valid for legacy backups, **present but not an array** fails closed. Added Android test for rejected import, preserved in-memory history, blocked read of corrupted on-disk save and no overwrite of its original bytes.

### H-03 (Medium) — Temporary postcard cache had no bounded age cleanup

Repeated voluntary image shares could leave old PNGs in the app's cache indefinitely until Android reclaims storage. **Fix:** on creation of a new share, prune only files matching `gridmates-result-*.png` in the dedicated postcard cache subtree that are **older than 7 days**, leaving fresh recipient URIs and all unrelated cache/backup files untouched. On-device regression test verifies old deletion, fresh preservation and unrelated-file isolation. OS can still evict cached files; never promise unlimited history or permanent URI validity.

### H-04 (Coverage gap) — End-to-end app route had only separate screen checks

Added an Android UI smoke test traversing Home → Daily → Home → Records → Collection → Settings → Home → Nearby → Home. No live network or destructive backup confirmation is involved. Existing gameplay-specific tests remain.

## Deliberate nonchanges

- **No** extra game modes, achievement/cosmetic entitlements, ads, backend, Bluetooth, contacts, or online matchmaking. No gameplay rule, score, LAN wire or saved schema change.
- Candidate **v0.7.5 versionCode29**, same QA-only signer and `com.playereden.gridmates.wordsearch.qa`, **LAN protocol v7**, AtomicFile profile schema 4 unchanged.
- Android permission surface unchanged: nearby local socket permissions, no new photos/media/files permission, `FileProvider` remains scoped to postcard cache.
- Report prior device testing only as **on-device emulator**; report 2-phone physical testing as **OPEN**.

## Explicit release blockers / risk register

**P0 for inviting broad testers:** real two-phone same-Wi-Fi Co-op, Race and Duel acceptance, LAN v7 compatibility, Wi-Fi discovery/manual join/reconnect and final result persistence on both devices; preview theme look agreement, private Duel word isolation, and actual device update from installed APK with verified export backup. [Two-phone protocol and sign-off card](WS06H_TWO_PHONE_ACCEPTANCE_2026-11-07.md).

**P0 for Play:** create/verify a Google Play developer account, configure **private production upload signing** (never reuse public QA key), package Play-ready release/AAB, privacy/data-safety/listing and required testing track/eligibility; review current Play Console policy when preparing. Existing `.qa` APK is only a test artifact, not a Play release.

**P1 usability:** physical 320dp phones, landscape, 200%-text, TalkBack focus/order, real native sharing targets and image URI lifetime; voice-over/contrast and mid-session background/resume.

**P1 engineering:** stacked drafts #30 onward remain **unmerged**. Audit and review cumulative ancestry before selecting an integration/merge strategy. Do **not** merge automatically in the name of release readiness.

**P2 scope:** LAN plaintext and host-authoritative local trust, no E2EE or server anti-cheat; automatic cloud backup intentionally absent (`android:allowBackup=false`), manual verified JSON export needed before uninstall.

## Automated acceptance gate

Run standard CI, core/app unit/lint + Android instrumentation compile, and the **full API35 emulator**, including original **signed v0.7.4 versionCode28** → v0.7.5 versionCode29 in-place update, same public QA cert and preservation of `schema`, `active`, `results`, `daily`, `identity`, `races`, `duels`. Record exact tested SHA, final count, APK SHA-256 and exact-source rollback when green. Before those pass, this is a **candidate**, not a verified build.


## Final WS-06H automated acceptance — PASS (November 7, 2026)

The dedicated [API35 emulator run #42362755990](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42362755990) passed **74/74 Android tests, zero failed/skipped**, and installed signed **v0.7.4 code28 → v0.7.5 code29** in place, preserving schema, active Solo, results, Daily, identity, Race and Duel records. [Standard Android CI #42362760272](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42362760272) also passed. Exact executable source `0c958508468e987d1668da7feeaca104d7087c1d`; APK SHA-256 `863ca03c8c6a9078392158accb3a19424f064421eb58b671308c884a0a5c46bd`.

[Build QA receipt and artifact links](WS06H_BUILD_QA_2026-11-07.md) · [Pinned rollback checkpoint](https://github.com/PlayerEden/GridMates-Word-Search/tree/checkpoint/ws06h-v075-integration-audit-74-android-tests-upgrade-green-2026-11-07).

**No claim of actual two-phone testing or Play-readiness**. All hardware/signing/merge blockers above remain open. A separate post-audit risk concerning present-but-malformed Solo/Daily fields is tracked as [WS-06I issue #50](https://github.com/PlayerEden/GridMates-Word-Search/issues/50).
