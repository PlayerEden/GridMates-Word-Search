# WS16BI — One cohesive physical Library (design + implementation contract)

**Date:** 2026-11-12. **Status:** owner direction locked; artwork and QA candidate remain unapproved. **Work branch:** `feature/ws14b-style-anchor-and-art-direction-2026-11-08`, draft PR [#131](https://github.com/PlayerEden/GridMates-Word-Search/pull/131). **Last owner visual reference:** WS16BH v67 screenshot (three closely grouped readable spines, very empty second shelf, pasted-on wooden shelf bars, generic rounded button/plaque chrome).

## Owner decisions to preserve

- A **spine-first collection**, not an illustrated cover gallery. Owners expect dozens or hundreds of themed volumes packed side by side and 2–3 shelves as the screen permits.
- Real book spine and matching illustrated cover are independent paired art assets. Tapping the real spine opens existing full-cover preview and puzzles. Never change book IDs, save schema, LAN behavior or catalog ordering to decorate a screen.
- **Decorative blank books have no lettering at all**. Build several rich, source-generated, walnut/leather-and-gold bindings with banding, filigree, embossing and simple motifs. Blanks are purely environmental, not locked inventory and not selectable. No invented future theme names.
- Books need a little more visible width, genuinely easier taps, curved body shade, projected shadow on cabinet back and convincing weight on shelf. Never bring back the wide fake double-spine panel from WS16AO.
- **Shelves, title plaque, count label, Back/menu and paging controls must belong to one cabinet furniture design.** Current WS16BB over-ornamented fascia and flat pill-shaped native UI are not visually approved.
- Preserve the high-resolution carved walnut/gold/ivy shell and all prior graphics as fallbacks. New assets are generated reproducibly, tracked by SHA, and debug-only until approved. Keep PR draft.

## Proposed v68 pilot contract — testable before owner acceptance

### Shelf architecture
- A new reusable shelf source **1172×250 RGBA**, authored from the existing painted walnut/backboard reference, with a visibly thick top surface, horizontal wood grain, recessed brass line, restrained center emblem and structurally integrated brackets. No baked books, text, UI or fake locked bindings.
- The **contact plane must be explicitly documented**, then measured in Compose rather than assuming the WS16BB `42.dp` offset on a new asset. Add dark occlusion immediately behind the shelf, a bevel under the books and side bracket shadows; no giant floating leaf garlands.
- Render exactly 2 shelves for ordinary portrait; reserve a third only when sufficient vertical space keeps books readable, not merely to fill empty space. Keep lower count label below wood without collision. Tablet/landscape continue proven fallback.
- Reuse the same shelf sprite and contact math for every tier, including page two.

### Real bindings / hit targets
- Widen the binding **slightly** and make click regions approximately **48dp** per book on portrait; if adjacent hit targets share edges, each owns its own nonoverlapping cell. A separate full 48dp invisible hitbox that overlaps the next book is NOT acceptable.
- Source-pinned single-body WS16BF spines remain the visual basis; natural rounded left highlight and darker right shoulder. Add ambient shadow to the backboard and tight contact beneath each binding.
- Capacity is recalculated from actual interior width and hit-cell size. Keep row-major fill, no clipping/silent book loss. When full, paginate real books, preserve ordered IDs.

### Decorative blanks
- Three reproducibly generated themed-*neutral* ornamental blank binders (e.g. deep evergreen, antique midnight blue, muted burgundy/chestnut). Similar craftsmanship to real spines, but no words, digits, fake theme names or real-book insignia.
- Blanks are **always** lower-contrast than real owned books; keep a modest number to hint at collection growth: first shelf after owned spines, then a small cluster on the empty second shelf. Never fill every empty slot, never inflate real book count/page count, never create tap semantics or tooltips implying ownership.
- When actual books grow, decoration yields without affecting real-book placement or pagination. Blank art appears only in debug builds until specifically approved.

### Unified cabinet chrome
- **Feature title:** fitted carved wood + inset brass nameplate, readable native serif text, no generic pill contour.
- **Book count:** small inset catalog label beneath shelf, *not* a button and not overlapping carved wood.
- **Back, menu, previous/next:** consistent brass fittings, wood inlay, at least 48dp real touch targets, native icon and semantic labels, with clear disabled state and contrast.
- All components share the cabinet's warm-light direction, muted gold line weight, bevel/shadow depth, and wood tones. Native text and icons—not painted labels—remain scalable and localizable. Profile/Collection keep their own documented existing layout unless explicitly reviewed.

## Tests and release gates
1. Build / regenerate source-pinned shelf and blank PNGs and manifest deterministically; verify exact source SHA, alpha bounds, no lettering inventory and debug-only release exclusion.
2. Unit/source + Android Compose UI coverage: 0, 1, 3, 6, 12, 30, 100 real books, tiny/narrow/large text, two/three tiers, first/last click, count and pagination, native chrome semantics, blank books impossible to tap.
3. Run independent Android QA and art/release GitHub workflows at the **same code SHA**; exact-check source-matched APK SHA, version, expected unchanged QA signing certificate and packaged assets.
4. Owner installs over v67 **without uninstall/reset** and sends one portrait Library screenshot plus book-tap/save check. CI-green does not equal visual approval. If the new shelf art is worse than the previous candidate, revert the art alone without moving game state.

## Priorities
**First:** detailed shelf rendering and contact geometry; **second:** real book width, depth and hit accuracy; **third:** unlettered ornamental blanks; **fourth:** library-specific chrome; **fifth:** integrated QA/Android APK. Do not make a release/merge claim before all gates finish.

## Non-goals
No new playable themes or puzzles; no invented unlocks; no cloud, payments, online multiplayer changes; no new external licensed reference art; no accounts or sign-in changes.
