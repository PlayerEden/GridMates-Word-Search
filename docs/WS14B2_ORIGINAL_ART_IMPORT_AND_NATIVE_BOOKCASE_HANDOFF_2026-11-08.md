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
