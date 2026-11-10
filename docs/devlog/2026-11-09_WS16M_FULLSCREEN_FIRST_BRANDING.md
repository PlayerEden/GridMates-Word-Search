# WS-16M — Fullscreen-first GridMates brand and Word Search title

## Owner's decision and design constraint
Owner compared WS16L on their Android handset in both Standard and Fullscreen. **Fullscreen's illustrated top/bottom page composition is preferred**, despite familiarity with Android's three-button Back controls. User explicitly requested **design for Fullscreen and allow opting out to Standard**, with a stronger title composition that leaves space for upper-corner foliage. No requested changes to book content, puzzle mechanics, LAN, monetization, library, save schema or article layout.

## Implemented (November 9–10)
- [Original two independently editable SVG masters `b08798e5`](https://github.com/PlayerEden/GridMates-Word-Search/commit/b08798e54b40eb57951e0f505ab2d99e5166a52d):
  - `design/visual-system/masters/ws16m-brand/gridmates.svg`: generic two overlapping interlocking puzzle tiles in aged gold and forest green, intended to transfer to other GridMates puzzle titles.
  - `design/visual-system/masters/ws16m-brand/wordsearch.svg`: a separate forest-bordered word-search grid with golden pencil. Specific to Word Search, never part of a universal GridMates logo.
- Both vector components are **art assets**, separate from the words themselves. Keep live native text `GridMates` and `Word Search` for accessibility, clean scaling at different font settings and potential localization. Do **not** substitute baked/bitmap text or full-screen screenshot art.
- [App/source integration `2396d030`](https://github.com/PlayerEden/GridMates-Word-Search/commit/2396d03091f287765954d6b858b3ace8a0e99ebe): pinned CairoSVG 2.8.2 rasterizes both original SVGs to 256x256 RGBA transparent debug-only PNGs at `assets/ws16m/brand/{gridmates,wordsearch}.png`, with exact SVG/PNG SHA-256 manifest. Native `Ws16MBrandEmblem.kt` displays independent marks; original Canvas marks are fallback for release APKs, whose QA art is strictly excluded.
- `Ws16HomeHeader.kt`: the *real* content width on ordinary phones is ~324dp due to 18dp horizontal page margins; old `maxWidth >= 340.dp` condition excluded many ordinary phones. Adaptive new threshold is 310dp; on Fullscreen at 310–369dp the title block moves inward by 23dp, and on >=370dp by 35dp, preserving actual 48dp Profile/Menu touch targets. Narrow/enlarged-font screens reclaim spacing. Distinct 25/28dp emblems replace prior 22dp Canvas emblems in debug. Existing title word texts remain dynamic.
- `Ws16IHomeDecor.kt`: Fullscreen upper leaves grow from 152x100 to 168x113dp but are shifted higher and outward to prevent header collision. Standard keeps existing smaller/inset-safe decor; bottom botanical assets unchanged.
- `DisplayMode.kt` + `MainActivity.kt`: first-time/new-install display preference is now **FULLSCREEN**. Any explicit previously saved `STANDARD` remains STANDARD, and the user may change it at any time using Home overflow or Settings. The display choice stays in separate local SharedPreferences, not puzzle saves. The existing transient-swipe reveal and native BackHandler remain.
- `DisplayModeTest.kt` updated to assert default Fullscreen and stored Standard preservation; `Ws16CarouselHomeAndroidTest.kt` asserts both emblem nodes remain accessible in the native header.
- Two CI workflows render and source-hash test art before Gradle, assert exact PNG bytes in QA APK, and verify zero unapproved WS16M art in release APK. Artifact retention remains **3 days**. No unapproved candidate asset is silently promoted.

## Verification receipt at initial commit
- Exact new app code source `2396d03091f287765954d6b858b3ace8a0e99ebe`.
- [Standard Android QA #42763506745](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42763506745) and [QA art/release-isolation #42763506750](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42763506750) started and passed their dedicated WS16M SVG-to-PNG/render tests; Gradle steps **in progress at initial documentation**.
- The earlier WS16L source `871c0394` [artifact #13737283520](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42761082558/artifacts/13737283520) remains last independently verified APK if the new build fails.
- Android test source compilation does **not** establish actual on-device execution; user must approve the new brand and check main menu on real phone.
- Keep PR #131 draft pending owner approval. Avoid new book volumes. Finish menu artwork before content expansion.

## Owner acceptance after verified APK
1. On first launch with no display preference, Fullscreen should hide status and 3-button navigation bars. On upgrades with stored Standard, deliberately switch to Fullscreen via menu and verify it persists.
2. Compare corner foliage to live Word Search heading; no branch through emblem/letters; two marks feel visually distinct and intentional.
3. Confirm Profile/Menu still tap, full books carousel swipes, Play/Continue enters real puzzles, Custom/My Puzzles remain reachable, Back from nested screens works.
4. Toggle Standard and verify visible Android bars, sane safe insets and native controls. Check bigger font or compact width for ellipsis rather than overlap.
5. If artwork feels flat or weak, revise **the two original SVG masters**, not the book/game content or layout system.
