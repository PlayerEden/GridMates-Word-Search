# WS-14A — Visual Style Bible, Reusable Sprite Contract and Design Tokens

**State: design proposal / unapproved.** This is written against the owner's November 8, 2026 three-screen Play / Puzzle Books / Collection reference image. The concept image is *not* a sprite atlas. Every production sprite must be created, isolated, reviewed and exported separately.

## Design intent: Cozy Paperback Library

**Keywords:** warm, inviting, collectible, storybook, gently nostalgic, soft but crisp, purposeful detail, friendly, tactile.

**Avoid:** thin monochrome code sketches as finished art; generic clip-art icons; mismatched AI illustration styles; random excessive particles/faux 3D; hard-to-read ornamented text; a giant picture used as a whole phone UI; repetitive full-width action buttons; interfaces that require users to know what a "prototype study" is.

### Common composition rules

- Richness belongs mainly in **illustration focal points** (owl, planet, flowers, book jackets), not in every 2px border.
- A **single** family of stroke curves, object scale, ink contours, shadow direction, grain and paper tint for all books/icons.
- Gentle botanical foliage around page headers and occasional lower corners. Never put decorative leaves under small body text or on top of touchable puzzle letters.
- Focal book covers: consistent upright 3:4-ish proportions, uniform book edge/spine thickness and reading angle; all three should stand on the same shelf as believable related volumes.
- Shelf books need recognizable iconography at 100–140dp, while retaining additional details at full size.
- Every clickable illustration has a **native clickable container**, accessible name and visible selected/focused feedback.
- Dynamic labels (title, volume, completion counts, chapter number, user name, button text, date, puzzle numbers) MUST be Android text layers, never generated pixels.

## Proposed working palette, NOT locked final color tokens

Sample values below are illustrative and require visual review against on-device screenshots:
- Background/primary paper: #F9F3E7
- Card/panel ivory: #FFF9EE
- Main forest ink: #214E3C
- Darker ink on light backgrounds: #273F35
- Muted text: #637268
- Antique gold / accent: #AB8540
- Light gold active tab: #F1DEAD
- Deep button green: #1D563E
- Wood primary: #B88C58; wood shadow: #715338
- Woodland sage: #94AD78; Space navy: #283F6B; Garden peach/pink: #D88F88

Use brand-neutral component tokens rather than hardcoded per-screen paint. Check actual contrast ratios of body text/controls and adjust. Do not rely on a color-coded state without a text label.

## Fonts and icon conventions

- Pair a moderately high-contrast **bookish serif** for headings with a readable **humanist sans** for all body and buttons. Prefer fonts already reviewed/bundled in the existing project; new font adoption requires licensing metadata and APK budget review.
- Reserve detailed illustrations for large icon-like mode cards. Bottom tabs and in-board controls use a **consistent 24dp/32dp vector icon family** with uniform stroke weight, padding and active state (not blurry cropped paintings).
- One consistent active tab color/frame, plus selected portrait and earned badge states. Every noncolor status must also be indicated by outline/wordmark/check.

## Sprite families and batch order

| Family ID | Actual assets, not a flat image sheet | First usage | Guidance |
| --- | --- | --- | --- |
| BOOK_WOODLAND | Cover illustration/background, spine ornament, emblem, chapter marker, bookmark ribbon, completion seal | Shelf, opened book, chapter page | Owl on log + pines/mushrooms, warm green |
| BOOK_SPACE | Same six-role family | Shelf/chapters | Ringed planet, deep blue sky, stars and gentle horizon |
| BOOK_GARDEN | Same six-role family | Shelf/chapters | Flowers/butterfly/watering can, warm green/peach |
| LIBRARY_CHROME | Shelf plank simple, plank supported, carved plank; brackets, shelf shadow, small leaf accents | Shelf browser | Repeatable horizontal art / scalable length; no giant bitmap of whole screen |
| HOME_MODES | Solo owl vignette, Daily calendar/star, Together people/Wi-Fi, Puzzle Books little book-stack | Home list cards | All four identical icon treatment, padding, scale and softness |
| PAGE_ACCENTS | Heading foliage left/right, corner sprigs, restrained ground/tree silhouettes, divider trim | Play, Books, Collection | Separate transparent assets, used sparingly |
| COMPONENT_CHROME | Selectable panel frame, profile frame, portrait tile, achievement tile, progress milestone | Shared native components | Prefer scalable Compose/9-patch for variable size; no label text |
| COLLECTION | Shared base portrait frame and optional compatible decorative tile backgrounds | Collection/profile | Existing 12 animal portraits stay byte-identical until separately approved |
| NAV_AND_UTILITY | Four bottom nav icons, Back/Close, hint, settings, check, star, book, refresh | All screens | SVG/vector assets; not photorealistic sprites |
| RESULTS | Completed-book seal, results stars, celebration trim and earned badge treatments | Real results | Separate earned/locked states; no fictitious rewards |

**First generation batch**: one Woodland cover/hero concept family + matching **4 Home mode illustration concepts** and **2 reusable shelf variations**. Then evaluate Play/Books/Collection *together* before producing Space, Garden and detailed reward catalog.

## Professional generation and cleanup workflow

1. **Anchor prompt**: cozy hand-painted storybook illustration for a family-friendly paperback puzzle collection; soft gouache/watercolor color blocks with clean ink edges, warm natural paper texture, constrained forest-green/gold palette, crisp silhouettes, balanced negative space, clear depth, subtle texture, gentle right-below shadow direction, no human likeness, no trademark, no UI screenshot, **no words/letters/numbers**.
2. Specify subject separately: e.g. Woodland owl on mossy stump/log with layered evergreens and two small mushrooms. Keep a central visual read at shelf thumbnail size. Present 2–4 options. Choose anchor *only after real visual review*.
3. Save the **individual source image** and approved composition notes. For Space and Garden re-use this anchor/style rules, keeping book geometry/lighting fixed.
4. For each source image, create supporting transparent art layers or a cleanly masked motif. Do **not** simply crop a labeled moodboard with fake transparent whitespace and call it a reusable sprite.
5. Editors/tools can refine outlines, remove generation glitches, extend backgrounds and make clean alpha channels. An artist/owner reviews every candidate in an actual UI context; reject nonsensical tiny details, warped book spines, mismatched light, UI text accidents.
6. Record source model/provider/prompts if available, generation date, human edits, rights/licensing basis if known, original and exported SHA-256. If provenance/permission unknown, status stays **UNREVIEWED**, not approved.
7. Convert final selected illustrated assets to appropriate alpha PNG/WebP with sizes/densities to match Compose DP. Example draft targets (adjust after phone QA): book jacket master ≥1536×2048, phone export approx 384×512 or 512×683; 48–128px functional icon art; shelf plank seamless/stretchable independent from surrounding UI.
8. Keep generated art source outside release res/ until signed off, and maintain QA-only art as separately versioned assets. Never download/generate assets on the player's phone.

## Asset receipt contract (one manifest entry per accepted/exported artifact)

- stable_asset_id: e.g. BOOK_WOODLAND_COVER_V1
- theme / family / usage: e.g. woodland / book / front-cover-art
- state: PLANNED | GENERATED_UNREVIEWED | QA_INTEGRATED | OWNER_APPROVED | RELEASE_APPROVED
- source_master_file and exact SHA-256 (no placeholders presented as actual hash)
- prompt/reference/model/provider/seed when known; source license/usage basis and human review comments
- transparent alpha: true/false; bounding box and content-safe region
- export format/actual dimensions/density and exact SHA-256
- build source set: debug-only or release-approved; localized text NEVER baked in
- linked screen/variant test screenshots and approval reviewer/date
- rollback file/ID and replacement mapping for an existing asset

No manifest should claim a filename or hash before a real file exists. A moodboard/reference is tracked as **concept_only**, not export_ready.

## Component primitives: behavior and art separation

- IllustratedHeader: compact flexible forest/leaf trim + **native** heading/subtitle.
- IllustratedModeCard: stable illustration slot + native eyebrow/title/summary and one primary action; preserve Solo dual actions when required.
- BookcaseShelf: actual catalog books stand on a repeatable shelf strip, with native progress label and book selection. Art asset does not contain book title/progress.
- BookHero/Contents: themed cover and spine, real chapter/page count, Continue/Bookmark, accessible native text.
- PaperPanel/Button/Tab/Chip/Progress: Compose implementations driven by centralized tokens and states, with scalable border/tint; no pre-rendered UI words.
- CollectionPortraitTile: 12 existing source IDs retained, selected/locked/earned overlays from actual state, decorative frame only.
- BottomNav: real 4-tab click/Back behavior, vector icons and consistent selected pill, not image hotspots.

## Asset quality checks before any app integration

- Compare on transparent, cream and dark backgrounds; check for transparent fringe or uneven antialiasing.
- No letters/counters embedded; icon/illustration labels always authored in Android.
- Inspect a full-cover render and at **real 100–140dp shelf scale**.
- Consistent border/lighting across Woodland/Space/Garden and all mode vignettes.
- 320dp + 200% font: no dynamic text clipped by fixed art. Focus/pressed/disabled states obvious.
- Source and exports check exact SHA/manifests and Android debug/release filtering.
- Generated cover images, portraits and UI components must pass separate owner visual acceptance **before** release use.

## Review milestone decision

The first A/B visual sample should compare the **new true illustration art treatment** against the earlier source-generated Study B in a genuine Compose Play + Bookcase + Collection screen set. The question is not only "prettier?" but: **does this look consistent, understandable, playful and professionally made at phone size?**

Do not scale production until the answer is yes.
