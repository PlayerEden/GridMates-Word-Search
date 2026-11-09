# Contributor and Agent Instructions — GridMates: Word Search

This is **GridMates: Word Search**, a separate offline-first Android puzzle game from **SudokuTogether**. Preserve its own repository, package, saves, signing and LAN state. Do not treat the default branch or a historical chat summary as the latest development source.

## Always read before working

- **docs/START_HERE.md** — general coding/gameplay continuity (some old blocks are historical; compare with live GitHub).
- **docs/WS14_ACTIVE_HANDOFF_2026-11-09.md** — current visual-project checkpoint and how to restart in a new chat.
- **docs/ASSET_CREATION_GITHUB_INTEGRATION_PLAYBOOK.md** — reproducible image production, binary GitHub upload, import, QA and approval gates.
- **docs/WS14_FULL_APP_VISUAL_IDENTITY_AND_UI_PRODUCTION_ROADMAP_2026-11-08.md** and **design/visual-system/WS14_VISUAL_STYLE_BIBLE_AND_SPRITE_ASSET_CONTRACT_2026-11-08.md** — current art design standards.

Inspect the live repo HEAD, draft PRs and GitHub Actions first. The WS-14 snapshot is PR #131 stacked on #130; its exact known successful import/build evidence is commit 00d2a36 with workflows 42576785170 and 42576785096. Future work may supersede those IDs.

## Artwork integration rules

1. **Build actual game assets, not static concept screenshots.** Preserve original source images, SHA-256 and provenance; derive independent PNG/vector sprites, with native Compose controls/text.
2. **Use the profile-icon precedent:** actual local PNGs, hash-pinned source and repeatable Android import. For new art, transfer binary files through supported GitHub blob/tree/commit/ref or authenticated git workflows; do not insist the owner manually upload if you can commit the file.
3. **Don't mutate pinned V1 art silently.** New artwork gets versioned asset IDs, source paths, manifest entries, checks and visual review.
4. **QA vs release separation is mandatory.** The six WS-14 book covers/spines from the pinned sheet were verified **in the debug APK, absent from the release APK**. Do not mark generated content as rights-cleared or owner release-approved from a successful test.
5. Keep the original 12 Woodland profile icons and unlock/equip IDs intact. Preserve Solo/Daily, Nearby, book catalog/pages and all existing save data.
6. CI success is different from real handset QA. Keep v56 as the last owner-verified physical fallback until the owner confirms a later one. Do not merge draft stacks or remove app data automatically.

## Before ending any work session

Update **docs/WS14_ACTIVE_HANDOFF_2026-11-09.md** with latest exact PR/head SHA, changed code/assets, artifact/workflow runs and outcome, blocked gates, current verified fallback and one concrete next task. Cite durable repo sources, not promises of future background work. Avoid wasting GitHub Actions minutes by repetitively retrying external Gradle HTTP 504 failures.
