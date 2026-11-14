# WS-16AA — Painterly dimensional walnut shelf + subtly textured parchment
**Design/implementation brief — approved direction, not finished asset/code**  
**Date:** 2026-11-10 · **Project:** GridMates: Word Search · **Work branch:** `feature/ws14b-style-anchor-and-art-direction-2026-11-08` · **Draft PR:** [#131](https://github.com/PlayerEden/GridMates-Word-Search/pull/131)

## 0. Owner's actual request, corrected
The owner **does want shadows**. The problem with earlier WS-16V/W shelf art was a **hard-edged, full-width, rectangular/gray blur** that looked like a pasted-on overlay. Removing it entirely made the scene **too flat**. Owner supplied a reference screenshot of a richly illustrated storybook shelf with believable top surface, thickness, underside, carved face, brackets, softly grounded book shadows, natural lighting, and warm paper. This screenshot is **visual direction only**, not a reusable source bitmap or a UI to reproduce pixel for pixel.

The owner also wants a **small increase** in warm paper texture and tonal variation. Avoid a full-page wallpaper image or more motifs crowding the UI.

**Target:** retain the already-liked Home layout/carousel/card art and make the supporting environment feel like a tactile hand-illustrated puzzle book page, with the wooden shelf as a physical object.

## 1. Non-negotiables
- **Do not remove shadows.** Replace the boxy shadow with **shadows caused by real geometry**: darker underside, bracket crevices, small warm contact under individual book bases, and subtle diffuse separation from the parchment that follows the **actual shelf silhouette**, feathering smoothly with alpha. No rectangular color bar anywhere.
- **Use actual independent art assets** (proper original source files + transparent Android-ready exports) for the dominant wood texture, top/face, end supports and carving; do not propose another Canvas-only shade/gradient/fleck solution as if it were the finished painterly asset.
- Warm polished walnut/gouache watercolor, amber grazing light, soft natural pigment, fine but not noisy grain, dark brown recesses, engraved gold ornament. Reference has depth but not photo-real plastic.
- Preserve native **Compose** dynamic text, tappable book covers, native button actions, accessibility semantics, all six existing book cover/spine source assets and selected-book progress. No text baked into background or shelf.
- Reuse the series' restrained visual language: green/cream/gold, Literata for storybook and Atkinson for UI (not cosmetic unlocks), existing botanical corners untouched, no registered-trademark symbol.
- **No free lantern/mushroom/foliage props in this pass.** The reference contains decoration; this pass focuses on shelf material and background, so the scene is not cluttered. Props can be evaluated later.
- **No saves, game logic, LAN, signed package IDs, user profiles, fonts, unlock IDs, or cover-carousel gesture changes.** Existing Fullscreen and Standard modes must remain operable.
- Keep unapproved illustrations strictly in **debug/QA** assets, with release fallback and **zero QA art in release** until explicit provenance and release approval.
- Preserve and tag the pre-WS16AA look as a **reversible** comparison; do not overwrite existing `ws16g` hash-pinned source assets.

## 2. The new shelf: real modular painted assets
Create a coordinated **WS16AA_SHELF_V1** family of standalone editable masters (SVG/PNG/PSD or equivalent where supported; output final alpha PNG/WebP), independently exported and SHA-256 pinned:

| ID / role | Visual content | Layout/runtime treatment |
| --- | --- | --- |
| `WS16AA_SHELF_TOP_V1` | Rounded front edge and visible **top plane** with warm highlight; book-weight contact tint | Stretch/repeat **only the seamless central grain portion**, cap ends separate; maintain apparent thickness |
| `WS16AA_SHELF_FACE_V1` | Walnut front fascia, modeled light/dark planes, carved lip and subtle non-repetitive wood variations | Repeatable/nine-slice center, not indiscriminate `FillBounds` on distinctive knots |
| `WS16AA_SHELF_UNDERSIDE_V1` | Dark recessed underside/lower bevel; real **thickness** rather than flat rail | Cropped strictly to silhouette; deeper shade below face, never a background-colored rectangular glow |
| `WS16AA_SHELF_LEFT_SUPPORT_V1` | Painted left corbel/bracket with depth/edge occlusion | Fixed end piece at aspect ratio; align with top and front |
| `WS16AA_SHELF_RIGHT_SUPPORT_V1` | Painted right support in matching light direction | Independent art if asymmetric illumination; do **not** blindly mirror light/shadows |
| `WS16AA_SHELF_CARVING_V1` | Carved central gold-leaf emblem, slight relief/light and recessed brown detail | Center at natural size without scaling across width |
| `WS16AA_SHELF_CONTACT_V1` | Soft localized book-foot contact shadow, warm transparent alpha | Align under **centered hero book** atop wood; no full-width band |
| `WS16AA_SHELF_CAST_V1` | **Optional** very faint alpha silhouette shadow below **actual bracket/apron outline** | Only if visible improvement; transparently feathered, not a rectangular bitmap; no gray/black slab |

A source/master grouping that maintains separate export layers is acceptable; there must be independently testable sprites. Do not cut an image of the whole Home screen into pieces.

**Stack order (top to bottom):** contact beneath book / top-plane highlight; distinct front edge; painted face and inset carving; underside; bracket supports; optional silhouette-following ambient cast behind them. Order must look physically plausible at screenshot scale.

**Responsive implementation:** current reference is `Ws16EStorybookShelf()` in `Ws16GShelfArtwork.kt` inside the `Ws16BookCoverPager.kt` 252dp area. Current shelf height is **42dp**, book card placement is an intentional bottom anchor. A richer asset may need **about 48–56dp** total height *only if required for readable depth*; preserve **top resting surface at the present book-baseline** and avoid lowering book image, hiding button rows, or creating vertical scroll regressions. Adjust offsets transparently and test; do not change book-cover width or carousel page count.

**Asset scaling:** end-caps and bracket silhouettes must **never squash** with width. Use fixed-size endpoints plus truly tileable middle runs/nine-patch. Verify that repeating center grain does not produce visible stripes. Keep color/perspective consistent across Woodland, Space and Garden. Masked alpha edges must not show dark fringing on cream parchment.

## 3. Background: quietly richer paper, not a competing illustration
Create a separate original **WS16AA_PARCHMENT_V1** reusable series-friendly paper texture asset, e.g. seamless `512×512` or `1024×1024` tile plus (optionally) separately defined scalable edge wash:

- Existing `GridMatesParchmentBackground.kt` draws #F5F1E7 base, low-alpha warm gradients and deterministic flecks/fibers. **Retain it as fallback.**
- New texture should look like finely pressed or aged paperback paper: **fine organic pulp fibers, minute cream/tan mottling, faint paint wash and soft paper tooth**.
- Add small variation behind large negative spaces. **At normal phone size it should be felt before it is noticed**; the owl, title, buttons and text must remain dominant.
- Initial art calibration: keep most local luminance fluctuation within roughly **±2–4% relative to the existing base**, with a very soft **~1–3% perceptual darkening near outer edges**. These are review targets, not substitutes for looking at phone screenshots. Adjust alpha rather than painting a saturated overlay.
- Avoid a repeating visible checkerboard/grid, noise/static, dirty blotches, dark brown stains, photographic grain, strong vignette, high-frequency sparkle, fake torn edges, or baked botanicals. Preserve the existing separate real foliage assets.
- **Tile seamlessly** without visible 512px periodic motifs, on both narrow and larger devices. Prefer GPU-friendly immutable bitmap caching/brush rather than per-frame procedural noise.
- Ensure legibility behind smallest status text and button captions; dynamically measure contrast where possible. Paper should not change tint abruptly at Android safe-area edges or behind scroll.
- The sheet and shelf must share one light direction and warmth; visual coherence matters more than maximizing texture.

## 4. Actual source locations / integration path
Current code to inspect before changing anything:
- `app/src/main/java/com/playereden/gridmates/wordsearch/Ws16BookCoverPager.kt` — one real moving carousel; shelf positioned at bottom.
- `app/src/main/java/com/playereden/gridmates/wordsearch/Ws16GShelfArtwork.kt` — debug-only six-piece `ws16g` PNG runtime + fallback, currently uses `ContentScale.FillBounds`.
- `app/src/main/java/com/playereden/gridmates/wordsearch/Ws16TShelfPatina.kt` — hand-drawn line glaze, should not fight actual texture.
- `app/src/main/java/com/playereden/gridmates/wordsearch/Ws16EStorybookShelf.kt` — native Canvas fallback for release and failed-asset case.
- `app/src/main/java/com/playereden/gridmates/wordsearch/GridMatesParchmentBackground.kt` — existing deterministic near-flat background.
- `app/src/main/java/com/playereden/gridmates/wordsearch/GridMatesShell.kt` and `Ws16HomeScreen.kt` — safe areas, scroll and bottom actions.
- `docs/ASSET_CREATION_GITHUB_INTEGRATION_PLAYBOOK.md` — actual local image bytes → Git blobs/tree/commit/ref → SHA256, debug import → dual-CI.
- `design/visual-system/WS14_VISUAL_STYLE_BIBLE_AND_SPRITE_ASSET_CONTRACT_2026-11-08.md` — original art identity and review gate.

**Implementation production sequence:**
1. Inspect source and verify current branch HEAD/CI before adding assets. Compare owner images with real unmarked WS16Y screenshots and supplied reference. Record the **accepted material/depth behavior** (reference, not literal copy).
2. Produce **actual independently exported** shelf and paper masters; inspect transparent pixels, edges, grain, and at **phone-sized** render. Record provider/provenance/license state honestly. Never claim an image exists based only on a prompt.
3. Commit actual binary source/exports to GitHub using the established blob/tree/ref procedure or authenticated git; SHA256 and manifest must reflect bytes, alpha and dimensions. New versioned paths only.
4. Integrate debug-only through reproducible local-import/render script and make isolated paint components. Prefer bitmap assets for material, Compose for geometry/layout/interaction; retain tested legacy fallback without loading the old blocky shadow in the debug path.
5. Test with a **true toggle or separate QA comparison route** for Old vs New, if convenient, without shipping that switch in release. Old image must remain identical for visual A/B.
6. Keep current Home button arrangement/header/selected text, no new unrelated artwork. Check memory allocation and decoding (avoid decoding full-resolution masters for each recomposition); cache immutable decoded sprites.
7. Add asset existence/hash/alpha tests, compositing bounds/safe area regression and release isolation. Verify two real GitHub builds. Hand out **one verified artifact with exact commit, APK SHA-256** only after green checks; never assert on-phone QA from CI.

## 5. Visual acceptance criteria — owner phone screenshot test
Capture with developer overlay **OFF** (then optional overlay ON for coordinates). Use one consistent device, UI scale and book index per pair. Both **Fullscreen** and **Standard** where supported:

1. **Woodland focused:** at normal zoom, clearly identify **top plane / front face / underside / corbels** without a blocky strip; central cover stands naturally, not floating. Gold carving readable but restrained.
2. **Space and Garden focused:** carousel moves intact, same shelf and realistic contact; neighboring side covers peek naturally without shelf jumps or scaled end brackets.
3. **Shadow close-up:** no gray rectangle, dark hard edge, straight uniform haze below the apron, tile seam, alpha fringe or repeated wood knot. Shadow is still present, soft and physically caused by the shelf / book.
4. **Parchment A/B:** new background is subtly warmer/richer and organic; no visible seam/repetition or gritty noise; legibility of “Your puzzle books”, progress, “Daily Puzzle” and “Quick Play” unchanged.
5. **Tight screen:** at 320dp, 360dp and 412dp plus larger/font-scale ≥1.25, shelf does not clip or push key actions behind device navigation; scroll and TalkBack still usable; all tap targets remain intact. Include tablet/landscape sanity check if supported.
6. **Function:** swiping, focused-book tap, Back, Full Library, Collection, Continue Solo whole-card tap and saved progress unchanged. Debug visual overlay neither influences taps nor appears in release.
7. **Performance:** no repeated full bitmap decode on recomposition, unnecessary overdraw or obvious scroll stutter; visualized asset files are source-pinned.
8. **Release gate:** candidate shelf/paper are absent from release assets until permissions and owner approval are recorded. CI success does not equal visual sign-off.

**Fail conditions:** gray rectangular undershelf halo, obvious paper tile repeating, shiny plastic/cut-out wood, stretched brackets or central ornament, slow scrolling, loss of any saved puzzle, touching existing approved book sprites, unpinned generated asset, or “recreated” reference as one full-screen static image.

## 6. Scope of WS-16AA vs later work
**This pass:** physical painterly shelf AND faint organic parchment **together**, compare and refine based on real device. No need to add lantern, ground mushrooms, new books, more fonts, new animations, expanded Collections or Quick Play behavior.

**Later:** after the art is approved, move into actual book-page solving, hints, saving and Library/Collections QA. The screenshot reference may inform later props, but do not add them automatically.

## 7. Source/QA baseline and rollback
The first approved WS16AA **direction** is recorded here, but **no new art asset/Android integration/APK is claimed by this brief**. As of handoff, original draft PR #131 contains WS16Z `959ee6a9` code change and later docs commits. The last independently verified full APK mentioned in source handoff is WS16Y `7cb896fa` ([Android #42889659113](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42889659113), [art #42889659313](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42889659313), [ZIP #13797149827](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42889659113/artifacts/13797149827)); re-check live GH CI before naming the latest green build. **Never uninstall a user's app or clear data to compare visuals.**

## 8. Ready-to-use build chat handoff
> Continue GridMates: Word Search on draft PR #131. Read AGENTS.md, the newest development journal/handoff, and `docs/design/WS16AA_DIMENSIONAL_SHELF_AND_PARCHMENT_ART_PRODUCTION_BRIEF_2026-11-10.md`. The owner's reference shows a 3D, cozy carved walnut shelf with real natural shadows and faint rich parchment; the problem was the rectangular *shadow artifact*, NOT shadows themselves. Produce and commit **actual original modular painterly shelf assets and a subtle seamless parchment texture**, integrate them **debug-only** on top of current Compose layout while keeping legacy fallback/rollback, then run **Android + art/release isolation CI**. Do not substitute Canvas-only gradients, a full-screen reference screenshot, pretend files or a documentation-only pass for finished art. Keep carousel, book/progress, fonts, saved game and menu actions intact. Share one exact-source verified APK for owner visual comparison after both checks pass. Log files/hashes/runs/artifact, and leave PR draft pending phone review.
