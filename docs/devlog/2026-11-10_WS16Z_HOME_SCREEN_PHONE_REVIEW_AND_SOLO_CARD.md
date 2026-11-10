# WS-16Z — Owner-phone Home review and single-tap saved Solo card (November 10, 2026)

> **Quick status:** OWNER VISUAL REVIEW + SOURCE COMMITTED; exact-source Android/art CI pending at first receipt. This is not proof that updated code ran on a handset.

## Who, what, where, why

- **Owner evidence:** Six real Android Home screenshots from WS-16Y: unmarked Woodland/Space plus grid/bounds/safe-area screenshots on Woodland, Space and Garden, and a repeat of Space. The swipe carousel centers each book and updates book counts: Woodland 0/3, Space 0/3, Garden 1/3. Woodland saved Solo remains 2/44 words when another book is focused.
- **Work performed:** ChatGPT reviewed the screenshots and made a surgical Kotlin UI/AndroidTest source pass on [draft PR #131](https://github.com/PlayerEden/GridMates-Word-Search/pull/131); owner supplied phone images.
- **Goal:** Do not introduce another unrequested shelf redesign. Clarify that “Continue Solo” is an independent saved puzzle and make the entire framed card respond to tap, rather than only its green Play pill.
- **Location:** `Ws16HomeScreen.kt`, `GridMatesIllustratedFrames.kt`, `Ws16CarouselHomeAndroidTest.kt`.

## Previous checkpoint and screenshot findings

- Previous [WS-16Y developer overlay](2026-11-10_WS16Y_DEBUG_VISUAL_OVERLAY.md): source `7cb896fab29493202276991ee376acb9b9e92c36` was dual-green, [Android #42889659113](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42889659113), [art #42889659313](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42889659313), [verified ZIP artifact #13797149827](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42889659113/artifacts/13797149827); APK SHA-256 `309299bcc847dc03b9f52e05c5e0006ed6cd042c5e0625f5c10ad8a231a60f03`.
- The Walnut shelf now sits directly below the covers, without the broad rectangular gray undershadow observed earlier; visual reviewer considers its proportions/position better, **not final owner sign-off**. Book right/left partial peeks are intentional. The debug grid shows clear bounds and safe-region frames; labels sometimes cover artwork, which occurs only with diagnostics ON.
- Screenshots do **not** establish that touch dots, taps, Android Back, Standard mode, accessibility font scaling, or save reopen all work. They also do not prove the updated WS16Z card click, which did not exist in this image set.
- No clear safe-area violation or severe button/card collision seen at shown device size. Do not invent numerical alignment measurements from screenshots.

## Changes and decisions

- `GridMatesIllustratedPill` now accepts an optional click callback, so the same green illustrated shape can be **decorative** inside another interactive action. All existing non-null call sites retain their actions.
- Continue/New Solo **outer illustrated card** is now one accessible tap target with action-specific semantics. Its inner green Play/New pill is inert as an independent control; the entire card routes to the existing onSolo/onNewSolo callback. There should be no overlapping click actions.
- Existing running Solo subtitle now starts with **“Saved:”** to distinguish Woodland's standalone progress from an unrelated focused carousel book. Completed Solo keeps the existing “Finished” state.
- Android Compose test source now asserts clickability of the full Solo card and checks the nested decorative pill in the **unmerged semantics tree**. CI may only compile, not execute, instrumented tests.
- **Preserved:** no changes to carousel/shelf layout, paperback background, 21 illustrated sprites, theme catalogs, book or Solo saves, custom fonts/collections, puzzle generation, Nearby, package IDs, debug overlay or production art isolation.

## Exact implementation / build receipt

- Shared pill nullable callback commit: [`9bc7c173`](https://github.com/PlayerEden/GridMates-Word-Search/commit/9bc7c1733bd28ee0f2a336ab51d5d91fa0b0caf8).
- Whole-card tap and saved-state label: [`85afb218`](https://github.com/PlayerEden/GridMates-Word-Search/commit/85afb2188d9de2821132fc7dda633c8f8393a77a).
- AndroidTest clickability assertion: [`a89a3448`](https://github.com/PlayerEden/GridMates-Word-Search/commit/a89a3448bcca661c41e3df9638526b47ddf351b8).
- Unmerged-test semantics correction: [`959ee6a9`](https://github.com/PlayerEden/GridMates-Word-Search/commit/959ee6a9094e96b20fdd46a1cece3a024a833d7b).
- **Source SHA tested:** pending exact-source CI conclusion for `959ee6a9094e96b20fdd46a1cece3a024a833d7b`.
- **APK from this pass:** not yet verified. Never distribute superseded intermediate builds, or assert success from an in-progress workflow.
- **Fallback:** last exact-source dual-green WS16Y debug APK is GitHub artifact #13797149827 (above). Keep QA package installed and save backups intact; no uninstall or merge required.

## Outcome and next task

1. Verify exact-code Android and independent art/release workflows on the latest code SHA; check APK artifact and source/checksum before labeling a new owner APK ready.
2. On phone, with guides OFF: tap the **left text/illustration area** and the green **Play** area of Continue Solo; both should resume the **same Woodland 2/44 saved puzzle**. Change focused book to Space/Garden before this to confirm independent game state. If Solo was completed, full card should go to New Solo.
3. Verify book tap opens the book, swipe transitions remain smooth, Home/Back routes work, and no unexpected vertical scroll was introduced. Capture further screens only if a problem is observed.
4. If those pass, declare the Home visual direction stable and move on to **real puzzle/gameplay and Library/collection QA** (including hint, 20×20 trace, saved progress), rather than adding more Home decoration. Quick Play remains an honest placeholder; WS-17 should make it real with its own generator before enabling it.

**New-chat one-liner:** Continue draft PR #131 on feature/ws14b-style-anchor-and-art-direction-2026-11-08, inspect WS16Z CI for source `959ee6a9`, preserve the now-reviewed shelf/carousel, verify whole-card Solo tap on phone, then pivot to puzzle/Library gameplay QA.
