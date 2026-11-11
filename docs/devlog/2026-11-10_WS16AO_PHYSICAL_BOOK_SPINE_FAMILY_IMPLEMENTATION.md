# WS-16AO — Physical Spine Family V4: all six books / two height profiles

**Date:** November 10, 2026 · **State:** Source integrated in draft PR #131; exact-commit dual GitHub CI and phone owner approval PENDING at this initial implementation commit.

## Why
Owner's actual WS16AN phone screenshots show the cabinet is now cohesive and the 3 real theme spines are correctly placed, but the **short lower-shelf fillers are too thin and all books remain too separated** despite ≥48dp tap areas. Original \`ws16ai\` master geometry (104×542) is narrower than the new physical binding target. Stretching the entire bitmap would distort both text and motif.

## Physical book art implemented
- New deterministic \`tools/asset_pipeline/build_ws16ao_spine_family.py\`: builds **12 QA-only PNGs**, two independent profiles (166×542 standard and 166×662 tall) for all six book names: Woodland, Space, Garden, linen, night, burgundy.
- **Real book interior lettering, emblems and painterly grain are copied pixel-for-pixel** from current source-pinned \`ws16ai/spines\` and never scaled or re-typeset. A physically thicker shoulder is rendered at the two sides from genuinely source-sampled pigments plus controlled leather tooling/rib bands; no false registered mark is introduced.
- **Tall book profile** adds a controlled 120px strip of bare leather at original y216, leaving *every single original source row* intact. The upper and lower books therefore can be similar **physical widths** without shrinking lower-row touch targets or squashing text. This source-derived added material is a first QA art candidate, not a claim of fully hand-painted brand-new book illustration.
- **Blank three** reuse the same physical binding contour, material texture and recessed gold tooling, remain **strictly anonymous** and have no playable ID, chapter/progress, status or click callback. Existing Home front covers remain separate and unchanged.
- New outputs live at \`app/src/debug/assets/ws16ao/spines/{standard,tall}/{woodland,space,garden,linen,night,burgundy}.png\`, with \`ws16ao/manifest.json\` recording true source and output SHA256, 12 output dimensions and silhouette bounds, unapproved/QA-only state.

## Runtime wiring / safe fallbacks
- \`BookcaseWoodArtwork.kt\` loads \`ws16ao/spines/$profile/$name.png\` only when the WS16AM immersive caller explicitly requests a profile; **previous bookcase fallback requests no profile** and continues using original WS16AI assets.
- \`ContentScale.Fit\` is used for new tall/standard images, preventing original lettering distortion. If a candidate asset is missing, the original spine art is loaded.
- \`WS16AMInteractiveBookcaseScene.kt\` now derives rendered visual width from **actual raster aspect**: tall 166/662 for the upper bay, standard 166/542 for the lower bay. It calculates the image's height from its chosen width, capped by bay height. The independently tappable **48dp (54dp large font) slots remain unchanged**; blanks still cannot be selected. No puzzle/catalog/progress changes.
- The illustrated woodwork and Carved Home shelf are entirely unchanged. Previous APK WS16AN remains fallback.

## Quality gates
- \`test_ws16ao_spine_family.py\`: full 12-image manifest & source-hash equality, alpha bounds, direct core-pixel fidelity on real book lettering, exact standard↔tall roundtrip by deleting insert strip, no transparent white matte, and no placeholder callback/progress.
- \`verify_ws16ao_apk.py\`: assert **exactly 12 generated PNG bytes and the manifest** are packaged in Android debug APK with matching SHA256, and no WS16AO images present in the release APK.
- Both original Android and independent debug/release-art GitHub workflows regenerate this family **after the pinned WS16AI source book assets**, run tests, and verify actual APK bytes. Both must pass on **the same exact source SHA** before providing a test APK.
- Owner phone acceptance pending: inspect both shelves with overlay off; verify near-touch spacing, rounded edge quality, colored aging/texture of blanks, natural upper/lower title proportions, and all real spine→cover→View pages/Back interactions. Inspect 320/360/412dp and large font. Rework art if new book shoulders look artificially mechanical.

**IMPORTANT:** WS16AO is a QA candidate using authored source-derived leather bindings, not an excuse to silently call all new spines "approved." If the material doesn't match the richly illustrated covers, keep WS16AN as rollback and redesign the actual physical spine masters.
