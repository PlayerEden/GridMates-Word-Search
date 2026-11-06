# WS-04B — Local Profile, Woodland Catalogue & Achievements

**Status:** Draft implementation on [PR #12](https://github.com/PlayerEden/GridMates-Word-Search/pull/12), stacked on WS-04A Daily. Automated Android CI and real phone art/collection acceptance are separate gates.

## Scope of this increment

- **Local editable display name** (trimmed, 24 character maximum), stored only inside the Word Search app and included in manual JSON backup.
- **Woodland portrait catalogue:** 12 exact IDs and labels from Sudoku's approved v0.11C Woodland collection. Owl and Fox start unlocked. The remaining 10 unlock through increasing counts of genuinely completed Solo + Daily puzzles (1,2,3,4,5,7,10,14,20,30 completions); this is a **baseline proposal** and may change after review.
- **Equipped portrait:** saved by stable ID; locked entries cannot equip even through malformed data. Selection does not spend a reward currency or remove unlocks.
- **Achievements:** eight always-computable badges from verified local histories, including first completion, five/twenty puzzles, one/seven Daily completions (not a streak), a no-hint Solo, a Hard Solo, and all three starter themes.
- **Collection UI:** player name, equip state, two starter portrait choices, locked/unlocked progress, and achievement progress bars. No mandatory account or online connectivity.
- **Save schema 3:** previous schema 1 (Solo) and 2 (Daily) continue decoding; identity is added non-destructively and exported in the manual JSON backup. Old history is preserved and unknown/future schemas are not silently replaced.

## Existing original Woodland art — important limitation

The canonical **twelve PNGs and original IDs** were found and verified in [SudokuTogether / feat/gridmates-nearby-animals](https://github.com/PlayerEden/SudokuTogether/tree/feat/gridmates-nearby-animals/app/src/main/res/drawable-nodpi). Their filenames and expected SHA-256 values are captured in [our Word Search reference manifest](../design/woodland/approved-portrait-reference.json).

**The PNG binaries themselves are not yet packaged in Word Search**. Current GitHub connected file retrieval refuses non-UTF-8 binary images, and the unrelated repositories cannot reference each other's Git blob SHAs directly. The gallery uses clearly described **monogram placeholders**, not generated lookalikes.

**Next safe original-art transfer:** using an authenticated checkout/local computer/Work environment with working git + asset download, copy all twelve PNGs byte-for-byte from the Sudoku feature branch into the Word Search `app/src/main/res/drawable-nodpi/` folder, verify each against the pinned SHA-256 manifest, wire them with `painterResource`, and capture narrow-screen screenshots. Review origin and permission for reusing artwork in a second public app. Do not regenerate the original Woodland collection.

**New generated art still to plan:** Ocean and Bugs avatar sets, theme-pack illustrations, check seals and optional soft marker-grain overlays; generate cohesive batches, inspect at actual mobile scales and register each asset with provenance/size/hash. Avoid expanding APK with unused full-resolution generated images.

## Automated checks

- Core JVM tests validate stable IDs, unlock thresholds, display name sanitization, locked/equipped behavior, achievements from records, and nonconsecutive Daily totals.
- Android instrumentation SOURCE validates schema-2 legacy import to schema-3, profile rename/portrait save, export/import, completed Daily unlocks and unknown-future-schema non-destruction.
- GitHub workflow covers core unit tests, Android lint/build, UI/instrumentation test **source compilation**, APK identity and artifact. **It is not device execution.**
- The final build must capture source SHA, run URL, artifact ID, APK hash and known-good rollback checkpoint before any CI-green claim.

## Owner/phone acceptance still open

- [ ] Upgrade or backup-import real previous WS-03/WS-04A state into schema 3 without losing Solo or Daily entries.
- [ ] Rename profile, select Fox, restart app, confirm name and equipped portrait ID survive.
- [ ] Finish one puzzle and unlock Rabbit; verify Cat is still locked and cannot be equipped.
- [ ] Achievements display appropriate earned/progress values; no fake badge for mere button taps.
- [ ] Large-text, narrow portrait and landscape Collection cards and progress bars remain usable.
- [ ] After actual art import, visually compare all 12 original PNGs (edge crop, color, sharpness, loading/placeholder fallback).
- [ ] Stable QA signing and real install-over-install with saved state remain unresolved ([issue #11](https://github.com/PlayerEden/GridMates-Word-Search/issues/11)).
- [ ] Android instrumentation tests actually run on emulator or phone; source compilation alone does not count.

## Relationship to planned WS-04

WS-04A provided offline Daily. WS-04B adds profile/achievement/cosmetic **rules and UI**. WS-04C can integrate original Woodland PNG binaries and a reviewed first generated Ocean/Bug set. Optional Daily-exclusive points/stamps remain an explicit design decision, not an invented live currency.

No actual LAN, Co-op, Versus, ads, cloud sync or Play production signing is introduced here. Existing QA builds remain separate from Sudoku; leave stacked PRs unmerged until acceptance evidence is available.