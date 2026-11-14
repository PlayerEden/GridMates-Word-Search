# Modular GridMates UI and Word Search shelf source package

**Started:** 2026-11-09. **Scope:** source files and composables inside Word Search first, portable later by deliberate opt-in. **Owner's reference:** illustrated paperback Home showing a carved walnut shelf beneath the actual featured book and replaceable parchment controls. Never use the entire reference screenshot as a clickable background.

## Implemented WS-16E shelf source (original and separately editable)

Editable components in `masters/wordsearch/`:

| Piece | Editable source | UI purpose |
| --- | --- | --- |
| Surface | `ws16e-shelf-top-v1.svg` | top walnut plank with bevel/woodgrain |
| Repeatable rail | `ws16e-shelf-front-center-v1.svg` | front face; no fixed ornament in center |
| Left support | `ws16e-shelf-end-left-v1.svg` | ornate bracket/endcap, preserving shape |
| Right support | `ws16e-shelf-end-right-v1.svg` | separate opposite bracket/endcap |
| Center flourish | `ws16e-shelf-leaf-medallion-v1.svg` | gold scroll + leaf separate from rail |
| Book contact shadow | `ws16e-shelf-contact-shadow-v1.svg` | transparent shadow independent of books |

**Runtime correspondence:** `app/src/main/java/com/playereden/gridmates/wordsearch/Ws16EStorybookShelf.kt` draws these same roles as lightweight Compose Canvas geometry with scalable wood gradients, woodgrain, relief brackets, separate flourish and book contact shadow. It is not using the SVGs as direct runtime bitmap imports, and the SVG and native drawings are **design variants**, not guaranteed pixel-identical. Book pager implementation still exists in `Ws16BookCoverPager.kt`; only the 13dp shelf strip is replaced by the new 34dp illustrated shelf. The new Compose component is tagged `ws16e-storybook-shelf`.

**Approval state:** these are first independently sourced shelf assets on a DRAFT PR. The final painterly look, gradients, book-to-wood contact, alpha and spacing need owner review on the real APK. Do not mark generated/illustrated UI concept screenshots as approved sprite sheets or announce new release assets. The six SVG masters are editable design sources, not packaged PNG exports and are not automatically injected into release resources. The currently generated books in the QA app continue behind the existing debug/release gate.

## How modular template production follows

The [WS-16E plan](../../../docs/design/WS16E_MODULAR_GRIDMATES_UI_ASSET_PRODUCTION_PLAN_V1_2026-11-09.md) owns the complete checklist. Next is `GM_SHARED` parchment frames (medium, feature, general panel, dialog, CTA pill, icon plate, text link), with **native** Compose labels and dynamic values. Text, button semantics, ripple/focus, and press state must not be inside art. Keep Word Search-specific illustrations (books, shelf, lantern, puzzle icons, leaves) outside shared frames. Shared does not mean changing Sudoku/Crossword automatically.

## Testing and safe rollback

- Source art exports not invented; use original SVG files or actual separate approved PNG/9-patch candidate outputs and record precise SHA-256 in the existing manifest schema if/when exported.
- Compare the ornate shelf with previously verified 13dp shelf using the WS-16D APK. Watch book bottom contact, side-book peek and swipe, book cover tap/direct play, section progress, 320dp/360dp/393dp/411dp widths, large fonts and bottom of scroll.
- Use exact-source Android QA + art/release isolation workflows; instrumentation compile is not an on-phone test.
- Keep PR #131 draft/unmerged until review; preserve fallback and saved progress. No new mode unlocks or reset of current book/solo progress in this pass.


## Implemented GM_SHARED framed buttons — WS-16E Pass 2

The **shared first family** is now in `app/src/main/java/com/playereden/gridmates/wordsearch/GridMatesIllustratedFrames.kt`. This class renders scalable medium cards, feature cards, a quiet panel/dialog backing and the forest-green/gold CTA pill through Compose Canvas. It draws **independent corner flourishes and border geometry** at whatever size the native content requires, not a stretched screenshot. Separate utility links keep underlined live text, and dedicated action subject icons remain swap-friendly.

Individual editable masters are in `masters/shared/`: `gm-frame-medium-v1.svg`, `gm-frame-feature-v1.svg`, `gm-frame-panel-v1.svg`, `gm-pill-primary-v1.svg`, `gm-ornament-corner-v1.svg`, `gm-icon-plate-v1.svg`. The **actual SHA-256 for every source master** is in [the shared frame source register](gm-shared-frame-source-register.json), all pending individual owner artwork review. These SVGs are **not used directly** as Android drawable assets: native Compose draws analogous shapes, while the SVGs are editorially editable masters for later adoption. An important design limit is **no baked strings, no one-piece UI screenshot and no wallpaper texture**.

**Current first integration:** `Ws16DLibraryShortcuts.kt` (Full Shelf/Collection), `Ws16BHomeActions.kt` (Daily/Together/QA Quick Play, menu, Custom/My Puzzles links), `Ws16HomeScreen.kt` (Continue feature frame plus green Play pill). Existing actions, text and backups remain native and functional; QA Quick Play remains explicitly non-clickable until the real generator is implemented. Not yet copied to other GridMates repositories.

**Implementation SHA:** initial `47ae4f47`, corrected `227b55e6` after removing a compile-only import error. Exact-source Android/art checks require a verified success before owner APK handoff. See [Pass 2 development log](../../../docs/devlog/2026-11-09_WS16E_PASS2_SHARED_PARCHMENT_FRAMES.md).

## WS-16F family paper material and title-specific bookcase layers

**Deployed for Word Search QA, not yet extracted to other GridMates repositories.** Shared `GridMatesParchmentBackground.kt` draws nearly-flat ivory parchment, radial warm edge tint and deterministic faint paper flecks. It fills any viewport in the Scaffold and keeps text/cards native. Two independent editable SVG masters under `masters/shared/` document the tile and edge-wash references. This is intentionally a subtle material surface, **not** the fully illustrated mockup screenshot or a baked room mural.

**Word Search only:** `masters/wordsearch/ws16f-carved-shelf-apron-v2.svg` and `ws16f-bookcase-backboard-v1.svg` are separate editable references; `Ws16EStorybookShelf.kt` and `BookcaseBookshelfScreen.kt` render corresponding scalable Compose Canvas wood layers. Home pager now overlaps the shelf rather than stacking separate whitespace blocks. The Library uses repeated real populated shelves and independent live book art.

[Exact SHA-256 source register](ws16f-source-asset-register.json) lists all four new masters as `SOURCE_CREATED_PENDING_OWNER_ART_REVIEW`; no claim of pixel-identical Android SVG import or merged production asset. [WS-16F design plan](../../../docs/design/WS16F_SHARED_PARCHMENT_AND_LAYERED_FULL_LIBRARY_2026-11-09.md), [devlog and CI receipt](../../../docs/devlog/2026-11-09_WS16F_LAYERED_PARCHMENT_FULL_LIBRARY.md). Future implementation in Sudoku/Crossword requires deliberate reuse and tests.
