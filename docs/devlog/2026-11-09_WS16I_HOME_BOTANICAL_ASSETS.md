# WS-16I — Focused Home finishing: organic botanical overlays and antique-gold divider

> **Status at creation:** Five individual original artwork masters and actual debug-only Android integration COMMITTED. Exact-source Android and art CI both started; their final results and a new APK were not yet established when this note was written. PR #131 remains draft, no release approval.

## Context / owner decision

The owner installed WS-16H and supplied real screenshots of Home (illustrated buttons) and Full Library (three real spines on first shelf and empty second shelf). Owner's response: "Oh yeah we're getting there. We'll come back to the library but yes let's get back to the main menu." This settles the near-term scope: **stop iterating the Library or expanding puzzle books**, prioritize finishing the existing Home visual language against the owner's richly illustrated storybook reference. No new gameplay, books, store content, profiles, LAN or saves in this pass.

## What actually changed

- New original/editable source art, [commit `5e3cd201`](https://github.com/PlayerEden/GridMates-Word-Search/commit/5e3cd20176fb0b518d432692bd528502f3891330): five independent transparent SVG masters under `design/visual-system/masters/ws16i-home-decor/`: `top-left.svg`, `top-right.svg` (hanging golden-green leaves), `bottom-left.svg` (meadow daisies/flowers), `bottom-right.svg` (mushroom/grass cluster), `divider.svg` (antique-gold double line and leaf emblem). No text, copyrighted reference-image fragments, fake book art or touch layers baked in.
- Actual source rendering and UI integration, [commit `86047a8a`](https://github.com/PlayerEden/GridMates-Word-Search/commit/86047a8a476d812821ae45767574adbd674ff8fd):
  - `tools/asset_pipeline/render_ws16i_home_decor.py` converts each SVG with pinned CairoSVG 2.8.2 to standalone RGBA debug-only PNG in `app/src/debug/assets/ws16i/decor/`, recording source+export SHA-256 and dimensions in generated manifest.
  - `Ws16IHomeDecor.kt` loads each actual exported PNG, shows top foliage and bottom floral/mushroom cutouts **behind** transparent Home Scaffold in `GridMatesShell.kt`, and a lightweight independent 15dp separator after the native `Ws16HomeHeader`. All assets are contentDescription=null/decorative, never clickable.
  - Existing book carousel, 218dp focused covers, native title typography, 6 illustrated action sprites, Daily/Co-op/Custom shortcuts, dynamic accessibility labels, local progress, Library bookcase and other screens **unchanged**.
  - `test_ws16i_home_decor.py` validates the five decoded RGBA raster dimensions and source/export hashes; `verify_ws16i_home_decor_apk.py` verifies debug APK PNG bytes and their absence in the release APK. Both Android and separate art workflows run those checks. Android test source compiles an assertion for divider presence but CI compilation is not evidence of real device execution.
- **Layout principle:** decorative overlays are anchored to the screen edges *under* the Scaffold, so they cannot steal taps; no giant fixed Home screenshot, no new layout height for corners; the divider exists only when debug source art is present. Designs intentionally leave center text/interactive areas clear.

## Build and next action at creation

- Exact code source: `86047a8a476d812821ae45767574adbd674ff8fd`.
- [Standard Android QA #42751167817](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42751167817): in progress at initial log.
- [Art/Release isolation #42751167700](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42751167700): in progress at initial log.
- Last verified/prior owner-tested baseline: WS-16H [Android QA #42749504771](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42749504771) and art #42749504688, QA [artifact #13732658725](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42749504771/artifacts/13732658725).
- Verify both exact-source runs, fix failures, then extract generated APK and confirm five botanical sprites plus six previous Home action and eight shelf pieces; release must exclude unapproved QA art. Offer **one** tested APK for owner Home screenshot. **Only after owner comparison** decide whether stronger painterly paper/frame texture and a small lantern/shelf decoration are worth adding; do not revert the functionality or overcrowd controls.
