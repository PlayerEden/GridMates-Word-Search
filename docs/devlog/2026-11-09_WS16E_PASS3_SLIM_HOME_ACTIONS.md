# WS-16E Pass 3 — Slim Home actions after owner handset review

**Date:** November 9, 2026. **Branch:** `feature/ws14b-style-anchor-and-art-direction-2026-11-08`. **PR:** #131 (draft/unmerged).
**Exact gameplay/UI source SHA:** `338e4c65783240579d614202696a44cb84903f80`. At this log's creation, exact Android QA [#42733181348](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42733181348) and art/release [#42733181417](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42733181417) were still running. Do **not** infer green/verified artifact before checking final conclusions. Later docs-only `[skip ci]` commits are not new APKs.

## Owner visual feedback and the concrete issue

Owner supplied two **real Android screenshot states** of WS-16E Pass 2 with rich parchment frames, the carved shelf, large Woodland/Space book peeks, full-shelf/Collection and mode buttons. Owner said the menu is getting somewhere but **Continue** and **Daily Puzzle** look disproportionately tall; just a little extra vertical scrolling keeps **Custom** and **My Puzzles** below initial viewport. The frames/books/colors otherwise look coherent.

**Analysis:** Continue's 100dp min height plus 12dp vertical padding and a too-long subtitle (`Woodland · 30×30 · 2/44 found`) wrapped the final word `found` onto a third text row, leaving a visually tall empty-feeling parchment card. Daily and Together share an 82dp minimum row, even though their icon and label need less. The Home vertical gap is another small contributor. The owner wants to recover scroll distance primarily by slimming these actions, **not by shrinking the featured book covers or removing accessibility**.

## Implemented source changes

- `Ws16HomeScreen.kt`: Continue frame minimum **100dp → 76dp**; row vertical padding **12dp → 8dp**, horizontal padding 14→12dp, and bookmark art 44→40dp. Replace its wrapped subtitle with an explicit compact `Woodland · 2/44 found` example. The grid dimensions are still available when opening Custom or the puzzle; avoid truncating the actual completion count. Complete state still says Finished, route remains New/Custom.
- `Ws16BHomeActions.kt`: Daily and Together min height **82dp → 68dp** each; Daily calendar 42→40dp, row vertical padding 10→8dp. Together's narrow column silhouette 42→36dp with 3dp vertical padding; stacked variant 42→40dp, padding 10→8dp. This keeps a full `Daily Puzzle` text label and `Together` label without returning to clipped `Toget…`.
- `GridMatesShell.kt`: **Home-only** outer vertical padding 12→10dp and between-section spacing 10→8dp. Other page spacing unchanged. Books, shelf/carousel, Profile/Menu, Full Shelf/Collection, Quick Play QA-only coming-soon row and Custom/My Puzzles routes unchanged.
- These are **minimum heights**, not forced fixed heights. On larger fonts or narrower screens, native Compose content can still grow/reflow; the inherited native tap areas stay >=48dp. Compact card presentation is focused on the owner's usual portrait viewport, without claiming zero scrolling on every device.

## Acceptance / regression instructions

**Exact source build gate:** wait for **both** Android and art/release-isolation workflows on SHA `338e4c65`; test sources compiling is not proof of actual instrumented device execution. Fix source/build failures before owner APK link; do not recommend the older Pass 2 artifact as the new one.

**Owner phone check:** compare initial Home at top against Pass 2 screenshot. Confirm parchment frames still look richly illustrated; the Continue text now stays on a compact line; Daily/Together are slimmer but readable/tappable; Custom/My Puzzles come into view sooner; the shelf and cover peeks remain untouched; hamburger/Profile, Full Shelf/Collection, Solo resume, Daily and Together still launch. Slight remaining scrolling is acceptable for reduced-height or large-text devices.

**Safe fallback:** Previous dual-green WS-16E Pass 2 [QA ZIP #13724199339](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42731226177/artifacts/13724199339) (APK SHA-256 `d4535ecbc312afac18ddd23b53c0f80d03ee3f0f14847fb0bf40185e9ccf6a6e`). No saved puzzle schema/profile changes in this pass. Back up progress before uninstall due to signing conflicts.

**Next:** after owner phone review, refine only remaining awkward spacing or icon sizing; avoid another whole-screen makeover. Separate WS-17 mixed-word Quick Play generator remains future work.

## Verified exact-source Android and artwork receipt

**Both CI workflows completed SUCCESS on UI/gameplay source `338e4c65783240579d614202696a44cb84903f80`**. Later documentation-only [skip ci] commits do not represent rebuilt APKs.

- [Android QA #42733181348](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42733181348): **SUCCESS** — core/app unit tests, lint, Android instrumentation **source compilation**, assembled debug APK. Source compilation is not proof tests were executed on a real phone.
- [Art/release isolation #42733181417](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42733181417): **SUCCESS** — 6 QA-only cover/spine sprites absent from release; 18+18 portrait sources and 36 SHA-matched native QA PNGs verified; 3 theme motifs debug-only with correct hashes.
- **Owner test ZIP artifact:** [#13726026545](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42733181348/artifacts/13726026545), contains `app-debug.apk`. Historical artifact name includes Woodland, but compiled code is the Pass 3 slimmer Home.
- **APK SHA-256:** `5d8cb4c3cc0e1dcc0bfe9ab029ad851eb18720b562eba81f5a82c2ef31bde5c7`.
- **Artifact ZIP SHA-256:** `23189789669ff425d7900351259c0ee55a26e15d6c1cb74eec56cfcb6107bbc2`; GitHub artifact size 13,970,090 bytes.
- QA package follows unchanged `com.playereden.gridmates.wordsearch.qa`, versionCode 57 family; no signing/version guarantee, back up progress before uninstalling a prior APK if a signing conflict occurs.
- **Device UI acceptance pending:** owner needs to compare first-screen visibility of Custom/My Puzzles, text wrapping and card balance against screenshots. PR #131 remains draft/unmerged.
