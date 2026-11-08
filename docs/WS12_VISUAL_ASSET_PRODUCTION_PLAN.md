> **Current WS-12E update (Nov 8, 2026):** This older initial plan is retained as historical intent; several source-only milestones are now actually implemented. Existing draft PRs [#92](https://github.com/PlayerEden/GridMates-Word-Search/pull/92), [#93](https://github.com/PlayerEden/GridMates-Word-Search/pull/93), [#94](https://github.com/PlayerEden/GridMates-Word-Search/pull/94) contain reviewed-for-prototype cover concepts, a deterministic Quiet Illustrated Paperbacks v1 SVG book/emblem system, and 9 matching chapter/bookmark/seal graphics. **None are owner-approved Android product art** and Adobe refinement has not been performed. The active production system adds a [Style Bible](WS12_ART_DIRECTION_STYLE_BIBLE.md), [Prompt/AI Playbook](WS12_AI_ASSET_PRODUCTION_PLAYBOOK.md), [18-slot + Collection queue](../design/asset-pipeline/production-queue-v1.json), [Anchor Plan](WS12_ANCHOR_ASSET_PLAN.md), [Bookshelf/Collection integration](WS12_BOOKS_COLLECTIONS_INTEGRATION_PLAN.md), [QA review protocol](WS12_VISUAL_QA_AND_APPROVAL_CHECKLIST.md) and [#91 child issues #102–#108](https://github.com/PlayerEden/GridMates-Word-Search/issues/91). The original twelve approved Woodland portraits stay untouched and no new production approved-art manifest entries have been added. Read the newer files for executable next steps.

# WS-12 — Visual Production Pipeline: AI Concepts, Adobe Refinement, Frozen Game Assets

**Planning status:** Documented in [WS-12 #91](https://github.com/PlayerEden/GridMates-Word-Search/issues/91). No artwork generated, reviewed or installed in the current APK as a result of this plan.

## Art direction

**GridMates: Word Search** should feel like a well-loved, well-organized paperback puzzle library: friendly, tactile, restrained, readable, and coherent across dozens of themes. The proposed **Puzzle Bookshelf (WS-11)** is a browsing collection of actual playable books; the existing **cosmetic Collection** handles avatars, fonts, achievement emblems, pencil/highlighter styles, borders, seals and bookmarks. **Don't conflate these two concepts in app navigation.**

The first three proposed design pilots are **Woodland Volume I, Space Volume I and Garden Volume I**. Names, book covers and any generated sprites are concept proposals, not currently approved assets.

### Production approach

| Stage | Tool/workflow | Output and approval |
| --- | --- | --- |
| 1. Design brief | Source-controlled theme, book format, style/contrast and visual hierarchy | Signed-off brief, not art |
| 2. Concept exploration | Image-generation tooling and reference boards where requested; multiple options | Unapproved concept files; record generation/source |
| 3. Editorial shortlist | Human selects coherent, non-distorted candidate concepts | Chosen source only, not auto-approved |
| 4. Adobe refinement | **Adobe Express**: cover/layout composition, editable titles/labels and export. **Adobe image editing/Photoshop-like tools**: cropping, resizing, color consistency, cleanup. **Illustrator/vectorization** where appropriate for icons and insignias; **Adobe Fonts** for exploration and licensed text choices | Editable master artwork and consistent derivatives; tools depend on access and input type |
| 5. Rights / visual review | Human check, license/usage/third-party asset review, grade-style review, consistency, good legibility on small phones | Explicit approved/not-approved decision |
| 6. Freeze and optimize | Versioned `asset_id`, source URL/hash, optimized image/vector export + SHA-256, proper notice and fallback | Only **approved** source hashes and exports are packable |
| 7. Android verification | Actual app preview at 320dp, landscape, large fonts, light/dark, slow/older devices; accessibility, FPS/memory and APK size | Evidence and physical signoff before release |
| 8. Further expansion | Repeat the same protocol for new volumes/themes and cosmetics | No overwriting earlier published IDs/files |

**Never dynamically regenerate artwork on a player's device.** Only stable, bundled/previously installed assets participate in books and profile metadata. Actual book titles, progress counts, chapter numbers and Continue buttons must remain real rendered text/controls, not baked into generated pixels.

### Three pilot visual briefs

- **Woodland I:** inviting natural-material book cover with simple leaf/woodland motif, warm paper and green-brown colors; no stereotypical AI patchwork; maintain source provenance and existing Woodland avatar identities.
- **Space I:** dark-blue/cosmic illustration with clear celestial motif and paper texture, not crowded technical labels; legible cover title, consistent series mark.
- **Garden I:** restrained botanical cover with tidy leaves/flowers, subtle warm paper and distinct floral emblem; natural details that survive small-card scaling.

Each pilot eventually includes: **cover foreground art**, *spine accent*, *small theme emblem*, *chapter marker*, *completion stamp/seal*, and optionally a *book-end/shelf decoration*. Favor **one adaptable source master** per book with multiple exports rather than unrelated generations for each sprite.

### Asset matrix / placement guidance

| Type | Suitable for AI concepts? | Recommended finish | Gameplay caveat |
| --- | --- | --- | --- |
| Book covers and spines | Yes | Adobe Express layout with editable real text | Title and progress remain legible |
| Theme emblems, collection badges | Yes, then curate | Adobe vector cleanup / scalable outlines | Tiny symbols must work at 24–48dp |
| Completion ribbons, stickers, bookmarks | Yes | Adobe cleanup, transparent export | Avoid cluttering the puzzle board |
| Shelf background and props | Yes, restrained | Crop/texture/detail cleanup | Fast fallback, no heavy scenery |
| Profile portrait additions | Concept exploration | Match existing approved 12 Woodland original-asset style; preserve original IDs | No unreviewed replacements |
| Pencil stroke/highlight trails | Usually prefer engineered vectors | Native UI and controlled sprite only if tested | Critical selection path visibility, no misleading marks |
| Buttons, navigation icons, error labels | Prefer native UI/vector icons | Native Android controls, accessible semantics | Avoid AI-generated text |

### Source-of-truth metadata

The planning schema is [`design/asset-pipeline/asset-manifest.schema.json`](../design/asset-pipeline/asset-manifest.schema.json). All future approved resources should have stable identifiers, `source_type` (original / generated / adapted / licensed), Adobe processing step notes if any, license information, review state, exact content hashes and localized alt labels/fallbacks. **The existence of a schema does not mean assets have been created or approved.**

### Integration sequence and dependencies

1. **WS-12A:** formal art bible and three concise cover briefs; compare UI design against established GridMates Sudoku aesthetics and actual imported approved Woodland portraits.
2. **WS-12B:** source-controlled manifest, review states, validation tests and immutable naming rules; no dynamic overwrite.
3. **WS-12C:** concept generation and *Adobe refinement* for the first three cover/spine/emblem packs, with visual owner approval before Android packaging. May be developed parallel to **WS-11A/B** bookshelf UX and book metadata.
4. **WS-12D:** additional earned cosmetics / themed portrait proposals, with collection categories distinct from books.
5. **WS-12E:** Android static asset packaging, missing-resource fallback, density and memory constraints; no changes to saved puzzle IDs.
6. **WS-12F:** source-hash QA, regression, 320dp and 200%-text snapshots, physical use acceptance and future expansion protocol.

Full playable **WS-11** chapters still depend on **WS-10's editorial-approved, immutable word packs**, not merely decorative cover art. The original V1–V3 saves and released puzzle content must remain exactly replayable.

## Tool and rights constraints

The connected Adobe toolset includes Adobe Express editing/export, image cropping/adjustments and image-to-vector workflows, plus Adobe Fonts research. **Each operation needs the appropriate file or supported URL, connection permissions and source assets**, and tools may change. Do not claim a project is already in Adobe or that Adobe has approved any use merely because tools exist. Third-party stock assets, fonts, photography and generated content each require their own rights and distribution assessment.

**Status remains planned** until real files, manifests, reviews and on-device QA exist.
