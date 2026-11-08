# WS-12 — GridMates: Word Search Visual Style Bible v1

**Status: PROPOSED ART DIRECTION / SOURCE-CONTROLLED PROTOTYPES. No new assets are owner-approved for release.**
**Source of truth:** [WS-12 issue #91](https://github.com/PlayerEden/GridMates-Word-Search/issues/91) · [existing design rationale](WS12C_QUIET_PAPERBACK_STYLE_SPEC_2026-11-08.md) · [existing companion art](WS12D_MATCHING_PAPERBACK_ACCENTS_PLAN_2026-11-08.md).

## Product promise

"Good puzzles. Good company." The app should feel like an intentionally designed collection of familiar, comfortable paperback puzzle books: **thoughtful, readable, warm, slightly illustrated, never busy**. Older users must immediately understand where to play, where they stopped, what is locked, and how to get back. Good artwork supports those tasks; it does not compete with them.

**Default creative system:** *Quiet Illustrated Paperbacks v1*, derived from existing Set-B-style prototypes and controlled by [paperback-v1-tokens.json](../design/asset-pipeline/paperback-v1-tokens.json). This is our **prototype house style**, not an assertion that the owner has approved final covers. We already have deterministic source generators for 3 text-free covers, 3 emblems, 3 chapter marks, 3 bookmarks and 3 seals. **Do not regenerate them randomly or pretend the already-created prototypes are missing.**

## Visual constants — change only through a versioned design decision

| Token / property | Rule |
|---|---|
| Brand neutrals | Cream #F4F3EB, paper #FFFDF8, forest #214F3F, ink #263D36, muted #55665C, gold #8A6729, hairline rule #DFE0D6 |
| Illustration | Flat editorial / handmade block-print-like *shape* language, lightly tactile if needed; never photorealistic |
| Cover ratio | 3:4, source master 1200×1600; the title, volume, series wordmark, chapter and progress are **native Compose text**, not image pixels |
| Main motif | **One primary subject** per theme, simple silhouette, consistent weight, controlled negative space |
| Detail budget | Existing token rule max 18 main geometry shapes, max 8 colors, one subject; add detail only if visible at smallest intended placement |
| Line and edges | Stable outlined/filled silhouette treatment across the complete family; no random shifts between watercolor, 3D, vector, clay and anime |
| Light and texture | Quiet print feeling; no glowing particles, chrome, fake embossed leather, oily gradients, AI-looking distressed speckles or busy borders |
| Touch / reading | 48dp+ interactive targets, readable native text, no artwork-only status or tiny baked labels; preserve 320dp phone and 200% font scale |
| Theme identity | Keep composition and border proportions fixed; swap motif and theme palette, not the layout/typography/navigation |

### Theme-specific rules (same family, not three different games)

- **Woodland:** geometric pine grove, calm moss/eucalyptus greens, forest interior, soft ground line. Existing 12 approved Woodland **portrait images/IDs are frozen and must not be redrawn or replaced**.
- **Space:** one ringed planet with a few stars, muted slate blue and pale sky, **no** busy nebula/photo collage, accidental lettering or realistic sci-fi equipment.
- **Garden:** one flower/stem/leaves, clay/soil and muted cream, tidy simple botanical forms, **no** random butterflies or tangled ornate vines unless new motif reviewed.
- **Future themes:** add a tokenized accent palette and one reviewed motif; keep the same proportions and use native typography. A genuinely new visual grammar means a separately versioned style system, not drift inside v1.

## Component grammar — reuse a kit, never a pile of one-offs

**Book family:** cover (3:4), spine *color treatment* (do not generate tiny decorative copy), emblem, chapter marker (240×240 proof), bookmark (112×360 proof), completion seal (300×300 proof). Siblings use the SAME motif/colors and trim rules. Book title/volume/status/number/found percentage always stay Compose labels.

**Puzzle Bookshelf:** each book card remains a readable PaperPanel. An optional approved cover is a quiet anchor at the side/top of a card; the primary action ("Open book"/"Continue") remains a native button. No illustrated wallpaper behind the word grid. The chapter screen displays clean book-page hierarchy, not ornamental scaffolding.

**Collection:** portraits, fonts and achievements are a **separate cosmetic catalog**, not playable books. Preserve all approved original Woodland portraits and unlock counters. New portraits must match the established framing, character scale, soft outlines, eye placement and palette **as a family**, not merely share a background color. New font cards show actual licensed fonts rendered by the app, not mock letter shapes baked into an image. Achievement seals use a small emblem set with one silhouette/outer ring/proportion rule, not unrelated metallic trophy renders. Cosmetic icons may use the book motifs but must not reuse book-progress IDs.

**Interaction:** selection strokes, hint paths, success/failure, team status, badges and page completion have native color+text and accessibility semantics. Artwork is decoration and must have a text-only fallback.

## Non-negotiable anti-slop review rubric

A reviewer checks **every** candidate at native size and as a side-by-side family contact sheet:

1. **Subject accuracy:** exactly the intended recognizable motif; no extra fingers, phantom details, pseudo-letters, distorted animals or invented objects.
2. **Family consistency:** color palette, edge weight, padding, layout, lighting, texture, portrait proportions and icon silhouette agree with the anchor. Compare against **all** existing approved siblings, not just the prompt.
3. **Clarity:** recognizable at 32dp emblem, 48dp badge, 120–140dp book thumbnail; no tiny essential marks, visual noise or low-contrast art.
4. **No false typography:** no generated readable or fake text, volume labels, UI icons, random watermarks, imitation logos or trademark symbols.
5. **Product fit:** does not crowd native text, grids, word lists or touch controls; looks like this GridMates app, not an unrelated fantasy RPG or generic clipart pack.
6. **Legal/reproducibility:** source, tool/model/version when known, seed/parameters when known, rights/notice, edits, prompt revision, original file and hashes recorded. Similarity to a known copyrighted character/logo triggers rejection or legal review.
7. **Purpose:** asset solves a specific screen problem; no filler decorations merely because an image generator offered them.

**Reject** a piece when any of 1, 2, 4, 6 or 7 fails. Fix with controlled edit or deterministic vector revision before considering another generation. **No auto-approval based on a passing hash test; technical QA ≠ aesthetic approval.**

## When AI is appropriate

- **Default:** deterministic SVG/Compose for borders, simple icons, page markers, navigation and badges. This is cheaper, more reproducible, more accessible and more consistent.
- **AI candidate:** a richer standalone illustration genuinely improves a book cover, one new portrait batch or one rare special reward. Generate the isolated illustration **inside a predefined art window**. Never let the model redraw a complete app screen or the entire book family independently.
- **Human finishing:** select small shortlist, remove artifacts, align to exact palette/shapes, vectorize or retouch if worthwhile (Adobe optional), export stable master, review with a person and lock versioned hashes. AI generation is **design exploration**; editorial judgement and final packaging remain explicit.
- **No runtime AI:** all approved art is static and offline-compatible; do not require accounts/cloud/network to display a book.

## Approval and freeze boundary

This style bible governs **proposed** artwork but is not owner sign-off. The actual approved-asset registry [approved-assets.json](../design/asset-pipeline/approved-assets.json) is still empty. Art only enters that registry with source files, hashes, licenses, human review evidence and verified Android export. A changed approved asset must get a new asset ID or explicit compatible replacement/migration; old portrait IDs must remain accessible.

Release acceptance: viewed next to real app screenshots at 320dp/200% text, small icon sizes, portrait/landscape and older device; approved by owner; CI and licensing checks pass; art disappears gracefully without impairing gameplay.
