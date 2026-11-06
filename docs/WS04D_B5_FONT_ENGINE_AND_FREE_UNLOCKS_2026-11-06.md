# WS-04D-B5 — Font engine, free unlocks and Android acceptance

**Checkpoint:** November 6, 2026  
**Implementation:** [Draft PR #30](https://github.com/PlayerEden/GridMates-Word-Search/pull/30), stacked on B4 draft PR #28.  
**Owner decision:** All current cosmetic fonts are **free achievement unlocks**. No shop, ads-for-rewards, paid currency, IAP, purchase entitlement or monetization gate.

## Intended experience

Letter *ink* must sit in equal cells with visually balanced free space, even for narrow `I`, wide `W/M`, round `O/Q` and handwriting fonts. The player can preview and equip a bundled font without changing puzzle generation, positions, board sizes, hint allowance, saved puzzle identity, completed results or touch mapping.

The default remains a free accessible font. Unlocks derive from persistent achievement evidence and should reappear retroactively when prior completion records are loaded. Unknown or locked equipped-font IDs fall back to the default.

## B5 implementation under review

- `BoardGlyphPainter` draws glyphs on the same Canvas as the grid and found/highlight paths. It measures each font's uppercase ink rectangle, chooses a common safe text size per cell, and centers the actual bounds rather than centering a variable-width text layout box. The existing *whole-board* graphics-layer transform applies to the letter paint, lines and touch hit regions together.
- Font files are built **offline into the Android APK**. The CI importer checks a pinned `google/fonts` revision, exact Git blob identity, byte length, SHA-256 and uppercase A–Z coverage before copying each font and its matching full OFL notice into app resources/assets. See `design/font-library-approved-b5.json`.
- `Collection → Fonts` provides a sample puzzle-letter preview, earned/locked/equipped status, an equip action and a full-license dialog; `SoloStore` remembers the equipped font separately from the JSON gameplay save.
- The chosen fonts affect **puzzle letters only**. Independent UI typeface selection is not shipped in B5.

### Bundled styles and unlocks

| Style / font | Registry ID | Unlock evidence |
| --- | --- | --- |
| Readable Print — Atkinson Hyperlegible | `atkinson` | Available by default |
| Classic Typewriter — IBM Plex Mono | `plex` | First completed puzzle (`first_puzzle`) |
| Modern Print — DM Mono | `dmmono` | Five completed puzzles (`five_puzzles`) |
| Pencil Notes — Patrick Hand | `patrick` | First completed Daily (`one_daily`) |

Names and counts above describe the draft B5 implementation, not owner acceptance of font readability.

## Quality gates before next owner phone playtest

1. **Build:** Android CI succeeds on the exact PR head for core tests, app unit tests, lint, instrumentation-source compilation and QA APK assembly. Include identity `com.playereden.gridmates.wordsearch.qa`, versionCode 11, SHA-256 and 12 **original** Woodland assets.
2. **Font provenance:** The pinned upstream four .ttf files, their four original OFL notices, A–Z coverage and resource packaging pass verification. Store the exact resulting hashes and package evidence in the final B5 QA receipt.
3. **Actual Android execution:** The API-35 emulator must execute tests with reported counts and zero failures. A compile-only pass is insufficient. Runtime tests must check typeface ink bounds, font package/notice access, old progress, unlocks and equip-after-relaunch. Record the Android job/run URL.
4. **Physical visual review (owner):** Photograph Short 12×12, Standard 15×15 and Extended 20×20 at **100%, 145%, 185% and 250%** in Find and Move modes. Look especially at `I/W/M/O/Q`: equal cell margins, no neighboring ink collision, last row and column fully visible at 100%, no disappearing letters after zoom. Confirm selection highlights, tap endpoints and long diagonal drag stay aligned.
5. **Accessibility:** Test portrait, landscape, smaller/narrow screens, Android 200% system font and different display densities. UI labels must not cover the board, and the player can always revert to Readable Print. Previewed style must match on-board style on at least two actual phones.
6. **Gameplay regression:** Existing v1/v2 seeded Solo and Daily identifiers, achievements, hint budgets, current profile saves and original Woodland art must be unchanged. Repeat a completion, relaunch, switch fonts and verify no duplicate achievements or seed changes.

## Known boundaries / audit notes

- **Measured bounds are not physical screenshot proof.** The test asserts geometric ink centering; owner judgment is still required for optical balance, legibility and how small high-density 20×20 letters feel.
- **Native Android font scaling** changes the surrounding UI but intentionally should not change cell hit geometry or the bundled puzzle glyph's chosen safe size. Verify visual comfort and accessible alternatives on a physical phone.
- **Offline first.** No dynamic download, account or device-installed font should be required to use equipped fonts.
- **Privacy and purchases:** No new analytics, network requirement, purchases, ad-watch gates or virtual economy are introduced.
- **No merge yet.** PR #30 is deliberately stacked/draft while B4 owner-device acceptance is still open. Keep the QA package separately installed; stable signing/Google Play update migration belongs to WS-08.

## Next milestone after B5 approval

Nearby Co-op (WS-05): two phones on the same Wi-Fi, clear host/join, visibly present teammate, authoritative shared-grid claims and reconnect. Do not widen art/font collections before this core playability milestone.
