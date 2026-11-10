# WS-16W — Natural contact and silhouette-safe shelf finish

**Owner request, 2026-11-10:** After reviewing fullscreen Home with the revised fonts, the owner still found the shelf's shadow artificial and asked for a **controlled final cleanup** before considering a full painterly shelf-art replacement. Keep the large storybook carousel, buttons, identity typography and game logic unchanged.

## Exact changes

- Implementation commit [`d56460f51845f10e65a7c9840cfa65fd301ae2aa`](https://github.com/PlayerEden/GridMates-Word-Search/commit/d56460f51845f10e65a7c9840cfa65fd301ae2aa) on unmerged [draft PR #131](https://github.com/PlayerEden/GridMates-Word-Search/pull/131).
- `Ws16GShelfArtwork.kt`: stops **loading and displaying** the old full-width `contact-shadow` source PNG (still archived, hash-pinned and render-tested in the eight-piece QA registry). Instead uses a new narrow, warm, centered `ShelfBookContact` Canvas oval **on the top wooden surface**, not under the shelf apron, with transparent radial feathering. The hero book's contact point remains visually grounded without a gray rectangle stretching across the background.
- `Ws16TShelfPatina.kt`: removes the cross-width edge wash and rectangular warm fill that were being drawn past the shaped shelf ends; retains only sparse subtle pigment curves **clipped inside x=28dp to width-28dp and y=16–29dp** on the actual walnut face. Every mark is deterministic, and no new raster assets are added.
- `Ws16EStorybookShelf.kt`: removes the two broad fallback oval shadows behind the entire top rail, replaces them with one smaller low-opacity contact mark inside the top plank so release/fallback parity is maintained.
- `test_ws16g_shelf_art.py`: explicitly asserts production UI no longer draws the old wide shadow layer and that patina has no outer-zone wash. All eight original debug source files and SHA manifests stay intact; do not confuse retained archive bytes with on-screen presentation.
- Books, real swipe carousel, main menu structure, card art, full/standard display preferences, keyboard/system navigation, save files, titles, user-selected board fonts, commercial OFL notices, puzzle gameplay and production-release isolation untouched.

## Visual and build acceptance

1. In Fullscreen Home, no visible gray rectangular bar behind the bracket ends or under the shelf. The shelf retains its walnut contour and gold center medallion and its top subtly grounds the central book.
2. Standard mode likewise has no silhouette bleed. No changes in menu vertical size/scroll or the featured book's size.
3. Swipe Woodland → Space; centered contact stays discreet, not thick or conspicuous. Partial side books and shelf geometry remain unchanged.
4. If the actual handset still says **code-drawn** or overly clean, the *next* separate pass should deliver a genuinely painted shelf SVG/PNG master; no more procedural ambient-shadow passes.
5. QA runs for exact source [Android #42884240846](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42884240846) and [art/release isolation #42884240843](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42884240843) started; **pending when this log was first written**. Only claim APK ready once dual-green and actual APK contents checked.
6. Previous WS16V code source `147c68cc` remains rollback; old debug artifacts subject to three-day retention. This is visual QA **not** production approval or phone acceptance.
