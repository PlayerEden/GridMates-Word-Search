# GridMates: Word Search — Artwork Production Brief (WS-04C onward)

**Status:** Original Woodland reuse has a reproducible, checksum-verified import workflow in draft PR #15. This brief plans **new** art and does not claim it exists in the APK.

## Art identity

**Goal:** cohesive nostalgic paperback / sticker-book warmth in the GridMates family: friendly readable silhouettes, subtle paper-grain potential, forest/cream/gold surroundings, and expressive animals. Match the *approved source Woodland portraits* in shape, line weight, facial warmth, rendering treatment, and padding. Preserve those originals unchanged; do not generate close substitutes.

Actual series palette: Cream `#F4F3EB`, Paper `#FFFDF8`, Forest `#214F3F`, Ink `#263D36`, Gold `#8A6729`. Animal palettes may vary by theme but must still work over Paper and Forest. Artwork must remain distinct at 54–64 dp on Android rather than relying on fine details, glossy shading, or embedded labels.

## First proof-of-style batch — review before production

Make a **coherent six-character contact sheet** from one shared style brief, not six individually generated designs. Proposed characters:

| Ocean proof | Bugs proof |
| --- | --- |
| `ocean_octopus`: friendly rounded octopus with readable arms | `bugs_bee`: cheerful bee with clear wings |
| `ocean_crab`: compact crab with distinctive claws | `bugs_ladybug`: circular red beetle with spots |
| `ocean_seahorse`: unmistakable curled-tail silhouette | `bugs_dragonfly`: elongated dragonfly with simple paired wings |

After the first six match existing Woodland: complete remaining candidates in consistent sets. Proposed Ocean follow-ups: sea turtle, manta ray, seal, jellyfish, starfish, clownfish, dolphin, whale, shark. Proposed Bugs follow-ups: butterfly, moth, beetle, firefly, grasshopper, ant, praying mantis, caterpillar, spider. Taxonomy labels may use the accessible player-friendly word 'Bugs'; these candidate rosters are **proposed**, not approved final inventory.

## Asset contract

1. Keep an original editable source and a documented generation/design brief for every batch; transparent alpha backgrounds, one character per frame, centered with comparable padding and direction.
2. Maintain a shared internal 512×512 source crop and export mobile-optimized ~256×256 PNG/WebP, tested at 54 and 64 dp and on light/dark backgrounds. Image dimensions/size are targets, not permission to upscale poor generations.
3. All icons have stable semantic IDs (e.g., `ocean_crab`); never rely on collection order to persist the selected portrait.
4. No logos, copyrighted third-party designs, celebrity likenesses or text baked into the icon. Use the existing approved originals only as permitted stylistic references, not raw material to alter Woodland assets.
5. Record source type, source hash, packaged hash, file bytes, intended uses, license/rights and creator approval in the art registry before adding assets to the Android app.
6. Display explicit locked/unlocked/equipped visual states; locked icons must stay understandable and never hide characters solely for paywall pressure.
7. Add matching accessible labels and a non-image fallback; test at small screen sizes, high font scaling and offline startup.

## Production sequence

- **WS-04C-A — Reuse shipped original Woodland:** pinned Sudoku SHA, verify original SHA-256 and count, package all 12 in CI APK, display via `drawable` ID, capture independent APK inspection and on-device gallery QA.
- **WS-04C-B — Ocean/Bugs style proofs:** generate six coherent concepts, compare with Woodland contact sheet, review small-size renders, ask for art-direction feedback; not in live game until approved.
- **WS-04C-C — Collection implementation:** after proof approval, add finalized Ocean/Bugs sets under independent collections with unlock thresholds, save schema changes, an import/manifest test and proper selection preview.
- **WS-07 art polish:** theme header cards, stamp/reward tokens if economy is approved, original highlighter stroke textures and success seals; build transparent visual layers around the already-working procedural marker path.

## QA / release protections

- New app builds and local checkouts should never overwrite the approved Woodland originals from Sudoku or mutate PNG bytes during import.
- Verify source SHA and hashes, and assert all resources are packaged in the build artifact before treating the Woodland visuals as shipped.
- Preserve per-app progress; image packaging must not change Solo/Daily schema or unlock IDs.
- The GitHub debug QA signing identity is not stable yet. Export profile/Solo/Daily JSON backup **before any uninstall**; artwork milestones do not solve Android signing.
- Until visual review is performed by the owner, generated art stays concept-only, and draft PRs remain unmerged.

**Source of approved Woodland image hashes:** [Word Search pinned manifest](../design/woodland/approved-portrait-reference.json). **Roadmap:** [Development Roadmap](DEVELOPMENT_ROADMAP.md). **Original art import:** [WS-04C PR #15](https://github.com/PlayerEden/GridMates-Word-Search/pull/15).