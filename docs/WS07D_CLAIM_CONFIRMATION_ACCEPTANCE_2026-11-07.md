# WS-07D — LAN word-claim confirmation and host-authority reliability

**Date:** November 7, 2026  
**Implementation:** [Draft PR #57](https://github.com/PlayerEden/GridMates-Word-Search/pull/57), stacked on WS-07C #56.  
**QA candidate:** v0.8.0 / Android versionCode 34; same QA package ID, **LAN wire version 7**, schema4, test signing certificate and trusted Wi-Fi requirement.  
**Owner physical testing:** deferred by owner. No real-device, TalkBack or two-phone acceptance is claimed.

## Playtest problem being addressed

The initial physical Co-op test showed both phones connected but unable to mark a word. WS-07A repaired input geometry (automated tests green), WS-07C introduced an accessible alternate word picker (83/83 API35 tests), but an **additional feedback defect** remained: when a guest sent a valid-looking word and the host rejected it, the local board could remain on “Checking word with the host…” indefinitely.

A valid-looking local line is not a confirmed win. Shared Board Race must honor the first host-confirmed owner, and Speed Duel must not expose the opponent’s hidden word paths. Guests need reliable, truthful feedback about what the host actually accepted.

## New host confirmation contract

- The authenticated guest still sends `claim` with a 1–64 character ASCII-safe ID and endpoints. The host decides using existing Co-op/Race or Duel authorities; it **does not trust a client-supplied seat or word ID**.
- The host sends a normal **v7 `state` packet** containing an optional `claimReceipt: {id, status}`. This is additive, carries no word IDs or paths and does **not** require a new LAN protocol version. Legacy v7 peers ignore the additional JSON field and retain the existing state.
- The guest validates the canonical state and the optional claim receipt. A receipt is only applied when its request ID matches the currently pending guest claim. Stale/forged/unrelated confirmation cannot overwrite another claim's feedback.
- Visible statuses explain `FOUND`, `ALREADY_FOUND`, `INVALID_PATH`, `NOT_READY`, `NOT_CONNECTED`, `NOT_STARTED`, `FINISHED` and invalid room/request. The no-confirmation fallback after 9 seconds is explicitly **uncertain** rather than pretending a failed host verdict; the guest can check the updated board/reconnect.
- **No automatic replay** after missing acknowledgements: the host might have already accepted the selection. Existing host-side idempotency and first-claim ownership remain the authority.
- Host-only private Duel ledgers and recipient-scoped count/own-ID views remain unchanged; a receipt never contains an opponent's word ID/path. Existing Solo/Daily save fields, Race/Duel journals and package/signing remain unchanged.

## Automated acceptance gates

- [ ] Standard Kotlin/unit/lint/test compilation and app QA APK compile.
- [ ] Host-sent status round-trip matches actual FOUND / ALREADY_FOUND / INVALID_PATH authority outcomes without altering snapshots/revisions.
- [ ] Old v7 state without receipt remains valid; malformed status/ID/object shape is rejected, not silently coerced.
- [ ] Real TCP loopback frame includes only existing `state` with optional receipt, wire version remains 7; saved-data format unchanged.
- [ ] Duel guest receipt does not contain private opponent word IDs/paths; the shared snapshot validates.
- [ ] Existing 83 Android instrumentation tests still pass, including real Compose word swipe and precise row/column selection.
- [ ] Real QA-signed **v0.7.9 code33 → v0.8.0 code34** install-over retains schema4, Solo/Daily, identity, Race and Duel journal data.
- [ ] Source-matched CI and APK SHA, cert SHA, rollback branch recorded after the full API35 pass. A compile-only green build is **not** final acceptance.

## Physical acceptance — deferred

When the owner chooses to resume phone tests, use *both actual Android phones* on the same trusted Wi-Fi, with signed QA app updated in place (export backups first). Test:
1. **Co-op:** both host and guest claim visible targets and see the board update; test straight, diagonal, backward and two-tap/precise picker, no lost word ownership.
2. **Race:** both attempt the same target. Exactly one host-approved point, both screens converge. The losing guest gets an understandable “already found” message rather than hanging on Checking.
3. **Duel:** both tap Ready, GO reveals each private board; rejected path gets a message, accepted private claim increments own count only, and opponent path stays private.
4. **Interrupted requests:** briefly interrupt Wi-Fi after submitting an endpoint. No fabricated success, no double award after reconnect, and an understandable unconfirmed status when necessary.
5. **UI/accessibility:** inspect at 320dp, 200% text, portrait/landscape, TalkBack, Zoom/Select buttons; ensure host results and recovery messages fit without burying the puzzle.

Manual network, real Wi-Fi discovery/NSD and two-phone physical tests are distinct from same-device loopback. The original [physical P0 issue #53](https://github.com/PlayerEden/GridMates-Word-Search/issues/53) stays open until user-observed completion.

[WS-07 roadmap](DEVELOPMENT_ROADMAP.md) · [WS-07C automated QA](WS07C_BUILD_QA_2026-11-07.md) · [WS-07 research](WS07_PLAYTEST_RESEARCH_AND_UX_SPEC_2026-11-07.md).
