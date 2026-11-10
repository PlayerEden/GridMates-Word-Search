# WS-16Y — QA-only GridMates Developer Overlay

**Owner request:** Before testing the latest Word Search shelf-shadow cleanup, add optional on-phone visual diagnostics for spacing, actual element bounds, safe areas and touch markers. Useful for future GridMates visual QA; preserve normal play and allow screenshots with guides off/on.

## Source and scope
- App source commit: [`a89aeb375b3ccea85e741ef30d874c66d6130db4`](https://github.com/PlayerEden/GridMates-Word-Search/commit/a89aeb375b3ccea85e741ef30d874c66d6130db4), unmerged [PR #131](https://github.com/PlayerEden/GridMates-Word-Search/pull/131).
- Shared `DevOverlayFlags` state represents: master `enabled`, `grid` (8dp), `bounds` (measured Compose layouts), `safe` (drawing/gesture insets), `touch` (live pointer marker).
- **Debug source-set** `GridMatesDevTools.kt` holds all real controls, state store and canvas painting; **release source-set** holds same API as inert stub, with no overlay or touch listener. Debug settings save locally to SharedPreferences, default OFF, not part of puzzle/profile or exported backups.
- Settings & backup (reachable from Home menu) has **Developer Options · QA only** with collapsed “Show developer guides” disclosure and switches. Dev guides render above Scaffold but have no click handlers, hitboxes or layout size; non-consuming parent Initial pointer pass observes touch location without stealing paging/taps/Back.
- Actual key-item Compose `onGloballyPositioned` / `boundsInRoot` tracking added to Home header, book section, carousel, wooden shelf, Full Library, Collection, Continue Solo, Daily Puzzle, Together, Quick Play and bottom links. Orange outlined debug rectangles and labels correspond to live positions, not guessed approximations.
- Draws 8dp teal grid and both red safeDrawing and purple safeGestures frames using actual Compose insets at device font/density/Android display mode; optional pointer marker while finger pressed.
- Art source assets, wallpaper, book carousel, font and unlock collections, save logic, network and Android display-mode behavior untouched. Release art-isolation/QA hooks remain unchanged.

## QA
- On exact source, Initial failed Android [run #42888160812](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42888160812) and independent release art [#42888160920](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42888160920) started after commit, not green as of first doc write. AndroidTest `GridMatesDeveloperOverlayAndroidTest` asserts controls exist and compiles in CI, but does **not** constitute running instrumentation on the phone.
- Expected owner flow: Install debug QA APK over compatible signer; Home → hamburger → Settings & backup → Developer Options (scroll down) → Show developer guides → Enable overlay, select grid/bounds/safe/touch. Navigate back to Home, screenshot fullscreen both with overlay OFF and ON; test horizontal book swipes, footer taps, display-mode setting and Android Back. Toggle off before normal play.
- Release build must show **no developer controls, overlay graphics or pointer tracking**.
- No need to enable Android system developer settings; this is an app-contained QA tool.
- Last known dual-green rollback: WS16W code `d56460f5`, Android artifact #13796575169. If this experimental overlay fails compilation or input QA, fall back.

## Final tested build receipt

- Final source `7cb896fab29493202276991ee376acb9b9e92c36` includes the Compose import/test fixes after initial failed compile attempts; original overlay source `a89aeb37` remains in history.
- [Android QA #42889659113](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42889659113) **SUCCESS**, with core tests, Android unit tests/lint, debug AndroidTest **source compilation** and debug APK.
- [Independent art/release isolation QA #42889659313](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42889659313) **SUCCESS**.
- [Preferred GitHub QA artifact #13797149827](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42889659113/artifacts/13797149827), ZIP contains `app-debug.apk`. Standalone user file in conversation: `WS16Y_Developer_Overlay_QA.apk`, **15,537,609 bytes**, SHA-256 `309299bcc847dc03b9f52e05c5e0006ed6cd042c5e0625f5c10ad8a231a60f03`.
- [Alternate art QA artifact #13797868283](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42889659313/artifacts/13797868283): independently extracted; both APKs contain the **same 260 internal path names and uncompressed bytes** (outer APK ZIP packaging metadata may differ). All shelf art paths and five font notices were retained.
- Automated tooling validates code packaging **not the actual on-device overlay or swipe behavior**. Owner should test the grid and bounds guide with Home visible, tap the central book, swipe to Space, navigate Settings then back, and verify touch dots do not affect interaction. If overlays block touches or appear in release, treat that as a regression despite green CI.
- On-device acceptance still pending. Leave draft PR #131 unmerged until user screenshots and follow-up decisions.
