# GridMates: Word Search — Development Roadmap

**Baseline:** November 5, 2026

**WS-03 Solo beta:** [Draft PR #7](https://github.com/PlayerEden/GridMates-Word-Search/pull/7) and [issue #8](https://github.com/PlayerEden/GridMates-Word-Search/issues/8) contain local save/resume, completion records, hint ladder and manual export/import. The [phone test playbook](https://github.com/PlayerEden/GridMates-Word-Search/blob/feature/ws03-solo-save-results-2026-11-05/docs/WS03_SOLO_BETA_PLAYTEST.md) is the acceptance checklist. Build source QA is underway; no device acceptance or seamless update guarantee yet.

**WS-02 source QA:** [Draft PR #4](https://github.com/PlayerEden/GridMates-Word-Search/pull/4), [CI run #42138730965](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42138730965), [build and physical playtest receipt](https://github.com/PlayerEden/GridMates-Word-Search/blob/feature/ws02-word-engine-touch-prototype-2026-11-05/docs/WS02_FIRST_PLAYABLE_QA_2026-11-05.md). Deterministic generator and native touch-grid prototype exist **on a stacked draft branch**. No saved game or physical UX approval yet.

**Latest WS-01 evidence:** [Draft PR #3](https://github.com/PlayerEden/GridMates-Word-Search/pull/3); [successful build and package-ID verification](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42131133443). Android UI test execution and actual phone installation remain open. A native shell exists on the WS-01 branch, not yet on `main`.  
**State:** WS-01 and WS-02 gameplay prototype are CI green on draft branches; physical testing and release remain open. WS-02B refinement is now planned.  
**Master specification:** [MASTER_GAME_DESIGN.md](MASTER_GAME_DESIGN.md)  
**Quick handoff:** [START_HERE.md](START_HERE.md)

## How to use this roadmap

A milestone is done only when its acceptance evidence exists in **this** repository. A passing GitHub check is not a substitute for actual device play when gameplay/networking is involved. Use bounded branches and PRs, documented tests, and a known-green checkpoint after each major phase. Do not bulk-port Sudoku or break either app's save/release identity.

**Statuses:** COMPLETE (evidence linked), NEXT, PLANNED, FUTURE/DECISION, BLOCKED (reason recorded).

## Milestones

| ID | Stage | Status | Core deliverable | Acceptance gate |
| --- | --- | --- | --- | --- |
| **WS-00** | Master design / repo setup | **COMPLETE (docs only)** | Master spec, this roadmap, readme, session handoff | Documents committed and linked on default branch |
| **WS-01** | Native app and safe reuse | **IN PROGRESS — CI GREEN; DEVICE QA OPEN** | Independent Kotlin/Compose app skeleton, GridMates UI, separate Android identity, starter asset audit | Clean build, basic navigation captures, distinct package from Sudoku |
| **WS-02** | Puzzle engine and touch prototype | **CI GREEN; PHONE QA OPEN** | Deterministic word placement/validation, word-list content, drag selection, basic game loop | Unit tests + real input/UI smoke |
| **WS-02B** | Input feel, marker highlights, compact play layout | CI GREEN; PHONE QA OPEN | Snapping, ink-like highlight, haptics, word list near grid, art pipeline | Automated checks + real-device drag/visual acceptance |
| **WS-03** | Complete Solo experience | IN PROGRESS — SAVE/RECORDS CODE, PHONE QA OPEN | Difficulty/theme selection, resume, hints, results, player-friendly interactions | Save/restart, narrow-phone, long-session, accessibility checks |
| **WS-04** | Daily, profile, collections | PLANNED | Dated puzzle/history, local stats/achievements, original portraits and settings | Upgrade tests, no duplicate Daily rewards, asset/hash checks |
| **WS-05** | Nearby Co-op | PLANNED | Open Nearby host/join, optional code, shared word claims, chat/presence, reconnect | Two actual Android phones + protocol/failure tests |
| **WS-06** | Both Versus modes | PLANNED | Shared Board Race and Speed Duel, separate records, fair completion logic | Two-phone races, same-word collisions, near-ties and disconnect QA |
| **WS-07** | Content and UX polish | PLANNED | More themes, hint refinements, device-consistent fonts, profile/collection polish; optional sharing and rewards if approved | Physical UX/accessibility/art/content quality audit |
| **WS-08** | Beta, launch, monetization | PLANNED | Production signing/build, privacy/store preparation, optional ads and Remove Ads after clean beta | Play test eligibility, AAB, real-device signoff, policy disclosures |

Milestone sequence can be rearranged for risk reduction, but **no stage is marked passed without evidence**. Do not promise an app release date or skip compatibility gates just because automation is green.

---

## WS-01 — Start here: a native GridMates sibling, safely

**Goal:** an installable blank-but-real Word Search application without touching Sudoku.

Work:
1. Survey the latest Sudoku branch/source and reference manifests. Latest relevant reference as of this document: [draft PR #28](https://github.com/PlayerEden/SudokuTogether/pull/28), branch feat/gridmates-nearby-animals. Its feature branch includes the accepted Woodland assets and easiest-join UX. Default main is not its Android app source.
2. Record exact source commit and file-by-file reuse/replace/reject inventory. Do not copy Sudoku project wholesale without checking package IDs, saves, assets, font licenses, network namespace, and signing references.
3. Bootstrap a Kotlin/Jetpack Compose Android project under this repo, with original title and launcher identity, a distinct proposed package such as com.playereden.gridmates.wordsearch (confirm before Play publication), and separately installable QA variant.
4. Port only generic look-and-feel/navigation patterns and a minimal theme: paper/forest/ink/gold plus a **tentative** Word Search accent.
5. Create Home, Puzzle placeholder, Records, Collection, Settings and How to Play navigation structures; no fake functional controls.
6. Stand up manual build/unit/instrumentation workflows; keep release secrets out of GitHub. Verify first sideload install does not replace Sudoku.

**Acceptance:**
- Gradle build/lint/unit/instrumentation compile pass (with run URLs).
- Installable Word Search QA APK with verified package/label/version; Sudoku remains installed.
- Device/emulator captures at narrow and standard sizes; no broken navigation/200% text dead end.
- README/handoff and rollback SHA updated.
- No claims of playable words, LAN, stores or ads.

## WS-02 — A reliable word-search engine

**Goal:** something genuinely fun to drag a finger across.

Work:
1. Define WordSearchPuzzle, TargetWord, FoundClaim and engine version with stable identifiers; keep core model Android-independent.
2. Curate a tiny initial word list with source/license notes and appropriate theme(s).
3. Implement deterministic seeded placement, grid fill, target/path validation, all eight directions, ambiguity checks and bounded fallback.
4. Implement a responsive Compose grid with accurate straight-line dragging, cancel state, reverse selection and found-word visual treatment.
5. Add visual/manual test scenes for overlap, diagonal path, short/long words, near-edge starts and letter density. Check effective hit areas on different devices.
6. Record benchmarks and generator error/collision cases; no content infinite loops.

**Acceptance:** reproducible seed puzzles; every generated target has a validated legal path; accidental duplicate target paths are rejected or handled by design; false paths never score; real touch gesture smoke passes. Save polished visuals and QA evidence.

## WS-02B — Input, Marker, Layout, and Art Pipeline

Following the owner's first real-phone test, refine unsatisfying drag gestures and oversized layout **before WS-03 saving**. See [WS-02B design and incremental art plan](https://github.com/PlayerEden/GridMates-Word-Search/blob/feature/ws02b-touch-highlights-layout-2026-11-05/docs/WS02B_INPUT_HIGHLIGHT_ART_PLAN.md) and [phone UX issue #5](https://github.com/PlayerEden/GridMates-Word-Search/issues/5).

Work: eight-direction snapping, finger tolerance, haptic/sound hooks, procedural marker-ribbon highlights, compact in-game header with theme/difficulty in a settings dialog, scrollable targets beside/below the visible board, brand palette and real iconography. Generated artwork is introduced incrementally through a reviewed asset manifest, **not** as a replacement for the original Sudoku Woodland portraits. Real-phone touch and layout QA is required, not just a green Android build.

---

## WS-03 — Real Solo game

**Goal:** full offline Word Search without relying on a second player.

Work:
1. Quick Play and theme/difficulty selection with safe defaults; update for prior progress.
2. Pause/resume, accurate active time, found-word storage, durable completion.
3. Optional hints with tier/usage recording, assist flag; distinguish retry/cancel from a paid hint.
4. Results and review screen with honest stats, next puzzle, Home.
5. Helpful Help screen and user-friendly failure/loading/retry states.
6. Content calibration and readability audits; do not make difficulty depend solely on microscopic grid size.

**Acceptance:** airplane mode works; installed app survives background/kill/restart without losing or mutating board; assisted/unassisted stats do not fabricate scores; narrow/landscape/200%-font device paths pass.

## WS-04 — Daily + identity that feels like GridMates

**Goal:** familiar, rewarding long-term use without an account.

Work:
1. Word Search date seed/version and local calendar policy; idempotent completed-date record and saved Daily History; prevent silent deletion of unfinished dates.
2. Local profile name, stats, achievements and accessible settings patterns.
3. Copy the *actual approved original* 12 Woodland portrait resources and source/provenance manifest from Sudoku v0.11C branch; preserve stable asset IDs. Do not send portrait binaries over LAN.
4. Add grouped collection browsing and correct locked/owned/equipped states; use separate Word Search-local unlock history initially.
5. Bundle agreed/licensed typefaces rather than relying solely on device OEM font variants if implementing font cosmetics.
6. Keep daily reward currency/weekly store as an **explicit decision**, not an unplanned feature.

**Acceptance:** Daily remains stable across restart; old dates retained under documented limits; no replay/reconnect double award; no missing asset crash; original portraits appear at small sizes on two different phones; saved profile survives actual QA app upgrade.

## WS-05 — Nearby Co-op

**Goal:** authentic same-Wi-Fi partner play.

Work:
1. Port generic NSD discovery, socket framing, admissions/rejoin patterns from Sudoku with **Word Search-specific** protocol and service identity.
2. Nearby list is primary Join experience; host's **Require a Code** is opt-in, persists for future rooms, and protects resumed legacy/protected rooms.
3. Authoritative room state with persisted validated claims, dedupe and revisions; both phones see one board and one confirmed found-word list.
4. Player/partner portraits and optional chat/temporary gesture presence; keep header compact and large-text safe.
5. Error/reconnect lifecycle tests; never lose prior valid claims or reassign a reserved seat without an explicit action.

**Acceptance:** test with two physical Android phones on same Wi-Fi; host, nearby join, claim from each side, simultaneous same word, completion, host/guest pause/restart, optional-code and reconnect paths. Automated protocol tests complement but do not replace this gate.

## WS-06 — Both Versus games

**Goal:** two distinct fun competitions, not duplicate modes with different labels.

**Shared Board Race**
- Public shared board/list; one accepted owner per word, 1 word = 1 point initially, tie if totals equal.
- All targets claimed ends the match (ordinary timer does not unexpectedly end it).
- Validate host and guest operations through the same authoritative queue; record win/loss/tie.

**Speed Duel**
- Identical start puzzle and coordinated ready/start; each has private claim progress.
- Opponent sees only progress/presence, never the other player's exact hidden found-path state.
- First verified completion wins, with a documented near-simultaneous tie/disconnect policy finalized after QA.

**Acceptance:** two-device same-word race and own-goal prevention; duplicate/replayed claim rejects; one winner/tie state saved identically on both phones; real disconnect, app background, reconnect and near-finish scenarios tested. Distinct per-mode records survive upgrades.

## WS-07 — Delight without clutter

Examples to assess after real play:
- More curated themed packs and responsive hard-board zoom.
- Original Word Search marker/highlighter cosmetics and consistent cross-device bundled font selection.
- Ocean and Bug portrait pilot packs if the common GridMates art direction is approved.
- Optional Co-op Split Word Lists, additional hint UX, and optional Share Result images via Android Sharesheet.
- Daily milestone rewards or Puzzle Stamps only after explicit ownership/economy design, no FOMO.
- Contrast, reduced motion, TalkBack, keyboard, orientation and frame/touch QA.

**Acceptance:** collect actual user playtest feedback first; prioritize defects that block the puzzle; ensure optional polish has no regression to thumb reach, saves, multiplayer or APK size.

## WS-08 — Beta and release readiness

- Review app/name/logo and content/licensing/trademark questions before promotion.
- Keep QA/production IDs distinct and permanent; establish secure release signing and app update continuity.
- Verify current Play Console eligibility/test-track requirements and Android target API at submission time. Produce verified AAB, store assets, truthful data safety/privacy declarations and installer migration tests.
- Free install, rare between-puzzle interstitials and an optional one-time Remove Ads purchase are the GridMates business direction, **not part of the first technical playable**. Test consent and Billing if implemented.
- Finish full two-phone multiplayer acceptance, SDK/runtime permission behavior, lifecycle/rotation/accessibility/large-text/device-family checks.
- Preserve documented source checksums, release/version notes and rollback paths.

**Acceptance:** no Play publication, revenue claim or “ready for everyone” messaging without all relevant gates checked and separately approved.

---

## Required QA evidence template

At the end of each stage, document:
- **Milestone / branch / exact commit SHA**
- **User-visible behavior changed**
- **What was reused from Sudoku (exact branch/commit, licenses/asset manifest)**
- **What is proposed vs implemented**
- **Automated tests (run URL, passed/failed/skipped)**
- **Android emulator tests and screenshot review**
- **Real phone verification, devices/build IDs, if performed**
- **APK/AAB filename, install identity and SHA-256 if built**
- **Save upgrade and LAN protocol compatibility**
- **Known failures, blockers and next practical action**
- **Rollback checkpoint/branch**

Don't present an expiring GitHub Actions artifact as permanent backup. Keep the code and documentation committed, and preserve checkpoints.

## Review points / open design gates

Before WS-02: actual launch theme/difficulty sample, board target counts, content licensing method, gesture accessible fallback.

Before WS-05: choose initial Co-op hint visibility and precise chat policy.

Before WS-06: simultaneous claim fairness/latency test method and Speed Duel tie/disconnect state machine.

Before WS-07: sharing priority, reward economy or no currency, any cross-game identity ambitions.

Before WS-08: exact Play listing, privacy, test eligibility, signed artifact, ad behavior and IAP scope.

## Historical notes

- **November 5, 2026:** New separate Word Search repo chosen; both Shared Board Race and Speed Duel agreed for Versus; existing Sudoku portraits/systems are references, not proof of a built cross-game library; master design established.
- Latest Sudoku accepted baseline relevant to reuse: v0.11C draft PR #28, with owner-reported basic two-phone success on preceding v0.11B. A renewed complete physical LAN checklist remains open.

## Tracked starting issues

- [#1 — WS-01 Native Android foundation and Sudoku reuse audit](https://github.com/PlayerEden/GridMates-Word-Search/issues/1)
- [#2 — WS-02 Word-search engine and tactile input prototype](https://github.com/PlayerEden/GridMates-Word-Search/issues/2)

Later milestone issues should be opened as work approaches, rather than creating a large unattended backlog.
