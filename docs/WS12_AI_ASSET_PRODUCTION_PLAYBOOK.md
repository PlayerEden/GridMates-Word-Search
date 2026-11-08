# WS-12 — AI-Assisted Art Production Playbook v1

**Intent:** consistent visual design across a potentially large GridMates puzzle library and cosmetic Collection — **not** an endless feed of unrelated AI images.
**Authority:** [Visual Style Bible](WS12_ART_DIRECTION_STYLE_BIBLE.md) · [production queue](../design/asset-pipeline/production-queue-v1.json) · [existing token system](../design/asset-pipeline/paperback-v1-tokens.json).

## Choose the cheapest faithful medium *before* generating

1. **Compose/native text** for book titles, dates, scores, progress, buttons, hints, labels, accessibility, numbers, names and fonts.
2. **Deterministic SVG** for book shape/spine rules, emblems, chapter marks, bookmark ribbons, seals, basic achievements and standard frames. Reuse the existing WS-12C/D renderers; do not ask AI to redraw them.
3. **Generative isolated illustration** only when it adds a distinct character/scene and can match a locked anchor. Output is an unapproved *concept*, not a final Android asset.
4. **Adobe or other editing tools** only when authorized/available and materially useful for manual cleanup, color corrections, crop, vector tracing or layered source. Tool connectivity and use rights are separately verified; never claim edits happened when they did not.

## Three-pass workflow (small batches, controlled learning)

**Pass 0 — brief and frozen anchors:** identify asset ID, exact screen role and smallest display size, theme, one subject, approved/prototype reference pack, native UI text zones, target palette and maximum detail. Choose *one* master system/version. Save brief and reference hashes in GitHub.

**Pass 1 — controlled concept batch:** at most **2–4 alternatives** for the *single motif only*. All candidates share exact dimensions, clear margins, same angle, same background/alpha treatment, no text or unrelated props. Record prompt version, model/provider/version if surfaced, seed if exposed, reference hashes and source images. Unknown settings should say "unavailable", never fabricated. Reject off-style images immediately.

**Pass 2 — human shortlist and controlled edits:** compare candidates together on a contact sheet beside Woodland/Space/Garden siblings and the current Android screen. Pick **one** promising version; fix defects with targeted edits to preserve composition rather than repeatedly rewriting the whole picture. If two targeted edits still drift, return to deterministic vector source or re-brief.

**Pass 3 — production and packaging:** save original input and final layered source, rights decision, any licensed notices, actual export, SHA-256 receipts and before/after thumbnails. Owner checks small-scale reading and consistency. Only owner-approved, metadata-complete files enter production \`approved-assets.json\` and Android resources. Verify release builds *do not* accidentally bundle prototype/test art.

### Reusable image-model brief template (for a single illustrative motif)

> Task: **one isolated decorative motif**, NOT a full app screen, book cover, title, button or lettered badge.
>
> GridMates: Word Search paperback series, system \`gridmates-ws.paperback-illustrated.v1\`. Subject: **{one approved motif}** for **{theme}**. Camera: straight-on / flat orthographic editorial print illustration. Output: text-free isolated artwork, clean simple contours, generous negative space, centered in the reference illustration window. Match **{reference asset IDs + verified hashes}** for stroke weight, silhouette complexity, shape spacing, quiet print texture and calm tone. Use only **{four palette tokens}** plus fixed brand paper/ink. No logo, labels, letters, numbers, fake writing, watermarks, extra props, rays, 3D lighting, fantasy ornament, uncontrolled grain or photorealism. Keep a single clear subject recognizable at **48dp**.
>
> Negative review criteria: different illustration style from approved anchor, extra limbs/objects, muddy palette, thin illegible geometry, unexpected text, overworked shiny treatment, inconsistent cover geometry.
>
> Candidate status: **concept — requires human review**.

**IMPORTANT:** Populate all braces with real known IDs/palettes before using with an image model. If no anchor has been owner-approved, use the existing *prototype* WS-12C/D reference and mark the output prototype too; don't call it a final approved asset.

## Asset-family production rules

| Family | Source strategy | Mandatory tests |
| --- | --- | --- |
| Woodland/Space/Garden covers | Existing deterministic 1200×1600 text-free SVG recipe; optional single AI illustration inside art window | identical safe zones; three covers side by side; actual native title placement |
| Spines | Programmatic palette bands and consistent width, title native outside image when needed | tiny thumbnail and narrow shelf; no baked unreadable type |
| Chapter symbols | Existing text-free 240px source, native chapter number | recognizable at 32/48dp, 200% text |
| Bookmarks | Existing 112×360 SVG ribbons + real bookmarked status | status accessible without colored ribbon |
| Completion seals | Existing 300×300 SVG, native completion count | no "completed" claim until actual book is finished |
| Original Woodland portraits | **KEEP unchanged**; freeze source and unlock IDs | 12 original portraits intact, update/install compatibility |
| New portrait collection | One approved silhouette/framing sheet first; 3–6 variations sharing eye/head scale/lighting | contact sheet; 48/96dp thumbnails; no odd anatomy, duplicate characters |
| Achievement emblems | Same ring thickness and logo footprint, limited motif library | clear silhouette at 24/48dp, rarity/status not color-only |
| Font collection cards | Native licensed font preview + existing paper frame | real rendering at high font scale; license notice |
| Empty states/shelf ornaments | Prefer one reusable SVG/drawn line; never behind active letter grid | smaller Android widths and screen reader |

## Decision gates for every asset

1. **Functional need and placement:** identify the exact screen, sizes, expected hierarchy and fallback.
2. **Style and source:** follows v1 token/motif; output and source are genuinely reproducible; no undisclosed stock/licensed material.
3. **Visual comparison:** judge with cover siblings and Collection side-by-side, not isolated in a lightbox.
4. **Accessibility:** 320dp, 200%-font and 32/48/140dp previews; actual text stays native and button contrast/touch is not reduced.
5. **Owner review:** thumbs up/needs changes/rejected with issue, screenshot and date; AI cannot approve its own outputs.
6. **Build QA:** protected frozen originals, hash checks, opt-in approved registry, Play packaging, install-over and save/schema compatibility.

### What goes in each review note

\`asset_id | theme | usage | prototype reference | source file SHA256 | editor/version | prompt revision | rights + notice | reviewed screenshot/sizes | decision + reviewer + date | follow-up\`.

Record reasons for rejection. Failed concepts remain *out of the Android app* and the production registry. Do not silently delete older approved variants; deprecate with migration and rollback.

## Command-line starting point

From a checkout of this art branch, without internet or an image-generation API:

\`\`\`bash
python3 -m unittest discover -s tools/asset_pipeline -p 'test_*.py' -v
python3 tools/asset_pipeline/validate.py --repo-root .
python3 tools/asset_pipeline/validate_production_queue.py --repo-root .
python3 tools/asset_pipeline/render_paperback.py --tokens design/asset-pipeline/paperback-v1-tokens.json --output-dir /tmp/ws12-covers
python3 tools/asset_pipeline/render_paperback_accents.py --tokens design/asset-pipeline/paperback-v1-tokens.json --output-dir /tmp/ws12-accents
\`\`\`

Outputs are source-reproducible **review proofs**, not an assertion of owner approval or final app artwork. Re-run after any recipe change and compare SHA receipts. Never share binary assets or font files outside properly licensed build artifacts.
