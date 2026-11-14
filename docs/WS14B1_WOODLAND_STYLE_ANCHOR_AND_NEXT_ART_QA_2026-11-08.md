# WS-14B1 — Woodland Illustration Style Anchor and Coordinated Asset Production

**Date:** November 8–9, 2026 (owner conversation).  
**State:** Owner likes all three *visual directions* — Woodland, Space, and Garden. **Concept/style preference only — not reviewed production sprite, not Android QA, not release art approval.**  
**Build/fallback:** Based on WS-14A1 source PR #130 (head b18ecc1); v56 remains verified fallback; v57 standard Android QA failed due to external HTTP 504 dependency downloads, while isolated art/debug/release build passed. Do not merge or distribute v57 as fully cleared.

## Selected direction

The owner responded **“I like it”** after seeing the initial warm, full-color Woodland owl illustration rendered through OpenArt. Preserve this as the visual anchor for further coordinated concepts.

- Illustration source/generation: OpenArt **Seedream 4.5**, text-to-image, 2K, portrait 3:4.
- Creation/history ID: `4ijWEmShE4FEPh2z10DX`.
- Output resource ID: `pEkZaadtQzpp8i08OjU0`.
- Generation dimensions and metadata: **1728×2304**, provider reports **JPEG**. The available CDN link is a **watermarked thumbnail**, not a verified lossless production source.
- Concept preview link (reference only): https://cdn.openart.ai/watermarked_images/sUbcfkYEGSc64xfT8Yh2/thumbnail_df736618_1794203105745.webp
- Subject: friendly rounded brown owl on mossy log; red mushrooms, ferns, layered evergreens, warm golden illumination.
- Treatment: hand-painted gouache/watercolor, soft parchment warmth, crisp fine botanical ink, subdued forest green + sage + antique gold + terracotta, readable small thumbnail silhouette.
- Format: art only, NOT a worksheet, flattened phone screenshot, title, progress display, fake buttons or actual puzzle letters.

## What “I like it” authorizes

Use the Owl scene as a **preferred style anchor** while deriving compatible Space, Garden and Home mini-vignettes. It does **not** authorize downloading a watermarked preview into Android release resources, registering it as an approved source/export, marking rights documented, or claiming the whole game's look has been approved.

**Explicit release gate:** obtain actual usable high-resolution source files (not thumbnails), verify provenance/licensing and actual hashes, perform human review of exact candidate files/crops and compare 100–140dp bookshelf imagery and native Play / Collection screens. Keep `ws14-art-register.json` honest/empty until valid source files and exports exist. Do not bake in dynamic labels.

## Matching asset specification (separate sprites, not screenshot crops)

| Order | Asset | Illustration subject | Key consistency requirement |
| --- | --- | --- | --- |
| 1 | BOOK_WOODLAND_COVER | Approved-direction owl/log/fern vignette | Retain shape, material/lighting and botanical edging |
| 2 | BOOK_SPACE_COVER | Friendly ringed planet, starfield, distant moons, soft glowing nebula clouds | SAME picture-book ink/gouache density, framing, depth and warm paper feel; navy instead of forest green |
| 3 | BOOK_GARDEN_COVER | Floral garden arch, butterflies, small watering can, gentle sunlight | SAME framing/ink and gouache; sage, blush and peach accents |
| 4 | HOME_SOLO_OWL | Owl/leaf vignette separate from large cover | Legible at 76dp, no baked title |
| 5 | HOME_DAILY | Pocket calendar + tiny sun/star | Identical silhouette weight |
| 6 | HOME_TOGETHER | Two cozy seats/figures or friendly intertwined puzzle motifs | No fake Wi-Fi navigation indicator; real label remains native |
| 7 | HOME_BOOKS | Three stacked illustrated books | Existing physical bookcase remains the functional destination |
| 8 | LIBRARY_CHROME | Independent stretchable shelf wood and leaf flourishes | Compatible with existing standing books, 320dp and 200% fonts |

## Next implementation checkpoints

1. **Completed as concepts:** generate Woodland, Space and Garden matching 3:4 imagery and obtain positive owner feedback on each. Next, compare their exact source images at 100–140dp thumbnail size and check lettering/artifacts/style consistency before treating the family as production-locked.
2. Acquire full-resolution source originals and produce isolated Android-ready exports; never import watermarked thumbnails. Record prompt/provenance/format/alpha/crop, SHA-256 and rights status.
3. Register source-backed candidates as `GENERATED_UNREVIEWED`, only advance when valid evidence exists. Keep debug/release isolation validators green.
4. Stage a new **stacked draft PR**, separate from main, with exact assets, native Compose integration and 320dp/360dp/large-font visual tests; then run Gradle CI on exact head when dependency service works.
5. Require owner on-phone acceptance before replacing existing v56/approved art or marking asset release-approved.

## QA guardrails

- Preserve Solo, Daily, Nearby, the actual functional standing-books bookshelf, book/page IDs, journals, saved progress, and 12 original Woodland portrait IDs.
- No image generation on the player phone. No accidental art in release.
- Prefer native interactive UI; art never contains dynamic game state. New asset previews are optional and behind QA gating.
- Do not retry flaky Gradle downloads repeatedly and burn Actions minutes. Record environmental failure separately from source failures.

## Three-book concept preference checkpoint (November 8–9, 2026)

The owner gave positive feedback on **all three independently generated illustration concepts**:
- **Woodland** — “I like it” (owl scene). OpenArt generation `4ijWEmShE4FEPh2z10DX`; resource `pEkZaadtQzpp8i08OjU0`.
- **Space** — “That's good” (ringed-planet sister artwork). OpenArt generation `Nq1AHKvNoH8URouwDfxS`; reference resource `AhHXNaLQ28QL0RZvrSMb`.
- **Garden** — “That is good too” (flowering arch/butterfly/watering can). OpenArt generation `lXsjAcPhh6rglwCa3UrY`; resource `pmMFJSH3PK9wYn6BabVA`; **1728×2304 JPEG** according to provider metadata. Garden preview is a watermarked thumbnail, *not* a source file: https://cdn.openart.ai/watermarked_images/sUbcfkYEGSc64xfT8Yh2/thumbnail_b2c727a2_1794203409405.webp

**Review interpretation:** Approve these as **coordinated creative directions** suitable for continuing design work; no evidence of approval for final crops, exact Android build, source rights, licensing, or publishing. Do not silently convert positive conversation feedback into `OWNER_APPROVED` or `RELEASE_APPROVED` manifest states. The registry currently has zero verifiable production sources, as intended.

**New practical priority:** source-file acquisition and native asset handoff, **not another round of flat screenshot studies**. Obtain original master files with permitted commercial use (never watermarked previews), verify files/hash/dimensions; generate 3:4/transparent exports and test on actual standing book spines/covers within the existing functioning Compose bookshelf. Keep native, dynamic title and page progress outside bitmap artwork. Follow with four matching independently usable Home vignettes and reusable shelf/collection trim. Keep a draft PR, preserve v56 fallback and do not call an APK QA-cleared until all CI and on-device gates have passed.
