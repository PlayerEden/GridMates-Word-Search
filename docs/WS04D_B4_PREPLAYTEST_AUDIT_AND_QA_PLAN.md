# WS-04D-B4 — Pre-playtest Engineering Audit and Next Acceptance Gate

**Date:** November 6, 2026  
**Purpose:** Finish a meaningful slice of gameplay before asking the owner to install another QA APK. This is a **developer audit**, not an assertion of finished-game quality or full independent security testing.  
**Active work:** [Draft PR #28](https://github.com/PlayerEden/GridMates-Word-Search/pull/28), stacked on [v0.4.6 PR #27](https://github.com/PlayerEden/GridMates-Word-Search/pull/27).  
**Established product goal:** Reach/exceed GridMates: Sudoku's comfortable, complete mobile experience before adding another portrait collection or starting full multiplayer. The owner's QA saves are explicitly disposable during development; do not make manual backup/export a prerequisite for their next test. Continue to protect schema/legacy compatibility in engineering.

## Pre-audit state (verified from current source and owner's screenshots)

- Actual **v0.4.5** owner screenshots show **true whole-board zoom** (grid and glyphs enlarge together), clearly labeled Find/Move, a two-row header and eight or more visible word entries at typical portrait size. This materially improves on v0.4.4.
- **20×20 at 100%** still needed final-row/corner verification; code fix was added in v0.4.6 but the owner has not tested it. **Do not claim that clipping is fixed on hardware.**
- Version **v0.4.6** has a bounded 2/3/4-step hint policy and a permanent 2-step Daily limit, enforced in core with unit tests; real button/resume QA remains open.
- Old Solo and Daily generators are immutable v1/v2 seeded contracts; the app uses separate QA package `com.playereden.gridmates.wordsearch.qa`; original 12 Woodland portraits are imported from a pinned approved Sudoku source.
- Google Play production release keys, stable QA debug signing and owner two-phone LAN QA are separate later tasks; none are claimed complete.

## Audit findings and work scope

| Priority | Finding | B4 change | Proof/gate |
| --- | --- | --- | --- |
| **High** | Word finds/hints repeatedly called seeded puzzle regeneration, potentially affecting responsiveness on 20×20 boards | Bounded **six-entry immutable puzzle-ID cache** in core; version/seed/size/challenge preserved | Core tests assert stable grid identity for repeated reads and distinct seed isolation; monitor on-device latency |
| **High** | 20×20 long words require repeated zoom-outs; Move mode lacked pinch navigation | Move mode accepts **one-finger pan and two-finger pinch/zoom** via Compose `detectTransformGestures` and anchors zoom to gesture centroid; Find stays separate | Math tests for panning limits/focal preservation; actual Android multi-touch test pending |
| **High** | Zooming back to 100% could leave a fractional or negative-zero offset, causing test and potential viewport state mismatch | Exact pan X/Y **zero** whenever board is fitted at 100%; zoom percentage doubles as **Fit full board** control | Core regression tests; Android visual fit check pending |
| **High** | Completed game had only a minimal inline message, no intentional results/review experience | Read-only completion panel: count, time, **Assisted/Unassisted**, Review Board ↔ Results, Records/Home, Play Another for Solo | Compose UI runtime smoke plus real device acceptance |
| **Medium** | Players lose target context while scrolling large word list | Pinned target-list heading, Remaining filter with selected state; scroll only the target chips | Android small/large portrait and landscape checks |
| **Medium** | Success feedback is plain and easy to miss | Brief **✓ Found [WORD] · N of X** message with restrained original haptics/optional click | User phone feedback and cancellation behavior |
| **Medium** | Feedback preferences reset after leaving the puzzle screen | Local preferences persist haptics and sound across sessions; not imported into immutable puzzle save | Android store-restart instrumentation test |
| **High (QA)** | Previously written `ShellSmokeTest` expected long-removed prototype strings and failed real emulator tests | Rebase assertions onto current Home, Collection, Solo, Hint, Options and achievements | API 35 emulator execution must show PASS |
| **High (QA)** | Android instrumentation was only **compiled** in normal CI | Branch-scoped one-time KVM Android emulator workflow executes `:app:connectedDebugAndroidTest` | Actual device/emulator results and reports; no fabricated pass |
| **Low** | Old Android QA APK debug signing may change | Keep issue #11 open; development data explicitly disposable, preserve user-facing production direction | Stable in-place upgrade before wider beta, not a blocker for disposable alpha QA |

**Developer workflow:** code lives only in stacked **draft PR #28**; never silently merge an unapproved previous stack or change Sudoku's separate package.

## Audit implementation details

1. `BoardViewportTransform.zoomedAt` keeps the same content point beneath pinch centroid; zoom limits 100–250%, and panning cannot exceed board extent.
2. `SoloProgressRules.puzzle` uses `@Synchronized` and max-six LRU-style map keyed by versioned immutable puzzle ID; old English v1/v2 results remain exactly reconstructible, without changes to generator algorithms or content.
3. `WordSearchScreen` has a completion panel that **does not invent scores**. It uses actual saved result seconds/hints or Daily run values and preserves board review. Button layout still requires small-phone testing.
4. `WordList` title stays present and a toggle shows Remaining or all words; tapped targets are **not** used as free location hints.
5. Preferences use Android private `SharedPreferences` for feedback only, with independent namespacing; no new permission or external backend.
6. The one-time emulator workflow uses Android API 35 and physical Android runtime instrumentation test classes. It is branch-scoped to avoid a permanent extra job on every PR.

## Engineering test gates — do not mark green without matching runs

- [ ] Latest B4 Kotlin **core JVM tests** pass, including 20×20 cache identity, focal pinch and exact-fit pan.
- [ ] App unit, lint, Android instrumented-test **source compilation**, APK assembly and exact QA identity/version are green.
- [ ] All **12 original** Woodland PNGs are included and checksum-verified.
- [ ] New API35 Android emulator workflow actually **executes** instrumentation; look for **0 failures**, not only build success.
- [ ] The updated shell smoke covers current Home → Solo → Options; collection shows real original portraits/achievements.
- [ ] UI completion test starts from a fully solved deterministic board and asserts Summary → Review → Results without losing a recorded completion.
- [ ] Feedback preference test confirms device persistence independent of seed/run backup.
- [ ] Record exact source SHA, GitHub Actions run IDs, APK SHA256/size, a known-good rollback checkpoint and any failures/repairs. Keep PR draft.

## Owner's single consolidated next phone test

Use the latest **B4 QA APK**, *after* the above checks. No requirement to export previous test-only progress.

1. 15×15 at 100%/145%/250%: Find mode traces accurately; Move mode pans; pinch-to-zoom works **in Move mode**, does not claim letters; tapping percentage fits the board.
2. 20×20 at 100%: confirm the bottom row is complete; switch to ~185% and pan to **all four corners**; find an actual diagonal and a long edge-crossing word (zoom out or endpoint tap where necessary).
3. Use three Standard hints, verify count 3→2→1→0, then restart the app; 20×20 has four and Daily has two.
4. Finish at least one Solo puzzle: expected friendly `Puzzle complete!` panel, truthful elapsed time, hints flagged Assisted if used; **Review completed board** and **Results** return without making a new puzzle. Try Play Another.
5. Toggle sound/haptics, exit and reopen: preference should stay set.
6. Open Daily, Collection, Records and Settings; check long titles, font scale, landscape, board/word-list balance and general app feel. Send screenshots of any areas that look strange.

## After B4 — what is next

- **WS-04D-B5:** follow owner actual touch/UI feedback; improve long-word cross-viewport interaction, smooth feedback, portrait/landscape typography and completion polish. Do not add monetization or cosmetic packs instead of fixing gameplay.
- **Pre-WS-05 gate:** stable QA/release signing for non-disposable testers, emulator/device execution, accessibility and data safety; then prototype **Nearby Co-op** with same-Wi-Fi easy host/join and two-phone acceptance.
- **Later WS-06:** Shared Board Race and Speed Duel, once basic nearby co-op/reconnect works.
- **Deferred research:** massive curated word library, multilingual/Spanish generator and UI support, Ocean/Bugs collections, puzzle-book chapters, optional share/results and nonintrusive ads.

**Current QA honesty:** Source changes are implemented on draft PR #28, but any run-level status or user physical verdict must be recorded separately, with evidence.
