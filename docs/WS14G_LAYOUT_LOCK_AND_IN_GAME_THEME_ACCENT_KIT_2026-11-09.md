# WS-14G — Layout Lock and In-Game Theme Accent Kit

**Date:** November 9, 2026 · **State:** OWNER DIRECTION → implementation plan. **No new art source or APK created by this plan.** This follows physical approval of the horizontal book browser and completion of the WS-14F code/QA checks; acceptance of a final in-game design is **not yet established**.

## What the owner wants

After seeing the illustrated Woodland/Space/Garden books inside the Android app, the owner wants us to **converge on one solid, usable layout**, then start producing **larger collections of consistent art**, including **a few simple theme-specific elements visible *while playing* a puzzle**. It should feel like choosing a Woodland, Space, or Garden book carries its world into the page, without building a visually noisy game or filling every surface with illustrations. Assets and palette/style rules should be reproducible and potentially reusable in GridMates: Sudoku/Crossword where appropriate.

**North star:** “One cohesive GridMates family, individual books with personality, the grid stays king.”

## Layout-lock gates: the next working vertical slice

**Gate 1 — Functional layout on phone (WS-14F acceptance):**
- Keep Swipe Puzzle Books as the comparison front-runner and **Classic shelves** as reversible QA rollback.
- Verify a new player can pick a book, recognize progress, open the next page, save a bookmark, return to the same selected book.
- Book details: condensed chapters, one readable page action per task, no wrapped control text at normal phone scale.
- Gameplay: examine 12×12, 15×15 and **20×20** on the phone with actual finger word-tracing, two-tap precise selection, zoom +/−, Fit, and pinch/pan. Check that controls don't steal touches and every grid state can be reached without gesture-only discovery. Capture portrait screenshots and one landscape/large-text pass.
- Do **not** call the layout locked merely because an APK compiles or a screenshot looks attractive. Owner approval after practical use is the gate.

**Gate 2 — Shared page chrome (next code pass):**
- Set stable visual zones: (a) compact **Back / save state** top line, (b) **book title + Page N + small thematic mark**, (c) **found counter and progress**, (d) Hint/status/tool strip, (e) largest practical untouched **word-grid area**, (f) words list/section marker below grid. Optional small completion accent only when genuinely solved.
- Align existing Play, Book Details and Collection with the shared paper, heading, panel spacing and native buttons without redesigning the game engine. A visually consistent **selected book → detail → game page** path is required before bulk sprite production.
- Responsive behavior matters more than decoration: 320dp/360dp/430dp phone widths, enlarged type (~200%), orientation, TalkBack and reduced-motion availability. Text and actions never depend on sprites.

**Gate 3 — Visual sign-off and scale threshold:**
- Compare **current (no gameplay accents)** to a **three-theme specimen** on the same source and same live page/puzzle. Capture three same-size device screenshots, one each for Woodland/Space/Garden.
- Accept if each theme is recognizable *without* compromising grid readability, touch area, native text contrast or progress truth. Reject if it feels like pasted unrelated stickers.
- Only after acceptance **freeze typography, component spacing, panel/shelf treatment and art slot positions** as Layout v1; produce broader asset sets on that locked layout. “Layout locked” means a reviewed reusable baseline, not a ban on future accessibility improvements.

## First in-game theme kit — keep it very small

**Reuse first:** existing `BookArtKind.EMBLEM`, `CHAPTER`, `BOOKMARK`, `COMPLETION` from `BookArt.kt` already select art from `WordTheme`. Prototype a 24–36dp emblem in the *native* book-page header without adding any new saved selection data or network dependencies. Current `BookPageGameplayScreen` receives `book.theme`; themes must be derived from the opened book, **not** a new user setting.

**Proposed first three *new* standalone transparent assets** (states all **PLANNED**, not generated):
- `PAGE_ACCENT_WOODLAND_V1`: small ivy/oak-leaf sprig or fern + tiny mushroom, subdued woodland green/brown. **Use:** beside page title or word-list heading (not over grid).
- `PAGE_ACCENT_SPACE_V1`: restrained Saturn-ring/star cluster, muted navy/gold, visually matching the existing Space jacket. **Use:** same slot/scale as Woodland.
- `PAGE_ACCENT_GARDEN_V1`: simple blossom/leaves or butterfly silhouette, muted sage/peach, matching Garden jacket. **Use:** same slot/scale.

All three must use the **same composition box, contour strength, light direction, opacity, padding and scale**. Avoid text, faux UI, registered trademark symbols, extraneous detailed characters or giant transparent sheets. Make one **approved style anchor** before batching; export separate lossless alpha PNG at 256px master with test-appropriate 64–128px runtime variant if file-size evidence supports it. Final resolution is subject to 320dp handset review. Store originals, provenance, edit notes, review state and SHA-256 in the existing asset registry and import pipeline; **do not invent hashes or claim rights approval**.

**Possible second batch after gate 3 only:** (1) three matching tiny page-corner ornaments OR (2) three understated solved-page flourishes; don't do both automatically. **Shared neutral family:** a quiet paper tab/divider for Word Search, potentially reusable across GridMates games without theming. Current emblem/completion assets may already do enough—avoid redundant new images.

## In-app placement / behavior

| Slot | Purpose | Treatment / limit | Fallback |
| --- | --- | --- | --- |
| Book-page header, near `Space · Page 3` | Immediately identifies book theme | One 24–36dp illustration, native text remains authoritative | Title only |
| “Words to find” heading | Very subtle themed flourish, if there is room | Reuse the *same* motif at smaller size, or leave blank. Don't crowd 20×20 controls | Plain header |
| Page-complete status | Celebrate genuine completion | Reuse existing `COMPLETION` art; optional one-time 180–220ms small pop | Native completion text |
| Board cells, tracing overlays, words list rows | **Core gameplay** | **No decorative art in these hit zones.** Preserve full contrast and letter clarity | Existing native UI |

**Motion:** 150–220ms feedback on select/complete only; never animate idle particle fields or loop effects during play. Respect system animator scale/reduced motion; static is valid. No glows covering board cells or altering touches.

## Integration discipline

- One **native Compose** reusable `BookPageThemeAccent(theme, slot)` concept; art source should be local/verified and optional. Missing/disabled art must not change page layout materially or crash.
- Gate through the existing QA-only `showPrototypeArt` / `fieldNotesArt` presentation layer while art is unapproved. The existing `BookArtKind` and WS-14 debug/release asset isolation remain the known pattern. No release packaging of unapproved generated sprites.
- Do not change `BookCatalog`, `WordTheme` IDs, seeds, saves/GMWS/GMWB, word claim coordinates, multiplayer packets, font unlock IDs, or original Woodland profile icons.
- Native actions and content descriptions always convey meaning; decorative sprites use null descriptions. Never print real progress in pixels.
- Release art rights and trademark checks are independent of CI. Keep **GridMates** plain; do not use ® while unregistered.
- Asset master and exports: versioned, binary originals, checksums, source/usage records, automated decode + resolution/alpha + APK presence/isolation checks. Follow [art pipeline](ASSET_CREATION_GITHUB_INTEGRATION_PLAYBOOK.md) and [shared GridMates identity](GRIDMATES_SHARED_VISUAL_IDENTITY_AND_MOTION_2026-11-09.md).

## Sequence from here — one small pass at a time

1. **Now (WS-14F):** owner device-check from [standard Android QA #42650786851](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42650786851) / [Art QA #42650786752](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42650786752), both **SUCCESS** on source `8fd7ff8056bb5ce224b040a9d5f90eb096c1ad0b`. Standard artifact [#13690146702](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42650786851/artifacts/13690146702) has the QA APK ZIP; **device acceptance is still OPEN**.
2. **Next code vertical slice:** before generating new sprites, reuse the existing three emblems in the native page header under a reversible QA presentation flag, with one 12×12 and one 20×20 viewport screenshot/test per theme; no new master artwork needed to validate dimensions.
3. **First art production pass:** after the UI slot is proven useful, create and version the three transparent accents above, with identical look/scale and exact asset receipts. Import into QA only. Owner reviews screenshots as a consistent three-theme set.
4. **Layout v1 sign-off:** document accepted book browse, detail, gameplay and Collection/Play hierarchy, target sizes, safe zones and motion rules. Then extend image library to other game screens and potentially other GridMates titles.
5. **Each source/art/build pass:** record an indexed dated devlog and one explicit next action. Preserve the last physically reviewed APK and production source; no uncontrolled bulk art generation.

## What does NOT happen yet

No new sprite image is generated by this document. No layout or gameplay changes, APK build, production merge, expansion of book themes or direct Sudoku repo edits. A separate APK build is unnecessary for this documentation-only pass.

**Next immediate action:** perform WS-14F phone test if not already done, then implement the tiny existing-emblem-in-game header QA specimen with screenshots, regressions and a fresh journal entry before generating the new matching three-asset set.
