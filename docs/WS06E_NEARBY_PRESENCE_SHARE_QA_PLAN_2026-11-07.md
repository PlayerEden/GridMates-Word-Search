# WS-06E — GridMates cross-game design transfer and QA plan

**November 7, 2026 — PR #46 implementation and automated QA PASS (63/63 Android API35 tests). Real two-phone QA remains OPEN.** [Exact-source receipt](WS06E_BUILD_QA_2026-11-07.md).

Sudoku reference source: `PlayerEden/SudokuTogether` branch `development` SHA `1818cd54e2fc5c5eaecf37389295ae3d55b898c9`. The new documents are **proposals and design direction**, not proof that the features are implemented in Sudoku.

- [Share Results UX](https://github.com/PlayerEden/SudokuTogether/blob/development/docs/GRIDMATES_SHARE_RESULTS_UX_DESIGN.md): A low-pressure, visible, optional Share result action with preview and native share sheet. Word Search already supported Solo/Daily text sharing; WS-06E adds preview/Cancel and extends privacy-safe sharing to validated nearby completions. **This slice shares text only, not PNG images.**
- [Versus Post-Match Comparison](https://github.com/PlayerEden/SudokuTogether/blob/development/docs/GRIDMATES_VERSUS_POST_MATCH_COMPARISON_DESIGN.md): Show real recipient-relative outcomes and counts. No false opponent completion times, full replay, or leak of private Speed Duel word IDs. Shared Board Race can already compare public claimed words; Speed Duel only shares totals.
- [Shareable Cards Concept](https://github.com/PlayerEden/SudokuTogether/blob/development/docs/GRIDMATES_SHAREABLE_CARDS_CONCEPT.md): Design a future 4:5 PNG postcard rendered offline, with opt-in preview, anonymous partner by default and no fake Play link. **Not implemented in WS-06E.**
- [Shared Co-op Board Themes](https://github.com/PlayerEden/SudokuTogether/blob/development/docs/GRIDMATES_SHARED_COOP_BOARD_THEMES_DESIGN.md): Borrowing a partner's owned board skin during Co-op may be fun, but Word Search has a different set of cosmetic assets and **no defined ranked board tiers**. Do not copy Sudoku's board IDs/tiers or unlock ownership to Word Search until reviewed. Respect independent fonts and contrast/local override.
- [Offline Nearby/Bluetooth](https://github.com/PlayerEden/SudokuTogether/blob/development/docs/GRIDMATES_OFFLINE_NEARBY_BLUETOOTH_DESIGN.md): Valuable no-router future prototype, but LAN v6 remains unchanged in this milestone until real two-phone Wi-Fi acceptance.
- [Brand and Series](https://github.com/PlayerEden/SudokuTogether/blob/development/docs/GRIDMATES_BRAND_AND_SERIES.md): Intentional friendly in-person puzzling, not compulsory online social play or rank pressure.

## WS-06E implementation
- Visible **Playing with someone nearby** presence panel shows live connection, sanitized bounded friend display name, each person's already-validated contribution count and stage. Compact or 200% font stacks the entries. Co-op/Race can show latest public shared word; Speed Duel reveals **only per-person counts**.
- Completed, admitted nearby Co-op/Race/Duel can produce a true local perspective text recap. Competitive Share is enabled only once the completed result has been saved locally. Names, chat, room ID/code/address, IP, raw puzzle seed, word IDs and answers never appear in shared summary.
- **Share result** is optional and never opens automatically. A preview shows exactly the text being offered and a Cancel choice; only tapping **Share now** opens Android's native text Sharesheet. The same preview now wraps existing Solo/Daily share.
- v0.7.2 / versionCode26, same public QA-only signer, same `com.playereden.gridmates.wordsearch.qa` app ID and nearby protocol v6. No schema, wire, backend or contacts permissions added.

## QA
- Pure formatter tests: truthful friend-relative outcomes, no sharing incomplete/forged results, no private word ID or network secret content.
- Android Compose tests: preview and Cancel, friend presence and progress, Speed Duel private IDs not presented.
- Standard core/app tests, Android lint/compile/assets, plus actual API35 emulator install-over from stable-signed **v0.7.1 code25** to v0.7.2 code26 preserving Solo/Daily, profile, completed Shared Board Race **and** Speed Duel history; run full Android test suite.
- Test real two phones later for room discovery, Ready/GO fairness, final packet delivery, 320dp/200% font, device-native share targets and actual update. All cumulative PRs remain draft/unmerged.

**Future owner-reviewable proposals:** Offline PNG result cards, shared theme borrowing/priority and accessibility override, Nearby Connections/Bluetooth transport. None are released or implied by these documents.
