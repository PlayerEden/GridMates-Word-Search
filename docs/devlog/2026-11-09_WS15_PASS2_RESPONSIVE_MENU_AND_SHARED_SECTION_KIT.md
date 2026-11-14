# November 9, 2026 — WS-15 Pass 2: Responsive Home, clearer puzzle chrome, consistent Collection/Records/Settings assets

> **STATUS: CODE COMMITTED / FULL EXACT-SOURCE ANDROID + ART QA PENDING / OWNER PHONE REVIEW NOT YET DONE.** This is a combined implementation milestone in draft PR #131, not a one-icon preview APK.

## User direction and actual screenshots

The owner approved WS-15 Pass 2 after a real phone test of WS-15 Pass 1. The screenshots showed:
- **Home:** original native Solo/Daily/Together/Books icons match the game, but "03 · TOGETHER" and "Same Wi-Fi" wrap into narrow strips beside full-size action buttons; Books is partly pushed behind bottom navigation.
- **Illustrated swipe library:** current spacious cover shelf, readable book progress and clean back/overflow header are strengths; preserve rather than replace.
- **Book details:** Woodland original owl cover, matching fern emblem and themed chapter art feel consistent; "Reading progress" and "Start reading" sound more like a novel than a puzzle.
- **Game page:** compact Back/Tools/History/Hint 48dp actions and large readable word list are good, but a duplicate small theme illustration in the header truncates "Woodland" to "Woodl...". The motif already appears in the word-list heading. The 20×20 grid is legible but touch/zoom must remain priority.
- **Notices:** screenshot history included multiple "Try selecting a word from the list" entries, and large floating messages have previously covered the upper grid. Meaningful hints/results are the priority, not transient mis-traces.

The screenshots are visual QA evidence, not published in this public repo (device status bar visible). We are not claiming new 20×20 trace, zoom/pan or save-after-reinstall results unless explicitly reported.

## Actual source implementation

### 1. Responsive Home cards
- In `StorybookVisualKit.kt`, `StorybookQuickEntry` now uses an original native **48dp accessible forward-arrow action** at typical phone widths; the label remains available to TalkBack and full-size labeled action buttons stay in wider or very large-font layouts.
- Titles and section eyebrows have overflow limits, but the intended standard 320–360dp layout now has **more text width** rather than solving the issue by silently truncating long words.
- New `Ws15ForwardActionButton` in `Ws15VisualKit.kt` is a real tap target, not decorative art. Solo remains a clear Continue/New puzzle flow. Daily, Together and Books retain exact destination callbacks; no navigation rerouting.
- Existing Home Android tests adapted for icon-accessible actions, and added `Ws15P2ResponsiveMenuAndroidTest.kt` which asserts complete Together/ Wi-Fi labeling and a functional action on a 320dp test width.

### 2. Gameplay header, notices and safety
- Removed only the **duplicate theme decoration from the top 4-action gameplay header**. The corresponding original WS-14K2 theme sprite remains clearly visible in the word-list heading. The book title regains width while Back, Tools, Notice History and Hint keep their native 48dp tap targets.
- Changed an invalid trace result from misleading `Try selecting a word from the list.` to `No matching word in that selection.`.
- `shouldRememberBookPageNotice()` ensures invalid mis-traces and already-found mistakes **do not pollute** the bounded in-session notification history. Hints, found words and important save/recovery messages remain in that list; the existing durable hint/claim/save algorithm and schema are untouched.
- Brief routine notices are shorter (~1.2–2.6s); hints show ~4s, recovery/save issues ~6.5s. The floating `BookPageNoticeOverlay` is a **compact 2-line maximum banner anchored at the bottom** of the book page instead of covering progress/top rows of letters. It still reserves **zero permanent height**. No new gesture overlay over the grid.
- Pure-policy unit test `Ws15P2NoticeHistoryPolicyTest.kt` verifies hints/found/error eligibility and low-priority exclusions. The historical WS-14G header emblem helper remains for prior layout regression reference, but new gameplay intentionally avoids redundant header art.

### 3. Book detail wording and native icon library
- Book detail now reads **Book progress**, **Start solving**, **Continue solving**. This preserves the illustrated-book concept while describing actual puzzle actions.
- Android book-flow tests were updated to look for the new player-facing labels rather than old words, without changing progression or bookmarks.
- New reusable source-authorable **`Ws15P2SectionVisuals.kt`** contains seven matching Kotlin Canvas glyphs: Portrait, Typeface, Award, Journal, Backup, Preferences and Artwork. They are standalone vectors, decorative and screen-reader hidden; each associated section keeps its live native text. No dependency on an AI-generated screenshot as a playable asset.
- `CollectionScreen.kt` uses these for Woodland companions, Board fonts and Achievements with concise unlocked counts, preserving existing real portraits, pagers, equip actions and font licenses.
- `SoloRecordsScreen.kt` uses the Journal marker for results and applies the common paperback card surface/border to real statistics. Saved records and pagination unchanged.
- `SoloSettingsScreen.kt` adds matching section labels for Backup, Gameplay preferences, About and QA artwork, while condensing duplicate/outdated upgrade copy into accurate save-backup/signature guidance. File export, import, confirmation and privacy semantics unchanged.

## Commit and quality trail

- Source commits:
  - `ca70d95594159ffec98d9a8111bddf94a3afac90`: responsive Home + 48dp forward arrow, adapted home/book launcher tests
  - `ad105e63f5e7a297d5e630143d4b1ea497562213`: book/gameplay notices, full header, word-list motif preservation, book terminology, test updates
  - `265d4e9a5393d8c6c7931d4d37dc32363a37e599`: new seven-glyph reusable section visual kit
  - `144267b65022f27bcc087a558ffaf78b1538fb74`: real Collection/Records/Settings integration
  - `359f27f363169b33ec70132c9b1a7100756e6c73`: 320dp source-compiled Android UI regression test
- **Exact latest code source for QA:** `359f27f363169b33ec70132c9b1a7100756e6c73`.
- Source-matched [Android QA #42691135246](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42691135246) and [art/release isolation #42691135427](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42691135427) were **pending** at journal creation. Early superseded runs for intermediate commit hashes were cancelled by CI concurrency; do not interpret cancellation as failure or pass.
- **Do not claim a new APK yet** until both workflows finish SUCCESS on `359f27f`, with an actual APK artifact, package metadata and checksum. Then hand the owner one *consolidated* WS-15 Pass 2 test build; avoid one-asset APK spam.
- **Last verified Android fallback:** WS-15 Pass 1 source `80636c85c0bcf26696d2828414ada1cf02d94860`, both [Android #42687644991](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42687644991) and [Art #42687645295](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42687645295) SUCCESS; [QA ZIP #13704788048](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42687644991/artifacts/13704788048), Android APK SHA-256 `7c351af05a648548e7ca588a46e9a424f11c6f5465a01b7672ce807fd2247e1c`.

## Next owner phone test and pass

- **One WS-15 Pass 2 Android test only, after exact-source dual-green:** Home Daily/Together/Books should present complete words without awkward wrapping; try each right-arrow action and verify navigation.
- Open the real Woodland/Space/Garden details; verify Book progress / Start solving, same owl/planet/flower art, chapter markers and bookmarks.
- In the 12×12 and 20×20 pages, full book title should be visible in usual phone portrait mode and theme motifs should still show near the word list. Confirm puzzle tools, pinch/precise select, notice history and saved progress remain functional.
- Check Collection Portraits/Fonts/Achievements, Records and Settings for cohesive small native symbol headings, at ordinary and enlarged font if feasible. Verify backup export remains accessible.
- Record issues by user-visible behavior, not by aesthetic guesses; refine together in a meaningful WS-15 Pass 3 only if needed. Keep PR #131 draft/unmerged until owner acceptance.

## First source CI outcome + focused correction

- [Android #42691135246](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42691135246) for source `359f27f` **FAILED** at `:app:compileDebugAndroidTestKotlin` because `QaBookshelfLauncherAndroidTest.kt:21` newly called `onNodeWithContentDescription` without importing its Compose UI test extension. The exact job log identifies **only this test import issue**; it does not prove gameplay failure.
- [Art/debug-release #42691135427](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42691135427) at `359f27f` **SUCCEEDED**. The three original theme source/export hashes, app builds and release art isolation remained intact.
- **Exact focused correction:** [`6d7e53e2c6b7f51b16f517f802505bc5c6635768`](https://github.com/PlayerEden/GridMates-Word-Search/commit/6d7e53e2c6b7f51b16f517f802505bc5c6635768) adds only `import androidx.compose.ui.test.onNodeWithContentDescription` to the changed Android UI test.
- Corrected same-source workflows: [Android #42692044545](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42692044545) and [Art #42692044285](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42692044285), **in progress** at observation. No new APK or dual-green claim until both finish successfully. No user saves, word selection or UI appearance modified in the fix.

## FINAL WS-15 Pass 2 CI and APK receipt — dual green

**Verified on the *same exact gameplay source* `6d7e53e2c6b7f51b16f517f802505bc5c6635768` (the one-line Android test import correction):**
- [GridMates Word Search Android QA run #42692044545](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42692044545) — **SUCCESS**. Actual core/app unit tests, lint, Compose Android test **source compilation** (not physical emulator execution), APK generation, package metadata and debug artwork SHA verification passed.
- [WS-12G QA Art Lab and Debug/Release Isolation run #42692044285](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42692044285) — **SUCCESS**. Debug and release packages built. Verified all original WS-14 cover/spine candidates, 18 original + 18 alternate SVG/Png source arrays, WS-14J candidate and exact three WS-14K2 transparent page motifs present in QA where expected, **none of these unapproved art assets in release**.
- **One user QA ZIP artifact:** [#13707642014](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42692044545/artifacts/13707642014), named `GridMates-WordSearch-Woodland-Verified-QA` (historic name, contains **all themes and WS-15 Pass 2 UI**). Extract `app-debug.apk`.
- **Actual Android APK SHA-256**, recorded by the exact-source build: `e3da26892db7638ef8c95bbaeb9214076cfc3852286036acde884719221ec8ea`.
- **Artifact ZIP SHA-256** recorded by Actions upload: `07e22fba70bf5865ac223eabff6332f99c6a81782dbb86764f1dea7436f7c097`.
- **Package ID** `com.playereden.gridmates.wordsearch.qa`; **versionCode 57**, **versionName 0.9.13-visual-kit-foundation-qa**. VersionCode and versionName remain the same as preceding QA builds, so **do not imply automatic higher versioning or signing compatibility**. Export/verify local backup before considering uninstall if Android reports signature mismatch.
- Separate art workflow artifact exists but **do not distribute two competing installation ZIPs**. Prefer the standard Android QA ZIP link above.
- All these checks are actual CI/build results; **new visual appearance, 20×20 finger/zoom ergonomics, live notifications and book/Collection navigation still need owner physical phone review**. Retain draft PR #131, no automatic merge/release art approval.
- **Latest owner test request:** one consolidated screenshot group — Home Daily/Together/Books label widths, Woodland/Space book and 12/20 puzzle title + notice, Collection (Portraits/Fonts/Achievements), Records and Settings backup. Report whether actions actually navigate, the history still reopens, and 20×20 tap/zoom remains easy.
