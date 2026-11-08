# WS-09 — Foundation-first Word Search expansion and owner playtest refinement

**Planning date:** 2026-11-07 ET; **status:** DESIGN APPROVED IN PRINCIPLE / IMPLEMENTATION GATED.
**Current verified executable:** [WS-09B v0.8.8/code42, schema5](WS09B_BUILD_QA_2026-11-07.md) — **100/100 API35 PASS**, signed real v0.8.7→v0.8.8 schema4→5 upgrade with partial Solo/Daily/identity/Race/Duel preserved, source `36ba64d3199cba8878de5126eb1c924f8b9ce897`, [draft PR #68](https://github.com/PlayerEden/GridMates-Word-Search/pull/68). This does not establish physical owner, two-phone or production release acceptance.
**Relationship:** WS-09 is deliberately developed **before** the already-defined WS-08 beta/release-readiness gate; existing WS-00–08 IDs and evidence are preserved. This plan is subordinate to the [master roadmap](DEVELOPMENT_ROADMAP.md). Never conflate a draft PR, automatic emulator PASS, owner visual PASS or two-phone acceptance.

## 1. Owner-confirmed goals (design constraints)

1. **Word Search should support much bigger boards**, eventually up to **100×100** as a distinct long-term Marathon experience, not as a near-term promise.
2. **Continuous giant board** is the preferred experience. A region overview/grid navigator may help locate portions but must NOT split it into unrelated 20×20 puzzles. Words may cross navigation-region boundaries.
3. **Choose game mode, board size, theme and challenge before entering gameplay**. Save recent presets; an in-game Options menu should mostly contain presentation, accessibility and help, not irreversible puzzle replacement.
4. **Multiple unfinished Solo puzzles** should be resumable without losing another session, especially long-session Marathon boards.
5. **Square-corner actual letter grid**, straight outer border, zero letter-cropping, book-style paper margins. Prefer ~16–20dp horizontal breathing room on normal portrait phones initially, adjustable by available width/screen scaling; never shrink dense letters simply to protect an arbitrary margin.
6. Keep GridMates' **calm, readable paperback-book visual identity**, make Home, Records, Collections, Fonts, Achievements and Settings more compact and less verbose; retain usable touch sizes and accessibility.
7. An existing family tester already plays an **older Word Search APK**. Preserve their progress; no destructive reinstall, clearing storage or signing change without version identification, independently verified backup and explicit migration steps. Phone version is NOT yet known. No claim that an old runner-signed build can be installed over.
8. Near-term developer QA without asking for two phones; real connected Co-op word marking [P0 #53](https://github.com/PlayerEden/GridMates-Word-Search/issues/53) stays open. Maintain draft PRs, checkpoints and rollback; do not auto-merge or publish.

**Proposals to validate with owner playtesting:** exact preset names and target counts, 16–20dp side margin, 200–400 words at 100×100, 25×25/30×30 as the first expanded sizes, persistent zoom/viewport as preference, Room/database versus atomic JSON as storage for multiple saves. None is implemented or locked merely by appearing here.

## 2. Source-of-truth and high-risk constraints

- Present `PuzzleBoardSize` values: legacy COMPACT=8×8, STANDARD=10×10, LARGE=12×12, plus BOOK_SHORT=12×12/12 targets, BOOK_CLASSIC=15×15/16, BOOK_BIG=20×20/24. Frozen names and old ordinal positions matter because v2 generator seeds include enum ordinal. Do **not** insert or reorder enum entries, alter v1/v2 dictionary/order/filler/directions/placement/randomness, or silently reinterpret existing puzzle IDs. Version any new generator + content packs.
- Current `SoloProfile` schema4 has exactly one `active` Solo run + completed results, Daily, identity and Race/Duel journals. `SoloStore` saves via AtomicFile; backup JSON contains core profile, **not selected font, sound or haptic preferences**. Future save schema must migrate additively, preserving source bytes/recovery path on failure and faithfully round-trip all player-visible state.
- `SoloProgressRules.validate` currently silently intersects found-word IDs against regenerated targets and filters unknown hint IDs. Before touching old generators or save schema, establish immutable golden puzzle snapshots/word-target tests and an upgrade fixture with partially found words, real hints and Daily.
- QA package is `com.playereden.gridmates.wordsearch.qa` with stable **public test-only** signer from v0.5.9 onward. v0.5.8 and earlier debug APKs used different signing. [WS05J signing/migration contract](WS05J_STABLE_QA_UPDATES_2026-11-07.md). Do NOT use public QA keys for production, claim same-app migration to the future production ID, or delete a tester's install.
- Manual export/import must be validated **on the actual installed older variant** before advising a destructive transition; keep the original app untouched if version/backup capability is unknown.
- Existing WS-07 physical touch and WS-08 production gates remain BLOCKED pending their original acceptance evidence.

## 3. Planned workstreams and gates

| Stage | Status | Implementation scope | Pass criteria |
| --- | --- | --- | --- |
| **WS-09A — Save and compatibility baseline** | **AUTOMATED COMPLETE; old family APK OPEN** | [A1](WS09A1_BUILD_QA_2026-11-07.md) and [A2](WS09A2_BUILD_QA_2026-11-07.md) established known v1/v2 historical goldens, partial old APK update, backup/restore defenses; no historical generator mutation. | Old family tester actual APK version/signing and independent backup confirmation still OPEN, no forced uninstall. |
| **WS-09B — Multi-puzzle persistence** | **AUTOMATED PASS, OWNER QA OPEN** | [v0.8.8 schema5 / 100 tests](WS09B_BUILD_QA_2026-11-07.md) implements 24 paused Solo slots, resume/swap/confirmed remove, fail-closed legacy migration, atomic backup. No Room migration; future timestamps/viewport persistence optional. | 3 unfinished sessions persist over save/reopen/backup/restore, real signed partial old APK migration; phone user acceptance and full font/landscape/reboot OPEN. |
| **WS-09C — Pre-game setup and Home flow** | PLANNED | Select Solo/Daily/Nearby from Home; explicit New Puzzle vs Continue/My Puzzles; choose theme, size, challenge **before** starting; remember most recent setup. Nearby host chooses pre-match options; no midgame room changes. | Novice can begin/resume in few taps; pre-start confirmation only when intentionally discarding an existing save; at 320dp and 200% font. |
| **WS-09D — Paperback viewport and touch** | PLANNED | Square grid and complete border/corner letters; balanced responsive side margins; no under/overdraw/clipping; large-board zoom/pan and two-tap select, Fit, accessible navigation. | Edge and corner words selectable; 12/15/20/25/30 test matrix in portrait/landscape and large fonts; no pinch→claim confusion; no inaccessible controls; physical solo review. |
| **WS-09E — Versioned generator and content** | PLANNED | Preserve v1/v2 exactly; introduce separate versioned v3 for 25×25/30×30, expanded curated theme vocabulary and difficulty/size independence; benchmark uniqueness and placements. | Deterministic across app upgrades, no missing/duplicate target IDs, no accidental occurrences, known-seed golden snapshots, all words findable; generation time/memory budgets set from measurement, never guessed. |
| **WS-09F — Giant board navigation** | FUTURE / LAB | Prototype 40×40/50×50 then 75×75/100×100 after benchmarks; one continuous board, optional region navigator + current-location marker, bookmarks, list search/filter, viewport persistence. | Real device smooth panning/drawing, low memory and resume latency, performant generation, readable selection, words spanning overview regions, crash-safe resume; no forced scrolling entire page. |
| **WS-09G — Compact collections, records and settings** | PLANNED | Portrait gallery in real tiles; clear locked/equipped states; non-dominant font-license details; tighter achievement/Record cards and zero-state layouts; trim old QA-only technical copy in main UI; Settings contain sound/haptics. | Owner screenshots, card spacing, scroll reachability, contrast/large text/TalkBack; preserve all portrait/font unlock rules and stats. |
| **WS-09H — Integrative migration and performance QA** | PLANNED | Full regressions across historical version APKs, valid/invalid backups, large active sessions, upgrades, resume and performance. Source-matched QA APK and rollback. | 100% same-source automated suite plus physical solo owner acceptance before test recommendation; public QA signing unchanged; physical LAN P0 still separately open. |

**Sequencing:** WS-09A → 09B → 09C, and 09D/09E in parallel only once compatibility fixtures are green. Refine default 12–20 after viewport is stable; offer 25/30 before 40/50; 75/100 is an opt-in experimental/lab milestone, not a release commitment. WS-09G can proceed as a separate UI-only branch with source-matched testing. WS-08 remains a distinct later release gate.

## 4. Proposed size and challenge design

| Size | Working name | Near-term role |
| --- | --- | --- |
| 12×12 | Short | Preserve existing |
| 15×15 | Classic | Preserve existing |
| 20×20 | Extended | Preserve existing, repair small-letters/zoom feel |
| 25×25 | Large | First new increment / versioned generator |
| 30×30 | Giant | First serious long-session target |
| 40×40 | Marathon | Performance/UX lab |
| 50×50 | Colossal | Performance/UX lab |
| 75×75 | Extreme | Optional long-term |
| 100×100 | Atlas / Marathon | Experimental continuous board, ~10,000 cells |

Challenge is independent from area (Relaxed: constrained directions; Classic: some reverse/diagonal; Expert: eight directions/crossings). No arbitrary words-per-grid formula yet: benchmark fun, density and meaningful words rather than committing untested 100×100 counts. Larger boards should not take away readable 12–20 boards.

## 5. Touch and layout interaction contract

- Square board **visual** with outer line inset enough that all edge/corner glyphs stay fully visible and selected. Rounded **menus/buttons** remain fine. Do not force every cell on screen at once.
- Start with 16–20dp comfortable portrait side whitespace, use responsive minima/maxima and device/safe-area insets; too-small letter size overrides rigid margin. Handle portrait and landscape.
- 1 finger trace/select; 2 fingers zoom and pan; explicit Fit, +/−, two-tap endpoint alternative; optional move/navigate control for people unable to pinch; do not conflate board navigation with guesses or auto-complete.
- At >20 boards, consider an overview navigator with current viewport and optional region labels; regions do not affect puzzle definition or allow score farming. Test hit transforms at zoom/pan near boundaries.
- Word list: show remaining/complete clearly, reduce page-turn burden (current 24 words requires six pages), allow optional scroll/search/filter, retain position; don't cover or shrink board unnecessarily.
- Preserve meaningful active-run feedback, progress and hint choices. A landscape or large-font screen cannot hide navigation or selection.

## 6. Family tester backup / update acceptance matrix

1. Identify **actual installed APK versionCode, app label/ID and signer** from app About/version or APK when possible. Never guess older version from memory. Determine whether Settings → Export backup exists on her APK; test writing the JSON to Documents/Drive **without uninstalling**. Keep a second copy and verify accessible/readable bytes.
2. If ≥v0.5.9 and same pinned QA test signer, prefer **install over existing** with incremented code and then compare stored content. If ≤v0.5.8/other signer, no install-over assumption: **backup/export first; verify backup is importable in a separate test installation before uninstall**. If export is absent, PAUSE and design a nondestructive migration path; no uninstall suggestion.
3. Run a historical signed APK → new executable fixture with a partly completed puzzle (actual foundIds, real hint levels), saved elapsed time, multiple Daily records, profile/font/achievements, Race/Duel journal; compare puzzle rows and target positions, not just JSON bytes or totals.
4. Separately run backup → clean install → restore → compare all preserved fields. Old backups lacking new optional fields remain accepted. Missing local preferences acknowledged; add explicit optional backup representation or clearly document them.
5. Failure paths: corrupt/truncated JSON, unsupported future schema, interrupted write, missing assets, low space, duplicate journals, incompatible signature. Fail **closed**; never present a fabricated reset as recovered progress.
6. A test-green APK is **not** permission to replace a family member's real install. Owner-led migration only when the version and backup path are confirmed. Keep rollback docs and no production key in repo.

## 7. Current-implementation audit and research

- Current v2 generator binds layouts to enum ordinals, deterministic seed and mutable hand-edited thematic lists; **changing them in place would break old seeds**. Its word packs currently hold only about 36 words per theme and cannot feed hundreds of unique Marathon targets without new content.
- Current renderer offers Fit, pinch/zoom, two-tap alternative and zoom buttons. Its fixed-height gameplay layout and word-list paging can become unusable as density grows; 100×100 needs viewport-first rendering/interaction and careful profiling.
- Android docs: [installed-app update certificate/package/version rules](https://developer.android.com/google/play/app-updates), [Compose cached custom drawing](https://developer.android.com/develop/ui/compose/graphics/draw/modifiers), [Room migrations and tests](https://developer.android.com/training/data-storage/room/migrating-db-versions). These guide design and testing, but **do not prove** performance or compatibility on our app.
- Confirm goldens against a known executable/version, not re-generated expectations from changed code. Keep generated-content snapshots and a version map in source control.

## 8. QA / owner acceptance gates

**Automation per bounded PR:** JVM/core deterministic fixtures, Android API35 instrumentation, source-matched stable-signed install-over, save-byte/decoded-object comparisons, package ID/cert/monotonic versionCode, limited font/responsive test matrix. Only declare PASS after run completion and logs; preserve full artifact/checkpoint SHA.

**Human solo/visual when requested:** small and dense board screenshots; select edge/corner/backward/diagonal words, pinch/drag, 2-tap fallback, page margin visual comfort; resume after leaving/force stop; change settings pre-game; backup export; legacy install version. No request for two-phone QA until owner volunteers.

**Explicitly not yet accepted:** real Wi-Fi two-phone marking (#53), Play app signing, production publication, 75/100 generation feasibility, 200% font/TalkBack hardware acceptance.

## 9. Immediate first slice (WS-09A1, implementation)

- Establish a compatibility test-only checkpoint of historical 8×8 v1, legacy v2, 12×12/15×15/20×20 seeds, real complete board rows and word placement coordinates; capture once from v0.8.5 executable. Mark v1/v2 source dictionaries and ordering frozen.
- Add Android backup/import save fixtures with partial found word IDs and hints; test validation does not alter them, and fail-closed corrupt handling remains intact.
- No new size, save schema, DB migration, release signer, APK update, or destructive behavior in first slice. Use draft PR, standard CI, and only trigger emulator if needed for added Android instrumentation. Move forward to WS-09B after source-matched evidence.

## References in project

- [Development Roadmap](DEVELOPMENT_ROADMAP.md)
- [START_HERE](START_HERE.md)
- [WS-07 Research and UX specification](WS07_PLAYTEST_RESEARCH_AND_UX_SPEC_2026-11-07.md)
- [Stable QA signing and older-test migration](WS05J_STABLE_QA_UPDATES_2026-11-07.md)
- [Physical touch acceptance](WS07A_REAL_PHONE_TOUCH_ACCEPTANCE_2026-11-07.md)
