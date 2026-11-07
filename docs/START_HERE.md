# START HERE — GridMates: Word Search

**Current cumulative QA candidate (November 7, 2026):** [WS-07B v0.7.8 exact-source receipt](WS07B_BUILD_QA_2026-11-07.md) — **80/80 API35 emulator tests PASS** and real signed v0.7.7→v0.7.8 install-over preserving all saved Solo/Daily/identity/Race/Duel fields; tested SHA `4702543400ef499885dea7a033943974323aacab`, APK SHA-256 `0b0d997e28dbc3ec04eaffe08d59de8c7241c5289caec222c07b8f0f6882126f`. Compact Home and first-access Nearby Host/Join tested at 360×720dp; WS-07A gestures remain green. **Physical two-phone word-marking issue #53 is still OPEN**; actual visual and large-font experience need owner testing. [Draft PR #55](https://github.com/PlayerEden/GridMates-Word-Search/pull/55) remains stacked on #54 and `main` is untouched; previous v0.7.7 automated rollback pinned.

## Current state and documents

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
