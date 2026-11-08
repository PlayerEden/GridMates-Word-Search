# WS-11D2B — A Real Word-Search Book Page Screen, Without Publishing Unreviewed Books

**2026-11-08 — source-level Android UI integration. Release status: DRAFT.**

## What is now implemented

`BookPageGameplayScreen` is a real Compose screen using **the identical reusable `ZoomableWordGrid` and `WordList` components** already used by the normal Solo/Daily game. It handles swipe/tap/precise word selection, zoom and the existing responsive puzzle-board rendering. There is **no duplicate touch-engine implementation** and no routing a book into `SoloStore`.

The screen accepts an injected `BookCatalog`, exact `bookId`/`pageId`, independent `BookPageSessionStore` (GMWS partial file) and `BookLibraryStore` (GMWB committed completions), plus an effective board font and navigation callbacks. It reconstructs **only an immutable hash-matching V2/V3 book page**. If the catalog is missing/changed or a save is blocked, the screen displays a readable recovery/unavailable state and does not create any temporary fake book or overwrite damaged progress.

When a real word is traced, the existing claim rules validate the exact target path; the GMWS AtomicFile write succeeds **before FOUND is returned**. The view then refreshes its words-left list and progress. Session hints are saved immediately and reuse the existing three-stage highlighted-path UI. A foreground timer checkpoints every five seconds instead of writing on every frame; a crash can lose up to one checkpoint of timer count, but cannot lose a successfully claimed word.

On the final word, `BookPageSessionStore.finalizePage` preserves a fully solved GMWS sidecar until GMWB durably commits the book-completion award, then clears the sidecar. Failed cleanup can retry. A solved sidecar is reconciled before opening a new page. This is **recoverable two-file sequencing, not an atomic transaction**.

A completed page shows its original registered answers for review; **Practice again** starts a separate, ephemeral no-reward replay. No GMWS or GMWB write is made from that replay and its words are discarded when practice ends. A Next Page callback is available only when a verified unfinished page exists.

## Device UX details

- Native paper/cream/forest styling; readable title, page number, word count, visible saved status and Back.
- Exactly the existing `ZoomableWordGrid` toolbar: Select (no drag), Fit, zoom in/out, and large-board map where applicable.
- Existing `WordList` with paged targets. Phone-height budget and landscape rows are adapted without changing the old Solo rendering.
- 48dp minimum primary buttons. Independent hint reveal and saved three-step hint-path highlight.
- Book title and status use real Compose text rather than unapproved/generated decorative cover lettering.
- No change to existing Home navigation, Solo, Daily, Nearby, cosmetic Collection, old portraits, or schema-5 saves.
- No public Library button appears yet. **The first production BookCatalog remains intentionally empty** pending WS-10 editorial, content-rights and golden board approvals.

## Internal navigation adapter added

`BookLibraryFlow.kt` now connects the native `PuzzleBookshelfScreen`, `PuzzleBookDetailScreen` and new `BookPageGameplayScreen` into a complete internal route. Its only inputs are a caller-supplied approved `BookCatalog`, both independent book stores, effective board font and an Exit action. It does not create a catalog, activate an unapproved book, or alter `GridMatesShell`/MainActivity. A failed/unknown save shows recovery copy rather than pretending the library is empty.

`BookLibraryFlowAndroidTest.kt` adds a synthetic-only instrumented route from Library to Garden chapter to real shared board and back, with an actual AtomicFile bookmark saved and a persisted session after returning. These test fixtures are excluded from production source. The Android CI compiles these tests; actual emulator/phone execution remains a distinct gate.

## Source tests added

`BookPageGameplayScreenAndroidTest` has three **instrumentation source** tests:
1. Open an actual V2 synthetic test-only book page on a 320dp layout, resume an already found word and verify the shared precise-selection toolbar exists.
2. Open a genuinely completed test page, enter/exit practice mode and verify the committed GMWB file and GMWS sidecar are not modified.
3. Attempt to open an unregistered page against an empty catalog and verify no board, fake Hint button or session file is created.

CI compiles these tests and runs standard unit/lint/package verification. An actual Android emulator/physical run, screenshots, 200% font/landscape accessibility review, and touch-to-completion playtest remain open acceptance gates.

## Next integration gates (WS-11D3 / WS-10)

1. Approve source-provenanced themed vocabulary, rights and curated fixed page manifests for Woodland/Space/Garden. Do not promote the 532 editorial review rows or V4 preview research as approved.
2. Create an app-level lifecycle owner for the **approved** BookCatalog and both book stores. Drive `PuzzleBookshelfScreen` -> `PuzzleBookDetailScreen` -> `BookPageGameplayScreen` through one Home entry, without adding a crowded fifth nav tab.
3. Preserve/restore viewport on large book pages if a player relies on map zoom, and ensure a real device's Back/force-stop behavior matches current game.
4. Integrate independent GMWS and GMWB into explicit backup/export/import with recovery and upgrade checks; old Solo schema5 remains untouched.
5. Run physical-phone or emulator instrumentation and narrow-phone/200% text QA, install-over regression, actual touch and selected-word tests; sign off approved WS-12 art if used.
6. Merge in dependency order: gameplay PR #85 -> book core #95 -> book progress #96 -> bookshelf UI #97 -> session #98 -> this screen PR. The WS-12 art stack #92-94 is separate.

**This is a working UI component in source, not yet a new playable book feature in the shipped APK.**
