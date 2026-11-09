# WS-12K — Controlled, Illustrated Book Art Study B (QA v55)

**Nov 8, 2026 — Design exploration, not a production art approval.** Builds on signed Android QA v54, after owner's feedback that first book art is consistent but too plain, while preserving the main request to keep developing without another immediate physical-device test.

## Scope actually implemented

The existing 18 **Quiet Paperback** SVG originals are **byte-for-byte unchanged**. They remain the default Art Study A. This branch adds a coordinated **Field Notes Study B** with 18 entirely separate alternate SVGs and 18 native pre-rasterized PNGs for:

- Woodland: background ridgelines, delicate additional pines, forest ground/foliage, small woodcut detail.
- Space: softer orbital depth and constellation points, an inked ring glint, moon and star marks.
- Garden: climbing leaves, extra botanical branches, ink hatching and seed details.

**All three** themes are authored with the same design rules and color tokens. Every cover, spine, emblem, chapter tile, bookmark and seal inherits the same consistent extra layer for its theme. No random full-cover AI image generation or unrelated new styles; no fake product photos, logos, letters, runtime model usage, network assets or payment systems.

## Real source implementation

- `tools/asset_pipeline/render_field_notes_study.py`: reads the existing SHA-verified **18** WS-12F SVGs, checks original digest receipts, and injects a **controlled field-notes layer around the exact original primary motif** on all six different asset types. Writes new 18 SVGs, their separate SHA and original-hash comparison receipt to `ws12/alt/field-notes/`, and a semantic `compare-studies.html` alongside the old review HTML. All SVGs remain text-free.
- `tools/asset_pipeline/test_field_notes_study.py`: tests completeness, original bytes unchanged, SVG/XML/text-safety, deterministic regeneration, comparison screen without fake Open button, unapproved status and fail-closed behavior on modified source art.
- Existing `rasterize_android_qa.py` reused with an alternate input directory, so Android gets local **18 original PNGs + 18 illustrated PNGs**, all derived from their corresponding SVGs. Both raster manifests retain source/PNG SHA and `owner_approved=false`.
- `BookArt.kt` can source either renderer via a local `fieldNotes` argument without changing book/cosmetic IDs, save schemas or Compose controls.
- `BookLibraryFlow` holds the choice **only in memory**, not in profile or game progress. The QA Bookshelf displays **Try more illustrated art (B)** / **Show original art (A)** as native accessible actions; switching affects the cover, spine, emblem, chapter art, saved bookmark and completion seals throughout actual playable test books. The art comparison is unavailable in production.
- `QaBookArtworkIntegrationAndroidTest` adds a QA route test for switching styles, opening a real book, navigating back, and restoring the original study.

## Build and safety

- QA versionCode **55**, versionName `0.9.11-art-study-ab-qa`, same QA app ID `com.playereden.gridmates.wordsearch.qa` and compatible public QA test signing; intended as an **install-over v54** once verified.
- The original **12 Woodland Collection portraits**, their IDs/unlocks, and all saved Solo/Daily/Book progress and published book/puzzle IDs are unchanged.
- All extra prototype SVG/PNG images are **debug-only**. The custom Android workflow checks 18 original + 18 alternate source SVGs, SHA matches for all **36** corresponding PNGs, and **zero** WS-12 preview assets in the release variant.
- The production book catalog remains intentionally empty until content and rights review. These QA fixture books are not marketed as completed published chapters.
- Standard Android compilation/core tests/app unit tests/lint/instrumented test **source compile** are required. This is not physical-device test execution or owner aesthetic approval.

## When the owner chooses to review

Open Word Search TEST → **Test Puzzle Books**. Toggle between Study A and Study B and compare books at actual phone scale, not just isolated images. Open a book and compare the same artwork family in its chapter, bookmark and completion locations. Both styles use identical controls and puzzle data. Which treatment is more eye-catching without becoming busy or generic? Only after that decision should we refine/export approved graphics into production.

**Follow-up:** deliberate book motif refinement, small-screen performance/200% font, actual on-device bookmark/completion checks, an additive Collection model sheet, source/license review and final owner approval. Do not auto-promote Study B as the new default or a finished art license.
