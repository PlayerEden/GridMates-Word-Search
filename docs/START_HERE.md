# START HERE — GridMates: Word Search

**Latest green automated checkpoint (November 7 ET / November 8 UTC): WS-09A2 v0.8.7/code41, 97/97 Android API35 tests PASS**, real signed v0.8.6→v0.8.7 **partially solved Solo + Daily** upgrade preserved target IDs, hints, time, identity and Race/Duel. [Source-matched build receipt](WS09A2_BUILD_QA_2026-11-07.md) · [Draft PR #66](https://github.com/PlayerEden/GridMates-Word-Search/pull/66) · exact tested source `2bd6b6a005d361ebc3304a5d46044b83b7b0c283` · stable QA test-only signing. **Next WS-09B multiple unfinished sessions;** old family APK version/backup OPEN and physical two-phone P0 #53 still deferred.

> **LATEST HANDOFF (November 7, 2026):** [WS-09 Foundations and giant-board roadmap](WS09_FOUNDATIONS_GAMEPLAY_SAVE_MARATHON_2026-11-07.md) is current. WS-09A1 v0.8.6 [96/96](WS09A1_BUILD_QA_2026-11-07.md) and WS-09A2 v0.8.7 [97/97 with frozen puzzle goldens and real partial-progress install-over](WS09A2_BUILD_QA_2026-11-07.md) are automated verified. WS-09B (multiple resumable sessions and safe migration) is next, then pre-game setup, square uncropped grids/book margins, 25/30 generation, continuous 100×100 R&D and UI polish. Existing WS-08 beta gate remains later; no owner family phone migration/uninstall without exact version+independent backup; physical Co-op #53 OPEN.

- **[READ FIRST: WS-07 research and UX specification](WS07_PLAYTEST_RESEARCH_AND_UX_SPEC_2026-11-07.md)** — direct owner playtest evidence, Android Compose/W3C research, touch/zoom interaction contract, responsive/accessible no-scroll screens, acceptance matrix.
- **[Active WS-07A–G implementation order](DEVELOPMENT_ROADMAP.md#live-development-priority--ws-07-refinement-arc-november-7-2026)** — touch P0, layout, accessible precision, 2-phone modes, app/content, durability/performance, integration/release.
- **[Physical Word Marking acceptance checklist](WS07A_REAL_PHONE_TOUCH_ACCEPTANCE_2026-11-07.md)** and [P0 issue #53](https://github.com/PlayerEden/GridMates-Word-Search/issues/53); historical whole-game audit [#52](https://github.com/PlayerEden/GridMates-Word-Search/issues/52).
- **[WS-07A draft implementation PR #54](https://github.com/PlayerEden/GridMates-Word-Search/pull/54)** stacked on [WS-06I save integrity PR #51](https://github.com/PlayerEden/GridMates-Word-Search/pull/51) and [WS-06H integration PR #49](https://github.com/PlayerEden/GridMates-Word-Search/pull/49). Preserve the latter as verified source checkpoints; do not merge the stack without review.
- [Master game design](MASTER_GAME_DESIGN.md): the full accessible paperback-inspired Android puzzle game vision.
- [Development roadmap](DEVELOPMENT_ROADMAP.md): phased implementation and acceptance requirements.
- [WS-05I Android QA](WS05I_BUILD_QA_2026-11-06.md): v0.5.8, 47 API35 tests, compact-screen and 200% Android text captures; real devices still outstanding.
- [WS-05J QA APK updates](WS05J_STABLE_QA_UPDATES_2026-11-07.md): stable test-only signer, v0.5.9, **verified emulator update preservation**, and **required one-time backup / reinstall / restore** from earlier APKs.
- [WS-05J build and QA receipt](WS05J_BUILD_QA_2026-11-07.md): exact source SHA, successful standard/emulator CI, downloadable cumulative APK and rollback checkpoint.
- [WS-06A first Versus mode and QA plan](WS06A_SHARED_BOARD_RACE_QA_PLAN_2026-11-07.md): Shared Board Race host/guest scoring, winner/tie, protocol v5 and testing.
- [WS-06A exact-source build and QA receipt](WS06A_BUILD_QA_2026-11-07.md): 49 passing Android emulator tests, prior v0.5.9→v0.6.0 update.
- [WS-06B Versus journal plan](WS06B_VERSUS_JOURNAL_QA_PLAN_2026-11-07.md) and [verified QA receipt](WS06B_BUILD_QA_2026-11-07.md): prior 52 emulator tests.
- [WS-06C Speed Duel plan](WS06C_SPEED_DUEL_QA_PLAN_2026-11-07.md) and [verified QA receipt](WS06C_BUILD_QA_2026-11-07.md): prior 57 emulator tests and private Duel rules.
- [WS-06D persistent Duel journal plan](WS06D_DUEL_JOURNAL_QA_PLAN_2026-11-07.md) and [source-matched QA receipt](WS06D_BUILD_QA_2026-11-07.md): prior **60 green Android tests**.
- [WS-06E friend presence/share plan and Sudoku design transfer](WS06E_NEARBY_PRESENCE_SHARE_QA_PLAN_2026-11-07.md) and [exact-source QA receipt](WS06E_BUILD_QA_2026-11-07.md): previous **63 emulator tests**.
- [WS-06F PNG postcard design and QA](WS06F_POSTCARD_DESIGN_AND_QA_2026-11-07.md) and [exact-source QA receipt](WS06F_BUILD_QA_2026-11-07.md): previous **67 passing Android tests**.
- [WS-06G temporary shared Co-op appearance policy](WS06G_SHARED_COOP_BOARD_LOOKS_QA_PLAN_2026-11-07.md) and [source-matched QA receipt](WS06G_BUILD_QA_2026-11-07.md): **71 passing Android tests**, original stable-signed v0.7.3→v0.7.4 data-preserving update, protocol v7, rollback branch and APK checksum.
- [Word Search GitHub](https://github.com/PlayerEden/GridMates-Word-Search): the only source of truth for this game. Work stays separate from the SudokuTogether repository.

## Implemented across the cumulative stack

Real Solo and offline Daily, word-search touch selection and multiple book sizes, hints and results, local profiles/achievements, free Woodland portrait collections and board fonts, manual JSON backup/restore, same-Wi-Fi co-op with chat/presence and reconnection scaffolding, polished paperback UI and compact/large-text improvements. Shared Board Race has durable on-device win/loss/tie records and results comparison. Speed Duel has separate recipient-private boards with Ready/GO and host-authoritative first finish. Speed Duel now saves completed wins/losses in a **separate durable journal** and JSON backup. Co-op never awards a victory. WS-06A LAN protocol is v5; older v4 clients cannot join v5 rooms. Do not claim physical multiplayer QA passes until two phones are tested.

## Locked choices and guardrails

- Friendly to older and less technical players: readable, warm paper/ink visuals and low-friction nearby hosting/joining; optional room code.
- Cosmetics are earned for free with achievements and progress. No paid cosmetics, ad-watched cosmetics or cosmetic store.
- No ads, online login, cloud backend or purchases unless explicitly approved later.
- Separate Word Search Android package and save file from Sudoku; do not modify Sudoku in this project.
- The QA signing key in qa-signing is **public and test-only**. Never sign production releases with it, and never commit actual production signing keys or other secrets.
- Old pre-stable QA APKs (including the separately signed screenshot-only v0.5.9 PR #40 APK) cannot update to v0.6.0 in place. Export and verify a JSON backup before uninstalling; restore it afterward. Stable-signed PR #41 v0.5.9, WS-06A v0.6.0 and WS-06B v0.6.1 plus WS-06C v0.7.0 **can update in place** to WS-06G v0.7.4; the exact v0.7.3→v0.7.4 path passed the emulator. Real handset tests remain open. Both Nearby phones must use **protocol-v7-compatible** builds, even for Race/Duel rooms.
- Draft PRs remain unmerged until owner hardware testing and review. Capture exact SHA, CI evidence and rollback checkpoint at major milestones; conserve GitHub Actions emulator minutes.

## Next

WS-06G added a **small, entirely free Co-op board appearance preview catalog** and host-authoritative, round-frozen shared look with local Classic readability override. Standard QA and 71/71 API35 tests passed; actual signed v0.7.3 updated in place to v0.7.4 with Race/Duel histories retained. LAN protocol increases to **v7**, so both phones must match. There are NO earned board skin tiers or ownership changes. **Next WS-06H:** overall integration/release-readiness audit across modes, saves/updates, accessibility and real 2-phone acceptance. Keep drafts unmerged.


## Priority handoff — WS-07 owner screenshot correction (supersedes old “Next” above)

1. **AUTOMATED INPUT PASS; PHYSICAL P0 REPRO STILL OPEN.** Latest full emulator run #42411678348 passed the v0.7.6→v0.7.7 save update but returned `INVALID_PATH` for two real UI gesture-injection tests: 20×20 expected `(5,17)→(11,11)`, got `(6,17)→(12,11)`; 15×15 expected `(6,5)→(6,12)`, got `(6,5)→(7,6)`. Investigate coordinate transform/density/hit geometry and premature direction locking, not generic network connectivity. Test raw pointer points and final accepted endpoints in the **same measured board space**; verify with deterministic 12×12, 15×15, 20×20 horizontal/vertical/diagonal/edge cases. Do not silently adjust test fixtures to accept wrong coordinates.
2. Candidate already contains fit-first board, single-finger tracing, two-finger pinch/pan, 2-tap semantics, fixed active Nearby, collapsible room options and Solo word paging. **These designs are not considered accepted until ergonomics and P0 retest pass.** Actual users must be able to mark words on both phones and see the same count.
3. Follow the [WS-07A–G milestone order](DEVELOPMENT_ROADMAP.md). No full-page active-game scroll. Keep all modes, history, sharing and free collection functionality. Design accessibility paths independent of dragging/pinch/400 separate cell taps.
4. **Automated gates now passed**: use the exact [v0.7.7 QA receipt](WS07A_BUILD_QA_2026-11-07.md) to install safely over the same QA-signed package (back up first). Next, test both actual phones, swapped roles, three modes and UI/layout checks. Do not call the physical P0 fixed until the owner verifies it.
5. No changes to SudokuTogether, QA package ID, signing, schema4 or LAN wire v7 without a separate approved/migrated plan. `main` remains untouched; do not spend extra GitHub Actions minutes re-running full emulators for documentation-only commits.


## Latest automated acceptance (supersedes historical 76/78 diagnostic paragraph above)

[WS-07A exact-source build receipt](WS07A_BUILD_QA_2026-11-07.md): **78/78 Android emulator tests PASS** including formerly failing touch injection; v0.7.6→v0.7.7 same-signer update PASS; verified checkpoint and APK SHA recorded. **Two-phone hardware acceptance still OPEN; no `main` merge.**


## Latest WS-07B checkpoint (supersedes older v0.7.7-only status above)

**80/80 full emulator success and same-signed v0.7.7→v0.7.8 upgrade**; [build artifact, exact SHA and checksums](WS07B_BUILD_QA_2026-11-07.md), [real phone UI checklist](WS07B_COMPACT_NAVIGATION_ACCEPTANCE_2026-11-07.md). Start new implementation passes from draft #55; preserve its stacked relationship to #54. The two-phone physical P0 and 200% font/TalkBack remain owner gates. Do not rerun expensive full emulator on documentation-only commits.


### Latest WS-07C source-backed evidence (supersedes earlier v0.7.8-only summary)

[Full green 83/83 Android run #42419162816](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42419162816), [exact tested APK SHA and rollback](WS07C_BUILD_QA_2026-11-07.md). Follow-up WS-07D should prioritize automated LAN/claim reliability without asking owner for phone testing. Keep v0.7.9 verified fallback, ensure no untested code is called stable.


### WS-07D next handoff (supersedes earlier v0.7.9-only summary)

**87/87 Android tests PASS**, real same-signer v0.7.9→v0.8.0 update PASS. [Full exact-source receipt](WS07D_BUILD_QA_2026-11-07.md). Future WS-07E design/polish work can proceed in a new stacked draft after examining this checkpoint; do not claim two-phone acceptance, and do not ask for physical playtests until user says ready.


### Latest WS-07E evidence (supersedes prior v0.8.0-only status)

[Complete green 90/90 API35 suite #42424216761](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42424216761), [source and APK receipt](WS07E_BUILD_QA_2026-11-07.md), [compact Daily and saved-clue acceptance checklist](WS07E_DAILY_CLUES_UX_ACCEPTANCE_2026-11-07.md). A future follow-on can audit Records/Collection/profile/share UX without requesting physical testing prematurely. The original two-physical-phone word marking remains the top untested P0, not an automated pass.


### WS-07F Records UI verification and next UX handoff

[Full source-matched 92/92 pass](WS07F_RECORDS_BUILD_QA_2026-11-07.md) on `85ee1c05a8d3f34c8486282ac74fb58b64e29927`, with saved-progress upgrade PASS. The planned Collection paging follow-up is isolated in [draft PR #61](https://github.com/PlayerEden/GridMates-Word-Search/pull/61); treat the code37 branch as **UNVERIFIED** until separate full Android QA. Original roadmap durability/performance and beta gates remain incomplete. Don't ask the owner to test on phones until they volunteer readiness.
