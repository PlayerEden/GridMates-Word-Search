# WS-16AD — Repeatable Puzzle-Book Asset Factory (QA geometry proof)

**November 10, 2026.** Source-only geometry and art-manufacturing pass, not final ®-corrected V2 book approval. This is meant to let us make *many* future volumes without generating new book dimensions for every cover. No catalog, save ID, gameplay mechanic or existing approved profile art is changed.

## Geometry lock

| Sprite | Canvas | Content safe/visual bounds | Purpose |
| --- | --- | --- | --- |
| Cover with integrated physical spine and pages | **480×640 RGBA** | x12–468, y65–574 (±2 px) | Home carousel, selected-book hero, cover-first library views |
| Spine-only | **128×640 RGBA** | x14–114, y51–589 (±2 px) | Full Library bookcase and optional closeups |

Every theme is constructed from the same front/spine silhouette, resting baseline and scale, with title/plaque safely contained. Lettering and decorations may vary by theme but **outer geometry must not**. On future Full Library screens, vary visible book heights only by a few planned proportional variants; target large invisible tap regions (≥48dp) independent of a visually narrow spine. Do not distort the book PNG itself or overlap hit targets.

## Asset bundle and source contract

Per published themed book: stable book ID, editable theme illustration master, separate versioned cover and spine art, optional combined-shelf render derived from those, thumbnail as smaller export of cover, plaque/badge and optional chapter art **only if actually used**, metadata/palette, output SHA, source SHA, provenance/license and QA/release approval fields. Spine is separate art and can be displayed wide enough to read and tap.

This checkpoint derives **geometry proofs** from the hash-pinned original WS14 1536×1024 artwork sheet. After the normal `import_ws14_candidate_sheet.py` workflow, run:

```bash
python3 tools/asset_pipeline/build_ws16ad_book_factory.py
python3 -m unittest discover -s tools/asset_pipeline -p 'test_ws16ad_book_factory.py' -v
```

Outputs go to debug-only `assets/ws16ad` with `manifest.json` recording actual input/output hashes, bounding rectangles and purpose. New `BookArt` preference picks the **three normalized QA candidate covers/spines** on debug when available; if missing, all existing ws14/ws12 sources are unchanged and used as fallback. Release APK receives no WS16AD assets.

The following three blank books are **non-playable and not yet displayed in the Full Library**: `linen`, `night`, and `burgundy`. Original editable SVG masters live in `design/visual-system/masters/ws16ad/`, and their separate cover/spine PNGs are rendered at the same canonical sizes. These blank spines are candidates only; there are *no* fake published book IDs, chapters, progress records, or tappable unfinished puzzle destinations.

## Remaining art gates (not silently solved by code)

1. **Remove the inaccurate baked-in ®** via actual corrected artwork (not a blur, noise filter or one-pixel guess) in Woodland/Space/Garden V2 illustrated masters. Current geometry proofs still use V1 title pixels. Never claim they are fully cleaned V2.
2. Inspect 1:1 transparent edges on cream and black matte and at real Android Home size, especially spine seams, bright page blocks and the book bottom; minor matte reduction alone doesn't guarantee perfect masking on every display.
3. Approve typography and consistent physical cover/binding geometry, source rights/provenance, and final 3D pose on the accepted WS16AB shelf.
4. Only then promote production V2 artworks and introduce Coming Soon covers to the Full Library with native accessible labels.

**Rollback:** the original six `assets/ws14/{books,spines}/` inputs remain intact. The debug factory writes only a new versioned namespace and app preserves fallback art. Keep PR #131 draft until owner review; do not uninstall an older QA APK or clear saves.
