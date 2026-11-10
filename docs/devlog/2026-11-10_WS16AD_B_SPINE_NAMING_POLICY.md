# WS16AD-B — Repeatable Book Naming and Spine Display Profile (November 10, 2026)

> **Status:** SOURCE POLICY + MANIFEST INTEGRATION, test/Android/art QA **PENDING** on new commit; no owner device art acceptance. **Not** a newly generated painted book or corrected trademark.

## Who, what, where, why
- **Owner request:** Future themes preferably have short names. A name such as **Woodland** may deserve a slightly taller library spine, but longer titles must stay readable and the publisher/book geometry must remain cohesive. The Full Library's actual height-variation experiment can come later.
- **Implemented:** Editable GitHub metadata policy `tools/asset_pipeline/book_spine_policy.py`, new unit tests `test_ws16ad_spine_policy.py`, and metadata generation in `build_ws16ad_book_factory.py`. The two existing workflows run the new tests. No application Compose layout, sprite source or gameplay code changed in this pass.
- **Why:** Future series expansion should have predictable decisions and hash-pinned source geometry rather than one-off visual distortion for every theme.
- **Branch/PR:** `feature/ws14b-style-anchor-and-art-direction-2026-11-08`, [draft PR #131](https://github.com/PlayerEden/GridMates-Word-Search/pull/131).
- **Starting checkpoint:** `ac8ec4d5ff263d85fcaa04adccfb36ba55334386` (post-WS16AD stage-1 log), with stage-1 compiled source `6e875255b38f0d03519c14aabe9e15d02b66c1d2` dual-green: [Android #42913471215](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42913471215), [art #42913471182](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42913471182). [Last exact-source APK ZIP #13808268305](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42913471215/artifacts/13808268305), APK SHA256 `790a964f7cd31f9f1d4c3cbbe8877b0436b231ecb545d94598f1c031e2722add`.
- **Fallback:** All WS16AD stage1 assets and older WS14 originals retained, approved-direction WS16AB shelf untouched. Do not uninstall/clear app data to test.

## Exact policy
- One-word names of 1–7 letters -> `standard`; 8–11 -> `tall`; longer/multiword -> `editorial-review`. Owner preference remains a *recommendation*, ideally 4–8 letters, **not** a migration or change to existing book IDs.
- Woodland (8 letters) is tall; Space (5) and Garden (6) are standard. Other titles like Halloween/Underwater are tall; Northern Lights requires designer review.
- The **source spine remains 128×640** regardless of class. Future *proportional, not stretched* library heights 140dp and 148dp (nominal widths 28dp and 29.6dp). Current Android UI retains its 48dp (54dp for large type) distinct tap slot and existing visible 148dp spine for all books; **no variation applied yet**.
- The source manifest records a `spine_name_profile` on each of the 3 theme spine entries. All 6 placeholder cover/spines remain non-playable with no name-derived profile or fake catalog ID.
- Registered/unsupported trademark marks throw a validation error **for proposed future names**; the old painted ® in existing art is explicitly **not fixed** by this code.
- [Detailed specification](../design/WS16AD_B_SPINE_NAMING_AND_PRESENTATION_POLICY_2026-11-10.md) and [book factory contract](../WS16AD_BOOK_FACTORY_CONTRACT.md).

## CI/build receipt (fill only with actual observed results)
- **Source commit:** pending new atomic GitHub tree commit.
- **Local proof:** not yet independently executed; unit test files have been written and CI wired.
- **Standard Android workflow:** not yet verified.
- **Independent artwork/release workflow:** not yet verified.
- **New APK/artifact:** none claimed in this pass.
- **What is untouched:** same 12 sprite output pixel sources, no gameplay/save/catalog changes, no source art overwrite or production-art approval. Original approval gates still open.

## Next exact action
Verify new source hash, standard Android and art/release-isolation results, confirm all 12 PNG export SHA digests are unchanged vs stage 1, then continue with actual **Woodland V2 corrected book master** (no ® and no white alpha fringe), followed by Space and Garden. Later bookcase A/B can exercise standard/tall display heights and comfortable tap targets.

**Handoff:** Read this entry, the updated book contract, and the latest workflows; do not infer that a naming rule changes visible art or that a CI-green candidate is phone-approved.
