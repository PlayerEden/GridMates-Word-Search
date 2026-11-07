# WS-07B — Compact Home & Nearby Entry: QA and UX Acceptance

**Date:** November 7, 2026  
**Candidate:** draft [PR #55](https://github.com/PlayerEden/GridMates-Word-Search/pull/55), QA-only v0.7.8 / Android versionCode32, stacked on [WS-07A PR #54](https://github.com/PlayerEden/GridMates-Word-Search/pull/54).  
**Status:** Automated checks still require source-matched confirmation. **No user screenshot/physical-device approval claimed.**

## User problem and design hypothesis

Owner's nine real-device screenshots showed oversized Home cards and lengthy Nearby setup above active gameplay. Earlier WS-07A fixed board-first play and input, but first-time entry still made people scroll past large branding or optional instructions just to start playing. This pass prioritizes the main tasks on first sight, preserving the deliberate paperback design and simple trusted local Wi-Fi pairing.

**Default Home:** compact app identity and welcome, current saved puzzle summary and Continue Solo; quick Daily and Play Together actions. Keep progress, records, collection, help and settings available.

**Default Nearby setup:** Back, Play Together title, choice of Co-op/Race/Duel, primary Host selected mode and Join nearby game, with optional appearance, theme, challenge, board size and room-code settings secondary. Room discovery and manual Wi-Fi address appear only after selecting Join. Live Co-op/Race/Duel board uses the fixed active-match screen from WS-07A. Setup/history/optional room lists may scroll when expanded; *live gameplay* must not scroll as a page.

## Automated gates

- [ ] Standard Android CI builds and lints with **zero Kotlin compilation errors**.
- [ ] Actual Compose test: on **360×720dp** viewport, Home Word Search, Continue Solo, Open Daily and nearby primary action are **displayed** without scrolling.
- [ ] Actual Compose test: on **360×720dp** viewport, Host Co-op game and Join nearby game are **displayed** on setup; optional appearance and nearby-room listings are absent until selected.
- [ ] Existing compact 320dp / 200% font tests for gameplay and Nearby options still pass. No clipping/collision that disables actions; controlled scrolling permitted where increased text genuinely requires it.
- [ ] Historical WS-07A finger-up, 15×15/20×20 physical touch-injection, all three modes, portrait/landscape and atomic-save regression suite all green in the **full API35 emulator**, not unit-only.
- [ ] Real same-QA-signer **v0.7.7 code31 → v0.7.8 code32** APK install-over retains schema4 Solo, Daily, identity, Race, Duel and settings. Verify source hash, package, cert, APK sha256, and pin rollback.
- [ ] Do not include production signing, change the package ID, or merge the stacked draft PRs.

## Owner real-device acceptance — not yet performed

1. **Before install:** Export a personal backup from each Android handset. Install new same-signer QA package over old app; do not uninstall/clear application data. Confirm current Solo puzzle/progress, Daily and stats survive.
2. **Home:** With your natural system text size, launch and verify Word Search + current puzzle + Continue Solo + Daily + Play Together are easy to find without scrolling. Test a narrow screen and a 200% text setting; when larger text requires scrolling, no primary action can disappear permanently.
3. **Nearby entry:** Open Play Together. Do Host and Join appear *before* advanced puzzle size, cosmetics and Wi-Fi notes? Switch Co-op, Race, Duel and confirm the selected Host label follows the mode. Expand then collapse board look, puzzle options and room code; confirm defaults remain intact.
4. **Join:** On B, select Join and find A's room on trusted Wi-Fi; if discovery is blocked, use manual address without changing gameplay state. The game should explain that guest Wi-Fi isolation can block local discovery; no internet connection is necessary.
5. **Live match:** Both connected phones can mark words and see updated owner/progress. Word selection, pinch zoom, chat, room info and remaining words are reachable without whole-page gameplay scrolling. The prior **two-phone P0 gate #53 stays OPEN until this passes**.
6. **Accessibility:** Repeat a short session in portrait/landscape, 320dp/compact device and 200% font. Check TalkBack/keyboard/swipe input, real recipient sharing and large board precision separately under WS-07C/D.
7. Record PASS/FAIL with phone model, Android version, APK version and hash, redacted screenshot/video (remove room code/IP/chat) and reproducible steps.

## Related source and signoff

[WS-07 research/spec](WS07_PLAYTEST_RESEARCH_AND_UX_SPEC_2026-11-07.md) · [Full roadmap](DEVELOPMENT_ROADMAP.md) · [WS-07A verified build receipt](WS07A_BUILD_QA_2026-11-07.md) · [P0 physical word-marking #53](https://github.com/PlayerEden/GridMates-Word-Search/issues/53).

**Acceptance is explicitly separated:** automated source/build correctness, owner visual/usability quality, and real two-phone authoritative gameplay. No single green workflow proves all three.
