# WS-16AC — Owner WS16AB shelf acceptance, V2 cover-edge cleanup and shared wordmarks
**November 10, 2026 · design/QA decisions only · no art/code/APK created by this note**

## Owner's real-phone feedback (WS16AB)

Owner supplied a phone Home screenshot of the live painted WS16AB shelf with **Space** selected (developer 8dp grid / element bounds / safe-area overlay visible). Owner response: **“Oh yeah that's nice.”** The deeper walnut shelf and gold relief are accepted as the current preferred **visual direction**. The owner observes no notable separate shadow on the wall/parchment beyond the shelf's own shading, but explicitly says it looks good regardless. **Do not automatically add another ambient shadow** or revisit the old gray blocky rectangle. Keep the current shelf's original baked-in depth and the phone-proven size/alignment intact.

Screenshot visibly retains the carousel focus (Space 2 of 3, adjacent Woodland/Garden peeks), "Space · 0/3 pages complete", native menu actions, and the separate saved Solo status ("Saved: Woodland · 2/44 words"). A still image proves appearance, **not** tap/Back/resume or release readiness. Overlay lines/labels are *debug only* and must be disabled for final aesthetic evaluation.

Baseline: [WS16AB build receipt](2026-11-10_WS16AB_ACTUAL_PAINTED_SHELF_CANDIDATE.md), exact source `1b1bf49c`, [Android #42906649312](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42906649312) + [art isolation #42906649069](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42906649069) both SUCCESS, [Android artifact #13805212782](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42906649312/artifacts/13805212782). PR [#131](https://github.com/PlayerEden/GridMates-Word-Search/pull/131) remains draft.

## Owner-observed defects: books and branding
1. The existing **Woodland, Space and Garden** illustrated book jackets are visually strong, but show **small white/light fringe pixels and untrimmed material at some exterior edges**, especially noticeable against the parchment and near bottom edges at the shelf. The book sprites must have genuinely clean, straight, consistently aligned **3D outer silhouettes** without thin white matte halos.
2. The generated GridMates lettering on at least some book art contains an **® registered-mark symbol**. Owner confirms the GridMates name is **not registered**. **Remove ® in the artwork itself** (not just native Compose text), and audit *all* resulting V2 cover/spine assets; use **no mark** by default. The original artwork's V1 SHA-pinned sources must remain unchanged for rollback.
3. The **Home header's current two emblems are already separated** (`Ws16MBrandEmblem.kt`, `Ws16HomeHeader.kt`), but the visible words “GridMates” and “Word Search” are **ordinary native text alongside those small icons**. Owner asks for **actual full title artwork**, with one complete **GridMates family wordmark** reused across Word Search / Sudoku / future Crossword, and a **separate Word Search title wordmark** specific to this game. This is *not* the same as inventing yet another small emblem.

## Next art pass — implement in this order, without disturbing Home layout

### A. Versioned cover/spine V2 cleanup
- Work from original source sheets / layered masters with image editing, not a screen capture. Do **not** whiten or blur away fringe with a generic rectangle. Correct premultiplication / alpha-matte contamination, remove off-silhouette stray pixels, inspect at **1:1**, at the Android-rendered size and on **cream AND dark contrasting backdrops**. Confirm no 1–2px white band on the left/right/bottom perimeter or at spine seams.
- Keep a consistent top/bottom baseline, edge alignment, believable spine thickness and binding angle for **all three** covers; do **not** flatten them into perfectly front-facing featureless rectangles. Straight, intentional **book geometry** rather than jagged transparent crop artifacts.
- Remove any unverified ® baked in the artwork. Recreate corrected lettering so it blends organically with the printed cover banner, not a noticeable paint-over. Preserve owl/planet/garden art, themes, legibility, cover IDs and 3D perspective.
- Create **new versioned masters and exports** (V2; no overwrites), record exact hash/rights status, debug-only integration and rollback. Verify source manifest, alpha geometry and dark/cream checkerboard close-up samples.

### B. Reusable two-part *full* title-wordmark system
- **`GM_SHARED_GRIDMATES_WORDMARK_V1`:** one original transparent, professionally rendered **GridMates** title graphic including its distinctive lettering and, if appropriate, the established paired-puzzle family emblem. The **identical source/export** should later be reusable by Sudoku and Crossword via pinned bytes/hash—no game-specific pencil or word-search grid baked into this asset. No ®.
- **`GM_WS_WORD_SEARCH_WORDMARK_V1`:** a separate original transparent **Word Search** title graphic, styled as its partner, free to incorporate a puzzle grid/pencil motif. Can be changed without regenerating the family identity.
- Optional future sibling titles: `GM_SD_SUDOKU_WORDMARK_V1`, `GM_CW_CROSSWORD_WORDMARK_V1`; only after an approved shared family mark exists, **not** part of this Word Search pass.
- Current native text + existing WS16M emblem arrangement stays as an **immediate fallback**; new art may be enabled in debug for A/B. Use descriptive native accessibility semantics / fallback, preserve 48dp Profile/Menu tap targets, make title graphics non-interactive, and protect legibility at 320dp/360dp/412dp, larger font scale, Standard/Fullscreen and landscape. The two visual wordmark components must be **independently editable/exportable**, rather than one rigid screenshot of the entire header.
- Preserve familiar hierarchy: smaller family **GridMates** over larger game-specific **Word Search**. The end result should look more cohesive and professionally illustrated than two small generic icons with adjacent text, **without becoming busy or taller**.

### C. CI, phone review, release gates
- Actual generated image masters → PNG/WebP exports → source and output SHA-256 pinned → debug import → Android/unit/lint + independent art/release-isolation CI. No claim that the art exists until actual file bytes are committed.
- Examine books and title with developer overlay OFF first. Test carousel tap/swipe, in-book visuals, saved progress, Back, Standard vs Fullscreen, readability and overflow/large text. No data clear/uninstall for visual testing.
- Keep current **approved shelf / parchment / action cards unchanged**. No new shelf shadow, lantern, button rearrangement, Quick Play implementation, LAN change or cosmetic expansion in this visual cleanup. PR stays draft until source/owner approvals.

## Source of truth and outcome

Existing shared principles remain in [GridMates Shared Visual Identity and Motion](../GRIDMATES_SHARED_VISUAL_IDENTITY_AND_MOTION_2026-11-09.md), particularly **no ®**, shared family + game-specific identity, and pinned portable assets. This note adds the owner's explicit request for **complete independent title artworks** rather than emblems plus native type.

**This pass records a design decision and real-phone screenshot feedback only.** No new book covers, wordmarks, code changes, QA runs or APK were created; last verified install remains WS16AB. Next engineering pass: begin with V2 source-image edge/® corrections and the two actual title artwork masters; do not spend another cycle polishing the shelf.
