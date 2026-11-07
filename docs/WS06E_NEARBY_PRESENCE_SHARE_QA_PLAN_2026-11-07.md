# WS-06E — Friendly Nearby presence and optional share preview

**November 7, 2026 · Stacked development draft; automation and two-phone acceptance tracked separately.**

## Referenced Sudoku docs (read from PlayerEden/SudokuTogether at development 1818cd54e2f, not main)
- `docs/GRIDMATES_SHARE_RESULTS_UX_DESIGN.md`: Share result is a small optional secondary action after valid completion, ideally previewed before Android Sharesheet; no auto-nags, leaderboard or unverified Play URL.
- `docs/GRIDMATES_VERSUS_POST_MATCH_COMPARISON_DESIGN.md`: exact validated results, privacy boundary before finish, honest opponent progress; no fabricated “full board” or timeline. Real per-player move timing not recorded; Word Search should likewise avoid fake timestamps or time comparison.
- `docs/GRIDMATES_SHARED_COOP_BOARD_THEMES_DESIGN.md`: a potential shared appearance from the highest ranked equipped co-op board, keeping personal number/ink styles. **Word Search currently has bundled board fonts and Woodland portrait cosmetics, but not matching Sudoku board-theme tiers**; defer cosmetics transport, ranking or entitlement changes until assets, accessibility override and tier order are approved.
- `docs/GRIDMATES_SHAREABLE_CARDS_CONCEPT.md`: optional later downloadable PNG, preview and native Android Sharesheet without accounts, permission to contacts or partner identity by default.
- `docs/GRIDMATES_OFFLINE_NEARBY_BLUETOOTH_DESIGN.md`: no-router Nearby Connections feasibility, not a shipped feature. Do not alter stable LAN v6 before physical network tests.
- `docs/GRIDMATES_BRAND_AND_SERIES.md`: friendly, intentional real-life nearby play, not anti-internet, no aggressive social prompts.

## Implemented WS-06E slice
- A visible **Playing with someone nearby** companion card makes friend connection, their chosen display name (bounded to 24 chars), per-person contributions, and game stage visible. A shorter friend name is a display-only treatment; it isn't stored or shared externally. The card stacks its two progress entries at narrow widths or enlarged font scales.
- Shared Board Co-op and Race use publicly owned shared claims; Co-op shows total shared progress and, when known, the last public partner find. Speed Duel displays only own progress and opponent count, never the opponent's private found IDs, word list or paths.
- A separate, pure formatter makes truthful **finished and admitted** Co-op, Shared Board Race and Speed Duel summary text from a validated final snapshot; Score is relative to the local player, not assumed host win. No room ID/address/code, IP, chat, player identity, word answers, puzzle seed, store link or claims are exported.
- Optional **Share result** button on Nearby completed matches. Competitive modes enable only once the result was actually saved locally; incomplete rounds cannot be shared as victories. Co-op share never claims a private journal that doesn't exist.
- The native Android text Sharesheet now has an **explicit preview and Cancel** step, also for existing Solo and Daily share action. No auto-share and no backend. This phase exports **text**, not the future PNG card; copy/share image formats remain future design options.
- v0.7.2 / versionCode 26, same public **test-only** signer, Android package and LAN protocol v6. No new network permissions or wire fields.

## QA gates
- Pure unit: valid/invalid snapshot, proper local player outcomes and totals, no secrets in share text, no private Duel IDs.
- Android Compose: preview visible/cancel works, presence names and contributions accessible, private Duel only shows count.
- Standard Kotlin/core/app unit/lint/instrumentation compilation/approved asset and font checks.
- API35 emulator: install verified historical v0.7.1 stable-signed APK, preserve prior Solo/Daily/profile/Race/Duel records when updating in place to v0.7.2, run full instrumented regression, record artifact SHA and rollback checkpoint.
- Owner physical acceptance deferred: 2 phones on same Wi-Fi, 320dp/200% fonts, TalkBack, join/reconnect, guest ready/go, actual sharing app targets, correct results and safe install-over. All cumulative PRs stay draft/unmerged.

## Deliberately deferred
- PNG/shareable image cards, shared cosmetic board-theme tiers/borrowing, Bluetooth/Nearby Connections, opponent private word map comparison, online accounts or leaderboards. These require dedicated design and/or two-device tests, not silent integration from Sudoku.
