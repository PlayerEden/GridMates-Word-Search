# WS-14D — Swipe Browser Android QA Build Receipt — 2026-11-09

**Result: Android QA SUCCESS on exact source `a26fe8f20d5dde595d73937d43da1e813946a778`; separate art release-isolation job FAILED on external Kotlin compiler HTTP 504. Physical phone test of the swipe UI remains OPEN.**

## Who, what, where, why

Owner approved beginning a grounded, subtly animated horizontal library experiment, after real illustrated APK screenshots revealed detached spines and excessive vertical travel. Assistant implemented and tested a native horizontal pager in draft [PR #131](https://github.com/PlayerEden/GridMates-Word-Search/pull/131), using existing Woodland/Space/Garden cover art. GitHub Actions compiled and packaged Android test build. This test determines whether swipe navigation is preferable to classic shelves **before creating more sprites**.

## Where we came from and what changed

Previous [feature pass](2026-11-09_WS14D_HORIZONTAL_BOOKSHELF_IMPLEMENTATION.md) implemented `HorizontalBookBrowserScreen.kt`, in-memory Classic/Swipe comparison via `BookLibraryFlow.kt`, focused native book title/progress/Open CTA, 220ms selection scale, accessible arrows, and removing the detached spine from book detail. [CI repair pass](2026-11-09_WS14D_GRADLE_504_CI_REPAIR.md) installed SHA-pinned Gradle 8.11.1 through verified fallback endpoints following official distribution HTTP 504. The first actual compile caught an invalid top-level `assertExists` import in new instrumentation test; removed in source commit **`a26fe8f20d5dde595d73937d43da1e813946a778`**. No game saves, book IDs, player progress, 12 Woodland profile icons, LAN logic or release signing were changed.

## Exact build evidence

- **Source commit:** `a26fe8f20d5dde595d73937d43da1e813946a778` on `feature/ws14b-style-anchor-and-art-direction-2026-11-08`; current doc-only commits may be ahead of this executable source.
- **Android QA:** [GitHub Actions run #42584167358](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42584167358), **SUCCESS**: original Woodland portrait and font import; exact six WS-14 cover/spine PNG imports; SHA-256 verified Gradle setup; core/app unit tests, lint, Android instrumentation-test **compilation** and debug APK assembly; actual APK verification of six PNG source hashes and .qa package.
- **Android APK artifact:** [#13657879885](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42584167358/artifacts/13657879885), ZIP containing `app-debug.apk`; artifact links expire.
- **Independent downloaded APK receipt:** **14,169,250 bytes**, SHA-256 `ef24eb84327148b1e58480cc60814d7e4541aeb9260fc77c9ebf3d5c404dec1d`. APK ZIP was inspected and includes `assets/ws14/books/{garden,space,woodland}.png`, `assets/ws14/spines/{garden,space,woodland}.png`, and the 12 preexisting Woodland portraits. `com.playereden.gridmates.wordsearch.qa`, v57 code default, publicly signed QA-only test build.
- **Art/release isolation:** [#42584167412](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42584167412) failed **before source compile**, resolving `kotlin-compiler-embeddable:2.1.20`: Maven redirected to JetBrains GitHub release returned **HTTP 504**. **Do not call release-isolation passed** for this new commit. Previous art-isolation run passed only for older source.
- **Not yet completed:** executing the new instrumentation tests on an emulator, physical handset swipe/large-font testing, full save-install migration, two-phone testing, release approval, or a production merge. Keep old v56 wide fallback and the previous visually tested illustrated QA build.

## Immediate next action for owner/new chat

Download [GitHub artifact #13657879885](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42584167358/artifacts/13657879885) (ZIP), extract `app-debug.apk`. Back up saves; try install-over only when QA package/signing compatible; don't uninstall merely to fix a broken ChatGPT file link. In Play → Test Puzzle Books, evaluate **Swipe library** (default), next/previous arrows, cover lift and Open/Continue action, then toggle **Classic shelves** and **Original art (A)** / **Illustrated art (B)** separately. Open/return from Woodland/Space/Garden and check progress/bookmarks. Share phone screenshots. Determine whether to adopt or revise this navigation before expanding artwork. After feedback, add another dated journal entry. Do not confuse Android QA source compilation with owner acceptance.

**New chat:** Read `AGENTS.md`, `docs/DEVELOPMENT_JOURNAL.md`, this receipt and `docs/WS14_ACTIVE_HANDOFF_2026-11-09.md`; inspect live PR and CI status, then continue from owner swipe-library review.
