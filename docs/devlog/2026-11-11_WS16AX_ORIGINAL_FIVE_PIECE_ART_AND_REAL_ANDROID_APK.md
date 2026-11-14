# WS16AX — Original five-piece cabinet art imported; real-art Android QA dual-green

**Date:** November 11, 2026 (Kentucky) · **Status:** genuine source artwork committed, real-art QA APK built and verified in CI; physical-phone review **NOT** yet performed · [Draft PR #131](https://github.com/PlayerEden/GridMates-Word-Search/pull/131).

## Who / what / where / why

- **Owner:** supplied the exact original `WS16AS_Modular_Library_Art_Kit.zip` attached to this chat, unblocking the previous WS16AW art transfer.
- **Implementation:** assistant, via GitHub connector and a temporary private file transfer route. Temporary staging copies were deleted after the source art was committed. The ZIP from the owner is preserved in ChatGPT; it was not replaced by an approximation.
- **Goal:** installable Android QA with the **actual five original pieces** integrated into the WS16AW full-bleed, independently composited, interactive Library. No fake source art, no permanent shelf/placard/text painted onto the shell.

## Where we came from

- Prior pass: [WS16AW full-bleed native UI and safe overlays](2026-11-11_WS16AW_FULL_BLEED_LIBRARY_SAFE_OVERLAYS.md) at code/test source `fefbe7a9`, then docs head `8a97f64260d08ea08611d52ed069ea845e267e81`.
- Earlier WS16AW CI was dual-green **without original cabinet art**: [Android #42978836813](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42978836813) / [art #42978836809](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42978836809).
- Previous independently signed WS16AV one-off local APK contained the original five images but not the new source-managed WS16AW full-screen layout and used a different signing key. Preserve as visual/rollback reference, not production acceptance.

## Verified input

- Original uploaded ZIP SHA256: `db91d99ed5e2f628dcd1e7244f64f1c30e891f57b891324a5021d797a258d8ee`.
- Original painted master: `1017 × 1547` PNG, SHA256 `c1e42abaaef55b05aba8493637e3bc702376fd4078bd07699c504f1a21941264`. The five original shell crops reassemble the **602 × 1120** left cabinet *pixel-for-pixel*, with all five original file SHA256s and dimensions verified against the uploaded manifest:
  - Crown `top_crown.png`: `c4365f20bf839e504cd5828fdacf2f89187a4770c72911e955de1a7b3ccebc63`
  - Left column `left_pilaster.png`: `caea09cae3e4114b99fa6fd7c8b9a3d736471b389864b9efb28b8c2f58d21dd9`
  - Backboard `recessed_backboard.png`: `6d63a53556cbc8ae7059418d8210ca79796becfa7e8642b217debca696c17c64`
  - Right column `right_pilaster.png`: `843346dca3c6f711bd5d2d55076e664bce16131a17efbffb27d996434a9e98`
  - Base `bottom_base.png`: `1192023c1f02efd042a4e75fc5d3ec56a45bdff6f573c741a0a0f0b0112578a2`.
- Original source master includes future decorative plaque candidates on the separate right-hand part of the painting, **not painted into the cabinet**. This milestone did not import/publish standalone plaques or buttons.

## Actual code, asset, and build changes

- **Original image bytes committed** to `design/visual-system/masters/ws16as/original_candidate_sheet.png` and the five independent `design/visual-system/masters/ws16as/shell/*.png` files, each checked against its original Git blob SHA. `manifest.json` pins the exact original source checksums, dimensions/crop rectangles, empty-shell flags and conversation ZIP provenance.
- `tools/asset_pipeline/assemble_ws16as_github_kit.py`: independently rechecks all six exact original images and the assembled cabinet, then rebuilds a **source-only ephemeral** `WS16AS_Modular_Library_Art_Kit.zip` in the CI checkout, preserving the actual original image bytes. This reassembled *archive* is intentionally not byte-identical to the 5.98MB user ZIP; unrelated previews/proof-only assets were excluded. Every *used original image* remains pixel- and byte-identical.
- Both `.github/workflows/ws01-android-qa.yml` and `.github/workflows/ws12g-qa-art-lab.yml` now assemble that runtime kit and invoke the existing strict `import_ws16as_modular_library.py` importer. Exactly five shell assets go only to `app/src/debug/assets/ws16as/shell` in the QA build. The existing `verify_ws16as_apk.py` asserts precise APK image hashes and no release leakage.
- `test_ws16at_modular_library.py` checks the genuine GitHub original master and five images before build.
- `BookcaseBookshelfScreen.kt`: when all five genuine images are present, the QA build **defaults to this new modular Library**, retaining the top-right QA return to the previous immersive bookcase. On a build without images the original QA fallback remains.
- **Intentionally untouched:** standalone Home art, real catalog/page IDs, book progress, local saves, achievement state, Nearby LAN, logic/touch/puzzle rules, release approval flags and old rollback visual routes.
- Full source commit for the test package: [`b4e57f2ab220a5c6c326b1da53063474d8eca01e`](https://github.com/PlayerEden/GridMates-Word-Search/commit/b4e57f2ab220a5c6c326b1da53063474d8eca01e).

## Exact-source QA receipt

Both workflows were confirmed **SUCCESS on the same commit** `b4e57f2ab220a5c6c326b1da53063474d8eca01e`.

- [Android QA #42982189161 — SUCCESS](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42982189161): original-source kit assembly and import, core + app unit tests, lint, Android QA APK compile, and exact 5-art-in-debug APK verification all succeeded. Install artifact: [`GridMates-WordSearch-Android-QA` ZIP #13838735135](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42982189161/artifacts/13838735135), GitHub archive size **25,517,479 bytes**, archive SHA256 `2e7f1affcf7d2dafcf2758bf402310aac069e4acad0bd1cbe1d61da9773dc623`. Extract `app-debug.apk`. **This SHA is the GitHub archive digest, not an independently extracted APK checksum.**
- [Independent art/release isolation #42982189117 — SUCCESS](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42982189117): source-image assembly/import; Android DEBUG and RELEASE compilation; verified all five original WS16AS PNGs exact in debug and no WS16AS artwork in release. Independent QA artifact #13838735125 ZIP size **25,517,479 bytes**, archive SHA256 `89d19f1751bfbe37159bd59b15cb4f3775dc4b2b06155ab4f643bf53e7d9f2a5`.
- No device installation, pixel-perfect Android screenshot, live 48dp tapping or emulator runtime execution is claimed from CI.
- All eight temporary transfer-staging files created by the assistant in the owner's Google Drive were deleted after source art was verified and committed. There is no external Drive dependency.

## Owner physical-phone acceptance next

1. Download the **Android QA ZIP #13838735135** (GitHub sign-in may be required), extract `app-debug.apk`, back up existing QA saves, and install the APK. **WS16AV locally signed test APK uses an independent signing key**, so installing over WS16AV may fail due to signature mismatch. Uninstall the old QA package *only after* backing up needed data, or use another device.
2. On the phone, Home → **Full Shelf/Library** should open the new original five-part cabinet by default. Verify there are **no detached parchment header/footer strips**, and the crown, pilasters, backboard and base fill the screen coherently. Title/Back and page count are native safe-inset overlays, not painted panels.
3. Confirm actual Home shelf sprites are independently layered behind carved columns, and real book spines contact shelf planes. Tap a real book → front-cover progress modal → **View pages**; test Android Back, the top-right previous-art escape and horizontal paging when real catalog overflows. Decorative future blanks must never open a book or count as owned.
4. Screen acceptance: narrow 320/360/412dp, tall, landscape and larger font; title and book-count legibility; controls remain usable around Android bars/cutouts. Send real phone screenshot(s) and any installation/error notes before changing the art.
5. Only after approval: produce and integrate separate reusable text-free placard, arrow, pagination dot, and button art family; keep live native text and ≥48dp interactive targets.

**No merge/release:** PR #131 remains draft. Keep prior source-matched WS16AW/WS16AM Android QA, WS16AV original-art locally signed tester and current feature branch for rollback. Visual and gameplay handset review remains pending.

**New-chat one-liner:** WS16AX is actual source-art-integrated, dual-green on `b4e57f2a`; Android artifact [#13838735135](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42982189161/artifacts/13838735135). Ask for phone screenshots and tap results; do not merge until owner acceptance.
