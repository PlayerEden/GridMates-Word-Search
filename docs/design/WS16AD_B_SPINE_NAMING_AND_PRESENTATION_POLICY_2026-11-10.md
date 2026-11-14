# WS-16AD-B — GridMates Book Naming and Spine Presentation Standard

**November 10, 2026 · implementation and editorial contract**  
**Status:** Source-metadata rule ready for the WS16AD Book Factory. **Not a redesigned Full Library, finished artwork, or approved release.**

## Why this rule exists

The owner is planning many future GridMates: Word Search puzzle books. Short, memorable names help both the cover plaque and narrow physical-book spine remain legible, especially on a phone. However, we must not change established theme identities or squash a long title into an arbitrary template. In particular, **Woodland** is a valid existing eight-letter theme name and deserves a little more spine breathing room.

There are two independent design systems:

1. **Master-asset geometry**, locked for reproducibility: canonical transparent cover `480×640` and spine `128×640`, preserving edge, binding, lighting, book baseline and title safe zones.
2. **Future Full Library display classes**, which may proportionally scale a whole, correctly designed spine within a larger independent touch target. They **do not** stretch native PNGs horizontally/vertically or change the source master geometry.

## Naming guidance (editorial, not a catalog-validation migration)

| Name length | Recommended class | Future proportional library display | Examples |
| --- | --- | --- | --- |
| **1–7 letters**, one word | `standard` | Approx. **140dp** display height | Space, Garden, Ocean, Winter |
| **8–11 letters**, one word | `tall` | Approx. **148dp** display height | Woodland, Halloween, Underwater |
| **Longer than 11 letters or multiword** | `editorial-review` | **No automatic size**; typeset deliberately | Northern Lights |

**Preferred naming habit:** one meaningful word, ideally **4–8 letters**, not a ban on longer names. Existing approved names remain unchanged. Some good future short themes: Ocean, Bugs, Farm, Winter, Jungle, Desert, Castle. Do **not** rename a theme or its durable content ID without a separately approved migration.

**No unsupported trademark notation:** Never print ® or ™ beside GridMates on a cover or spine without verified legal status. The current old cover art still needs its baked-in ® corrected in the actual V2 artwork; a metadata policy does not erase pixels.

## Typeface, geometry and accessibility rules

- Preserve the **same 128×640 painted source canvas** for standard and tall. `height_class` is a **presentation hint**, not a second incompatible 3D book master.
- If/when the Full Library offers size variations, scale whole sprites uniformly to keep 1:5 width-to-height ratio: approximately **28×140dp standard** and **29.6×148dp tall**. The **48dp minimum invisible tap target** remains independent of painted width; current library already uses 48dp slots (54dp at higher text scales).
- These dp sizes are illustrative constrained initial options, **not an immediate layout change**; the existing 148dp library sprites remain unchanged in this pass. Broader bookcase experimentation stays scheduled later.
- Retain the same bottom resting baseline on the walnut shelf. A taller book rises **upward**, not down through the shelf or over adjacent text.
- Text must be authored **inside the spine source** or as a deliberately layered native label, within consistent safe margins; no automated horizontal compression, illegible microscopic type, fake line breaks, hard crops, or stretching just to fit a name.
- For unusually long words or multiword names, involve an editor: consider a naturally placed title treatment, wider/optional deluxe binding class, or a shorter **approved displayed nickname** while retaining the full accessible name. Never abbreviate automatically.
- Phone-scale QA in 320/360/412dp widths, accessibility text scale and TalkBack, plus dark/cream alpha-fringe views. Test each book's exact visible tap region and nonoverlapping selection behavior.
- **Coming Soon** linen/night/burgundy placeholders use common physical spine geometry but are not published themes. They have no playable catalog IDs, fake progress, or misleading tap action; don't run theme-name rules on anonymous blank spines.

## Executable rules and integration

- `tools/asset_pipeline/book_spine_policy.py` provides `spine_name_profile(name)`, using one-word letter counts to choose `standard`, `tall`, or `editorial-review`.
- `tools/asset_pipeline/build_ws16ad_book_factory.py` attaches `spine_name_profile` to the three existing, QA-only theme spine manifest entries and writes the human-facing profile contract into `manifest.json`. It does **not change the actual generated sprite pixels**, save files, chapter IDs, art release state or the current library Compose layout.
- `tools/asset_pipeline/test_ws16ad_spine_policy.py` guards all three current theme names, examples of future names, multiword review, unverified marks and versioned master geometry. Both standard Android and independent art workflows run these checks.
- This policy is durable **starting guidance**. If the owner later wants visual tall/short variants, implement an explicit A/B library composition with uniform scaling and accessibility QA, rather than forcing this test-only metadata to alter the current game unexpectedly.

## Next decision points

1. Correct the existing printed ® and remaining white-fringe artifacts in a **real Woodland V2 source cover/spine**, following the shared Book Factory dimensions.
2. Compare Standard (Space/Garden) and Tall (Woodland) source text layouts, with no distortion, using actual pixel art and on-device screens.
3. Only then optionally enable the two proportional height classes in a **separate library QA variant**, with careful tap-target and row-density verification. Do not redesign the accepted Home shelf.
4. Create future theme art from this one publisher family; retain V1 and WS16AD geometry proof for rollback.

**Acceptance statement:** This pass is complete when the policy, generator, tests and CI checks are committed and source-verified. **It does not claim a new book-cover image, a new Android look or owner handset approval.**
