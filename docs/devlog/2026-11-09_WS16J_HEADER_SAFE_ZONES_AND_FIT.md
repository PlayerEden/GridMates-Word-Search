# WS-16J — Reference-faithful Home header safe zones and compact Continue

**Status:** Code committed; owner physical handset acceptance pending. Initial documentation made while Android and art/release-isolation CI are still running. No new QA APK claim until those workflows pass.

## Owner evidence and scope
- Owner supplied two actual WS-16I portrait screenshots. The new upper foliage overlaps the top GridMates branding, Profile/Menu controls and headline; tiny extra scroll remains and the Continue frame is too tall.
- The owner then supplied the intended illustrated reference. Its title is intentionally grouped away from page-edge foliage, with separate space for right-side controls. This is a **layout-safe-zone fix**, not a request to delete foliage, repaint book covers, rebuild menus or add puzzle volumes.
- Owner approved moving straight into implementation. Existing three real spine-out Library books, empty shelf, Home carousel, puzzle data, local progress and gameplay must remain unchanged.

## Exact source change
- Code commit: [`a84ce7f34a5c450c5f0162c0083c2046c04af406`](https://github.com/PlayerEden/GridMates-Word-Search/commit/a84ce7f34a5c450c5f0162c0083c2046c04af406), in [draft PR #131](https://github.com/PlayerEden/GridMates-Word-Search/pull/131).
- `Ws16HomeHeader.kt`: use a width-aware outer `BoxWithConstraints`. On regular phones >=340dp and fontScale <1.35, move the native logo/title group 20dp inward and reserve an extra 7dp of negative space before the two still-48dp tappable Profile/Menu buttons. Under 340dp or with enlarged text, reduce inset to 4dp and omit extra spacer to protect usable text width. Title still draws live native text with ellipsis, not flattened artwork.
- `Ws16IHomeDecor.kt`: top-left and top-right translucent foliage are repositioned **upward and outward** and slightly smaller; they stay behind transparent Scaffold and cannot intercept taps. Bottom flowers/mushrooms remain unchanged. The original gold divider SVG is now displayed `FillBounds` across the full width at 11dp height, rather than showing tiny center-only lines in a 15dp Fit image; no art master/source hashes have been altered.
- `Ws16HomeScreen.kt`: Continue frame minimum reduced from 76dp to 68dp, vertical row padding 8dp -> 5dp; the Play pill remains at least 48dp tappable, the 40dp bookmark remains, and progress text remains native/accessible.
- `GridMatesShell.kt`: only PLAY Home changes: vertical outer padding 10dp -> 6dp, inter-section gaps 8dp -> 6dp. Other routes untouched, and Home still supports vertical scrolling on smaller or enlarged-text devices (do not force clipping to falsely eliminate scroll).
- `Ws16CarouselHomeAndroidTest.kt`: asserts accessible header/menu and Continue Play remain present alongside divider and all existing navigation tests. Android test source compilation is not proof of instrumentation execution.

## QA / release controls
- Source-matched standard [Android QA #42752903137](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42752903137) and [art/release-isolation #42752903075](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42752903075) started; **at first log: in progress**. No brand-new bitmap exports required this pass; prior WS16I five-asset manifest and WS16H six-action, WS16G eight-shelf QA asset hashes remain exactly pinned.
- Existing WS16I QA artifact [#13733198146](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42751167817/artifacts/13733198146) is last verified fallback (APK SHA-256 `a920d3969af39eacd1a5705f0e0d5e1ddce6291c0c8c780b165c207383de1af6`).
- No release merge/owner sign-off, no book expansion, save migration, new network behavior or change to library navigation.

## Acceptance checks after a dual-green APK
1. Compare Home at normal phone font with user's last screenshot: no foliage over logo/Word Search or Profile/Menu glyphs; title safely inset, decorative line visible.
2. Continue looks lower by around 8dp and remains 48dp accessible at the Play control.
3. Custom and My Puzzles should be visible without requiring scrolling on owner's usual portrait screen, while scroll remains available for accessibility/short screens.
4. Review narrow-width and >=1.35 fontScale layouts for ellipsis rather than title/icon collision.
5. No changes in Library three spines/empty shelf, focused book cover, save/resume or QA-only asset isolation.

**Next:** verify both workflows, inspect actual APK and SHA, then owner Home screenshot. If screen visually fits, return to the remaining genuinely painterly paper/panel assets, not new catalog content.

## Final WS-16J exact-source QA receipt (Nov 10 UTC)

**Compiled code source:** [`a84ce7f34a5c450c5f0162c0083c2046c04af406`](https://github.com/PlayerEden/GridMates-Word-Search/commit/a84ce7f34a5c450c5f0162c0083c2046c04af406). Subsequent documentation-only commits use `[skip ci]` and are not new APK code.

- **Android QA** [#42752903137](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42752903137) **SUCCESS**, including project unit tests, lint, Android instrumentation *source compilation*, exact art manifests and debug APK. Compilation is NOT on-device instrumentation execution.
- **Artwork/release-isolation QA** [#42752903075](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42752903075) **SUCCESS**, including release candidate art exclusion. PR #131 remains draft/unmerged.
- **Preferred owner QA artifact** [#13734770660](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42752903137/artifacts/13734770660), ZIP contains `app-debug.apk`, size **14,925,742 bytes**, SHA-256 `1e10a5bb62aa91e1f36f226f1b5de1624000f029220f518e50948ced622d126e`.
- Alternate art workflow [artifact #13734825356](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42752903075/artifacts/13734825356) contains an APK SHA-256 `6aceaba64b8beb6be15f29d357cc42cb3fc058d734ece5713619eb2e6cbe0a40`. The outer ZIP metadata differs, but independently downloaded both and compared **all 249 internal file paths and extracted bytes, all exactly identical**.
- Independently read the standard Android APK's source/export manifest files and verified **8 shelf sprites + 6 illustrated actions + 5 botanical pieces** and three manifests against exact embedded PNG byte hashes: all pass.
- No gameplay, existing book art, local save schema, bookcase/Library structure, media licensing/owner approval or production release changed.
- Owner has not physically tested this WS-16J corrected layout yet. A code/CI pass does not prove it eliminates the tiny scroll on owner's specific device; that needs the next screenshot.

**Next owner acceptance:** install standard artifact #13734770660, screenshot full Home at normal font. Check no leaf/letter/icon collisions; full-width gold header rule; Continue shorter and Play still usable; Custom/My Puzzles without tiny scroll. Check at least one larger-font setting for intentional overflow/scroll. If accepted, move to paper/frame painterly finishing—not expanding the three-book Library.
