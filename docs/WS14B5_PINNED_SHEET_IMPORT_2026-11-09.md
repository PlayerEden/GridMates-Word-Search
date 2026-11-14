# WS-14B5 — Use the same static PNG process as profile icon collections

The owner correctly pointed out that GridMates already imports twelve approved Woodland profile portraits through scripts/import_woodland_portraits.py: pinned local PNGs, exact SHA-256 checks, and Android packaging. Word Search can do the same. Using preview URLs from OpenArt was not a necessary prerequisite.

A new unwatermarked QA candidate art pack contains a source sheet and six extracted PNGs: Woodland, Space, and Garden front covers plus independent narrow spines. These are derived from a ChatGPT-generated visual study, NOT the identical previously liked OpenArt concepts. They include some baked lettering, so they are for illustration integration QA only until exact visual review and dynamic text separation.

The single source file to upload to the existing WS-14 draft branch is:

design/visual-system/masters/ws14-generated-asset-sheet-original.png

SHA-256:
9c452ba81352b9a3bd08496de5e8cf8d8ea9ed188af540d4db2b2d558c41808e

After that single PNG is in the branch, the artwork workflow automatically verifies the master hash, crops six images using pinned Pillow, runs the existing six checksummed debug-only imports, verifies generated source/register hashes, checks the debug APK actually contains those images, and that the release APK does not.

A GitHub source-code commit cannot itself transfer binary files from this ChatGPT session. Until the PNG is uploaded, the existing artwork remains the fallback; the registry remains empty and the QA preview reports zero imported images. This handoff is explicit rather than claiming the pictures were installed already.

The original v56 Android build remains fallback pending exact-phone testing and user review.
