# WS-16V — Remove shelf shadow stripe and establish separate GridMates font roles

**Owner phone observations (Nov 10, 2026):** WS-16S/T screenshots show a long strange gray shadow strip below the painted shelf, which hurts the visual quality. The buttons and book carousel are otherwise substantially improved. Owner confirmed that GridMates must have **one or two permanent identity fonts**, separate from fonts earned for customizing puzzle-grid letters. Existing font unlocks and saved games must remain compatible.

## Source and scope

- Source [`147c68ccea6dbbd7c55b605ce6a515d897147f62`](https://github.com/PlayerEden/GridMates-Word-Search/commit/147c68ccea6dbbd7c55b605ce6a515d897147f62), draft [PR #131](https://github.com/PlayerEden/GridMates-Word-Search/pull/131).
- **Shelf:** `Ws16TShelfPatina.kt` removes the 13dp outboard `drawRect(Brush.verticalGradient(...))` that caused a rectangular gray smear beneath the walnut apron on both Fullscreen and Standard. Existing hand-worked wood grain, restrained warm glaze, top/contact paint details and independent carved shelf parts remain. No attempt to create a brand-new painterly shelf image here; preserve all original rendered image assets and their hash-checked masters. Never put the gray underrail band back.
- **Permanent GridMates typography:** `GridMatesTypographyRoles.kt` is a pure Kotlin core contract: `STORYBOOK=literata`, `INTERFACE=atkinson`. These are always-on family fonts: Literata for headings/bookish labels; Atkinson Hyperlegible for buttons, guidance and smaller text. `GridMatesTheme.kt` records those roles and continues to use the already source-pinned, SIL-OFL, license-packaged files; no download needed on players' phones.
- **Puzzle board cosmetics stay separate:** `BoardFontCollection.kt` now exposes `unlockableFontIds` and checks at initialization that none overlaps the two brand IDs. The three actually **earned** fonts are IBM Plex Mono (`plex`), DM Mono (`dmmono`) and Patrick Hand (`patrick`), unchanged achievement milestones and saved IDs. The existing always-available board starter **Atkinson** is the same legible face as the UI by historical design, but is **never** an achievement unlock. Keeping the `atkinson` default prevents save migrations/rewards being silently changed. In future, if a distinct offline starter board face is introduced, it should be a deliberate separate change with migration and owner review.
- `CollectionScreen.kt` explicitly explains that the selected board fonts change *puzzle letters only*, not GridMates app menus or branding. Existing card previews and licenses unaffected.
- `BoardFontCollectionTest.kt` adds assertions that the brand IDs and board unlock IDs stay disjoint. No unlocking logic, achievements, currency, purchases, puzzle seeds, ranks, save schema, or profile changes.

## Visual design next step
- **WS-16V** addresses the conspicuous bad shadow; it's not claiming that the shelf is now as richly painted as the book covers. A dedicated real painterly SVG/PNG design pass can replace the existing layered shelf artwork later, with source exports/QA manifests and no distorted scaling.
- Preserve fullscreen-first Home, photo-sized illustrated books, partial-edge single pager, current button hierarchy, Custom/My Puzzles links and Standard opt-out.
- GridMates visual roles are a *cross-title design decision*, but no silent Sudoku repository edits in this pass.
- Planned separate **unlockable board typography collection** remains expandable (e.g., classic typewriter, clean mono, handwritten, future niche paperback designs). Any new fonts require license verification, app bundling, glyph-fit tests and a meaningful progression milestone. Do not quietly give brand fonts away as rewards.

## QA and acceptance
- [Standard Android QA #42879603513](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42879603513) / [debug-art vs release isolation #42879603631](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42879603631) were initially queued. **Not called green at writing**.
- Verify core tests and Android compile, native font resources and OFL notices, source-pinned 21 image assets and release-art isolation. Collect one source-matched APK only if dual-green.
- Owner should compare Fullscreen and Standard, concentrating on **removal of gray stripe directly beneath shelf**, and ensure the rest of the shelf still has appropriate depth. Also open Collection → Board fonts and verify text explains that board customization doesn't change app identity fonts, and existing unlocks/selections still work.
- On larger fonts/smaller devices, both branding and board choices remain readable; this source change should not affect Home height or card taps.
- This branch remains **unmerged draft**, with rollback via earlier source `4f796096` and verified WS16S/T APK. No claim of owner phone acceptance until real screenshots.
