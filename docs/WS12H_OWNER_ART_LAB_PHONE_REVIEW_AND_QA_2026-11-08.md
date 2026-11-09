# WS-12H — First Owner Art Lab Screenshot QA / v52 Usability Repair

**November 8, 2026 · Actual Android screenshots from the owner's v51 test. New artwork remains prototype, not owner-approved.**

## Evidence received

Four real phone screenshots show **Woodland, Space, Garden and the Art Lab introduction**. All 3 six-piece art families visibly render: covers, spines, emblems, chapter markers, bookmarks and completion seals. No missing art was evident. The stable paper palette, visual proportions, and distinct green, slate-blue and clay colors feel related, though the flat illustrations remain subject to visual approval.

## Phone issues and implemented fixes

1. **Back overlaps Android status bar:** the Art Lab route failed to pass the Scaffold top/bottom content insets. Now the Art Lab is wrapped in a full-size Box with the same Scaffold contentPadding used by other full-screen routes. The 48dp Back to Play button is left-aligned and no longer pinned into the system icon area.
2. **Oversized introductory copy:** the previous multi-line hero and long warning hid the first artwork on entry. The gallery now starts with a compact "Library artwork" title and one brief explanation. Detailed process notes are hidden in optional expandable HTML details below the three art families.
3. **Fake "Open book" button:** the review card had a green button-shaped element that did not navigate to gameplay. The gallery now uses a neutral "Prototype artwork" label, not a false action. Real Test Puzzle Books retain their working native buttons.
4. **No accidental final approval:** the source-generated 18 SVG shapes and their old hashes are unchanged. The master art branch PR #110 and QA branch contain identical gallery generator presentation changes. Artwork remains explicitly unapproved until owner feedback and actual app integration.

## Code / technical regression gates

- Gallery test requires 3 visible theme headings, all 18 assets, no misleading "Open book" label, and explicit prototype note.
- Existing standalone source-art tests must pass, proving cover/emblem/chapter/bookmark/seal SVG file hashes remain stable and text stays native.
- Standard Android CI must pass core/app unit tests, lint, Android test compilation and package check.
- Custom QA must build Debug with exactly 18 SHA-validated source art SVGs and Release with zero WS-12 preview assets.
- QA app versionCode **52**, versionName **0.9.8-art-lab-phone-review-qa**, debug application ID unchanged: **com.playereden.gridmates.wordsearch.qa**. Same public QA signer permits install-over of v51 when compatible.
- No migration or edits to Solo, Daily, Nearby, Books progress, Collection portraits, 12 original Woodland art files, or licensed font resources.

## What to verify on phone

1. Install the v52 QA APK **over** v51 without uninstalling. Open Home → View Library Artwork.
2. Confirm Back to Play sits below the phone's status icons and the bottom Android navigation stays unobscured.
3. The first view should show Library artwork followed quickly by Woodland's cover, not the long wall of explanation.
4. No fake "Open book" control should appear in this art-only preview.
5. Review Woodland/Space/Garden and exit Back to Play; check the actual Test Puzzle Books and other saved games remain available.
6. Send one top-of-Art-Lab screenshot and one qualitative art decision: **keep the simple print look / add some deliberate illustration detail / change style direction**.

**Remaining:** true owner aesthetic approval, rights review and a separate QA-only test of approved art inside real playable Bookshelf cards. Technical CI and these screenshots are not a signoff.
