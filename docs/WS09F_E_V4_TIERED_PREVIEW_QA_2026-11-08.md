# WS-09F-E — Source-derived V4 tiers and deterministic bounded content redraw

**November 8, 2026 · research proposal, NOT editor-approved or an APK release.**

This branch stacks on [V4 preview core PR #83](https://github.com/PlayerEden/GridMates-Word-Search/pull/83). That source has passed its historical core and standard Android CI, but **no 40×40 / 50×50 Solo mode is enabled**. The game on the family phone is untouched.

## Work completed

- Copied the original **unapproved editorial triage** manifest unchanged from earlier [C3 PR #80](https://github.com/PlayerEden/GridMates-Word-Search/pull/80) for test-source parity. The frozen original v3 dictionaries, V4 full-pool lab fixture, and historical puzzle IDs remain unchanged.
- Created `design/marathon/v4_candidate_tiers_workbench_2026-11-08.json` and `core/src/test/resources/ws09f_e_v4_provisional_tiers.txt`. A Python source-checker rebuilds all trial categories from the exact v3 advanced vocabulary, original frozen V4 candidate fixture, triage's advanced-only proposals and hold queue. No user-facing dictionary or reviewed labels are introduced.
- **Tier supply**: Woodland 126 tentative Relaxed / 59 proposed advanced / 9 manual holds; Space 121 originally tentative Relaxed, minus **RADAR** (an intrinsically impossible all-eight-direction palindrome) plus **10 unapproved extra Space word proposals = 130 trial Relaxed**, 53 proposed advanced / 11 manual holds **plus 1 generator-compatibility hold**; Garden 135 trial Relaxed / 29 proposed advanced / 10 holds. Both Relaxed 70-word and 105-word experiments have at least a **125-word proposed buffer**, subject to a genuine human editing pass and playtesting.
- Created separate **display-label proposals**, such as *Milky Way*, *Black hole*, *Sweet pea*, *Raised bed* and *Full moon*; labels are **metadata proposals, not displayed by the current app**. Verify idiomatic terms, regional usage and audience familiarity before accepting.
- Implemented `core/src/main/.../V4PreviewTierSelection.kt` containing **no vocabulary**, only deterministic candidate selection using SHA-256 seeds. The experimental quotas are: Relaxed **zero advanced**; Classic **7/70 or 10/105 advanced**; Expert **17/70 or 26/105 advanced**. The actual `V4PreviewPlacement` still uses its independent 24-attempt × 160-samples/word bounded placement algorithm.
- Added a **second bounded selection budget**, at most **8 distinct deterministic candidate-list draws** per test puzzle, after catching *only* the explicitly exhausted 24-attempt placement condition. Neither max bound is removed. Any exhausted outer budget causes a hard failure; every selected candidate set has an identity tied to its source words. No fallback to a smaller board, alternate published ID or unknown seed.
- Added `V4ProvisionalTierCoreTest.kt` for **180 tiered trial puzzles** (3 themes × 2 sizes × 3 challenges × 10 seeds), strict quota, 15,750 path-claim checks, deterministic replay including actual draw count, explicit review-hold exclusion, ten Space proposals being exercised, and unchanged existing Solo/V1–V3 board sizes. The existing [18 literal V4 preview goldens](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42510018590) are still tested without modifying their generator or original source pool.

## First failure found — and why it matters

A repeatable failure investigation also found **RADAR**, a five-letter palindrome, inside the Space trial Relaxed pool. Because it reads identically in both opposite directions on every placed straight-line path, the engine's strict eight-direction `occurrences == 1` rule makes such a target **mathematically impossible**. This is not solved by raising retry budgets. The staging corpus now explicitly quarantines RADAR, and the tier selector rejects **all** palindrome candidates immediately. The frozen original generator fixture and 18 established lab goldens stay unchanged.

The first source-matched preview selection tests [#42511352891](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42511352891) failed on `V4ProvisionalTierCoreTest`. Diagnostic run [#42511799716](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42511799716) identified the **exact issue**: `SPACE`, **40×40**, **RELAXED**, seed **11**, candidate selection 70 of 131 tentative words. That list was valid A–Z and substring-safe, but the placement engine exhausted its original 24 attempts.

This is why the new content-level redraw is **separately bounded**. It preserves the inner placement cap, determinism and all older saved-game goldens. Both the content-draw count and resulting target set are checked. **Do not call this stable until the complete core JUnit suite passes** and the 1,800-seed tier stress reaches a separate green checkpoint.

## Verification gates

- `python3 tools/ws09f_e_tier_workbench_audit.py`: validates exact v3/frozen-lab/triage derivation, held and advanced isolation, substring-free merged pools, 125-word minimum, all display label normalization and no Solo/UI tier engine routing.
- `gradle --no-daemon :core:test`: executes the new tier JUnit **and** all existing historic V1/V2/V3, old V4 lab and old 18 pinned preview goldens.
- [Dedicated research workflow](https://github.com/PlayerEden/GridMates-Word-Search/actions/workflows/ws09f-e-tier-preview-core.yml). **Final post-redraw run pending at document creation.**
- No actual APK installed, signed or released from this branch. Existing [v0.9.4/code48](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42501935166) remains the last fully signed-upgrade Android-test-verified family QA candidate; the previous [117/117 A–Z index QA](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42506712015) remains separate.

## Next blockers, NOT resolved by this research

1. **Editorial approval, not just counts:** do not assume all 126/131/135 trial Relaxed entries are actually familiar. Remove esoteric/science terms, resolve regional word choices, proofread compound/proper-name display labels, and select final known-good 125+ words per theme without introducing substring collisions.
2. Expand tier-specific stress beyond the 10-seed set to identify worst-case outer redraws, failure rates and CPU/memory cost; record exact code SHA and golden fingerprints before changing version/ID semantics.
3. Make permanent *new* v4 content namespace, generator fixtures and seed replay behavior. Never ship `wordsearch:v4:PREVIEW`, mutate historical v1/v2/v3 content, reorder existing `PuzzleBoardSize` ordinals or silently change a saved giant board after release. **Explicitly fix the existing `PuzzleBoardSize.v2Presets = entries.filterNot { it in largePresets }` logic before appending any V4 enum**, or the v2 generator could mistakenly accept V4 sizes.
4. Route v4 boards through a safe, explicit SoloRun/pause/backup/resume version with legacy-schema validation. Test 105-word A–Z, real touch precision, viewport culling, saved map/zoom, large-font/landscape, long-session CPU and device memory.
5. Separate real-device & QA install-over approvals, verified existing family signer/version and restorable backup, physical two-phone Co-op P0 #53, and draft PR integration before any public Android rollout.

**Current milestone is an unmerged V4 content design/algorithm experiment, not an editorial or gameplay release approval.**
