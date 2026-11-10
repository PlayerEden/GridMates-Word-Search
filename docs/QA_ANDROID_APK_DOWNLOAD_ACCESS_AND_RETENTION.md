# GridMates Word Search — QA APK downloads and evidence

**Status:** Development/debug QA only; **not** a production release or an invitation to enable unapproved art in release.

## Why some GitHub APK links display 404

GitHub Actions artifacts, including artifacts from **public repositories**, require the viewer to **be signed in to GitHub** with read access. A mobile browser outside the signed-in GitHub session may show GitHub's "404 / Need to sign in" page. This is not evidence of missing APK code or failed CI. See https://docs.github.com/en/actions/how-tos/manage-workflow-runs/download-workflow-artifacts.

A direct URL such as `https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42920066449/artifacts/13811856052` is **not a public anonymous download link**. For GitHub-hosted downloads: sign into GitHub in the same browser, open the specific successful Actions **run page**, find **Artifacts**, download the Android QA ZIP, extract `app-debug.apk`, and install/update. Do not delete existing app data.

## WS-16AE: exact verified candidate

| Item | Value |
| --- | --- |
| Source code | `8d4a49170c9eab43afff74b23e8fd42eb72340a4` |
| Android build | https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42920066449 (success) |
| Artifact (requires GitHub sign-in) | https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42920066449/artifacts/13811856052 |
| Download archive SHA-256 | `cd82705d8fb1feee98759cf2119a4801cbf16d33b89aa953695897de3eadb13e` |
| APK inside ZIP | `app-debug.apk`, 20,044,271 bytes |
| APK SHA-256 | `b5b7125bd83f3164d14381aabff62a34ed2ae70803524fe1acce0a5e8a54e1f1` |
| Independent art/release QA | https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42920066338 (success) |
| Artwork | 3 V2 front cover PNGs + 3 independent V2 spine PNGs; manifest hashes verified |
| Quality gate | QA owner handset testing pending, source and visual art **NOT release-approved** |

GitHub Action artifacts previously expired after **3 days**. Future builds from the updated workflows now ask GitHub to retain artifacts for **30 days**. **This does not extend older artifacts**, including the original WS-16AE upload that expires on **November 13, 2026**.

For anonymous non-GitHub-signed-in access, a third-party proxy such as https://nightly.link supports public repository artifacts; it is **not controlled by GitHub or this project**, may be rate-limited, and must not be mistaken for a permanent release channel. The long-term fix for shareable stable public builds is an **explicitly approved QA-only release distribution process** that clearly separates experimental debug artifacts from the signed production release channel. Do not auto-publish experimental builds as production GitHub Releases.

## QA install checks

- Debug application ID is `com.playereden.gridmates.wordsearch.qa`, not the production package.
- Debug code is signed with a designated QA signer. If Android reports a signature mismatch, check which test APK was previously installed; **do not casually uninstall because that may delete puzzle saves**.
- The actual APK has a valid ZIP structure, five DEX files, Android manifest, and an APK Signing Block (v2). SHA match shows exact tested binary. This does not establish end-user installation success: **install and launch on a physical phone still required**.
- If download succeeds but Android itself refuses installation, capture the **exact Android install error** and the app already installed; treat it separately from GitHub browser 404.
