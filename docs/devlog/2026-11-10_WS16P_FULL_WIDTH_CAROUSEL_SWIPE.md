# WS-16P — Full-width swipe animation without permanent cropped side covers

**Owner evidence — November 10, 2026:** Three real phone screenshots of WS-16O were supplied, including a frame while swiping with Space selected. At rest, the new full miniature Woodland/Garden cover previews and green arrows worked; during a swipe, however, the large moving covers were **chopped at the narrow center pager boundary**, while fixed tiny preview copies remained beneath. This looked like duplicate book volumes and incomplete artwork. Owner didn't include text but screenshots are a concrete QA observation following the requested WS-16O visual check.

## Exactly implemented, not yet phone-reviewed

Source commit: [`be4d76eb2fe2fece0ef3aef319b789b96bd2660b`](https://github.com/PlayerEden/GridMates-Word-Search/commit/be4d76eb2fe2fece0ef3aef319b789b96bd2660b), in draft [PR #131](https://github.com/PlayerEden/GridMates-Word-Search/pull/131).

- `Ws16BookCoverPager.kt`: `HorizontalPager` now spans **full available carousel width**, not only `coverWidth`. Each pager page is full-width but the tappable native book content remains the original centered 186dp max-width art size/218dp illustration. During drag and animated page changes, full-size covers travel across the screen rather than being cropped at the center-column boundary. The normal device edge can naturally clip an exiting cover while in motion; not a permanent clipped side book.
- The miniature complete adjacent books and chevrons are drawn **after the full-width pager**, giving taps precedence at rest. They appear only when `pagerState.isScrollInProgress == false`, preventing frozen/duplicated mini books over the moving covers. Preview width grows slightly from max 67dp to 74dp but stays inside its side column. Each preview still calls the existing `onCoverTap(index, false)` to animate book selection.
- Both Home's displayed featured title/progress and the carousel's resting neighbor index now use `pagerState.settledPage` instead of `currentPage`; this prevents the counter and title changing in the middle of a drag, before the transition finishes. Tap-to-open active book, QA book IDs/positions and saved selection remain unchanged.
- `Ws16CarouselHomeAndroidTest.kt` retains the accessible next-book assertion and adds pager-presence assertion. These instrumentation sources compile in CI, but are not automatically executed on a real phone/emulator.
- Source changes are limited to the carousel renderer, related Home labels, and Android test. No book art, launcher vector, app splash, saving, gameplay, menu geometry, artwork-hash inputs, Standard/Fullscreen modes, library layout or catalog is changed.

## QA / release discipline

- Exact source: `be4d76eb2fe2fece0ef3aef319b789b96bd2660b`.
- [Android QA #42771031364](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42771031364) and [art/release-isolation QA #42771031442](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42771031442) were running at initial documentation; do not claim new APK until verified.
- Last dual-green fallback WS-16O [Android APK artifact #13741496198](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42769471322/artifacts/13741496198); original source `a16f0b7`.
- Accept on real phone only after testing both **dragging the center book** and **tapping left/right mini books**: minis should disappear during transition and reappear when it completes; the large covers should move as whole books across the scene rather than being chopped at the center crop. Compare Space selected, Woodland/Garden previews and arrows. Verify the featured progress line stays aligned with the settled book.
- Keep the three-book Library as-is, and continue toward actual puzzle-board UX after this pass. No fake loading spinner has been added. Draft PR remains unmerged.


## Exact source correction and refreshed workflows

- The initial functional commit `be4d76e` accidentally omitted an `Arrangement` import that the unchanged miniature-book Column requires. Before any APK was approved, corrected source commit [`57f73c7a50681b189a7ffae52d766daa539ca9e4`](https://github.com/PlayerEden/GridMates-Word-Search/commit/57f73c7a50681b189a7ffae52d766daa539ca9e4) restored that import. No behavior change beyond the intended WS-16P animation fix.
- Initial Android/art runs #42771031364/#42771031442 were **cancelled** in favor of the corrected source. They are **not test evidence**.
- Current source-matched workflows: [Android QA #42771300333](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42771300333) and [art/release isolation #42771300203](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42771300203), both pending at this log update. Never distribute an APK from the cancelled source.

## Final corrected-source WS-16P QA receipt — both successful

- **Exact compiled source:** [`57f73c7a50681b189a7ffae52d766daa539ca9e4`](https://github.com/PlayerEden/GridMates-Word-Search/commit/57f73c7a50681b189a7ffae52d766daa539ca9e4). This is `be4d76e` plus the `Arrangement` import restoration. Subsequent documentation-only `[skip ci]` commits do not modify APK code.
- [Standard Android QA #42771300333](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42771300333) **SUCCESS**, including source build/tests/lint and instrumentation test-source compilation. This is **not** execution of instrumentation on a device.
- [Artwork/debug-release isolation #42771300203](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42771300203) **SUCCESS**.
- Preferred owner [Android artifact #13742221740](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42771300333/artifacts/13742221740) contains `app-debug.apk`. Independently extracted 15,006,816-byte APK, SHA-256 **`50ea06a94e34d6ecffac6c354c2505573730aea814711cc5bb8b06ad5e05ef07`**, attached in conversation as `WS16P_Full_Width_Smooth_Carousel_QA.apk`.
- Alternate art QA [artifact #13742276575](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42771300203/artifacts/13742276575) produces APK SHA-256 `3d5de4294327c3e3ae235e05af0ba1d64a1008f91f074e6a8cbf9f1a14da6ee2`. **All 258 internal files have identical extracted bytes** between the two independent builds; only APK ZIP metadata differs.
- Re-read the actual APK art manifests and computed hashes of **8 shelf PNGs + 6 Home action PNGs + 5 botanical PNGs + 2 header brand PNGs = 21/21 correct**.
- Owner has **not yet run the corrected WS-16P APK on physical Android**. The screenshot-only bug is addressed by a source code fix, but the animation must be visually accepted on the device. PR #131 stays draft, no merge/release approval.
- Next: owner tests swipe and taps on both sides with **Space focused** and a screenshot captured mid-gesture and after settling. Then avoid further Home churn and return to Word Search puzzle-board usability, saved-game functions and feature completeness.

