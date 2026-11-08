# WS-10B2 — First Three-Theme Word Review Queue: 532 Terms

**Nov 8, 2026 — Verified GitHub Actions success; zero user-facing words approved or changed.**

This independently versioned lexicon effort builds on [WS-10A PR #86](https://github.com/PlayerEden/GridMates-Word-Search/pull/86) and [WS-10B1 PR #88](https://github.com/PlayerEden/GridMates-Word-Search/pull/88), without merging the experimental V4 gameplay stack or changing any existing Solo/Daily/save reconstruction.

## Exact verified evidence

- [GitHub QA run #42519094538](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42519094538) **SUCCESS** on source SHA `449d27b2ace3a7eaf56b867641d6226fef316a3d`; **19/19 Python tests**, including WS-10A canonical lexicon, WS-10B1 exact-source ZIP/license checks and five new editorial queue safety tests. Source checksum of official US 2026.02.25 ESDB/SCOWLv2 archive again verified.
- Reproduced **36,213 unique unreviewed U.S. English spelling-root candidates** from the pinned official source.
- Replayed **563 existing V4 research proposals** from frozen [V4 research source blob `e5afe73c962a5d69eed09aa4e7a4842c119fd89a`](../data/lexicon/intake/v4_three_theme_research_seed_suggestions.csv). This input is purely *candidate research*, not a reviewed or published lexicon.
- Generated **three human-editor review sheets totaling 532 proposal rows**: **185 Woodland, 183 Space, 164 Garden**; and a separate **31-row historical editorial/compatibility hold sheet** (9 Woodland, 12 Space, 10 Garden).
- Flagged **176 proposals without a direct source spelling-root match**: 42 Woodland, 81 Space, 53 Garden. A missing source match is a **review flag**, not evidence of invalidity; open compound nouns and technical terms may not be one Hunspell lemma.
- **All fields requiring editor decisions, approved labels, approved topics, approved subtopics, approved familiarity, reviewer ID and date are blank**. Provenance and original difficulty proposals are preserved. **0 approved unique words and 0 approved topic assignments.**
- [Download complete crosswalk, all theme review sheets, 31 holds, report, 36,213-source vault and publisher Copyright — artifact #13632282442](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42519094538/artifacts/13632282442); expires **Nov 22, 2026**, and is fully reproducible from exact pinned inputs.

## What happens next

1. A human editor works through actual **100–250-word editorial batches** from these sheets, checking spelling/phrases, thematic accuracy, US usage, age appropriateness and familiarity for Relaxed versus Expert. Never treat all 532 as approved.
2. Separate actual source words from legacy V4 experimental terms requiring independent recognition; if a familiar compound is absent from Hunspell roots, manually validate rather than silently discarding it.
3. Record decisions and provenance in the central canonical word and assignment manifest. Word and theme assignment require signed review independently, and license clearance. Any rejected/held term must remain traceable rather than deleted.
4. Extend review queue generation across the remaining 30 proposed topic families using **controlled seed subtopic vocabulary**, not naive word-length heuristics or raw substring categorization.
5. Only after word approvals and immutable content manifest/versioning may Android use a new lexicon. Prior V1/V2/V3 generators, old seeds and v0.9.4 QA APK remain untouched.

**This QA proves a real review workflow and initial candidate volume, not that 10,000 playable words have been curated.**