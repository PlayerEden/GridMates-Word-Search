# GridMates: Word Search — Competitor UX Benchmark & Mobile Board Research

**Research snapshot:** November 6, 2026  
**Status:** External desk research and design recommendations; **not direct hands-on installation/testing** of competitors.  
**Why now:** Owner's Android v0.4.4 screenshots reveal glyph/cell zoom mismatch, 15×15/20×20 readability issues, excessive header space, clipped theme title, unclear Move/Find state and target words pushed below the fold. See [P0 issue #24](https://github.com/PlayerEden/GridMates-Word-Search/issues/24) and [book-size QA issue #23](https://github.com/PlayerEden/GridMates-Word-Search/issues/23).

## Research methodology / confidence

Primary source: **official Google Play developer descriptions and store galleries**, plus PlaySimple's official site. Claims about features are those **developers advertise**, not independent verification of every current Android gesture. Secondary sources: selected community posts and screenshot references, which are **qualitative anecdotes, not representative user research**. Avoid asserting apps have pinch zoom or two-finger panning unless independently confirmed. Research focused on *classic straight-line grid word searches*, not word wheel or NYT Strands games. This is a competitive benchmark, not permission to copy another game's art, code or protected assets.

## Comparator scorecard — strongest directly documented mechanics

| Product / developer | Directly documented features | Specific lesson for GridMates | Cautions |
| --- | --- | --- | --- |
| **Word Search Ultimate — Havos** | Configurable grid 3–20 rows/columns incl non-square, direction ratios, vocabulary 500 common to ~80k, word-count cap, min/max word length, found highlighting, columns/spacing of target list, language, orientation, categories; supports both swipe and first/last letter selection | **Our most relevant mechanical reference.** Board size and search complexity should be independent; keep simple presets over an advanced customization screen. Make list density and input method adjustable | Claims from official store description, not proof of observed rendering or current pinch gestures |
| **Word Search Ultimate+ — Havos** | Explicit **movable divider** to allocate screen space between word grid and word list, plus similar customization | For 15×15/20×20, a player-adjustable grid/list split is a credible, existing pattern. Prototype a draggable split or persistent pull-up word drawer | Paid app; verify actual phone screenshots and accessibility before copying pattern |
| **Word Search Puzzles — LR Studios** | Unlimited offline generated puzzles, **night mode**, font choices, device-adapted puzzle generation, multiple languages. A user review mentions ability to change font size/type | Readable letter/font settings and responsive layouts are *first-class gameplay*, not cosmetic extras | "Adaptive" is a developer description; font sizing detail also from user review |
| **Word Search — Rottz Games** | Grids from **9×9 up to 20×20**, four levels and 15 languages; landscape/Chromebook support | A 20×20 long-form board is reasonable as a niche option, but must adapt to available viewport and input accuracy | Their marketed difficulty is size-dependent; do not copy that coupling |
| **Word Search - Classic Game — Pink Pointer** | **Three board sizes and three difficulties**, with easy allowing normal/diagonal and medium adding backwards words, hard adding clue-based play in supported languages. Grid lines and word-location hints | Direct validation for **direction-based difficulty independent of size**, plus optional clues/hints to increase challenge without shrinking text | Clue-based Hard differs by language; don't assume identical in every locale |
| **Word Search - crossword puzzle — Easybrain** | Daily challenges/trophies, hints, thousands of levels and words, offline, swipe across/vertical/diagonal, overlapping word letters | Benchmark for clear win-state, Daily continuity, tutorial and rewards; keep gameplay clean before adding a heavy shop or new cosmetics | Screenshots and description don't establish large-grid zoom mechanics |
| **Word Search Explorer — PlaySimple** | Developer describes smooth swipes, themed puzzle progression, destinations, calming sounds, visual polish | A coherent *journey/progression* can make the app feel finished; future GridMates book chapters can use this idea without copying backgrounds or economies | Popular level progression isn't the same as customizable traditional paperback boards |
| **SozGrid — KodeVertex** | No failure/timer pressure, partial-state save, secret word, offline, paper-like look with crisp grid/highlighter and a full-width-grid setting | Strong match for cozy, non-punitive GridMates tone. A successful find should feel satisfying without unnecessary countdowns or disruptive interstitials | Smaller newer app; not evidence of market-wide player preferences |

### Source links (official primary references)

1. Havos Word Search Ultimate: https://play.google.com/store/apps/details?id=com.havos.wordsearch
2. Havos Word Search Ultimate+: https://play.google.com/store/apps/details?id=com.havos.wordsearchplus
3. LR Studios Word Search Puzzles: https://play.google.com/store/apps/details?id=net.lrstudios.word_search
4. Rottz Word Search: https://play.google.com/store/apps/details?id=com.rottzgames.wordsearch
5. Pink Pointer Word Search - Classic Game: https://play.google.com/store/apps/details?id=com.pinkpointer.wordsearch
6. Easybrain Word Search: https://play.google.com/store/apps/details?id=com.easybrain.word.search
7. PlaySimple Word Search Explorer (official): https://playsimple.co/games/word-search and https://play.google.com/store/apps/details?id=in.playsimple.wordsearch
8. SozGrid: https://play.google.com/store/apps/details?id=com.kodevertex.sozgrid

### Community signal (use cautiously)

- A small August 2026 community thread describes older players struggling with tiny letter grids, interruptions and unwanted timers, and preferring a one-tap route into a puzzle: https://www.reddit.com/r/windowsapps/comments/1vw87g6/i_made_a_word_search_because_i_got_tired_of/
- Another small July 2026 post about a large-print puzzle site says **first/last-letter tapping** is easier than dragging for some users: https://www.reddit.com/r/puzzlevideogames/comments/1uu0jj5/i_made_a_word_search_site_for_my_grandparents/
- These are individual creator anecdotes (tiny engagement), not validated accessibility studies; use as test hypotheses and recruit actual target players.

## What the owner screenshots tell us (observed, NOT competitor claims)

- At zoom 125–250%, letters overlap or get clipped while apparent cell geometry stays nearly unchanged. **True viewport zoom is P0**.
- Up to three menu rows plus zoom toolbar cause wasted vertical space; part of the selected difficulty is truncated in the title.
- In 15×15/20×20, only a couple rows of target words are visible simultaneously, making scanning inconvenient.
- **Move / Find** currently reads as the next action rather than the current state; re-entry into tracing is hard to discover.
- The 20×20 board at 100% is physically tiny: a ~360dp-wide screen offers only ~18dp per column before margins. At 15×15 it's ~24dp. **A phone-sized board cannot display a 20×20 grid as large-font, accurately tappable cells all at once.** Therefore an enlarged scrollable/zoomable viewport or companion mode (tap endpoints, list overlay) is essential; a larger font alone does not fix it.

## Design principles: what to adopt vs what not to copy

**Adopt**
- Decouple **board dimensions**, **direction challenge**, **word rarity** and optionally **number of words** (Havos, Pink Pointer).
- Prioritize a fast “Continue / Daily / New Puzzle” path; hide fine-grained customization behind a single **Customize** control (PlaySimple experience).
- Offer adaptable and persistent **grid-to-target-list space allocation** (Havos+), rather than forcing the word list completely offscreen.
- Support both **swipe** and **tap first/tap last**, especially for seniors, motor accessibility and offscreen long words (Havos, community).
- Offer font/contrast/grid-line options without corrupting game difficulty (LR Studios).
- Preserve no-pressure Solo, offline resume and understated highlighter feedback (SozGrid); avoid mandatory timer, interstitial interruptions or rewards that punish missed days.
- Use theme/book organization and a satisfying end-of-puzzle moment later, but do not introduce cosmetics before the usability repair.

**Do not blindly adopt**
- A visually overloaded store/coin economy; brand identity is quiet paper/pencil and approachable.
- Lots of configuration controls on the first launch screen; preserve simple default and last-used presets.
- Pretend that 20×20 fits legibly on a small portrait phone without viewport interaction.
- Change saved board IDs or v1/v2 seed behavior when adjusting board rendering.
- Copy competitor backgrounds, icons, UI designs, screenshots or code into the shipped app.

## Concrete proposed GridMates screen design (NOT implemented)

### Compact header

- **Row 1:** back/Home (icon or one short word), *Woodland* with secondary small *15×15 · Classic*, overflow/more menu. No ellipsized full theme/challenge text.
- **Row 2:** *5 of 16 found* + elapsed time (nonpunitive) + hint icon, with accessible labels.
- **Zoom/navigation:** small controls overlayed on board edge or in its frame; show current mode **Find [selected] / Move**, and zoom % only when changing it. Don't take another full text-height row if avoidable.
- Move **New Puzzle**, feedback settings and Customize behind a well-labeled overflow/sheet. Confirm before replacing an unfinished game.

### Grid + target list allocation

Proposed **responsive split workspace**: top grid viewport occupies ~60–70% of available *gameplay* area (after status/navigation), with a persistently visible word-list region occupying the rest. Let users pull the divider, peek a drawer, or expand target list; test layout with actual 15×15 and 20×20 boards in portrait and landscape. At least 4–6 target words should remain visible in a typical portrait session when grid uses scroll/zoom; don't rely on fixed numeric heights across devices.

- On tablets/landscape with enough horizontal room, use side-by-side list and board.
- Word-list panel: two compact columns of words, found entries crossed/highlighted, can scroll independently; optionally filter to **Remaining**/All.
- If viewport is tiny due to font scaling, the word panel can collapse to a labeled, obvious sheet, but must not disappear without discoverability.

### A correct zoom + gesture contract

- Render **cells, letters, grid borders, highlighter strokes and pointer hit testing with one transform**; no independent font multiplication.
- Use intrinsic virtual-board dimensions `boardPx = cells × minComfortableCellPx` and a viewport with independent pan/zoom transform. Explicitly avoid Compose parent's measure constraints collapsing a requested larger `Modifier.size()`; check `requiredSize` or Canvas/graphics transforms and coordinate-inverse hit testing.
- Pan bounds should use actual `contentExtent - viewportExtent`; center when the content is smaller, clamp offsets when larger; handle orientation resize.
- Keep touch targets comfortably sized at chosen zoom; support long words crossing the visible viewport via zoom-out and reliable tap endpoints first, and assess optional edge-autopan or two-finger zoom/pan once single-finger tracing is reliable.
- Alternative accessible one-finger Move toggle can remain, with **current mode visually selected**, not only a button labeled with the *other* mode.
- The word-selection engine should never silently lose a path or award a word just because the board moves or a second finger appears.
- Correctness should hold across 100%, 125%, 145%, 150%, 185% and 250%, all corners, and found/highlight/hint paths.

## Prioritized repair backlog from research

| Priority | Change | Acceptance |
| --- | --- | --- |
| **P0** | True board zoom/pan/hit-test transform fix | Cell bounds, letters, highlighters and taps remain aligned on 15×15 & 20×20 at all zoom levels; no clipping/overlap |
| **P0** | Compact gameplay chrome | Core title/progress/time/hint visible in at most two concise rows; board starts materially higher |
| **P1** | Visible target words / resizable split | Typical portrait has several target words visible while grid remains playable; word list scrolls independently |
| **P1** | Clear current Find/Move state; improve gesture ergonomics | No accidental claims while panning; accessible current-mode indication, two-tap input preserved |
| **P1** | Explicit large-board usability paths | 20×20 is solvable without physically dragging offscreen beyond viewport; test long diagonals |
| **P2** | Font/grid/contrast settings and landscape adaptive layout | 200% font, narrow phones, portrait/landscape, screen reader support |
| **P2** | Completion and progress delight | Clear found feedback, honest results, replay/review, without disruptive animations |
| **P3** | Theme/puzzle-book storytelling | Smooth progression only after base board is pleasurable, reliable and saved-state safe |

## Validation study: measure rather than guess

1. Record current GridMates v0.4.4 screenshots and phone geometry as the **baseline**. Keep these screenshots private; don't commit personal status-bar images to public GitHub unless intentionally sanitized.
2. On a representative small/narrow Android and tablet/emulator, capture a **15×15 and 20×20** puzzle at 100/150/200/250% zoom, a found diagonal, and maximum pan to all four corners.
3. Count actual **visible target words**, top unused vertical dp, size of one touch cell, word-selection error rate (missed/incorrect drag), time to recover from pan or change zoom, and whether instructions were needed.
4. Run scripted Android instrumentation rather than only compiling Android tests, and retain screenshots/QA receipts with exact APK SHA.
5. Have at least one older/less-tech player and one experienced book-word-search player try **Resume, find a word, change zoom, find a long diagonal, open word list, quit/restart** without guidance. Record confusion and paths.
6. If comfortable, install the **reference competitor apps** on a test device and repeat the same tasks. Store listing claims do not count as a hands-on competitor test.

## Next action

Work from [P0 owner feedback issue #24](https://github.com/PlayerEden/GridMates-Word-Search/issues/24). Create a narrowly scoped follow-up draft branch for geometry, compact HUD, and word-list split; keep all existing v1/v2 seed contracts and save identities stable. **No new cosmetic collection or extra difficulty pack until this passes real Android rendering QA.**

**Summary:** Use Havos Ultimate for the **mechanics**, LR Studios for **readability**, Pink Pointer for **direction-based challenge**, PlaySimple for **clear progression**, and SozGrid for **calm, nonpunitive feel**. GridMates can combine those strengths while retaining its own warm paper-and-pencil identity and future nearby Co-op.
