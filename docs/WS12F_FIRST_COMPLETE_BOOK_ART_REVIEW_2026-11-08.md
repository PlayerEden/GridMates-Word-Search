# WS-12F — First 18-Piece Consistent Library Art Review Pack

**2026-11-08 · Production-style art proof. All artwork is UNAPPROVED until owner reviews actual source renders on a phone.**

## Real output now implemented

The existing WS-12C cover/emblem generator and WS-12D companion-art generator were already producing **15** actual deterministic theme images. WS-12F adds **three real, derived matching SVG spines**, and assembles **all 18 assets** in one review:

| Family | Cover | Spine | Emblem | Chapter | Bookmark | Seal |
| --- | --- | --- | --- | --- | --- | --- |
| Woodland | original SVG unchanged | **new SVG** | original unchanged | original unchanged | original unchanged | original unchanged |
| Space | original SVG unchanged | **new SVG** | original unchanged | original unchanged | original unchanged | original unchanged |
| Garden | original SVG unchanged | **new SVG** | original unchanged | original unchanged | original unchanged | original unchanged |

**Actual source:** \`tools/asset_pipeline/render_family_review.py\` uses the same \`motif_markup()\` and four palette colors to build each **160×920** text-free spine in a fixed paperback frame. It imports the **existing** cover/emblem and chapter/bookmark/seal functions, copies their bytes untouched, and verifies their original SHA-256 receipts exactly.

**Visuals to review:** offline \`review.html\`, **WS12F-three-book-art-contact-sheet.png** and **WS12F-phone-width-art-preview.png** plus the 18 individual text-free SVGs, all delivered together in a GitHub Actions review artifact. The actual book labels, page counts, and Open book indication in the HTML/PNG are purely native-text UI simulations; they are **not** baked into art.

## How to get the source-backed visuals

In the [WS-12F GitHub Actions workflow](https://github.com/PlayerEden/GridMates-Word-Search/actions/workflows/ws12f-family-art-review.yml), open the latest successful run and download **WS12F-THREE-THEMES-18-SPRITES-FAMILY-REVIEW-PROTOTYPE**. Extract the ZIP. Open the two PNGs immediately on a phone, or \`review.html\` in a browser for an editable-text layout proof; \`books/\`, \`spines/\`, \`emblems/\`, \`chapters/\`, \`bookmarks/\` and \`seals/\` contain each real vector asset.

To generate from the source instead:

\`\`\`bash
python3 -m unittest discover -s tools/asset_pipeline -p 'test_*.py' -v
python3 tools/asset_pipeline/render_family_review.py \
  --tokens design/asset-pipeline/paperback-v1-tokens.json \
  --output-dir /tmp/gridmates-ws12f-review
python3 -m pip install Pillow==11.3.0 CairoSVG==2.8.2
python3 tools/asset_pipeline/render_review_png.py \
  --input-dir /tmp/gridmates-ws12f-review \
  --output-dir /tmp/gridmates-ws12f-review
\`\`\`

The PNGs are derived from the exact SVG source, with text overlays added only for *review*. The game never needs CairoSVG, Pillow or network image generation at runtime.

## Owner's first visual decision

Evaluate **Woodland as a coherent set**, then compare Space and Garden side-by-side:

- Does the paperback family feel calm, collectible, and consistent with the actual warm paper Word Search UI?
- Is the simple pine/orbit/flower imagery attractive enough, or would one isolated illustrative motif need richer **controlled** finishing?
- Can all three books be recognized at their thumbnail sizes? Are the chapter, bookmark and completion decorations visibly related to their covers?
- Is the spine useful as a hint of a physical book, or too narrow for a phone layout?
- Does the system look intentional rather than an accumulation of AI pictures?

Choose **keep / modify / reject** per family, with concrete reasons. This review is not a generic blanket approval. Human selection is necessary before a new production registry entry or publishing new art.

## Explicit guardrails

The [WS-12E source queue](../design/asset-pipeline/production-queue-v1.json) now lists **18/18 actual prototype slots**, not 15 prototypes + 3 absent. The approved production registry remains **empty**. No Android code, original Woodland portrait, book data, unlock ID, save profile, package signing or existing QA APK is modified by this art branch. No Adobe project or artificial human signoff is claimed. The separate WS-11E2 Android playtest still uses its original book cards until an opt-in QA art preview is implemented and tested.

**Following milestone:** optional QA-only native Android art preview against the current Bookshelf branch, preserving installed test data and offering a clear on/off comparison. Only after reviewing that in-app result should a style be promoted for final packaging.
