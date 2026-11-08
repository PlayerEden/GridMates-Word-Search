# WS-11D2A — Immutable Book Page Game Sessions and Crash Recovery

**November 8, 2026 · DRAFT, source-only checkpoint; no player-visible Bookshelf or approved book release**

## What this milestone enables

Book gameplay must remember partial real found words, hints and time independently from Free Play, My Puzzles, Daily and Nearby. A book page may be completed only by finding all genuine word placements on its registered, checksum-verified board. If the process dies between marking a page solved and saving book completion, the player's completed progress must be recoverable.

### Added to the actual Word Search codebase

- `BookPageSession.kt`: real standalone core model for a page's immutable book ID, page ID, full puzzle ID and board checksum, found target IDs, hint levels and elapsed time.
- `BookPageSessionRules`: reconstructs ONLY a registered exact-hash V2/V3 board with its proper theme, board size, challenge and seed. Any unknown book/page, mismatched hash, experimental V4 preview, or inconsistent puzzle generation fails closed.
- `find`: calls the game's actual `WordSearchRules.claim` so valid word paths count only once; invalid gestures never award progress.
- `hint`: independent bounded 3-step target clues, never auto-completing a found word. `tick`: bounded foreground time, no timer after solved.
- `BookPageSessionCodec.kt`: independent GMWS v1 portable partial-page file with canonical sorting and SHA-256 corruption detection. Completed BookProgressCodec GMWB v1 and historical Solo JSON schema-5 remain byte-format unchanged.
- `BookPageSessionStore.kt`: independent Android `AtomicFile` adapter, save-before-success for newly found words, reopen/resume, blocked writes for corrupt or unknown-content journals.
- `finalizePage` plus `recoverSolvedPages`: two-step recoverable award. Save the fully found page first; commit its result in the separate GMWB book completion file; delete the session only after that commit. If interrupted at either stage, a repeated finalize is safe and idempotent. This is NOT a cross-file atomic transaction; the session is a recovery record.
- `BookPageSessionTest.kt` and `BookPageSessionStoreAndroidTest.kt`: tests for exact board generation, actual found and reversed word paths, no double claim, hints, clocks, separate books, tampering/unknown IDs, canonical backup round-trip, crash after full page and resumed completion, corrupt-files/downgrade preservation, legacy Solo preservation.

## The practical book-player UX still to implement

Do not connect this to MainActivity or pretend it is already a public playable book mode. A full book-page board requires reuse/refactoring of our proven touch selection, highlight rendering, compact HUD, accessibility, hint overlays and finish dialog from `WordSearchScreen` without routing a book into `SoloStore` or overwriting its active game. For now, the new native Bookshelf and Detail screens (PR #97) are unhooked and remain truthful empty-state shells.

The first REAL content pack must still pass WS-10 human editor sign-off and frozen manifest hashes. `BookCatalog()` remains empty for production. Synthetic V2 words in tests are not approved production volumes, and preview V4 is prohibited.

## Why a separate in-progress file

`GMWB` is the already-defined book-completion journal (completed page IDs, manual bookmark). Altering its wire format would invalidate earlier WS-11C tests. `GMWS` is a separate local in-progress sidecar with a 1MiB bound, strict version and catalog checks, and a standalone Android AtomicFile. It is intentionally NOT a new field in the historical Solo schema-5 profile.

## Before a player-facing release

1. Approve real Woodland / Space / Garden themed word lists, licensing, page order and exact board fingerprints; freeze all content.
2. Implement a shared book-player UI reusing verified touch/zoom/selection mechanics, including the board-specific hint states.
3. Wire actual book page navigation and store lifecycle; call save after every find, throttle timer flushing, save on STOP; finalize completed pages and reconcile solved sidecars at next safe startup.
4. Validate 320dp/landscape/200% font, actual emulator touch and screen reader. Instrumentation code compiled by CI is not proof tests ran on devices.
5. Test unplug/offline, app force-stop halfway through a page, crash between the two completion writes, upgrade over current QA APK, import/export, rollback, and past Solo/Daily history unchanged.
6. Reconcile PR ancestry: gameplay #85 -> book model #95 -> book progress #96 -> native UI #97 -> this D2A PR. WS-12 art is independently stacked on draft PRs #92–#94 and requires review.
7. Do not mark the product Bookshelf playable, claim a new release or publish artwork until actual production page content + Android integration pass all gates.

This PR proves the page-game state/recovery engine. It is not a user-visible playable-book APK.
