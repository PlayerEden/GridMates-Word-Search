# WS16BG — Front-facing physical collectible books in the Library

> **Status (Nov 12, 2026):** Source implemented in draft PR #131; v66 Android and independent art/release QA in progress; OWNER phone visual acceptance not yet tested.

## Why, who and prior version

- **Owner review:** After installing [WS16BF v65](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/43176204747/artifacts/13924198630) and sharing a portrait Library screenshot, owner saw cleaner edges but tall spines still did not resemble physical books; prior feedback also called out flatness, title overlap and sparse wooden empty space. Owner asked to continue after the proposal to prioritize illustrated front covers instead of further spine polishing.
- **Old known-good build, not visual approval:** v65 Android [#43176204747](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/43176204747) and independent art [#43176204757](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/43176204757) both passed. v65 APK SHA-256 `73989c2f2c71931d4d6b62bfe7309ddd8496dd48ddc7e2beff1bfa2398d84921`, code65, same long-lived QA signing certificate. Physical screenshot shown but appearance not approved.
- **Where:** Existing draft [PR #131](https://github.com/PlayerEden/GridMates-Word-Search/pull/131), `feature/ws14b-style-anchor-and-art-direction-2026-11-08`. Do not merge or replace original art in place.

## What changed (source only)

1. New `WS16BGFaceOutBookBay.kt`: displays the real Woodland, Space, Garden, and future actual catalog covers on carved shelves. Uses the exact **existing WS16AE painted front cover PNGs** (same artwork as Home). Crops only their empty periphery: 480×640 original image has alpha bounds near x12–468, y65–574; render-time crop `Bitmap.createBitmap(original, 10, 63, 460, 513)` preserves every painted pixel with 2px breathing room. This addresses the previous floating-book risk caused by the 65px top/66px bottom transparent margins.
2. A native 6dp **right-hand page edge** using an antique-paper gradient, soft shadow, and a slight ±2° planar/±5° perspective turn add physicality. This is a **narrow book thickness**, not the earlier false second spine/backing panel. The front is a single image; no fake future books or painted click regions are created.
3. `WS16ATModularLibraryScene.kt`: opt into face-out covers only in the high-res portrait two-tier cabinet. Show **two real covers per shelf**, four per page; page only actual catalog entries. Current three volumes = Woodland + Space upper, Garden lower. Five or more books create a new real page. The existing `WS16AMSpineBay` route remains for landscape/compact/older views. Existing cabinet art, gold shelves, count plaque, and upper/secondary navigation untouched.
4. Callbacks use `onBookSelected(book.id)`; open-the-book dialog, View pages, Solo/Daily, achievements, profiles, local saves, LAN and unlocks are not changed. Book covers are limited to existing source-backed debug art; release-only fallback is already handled by `BookArt`. No new image rights or production approvals.
5. `QaActualBookcaseAndroidTest.kt` portrait test now checks 3 real cover tags, 2+1 placement, missing fake spines, no title collision, lower-count clearance and Garden's correct stable ID on tap.
6. `test_ws16bg_face_out_library.py` and both Github QA workflows test original cover alpha/source geometry, render-time crop, real-only paging and Android contract. No new generated image assets or need for a binary Git upload.
7. QA app default `versionCode=66`, `versionName=0.9.22-face-out-collectible-books`, unchanged `com.playereden.gridmates.wordsearch.qa` debug package and established QA test certificate. Intended install directly over v65; **never uninstall/reset**.

**Initial implementation code commit:** [`a085a944529499c96513442634f92c1b6a061449`](https://github.com/PlayerEden/GridMates-Word-Search/commit/a085a944529499c96513442634f92c1b6a061449).

## Checks, rollback and next step

- **Android QA run:** [#43187644879](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/43187644879) started on initial source, final result PENDING at entry creation.
- **Independent art/release run:** [#43187644703](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/43187644703) started, final result PENDING.
- **APK:** NOT YET VERIFIED OR DELIVERED. Check both CI conclusions and actual artifact, its version, signature, original cover SHA images and checksum before sharing.
- **Rollback:** Prior owner-installed v65 [artifact #13924198630](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/43176204747/artifacts/13924198630) is the last verified binary; keep v64 etc. The current design is experimental.
- **Owner QA:** If dual-green, share real APK and GitHub artifact, install-over only; ask for new portrait Library screenshot, book taps, saved progress, and first reaction to physical-cover styling. Review real art on phone before any further change or PR merge.
