# GridMates: Word Search — Word Library Inventory, Quality & Expansion Plan

**Audited:** November 8, 2026  
**Status:** Source-derived inventory + **unapproved** V4 editorial proposals; **not** a new word pack, generator migration, APK or gameplay release.  
**Source snapshot used:** [PR #85, head 77fdd2f9c6eab612960984cb211e7e8058e1384f](https://github.com/PlayerEden/GridMates-Word-Search/pull/85), which stacks on V4 tier proposal [PR #84](https://github.com/PlayerEden/GridMates-Word-Search/pull/84).  
**Parent references:** [Genre research](WORD_SEARCH_GENRE_MASTER_RESEARCH_2026-11-08.md) · [Master Game Design](MASTER_GAME_DESIGN.md) · [WS-09F-E tier QA](https://github.com/PlayerEden/GridMates-Word-Search/blob/feature/ws09f-e-v4-tiered-corpus-preview-2026-11-08/docs/WS09F_E_V4_TIERED_PREVIEW_QA_2026-11-08.md) · [WS-09F-F spread QA](https://github.com/PlayerEden/GridMates-Word-Search/blob/feature/ws09f-f-spatial-and-variation-audit-2026-11-08/docs/WS09F_F_SPATIAL_VARIETY_QA_2026-11-08.md)

> **Interpretation rule:** “How many words do we have?” has two different answers: (a) the number frozen in each older **versioned puzzle pack**, and (b) the tentative words eligible for a **future** V4 word-selection tier. **Do not sum versions and call them one active dictionary.** A previously issued puzzle must retain its original word pool, RNG, placements and ID.

## 1. Actual source-level historical inventory

The following counts are literal string entries parsed by theme from the V1, V2 and V3 Kotlin source files at the source snapshot above. They are **pack entry counts**, not counts of words currently selectable in one session. V2 and V3 also filter by chosen difficulty/size.

| Frozen source-version library | Woodland | Space | Garden | Count and meaning |
| --- | ---: | ---: | ---: | --- |
| V1: original \`WordTheme.words\` | **14** | **14** | **14** | Old compatible 8×8 / 10×10 / 12×12 puzzle vocabulary |
| V2: \`WordLibraryV2\` | **36** | **36** | **36** | Hand-edited pack split into common/familiar/unusual editorial bands; 8–20-cell size presets, with exclusions by max word length |
| V2 common / familiar / unusual | 17 / 14 / 5 | 12 / 14 / 10 | 17 / 14 / 5 | Developer-assigned familiarity, **not** measured word frequency |
| V3: \`WordLibraryV3\` | **81** | **81** | **79** | Frozen expanded pack for 25×25 Grand and 30×30 Giant, 32/44 target words |
| V3 everyday / familiar / advanced | 28 / 38 / 15 | 30 / 37 / 14 | 29 / 36 / 14 | V3 labels describe editorial intent, not verified population frequency |
| V3 “common+familiar” subtotal | **66** | **67** | **65** | Potential eligible pool before board-specific selection |
| Diagnostic *unique union* of V1+V2+V3 for a theme | **101** | **91** | **99** | Source overlap removed within theme. **Not** one selectable pool or permission to rewrite frozen packs |

Relevant source:
- [V1 — WordSearch.kt](https://github.com/PlayerEden/GridMates-Word-Search/blob/feature/ws09f-f-spatial-and-variation-audit-2026-11-08/core/src/main/kotlin/com/playereden/gridmates/wordsearch/core/WordSearch.kt)
- [V2 — WordSearchV2.kt](https://github.com/PlayerEden/GridMates-Word-Search/blob/feature/ws09f-f-spatial-and-variation-audit-2026-11-08/core/src/main/kotlin/com/playereden/gridmates/wordsearch/core/WordSearchV2.kt)
- [V3 — WordSearchV3.kt](https://github.com/PlayerEden/GridMates-Word-Search/blob/feature/ws09f-f-spatial-and-variation-audit-2026-11-08/core/src/main/kotlin/com/playereden/gridmates/wordsearch/core/WordSearchV3.kt)

**Important preservation rule:** V1/V2/V3 legacy word pools, generator randomness, puzzle ID syntax and \`PuzzleBoardSize\` enum ordinals are frozen due to saved runs and daily/history. If we improve spelling, labels or curation for **future** boards, it must be behind a **new immutable content version**, not by silently editing historical source.

## 2. V4 research candidates — not published content

V4 40×40/50×50 research plans **70 / 105 target words per puzzle** and an independent generator. The proposed candidates have passed technical source and generation audits but **have not passed a complete human vocabulary/editorial review**. A label proposal does not make a word approved.

| V4 proposal, theme-specific | Woodland | Space | Garden |
| --- | ---: | ---: | ---: |
| Tentative Relaxed-eligible words | **126** | **130** | **135** |
| Tentative advanced words | **59** | **53** | **29** |
| Held for editing, incompatibility or review | **9** | **12** | **10** |
| Unreviewed additions inside Relaxed count | 0 | **10** | 0 |
| Relaxed words if all unreviewed additions excluded | **126** | **120** | **135** |
| Spare words beyond 105-word puzzle, with tentative additions | 21 | 25 | 30 |
| Spare words beyond 105 without unreviewed additions | 21 | **15** | 30 |

Source files in the draft branch:
- [Exact proposed word inventory and display-label ideas](https://github.com/PlayerEden/GridMates-Word-Search/blob/feature/ws09f-f-spatial-and-variation-audit-2026-11-08/design/marathon/v4_candidate_tiers_workbench_2026-11-08.json)
- [Original unapproved editorial triage](https://github.com/PlayerEden/GridMates-Word-Search/blob/feature/ws09f-f-spatial-and-variation-audit-2026-11-08/design/marathon/v4_editorial_triage_proposal.json)
- [Non-shipping V4 candidate fixture](https://github.com/PlayerEden/GridMates-Word-Search/blob/feature/ws09f-f-spatial-and-variation-audit-2026-11-08/core/src/test/resources/ws09f_e_v4_provisional_tiers.txt)
- [Exact-source 1,800/1,800 V4 tiered technical run](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42512914406)
- [Exact-source 1,800/1,800 spatially balanced technical run](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42514815037)

### Known editorial / generation issues

- **Inappropriate complexity:** “Familiar” is currently a subjective editorial band. Proper science terms, technical nouns, long compounds and uncommon regional names may not belong in Relaxed for an ordinary player. No representative familiarity survey has been performed.
- **Space shortfall:** Ten additional Space words are **NOT REVIEWED**; the original tentative Relaxed pool after compatibility holds has only **120** candidates. That's enough to sample 105 but only **15 spare**, producing substantial repetition. These ten words must not be treated as accepted merely because a generator can place them.
- **Palindrome incompatibility:** **RADAR** was quarantined from the new Space V4 tier. It is intrinsically incompatible with the current “each target appears exactly once in all eight directed scans” requirement: a placement also yields the reverse occurrence. This *does not* mean the English word itself is incorrect; it is a **generator-specific prohibition**, and a different rule would need a separately versioned design.
- **Substring and ambiguity:** Check \`RIVER\` vs. \`RIVERBANK\`-type containing pairs, duplicate normalized answers, confusing inflections, and terms whose filler creates unintended extra occurrences.
- **Readability:** Answers are stored as uppercase A–Z runs (\`BROWNDWARF\`, \`MILKYWAY\`), but user-facing words should often display as natural spaced labels (**Brown dwarf**, **Milky Way**). Current ideas are proposals only; do not imply all labels are implemented.
- **Theme coherence:** Each theme needs distinct subsections, not simply any term loosely connected to a category. Examples: trees, woodland mammals and birds, natural features; solar system, astronomy phenomena, exploration/engineering; plants, pollinators, growing conditions and tools.
- **No automatic AI dictionary approval:** Generated candidate lists can contain invented compounds, uncommon spelling, trademarks, adult terms, offensiveness, incorrect taxonomy and scientific concepts that are inappropriate for the chosen challenge. Human editors must review each proposed answer/label before a production freeze.

### Technical difficulty quotas in research (not an approved release contract)

| Size | Targets | Relaxed advanced | Classic advanced | Expert advanced |
| --- | ---: | ---: | ---: | ---: |
| 40×40 | 70 | 0 | 7 | 17 |
| 50×50 | 105 | 0 | 10 | 26 |

The values are merely experimental constraints in \`V4PreviewTierSelection.kt\`. They are not validated “fun” or “fair” quotas and may change before a final V4 content version.

## 3. Why repetition is our biggest content weakness

For two 105-word boards drawn from a pool of N eligible terms, the **minimum** overlap is max(0, 210 − N). Thus, even with perfect design and perfect deliberate separation, all pairs of 50×50 Relaxed puzzles must share at least:

| Draft Relaxed theme | N eligible | Minimum repeated words in any two 105-word boards | Minimum repeat fraction |
| --- | ---: | ---: | ---: |
| Woodland | 126 | **84** | **80.0%** |
| Space | 130 | **80** | **76.2%** |
| Garden | 135 | **75** | **71.4%** |

These are **mathematical lower bounds**, not statistical averages. Typical independent uniform samples overlap even more than the lower bound; our generation modes and deterministic tier selection are not perfectly uniform, so actual measured adjacent-board overlap needs its own QA.

An editorial target of **210–250 genuinely familiar words per theme** is a useful *research hypothesis* for repeat-play diversity (at 210, two uniformly chosen 105-word lists overlap by ~52.5 words on average; at 250, ~44.1). It is not an accepted target until we know we can actually source that many good, understandable, nonconflicting entries.

### Practical expansion strategy

1. **Freeze existing versions; create a new V4 source-controlled content manifest** with exact immutable corpus ID, content hash, word answer, display label, theme, subtopic and familiarity rating.
2. Editorially review all current candidate words, including the ten Space proposals. Remove inappropriate/obscure Relaxed items and unresolved holds; investigate spelling and proper nouns.
3. Add genuine familiar words in balanced theme subtopics rather than chasing a gross count. Check conflicts as each new word is proposed.
4. Apply explicit **frequency limits or recency-aware content selection** only if they can remain deterministic/reconstructable offline and do not accidentally break saved seed replay. Separate content version and persisted puzzle seed/corpus identity are mandatory.
5. Compare many consecutive same-theme generated puzzles for word-set Jaccard overlap, repeat within last N games, identical word-path placements, direction shares, center/region target coverage and length-band variety.
6. Run *human* discovery tests: “Which words felt obscure or out of place?”, “Did this look like the same board?”, “Could you find the next word comfortably?” Collect both novice and experienced puzzle-book solvers' judgments.
7. Lock the final manifest and literal golden puzzles **before** any V4 save or Google Play release. After publication, only **V5** (or another new version) may change the library.

## 4. Proposed editorial metadata contract

For a **future immutable content version**, each new term should be reviewed with fields like:

| Field | Example | Check |
| --- | --- | --- |
| \`answer\` | \`BROWNDWARF\` | Uppercase A–Z, distinct, no prohibited palindrome |
| \`display\` | \`Brown dwarf\` | Accurate spacing, punctuation, case; normalized spelling exactly matches answer |
| \`theme\` | \`SPACE\` | Single canonical theme |
| \`subtopic\` | \`stellar objects\` | Controlled editorial taxonomy |
| \`tier\` | \`advanced\` | Human reviewed with evidence; not simply long word = rare |
| \`editor_status\` | \`draft / reviewed / approved / hold\` | Only approved words enter final shipped corpus |
| \`editor_note\` | \`recognized astronomy term\` | Justification for ambiguity, audience and factual checks |
| \`content_version\` | \`v4-final-1\` | Immutable; never mutate published payload |
| \`normalized_key\` | \`BROWNDWARF\` | Distinct across display variations; cross-word substring checks |
| \`source_provenance\` | \`human-reviewed original list\` | Avoid unclear scraped/copyrighted data |

The table is a **proposed schema**, not actual implemented fields. Existing Kotlin \`WordTarget\` currently uses the same string for its ID and displayed word in V3; adding a distinct display field is a future versioned operation.

### Acceptance checklist for a future real shipping pack

- [ ] All 3 theme packs genuinely reviewed; written explicit approval.
- [ ] Every entry has a legitimate, clearly understandable answer/display pair.
- [ ] No duplicate normalized words, substring collisions or palindrome incompatibilities; word lengths fit.
- [ ] Expert tier is meaningfully more specialized without bizarre/arbitrary vocab.
- [ ] Sufficient topic/length variety; test actual consecutive puzzles, not just theoretical pack size.
- [ ] All target cells distribute across board and all allowed directions appear naturally; no misleading false occurrences.
- [ ] Filler/overlap rate produces legible, enjoyable puzzles, verified on Android.
- [ ] Immutable versioned IDs and seed/corpus replay; old frozen generators and saves unchanged.
- [ ] Human mobile playtesting, large-font/landscape, and actual signer-preserving save update QA passed.
- [ ] No unsupported word-search-specific demographic or brain-health claims in the store description.

## 5. Current source of truth and status

**Playable QA APK:** [v0.9.4/code48, source-matched 114/114 Android instrumentation tests](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42501935166) remains the earlier established package; **it does not contain public 40×40/50×50 V4 gameplay**. The A–Z 105-word list and spatially balanced V4 algorithm exist in separate draft research/testing branches; their successfully compiled tests are **not** a new installable build.

**Unmerged work:** [PR #82 — A–Z large word list](https://github.com/PlayerEden/GridMates-Word-Search/pull/82) · [PR #83 — internal V4 preview engine](https://github.com/PlayerEden/GridMates-Word-Search/pull/83) · [PR #84 — tier selection and palindrome hold](https://github.com/PlayerEden/GridMates-Word-Search/pull/84) · [PR #85 — balanced layout](https://github.com/PlayerEden/GridMates-Word-Search/pull/85).

**Owner-action blockers:** Review proposed V4 vocabulary and labels; approve future corpus; inspect real phone accessibility and actual installed version/signing; physical two-phone Co-op issue #53.

**Do not overwrite historical word lists in source to “fix” currently saved boards.**
