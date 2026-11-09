# 2026-11-09 — WS-14C-LOG: APK link recovery and permanent development journal

**Status:** **APK artifact verified; download/install issue reported by owner; owner on-device QA still OPEN.** Repository logging policy and reproducible handoff are being established in this documentation-only pass.

## Who, what, where, why

- **Owner feedback:** After receiving the WS-14 illustrated QA APK through a ChatGPT sandbox link, the owner reported **"Library file not found."** They also requested a mandatory GitHub development record after **every pass and build** explaining who/what/where/why, what preceded it, what we were building toward and how a new chat can continue.
- **Work performed:** Assistant validated the APK already stored in the working environment, recovered the independent **GitHub Actions artifact** location, and created the journal/index/template/agent instructions. GitHub Actions originally built the APK. The owner has **not** yet installed or visually reviewed the illustrated app.
- **Goal:** Let the owner test six actual book-art sprites in the real Android game, and ensure future chats can reconstruct the project without depending on an exhausted transcript.
- **Where:** GridMates Word Search; PR [#131](https://github.com/PlayerEden/GridMates-Word-Search/pull/131), stacked on #130; **Play → Test Puzzle Books**, and **Play → View Library Artwork → Review WS-14 assets**.

## Where this came from

1. The owner liked Woodland owl, Space ringed planet and Garden flower illustration concepts, but original OpenArt downloads were watermarked previews.
2. The team instead created a **different unwatermarked six-sprite QA candidate sheet**, transferred the original binary source directly into GitHub and established the same SHA-256/local PNG model as the existing Woodland profile portrait collections.
3. At exact source commit [00d2a36](https://github.com/PlayerEden/GridMates-Word-Search/commit/00d2a36d12c0410a5f04b780ff8c3fe83f69c841), both [standard Android QA #42576785096](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42576785096) and [art/debug-release isolation #42576785170](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42576785170) **passed**.
4. The artwork run produced an Android package with six `assets/ws14/` PNGs (Woodland/Space/Garden covers and spines), none in the release APK. The **original art registry is generated during CI**, so the empty source checkout register is expected.
5. The assistant linked a direct ChatGPT `sandbox:` APK download, but the owner got **"Library file not found."** This error is **not proof the APK is corrupt**; its UI/link-delivery cause is not confirmed. A GitHub-hosted download is the better alternative.

## Exact APK and independently checked evidence

- **GitHub source artifact link:** [WS-14 art QA APK artifact #13656070327](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42576785170/artifacts/13656070327) (download ZIP, extract `app-debug.apk`). This is the **artwork workflow** APK; the general Android QA artifact may lack WS-14 imported images.
- **Locally verified extracted APK:** `GridMates-WordSearch-WS14-Illustrated-QA.apk`, **14,152,866 bytes**, SHA-256 `8a6c45ab4f879d3f7c6d5c5ba5c010b3b6d72ce2c32c8b4ebf34792506c45e0a`. The six exact paths were observed in its APK ZIP:
  - `assets/ws14/books/woodland.png`, `space.png`, `garden.png`
  - `assets/ws14/spines/woodland.png`, `space.png`, `garden.png`
- **QA app identity:** `com.playereden.gridmates.wordsearch.qa`, version code 57 (0.9.13 visual-kit QA), public test-only signer. A safe in-place install over every historical QA build is **not assumed**; save export before changing builds.
- **Prior fully documented source/build:** `00d2a36`; **GitHub documentation branch** was at `d88f5443` at start of this pass. Docs-only changes are not themselves evidence of a newly compiled APK.
- **Last owner-verified handset fallback:** **v56** from prior WS-14 work. No evidence that this newer APK has been successfully installed on the owner's phone yet.
- **Library error:** user-visible message while opening ChatGPT library/sandbox link. Check via GitHub artifact and browser; don't recommend uninstall/clear data to fix a download problem.

## What this pass changes

- Adds an [append-only development journal index](../DEVELOPMENT_JOURNAL.md) and [reusable pass template](ENTRY_TEMPLATE.md).
- Makes pass logging compulsory through root `AGENTS.md`, README, START_HERE and the active WS-14 handoff.
- Requires **per-pass and per-build** entries with origin, objective, files/assets, exact source SHA, tests, artifact, outcome, remaining blocker, fallback and one concrete next action.
- No source art, Android code, local profile IDs, saves, app signing or LAN modes are modified in this documentation pass. The existing code-tested artifact remains the recommended visual candidate.

## Next action and long-term direction

1. **Immediate:** open the [GitHub-hosted artifact](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42576785170/artifacts/13656070327) in a browser, download ZIP and extract `app-debug.apk`. Back up Word Search save data; install over compatible QA build if Android permits. If the download itself is blocked, record the exact browser/app message before touching app data.
2. **On phone:** Test **Play → Test Puzzle Books**, default **Study B** vs **Show original art (A)**; then **View Library Artwork → Review WS-14 assets**, where installed artwork should report **6 of 6**. Inspect gameplay, book details and Collection. Share screenshots.
3. **Decide:** Are colors/lighting/shapes unified? Does embedded cover lettering clash with native book labels? Are spines too small and is the shelf readable? Choose *refine the current six* vs *new standalone illustrated art* vs *broader visual rethinking* based on actual device evidence.
4. **After owner feedback:** create a **new journal entry** with accepted/rejected assets, version new sources, upload binary originals using the documented GitHub process, run matched CI, update rollback and journal.

**New-chat one-liner:** Read `AGENTS.md`, `docs/DEVELOPMENT_JOURNAL.md`, this entry and `docs/WS14_ACTIVE_HANDOFF_2026-11-09.md`, inspect live PR #131 and CI. **Next task: unblock owner APK download and review six local sprites in actual Android UI.**
