# WS-11C — Safe Offline Puzzle Book Progress (source checkpoint)

**November 8, 2026. Status: DRAFT source only; not yet visible in Android or a published book.**

## Player value

When the first curated Puzzle Books ship, each finished page and bookmark should survive force-stop, restart, offline play, and a normal app upgrade. A broken/incompatible file must *never* silently reset a player's books. Existing **Solo / My Puzzles / Daily / Nearby / Records / Collection** data must not be mixed with these book pages.

## Implemented

- **Core BookProgressCodec.kt**: version-1 deterministic binary format with "GMWB" magic, 1MiB maximum, bounds on counts and identifiers, canonical sorted book/page IDs, and SHA-256 over all preceding bytes.
- **Fail-closed decoder**: bad/truncated/checksum-modified bytes, unknown version, trailing fields, invalid UTF-8, duplicate IDs, unknown chapters/pages, and a temporarily missing publisher catalog are rejected without silently reducing previous completion history.
- **BookLibraryStore.kt**: separate Android AtomicFile under a validated filename. Bookmarks/completions are committed only after the core catalog and state pass validation. startWrite/finishWrite/failWrite preserve an earlier committed file across errors. It never writes the SoloStore's historical schema-5 profile.
- **Backup integration seam**: exportForManualBackup and importManualBackup operate on the exact versioned blob after integrity and catalog validation; the UI/manual share picker is NOT yet connected. A blocked/corrupted store refuses imports and all writes to preserve evidence for explicit recovery.
- **Core JUnit tests**: stable serialization regardless of collection order, roundtrip of two sample volumes, detection of byte edits, truncation, unknown version, forged trailing fields, dangerous record counts, deleted books on app downgrade and previously saved bookmarks.
- **Android instrumentation source tests**: real AtomicFile reopen/restore, validated full board completion, manual import rejection without modifying saved bytes, and non-destructive failure on bad/unknown-catalog file. An Android emulator/device execution is a separate acceptance gate; compiling these tests does not prove runtime success.

## Separate storage and safe sequencing

Do **not** integrate this class into MainActivity yet. The class needs an approved, bundled, nonempty BookCatalog to bind future saved progress correctly. Instantiating it with an empty list now could mark previously saved future books as unavailable on a downgrade. No fake volumes, experiments or word-review candidates belong in production.

Avoid introducing an incompatible schema-6 migration just for book data. The independent journal can later be included explicitly in a user-exported full backup bundle, with a transaction/recovery plan for paired restore. Nothing currently claims that the existing manual Solo backup already contains book progress.

## Format v1 (design freeze for future release review)

- Four ASCII magic bytes GMWB; big-endian int version=1; int volume count.
- Per volume in ascending stable book ID: ID with bounded UTF-8 length, int completed count, ascending page IDs, one byte 0/1 bookmark flag, optional bounded bookmark ID.
- Final 32 bytes are SHA-256 of the entire preceding payload. The digest is for integrity/error detection, **not cryptographic authentication or anti-cheat**. Users control their offline data.
- Strict core catalog validation after decode, and strict old-vs-new catalog checks before production upgrades. Future format/schema changes require an explicit v2 migration and test fixtures.

## Remaining gate before claiming playable books

1. WS-10 editorially approve a real themed word pack and its exact dictionary/source revisions (candidate words and V4 preview are not publishable).
2. Freeze source-built Woodland, Space and Garden pilot page manifests using real board/answer hashes, explicit chapter order and unique IDs.
3. Combine this journal with a real Bookshelf/Book/Chapter/Play UI and confirm proper save-before-success behavior. Replay a finished page without awarding duplicate completion.
4. Integrate a safe manual export/import/restore control and validate mixed old schema-5 Solo plus independent book journal upgrades.
5. Test real emulator/phone force-stop, app update, invalid import, no-network access, 320dp + 200% font scale, landscape, screen reader and content novelty. WS-12 artwork needs separate human approval.
6. Only then consider turning the Library card on in the home screen; preserve current Free Play/Daily/Nearby navigation unchanged.

**QA must record the exact workflow/SHA. A green CI compile is not an Android physical playtest.**
