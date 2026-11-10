# WS-16E — Modular illustrated walnut shelf: first source implementation

**Date:** November 9, 2026 · **Draft PR:** #131 · **Game:** GridMates: Word Search.  
**Gameplay/assets source SHA:** `92a1d89905c5ce1ff1223d0558164795f400ee43` · **At documentation creation:** Android/art checks running, not yet verified. Subsequent [skip ci] documentation-only commits do not create new APKs.

## Owner intent and approved visual goal

The owner approved the warm illustrated screenshot reference with prominent storybook covers **standing on an actual ornate carved wooden shelf**, antique-gold buttons, paper panels, leaves and little decorations. Artwork must be modular where sensible, retaining native mutable button text/labels, and the Home layout may be tightened after seeing actual assets on a phone. The previous WS-16D Home with direct Full Shelf + Collection, Continue, Daily, Together, QA-only Quick Play, Custom and My Puzzles remains the functional baseline; no bottom navigation is reintroduced.

## Actual work in this first WS-16E batch

- Added **six original independent editable SVG masters** under `design/visual-system/modular/masters/wordsearch/`: top bevel `ws16e-shelf-top-v1.svg`, repeating front rail `ws16e-shelf-front-center-v1.svg`, separate left and right end brackets, center golden leaf medallion, and transparent contact shadow. Each is a separate source component, not a crop of the illustrated Home mockup. The SVGs are source masters, **not direct Android resources**.
- Added **`Ws16EStorybookShelf.kt`**: a native scalable Compose Canvas rendering analogous **wood gradient/top/rail**, detailed horizontal grain strokes, carved bracket supports, centered golden flourish and soft contact shadow, using separate graphical layers. No baked text; none of the shelf decoration is clickable. Shelf height is **34dp** vs old 13dp strip; geometry adapts to content width without stretching the central flourish or ends.
- Updated **`Ws16BookCoverPager.kt`** by replacing ONLY its old 13dp gradient strip with `Ws16EStorybookShelf()`. Its same 234dp pager, 218dp actual cover, fixed book IDs, neighbor-cover focus and selected-cover direct play behavior are intentionally unchanged. The shared swipe library inherits the same shelf.
- Extended **`Ws16CarouselHomeAndroidTest.kt`** to require the real `ws16e-storybook-shelf` node before validating the existing focused cover entry/back route.
- Added **`design/visual-system/modular/README.md`** to document editable source ownership, correspondence to runtime Compose art, and future shared GridMates parchment frame kit. See the full source-backed [WS-16E modular production plan](../design/WS16E_MODULAR_GRIDMATES_UI_ASSET_PRODUCTION_PLAN_V1_2026-11-09.md).

## Constraints and status distinctions

- This is the **first art-and-code build** of the shelf, not a finished painterly shelf or owner-reviewed visual acceptance. The hand-painted reference image is only a design target; do not flatten its screenshot into an in-game full-screen image.
- The six SVGs are clean independent **source designs**, not alpha PNG exports for runtime; the runtime uses its own Canvas implementation. The two are analogous by roles/color, not asserted pixel-identical. Do not claim a full PNG asset receipt, generated image rights approval or production release approval from this commit.
- Existing QA books remain debug-only. No book catalog, save model, profile unlock, Collection rewards, Quick Play generator, routing, screen text, or Android permissions changed.
- The shelf adds **21dp** of display height relative to the old 13dp strip; this is an intentional first fit tradeoff to review. If lower Home content drops below the fold, compress empty spacing or reduce bracket height before shrinking covers, unless owner prefers otherwise.
- User feedback should prioritize whether book bottoms actually touch the top, whether the shelf is ornate enough compared to book art, fit of neighbor peeks, page scroll and lower buttons, and whether wood/gold feels neutral across Woodland/Space/Garden books.

## CI and device QA gates

Check workflows at exact gameplay source SHA:
- [Android QA #42724381974](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42724381974) — core/app tests, lint, Android instrumentation **source compilation**, debug APK.
- [QA Art Lab / Release isolation #42724381998](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42724381998) — debug/release content and art gates.

Do **not** claim a new verified APK or install recommendation until both runs succeed and the exact artifact/checksum is confirmed. Instrumentation source compilation is not a device playtest. After both success, hand off one Android QA ZIP for the owner to compare against previous WS-16D [verified artifact #13717909916](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42719067784/artifacts/13717909916). Keep PR draft until owner approval.

## Next modular batch after shelf feedback

Make scalable `GM_SHARED` parchment frames (medium / feature / panel / CTA pill / dialog), with native icon/text slots and pressed/disabled/focused states. Refit existing main-menu controls into those frames as one grouped pass; afterward consider a small lantern, title divider, corner foliage and theme-neutral lower flora. Avoid a tiny APK per decoration.

## Final exact-source CI and owner test package

**Dual green on exact code SHA `92a1d89905c5ce1ff1223d0558164795f400ee43`.** Documentation-only [skip ci] commits after this SHA do not represent a rebuilt APK.

- [Android QA #42724381974](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42724381974): **SUCCESS** — core/app unit tests, lint, instrumentation test **source compilation**, assembled debug APK.
- [Art/release isolation #42724381998](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42724381998): **SUCCESS** — six existing QA-only book cover/spine sprites absent in release; 18 + 18 portraits/36 matched PNGs and three motif assets verified; no QA-only release contamination.
- **Owner QA ZIP:** [GitHub artifact #13720434658](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42724381974/artifacts/13720434658), contains `app-debug.apk`. Artifact label mentions Woodland historically; code is full WS-16E app.
- **APK SHA-256:** `a71680c1c3b476f25727a33efcf2ff657d0bcacf5377b897666b7b549edc7d53`. **ZIP SHA-256:** `fb73f1e2cf5168be30b8441f09e54d696827e3ad51f8b9741a32007829005d87`. ZIP final size 13,960,893 bytes as reported by GitHub.
- Editable-source files have independently computed, validated SHA-256 entries in [WS-16E shelf source register](../../design/visual-system/modular/ws16e-shelf-source-register.json). Status stays `GENERATED_UNREVIEWED`: editable SVG masters are not Android runtime PNG exports; their analogous native Compose shelf is QA tested, **not yet owner-approved**.
- Device QA/owner on-phone review still pending; test book/shelf contact, walnut vs book art quality, side-peek gestures, Full Shelf + Collection, Quick Play placeholder, and scroll height. Existing profile/custom/word-search saved data unchanged. PR #131 draft/unmerged.
- QA package/versionCode are unchanged from preceding v57 builds; before uninstalling for a signing conflict, export/backup progress.

## Next owner-feedback decision
If the shelf feels too flat or basic beside the painted covers, refine the **individual SVG-source and Compose rendering** with richer wood texture and a better carved bracket/medallion (rather than replacing Home with a static background). If it fits visually, proceed to the reusable GM_SHARED parchment action frame kit and only then secondary foliage/lantern polish. Keep covers large until phone evidence justifies otherwise.
