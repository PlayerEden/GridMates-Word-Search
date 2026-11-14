# 2026-11-09 — WS-14D CI repair: official pinned Gradle fallback for recurring HTTP 504

**Status:** **BUILD-PIPELINE SOURCE FIX COMMITTED; NEW EXACT-HEAD CI PENDING.** This is not evidence a new APK succeeded.

## Who / What / Where / Why

- **Who:** Assistant investigated failing Android and artwork QA. The owner asked us to begin a grounded swipe-library test and maintain exact per-pass records.
- **What:** Both GitHub workflows have repeatedly failed provisioning Gradle 8.11.1, **before source compilation**. The 8bbb911 implementation's [standard run #42583044917](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42583044917) and [art run #42583044918](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42583044918) failed at `gradle/actions/setup-gradle@v4` due to external HTTP 504. Earlier art run [#42582505308](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42582505308) explicitly recorded HTTP 504 for Gradle distribution fetch.
- **Where:** `.github/workflows/ws01-android-qa.yml`, `.github/workflows/ws12g-qa-art-lab.yml`, and new `tools/ci/install_pinned_gradle.sh`.
- **Why:** Avoid repeated identical failed Actions-minute-consuming provision attempts; get an actual source-matched illustrated QA APK into the owner's hands for swipe-layout visual review.

## Prior state / dependencies

- Previous journal: [WS-14D native swipe bookshelf feature implementation](2026-11-09_WS14D_HORIZONTAL_BOOKSHELF_IMPLEMENTATION.md).
- New UI source `8bbb911948dd7561d9b4e2add658ec7ae39302d3` implements horizontal pager/arrow navigation, reversible old shelf, selection focus motion, native progress, QA test coverage and original six-art import in standard QA. That source was **not** verified by the above failed runs; the failures happened before compilation.
- The old illustrated art APK from source `00d2a36` was successfully built earlier ([Android QA](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42576785096), [art isolation](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42576785170)); the user has shared real handset screenshots of that *older* layout. The last earlier broad fallback remains v56. No project app signing or save schemas changed.

## Solution committed

Commit **`8c22f0eed131e335eec49d9a49e67a3ee53cd865`**:
- Adds `tools/ci/install_pinned_gradle.sh`, downloading official **Gradle 8.11.1** from `downloads.gradle.org`, falling back once to `services.gradle.org` if necessary. Uses **one bounded HTTP retry per endpoint** rather than unbounded retries.
- Verifies exact Gradle distribution SHA-256 **`f397b287023acdba1e9f6fc5ea72d22dd63669d59ed4a289a29b1a76eee151c6`** against the [official Gradle release checksums](https://gradle.org/release-checksums/) before extracting or executing anything. A checksum mismatch fails closed.
- `gradle/actions/setup-gradle@v4` still configures cache/wrapper validation but is no longer responsible for downloading Gradle distribution; pinned Gradle command is placed on Actions `GITHUB_PATH` and version-checked.
- Both standard Android QA and art release-isolation workflows use the same small CI script, not two divergent build-tool versions. No runtime app code, artwork, portrait IDs, catalog, player data or signing changed.

## Exact QA and artifact gate

- New GitHub Actions runs triggered on `8c22f0e`: [standard Android QA #42583425104](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42583425104) and [art/release-isolation #42583425251](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42583425251). They were **pending** when this pass was written. On any later docs-only commit, check the newer exact HEAD's runs; do not equate these run URLs with success.
- **No source-matched WS-14D APK is yet verified** by this journal entry. If these runs fail, first inspect whether the failure is the distribution endpoint, Kotlin compiler, lint or code tests; don't infer a cause from the run badge alone.
- Art-packaging acceptance: all six Woodland/Space/Garden covers+spines inside debug APK with hash receipts; zero in release in art workflow. Standard QA now also verifies six originals in its debug APK.
- Real owner installation, swipe gestures, save continuity and accessibility still pending. No production merge.

## Next action

Read live PR #131 HEAD and the newest Actions workflow results. Fix any **real compilation** issue once the distribution step works. Prefer one passing latest artifact and provide the GitHub Actions ZIP extraction route (ChatGPT sandbox downloads previously produced "Library file not found"). Preserve old v56 and old classic shelf as rollbacks. Record successful artifact path, app identity, checksum and user screenshot feedback as a new journal entry, not just chat prose.

## Follow-up evidence — official endpoints also HTTP 504

On source `073cd2a73f3ded483b5d2092657477bf7991f667`, [standard Android #42583551788](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42583551788) and [Art QA #42583551775](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42583551775) both reached the custom Gradle installer but **both official endpoints still returned HTTP 504**. No Gradle ZIP was downloaded and no Kotlin compilation took place. This confirms a broader distribution-network outage for those runner requests, not source-code correctness.

One bounded alternative is now implemented: after the two official sources fail, attempt `mirrors.aliyun.com` and `mirrors.huaweicloud.com` for the **same Gradle 8.11.1 ZIP**, applying the **unchanged official SHA-256** `f397b287023acdba1e9f6fc5ea72d22dd63669d59ed4a289a29b1a76eee151c6` before extraction. Mirrors are transport fallbacks only; no unverified binaries execute. Each endpoint is tried once with strict timeouts; **no looping GitHub Actions retries**. Await a fresh exact-head workflow and record actual result before sending a replacement APK.
