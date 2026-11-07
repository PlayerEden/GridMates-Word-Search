# WS-06G — Shared Co-op Board Looks (First Word Search Preview Catalog)

**November 7, 2026 · Cumulative draft on WS-06F v0.7.3, physical two-phone owner acceptance open.**

## Design reference, not copied cosmetic ownership

The owner's Sudoku [shared board theme concept](https://github.com/PlayerEden/SudokuTogether/blob/development/docs/GRIDMATES_SHARED_COOP_BOARD_THEMES_DESIGN.md) proposes that two players can admire an equipped board appearance together during Co-op, higher-ranked equipped theme selected deterministically, while each player's authored ink and font remain theirs. That proposal is not itself an existing Word Search feature or an authority to import Sudoku cosmetic IDs or unlock tiers.

Word Search previously had collectible **board fonts** and Woodland portraits, but **no earned or ranked board skin catalog**. This focused first pass introduces **three free, built-in visual previews** only: **Classic Paper** (`classic`), **Linen Sketch** (`linen`) and **Blueprint Study** (`blueprint`). Their technical preference order classic < linen < blueprint is purely a display tie-break for this QA prototype, **not a rarity label, store entitlement, purchase, achievement or proof of who owns better cosmetics**. Do not award/unlock any board skin, alter the permanent profile or copy Sudoku's specific IDs as entitlements.

## Round behavior and visuals

- Host and joining guest can each choose one of the three free preview looks before hosting or joining a Co-op room, with no extra multi-step setup. Each phone sends only its allowlisted ID, never an image/font file or arbitrary color.
- The host resolves **highest technical display preference**, ties favor host; both phones receive one canonical `boardLookId` in the Co-op snapshot. A guest's proposed look in the initial uncommitted hello/welcome does **not** reserve the seat. Only a confirmed receipt and accepted first admission freezes the look.
- After initial admission, reconnect must restore exactly the frozen look even if the guest selects something else; later claims and chat do not change it. This avoids mid-game appearance flicker and does not reset shared found words.
- The board letters/grid/background/frame use a cohesive paper/linen/soft blueprint palette. The existing locally selected **board font stays personal**. Host and guest claim colors (green vs blue) and puzzle rules/word list remain unchanged. Neither device learns the other's font choice.
- **Use Classic on my phone** is an accessibility/display override for that device only, never a wire action or shared-theme renegotiation; both devices still agree on the canonical look.
- **Solo/Daily, Shared Board Race and Speed Duel are unaffected**. Competitive modes accept only Classic in the host snapshot and retain their existing privacy/fairness/scoring. Theme selection applies to Co-op only.
- Local room look is temporary; no profile schema, collection unlock, backup record or cosmetic entitlement changes.

## Compatibility, security and QA

- Bump LAN protocol v6 → **v7** because the state packet now includes the mandatory canonical `look` field and the guest's initial hello may supply a preview ID. Both physical phones must use v7-compatible builds for ALL Nearby modes; older v6 peers cannot join these rooms. LAN remains casual trusted-Wi-Fi, not encrypted/cheatproof.
- v0.7.4 / versionCode **28**, same QA app ID and pinned public **test-only** certificate. No production signing.
- Pure Kotlin unit tests: host/guest choice order and fallback, do-not-freeze during unconfirmed offer, canonical board on admission, no mid-round/rejoin reset, invalid/competitive look rejection, Solo/profile isolation.
- Android codec and UI tests: v7 snapshot round-trip, malicious/unknown IDs fail closed, original offer and Joined agree, original claims/revisions survive reconnect, in-room palette contrast, locally reachable choice chips.
- Standard Android unit/core/lint/compile/font/art checks, full API35 emulator. The emulator installs **real historical signed v0.7.3 code27** APK and updates in place to code28, verifying prior Solo/Daily/profile and Race/Duel journals survive, then runs all Android instrumentation. Save exact source SHA, APK SHA-256 and rollback branch only once green.
- Real two-phone Wi-Fi host/guest theme convergence, forced reconnect, cramped/landscape/200% font, TalkBack, theme contrast and physical APK update are **owner-deferred**; no emulator result is a claim of physical acceptance.

## Future design direction

When an actual earned Word Search board collection exists, create explicit allowed skin IDs, genuine unlock rules and curated technical display priorities. Validate host and guest eligibility at the appropriate layer, without granting unowned skins or replacing either player's font. A later host override, per-player contrast preference and room-rematch theme exchange require separate review. **Do not call these free preview looks rare or unlockable.**

All cumulative PRs remain DRAFT/UNMERGED until owner physical testing.
