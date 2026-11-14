# WS16BN — Lower bookcase composition and true Home Play-green controls

> November 14, 2026. **v73 source committed; initial Android + art/release QA IN PROGRESS; owner phone visual/tap approval OPEN.** Preserve the v72 installed APK as the safe comparison until v73 is verified.

## Owner evidence and decision

The owner reviewed **real WS16BM v72 Android screenshots** of Library page 1 and page 2. The parchment controls still feel wrong against the walnut cabinet, despite matching Home’s general paper-card system. Their direction was specific:
1. **Lower the bottom bookcase paging rail**, releasing vertical space.
2. **Lower both shelves and their actual books together**, creating visible breathing room beneath the carved “Your Library” banner instead of pushing the title again.
3. **Replace the parchment icon buttons** with the actual deep emerald/green material of the **main menu Play button**, carried across all Library buttons. The dark walnut environment, current pager and motion remain.
4. Disabled arrow must retain the same green hardware face and subdued cream icon—not become translucent gray.

Do not substitute another broad cabinet redesign, new volume assets, saved-game changes or further animation passes.

## Exact code changes

- **Shared frame renderer** `GridMatesIllustratedFrames.kt`: add `GridMatesFrameVariant.GREEN_ICON`, using the **same shared `greenSurface = pill || greenIcon`** and exact Home Play pill gradient colors `#416C49 → #28583F → #173F32`, gold double outline, soft inset enamel bevel and short brass specular. The new compact icon variant has a 14dp rounded radius (Home Play pill stays 26dp), no separate image assets. Its face stays opaque when disabled; subtle dark green overlay is independent of the icon state. Original `PRIMARY_PILL` behavior and all existing cream card variants remain intact.
- **Unified controls** `WS16BICabinetChrome.kt`: same original `drawGridMatesPaperFrame`, now called with `GREEN_ICON` (not `MEDIUM`) for Back, artwork ellipsis, previous/next in portrait and compact scenes. Cream/gold `#FFF8E8` 24dp symbols replace dark green; disabled icons fade to 38% while frame remains green. Preserve previous physical 54dp clickable area, accessible labels, enabled/callback behavior, 115ms press-in/release movement, disabled feedback and Android motion-off policy.
- **Stage geometry** `WS16ATModularLibraryScene.kt`: derive a `stageTop = top + stageDrop` with `stageDrop = 18dp` for high-res portrait; `pagerDrop` is `min(22dp, max(0, bottom-32dp))` and `stageBottom = bottom-pagerDrop`. Recompute `available` from stage top/bottom, and apply those stage insets to the real `HorizontalPager`, keeping both physical shelves, spines and placeholders in one coordinated vertical composition. Move the bottom pager by the exact same `pagerDrop` using its bottom inset. Clamp against short cabinets; leave nonportrait layout unchanged. The cabinet shell, title plaque location, shelf artwork/pixels, book IDs, fake-book semantics, page count and save state are untouched.
- **QA source/unit safeguards**: `test_ws16bn_green_layout.py` checks exact shared Home Play gradient, green face even when disabled, cream icon, unchanged 54dp hit/115ms press, 18/22dp downshift on ordinary handsets and clamped base on narrow screens, row-major books and three-page starter layout. Existing WS16BM, WS16BL and WS16AT source tests updated for the new painter variant and stage insets. `QaActualBookcaseAndroidTest.kt` adds a 360×760dp sample with **42dp safe top inset** requiring books to clear title and rail to remain beneath second shelf, plus first-page disabled Prev status and Next navigation. Source compiles in CI; a physical device layout test still requires handset/emulator execution.
- **Android QA** versionCode **73**, versionName `0.9.29-play-green-library-layout`, same `com.playereden.gridmates.wordsearch.qa` package and existing public QA certificate. Intention: install **over v72** without uninstall/clearing app data. No new art generated for this pass; original QA art must stay excluded from release.

## Initial build evidence

- Active draft [PR #131](https://github.com/PlayerEden/GridMates-Word-Search/pull/131) on `feature/ws14b-style-anchor-and-art-direction-2026-11-08`; do not merge until owner visual acceptance.
- First exact-source commit [`7f4347c7fc93bf436a64179aafe338877ccc683d`](https://github.com/PlayerEden/GridMates-Word-Search/commit/7f4347c7fc93bf436a64179aafe338877ccc683d).
- [Independent art/release #43584113826](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/43584113826): IN PROGRESS when journal created.
- [Android QA #43584113831](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/43584113831): IN PROGRESS when journal created.
- No verified v73 APK or signing receipt yet at initial entry. Fix any build failures at an exact new source SHA and rerun both before sharing.
- Last user-installed v72: verified [Android GitHub ZIP artifact #14109290802](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/43582240449/artifacts/14109290802); APK SHA256 `0f7a57662cf8cf380b5e48ee2c0da8e6461a57ad1946135b90301cf6f6c29b53`. Do not instruct uninstall to downgrade.

## Physical acceptance gate

Owner should inspect new page 1, page 2 and page 3, including title clearance, two shelf contact planes, lowered pager-to-base fit, green button shades matching the Home **Play** control, and opaque-but-dimmed disabled Previous arrow on first page. Hold Back/options/Next to evaluate the already-liked 115ms press animation; test Back/options/Next/Previous, all three active dots, untappable future spines, owned book preview and preserved saved puzzle progress. Only after physical screenshots and feedback consider a further book-depth or cross-screen UI polish pass. Do not promote QA art to production, and keep PR #131 draft until accepted.
