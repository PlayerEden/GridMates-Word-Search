**WS-12D companion art progression (Nov 8, 2026, REVIEW-ONLY):** Building directly on the Quiet Illustrated Paperbacks v1 design proof in draft PR #93, this stacked branch adds a deterministic vector source generator for nine matching Woodland/Space/Garden chapter markers, bookmarks and completion seals, dedicated tests and a separate Actions artifact. See [WS-12D design/QA plan](WS12D_MATCHING_PAPERBACK_ACCENTS_PLAN_2026-11-08.md). Preserve native titles/progress, existing book/cover byte hashes, original portrait and cosmetic IDs, puzzle saves, signing and Sudoku. **No Android artwork or APK has been changed; assets are unapproved and Adobe refinement is still pending due to connector error.** **CI verified: [26/26 tests](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42524647312) passed on source SHA f99e4cd, with [9-file prototype artifact](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42524647312/artifacts/13634668023); [draft PR #94](https://github.com/PlayerEden/GridMates-Word-Search/pull/94).** Next: inspect phone-sized appearance, then WS-11A Bookshelf UX and WS-11B immutable chapters. 

**WS-04D-B5 implementation (Nov 6, 2026):** [Draft PR #30](https://github.com/PlayerEden/GridMates-Word-Search/pull/30) is building a **font-neutral measured-ink board renderer** and an offline four-font library (Atkinson Hyperlegible default; IBM Plex Mono for First Page; DM Mono for Puzzle Regular; Patrick Hand for Today Counts). All unlockable fonts are **free achievement rewards**; no ad, shop, currency or IAP logic. Font files and full licenses are imported at build time from an exact Google Fonts commit with pinned blob checks, A–Z fontTools validation and Android resource/notice bundling. Collection → Fonts shows an actual board-letter preview and honest locked/owned/equipped state; selecting a font does not touch puzzle IDs, hint allowance or data. [B5 design and acceptance plan](WS04D_B5_FONT_ENGINE_AND_FREE_UNLOCKS_2026-11-06.md). **Build, emulator execution and owner phone readability are separate gates.**

**Cosmetics decision — NOVEMBER 6, 2026: ALL fonts and other current unlockable cosmetics are FREE, earned through gameplay achievements/milestones.** No font shop, paid unlocks, in-app purchases, ad-watching requirement, virtual currency or Play Billing in the active development scope. Keep a readable free default plus optional earned cosmetic looks. For now, add earned/owned/equipped states, deterministic one-time achievements, and meaningful Collection previews; possible monetization is an explicitly deferred future decision, not a current milestone commitment. Font commercial-use and redistribution/license checks **still apply even when given away for free**. [License shortlist](FONT_LIBRARY_LICENSE_RESEARCH_2026-11-06.md) · [Font implementation issue #29](https://github.com/PlayerEden/GridMates-Word-Search/issues/29).

**Font licensing and future IAP research (Nov 6, 2026):** The first verified-license research intake is in [FONT_LIBRARY_LICENSE_RESEARCH_2026-11-06.md](FONT_LIBRARY_LICENSE_RESEARCH_2026-11-06.md) and eight machine-readable candidates in [design/font-library-candidates.json](../design/font-library-candidates.json). The official OFL-1.1 family notices were located for Atkinson Hyperlegible, IBM Plex Mono, DM Mono, JetBrains Mono, Lexend, Nunito, Literata and Patrick Hand. Commercial app bundling is permitted **with license conditions**, but standalone font resale is not; cosmetic IAP entitlements must also meet Google Play Billing policy. **No fonts have been downloaded, hashed, approved or shipped based on this research yet.** Next B5 source intake requires a pinned upstream revision, a font binary SHA-256, matching license/copyright text, visual legibility QA and device consistency before packaging. Default board font must remain readable/free; do not create shop/entitlements until gameplay parity and review.

**WS-04D-B5 font customization requirement (November 6, 2026):** Correct uneven glyph spacing with a **font-capable renderer**, not a hardcoded single monospace-only board. Add a stable bundled font registry, font-specific measured centering/size constraints, independent board vs interface scope, consistent cross-device previews, readable default, and future locked/owned/equipped Collection support. Equipping fonts must never affect seeded grids, solution paths, touch positions or saves. Build the renderer foundation in B5; defer broad font collections until game-feel and readability QA. [Issue and acceptance details](https://github.com/PlayerEden/GridMates-Word-Search/issues/29).

**WS-04D-B4 audited build v0.4.7 (November 6, 2026):** Native gameplay polish is implemented in [draft PR #28](https://github.com/PlayerEden/GridMates-Word-Search/pull/28). The [standard Android QA #42188655477](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42188655477) is **GREEN** and, for the first time in this Word Search arc, a dedicated [Android API35 emulator execution #42188648196](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42188648196) ran **17 instrumented tests: 0 failures**. Source checkpoint [`2f888a2...`](https://github.com/PlayerEden/GridMates-Word-Search/tree/checkpoint/ws04d-b4-v047-ci-emulator-green-2026-11-06); [full audited QA receipt](https://github.com/PlayerEden/GridMates-Word-Search/blob/feature/ws04d-b4-gamefeel-nav-audit-2026-11-06/docs/WS04D_B4_BUILD_QA_2026-11-06.md); [APK artifact](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42188655477/artifacts/13488667619) (expires ~November 13). Includes Move pinch/pan and one-tap fit, faster cached seeded puzzles, reviewable Assisted/Unassisted completion, pinned target header and saved sound/haptic preferences. **Actual owner phone visual/gesture acceptance remains open.** User treats QA progress as disposable; don't require export before next alpha test. Do not merge the stacked draft yet.

**Current milestone — WS-04D-B4 pre-playtest audit (November 6, 2026):** Build a deeper gameplay slice and audit before asking for another disposable QA APK. [Implementation draft PR #28](https://github.com/PlayerEden/GridMates-Word-Search/pull/28) adds **bounded seeded-puzzle caching**, **Move-mode pinch/focal zoom and tap-to-fit**, persistent word-list header, a real assisted/unassisted **completion → board review → results** flow, restrained found-word feedback and persistent sound/vibration preferences. The [full pre-playtest audit and acceptance plan](WS04D_B4_PREPLAYTEST_AUDIT_AND_QA_PLAN.md) lists code findings, honest test gates and one consolidated owner phone test. An actual API-35 emulator workflow is part of the audit; early test failures exposed an exact-zero zoom bug, legacy prototype-only smoke assertions and a Kotlin preference-setter clash—corrected on the B4 branch and being reverified. **Do not advertise APK-ready until green CI + runtime instrumented evidence**; preserve seeded old Daily/Solo, 12 Woodland portraits and Sudoku independence. The user accepts disposable QA saves, so don't make export/import a blocker for this development cycle.

**Latest WS-04D-B3 owner screenshot follow-up (November 6, 2026):** The v0.4.5 physical screenshots confirm improved whole-board zoom/visible targets, but show a clipped bottom row at 20×20 100% and highlight the need to gate hints more strictly than Sudoku. [Draft PR #27](https://github.com/PlayerEden/GridMates-Word-Search/pull/27) adds **finite per-puzzle hint steps** (12×12=2; 15×15=3; 20×20=4; Daily=2) and distributes grid row heights to fit the full square board. [CI-green run #42184456076](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42184456076), [QA APK artifact](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42184456076/artifacts/13486703307), [full QA receipt and checksum](https://github.com/PlayerEden/GridMates-Word-Search/blob/feature/ws04d-b3-hints-budget-board-cell-fit-2026-11-06/docs/WS04D_B3_BUILD_QA_2026-11-06.md), [source rollback branch](https://github.com/PlayerEden/GridMates-Word-Search/tree/checkpoint/ws04d-b3-hint-cap-board-fit-v046-ci-green-2026-11-06), and [owner phone acceptance issue #26](https://github.com/PlayerEden/GridMates-Word-Search/issues/26). **Android instrumentation execution and physical bottom-row/hint testing remain open; don't call production-ready or merge draft yet.**

**Latest WS-04D-B2 verified source checkpoint (November 6, 2026):** The compact puzzle workspace and unified whole-grid zoom implementation are **CI green** in [draft PR #25](https://github.com/PlayerEden/GridMates-Word-Search/pull/25), [run #42182216221](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42182216221) at SHA `6a8f6426fb6df3cfa145643ecccd06e8a27aeb27`; [QA APK artifact](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42182216221/artifacts/13486481273), [QA receipt and checksum](https://github.com/PlayerEden/GridMates-Word-Search/blob/feature/ws04d-b2-true-zoom-compact-workspace-2026-11-06/docs/WS04D_B2_BUILD_QA_2026-11-06.md) and [rollback branch](https://github.com/PlayerEden/GridMates-Word-Search/tree/checkpoint/ws04d-b2-workspace-v045-ci-green-2026-11-06). Original puzzles, saves, QA ID and Woodland art preserved. **No Android instrumentation runtime or real phone visual acceptance yet**; do not close [P0 issue #24](https://github.com/PlayerEden/GridMates-Word-Search/issues/24).

**CURRENT BUILD PRIORITY — WS-04D-B2 P0 (November 6, 2026):** The owner's eight actual Android screenshots expose critical zoom geometry and vertical-layout defects. Follow the actionable [Puzzle Workspace Repair Plan](WS04D_B2_P0_PUZZLE_WORKSPACE_PLAN.md), supported by the [Competitor UX Research](COMPETITOR_WORD_SEARCH_UX_RESEARCH_2026-11-06.md) and [issue #24](https://github.com/PlayerEden/GridMates-Word-Search/issues/24). Fix scaled cells/letters/selection/highlights together; compact gameplay header; keep an independently scrolling word list visible; clarify current Move/Find mode. Build on WS-04D-B1 draft PR #22, preserve old saves/seed behavior and original Woodland art, require Android real-device acceptance after CI. **Hold multiplayer, language packs and additional collections until this P0 pass is validated.**

**Competitor UX benchmark (November 6, 2026):** [Word Search competitor research and actionable repair recommendations](COMPETITOR_WORD_SEARCH_UX_RESEARCH_2026-11-06.md). Prioritize a properly scaled board, compact play HUD and continuously accessible word list over additional features. See P0 phone-playtest issue #24. The report is desk research from official app listings, not competitor app install testing.

**Book-sized Solo puzzle update (WS-04D-B1):** new puzzle presets start at **12×12** Short (12 words), with **15×15** Standard (16, new default), and **20×20** Extended (24). Relaxed/Classic/Expert challenge stays independent from board size. The first zoomable board offers Move/Find panning and adjustable 100–250% zoom; old v1/v2 seeded Solo and dated Daily puzzles remain supported. [Draft PR #22](https://github.com/PlayerEden/GridMates-Word-Search/pull/22), [green Android CI #42174823751](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42174823751), [APK artifact](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42174823751/artifacts/13482927260), [QA receipt](https://github.com/PlayerEden/GridMates-Word-Search/blob/feature/ws04d-book-size-presets-zoom-2026-11-06/docs/WS04D_BOOK_SIZES_BUILD_QA_2026-11-06.md), [exact-source checkpoint](https://github.com/PlayerEden/GridMates-Word-Search/tree/checkpoint/ws04d-book-board-presets-v044-ci-green-2026-11-06). **20×20 real-phone pan/touch/accessibility testing and stable QA signing still open**; do not call this a production-ready experience. Follow [issue #23](https://github.com/PlayerEden/GridMates-Word-Search/issues/23).

**Latest WS-04D-A Gameplay V2:** Theme, **Board Size (8×8 / 10×10 / 12×12)** and **Challenge (Relaxed / Classic / Expert)** can now be selected independently for new English Solo puzzles. The old English v1 puzzle engine, existing Solo records and offline Daily seeds remain unchanged; save schema 4 reads older schema 1–3 data. Home is streamlined around playable Solo and Daily. [Draft PR #20](https://github.com/PlayerEden/GridMates-Word-Search/pull/20) · [CI-green run #42173169314](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42173169314) · [v0.4.3 QA artifact](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42173169314/artifacts/13481938907) · [source/QA receipt](https://github.com/PlayerEden/GridMates-Word-Search/blob/feature/ws04d-v2-puzzle-setup-2026-11-06/docs/WS04D_A_BUILD_QA_2026-11-06). **Actual Android test execution, signed in-place updates, touch/visual owner acceptance and deeper UX work are still pending**; follow [WS-04D-B issue #21](https://github.com/PlayerEden/GridMates-Word-Search/issues/21). Keep all PRs draft, preserve saved data and original Woodland art.

**Priority update — November 5, 2026:** Pause new Ocean/Bugs collections until Word Search reaches or exceeds Sudoku’s gameplay quality and feature completeness. Follow [Parity-First Quality Plan](PARITY_FIRST_QUALITY_PLAN.md): actual device QA and stable data updates, improved puzzle feel and results, Nearby Co-op, both Versus modes, and full navigation/accessibility. Keep the existing twelve approved Woodland portraits. The current build still needs physical testing.

**Latest WS-04C original-art checkpoint:** The **actual approved twelve Woodland PNGs** are now packaged in the QA Android build via a pinned, checksum-verified checkout of the Sudoku source (not AI replacements). [Draft PR #15](https://github.com/PlayerEden/GridMates-Word-Search/pull/15), [green CI #42162387676](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42162387676), [final APK artifact](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42162387676/artifacts/13477367662), [source and APK QA receipt](https://github.com/PlayerEden/GridMates-Word-Search/blob/feature/ws04c-verified-woodland-assets-2026-11-05/docs/WS04C_BUILD_QA_2026-11-05.md), [exact green source checkpoint](https://github.com/PlayerEden/GridMates-Word-Search/tree/checkpoint/ws04c-woodland-originals-v042-ci-green-2026-11-05). **Actual device rendering, backup migration, and stable QA signing remain open.** Next generated art work is a reviewed Ocean/Bugs proof batch [issue #16](https://github.com/PlayerEden/GridMates-Word-Search/issues/16), not a shipped collection.

**Final WS-04B source verification:** the save-export safeguard [passed CI #42159885088](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42159885088) at SHA `fe448b6584326a1e3b241792584a3689d4de194c`. [Final rollback checkpoint](https://github.com/PlayerEden/GridMates-Word-Search/tree/checkpoint/ws04b-collections-safeguarded-ci-green-2026-11-05), [QA details and checksum](https://github.com/PlayerEden/GridMates-Word-Search/blob/feature/ws04b-profile-woodland-achievements-2026-11-05/docs/WS04B_BUILD_QA_2026-11-05.md). **Next:** original Woodland PNG import [issue #14](https://github.com/PlayerEden/GridMates-Word-Search/issues/14), then owner phone QA. Avoid unintended uninstall of QA apps with local progress; export first.

# GridMates: Word Search — Development Roadmap

**Future multilingual expansion (deferred until parity):** Research and implementation plan: [Multilingual & Spanish Development Plan](MULTILINGUAL_SPANISH_RESEARCH_AND_DEV_PLAN.md) and [issue #19](https://github.com/PlayerEden/GridMates-Word-Search/issues/19). Plan app UI language separately from puzzle language, preserve existing seeded English puzzles/Daily and their saved data, and keep future pack/network IDs language/version aware. **No Spanish implementation or new APK is authorized yet.** Align with [Big Word Library issue #18](https://github.com/PlayerEden/GridMates-Word-Search/issues/18); quality parity remains the priority.


**Baseline:** November 5, 2026

**WS-04B Profile & Collections:** [Draft PR #12](https://github.com/PlayerEden/GridMates-Word-Search/pull/12) has an offline display name, 12 stable Woodland avatar IDs, completion-based unlockable selection, eight evidence-based achievements, Collection UI and backward-compatible save schema 3. [WS-04B plan / QA gates](https://github.com/PlayerEden/GridMates-Word-Search/blob/feature/ws04b-profile-woodland-achievements-2026-11-05/docs/WS04B_PROFILE_COLLECTION_PLAN.md). Original approved Woodland PNGs are **not yet packaged**; placeholder monograms are intentional while safe original art migration is pending. Android source CI [#42159209553](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42159209553) passed; [WS-04B source QA receipt](https://github.com/PlayerEden/GridMates-Word-Search/blob/feature/ws04b-profile-woodland-achievements-2026-11-05/docs/WS04B_BUILD_QA_2026-11-05.md). Physical QA and approved original art import remain open.

**WS-04A Daily alpha:** [Draft PR #9](https://github.com/PlayerEden/GridMates-Word-Search/pull/9) adds device-local deterministic Daily, persistent dated history, schema-1→2 migration and backup that includes Solo + Daily. [GitHub CI #42152990119](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42152990119) is green; [QA receipt](https://github.com/PlayerEden/GridMates-Word-Search/blob/feature/ws04a-offline-daily-2026-11-05/docs/WS04A_BUILD_QA_2026-11-05.md). Physical UX/migration checks and all other WS-04 collections/profile/rewards work remain open.

**WS-03 Solo beta:** [Draft PR #7](https://github.com/PlayerEden/GridMates-Word-Search/pull/7) and [issue #8](https://github.com/PlayerEden/GridMates-Word-Search/issues/8) contain local save/resume, completion records, hint ladder and manual export/import. The [phone test playbook](https://github.com/PlayerEden/GridMates-Word-Search/blob/feature/ws03-solo-save-results-2026-11-05/docs/WS03_SOLO_BETA_PLAYTEST.md) is the acceptance checklist. **Source CI is green** at [build #42149214204](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42149214204); [QA and APK receipt](https://github.com/PlayerEden/GridMates-Word-Search/blob/feature/ws03-solo-save-results-2026-11-05/docs/WS03_BUILD_QA_2026-11-05.md). Physical testing and seamless update signing remain open.

**WS-02 source QA:** [Draft PR #4](https://github.com/PlayerEden/GridMates-Word-Search/pull/4), [CI run #42138730965](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42138730965), [build and physical playtest receipt](https://github.com/PlayerEden/GridMates-Word-Search/blob/feature/ws02-word-engine-touch-prototype-2026-11-05/docs/WS02_FIRST_PLAYABLE_QA_2026-11-05.md). Deterministic generator and native touch-grid prototype exist **on a stacked draft branch**. No saved game or physical UX approval yet.

**Latest WS-01 evidence:** [Draft PR #3](https://github.com/PlayerEden/GridMates-Word-Search/pull/3); [successful build and package-ID verification](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42131133443). Android UI test execution and actual phone installation remain open. A native shell exists on the WS-01 branch, not yet on `main`.  
**State:** WS-01 and WS-02 gameplay prototype are CI green on draft branches; physical testing and release remain open. WS-02B refinement is now planned.  
**Master specification:** [MASTER_GAME_DESIGN.md](MASTER_GAME_DESIGN.md)  
**Quick handoff:** [START_HERE.md](START_HERE.md)

## November 8, 2026 — Active WS-10 Vocabulary Scale Program

**Owner direction:** Build an offline-friendly word-search library with **10,000+ unique, human-approved, playable English answers** across **30+ coherent themes**, rather than treating the current few-hundred-word packs as the permanent ceiling. [Tracking issue #87](https://github.com/PlayerEden/GridMates-Word-Search/issues/87) · [initial 33-topic schema draft PR #86](https://github.com/PlayerEden/GridMates-Word-Search/pull/86) · [detailed WS-10 program](https://github.com/PlayerEden/GridMates-Word-Search/blob/feature/ws10-thousands-word-lexicon-foundation-2026-11-08/docs/WS10_TEN_THOUSAND_WORD_LEXICON_ROADMAP_2026-11-08.md).

**Live WS-10B2 review snapshot (Nov 8, 2026):** [source-matched review CI #42519094538](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42519094538), 19/19 tests; [draft PR #89](https://github.com/PlayerEden/GridMates-Word-Search/pull/89) produces three priority editorial CSVs from the 563 previously proposed V4 topic-word combinations: **532 for review, 31 original holds**, with **176 terms absent from the exact dictionary-root extraction** (flagged for independent review). [Review workbook artifact](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42519094538/artifacts/13632282442) expires November 22; pinned source reproducible. **Zero words auto-approved; existing game untouched.**

**Live WS-10B1 intake snapshot (Nov 8, 2026):** [official-source QA #42518555704](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42518555704), 36,213 de-duplicated, UTF-8 U.S. English dictionary-root candidates staged *outside* app source, 38 already in the original 40-word starter CSV, 56 mode-incompatible palindrome flags. The source archive and complete publisher notice are pinned in [draft PR #88](https://github.com/PlayerEden/GridMates-Word-Search/pull/88); [14-day candidate/artifact download](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42518555704/artifacts/13632471594) is a QA resource, not a stable released asset. **Approved unique total remains ZERO.**

**Progress categories must remain separate:** raw words in an external source → rights-cleared quarantined *candidates* → thematically classified *review batches* → editorially *approved* unique words → **eligible** for a published, versioned game pack. A term mapped to three categories counts as **one** unique word. A licensed source does not mean each entry is appropriate for family-friendly Relaxed puzzles.

| ID / dependency | Phase and player benefit | State / what is genuinely verified | Acceptance before moving on |
| --- | --- | --- | --- |
| **WS-10A** | Canonical IDs, source rights ledger, 33-topic/subtopic registry, draft CSV and strict QA | **AUTOMATED GREEN; NOT IN APP:** [PR #86](https://github.com/PlayerEden/GridMates-Word-Search/pull/86), [8/8 tests, source SHA-pinned #42517594304](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42517594304). 40 illustrative candidate words / 76 tentative assignments / **0 approved**. | Thousands-row schema capacity, duplicate/label/review/rights gating demonstrated, never auto-approve |
| **WS-10B1** | Rights-verified reproducible U.S. English spelling source, hash pin and **quarantine** | **GREEN / COMPLETED FOR FIRST SOURCE:** [draft PR #88](https://github.com/PlayerEden/GridMates-Word-Search/pull/88), [CI #42518555704](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42518555704); **36,213 unique unreviewed candidate roots**, 14/14 tests, pinned full license notice and SHA. **0 approved** | Exact verified source+notice and artifact; source archived reproducibly; content never auto-approved or loaded into Android |
| **WS-10B2** | Initial review queue from the 36,213-root source and three existing game themes | **GREEN FOR FIRST THREE THEMES; EDITOR SIGNOFF OPEN:** [PR #89](https://github.com/PlayerEden/GridMates-Word-Search/pull/89), [19/19 CI #42519094538](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42519094538). **532 human-review queue rows** (185 Woodland / 183 Space / 164 Garden), **31 holds**, 176 independent-lookup flags; **zero approved** | Begin signed 100–250-row editorial review, then extend remaining 30 themes; no automatic inclusion in gameplay |
| **WS-10C1** | Review system: topic matching, family-appropriate familiarity, acceptable labels and specialist holds | **PLANNED** | Human-signed review batches of 100–250, 30+ coherent themes, source provenance per candidate, audit trail and rejected/held records |
| **WS-10C2** | Genuine vocabulary expansion milestones | **PLANNED:** **1K → 3K → 5K → 10K approved unique**. Progress to target is **0/10,000** in the new lexicon catalog today | Counts only words with reviewed metadata, rights cleared, approved topic mapping and usable generator rules; no inflated assignment count |
| **WS-10D** | Offline approved content snapshot: fast themed puzzle generation without login | **BLOCKED until human curation & immutable version contract** | Compacted APK/AAB offline assets, stable corpus hash, new permanent content IDs, versioned deterministic seed replay, fixed older V1/V2/V3 generators/enum ordinals and schema-safe backup |
| **WS-10E** | Every new puzzle *feels* different without random clustering | **REFERENCE QA PASSED, PRODUCTION BLOCKED:** [V4 balanced lab PR #85](https://github.com/PlayerEden/GridMates-Word-Search/pull/85), [1,800/1,800 lab quality checks](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42514815037), **not** an installed game feature | 100+ consecutive puzzles/theme: subtopic breadth, word overlap/recency, exact-path reuse, allowed directions, 10×10 actual-target coverage, real phone reading comfort |
| **WS-10F** | Real phone release signoff for expanded themed content | **BLOCKED:** old QA v0.9.4/code48 baseline remains separate | 320dp/200% font/landscape/105-word navigation, low memory/FPS, TalkBack, actual save-preserving same-signer upgrade, two-phone acceptance, rollback and release review |

**Priority order:** WS-10B1 has a green 36,213-word source quarantine; WS-10B2 has produced **532 editor-ready proposed rows** and 31 holds across the first three game themes. **WS-10C1 human editorial decisions are the next bottleneck.** WS-10B1/B2 can run alongside **existing** stable Solo polish; WS-10C curation can run in parallel with separate V4 and Android UX work. Only connect approved WS-10D content after the V4 production-version/saved-game gates are ready. Existing v0.9.4 APK and all historical published/seeding behavior must be preserved.

**External source research:** ESDB/SCOWL 2026.02.25 release information and copyright terms: [official releases](https://github.com/en-wl/wordlist/releases/tag/rel-2026.02.25) / [official license notice](https://github.com/en-wl/wordlist/blob/v2/Copyright). Wordfreq code/data licensing is different and must **not** be silently combined. No release estimate is implied by these tasks.

---

## WS-11 — Puzzle Bookshelf / themed book collections (recovered feature)

**Status: PLANNED / DESIGN TRACKED — no playable book UI or book-specific save yet.** Originally scoped under [large word-library issue #18](https://github.com/PlayerEden/GridMates-Word-Search/issues/18) as curated **puzzle books with fixed chapter seeds alongside procedural Free Play**. It was not canceled, but became obscured by the 10,000+ lexicon intake and cosmetic Collection work. Its player-facing milestone is now [WS-11 Bookshelf issue #90](https://github.com/PlayerEden/GridMates-Word-Search/issues/90).

**Experience boundaries:** A bookshelf of themed volumes with readable covers and chapter/page previews, per-book progress, bookmarks and a one-tap **Continue Reading**; Free Play remains endless and Daily remains date-driven. Keep avatars/fonts/cosmetics distinct from actual puzzle *books*. Prototype names such as Woodland Vol. I / Space Vol. I / Garden Vol. I are suggestions pending design approval, not implemented content.

**Implementation stages:** **WS-11A** book navigation and accessible UX → **11B** immutable book/chapter manifest and storage safety unit tests → **11C** curated, approved 3-theme pilot books with fixed seeds/word manifest → **11D** Android browse/play/continue/completion integration → **11E** real-device, seed-version replay, resume/backup/upgrade and consecutive-page novelty QA. Visual and metadata work can progress before full word approvals; real books depend on **WS-10C/D** reviewed themed corpus, permanent content IDs and publisher-approved book artwork. **Never store experimental `wordsearch:v4:PREVIEW` or `BALANCED-PREVIEW` IDs.** Do not change old V1/V2/V3 seed replay, Daily or schema5 saves.

**Acceptance:** A family tester can browse offline books, open any eligible chapter, find words, quit, resume from their bookmark, complete a book, close/reopen/update the app without progress loss, and still use Free Play/Daily. Confirm on actual narrow Android phones; do not call a cosmetic collection tab a completed book library.

---

## WS-12 — Generated visual assets and Adobe refinement pipeline

**Status:** PLANNED; [WS-12 issue #91](https://github.com/PlayerEden/GridMates-Word-Search/issues/91). [Production and tooling specification](WS12_VISUAL_ASSET_PRODUCTION_PLAN.md) and [draft source/approval manifest schema](../design/asset-pipeline/asset-manifest.schema.json) are now documented. **No image has been generated, approved or packaged by this planning work.**

**Owner direction:** Use generated sprites and illustrations for flavor and identity **where suitable**, then polish/edit selected designs using available Adobe workflows (Adobe Express layout and editable titles; image crop/color/cleanup; Illustrator/vectorization for clean emblems; font research with correct commercial-app licensing). Review before exporting; ship only static, frozen, approved resources with provenance, hashes and safe fallbacks. Avoid AI-generated tiny gameplay icons or lettering baked into covers; keep core gameplay and progress information as native accessible UI.

| ID | Deliverable | Dependency / acceptance |
| --- | --- | --- |
| **WS-12A** | Cross-theme art-direction bible and pilot cover briefs (Woodland / Space / Garden) | Cozy paperback identity, original Woodland portraits preserved, 320dp/200%-font design rules |
| **WS-12B** | Asset manifests, source/edit/rights ledger and stable IDs | Review states, editor approval, source & optimized SHA-256, frozen approved assets, no mystery files |
| **WS-12C** | Generated-then-Adobe-refined cover/spine/emblem pilot pack | Shortlist, refine, rights audit, owner visual approval, readable static exports |
| **WS-12D** | Earned cosmetic visuals (portraits, stamps, bookmarks, highlight/page-border themes) | Distinct from **playable Bookshelf**, consistent with existing asset IDs and free-unlock direction |
| **WS-12E** | Android packaging, fallbacks, raster/vector optimization | No changed puzzle seeds, save schema or signer; APK-size, memory and UX checks |
| **WS-12F** | Full source-hashed visual QA and reusable future-theme pipeline | Exact artifact manifests, multiple phone sizes, accessible alternatives, rollback plan |

**Connection:** **WS-11A/B** can design Bookshelf UX and book metadata without finalized artwork; the **WS-11C/D pilot books** should use the WS-12C *approved* cover/emblem set once it exists and must wait for stable WS-10 versioned editorial content for actual fixed puzzles. **WS-10** future themes can reuse the art pipeline rather than generating inconsistent assets ad hoc. Font/art redistribution rights must be checked independently of the availability of Adobe tools. Preserve the previously approved Woodland art and all historical puzzle saves.

---

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
| **WS-03** | Complete Solo experience | **CI GREEN — PHONE QA OPEN** | Difficulty/theme selection, resume, hints, results, player-friendly interactions | Save/restart, narrow-phone, long-session, accessibility checks |
| **WS-04** | Daily, profile, collections | **WS-04A/B/C CI GREEN; phone QA OPEN** | Dated puzzle/history, local stats/achievements, original portraits and settings | Upgrade tests, no duplicate Daily rewards, asset/hash checks |
| **WS-04D-A** | Versioned Solo puzzle overhaul | **CI GREEN; DEVICE QA OPEN** | Board size independent from challenge; expanded English starter lists; legacy save safety; simpler Home | Seeded v1/v2 regression + real touch/upgrade tests |
| **WS-04D-B** | Gameplay feel and native QA | BOOK SIZES CI GREEN; PHONE QA & MORE POLISH OPEN | Compact HUD, drag/hints/results polish, executed Android instrumentation and stable update signing | Android emulator execution + real owner phone UX + safe update proof |
| **WS-04D-B2** | P0 board/UI workspace repair | **SOURCE CI GREEN; PHONE/EMULATOR QA OPEN** | True board zoom/pan, compact HUD/options, visible independent target-list panel, clear Find/Move mode | Screenshot comparison, generator/save regression, actual Android touch/large-font QA |
| **WS-04D-B3** | Hint gating and 20×20 grid fit | **CI GREEN; OWNER PHONE QA OPEN** | Finite 2/3/4 hint steps per board and Daily 2; exact square row heights | Hint reset/restart and bottom-row screenshot on real Android |
| **WS-04D-B4** | Gameplay navigation, completion/results and deep pretest audit | **CI + 17 EMULATOR TESTS GREEN; OWNER PHONE QA OPEN** | Pinch/fit in Move, cached seed boards, full honest results/review, persistent feedback preferences and list header | Unit/lint + Android executed instrumentation + owner one-pass touch QA |
| **WS-04D-B5** | Board typography + fonts | **DRAFT PR #30; ANDROID QA IN PROGRESS** | Even optical centering; license-verified bundled font library, free readable default, preview and earned/owned/equipped cosmetic states | 12/15/20 at 100–250% zoom, Android UI/font legibility, achievement unlock and restart QA |
| **WS-05** | Nearby Co-op | PLANNED | Open Nearby host/join, optional code, shared word claims, chat/presence, reconnect | Two actual Android phones + protocol/failure tests |
| **WS-06** | Both Versus modes | PLANNED | Shared Board Race and Speed Duel, separate records, fair completion logic | Two-phone races, same-word collisions, near-ties and disconnect QA |
| **WS-07** | Content and UX polish | PLANNED | More themes, hint refinements, free achievement-earned fonts and cosmetics, profile/collection polish; optional sharing and non-currency Daily milestone rewards if approved | Physical UX/accessibility/art/content quality audit |
| **WS-09F/G** | V4 giant-board placement, spatial quality and 105-word list UX | **EXPERIMENTAL CORE/QA GREEN; NO PUBLIC V4 SOLO** | Bounded spread-scored 40×40/50×50 research, alphabetical 105-word list, content proposals | Versioned approved V4 corpus, saves, real Android input/performance, maintain V1/V2/V3 golden compatibility |
| **WS-10A–F** | 10,000+ unique approved-word lexicon | **10A CI GREEN; 10B NEXT, 10C–F PLANNED** | Source-verified candidate intake, human editorial themed packs, offline versioned content, repeat-play freshness | 10,000 approved (not imported) with rights/labels, content goldens and phone save/UX QA |
| **WS-11A–E** | Puzzle Bookshelf — curated themed books, fixed chapters and bookmarks | **PLANNED; issue #90** | Browse paper-book-style volumes, replayable chapter manifest, local progress and Continue Reading; separate from Free Play/Daily/cosmetics | Approved versioned content, fixed-seed golden snapshots, save/backup/upgrade tests and real-phone accessibility |
| **WS-12A–F** | Approved generated sprites + Adobe-refined visual production | **PLANNED; issue #91 / guide committed** | Book covers/spines/emblems, stamps, cosmetic art, original asset hash/rights ledger, polished static exports | Coherent style, editor signoff, source-hashed manifest, narrow-phone/readability/performance QA and safe fallbacks |
| **WS-08** | Beta and launch readiness | PLANNED | Production signing/build, privacy/store preparation, Play testing; advertising and purchases deferred pending a new owner decision | Play test eligibility, AAB, real-device signoff, policy disclosures |

| **ML-FUTURE** | Spanish and multilingual packs, UI localization, Unicode-safe engine | **RESEARCHED / DEFERRED** | Legacy-safe puzzle v2, reviewed Spanish vocab, independent UI/puzzle language, locale-aware Daily & LAN | Unicode, pack/license, real Android, save and two-phone tests |

Milestone sequence can be rearr for risk reduction, but **no stage is marked passed without evidence**. Do not promise an app release date or skip compatibility gates just because automation is green.

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

**Sequencing:** WS-04A delivers Daily gameplay and schema-2 saves; WS-04B adds local identity, collections and achievements with schema-3 migration. The original approved Woodland PNGs are scheduled for a separate **WS-04C art import** so the asset provenance and checksum gate cannot be skipped. Ocean and Bugs art, profile share cards and optional daily rewards remain future design decisions.


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
