# WS-14 Active New-Chat Handoff — November 9, 2026

**Start here for a new game-dev chat.** This is a snapshot, not a replacement for inspecting live GitHub.

## Latest WS-14C playable artwork acceptance checkpoint (Nov 9, 2026)

A **real downloadable illustrated QA APK** has been extracted from the successful WS-14 Art Lab workflow #42576785170 and SHA-256 checked against exact source commit 00d2a36: \`8a6c45ab4f879d3f7c6d5c5ba5c010b3b6d72ce2c32c8b4ebf34792506c45e0a\` (14,152,866 bytes). It has **3 Woodland/Space/Garden cover PNGs and 3 spine PNGs** inside \`assets/ws14/\`. We have not yet received physical-device results from the owner.

**Read the actionable playtest guide first:** [WS-14C illustrated Android APK review and decision checklist](WS14C_ILLUSTRATED_APK_PHONE_REVIEW_2026-11-09.md). The exact artwork QA artifact (expiring) is [here](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42576785170/artifacts/13656070327). This is the **artwork workflow APK**, not the standard Android QA artifact.

**Test routes:**
- Play → **Test Puzzle Books** → Study B illustrated covers/spines; toggle **Show original art (A)** / **Try WS-14 covers (B)** and open an actual book/page.
- Play → **View Library Artwork** → **Review WS-14 assets**; should report **6 of 6** imported QA cover/spine sprites.
- Check Collection/portraits, Solo/Daily/Nearby and save preservation; the new art does **not** yet decorate all Home/Collection screens.

**Known questions:** book title lettering is baked into the candidate illustration and may clash with the native book labels; determine readability, scale, style coherence, visual density and desired further assets from genuine on-phone screenshots. **Do not generate another art family before this review.** Keep V1 immutable and make revisions as separate asset versions.

**Release status:** not owner-approved, not on-phone verified, not merged to main; v56 remains known physical fallback. GitHub source/doc branch can be ahead of the exact successful APK commit from non-binary documentation changes.

---

## Current Word Search state verified from GitHub

**Repository:** https://github.com/PlayerEden/GridMates-Word-Search

**Working visual branch (at handoff):** feature/ws14b-style-anchor-and-art-direction-2026-11-08

**Draft stacked PR:** https://github.com/PlayerEden/GridMates-Word-Search/pull/131 (base is WS-14A1 PR #130 branch; do not assume main contains these changes)

**Last fully CI-verified artwork source commit:** 00d2a36d12c0410a5f04b780ff8c3fe83f69c841

**Confirmed green GitHub runs on this exact commit:**
- Android QA: https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42576785096
- Art import, debug and release-isolation QA: https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42576785170
- QA APK artifact: https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42576785170/artifacts/13656070327 (expires; may need rebuilding)

The artwork job logs specifically verified **six actual imported book sprites** and **zero WS-14 sprites in release APK**. This is beyond code scaffolding: the source PNG and the generated assets were consumed by CI. On-device owner acceptance, production rights approval and general release are **not** established.

**Artwork source:** design/visual-system/masters/ws14-generated-asset-sheet-original.png; 1536×1024 PNG, SHA-256 9c452ba81352b9a3bd08496de5e8cf8d8ea9ed188af540d4db2b2d558c41808e. It is an unwatermarked **ChatGPT-generated QA replacement sheet**, NOT the exact three earlier OpenArt concept images liked by the owner. Six generated exports: Woodland, Space, Garden book covers and matching spines. Text on that image sheet is baked into the illustrations, and it still requires visual cleanup and review before release.

**Import code:** tools/asset_pipeline/import_ws14_candidate_sheet.py calls import_ws14_cover.py; validated by validate_ws14_assets.py and CI workflow ws12g-qa-art-lab.yml. Source paths/hashes immutable; debug assets are generated during the build. A registered asset is QA_INTEGRATED, with rights PENDING_REVIEW. The source-controlled register starts empty and is filled in the CI worktree.

**Android integration:** BookArt.kt selects new WS-14 cover/spine in Study B, preserving original Study A and older WS-12 art fallback. There is a native owner-preview screen accessed as **View Library Artwork → Review WS-14 assets**. Play screen now uses compact launchers. No source edits to saved progress, earned portrait IDs, book pages or LAN modes were required for these art changes.

**Old physical-device fallback:** v56 was the known owner-verified fallback in the WS-14 conversation. Do not infer that the current generated APK was installed or accepted.

## Immediate next work

1. Read docs/ASSET_CREATION_GITHUB_INTEGRATION_PLAYBOOK.md. Inspect live PR #131, branch tip, files, and GitHub runs rather than treating these numbers as forever-current.
2. Have the owner inspect the now-real illustrated QA APK on their phone (or build/send the current matching APK artifact if needed). Verify package identity and back up saves; do not demand a reinstall by default.
3. Audit the six actual cover/spine pixels at bookshelf size: imported candidate sheet contains baked labels and decorative framing, which may need to be replaced by art-only masters/native text. Check 320dp, 360dp, 200% text, portrait/landscape, book click/progress and old portrait collection.
4. If the owner approves the direction but asks for visual fixes, generate **new versioned master artwork files**, upload the actual binary blobs to GitHub, record their SHA-256, and update a new/importer mapping. Do not silently overwrite the pinned candidate or pretend the prior OpenArt art was shipped.
5. Complete the broader Play/Bookshelf/Collection illustrated visual slice once the artwork itself is accepted. Keep v56 rollback, the release-art isolation rule and draft PR until appropriate QA.

## Continuity prompt

Continue GridMates: Word Search based on the actual latest GitHub sources. Read AGENTS.md, docs/ASSET_CREATION_GITHUB_INTEGRATION_PLAYBOOK.md and docs/WS14_ACTIVE_HANDOFF_2026-11-09.md. Compare live head commits and CI results, then continue real asset/art integration and test a playable Android QA APK. We already solved binary uploads: use original file bytes → SHA-256 → GitHub blob/tree/commit/ref → deterministic PNG importer → debug/release APK checks. Do not stall on watermarked OpenArt URLs or replace the exact approved concept without explaining it. Preserve save data, profiles and fallback, distinguish CI from physical QA, document changes in this handoff.

## Useful links

- [Style bible](../design/visual-system/WS14_VISUAL_STYLE_BIBLE_AND_SPRITE_ASSET_CONTRACT_2026-11-08.md)
- [Full WS-14 roadmap](WS14_FULL_APP_VISUAL_IDENTITY_AND_UI_PRODUCTION_ROADMAP_2026-11-08.md)
- [Pinned sheet import handoff](WS14B5_PINNED_SHEET_IMPORT_2026-11-09.md)
- [Master game design](MASTER_GAME_DESIGN.md)
- [General coding handoff](START_HERE.md)
