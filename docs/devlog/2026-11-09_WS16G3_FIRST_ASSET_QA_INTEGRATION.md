# WS-16G3/G4 — First shelf art masters and actual QA Android integration

**Game:** GridMates: Word Search · November 9, 2026 · [draft PR #131](https://github.com/PlayerEden/GridMates-Word-Search/pull/131). **Status at initial log:** implementation committed; exact-source Android and artwork/release-isolation workflows started, result not yet established.

## Who / what / why
- Owner asked to progress from 20-role production brief to a complete illustrated asset family. Reference is the user's watercolor/gouache warm parchment with carved walnut shelf and decorated mode icons. Preserve responsive real Android UI.
- Assistant generated an original 1536x1024 art **contact sheet** (SHA-256 `e0fc50537512cfc4561dfa49652003999e91f589730f1e1c1392b8e14948f74a`) and locally separated **26 alpha-candidate standalone PNGs** (shelf, panels, icons, decor, seamless paper texture) with original source, a manifest, hashes and extraction script. **Candidate ZIP lives in conversation / sandbox only; it is not stored in GitHub or installed in the APK.** Its alpha edges and some contact-sheet residuals still need cleanup; the image is not a commercial/release approval.
- Independently authored and GitHub-committed eight **SVG source masters** are not pixel-identical to the contact sheet. They cover shelf top, front, carved apron, left/right brackets, independent gold ornament, book contact shadow and bookcase backboard. Actual per-source SHA256 saved in [Batch 1 production register](../../design/visual-system/modular/ws16g-batch1-production-register.json). New source commit: [`7c77f28f`](https://github.com/PlayerEden/GridMates-Word-Search/commit/7c77f28f8eb28b1e704b6990e99f66168e5c5ff3).

## What source code actually changed
- [Implementation commit `317071b3`](https://github.com/PlayerEden/GridMates-Word-Search/commit/317071b3744e2f162a2bde89ddacdb9ae65b78ad).
- `tools/asset_pipeline/render_ws16g_shelf.py`: CairoSVG 2.8.2 renders eight source-hash-pinned, text-free SVG masters into QA-only `app/src/debug/assets/ws16g/shelf/*.png` plus JSON checksums.
- `app/src/main/java/com/playereden/gridmates/wordsearch/Ws16GShelfArtwork.kt`: constructs a **layered 42dp illustrated wooden shelf** using actual PNGs when present; original Canvas shelf remains fallback when debug assets missing. Backboard image is drawn behind **real** Library book rows.
- `Ws16EStorybookShelf.kt`: original Canvas preserved, renamed to fallback; pager's 218dp interactive covers, click/slide routes and 252dp region untouched.
- `BookcaseBookshelfScreen.kt`: optional backing sprite behind existing real clickable library books/shelves.
- Both Android QA and art/release workflow definitions run the new source renderer before Gradle and validate that the resulting debug APK has exact expected hashes. The artwork workflow checks **zero** WS16G sprite leakage into release.
- Pure Python source render test and APK-level byte/hash/isolation verifier added. Instrumented Android tests, if compiled, are not evidence of physical device execution.
- **Intentionally unchanged:** book IDs/cover source artwork, Solo/Daily puzzles, Wi-Fi Together, persistence, versionCode, release art, navigational labels, profile icons, package signing, planned Quick Play generator.

## Exact-source build receipt (initial)
- Android workflow [#42744463642](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42744463642): in progress at log creation.
- Art/release isolation [#42744463637](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42744463637): in progress at log creation.
- **No newly verified APK URL/hash** at this initial checkpoint. Do not claim new art approved just because the SVG source files or CI workflows exist.
- Last verified owner-viewed fallback: WS-16F [QA ZIP #13727073915](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42737788114/artifacts/13727073915), APK SHA-256 `88075143e0a82a1fb59e96c3519de96043bbf734fa4f8b75b1b213b31f76dec8`. Previous WS-16G1 button alignment dual-green source `2ff2918e`.

## Next exact action
Check both workflows on the same implementation SHA `317071b3`. If compiler/Python/build error, fix and rerun with exact SHA, then read actual Android artifact ZIP contents and APK hash. Ask owner for Home and Your Library screenshots to assess real shelf/contact shadow/card balance. Don't promote owner-unapproved art into release; don't reset saved progress.
