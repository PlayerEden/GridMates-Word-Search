# WS-16G2 — Batch 1 Painterly Asset Production Brief
**GridMates: Word Search** · November 9, 2026 · Draft PR #131 · **SPECIFICATION ONLY — NO NEW PAINTERLY ART OR APK IN THIS PASS**

This is the **production brief**, not another home-screen redesign. It translates the owner's demonstrated reference (wooden illustrated shelf, warm parchment, book covers, botanical detail, vintage illustrated mode buttons) into **20 concrete rendering roles**. Several roles already have separate authored SVG references and analogous native Canvas output: keep those, then produce richer *actual* painterly masters/exports under new versioned IDs as they are accepted. Existing book PNGs, user progress and gameplay must not change.

## Design north star and constraints
- **Look:** editorial watercolor/gouache and selective inked edges; warm storybook paperback, grounded objects, slightly imperfect hand-painted wood, no generic flat icon pack, polished but not busy.
- **Light:** soft diffuse top-left warm light; underside and contact shadows fall slightly down/right; no hard black 3D bevels, glass/glossy plastic, neon, sharpened AI hallucinated seams.
- **Palette:** paper #F9F3E7, card ivory #FFF9EE, forest ink #214E3C, deep green #1D563E, antique gold #AB8540, walnut warm #B88C58 / dark #715338. Colors are working references, not a demand to repaint the already-loved original book covers.
- **Image source:** original art drawn/generated specifically for this project. Treat the owner's full-screen example as **inspiration**, never crop it into supposed transparent component source files.
- **No copy baked into art:** labels, book names, counts, dates, navigation, button actions and type remain native Compose. Do not create ® or a fake registered mark. Illustrations should have clean alpha edges, no white matte.
- **Component boundaries:** background → optional corner decor → backboard/shelf/shadow → real interactive book covers → live captions/buttons/actions → transient feedback. The shelf/shadows/ornaments never intercept touches. Do not put graphics over book focus or the 48dp Profile/Menu targets.
- **Responsiveness:** 320/360/393/411dp phone widths, 1.0/1.5/2.0 font scales, landscape/tablet 700–1200dp. Background must fill viewport, 9-slice frames must preserve corner proportions, shelf centers repeat without turning carved medallions into smears.
- **Existing acceptance anchor:** WS-16F Home phone screenshot is broadly functional, readable, fits Custom/My Puzzles and illustrates book scale. It is **not** approval of all new painters' pixels. Don't shrink the featured 218dp cover or reintroduce tall Continue/Daily buttons just to accommodate illustration.
- **Ownership:** GM_SHARED materials live as local reusable components and may be ported into Sudoku/Crossword later deliberately; WS_THEME remains local to Word Search. Preserve QA vs release asset gates and actual existing Woodland/Space/Garden covers.

## Deliverable definitions
**Final deliverables per unique asset:** a source master (layered/native vector or high-resolution lossless image), a clean standalone exported PNG/WebP as appropriate, source + exported SHA-256, pixel dimensions, alpha check, mobile-size review image, provenance notes, manifest update, debug-only Android import and source-matched build verification. Register release approval only with owner sign-off and licensing review. SVG references alone do **not** count as completed painterly exports.

**Raster strategy:** make a large source (>2× normal displayed size), export appropriate density renditions; avoid huge full-screen 4K alpha screenshots. A 512px action master should remain readable at 30–48dp. Use separate corners/caps/repeatable rails for illustrated frames and shelf; never uniformly stretch the only illustration.

## Batch 1 — 20 asset roles

| ID | Target export/master size | Required visual treatment | Alpha, assembly and special rules |
|---|---|---|---|
| `GM_BG_PARCHMENT_MAIN_V1` | 1024×1024 seamless tile plus separately composited native edge wash | Warm ivory fibers, minimal speckling, very faint organic amber edge wash; no visible repeating motif. | opaque; repeatable tile/native full-viewport fill; edge wash independent |
| `GM_FRAME_PANEL_LIGHT_V1` | 1024×256 landscape; 4 separate corners + horizontal/vertical edge strips | Soft cream paper inset, thin uneven antique gold border, little corner brush flourishes, negligible internal texture. | transparent center OR low-opacity paper fill independently; 9-slice-safe frame components, fixed corner caps |
| `GM_FRAME_PANEL_FEATURE_V1` | 1280×320 landscape; independent corners and edges | Slightly richer gold double edge, lower warm cast shadow, corners restrained so live subtitle stays clear. | transparent/ivory interior optional layer; 9-slice/cap-composition; no stretched gold corners |
| `GM_BTN_PRIMARY_V1` | 720×240 master for 120–240dp wide pills | Forest-green enameled pill, narrow antique brass bezel, subtle painted inner highlight; no letters. | transparent outside silhouette; nine-patch or cap/center; native label, focus/pressed state |
| `GM_DIVIDER_SHORT_V1` | 512×96 ornament center; line native | Fine tapering gold flourish with tiny centered leaf; not overly ornate; thin line extends natively. | transparent; fixed-size center motif + native scalable rules |
| `WS_SHELF_TOP_V1` | 1024×96 horizontal repeat-safe midsection | Amber walnut top, beveled front edge, short horizontal grain, warm overhead highlights; flat enough for books to stand. | transparent outside shape; repeat center x; rounded end caps separate |
| `WS_SHELF_FRONT_V1` | 1024×160 repeatable center rail | Carved walnut grain in subtle bands, realistic thickness and deep brown underside, no stretched central medallion. | transparent outside rail; tile center horizontally, don't scale decorative ends |
| `WS_SHELF_APRON_CARVED_V1` | 1024×160 wide scalloped segment | Deep walnut face with soft carved lobes, recessed panel bevel and painterly wood modeling, less linear than existing Canvas. | transparent outside apron silhouette; modular left/right contours + quiet stretch-safe center |
| `WS_SHELF_BRACKET_LEFT_V1` | 256×320 portrait | Chunky curved carved shelf support, softly chipped/highlighted edges, weight-bearing silhouette like reference. | transparent; fixed-aspect cap on left; mirror not assumed |
| `WS_SHELF_BRACKET_RIGHT_V1` | 256×320 portrait | Handcrafted symmetrical companion to left with individual asymmetric woodgrain and consistent illumination. | transparent; fixed-aspect cap on right; no horizontal stretching |
| `WS_SHELF_CENTER_ORNAMENT_V1` | 512×180 wide | Antique-gold foliage curl and small central oak leaf; carved into/attached to dark apron; single high-read focal flourish. | transparent; fixed center, hide/scale modestly when width constrained |
| `WS_SHELF_CONTACT_SHADOW_V1` | 1024×96 wide alpha-gradient | Subtle soft charcoal-brown ambient occlusion, tight near base with wider weak feather; no literal ground. | transparent fading to zero; position beneath cover bottoms, top of shelf; no input |
| `WS_BOOKCASE_BACKBOARD_V1` | 1024×1024 seamless/tileable panel | Dark subdued walnut planks, gently vertical grain; contrast tuned so book covers pop; no painted fake books. | opaque or transparent boundary outside panel; tile by populated rows; wood uprights on separate existing geometry |
| `WS_BOOKCASE_ROW_SHELF_V1` | no new bitmap; layout composition recipe | Reuse exact WS_SHELF_TOP/FRONT/APRON/BRACKETS/CENTER/SHADOW family with a narrower visual scale and consistent contact. | composed layers; real rows only: 1/2/3 live book-column densities |
| `WS_ACTION_CONTINUE_V1` | 512×512, 12% transparent padding | Forest-green hanging satin bookmark with little embroidered oak sprig, warm stitching/shadow. | transparent; display 36–48dp inside live framed card |
| `WS_ACTION_DAILY_V1` | 512×512, 12% transparent padding | Small creamy tear-off calendar with sage binding, ochre star and tiny leaf at base, no date numerals. | transparent; display 36–48dp; native Daily Puzzle label |
| `WS_ACTION_TOGETHER_V1` | 512×512, 12% transparent padding | Two friendly woodland squirrels facing one another on a small stump; clean paired silhouette at 32dp, not a fine-detail scene. | transparent; display 30–44dp; native Together text, Wi-Fi accessible label |
| `WS_ACTION_QUICKPLAY_V1` | 512×512, 12% transparent padding | Two overlapping blank letter-tile shapes with simple A/B if legitimately illustrated? Prefer no typography: checker-letter squares + curled arrow and tiny ivy. | transparent; display 40–48dp; NOT an enabled playable mode until generator exists |
| `WS_ACTION_FULL_LIBRARY_V1` | 512×512, 12% transparent padding | Four compact vintage spines in forest, russet, ochre and olive, resting on a tiny shelf; no labels on spines. | transparent; display 36–48dp, native Full Library text |
| `WS_ACTION_COLLECTION_V1` | 512×512, 12% transparent padding | Two layered vintage picture frames, forest landscape in front and small gold botanical accent; distinct from a book stack. | transparent; display 36–48dp, native Collection text |

### Existing sources vs new art production
**Do not silently rename/delete the source IDs already integrated.** E.g. `GM_FRAME_PANEL_LIGHT_V1` in this brief is the *production role* of current `GM_FRAME_PANEL_V1`; `GM_FRAME_PANEL_FEATURE_V1` maps to `GM_FRAME_FEATURE_V1`; `GM_BTN_PRIMARY_V1` maps to `GM_PILL_PRIMARY_V1`. The Word Search shelf also already has `ws16e-*` masters and `ws16f-*` references. When integrating, use explicit `role_id` → `existing_asset_id` mapping in the manifest, or version fresh painterly exports (`*_PAINTERLY_V1`) without overwriting originals.

`WS_BOOKCASE_ROW_SHELF_V1` is a **composite reuse role**, not an excuse to export an identical large shelf image again. Make it from shelf family layers, repeating for the real rows and populated book widths. Likewise the main parchment is one subtle repeatable material plus viewport-native edge wash, not a 9:16 fixed backdrop.

### 3 production sub-batches (finish actual assets, not endless plans)
1. **1A — Material + structure:** parchment, light and feature frames, primary pill, divider + **all eight shelf/backboard roles**. First integrate the shelf/contact shadow in Home and repeated rows in Your Library. Mobile screenshot before final ornamental approval.
2. **1B — Six action illustrations:** Continue, Daily, Together, Quick Play, Full Library and Collection created as one coherent illustrated subject family with same light, ink and silhouette. Inject into the preexisting native Compose buttons without modifying click semantics or labels.
3. **1C — Export/integration sweep:** fix alpha halos and nine-slice boundaries, run 320–411dp + large text + landscape screenshot comparison, record per-sprite hashes; make one consolidated **debug** APK for visual acceptance. Keep candidate artwork out of release until explicit approval.

## Exact layout and scaling requirements
- **Home shelf:** maintain the WS-16F 252dp overall composition, 218dp live covers and focused-cover tap/neighbor focus. Production shelf cap heights should fit the existing ~42dp visible shelf region; the larger source masters exist for sharpness, not to demand a 160dp-tall furniture slab. First adjust artwork alpha/contact shadow, not cover dimensions.
- **Library:** 1/2/3-column dynamic book layout remains. Wooden backboard only behind actual catalog rows, no fake book spines; use the same shelf layers as Home.
- **Action icons:** source masters roughly 512×512 with ~12% transparent safe padding; test 30/36/40/48dp displayed silhouettes. Keep Together centered at the narrowed button size; don't move label below the safe viewport. Decorative art is not the touch target.
- **Frames:** use independent rounded corners + repeat/stretched blank edges and native paper panel/body where possible; no embedded pre-rendered text or required absolute-size card textures. Treat focus/pressed/disabled as Compose states, not inaccessible static art.
- **Parchment:** tile texture should pass seam checks on 320×720 and 1200×800; use very low luminance variation behind body copy. No heavy paper edges baked into tile, no empty-gutter tablet expansion.
- **Divider:** native extending lines flank a small centered emblem; optional on small screens, never obscure shared mark/Profile/Menu.

## Visual acceptance at phone size
- The book visibly **touches** the walnut plank, with a believable short soft shadow, not a 12–20dp floating gutter.
- Shelf front feels hand-carved with independent caps and central medallion, but it remains a subordinate accent below the book cover.
- Frame art looks editorial, not overworked. All current buttons fit comfortably; text never clips at 1.5× and can reflow/scroll at 2×.
- The Together action is instantly understandable and horizontally centered at 30–40dp. Daily icon and Collection art remain recognizable at small size.
- The **Your Library** bookcase must read as *repeated actual shelves*, not one flattened screenshot; entry/return to book remains functional.
- Initial Home, full scroll, actual Library 2+ rows, book detail, gameplay, profile, small device, 700dp width, accessibility text tests and released-art isolation are reviewed together.
- Keep the reference's flowers, lantern, top/bottom foliage for the **optional decor follow-on pack**, not mandatory parts of Batch 1. Those should remain separate transparent pieces, never obstruct touchable UI.

## Pipeline and go/no-go
1. Produce **original standalone painterly master** per unique asset (or composable pieces for stretch-safe families), verify alpha/dimensions and save under `design/visual-system/masters/`; never claim reference screenshot crop is an asset.
2. Export under `design/visual-system/exports/ws16g-batch1/` at tested densities, with clean alpha and source/export hashes; fill the [Batch 1 production register](../../design/visual-system/modular/ws16g-batch1-production-register.json).
3. Integrate **debug-only** behind existing feature/QA art controls. The sprite must actually be consumed at runtime; a master sitting in GitHub is insufficient.
4. Android compilation, core tests, lint, Android test source compilation, debug APK assembly, *separate art-isolation workflow*, and real owner phone screenshots/taps. Never describe source-compiling instrumentation tests as executed on a handset.
5. Keep PR #131 draft, save schemas unchanged, and WS-16F verified [fallback ZIP](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42737788114/artifacts/13727073915) until a newly built APK has dual-green CI and owner feedback. Export progress backup if signing conflict demands uninstall.

**Next single implementation task:** start sub-batch **1A** by creating/rendering the painterly **shelf 7-piece family + parchment/frame** masters, verify separate actual alpha pixels and import them in QA; leave button icons to the coordinated 1B subject group. This is the handoff that prevents another plan-only loop.
