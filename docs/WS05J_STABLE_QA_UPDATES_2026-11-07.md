# WS-05J — Stable QA signing and save-preserving Android updates

**Branch:** feature/ws05j-stable-qa-signing-upgrade-protection-2026-11-07  
**Version:** v0.5.9, versionCode 21, application ID com.playereden.gridmates.wordsearch.qa  
**Stack:** on WS-05I #39; all previous cumulative draft work included, no merge to main.

## Problem and solution

Previous GitHub Actions debug APKs were signed by the Android runner's freshly generated debug keystore. Increasing versionCode and retaining the same package name **is not enough**: Android rejects an in-place upgrade if the signing certificate differs, even if both APKs are from the same GitHub project.

The **debug QA build only** now uses the pinned, public, deliberately test-only key at qa-signing/wordsearch-qa.p12, SHA-256 **cbe50b2b4a34f3a8433e28ddc0efd5307a1adf9aa91e324ca757f40c6aec91ee**. The fixed identity supports upgrade continuity across new QA builds *only while the QA package, signing identity and increasing versionCodes are preserved*. Because this QA key is public, it is **not suitable for production or Google Play distribution**. Future release signing must be kept confidential and separate. No user data or production secrets belong in the repository.

## Important one-time migration for earlier testers

v0.5.8 and earlier APKs came from a different, ephemeral signer. v0.5.9 **cannot be installed directly over them**. For each phone with an existing QA Word Search app:

1. In the old app go to **Settings → Export backup**; choose a location outside the app's private storage (Documents or Drive) and check that the exported JSON file actually exists.
2. After verifying the backup, uninstall **only the old Word Search QA app** (do not uninstall GridMates: Sudoku). Install the new v0.5.9 QA APK.
3. Open **Settings → Restore backup**, select the file, review the preview, confirm and verify your current unfinished Solo puzzle, Daily progress, profile and collections.
4. For all subsequent v0.5.9+ stable-signed QA versions, install the next APK **over** the existing QA app. Do not uninstall or clear its storage. Back up periodically anyway.

Manual backups contain Solo and Daily runs, completed results and profile/portrait identity. Device-specific sound/haptics and chosen board font preference are currently **not included** in the JSON export, although in-place APK updates retain those preference files.

## Actual automated acceptance

.github/workflows/ws05j-qa-upgrade.yml pins and checks the QA signer, imports approved artwork/fonts, then builds a versionCode 20 baseline and versionCode 21 candidate from the same source. It runs JVM tests, lint and Android test compilation; verifies package ID and certificate digest; installs the baseline on API 35; seeds an on-device, schema-4 profile with an unfinished timed puzzle and custom display name; runs **adb install -r** with the new APK; launches the updated app; and byte-compares the private save file before and after. It publishes the cumulative APK artifact if successful.

This specifically tests Android update continuity with the *new stable signer*. It does **not** prove that v0.5.8 can be updated without the one-time backup/reinstall, and same-source baseline-vs-candidate is not a full migration test between two different feature releases. The atomic backup restore now rolls back its in-memory imported profile on a failed write.

**QA state: AUTOMATED PASS.** [Successful API35 install-over-and-save-preservation run](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42341911826) and [exact source/APK receipt](WS05J_BUILD_QA_2026-11-07.md). Real phone migration and two-phone acceptance remain OPEN.

## Deferred acceptance

- Real APK update on two physical Android phones, full old-v0.5.8 backup/uninstall/restore migration.
- Word Search Solo/Daily/profile font preferences after normal updates, rotation/restart and background process death.
- Real two-phone LAN co-op, chat, reconnect and 200% text/TalkBack acceptance.
- Release signing, Play Console preparation, production security hardening and cloud backup remain later work.
