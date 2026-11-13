# WS16BJ — Carved walnut Library title / headline clearance

> **Status: v69 CODE COMMITTED / DUAL ANDROID + ART CI IN PROGRESS / OWNER VISUAL ACCEPTANCE PENDING.** Do not claim an installable verified APK until exact-source CI, signer, APK hash and packaged files pass. Date: November 12, 2026.

## Owner screenshot and request

Owner installed WS16BI v68 and supplied a real Android Library screenshot, responding **“That's a whole lot better. Now the library plaque could be better/more themed, also moved up to not block the books.”** The spine-first collection, slightly wider tappable Woodland/Space/Garden bindings, wordless decorative blank volumes, stronger shadows and revised physical shelves are visually more promising. Owner's remaining problem is specifically the **generic-looking top “Your Library” rectangular nameplate** overlapping the upper part of real books. This pass is **title-only**, not another bookshelf redesign. The other shelf/count/buttons remain intact.

## Repro / visual diagnosis

In `WS16ATModularLibraryScene.kt`, the portrait title previously used
`.padding(start = 62.dp, end = 62.dp, top = (top - 58.dp).coerceAtLeast(38.dp))`.
Under the v68 source, `WS16BICabinetPlaque(title=true)` gave that text a comparatively thick flat rectangular wood/brass UI slab with 11dp text vertical padding. The owner screenshot shows the plaque's lower edge visually covering the rounded tops of the real first-shelf bindings. Cabinet crown and bulbs are otherwise already approved as the visual context.

## Source changes

1. New independent, no-asset `WS16BJCarvedTitlePlaque.kt`: **46dp total height**, physically shaped walnut cartouche with tapered shoulders, a very slight curved crest/underside, an inset aged-brass edge, paired carved foliage at the ends and tiny mounting studs, small top/bottom light engraving. Text remains native `Text`, one-line, readable cream serif with no baked raster words. No touch interaction; no new art, fonts, licensing or permissions.
2. `WS16BIAdaptivePlaque` now chooses `WS16BJCarvedTitlePlaque` **only if `styled && title`**. The existing v68 compact count, page labels and other cabinet plaques keep their current appearance; non-portrait/compact/landscape falls back to the original AZ rendering. The back/menu controls are unchanged.
3. `WS16ATModularLibraryScene.kt` positions the title **22dp higher on a typical tall portrait**: `.padding(start = 66.dp, end = 66.dp, top = (top - 80.dp).coerceAtLeast(16.dp))`. It is inset 4dp further from controls on either side and shorter vertically, leaving extra clearance beneath it so book tops can be seen without moving books or changing tier/shelf dimensions.
4. New `test_ws16bj_carved_title.py` validates carved shape/text, title-only routing, position, non-overlapping button area, unchanged spine-first paging, signature/package contract. Both GitHub QA workflows execute this source suite. `QaActualBookcaseAndroidTest.kt` additionally asserts 10 pixels of positive clearance above the upper real spine and unobstructed Back/menu hit zones in the 360×760 portrait fixture (instrumented source compilation is part of CI, actual device execution requires hardware/emulator).
5. Default `versionCode=69`, `versionName=0.9.25-carved-library-title`; same QA package `com.playereden.gridmates.wordsearch.qa` and established public QA signing certificate. This should **install directly over v68 without uninstall**. No saved-game, puzzle rules, LAN, catalog, art asset or monetization change.

## Source and verification

- **Latest feature/test source at initial QA** [`8c5b54fe4606ee943c1b634a784e0af9f8f1c102`](https://github.com/PlayerEden/GridMates-Word-Search/commit/8c5b54fe4606ee943c1b634a784e0af9f8f1c102). GitHub draft [PR #131](https://github.com/PlayerEden/GridMates-Word-Search/pull/131), branch `feature/ws14b-style-anchor-and-art-direction-2026-11-08`. Documentation-only `[skip ci]` commits after code don't alter compiled APK.
- **Android QA** [#43335903651](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/43335903651) — IN PROGRESS.
- **Independent art/release isolation** [#43335903647](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/43335903647) — IN PROGRESS.
- **No verified v69 APK yet**. Check both workflows, repair actual failures at a new exact source SHA, and after dual-green inspect actual artifact/checksum, Android package/version/signing, unchanged artwork and release-art exclusion.
- **Last installed v68 fallback** [GitHub ZIP artifact #13978378051](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/43294624409/artifacts/13978378051); owner visually reviewed that version. Normal Android downgrades are not guaranteed to install over newer versions. Never ask to uninstall/clear saves to compare.

## Exit criteria

Owner installs final signed v69 over v68, sends full Library screenshot. We should see the fitted carved title sitting higher beneath the arch, **no overlap with rounded real books**, clear text, lamp and side-control clearance, and unchanged shelf/spines/blank-books appearance. Confirm a real book still opens and saves persist. CI success is not owner visual approval; PR #131 stays draft/unmerged and QA artwork remains debug-only until explicitly accepted.
