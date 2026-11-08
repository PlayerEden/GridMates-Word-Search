# WS-11E2 — First Owner Screenshot Review and Compact Book-Word List

**2026-11-08 · Review of first owner Android screenshots from WS-11E QA v49.**

## Observed on the owner's device (eight screenshots)

- Home displays Solo, Daily, Nearby and a test book entry underneath; original Collection, My Puzzles and unlocked portrait flow are intact at a glance.
- QA Bookshelf shows all three test books with readable progress and actions, Woodland Book detail shows chapter progress and three selectable pages, and the actual 12×12 V2 board appears with the established Select, Fit, +/- controls, Hint, target words, and scrollable layout.
- No obvious crash, missing asset, giant board clipping or empty-word display in screenshots. The supplied game board shows **0/12 found**, so screenshots alone **do not** prove word selection, hint persistence, successful completion, or restart restore yet.
- **Usability problem:** first page of the 12×12 puzzle shows only two target words and "Page 1 of 6" for its word list. This makes finding words unnecessarily dependent on paging and hides the whole word set, despite the puzzle grid being nicely visible.
- **Visual problem:** "Woodland · TEST BOOK · Page 1" wraps across two large heading rows. Keep the test-book identity visible in the Library and in the app label; shorten the gameplay heading only.

## Changed in WS-11E2

- Extended the existing \`WordList\` composable with \`scrollAll: Boolean = false\`. Its default and all Solo/Daily callers retain exactly the old paginated/compact behavior.
- **Book-only:** \`BookPageGameplayScreen\` passes \`scrollAll=true\` in portrait and landscape. All words appear in the same scrollable word list below the board, with no extra six-page pager. Existing Remaining filter still works, with scroll position reset on filter change. The entries use the **same current** styling, word order, found markers and font scale. The grid, precise-select and hint mechanics are unchanged.
- Book gameplay heading omits the huge QA suffix in the header; the Bookshelf/Book cards still say TEST BOOK and QA IDs/storage stay unchanged.
- Android instrumentation source test now asserts the final target word is present in the continuous list and the previous "Page 1 of 6" pager isn't rendered in book mode. Actual on-device instrumentation execution remains a separate gate; screenshot review is not proof of feature success.
- QA debug APK \`versionCode=50\`, \`versionName=0.9.6-bookshelf-list-review-qa\`, same debug application ID and stable public QA signer; intended as an **in-place update over QA v49**, preserving Solo/Daily data and separate QA Book journals. Do not uninstall old version.
- Debug-only test book catalog remains untouched; release remains empty/unpublished. No artwork, save schema or content changes.

## Next owner checks

1. Upgrade over the existing WS-11E APK and confirm app installs and prior book progress is retained.
2. Open Woodland Page 1. **Scroll word list**, verify all 12 target names are accessible without paging and the grid still accepts swipes / no-drag selection.
3. Find two words; return to Home, force-stop/relaunch and verify recovered highlights, list status, time/hints.
4. Complete Page 1 and check chapter progress, Continue page 2, and no double completion on practice replay.
5. Optional: screenshot at 200% system font scale and in landscape; book header and list should not collide.

No final production volume release implied.
