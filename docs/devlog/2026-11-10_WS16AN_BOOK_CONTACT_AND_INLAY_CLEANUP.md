# WS16AN — Owner On-Phone Library Review, Contact and Inlay Polish

**Date:** November 10, 2026 · **Status at initial source commit:** code implemented; Android/art QA pending. Draft PR #131 (no release promotion).

## Evidence

Owner installed the WS16AM APK and supplied an actual portrait screenshot of the illustrated **Your Library** screen showing:
- Three correct source-derived theme spines in the top bay: Woodland, Space, Garden; two generic future volumes to their right and five more in the lower bay.
- The carved, nearly full-screen cabinet, ivy, top crown, shelves and native header display cohesively. Native count remains the true three catalog books.
- The dark-green `3 books` badge sits visibly over the left side of the bookcase base, its opaque box obscuring part of the inlaid walnut/gold woodwork.
- Upper book bottoms show a small apparent gap at the shelf contact line. Lower books sit closer to their shelf. Paint/wood overall presentation is now an acceptable **working direction**, but user's screenshot alone does not confirm the spine tap, modal, View pages, Android Back, paged library scaling or owner final approval.

## Root cause and immediate code correction

`WS16AMInteractiveBookcaseScene.kt` uses distinct **48dp (54dp high font)** touch slots, with book image widths derived from the actual (104×542) spine aspect. This is mechanically accessible but means the visible art is not as wide as the touch slot and leaves conspicuous gaps. **Do not violate the tap-target minimum or stretch the illustrated title text** to pack the books. New wider publishing master sprites and no-lettering-distortion are a separate source-art project.

Small visual geometry improvements in this pass:
- Spines rest **3dp closer** to painted shelf boundaries by reducing Row bottom padding from 5dp to 2dp. Do not change the natural book heights or shelf geometry.
- Increase actual contact shadow depth from 3dp to 4dp and darken subtly (`0x990F0806`), staying restricted to each book's bottom so there is no wide rectangular global shadow.
- Remove the opaque, rounded dark-green book-count badge overlay. Keep a small, legible **native count text** directly on the woodcase's existing dark-green inlay, offset from the carved center ornament, without presenting it as a fake clickable button. No change to actual book count.
- QA source tests assert these exact changes, existing 48/54dp touch areas, real book selection and non-clickable decorative future volumes.

## Planned distinct V2 art task (not shipped here)

Rebuild/grow publisher spine masters to look physically thicker and **nearly touch** while retaining independent >=48dp tap slots and fully legible typography. Make the styled future-volume blank spines match the wear/texture/curvature of real Woodland/Space/Garden book spines; controlled color/height classes rather than repetitive flat gradients. Source- and alpha-pinned masters, no fake PuzzleBook catalog IDs. Review first-row gap and bottom-row book proportions by real-phone screenshot before committing a new master. Potentially refine the Android bottom navigation bar palette and blank area under base later, after interaction QA.

## Acceptance

- Both Android + independent art/release-isolation workflows must pass exact-source; inspect APK contents and preserve old WS16AM binary.
- Owner phone compare top row shelf grounding and inlay text position at 320/360/412dp. Tap all three actual spines to correct full cover/progress, View pages, Back to shelf, and inspect 3-dot fallback options.
- Never claim the static screenshot proves interactions or publication readiness.

**Implementation is deliberately small; no new art, book IDs, save changes, Home UI changes or automatic PR merge.**


## Exact-source QA receipt — both workflows passed

- Compiled source commit: [`14b995215a2d489f734fecd3bed1886637323aa9`](https://github.com/PlayerEden/GridMates-Word-Search/commit/14b995215a2d489f734fecd3bed1886637323aa9).
- Android CI [run #42954702947](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42954702947) **SUCCESS**, [artifact #13826443678](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42954702947/artifacts/13826443678). Artifact accessible with GitHub login, expires **December 11, 2026**.
- Independent artwork/debug/release isolation [run #42954703030](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42954703030) **SUCCESS**, [artifact #13826642822](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42954703030/artifacts/13826642822).
- Downloaded actual artifact archives from both runs; both archives contain `app-debug.apk` and pass ZIP integrity checks. Inspected both APKs: **311 of 311 identical path names and uncompressed content bytes matched** between independent workflows. APK signature-block marker is present; cryptographic signing/trust and device installation were not independently asserted.
- Android standard QA `app-debug.apk`: **23,827,752 bytes**, SHA256 `3ca6ee59d0128211fc3412f53e0f0f39166091b6573a7288b9f7c2673df802ae`. Standard artifact ZIP SHA256 `82a458b7095c52ab4605ab63f5e5f653990e24b30efbeeb8a28bb1486b818879`.
- All seven WS16AK actual debug entries (six painted parts + manifest) are bundled, and independent artwork/release-isolation workflow succeeded; book source IDs and fallback artwork are unchanged.
- **Owner on-phone visual/tap acceptance pending.** Verify contact plane, book-count typography over the inlay, correct real spine → full-cover dialog → View pages, blank spine non-interaction, and Back/overflow at real phone dimensions. Prior green WS16AM APK remains rollback; PR #131 is still unmerged.
