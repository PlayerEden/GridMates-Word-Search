# WS-09B1 — Multiple unfinished Solo puzzles: persistence contract (design-only)

**Date:** 2026-11-07 America/New_York. **Status:** source-audited design proposal, **NOT implemented**.
**Parent:** [WS-09A2 draft PR #66](https://github.com/PlayerEden/GridMates-Word-Search/pull/66), verified executable v0.8.7/code41 commit `2bd6b6a005d361ebc3304a5d46044b83b7b0c283`, with [97/97 API35 instrumentation, nine fixed old-puzzle fingerprints and real partial v0.8.6→v0.8.7 upgrade](WS09A2_BUILD_QA_2026-11-07.md). This design branch is stacked on the post-verification docs head of PR #66. No change to the APK, storage file, schema, signer, generator or active installed family devices.

## Why WS-09B comes before the 100×100 generator

Today a player can have **one unfinished Solo puzzle**. Starting a fresh puzzle replaces `profile.active`. The owner wants to pause a 15×15, start a 20×20, and eventually return to a continuous 100×100 board without losing any previous work. This is a correctness and data-survival milestone, not a visual gallery milestone. **Do not widen generators or change enum order before completing this.**

### Source audit of the current implementation

- `core/.../SoloProgress.kt`: `SoloProfile` contains one required `active: SoloRun`, with separate `results`, `dailyEntries`, `identity`, `raceResults`, `duelResults`. `SoloRun.puzzleId` identifies deterministic content; it does **not** identify a unique user session. `SoloProgressRules.complete` records a completed puzzle once.
- `app/.../SoloStore.kt`: `decode` accepts schemas **1..4** and reads one `active`. `encode` always emits schema **4** with keys `active`, `results`, `daily`, `identity`, `races`, `duels`. Backups share this serializer. One `AtomicFile` named `gridmates_wordsearch_solo_v1.json` protects the profile.
- `choose`, `chooseV2` and `newPuzzle` currently **replace** `profile.active`. `claim`, `hint` and `tick` update only that active run; writes flush on important events and every 10 running seconds. Current `nextSeed` compares only with the active seed.
- `GridMatesShell.kt` Home currently shows **one** current Solo card and its Continue/Play button. Pre-game selection belongs to later **WS-09C**; WS-09B must still expose safe library actions rather than silently replace work.
- Daily date entries, Race/Duel journals and cosmetic/user preferences must remain separate. Sound/haptics/selected font are stored in **SharedPreferences** and are not currently part of backup JSON. Existing historical APK compatibility tests do not prove family-device migration.

## Storage decision proposal

**Prefer a versioned extension of the existing single `AtomicFile` JSON profile for WS-09B, not Room yet.** Small numbers of Solo metadata-only runs are cheap to store; a single atomic document preserves all existing identity, history and journal state together; it avoids adding a second database and a multi-store consistency problem in a high-risk migration. Benchmark save/load time and file size with 3/25/100 sessions before locking this choice. Switch to Room only if measured needs outweigh the migration risk; never silently mix two authorities.

**Proposed schema5 shape** (not final field names or implemented code):

```json
{
  "schema": 5,
  "active": { "theme": "WOODLAND", "difficulty": "MEDIUM", "seed": 42 },
  "selectedSoloId": "legacy:<stable-puzzle-id>",
  "soloSessions": [
    {
      "id": "legacy:<stable-puzzle-id>",
      "createdAtMs": 0,
      "lastPlayedAtMs": 0,
      "run": { "theme": "WOODLAND", "difficulty": "MEDIUM", "seed": 42 }
    }
  ],
  "results": [],
  "daily": [],
  "identity": {},
  "races": [],
  "duels": []
}
```

The `active` shown above is a **compatibility mirror**, not a second independent source of truth. If retained, it must exactly equal the selected `soloSessions[].run`; reject mismatches, never pick whichever silently seems newest. Alternatively remove the mirror **only after** auditing every caller and ensuring schema5 backup contracts have a deliberate legacy-read policy. Keep the old schema4 encoder/fixture for downgrade/export testing.

### Identity and ordering

1. Each session gets an **immutable, unique session ID**, independent of its puzzle content ID. Legacy migration derives a deterministic ID so repeated imports do not create different IDs for the same pre-upgrade active puzzle. New sessions use collision-resistant IDs; two same-setup plays must be distinct, and seed/puzzle-ID uniqueness checked separately.
2. Store **created** and **last played** timestamps for sorting; preserve the original run's seed/theme/size/challenge/found IDs/hints/elapsed seconds exactly. Legacy timestamps may be unknown—use a documented sentinel, **not invented historical play dates**.
3. Keep **all unfinished sessions**, including untouched runs. A completed session remains traceable through its result; decide explicitly whether it stays in the session library as completed/archived, but never delete its data because a new game starts.
4. Default view should sort by `lastPlayedAtMs`, with current/unfinished easily identifiable. No auto-deletion or unexplained capacity eviction. If a safe bound is necessary, refuse creation with an actionable message **before** overwriting old sessions.
5. Daily entries are date-keyed and **never inserted into Solo session lists**. Race/Duel record IDs, profile identity and cosmetic progression are carried forward unchanged.

### Migration and failure semantics (P0)

- For each supported **schema1–4** save, construct **exactly one** initial session from the existing `active`, with the identical `SoloRun`; preserve other fields and their existing validation. A schema4 imported backup must still preview and restore without requiring a pre-upgrade app.
- Only commit schema5 **atomically** after a full parse, validation and successful write. A corrupt/truncated/unsupported file must fail closed and leave original bytes/.bak intact. Never replace an unreadable save with a fresh empty library.
- A **schema5** backup must round-trip all sessions, selected ID and old records. Reject malformed session arrays, duplicate IDs, missing selected session, duplicate or conflicting puzzle IDs if disallowed, invalid timestamps, impossible partial state and contradictory mirror data. No silent filtering/dropping of a whole session.
- Changes to active session, claims, hints, completion, deletion and creation need a **commit-success contract**: if a write fails, retain the previously durable state on screen and disk, make the error visible and block risky navigation. Existing `chooseV2` changes in-memory state before flushing; do not extend that pattern.
- Future engine versioning remains separate. **Never modify** v1/v2 target orders, placement, filler, seeds, `PuzzleBoardSize` enum ordinals, or the nine WS-09A2 pinned goldens. Before writing schema5, confirm legacy partial Solo/Daily fixtures load identically.
- The **existing family member's APK** has unknown package/version/signing. The verified v0.8.6→v0.8.7 QA-signer upgrade does **not** establish an upgrade route for it. Do not uninstall, clear storage, promise a safe install-over, auto-merge or recommend a schema5 APK for that phone before backup/version verification. Reinstalling an older schema4 APK over schema5 may be unsupported even when signatures match; regression must explicitly cover rollback behavior.

## Implementation breakdown (each bounded; keep draft PRs and green checkpoints)

### WS-09B2 — Pure model and migration tests first
- Define the immutable session identity + collection API separately from Android storage and UI; use the current selected-run compatibility accessor to limit existing caller changes.
- Add deterministic schema1/2/3/4→5 test fixtures, using **WS-09A2 actual v0.8.6 partial** fixture. Assert exact grid/target placement, found/hinted IDs, timer, Daily dates, identity and Race/Duel journals.
- Add three distinct concurrent Solo sessions (e.g. 12×12, 15×15, 20×20) with separate found/hint/timer state; verify choosing one does not mutate the other two.

### WS-09B3 — Atomic storage and backup
- Add schema5 read/write and explicit recovery behavior to `SoloStore`; leave historical fixture and old decoder test paths available.
- Exercise create/switch/claim/hint/tick/complete/restart/export/preview/import across all sessions.
- Test corrupt JSON, duplicate IDs, invalid selected ID, unsupported future schema, low-space/failed-write and partially interrupted atomic write; prevent uncommitted transitions.

### WS-09B4 — Minimum library UI and Android verification
- Provide **My Puzzles**, visible Continue/switch, and an explicit New Puzzle action. Avoid building the full theme/size pre-game flow until WS-09C.
- Pass multiple-session navigation and resume on API35, rotation/process death, Android background/restart, full backup export/import, and **real same-signer versioned APK install-over** with nonempty partial saves.
- Minimum acceptance: **three simultaneous unfinished puzzles** survive restart, signed upgrade and export/import; their exact board letter rows, word placements, found words, hints and elapsed time are intact; old Daily, identity, Race and Duel histories are unchanged.
- Pin source commit, test count, APK SHA-256, signer, full report and rollback checkpoint. Only then evaluate optional real-phone Solo beta; never conflate that with the still-open physical two-phone [Co-op P0 #53](https://github.com/PlayerEden/GridMates-Word-Search/issues/53).

## Explicit non-goals of this design PR

No schema5 code, no runtime migration, no new APK, no Google Play/release signing, no actual physical-phone acceptance, no 25×25/30×30 generator, no square-margin redesign, no 100×100 claims, and **no merge to main**. The approved next coding slice is **WS-09B2**, contingent on preserving the WS-09A2 green baseline.
