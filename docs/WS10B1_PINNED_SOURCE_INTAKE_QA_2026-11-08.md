# WS-10B1 — Verified 36,213-word Source Quarantine (No Game Content)

**November 8, 2026 — CI-green, source-pinned English candidate intake.**

**Source:** Kevin Atkinson's **English Speller Database / SCOWLv2** official **en-US Hunspell dictionary** release **rel-2026.02.25**. The downloaded archive was validated against the official release asset's `SHA-256 ac8e73310e951d88c52c2cf2ba54ceaca34f8486a81630ac8a75dc5f931179f9` and exactly **187,271 bytes**. The [pinned full official Copyright notice](../data/lexicon/licenses/ESDB_SCOWL_COPYRIGHT_2026_02_25.txt) is included in Git source and **copied inside the generated archive**. The source manifest is [here](../data/lexicon/intake/esdb_us_2026_02_25.json). External publisher notices remain subject to final shipping review.

## Actual GitHub Actions evidence

[Run #42518555704](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42518555704) **SUCCESS** at exact code+manifest SHA `2d0a3f0bdab4bbf196353fceb663b6e70e456e03`:
- **36,213 UNIQUE uppercase ASCII spelling-root candidate answers** (length 3–20) extracted from the signed-format official US dictionary file. These are **not app words and not fully affix-expanded forms**.
- Upstream Hunspell dictionary declared 47,551 entries; extraction excludes mixed-case/proper names, short/long, non-ASCII and punctuation-bearing entries; the report records per-reason exclusion counts.
- **38** accepted roots match the existing **40 illustrative catalog candidate answers**; do not count them as new, independently approved words.
- **56** valid language-word candidates flagged as palindromes for V4 placement compatibility; not deleted from the global dictionary.
- **0 newly approved playable words**, **0 themed assignments**, **0 Android app content changes**.
- **14/14 Python tests passed** (eight prior WS-10A validation tests plus six WS-10B1 source-hash, ZIP-path, copyright and fail-closed import tests).
- [Download 36,213-candidate quarantined CSV, JSON receipt and exact copyright notice — GitHub artifact #13632471594](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42518555704/artifacts/13632471594). Artifact has **14-day retention, expiring November 22, 2026**. The immutable source+hash lets us reproduce it after expiration; it does **not** create a stable public approved lexicon.

## Scope and safety

All candidates are `review_status=candidate`, `topic_status=unclassified`, `human_review_required=true` and carry source/line/release provenance. The importer writes **only to a separate temporary quarantine output directory**, never to the 40-word illustrative catalog, Kotlin production source, game assets, saves, Play signing or family devices.

The official ESDB license permits use and redistribution under specific copyright/supporting documentation notice conditions; these notices are preserved in full. Avoid mixing this dataset with `wordfreq` data because its code/data license and downstream notice obligations are different. **Legal source permission and spelling validity do not imply suitability for a young or older family audience, nor for any particular theme**.

## Immediate next work (WS-10B2 / WS-10C1)

1. Produce a source-backed **review queue** by *topic/subtopic* for the already supported Woodland, Space and Garden themes. Use human-reviewed topical seed references and conservative suggestions, never automatically treat a word as approved or familiar just from spelling.
2. Deduplicate new imports against the canonical 40-word catalog, keeping stable IDs and all original provenance. Keep *RIVER* and *RIVERBANK* globally; enforce substring conflicts only inside a selected puzzle.
3. Build an editor-review CSV (answer, proposed display, source+line, topic suggestion, proposed familiarity, reason/provenance, review decision, reviewer and date). Record explicit rejects and holds, and issue batch sizes of ~100–250 terms.
4. Add automated checks for duplicate normalized answers, dangerous auto-approval, unfamiliarity/length outliers and incomplete topic metadata, with honest dashboards.
5. Expand to more of the 33 registered topic families, prioritizing high-frequency real-world nouns and distinct subtopics. A spellchecker dictionary does **not** supply safe or complete semantic theme assignments. Human curation is required.
6. The target remains **10,000 unique approved playable English words**, not 10K raw dictionary entries. A first production corpus needs immutable identifiers, reviewed labels, versioned seed replay, spatial/variety QA, actual source-matched Android checks and save compatibility.

**This milestone is candidate intake only; the older v0.9.4 Android QA build remains untouched.**
