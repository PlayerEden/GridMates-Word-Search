# WS16BI — One coordinated walnut Library, not isolated patches

> **Status (Nov 12, 2026):** v68 SOURCE COMMITTED / Android and independent art CI RUNNING / owner on-phone visual approval PENDING.

## Owner decisions and actual evidence

The owner reviewed the real WS16BH v67 screenshot after a long Android artwork iteration. Three real spines finally look like a coherent group, but the books need slightly more width/easier touch, more projected 3D depth, **a few decorated wordless future-book spines** to relieve the empty shelf, and a complete rethink of generic-looking pill-like title/count plaques, Back/menu controls and thin floating shelves. The screenshot is negative acceptance of shelves/chrome, not a rejection of a spine-first Library.

The owner specifically requested **NO WORDS on decorative blank spines**, only tasteful details matching art-generation quality. Long-term behavior remains scalable 2–3 physical shelves and real books filled in order, with illustrated covers only as selection preview / featured Home image. The full [frozen design contract](../design/WS16BI_LIBRARY_COHESION_SPEC_2026-11-12.md) records these choices.

## Implementation in draft PR #131

Working branch `feature/ws14b-style-anchor-and-art-direction-2026-11-08`, [draft PR #131](https://github.com/PlayerEden/GridMates-Word-Search/pull/131). New code at [`4e4bea3d`](https://github.com/PlayerEden/GridMates-Word-Search/commit/4e4bea3df6946b0ae172cc96c7a0fc0031ccee3a), later documentation-only `[skip ci]` commits do not change compiled code.

1. **Detailed integrated shelf:** New `build_ws16bi_library_art.py` deterministically builds a 1172×250 PNG from pinned actual WS16BA walnut cabinet backboard and previously approved-as-a-fallback WS16BB shelf source. Mounted ledge has apparent top depth, warm wood grain, subdued brass inlay, smaller central carved motif and integrated wooden side corbels rather than a giant floating gold leaf. Generated SHA-manifest and debug-only `assets/ws16bi/shelf.png`; old sprite remains as fallback. New `Ws16BIMountedCabinetShelf.kt` preserves exact cabinet contact plane and source aspect to avoid stretched furniture.
2. **Real bindings:** Updated `WS16BHDenseSpineBay.kt` to render source-pinned WS16BF clean single-piece bindings, slightly expanded visible X thickness (7% face scaling) in **48dp normal / 52dp enlarged** nonoverlapping cells. Layered soft ambient shadow behind the binding plus a tight contact shadow, not a fake rectangular leather book. Stable `onBookSelected(book.id)` for only real books; row-major paging remains. Android layout's third shelf now requires >=960dp portrait height (not just book count) so books remain legible; otherwise the two physical shelves paginate more books.
3. **Wordless blank art:** Same deterministic generator creates three 104×542 walnut/leather/gold ornamental bindings (`fern`, `dusk`, `clay`) with gilded lines and simple engraved motifs, **no words, numbers or fake theme title**. `WS16BIUnletteredBlankSpine.kt` reads debug-only assets with `clearAndSetSemantics {}`, no clickable. Existing real books are always first; at most two blank binders after an occupied row and at most four on an empty lower shelf, with no effect on count, capacity, page or saved data. On a production build without debug-only art, no invented book art is displayed.
4. **Cohesive chrome:** New `WS16BICabinetChrome.kt` provides cabinet-specific wood/brass fitted title plaque, engraved slim count label and brass-wood icon fittings. 54dp native button hit targets, native editable text/icons, preserved Back/menu/previous/next callbacks, disabled state. `WS16BIAdaptivePlaque/Control` preserves established AZ styling in compact/landscape fallback, changing only the high-res portrait cabinet.
5. **Verification:** `test_ws16bi_library_cohesion.py` and `verify_ws16bi_apk.py` pin all four new PNGs and manifest inside debug, assert release isolation, source SHA, typography-free blanks, live markup and real ID/click semantics. Existing WS16BB, WS16AT, WS16BH, Android Compose tests updated for the new shelf and 48/52dp layout. Workflows regenerate art **after WS16BA/WS16BB source imports**, then verify debug APK bytes and absence from release. QA versionCode **68**, versionName `0.9.24-mounted-shelf-and-cabinet-chrome`, same `com.playereden.gridmates.wordsearch.qa` package and same public QA signing cert.

**Important:** No user progress, puzzle generation/seed, achievements, remote/local LAN, data schema, account features or play-screen art changed. Original generated painting rights remain unapproved for production; all WS16BI assets stay debug-only. Do not merge PR #131 or switch release art flags.

## Source CI and rollback

- **Initial source:** [`4e4bea3df6946b0ae172cc96c7a0fc0031ccee3a`](https://github.com/PlayerEden/GridMates-Word-Search/commit/4e4bea3df6946b0ae172cc96c7a0fc0031ccee3a).
- **Android QA [#43293978365](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/43293978365): IN PROGRESS** when journal created.
- **Independent art/release [#43293978579](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/43293978579): IN PROGRESS** when journal created.
- **No v68 APK verified** at journal creation; do not invent a link/hash or imply physical test.
- **Prior owner screenshot / fallback:** dual-green v67 [Android #43192422437](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/43192422437), GitHub APK ZIP [#13930584152](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/43192422437/artifacts/13930584152), APK SHA `01189576df4d0d977ebd74410a53911d2b58b42f538ff2831cdb39fc3bc48dcd`. Do not uninstall to downgrade/compare and erase user saves; Android versionCode prevents simple downgrade over v68.

## Next exact action

Inspect both WS16BI workflows for failing per-pixel tests, Kotlin compile errors, stale source assertions or signing issues. Repair precise failure(s) with a new exact code commit, rerun both and publish only their matching verified artifact. Then give owner the real install-over v68 APK/ZIP and request portrait screenshots plus at least Woodland and Garden book selection and saved-progress checks. Evaluate shelves/buttons/blanks together before another redesign.
