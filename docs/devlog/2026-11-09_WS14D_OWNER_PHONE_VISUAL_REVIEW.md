# WS-14D — Owner phone review: illustrated swipe library and book gameplay

**Date:** November 9, 2026 · **Outcome:** OWNER POSITIVE VISUAL FEEDBACK on new native swipe-library presentation. **Not** comprehensive gameplay, gesture, accessibility, save or release QA.

## Who / what / where / why

- **Owner:** Installed and photographed the WS-14D Android QA build on an actual Android phone, sent six screenshots, and said **"Oh yeah that's pretty sick."** This supports keeping the horizontal featured-book direction for the next refinement phase. It is *not* an explicit approval for immediate production merge, release, or all remaining behaviors.
- **Assistant:** Reviewed actual screenshots of **Puzzle Books (Woodland selected, Space selected)**, **Space book details** (header + scroll continuation), and **Space Page 1 and Page 3** gameplay. No code or art was changed during this feedback/logging pass.
- **Why:** Owner previously disliked detached tiny book spines and excessively tall shelves. The first interactive QA version was designed to replace those with one focused illustrated cover, peeking neighboring books, restrained motion and horizontal browsing while keeping older players' arrow controls.
- **Repository:** [PlayerEden/GridMates-Word-Search PR #131](https://github.com/PlayerEden/GridMates-Word-Search/pull/131), draft branch `feature/ws14b-style-anchor-and-art-direction-2026-11-08`.

## Previous checkpoint / tested build

[Previous WS-14D QA receipt](2026-11-09_WS14D_SWIPE_BROWSER_ANDROID_QA_RECEIPT.md): code source `a26fe8f20d5dde595d73937d43da1e813946a778`, [standard Android run #42584167358](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42584167358) **SUCCESS**, [artifact #13657879885](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42584167358/artifacts/13657879885) contains QA debug APK with six source-checked cover/spine sprites. Art release-isolation run #42584167412 stopped by external Kotlin compiler HTTP 504; **production/release sign-off remains pending**. Owner's new screenshots demonstrate an illustrated QA installation and real navigation but do not prove a source-package signature audit on-device.

## Screenshots — what the phone actually demonstrates

1. **Puzzle Books, Woodland selected**: a large in-focus illustrated book with neighboring Space cover peeking at right, a wood shelf rule, native `1 of 3 books` indicator, previous/next controls, a prominent Woodland title and a single Open book CTA. The previously separated tiny spine is absent from this view.
2. **Puzzle Books, Space selected**: centered Space cover, Woodland peek on left and Garden peek on right, `2 of 3 books` position indicator, and consistent native progress/Open action. This proves the **selection changes**; static screenshots do not prove whether selection occurred by swiping, tapping neighboring art or pressing arrows.
3. **Space book detail**: hero cover, native heading, progress, `Start reading`, chapters and page actions render. The main hero no longer draws the little right-hand spine. Visual remnants remain: `TEST BOOK`, `Getting Started · TEST`, and somewhat repetitive book theme labeling (`Space` vs `Outer Space`).
4. **Lower book detail**: three page cards across two chapters, reliable-looking native CTA targets, but labels such as `Play page 1` and `Bookmark page 1` wrap into two lines and chapter headings remain tall.
5. **Space Page 1 gameplay**: 12×12 grid, 12 words, trace/zoom/selection controls, on-device progress text visible. This screenshot proves the play page **renders**; 0 of 12 found means completion/drag gestures are not verified.
6. **Space Page 3 gameplay**: 20×20 grid, 24-word target, native zoom/fit controls, word list. This offers a useful dense-grid reference; letters/cell tap sizing look tight at phone width and deserve later finger-use and accessibility review. 0 of 24 found; no completion claim.

## What to retain, what to refine

**Keep:** current warm paper+forest ink palette; visually expressive but restrained covers; focused book and neighboring peeks; native status and one large Open action; ability to compare Classic shelves; no constant idle motion. Shared GridMates visual/motion identity remains a cross-game *design principle*, not a reason to transplant game-specific covers into Sudoku.

**Next priority, restrained UX polish:**
1. Clean the book-detail hierarchy: reduce redundant title/theme lines and development-only QA copy **where safe**, while retaining honest QA labeling in unreleased builds.
2. Improve page action button layout so `Play page 1`/`Bookmark page 1` labels don't awkwardly split at normal phone width; preserve 48dp targets and accessible full labels.
3. Check 20×20 grid word-tracing and zoom ergonomics on real phone (hit areas, readability, panning, no accidental selection), rather than pretending visual screenshots prove play comfort.
4. After functional review, distribute subtle illustration accents into Play/Collection using shared palette/materials, but **do not start generating another unrelated art family**.
5. Audit burned-in `®` and any native marks; default to plain `GridMates`, create versioned V2 artwork rather than overwriting previous hash-pinned originals.

## Tests and release gate

- **This pass:** no code changes, no APK build, no CI action consumed. Evidence is **owner-provided physical phone screenshots + positive high-level reaction**.
- **Verified now:** illustrated WS-14D views appear on owner's phone; Space book details and Page 1/Page 3 were entered successfully.
- **Not verified by screenshots:** exact swipe touch behavior vs arrows; word selection or puzzle completion; bookmark/save migration on this new QA version; two-phone co-op; landscape/200% fonts; separate art release-isolation workflow.
- **Fallback:** retain Classic shelves A/B choice, prior illustrated QA build, and earlier v56 owner-wide fallback. Leave PR #131 draft pending subsequent checks.

**New-chat restart:** Read `AGENTS.md`, `docs/DEVELOPMENT_JOURNAL.md`, this entry, and `docs/WS14_ACTIVE_HANDOFF_2026-11-09.md`; inspect live PR/CI. Next controlled source pass: clean awkward chapter/page CTA wrapping and QA-heavy detail text without touching saves or the puzzle engine; run verified Android QA, log receipts, then get owner phone screenshots. Do not broaden into unnecessary decoration.
