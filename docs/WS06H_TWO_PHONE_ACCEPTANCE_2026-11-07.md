# WS-06H — Two-Phone Owner Acceptance Card (Not Yet Performed)

**Target:** installed GridMates Word Search **v0.7.5 QA** or newer, **both phones on LAN v7**. This is a **manual test script, not evidence the tests have happened.** Do not clear data or uninstall older QA builds without exporting, verifying, and storing backup JSON.

## Capture for reproducibility
Device A model/Android version/installed package + code: ______
Device B model/Android version/installed package + code: ______
Wi-Fi network conditions (guest/client isolation? hotspot?): ______
QA APK SHA-256 / signer checked: ______
Result screenshots (no room codes, IPs or private chat publicly): ______

## Same-Wi-Fi multiplayer — execute with both actual phones
- [ ] Open Host Co-op on A, **Classic Paper**; on B choose **Blueprint Study**, join from auto-discovered room (no code). Both must see same 1st-admission shared **Blueprint Study** without awarding a cosmetic.
- [ ] Tap **Use Classic on my phone** on B; only B's look changes. Host view and players' personal board fonts remain unchanged.
- [ ] Each phone finds separate words; both show same shared completion, owner-highlight color and accurate counts.
- [ ] Disable/re-enable B's Wi-Fi once; reconnect to reserved host room; shared board look remains frozen and found words persist. Test rejoin attempt with different theme; must not change room look.
- [ ] Host **Shared Board Race** (both v7). Two users race a single word: exactly **one** receives each point. Complete board; both see exact host-authoritative score and correct winner/tie, save once only, compare word owners after match.
- [ ] Host **Speed Duel** (both v7). Both press Ready, GO begins, each has own identical seeded board and private word finds. Partner sees **only progress counts**, no private highlighted word IDs. First true completed private board wins; both save correct local result.
- [ ] Test optional room code, wrong code lockout, manual address fallback, guest reconnect, and host close/restart without pretending the old room persists.
- [ ] Check chat ordering, duplicate/retry behavior, 320dp/large text and screen rotation (do not expose chat/room code in shares).
- [ ] Put one phone in background then resume, reconnect if needed; verify no Solo/Daily saved progress loss.

## Solo, Daily, cosmetics, saves, sharing
- [ ] Play/resume Solo puzzle, find words and hints, review completed board/results and restart only after confirmation.
- [ ] Open Daily, return to Home then reopen. Same local date preserves same puzzle; prior completed days remain reviewable.
- [ ] Verify portrait/font collection unlock state stays owned only as earned; Co-op temporary board previews **do not** appear as earned items.
- [ ] Try 320dp portrait, landscape, 200% system font, and TalkBack: controls/titles should be legible, reachable and correctly announced. Check actual grid font contrast for Linen/Blueprint and the local Classic override.
- [ ] Complete Solo and one Nearby game; preview/share **text** and **image** to an actually installed target (Messages, email etc.). Confirm no names/chat/room credentials/answers/private Duel words. Validate the recipient can open the PNG.
- [ ] Export JSON from Settings, confirm backup contains existing Solo/Daily, Race/Duel and profile; save it externally before any uninstall. Reject an intentionally invalid backup without replacing progress.
- [ ] Verify in-place Android update of same QA signer/versionCode on each phone (back up first). No deletions of locally saved progress; previous Race/Duel records remain readable.
- [ ] Leave and relaunch app; confirm both scores/journals and preferences persist.

## Outcomes
Date/time: ______
Pass / Fail / Blocked with steps, APK version, screenshots and logs: ______
Owner approval for merging/testing track: **NOT GIVEN until recorded**.

**Important:** API35 emulator and local loopback tests are valuable, but are **not proof of real two-phone Wi-Fi discovery, connection timing, receiving-app URI access, physical display layout or handset APK updates**.
