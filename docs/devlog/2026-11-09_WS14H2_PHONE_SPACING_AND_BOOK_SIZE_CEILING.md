# WS-14H2 — Owner phone visual review and regular book size ceiling

**Date:** November 9, 2026. **State:** User phone screenshots + product design decision recorded. **NO source-code changes, new image assets or new APK built in this pass.** Layout visually promising, but no word tracing, full hint behavior or persistence acceptance has been demonstrated.

## Who, what, where, why

- **Owner:** Supplied four actual Android screenshots showing Woodland 12×12 page 1, Woodland 15×15 page 2, and Woodland 20×20 page 3 (before and after using the bulb). Said: “So much better room wise. It doesn't feel all cramped or anything. Gives us more room to play with and such.” Also proposed 20×20 as a sensible regular puzzle-book ceiling, with bigger puzzles accessible through a separate mode outside the illustrated bookshelf.
- **Assistant:** Reviewed screenshots and inspected source-backed board-size presets, Solo selection and book-session hint rules. Recommends making the owner's 20×20 observation the regular book-content policy while preserving larger Free Play options and historical game state.
- **Why:** Regular books should feel like comfortable paperback puzzles; 20×20 is already at the limits of readable single-screen phone grid density. Larger puzzles should be an intentional optional challenge rather than unavoidable progression.
- **Where:** Source-backed [WS-14H compact gameplay roadmap](../WS14H_COMPACT_GAMEPLAY_HEADER_AND_THEME_ROADMAP_2026-11-09.md), [Master Game Design](../MASTER_GAME_DESIGN.md), current [draft PR #131](https://github.com/PlayerEden/GridMates-Word-Search/pull/131).

## Previous source/build checkpoint (do not mistake this doc pass for a new APK)

- Source `7856282d07cc440a4365132b8854f13a6fd57d1c`, [standard Android #42658174525](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42658174525) **SUCCESS**, [art/release-isolation #42658174462](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42658174462) **SUCCESS**.
- Verified QA artifact: [#13693073000](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42658174525/artifacts/13693073000) (ZIP containing `app-debug.apk`, package `com.playereden.gridmates.wordsearch.qa`, versionCode 57, APK SHA-256 `410716124b8c4ef35b2a934c097faf49aab51efb5a6fa425ff438c995220cac8`).
- Four owner screenshots are visual device evidence for this compiled QA build; owner did **not** report passing actual found-word gestures, Fit/zoom, completion, offline saving or two-phone tests. Do not claim those passed.

## Phone review — supported observations

1. **Compact header genuinely frees room:** no big full-width Hint button; native 48dp circular light bulb next to “Woodland · Page N”, stable back/save line, fewer placeholder lines, and board/word list fit as one clear flow.
2. **Theme treatment is restrained:** subtle pale-sage Woodland paper leaves the word grid neutral and readable. Different sizes show consistent paper treatment; Space/Garden colors are not shown in these four screenshots.
3. **Book identity uses current source art:** on page 1, bookmark ribbon art is displayed; on pages 2/3, a round emblem appears. This is a state-dependent current design, not evidence of a broken art import. Consider consistency when the final asset family is ready, without adding a big decorative header.
4. **Density:** 12×12 is visibly relaxed, 15×15 is a good everyday middle ground; 20×20 fills the width and has dense tiny letters. This is a credible regular-book *maximum* for the owner's phone. The zoom guide wraps to two lines, offering a possible future micro-polish opportunity.
5. **Hint feedback needs functional QA:** final screenshot on Woodland Page 3 shows “No hints available. Keep looking!” at **0/24 words found**. The bulb tap clearly triggered feedback, but that is not evidence that a *usable* hint was delivered.

## Existing source already supports the proposed separation

- `PuzzleBoardSize.bookPresets` in `core/.../WordSearchV2.kt` = **12×12 with 12 targets (BOOK_SHORT), 15×15 with 16 targets (BOOK_CLASSIC), 20×20 with 24 targets (BOOK_BIG)**. Frozen legacy sizes/ordinals/IDs remain untouched.
- `PuzzleBoardSize.largePresets` = **25×25 / 32 targets (BOOK_GRAND)** and **30×30 / 44 targets (BOOK_GIANT)**, generated under **WordSearchGeneratorV3** with different versioned IDs.
- `PuzzleBoardSize.soloPresets = bookPresets + largePresets`. `SoloSetupScreen.kt` already exposes those five size options in Free Play. **No new puzzle engine or new game mode is necessary to offer large boards now.**
- `BookCatalog.kt` currently permits valid Solo presets in book manifests; the **new editorial policy** should restrict *new regular illustrated book content* to <=20×20. Do **not** remove supported sizes from core enums or mutate published book/page recipes, hashes or saved sessions. Any historic large book page, if present, must continue loading safely.
- Later UI: consider a clear optional **Large Puzzles / Grand & Giant** grouping under Free Play; don't make this a mandatory book chapter or start a separate incompatible save store. A possible 100×100 Marathon is an *entirely separate, unimplemented future concept*, not part of this regular-book decision.

## Decision and next actionable milestones

**Design decision:** Regular illustrated Puzzle Books use **12×12, 15×15 and 20×20 max**. **25×25 and 30×30 remain optional Free Play / Large Puzzle boards**. This is a content-selection/product presentation boundary, not a technical rejection of larger sizes and not a breaking migration. Treat as the accepted direction unless the owner changes it after a later device test.

**P0 functional check before layout locked:**
1. On a fresh Woodland page, tap the bulb **once**: verify the actual first-stage hint, then tap repeatedly to observe what happens when the quota expires. Record number of taps, page identity and whether hints survive returning to the book.
2. Inspect current code: `BookPageSessionRules.hint` allows **4 total hint steps** for 20×20, then returns `null`. `BookPageSessionStore.hint` also returns `null` when saves are blocked or a hint can't be committed. `BookPageGameplayScreen` renders the same generic “No hints available” for every null. At 0/24 found, the screenshot alone cannot tell which happened. Improve explanation and reconsider hard limits (or an unlimited accessible tier) in a **separate tested gameplay pass**, without silently changing scoring/hint persistence.
3. Actually find at least one word via drag and via precision Select on 20×20; test +/−, Fit and pinch/pan, then exit/reopen and confirm the word remains found. Save data must remain intact.
4. Check the new bulb at large text / landscape and Space/Garden book colors before marking page chrome **Layout v1 accepted**.

**After that:** move to planned WS-14I: three tiny, cohesive independent gameplay accents from versioned transparent originals, compare against existing emblems on the approved layout. Keep the board neutral; unlockable board skins remain a separate future collection.

**Handoff rule:** Do not reopen the regular book size debate or re-run unchanged builds in a new chat. Continue at this visual acceptance checkpoint and the functional hint/claim verification. Every subsequent code/art pass gets a dated indexed journal entry and source-matched build receipt.
