# WS-16N — Fullscreen Home fit, clarified solo progress, launcher icon and native splash

**User acceptance context:** After WS-16M handset screenshots, owner wants one focused pass to fix slight scrolling/header clipping, decor/footer crowding, inconsistent Together action, confusing book versus Solo progress, and to adapt the attractive GridMates Word Search header emblem into the Android app launcher. They asked whether a loading screen is useful. **Decision:** use standard Android native splash with no forced delay; don't add a separate animated loading Activity.

## Actual implementation (branch `feature/ws14b-style-anchor-and-art-direction-2026-11-08`, draft PR #131)

- **UI source:** [`4dd16b20d`](https://github.com/PlayerEden/GridMates-Word-Search/commit/4dd16b20ddfdd14a2e5c1ecb7d297cfcf99cd1eb). `GridMatesShell.kt` uses available constraints in `BoxWithConstraints` to compact only fullscreen Home on ordinary portrait heights (>=690dp and <960dp, fontScale <1.30) by tightening outer padding/gaps 6→3dp. Short/large-font screens retain the previous scroll layout. `Ws16HomeScreen.kt` reduces only compact top spacer 14→7dp (book cover/pager **not resized**), passes compact state to buttons, and renames card to `Continue Solo` / `New Solo`, with word-count subtext distinct from the book's page count. `Ws16BHomeActions.kt` shortens Quick Play internal padding in compact mode (8→5dp), centers bottom links farther from edge art, and unifies Together icon-and-label horizontal layout while retaining tall tap areas. `Ws16IHomeDecor.kt` drops and slightly shortens fullscreen bottom flower/mushroom art so it frames rather than touches Custom/My Puzzles. Standard untouched.
- **Brand and launch source:** [`c642dbd17`](https://github.com/PlayerEden/GridMates-Word-Search/commit/c642dbd17c437ea4fb00d2610740ca0ff8110ffb). `app/src/main/res/drawable/ic_wordsearch_foreground.xml` now adapts the actual **WS-16M wordsearch SVG path geometry** from `design/visual-system/masters/ws16m-brand/wordsearch.svg` into an Android VectorDrawable (simplified gradients to solid source-palette colors; no separate raster). Scaled to adaptive 66dp safe mask. `ic_wordsearch_monochrome.xml` is matching one-color themed icon; `mipmap-anydpi-v26/ic_launcher.xml` uses both. Initial launch applies `@style/Theme.GridMates.WordSearch.Starting` from `res/values/styles.xml` to MainActivity and immediately `installSplashScreen()` before `super.onCreate`; post theme remains standard no-actionbar app. Parchment splash color `#F8F1E1`. AndroidX core-splashscreen dependency 1.0.1. No fake progress, no keep condition or timer.
- Fullscreen remains initial default **only for users without a saved display mode**, existing Standard preference persists. System Back handling on nested pages, local saves/book volumes and debug artwork isolation unchanged.
- New production app icon is **source-derived, not a byte-identical export of the SVG**. Confirm circle/squircle masks and themed home-screen icon on owner handset.
- The rest of the existing 19 hand-illustrated QA sprite assets remain exactly as they were. No new artwork assets are generated in Actions, and both standard and art workflow APK artifact retention is still 3 days.

## Verification
- Most recent app source commit: `c642dbd17c437ea4fb00d2610740ca0ff8110ffb`.
- Android exact-source run: [#42766438704](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42766438704) **pending at initial log**.
- Art/debug-release isolation exact-source: [#42766438685](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42766438685) **pending at initial log**.
- The immediately preceding UI-only run from `4dd16b20d` is not the final icon/splash build. Do NOT hand that APK to owner as WS-16N.
- Do not claim native splash actually appears correctly or scrolling is entirely gone until an owner handset screenshot. Android test source *compilation* is not on-device execution.
- Current latest previously verified fallback: WS-16M, source `2396d03091...`, [Android run #42763506745](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42763506745), artifact #13738871375 (may expire at configured 3-day retention).

## Handset acceptance
1. Install exactly source-matched debug APK from successful WS-16N Android run (preserve user local progress before uninstalling if signature conflict).
2. Verify launcher changed to green/gold grid with pencil; test mask and Android themed icon if available.
3. Force-close then cold-open: native parchment-color logo splash without extra timed wait; immediately functional Home.
4. On regular fullscreen phone, inspect at top, then make a short upward swipe; can Custom and My Puzzles remain visible without the header moving into foliage? Other form factors/large fonts may scroll intentionally.
5. Confirm footer flowers/mushrooms below labels, Together side-by-side icon/label, `Continue Solo` and its x/y word progress separate from book pages. Play, Daily, Together, Library, Back still route correctly.
6. Toggle Standard and Fullscreen, re-open app; verify stored preference respected. Keep **fullscreen as design target** and Standard as the user opt-out.
7. Only if owner screenshots confirm this, return to remaining gameplay/menu polish. **No new puzzle books**.

