# WS-12C — Quiet Illustrated Paperbacks v1: Repeatable GridMates Art Language

**Design decision: November 8, 2026.** Choose the **repeatable 3-book structure of concept Set B**, radically simplify its artwork into **warm editorial / block-print-like vector motifs**, and **reject** the highly ornate shelf-diorama visuals of Set A for app-level navigation. This decision is based on **actual Word Search Compose style source**, not preference for a disconnected art trend.

**Status:** Executable, fully reproducible **design proof** only. It has been source-tested but **no owner-approved final artwork, Adobe document, Android release, or book-play implementation** is claimed.

## Why this rather than Set A or raw Set B

The current application already renders `GridMatesTheme.kt` with Cream `#F4F3EB`, Paper `#FFFDF8`, Forest `#214F3F`, Ink `#263D36`, Gold `#8A6729`, muted text `#55665C`, clean cards, **serif headings** and **sans-serif body**. These are part of the actual tested Compose source, not speculative colors. Source reference: [GridMatesTheme.kt in current V4 research stack](https://github.com/PlayerEden/GridMates-Word-Search/blob/feature/ws09f-f-spatial-and-variation-audit-2026-11-08/app/src/main/java/com/playereden/gridmates/wordsearch/GridMatesTheme.kt). [PaperbackUi.kt](https://github.com/PlayerEden/GridMates-Word-Search/blob/feature/ws09f-f-spatial-and-variation-audit-2026-11-08/app/src/main/java/com/playereden/gridmates/wordsearch/PaperbackUi.kt) shows the present calm page panels and thin rules.

- **Set A** is excellent as a moodboard but too reflective, three-dimensional and detail-heavy to coexist naturally with the simple game UI.
- **Set B** establishes a coherent series layout, but its rendered embossed gold, busy illustrations and text baked into images would be hard to reproduce precisely for 33+ themes.
- **Chosen Quiet Paperback v1** retains familiar 3:4 cover shape and modest themed illustration but uses **fixed geometry**, a small color system, strong accessible type hierarchy and one hand-controlled scalable icon per category. The app still feels like a paperback puzzle book.

## Locked proof components and rules

### System tokens
`design/asset-pipeline/paperback-v1-tokens.json` is the machine-readable template. Its **versioned `system_id=gridmates-ws.paperback-illustrated.v1`** defines:
- Artboard **1200×1600**, 3:4 aspect, fixed margins and illustration-safe zone.
- One consistent brand cream/paper/forest/ink/gold rule; four per-theme illustration tokens (main, dark, light, highlight).
- Single recognizable illustrated motif, not a new layout for each book.
- Unadorned backgrounds, slim spine color, delicate frame/rules, no fake leather, endless gold filigree, heavy gradients or unnecessary simulated lighting.
- **Real text separate from generated art**: GridMates branding, word-search label, theme title, volume, chapter and book progress must come from **Compose UI** with semantic accessibility labels, never from generated image text.
- No unsupported trademark registration symbols in decorative art; don't bake `®` or arbitrary tiny English words into images.
- Keep the existing Woodland collection icons/portraits and player unlock IDs untouched. Bookshelf is *playable content*; portrait/font Collections are a different navigation area.

### Current deterministic motifs

| Theme | Four-color token family | Reusable silhouette | Composition |
| --- | --- | --- | --- |
| Woodland | Forest green, darker pine, pale eucalyptus, muted moss | Pine grove + simple ground line | Central illustration window |
| Space | Slate/ink blue, dark midnight, pale sky blue, muted blue-gray | Ringed planet and three stars | Same exact window |
| Garden | Clay green-brown, darker soil, pale cream, muted wheat | Flower, leaves and stem | Same exact window |

**One new theme** means: add a valid entry to the versioned recipe and select an existing reviewed motif, or explicitly review/implement a new motif. It does **not** require generating a whole new layout or inventing a new color scheme. When motif design changes, introduce a **new style system version**, not a silent change to already frozen book asset IDs.

### Source-backed files and repeatability

- [Machine-readable art tokens](../design/asset-pipeline/paperback-v1-tokens.json)
- [Deterministic book/emblem/HTML renderer](../tools/asset_pipeline/render_paperback.py)
- [Nine reproducibility tests](../tools/asset_pipeline/test_paperback.py) plus eight previous asset-approval integrity tests
- [Empty approved production asset register](../design/asset-pipeline/approved-assets.json) remains empty
- [WS-12A earlier generated concept-set review](WS12A_PILOT_BOOK_ART_BIBLE_2026-11-08.md)

Run locally after checking out this design branch (does **not** require Adobe, an internet connection or image-generation subscription):

```bash
python3 -m unittest discover -s tools/asset_pipeline -p 'test_*.py' -v
python3 tools/asset_pipeline/render_paperback.py \
  --tokens design/asset-pipeline/paperback-v1-tokens.json \
  --output-dir /tmp/gridmates-paperback-v1
```

Open `/tmp/gridmates-paperback-v1/index.html`. The preview uses real editable HTML text over **three text-free decorative SVGs**; Android should use actual Compose text, not the HTML directly. The generator also creates **three same-style vector emblems** plus a SHA-256 report covering all seven generated preview files.

[Official WS-12C source-pinned GitHub QA, 17/17 tests](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42523233066) **SUCCESS**, source SHA `ab40b3e2a72c42c149ff824dfc02009265d21f4c`. The run also publishes a [7-file vector and HTML style-proof artifact](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42523233066/artifacts/13633884046) for side-by-side review (GitHub's 14-day retention, then reproducible via source). This QA checks deterministic bytes, app palette, exact shared geometry, editability, no unsupported trademark symbols, new-theme non-regression and absence of unapproved Android artwork; it **does not** certify human aesthetic preference or actual on-phone FPS.

## Reuse elsewhere around the app

- **Bookshelf cards:** retain normal PaperPanel and Material Typography; insert cover art as a lightweight illustration/card child; native heading/progress overlays remain the dominant UI.
- **Chapter cards:** reuse emblem family and background colors, but render chapter numbers in native text. Do not make the whole screen an illustration.
- **Collection badges and completion stamps:** derive from the same theme motif; one simpler small shape instead of new detailed paintings.
- **Bookmarks:** slender accent and one small emblem, preserving usable touch targets.
- **Profile cosmetics:** new generated artwork may follow the same palette/line weight, but original approved Woodland portraits and IDs are untouched.
- **Other GridMates games:** share conceptual typography/tokens if visually compatible, but do not assume cross-app collections/asset IDs are synced or modify the Sudoku app without its own review.

## Repeatable AI + Adobe micro-art workflow (optional, not required)

Where richer illustrations are genuinely needed, constrain generation to the **illustration window only**, rather than re-generating the entire cover:
1. Start with a fixed 1200×1600 layout and a chosen motif from the recipe. Describe only a **single** simplified printmaking/editorial illustration of that motif; ask for plain background, no letters, no product logo, no text.
2. Generate **one isolated illustrative motif**, not a cover with title and spine. Avoid ornate borders, shadows, 3D plastic, gold foil, stock-photo realism or unrelated objects.
3. Import the selected motif into Adobe Express or the available Adobe image/vector editor (when the connector is working). Remove AI artifacts, adjust edges and colors to the fixed tokens. Keep text independently editable in the page layout.
4. Save original concept, Adobe-edit master and all derived transparent PNG/SVG/WebP assets with exact SHA-256 and documented rights. Review at **140dp and 320dp phone width** with large fonts before actual integration.
5. Require owner visual approval and full source/size/contrast/licensing check. Only after this should assets enter the production registry and Android resources.

**A generated motif must never silently replace the deterministic silhouette library or a previously approved book cover.** Adobe is a refinement tool, not a required runtime dependency or proof of use rights.

## Future design-change checklist

- A new 30+ theme palette still honors core brand colors and the one-illustration rule.
- New motifs are limited to a known source-controlled vector library or individually reviewed generated art; no per-puzzle generation.
- Books can share UI geometry and localized text without having identical theme imagery.
- Premium-looking doesn't mean visual clutter: use whitespace, consistent proportions and readable type.
- Human phone playtesting may adjust art scale but must not silently rewrite existing published asset version/hash.
- Do not merge WS-12 art proof into public game content until actual resources, release rights, accessibility and Android performance are verified.

**Current bottom line:** The app now has a **code-defined, reproducible style prototype**; it does not yet have approved final book covers, Adobe exports, public bookshelf UI or a new playable APK.
