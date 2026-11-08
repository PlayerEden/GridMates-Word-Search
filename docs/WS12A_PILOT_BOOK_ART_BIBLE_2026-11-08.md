# WS-12A — GridMates Word Search Pilot Book Art Bible (Concept Pass #1)

**Date:** November 8, 2026  
**Scope:** 3 book-cover themes plus shared bookmark/stamp/chapter icon language for WS-11 Bookshelf.  
**Status:** **GENERATED CONCEPTS ONLY — UNREVIEWED/UNAPPROVED.** No Adobe refinement, shipped sprites, Android integration or changes to existing Woodland portrait IDs. See [concept metadata and exact SHA fingerprints](../design/asset-pipeline/pilot-concept-register.json).

## First generated exploration

Two cohesive exploratory *style boards* were generated and a review bundle was delivered as a **conversation attachment**, **not a source-controlled image asset yet**:
- **Set A — library diorama**: three hardcover themed books in a cozy shelf scene with matching bookmarks and decorative stamps. Very ornamental. Excellent moodboard; background detail too dense for direct tiny mobile controls.
- **Set B — coordinated book family**: three repeated-format illustrated covers with themed bookmarks, chapter emblems and completion medallions. More consistent starting point for a reusable book asset family, but the generated book titles and some labels are **baked into pixels** and must be replaced by editable native UI text in the final implementation.

Both source PNG SHA-256 values and all six independent cover crop/phone thumbnail hashes are recorded in `pilot-concept-register.json`. The corresponding binaries are in the conversation's **GridMates_WS12_Pilot_Book_Concepts.zip** attachment; that ZIP includes a full list of exact hashes. **GitHub currently contains the receipt and review pipeline, not those images.** Anyone importing the binary pack should verify its hashes before considering it.

The generated artwork suggests a richer, more ornamental style than our existing *simple paperback* goal. **Do not treat the results as final art direction without an owner review.** We should simplify decoration and balance visual warmth against list/grid readability.

## Consistent series rules

| System element | Proposed rule |
| --- | --- |
| Cover composition | Reusable 3:4 or ~0.74 portrait shape; same safe area, consistent GridMates mark and title placement |
| Visual hierarchy | Book title → large main theme illustration → Volume or chapter metadata → optional progress badge (as REAL text/UI, not generated pixels) |
| Materials | Cream paper, modest ink texture, restrained embossed/linen feel, fine borders; do not simulate excessive glitter or shiny gold on all elements |
| Woodland | Evergreen / moss + paper, a recognizable tree/animal/habitat motif |
| Space | Ink blue / twilight purple + paper, legible moon/planet/telescope motif |
| Garden | Sage / warm terracotta + paper, botanical leaf/flower motif |
| System consistency | Unified corner radius, emblem weight, safe margins, title type scale, texture intensity and chapter badge shape |
| Small screen | Look at a **140–180dp** book thumbnail on a 320dp wide phone, and test volume/page text at actual system text sizes |
| Accessibility | Book cover alt label, high-contrast real text overlay; animations optional and respecting reduced motion |
| Offline | All game art local and stable. No runtime AI generation, remote image dependencies or mandatory accounts |
| Legacy protection | Existing original Woodland portrait files and user-unlocked cosmetics must not be overwritten |

## Production asset matrix

| Type | Proposed master export | Minimum acceptance |
| --- | --- | --- |
| Book cover **decorative artwork only** | 1200×1600 source (or responsive equivalent) → optimized WebP at target resolutions | No baked UI labels in final overlay, recognizably different themes, no compression artifacts |
| Spine | Slim 1:4 aspect export, or native resizable composed UI | Theme emblem visible at small size; volume label remains native text |
| Theme emblem | Clean SVG/vector or small transparent PNG/WebP | Recognizable at 24/32/48dp and grayscale |
| Chapter icon | Native text number over vector badge shape | Chapter numbers are programmatic, not AI numerals |
| Completion seal | Reusable themed vector/stamp | Readable completion state using UI text, not color alone |
| Bookmark | Portrait sprite/narrow tab | Does not hide puzzle interaction, no accidental touch interceptions |
| Shelf background | Subtle theme-neutral background, optional | Must not reduce contrast, consume excessive memory or overwhelm cover art |
| Collection cosmetics | Portraits/stamps/highlighter/page borders | Distinct navigation from actual books, no loss of existing user unlocks |

## Concrete Adobe refinement pass (pending working Adobe tool access)

For **each selected theme concept**:

1. **Preserve** raw source PNG and SHA-256; import into Adobe Express/Creative Cloud or appropriate Adobe image editor, with original kept unchanged.
2. **Remove or mask baked-in book words** from decorative illustration where possible. Do not depend on image generation to render product titles consistently.
3. Make a layered composition with real editable text `GridMates`, `Word Search`, `WOODLAND/SPACE/GARDEN`, `Volume I`. Check spelling, hierarchy and readability at phone-card scale.
4. Harmonize theme color, crop, picture detail, book edges, border weight, spine padding, margins and lighting across the three books. Fewer decorative elements may be better.
5. Derive matching emblem and bookmark variants from one selected theme source, instead of separately generating inconsistent sprites.
6. Export preview PNG/WebP (and editable source when supported); record Adobe document URL/URN, operations, output hashes, actual font/stock usage and rights/redistribution details.
7. Require visual approval of ALL three as a set; if one is off-style, fix it before packaging any. Finalize accessible labels and vector fallbacks.
8. Package approved-only exports into a **separate draft PR** with app snapshots and same-signer upgrade QA. Never auto-approve from a nice-looking concept.

**Adobe status as of this pass:** Adobe connector initialization returned an internal error, so no Adobe document was created or edited. Don't invent an Adobe export link or editing completion.

## Human quality checks before final assets

- [ ] Review both style-board directions and choose **one** shared family.
- [ ] Decide whether books should be simpler/more paper-like than these ornate concept sheets.
- [ ] Eliminate incorrect/ambiguous/gibberish small decorative text. **Set A contains a generated ® after GridMates—remove it unless formal trademark registration is verified.** Do not embed a generated logo or legal mark into approved UI.
- [ ] Confirm no unlicensed names, trademarks, third-party photos or protected logos.
- [ ] Confirm exact portrait/icon provenance and preserve existing cosmetically unlocked items.
- [ ] Inspect 140dp/180dp/240dp thumbnails against simple metadata text, 320dp wide screens and 200% fonts.
- [ ] Final exported art is source-controlled in GitHub and passes the approved asset integrity gate.
- [ ] Confirm real-device app performance and locally persisted Bookshelf progress once WS-11 integration is ready.

## Traceability and source of truth

- [WS-12 issue](https://github.com/PlayerEden/GridMates-Word-Search/issues/91)
- [WS-11 Bookshelf](https://github.com/PlayerEden/GridMates-Word-Search/issues/90)
- [Manifest validation tool](../tools/asset_pipeline/validate.py)
- [Approved production registry](../design/asset-pipeline/approved-assets.json), intentionally **empty**
- [Complete WS-12 production plan](WS12_VISUAL_ASSET_PRODUCTION_PLAN.md)

**This is a concrete first reviewable art pass, not proof that these images are licensed, accepted, edited in Adobe, or live in the app.**
