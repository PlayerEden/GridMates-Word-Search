# WS-16D — A real Collection Home shortcut and theme-gallery planning

**Date:** November 9, 2026. **Branch:** feature/ws14b-style-anchor-and-art-direction-2026-11-08. **Draft PR:** #131.
**Status on authoring:** code committed through 47aa0f10b34186cd7eea6c2160ea7c476627108e, exact Android/art QA initially running; no phone acceptance yet.

## Owner review / problem
After WS-16C removed the footer, the owner's on-phone screenshots showed good breathing room and a working local Profile. But Collection was buried within Profile. The owner wants collecting and admiring illustrated unlockables to be a core reward loop, eventually with a theme album for each puzzle book, plus basic general unlocks. Reintroducing a permanent bottom bar would waste space and contradict the just-approved simplification.

## Implemented small and reversible UI change
- Added Ws16DLibraryShortcuts.kt: two side-by-side, equal prominence, 56dp+ accessible native Home buttons directly under the book carousel: **Full shelf** and **Collection**. On a narrow width or >=150% text scale, they stack and remain readable. No cover shrink required.
- Added original source master design/visual-system/masters/ws16d-collection-gallery-shortcut.svg and matching native Canvas illustration (framed art + gold spark). Full Shelf gets a companion native book-spine/shelf mark. The labels remain native and dynamic, never image-baked.
- Ws16HomeScreen.kt places the shortcuts below book progress and outside book-only QA condition. Collection remains available even if the production catalog has zero published books; Full Shelf remains conditional on actual books being available.
- GridMatesShell.kt routes the new Home Collection shortcut to the existing working CollectionScreen, with the prior WS-16C Back to Home route. Profile continues to link to Collection as an alternative.
- Ws16CarouselHomeAndroidTest.kt checks that tapping the new shortcut actually opens Your Collection and Back returns Home, Full Shelf still exists and the old bottom navigation hasn't reappeared.
- No change to existing portrait/font/achievement unlock calculation, saved data, book progress, or gameplay.

## Source-backed future direction — NOT built yet
See docs/design/WS16D_COLLECTION_HOME_SHORTCUT_AND_THEMED_GALLERY_ROADMAP_2026-11-09.md. Current WoodlandCollection portraits unlock via general Solo+Daily completion counts; BoardFontCollection via general achievements; the production BookCatalog is currently empty and prototype book pages must not grant production rewards. Future gallery can feature General and book-specific albums (Woodland, Space, Garden) with original art, theme frames, portraits, board cosmetics, stamps, milestone explanations and accurate progress. Book rewards must derive from verified published page/chapter/volume completion and never revoke pre-existing generic Woodland unlocks. Design and validate the catalog and persistence separately before adding rewards.

## QA gate, rollback
Both workflows must complete **SUCCESS** on exact gameplay source 47aa0f10b34186cd7eea6c2160ea7c476627108e (Android unit tests/lint/instrumentation source compilation and art/release isolation). Physically verify Home has Full Shelf + Collection side by side, Collection opens directly, Back returns Home, Profile still opens it, no bottom bar, no book cover shrink, Quick Play remains a truthful non-clickable QA preview, Custom/Daily/Together still work. With no published books in release, Collection must stay available.
Last dual-green fallback is WS-16C owner QA ZIP https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42717003929/artifacts/13717803408 (APK sha256 42bebe4ddcfef4056d25ddb562f1ef2abab2df857dd0bef8d5437bf97a9a4bbc). Keep PR draft/unmerged.

## Exact-source CI verification and owner APK (final)

**Both workflows completed SUCCESS** for code/art SHA `47aa0f10b34186cd7eea6c2160ea7c476627108e`.
- [Android #42719067784](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42719067784) passed unit tests, lint, instrumentation test **source compilation** and built the debug QA APK. Instrumentation source compile is not physical-device interaction execution.
- [Art/release #42719067846](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42719067846) passed debug/release compilation and verified approved art isolation: 6 QA-only book cover/spine sprites absent from release; 18 originals + 18 alternatives and 36 native exports verified; 3 original theme motifs hash checked and release isolated.
- Owner [WS-16D QA ZIP artifact #13717909916](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42719067784/artifacts/13717909916) contains `app-debug.apk`. Artifact original workflow title contains Woodland but code is the full WS-16D app. ZIP length 13,955,062 bytes reported by GitHub.
- **APK SHA-256:** `39dc1d519517cf6d64077079bcffbaf294e3a1b26e3dab87893875aca5bbe210`. **ZIP SHA-256:** `106108bce43c4bd4d5f436cae63f42c75cace94d9c137c5c2374daa865f2841e`.
- Package `com.playereden.gridmates.wordsearch.qa`, versionCode 57, versionName 0.9.13-visual-kit-foundation-qa. Same version code as preceding QA: backup progress before any uninstall for a signing mismatch.
- User-on-phone acceptance for WS-16D still **pending**. PR #131 stays DRAFT/unmerged. Previously verified rollback WS-16C remains available.

## Owner WS-16D handset screenshot feedback (November 9, 2026)

The owner supplied two actual Android phone screenshots: Home at initial scroll showing a large Woodland cover with adjacent Space peek, one shared shelf, the **Full shelf / Collection** original illustrated shortcuts arranged 50/50, Continue, Daily Puzzle, Together and the entire QA-only Quick Play coming-soon row; and Home scrolled lower showing **Custom** and **My Puzzles** without the former footer. No navigation-tab bar is present.

**What visual evidence supports:** the Collection entry looks readable and naturally integrated beside Full Shelf at this device width; both show full labels with no clipping; the book cover remains large; Quick Play can be seen before scrolling and Custom/My Puzzles remain reachable after scrolling. The user supplied screenshots but did **not** separately report tapping Collection, restoring progression, accessibility interaction or Back behavior; don't claim functional acceptance based only on still images.

**Focused potential refinements from review (not owner-mandated):** keep book art scale, avoid another Home overhaul, consider shaving excess whitespace above the cover and modestly reducing Continue card height before touching artwork; check whether scrolled-only Custom/My Puzzles discoverability is sufficient for older players. The user's long-term Collection request remains a separate book-specific unlock/gallery roadmap, NOT part of WS-16D.
