# WS-07F Records v0.8.2 — source-matched QA receipt

**Completed November 7, 2026 (Detroit local; CI UTC November 8).**  
**AUTOMATED PASS: 92/92 Android API35 emulator tests, 0 failures, 0 skipped. Physical acceptance still deferred by owner.**

## Exact candidate, integrity and checkpoint

- **Exact source/test workflow commit:** `85ee1c05a8d3f34c8486282ac74fb58b64e29927`. Documentation-only commits following this checkpoint are **not** the compiled APK SHA.
- **QA package:** `com.playereden.gridmates.wordsearch.qa` · `versionCode=36` · `versionName=0.8.2-records-journal-o-alpha-qa`. Save schema4, LAN v7 and gameplay scoring unchanged.
- **APK SHA-256:** `53f2ac5f9226312a2e04101445c8f755b129ab81abcbcd910faf7926e2181d2f` (the `app-debug.apk` inside the ZIP, *not* the ZIP hash).
- **Public QA signer certificate SHA-256:** `75cc3c896247d71fbd51f7a3ed41f90c8218a8a5a70df8c0f6b494b5cc23d419`. Not a production signing key.
- [QA APK ZIP artifact #13598478387](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42432833373/artifacts/13598478387) · [Android reports artifact #13598069229](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42432833373/artifacts/13598069229), both expiring **November 15, 2026**.
- **Pinned exact-green rollback:** [checkpoint/ws07f-v082-records-92-tests-upgrade-green-2026-11-07](https://github.com/PlayerEden/GridMates-Word-Search/tree/checkpoint/ws07f-v082-records-92-tests-upgrade-green-2026-11-07) at `85ee1c05a8d3f34c8486282ac74fb58b64e29927`.
- Prior [v0.8.1 exact-green rollback](https://github.com/PlayerEden/GridMates-Word-Search/tree/checkpoint/ws07e-v081-daily-clues-90-tests-upgrade-green-2026-11-07) still exists.

## Automated acceptance evidence

- [Standard Android CI #42432837445](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42432837445): **SUCCESS**, core and app tests, lint, instrumentation test Kotlin compilation, debug QA APK.
- [Full API35 emulator #42432833373](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42432833373): **SUCCESS, 92/92 instrumented tests**, zero failures and skips. Includes all prior 90 tests plus Records tabs/page math and a simulated 360×720dp scrollable phone view requiring the selected Race/Duel heading to be **visibly accessible**.
- Actual previously signed **v0.8.1/code35 APK → v0.8.2/code36 APK** in-place install via `adb install -r`, on emulator. Verified `schema`, `active`, `results`, `daily`, `identity`, `races`, `duels` survived unchanged. **PASS**.
- The first [emulator run #42430710954](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42430710954) **failed 1/92** because the selected Race heading was not visible beneath unrelated Solo/Daily summary cards at 360dp. Corrected by moving mode tabs above the summaries and displaying those cards only under Solo; updated regression now verifies visible headings. Do **not** treat that first failing run as green.
- Records histories use independent Solo/Race/Duel tabs and four results per page, with no silent truncation at 25, 15 or 12 saved entries. No data deletion or schema mutation.

## Limits and project scope

- **Physical two-phone Co-op P0 #53 remains OPEN**; user explicitly postponed hardware testing. No real phone Race/Duel validation, 200%-font/TalkBack manual signoff, production signing or Play release.
- [Draft PR #60](https://github.com/PlayerEden/GridMates-Word-Search/pull/60) remains **unmerged**, stacked on the WS-07E #58 branch. `main` untouched.
- Numbering note: the current PR's label **WS-07F Records** is a **whole-app UX subpass**. It does **not** complete the original roadmap's **WS-07F durability/performance audit** or **WS-07G integration/beta gate**. Those major gates remain planned/blocked until physical and source acceptance.

[Records owner acceptance](WS07F_RECORDS_JOURNAL_ACCEPTANCE_2026-11-07.md) · [Master roadmap](DEVELOPMENT_ROADMAP.md) · [P0 Co-op issue #53](https://github.com/PlayerEden/GridMates-Word-Search/issues/53).
