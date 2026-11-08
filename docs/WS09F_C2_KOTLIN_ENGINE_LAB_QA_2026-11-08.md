# WS-09F-C2 — Isolated Kotlin V4 40×40/50×50 generation lab

**November 8, 2026. Draft, lab-only.** This work branches off [WS-09F-C vocabulary/positioning lab PR #78](https://github.com/PlayerEden/GridMates-Word-Search/pull/78), itself stacked on verified WS-09F-B #73. The separately developed v0.9.4/code48 saved-viewport candidate [#74](https://github.com/PlayerEden/GridMates-Word-Search/pull/74) is **not** included here.

## What now exists
- Independent JDK/Kotlin source `labs/ws09f_c_kotlin/V4EngineLab.kt`, deliberately **outside** `core/src/main`, `app/`, and the Android Gradle source sets. It never changes Solo setup, legacy content versions 1–3, stable puzzle IDs, schema5 backup, LAN v7, APK, signing or installed user data.
- Experimental target counts: **40×40: 70 words**, **50×50: 105 words**. The content is a **test-only** fixture of 194 Woodland, 185 Space, 174 Garden candidates (unique, alphabetic, 5–16 characters, conservatively free of substring pairs), **not yet editorially approved, familiarity-tiered or licensed for release**.
- Fixed bounded generator: 24 deterministic board attempts × 160 candidate placements/word, 8-way occurrence counter and independent path/direction validation. `wordsearch:v4:LAB` IDs intentionally cannot collide with any released v1/v2/v3 puzzle.
- Test cases: 3 themes × 2 sizes × 3 mode direction-sets × 10 seeds = **180**, each generated twice and checked for identical grid fingerprint, target paths, ID, allowed directions, full bounds, exact word spelling and unique occurrence. Negative cases reject 75×75 and substring-conflicting pools.
- `tools/ws09f_c_kotlin_fixture_audit.py` guards the fixture against drifting away from the frozen v3 source and current review-pending v4 candidate manifest.

## Verified source checkpoint
- **Exact first Kotlin lab QA SHA:** `1160e461b36fa4e99907d37e5fde379385a474d5`.
- [GitHub Actions run #42471049311](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42471049311) **SUCCESS**: source-fixture parity, Kotlin compiler found (`kotlinc-jvm 2.4.21`, JRE 17), standalone compilation and **180/180 laboratory scenarios plus deterministic repeats PASS**.
- Worst retries observed: **8** for a Space 50×50 Relaxed test; bounded maximum **24**. This does not establish worst-case time/memory on Android or all possible seeds.
- The Kotlin generation algorithm is a new frozen-*candidate* design; its seeds/fingerprints are **not intended to equal the Python prototyping fingerprints**. The two labs test common invariants instead.
- Reproduction on a machine with Kotlin: `kotlinc labs/ws09f_c_kotlin/V4EngineLab.kt labs/ws09f_c_kotlin/Smoke.kt -include-runtime -d /tmp/ws09fc.jar && java -jar /tmp/ws09fc.jar labs/ws09f_c_kotlin/candidates_test_only.txt`. Python audit: `python3 tools/ws09f_c_kotlin_fixture_audit.py`.
- CI detects missing standalone Kotlin compiler explicitly; it must not silently treat fixture-only success as a Kotlin compile pass.

## Next gates, not yet passed
1. Editorial review and assignment of familiar/advanced/rare word categories; accessibility and readable word lists for 105 targets. Any edit to a shipped v4 corpus later must increment its content version. Do not silently mutate a seeded published puzzle.
2. Capture **literal cross-commit Kotlin golden checksums** for selected cases and run broader failure-seed, negative-content and performance matrices (include 100–1,000 seeds and low-end CPU/memory sampling before release acceptance). The 10-seed suite is an initial smoke, not proof all seeds succeed.
3. Move a reviewed, frozen v4 content library and generator into `core` in a **new isolated integration PR** only after preserving v1/v2/v3 golden fingerprints and historical puzzle reconstruction. Preserve existing enums/ordinals and IDs.
4. Prototype 40/50 viewport, Map crossing, selection/hit testing, zoom and readable 105-target list. Compare p50/p95 frames and memory on real Android; long-play resume and backup. No 40/50 public Solo selection before device acceptance.
5. v0.9.4 viewport-save [PR #74](https://github.com/PlayerEden/GridMates-Word-Search/pull/74) **still lacks Android compile and old→new signer-safe upgrade proof** because external Gradle/Kotlin downloads hit HTTP504. Keep last verified Android executable **v0.9.3/code47, 111/111 Android tests** as rollback. Older family app signing/version and independently restored backup unknown. Physical two-phone Co-op #53 remains open.

**No APK, production signing, merged PR, new public board or real-device performance is claimed by this Kotlin lab.**
