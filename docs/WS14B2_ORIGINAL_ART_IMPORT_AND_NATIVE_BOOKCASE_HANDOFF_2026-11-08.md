# WS-14B2 — Original Artwork Import + Native QA Cover Fallback

**State:** integration infrastructure committed in draft PR #131. **No WS-14 PNG masters or production-grade exports have been obtained from the generation provider.** The three owner-liked images remain concept visuals, NOT shipped pixels. The OpenArt connection currently returns watermarked preview URLs, which cannot be used as production originals.

## Implemented in source

- `tools/asset_pipeline/import_ws14_cover.py` imports a verifiable, locally supplied original master and optimized 3:4 PNG with explicit SHA-256 receipts and a concrete provenance note. Requires master in `design/visual-system/masters/` and export in `design/visual-system/exports/`; rejects suspicious filename patterns (watermark/preview/thumbnail), corrupt PNG chunks, wrong aspect ratio, unverified hashes, duplicates, escaping paths and missing evidence.
- Successful import creates `app/src/debug/assets/ws14/books/{woodland|space|garden}.png` and a `QA_INTEGRATED` manifest entry with `PENDING_REVIEW` rights status. No release asset, rights approval or owner acceptance is inferred. **File naming and checksums cannot themselves detect a watermark in image pixels; human pixel inspection remains mandatory.**
- `BookArt.kt` now prefers a WS-14 original QA cover *when present* and automatically falls back to existing WS-12 A/B local imagery when absent. Spine/emblem/chapter/bookmark/seal continue to use old art until individually integrated. Title, book progress and selection remain native and unchanged.
- `test_ws14_cover_import.py` covers correct debug-only import, wrong digest, corrupt PNG, disallowed preview filenames, invalid proportions, duplicate asset and missing provenance.
- `Ws14CoverSelectionAndroidTest.kt` asserts 3 theme path mappings and non-cover fallback. CI compiles Android instrumentation tests, not executes on a device.
- Dedicated WS-12G artwork workflow runs importer tests and inspects actual debug/release APK ZIP contents against `ws14-art-register.json`: all registered QA cover bytes must be present only in debug and must exactly match their SHA-256; release must include **zero** WS-14 QA art.

## Next practical asset handoff

1. Download **true original file** for each owner-preferred generation from the generation service through a legitimate download/export option, not a watermarked preview. Inspect actual file visually and note its model, origin and rights/terms.
2. Save the unmodified originals in `design/visual-system/masters/{theme}.*` (PNG or JPEG accepted as *master*), and produce small readable, independently edited, 3:4 optimized **PNG** cover exports at `design/visual-system/exports/{theme}.png`. Crop with titles/progress kept outside the bitmap; no fake book pages.
3. Calculate `sha256sum design/visual-system/masters/{theme}.* design/visual-system/exports/{theme}.png`, then pass the exact values to the importer. Example using genuine source paths:

```bash
python3 tools/asset_pipeline/import_ws14_cover.py \
  --theme woodland \
  --master design/visual-system/masters/woodland-original.jpeg \
  --export design/visual-system/exports/woodland.png \
  --master-sha ACTUAL_64_HEX_SHA_FROM_SHA256SUM \
  --export-sha ACTUAL_64_HEX_SHA_FROM_SHA256SUM \
  --provenance "OpenArt creation 4ijWEmShE4FEPh2z10DX; original export; commercial rights awaiting review"
```

4. Run `python3 tools/asset_pipeline/validate_ws14_assets.py` and `python3 -m unittest discover -s tools/asset_pipeline -p 'test_ws14_cover_import.py' -v`.
5. Test the actual standing Bookshelf at 320dp/360dp and large type, compare thumbnails to the owner's reference, verify saved progress and taps, then stage owner sign-off. Add matching real **spines** next rather than treating old spine art as finished.
6. Do not release or auto-merge while Android CI or physical device QA is pending. **v56 is the known verified fallback.**

**No source images were smuggled in from thumbnail links, no puzzle content was added, and no save schema was modified.**

## WS-14B4 — Matching spine sprites and memory safety (Nov 9, 2026)

- The importer now also supports `--kind spine`. The spine must be **independently illustrated**, not an automated warped/cropped text-bearing book cover. Required QA PNG export: 64–1024px wide, 300–4096px high, narrow vertical aspect ratio 0.06–0.40. The existing cover default stays `--kind cover`.
- Spine files are written only to `app/src/debug/assets/ws14/spines/{theme}.png`, with separate IDs `BOOK_{THEME}_SPINE_V1` and purpose `spine-ornament-art`. All validations/hash receipts and pending rights constraints remain.
- `BookArt.kt` resolves approved-direction original spine assets only for QA **B**, falling back to WS-12 for any missing spine. A always uses the real old study.
- Cover/spine bitmaps are sampled to at most about **512×768px decoded**, reducing memory on lower-end Android devices without changing full master quality. Original 12 Woodland portrait bytes and save state remain untouched.
- Debug/release APK inspection includes **both** front covers and separate spine sprites. No imported artwork enters release.
- Example spine import command (pass actual SHA-256 values from the verified files, not placeholders as evidence):

```bash
python3 tools/asset_pipeline/import_ws14_cover.py \
  --kind spine --theme woodland \
  --master design/visual-system/masters/woodland-spine-original.png \
  --export design/visual-system/exports/woodland-spine.png \
  --master-sha ACTUAL_SHA256 --export-sha ACTUAL_SHA256 \
  --provenance "Original independently edited artwork; rights review pending"
```

**IMPORTANT:** The three source image masters and actual spine illustration art have NOT been acquired by this change; the registry must stay empty until real original bytes and hash receipts are available.

## WS-14C — Native owner-review artwork sheet

The QA-only Art Lab now has a **Review WS-14 assets** switch. It presents Woodland, Space and Garden side by side as **Study A original** versus **Study B illustrated**, including independent covers and narrow spines at small phone scale. The Android asset manager reports the actual count of shipped files (0 to 6), and each theme explicitly says when an original file is absent. Study B falls back to existing art, so a missing file cannot be mistaken for an imported illustration.

This native comparison is offline, doesn't display fake approval buttons, doesn't modify puzzle content/saves, and stays inaccessible from production. An instrumentation test covers navigating in/out and checks no non-existent art is falsely reported. Use it for owner review when the actual unwatermarked originals are imported; **it does not replace commercial rights and source hash checks**.
