# WS16AY — Cabinet proportions and stable install-over QA
Date: November 11, 2026. Status: code/CI changes submitted; Android and art builds must pass before a new APK is handed off. PR #131 remains draft.

## Owner evidence and reasoning
The owner installed WS16AX and provided a 674 × 1536 phone screenshot. The original five painted pieces are attractive but the middle side columns and backboard are elongated. The stage uses source-proportional crown/base but ContentScale.FillBounds on the three tall middle pieces. Do not remake the five master images yet.

## Changes and safeguards
- WS16ATModularLibraryScene: use uniform-aspect ContentScale.Crop for both pilasters and backboard, avoiding vertical elongation; crown/base remain FillBounds with already proportional height. Note possible side cropping on tall screens and review before approval.
- Android QA debug package stays com.playereden.gridmates.wordsearch.qa, signed with the same existing public QA-only qa-signing/wordsearch-qa.p12, not production credentials.
- Both CI flows check the known successful WS16AX signer SHA-256 75cc3c896247d71fbd51f7a3ed41f90c8218a8a5a70df8c0f6b494b5cc23d419 before publishing. Any mismatch stops the workflow.
- Increase default versionCode 57 to 58 and change versionName to 0.9.14-cabinet-proportions for an install-over update.
- Add source contract tests. Do not alter book catalog, UI hit slots, saves, Nearby/LAN, release asset permissions or keys.

## Last verified fallback and follow-up
WS16AX source b4e57f2ab220a5c6c326b1da53063474d8eca01e passed Android run 42982189161 and art/release run 42982189117. Owner-installed artifact: https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42982189161/artifacts/13838735135.
The earlier WS16AV locally repackaged tester had an independent signer; do not use it as a signing standard. Current WS16AX from GitHub uses the established public QA signing key.
New WS16AY build is not yet claimed green or tested on phone. When dual-green, install over WS16AX *without uninstalling*. If update fails, preserve app data and diagnose package/version/certificate instead of reinstalling.
Owner should compare the ivy/carvings, seam alignment and any side cropping; tap real books, verify Back, count, and page navigation. Revisit 9-slice art or fresh source artwork only if this geometry fix is still inadequate.
