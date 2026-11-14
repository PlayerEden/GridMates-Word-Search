# WS16AV — Independently signed local visual-test APK using original cabinet PNGs
**Date:** 2026-11-11 · **Status:** real installable QA candidate created and verified **outside GitHub**, not yet phone-tested, not Play-ready. The PR remains draft.

## Why this alternate path was needed
Existing GitHub text-file tooling could not transfer the new `WS16AS_Modular_Library_Art_Kit.zip` binary. Adobe transfer attempts failed. Instead of copying an old illustration or requiring the owner to redo the artwork, we assembled a QA APK from the exact Android artifact that had already passed CI.

## Exact provenance
- Android build source commit: `44c02730dba3fa53439200a8dbfb9f6de3465c3a`.
- Android QA workflow passed: https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42974359635, artifact `13835142140`, original 24,884,486-byte `app-debug.apk`.
- Independent art/release isolation workflow passed: https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42974359642. These CI runs tested the modular **code gate only**; original five images were not in the GitHub checkout.
- Five exact PNG bytes were read locally from the original conversation ZIP (not recreated). Their hashes, geometry, and original art manifest had already been validated. Their destination in the APK is `assets/ws16as/shell/`: `top_crown.png`, `left_pilaster.png`, `recessed_backboard.png`, `right_pilaster.png`, `bottom_base.png`.

## Packaging performed locally
- Preserved every original APK ZIP entry and its original local header offsets, manifest, DEX, resources and native libraries.
- Removed the previous APK v2 signing block, appended the five source-exact PNGs and an art provenance manifest, and generated a new Android APK signature scheme **v2** with an independent test-only RSA-3072/SHA-256 signer.
- Both the signer and a separate signature verifier recomputed the three-section, 1MB-chunked Android v2 content digest; X.509 public key and signature verified. ZIP CRC for all entries passed and the five original image hashes exactly matched.
- Packaged APK filename: `GridMates_WordSearch_WS16AV_Modular_Library_QA.apk`, size **25,982,926 bytes**, SHA256 `2b47e4e9a92ed5c1f11c20c83344763bc934cfdbc3672675f436fbb83d156bfa`.
- It is available as a **ChatGPT conversation-generated file**, not in GitHub Actions artifacts; **do not invent a GitHub download URL**.
- Certificate SHA256 fingerprint: `e89dcec8d1dd3684f76152e11ca350d90dc38dc1dce5f021db2c9b33e57943ee`. This differs from GitHub runner's usual QA key, so install-over-old-QA may fail until prior QA app is uninstalled. Back up any test data first.
- No device installation or gameplay tests have yet been performed; independent Python integrity checks do not guarantee Android phone install compatibility. The APK is intended for **manual phone QA only**.

## Suggested owner phone test
1. Back up any necessary saves from existing **QA** package before uninstalling it. Production app is untouched.
2. Install the conversation-provided WS16AV QA APK. Open **Library → QA overflow menu → Try modular Library**. The option appears only if all five real PNG files are present (they are in this APK).
3. Look for seamless full-screen crown/backboard/columns/base, real 2+ movable Home shelves **behind** the columns, book spines actually resting on top of their shelves, tap-to-pull-book → View pages, and physical Back.
4. Test 0/3/7/13/24+ real books if debug fixtures are available, landscape, larger font and pagination.
5. Report screenshots, title/crest conflict, bottom book count readability, edge/cropping, any blank art areas, install errors and tapping problems. This remains QA only until reviewed.

## Source-control status remains incomplete
The **original new image bytes are still not committed to the repository**. GitHub's original ZIP upload/import workflow remains staged. This local binary bridge makes owner testing possible without falsely claiming GitHub art source parity or a release-ready package. Preserve fallback Library and unmerged PR #131.
