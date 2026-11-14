# WS-16B: Illustrated asymmetric quick actions + Random Puzzle mode
**Date:** November 9, 2026. **State:** Design proposal. This document commits no app implementation or new assets.

## Owner's direction
The owner likes WS-16A's real-phone carousel-first Home. Wants visual buttons in place of truncated labels: an original two-person silhouette for Together, a labeled Daily Puzzle with its own calendar asset, and intentional width asymmetry. Newly requested: a real randomly generated word-search board, mixing words from any approved books plus extra eligible vocabulary, so the game remains replayable after the library's few volumes are completed. This means a random *board*, NOT randomly picking an existing book/page.

## Home layout underneath illustrated carousel

    ┌──────────────────────────────────────────┐
    │ [bookmark] Continue Solo              ▶  │
    ├──────────────────────────────────────────┤
    │ [shuffle art] Random Puzzle           ›  │
    │   A fresh mix of words each game         │
    ├──────────────────────────┬───────────────┤
    │ [calendar] Daily Puzzle  │ [2 figures]   │
    │         ~65% width       │ Together ~35% │
    └──────────────────────────┴───────────────┘
           New Solo                  My Puzzles

Retain the large illustrated book as the main visual anchor. Compact Continue and Random into short, useful rows. Daily gets full visible text; Together gets visually dominant original silhouette but a small visible label and full accessible name. New Solo/My Puzzles remain as tertiary text links. Keep all touch surfaces at least 48dp; stack / adapt at 320dp or increased font scale rather than truncate. Avoid adding three oversized hero cards. Keep bottom navigation during this transitional milestone.

## Important current-source finding
SoloSetupScreen already lets a player select theme, board size and challenge. SoloStore.chooseSolo creates a fresh seed. WordSearchGeneratorV2/V3 use curated words **from a selected theme** and reconstruct stable puzzles from versioned seeds. Random Puzzle must clearly differ: a single one-tap *mixed-theme word pool*, not the existing New Solo under a different name.

## Random Puzzle V1 product flow
- Home > Random Puzzle > immediately playable fresh board, no mandatory setup sheet.
- Proposed default: 15x15 Classic or last sensible selected size/challenge. Optional size/challenge choice affects *the next puzzle* and never destroys a saved run.
- Puzzle results: Play Another Random, Review, Home. Unlimited new seeds/arrangements in the practical sense; finite curated vocabulary means word repetition is inevitable over time. Do not claim mathematically infinite unique words.
- Separate selection identity and results from book completion and fixed Daily challenge. Keep distinct Random solved count while accurately counting any shared Solo statistics.
- Clearly label save/resume. Existing incomplete Solo and book pages must not be overwritten, reset, or lose hints/time when Random begins.

## Word bank and generation contract
- Add a **separately versioned mixed bank** containing approved words from existing theme packs (Woodland, Space, Garden initially) plus a new curated non-book pool (e.g. general everyday words). QA-only test book vocabulary must not silently ship in release.
- Normalize words, dedupe across packs, label provenance, filter by length for chosen grid, reject inappropriate/ambiguous/poorly formed entries; validate that each target can be traced and no accidental duplicate target path is scored.
- Weight across thematic pools so a board really feels mixed, not dominated by one existing theme. Avoid recent repeats when feasible and never duplicate a word within a single puzzle.
- Use reproducible versioned ID with content version, mode, board size, challenge, seed. **Freeze V1/V2/V3 IDs, word banks and placement behavior**; develop a new versioned generation path rather than modifying existing saved-board replays.
- Reuse proven placement, retry, hint, scoring and path-validation concepts, backed by unit regression for 12x12, 15x15, 20x20, optionally 25x25/30x30 only if validated.
- Offline only; no external word fetch or unchecked AI-generated word lists. If generation cannot produce a valid board, leave saved state intact and provide a truthful error/retry.

## New illustration assets
- Random: original shuffle/letter-grid insignia, avoiding a tiny ambiguous dice-only symbol.
- Daily Puzzle: original calendar/puzzle-page asset, always accompanied by complete native text.
- Together: a strong, friendly two-person silhouette with a shared-page clue; compact native Together label and screen-reader "Play Together".
- Continue: short bookmark ribbon motif. These should have independent editable SVG/vector masters, transparent exports and manifest hashes, matching current GridMates visual identity.
- Native action labels, save details, and all changing text must remain native, never baked into PNGs.

## Focused next implementation batch
1. Responsive quick-action UI with asymmetric Daily/Together widths, new Random Puzzle row, no clipping; compact tall, sparse hamburger menu without losing routes.
2. Versioned mixed-word bank and deterministic Random generator in core with distinct run ID and save compatibility; do not implement Random button as a dead placeholder. Respect current QA-only book content gate.
3. Navigation + local Random session, resume, achievements/records separation, Play Another Random, and non-destructive failure behavior.
4. Full Shelf polish from owner screenshots: avoid detached duplicate spine art and improve visually sparse final row while keeping genuine chapter/page navigation.
5. One consolidated Android QA artifact, exact-source Android and artwork/release checks, real device review; draft PR remains unmerged.

## Decision status
Confirmed user desires: visually led Together, labeled Daily Puzzle, intentionally asymmetric width, Random generates a new board using book and extra vocabulary for ongoing replay.
Recommended defaults to evaluate on phone: visible Random Puzzle button, one-tap instant play, initial 15x15 Classic preset, 65/35 Daily/Together width, next-puzzle settings. No Random implementation or artwork is claimed by this doc.
