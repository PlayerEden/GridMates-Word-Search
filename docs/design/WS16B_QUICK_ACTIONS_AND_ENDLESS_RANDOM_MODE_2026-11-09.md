# WS-16B — Asymmetric Quick Actions + Endless Random Play

Date: 2026-11-09
State: Approved design direction / detailed implementation brief. NOT a completed new mode or APK.
Scope: Word Search Home quick actions beneath WS-16 carousel and future standalone procedural mode.

## Owner requests and intended result
- Daily Puzzle may take more horizontal space than Together; buttons do not need equal width.
- Together can use a custom, recognizable two-person silhouette as a small illustrated button; do not force the "Toget..." truncated label.
- Daily Puzzle should retain its full visible text alongside a charming calendar asset.
- Leave room for a genuine "Random Play" mode which creates NEW word-search boards from a mixed pool of available book-theme words and additional independent vocabulary; not a random existing book/page. Finishing all books must never stop play.
- Keep existing one-tap book carousel, Continue Solo, New Solo and My Puzzles discoverable.

## WS-16B home actions wireframe (concept only)

    ┌─────────────────────────────────────────┐
    │      ... featured book carousel ...     │
    │            [ Full shelf ]               │
    │ [bookmark] Continue Solo         [>]    │
    │ ┌────────────────────────┬────────────┐ │
    │ │ [calendar]             │ [two       │ │
    │ │ Daily Puzzle           │ silhouettes│ │
    │ │                        │ Together*  │ │
    │ └────────────────────────┴────────────┘ │
    │ [shuffled letters] Random Play      [>] │
    │        New Solo        My Puzzles       │
    └─────────────────────────────────────────┘

**Layout:** Daily approximately 60-65% of usable quick-action row, Together 35-40%. Use a large, recognizable paired-person asset on Together; the full visible "Together" label is preferred where space allows and a clear first-use cue must exist for icon-led designs. ALWAYS use native accessible action label "Play Together — local Wi-Fi", even if the decorative art has no visible wording. The Daily label stays fully spelled out. On 320dp or 1.5–2x font sizes, reflow/stack; never ellipsize core action labels. Each hit area at least 48dp. Buttons are original warm-paperback assets with native text; avoid flat oversize generic cards and baked-in progress text.

**Random Play:** standalone highlighted illustrated row with jumbled letter tiles and shuffle flourish, native "Random Play" label and a short explanatory caption such as "Fresh board every time". Do not make the new mode look like opening a book. Preserve vertical space for the bookshelf and existing bottom navigation (temporarily).

## Random Play game contract

1. **What it is:** a true offline procedural board generator, not a random book/page and not a daily reroll. Each round chooses a fresh seeded set of words and valid paths from a reviewed on-device mixed vocabulary. Fresh challenges keep coming after a finite bookshelf is completed.
2. **Vocabulary:** pool is assembled from approved Woodland, Space, Garden and future theme vocabulary plus a separate editor-reviewed general/everyday word list not tied to any book. Should function when zero finished/published books are installed. Do not leak QA-only book pages into production. Validate letters, lengths, duplicates, substring collisions, familiarity and sensible difficulty. Neither obscene/inappropriate nor meaningless filler may become a target.
3. **Existing foundation:** current Solo already uses seeded generators with themed word pools: v1; WordSearchGeneratorV2 for 12/15/20 boards; WordSearchGeneratorV3 for 25/30; SoloSetupScreen currently selects one theme, board and challenge. The request is different: mix themes and add independent vocabulary. Do NOT mutate frozen existing V1/V2/V3 word lists, IDs, enum ordinals or BookPage SHA/payloads; existing saved puzzles must continue to reconstruct exactly.
4. **Implementation:** build a new versioned mixed-vocabulary generator / identity namespace, or explicit separate generator policy and lexicon version. Reuse proven placement and validity checks where safe. Save the random seed, generator/lexicon version, board size, challenge, found words and hints so a partially solved board survives app restart. Keep backups compatible and avoid corrupting Solo and Book progress.
5. **Play flow:** Home → Random Play generates one board with default 15×15 Classic, without mandatory setup; optional settings for sizes 12×12, 15×15, 20×20 (larger only after verified performance) and Relaxed/Classic/Expert. Finish → results → "Play Another" makes a new seed/word subset quickly; keep an optional "Review Board".
6. **Quality:** every target actually placed, each expected word has an unambiguous line, duplicate/substrings filtered, all hints lead to real answers, tap and finger-trace recognized, challenge directions followed, failure retries bounded. Seed + version should recreate board. Add varied mixed-theme sampling and modest recent-board/word-list anti-repeat history; "endless" means a continuing stream of generated challenges, not a mathematically infinite supply of never-repeated words.
7. **Scoring/progress:** Random Play count, solved words and optional streak tracked separately from book page completion and old Solo records. No need for online accounts, global leaderboards, ads or networking.

## Asset pack
- Daily: original calendar/page with marked date, readable native "Daily Puzzle".
- Together: original two-person silhouette, gently linked by shared puzzle motif; do not pack small facial detail into 44dp.
- Random: original scrambled letter tiles with shuffle line; more word-search appropriate than casino dice.
- Continue: bookmark/ribbon/turned page and native dynamic progress text.
All individual editable masters, documented exports/hashes, QA/release asset gates. Reuse family palette and visual weight.

## Work plan and acceptance
- **WS-16B visual batch:** asymmetric quick actions and original action assets, fully fix Daily/Together truncation, compact the giant hamburger menu while preserving routes, improve Full Shelf detached miniature spines and empty Garden shelf. Keep owner-approved big book cover carousel.
- **WS-17 gameplay batch:** implement and unit-test the new mixed generator, local resume and backup, playable Random route and Play Another; no fake working action. The Home slot can be pre-planned in WS-16B, but only make the actual button active when the mode really exists. Coordinate the two batches for one meaningful owner APK when feasible.
- **Regression:** 320/360/393dp, large fonts, TalkBack, Back, quick-action touch targets, save/reopen, existing Solo/Daily/Nearby/Book routes. Test at least several distinct Random boards for solvability and saved seed replay.
- **Status honesty:** this file is a specification, not proof of working Random Play or compiled APK. Keep PR #131 draft and existing WS-16A verified build available for rollback.

Current relevant source files: Ws16HomeScreen.kt, Ws15VisualKit.kt, SoloSetupScreen.kt, SoloProgress.kt, WordSearchV2.kt, WordSearchV3.kt, BookCatalog.kt.


## 2026-11-09 owner follow-up — player-facing naming and Quick Play setup (supersedes earlier labels)

**Approved direction:** Rename current selectable solo mode **Custom Play** because it already lets the player choose theme, board size and challenge. Rename proposed new endless mixed-vocabulary mode **Quick Play** (not "Random Play" or "Endless" on the home UI). This is a design decision; rename/route and new generator are **not implemented** by this note.

**Home:** Quick Play gets its own prominent original shuffled-letter-tile button beneath Daily Puzzle + Together; Custom Play and My Puzzles remain accessible, and current Solo resumes become **Continue** or **Continue Custom** when clearly the existing custom puzzle. Do not lose resume or ongoing solo save history during the name change.

**Quick Play interaction:** Home → Quick Play opens a compact native size-selector overlay with three large choices:
- Small: 12×12, 12 targets (existing BOOK_SHORT dimensions)
- Medium: 15×15, 16 targets (BOOK_CLASSIC; initial selection)
- Large: 20×20, 24 targets (BOOK_BIG)

**Recommended shipped behavior: tap the chosen size to start immediately** — Quick Play → Small / Medium / Large → puzzle, just two taps total. No extra Start button in the actual overlay unless owner testing demonstrates the need for a confirmation step. Medium (15×15) should be the visual default/recommended choice, not a forced selection. No theme or difficulty picker in Quick Play: internally use a balanced Classic challenge across a curated mixed-theme + independent general vocabulary. UI may remember last size, but never force a tutorial/setup dialog. After completing a board, **Play Another** immediately generates a fresh puzzle at the same size; optional Change Size reopens selector.

**Custom Play:** preserve all current Solo Setup choices and gameplay; only rename UI labels where appropriate, not serialized puzzle IDs or content versioning. Avoid displaying "Solo" and "Custom" in a confusing mixed fashion; underlying internal class and file names may stay for backward compatibility.

**Technical / QA:** Versioned separate generator for mixed words (the user specifically wants randomly generated boards, not random book pages), works offline and with no approved book content, seed/lexicon save identity stable; preserve current V1/V2/V3 generated output and save semantics, and compare different Quick Play boards for quality and reproducible restart. Book play, Daily, Together unaffected. Avoid "Play Another" overwriting unfinished boards silently; save/abandon should be explicit or supported by existing paused-puzzle library.

**Action art:** Change formerly named Random Play asset to **Quick Play**; shuffled letter tiles visual stays. The button label should be full native "Quick Play". Custom Play can use the existing Solo grid illustration or new sliders/pencil icon, not a separate giant illustrated card.

**Implementation status:** Player-facing naming and selection overlay are approved for WS-17 design only; no new Android APK is produced by a design-doc change.
