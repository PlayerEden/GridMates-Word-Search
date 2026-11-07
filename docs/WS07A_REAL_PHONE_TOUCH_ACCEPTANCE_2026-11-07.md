# WS-07A — Real-phone Word Marking & Compact Play Acceptance

**Status:** OWNER TWO-PHONE RETEST NOT PERFORMED; automation is GREEN (78/78 Android emulator). The owner's November 7 screenshots established that two phones could connect to a Nearby room but could not successfully mark words. This checklist describes a follow-up **test**, not a completed fix.

## Install safely
- [Verified v0.7.7 QA APK ZIP](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42413940520/artifacts/13591057182) (expires Nov 14, 2026). The `app-debug.apk` within has SHA-256 `0a807b2de2db335f4b723ae5d9d5882ad2df1768481c78d1c63cd484d9950a8f`; [full receipt](WS07A_BUILD_QA_2026-11-07.md).
- Record Android model/version for each phone, actual installed QA package, **versionCode**, signing certificate and APK SHA-256. The new candidate is v0.7.7 code31 QA-only. Both phones must use compatible LAN v7 builds.
- Export valid local Word Search JSON from **each phone**, store it away from the app, verify it contains your expected Solo/Daily/Race/Duel records. **Do not uninstall or clear data just to try this build.**
- If staying on the same signed QA package, install the new APK **over** the old app. Confirm all profile data remains.
- Do **not** share screenshots publicly with room codes, Wi-Fi addresses, chat transcripts or IPs visible.

## P0 — word marking, both phone roles
1. Put both phones on the same trusted Wi-Fi. Host a **12×12 Co-op** on A; join from B. Record whether both display each other as connected and matching board letters.
2. With the board fully fitted, choose an easy horizontally readable target. **Finger-drag from the FIRST letter through the LAST letter** on A; a valid word should turn green locally and appear on B (with A as partner owner). Note whether the temporary feedback says Checking, Recorded, or Invalid.
3. Find a second horizontal/vertical word on **B**. Both screens should update once; total must match exactly.
4. Test a diagonal and backwards target on each phone. Record exact target letters, gesture and result. Test **tap-first-letter, tap-last-letter** instead of dragging, both A and B.
5. Deliberately drag over a nonword: the game should show understandable local invalid-selection feedback and award nothing.
6. Swipe a valid vertical word; the page must **never scroll away from the board**. No Find/Move mode toggle should be necessary.
7. Pinch with **two fingers** to zoom; move the enlarged board with **two fingers**; no word should be awarded. Use Fit, +, and −; verify the board returns to fully visible 100%.
8. Repeat 1–7 using **20×20 Expert** and trade host/guest roles. No claim should require extreme zoom to begin near the correct cell.
9. Host a **Shared Board Race**; submit the same target nearly simultaneously. Host-confirmed first valid winner only; both screens agree. Finish, compare, save, relaunch, and verify one journal result per room.
10. Host **Speed Duel**; both press Ready, private boards become visible. Guest cannot see host hidden word IDs or highlighted paths, only counts. Finish and verify own result/history on both phones.

## Compact active-game layout
- No full-page vertical scroll while the puzzle is running. Board stays anchored, with mini word preview and readable scores/connection state visible.
- Open **See all** and scroll only the word sheet; dismiss back to the exact puzzle position. Open Chat, send/confirm a message, dismiss; no scrolling past the board to reach chat.
- Open Info to get room address/code when needed and toggle Classic-local readability only in Co-op. Don't expose these options as primary active-game clutter.
- Repeat portrait, landscape, 320dp viewport, fontScale 200%, and screen reader focus. No overlay should prevent marking or hide the Back/Info/Ready/Chat buttons.

## Connection and saved progress
- Disconnect B Wi-Fi mid-Co-op, then reconnect; existing words remain and new valid claims work.
- Exit a host room and reconnect to a new room; don't mistake closed host rooms for persistent cloud sessions.
- Confirm Solo, Daily, portrait/font unlock state and Race/Duel journals survive an in-place signed APK update.
- Share actual result text/image to another Android app; verify the recipient opens the PNG and sees no spoilers or private chat.

## Record evidence
For each step: **PASS / FAIL / BLOCKED**, phone and Android version, APK code/SHA, mode, exact target, input action, expected vs observed, elapsed/failure conditions and private screenshot/video/log. A passing emulator is not sufficient to sign off P0; this requires the owner to repeat on physical devices.

Reference: [WS-07A issue #53](https://github.com/PlayerEden/GridMates-Word-Search/issues/53) and [draft PR #54](https://github.com/PlayerEden/GridMates-Word-Search/pull/54).
