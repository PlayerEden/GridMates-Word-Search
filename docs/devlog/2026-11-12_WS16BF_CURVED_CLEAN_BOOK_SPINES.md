# WS16BF — Clean edges and curved leather binding, without double spines

> **Status:** SOURCE COMMITTED / Android and art CI queued or running / owner phone approval pending. November 12, 2026. Do not call v65 approved until actual APK identity and owner visual test.

## Who / why / evidence

- **Owner:** Submitted actual v64 Android Library screenshot and reported `"There's a lot of specks on the edges of the spines they also feel 'flat'"`. These concerns are separate: fringe pixels and insufficient material depth. Earlier v63 complaint was a full-width faux backing behind the book.
- **Starting point:** Verified owner-installed v64 [Android artifact #13924000090](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/43173531241/artifacts/13924000090), SHA-256 `654ef1af1b7ba54f323c37dc0103c82b5d80e370ee5d300b764812b9b45acf44`, dual-green [Android #43173531241](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/43173531241) and [art #43173531335](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/43173531335); v64 appearance not approved by owner. Existing [PR #131](https://github.com/PlayerEden/GridMates-Word-Search/pull/131) is draft/unmerged.
- **Evidence:** Inspection of v64 APK `assets/ws16ai/spines/{woodland,space,garden}.png` (104×542 RGBA) found fringe alpha values and small colored or white slivers on original outer edges. This is not an extra Compose sprite. The book remains visually flat because its face is evenly lit and the v64 renderer removed the only contact shadow.

## Actual source implementation

1. **New reproducible generator** `tools/asset_pipeline/build_ws16bf_curved_spines.py`: build ONLY three playable 104×542 spines using original pinned `ws16ai` source PNGs, preserving printed graphic location and **without any wider second leather backing**. Replace colored edge fringing with one 104px rounded mask; smoothly reconstruct the first/last edge pixels from dark leather palette, suppress the source's top/bottom bright artifacts, and apply a left highlight/right shadow gradient within the single silhouette. Save PNGs and a manifest containing exact source+output SHA-256 under `app/src/debug/assets/ws16bf/`. Original WS16AI and wider WS16AO binaries are untouched.
2. **Runtime:** `BookcaseWoodArtwork.kt` loads `ws16bf/spines/$name.png` only for a new `curved` profile; falls back to unchanged original `ws16ai` safely. `WS16AMSpineBay` uses real 104:542 proportions and a much subtler 2dp / 30%-opacity contact shadow for the curved portrait path. Existing book ID/click handler, 2+1 shelf distribution, title clearance, cabinet, Home/Profile/Collection, LAN and saved progress are unchanged.
3. **CI:** Both `.github/workflows/ws01-android-qa.yml` and `ws12g-qa-art-lab.yml` generate the source-pinned artwork and run `test_ws16bf_curved_spines.py`. The independent `verify_ws16bf_apk.py` ensures all three exact derived PNGs/manifest appear in debug and none in release. The tests enforce no out-of-silhouette edge alpha, deterministic render, original dimensions and runtime integration. Existing art/source verifiers remain.
4. **Build:** `app/build.gradle.kts` versionCode **65**, versionName `0.9.21-curved-clean-spines` (debug suffix `-qa`). Same package `com.playereden.gridmates.wordsearch.qa` and same public QA test key. This remains a debug-only visual candidate; not a production art approval.
5. **Original artwork rights:** Still QA only; no new licensed or externally sourced pixels. User progress, save schema, unlocks, and multiplayer unchanged.

**Latest code commit before CI:** [`70fa6e39674d0bd1a6b54066e2a44b626d759c14`](https://github.com/PlayerEden/GridMates-Word-Search/commit/70fa6e39674d0bd1a6b54066e2a44b626d759c14). Later documentation-only changes are `[skip ci]`.

## QA gate and rollback

- **Android QA:** [run #43175871386](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/43175871386) started, final result pending at journal creation.
- **Art/release isolation:** [run #43175871287](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/43175871287) started, final result pending at journal creation.
- **v65 APK:** not yet verified or delivered. Must retrieve artifact, confirm exact commit, signer, package, version and SHA before claiming readiness.
- **Owner fallback:** v64 already installed [artifact #13924000090](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/43173531241/artifacts/13924000090); preserve progress by install-over with same signer, never uninstall/clear app data.
- **Known limitations:** Unit/source checks and CI cannot evaluate whether shading looks genuinely like a leather book on the owner's phone. Physical screenshot / tap / save-resume review remains required. Keep PR #131 draft; art QA-only.
- **Next exact action:** inspect both workflows (especially new per-pixel asset tests). Repair any true failure on same branch; on dual-green inspect actual APK and deliver signed v65 visual comparison. If visually flat even after clean shading, move toward front-facing illustrated covers rather than another fake extra panel.
