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
