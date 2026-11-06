# Word Search — Parity-First Product & QA Plan

**Decision: November 5, 2026.** The owner wants **GridMates: Word Search to reach or exceed the quality of GridMates: Sudoku before adding any more character collections.** Prioritize features, polish, reliability, accessibility, and real two-phone multiplayer. Extra Ocean/Bugs art and collectible economies are deferred. The twelve existing approved Woodland portraits may remain in the game.

## Grounded reference and truth boundary

- Sudoku source of truth: [PlayerEden/SudokuTogether, feat/gridmates-nearby-animals](https://github.com/PlayerEden/SudokuTogether/tree/feat/gridmates-nearby-animals) at v0.11C, [delivery plan](https://github.com/PlayerEden/SudokuTogether/blob/feat/gridmates-nearby-animals/docs/GRIDMATES_DELIVERY_PLAN.md) and [actual QA](https://github.com/PlayerEden/SudokuTogether/blob/feat/gridmates-nearby-animals/docs/V0.11C_NEARBY_WOODLAND_QA.md).
- Sudoku v0.11C reports **117 JVM tests + 78 accepted Android native executions**; actual QA certificate continuity for 18→19 update; basic two-phone co-op success reported by owner with further lifecycle/reconnect tests open. Installed gameplay includes Solo, Daily, nearby co-op/versus, optional room-code admission, room/chat controls, records, achievements, a native visual identity, original Woodland portraits and real Android execution evidence.
- Some Sudoku features are **proposed, not shipped**: detailed coach explanation upgrades, share-result cards, score calibration and Play release. Don't present proposals as parity blockers already shipped in Sudoku.
- Word Search source of truth: [WS-04C draft PR #15](https://github.com/PlayerEden/GridMates-Word-Search/pull/15), [CI-verified v0.4.2 source and original art](https://github.com/PlayerEden/GridMates-Word-Search/blob/feature/ws04c-verified-woodland-assets-2026-11-05/docs/WS04C_BUILD_QA_2026-11-05.md). Solo, offline Daily, local saves/backup, hints, Records, names, achievements and 12 original portraits are in a CI-built APK. **Word Search Android instrumentation tests have been compiled, not actually run; real current phone usability and update continuity remain open.** Nearby Co-op, Shared Board Race and Speed Duel are not implemented.

## Priority matrix — parity before collecting more art

| Priority | Capability | Sudoku reference | Word Search state | Success gate |
| --- | --- | --- | --- | --- |
| **P0** | Player trust: saves, upgrades, repeatable builds | v0.11C preserved profile/active puzzle and certificate through actual 18→19 install | Manual backup exists; temporary QA signing; no real upgrade verification | Establish stable protected QA signing, safe one-time migration from old signing, actual old→new in-place data-preserving update, schema-1/2/3 migration tests on Android |
| **P0** | Device QA, adaptive UI, no stuck screens | 78 native executions with 200% text, compact landscape, active workflows | Unit tests/Android UI source compile only; phone feedback was drag/layout roughness | Build/run emulator instrumentation suite, screenshots across narrow/large/landscape/large-font, owner confirms smooth gesture and no clipping |
| **P1** | Standalone Word Search gameplay polish | Sudoku has complete native board controls, undo/entry check, results/help/records | Basic drag/highlights, simple hint ladder and compact list; standalone results presentation limited | Smooth forgiving drag/tap, legible word paths, satisfying find/finish feedback, optional undo where meaningful, clear complete/review/play-again, friendly tutorial and accessibility fallback |
| **P1** | Puzzle/content depth and fairness | Sudoku calibrates deterministic Easy/Medium/Hard, regenerates and avoids repetition | Word Search generator with themed Easy/Medium/Hard grids; limited starter word banks | Curated repeat-safe puzzles, meaningful difficulty beyond tiny glyphs, adequate contrast and content variety, deterministic replay/provenance, bounded generator QA |
| **P1** | Daily and personal records | Sudoku saved dated Daily History, assisted flags and profile journal | Offline Daily saved in schema 2/3, basic journal and completions | Robust date-change, offline/process restart/rollback behavior; truthful stats by difficulty/mode, Daily review and no double completion |
| **P2** | **Nearby co-op** | Same-Wi-Fi discovery, open-by-default rooms, optional remembered Require a code, partner chat, rejoining | Not implemented | Real nearby-host/join, single shared board/claims, partner presence/color cues, compact chat, resilient reconnection; two actual phones |
| **P2** | **Two distinct Versus modes** | One race mode over separate Sudoku solutions with limited opponent progress | Shared Board Race and Speed Duel designed, not implemented | First-claim fairness/near-ties, both players see appropriate presence and scores, identical-seed private duel, honest results and disconnect handling; two-phone proof |
| **P3** | Identity, helpfulness and delight | Navigation, profile, unlocks, native assets, practical hint launcher and UI polish | Profile/achievements/12 portraits implemented; basic Help/Settings | Refine navigation and menus, accessibility, consistently readable font options, profile and earned milestones, smooth theme selection/finish screens; no new portraits necessary |
| **P3** | Optional sharing and monetization | Share-result UI planned, not shipped; ads/remove-ads deferred | Neither shipped | Optional non-spoiler results/profile card after verified records; monetize only after good beta with transparent, light ads and optional removal |
| **P4** | Release readiness | No Play release; current policy/signing work remains | No Play release | Production identity/key custody, AAB, privacy/permissions/Play rules verified afresh, closed-test flow, actual physical acceptance |

## Revised execution order — don't keep pushing major beta APKs without quality gates

### Gate A — QA, safe updates and controlled first-phone feedback (next)

1. Preserve **WS-04C CI-green source checkpoint**; don't merge stacked PRs or delete existing QA test APKs.
2. Accept the owner's first consolidated WS-04C physical feedback as bug tickets: drag/highlight fluidity, compact Easy/Hard layout, hint clarity, Solo/dated Daily save-resume, Home/Records/Collection/Settings, and original Woodland art.
3. Add real Android emulator instrumentation execution (not just `compileDebugAndroidTestKotlin`), stable CI screenshots and repeatable assertions around restart, profile migration, Daily, unlocks and the image rendering.
4. Establish a protected **stable QA signing identity** separate from production. One-time backup/export → remove old ephemeral-key Word Search QA → install stable-key QA → import may be necessary; never promise an incompatible update will work.
5. Fix P0/P1 quality regressions before undertaking multiplayer networking or new content bundles. Do not change Sudoku's package, keys, or save files.

### Gate B — Enjoyable, high-retention Solo & Daily

Refine actual word selection (finger occlusion, snap/cancel/wobble, feedback/haptics, found ribbon), difficulty calibration, unique and curated themed content, responsive board/list, help/hints, meaningful end-of-game transitions and richer real records. Keep simple default UX for older/less technical players, and make feedback optional. A polished Solo game should feel complete even without Wi-Fi.

### Gate C — Nearby Co-op (WS-05)

Port **patterns**, not Sudoku game rules: NSD discovery, remembered open/optional-code room policy, host-authoritative state, chat and reconnect. Include progress/presence on the puzzle so it genuinely feels shared. Validate claims against the same deterministic Word Search puzzle and host revisions; reject duplicate/out-of-order operations. Do LAN protocol/JVM fuzz tests, then test two real Android phones.

### Gate D — Competitive Word Search (WS-06)

Implement **Shared Board Race** (one contested board, accepted first claimant gets a word) and **Speed Duel** (private identical-seed boards, first valid completion wins), with separate fair results and readable opponent presence. A near-tie/disconnect protocol must be deterministic and tested; don't prematurely end idle matches.

### Gate E — Presentation and release (WS-07/08)

Only after the core experience works: optional share-result card, final fonts/icon/navigation polish, onboarding, accessible settings, stability, privacy and Play closed testing. Ocean/Bugs expansions and optional Daily prize economies resume only after the owner approves the gameplay and actual art proof.

## Nonnegotiable quality rules

- **Parity means working behavior plus evidence**, not a matching number of menu pages or cosmetic icons. Sudoku's 117/78 result is a reference standard, not an arbitrary target test count.
- No payment, cosmetics, growth gimmicks, new portrait packs or random rewards prioritized over game feel, trustworthy progress or multiplayer fairness.
- Never use `assembleDebug` as proof of real Android test execution or phone acceptance. Keep exact APK SHA-256, source commit and known-good rollback for every candidate.
- Protect Solo and Daily progress, local identity and existing earned avatars through upgrades. Avoid saving guesswork, invented stats, surprise date reset or destructive migrations.
- Maintain one readable player-facing handoff and an issue-backed list of actual owner-reported bugs, not generic unbounded polishing.

**Primary owner-approved reprioritization:** Word Search should be **as complete and dependable as Sudoku, preferably better in tactile puzzle play**. Approved Woodland art stays; new collection art and economies pause until this bar is met.
