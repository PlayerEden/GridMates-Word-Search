# WS-14D — Grounded Paperback Library UX: Proposed Horizontal Browsing

**Date:** November 9, 2026 · **State:** design direction proposed after the owner's first physical Android art review. **Not an implementation or final owner-approved UI layout.**

## Where this came from

The owner installed the illustrated QA APK and shared five actual handset screenshots of **Play**, **Puzzle Books (two views)**, and **Woodland Book Details (two views)**. Their initial verdict: **"It looks sick"**, with clear refinement requests:
- Spread visual identity across the app, but **keep it grounded**—not every surface needs dramatic decoration.
- The new cover art works; showing a **separate spine beside each front cover feels weird**.
- The two-column bookshelf followed by a second shelf requires excessive **up/down scrolling** for only three books.
- Prefer **swiping left/right** for browsing certain visual collections, balanced against sensible vertical scrolling for content screens.
- Review navigation conventions across our GridMates games for consistency instead of redesigning a single screen in isolation.

The design below is a **recommendation for the next QA comparison**. It does not imply the owner approved a specific carousel mockup or shipping layout.

## Screenshot-specific findings (observed, not inferred)

**Play screen:** the Solo resume card is dominant with readable buttons. Daily and Nearby launchers have compact horizontal actions but their uppercase eyebrow labels wrap. The QA Puzzle Books entry is partly below the first fold; the four-tab bottom navigation is consistently accessible. Warm cream + green + gold has a recognizable look; screen hierarchy can be improved without filling it with new images.

**Puzzle Books shelf:** the three covers are vivid and cohesive (Woodland owl, Space ringed planet, Garden florals). Each **extra detached spine** appears as a thin mini-book floating to the right of its cover; it doesn't read as a physical attached binding. Two full covers on shelf 1, then Garden alone on shelf 2, leaves a lot of empty space and vertical travel. Book labels and progress repeat below the art; wordmark/theme lettering also exists on the cover itself.

**Woodland detail:** title, volume/chapter count, illustrated cover and progress are readable and functional. The page-list cards use generous vertical space. Some CTAs wrap (`Play page 1`/`Bookmark page 1`) and QA/test identifiers recur in headings. The owner could successfully navigate to detail and save a bookmark; no evidence yet of a full puzzle completion/save-migration pass.

## Design principles / boundaries

1. **Illustrations are the main event; UI supports them.** Keep the current book illustration family and soft paper/ink palette as the base. Add modest, purposeful accents only where they improve recognition and warmth. Less ornate trim; avoid making every panel or button a different art piece.
2. **Books look like books.** Front-facing covers should show **one attached book object**—optionally a narrow integrated left binding/shadow for depth. Don't render the spine as an independent object to the right. Retain source spine assets for a future side-facing/shelf-density treatment but don't force them into every view.
3. **Navigation direction reflects information structure.** Horizontal for browsing a finite visual library/collections, vertical for reading chapter lists, records, settings, long-form text and discovery feeds. Never steal left/right word tracing on the puzzle grid.
4. **One primary action per focal area.** The selected book has one clear `Open / Continue book` action, plus accessible secondary status; don't repeat another `Open book` under every tiny cover.
5. **Older/less-technical-player friendly.** Gestures are a convenience, not the only option: also allow tapping a neighboring book or native Previous/Next controls with clear book names, accessibility labels, stable focus, 48dp targets and non-gesture alternatives.
6. **Native accessibility and game data are sacred.** Keep saved puzzles, BookCatalog IDs/pages, progress/bookmarks, original Woodland portraits, Solo/Daily/Nearby, keyboard/back and TalkBack. No fake text/buttons embedded in artworks.
7. **Treat this as a QA study, not a sweeping redesign.** Preserve existing shelf as A comparison and implement new browse concept as B or an explicit QA toggle; no `main` merge from design review alone.

## Preferred layout: Featured Book Browser (first QA candidate)

- Header: **Puzzle Books** plus a short contextual line, with A/B style-layout comparison available in QA.
- **One prominent centered 3:4 front cover** at phone scale, visually attached dark/green binding edge or subtle native shadow **inside its own cover silhouette**. No free-floating spine.
- The **next and previous covers peek** from either side of the selected book. User swipes left/right or taps visible controls to move focus. Simple position indicator e.g. `1 of 3` / dots.
- **Under the focused cover only:** `Woodland`, `0 of 3 pages completed`, `2 chapters`, one large `Open book` or `Continue book` CTA. Keep status native; remove repetitive labels beneath each small image.
- A modest, softly drawn horizontal wooden shelf **under the covers** can preserve the tangible library motif. It must be subordinate to the covers; don't create huge stacked empty shelves.
- For a growing collection, use Compose horizontal pager or snap-capable lazy row with stable book IDs, not hard-coded theme count. Saving page position is optional (QA initially in-memory), and it must not rewrite book progress/save schema.
- Suggested first-phone-size experiment: focused cover approximately **150–180dp wide**; allow side-peek; adapt to 320dp and large fonts using available constraints, not fixed sizes that clip native text. Exact dimensions should be chosen from real screenshot QA rather than assumed correct.
- Landscape/tablets can display multiple covers without inventing new books. Maintain a single clear selected book and CTA.

**Alternative for comparison:** compact horizontal cover row over a persistent native detail/action panel, showing two-to-three visible books and a short selected-book preview. This may be preferable if collection grows far beyond three. Test both only if the first concept fails usability; don't spend time polishing multiple competing layouts without feedback.

## Where vertical scrolling still makes sense

- **Individual book view:** vertical chapter/page list, but condense overly tall heading and repeated QA labels; consider collapsed chapters for many pages. User should understand volume, chapter, page and progress.
- **Records, history and settings:** standard vertical list, with filters/tabs where valuable.
- **Word search board:** word-tracing gestures and any zoom/pan controls take priority over horizontal navigation. No global swipe pager interfering with puzzle input.
- **Play screen:** vertical scroll is okay for mode discovery, but prioritize Resume, Solo, Daily, Nearby and eventually Puzzle Books; avoid a home screen full of large repetitive cards.

## Art coverage: spread the love gradually

Phase 1 (priority): **Bookshelf interaction + book silhouette**, reuse the six available PNGs; don't generate new art yet.

Phase 2: detail-screen hierarchy—slightly more book-aware header and native chapter design, cleaner CTA sizing. Keep existing chapter emblems unless clear deficiencies appear.

Phase 3: selective Play/Collection illustration—for instance, **one quiet small vignette per major play mode or collection group**, not ornate borders everywhere. Preserve paper/ink and accessible native text. The profile collection's simple-icon discipline is a useful restraint but doesn't require all images to be flat icons.

Phase 4: only after real phone feedback decide whether revised cover/spine exports, lighter art intensity, expanded book themes, shelf decorations or custom assets are worth generating.

## Proposed WS-14D acceptance checks

- Does a new user discover there are **three books** and how to reach the next book *without* knowing swipe gestures?
- Can they find `Open book`/resume and read progress in one screen? Is it clearer and faster than the current vertically stacked shelf?
- Does each cover look physically coherent (no detached mini-spine), with correct aspect ratio and no clipped title?
- Does the page remain usable at **320dp/360dp**, font scale **200%**, portrait/landscape and screen-reader/back navigation?
- Do book detail, bookmark, page play, saved progress and existing portrait collections still behave exactly as before?
- Can we reject the carousel and revert to the old shelf **without migrating or losing saves**?
- No release/rights acceptance inferred from a visually liked mockup; record real owner screenshots before deciding.

## Next executable step

**Implement one QA-only alternate bookshelf layout** on a new draft source pass, using the **existing three covers** and existing book IDs. Make the integrated book silhouette, horizontal browse, position indicator and accessible taps functional. Keep old shelf layout as the comparison option; build a real APK with CI and test on the owner's phone. **Do not spend a pass generating more assets before this UI experiment.**

**Related:** [chronological journal](DEVELOPMENT_JOURNAL.md) · [current handoff](WS14_ACTIVE_HANDOFF_2026-11-09.md) · [art pipeline](ASSET_CREATION_GITHUB_INTEGRATION_PLAYBOOK.md) · [PR #131](https://github.com/PlayerEden/GridMates-Word-Search/pull/131).
