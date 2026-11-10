# GridMates — Shared Visual Identity, Art Library & Motion Language

**Date:** November 9, 2026 · **State:** proposed cross-series design standard. Direction informed by Word Search's *actual* illustrated Android screenshots and owner feedback. Not yet an implemented animation system, a shared external asset repository, or approval to modify Sudoku/Crossword.

## Why this exists

The owner wants the GridMates games to feel **like one family rather than unrelated art pasted onto screens**. Word Search's Woodland/Space/Garden covers demonstrate a cohesive illustrated book style; however, rich decoration should be selective and surrounding controls should stay calm, readable, and practical. The same asset families might eventually serve **GridMates: Sudoku**, **GridMates: Word Search**, **GridMates: Crossword**, and future entries, without collapsing each game's own identity.

**Creative principle: "Quiet paper, living details, shared family."** Illustration gets character and detail; interface controls remain clear, consistent and predictable; motion makes actions legible rather than making the screen constantly busy.

## Brand and trademark policy

- **Default wordmark:** **GridMates** (plain, no registered mark). Game names: **GridMates: Word Search**, **GridMates: Sudoku**, future **GridMates: Crossword**.
- **Never use ®** unless the relevant trademark and goods/services are actually registered and verified. The owner specifically noted that the name isn't registered. No current Word Search cover should claim registration by inference.
- **™ is optional** when intentionally using an unregistered mark as a trademark; simpler UI wordmarks may omit symbols entirely. Do not confuse ® with a decorative design flourish.
- **Audit both native UI text AND image pixels**, since the symbol can be baked into generated title artwork. A code search alone will not prove raster assets are clean.
- For the **next versioned artwork master**, correct any ® directly in the source image/edit, remove any unverified claim and export a clean cover. Don't quietly overwrite the current SHA-pinned WS-14 QA V1 master; create V2 and retain rollback.
- Source: [USPTO — What is a trademark?](https://www.uspto.gov/trademarks/basics/what-trademark) (TM may be used for an unregistered mark; ® follows registration for covered goods/services).

## Shared visual DNA across every GridMates game

**Stable across games:**
- Palette architecture: warm parchment/cream base, dark forest-ink text, sage/deep-green active controls, antique-gold *small accents*. Each game may introduce one clear thematic secondary hue.
- Text hierarchy: readable bookish/editorial heading, clean practical body text, clear large numbers/letters; all dynamic text native, with proper screen-reader labels.
- Materials: gentle paper grain, fine ink outlines, warm highlight from upper left, coherent shadow falloff, softly rounded but not bubble-like tactile components.
- Shapes: consistent corner radii, icon stroke weight, button touch dimensions, cover/book silhouette treatment, accessibility contrast and selected/disabled states.
- Interaction: small book/card raise on selection, visible focus, progress transition, gentle page change; no autoplay spectacle during puzzles.
- Voice: calm, friendly, handmade, *not* ornate on every edge. One richly illustrated focal subject per major destination is plenty.

**Game-specific layers:**
- Word Search: collectible illustrated puzzle books, chapter/page navigation, distinctive Woodland/Space/Garden cover subjects.
- Sudoku: number grid readability and pencil/ink cues first; reuse shared framing, background, controls, achievement polish, not forced woodland book art.
- Crossword: grids, clues and newspaper/paperback typography first; shared framing and micro-interaction family.
- Future games: keep the GridMates material/typography/motion system but introduce thematic icons/focal art, never visually indistinguishable game modes.

### Asset library taxonomy

| Tier | Example assets | Reuse policy |
| --- | --- | --- |
| **Core/shared** | Neutral paper texture, ink divider, simple badge frames, button icons, page-turn cue, profile presentation frames, generic sparkles/achievement accent | Candidates for reuse across GridMates apps after source license, original bytes and hash tracking |
| **Theme-compatible** | Woodland leaves, stars, flowers, bookbinding trims, muted edge decorations | Reuse selectively only where context fits; adapt palette and scale consistently |
| **Game-specific** | Word Search owl/Space/Garden covers, Sudoku number-face sets, Crossword clue cards | Preserve as distinct named assets; don't transplant blindly between games |
| **State-specific** | Selected/locked/unlocked, pressed/disabled, progress/achievements | Keep as native Compose state whenever practical; separate illustration from dynamic labels and controls |

**Future shared library proposal, not yet created:** version a canonical art library with immutable source masters, approved transparent exports, per-asset IDs, SHA-256, provenance/rights, color/tokens, recommended dimensions, background contrast samples, and source-game origin. Games consume a **pinned version/hash** (as the current Woodland profile portrait import does). A game may opt into only relevant assets; do not make games depend on runtime downloads or shared cloud accounts.

**Naming example:** \`GM_CORE_PAPER_CREAM_V1\`, \`GM_CORE_PAGE_EDGE_V1\`, \`GM_ACCENT_SUCCESS_SPARK_V1\`, \`GM_WS_BOOK_WOODLAND_COVER_V2\`, \`GM_SD_NUMBER_FACE_PENCIL_V1\`. Shared assets must be separate from Word Search save/profile IDs. Original production assets remain immutable. Revisions get new versions and review.

## Motion: three layers, deliberately restrained

### 1. Touch response (first priority, inexpensive)

- **Book selection:** lift/scale to roughly 1.02–1.04, soften/expand shadow, then settle; ~160–250ms. Do not bounce wildly.
- **Horizontal book browser:** swipe/snapped movement with a short ~220–340ms easing/spring, nearby book peeks; focused CTA follows selected book. Provide arrow/tap alternatives.
- **Buttons/cards:** subtle press scale/elevation/tint ~100–180ms, clear touch feedback and accessible state. No decorative animation needed to indicate *critical* status.
- **Progress/check marks:** gentle fill/check reveal ~180–350ms on change, no unnecessary repeat.

### 2. Transition/feedback (occasional, contextual)

- **Open book:** selected cover moves/fades into book detail or a restrained page-turn cue; actual navigation remains fast and predictable.
- **Collection unlock:** one small star/spark flourish or stamp settling on the earned icon (<1 second), **once**, not looping.
- **Results:** short celebratory accent proportional to accomplishment, no confetti storm; same family across games.
- **Profile/icon changes:** gentle crossfade and selected outline only; preserve clarity for less technical players.

### 3. Ambient/illustrative life (optional, rare)

- Woodland: occasional eye blink or a drifting mote; Space: a few twinkling stars; Garden: a butterfly shift or leaf sway. **Use only when the relevant illustrated scene is foregrounded and stationary**, not while solving a puzzle.
- Existing **flattened PNGs** can be lifted, tilted, scaled, faded, shifted or overlaid with drawn stars. They **cannot independently blink an owl or flutter a butterfly** unless the relevant feature is delivered as a **separate transparent layer** or frame set.
- For a future layered master, request e.g. \`woodland/base.png\`, \`woodland/owl_eyelid.png\`, \`garden/butterfly.png\`, \`space/stars.png\`. Anchor coordinates and matching texture/shading belong in the asset manifest. Don't rebuild every cover into a large video/GIF unnecessarily.
- Default to **no constant idle motion**. Any ambient motion should be subtle, run only while visible, and cease when screen changes, app goes to background, puzzle starts, battery/low-motion settings suggest restraint, or user chooses Reduce Motion.
- Never use animation to obscure, delay or compete with word tracing, number input, clue entry, accessibility focus, or multiplayer turn state.

## Compose technical implementation (when authorized)

- Reuse native Compose \`AnimatedVisibility\`, \`AnimatedContent\`, \`animate*AsState\` or a pager's built-in transition; prefer GPU/draw-phase transforms with \`graphicsLayer\` for changing a visual layer rather than remeasuring the whole page per frame.
- Give each motion the same timing/easing family through a shared \`GridMatesMotionTokens\` contract, **but don't turn a style document into a global dependency between apps**. When stabilized, export tokens and a standalone reusable component into each game with tests.
- Honor device animation/accessibility preferences and provide a visible **Reduce Motion** or "Animations Off" option if needed. With reduced motion, keep instant state changes and all accessibility/navigation functionality intact.
- Keep 320dp/360dp, 200% text, landscape, older phones and low frame time in scope. Test swipe gestures and screen-reader/keyboard/arrow alternatives. A horizontal bookshelf pager must not capture word-search grid traces.
- Performance check: avoid decoding unscaled 2K masters on the device, avoid looping full-cover invalidations, preserve existing bounded bitmap sampling.
- References: [Android official Compose animation quick guide](https://developer.android.com/develop/ui/compose/animation/quick-guide) and [Compose animation introduction](https://developer.android.com/develop/ui/compose/animation/introduction).

## First test plan — without new artwork first

1. **Implement WS-14D horizontal bookshelf A/B** using existing covers and **no detached spine**, with clear selected-book controls and grounded color/material system. Keep original vertical shelf available and save behavior untouched.
2. Add **one gentle selected-book lift** and one native progress/selection transition in QA only. Verify instant rendering when animation is disabled.
3. Hand the real APK to owner; compare still vs motion at actual handset size, touch responsiveness, navigation and clarity.
4. Only after that decide which *independent layered sprite assets* to create for selective ambient life. Build one layer as a small proof-of-concept before scaling across three themes.
5. Once accepted, produce a **versioned shared-family design kit** and pilot reuse of neutral/core assets in the Sudoku repo on a separate, reviewed branch. Do not auto-modify Sudoku or future Crossword from this proposal.

## Non-negotiables and release gates

- This document is **a design/technical proposal, not code already implemented**.
- Owner liked the new Word Search book family, but has not approved any future motion implementation, brand registration claim, exact new V2 art, or cross-game SDK.
- Correct ®/rights status before any production release. No blanket claims of trademark rights, license clearance or source approval.
- Do not add motion just because the technology can: quiet idle states are part of the identity.
- Each subsequent implementation/asset/build pass must be recorded in [development journal](DEVELOPMENT_JOURNAL.md), with exact SHA, QA result, owner feedback, fallback and next task.


## Owner clarification — The GridMates Home Library (November 9, 2026)

**Approved family-level atmosphere:** GridMates should feel like a **cozy home and a personal library** — warm, lived-in, welcoming, bookish and calm. Think a space someone would happily spend a quiet afternoon in, with puzzle books, soft lamplight and useful handcrafted objects. This is a **shared art-direction principle**, not a literal requirement to render a full interior on every screen. The tagline **“Good puzzles. Good company.”** remains the emotional center.

**Coherent family, different nooks:** Keep GridMates wordmark, wood/paper material language, typography, gentle lighting, tactile frames, art presentation and native navigation consistent across games. Let each title inhabit a distinct corner of that imagined home: **Word Search — the illustrated bookshelf**; **Sudoku — the puzzle desk/notebook**; **Crossword — a reading/writing nook** (future concept, not yet selected for implementation). Theme-specific covers, icons, and collectibles vary. Never reskin all screens as the same furniture view or replace real native labels with text painted into a full-screen illustration.

**First phone evidence from WS-16E:** In the owner's November 9 Android screenshots, the carved walnut shelf is visibly integrated and has depth and a gold center ornament, beneath the Woodland cover with Space peek. The illustrated covers still appear to float **roughly a small visible gap** above the shelf rather than physically touching the top. In the next visual refinement, align cover bottom with the shelf top by adjusting pager alignment/padding or shelf compositing; avoid scaling down the strong book art just to correct contact. This is a visual observation, not proof of any broken click behavior. Preserve focused-cover tap/side-peek and the clear full-shelf/collection shortcuts.

**Practical material system:** Warm walnut shelves, quiet parchment and ivory panels, antique-gold details, deep forest-ink text, subtle botanical/bookish ornaments, restrained shadows. Decorative elements should be deliberately placed and **not** occupy every free pixel. Aim for a comfortable home atmosphere rather than a lavish fantasy museum or rigid institutional library.

**Modular sourcing:** GM_SHARED frames, button/pill templates, icon chips, text treatment and shared brand are portable. WS_THEME bookshelf, paper-book art and leaves belong to Word Search first. Existing working Android source is at WS-16E shelf commit `92a1d899`, with dual-green Android/art QA and owner screenshots; this new note changes *direction only*, no app code or Sudoku/Crossword repo. Detail and checklist: [WS-16E modular kit](design/WS16E_MODULAR_GRIDMATES_UI_ASSET_PRODUCTION_PLAN_V1_2026-11-09.md).
