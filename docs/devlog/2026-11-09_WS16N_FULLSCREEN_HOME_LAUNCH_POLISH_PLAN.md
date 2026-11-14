# WS-16N — Fullscreen-first Home fit, actual Word Search launcher identity, no artificial loading delay

**Status:** Owner phone screenshot findings and next-scope design only. No APK, icons, splash, app code, test results or release flags changed by this document. Branch: `feature/ws14b-style-anchor-and-art-direction-2026-11-08`, draft PR #131.

## Owner screenshot readout
Three real Android handset images supplied after WS-16M:
- **Standard / three-button navigation:** Android status/navigation bars are visible, making top-left brand foliage compete with title; bottom three-button nav region still takes physical display space. Standard remains a supported opt-out, but owner explicitly wants **Fullscreen as primary design priority**.
- **Fullscreen with page at top:** the two independent illustrated GridMates/Word Search marks, centered storybook volume and wooden shelf look cohesive. Distinct leaves in upper corners and meadow/mushrooms in lower corners look better than Standard. Preserve these hero assets and their sizes.
- **Fullscreen after short scroll:** Home uses one `Column.verticalScroll(pageScroll)` around the entire header/content/footer; even a modest scroll pulls GridMates partly above visible top edge. The intended title is clipped while all cards move. This is not an isolated art overlap; it is a layout issue.
- **Bottom:** illustrated flowers grow close to the underlined Custom/My Puzzles link text and mushrooms frame the right link tightly; keep all footer tap zones clear of decorative visual clutter.
- **Progress ambiguity:** Home says `Woodland · 0/3 pages complete` under the featured *book*, while the Continue card also says `Woodland · 2/44 found` for the independently saved Solo puzzle. These can look like conflicting progress for one book. Consider a clear native label `Continue Solo` / `Solo · Woodland · 2/44 words` while preserving existing routes and stored progress.
- **Together:** squirrel illustration sits above label while Daily and other action buttons tend to use icon beside text. Consistent horizontal action layout and slightly larger original art would make the six buttons feel part of the same family.
- **Quick Play:** currently says `coming soon`; preserve that honest state and do not imply live functionality.

## Owner direction
- Prioritize finishing the existing **Fullscreen Home**; leave Standard as a persistent, user-visible opt-out, without spending the next pass redesigning Standard.
- The source-matched `ws16m/brand/wordsearch.svg` book-grid/pencil emblem feels right for the **Android launcher icon**. It is currently displayed by `Ws16MBrandEmblem` in debug Home, but the actual launcher is still `@mipmap/ic_launcher` backed by `res/drawable/ic_wordsearch_foreground.xml`, which is a different and simpler vector. Use the approved new emblem as the source for a dedicated adaptive icon and optional monochrome themed icon, while retaining the separate GridMates family mark. Check the 66dp safe zone/masks, 48dp launcher appearance and Android 13+ themed color, plus older-device fallback; avoid exporting the full two-line heading as a tiny raster icon.
- **Startup:** Android 12+ already provides a launch SplashScreen from the app icon. There is no need for a second full-page timer or fake loading progress. For a branded transition use `androidx.core:core-splashscreen` and a cohesive warm parchment background + simplified Word Search emblem (optional brief fade). Exit as soon as the first interactive Home frame is ready; only retain the splash while truly necessary local initialization is incomplete. No extra activity or forced wait.
- Preserve real Android Back behavior for nested screens. Hiding system bars in immersive mode does not mean disabling Back. The visible in-app Back button remains for discoverability; Standard/Fullscreen preference survives updates.
- Preserve all current book art, three real volumes, sparse spine-out Library, progress and solo/Daily/Together logic. No catalog expansion, cloud account, billing, or unrelated gameplay.

## WS-16N recommended ordered implementation
1. **Fit and scroll behavior first.** Measure screen content against available Compose constraints after fullscreen `safeGestures` insets; reclaim actual surplus from blank header gap and gaps between sections. Keep book cover size/wooden shelf and 48dp+ taps. Aim for zero vertical scroll at the owner's normal portrait size, while enabling small-screen/large-font overflow without clipping controls. Consider fixed header and gently scrollable main content on constrained sizes, but do not hard-disable scroll if natural content exceeds height.
2. **Bottom footer safe zone.** Keep flower/mushroom sprites decorative and non-touchable, reserve visible clearance around Custom/My Puzzles; separate decorative baseline from link hitboxes. Avoid introducing a fake 3rd navigation row.
3. **Consistency.** Reposition Together's squirrel art next to title, improve icon size/label balance. Keep existing interactive button semantics and Quick Play's coming-soon flag.
4. **Launcher asset.** Convert the distinct source `design/visual-system/masters/ws16m-brand/wordsearch.svg` into final production launcher foreground; provide adaptive and monochrome layers, compare circle/squircle masks and small-home-screen icons. QA-only SVG raster shouldn't be silently promoted to release before owner review.
5. **Native branded splash.** Configure an Android SplashScreen theme with parchment-color background and the Word Search icon; on cold/warm start immediately transition to live Home. A fixed 2–3-second animation would slow the casual game and should not be added.
6. **QA one consolidated APK.** Screenshot Home at the top and after a swipe in Fullscreen, navigate into puzzle/Library and use device Back swipe/reveal. Confirm first-run default Fullscreen, stored Standard opt-out, portrait/short/tall phone, 1.3x font, zero tappable-content overlap, launcher icon after installation and launch behavior. Source-matched Android and art/release workflows must be green, APK source hash inspected before claiming ready.

## Concrete pass criteria
- At owner's normal phone font-size and height, the entire Home can be read including Custom/My Puzzles with no scroll, **without** shrinking the illustrated book substantially or reducing controls below 48dp.
- At shorter heights or enlarged text, overflow is intentionally scrollable and any top obscuring behavior is predictable; no header art clipping over text.
- Footer flowers and mushrooms frame links instead of touching their labels or covering tap areas.
- The same recognizable Word Search mark links Home header, launcher and native splash, with source/production boundaries clear.
- Fullscreen remains default for first-install users; previously saved Standard remains respected.
- No fabricated books, no extra startup delay, no gameplay/store/save migration.

**Next after implementation:** owner handset screenshot and judgement; only then decide whether to polish illustrations on secondary screens. Avoid generating separate APKs for each tiny tweak.
