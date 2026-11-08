# WS-09F-F — Target spread, direction and cross-seed variety gate

**November 8, 2026. Draft research only: V4 giant puzzles are NOT offered in Android Solo, and no experimental puzzle IDs are persistable.** This branch stacks on V4 candidate tier PR #84. Existing V1/V2/V3 published generators, original V4 placement lab and all 18 frozen preview grid fingerprints are untouched.

## Why this is necessary

A board can be valid and solvable but still feel repetitive if words cluster in a corner, most words share a direction, or subsequent puzzles reuse the same vocabulary/paths. Previously the V4 generator chose its first fitting path in Relaxed and a fitting overlapping path in Classic/Expert; it did not score *board-wide* spread.

### Source-grounded A/B measurement

[Original preview distribution baseline, 1,800 cases — GitHub workflow #42514221261](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42514221261), before any quality-scored placement:

- All **1,800/1,800 grids were distinct**, with full available direction variety: Relaxed 2/2, Classic 6/6, Expert 8/8.
- Only **953/1,800** passed the *provisional combined* region/direction/spread acceptance gate. There were **390 puzzles with at least one empty 10×10 region**, **786 with at least one region containing fewer than five distinct target cells**, **56 with overdominant directions**, and **19 with imbalanced quadrant target centers**. These category counts overlap.
- Classical/Expert modes are particularly at risk because the old overlap preference can repeatedly choose paths near already occupied regions. The metrics count **actual hidden-word path cells**, never filler letters.
- Across 1,782 adjacent same-theme/size/mode seed pairs, no full grids repeated. Only 14 pairs had even one identical path for a shared word. The primary objective issue is uneven area coverage, not duplicate whole grids.

### Separately namespaced balanced preview engine

- `core/src/main/.../V4PuzzleQuality.kt` measures: all actual word-center locations across quadrants and 4×4 zones; target-cell coverage by 10×10 map section; unique direction vectors and dominant direction share; word-length variety; overlap and identical path comparisons between consecutive seeds.
- Proposed **hard spatial/variety checks** (`V4PuzzleQuality.issues`): at least 10% of word centers in each quadrant; at least 12 of 16 4×4 center tiles occupied; **every 10×10 map section has at least five distinct actual hidden-word cells**; at least 2/4/6 different directions for Relaxed/Classic/Expert; no single direction above 65% Relaxed or 48% Classic/Expert; more than one word-length band. Human playtesting must still judge whether these numbers are satisfying.
- `core/src/main/.../V4BalancedPlacement.kt` is a **separate experimental algorithm**, not a rewrite of the frozen prior engine. It scores up to 160 compatible placements per target and favors **underused 10×10 sections**, spread of target centers, and neglected allowed directions; gives only a modest bonus for overlaps. It fills remaining cells, rejects accidental 8-way duplicate occurrences, and rejects layouts failing the quality checks. Every puzzle attempt remains deterministic by a named independent SHA seed and bounded to 24 complete construction attempts; **never unlimited retries or silently reduced word counts**.
- All new experimental IDs use `wordsearch:v4:BALANCED-PREVIEW:...` including exact word-order fingerprint. **These are not shipping IDs and must never be saved in production**. No approved V4 dictionary, new enum values, active Solo routing or APK is introduced.

### Measurements and verification

- [Initial 180 balanced-preview test #42514621213](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42514621213): **180/180 quality gates passed**, **15,750 real word-path claims**, **0 adjacent identical grids**, worst 4/24 allowed construction attempts. In every tested board, *all* 10×10 regions were covered and all 16 4×4 word-center zones occupied; Relaxed used 2, Classic 6, Expert 8 distinct directions.
- [Full 1,800-case balanced QA and real Gradle core JUnit #42514815037](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42514815037) — **in progress when this document was first authored**. The workflow also includes a synthetic deliberately clustered puzzle that **must fail** the spread/direction gate, 54 real bounded balanced previews, historical V1/V2/V3 goldens and the previous 18 unchanged V4 source goldens. No approval until both stages are completely green.
- Separately, V4 tiered word pools passed 1,800/1,800 *technical* generator checks in [#42512914406](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42512914406). Those did not demonstrate acceptable spatial quality.

## Critical: word-list size is still too small for novelty

Each current proposed Relaxed V4 corpus has only **126 Woodland, 130 Space, 135 Garden words**, but a 50×50 puzzle uses 105. Inclusion-exclusion means **any two puzzles must share at least 84 Woodland / 80 Space / 75 Garden words** even under the most favorable selection. For consecutive seeds, measured word differences are often just **13–29** in 50×50 Relaxed.

**Proposed editorial target: ~210–250 varied, genuinely approachable, source-reviewed words per theme** before promising repeat-play novelty at 105 targets. With 210 candidates, two independently randomized 105-target sets overlap by ~52.5 words on average; with 250, ~44.1, although actual deterministic selection must be tested. Distinct subtopics (e.g. forest animals / trees / habitats; planets / spacecraft / sky; plants / tools / garden creatures), length variety and understandable display labels must also be editorially reviewed. We must avoid trying to solve this through random placement alone.

## Remaining launch gates

1. Finalize real editorial-approved content, topic diversity, difficulty tiers and player-facing spaced labels; exclude impossible palindromes such as RADAR under the current strict eight-direction exactly-once uniqueness rule.
2. Freeze the actual new V4 quality-scored algorithm, version namespace, approved dictionary, IDs and literal golden snapshots before adding persistent SoloRun v4 reconstruction. Preserve historical snapshots, seed paths and existing enum ordinals.
3. Human screenshot review at 40/50 board sizes; check no hidden word visually dominates one area and direction balance feels natural—not artificially patterned. Play on 320dp, large text, landscape, touch/pinch/map, 105-word index and multi-hour save/bookmark restore.
4. Same-signer source-verified QA APK update tests and physical two-phone Co-op #53 before wider rollout; unknown family installed APK/signer and backup remain blockers.

**No new APK published and no change to the currently verified v0.9.4/code48 family QA candidate.**
