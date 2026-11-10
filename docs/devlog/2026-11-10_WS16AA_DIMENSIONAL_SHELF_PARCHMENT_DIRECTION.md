# WS-16AA — Painterly dimensional shelf and parchment art handoff (2026-11-10)

> **Quick status:** SPECIFICATION / APPROVED ART DIRECTION ONLY. No generated source image, runtime Kotlin art integration, new APK or on-phone acceptance is claimed for WS16AA.

## Who, what, where, why
- **Owner request:** Previous full-width gray rectangular shelf shadow was bad, **but shadows were wanted**. Removing the shadow exposed a **flat** shelf. The owner's storybook reference screenshot shows a deep carved wooden shelf, realistic contacts, shaded underside and soft illustration-material light. Owner additionally requested a **slightly more textured** warm parchment backdrop.
- **Work performed by:** ChatGPT created an implementation-ready handoff on the current Word Search draft branch, based on code and existing art/QA procedures.
- **Objective:** Make the environment match the rich existing Woodland/Space/Garden illustrated covers without changing the already-liked layout or needing another Canvas-only shader pass.
- **Source of truth:** [WS16AA production brief](../design/WS16AA_DIMENSIONAL_SHELF_AND_PARCHMENT_ART_PRODUCTION_BRIEF_2026-11-10.md) and [draft PR #131](https://github.com/PlayerEden/GridMates-Word-Search/pull/131).

## Previous checkpoint / what was inspected
- [WS16Z phone review + saved Solo card](2026-11-10_WS16Z_HOME_SCREEN_PHONE_REVIEW_AND_SOLO_CARD.md), after [WS16Y visual overlay](2026-11-10_WS16Y_DEBUG_VISUAL_OVERLAY.md).
- Inspected existing `Ws16GShelfArtwork.kt`, `Ws16TShelfPatina.kt`, `Ws16EStorybookShelf.kt`, `Ws16BookCoverPager.kt`, `GridMatesParchmentBackground.kt`, the art pipeline and style bible. Current debug shelf is largely six asset layers with a 42dp layout and some Canvas patina/contact. Existing background is procedural parchment with sparse fibers/flecks.
- Starting PR branch head before brief: `21a96de7f29c8c246f6bed9239b748242dcfb034`.
- Existing code/asset QA at that head: [Android QA #42893343550](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42893343550) **SUCCESS**, [art/release QA #42893343548](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42893343548) **SUCCESS**. The workflow listed [Android artifact #13799831994](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42893343550/artifacts/13799831994) and [art artifact #13798389562](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42893343548/artifacts/13798389562); no independent checksum/APK-byte comparison was done in this documentation pass. This is **pre-WS16AA** code, not a new shelf look.
- Prior stronger byte-checked rollback: WS16Y source `7cb896fa`, [Android #42889659113](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42889659113) / [art #42889659313](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42889659313), [artifact #13797149827](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42889659113/artifacts/13797149827). Do not remove user saves or claim user has installed a newer APK.

## Decisions in this pass
- New modular, true painterly shelf asset family with visible top plane, fascia, underside, independently shaded end supports, carved medallion, **real contact** and **optional silhouette-soft cast shadow**.
- New subtle seamless parchment texture to supplement existing warm base; preserve negative space, avoid tiling seams/noise, retain Compose text and old fallback.
- One unified art direction, not a full-screen picture; no lantern/mushroom props yet.
- New versioned source and export paths, SHA256 manifest, provenance and licensing, QA-only integration, release isolation, exact-source CI, real phone review. Keep older WS16W/Y art for A/B and rollback.
- **Source documentation commit:** [`4d2b3f42dc94146ee227e56e768d44fb72ff6aa7`](https://github.com/PlayerEden/GridMates-Word-Search/commit/4d2b3f42dc94146ee227e56e768d44fb72ff6aa7). No code change in it.
- **No APK built for WS16AA.** Documentation-only [skip ci] write. No implied approval of new pixels.

## Next exact action
Build the **actual asset sources**, not merely a reference UI screen or Canvas gradients: export shelf pieces and seamless paper PNG, import/pin/QA them through the established pipeline, integrate debug-only, preserve fallback and gameplay, verify both Android and art/release builds, then share one verified APK and compare owner screenshots with WS16Y/WS16Z. Keep draft PR unmerged until art and phone acceptance.

**New chat one-liner:** Read WS16AA production brief, then implement the real source-controlled modular shelf + softly textured parchment asset pass; current document is only the approved design and build plan.
