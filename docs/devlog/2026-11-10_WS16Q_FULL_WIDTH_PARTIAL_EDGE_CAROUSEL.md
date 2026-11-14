# WS-16Q — One full-width partial-edge carousel, no static mini books

**Owner direction (Nov 10, 2026):** The owner prioritizes the *visual first impression* of the Word Search Home. They explicitly chose **option 1 first**: restore large, real partial neighboring books to the left and right boundaries of the actual screen, with no mini preview books and **no decorative frame yet**. If this still looks accidentally cropped, consider a gentle separate edge treatment *after phone review*, not now.

## What changed
- Code commit [`bfad66f6bcbf09a65d492aac0f01ba96a2d0cfb5`](https://github.com/PlayerEden/GridMates-Word-Search/commit/bfad66f6bcbf09a65d492aac0f01ba96a2d0cfb5), on open draft [PR #131](https://github.com/PlayerEden/GridMates-Word-Search/pull/131).
- `Ws16BookCoverPager.kt` now renders all books as **one and the same `HorizontalPager`** with `PageSize.Fixed(coverWidth)`, symmetrical content padding to center the current real book, and 10dp inter-page gap. Since pages are narrower than the pager viewport, the adjacent full-size books genuinely peek into the visible left/right sides. No duplicate rest-state mini pages, separate click actions, floating arrows, masks or fake volumes.
- The Home page shell has 18dp horizontal padding. The carousel pager alone requests a viewport 36dp wider than its normal content width via `Modifier.requiredWidth`, centered in the existing shelf container. This intentionally pushes the book view into those reserved left/right gutters **to the actual viewport boundary**, while retaining the current wooden shelf width, title/cards, and safe-area handling.
- Original hero cover calculation `(contentWidth * .59f).coerceIn(136.dp,186.dp).coerceAtMost((contentWidth - 80.dp).coerceAtLeast(120.dp))` and 218dp art height retained. During drag, each book scales smoothly from 1.0 in the center to .88 near the edge using actual `currentPageOffsetFraction`. The scale transform uses the bottom-center origin, keeping books visually seated on the same shelf instead of floating upward.
- Existing `pagerState.settledPage` label/progress state, focus persistence, stable `book.id` page keys, tap-current-book-to-open, tap-side-book-to-focus and Android Back logic are unchanged. Partial edge previews themselves remain tappable within the pager's scroll area; no two separate views to desynchronize.
- `Ws16CarouselHomeAndroidTest.kt` now asserts center pager and adjacent real `Select Space` page exist, while old miniature `ws16-carousel-next`/`ws16-carousel-prev` tags are absent. **This source compiles in CI; it is not a device/emulator instrumentation pass.**
- No book titles/pages/unlocks or other gameplay changed, no new art assets, launcher changes, fonts, splash behavior, network features, release approval or save schema. Both Standard and Fullscreen continue to work with the same pager.

## Acceptance and visual QA
1. **Woodland selected:** one large center Woodland, a visible slice of actual Space cover at right extending to screen edge. No separate miniature or floating green arrow.
2. **Space selected:** large Space center, original Woodland and Garden covers visible partially at left and right. Move swipe slowly and capture mid-swipe; the three real books must move continuously with no duplication or abrupt center-column crop.
3. **Garden selected:** only Woodland/Space-related preceding volume on left depending on catalog index, no phantom fourth book.
4. Swipe works from the center book and partial visible side book; tapping adjacent partial book selects it, tapping current center book enters Book Library pages. Native progress should change *after* page settles, not halfway through a gesture.
5. Ensure partial books actually reach the visible viewport edge (rather than stopping 18dp short), no bleed beneath protected system controls or logo, no scroll regression, no taller Home/smaller central cover.
6. Compare Fullscreen primarily, and Standard secondarily. If hard edge still feels accidental, defer optional designed edging to the next owner-approved pass; don't preemptively add it here.

## CI/build discipline
- Exact source commit: `bfad66f6bcbf09a65d492aac0f01ba96a2d0cfb5`.
- [Standard Android QA #42866268890](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42866268890) and [art/release isolation #42866268846](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42866268846): **running at initial log**.
- No new APK claim until both workflows finish and actual ZIP/APK contents are inspected. Previous verified WS16P [Android QA artifact #13742221740](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42771300333/artifacts/13742221740) is the known-good rollback if this experiment fails; note older 3-day GitHub artifacts expire.
- This is one focused consolidated test pass, not a production merge.

## WS-16Q final source-matched QA receipt (Nov 10 UTC)

**Compiled code:** [`bfad66f6bcbf09a65d492aac0f01ba96a2d0cfb5`](https://github.com/PlayerEden/GridMates-Word-Search/commit/bfad66f6bcbf09a65d492aac0f01ba96a2d0cfb5). Later `[skip ci]` documentation commits do not change the APK source.

- **Standard Android QA** [#42866268890](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42866268890): **SUCCESS**. [Owner ZIP artifact #13788295356](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42866268890/artifacts/13788295356) includes `app-debug.apk`. Independently extracted to `WS16Q_Partial_Edge_Carousel_QA.apk`, **14,990,432 bytes** and SHA-256 **`9d43e3360bd9f01fea25d8397dd3cabb02ec526c4fec79eb045e548f0e08a513`**.
- **Art/debug-release isolation QA** [#42866268846](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42866268846): **SUCCESS**. Alternate ZIP [artifact #13787612114](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42866268846/artifacts/13787612114) contains a second APK with complete-file SHA-256 `c5cb47d5b2c99181f7f8037ca930aa3b8636ef00c577be7b1edf714584ad6b3b`. **All 258 internal APK paths and extracted file bytes match exactly between the independent builds**; their ZIP container metadata differs.
- Opened actual standard APK and verified **8 WS16G shelf + 6 WS16H Home action + 5 WS16I botanicals + 2 WS16M brand PNGs = 21/21 source/export hashes correct**. No artwork or stored progress changed. Candidate art release-isolation tests remain green.
- Android instrumentation test **sources compile**, but no instrumented phone/emulator tests were executed by the workflows. The owner has not yet installed this specific new carousel build, so there is **no claim of on-device visual acceptance**. PR #131 remains draft, unmerged.
- The GitHub Actions artifacts have 3-day retention. The compiled standalone copy in conversation has the independently checked SHA above; the downloadable GitHub artifact may eventually expire.

**Next owner screenshot request:** With **Fullscreen** active, capture (1) Space centered with actual Woodland and Garden partial covers at both physical side edges, (2) a mid-swipe and/or after-settle view to confirm smooth unified movement, (3) Woodland as first book to assess intentional right edge. Standard is secondary. If the real edge still feels harsh, experiment with the previously proposed delicate side edging as a separate optional next pass; do not preemptively add it or return to static minis.
