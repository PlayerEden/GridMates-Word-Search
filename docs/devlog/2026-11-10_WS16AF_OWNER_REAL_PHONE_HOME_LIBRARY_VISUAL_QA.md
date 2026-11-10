# WS16AF — Owner real-phone Home and Full Library visual review
**Date:** November 10, 2026 · **Milestone:** WS16AE V2 installed and screen-render QA observed on real Android phone · **State:** owner's final book-art acceptance not yet granted; no code/art changed by this note.

## Evidence
User supplied two actual Android screenshots after the authenticated GitHub download issue was resolved:
1. **Home / Garden focus:** illustrated Garden book is centered in carousel (3 of 3); Space peeks from left; the approved-direction high-depth painted walnut shelf remains intact; `Garden · 1/3 pages complete`; `Continue Solo — Saved: Woodland · 2/44 words`; all core Home actions visible.
2. **Your Library:** two full-width bookcase rows rendered, first with 3 distinct theme spine artworks (Woodland/Space/Garden) and walnut shelf; second row entirely empty; footnote states `3 books in your library`.

These are **real-phone visual smoke-test screenshots only**. They prove the app installs/launches and both screens render on this device, **not** that Home's cover taps, Full Library spine selection/pulled-out dialog, Back, save persistence, two-device LAN, or production signing have been accepted/tested.

## Owner priorities and grounded visual observations
- Preserve current **real painted walnut shelf, covers, core Home composition and gameplay**. Garden V2 visually looks good at normal size; no obvious broad rectangular matte/white box. Do not re-render the entire cover art without owner direction.
- The **spines render as distinct, actual spine assets**, not cropped full covers. Code confirmation: `BookcaseBookshelfScreen.kt` uses `BookArt(..., BookArtKind.SPINE)` for the shelf; tapping its 48dp (large-text: 54dp) selection slot sets `selectedBookId`, which shows the full `BookArtKind.COVER` 144×192 preview with chapter/progress and `View pages`. **Interaction remains unproven without a tap**.
- The first row's spines appear to sit **slightly above their wooden plank**, with weak contact grounding. Source layout uses fixed 198dp backboard, 156dp hit-slot, 148dp tall drawn spine and `bottom = 36.dp` row inset; review exact shelf top vs book bottom bounds before adjusting, and preserve touch slots.
- Spines are **legible as themed books but visually tiny against the very broad tall backboard**, with enormous empty horizontal space. Future library A/B could try subtly more substantial spine sizes and standard/tall height classes, but no random nonuniform bitmap stretching, overlapping touch targets or forced extra titles; show 320dp/360dp/412dp and large-font QA.
- The second empty shelf feels barren. The three **source-controlled, non-playable blank linen/night/burgundy covers and spines** from WS16AD are a natural option to convey more books coming, but MUST be visibly labeled Coming Soon and NOT added as fake catalog IDs, unlocked titles, book progress, or actions. This is a later presentation pass, not permission to change content.
- Prior [WS16AE QA asset audit](2026-11-10_WS16AE_QA_DOWNLOAD_AUDIT_AND_SPINE_TYPO_BLOCKER.md) found a printed tiny superscript/registered-looking glyph on **spines**. The code for `repair_banner` touched covers only; inspect and regenerate all three spines from corrected masters before calling book art complete; also reinspect front banner glyphs and edge alpha at 1:1 on both cream and dark.
- Shared GridMates full family wordmark plus independent Word Search title wordmark remain planned; do not disturb existing header sizing/menutaps during book-art QA.

## Download/QA workflow status (separate source change)
- GitHub Actions artifacts used to require authenticated download and expired after 3 days; user hit GitHub 404 when not signed in. Source commit `1cf6aef70fc6ccd1f6c5da191e3c26cfc5e8debe` changed future artifact retention to 30 days and made names readable, without game-content changes.
- [Android #42922678480](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42922678480) **SUCCESS**, [artifact #13812736844](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42922678480/artifacts/13812736844) expires December 10, 2026 (requires GitHub sign-in).
- Independent art/release verification [#42922678475](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42922678475) was initially canceled during a docs update, reran successfully on second attempt; [artifact #13813052640](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42922678475/artifacts/13813052640) expires December 10, 2026. Earlier WS16AE original dual-green builds remain documented.
- PR #131 remains unmerged draft; owner-approved shelf material is not blanket production art release approval.

## Prioritized next checks
1. Fix the printed spine marks and recheck all three corrected cover/spine pixels with the source-pinned Book Factory, keeping the artwork and saved content IDs stable.
2. Real owner phone tap test: tap Woodland, Space, Garden spines; confirm correct full-cover preview, chapter/page progress and View pages/Back behavior. Also confirm center/neighbor carousel direct play.
3. In later **independent** Full Library design pass, improve spine-on-shelf grounding, compare natural slightly thicker and standard/tall spines, and preview the 3 clearly non-playable blank volumes on empty shelf. Do not automatically ship these while checking corrected artwork.
4. Continue separate two-piece illustrated wordmark stage and puzzle gameplay/Quick Play audit as prioritized in roadmap.

**No new APK or art change is claimed by this screenshot-review note.**
