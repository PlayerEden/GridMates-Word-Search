# WS-09F-C — Initial 40×40/50×50 content-supply audit (lab only)

**November 8, 2026.** Proposed 40/50 expansion research; **not gameplay implementation and not a playable APK**. This isolated branch starts at [verified WS-09F-B / PR #73](https://github.com/PlayerEden/GridMates-Word-Search/pull/73) without stacking on unverified viewport-persistence [#74](https://github.com/PlayerEden/GridMates-Word-Search/pull/74).

## Measured existing source inventory

Audited the `WordLibraryV3` lists at GitHub source blob `6c64d1bc7478ce47be7c761252c04c7a696bee64`, used by 25×25/30×30 puzzles. The audit runs `python3 tools/ws09f_c_content_audit.py` using only the Python standard library and reads the versioned Kotlin source without modifying it.

| Theme | Familiar/everyday | Additional familiar | Advanced | Total unique | Conservative substring-safe pool |
| --- | ---: | ---: | ---: | ---: | ---: |
| Woodland | 28 | 38 | 15 | 81 | 81 |
| Space | 30 | 37 | 14 | 81 | 76 |
| Garden | 29 | 36 | 14 | 79 | 76 |

The substring-safe pool is a stable greedy check across ALL tiers (longest words first); it is **not** the optimal independent-set size, does not include challenge-tier restrictions, and does not establish that a full board can place that many unique occurrences. In particular, Relaxed's smaller eligible pool is much tighter. The generator's random filler and 8-direction uniqueness checks can further reduce success.

## Experimental targets — not decisions

| Continuous board | Working target for content planning | Existing full-pack supply headroom |
| --- | ---: | --- |
| 40×40 | 70 targets | Woodland +11; Space/Garden +6 |
| 50×50 | 105 targets | Woodland −24; Space/Garden −29 |

A nominal 40×40 inventory with only 6–11 spare candidates is **not** enough to guarantee interesting repeat play or seed variation. **50×50 at 105 targets is impossible using just the existing v3 packs** by a simple candidate-count bound, even before placement and quality checks. These are only working targets; owner playtesting must determine satisfying density, reasonable completion time, legibility, and challenge balance.

## Concrete next engineering steps

1. Create a **separate v4 content corpus** with documented editorial curation and immutable, seeded version IDs. Do **not** modify v1/v2/v3 entries, order, enum ordinals, RNG or board fingerprints; old saved puzzles must reconstruct byte-for-byte.
2. Target an initial candidate inventory of **at least 130 per theme** for 40×40 lab and **170 per theme** for 50×50 lab as *planning buffers, not guaranteed sufficient*; run substring checks **per challenge eligibility**, reject duplicates and implausible/poorly themed words, and verify manageable average word lengths. Reassess as measured placement and player-fun data accumulates.
3. Prototype a pure **v4 generator in an isolated JVM laboratory**, with bounded deterministic placement and unique-occurrence search, multi-seed/failure matrix, literal golden outputs and generation-time/memory measurements. Keep 40/50 hidden from Solo setup and do not touch schema5 or production code until lab acceptance.
4. Test 25/30 renderer at Fit and zoom levels on actual Android hardware (p50/p95 frame duration, jank, memory/allocations and hit accuracy); then instrument 40/50 screen prototype before declaring it playable. Initial F-B culling proved a smaller draw-call budget, **not** FPS.
5. Owner acceptance: word-list navigation, region-crossing selections, fit/zoom readability, landscape/200% font, calm paperback presentation, resume for multi-hour puzzles, correct hints and fair scoring.
6. Signed historical APK upgrade, backup/restore and real two-phone Co-op P0 #53 remain separate acceptance gates. Do not encourage any destructive reinstall or auto-merge these experiments.

**Independent verification:** Python-only audit may run during the external Gradle/Kotlin HTTP 504 incident. It cannot be substituted for Android CI, a valid APK, Play readiness or physical testing. The verified v0.9.3/code47 checkpoint remains untouched; draft v0.9.4/code48 PR #74 is separately gated.
