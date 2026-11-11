# WS16AW — Full-bleed five-piece Library; safe-area padding moved to controls
**Date:** November 11, 2026 (Kentucky) · **Status:** dual-green exact-source Android/art CI confirmed; physical handset review NOT performed · **PR:** [#131](https://github.com/PlayerEden/GridMates-Word-Search/pull/131), still draft.

## Who / what / where / why
- **Owner:** continue the modular Word Search Library from the previous full chat, removing the visible flat parchment strips above and below the painted cabinet.
- **Work:** assistant edited native Android Compose UI source on the existing draft PR. No cabinet illustration, local save, book identifier or content generated or changed.
- **Reason:** The book route was wrapped in Scaffold's safe-area `contentPadding`, so the entire cabinet and wood background were inset instead of letting just the interactive controls obey Android bars/gesture edges.
- **Goal:** The page *is* the bookcase. Architectural wood paints to all available root edges; Back/title, count/page navigation and book gameplay remain safe and accessible.

## Where we came from
- **Previous:** [WS16AV independently signed original-art phone-test APK](2026-11-11_WS16AV_LOCAL_SIGNED_MODULAR_LIBRARY_APK.md). Source HEAD entering this pass `ad2378f38674ad85b23c5cf702e98a472358b2c2`.
- Source `44c02730` passed Android run [#42974359635](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42974359635) and independent art/release run [#42974359642](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42974359642), but those builds **do not contain the new five original PNGs**.
- The original five architectural parts are still unavailable in GitHub and were packaged only in the **conversation-generated WS16AV test APK** using an independent signature; do not confuse that local APK with an official GitHub Actions art-integrated APK or a production install. Back up QA data before a signing-key change.

## Actual source changes
1. **`GridMatesShell.kt`:** The BOOKS_QA outer `Box` now fills the entire window; pass the Scaffold-derived `contentPadding` to `BookLibraryFlow` rather than applying it around the wood.
2. **`BookLibraryFlow.kt`:** Inset actual book-page gameplay, book details and unavailable-state UI; pass safe padding along to Library browse UI.
3. **`PuzzleBookshelfScreen.kt`:** Inset swipe browser and classic fallback, but forward insets to the real bookcase.
4. **`BookcaseBookshelfScreen.kt`:** The new WS16AT five-piece renderer receives insets without using them on its full-screen root; old immersive/empty/classic paths still receive their historical safe padding.
5. **`WS16ATModularLibraryScene.kt`:** Wood background, foreground pilasters/crown/base and independently paged shelf/book layers all fill the parent. A separate `ws16aw-safe-overlays` layer insets only Back, title, QA escape, native real-book count and pagination. Title text has one-line truncation to protect 48dp edge controls on small screens.
6. **`test_ws16at_modular_library.py`:** Added regression contract for full-bleed architecture versus safe overlays, protected book route and retained QA art gate.

**Code/test commits:** `5532616`, `962398f`, `2ded722`, `8a93ada`, `524b307`, `b523ca8`, `fefbe7a90b953859444658a06ac0a440bc2bd04b` (final tested code/source commit).

## Build and QA receipt
- Two workflows on exact HEAD **`46270d2707b26539d0b0a675d2e91c9c1bab3040`** (Kotlin/test code unchanged from `fefbe7a90b953859444658a06ac0a440bc2bd04b`):
  - [Android QA run #42978836813](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42978836813) — **SUCCESS**, exact branch HEAD `46270d2707b26539d0b0a675d2e91c9c1bab3040`, code still `fefbe7a...`.
  - [Art and debug/release isolation #42978836809](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42978836809) — **SUCCESS**, same branch HEAD/source.
- **Code-only Android QA artifact:** [ZIP #13836419629](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42978836813/artifacts/13836419629), ZIP 24,417,827 bytes, archive SHA256 `38c6a627d5a4e1b9503b1dec0614be0ce30de58fdc6a70e06aa1af88f7516b2d` (GitHub artifact digest). Separate art-check [ZIP #13837075652](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42978836809/artifacts/13837075652), ZIP 24,417,826 bytes, archive SHA256 `d2d79f0444dcdd62ef0c371870b6d97e0e2ef05ef0ed14cbf333d09f59a79e47`. These **do not contain the original five WS16AS images**; QA modular visual option remains gated off. Internal APK SHA256 not independently extracted, no install or phone test claimed.
- Even if both CI builds pass, they remain **code/fallback-art QA builds** while the five new WS16AS PNG files are absent from GitHub. They cannot demonstrate the new cabinet visual on an Android phone.
- Existing WS16AV locally independently signed APK is the **previous original-art visual-test candidate** and current phone-test fallback. Earlier GitHub-source WS16AT/WS16AU artifacts lack original art. Preserve existing book data and don't silently uninstall.
- No merge, Play release, gameplay, catalog, LAN, unlock or signing changes.

## Owner acceptance criteria / next action
1. Confirm both exact-source Actions statuses and any Kotlin/test errors. If failed, correct source and rerun both workflows on the same commit.
2. Durably source-control the original five WS16AS PNG images (or an authorized equivalent ZIP) using the pinned import contract; validate SHA256 and QA-only packaging/release isolation. **Do not substitute unrelated wood crops** or claim the independently signed WS16AV contains WS16AW code.
3. Build a source-matched Android QA APK with both the real art and this edge-to-edge layout, and visually review the **complete screen** on 320, 360, 412dp/tall/landscape and 200% font. Verify safe controls, wood coverage, two or more independent ledges, real spines touching the ledges, pagination only for real overflowing books, Back, book modal and View pages.
4. Separate reusable standalone title/count plaques, arrows, dots and other consistent GridMates controls only *after* owner approves the shell geometry and edge-to-edge composition.

**New-chat one-liner:** PR #131 WS16AW code is dual-green via Android #42978836813 and art #42978836809 on commit `46270d27`. The original five WS16AS shell image binaries must be imported to GitHub before a real-art full-bleed QA APK can be built and phone-reviewed. The ZIP is listed in the ChatGPT Game Design Library, but attempted raw-byte materialization failed, so the images remain unavailable for repository ingestion; an accessible attachment of that existing ZIP (not regenerated artwork) is the simplest next input. Keep independently signed WS16AV test APK and older library path as rollback.
