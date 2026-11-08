**Long-term library decision — November 8, 2026:** Aim for **10,000+ unique, approved words across 30+ themed categories** rather than keeping the permanent design at 210–250 words per theme. That smaller number remains a useful *interim content-quality goal*. See [WS-10 content roadmap #87](https://github.com/PlayerEden/GridMates-Word-Search/issues/87) and [draft foundation PR #86](https://github.com/PlayerEden/GridMates-Word-Search/pull/86). **No 10K bulk import or approval has occurred.**

# Word Search — Master Genre Research & Design Reference

**Maintained for:** GridMates: Word Search  
**Created:** November 8, 2026  
**Document class:** External genre research + internal design hypotheses (not a gameplay implementation or release approval)  
**Review cycle:** Revisit demographic/market data annually; update internal game evidence per verified source-pinned QA milestone.  
**Related:** [Master Game Design](MASTER_GAME_DESIGN.md) · [Development Roadmap](DEVELOPMENT_ROADMAP.md) · [Source-level word-library inventory](WORD_LIBRARY_STATUS_2026-11-08.md) · [Physical UX study](WS07_PLAYTEST_RESEARCH_AND_UX_SPEC_2026-11-07.md)

> **Critical evidence convention:** A population statistic about **video games generally** does not prove who plays **word-search games**. A developer's proposed puzzle-quality threshold does not prove what players enjoy. And a cognitive-training study of **crosswords** does not establish a benefit from **word search**. Keep those categories separate.

## 1. Executive overview

A word search (also called a word find, seek-and-find, sopa de letras and other language-specific names) is a letter grid containing a list of hidden words. In the usual straight-line version, words run horizontally, vertically or diagonally, often in either direction; cells may overlap when letters agree. The player scans the grid, recognizes a target sequence, and marks the matching path. Designs range from small, highly readable classroom and leisure grids to large dense books with dozens or hundreds of targets.

Why the format endures is a **design interpretation**, not a measured population finding: rules can be explained in seconds; there is little language production or trivia recall; solving is self-paced; and progress can be made in brief sessions or extended, calm play. The format works in paper books without any internet and can be adapted for offline mobile play without replacing its essential appeal.

For GridMates, the product promise is **a trustworthy, comfortable puzzle-book experience** with consistently constructed puzzles and optional good-company nearby play—not unlimited features, mandatory accounts, or urgency.

## 2. History and origins

- **Early/mid-1960s (attribution uncertain):** Spanish creator **Pedro Ocón de Oro** developed a form of *sopa de letras* (letter soup). Multiple accounts describe independently emerging word-search-like games; it is **not** safe to claim one universally agreed worldwide inventor.
- **March 1, 1968:** **Norman E. Gibat** published an English-language word search in the advertising *Selenby Digest* in Norman, Oklahoma. One historical account describes a **20×20** grid featuring **34 Oklahoma town names**, arranged in horizontal, vertical and diagonal directions. Teachers and readers helped propagate the format.
- **Around 1970:** French-Canadian **Jo Ouellet** developed the themed **Wonderword** format, including a message revealed by remaining letters.
- **Later decades:** Word searches spread into newspapers, puzzle magazines, paperback collections, classrooms, printable worksheets, websites, and mobile apps. Regional naming varies; the central straight-line mechanic remains recognizable.

**Research nuance:** “Gibat invented the word search in 1968” is an oversimplification. Better phrasing: “Gibat is commonly credited with the earliest published English-language word search; Spanish work may predate it.” The timeline above draws on the Wiley *Word Search Puzzles For Dummies* historical excerpt and *The Art of Puzzles*' brief history.

Sources: [Wiley historical excerpt (PDF)](https://catalogimages.wiley.com/images/db/pdf/9780470453667.excerpt.pdf) · [The Art of Puzzles — rules and history](https://www.gmpuzzles.com/blog/word-search-rules-and-info/) · [The Spokesman-Review newspaper puzzle history](https://www.spokesman.com/stories/2020/feb/05/history-newspaper-puzzles/).

## 3. How word search works

### 3.1 Standard mechanics

1. Choose a theme or a set of words.
2. Select a rectangular, normally square, letter grid.
3. Place each word on a straight line that fits the grid. Conventional directions are the eight compass-like combinations of row/column steps, excluding (0,0): N, NE, E, SE, S, SW, W, NW.
4. Allow intersections/overlap **only where letters match**.
5. Fill remaining spaces with plausible-looking letters. Filler should not introduce an accidental extra copy of a target if the design promises unique placement.
6. Present the target list, or a variant using themed clues or hidden-message instructions.
7. A player marks a continuous path between the first and last letter. Feedback distinguishes found, already found, and invalid.
8. The puzzle is solved when all required targets are found; optional completion feedback includes remaining time, hint use, scoring, or a word/message revealed by unused letters.

A classic human-solvable puzzle does **not** require every visually recognizable word in the filler to score. The declared target list and placed target paths determine completion.

### 3.2 Variants and vocabulary terms

| Format | Difference from baseline | GridMates status |
| --- | --- | --- |
| Straight-line search | Across, down, diagonal; direction options vary | Current core |
| Relaxed / beginner | Forward-only horizontal and vertical paths; easier word choices | Current v2/v3 and experimental v4 |
| Classic | Adds diagonals and selected backward paths | Current v2/v3 and experimental v4 |
| Expert | All eight directions; denser crossing, potentially less common words | Current v2/v3 and experimental v4 |
| Hidden-message / Wonderword | Unused letters reveal an extra answer | Research opportunity; not approved |
| Clue-first / category puzzle | Targets inferred from clues or theme rather than an explicit list | Optional future concept |
| Snaking / bent words | A word may turn through adjacent cells | A different mechanic; **not** part of current straight-line rule |
| Multiplayer shared grid | Partners claim the same board cooperatively or competitively | GridMates nearby concept; physical two-phone acceptance still open |
| Very large continuous board | Zoom/pan/region navigation for one uninterrupted grid | Experimental 40/50; 100×100 still speculative |

Standard eight-direction rules: [The Art of Puzzles](https://www.gmpuzzles.com/blog/word-search-rules-and-info/).

### 3.3 What affects difficulty

**Independent variables** include grid size, number of words, word length, target density, overlap and decoy-letter density, allowed orientations, fraction of reversed targets, familiarity of vocabulary, similarity between target spellings, readability, precision of touch selection, and hint policy. Larger board **does not automatically mean** harder vocabulary. A 50×50 Relaxed puzzle may be long but still use familiar words and forward-only directions.

Examples of non-equivalent difficulty:
- **Search effort:** 105 familiar targets on 50×50 vs. 12 targets on 12×12.
- **Direction recognition:** only east/south vs. all eight directions.
- **Linguistic difficulty:** recognizable common words vs. rare scientific terminology.
- **Interface burden:** a crowded mobile grid whose letters are too small is **poor accessibility**, not intentionally well-balanced puzzle difficulty.

**Recommendation:** Keep **size**, **challenge**, and **word familiarity** separately controlled and disclosed.

### 3.4 Generator safety and solvability

Each generation should:
- Apply deterministic, versioned randomness; never change a previously released generator's seed, word ordering, or placement algorithm under the same content version.
- Enforce a bounded retry budget and fail visibly in test builds if exhausted; no silent fallback to a smaller/shorter puzzle.
- Reject duplicate/substring-conflicting candidate words and incompatible content such as palindromes **when the exact-one-occurrence-in-all-eight-directions policy is used**. Under that specific policy, a palindrome such as **RADAR** always reads the same in both opposite directions along a single placed straight path; it cannot meet occurrence count exactly one.
- Verify each path spells its word, is contiguous/straight/in bounds, and belongs to allowed direction modes.
- Verify target words do not accidentally appear multiple times in other grid paths.
- Ensure every player-selectable target is discoverable and recognized forward or reverse as intended.
- Score **spatial distribution** using actual target cells, not filler; evaluate 10×10 coverage, target-center quadrants and direction balance; avoid over-optimizing overlaps.
- Quantify novelty between seeds: repeated word proportions, same target path, same full board, subtopic variety.
- Retain the generated answer, player-facing display label, direction, positions and fixed content identity for exact save reconstruction and regression tests.

**Internal evidence (Nov 8, 2026):** [WS-09F-F exact source quality report](WS09F_F_SPATIAL_VARIETY_QA_2026-11-08.md), [draft PR #85](https://github.com/PlayerEden/GridMates-Word-Search/pull/85): in 1,800 sampled experimental 40/50 boards, **953/1,800** old layouts passed the project's *proposed* coverage+direction thresholds, vs. **1,800/1,800** on a **separately namespaced balanced lab variant**. Neither result establishes all possible seeds, a production V4 approval, phone rendering performance, or human delight. Frozen historic grids remain untouched.

## 4. Audience and demographics — separate evidence from hypotheses

### 4.1 What published US gaming research actually shows (2026)

| Verified statistic | Source population / meaning | What it does **not** establish |
| --- | --- | --- |
| **212.3 million** US residents play video games **at least an hour per week** | ESA's 2026 industry survey | Not 212.3 million word-search players |
| **29%** of US video game players are **age 50+** | ESA 2026 player-age distribution | Not the age composition of word-search audiences |
| **37 years** average player age | ESA 2026 | Not the average age of GridMates or mobile puzzle players |
| **91%** mobile among player platform usage | ESA 2026; platforms overlap | Does not mean 91% play word searches |
| **66%** of adult players cite relaxation/passing time; **32%** cite “use my brain / keep my mind sharp” | ESA 2026 motivations, **adult video game players overall** | Not a clinical cognitive benefit or measured word-search motivation |
| **81%** of gaming parents have played video games with their children | ESA 2026 family gaming respondents | Not a measured same-Wi-Fi word-search co-op demand |
| **79%** smartphone ownership among US adults **65+**, **91%** among ages **50–64**, **97%** among ages **18–49** | Pew US adult survey, Jan 30–Jun 18, 2026 | Smartphone ownership does not imply word-search play |

Data and definitions: [ESA 2026 Essential Facts](https://www.theesa.com/resources/essential-facts-about-the-us-video-game-industry/2026-data/) · [ESA 2026 player data](https://data.theesa.com/players) · [ESA June 3, 2026 press release](https://www.theesa.com/two-thirds-of-americans-play-video-games-every-week-according-tonew-report-from-the-entertainment-software-association/) · [Pew Research Center 2026 mobile survey](https://www.pewresearch.org/internet/fact-sheet/mobile/).

### 4.2 What is **not** presently verified

We do **not** have a reliable, representative current estimate for the total number of US word-search players, their gender/age mix, weekly retention, household co-op participation, average session length, average game completions, revenue per player, or global download volume. A store listing's “downloads” counts installs or download bands for a particular app, **not** the number of active users in the genre. Commercial market-report forecasts often mix crosswords, word connect, anagrams, and other games, use proprietary methodologies, and should not be presented as verified word-search-specific demographics.

**Research task:** If stronger genre-specific sizing becomes useful for monetization, seek independent transparent surveys or first-party opt-in analytics and record exact definitions, date, sample, methodology, and limitations.

### 4.3 Audiences to *design-test*, not stereotypes to assume

1. **Paperback leisure solvers:** Want an instantly recognizable list/grid, readable letters, calm pace, and optional long sessions.
2. **Older or low-technology-comfort players:** May value obvious New/Continue choices, clear save behavior, generous tap targets, adjustable type, and no forced account. Age **alone** is not a proxy for skill, disability or taste.
3. **Short-session mobile players:** Want the first puzzle quickly, clear pause/resume and no intrusive interruption.
4. **Families/friends nearby:** Could enjoy co-op or friendly races when hosting and joining are simple; two-phone behavior still must be physically tested.
5. **Hardcore puzzle-book solvers:** May seek many directions, large boards, extra-long modes, vocabulary variety, and exact/consistent challenge.
6. **Teachers/learning contexts:** May use thematic word searches as supplementary recognition activities; usability and educational learning claims need evaluation in the actual intended context.

Research plan: conduct short supervised tasks with a mix of ages and abilities; use task completion, mis-taps, errors, comprehension and qualitative feedback rather than assuming a persona likes a design.

## 5. Accessibility and interface ergonomics

### 5.1 Android usability foundations

- **Touch targets:** Google's Android accessibility guidance recommends at least **48×48 dp** for interactive controls, with roughly **8 dp** separation where practical. This is a guideline for user controls; **40×40 individual letter cells cannot all be 48 dp when the entire board is fit into a phone screen**. That is precisely why zoom, panning, explicit precise selection and map/region navigation are important.
- **Typography:** Support enlarged system text and reflow in menus and word lists. Do not make the player fit a 105-word list into tiny type. Review portrait, landscape, narrow 320dp layout, 200% font-scale, TalkBack and keyboard/alternate input.
- **Panning vs. selection:** Clearly distinguish Move/Find and pinch gestures; ensure changing zoom does not accidentally claim a target. Include a reliable two-tap/precision alternative.
- **Information design:** Show remaining words, optional alphabetical jump, list location/ranges and a way to resume saved position; never force users through dozens of Next taps.
- **Comfort:** Do not confuse high difficulty with illegibility, flashing, arbitrary time pressure, screen clutter, or overly frequent ads.
- **Consent/privacy:** Offline play should work offline; Nearby co-op should require explicit session consent and not demand remote accounts.

Sources: [Android Accessibility — touch target sizes](https://support.google.com/accessibility/android/answer/7101858?hl=en) · [Android Compose scalable content](https://developer.android.com/develop/ui/compose/accessibility/scalable-content) · [Android offline-first architecture](https://developer.android.com/topic/architecture/data-layer/offline-first).

### 5.2 GridMates direct UX evidence (not a general demographic fact)

The [WS-07 owner playtest research](WS07_PLAYTEST_RESEARCH_AND_UX_SPEC_2026-11-07.md) documented crowded zoomed boards and an actual two-phone match where both devices connected but could not claim words, plus needs for clearer gesture, responsive layout and accessibility testing. Those observations have motivated code improvements and test suites, but they are not evidence that every player shares the same difficulty, and they do not constitute final physical QA signoff.

## 6. Mental engagement, relaxation and responsible claims

Word search typically involves **visual search, selective attention, sequence detection, letter-pattern recognition and self-paced persistence**. People may experience it as enjoyable, relaxing or mentally stimulating; those descriptions are appropriate as player experiences, not clinical outcomes.

**Do not advertise word search as proven to prevent dementia, reverse cognitive decline, treat ADHD or improve IQ.** The US National Institute on Aging specifically cautions that evidence for the long-term benefits of commercial brain-training apps is insufficient. There are studies involving **crosswords**, including a randomized study of computerized crossword training in older adults with mild cognitive impairment, but these results **cannot be casually generalized to word search**. If future marketing discusses cognitive benefit, require word-search-specific substantiation and appropriate legal/medical review.

Sources: [National Institute on Aging — Cognitive Health and Older Adults](https://www.nia.nih.gov/health/brain-health/cognitive-health-and-older-adults) · [NEJM Evidence — computerized crosswords vs. games in MCI](https://evidence.nejm.org/doi/full/10.1056/EVIDoa2200121) (**crosswords, not word search**).

## 7. Design principles for a satisfying puzzle

### A. Avoid clustering

- Every quadrant should have meaningful work to find; actual hidden word paths should reach outer and central regions.
- Measure **target-cell coverage** per map region and **target-center** distribution. Counting filler characters is misleading.
- Overlap is pleasing when occasional; overconcentration produces one dense patch and visually empty zones.
- Avoid an obviously artificial “one word per square” layout; random texture and natural crossings remain important.
- Record quantiles and worst seeds, not just averages. Reject truly poor outliers within bounded retries.

### B. Direction and orientation variety

- Relaxed: across/down can still be balanced (not 95% one direction).
- Classic: diagonals and some backward directions genuinely appear, not just theoretically allowed.
- Expert: all eight directions can occur, but difficulty should arise from searching rather than tiny text or hidden gestures.
- Measure counts per direction per generation and across consecutive seeds.

### C. Word and theme variety

- Cover different **subtopics within a theme**, not just hundreds of synonyms. E.g., Woodland: trees, animals, habitats, tracks/weather, natural features; Space: celestial objects, exploration, equipment, observation, phenomena; Garden: flowers, plants, tools, pests/pollinators, growing methods.
- Balance word lengths, recognizable compounds, common/rare terms, morphology and spellings.
- Use a human-approved *display label* (e.g., **Brown dwarf**) separate from its normalized grid answer (**BROWNDWARF**) where appropriate.
- Mark factual names and regional vocabulary explicitly. Avoid inappropriate/ambiguous words, repeats from cloned lists, bad normalization, and copied protected source datasets.
- Reject words that cannot be placed uniquely given our intended all-eight-direction search rule (e.g., palindrome **RADAR**, unless the rule itself is carefully revised in a **new** version).
- A larger theme pack does not justify calling every word “easy”; familiarity bands require editorial review and playtest feedback.

### D. Repeat-play freshness

For a fixed theme with **N** eligible words, any two 105-word puzzles must share **at least max(0, 210 − N)** words, regardless of RNG or spatial placement. If the eligible pool has 130 words, the minimum overlap is **80**; truly different-looking grids alone cannot solve that content limitation.

For two *independently uniformly sampled* 105-subsets, expected overlap is approximately **105² / N**. Actual selection may differ because tier quotas and deterministic seeds bias the sampling. This expectation is a model, not a measured game outcome.

**Preliminary editorial design goal, not a production promise:** raise to around **210–250 human-approved, thematically varied, appropriately tiered words per theme**, then examine repeat-play statistics over many consecutive same-theme puzzles. Future subtopic “chapters” could further spread recurrence but need new content versioning and a reason to exist.

## 8. Word-library inventory — source audit, November 8, 2026

Full detail and provenance: [WORD_LIBRARY_STATUS_2026-11-08.md](WORD_LIBRARY_STATUS_2026-11-08.md). **Counts describe entries in each separate source-version pack**, not total unique words available to one current puzzle.

| Version/source | Woodland | Space | Garden | Player-facing status |
| --- | ---: | ---: | ---: | --- |
| **v1** core original pack | 14 | 14 | 14 | Frozen original generator; used only for old compatible v1 puzzle IDs |
| **v2** core pack | 36 | 36 | 36 | Frozen separately; familiar tiers are developer judgments, not corpus frequencies |
| **v3** core pack (all levels) | 81 | 81 | 79 | Frozen separately for 25×25 / 30×30, with 66 / 67 / 65 in initial common+familiar bands |
| **v4 proposed Relaxed pool** | 126 | 130 | 135 | **Unapproved research content, TEST resources only**; Space count **includes ten additional unapproved candidates** |
| **v4 proposed advanced tier** | 59 | 53 | 29 | **Unapproved research suggestions**, never a certified difficulty list |
| **v4 editorial/compatibility holds** | 9 | 12 | 10 | Not in proposed generation; Space includes **RADAR** excluded as a palindrome |

The 40×40/50×50 research targets are **70/105 words per board**, respectively. Within a single-theme 50×50 *Relaxed* board, any two boards' lower bound on shared words is at least **84 Woodland / 80 Space / 75 Garden** with the currently proposed eligibility counts. The case for growing and improving the corpus is therefore **mathematical, not just aesthetic**.

Important: the v4 pool counts are *not* official shipped content, and the Advanced counts are separate trial bands; one must not sum all historical versions and describe the result as one giant active word library.

## 9. Market position and monetization research

The casual word-puzzle market is broad, but we should not invent a “word-search market size,” downloads, market share or revenue forecast from ESA industry totals. Competitor research should separately sample actual word-search listings for first-puzzle friction, ads/interstitial timing, offline support, boards, word-list navigation, font/readability, save behavior, price, complaints and update recency. Record a dated screenshot/URL and do not assume listing claims are reliable without manual play.

**GridMates differentiation hypothesis:** familiar paperback atmosphere, offline-first reliability, clean word libraries with balanced distributed words, clear easy-to-use controls, no forced login, optional same-Wi-Fi company, and tasteful/predictable optional monetization. Hypothesis only until comparative study/player feedback.

**Monetization principles for evaluation:** no forced ad after every small accomplishment, never interrupt a live board/selection or lose progress, keep readable fonts and basic accessibility free, and if ads ship allow a clear remove-ads path. Exact ad cadence, pricing and purchase terms require later approval and user acceptance. No fabricated LTV/ARPDAU values.

**Opportunity areas worth evaluating**: occasional optional daily puzzles, curated collections and themed chapters, accessible large-print experience, and optional sharing with family. They are design hypotheses, not proven growth drivers.

## 10. What to measure in playtesting

| Dimension | Example metric | Status/limitation |
| --- | --- | --- |
| Onboarding | First valid word found without explanation; time to first correct claim; confusion incidents | Owner / controlled playtest needed |
| Readability | Mis-selections and visual strain at 320dp, landscape, 200%-font | Physical devices needed |
| Generator quality | Empty 10×10 sections, word-center distribution, direction entropy/share, duplicate paths | Automated V4 lab evidence exists |
| Variety | Shared target percentage over successive seeds; same word same path; subtopic and word-length diversity | Preliminary V4 research only |
| Comfort | Freeform reports of pacing, calmness, fatigue, overly dense 105-word list | Human feedback needed |
| Offline reliability | Resume, paused puzzles, Daily history, app update backup integrity | Versioned regression QA plus actual tester device still needed |
| Nearby play | Real two-phone find acknowledgement, partner presence, reconnect and conflict resolution | P0 physical acceptance not complete |
| Optional business metrics | Opt-in sessions, starts/completions, day-N return, payment/ad response | **No current production analytics**; obtain consent if measured |

**Never** turn the above into asserted market statistics before collecting data.

## 11. Research integrity, sources and update policy

**Source hierarchy**: (1) first-party official demographic/accessibility publications with explicit method/date; (2) credible history/genre sources describing disputed provenance; (3) independently verified repository source and GitHub Actions receipts; (4) player interviews/observations with provenance; (5) clearly labeled design hypotheses. “All word searches have X” or “most players are Y” requires a source that actually studied that population.

### External reference register (accessed November 8, 2026)

1. *Wiley historical word-search excerpt* — multiple independent origin accounts, Pedro Ocón de Oro, Gibat (1968), Wonderword: https://catalogimages.wiley.com/images/db/pdf/9780470453667.excerpt.pdf
2. *The Art of Puzzles — Word Search Rules and Info* — eight-direction rules, history caveat: https://www.gmpuzzles.com/blog/word-search-rules-and-info/
3. *The Spokesman-Review* (2020) — Gibat / March 1, 1968 historical account: https://www.spokesman.com/stories/2020/feb/05/history-newspaper-puzzles/
4. *Entertainment Software Association, 2026 Essential Facts* — national game participation, demographics, motivations, families: https://www.theesa.com/resources/essential-facts-about-the-us-video-game-industry/2026-data/
5. *ESA 2026 players data portal* — age and mobile platform stats: https://data.theesa.com/players
6. *ESA news release, June 3, 2026* — study framing, ages 5–90, one hour a week: https://www.theesa.com/two-thirds-of-americans-play-video-games-every-week-according-tonew-report-from-the-entertainment-software-association/
7. *Pew Research Center, Americans' Use of Mobile Devices in 2026* — survey dates and age-stratified smartphone ownership: https://www.pewresearch.org/internet/fact-sheet/mobile/
8. *Android Accessibility Help — Touch target size* — 48dp target guidance: https://support.google.com/accessibility/android/answer/7101858?hl=en
9. *Android Developers — Support user-scalable content* — pinch zoom/reflow and low-vision considerations: https://developer.android.com/develop/ui/compose/accessibility/scalable-content
10. *Android Developers — Build an offline-first app* — offline data access and local authority: https://developer.android.com/topic/architecture/data-layer/offline-first
11. *US National Institute on Aging — Cognitive Health and Older Adults* — caution on commercial brain-training effectiveness: https://www.nia.nih.gov/health/brain-health/cognitive-health-and-older-adults
12. *NEJM Evidence — Computerized Games vs. Crosswords in Mild Cognitive Impairment* — **crosswords, not word searches**, not transferable proof: https://evidence.nejm.org/doi/full/10.1056/EVIDoa2200121

### Internal source register

- [Main project master design](MASTER_GAME_DESIGN.md): actual design decisions; **not** a published external research source.
- [Versioned word library inventory](WORD_LIBRARY_STATUS_2026-11-08.md): literal counts sourced from V1/V2/V3 Kotlin packs and the draft v4 review manifest.
- [WS-07 human input and UX specification](WS07_PLAYTEST_RESEARCH_AND_UX_SPEC_2026-11-07.md): actual phone evidence, separate from automated tests.
- [WS-09F-F spatial audit and QA report](https://github.com/PlayerEden/GridMates-Word-Search/blob/feature/ws09f-f-spatial-and-variation-audit-2026-11-08/docs/WS09F_F_SPATIAL_VARIETY_QA_2026-11-08.md) and [source-matched 1,800-case Actions run](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42514815037).
- [WS-09F-E draft tier research, PR #84](https://github.com/PlayerEden/GridMates-Word-Search/pull/84) and [1,800 tiered sample](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42512914406).

## 12. Immediate research-to-development decisions

**Decided for current work:** Preserve frozen generator versions and saves; evaluate actual target coverage and directions; use family-friendly wording and readable interfaces; keep 40×40/50×50 content in research until a separately reviewed immutable V4 corpus and stable IDs are approved.

**Pending editorial decisions:** Review proposed everyday/advanced vocabulary, choose final player-facing display labels and subthemes, expand toward the 210–250-word-per-theme freshness goal, determine whether current eight-way palindrome prohibition remains the formal rule, and decide how to distribute words over several consecutive puzzles.

**Pending evidence:** Broad (not just selected) seed stress of any future finalized generator, actual APK frame/memory test, hardware readability, 105-word list accessibility, physical two-phone completion, and careful old-version migration.

**Review rule:** Update this master document whenever a new credible external study materially changes the audience assumptions, and update the companion **library status** after each approved content-version freeze. Never rewrite the numbers to claim a future lab change is already installed.
