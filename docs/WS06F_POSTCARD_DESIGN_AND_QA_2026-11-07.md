# WS-06F — Native result postcards and privacy QA

**November 7, 2026 · Design informed by Sudoku's November share-card briefs · implemented and automated QA PASS (67/67 Android tests).** Real handset QA remains OPEN. See [source-matched receipt](WS06F_BUILD_QA_2026-11-07.md).

## User experience
A finished Solo, Daily, Co-op, Shared Board Race or Speed Duel can preview a **1080×1350, 4:5 PNG result postcard** in addition to the exact same spoiler-safe share text. Buttons offer **Share text**, **Share image**, and **Cancel**. Android's standard chooser opens only after deliberate selection; dismissing the preview leaves progress and match records unchanged. Race/Duel sharing still requires the locally saved result. No accounts, uploads, contacts permissions, forced sharing, fake score or store link.

## Rendering and safety
- Android Canvas renders cream/forest/gold original card layout, with licensed bundled Atkinson/Plex typefaces and an abstract empty decorative puzzle grid containing **no actual letters or answers**.
- Only known GridMates local result mode headings and size-bounded summary text can be rendered. Data flows from existing `PuzzleResultShare.text` and `NearbyMatchShare.text`, which never include player/partner identity, room codes, IP/port, chat, answer coordinates, real puzzle IDs/seeds or opponent's private Duel word IDs.
- PNG is generated offline, written to temporary `cacheDir/result-postcards` and exported as a **read-only, scoped FileProvider content URI**. Manifest grants only that cache subtree, never backups or other app-private files.
- Text sharing remains available if postcard rendering fails. No gallery image save, server, analytics or new network protocol.
- Version v0.7.3 / versionCode27, same public **test-only** QA signer and LAN v6, profile schema4 unchanged. Production signing deferred.

## Automated gates
- Real Android Canvas tests for valid Solo/Daily and three Nearby outcomes, 4:5 dimensions, PNG encode/decode, provider URI/ClipData, read permission without write, rejection of unverified summaries and unsafe URI authorities.
- Compose test for actual image preview and Share text/Share image/Cancel actions. Cancel does not invoke any external intent.
- Existing core/app unit tests, lint, instrumentation compilation, font licensing/assets.
- Actual prior verified v0.7.2 code26 QA APK installed into API35 emulator; update in-place to code27 while retaining the previous `active`, `results`, `daily`, `identity`, `races`, `duels` fields and check same test signing cert. Then run the full on-device test suite. Record exact-source APK hash and pinned rollback checkpoint.

## Physical testing and future choices
Actual Sharesheet behavior varies between target apps; two real phones and different orientations, large text, missing recipients, caching and share URI access need owner acceptance. Also defer optional PNG Save-to-Photos, historical result postcard, cosmetic avatar display with consent, shared theme borrowing/tiering and no-router Bluetooth. No implementation of those is implied. All stacked PRs remain draft/unmerged.
