# WS-16G2 — Master Batch 1 asset production brief (spec/manifest pass)

> **Status:** DOCUMENTED / NO NEW PAINTERLY BITMAP OR APK. This is a design-production handoff, not a falsely claimed asset-generation milestone.

## Who, what, where, why
- **Owner direction:** after viewing the actual WS-16F Home versus its detailed reference, owner requested a **whole cohesive art asset set for most of the app**, not another series of one-off UI adjustments. Assistant initially documented an 81-role broad asset inventory, then owner requested a precise **Batch 1 production brief**.
- **Actual work:** write a detailed 20-role art brief with artwork treatment, master/export dimensions, clean alpha, layer assembly, responsive integration and QA gates; create a machine-readable tracking register.
- **Repository:** `PlayerEden/GridMates-Word-Search`, branch `feature/ws14b-style-anchor-and-art-direction-2026-11-08`, draft PR #131. New docs/design and modular production manifest only.

## Prior checkpoint / reusable baseline
- Latest substantive WS-16G1 code `2ff2918e5b11d4c61ad3bbca16a6ac0b71993230` centers the Together button content. Exact-source [Android QA #42740219803](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42740219803) and [art/release QA #42740219820](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42740219820) both **SUCCESS**; owner screenshot of patched button still pending.
- Prior owner-viewed actual WS-16F QA is [ZIP #13727073915](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42737788114/artifacts/13727073915), debug APK SHA256 `88075143e0a82a1fb59e96c3519de96043bbf734fa4f8b75b1b213b31f76dec8`. Need current Library screenshot for painterly backboard assessment.

## Files
- `docs/design/WS16G_BATCH1_PAINTERLY_ASSET_PRODUCTION_BRIEF_2026-11-09.md` — visual specs, exact dimensions, layering, source mapping and acceptance.
- `design/visual-system/modular/ws16g-batch1-production-register.json` — 20 record rows; references existing WS-16E/16F and WS-16B/D editable SVGs without pretending those are the painterly production sprites.
- This log, chronological journal and active handoff.

## Tests and build
- **This pass:** docs/manifest-only, no Android package build; commit message uses `[skip ci]`.
- **No new sprites, PNG exports, hashed binary masters, actual component code changes, release gates or handset tests.**
- **Fallback:** WS-16F dual-green owner APK. Keep PR draft/unmerged and data untouched.

## Next single action
- Produce the **actual original painterly masters** for Batch 1A shelf/parchment/frames and import unique pieces into debug QA with true pixel hashes. Then Batch 1B coherent illustrated action subjects; one consolidated tested build. Do not respond with another giant checklist instead of producing files.
