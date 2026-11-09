# GridMates Art Production and GitHub Integration Playbook

**Status:** Reproducible developer process, recorded 2026-11-09. **Source of truth:** this repository, the actual source files, hashes and source-matched GitHub Actions logs. Designed for future chats, Codex work, and the GridMates puzzle series. **Do not assume a process or image is finished solely from chat prose.**

## What we proved on Word Search

On branch feature/ws14b-style-anchor-and-art-direction-2026-11-08, draft PR [#131](https://github.com/PlayerEden/GridMates-Word-Search/pull/131), a **1536×1024 original PNG sheet** was committed at:

    design/visual-system/masters/ws14-generated-asset-sheet-original.png

SHA-256 of those exact original bytes:

    9c452ba81352b9a3bd08496de5e8cf8d8ea9ed188af540d4db2b2d558c41808e

The source-file commit was **00d2a36d12c0410a5f04b780ff8c3fe83f69c841**. The sheet is **ChatGPT-generated replacement/candidate art**, different from the earlier OpenArt Woodland/Space/Garden concepts the owner liked. It contains lettering baked into artwork, so its use is currently **debug QA only**; concept preference is not final pixel or commercial-rights/release approval.

**Verified automated evidence at exact commit 00d2a36:**

- [Artwork import + debug/release APK isolation run 42576785170](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42576785170): **success**. Logs say QA_SPRITE_OK for six IDs, WS-14 asset register OK: 6, and "WS-14 cover/spine sprites verified: 6 debug-only; zero in release."
- [Standard Android QA run 42576785096](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42576785096): **success**.
- [Actual WS-14 QA debug APK artifact](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42576785170/artifacts/13656070327); artifacts expire, so use the workflow run/source to rebuild if missing.
- **Not verified:** owner on-phone look/feel and save-migration review, commercial-rights and exact-pixel acceptance, release art approval, actual two-phone LAN acceptance. The last owner-verified phone fallback was v56. Do not silently promote this source to shipping status.

The same broad method was already proven for **12 Woodland profile portraits**: use immutable PNG assets and SHA-256 checking instead of runtime-generated art or transient image preview links. See **scripts/import_woodland_portraits.py** and **design/woodland/approved-portrait-reference.json**. The portraits are imported from a pinned **PlayerEden/SudokuTogether** commit in CI; do not change their source IDs or replace them while working on book art.

> **Permanent pass logging:** At every artwork/feature pass, including each debug/release build, update the [development journal](DEVELOPMENT_JOURNAL.md) and add a dated [pass entry](devlog/ENTRY_TEMPLATE.md) with exact source→asset→APK evidence and the next task. The old chat need not survive for the next chat to continue.

## 1 — Start a new chat correctly

1. Open this repository (not SudokuTogether, which is a separate app).
2. Inspect the **latest** open draft PR, branch head SHA, changed files, actual Android/Art Lab workflow runs, and relevant issues. The branch and hash in this document are historical anchors, not guaranteed to remain latest.
3. Read this playbook, **docs/WS14_ACTIVE_HANDOFF_2026-11-09.md**, **docs/START_HERE.md**, **docs/WS14_FULL_APP_VISUAL_IDENTITY_AND_UI_PRODUCTION_ROADMAP_2026-11-08.md**, the WS-14 style bible in **design/visual-system/**, and the current source/registry.
4. Distinguish **creative concepts**, **real source files**, **debug-integrated assets**, **CI-green APKs**, **owner device approval**, and **production release approval**. They are separate milestones.
5. Continue on a dedicated draft branch or stacked draft PR. Do not merge, modify the production package/signing, clear saves, or overwrite originals merely to move faster.

A practical prompt for a new chat:

> Continue GridMates: Word Search from GitHub, not the previous chat transcript. Read AGENTS.md, docs/WS14_ACTIVE_HANDOFF_2026-11-09.md and docs/ASSET_CREATION_GITHUB_INTEGRATION_PLAYBOOK.md. Inspect PR #131 or its successor, the latest source SHA and both workflows. Reuse the proven local PNG + SHA-256 + debug-only Android art pipeline. Do the actual source changes, test them, and update the handoff with precise outcomes. Preserve v56 as the physical fallback until owner QA and do not call candidate art release-approved.

## 2 — Design an asset family, not disconnected screenshots

For each new theme/collection, define:

- **Asset family:** e.g. Woodland, Space, Garden, future books/themes, profile portraits, board looks, button decoration, Home mode vignettes, and Collection ornaments.
- **Exact component role and stable ID:** cover, spine, badge, emblem, background, progress frame, button decoration, etc.; list target dimensions and whether transparency is expected.
- **One art direction:** warm paperback/storybook watercolor and gouache, restrained forest/sage/cream/gold palette, fine ink contours, consistent light direction and a readable silhouette at 100–140dp. Space/Garden can use thematic palettes while sharing the overall visual grammar.
- **Phone-sized acceptance:** 320dp, 360dp, large text (up to 200%), contrast, transparency fringes, focus/touch states and landscape.
- **Native UI owns behavior:** game titles, book names, counters, status, accessibility labels and actions should be native Compose, not fake tappable art. Generate **standalone illustrations**, ideally no baked words.
- **Rights/provenance:** record generation source/model or artist/source license, any reference image permissions, download/original format, and whether commercial use is documented.

Recommended future workflow: generate one clean independent master **per asset or tightly related asset group**, and archive the original before cropping. A multi-object contact sheet is acceptable for QA studies when extraction is deterministic, but not ideal for final production art. Prefer high-quality source images over screenshots or watermarked thumbnails. Never substitute an internet image result for artwork the owner specifically chose without reviewing that change.

## 3 — Make real local image files

1. Generate/obtain an **original unwatermarked** PNG/JPEG/SVG or equivalent. Do not call a watermarked preview a master.
2. Preserve the original unchanged under **design/visual-system/masters/** with a unique, versioned name. Source originals are not optimized sprites.
3. Inspect the actual pixels: bad typography, double borders, cropping, stray silhouettes, transparent edges, inconsistent shade and poor small-scale readability. Generated lettering particularly needs checking.
4. Export independent Android-ready PNG/WebP/vector sprites, preferably alpha for isolated covers/spines. Use a clear naming convention and stable IDs. Put intermediate exports under **design/visual-system/exports/**.
5. Compute SHA-256 from actual bytes, not an invented receipt:

       sha256sum design/visual-system/masters/your-asset.png
       sha256sum design/visual-system/exports/your-asset.png

6. Record source path/hash, export path/hash, pixel dimensions, creator/generator and rights/review status in an appropriate manifest. Never mark **OWNER_APPROVED** or **RELEASE_APPROVED** without explicit evidence.

**Existing WS-14 constraints:** the manual importer **tools/asset_pipeline/import_ws14_cover.py** accepts a **3:4 portrait cover** (300–4096px width; 400–4096px height; ratio tolerance ~0.025) or a **narrow spine** (64–1024px width; 300–4096px height; width/height 0.06–0.40). These limits are for that script, not universal requirements for every future kind of art. Extend the schema/importer explicitly when adding a new asset type.

## 4 — Commit image bytes to GitHub, not just a Markdown plan

**We can upload image binaries with the existing connected GitHub workflow. Do this proactively instead of repeatedly asking the owner to perform the upload.**

At the transfer stage:

1. Open the **actual** local PNG file and verify its SHA-256 and dimensions first.
2. With GitHub write access, use **create_blob with encoding=base64** for raw image bytes. Then **create_tree** with a 100644 blob entry at the exact repository-relative path, **create_commit** using the current branch HEAD as parent, and **update_ref** using expected_sha/fast-forward protection. This transfers real file bytes; a text-only create_file operation is **not** an image upload. A normal authenticated local git add/commit/push is also fine where available.
3. For large files, transfer the actual binary through a supported upload path; keep credentials out of chat and Git. Do not paste megabytes of Base64 into a Markdown file or the user-facing answer. Only claim upload success after GitHub confirms the blob/commit.
4. Re-fetch the branch tree and check **path, size, commit SHA** and, where possible, the downloaded file hash. A GitHub file entry alone is not proof of acceptable art; pixel QA is separate.
5. When transferring new artwork, do not overwrite a previously approved source under the same name/hash. Add a new version/asset ID or a separate branch for comparison.

**What went wrong in the original conversation:** we spent several passes on import scaffolding because an OpenArt integration exposed only watermarked previews, not original bytes. This was a *delivery-tool constraint*, not a reason to avoid all artwork integration. A clean image generated in this chat and written as a real file can be transferred directly to GitHub. The older OpenArt concepts remain references, but they are not the exact pixels in the committed WS-14 QA sheet.

## 5 — Existing automatic WS-14 six-sprite pipeline

This is the **specific proven historical job**, not a general recipe for all future images.

- Input: **design/visual-system/masters/ws14-generated-asset-sheet-original.png**, exactly 1536×1024 and SHA-256 **9c452ba81352b9a3bd08496de5e8cf8d8ea9ed188af540d4db2b2d558c41808e**.
- Extractor: **tools/asset_pipeline/import_ws14_candidate_sheet.py**, with six fixed crop rectangles.
- Import/registry: **tools/asset_pipeline/import_ws14_cover.py**, **validate_ws14_assets.py**, **design/visual-system/ws14-art-register.json**.
- Result: exactly six IDs — BOOK_WOODLAND_COVER_V1, BOOK_WOODLAND_SPINE_V1, BOOK_SPACE_COVER_V1, BOOK_SPACE_SPINE_V1, BOOK_GARDEN_COVER_V1, BOOK_GARDEN_SPINE_V1.
- Android debug assets: **app/src/debug/assets/ws14/books/{theme}.png** and **app/src/debug/assets/ws14/spines/{theme}.png**, built into the APK; these generated files may not be committed on the branch. The **registry in Git is intentionally empty** until a CI/local importer run creates source-backed entries. A zero-entry checkout is not evidence that the successful workflow APK lacked art.
- Source/rights status after import: **QA_INTEGRATED**, **PENDING_REVIEW**. **Never auto-approve**.
- Loader: **BookArt.kt** shows WS-14 artwork in **Study B** when available and falls back to source-backed WS-12 studies; **Study A** preserves original study. Other profile/collection reward art is unaffected.
- Review screen: QA app → **View Library Artwork** → **Review WS-14 assets**. It reports actual bundled 0–6 covers/spines and shows A/B at phone scale.

In a clone with requirements installed, the reproducible source-art checks are:

    python3 -m pip install "Pillow==12.3.0"
    python3 tools/asset_pipeline/import_ws14_candidate_sheet.py
    python3 tools/asset_pipeline/validate_ws14_assets.py
    python3 -m unittest discover -s tools/asset_pipeline -p 'test_ws14_asset_registry.py' -v
    python3 -m unittest discover -s tools/asset_pipeline -p 'test_ws14_cover_import.py' -v

The source sheet hash is pinned in the extractor. **For future variants do not quietly change the constant or crop coordinates to overwrite V1.** Create a named new version, fresh source/hash, dedicated crop mapping, and review assets. Any old file or manifest ID should remain traceable.

## 6 — Build, inspect and test the actual Android package

The two authoritative GitHub workflows are:

- **.github/workflows/ws12g-qa-art-lab.yml**: imports approved portrait references; conditionally imports the pinned WS-14 source when present; validates source/registry and exports; compiles debug and release; examines the actual APK ZIP entries and SHA-256; checks **six WS-14 debug sprites, none in release**; uploads installable QA APK.
- **.github/workflows/ws01-android-qa.yml**: standard source/test/lint/QA package build.

For any new artwork asset family, extend the appropriate tests and **APK ZIP isolation checks** rather than assuming a file simply exists. Run checks against the **exact latest commit** and record links and artifacts. Android test code compiling is not the same as instrumentation execution on an emulator, and emulator testing is not the same as owner phone acceptance. GitHub Actions artifacts expire; store reproducible source, not a permanently promised APK URL.

On-device review after both workflows pass:

1. Back up saved game/profiles first and confirm signing/package compatibility before installing over an existing QA build. Never tell the user to uninstall without verified backup.
2. Compare original Study A vs new Study B on actual physical Bookshelf **and** Play/Collection.
3. Verify small/large fonts, book selection, spines, chapters, progress, portrait unlocks, Daily/Solo/Nearby navigation, no clipped actions and no loss of local saves.
4. Review every actual sprite at small thumbnail size and at close view. Reject warped baked text and decorative elements that pretend to be UI.
5. Record explicit owner yes/no feedback on the **exact screenshot/asset/build**. Keep final production release rights and release-art status separately gated.

**Build failures:** if Gradle/Kotlin dependency download returns HTTP 504 or another external outage, distinguish infrastructure errors from compilation failures, avoid repeated Actions-minute-burning retries, and keep the previous verified APK. The artwork workflow deliberately runs the fast Python asset integrity checks before Gradle setup where feasible.

## 7 — When adding more art later

| New art | Preferred master/export | Android integration |
| --- | --- | --- |
| Another puzzle book theme | Independent cover and spine (both original files) | Add theme/ID, importer mapping, book catalog data where approved, A/B UI, source+APK tests |
| Home mode illustration | Independent art-only vignette, transparent as needed | Existing native StorybookModeCard/StorybookQuickEntry slot; label and button remain Compose |
| Collection icon/portrait set | Consistent individually named transparent portraits | Preserve existing 12 portrait IDs; new versioned IDs, actual unlock/equip states and source receipts |
| Decorative shelf/wood trim | Repeatable/stretchable asset, no text | Layout-specific Compose decoration with phone/large-font QA |
| App icon | Android adaptive icon layer exports | Dedicated icon previews/Android resources and launcher/round crop checks |
| New GridMates puzzle app | New repository/package/art register | Reuse the principles, scripts/test patterns; **do not** copy Word Search state, signing or game logic wholesale |

After every batch, record: source file names + hashes, image-generator/source provenance, original dimensions, export map, asset IDs, PR/branch/head SHA, workflow IDs and conclusions, artifact link if present, what is **not** yet accepted, and the exact next action. Update the active handoff and (if the milestones materially change) this playbook.

## Non-negotiable safeguards

- Preserve known-good Android fallback until exact-head CI plus owner device QA. **v56** was the last owner-confirmed fallback in the WS-14 discussion; re-check actual latest user confirmation.
- Do not alter signed app identity, local game save schemas, LAN protocol or tested portrait IDs as a side effect of art integration.
- Never label concept, automated-build pass or a generated illustration as legally cleared, owner-accepted release art without evidence.
- If a connector cannot transfer a source image, search available supported binary upload routes before making upload the owner's job; if truly blocked, say exactly which tool limit remains.
- Keep original images and evidence in GitHub. Chat context can be lost or read-only; the repository must suffice to reproduce work.

## Related documents and code

- [Active WS-14 new-chat handoff](WS14_ACTIVE_HANDOFF_2026-11-09.md)
- [WS-14 style bible](../design/visual-system/WS14_VISUAL_STYLE_BIBLE_AND_SPRITE_ASSET_CONTRACT_2026-11-08.md)
- [WS-14 full-app visual roadmap](WS14_FULL_APP_VISUAL_IDENTITY_AND_UI_PRODUCTION_ROADMAP_2026-11-08.md)
- [Original art import and on-device handoff](WS14B2_ORIGINAL_ART_IMPORT_AND_NATIVE_BOOKCASE_HANDOFF_2026-11-08.md)
- [Pinned-sheet code and notes](WS14B5_PINNED_SHEET_IMPORT_2026-11-09.md)
- [GitHub draft PR #131](https://github.com/PlayerEden/GridMates-Word-Search/pull/131)
