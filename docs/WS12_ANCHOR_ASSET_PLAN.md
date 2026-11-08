# WS-12 — Anchor Asset Plan: Woodland → Space → Garden

**Status: 15 reproducible proof slots already exist across three themes; 3 spines remain planned. All are unapproved artwork prototypes.**

## What is already available (DO NOT regenerate as random new styles)

Existing stacked art PR chain: [#92](https://github.com/PlayerEden/GridMates-Word-Search/pull/92) pilot concepts → [#93](https://github.com/PlayerEden/GridMates-Word-Search/pull/93) Quiet Illustrated Paperbacks v1 fixed tokens + 3 decorative cover SVGs and 3 emblems → [#94](https://github.com/PlayerEden/GridMates-Word-Search/pull/94) 3 chapter markers, 3 bookmarks and 3 completion seals.

These renderers are offline, text-free and reproducible:

- \`tools/asset_pipeline/render_paperback.py\` — three 1200×1600 decorative cover master SVGs and three emblems, plus an HTML native-text preview/receipts.
- \`tools/asset_pipeline/render_paperback_accents.py\` — three 240×240 chapter markers, three 112×360 bookmarks and three 300×300 seals.
- \`design/asset-pipeline/paperback-v1-tokens.json\` — one shared palette/geometry recipe and one separate theme motif.
- \`design/asset-pipeline/production-queue-v1.json\` — all 18 intended slots, their real prototype status and next review gate. Nothing is labeled owner-approved or imported into Android.

**Artistic choice:** don't commission six AI pieces for every book. The cover motif is one curated **source** whose geometry and visual weight are propagated programmatically to emblem/bookmark/seal/chapter. A richer AI illustration is an *optional* isolated substitute for the central motif, never a license to redesign the whole family.

## Phase 1 — Woodland anchor first

1. Render existing **Woodland cover + emblem + chapter mark + bookmark + seal** from current sources and arrange as a one-family contact sheet beside the owner's actual Bookshelf screenshot. **Spine is planned** and should derive from the same vector cover palette/geometry, not a different AI prompt.
2. Review at real sizes: 140dp book cover; 48dp emblem/chapter/stamp; 112×360 proof bookmark scaled down; 320dp-wide app screenshot; 200% native text. Collect three statuses: **keep / changes needed / reject** and 1–3 concrete reasons.
3. If *keep*, freeze owner-reviewed anchor reference IDs and source hashes and choose a first **QA-only** Android placement plan. If *changes needed*, alter tokens/geometry with a new design-prototype version and re-run deterministic test goldens. If *reject*, stay in concept, revise an isolated motif, and keep current app art unchanged.
4. Do not replace or recolor the **12 already approved original Woodland profile portraits**. A new visual system complements them through card framing/spacing and colors.

## Phase 2 — Space & Garden sibling review

Follow the **same** cover/shelf/card geometry; use orbit and flower-stem motif respectively. Show all three same-size covers on a single digital shelf in a mock or approved QA render. Reject variations that change border thickness, subject count, faux shadow/reflection, typography or scale. Do not create per-theme one-off layout components.

**Cross-family expected visual invariant:** layout grid, safe zones, motif scale class, icon framing, contrast, footer/header negative space, one-subject rule, no embedded text, same app-native title and button placement.

## Phase 3 — Collection anchor

A **separate** mini-approval checkpoint for new cosmetics:
- One portrait model sheet: shape/head/eyes/upper-body framing, one light/paper background, consistent 48dp avatar clarity. Existing 12 portrait binaries/unlocks and original artist sources stay untouched.
- One achievement badge master frame at 48dp with three simple motif variants, no fake stamped microtype.
- One completed-book native card concept, with optional vector seal shown only when progress rules say complete.
- Fonts previewed from actual licensed font binaries via Compose, with native "locked/owned/equipped" labels.

The first collection pack should be 3–6 additive portraits maximum, not an uncontrolled flood. Owners review the entire batch as a single row. New theme batches reuse the model sheet rather than re-describing every character from scratch.

## Acceptable reproducibility deliverables

- Original prompt and exact known model version/seed if available (unknown must be marked unavailable), inspiration/anchor IDs and reference hashes.
- Versioned SVG/PNG source masters in the repository with rights evidence; optional editing source (Adobe only if connection works).
- One contact sheet, desktop/thumb previews, mobile in-app screenshot and a manifest SHA-256 receipt.
- Explicit human approval of exact files, then an approved Android export with fallback and accessibility. **No art is approved just because automated tests pass.**

**Next concrete task:** review existing Woodland anchor sheet and test one small, reversible static book cover decoration in a QA-only Android branch. Do not gate gameplay improvements or the current user-installed test APK on decorative art.
