# WS-10 — The 10,000+ Word GridMates Lexicon

**Owner design direction — November 8, 2026:** Build a genuinely large, evergreen **10,000+ UNIQUE, APPROVED, PLAYABLE English answers** library, not merely the 126–135 provisional relaxed candidates per V4 theme. This is a content initiative independent of experimental giant-board generator PRs. **Actual approved count starts at zero in this new catalog foundation, not 10,000.** Existing v1/v2/v3 word packs and saves must not be changed.

## The desired experience

A player should be able to solve many puzzles in the same theme without feeling that they are hunting the same 105 words again and again. Players can also explore dozens of sensible subjects, short casual puzzles, themed collections, and bigger 40×40/50×50 boards. **Thousands of words are a means to better puzzles, not a reason to show absurd vocabulary to Relaxed players.**

**Content targets (planning, not existing assets):**
- Candidate intake vault: 20,000–40,000 possible entries from legally reviewed sources and original curation, including holds and rejected items. Candidate count is NOT the shipping count.
- **10,000+ unique, editorially approved, normalized English answers** with at least one approved topic assignment; no duplicate counting for words appearing under several categories.
- **30+ broad themes/topic families** with subtopics for editorial coherence. Prefer about 250–700 approved answers per substantial theme, with exceptions for narrow concepts. This is a portfolio goal, not a forced quota that encourages nonsense.
- For giant 50×50 boards with 105 targets, prioritize 300–500+ eligible familiar words in high-traffic themes rather than barely 125; test real repeated boards over seeds and sessions.
- Release increments based on quality evidence: 1,000 approved -> 3,000 -> 5,000 -> 10,000, with accurate dashboard counts for *approved and selectable* entries. **Never claim a milestone from raw candidate/import counts.**

### Candidate topic families

Examples, not forced releases: Woodland & Forests, Animals & Pets, Birds, Ocean Life, Plants & Flowers, Garden, Space & Astronomy, Weather, Geography, Travel & Landmarks, Food & Cooking, Fruits & Vegetables, Home & Family, Tools & Hobbies, Vehicles & Transportation, Sports, Music, Art & Design, Books & Literature, History, Science, Technology, Nature & Outdoors, Holidays, Seasons, Clothing, Jobs & Careers, School & Learning, Mythology & Folklore, Architecture, and General Everyday Life. Use category labels and subcategories to protect thematic coherence and avoid misleading terms.

## Source-control architecture (vocabulary independent of old Kotlin enums)

One **canonical global word** has an immutable `word_id`, `answer`, user-facing `display`, locale, provenance and a review state. Topic assignments live in a **separate many-to-many table** (`word_id`, topic, subtopic, familiarity tier, editorial review). Thus `RIVER` can legitimately belong to Woodland, Geography and Outdoors **without counting as three unique words**. Maintain a controlled list of topic/subtopic IDs.

CSV for easy human review:
- `data/lexicon/words_candidate.csv`: one canonical word per row, with display and exact provenance.
- `data/lexicon/word_topics_candidate.csv`: many-to-many topic/subtopic/tier assignments.
- `data/lexicon/topics.json`: allowed topic families and subtopics.
- `data/lexicon/sources.json`: source ledger, exact usage/notice obligations and rights-review state.
- `tools/lexicon/validate.py`: strict Unicode/ID/duplicate/reference/provenance validation with honest draft vs. approved counts.
- `tools/lexicon/test_validate.py`: fail-closed test cases.
- CI checks the above. **None of these files feed the Android app yet.**

The schema is a first foundation. When word labels, multi-language translations or content approval practices evolve, version the catalog format and source-tracked migrations. **Do not mutate a published seed-to-word-list mapping.** The first production V4 content snapshot needs a frozen corpus hash, approved manifest, topic registry version, puzzle ID namespace and known seed/golden examples.

## Editorial rules — a large library is not a raw dictionary

1. Every answer must be real and meaningful; confirm spelling, accepted American English usage and proper-noun policy. Reject pseudo-words, invented combinations, nonsense, distressing/offensive surprises in ordinary themes, trademark abuse and ambiguous duplicate forms.
2. **Familiarity is separate from spelling validity.** A dictionary entry can be valid but too rare for Relaxed. Words are judged in their topic context; reviewers sign off on tier assignments. Establish extra-long-word and morphologic variation caps within each puzzle, not just an enormous globally uniform filter.
3. Keep exact readable `display` separate from uppercase grid `answer` (e.g. `BROWNDWARF` / `Brown dwarf`). Normalization must be deterministic and locale-aware; start with verified `en-US`, expand languages only with separate approved normalization rules.
4. Subtopics ensure each puzzle feels like a coherent category. Use variety selection across subtopics and word lengths, not 105 near-synonyms or inflections.
5. Global **substring pairs are permitted in the library**: `RIVER` and `RIVERBANK` are both useful entries. A **particular selected puzzle** must apply its published target-conflict rules before generation. Rejecting all substring-related words from a 10,000-word global lexicon would throw away huge amounts of usable content.
6. Likewise, `RADAR` is a real word, but the **existing V4 experimental all-eight-directed-exact-one-occurrence test** cannot place palindromes. Mark ineligible for that generator mode, *not permanently forbidden from the entire language*. Future **separately versioned** canonical-path occurrence rules could support them if approved and source-matched.
7. No indiscriminate automated “bad substring” blocklist: innocuous words may contain blocked character sequences. Use manual context checks.
8. Record provenance and exact external-data licenses before bulk import or redistribution. **Permission to inspect or download a dictionary does not grant permission to ship it under any license.** Review source notices, derivative obligations and commercial use before product inclusion.

## Third-party data research (NO SOURCE DATA IMPORTED)

- **SCOWL / English Speller Database**: a substantial English spelling source, with a permissive collective license and required copyright/third-party notices. The exact release, size, dialect, code/data copyrights and bundled license files must be pinned and reviewed. Useful as a *candidate spellcheck source*, not a substitute for thematic or age-appropriateness curation. https://github.com/en-wl/wordlist ; https://wordlist.aspell.net/
- **ENABLE (Enhanced North American Benchmark LExicon)**: public-domain-style word list widely used in word games; verify exact distribution provenance, public-domain terms and any accompanying notices before ingest. Word-game legality ≠ everyday familiarity.
- **wordfreq** by Robyn Speer: useful for estimating real-world frequency / ranking review queues, **not proof of thematic fit**. Project code is Apache-2.0, while its included data may impose **CC BY-SA 4.0 attribution/share-alike** and upstream notice requirements. Do not copy/package the frequency data without the explicit content-license decision. https://github.com/rspeer/wordfreq ; https://pypi.org/project/wordfreq/

All sources above are research candidates, **not included data dependencies and not cleared product assets** as of this document.

## Phased implementation with evidence

**WS-10A — Lexicon infrastructure (current):** versioned canonical word IDs, topic mapping, source ledger, CSV validation, review statuses, GitHub CI and source-linked research. Real dataset is still **small and draft**, but pipeline must handle thousands of rows.

**WS-10B — Licensing and bulk intake:** freeze input-source release URLs + checksums + licenses; import into **quarantined candidates**, record provenance per record, reject obvious normalization/duplicate errors; do not promote to approved automatically. Establish a rights/notice check gate.

**WS-10C — Editorial batches:** curate by themed subtopics, with human-reviewed display labels, American English usage, familiarity, sensitivity and playtest feedback. Produce true *approved unique* and *approved topic assignments* dashboards. Aim for multiple reviewers where practical.

**WS-10D — Offline content engine:** add a new separately versioned topic registry and lexicon loader in Android without changing existing `WordTheme` enum order, old generators, Daily, Solo IDs or schema5 history. Store content in compact offline assets; benchmark startup and memory. If released, freeze word/weight/topic manifests and selected-puzzle reconstruction by content ID/seed and exact fixtures.

**WS-10E — Freshness + spatial QA:** preserve quality-scored V4 placement research; test subtopic mix, word-length bands, directions, regional coverage, word-set overlap across 100+ consecutive boards/theme, and deterministic restore. Allow optional diversity/recency scheduling, but never make an old puzzle restore to a different selection after updates.

**WS-10F — Human + signed Android QA:** family readability/play experience, older-audience usability, word-list navigation, local two-phone behavior, offline modes, upgrade/signing/source-parity and restorable backups; only then consider public release.

## QA definition of “10,000 words”

Report separate counts for:
- `candidate_unique` — valid spelling and metadata only.
- `reviewed_unique` — editorially reviewed (not necessarily approved).
- `approved_unique` — word approved **AND at least one approved theme assignment**; source rights accepted.
- `approved_topic_assignments` — useful coverage metric, **never** substituted for unique word count.
- `eligible_by_theme_tier` — selectable in an actual versioned generator with word-length/difficulty rules.
- `rejected_or_held` — tracked separately, not counted as available.

**Owner-facing promise:** We can absolutely build a library with thousands of words; it will take *deliberate intake and review*, not a one-shot random word dump. The currently verified v0.9.4 APK remains untouched. This is a planning/data infrastructure foundation, not a 10K-word content import.