# WS-16R — Illustrated Home button hierarchy and alignment

**Owner observation (Nov 10):** After testing the verified WS-16Q fullscreen partial-edge carousel, user said "Gooood. Now I think we need to focus on the buttons." Four owner handset screenshots show the side-peeking books, strong header, and stable current actions. Preserve the carousel design and keep the visual-first focus rather than pivoting prematurely to new features.

## Scoped implementation
- [Functional source commit `fc551418612b23d000e99a5f4129c3a8e4f364d3`](https://github.com/PlayerEden/GridMates-Word-Search/commit/fc551418612b23d000e99a5f4129c3a8e4f364d3), in draft [PR #131](https://github.com/PlayerEden/GridMates-Word-Search/pull/131).
- `Ws16BHomeActions.kt`: Day/Together row changed from asymmetric **1.72:1** to **1.38:1** to give squirrels and their label adequate horizontal breathing room. Raise stacked breakpoint to 305dp/1.25x font; stack instead of truncating user-visible routes at accessibility sizes. Daily illustrated asset now **42dp** (was 40), Together **36dp** in row (was an overly small 29), **42dp** stacked (was 40); both use matching native `titleSmall` hierarchy, vertically centered on the same 68dp touch card. The Quick Play placeholder gets a narrow inset gold separator and a consistent icon+copy lane; it remains **non-playable**, with no misleading click.
- `Ws16DLibraryShortcuts.kt`: Illustrated book/gallery assets increase from **37dp → 41dp**, compact 56dp card preserved. Native labels get predictable remaining width with controlled ellipsis at smaller scales. Existing fallback native Canvas glyphs remain.
- `Ws16HomeScreen.kt`: Continue Solo bookmark increases **40dp → 42dp**, with a narrow antique-gold vertical keyline between painted art and live progress. Its existing minimum 68dp container, working green Play pill, progress and button callback are untouched.
- `GridMatesIllustratedFrames.kt`: no changes to the shared pale parchment/gold frame material, live native text or accessibility semantics. The user’s reference already supports these ornaments; change only Home content alignment in this pass.
- No raster generation, catalog/game/book art changes, saved progression, Android system bars, launcher icon, book carousel or production release changes.
- The 48dp minimum Play pill and native clickable action cards remain; quick-play is still explicitly a preview.

## QA / owner's visual checklist
1. Inspect fullscreen Home on 360dp-class handset with Space centered. Featured book and entire page including bottom links should remain fully visible with **no tiny scroll regression**.
2. Daily Puzzle and Together should feel like sibling cards with icons and title aligned horizontally at their middles; squirrels no longer tiny compared with the calendar.
3. Full Library and Collection images should look intentional and balanced with their labels, not hit the ornamental borders.
4. Continue Solo bookmark, gold divider, native progress and Play pill must fit without clipping at normal font size.
5. Quick Play's divider and larger title remain clearly identified as *coming soon*, with no clickable fake button.
6. Briefly test Standard and increased font size; at larger font scale the two-row Daily/Together cards may stack, without obscuring actions.
7. Tap actual Library, Collection, Continue Solo, Daily, and Together routes. Existing Android instrumentation sources compile in CI but are **not run on a phone**. Owner phone remains the visual acceptance authority.
8. If the owner wants more painterly visual richness after this composition pass, create separate, source-controlled SVG/PNG illustrations and compare them against the current approved six WS16H icon sprites; do not distort textures by stretching whole button PNGs.

## CI
- [Standard Android QA #42872139928](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42872139928) and [art/release-isolation #42872140103](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42872140103): **running at initial documentation**; no new APK claimed until success and file inspection.
- Previous WS-16Q dual-green source `bfad66f6` and [QA artifact #13788295356](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42866268890/artifacts/13788295356) remains rollback (3-day retention).
