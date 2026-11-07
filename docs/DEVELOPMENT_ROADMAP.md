# GridMates: Word Search — Development Roadmap

**Baseline:** November 5, 2026  
**State (November 7, 2026):** **WS-07E v0.8.1 automated PASS, 90/90 Android API35 tests** plus QA-signed v0.8.0→v0.8.1 saved-progress install-over; [exact-source receipt](WS07E_BUILD_QA_2026-11-07.md), [draft PR #58](https://github.com/PlayerEden/GridMates-Word-Search/pull/58). Physical two-phone Co-op P0 remains OPEN and owner testing deferred. Stacked drafts not merged to `main`, no production release.

**Master specification:** [MASTER_GAME_DESIGN.md](MASTER_GAME_DESIGN.md)  
**Quick handoff:** [START_HERE.md](START_HERE.md)

## How to use this roadmap

A milestone is done only when its acceptance evidence exists in **this** repository. A passing GitHub check is not a substitute for actual device play when gameplay/networking is involved. Use bounded branches and PRs, documented tests, and a known-green checkpoint after each major phase. Do not bulk-port Sudoku or break either app's save/release identity.

**Statuses:** COMPLETE (evidence linked), NEXT, PLANNED, FUTURE/DECISION, BLOCKED (reason recorded).

## Milestones

| ID | Stage | Status | Core deliverable | Acceptance gate |
| --- | --- | --- | --- | --- |
| **WS-00** | Master design / repo setup | **COMPLETE (docs only)** | Master spec, this roadmap, readme, session handoff | Documents committed and linked on default branch |
| **WS-01** | Native app and safe reuse | **NEXT** | Independent Kotlin/Compose app skeleton, GridMates UI, separate Android identity, starter asset audit | Clean build, basic navigation captures, distinct package from Sudoku |
| **WS-02** | Puzzle engine and touch prototype | PLANNED | Deterministic word placement/validation, word-list content, drag selection, basic game loop | Unit tests + real input/UI smoke |
| **WS-03** | Complete Solo experience | PLANNED | Difficulty/theme selection, resume, hints, results, player-friendly interactions | Save/restart, narrow-phone, long-session, accessibility checks |
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


## WS-05J — Stable QA updates and saved-progress protection (November 7, 2026)

- Pin the **public test-only**, non-production QA signer and retain the existing standalone QA package ID while moving to versionCode 21 / v0.5.9.
- Add same-signer, versionCode 20→21 **adb install -r** emulator upgrade test and byte-compare the app's private profile file before/after. Keep a source-matched artifact and evidence for this milestone.
- Improve restore reliability by reverting in-memory import when atomic disk write fails.
- **Earlier v0.5.8 and older debug APKs need a one-time manual JSON backup → uninstall old QA Word Search → install v0.5.9 → restore** because old runner signing identities differ. No silent uninstall or data clearing.
- Preserve all existing Solo, Daily, profile and nearby LAN behavior. Physical old-build export/import, two-phone LAN and actual handset in-place update remain deferred.
- Documentation: [WS-05J stable QA upgrade and migration](WS05J_STABLE_QA_UPDATES_2026-11-07.md).



## WS-06A — Shared Board Race (November 7, 2026)

- **Automated PASS on Android (49 API35 tests; physical two-phone QA OPEN):** One shared board, first host-accepted claim owns each word and earns one point. All words found ends match; higher score wins, equal score ties; no idle timer.
- Nearby host UI selects Co-op or Shared Board Race with existing optional room-code behavior; guest discovery shows mode, both phones show real-time points, connection state and final personalized outcome. Co-op remains a separate default and has no winner.
- Host authoritative mode in snapshots; client mode handshake guarded; LAN protocol upgraded from v4 to v5. Older APKs must update to join v5 rooms; stable QA signer stays unchanged and versionCode rises 21→22 (v0.6.0).
- [WS-06A QA plan](WS06A_SHARED_BOARD_RACE_QA_PLAN_2026-11-07.md) and [verified build receipt](WS06A_BUILD_QA_2026-11-07.md): JVM race decision tests, Android codec and mode UI tests, 49 green emulator tests, exact v0.5.9→v0.6.0 signed update preserving private save. Real two-phone gates still open.
- **Later:** WS-06B persistent Race records with idempotent result claims; WS-06C Speed Duel private boards/ready/start. No fabricated win statistics or claims of two-phone hardware acceptance.


## WS-06B — Local Versus journal, comparisons and protected updates (November 7, 2026)

- **Automated PASS:** [standard Android CI](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42348095399), [full API35 emulator 52/52 tests and original APK migration](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42348032724). Real two-phone acceptance still OPEN.
- Completed, verified Shared Board Race result recorded at most once per room, with player-relative win/loss/tie and local historical scores. Co-op and unfinished Race never count.
- User-facing result comparison shows points, winner/tie, difference and expandable word ownership. Stats adds a distinct Nearby Versus section; Solo and Daily totals stay separate.
- Optional schema-4 races array in existing atomic profile and JSON backup, with preview/confirmed restore, invalid duplicate rejection, older saves compatible, no silent pruning. 1,000-match bounded journal.
- Stable test-only signing, versionCode 23 / v0.6.1, unchanged LAN v5. Actual old v0.6.0 code22 APK updated in place, preserving all previous profile fields. [Verified build receipt](WS06B_BUILD_QA_2026-11-07.md).
- **Next WS-06C:** Speed Duel needs truly separate per-seat private board, ready/start, host-authoritative finish/tie logic. Actual two-phone acceptance deferred and all drafts unmerged.


## WS-06C — Independent Speed Duel, private LAN protocol and safe update (November 7, 2026)

- **Automated PASS:** [regular Android CI #42350347969](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42350347969) and [full API35 emulator #42350506917](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42350506917), **57/57 Android tests, no failures/skips**. Real owner two-phone LAN QA still OPEN.
- Third distinct Nearby mode **Speed Duel**: identical private boards, hide puzzle/words before both press Ready, GO when host accepts both, independent per-seat discoveries, progress-count-only opponent visibility, host-authoritative first complete board wins (host accepted order breaks near-simultaneous finishes; no arbitrary tie).
- New separate core SpeedDuelSession, recipient-filtered v6 state packets, mode-aware handshake/reconnect. Both old Co-op and Shared Board Race still available, with Race journal unchanged. v5 clients must update to connect to v6 rooms.
- Stable **test-only** signer, Android versionCode24/v0.7.0; the **actual v0.6.1 code23 QA APK** updated in place on emulator while maintaining Solo/Daily, identity and existing race journal entries.
- [Exact-SHA APK/QA/checkpoint](WS06C_BUILD_QA_2026-11-07.md); [design and owner two-phone acceptance plan](WS06C_SPEED_DUEL_QA_PLAN_2026-11-07.md).
- **Next WS-06D:** independent persistent Duel results. Production signing, encryption, anti-cheat and physical two-device testing remain outside this milestone.


## WS-06D — Persistent Speed Duel results and protected app updates (November 7, 2026)

- **Automated PASS:** [standard Android CI](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42351978673) and [API35 actual-update emulator](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42351958926), **60/60 on-device tests**, no failures or skips. Real two-handset acceptance is still OPEN.
- Finished host-confirmed Duel views create one local win/loss per room, independent of Shared Board Race ties and Solo/Daily progress. Room replay or rejoin cannot re-award the result; no opponent private word IDs are stored.
- Distinct Speed Duel Stats with Played/Wins/Losses/Words found and recent scores, plus a player-relative final screen with local save confirmation.
- Optional `duels` array in existing schema-4 AtomicFile and JSON backup, compatible with older profiles and safe import rejection of impossible or duplicate records.
- Stable QA-only signing, **v0.7.1 versionCode25, LAN v6 unchanged**. The **actual signed v0.7.0 code24 APK** was upgraded in place and retained previous Solo/Daily/profile and completed Race entry. [Exact-source build/QA receipt, checksum and rollback checkpoint](WS06D_BUILD_QA_2026-11-07.md).
- **Next WS-06E:** Nearby player-presence/accessibility polish and optional sharing, preserving all work before physical two-phone testing. All ancestor PRs remain draft/unmerged.


## WS-06E — Nearby presence, spoiler-safe sharing and Sudoku design transfer (November 7, 2026)

- **Automated PASS:** [standard CI #42353429194](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42353429194), [API35 full emulator #42353406551](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42353406551), **63/63 Android on-device tests, 0 skips/failures**, actual signed v0.7.1→v0.7.2 install-over retaining both Race and Duel histories.
- Nearby partner **presence card** shows connection/name, authentic contributions, a mode-relevant message, and a stacked layout at narrow width/large fonts. Speed Duel still reveals **counts only** to the opponent, no private words.
- A small optional **Share result** action after real completed Co-op/Race/Duel; competitive outcomes must be saved first. Solo/Daily's existing share text now also has an explicit Preview/Cancel before Android's chooser. Anonymous, spoiler-safe, text-only results with no room/identity/secret data, no new network services.
- Sudoku's latest `development` docs informed this work; copied **design principles only**, not unshipped feature code or unauthorized themes. [Detailed source links, transfer boundaries and QA plan](WS06E_NEARBY_PRESENCE_SHARE_QA_PLAN_2026-11-07.md).
- Version **v0.7.2 versionCode26**, stable **TEST-ONLY** signer, LAN v6 and schema-4 data unchanged. [Exact QA source, checksum, artifact and rollback receipt](WS06E_BUILD_QA_2026-11-07.md).
- **Next WS-06F:** a scoped result-image card design pilot or shared Co-op cosmetic theme policy. Either needs its own implementation review and Android verification; two physical phones and actual share targets remain an open owner QA gate. All drafts remain unmerged.


## WS-06F — 4:5 offline result postcards and safe Android Sharesheet (November 7, 2026)

- **Automated PASS:** [standard Android QA #42358483446](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42358483446) and [real signed historical upgrade + full API35 emulator #42358477314](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42358477314), **67/67 Android tests; no skipped/failed**. Actual real two-phone acceptance remains OPEN.
- Optional **4:5 1080×1350 PNG postcard** created offline using Android Canvas and bundled licensed fonts. A spoiler-free decorative letterless puzzle grid and truthful mode-specific scores; no sharing of names, rooms, chat, actual puzzle answers or private Duel word IDs.
- Preview dialog offers **Share text**, **Share image**, or **Cancel**. Image export uses a temporary private-cache PNG with a scoped Android FileProvider `content://` URI and read-only grant; no gallery permission or backend.
- Stable QA-only signer, versionCode **27 / v0.7.3**, LAN v6 and schema4 unchanged. Real signed v0.7.2 code26 updated in place while preserving Solo, Daily, profile, saved Shared Board Race and Speed Duel results. [Source-matched QA receipt, checksum and rollback](WS06F_BUILD_QA_2026-11-07.md).
- Initial emulator migration run failed when seeding a private save before creating its folder; [corrected second run](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42358477314) passed. No failed prior run is counted as accepted QA.
- **Next WS-06G:** design a real Word Search shared Co-op visual theme policy with curated assets, accessibility fallback and round-stable selection; do not import Sudoku's proposed ranks or ownership model without a matching Word Search catalog. Physical two-phone testing remains owner-deferred; all stacked drafts stay unmerged.


## WS-06G — Free Co-op board looks and host-authoritative round freeze (November 7, 2026)

- **Automated PASS:** [standard Android CI #42360159954](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42360159954), [API35 emulator + actual historical signed update #42360155148](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42360155148): **71/71 on-device Android tests, zero failed/skipped**. Physical owner two-phone acceptance still OPEN.
- Word Search-only curated, baked-in **Classic Paper / Linen Sketch / Blueprint Study** free appearance previews. Co-op host/guest choose locally; first acknowledged guest admission freezes the host-resolved shared look based on technical display priority, independent of which phone is host. Aborted joins do not reserve a look, reconnect cannot switch it.
- Local-only **Use Classic on my phone** readability override affects one board renderer, not LAN state; player's earned **board font**, collection, Solo/Daily, Race and private-board Duel remain unchanged. These are not rare or earned cosmetic tiers.
- Required LAN state `look` field and guest offer raise **protocol v6→v7**. Old v6 peers are incompatible across all Nearby modes; both phones must use v7. No remote assets, ownership transfers, real-world online backend or new save schema.
- Actual stable-signed v0.7.3 code27 QA APK updated in place to v0.7.4 code28, preserving old Solo/Daily/profile and completed Shared Board Race / Speed Duel journals. [Exact source, checksum, emulator QA report and rollback checkpoint](WS06G_BUILD_QA_2026-11-07.md).
- Initial source test compile had a mistaken `JSONObject.remove` return type; corrected and fully retested in the separate green run. Prior failures do not count as accepted gates.
- **Next WS-06H:** whole-game integration and eventual Play-test readiness audit, particularly physical two-handset Wi-Fi/ready/finish/reconnect, room theme agreement, 320dp/200%-text/TalkBack, backups, signed app update and release prerequisites. Keep all stacked drafts unmerged.

## Live development priority — WS-07 refinement arc (November 7, 2026)

**This table supersedes the planning-only WS-07 row above.** No new modes, advertising, currency, collections or cosmetic expansion until actual word marking and readability are proven. The accepted fallback remains v0.7.6 `4250198aff1faaca510fde90569a3148db591f8a` (76/76 emulator tests, signed install-over, real two-phone gameplay not signed off). The latest candidate remains a separate *unmerged* QA-only v0.7.7 / code31 draft. Read [physical evidence + current research](WS07_PLAYTEST_RESEARCH_AND_UX_SPEC_2026-11-07.md).

| Priority / stage | Status | Deliverable | Required proof |
| --- | --- | --- | --- |
| **P0 / WS-07A — Accurate touch & connected Co-op** | **AUTOMATED PASS 78/78; PHYSICAL P0 TEST OPEN** | One-finger tracing, start and end from actual touches, 2-tap selection, inverse transform, second-finger cancel; fixed in-game HUD candidate; clear local/host claim feedback | All pointer-injected host/guest 12/15/20 scenarios green; same-signer upgrade; both real phones can mark multiple valid words and see them on each other |
| **P1 / WS-07B — No-scroll playing surfaces** | **AUTOMATED 80/80 PASS; PHYSICAL VISUAL QA OPEN** | Fixed Solo/Daily/Co-op/Race/Duel gameplay, compact HUD, visible word preview, optional word list/chat/info sheets, simple Home Host/Join, settings outside gameplay | Portrait and compact-height landscape; no board clipped by full-page scroll; every task reachable at 320dp/200% text |
| **P1 / WS-07C — Precision/accessibility** | **AUTOMATED 83/83 PASS; PHYSICAL/TALKBACK OPEN** | Finger-up and slop edge cases, optional 20×20 loupe/crosshair prototype, Fit/zoom, 2-tap plus larger alternative cell navigator, TalkBack/Switch/keyboard path, contrast/haptics | No false claims during pinch/pan; older/novice users select clearly visible words; no 400-cell-only screen reader dead end |
| **P0/P1 / WS-07D — Two-phone multiplayer acceptance** | **HOST RECEIPT AUTOMATION 87/87 PASS; PHYSICAL TWO-PHONE BLOCKED** | Co-op full round, Race simultaneous word arbitration, Duel Ready/GO/private paths, optional room code, discovery/rejoin/Wi-Fi interruption, friend presence, chat, post-match save/comparison | Two physical devices, swap host/guest, complete all three modes, per-mode journals agree, screenshots/video/logs redacted |
| **P1 / WS-07E — Whole-app UX and content** | **FIRST PASS AUTOMATED 90/90 PASS; REMAINING UX + PHYSICAL OPEN** | Home and Nearby first-time onboarding, meaningful hints through last word, difficulty/content review, Daily variety, records/collection/achievements/profile grouping, share previews and cosmetics clarity | Observe novice play, inspect screens for clarity, retain familiar paperback visual identity; no accidental extra monetization |
| **P1 / WS-07F — Durability, accessibility & performance audit** | PLANNED | Large journal and 1/3/5-year Daily stress, malformed-result fail-closed protection, backup size boundary, old→new install-over, low-end frame/memory/ANR, TalkBack, 200% font | Run complete CI/emulator and real-device long-session matrix; export must also import successfully; record exact APK SHA and rollback |
| **P1 / WS-07G — Integration & beta readiness gate** | BLOCKED until A–F verified | Audit stacked PR ancestry, fix docs/source of truth, checkpoint candidate, release QA/owner signoff and eventual private AAB signing/Play tracks | No auto-merge; all tests and physical P0 pass; production keys never use public QA signer, Play requirements verified near submission |

### WS-07A regression detail, source-matched status

- **Confirmed owner report:** two connected phones could not mark words; initial screenshots show auto-cropped zoom, confusing Find/Move selection, very long active Nearby page.
- **Candidate implemented in draft #54:** fit-first zoom, one-/two-finger gesture separation, first-down/final-up handling, fixed active Nearby board, collapsed optional room styling/options, Solo word paging, contextual word and game status. These are **candidate changes**, not accepted behavior.
- **First full [v0.7.7 emulator run #42410802309](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42410802309):** actual signed v0.7.6→v0.7.7 install-over preserved Solo/Daily/identity/Race/Duel; 78 Android tests executed, **76 pass / 2 fail**, specifically finger-swipes on 15×15 and 20×20 returned `INVALID_PATH`.
- **Revised [emulator run #42411678348](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42411678348):** update protection passed again; **76/78 pass**; endpoint diagnostics show an expected 20×20 path `(5,17)→(11,11)` was received as `(6,17)→(12,11)`, and an expected 15×15 `(6,5)→(6,12)` was received as `(6,5)→(7,6)`. **Do not dismiss as a test-fixture problem until actual board bounds/coordinates and direction lock are independently proven.** Correct raw pointer coordinate mapping and direction locking; run target-level tests before rerunning the expensive full emulator suite.
- **Latest standard Android CI #42411683407 passed**, which confirms code compiles but not gesture correctness. No passing full emulator run or accepted real-phone Co-op yet.
- **Exit condition:** all designed gesture tests green at actual board coordinates, no accidental pinch claim, same signer and saved journal retention, then owner repeats on both physical phones. Pin the exact-green source and APK only *after* this.

### Design constraints carried into every sub-milestone

1. **Active play never requires scrolling the whole page.** Reflow or change layout, not shrink words beyond legibility. Histories/settings may scroll, and an explicitly opened word/chat sheet may scroll.
2. **No forced Find/Move mode.** Default 1 finger = select; 2 fingers = zoom/pan; visible Fit/zoom and two-tap alternative. Additional assist for 20×20 subject to user tests. User's target word must be selected deliberately, never auto-guessed.
3. **Ordinary controls aim for ≥48dp** per Android accessibility guidance. The grid is a spatial surface: provide accessible alternatives and test legibility; do not claim all 400 cells individually meet 48dp. Group semantics and support TalkBack, Switch Access, larger font and non-gesture navigation.
4. **Feedback is immediate and true:** accepted/invalid/claimed/unready/disconnected; partner state and contribution are visible without burying the board. Keep Duel answers private and Race host-authoritative.
5. **Preserve app history and independence:** same Word Search QA application ID/cert, no changed schema or network protocol without explicit compatibility/migration; don't affect GridMates Sudoku, don't merge draft PRs or overwrite `main`.
6. **Quality gate cadence:** audit/a11y/backup at each major implementation; tests per PR, full cumulative replay at checkpoint and physical acceptance before calling milestone complete. A build must cite the *executable* SHA, not an unrelated later docs commit.

### Reference and issue map

- [Research evidence and UX specification](WS07_PLAYTEST_RESEARCH_AND_UX_SPEC_2026-11-07.md)
- [WS-07 whole-game audit #52](https://github.com/PlayerEden/GridMates-Word-Search/issues/52)
- [WS-07A physical P0 input/scroll issue #53](https://github.com/PlayerEden/GridMates-Word-Search/issues/53)
- [WS-07A draft implementation PR #54](https://github.com/PlayerEden/GridMates-Word-Search/pull/54)
- [Two-phone acceptance checklist](WS07A_REAL_PHONE_TOUCH_ACCEPTANCE_2026-11-07.md)


### WS-07A verified source-matched automated checkpoint — November 7, 2026

**The historical 76/78 failure evidence above is superseded for the revised executable by [full emulator run #42413940520](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42413940520): 78/78 API35 instrumentation tests PASS, 0 failed, 0 skipped.** The 15×15 host and 20×20 guest injected finger-claim tests now both pass after using actual board dimensions and recomputing whole-drag direction. Standard [CI #42413610368](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42413610368) passed. Exact source `f9e451b1921b004d7cec45bc28501b852e0df942`, APK SHA-256 `0a807b2de2db335f4b723ae5d9d5882ad2df1768481c78d1c63cd484d9950a8f`. Actual stable-signed **v0.7.6→v0.7.7** emulator install-over retained schema4 Solo/Daily/identity/Race/Duel. [Build receipt](WS07A_BUILD_QA_2026-11-07.md) · [verified code checkpoint](https://github.com/PlayerEden/GridMates-Word-Search/tree/checkpoint/ws07a-v077-touch-78-tests-upgrade-green-2026-11-07).

**Hardware gate NOT PASSED:** Two human-operated phones still need to prove both host and guest can find/claim words, pinch/zoom without accidental claims, and complete all three modes. WS-07B–G follow-on user-experience and release gates remain open. The v0.7.6 rollback also remains available.


### WS-07B — compact Home and Nearby entry automated checkpoint (November 7)

[Standard Android CI #42417348617](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42417348617) and [full API35 emulator #42417361001](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42417361001) **PASSED** on the v0.7.8/code32 candidate. **80/80 instrumentation tests, zero failed/skipped**, including newly added **360×720dp first-screen visibility** for Continue Solo, Open Daily, Nearby Host and Join; all previous 78 checks remain passing. A real same-QA-signer v0.7.7/code31→v0.7.8/code32 install-over preserved Solo, Daily, identity and both competitive journals. [Exact executable SHA, APK hash, artifact and rollback](WS07B_BUILD_QA_2026-11-07.md). [Owner visual/physical checklist](WS07B_COMPACT_NAVIGATION_ACCEPTANCE_2026-11-07.md).

**Interpretation:** WS-07B entry-screen automated acceptance achieved, **not** a claim that every settings/history page is scroll-free or actual 200%-text, landscape and physical host/guest gameplay have passed. The earlier physical two-phone unable-to-mark-words report remains an open P0 until the user re-tests. `main` and production signing remain unchanged.


### WS-07C automated checkpoint (November 7, 2026)

**83/83 Android API35 instrumentation tests passed, no failures/skips**; non-drag row/column picker and 20×20 accessibility structure tested, while legacy swipe and data checks remained green. Signed APK **v0.7.8→v0.7.9** install-over retained Solo/Daily/profile/Race/Duel. Exact QA source `113e5e51d44f77c2a4e1bd2dffe3cc0c9b0a8eb6`; APK SHA-256 `95be321feee5ef55abd40c0ac2af51670215e50f993a393fdd6006b362804d43`. [Full receipt](WS07C_BUILD_QA_2026-11-07.md). [Draft PR #56](https://github.com/PlayerEden/GridMates-Word-Search/pull/56) remains unmerged. WS-07D can proceed with nonphysical LAN/host authority tests, but **owner two-phone test and screen reader signoff remain blocked by user preference until ready**. Do not ask for handset testing prematurely.


### WS-07D verified host claim receipt checkpoint (November 7)

[Full API35 QA run #42420714493](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42420714493) **PASS 87/87**, including 4 new claim receipt packet tests, all previous 83 instrumentation checks and a signer-verified in-place **v0.7.9→v0.8.0** upgrade preserving schema4 Solo/Daily/identity/Race/Duel. [Exact source `7edb6cf01437b86767d3696a2a04b9e9f03d9e27`, APK SHA-256 and artifacts](WS07D_BUILD_QA_2026-11-07.md). Guest Co-op/Race/Duel receives *optional* host-validated claim status in v7 state, or an explicit unconfirmed fallback after 9 seconds; no new room/cloud service, saved-data change or hidden Duel path. [Draft PR #57](https://github.com/PlayerEden/GridMates-Word-Search/pull/57), unmerged. **Do not close P0 #53:** two actual phones, TalkBack and owner usability signoff remain unperformed and explicitly deferred. The [v0.7.9 rollback](WS07C_BUILD_QA_2026-11-07.md) remains intact.


### WS-07E first whole-app UX pass (November 7)

[Full API35 run #42424216761](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42424216761) **90/90 PASS (0 skipped/failed)** on v0.8.1/code35. Three new Android tests verify a bounded/paged Daily Journal, already-paid hint recall at exhausted budget, and Options clue review without another charge; previous 87 cases remain green. Real signed v0.8.0→v0.8.1 install-over preserved schema4 Solo/Daily/identity/Race/Duel data. Exact tested code `edcdc08c53d82dccca83ee1b3cf4255c662d08aa`, APK SHA-256 `d7d6707ca96b5afc938cee15acc341a0ae994d5ffce8eda8d029224800de20a0`, [artifact and rollback](WS07E_BUILD_QA_2026-11-07.md), [owner checklist](WS07E_DAILY_CLUES_UX_ACCEPTANCE_2026-11-07.md). **This is only the bounded first WS-07E pass**, not approval of all app screens or actual two-phone play. Physical P0 #53 and large-font/TalkBack owner acceptance remain deferred.
