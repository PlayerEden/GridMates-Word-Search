# November 9, 2026 — WS-14J1: First Real Woodland Transparent Asset

> **Quick status: CANDIDATE FILES COMMITTED / SOURCE + PIXEL CHECKS / PHONE QA PENDING.** This pass saves **real artwork bytes**, not a generated chat status-board or screenshot. There is no claim of owner art approval, gameplay integration, a release-approved asset, or a source-matched Android APK.

## Who, what, where, why
- **Owner request:** "Let's continue this" from the WS-14J three-theme artwork sprint.
- **Implemented by:** assistant (art vector construction, raster export, hash receipt, GitHub binary transfer, integrity test and log); owner has not reviewed this precise artwork.
- **Purpose:** establish a reproducible, small, transparent Woodland visual anchor for the compact puzzle-page header before matching Space/Garden.
- **Where:** draft [PR #131](https://github.com/PlayerEden/GridMates-Word-Search/pull/131), its feature branch, `design/visual-system/masters/`, `design/visual-system/exports/`, and the WS-14J separate candidate register.

## Starting verified state
- Prior log: [WS-14J kickoff](2026-11-09_WS14J_ART_PRODUCTION_KICKOFF.md).
- Branch before asset commit: `04630980c53d16a0e0a99e23ab0c1ce500aa8ef2`; latest actually code-tested WS-14I source `3a3d0bc`.
- Source-matched *pre-art* QA: Android [#42664221672](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42664221672) and debug/release [#42664221664](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42664221664), both success.
- Physical 20×20 word drag, fifth bulb tap and save-reopen still open. Existing book covers, profile portraits and native fallback remain unchanged.

## Actual artwork and source receipt
- `PAGE_ACCENT_WOODLAND_V1` is a **standalone unlettered** fern-like sprig, oak leaf and one tiny mushroom on fully transparent background, with restrained forest/sage ink tones.
- Editable **actual SVG master:** `design/visual-system/masters/ws14j-page-accent-woodland-v1.svg`, 4272 bytes, canvas 512×512, SHA-256 `a757ac3a5be430cb3fc2ae2ea32f4e6513baf8887a66dbd2818c76ef5d7fbb80`.
- Actual **PNG binary:** `design/visual-system/exports/ws14j-page-accent-woodland-v1-128.png`, 128×128 indexed RGBA-with-transparency PNG, 2745 bytes, SHA-256 `747af7c99b76b7ed38c91460409fd9d32a24decd1c1412991d9c364a3f3b331b`.
- PNG alpha bounds [13,10,113,123] (xyxy), 0–255 alpha. Created from original vector with CairoSVG, optimized with Pillow 48-color palette; actually inspected at 24/32/36/72dp on light paper. The vector source was built expressly for this pass. The separately generated chat-wide status boards were rejected and **not** used as asset sources.
- Recorded in `design/visual-system/ws14j-page-accent-candidates.json` as `GENERATED_UNREVIEWED` / `PENDING_REVIEW`, with source and export hashes, candidate ID, fallback, and explicit no debug/release integration.
- Added `tools/asset_pipeline/test_ws14j_page_accent.py` to verify the real files, hashes, dimensions, alpha and candidate-only status.
- GitHub first actual binary commit: [f2d84e7](https://github.com/PlayerEden/GridMates-Word-Search/commit/f2d84e7a1d9e64aa8b8bf55d6428207b305c86c6). Editable master commit [441bef1](https://github.com/PlayerEden/GridMates-Word-Search/commit/441bef1d2a0f9e0650be0c5d19b9d971a95b156e). Manifest `fba1a5e`, integrity test `f7f547d`.
- GitHub tree inspected after binary/master commit: actual PNG Git blob `e0e6dbed8aa89a7eda22bae6bd67d8f7bfcf43b3`, 2745 bytes; SVG Git blob `d37a06da1b67b128394aa32ed23fe6bf802141fe`, 4272 bytes. This is not merely a chat file.

## Build, testing and safeguards
- **APK in this milestone:** not claimed. Standalone source/art master is now in Git; its PNG is **not in Android debug assets or release assets**, and gameplay UI has not changed.
- **Checks performed locally:** source rasterization, PNG size and alpha bbox, SHA-256 of actual source/export, small-scale cream-background visual specimen.
- **Automated checks:** new integrity test was committed; inspect next exact-head workflow/test runs separately before saying it passed CI.
- **Not yet done:** on-phone 32dp evaluation with native book header, 320dp/360dp/large fonts, source-matched import/release isolation, owner comparison and rights review.
- **Rollback:** unchanged native WS-12 emblem/text; no saves, board sizes, book IDs, original covers, profiles, signing, LAN or package state changed. Preserve v56 physical fallback until newer APK acceptance. Keep PR draft.

## Next exact action
Evaluate this **actual committed asset** as a QA-only header candidate beside `Book · Page N` while retaining the existing emblem fallback; add pixel/source checks and ensure the new asset is absent from release. Verify exact Android and art CI, then compare the 32dp icon on a phone. If the style is accepted, make Space and Garden siblings using this same icon grammar; otherwise revise into V2 without overwriting this original.

## GitHub Actions follow-up (checked after asset/test commits)
- [Android QA #42673911715](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42673911715) on exact source `f7f547dd1d8bc5f4628cbe854f641fcb13adbbd4` was still **in progress** when inspected; **no green conclusion claimed**.
- [Art/release-isolation #42673911820](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42673911820) at that same SHA reported **failure**. Its GitHub Actions jobs API returned zero job records, so the underlying failure cause is **not verified**; do not invent a Kotlin, asset, Gradle or quota diagnosis from this status.
- Earlier art-only and source-commit PR-triggered workflows ([#42673585874](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42673585874), [#42673586292](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42673586292), [#42673763881](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42673763881) and [#42673763829](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42673763829)) also show failure without job-level evidence.
- Local independent checks **passed**: both source/PNG actual SHA-256 values, PNG 128×128, 0–255 alpha, bbox (13,10,113,123), transparent top-left and 4868 nonzero-alpha pixels. This is **not Android CI**.
- As a result, the prior green 6564f91/WS-14I build remains the verified Android fallback. Do not start new app or release-art integration on an assumed green source.
