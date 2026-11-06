# WS-04D-B5 — Font-neutral word grid and free achievements (implementation plan)

**Started:** November 6, 2026. **Source work:** [draft PR #30](https://github.com/PlayerEden/GridMates-Word-Search/pull/30), stacked on green B4 [PR #28](https://github.com/PlayerEden/GridMates-Word-Search/pull/28). **Owner phone approval and source CI at the new B5 commit still separate.**

## Product decision and player experience

Every cosmetic typeface is **free**, not purchasable; the existing achievement records decide access. No store, Google Play Billing, ads, reward watching, gacha or separate currency. Always provide a readable font from first launch. The player opens **Collection → Fonts** to preview a short letter grid in the real rendering style, then equips an earned font. Choosing a font changes no existing seeded letters, solution coordinates, hint allowance, scoring or saved completion.

Initial source-backed candidate set (each licensed under its publisher's **OFL-1.1**; exact binaries imported at Android build time):

| In-game style | Upstream typeface | Access |
| --- | --- | --- |
| Readable Print | Atkinson Hyperlegible | Always available |
| Classic Typewriter | IBM Plex Mono | **First Page**: complete first Solo or Daily |
| Modern Print | DM Mono | **Puzzle Regular**: complete five Solo/Daily puzzles |
| Pencil Notes | Patrick Hand | **Today Counts**: finish first Daily |

All achievements are already represented in durable Word Search profile/record data. Unlocks derive from those facts, including older completions. There is no extra inventory counter to duplicate or desynchronize.

## Implementation and font supply chain

- App renderer uses a single drawing surface with equal-width/height grid cells and computes visual ink bounds separately for each glyph. It chooses a **per-font safe size** from the widest/tallest A–Z glyph at 100-px sample scale and places each glyph by its actual ink center rather than Android's device-default text linebox.
- It retains the existing whole-board scale, marker drawings, cell drag/tap coordinates and accessible per-letter semantics. At 12×12/15×15/20×20 and zoom 100/145/185/250%, no glyph should cross a cell edge or appear stuck against a grid line.
- Android builds import TTF binaries and full OFL copyright/license text from **google/fonts commit `24ddc1f324cac5d9f5aedca126d298f56e3f1640`**, using four sparse family directories. The manifest [design/font-library-approved-b5.json](https://github.com/PlayerEden/GridMates-Word-Search/blob/feature/ws04d-b5-board-fonts-free-achievements-2026-11-06/design/font-library-approved-b5.json) pins original Git blob hashes, sizes, and source paths, and the importer computes SHA-256. The SHA values must also be pinned once verified in CI.
- Fonts are packaged inside the **APK** in `res/font/`, and full `OFL.txt` notices inside `assets/font_licenses/`; an in-app **License** dialog makes them accessible even when an unlock is still locked. None requires a network connection at runtime.
- The first approved source set is intentionally limited to four. Nunito, Lexend, Literata, JetBrains Mono and others remain *research candidates*, not shipped/approved just because their license text was located.
- Equipped font ID persists privately on this device. If an unknown or locked ID appears after a restore, the game falls back to the readable default without altering puzzles or granting a hidden unlock.

## QA gates and rollback

- [ ] Standard Android CI source build, AAPT package identity, 12 approved original Woodland assets, four verified TTF resources and all four OFL notices.
- [ ] All Kotlin JVM tests including real-achievement unlock logic, no false grants and puzzle-ID stability.
- [ ] Actual **API 35 Android emulator** execution of all instrumented tests (not merely source compilation): glyph ink centering for each A–Z at 20/32/48/72px; offline font/license presence; locked ID rejection, achievement award, equip and restart; Collection tab UI.
- [ ] Check original B4 Solo/Daily save and result screens still work; no changes to English puzzle generator versions or separate GridMates: Sudoku install.
- [ ] Record exact commit SHA, successful workflow run IDs, font and APK SHA-256 hashes, APK file size and protected checkpoint.
- [ ] On user's phone, compare **I, M, W, O, Q** across 12×12, 15×15, 20×20 and 100/145/185/250% at both Find and Move; ensure no clipped text, no off-center letters, and all rows visible at 100%.
- [ ] Compare Collection preview and the equipped real board with two phones, font scales/landscape, and older-reader accessibility. Decorative handwriting can be rejected for dense 20×20 if it fails readability.

## After B5

Verify B5 on the owner's phone, then continue with **WS-05 Nearby Co-op** (open-by-default same-Wi-Fi host/join and optional room code). WS-06 adds Shared Board Race and Speed Duel, then WS-07 brings final game/profile/content polishing, and WS-08 handles Google Play release readiness. Large word-library research, additional collections, Spanish and monetization remain future scope.

**Do not merge draft PRs or call this game Play-ready from a green build alone.** Keep source checks, emulator results and owner phone acceptance separate.
