# WS-14H — Compact Gameplay Header, Light-Bulb Hint and Theme Paper

**Owner direction:** November 9, 2026. **Status:** WS-14H1 implemented and **both GitHub CI workflows passed on source `7856282`**. [Verified QA APK build receipt](devlog/2026-11-09_WS14H_GREEN_ANDROID_ART_QA_AND_PHONE_HANDOFF.md) / [Artifact #13693073000](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42658174525/artifacts/13693073000). **WS-14H2 physical phone acceptance is next**; no final layout or release approval implied. This document is the current detailed roadmap for finishing page layout before scaling art production.

## One-sentence goal

**Make an ordinary Word Search page feel like it belongs to the selected illustrated book, without spending the player's screen on instructions, oversized utility buttons, or decoration that competes with the word grid.**

## Decisions already made by the owner

1. The current full-width **Hint** button is much too large. Replace it with **only a familiar light-bulb symbol**, located beside the book/page title. Keep its action recognizable through TalkBack label **Hint** and a real 48dp hit target; don't add an always-visible word label. In first-time onboarding later, explain the bulb and gestures naturally.
2. Delete persistent filler instruction **"Trace the words on your puzzle page."** On-page hint/found-word/error feedback remains visible **only after events**; essential saved/progress state remains native.
3. Give each book's gameplay page a **very subtle background paper tint**: Woodland pale sage (`#F1F3EA`), Space blue-gray (`#F0F2F7`), Garden floral cream (`#F6F2EA`). No wallpaper/large illustration.
4. Reuse a **small matching emblem**, chosen from the actual book theme, in the page heading if the device width/font scale allows. Completed/bookmarked status visuals have priority.
5. Keep the grid, its letter-cell contrast, tracing/hint coordinate overlays, word list and zoom mechanics separate from the book's decorative treatment. **Future unlockable board/number-like themes** are their own collection system, just as planned for Sudoku; book identity is not a board-skin override.
6. Keep reusable GridMates type, palette, icons and motion language consistent across series. No ® unless registered. No unnecessary UI text or decorative motion.

## Layout contract: first compact QA specimen

**Top-to-bottom (portrait):**

| Zone | Purpose | Planned content |
| --- | --- | --- |
| 1 | Return and state | Left **Back to book**, right native **Progress saved on device** / completed / practice state; no decorative art |
| 2 | Identity and Hint | Small theme emblem (if room) + native **Woodland · Page 3** title + **outlined light-bulb** control on right (48dp). Icon hidden during completed/practice states just as the old Hint action was |
| 3 | Progress | Native **N of M words found · elapsed time** and slim progress bar |
| 4 | Contextual feedback | *No default sentence or reserved row.* Only show meaningful hints, found words, errors, practice or completion notices |
| 5 | Board toolbar + grid | Existing **Select / − / Fit / +** and native touch/zoom grid; don't change its source or styling for book theme |
| 6 | Words list | Existing native word list, scrolled below the board; tiny future motif allowed only if it does not crowd 20×20 |

**Landscape / accessibility:** prioritise book/page text and hint hit target; omit emblem if insufficient space; allow title to wrap at most two lines; do not hide critical information. At 320dp and 200% text, inspect that title/bulb do not overlap, controls remain usable, and board is still reachable. Even a beautifully themed 20×20 board fails if word claims cannot be made.

**UI composition rules:** no invisible gesture-eating overlays; native button semantics stay active; bulb is a Canvas vector (no emoji/color mismatch, no separate PNG required); reduced-motion is valid static. The H prototype changes only QA art-visible book pages; **non-QA/release presentations must not be altered solely to show unapproved art.**

## WS-14H2 owner device findings and regular-book size boundary — Nov 9

[Actual phone/screenshots and source preset decision](devlog/2026-11-09_WS14H2_PHONE_SPACING_AND_BOOK_SIZE_CEILING.md): four Woodland 12/15/20 gameplay screenshots confirm the icon-only bulb and compact header have **much better on-phone breathing room**. **Regular illustrated books: 12×12 relaxed, 15×15 standard, 20×20 maximum.** Oversized **25×25/30×30** remain elective Free Play choices (already included via `soloPresets`), with an optional future **Large Puzzles** grouping. Do **not** remove larger generators, mutate frozen page recipes or move persisted sessions. 100×100 Marathon remains a separate unimplemented future concept.

**Remaining P0 gates:** the user saw “No hints available” at 0/24 on 20×20; core enforces 4 hint steps for this size and the UI currently conflates quota expiration with persistence/blocked states. Verify first hint on a fresh page, distinguish failure reasons before changing policy, and prove a saved 20×20 word claim/zoom. Space/Garden tint and accessibility/landscape are not yet owner-reviewed. No new art/production layout lock based on positive screenshots alone.

## WS-14J kickoff — cohesive in-game accents after owner layout endorsement

Owner wishes to move on from WS-14I to a small art-first pass. [WS-14J three-theme asset contract](WS14J_COMPACT_PAGE_ACCENT_ART_KIT_2026-11-09.md) is now the active next visual stage: produce one transparent Woodland style-anchor motif, then matching Space/Garden versions with actual source/image hashes, QA-only integration and device review. No giant collection or book-cover redesign. User's latest general “seems good” is not an explicit completion of the unresolved physical Hint/20×20 drag/save test; preserve it as open smoke QA rather than blocking basic art work.

## WS-14I functional hardening — completed source, phone acceptance pending

The WS-14H2 device screenshot exposed an old 4-tap total hint budget on 20×20. [WS-14I source-matched build receipt](docs/devlog/2026-11-09_WS14I_HINT_RELIABILITY_AND_20X20_TRACE_TEST_RECEIPT.md) now records **Android #42664221672 SUCCESS** and **Art QA #42664221664 SUCCESS** at code `3a3d0bc`. Hints now permit **three progressive steps per unfound word** (still bounded, persisted in unchanged GMWS v1, no free word claims); failure copy is specific. A new Compose test **compiles** 20×20 drag and store reopen behavior but still needs actual emulator/owner execution. [QA artifact #13696490440](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42664221672/artifacts/13696490440).

**Remaining H2 acceptance before new art:** user tests five Hint taps and actual found-word tracing/zoom/return/reopen on real phone. Don't present automation compilation as device gameplay success. 20×20 remains maximum regular book size and 25/30 optional Free Play. No new sprites yet.

## Milestones and acceptance gates

### WS-14H1 — Compact gameplay source and build (implementation underway)

- Fix the WS-14G nested `BoxWithConstraints.maxWidth` Kotlin scope bug (source `153cdab` was genuinely compile-failed).
- Implement single light-bulb button on title row, preserve exact existing `sessionStore.hint(bookId,pageId)` behavior and step messages; don't touch scoring or saved hint counters.
- Suppress empty/idle status text, keep dynamic feedback; add subtle theme background from `book.theme` for QA only; keep neutral WordGrid untouched.
- Add automated Android semantics/click test for the bulb and JVM color contract tests.
- **Gate:** standard Android unit/lint/instrumentation-code compilation + APK packaging **SUCCESS**, separate art release-isolation **SUCCESS** on exact code, six source-checked book cover/spine sprites remain debug-only. Save code SHA and artifact receipts. If failed, record actual compiler/network cause, fix and rerun once—not endless retries.

### WS-14H2 — Actual phone layout-lock check (next owner action)

- Install source-matched QA APK with backups and no forced uninstall. Review Woodland, Space and Garden page 1 and dense page 3.
- Confirm the hint **bulb is understandable**, clickable, accessible, and produces real persisted multi-stage hints; inspect final disabled state and hint messages.
- Compare vertical position and usable grid area against previous WS-14F phone screenshots; no excessive empty gaps.
- Prove an actual word claim on **12×12** and **20×20**, plus **Select**, +/- zoom, Fit, pinch/pan, progress after reopening. A screenshot showing zero found words is **not** word-tracing sign-off.
- Check portrait, landscape, 320/360/430dp, and large text (up to 200%); note density and color contrast. Accept the header as Layout v1 only after the owner tests these real interactions.

### WS-14I — Small shared art family (after H2 approval)

- Begin with **three independent transparent originals** that share the same illustrated language: Woodland fern/sprig, Space ringed planet/stars, Garden blossom/leaves. Same scale, padding, weight, edge treatment, lighting, alpha and provenance.
- Try the exact **same header slot** already proven by the existing emblems. Compare three screenshots and reject unnecessary ornaments; don't touch letter cells.
- Save original bytes, dimensions, artist/model/source, rights state, SHA-256, review record and export map; commit binaries through the documented GitHub blob/tree/commit pipeline. Source assets QA-only until explicit owner and release-rights approval.
- Optionally make **one neutral divider/tab** that can serve across Word Search/Sudoku/Crossword; defer excessive per-theme button skins/particles/background textures.

### WS-14J — Cohesive layout sign-off and wider visual spread

- Review full **Play → Swipe Library → Book Details → Puzzle Page → Collection** as a consistent product. The library stays horizontal with arrows; vertical navigation remains for chapters, records and settings.
- Define shared design tokens: paper colors, type sizes, button hit sizes, corner radii, spacing, emblem placement, simple selection/lift motion. Keep quiet controls and expressive covers.
- Gradually add small purposeful accents to Play/Collection and future titles using approved reusable neutral motifs; don't paste Word Search-specific covers into Sudoku.
- **Unlockable board theme collection is later** and explicitly separate from selected-book paper treatment. Profile IDs, stats, puzzles, LAN protocol and local saves are protected.

### WS-14K — Onboarding, accessibility, release QA (later)

- First-run walkthrough explains word-tracing, precise Select and bulb Hint with optional replay in Help; no permanent instructions taking screen height.
- Validate large font/TalkBack/switch access, slow or reduced animations, portrait/landscape, offline behavior, local storage migration, installation/signing, and release art isolation. No Google Play or production claim until all applicable gates pass.

## Guardrails, rollback and source of truth

- **Keep** old Classic shelves A/B, previous owner-tested WS-14D/F artifacts, original six book PNGs and 12 Woodland profile icons; do not mutate source image hashes in-place. Draft PR #131 remains unmerged.
- Never confuse **CI green**, **owner phone visual acceptance**, **actual puzzle interactions**, **rights approval**, or **production release**.
- The current feature-code checkpoint before the follow-up fix was `09490ef3d63df58f39843d34f7122fd24bbb221c`. Both [Android #42657230403](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42657230403) and [art #42657230390](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42657230390) were requested. Android caught a real `PathJoin`/Compose `StrokeJoin` typo; the follow-up commit fixes it. Check the live **latest** source SHA and its two workflows.
- Every code, art or build pass gets a dated journal entry and updated [Development Journal](DEVELOPMENT_JOURNAL.md) / [active handoff](WS14_ACTIVE_HANDOFF_2026-11-09.md).
- [Full-app WS-14 roadmap](WS14_FULL_APP_VISUAL_IDENTITY_AND_UI_PRODUCTION_ROADMAP_2026-11-08.md) · [WS-14G layout/asset plan](WS14G_LAYOUT_LOCK_AND_IN_GAME_THEME_ACCENT_KIT_2026-11-09.md) · [Shared GridMates art/motion design](GRIDMATES_SHARED_VISUAL_IDENTITY_AND_MOTION_2026-11-09.md) · [Art production playbook](ASSET_CREATION_GITHUB_INTEGRATION_PLAYBOOK.md).

**New-chat first action:** inspect PR #131 live HEAD and its standard/art CI, verify the real WS-14H APK, then get owner screenshots and successful word-claim evidence before starting the three-theme sprite kit.
