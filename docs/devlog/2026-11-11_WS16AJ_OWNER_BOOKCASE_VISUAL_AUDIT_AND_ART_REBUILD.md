# WS-16AJ — Owner Android screenshot visual audit and true bookcase-art rebuild

**Status:** REAL PHONE FEEDBACK VERIFIED; DESIGN/DEFECT AUDIT ONLY, NOT A NEW APK, NOT FINISHED ART.  
**Date:** November 11, 2026 · [draft PR #131](https://github.com/PlayerEden/GridMates-Word-Search/pull/131)

## Owner's actual phone evidence

The owner installed the dual-green WS16AI QA APK and provided a portrait screenshot of **Your Library**. Visible: two tall shelves, three real illustrated Woodland/Space/Garden spines, nine unlabeled decorative blanks, strong carved wood frame and ornate shelf lips, native "Your Library" header and `3 books in your library` footer. The near-touch density and large visual book size are meaningful improvements from WS16AG. **The owner explicitly said, "We have a LOT to work on."** Do not interpret this as approval of cabinet art.

## Specific verified failures and why

1. **Crown is a vertically flipped Home shelf.** In `tools/asset_pipeline/build_ws16ai_bookcase.py`, `crown = ImageOps.flip(shelf).resize((720, 138), ...)`. On phone this looks like an inverted shelf floating on top of the case; it is NOT an independently authored crown.
2. **Pillars are stretched shelf scraps.** Pipeline crops `shelf.crop((132,10,187,41)).rotate(90)` and scales to a tall thin texture, with end-bracket crops used as caps. This creates brightly lit manufactured columns rather than matching architectural framing. New artist-sourced left/right pillar masters are needed.
3. **Too many giant matching shelf lips.** Two repeated `Ws16EStorybookShelf()` pieces plus derived crown and base form an overly ornate, repetitive wood stack. The Home shelf may remain approved; Full Library needs an independent integrated carcass/crown/shelf/base artwork family with subtler inner ledges, realistic recesses and shared light direction.
4. **Blank books are visually flat and mechanically repeated.** Three simple 128×640 gradient placeholder spines are cycled across nine slots. They all share essentially identical heights and ornament placement; at phone size they resemble product placeholders, not an expanding collection of actual books. Need new matching publisher-book material, subtle individual leather/paper/grain and controlled variable heights, **not extra fake book IDs**.
5. **Lighting/material cohesion:** amber highlights and bright orange rails repeat too intensely. Need coherent restrained highlights, shadow inside the recesses, natural contact at spine bases and corners, real depth and alignment. Do not fix with a huge rectangular procedural gradient or diffuse global oval.
6. **Screen hierarchy:** separate large plain header and a count floating below the case. Tighten native heading around the bookcase and integrate the count/page status into a slim native footer, while preserving safe areas and accessibility.

## What already works — preserve

- Current real phone shows the new full-height cabinet fills most of the screen, with visually larger near-touching spines, native distinct catalog count `3`, and a full second row of decorative fillers.
- `BookcaseBookshelfScreen.kt` already has two-tier `HorizontalPager` pagination based on **real catalog books**, `48dp` or `54dp` independent per-book touch slots, a nonplayable branch for filler slots, and unchanged spine → full-cover/details → `View pages` control flow. Keep these stable.
- All production themes still have **separate full cover and spine art**; Home shelf and saves unchanged. The working WS16AI and WS16AG APKs stay available for rollback.
- Release artwork isolation and dual CI are green on WS16AI source `daa52f4c`; that establishes tech integrity, NOT visual approval.

## WS-16AJ phase 1 acceptance — real art, not another kitbash

Build a **new versioned illustrated architectural master set** using the previously established production concept / asset sheet as stylistic target, with:
- Individual transparent `crown`, left/right `pilaster`, `shelf ledge` and `base` images, and seamless `recessed backboard` tile; source hashes and editable original master retained in repository; no upside-down or rotated Home shelf.
- One shared perspective, carved walnut family, upper crown naturally rising not inverted, top row physically below the crest, correct shelf book-contact plane, no duplicated/stacked ornamental corbels at bottom.
- Artwork is **book-free and text-free**; all book sprites, labels, progress, tap areas, page controls remain separately live native Compose elements.
- Produce a screenshot-sized (portrait) art assembly proof with **real book spines layered separately**, compare side by side to owner's screenshot at 320dp, 360dp, 412dp and a tall phone BEFORE calling an APK ready.
- Source art must be locally bundled, reliable offline and reproduced by fixed-source scripts; SHA256 provenance, transparent bounds checks, debug-only until approved.
- Placeholder spines need a second independent art pass: varied but controlled bindings, real texture/embossing and plausible height silhouettes. Keep the three editable blank-master families as seed, but do not just repeat flat bars.
- Do **not** add Home Coming Soon cover teaser in this pass; verify this repaired cabinet with owner first.

## Later acceptance / safety

- After the art fixes, test 0,3,6,7,12,13,24+ real-book fixtures to validate row and page capacity, no unreachable real book or placeholder click; 1×/2× text scale, Android Back, `View pages`, rotation, 320dp/360dp/412dp viewports and TalkBack focus.
- Preserve current debug version as a known rollback. Build standard Android QA and independent art/release-isolation GitHub workflows; compare exact source and packaged image hashes, verify only desired debug artwork changes.
- Final release promotion only after owner phone screenshots and tap testing, not just CI green.

**Next concrete action:** source-control **genuinely new** crown/pillars/shelf/base originals (not a flipped crop), integrate into a same-sha modular extraction pipeline, assemble phone-size comparison, then update Compose placement only as required by new art. Treat the current cabinet as engineering proof, not visual direction approval.

## Work performed in this audit

Read live draft PR #131 source, investigated the WS16AI art generator and native case composables, and visually compared the real phone screenshot with the previously generated real architectural asset sheet. An initial local art-extraction feasibility study confirms separate genuine crown/pilaster crops exist in the reference sheet, but **these candidate crops have NOT been checked into GitHub or integrated into a build**. Avoid falsely claiming new art or a new QA APK.
