# WS16BK — Future bookcases, wordless decorative volumes and bottom-page hardware

> **Status (November 12, 2026):** Source committed, exact-source Android & art/release QA queued/running, OWNER phone approval not yet requested; no v70 APK verified. This is a **functional/page architecture pass**. UI microanimations are intentionally **next pass WS16BL**.

## Owner decision and visual evidence

On real Android v69 screenshot, owner reviewed the carved Walnut Library with three Woodland/Space/Garden spines and a few muted untitled book bindings. Owner explicitly requested the redundant bottom `3 books` plaque be removed. Its location should instead display **previous arrow, dots, next arrow** to browse full future bookcases. These bookcases should reuse the identical cabinet and mounted-shelf architecture and be filled with richly detailed **wordless** decorative bindings while more real themes are planned. Owner wants an additional later button-plaque **animation/pop/depth** pass after paging is working.

Prior owner-installed build: [WS16BJ v69 Android artifact #13997661426](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/43336457623/artifacts/13997661426), APK SHA256 `4264caf65e89f0e8229502b7263206ea5e861f1198e3eb534d5713b2a5bcc2bd`, dual-green Android and art QA, same public test signing cert. This screenshot is visual review, NOT release approval.

## Architecture/decisions

- **Real book pages** always come first, row-major by existing catalog IDs. `realPageCount = maxOf(1, ceil(books.size / capacity))`. Capacity still comes from the actual cabinet interior and number of shelves, **excluding decorative assets**. No saves, unlocks or puzzle state touched.
- In high-res portrait, append **two preview future bookcases** after real pages, yielding 3 visible bookcases for 3 books. As real books grow, real pages grow first, and two decorative future cases remain available. On landscape/compact layout, existing real catalog pages continue without invented capacity.
- Future bookcases show the SAME `WS16ATCabinetLayers`, WS16BI mounted shelves, scene lighting, title and controls. Both shelves are filled up to available real-slot count with wordless, **non-clickable** decorative spines. First page keeps current restrained decoration beside/under the three owned bindings.
- Removed `3 books` catalog-count plaque completely from the modular cabinet, including the compact count fallback. `WS16BKBookcasePager.kt` uses a fitted QA-only walnut/brass **pager_rail** image or native fallback, two existing 54dp cabinet controls and active/inactive dot indicators. Dots are deliberately non-clickable, since 18dp dots are too small as independent touch controls. Native semantic page position supports accessibility. The 7-dot viewport slides as larger real catalogs produce many pages; arrows/swiping still reach all pages. Disabled arrows preserve an obvious end-state.
- No native animations or haptics added to controls in this pass; swipe/arrow page transition uses the existing `HorizontalPager.animateScrollToPage`. Animation refinement deferred to WS16BL so first we can isolate paging/bookcase artwork defects.

## New asset pipeline

`build_ws16bk_future_bookcase_assets.py` runs after the source-pinned WS16BI generator. It creates **eight** 104x542 unlettered bindings `fern,dusk,clay,garnet,ochre,teal,violet,slate`, with original hand-painted rounded leather/gold proportions, varied low-contrast tones, engraved geometric/foliage emblems and no words/numbers. All are decorative; none has ownership/click or semantic unlock. It also generates `pager_rail.png` (760x166): walnut grain from the pinned WS16BA backboard plus a brass inset and small hardware. Arrows and dots remain crisp native UI over the rail. The byte-verified manifest and all nine PNGs are **debug-only**. Original WS16BI image files are untouched and remain safe fallbacks.

New `test_ws16bk_future_bookcases.py` checks deterministic art hashes/bounds, all eight wordless styles, no new titles, page-model integrity through 201 real books, real-first/decoration-only separation, source and QA-signing continuity. `verify_ws16bk_apk.py` enforces all nine exact QA PNGs + manifest and ZERO new WS16BK assets in release. Both GitHub workflows run the generator, source tests, and packaged APK verifiers.

`QaActualBookcaseAndroidTest.kt` has new prev/next/dots traversal test over three pages, verifies decorative-only second and third cases, no fake book taps, real first-page taps still select stable Garden ID, and confirms the old book count is absent. Portrait regression also tests pager clearance relative to woodwork.

**Version:** `versionCode=70`, `versionName=0.9.26-future-bookcases-and-pager-qa`, applicationId `com.playereden.gridmates.wordsearch.qa`, same QA certificate as v69. Install over current v69 **without uninstall/data loss** expected. No new login/cloud/ads features.

**Source commit at first QA:** [`48689e69ab1cccf9396ad9944035b1fdced63170`](https://github.com/PlayerEden/GridMates-Word-Search/commit/48689e69ab1cccf9396ad9944035b1fdced63170), draft PR [#131](https://github.com/PlayerEden/GridMates-Word-Search/pull/131), branch `feature/ws14b-style-anchor-and-art-direction-2026-11-08`. Later documentation-only updates include `[skip ci]` and must not be mistaken for compiled source.

## QA/rollback/owner gate

- Android run: exact-source CI pending at initial log creation.
- Art/release run: pending at initial log creation.
- **No v70 APK delivered yet**. On failure, inspect and repair precise source/job, rerun both and retain failed build IDs.
- Last owner-installed v69 [artifact #13997661426](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/43336457623/artifacts/13997661426) is the visual fallback. Android downgrade installation after a newer code may require special handling; never tell owner to uninstall and lose saves.
- **Visual acceptance:** owner screenshots page 1, page 2 and page 3, checks prev/next/dots and return to owned books, confirms blank taps do nothing, and confirms local progress survives install-over. CI-green is not phone approval. PR remains draft, art stays debug-only.

## WS16BL — next pass, deliberately deferred

Once WS16BK paging works on the owner's phone, add cabinet-button tactile feedback: subtle 96–140ms press-in scale, deeper inset shadow, brass glow release, slight guided arrow travel, active-dot light sweep and optional low amplitude haptic where appropriate. Respect reduced motion and touch exploration; no continuous glitter/bounce, no unnecessary transition that distracts older players. Add visual tests for Back, menu, previous/next, disabled states and fast repeated taps. Keep paging semantics stable. **Do not implement animations before the v70 phone review.**
