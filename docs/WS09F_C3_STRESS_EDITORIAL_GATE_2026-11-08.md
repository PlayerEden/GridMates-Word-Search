# WS-09F-C3 — 1,800-case Kotlin giant-board stress + editorial supply gate

**November 8, 2026 · DRAFT LAB ONLY.** Stacks on [WS-09F-C2 Kotlin generator #79](https://github.com/PlayerEden/GridMates-Word-Search/pull/79), and its prerequisite [Python content lab #78](https://github.com/PlayerEden/GridMates-Word-Search/pull/78). No Android source, Play signing, puzzle sizes in New Solo, save schema, LAN protocol, or frozen version-1/2/3 generator is modified.

## Full deterministic seed stress

**Verified source** `53235e940099007d2e04b54d339e4418234c0354`:
[Dedicated GitHub Actions #42472327191](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42472327191) **SUCCESS**. An additional [same-engine stress/triage job #42472610960](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42472610960) also passed.

- Exact lab fixture parity against frozen v3 plus unapproved review-pending v4 additions.
- **18/18 literal cross-commit lab grid fingerprints** preserved; prior 180/180 deterministic two-run checks still pass.
- **1,800/1,800 generated and independently verified**: 3 themes × 2 sizes × 3 direction-mode sets × 100 unique seeds (95 regular, 5 edge cases, including `Long.MIN_VALUE`/`Long.MAX_VALUE`).
- Every word was verified in the expected saved path and checked for exactly one occurrence across all eight reading directions. Four replay seeds per configuration remained bit-for-bit stable.
- Worst seen was **18 of 24 allowed retries** on a Space 50×50; no failures in sampled seeds. The implementation is intentionally bounded, but this observation does **not** establish a universal guarantee that every seed works.
- Github Linux x64 JVM reference only: a run measured ~11.7–14.2 seconds total for 1,800 puzzles, depending on runner. Space 50×50 Relaxed was the slowest category (one run reported p95 ~38.8 ms). These are **runner generation costs**, not Android FPS, memory peak, battery, device latency or user-experience evidence.
- 50×50 **105 words** and 40×40 **70 words** remain test hypotheses, not gameplay decisions.

## Editorial gate is still open

The new `design/marathon/v4_editorial_triage_proposal.json` is an **unapproved, proposed classification**. It does not filter the Kotlin generator, alter the lab fixture, or silently rewrite frozen IDs. `tools/ws09f_c_editorial_supply_report.py` enforces source and conflict consistency but cannot attest word meanings or player familiarity.

Provisional relaxed words after excluding original v3 advanced vocabulary, newly proposed advanced-only words and manual-review holds:

| Theme | Existing experimental candidate pool | Tentatively eligible for Relaxed | Spare beyond 105 targets | Against 125-word planning goal |
| --- | ---: | ---: | ---: | --- |
| Woodland | 194 | 126 | +21 | +1 |
| Space | 185 | 121 | +16 | **4 short** |
| Garden | 174 | 135 | +30 | +10 |

**Action:** Space needs at least four more **editorially approved, familiar** terms just to meet the provisional supply buffer. Ten extra review suggestions (e.g. FULLMOON, NEWMOON, NIGHTSKY, CRESCENT) are stored separately and checked for substring collisions; they are **not added to the generator or counted as approved**. Many existing proposed terms require display spacing and plain-language review (e.g. `RAISEDBED` should visibly read *Raised bed*, and `BROWNDWARF` should read *Brown dwarf*). This is especially important for the older, nontechnical and paperback-puzzle audience.

## Next engineering gate — separate PR, not yet complete

1. Perform genuine editorial QA and assign an immutable v4 content version with **separate normalized grid answers and visible labels**, plus hardcoded new-version goldens; no changing published v1/v2/v3 dictionaries or previously saved puzzle IDs.
2. After the content design is signed off, copy a reviewed v4 generator to `core` in a new gated branch. Append any v4 enum values only **after existing ordinals** and explicitly exclude new presets from v2 generation. Route `SoloProgressRules.puzzle` and saved reconstruction by correct version. Avoid treating the `wordsearch:v4:LAB` research prefix as a shipping ID.
3. Preserve schema5 active and up to 24 paused Solo, Daily/profile/Race/Duel, LAN v7; keep 40/50 hidden from the New Puzzle screen pending player-visible device QA, full historical goldens and signer-matched save-preserving install-over.
4. Test large-board **105-target list legibility** (alphabet/pagination/unfound filters), selections crossing 10-cell map regions, actual phone touch accuracy and 320dp/200%-font/landscape usability. Profile p50/p95 **frame-time, allocations and memory on Android** against existing 25/30, not JVM generation timings.
5. Resolve **v0.9.4/code48 saved-zoom PR #74** Android verification (currently Gradle/Kotlin download HTTP504) before coupling multi-hour giant-board progress to view bookmarks; preserve the last full green **v0.9.3/code47 with 111/111 Android checks**.
6. Family tester's actual installed APK version/signature and independently restorable backup remain unknown—**do not force install or uninstall**. Physical two-phone Co-op issue #53 remains open. Integrate stacked draft PRs only after a reviewed ancestry, source-matched QA artifact and real-device acceptance.

**No APK, merged PR, playable 40/50 board, production signing or real-device performance is claimed by this laboratory.**
