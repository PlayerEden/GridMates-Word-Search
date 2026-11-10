# WS-16K — Adaptive Standard / Immersive fullscreen decision build

**Status:** Feature implemented on the open **draft** [PR #131](https://github.com/PlayerEden/GridMates-Word-Search/pull/131). Android + artwork QA for corrected source `3d3844eb` had not yet completed when this initial handoff was authored. No APK/phone success is claimed until verified.

## Owner's goal and evidence

The owner viewed WS-16J on a real Android phone with **three-button navigation**. The translucent grey bottom navigation area obscured the bottom flowers and mushrooms, while the status bar occupied space at the top. The owner's reference is a fully illustrated storybook page with a confident botanical header and footer. Owner explicitly wanted to compare **Standard seamless edge-to-edge** with **Fullscreen immersive**, choose a presentation, then continue polishing the game, not add books/features.

## Implemented source behavior

- Feature commit [`5ae9296f`](https://github.com/PlayerEden/GridMates-Word-Search/commit/5ae9296f82c7506801b6152e834016ce9f449efa), followed by [`3d3844eb`](https://github.com/PlayerEden/GridMates-Word-Search/commit/3d3844ebf41f9c7609ec86ecdee6a08743cabb57) to drop invalid imports for `PaddingValues` member insets.
- `DisplayMode.kt`: stable `STANDARD` and `FULLSCREEN` enum, safe parser and separate local SharedPreferences file `gridmates_display_preferences`. **No gameplay-save schema / backup changes.** Default Standard.
- `MainActivity.kt`: `enableEdgeToEdge()` on older and newer Android, three-button contrast enforcement disabled on Android 10+, dark system glyphs on warm parchment. Standard shows bars. Fullscreen hides both status and navigation via `WindowInsetsControllerCompat` and uses `BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE`. Restores selected presentation on window focus regain (app switch/document picker). No deprecated sticky immersive flags.
- `GridMatesShell.kt`: the parchment still draws behind the entire window. Standard content uses `WindowInsets.safeDrawing`; Fullscreen uses `WindowInsets.safeGestures` to protect cutouts/important swipe regions. All app pages are affected consistently; app navigation and Back behavior unchanged.
- `Ws16HomeScreen.kt`: mode displayed in existing accessible Home overflow: “Display: Standard · Switch to Fullscreen” or the reverse. Switching is immediate. Fullscreen adds **14dp of breathing room** above the Home title to make botanical edge art more prominent without modifying the book carousel.
- `SoloSettingsScreen.kt`: dedicated native Standard/Fullscreen selection, current-state label and swipe-to-reveal explanation; controls remain at least 48dp tall.
- `Ws16IHomeDecor.kt`: two distinctly composed visual modes. Standard keeps upper leaves below/near the visible status inset and bottom flowers physically above the navigation-bar area. Fullscreen draws larger corner foliage and flowers into the newly available screen edges. Decor remains behind transparent Scaffold, cannot steal touches.
- `AndroidManifest.xml`: `windowSoftInputMode="adjustResize"` for keyboard/inset handling.
- `DisplayModeTest.kt`: parser/default/round trip tests. `Ws16KDisplayModeAndroidTest.kt`: Android test source for overflow toggle and Settings route. **Android test source compilation is not an emulator/physical-device pass.**

## How to compare on owner phone

1. Install the source-matched **debug QA APK** only after both workflows pass. Back up local progress before uninstall/signature conflict.
2. On Home tap the three-line menu → **Display: Standard · Switch to Fullscreen**; the app should immediately hide both Android bars.
3. Compare the header logo/profile/menu, foliage scale, and footer Custom/My Puzzles, flowers and mushrooms. Swipe from bottom to reveal system navigation temporarily, then return.
4. Toggle back from the menu or go to Settings & backup → Display mode → Standard; confirm system bars return and parchment remains behind transparent three-button nav.
5. Reopen the app and confirm the chosen mode persists. Briefly enter Solo/Daily, open Full Library and return, then verify local progress. Test in portrait first; orientation/small/large font/device differences remain owner QA.
6. If immersive navigation feels confusing or interferes with gestures, keep Standard as default. Owner decides after side-by-side phone screenshots; **do not make immersive mandatory** without approval.

## Scope and guardrails

Keep all 3 real books, spine-out Library, current Home buttons, Play/Continue semantics, local Solo/Daily/Together logic, save data, artwork manifests and production release gates. No fake books, redesigned menus, online service or ads. Both modes rely on Android system insets/gestures, so no guaranteed visual identical result across every phone. Fullscreen is a **reversible preference**, not a separate app.

## QA
- **Active functional source:** `3d3844ebf41f9c7609ec86ecdee6a08743cabb57` (earlier `5ae9296f` may have a compile failure due imports).
- [Standard Android QA #42756573168](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42756573168) — **pending initially**.
- [Art/debug-release isolation #42756573078](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42756573078) — **pending initially**.
- Last previous dual-green source WS-16J `a84ce7f`: [QA artifact #13734770660](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42752903137/artifacts/13734770660), debug APK SHA-256 `1e10a5bb62aa91e1f36f226f1b5de1624000f029220f518e50948ced622d126e`. Do not replace until WS16K dual-green.
- Next: verify both exact-source workflows, fetch actual artifact and hash APK internal contents; update this devlog and active handoff, then owner phone comparison/choice.

## Final exact-source QA receipt — BOTH CI workflows succeeded

**Actual compiled functional source:** [`3d3844ebf41f9c7609ec86ecdee6a08743cabb57`](https://github.com/PlayerEden/GridMates-Word-Search/commit/3d3844ebf41f9c7609ec86ecdee6a08743cabb57); later documentation-only commits use `[skip ci]` and do not alter compiled game/art logic.

- **Android QA:** [run #42756573168](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42756573168) — **SUCCESS**, including app/core unit tests, lint, Android test source compilation, debug APK and source-matched illustrated asset tests. It does **not** mean a physical phone or emulator executed instrumentation tests.
- **Artwork isolation/debug-release QA:** [run #42756573078](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42756573078) — **SUCCESS**, preserving the established separate candidate-art isolation gates and release fallback.
- **Install the standard Android QA ZIP:** [artifact #13736265925](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42756573168/artifacts/13736265925) → `app-debug.apk`. Independently extracted and hashed: **14,925,774 bytes**, SHA-256 **`dc8ec4ff1a5eb0bff2e180b36f54b1f6f9fb052c059d5488e633cbb0b4b799f6`**.
- Alternate [art workflow artifact #13736685099](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42756573078/artifacts/13736685099) has outer APK SHA-256 `b4129f3af0f24db1ffa5d928ba8ac8cfe4a7e8d8a80e108c76df1deb90a229fd`, but independently compared **all 249 internal paths and decompressed contents, with zero differences**. Outer ZIP metadata differs.
- Independently opened standard APK and verified **eight WS16G shelf + six WS16H Home action + five WS16I botanical assets**, each against their real embedded source-to-export SHA-256 manifests. No unreviewed art was promoted to release.
- A direct conversation file was created at `/mnt/data/WS16K_Standard_Fullscreen_Comparison_QA.apk`; it is the same extracted APK as the standard workflow artifact above, not a separate signed release.

### Still requires real-device review
The existing owner phone uses Android 3-button navigation. Compare Standard and Fullscreen via **Home three-line menu → Display: Standard · Switch to Fullscreen** and back (also under Settings & backup). Specifically check transparent nav contrast, top foliage not overlapping header icons, full bottom flowers/mushrooms, safe Custom/My Puzzles taps, swipe-to-show transient bars, restart persistence, and return from library/solo/nearby. Some device skins apply their own navigation UI behavior. **No owner decision or physical-device validation of this new WS16K build yet.**

Keep PR #131 draft, save files and old WS16J fallback. The next step is a single owner screenshot/choice, not additional menu design or book catalog expansion.
