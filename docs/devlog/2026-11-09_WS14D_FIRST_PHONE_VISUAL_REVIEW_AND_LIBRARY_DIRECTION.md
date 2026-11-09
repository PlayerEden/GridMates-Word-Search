# 2026-11-09 — WS-14D Design Pass: First Phone Screenshots → Grounded Library Browser

**Status:** **PHYSICAL VISUAL REVIEW RECEIVED; PROPOSED DESIGN RECORDED; NO NEW BUILD OR FEATURE CODE IN THIS PASS.**

## Who, what, where, why

- **Who:** Owner installed the current illustrated Word Search QA APK, shared five real phone screenshots, and said the result `looks sick`. Assistant reviewed the screenshots and proposed next UX direction. This is feedback, **not final approval of a future carousel**.
- **What:** Evaluate the actual Play, Puzzle Books and Woodland detail screens with new Woodland/Space/Garden artwork. Determine what to improve before making/generating more art.
- **Where:** `Play` landing, `Puzzle Books` real Bookshelf, `Woodland` book detail; source `BookcaseBookshelfScreen.kt`, `BookLibraryFlow.kt`, `BookArt.kt`, and related native Compose views.
- **Why:** Cover art has succeeded as a visual direction but the disjointed spines and vertically separated shelves look awkward, and the owner wants a grounded, coherent presentation with a better left/right swipe balance.

## Where we came from

- Previous journal: [APK link recovery and development-log policy](2026-11-09_WS14C_APK_LINK_AND_PERSISTENT_JOURNAL.md).
- Source-tested illustrated QA build: commit `00d2a36d12c0410a5f04b780ff8c3fe83f69c841`, [Android QA #42576785096](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42576785096) success, [art QA #42576785170](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42576785170) success with six debug-only covers/spines and zero in release. [APK artifact](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42576785170/artifacts/13656070327).
- Starting current documentation/branch HEAD (before this design pass): `4c6f394d154e8019d7398af083c466354394421f`, draft [PR #131](https://github.com/PlayerEden/GridMates-Word-Search/pull/131), stacked on #130. `main` unchanged.
- Previous issue **"Library file not found"** concerned a ChatGPT attachment route. The subsequent owner screenshot evidence confirms they accessed an illustrated APK and could open a book/detail and save a bookmark. We should no longer describe the owner as having **never tested the APK**. **Broader install-over/save and two-phone regression testing remains unverified.**
- Last earlier owner-verified rollback build was v56; WS-14 visual hands-on is now demonstrated for a later QA candidate, but it is not yet cleared as general rollback/release.

## Observed owner screenshots

1. **Play**: a large resume/solo card, compact Daily/Play Together cards, QA books below fold, persistent bottom navigation. Cream/green/gold is restrained but not yet visually connected to the illustrated books.
2. **Puzzle Books**: shelf 1 contains Woodland and Space, shelf 2 has Garden alone. Each cover has a separate little spine to its right; lots of vertical whitespace and repeated title/chapter/progress labels.
3. **Woodland detail**: decorative cover appears beside native book title/volume; the detail page repeats QA/test wording, has large cards/scrolling and some wrapped play/bookmark actions. Owner successfully tapped bookmark, as shown by `Bookmark saved for this book`. No completed-page evidence was supplied.

## Decisions, proposed direction and scope

- **Owner preference confirmed:** retain attractive illustrated book family; be more grounded than a fully ornate game; distribute some restrained art throughout app; consider left/right swipe for browsing instead of long vertical scrolling everywhere.
- **Assistant recommendation (NOT yet owner-approved layout):** prototype one **horizontal centered-cover browser** with peeking neighbors, a small shelf, attached binding/shadow instead of detached right-side spine, one focused book with native progress/CTA, accessible visible nav controls, and reliable non-gesture navigation.
- **Keep vertical scrolling for chapter/page lists, records and settings**, and prevent global pager from interfering with word-search grid traces.
- **No extra artwork generated** and no image assets, book IDs, save files, profiles, or production app resources modified.
- Full design/acceptance specifications: [WS-14D Grounded Paperback Library UX](../WS14D_GROUNDED_LIBRARY_AND_HORIZONTAL_BROWSER_DESIGN_2026-11-09.md).

## Build/QA receipts

- **New APK from this pass:** none — design/documentation only.
- **Known passing functional artwork APK:** commit `00d2a36` and workflow links above. Last verified artwork remains debug QA.
- **Physical QA:** visual screenshots and bookmark interaction observed; **not** a complete gameplay, save migration or accessibility QA pass.
- **Fallback:** v56 earlier known owner-verified build; keep original Study A and existing bookshelf layout available for quick rollback.

## Next exact action

Implement **WS-14D QA-only horizontal bookshelf comparison** on the existing draft stack/new feature pass, with real cover art but **without** detached spines. Make the current vertical Bookshelf selectable as A and the horizontal browser as B, preserve BookCatalog/progress/bookmark plumbing, and test 320dp/360dp/200% fonts and book-opening interactions. Build a source-matched Android QA APK and add a fresh dated journal entry with test evidence. Request screenshots of the new browser and book detail before expanding art assets.

**New-chat restart:** Read [Journal](../DEVELOPMENT_JOURNAL.md), this entry, [WS-14D design plan](../WS14D_GROUNDED_LIBRARY_AND_HORIZONTAL_BROWSER_DESIGN_2026-11-09.md), [handoff](../WS14_ACTIVE_HANDOFF_2026-11-09.md), then inspect live PR #131, source and CI. Implement the ONE QA browser experiment and log it.
