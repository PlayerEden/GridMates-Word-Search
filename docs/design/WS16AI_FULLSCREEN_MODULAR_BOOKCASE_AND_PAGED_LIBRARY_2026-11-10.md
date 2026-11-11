# WS-16AI — Full-Screen Painterly Bookcase and Paged Library
**November 10, 2026 · Owner-directed design / next implementation contract · NOT YET IMPLEMENTED**

## Owner's feedback and decision

The present Full Library displays two shallow, separated plank/backboard rows occupying only part of the phone screen. The owner wants a **beautiful, believable, almost full-screen wood bookcase** with the same **painterly dimensional material/depth/art quality** as the approved carved walnut shelf on Home, not flat procedural wood panels or more gradients. It should hold **large, closely packed, independently tappable book spines** and anonymous matching future-volume fillers until new books replace them. The library must scale beyond a few themes without building a physically infinite scrolling cabinet. Owner accepts either a tall modular case or horizontal swipe; **recommended next implementation is a reusable horizontal, paged bookcase** (each page a complete, repeatable cabinet with 2 tiers on normal portrait phones).

**This document records a direction, not a claim of committed visual artwork, Android UI or passing QA.** Illustrative mockups were generated in the owner's design conversation; **mockups contain rendered words, books and controls and must not be pasted into the app as one screenshot**. The real source assets must be **empty modular wood art** and the actual book objects/labels/touch interactions must remain separate live elements.

## Visual direction / art-system requirements

**Woodwork:** real painted walnut or warm oak bookcase, richly dimensional, visibly beveled leading edges, thick carved shelf plank compatible with WS16AB Home shelf, corbel/scrollwork detail, restrained gold leaf/filigree, top crown, side uprights, deep recess/backboard planks with believable soft occlusion and lighting. Not overburdened with decoration. Keep parchment margins *small*, using ~90%+ of usable post-safe-area width and almost all vertical area below the header. Dark wood helps the books stand out; avoid glow that degrades title legibility.

**Source-controlled modular components, individually transparent where useful:**
1. `bookcase_crown_v1.png` — richly carved header/upper cap, no baked-in text.
2. `bookcase_side_left_v1.png`, `bookcase_side_right_v1.png` — continuous matching pilasters, extensible between crown and base.
3. `bookcase_backboard_tile_v1.png` — repeatable recessed rear wall with wood grain, subtle depth, safe seamless top/bottom repeat boundaries. Avoid flat gradient imitation.
4. `bookcase_shelf_plank_v1.png` — **real painted** shelf with consistent carving and underside thickness, shadow and contact top plane. Fit / nine-slice or constrained repeat without horizontally stretching central ornament.
5. `bookcase_base_v1.png` — proper base plinth and optional footer ornament.

One **shared source/asset manifest** records exact dimensions, layer order, repeat/crop coordinates, SHA-256 input/output hashes, alpha silhouettes, provenance and portrait/landscape placements. V1 mockup is inspirational only, not a releasable production sprite or editable piece. Avoid text, book images, tap targets, fake locks and page counters baked into the wood artwork.

**Layer order:** parchment/background → recessed backboard → structural frame/pilasters/crown → shelf surfaces and contact shading → real independently rendered book spines and noninteractive future-volume spines → wood foreground lip, book-hover/highlight if any → native back, title, page controls and accessibility labels. Do not hide actual books behind a drawn static bookcase image.

## Layout and content model — use horizontal paging

- **Normal portrait phone:** two bookcase shelf tiers (not a tiny double panel), together filling the available screen height after a compact native Back/Your Library header and safe-area insets. Responsive sizing, avoid clipping on 320dp and tall phones. Landscape can adapt to a single taller/wider tier if necessary.
- Compute `slotsPerRow = floor(usableCabinetInnerWidthDp / minTapSlotDp)` with a modest cabinet inset. **Minimum 48dp independent nonoverlapping selection area** for real books; at 320–360dp expect ~**6 books per row**, possibly 7 on wider phones. Never claim 8–10 individually easy tap targets if physical width can't support them. At 360dp, two rows give roughly **12 slots per bookcase page**; derive capacity from actual measured width and text scale, do not hardcode 12 for all devices.
- Create wider **physical spine masters** if the current 128×640 art is too narrow for near-touch presentation: aim visually ~**42–45dp** width inside 48dp slots while retaining a natural, non-distorted binding ratio. Design an approved wider geometry; **do not stretch** original 1:5 PNGs to unnatural proportions just to fill gaps. Allow a few standard/tall height classes while keeping bottom baselines aligned.
- Book order is deterministic and based only on actual catalog records: real theme books first, then slots filled with **reused, visibly plain, noninteractive future-book spines** (cycle the 3 linen/night/burgundy masters and any future editorial variations). They share construction and materials with the real books. No fake book IDs, progress, completion, gifts, achievements, chapter pages, or tappable placeholder actions.
- On page #1 start with Woodland, Space, Garden and anonymous volumes filling the remaining available slots. As real themes arrive, they replace blanks in reading order, one at a time; a second bookcase page exists only once published real books overflow current page capacity. `pageCount = max(1, ceil(realCatalogBookCount / pageCapacity))`, not count of future placeholders.
- **Horizontal swipe** between complete cabinet pages, with explicit accessible previous/next controls *when more than one page exists*, and page indicator `1 of N`. No decorative inert arrows/dots on a one-page catalog. Maintain deterministic page on screen rotation/font scale, clamp saved page index after a catalog changes. The selected book preview remains exact existing cover/details/chapters/progress with functional `View pages`, Back and dismiss.
- Bookcase pages are a **visual presentational wrapper**; no changes to puzzle generator, saved progress IDs, actual book data, profile, LAN or game modes. Preserve in-place APK update and regression tests.

## Home bookshelf preview

Keep Home's three real book cover carousel items and count as **1 of 3**. Optionally append **one** fully separate future-volume *cover artwork* at the far end as a non-clickable visual teaser, with native “More books coming” semantics; it must not become an extra selectable playable carousel page, increment progress, or misrepresent content. This comes **after** the Full Library reaches art/interaction approval, not as a silent change to Home.

## Acceptance & rollback gates

1. **Actual source artwork**: create independently editable wood parts, not flat replacement or cropped one-piece mockup. Compare 1:1 and phone against real Home walnut shelf; consistent highlights, carving, contact shadows, alpha, source manifest and hashes.
2. **Whole-screen look**: before/after owner Android screenshots at 320/360/412dp and a tall phone. The bookcase visually dominates; no large wasted parchment area, no clipped carves or readable controls behind books. Full library no longer looks like 2 disconnected shelf thumbnails.
3. **Dense but usable**: genuine near-touching books (ideally ~2–5dp visual gap), 48dp+ distinct touch targets; every real book selectable, correct cover/preview; placeholders clearly decorative and never clickable. No overlaps/focus ambiguity. Validate TalkBack and large font.
4. **Scale**: test 0/3/7/12/13/24+ actual books in sandbox data without changing production catalog or saves. Verify replacement of placeholders, page count and swipe/arrow behavior, rotation and Back. Never automatically introduce fake published books.
5. **Two independent Android/art QA builds**: source hashes, successful generated PNG inclusion, release isolation (unapproved art debug-only), APK contents and aligned exact-source CI; run real phone acceptance before promotion. Keep current WS16AG dual-green QA artifact and original shelf/layout as rollback until owner approval.
6. **Engineering sequence:** lock frame/backboard/shelf module art → build native full-screen responsive cabinet and live page data model → overlay true/blank independent spines with density QA → add native accessible swipe/page navigation → owner phone/art approval → optional Home teaser.

## Related prior decisions

- WS16AG: actual separate spine assets, corrected stray marks, three nonplayable blank prototype spines; owner wants them **closer, larger and filling the case**, not spaced across mostly empty shelves.
- WS16AD Book Factory: fixed physical-book publisher geometry and no unsupported ®. Future wider bindings require a **new versioned consistent master family**, not ad hoc distortion.
- WS16AF: physical phone confirmed spine → full cover modal, preserve this function.
- WS16AB: approved directional richly carved shelf + parchment, use as craft/lighting/material reference.
- Future shared GridMates/Word Search two-piece wordmark stays separate; avoid baking any logo text into wood for cross-game reuse.

**Status at document creation:** no code, no modular bookcase assets, no build, and no APK created by WS16AI. This is the implementation brief to execute next on draft [PR #131](https://github.com/PlayerEden/GridMates-Word-Search/pull/131).
