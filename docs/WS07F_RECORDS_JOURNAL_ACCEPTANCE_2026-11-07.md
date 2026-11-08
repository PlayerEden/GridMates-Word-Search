# WS-07F — Records journal navigation

**Status:** Implementation in [draft PR #60](https://github.com/PlayerEden/GridMates-Word-Search/pull/60), Android verification pending. Stacked on verified WS-07E #58; no `main` merge.

## User problem
The original Records page stacked Solo, Race and Duel summaries and histories vertically. Even after long scrolling, it displayed only the newest 25 Solo, 15 Race and 12 Duel records, silently hiding older history.

## Changes
- One visible journal at a time, selected with **Solo / Race / Duel** chips immediately below the page heading; Solo is the default. Solo/Daily summary cards appear **only** on Solo, so they do not push Race/Duel details below the fold.
- Each journal shows **four results per page** and a clear Previous / Page N of M / Next control when needed.
- Separate remembered page selection per mode; switching tabs does not reset the other mode's current page.
- All existing records remain in their original on-device save journals, and the UI no longer imposes a hard 12/15/25 cutoff. No schema, score, Race, Duel, LAN or profile semantics change.
- Summary metrics and empty-state copy are retained within their respective tabs. Top-level Completed/Words/With hints and Daily summary remain visible above the tabs.
- QA-only Android **v0.8.2 code36** with unchanged QA signer.

## Acceptance
- [ ] Standard Android build, lint, unit tests and instrumentation compile PASS.
- [ ] Android UI tests: Solo, Race, Duel tabs expose only the selected section; at 360×720dp the selected Race/Duel heading is actually **visible**, not merely mounted off-screen. The test uses the real shell's scrolling layout.
- [ ] Page math: 0 and 4 entries => one page, 5 => two, 25 => seven, 50 => thirteen.
- [ ] Full API35 suite (prior 90 plus two new) PASS with zero failures/skips.
- [ ] Real QA-signed v0.8.1 code35→v0.8.2 code36 install-over preserves Solo, Daily, identity, Race, Duel and save schema4.
- [ ] Record exact source SHA, APK checksum, signer, artifacts, expiry, checkpoint and rollback after green full run.
- [ ] Real device/two-phone acceptance **deferred by owner**, original Co-op P0 #53 OPEN; no production release.

## Further refinement
Collections/achievements still deserve their own accessibility and content audit; no claim is made that this pass resolves every long page. Future work should consider a lightweight profile and share entry point while keeping offline-first privacy and free cosmetics intact.

[Previous verified WS-07E receipt](WS07E_BUILD_QA_2026-11-07.md) · [Roadmap](DEVELOPMENT_ROADMAP.md).


## Emulator run diagnosis (first WS-07F execution)

[API35 run #42430710954](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42430710954) installed v0.8.2 over v0.8.1 and preserved existing Solo/Daily/identity/Race/Duel records, but **failed 1 of 92 UI tests**: the new Race heading was not discoverable from an unscrollable 360×720dp test viewport. The correction moves the mode tabs above Solo/Daily-only summary cards and tests visibility inside the actual scrollable shell layout. This earlier run is **NOT** a passing WS-07F checkpoint. Full QA must rerun at the updated source revision.
