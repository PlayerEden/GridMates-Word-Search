# WS-12D — Matching Paperback Chapter, Bookmark and Completion Accents

**November 8, 2026 · DRAFT DESIGN PROOF · Not approved or bundled in Android**

## Why this exists

The art family must be calm, approachable, readable and repeatable across dozens of puzzle books, not a series of unrelated AI-generated decorations. This extends WS-12C Quiet Illustrated Paperbacks v1 using **the exact same source-controlled motifs, fixed palette, simple geometry, and native-text rules**.

The original Woodland / Space / Garden cover and emblem source has not been edited. The original approved Woodland portrait images, cosmetic IDs, puzzle content IDs, Solo/Daily progress, Android signer, and Sudoku are untouched.

## Companion artwork

Each of three themes produces three SVG design proofs:

| Type | Artboard | Future purpose | Native app text |
| --- | --- | --- | --- |
| Chapter marker | 240 x 240 square | Small image beside a chapter list entry | Chapter number/title and state stay Compose text |
| Bookmark ribbon | 112 x 360 | Library/Continue accent | Saved place and Continue are accessible real controls |
| Completion seal | 300 x 300 round | Subtle book completion detail | Completion/progress/date remain accessible real text |

The icons are decorative, not gameplay rewards or premium items. The separate Collection/Profile customization menus keep their existing portraits/fonts and earned cosmetics.

The renderer reuses the original WS-12C motif function and the existing theme tokens; new themes inherit exactly the same size and geometry without new unpredictable cover generations.

## Reproduction

In the WS-12D branch, run these offline from repository root:

    python3 -m unittest discover -s tools/asset_pipeline -p 'test_*.py' -v
    python3 tools/asset_pipeline/validate.py --repo-root .
    python3 tools/asset_pipeline/render_paperback_accents.py --tokens design/asset-pipeline/paperback-v1-tokens.json --output-dir /tmp/gridmates-ws12d

The output contains nine SVG files and a render_sha256.json receipt with exact output hashes. The WS-12D GitHub Actions workflow should publish a reviewable, time-limited ZIP artifact.

## Actual app acceptance gates

1. At 32dp, 48dp and about 140dp, confirm recognizability; avoid muddy microscopic detail.
2. At 320dp phone width and 200 percent Android font scaling, titles, chapter labels, progress, and Continue remain real readable UI text; hiding a decorative SVG must lose no information.
3. Book completion/availability must never be encoded by color alone. Pair any seal or bookmark with meaningful accessible status text.
4. Preserve Home -> Library -> book -> chapter -> board -> result -> Continue, while Free Play and Daily remain distinct. Do not conflate the playable Bookshelf with the existing cosmetic Collection.
5. Do not publish book/chapter IDs, fake puzzles, or unreviewed content. WS-10 approved lexicon, WS-11 immutable pages and local save/upgrade/recovery are prerequisites.
6. Review author/source, license, exact source and derived hashes, any actual Adobe editing, ownership and narrow-phone appearance before recording an approved production asset.
7. Verify real phone screenshots and accessibility, then test Android packaging and performance. Green source tests are not physical QA.
8. Adding a new Ocean theme or any other theme must keep existing Woodland/Space/Garden output bytes unchanged; breaking changes require a new art-system version.

## Milestone status

- [x] Nine text-free design-prototype SVG assets added to a deterministic generator.
- [x] Companion source and independent test suite committed for CI verification.
- [ ] Verify the complete WS-12D CI test result and emitted artifact on exact commit.
- [ ] Human review of actual artwork and device-size readability.
- [ ] Adobe fine-tuning if appropriate; Adobe initialization returned an internal error again here.
- [ ] Owner art approval, license/provenance check, Android integration and physical QA.
- [ ] WS-11 Bookshelf implementation with real approved word content and local progress.

**No new APK, no approved artwork, no published books, no Android changes.**
