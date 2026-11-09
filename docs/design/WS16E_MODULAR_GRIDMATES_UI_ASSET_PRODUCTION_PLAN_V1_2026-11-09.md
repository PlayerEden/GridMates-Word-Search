# WS-16E — Modular GridMates UI Kit v1 and Word Search Home Production Checklist

**Date:** November 9, 2026. **Status:** DESIGN/PRODUCTION PLAN ONLY; no finished new shelf or modular frames, UI code changes, APK or release approval from this documentation pass. **Working branch:** feature/ws14b-style-anchor-and-art-direction-2026-11-08 (draft PR #131).

**Owner-approved visual direction:** rich hand-painted storybook Home reference in chat (ornate walnut shelf, parchment buttons, antique-gold accents, light botanical corners, original Woodland / Space / Garden books), allowing proportion/layout adjustments to fit actual Android devices.

## North star

Make the **real, interactive app** resemble the approved concept without flattening it into a giant screenshot. Users should see one big featured book resting on a gorgeous shelf, with neighbor-book peeks, full native interaction and text that can change. Underneath: the actual direct **Full Shelf + Collection** shortcuts, then Continue, Daily Puzzle, Together, honest Quick Play QA preview, Custom and My Puzzles. Keep top GridMates/Word Search branding, Profile and Menu. No bottom footer.

**Rule:** art lives in separable layers; Android Compose owns text, layout, tap/press/focus, progress, accessibility, localization and save behavior. Create reusable assets **within reason**: reuse stable backgrounds and interaction patterns, keep game-themed character in icons and illustrations.

## I. Ownership boundary: GridMates shared vs Word Search specific

| Scope | Contents | Consumer |
| --- | --- | --- |
| **GM_SHARED** | Neutral parchment panel/button frame templates, clickable pill frame, common icon chip, title divider, dialog frame, compact text-link treatment, pressed/focused/disabled overlays; GridMates family mark; common Profile/Menu/Back/Settings/Records icons | Word Search first. Portable deliberately into Sudoku/Crossword later; do NOT sync other repos automatically |
| **WS_THEME** | Storybook wood bookshelf and contact shadow, optional lantern and page foliage, Word Search mark, Full Shelf/Collection/Continue/Daily/Together/Quick/Custom/My Puzzles illustrations, playful bookish ornaments | Word Search only for now; potential later sharing by explicit adoption |
| **BOOK_THEME** | Published Woodland, Space, Garden and future cover/spine/chapter/collection album motifs and unique rewards | Each real book ID, with QA-only art kept out of release |

Never bake Word Search wording into the shared GridMates logo or neutral shared button frame. Do not add a registered-mark symbol.

## II. Shelf-first asset checklist — priority P0

**Every item is PLANNED unless a real reviewed source/export appears in the manifest.** Existing simple Canvas shelf and current illustrated book covers are the starting point, not the final ornate shelf.

- [ ] **WS_SHELF_TOP_V1** (WS_THEME) — gently beveled walnut top surface, warm brush-painted grain and pale top glint. Independent horizontal sprite, contacts book bottoms.
- [ ] **WS_SHELF_CENTER_V1** (WS_THEME) — repeatable middle/front rail with dark carved underside; no detailed center ornament embedded in the stretchable region.
- [ ] **WS_SHELF_END_L_V1 / WS_SHELF_END_R_V1** (WS_THEME) — preserved carved endcaps and optional two short supports/brackets; must not distort when width changes.
- [ ] **WS_SHELF_MEDALLION_V1** (WS_THEME) — separate gold leaf/carved center flourish. Movable or hideable on small widths.
- [ ] **WS_SHELF_CONTACT_SHADOW_V1** (WS_THEME) — soft transparent shadow under books so they look planted on the plank, not floating; non-touchable decoration.
- [ ] **WS_SHELF_LANTERN_V1** (WS_THEME, optional) — small storybook lantern, only if side book peeks/swipes remain unobstructed; accent does not become mandatory.
- [ ] **WS_SHELF_SPRIG_L_V1 / WS_SHELF_SPRIG_R_V1** (WS_THEME, optional) — tiny natural bookend leaves, separate layers, no giant jungle around the carousel.

**Actual code boundary:** Ws16BookCoverPager.kt currently lays out roughly **218dp high book art inside a 234dp pager** and then a **13dp Canvas shelf edge**. Replace just the shelf area with a modular StorybookShelf component, without duplicating or rewriting HorizontalPager, book IDs, focus or direct play. Target a **24–32dp visible shelf face**, overlapping its shadow/supports with existing whitespace where possible, not a separate 100dp furniture card. These are provisional fit targets, not locked art exports. Compare 320, 360, 393 and 411dp screens before accepting size changes.

**Preferred assembly:** separate left/right endcap + repeat/tile/stretch-safe middle + distinct medallion + shadow. A single wide ornate bitmap will stretch its carving awkwardly, especially on 320dp phones. Do not crop the approved full-screen concept into a supposedly isolated shelf sprite; source actual clean-alpha parts.

## III. Modular component checklist — priority P1

The following **base frames are GM_SHARED**. Dynamic label, icon and text behavior are native layers placed into the same frame types.

| Checklist | Component ID | Purpose / expected use | Art specification |
| --- | --- | --- | --- |
| [ ] | GM_FRAME_MEDIUM_V1 | Paired Full Shelf / Collection, Daily / Together, other menu choices | Illustrated parchment border, gold corner flicks, transparent center and enough icon/text negative space |
| [ ] | GM_FRAME_FEATURE_V1 | Continue, Quick Play, results, future featured events | Wide parchment frame with optional subtitle lane; does not contain fixed wording |
| [ ] | GM_FRAME_PANEL_V1 | Profile, Records, Collection gallery panels and Settings | Calm paper frame with restrained trim, supports many heights |
| [ ] | GM_FRAME_DIALOG_V1 | Menu and size selector / confirmation dialogs | Scalable paper body, native focus/scroll/close controls |
| [ ] | GM_PILL_PRIMARY_V1 | Play / Start / Equip / Claim | Deep forest-green pill, gold rim, native centered text, focus/pressed/disabled states |
| [ ] | GM_ICON_PLATE_V1 | Holds any small art subject or collection emblem | Neutral rounded illustrated plate with a safe ~28–48dp icon interior |
| [ ] | GM_TEXT_LINK_V1 | Custom, My Puzzles, Back, View all | Clear native underlined/selected/pressed text; never rely on a flat label image |
| [ ] | GM_DIVIDER_V1 | Brand heading or book section separator | Thin flexible gold line + optional central independent leaf |
| [ ] | GM_STATE_OVERLAY_V1 | Normal/pressed/focused/selected/disabled variants | Native state feedback: contrast, shape/outline and not only color |

**Reusable Compose API proposal:** one GridMatesIllustratedButton primitive with variant, label, optional subtitle, icon slot, enabled, modifier, onClick, pressed/focus behavior and semantic description; plus GridMatesPaperPanel, GridMatesDialogFrame and GridMatesSectionDivider. Keep size- and text-dependent content native; do not implement eight separate click surfaces for eight artworks. Compose linework, vector, 9-patch or cap-and-stretch-center assets as appropriate. Carved corners, woodgrain and wreaths must not smear when resized.

**Initial flexible dimensions:** 48dp+ primary touch targets; medium paired buttons 56–72dp, feature cards 72–92dp, icon art 28–48dp inside hit area. Allow growth and reflow at large fonts rather than truncating Daily Puzzle or Together. Never make a required button smaller merely to preserve an ornament.

## IV. Illustrated icon checklist — priority P2

**Existing simple art is a baseline to improve, not an unimplemented feature.** All illustration subjects remain separate from shared frames.

| Checklist | ID / ownership | Readable subject and purpose |
| --- | --- | --- |
| [ ] | GM_ICON_PROFILE / GM_SHARED | Neutral player/avatar ring; actual equipped portrait remains its own image |
| [ ] | GM_ICON_MENU / GM_SHARED | Crisp hamburger, 48dp native target |
| [ ] | GM_ICON_BACK / GM_SHARED | Standard arrow with obvious focus state |
| [ ] | GM_ICON_SETTINGS / GM_SHARED | Approachable gear |
| [ ] | GM_ICON_RECORDS / GM_SHARED | Small book/journal or stats marker |
| [ ] | WS_ICON_FULL_SHELF / WS_THEME | Mini stack of colorful storybook spines |
| [ ] | WS_ICON_COLLECTION / WS_THEME | Overlapping picture-gallery frames and subtle gold glint |
| [ ] | WS_ICON_CONTINUE / WS_THEME | Green ribbon with antique-gold leaf |
| [ ] | WS_ICON_DAILY / WS_THEME | Dated paper calendar and gold star; full native Daily Puzzle label |
| [ ] | WS_ICON_TOGETHER / WS_THEME | Recognizable two-player silhouette OR small storybook partner vignette; fully accessible local-Wi-Fi name |
| [ ] | WS_ICON_QUICK / WS_THEME | Shuffled letter tiles and loop arrow; stay a truthful QA-only preview until WS-17 generator works |
| [ ] | WS_ICON_CUSTOM / WS_THEME | Grid and pencil or controls |
| [ ] | WS_ICON_MY_PUZZLES / WS_THEME | Bound puzzle journal |
| [ ] | WS_ICON_HELP / WS_THEME | Small help/open-book symbol, only if current menu needs it |

The approved concept’s little squirrels are a possible **Word Search-themed Together icon**, not a requirement to replace the clear two-person symbol everywhere. Keep tiny icons legible at actual on-phone scale, not only zoomed-in in an asset sheet.

## V. Supporting decoration checklist — priority P3

- [ ] **WS_LEAF_TOP_L_V1 / R_V1** — quiet transparent top-corner leaves around—not across—GridMates branding/Profile/Menu.
- [ ] **WS_SECTION_LEAF_V1** — small native-title divider ornament.
- [ ] **WS_PAPER_BG_V1** — low-contrast seamless warm parchment texture, not a giant 4K backdrop.
- [ ] **WS_FLORA_FOOT_L_V1 / R_V1** — flowers and mushrooms at the *end of the scrollable Home*, not covering Custom/My Puzzles.
- [ ] **WS_PROGRESS_PLAQUE_V1** (optional) — slim backing for native Woodland · 0/3 pages complete, without adding a full extra row of content.

Decoration must be removable without loss of action/meaning. Avoid having Woodland-only flowers around a Space book in a way that feels contradictory; use more neutral gold paper and foliage outside theme-specific scenes.

## VI. Production discipline and source files

- **Editable master first:** independent SVG, layered high-resolution artwork or equally editable source for each item. Do not treat a screenshot/moodboard as a finished sprite.
- **New kit staging path (proposal):** design/visual-system/modular/masters/shared/ for GM_SHARED, design/visual-system/modular/masters/wordsearch/ for WS_THEME, design/visual-system/modular/exports/qa/ for debug candidates, design/visual-system/modular/receipts/ for verified manifest/report.
- **Status and tracking:** use existing ws14-asset-manifest.schema.json with stable IDs, actual source/export SHA-256, dimensions, alpha, rights basis, owner review and Android source-set. Only add entries when an actual file exists. This checklist deliberately has no fabricated provenance or hashes.
- **Visual states:** one normal/pressed/disabled/focused treatment system shared across controls, not four separately painted images for every icon.
- **Content is never baked in:** book titles, page counters, profile name, continue words found, button labels/subtitles and lock/unlock counts belong to Compose.
- **Scaling:** prefer vector/Compose for light icons; Android 9-patch or fixed caps + stretchable center for decorated parchment. Keep corner sprites separate. Compare stretched export at short/wide/tall aspect ratios before release.
- **Performance and rights:** compress repeated textures, avoid large asset decoding or heavy animations, verify generation rights/provenance, keep QA-only books and unapproved art out of production.

## VII. Consolidated implementation/QA sequence

**Stage 1 — Illustrated shelf prototype (first actual build package):**
- [ ] Produce transparent isolated shelf top, repeatable front, endcaps/brackets, medallion and contact shadow.
- [ ] Integrate StorybookShelf under actual HorizontalPager; preserve neighboring peek, focused-cover direct play, back, scroll and progress; verify no artwork intercepts taps.
- [ ] Test 320/360/393/411dp; compare the current 13dp shelf baseline with the carved one and trim surrounding spacing if needed. Keep big covers unless the fit truly demands a small scale change.

**Stage 2 — Shared modular parchment frames:**
- [ ] Build and verify frame templates with native text/icon slots and clear pressed/selected states.
- [ ] Fit the actual Full Shelf/Collection, Continue, Daily/Together and honest Quick Play preview into these templates; preserve function and labels.
- [ ] Validate Custom/My Puzzles and Menu/size dialog framing before replacing any current working components.

**Stage 3 — Icon/atmosphere consistency:**
- [ ] Add separately replaceable illustrated icon subjects, small title divider, leaf corners and optional foot/flora.
- [ ] Remove flat placeholder visuals only after superior actual assets exist, and ensure the UI stays readable when decorative art is absent.
- [ ] Adjust Home spacing after real asset dimensions are known. A little scroll on small screens is acceptable; do not shrink touch targets or force all art into one fixed screenshot.

**Stage 4 — One meaningful owner QA package:**
- [ ] Core and app unit tests, lint, Android instrumentation-source compile, artwork/release-isolation; confirm 9-slice deformation checks and alpha/hash receipts.
- [ ] Phone-test 320dp/360dp/393dp/411dp, landscape, 1.5x and 2x fonts, TalkBack, scroll, book focus/direct-play, Profile/Menu, Gallery/Shelf, Continue, Daily, Together, Custom and My Puzzles.
- [ ] Provide one source-matched Android QA APK with checksum, rollback, journal entry and before/after screenshots; keep PR draft until owner approval.

**Out of scope:** implementing real mixed-vocabulary Quick Play, changing existing book/reward save IDs, changing currently earned portraits, restoring the bottom bar, forcing login/online services, automating asset adoption in the Sudoku/Crossword repos, or promoting unreviewed art to production.

**Next action:** Build the shelf pack first; then make modular parchment frames and fit the Home action buttons. This is the working asset-production contract for the illustrated WS-16E pass.
