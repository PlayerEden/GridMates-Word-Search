# 2026-11-09 — WS-14H source-matched green Android and artwork QA; next phone acceptance

**Result:** **BOTH WORKFLOWS SUCCESS** on exact code source `7856282d07cc440a4365132b8854f13a6fd57d1c`. Debug APK compiled, unit tests/lint/Android instrumentation *compilation* succeeded, six WS-14 source-matched illustrated book sprites verified in QA, and QA artwork isolation from release passed. **Not yet physically reviewed on a phone.** This pass documents verified source/build state; no further user-facing visual redesign or new images.

## Who / what / where / why

- **Owner instruction:** "Let's continue" after approving the bulb-only Hint, removal of filler instructions, subtle per-book paper tint, with the board reserved for independent future unlockable skins.
- **Assistant:** checked the actual live GitHub PR and both workflow conclusions, verified the artifact id, APK hash, debug package id, six-artwork validation and release isolation. No more code changes this pass.
- **Where:** [Draft PR #131](https://github.com/PlayerEden/GridMates-Word-Search/pull/131); prior [WS-14H implementation log](2026-11-09_WS14H_LIGHTBULB_HINT_AND_PAGE_THEME_IMPLEMENTATION.md); [complete roadmap](../WS14H_COMPACT_GAMEPLAY_HEADER_AND_THEME_ROADMAP_2026-11-09.md).
- **Why:** Establish a factual, downloadable checkpoint so the next step is an actual device comparison rather than repeated build work or making more artwork prematurely.

## Exact-source automated QA

- Code commit: `7856282d07cc440a4365132b8854f13a6fd57d1c` on branch `feature/ws14b-style-anchor-and-art-direction-2026-11-08`. Newer docs-only commits may move HEAD without changing this executable source; verify diff before assuming a newer build.
- **Standard Android:** [run #42658174525](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42658174525) **SUCCESS**. Core/app unit tests, lint, Android instrumentation-test *compilation*, APK assembly, QA package and hash check, all six source-hash-matched WS-14 book cover/spine PNGs inside actual APK.
- **Art debug/release isolation:** [run #42658174462](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42658174462) **SUCCESS**. Both Android debug and release compilation; all six WS-14 book sprites in debug, zero in release. Existing 18 original + 18 Field Notes WS-12 SVGs and 36 matched native PNGs present in QA, zero prototypes in release.
- **GitHub Actions QA artifact:** [artifact #13693073000](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42658174525/artifacts/13693073000), name `GridMates-WordSearch-Woodland-Verified-QA`, ZIP **13,749,764 bytes**, status not expired at time of inspection. Extract **`app-debug.apk`**; Actions artifacts may require GitHub login and can expire.
- **Debug APK identity:** `com.playereden.gridmates.wordsearch.qa`, versionCode `57`, versionName `0.9.13-visual-kit-foundation-qa`. APK SHA-256 from GitHub build log: **`410716124b8c4ef35b2a934c097faf49aab51efb5a6fa425ff438c995220cac8`**. Uploaded standard artifact ZIP SHA-256 from log: `abcf32fcf0a6548a84b4b822946c69b4e8c0cd946298249ba093d12ebf2c92b1`.
- **Important:** QA APK signature/identity is not a Google Play release. The art workflow's similar ZIP is another verified **source-matched** QA artifact [#13692528378](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42658174462/artifacts/13692528378), but use **standard** artifact above as the one owner download target.

## Final state vs previous blockers

- Previous WS-14G emblem source had a real Kotlin `maxWidth` receiver error; corrected by capturing outer constraints before Row.
- First WS-14H source had `PathJoin` instead of Compose `StrokeJoin` for Canvas bulb outline; corrected. **Both source defects are cleared by the above compiled green source.**
- Dynamic hint logic and durable hints remain unchanged, but **physical hint taps were not tested** by CI; code-level Android tests compiled but no emulator/device run was claimed.
- Existing owner-tested WS-14D/F illustrated APK and v56 broad fallback remain; no merge to main, no new sprite masters, no on-device save migration proof.

## One focused owner phone acceptance pass before WS-14I artwork

1. **Install safely:** Download [GitHub artifact #13693073000](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42658174525/artifacts/13693073000), extract `app-debug.apk`, save/backup prior data and install-over only if package/signing are compatible. **Do not uninstall/clear local data just to fix a download link.**
2. **Look:** Compare Space/Waterland/Garden page headers: small theme emblem, page title and outlined 48dp bulb; notice that the huge old Hint button and generic trace instruction are gone and more vertical room is available. Woodland/S P A C E/Garden colors should be a very *subtle* paper tint only outside letter grid. Check 12×12 and 20×20 fit and word list.
3. **Act:** Tap bulb on an editable puzzle and verify the existing gradual hint feedback, test `Select`, +/−, Fit and pinch. **Actually trace and find a word** on the 20×20 board; then back/reopen to check it remains found. Check bookmarks and previous page state.
4. **Accessibility:** portrait + landscape, if possible at larger system text, make sure bulb remains accessible and title does not overlap. Share screenshots and observations, including any clipping or unexpectedly strong tint.
5. **Gate:** Only after this test confirms function and appearance mark compact gameplay header **Layout v1 approved**. Then start the planned **three cohesive small transparent gameplay art accents** (one per book theme), with versioned binaries/provenance/hashes, QA-only import and new on-phone side-by-side comparison. The word board stays plain and future unlockable board looks are separate.

**New-chat instruction:** Read AGENTS.md, docs/DEVELOPMENT_JOURNAL.md, this receipt, docs/WS14_ACTIVE_HANDOFF_2026-11-09.md and the full WS-14H roadmap. Start from verified APK / physical feedback, not another unfocused artwork pass. Every follow-up pass gets a dated indexed log.
