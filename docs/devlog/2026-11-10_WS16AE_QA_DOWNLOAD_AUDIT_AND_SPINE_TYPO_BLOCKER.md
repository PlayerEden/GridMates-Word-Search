# WS16AE — Deep APK download/asset audit and open spine lettering defect

**Date:** 2026-11-10 · **State:** Technical APK verified; download UX originally blocked by GitHub sign-in; spine lettering needs a new reviewed art correction. **DO NOT call this release approved.**

## User-reported failure
The user's Android Chrome screenshot shows GitHub **404 “Need to sign in?”** when opening an Actions artifact URL. Repository `PlayerEden/GridMates-Word-Search` is public; GitHub Actions **artifact downloads still require GitHub sign-in and read permission**, including for public repositories. This is documented at https://docs.github.com/en/actions/how-tos/manage-workflow-runs/download-workflow-artifacts and https://github.com/actions/upload-artifact#outputs. This is not an Android runtime error and the existing artifact has not been lost.

## Exact original WS16AE APK bytes
- Source commit `8d4a49170c9eab43afff74b23e8fd42eb72340a4` (book cover/spine integration), later docs only.
- Android [run #42920066449](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42920066449) **success**; artifact [#13811856052](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42920066449/artifacts/13811856052) existed at inspection, was **not expired** and had expiry `2026-11-13T23:08:08Z` due to old 3-day retention.
- GitHub ZIP archive SHA256 `cd82705d8fb1feee98759cf2119a4801cbf16d33b89aa953695897de3eadb13e`, containing one `app-debug.apk`; verified APK size **20,044,271** bytes SHA256 `b5b7125bd83f3164d14381aabff62a34ed2ae70803524fe1acce0a5e8a54e1f1`.
- Independent [art/release QA #42920066338](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42920066338) success. Both independently built APKs have **288 identically named and identical uncompressed internal files**; their outer APK ZIP hashes differ because of packaging. Actual APK ZIP integrity `testzip() == None`, contains AndroidManifest.xml, classes.dex through classes5.dex, native libraries and a recognizable **APK v2 signing block**. Cryptographic signature/certificate chain was **not independently validated on-device**.
- Six actual candidate image paths: `assets/ws16ae/covers/{woodland,space,garden}.png` and `assets/ws16ae/spines/{woodland,space,garden}.png`. All six output SHA256 checks match the **packaged** manifest. Source/QA files exist and are separate, and debug-only.
- Alpha bounds: all three cover sprites share 480×640 PNG dimensions and approximately (12,65)-(468,574) visible silhouette; all three spines share 128×640 and bounds (14,51)-(114,589). Transparent canvas origin is fully transparent; no accidental full rectangular opaque matte.
- Actual device install, launch, carousel taps, save preservation and intended typography still require **real handset QA**.

## Download delivery correction (committed in `1cf6aef70fc6ccd1f6c5da191e3c26cfc5e8debe`)
- Both relevant GitHub Actions workflows changed from `retention-days: 3` to **30 days for future artifacts only**. Existing artifact expiry does not change.
- Stable informative names replace misleading `Woodland-Verified-QA` and `WS14A-VISUAL-KIT` labels.
- [Download/access guide](../QA_ANDROID_APK_DOWNLOAD_ACCESS_AND_RETENTION.md) explains authenticated artifact access, checksums, QA-only package, distinction between a browser 404 and actual Android install errors. No user saves or game code changed in this delivery fix.
- Longer term: a clearly labeled, owner-approved QA **pre-release** can enable anonymous public GitHub Releases downloads. Do not auto-publish unapproved debug art as a production release.

## Additional defect discovered by independent 1:1 artwork inspection
The original V2 builder `tools/asset_pipeline/build_ws16ae_books_v2.py` applies `repair_banner()` **only when role == 'cover'**. Spine PNGs are merely normalized and edge-decontaminated; they retain a small **superscript/registered-mark-like printed glyph after “GridMates”**. Viewing the actual APK spine glyph at 5× magnification confirms a faint extraneous mark in the typography, meaning the requested **no-® on every book asset** milestone is NOT yet fully done. The front cover banners also need another magnified aesthetic inspection; pass tests currently assert pixel modification, not a trusted visual confirmation that every mark is absent.

**Required art fix (separate new source-pinned visual pass):** retouch/redraw complete top typography on all three spine source assets (and re-examine front banners) to remove unsupported marks; do not erase adjacent s or make white rectangular masks. Update QA to verify both cover and spine banner regions, inspect cream/dark mats and on real phone. Keep original V2 sources and existing fallback. Do not promote unapproved painted sprites to release.

## Next
- Verify 30-day-retention workflow reruns `#42922678480` Android / `#42922678475` art after success; confirm new artifact expiry and exact source/checksums.
- User should sign into GitHub in the same browser to download Actions artifact; alternatively provide directly attached authenticated ZIP/APK or a documented public-repository proxy (third-party), without claiming it is first-party GitHub.
- Fix actual spine lettering in next art pass and request owner phone acceptance. **No new playable books or changes to library interaction were made in the download audit.**
