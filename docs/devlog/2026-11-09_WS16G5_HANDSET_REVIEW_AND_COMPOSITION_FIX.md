# WS-16G5 — Owner handset visual review and polish correction

> **Status:** Owner handset visuals received and reviewed, two small fixes committed. Source-matched CI in progress at initial writing. **No new verified APK or phone acceptance of fixes yet.**

## What was actually reviewed
- Owner supplied three portrait Android screenshots of WS-16G (Home focused Woodland, Home focused Space, Your Library with Shelf 1 Woodland/Space and Shelf 2 Garden).
- Screenshots visually confirm eight-part modular walnut shelf shown underneath real illustrated books on Home and in Library, brown backboard with vertical woodgrain in real catalog rows, native action buttons and Profile/Menu present. The Together icon/label layout is now centered in its card.
- Visual findings against the reference: Home book bottoms still hover a short visible distance above shelf; in Your Library, a thin independently rendered spine appears next to each existing cover, although each cover PNG already illustrates its own left spine. The library backboard texture is simple and rectangular rather than richly painterly; native action symbols are still icon-like rather than the hand-illustrated six-piece target kit.
- These images **do not** prove that all buttons work, that saved progress survives reinstall, or that art looks correct at different font scales/screen sizes. Keep those separate test obligations.

## Specific implemented fix
- [Source commit `228828621ceaac56b4057204ab8ad0b7476c4c05`](https://github.com/PlayerEden/GridMates-Word-Search/commit/228828621ceaac56b4057204ab8ad0b7476c4c05) on draft PR #131.
- `Ws16BookCoverPager.kt`: lift the decorative Home shelf by 6dp behind actual clickable cover PNGs, preserving pager bounds, cover dimensions, interactive semantics and thumbnail-focus behavior.
- `BookcaseBookshelfScreen.kt`: stop drawing a duplicate `BookArtKind.SPINE` beside the already-spined cover PNG in each real library row. Keep `BookArtKind.COVER` with existing 108×148dp size, hit region and accessible label.
- This is intentionally a small composition fix; no puzzle logic, progress storage, book catalog, data files, version changes, artwork hashes or release-approved art changed.

## QA receipt and follow-through
- **Previous owner-viewed WS-16G exact-source dual GREEN**: corrected SVG+Android source `d9c228f`, [standard Android #42744842244](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42744842244), [art/release isolation #42744842181](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42744842181), owner [QA artifact #13730138987](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42744842244/artifacts/13730138987); screenshot matches intended applied artwork but no proof of click-path testing.
- **New code source `22882862`:** [standard Android QA #42746766977](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42746766977) and [art/release #42746767151](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42746767151) started; status still needs checking. APK/artifact for this new code is not claimed in this log.
- PR stays a draft and original Canvas fallback remains for release without debug-only sprites. No installs/uninstalls or save schema changes performed here.
- **Next action:** verify both new workflows, fix any source-build errors, provide source-matched new QA link once green. Ask for owner Home and Library photo checks after the corrected build. Then resume the actual `WS-16G` **six-piece painterly action illustration family** and remaining foliage; don't endlessly tune minor padding instead of real asset production.

## Final corrected-source CI verification

**WS-16G5 actual source commit:** [`228828621ceaac56b4057204ab8ad0b7476c4c05`](https://github.com/PlayerEden/GridMates-Word-Search/commit/228828621ceaac56b4057204ab8ad0b7476c4c05).

- **Standard Android QA:** [run #42746766977](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42746766977), **SUCCESS**, source-matched Gradle/core/app/unit/lint/Android test compilation plus debug APK; owner [artifact #13732195532](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42746766977/artifacts/13732195532), contains `app-debug.apk` SHA-256 `1f10184ac3f5438221984f7548e19256c498f31fad3982e149954c082196ec8a`.
- **Illustrated art/QA-release isolation:** [run #42746767151](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42746767151), **SUCCESS**, art/debug/release tests and asset separation. Alternate [artifact #13732285531](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42746767151/artifacts/13732285531), debug APK SHA-256 `047736a5997d84465fced6a2add272b855d07bc85142f3efa3ecc259198181c5`.
- Downloaded both workflow artifact ZIPs and compared actual APK contents: every internal file path and its extracted bytes matched (zero content differences); only outer APK ZIP packaging metadata differs.
- Standard debug APK includes the eight `assets/ws16g/shelf/*.png` files and their SHA manifest, release build excludes unapproved WS16G QA art. No handset validation of the **new 6dp contact shift and duplicate spine removal** yet.
- **Next owner step:** install artifact #13732195532 over existing QA version when compatible, then screenshot Woodland/Space Home and both Library shelves. Check book tapping/open/back and saved game. No changes to versionCode or save schema.
