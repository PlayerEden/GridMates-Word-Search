# WS-14A1 — Shared Native Storybook Visual Kit (QA v57)

**Started November 8, 2026.** This is the first **real implementation** after WS-14 planning, not a final aesthetic signoff and not the finished generated illustrated asset release.

## Implemented in Android source

- A new `StorybookVisualKit.kt` centralizes working **screen spacing, native touch size, paper-panel geometry, warm paper/forest/gold tokens** and defines independent **StorybookHeading, StorybookFrame, StorybookIllustration and StorybookModeCard** building blocks.
- The new `StorybookHeading` is **already used by all three flagship surfaces**: Play/Home, actual physical Puzzle Bookshelf and Collection/Profile. Existing native title/subtitle text remains accessible and reflects the same underlying state.
- The art slot accepts an individual Android drawable rather than embedding text/buttons/progress in image pixels. It renders **nothing if no asset has been supplied**; this is preferable to unrelated procedural art or fake placeholders. New generated art must have real source, hashes, review/rights and export files before use.
- Native action in `StorybookModeCard` remains a real 48dp-minimum Android button, not a clickable image hotspot. The existing Home routes and controls are not replaced yet; their layout remains compact to protect the 360dp first-screen Solo/Daily/Nearby regression.
- New `StorybookVisualKitAndroidTest` **test source** verifies native mode action, no fake art, real displayed labels, and 200% text / 320dp composability; Android CI compiles it but real device interaction remains a separate gate.
- `validate_ws14_assets.py` and its eight Python tests enforce **actual file existence/SHA**, no path traversal, no duplicate IDs, explicit human review before approval and documented rights before shipping release assets. The real [art register](../design/visual-system/ws14-art-register.json) intentionally contains **zero** entries until individual source and exports exist.
- Dedicated WS-12/WS-14 Android artwork QA workflow now validates the new registry/tests and continues to check existing 18+18 source SVG and 36 PNG **debug-only** artwork plus **zero** source art in release variant.
- QA `com.playereden.gridmates.wordsearch.qa` versionCode **57**, versionName `0.9.13-visual-kit-foundation-qa` with existing test signer. Install-over intended once both CI gates pass.

## Preserved

- Stable book/page IDs, Book atomic journals and valid saved progress, original 12 Woodland portrait IDs/thresholds, fonts, names, Solo/Daily/Nearby mechanics, four bottom tabs and tested nontechnical player first-run navigation.
- No WebView, runtime image model, fake book content, changed artwork filenames or release-prototype art.
- The source-generated A/B study remains for rollback **only**, not promoted as the final owner-requested illustrated game.

## Next milestones, not claimed as done

**WS-14A2:** review and lock the visual foundation against all three actual phone screenshots; add reusable illustrated mode cards, portrait frames and native card variations to the app safely. Prepare actual source asset contract/9-patch rules and visual QA.

**WS-14B:** generate **standalone original images** (NOT flattened screen mockups) from the style brief, starting with 2–4 Woodland cover masters. Choose one human-reviewed visual anchor, then matching Space/Garden and Home mode illustrations. Separate sprite alpha/cropping/rights review, import with hashes and QA-only fallbacks.

**WS-14C:** use those reviewed asset files to style Play/Home, bookcase and Collection **together** as a real install-over QA vertical slice. The user wants visual consistency before new gameplay features.

**WS-14D/E:** finish all remaining gameplay/menus/result states and physical Android QA. Protect saves throughout.

## QA gates / reporting

Only call the v57 build **verified** after:
1. Standard [Android QA workflow](https://github.com/PlayerEden/GridMates-Word-Search/actions/workflows/ws01-android-qa.yml) passes **core/app unit, lint, instrumentation source compilation** and signed `assembleDebug` on the exact PR head.
2. Dedicated [A/B asset workflow](https://github.com/PlayerEden/GridMates-Word-Search/actions/workflows/ws12g-qa-art-lab.yml) passes registry validator tests, exact source/PNG hash checks and debug/release isolation on that same head.
3. Owner device screenshot/touch acceptance is separately required; **compiled instrumentation source is not evidence of device execution**.

Do not claim this version matches the richly illustrated three-phone reference yet. It starts the underlying native component system necessary to make that art import robust.
