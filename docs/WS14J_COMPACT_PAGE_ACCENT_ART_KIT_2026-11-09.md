# WS-14J — Small In-Game Theme-Accent Kit (first art sprint)

**Status: ART DIRECTION / FILE CONTRACT READY; no standalone asset PNGs imported, approved or installed by this document.** Owner said "Seems good. Let's move on" after WS-14I hint/20×20 build; earlier owner screenshots specifically approved the roomy compact light-bulb header. New phone word-claim test was requested but no explicit drag/save/reopen results have been reported. Preserve this as a **non-blocking open QA gate** while beginning *design* work; do not label physical game functionality or the new art as owner-approved.

## Goal and first three targets

Work toward one restrained and coherent paperback-look **three-piece** transparent family for the selected book theme. Do **not** produce another full-screen concept image, overlay, wallpaper or replacement book jacket. No baked-in UI words/®/numbers. Each independent asset uses one shared safe box and may appear at 24–36dp near the page heading; actual bitmap export should be tested for legibility on 320dp and 360dp phones.

| Proposed stable ID | Theme | Single isolated illustration | Palette guardrail |
| --- | --- | --- | --- |
| `PAGE_ACCENT_WOODLAND_V1` | Woodland | A small fern/oak-leaf sprig with one tiny mushroom | Quiet sage/forest green, muted earth brown |
| `PAGE_ACCENT_SPACE_V1` | Space | A tiny ringed planet with no more than two stars | Muted navy/indigo, antique warm gold |
| `PAGE_ACCENT_GARDEN_V1` | Garden | A small blossom with leaves, optional butterfly | Sage, subtle peach/cream |

**Shared composition:** transparent background, crisp near-ink outline, hand-painted gouache/watercolor edge rather than glossy 3D, restrained shadow cast down-right, same silhouette density, same subject-to-frame ratio and same padding. Match the illustrated *book covers* in feel, but keep micro-details sparse enough for actual 32dp view. At enlarged text/short widths the native title takes priority and the motif disappears gracefully. Use existing WS-12 theme emblems as reversible fallback; do not replace profile icon IDs.

**Candidate source slot:** book/page title row, immediately to the left of native `Book · Page N`, with the **light-bulb Hint on the right**. Exactly one theme motif per page and no decoration over the word grid, board controls or selection hit zone. Book-selected pale tint stays. Do not compete with existing bookmark/completion art.

## Reproducible production and approval sequence

1. Produce ONE **Woodland style-anchor master** first as a standalone unlettered illustration (not a dashboard/mockup). Visually review transparency, consistent line weight and 32dp icon readability.
2. After deciding if the style-anchor works, make **Space and Garden** at the exact same dimensions/visual density. Do not silently reuse incompatible illustrations or sample internet stock art.
3. Archive each **real original PNG** separately under `design/visual-system/masters/` with versioned filenames; export independent transparent source-backed assets under `design/visual-system/exports/`. Proposed 256px or 512px master for each, with density-specific Android export only after measured preview.
4. Compute real file SHA-256, dimensions, alpha bounds and source/provenance for each. Add separate `PAGE_ACCENT_..._V1` entries to the art register as `GENERATED_UNREVIEWED` until owner accepts; do not invent hashes or rights approval. Follow [art creation and binary GitHub pipeline](ASSET_CREATION_GITHUB_INTEGRATION_PLAYBOOK.md).
5. Build QA-only Compose integration by selecting local asset using the real `book.theme`. Keep all dynamic type, actions and hints native; a missing optional image must fail to existing emblem/text rather than crashing.
6. Extend both GitHub workflows to assert **the exact approved candidate IDs in debug** and **none in release**. Then compare actual Woodland/Space/Garden screenshots on a handset. Save full build receipts and the owner's decision before bulk generating additional art.
7. Only after comparing those three accents, consider ONE reusable neutral paper divider or completion flourish. New unlockable board-skin collections are a **separate roadmap**, not this book theme asset family.

## Current state and safe checkpoint

- [WS-14I code/Android/art QA](devlog/2026-11-09_WS14I_HINT_RELIABILITY_AND_20X20_TRACE_TEST_RECEIPT.md) **green on code SHA `3a3d0bc`**, QA APK [#13696490440](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42664221672/artifacts/13696490440). New hint steps are persisted per unfound word; touch-injection Android test code compiled, but **physical 20×20 drag/save was not explicitly verified**.
- Existing owner-reviewed compact header and book art remain baseline. Draft PR [#131](https://github.com/PlayerEden/GridMates-Word-Search/pull/131) stays draft, stacked on PR #130. Don't clear/migrate saves, change book content hashes, overwrite six book covers/spines or the original 12 Woodland portraits.
- Any illustrative dashboard/chat preview generated while discussing this plan is **not a source asset, a gameplay screenshot, a compilation record or evidence of runtime QA**; don't import one as a motif or copy any unverified text/status from it.

**Next single execution action:** create and inspect a properly isolated **Woodland page-accent V1 original** with clean transparent pixels (not a collage or UI mockup), transfer its actual binary to the GitHub master path with real SHA receipt, then use it as the style anchor for Space/Garden. Stop and report honestly if the generated image/file cannot be transferred; a draft plan alone is not binary art.
