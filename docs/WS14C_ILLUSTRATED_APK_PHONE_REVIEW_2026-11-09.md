# WS-14C — Illustrated Word Search APK / Owner Phone Acceptance

**Checkpoint date:** November 9, 2026

**Purpose:** Test the newly generated **real Android art assets inside the actual game** before deciding whether to create more art, refine the style, or rethink the illustrated bookshelf design.

## Exactly which build to test

- **Source code + original art commit:** [00d2a36d12c0410a5f04b780ff8c3fe83f69c841](https://github.com/PlayerEden/GridMates-Word-Search/commit/00d2a36d12c0410a5f04b780ff8c3fe83f69c841)
- **Illustrated Android APK (actual Art Lab build) and artifact:** [WS-12G artwork build #42576785170](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42576785170) → [QA artifact #13656070327](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42576785170/artifacts/13656070327). The artifact ZIP contains **app-debug.apk**. GitHub artifacts expire; if gone, rebuild from the pinned source and pipeline.
- **Standard Android QA:** [run #42576785096](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42576785096) passed.
- **Art import + APK ZIP audit:** [run #42576785170](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42576785170) passed. All six sprite entries were hash-checked inside the **debug APK**, with zero WS-14 assets in release.
- **APK SHA-256:** \`8a6c45ab4f879d3f7c6d5c5ba5c010b3b6d72ce2c32c8b4ebf34792506c45e0a\` (extracted from artifact #13656070327, \`app-debug.apk\`); size **14,152,866 bytes**. This is a checksum for **this exact APK** only, not all future v57 builds.
- **App:** \`com.playereden.gridmates.wordsearch.qa\`; \`versionCode=57\`, \`versionName=0.9.13-visual-kit-foundation-qa\`; public test-only signing. App installation compatibility still depends on previous signing/version; **back up saves** before updating.
- **Current draft branch:** [PR #131](https://github.com/PlayerEden/GridMates-Word-Search/pull/131) stacked on #130; current documentation commits may be ahead of the APK source without changing binary functionality. Keep both PRs draft while assessing art.

## What is actually inside the APK

Image assets are offline/local, not remote URLs:

- \`assets/ws14/books/woodland.png\`
- \`assets/ws14/books/space.png\`
- \`assets/ws14/books/garden.png\`
- \`assets/ws14/spines/woodland.png\`
- \`assets/ws14/spines/space.png\`
- \`assets/ws14/spines/garden.png\`

The source is a newly ChatGPT-generated unwatermarked art sheet in \`design/visual-system/masters/ws14-generated-asset-sheet-original.png\`. It is **not** the earlier OpenArt illustration. The candidate cover artwork **already has text printed on it**, so it may visually duplicate native Android book titles; this is exactly what the current acceptance test should identify. It is QA art, **not** commercially cleared/release-approved final art.

## On-phone test route

1. Export a verified backup of existing Word Search QA saves/profiles. **Do not uninstall** an older APK just for testing; first try the normal Android install-over upgrade. If Android rejects it due to a signing/version mismatch, stop and preserve current data.
2. Install the **illustrated** QA \`app-debug.apk\` from the **artwork** workflow, not the smaller standard Android QA artifact; the latter may not include six WS-14 image resources because the import happens in the dedicated art pipeline.
3. Open Word Search **Play → Test Puzzle Books**. The Bookcase opens with **Study B** selected by default. Confirm it reports "WS-14 covers · Field Notes supporting art" and shows **three actual illustrated standing covers** with three accompanying spines on wooden shelves.
4. Tap **Show original art (A)**, then **Try WS-14 covers (B)**. Compare aesthetics, book proportions and tap targets. Inspect the individual book details and an actual puzzle page; verify native content, opening a book, completing/continuing a page, bookmarks/progress and Back still work.
5. Return to Play → **View Library Artwork → Review WS-14 assets**. The screen should say **"6 of 6 WS-14 cover and spine assets bundled for QA."** Compare original A vs illustrated B for Woodland, Space and Garden. This is a diagnostic screen and is not itself the final polished production UI.
6. Inspect **Solo, Daily, nearby options, Collections and profile icons** to ensure none were corrupted by image integration; the new art is primarily in Puzzle Books and the Art Lab, **not yet a full-screen Play/Collection redesign**.
7. If possible, check narrow portrait, landscape and increased font size. Record clipped text, overly crowded shelves, blurry art, illegible labels, duplication of baked-in artwork titles, slow scrolling, bad touch hitboxes or loss of saves.

## Answer these questions after testing

| Decision | What to look for |
| --- | --- |
| **Style** | Does the warm, detailed illustrated book style feel professional, cozy, appealing and aligned with the GridMates identity? |
| **Cohesion** | Do Woodland, Space and Garden genuinely feel like the same collection at real phone size? |
| **Readability** | Is the decorative type legible? Does baked text fight with native Android labels/progress? |
| **Bookshelf design** | Should the books be larger, spines narrower, shelving less decorated, or cards replace the standing-book shelf? |
| **Interaction** | Can you find/open/continue a book, switch art A/B and play without confusion? |
| **Next art priorities** | Cover refresh? New independently illustrated spines? Native Play/Home vignettes, Collection decor, button motifs, more puzzle book themes? |

**Send screenshots of Play, Puzzle Books (Study B), one opened book, and Collection.** The development chat should interpret those images as actual phone evidence, not just stylistic preference from rendered concept art.

## Acceptance gate

- **PASS right now:** actual original file transferred to GitHub; 6 PNG exports generated; 6 hashed assets bundled into debug Android APK; separate Android QA and release-isolation workflows green.
- **NOT YET PASSED:** owner installing and reviewing exact APK on phone; accessibility and lived interaction; exact art sign-off; final production rights/release checks; actual two-phone testing.
- **Fallback:** v56 was the last owner-verified Android visual build in this arc. Don't remove it or claim production readiness.

After review, record the actual owner decision **per screen and asset** in \`docs/WS14_ACTIVE_HANDOFF_2026-11-09.md\`, and build a new versioned art package only for changes they approve. Preserve the original input source SHA and do not silently overwrite candidate V1.
