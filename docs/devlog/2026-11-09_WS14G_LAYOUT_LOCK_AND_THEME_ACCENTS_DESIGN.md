# November 9, 2026 — WS-14G layout lock and in-game art direction

**State:** Product/design planning decision, **no code or new sprites**, no APK build in this pass.

## Who / What / Where / Why

- **Owner direction:** After WS-14F cleanup, aim for a **good settled layout** so we can begin making a larger cohesive asset library, including **small in-game art matching the Woodland, Space or Garden book selected**. Should be restrained and fit GridMates as a family rather than random decorative art.
- **Assistant work:** Translated that direction into a testable **layout-lock gate** and a reusable three-theme in-game accent plan; checked actual live GitHub and existing `BookArt`/gameplay code before specifying an asset pipeline.
- **Where:** [WS-14G Layout Lock / Theme Accent Kit](../WS14G_LAYOUT_LOCK_AND_IN_GAME_THEME_ACCENT_KIT_2026-11-09.md), existing draft [PR #131](https://github.com/PlayerEden/GridMates-Word-Search/pull/131).

## Where we came from / verified state

- [WS-14D owner visual review](2026-11-09_WS14D_OWNER_PHONE_VISUAL_REVIEW.md): actual phone screenshots of swipe book selection, Space detail, Page 1 and Page 3. Owner liked overall direction; screenshots do not prove words selected/solved.
- [WS-14F book detail + dense zoom code](2026-11-09_WS14F_BOOK_DETAIL_AND_DENSE_GRID_POLISH.md) is committed. Current head **`8fd7ff8056bb5ce224b040a9d5f90eb096c1ad0b`** successfully ran [Android QA #42650786851](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42650786851) and [Art debug/release QA #42650786752](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42650786752), both **SUCCESS**. [Android QA artifact #13690146702](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42650786851/artifacts/13690146702) available as a GitHub Actions ZIP. App installation/save review and touch ergonomics of **this WS-14F build** have not been reported by owner. Prior WS-14D on-phone visual experience is positive.
- Existing `BookArt.kt` already supports `WordTheme`-selected **EMBLEM, CHAPTER, BOOKMARK, COMPLETION** local PNG (QA fallback) art. `BookPageGameplayScreen.kt` can derive the theme from actual opened book; there's no need for another account/theme setting.
- Older v56 fallback remains historical broad baseline. Keep current tested code and previous usable APKs; leave draft PR unmerged until owner review/rights/regressions.

## Planned implementation, not accomplished

1. First, confirm WS-14F phone layout and actual 20×20 tracing/zoom/precise-selection behavior.
2. Prototype a tiny native header motif **using the three *existing* book emblems** before generating anything new. The puzzle grid/letter overlays must remain untouched.
3. Once slot and sizing are accepted, create **three new matched transparent motifs** (Woodland leaf/fern, Space ringed planet/star, Garden blossom/butterfly) as versioned source art, with exact SHA, review gate and debug-only import. Reuse in header/words-heading sparingly. One-time celebration can reuse existing completion art; no constant idle motion.
4. Approve consistent layout v1 across browse, detail and game (later Play/Collection) before broad sprite production. Extend to GridMates: Sudoku/Crossword through shared material/tokens when useful, not copy-pasting Word Search art.

## Build and release receipt

- **This pass:** documentation-only, **NO APK built** and no new image generated.
- Latest confirmed automated source QA *before* this doc change: green Android #42650786851 and artwork #42650786752, both tied to head `8fd7ff8`. Do **not** claim art-owner acceptance, testing of word claims, or a WS-14G implementation based on these results.
- Asset status **PLANNED**, not `GENERATED_UNREVIEWED` or approved; no fake SHA/provenance.
- Keep existing data, profile icons, multiplayer, signing and installed fallbacks untouched.

## Next execution action / new chat

Read `AGENTS.md`, [current Journal](../DEVELOPMENT_JOURNAL.md), this entry, [WS-14G plan](../WS14G_LAYOUT_LOCK_AND_IN_GAME_THEME_ACCENT_KIT_2026-11-09.md), [active handoff](../WS14_ACTIVE_HANDOFF_2026-11-09.md); check latest live PR/CI. **Then prototype existing theme emblems in the actual book-page header, gated for QA; test on phone.** Only after UI placement works generate three small matched independent PNGs, not a giant new collection. Every pass receives its own log and actual build receipt.
