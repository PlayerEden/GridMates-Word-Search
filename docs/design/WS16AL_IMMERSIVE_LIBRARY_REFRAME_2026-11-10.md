# WS-16AL — Immersive Library Reframe / Bookcase 2.0

**2026-11-10 | Owner phone review of WS16AK | Architectural UX decision; design handoff, NOT a shipped Android change.**

## Decision

**Build an immersive, near-full-screen, responsive library environment. Do not merely scale the present 1024×1536 painting to fill the app screen.** The current WS16AK asset is a valid painted wood-style reference and a successful art-import proof, but is **not accepted as the final native library UI**.

The library should feel like a bookcase inhabiting the same world as the Home screen—not an inserted photograph with a separate app form above and under it. Retain a subtle parchment/page edge for continuity with GridMates; the cabinet is now the dominant stage and occupies almost all the height available below Android safe areas.

## What the owner's physical phone screenshot shows

On the 674×1536 submitted image, the actual illustrated cabinet occupies roughly x11–663 (97% width) and y282–1386 (72% total height), while a large, flat parchment header/QA controls occupies the entire region above it and an unrelated count floats below. The carved bookcase itself is good-looking, but its abrupt rectangular top/bottom and clashing screen-level presentation make it feel out of place. The high visual contrast amplifies the seam.

The owner explicitly questions the full-screen fit, what to do with surrounding space, and whether **all wood assets should be overhauled**. This is a legitimate composition problem, not just a 5dp gap or another alpha cleanup.

## Confirmed code causes (checked in PR #131)

- `BookcaseBookshelfScreen.kt`: a `Column` containing `Ws15BackHeader(title/subtitle)`, a 48dp+ `Inspect the empty bookcase artwork / Show books` row, then a `HorizontalPager(...weight(1f))` and an out-of-cabinet `3 books in your library` footer. These create large disconnected parchment blocks and shrink the cabinet's usable area.
- `WS16AKBookcaseBackground.kt`: six master-image horizontal bands preserve painted crown/shelves but use `ContentScale.FillBounds` and stretch the upper/lower bay region by remaining height. Without separately engineered continuous pillars and backboard seams, that risks inconsistent carved proportions and woodgrain on different ratios.
- The main bookcase paging/book tap code still uses the **older WS16AI** `BookcaseWoodParts` when `Show books` is selected; it is not yet using the accepted painted empty WS16AK background with interactive books overlaid. This is intentional QA gating, but there is no evidence the books are registered to the new shelf contact planes.

## Screen-level redesign — concrete target

**Top zone (~52–72dp depending on fonts/safe areas):** Native Back, `Your Library`, and overflow controls in one concise row. Remove the persistent subtitle and instructional text from the main layout (offer short first-use tutorial/accessibility hint instead). The crown begins immediately beneath or lightly behind this integrated top strip; ivy/paper texture can connect the wood to the global parchment shell. The QA **Empty art / Show books** comparison belongs in Developer Options or overflow, not as a permanent two-line panel stealing vertical space.

**Main stage (~85–92% of available post-safe-area height, not a rigid fraction of image pixels):** Real illustrated cabinet nearly edge-to-edge with 6–12dp responsive outer breathing room; crown and base retain unwarped aspect, side pilasters have genuinely repeatable midsections, recessed backing absorbs height changes, and actual shelf planes are defined as geometry anchors. Avoid blurry full-screen `FillBounds` stretching of carvings and avoid visibly abrupt dark rectangular mat boundaries.

**Bottom zone:** Move real `3 books in your library` and `1 of N` (only when N>1) into a native, accessible inlaid bookcase-base plaque or slim overlapping parchment/wood tab. Avoid a disconnected counter floating below the bookcase. Retain Android bottom gesture-safe padding; never obscure hardware Back, assistive interaction, or real-book count.

**Color/material transition:** Keep forest green, readable cream, modest foliage and parchment identity. Reduce artificial amber glow and excessive highlights. Let the recess stay comparatively dark; add soft occlusion under shelves and book-bottom contact shadows. Source assets should be real, independently reviewed painterly images, not another flipped shelf crop, procedural gradient or a single flattened UI screenshot.

## Asset system v2 (we can retire/refactor WS16AK band crops)

- `crown_v2`: a purpose-composed crown suitable for a compact integrated top bar; do not force the open-book crest and header label into the same visual space.
- `pilaster_top/middle/bottom_v2` each side: carvings are never stretched; repeat only visually neutral vertical wood in a versioned seam-safe middle segment.
- `recess_backing_v2`: modular back wall with compatible length extension, controlled grain/lighting and no obvious repeat marks.
- `shelf_front_mid_v2` and `shelf_contact_overlay_v2`: full depth at correct z-order (cabinet back → real/blank book sprites → *foreground* shelf lip/shadow), aligned to book bases.
- `base_v2`: sculpted plinth with safe space or planned overlay anchor for genuine native library counter and page controls.
- Source master and individual rendered PNGs get actual source hashes, output hashes, alpha bounds, preview-size checks and art-isolation QA; no composite with baked books, native text or static buttons.

**Do not assume all original painted pixels are useless:** walnut style, leaf treatment and carved motifs can inspire or be recycled when genuinely compatible. But if the six horizontal WS16AK crops cannot achieve a seamless responsive environment, redraw/replace them rather than endlessly patching crops.

## Book placement after empty scene approval

The existing book-art system (three theme covers, three separate spines, three blank spine families) and real-book catalog stays intact. Books belong to **shelf contact anchors in the native layout**, not free-floating fixed y offsets measured from screen top. Two bays per cabinet page; actual spines packed visually close with about 2–5dp gaps, while distinct real-book tap hit areas remain >=48dp (>=54dp at larger text settings). Controlled sizes/heights, physically plausible covers/spine binding, no clipping into the carved frame. Placeholders are non-clickable and do not count toward real pages or progress; as real books are added they replace blanks, and only overflowing *real* catalog books create the next swipeable bookcase.

## Implementation order and acceptance criteria

1. **Empty library screen shell first.** Build a QA alternative with compact navigation, near-edge-to-edge art, integrated base label, proper safe-area padding; no books. Test at 320×small, 360×standard, 412×large and 360×tall logical phone proportions. The entire *screen composition* must look coherent, not just the isolated wood.
2. **Actual modular wood architecture.** Redo pieces that warp/cause seams; preserve native painting sources + reproducible pipeline and exact source hashing. Test bay growth without stretching carvings. Compare before/after of entire screen (including title and controls), not a cropped cabinet.
3. **Live interactive book overlays.** Layer over the accepted shelf anchors; preserve independent covers/spines, genuine progress, `View pages`, Android Back and carousel ordering; filler volumes are environmental only.
4. **Page scaling + QA.** Real catalog fixture counts 0/3/6/7/12/13/24+, phone rotation, 200% font scale, TalkBack, tapping each real book; no fake unlocks or clipped tap areas.
5. Standard Android and independent art/release-isolation workflows from the **exact same commit**; compare actual APK internal files/hashes, keep earlier WS16AK and WS16AI as rollback. No production release approval without owner's real handset screenshot.

**Do not mark as implemented:** This document is the explicit design/engineering answer to the owner's 2026-11-10 screenshot. It does not contain new compiled app code, a new master art set or an APK. Do not re-enable the old illustrated background as the final production screen merely because source extraction passed CI.
