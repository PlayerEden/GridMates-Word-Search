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

## First CI failure and corrected source

Initial integrated code SHA `317071b3744e2f162a2bde89ddacdb9ae65b78ad` was **NOT green**: [Android #42744463642](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42744463642) and [art/release #42744463637](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42744463637) both stopped at the new SVG rasterizer. The exact error was `cairosvg.helpers.PointError`, traced to an extra minus sign (`--43.2`) in a new gold-medallion path. Renderer and app logic did not progress to build on these failed runs.

Fixed the malformed SVG coordinate **and re-hashed the pinned source** at [`d9c228f379ab99728696f20fcccd3295b14d460b`](https://github.com/PlayerEden/GridMates-Word-Search/commit/d9c228f379ab99728696f20fcccd3295b14d460b). Corrected medallion SHA-256: `103f9024af8e245ddc29a896631e6817d404ee25a8e339b394e52715b25159e6`. Corrected-source [Android #42744842244](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42744842244) and [art #42744842181](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42744842181) passed the new rasterization step and were running Gradle when this documentation was updated. **Do not call them green yet.**

The 26-candidate standalone PNG ZIP plus original generated image, extraction script, per-asset hashes and alpha QA was saved in the user's private ChatGPT Library at `/Game Design/GridMates Word Search/WS16G_Batch1_candidate_sprites.zip`. Actual archive SHA-256 `a4a3f9bf097dc61209cac52a1225655e629a022284354c4f478fc40515619aa9`. These image-generated cutout candidates are not the vector-sourced sprites imported by this Android build; alpha edges still await refinement, and no release approval is implied.

## Final corrected-source VERIFIED QA receipt

**Exact corrected raster-source SHA:** [`d9c228f379ab99728696f20fcccd3295b14d460b`](https://github.com/PlayerEden/GridMates-Word-Search/commit/d9c228f379ab99728696f20fcccd3295b14d460b). Later documentation-only commits with `[skip ci]` do not represent new APK code.

- Standard [Android QA #42744842244](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42744842244) — **SUCCESS**: new 8-sprite renderer/source SHA, Python art test, core/app tests, lint, Android instrumentation test *source compilation* and debug APK.
- [Artwork/debug-release-isolation #42744842181](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42744842181) — **SUCCESS**: original book art checks, eight debug-only WS16G shelf PNGs verified by exact hash, `assets/ws16g/` absent from release APK. Includes separate debug/release build.
- **Preferred owner ZIP:** [Android QA artifact #13730138987](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42744842244/artifacts/13730138987). Contains `app-debug.apk`, exact **APK SHA256 `eca5964ff7b43e3f54e156d49e0f48735d61397dc039b789ffc78990959463b8`**. Downloaded ZIP SHA256 `ccdff752f8918a686883b2e0708696acce7a0266c6ef8bc89fe8594ebb69c242`.
- Alternate art workflow ZIP [#13731131927](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42744842181/artifacts/13731131927) has a debug APK with SHA256 `323224bcf5ef3336d80c5fc18b8d304ef14e680a03a734e70a602ee31fc93878`. Both APK archives contain **exactly the same 236 internal entries and identical extracted bytes**; outer APK ZIP/packaging metadata differs between builds. Prefer the standard QA artifact for installation.
- Independently downloaded the standard QA artifact and inspected its APK: eight generated shelf PNGs + manifest present, all eight source-matched checksums validated. The artifact title remains a historical Woodland label, but the game includes WS16G shelf integration.
- QA package remains `com.playereden.gridmates.wordsearch.qa`, versionCode 57 family, subject to signing compatibility on owner phone. Back up progress before uninstalling or resolving signature issues. **Owner has not yet approved or tested this specific build on a physical device.**
- **Visual limitation:** The newly rendered standalone SVG masters are still more *vector/simplified* than the richer painterly reference. They establish the layered, source-hashed art pipeline and bookcase integration; owner must compare phone results before calling this final illustration quality. Separately cut-out image-generated painterly PNG candidate pack is stored in the owner's ChatGPT Library but needs alpha/text-edge cleanup and has NOT replaced these debug SVG renders.

**Next:** owner takes screenshots of Home/Your Library and tests book cover focus + open/back, Library row shading, Together, Solo, Daily and save/resume. Then produce/clean truly painterly button-sprite and wood masters, versioned and isolated, retaining current working UI. Keep PR #131 draft.
