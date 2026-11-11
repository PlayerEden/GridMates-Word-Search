# WS16AQ — Full-bleed whimsical Library art reset after owner style reference
**Date:** November 10, 2026 (America/Kentucky/Louisville) · **Status:** five-image painterly candidate created and locally SHA256-verified; not yet committed as binaries or integrated into Android; not an APK or owner-on-phone approval.

## Owner's new visual decision (supersedes WS16AP style target)
Owner supplied a portrait image of a warm, playful, almost-whimsical bookcase with sculpted walnut/amber wood, gilded relief, ivy, recessed warm light, three physical rows, textured jewel-tone leather spines and integrated sign/woodwork. Owner specifically prefers this richer image to our previous cleaner/darker five-part design.

**LOCK:** Library artwork extends to all four physical display edges. No separate opaque top header, footer or parchment side gutters. Native controls overlay the art in accessible Android safe inset regions. The carved architecture should be an immersive full-screen scene rather than a smaller bookcase card in a page. Reference text is not to be rasterized into real UI: headers, count, progress, page indicators, Back/overflow and book labels must remain live native text/controls.

## Actual new local candidate kit (NOT in GitHub)
- ChatGPT-conversation archive: `WS16AQ_Immersive_Library_Kit.zip` (locally produced, not a GitHub artifact or Android APK).
- Original generated empty illustrated walnut master: `painted_empty_bookcase_source.png`, 1017×1547, SHA256 `5caf395d273b9cdcb9ccea5b5e1c5f2842a811b02e2d5367a418b4eb896c05e1`. This master is **book-free and UI-free** and is in the new rich style. Uploaded owner reference remains a STYLE reference, not app source.
- Five separate art-only exports (all PNG): `recessed_backboard.png` (793×1120, SHA256 `df78e29f5a27a15f590b3ab1301eb624c578b576d047cb9a5733a5b565ea5656`); `left_pilaster.png` (112×1215, SHA256 `be47a2a52bc6a19517b041f5d3923fd4af6d9817f2edde1ab8e12022b09c2205`); `right_pilaster.png` (112×1215, SHA256 `215e218da29bf74107e4c6a7ce07f1a69e9435582193661f91830a75a8e6cf70`); `top_crown.png` (1017×310, SHA256 `c72acc7982825116619a8e10ab37bddd832b52fd2502b7c2632fa7eda112b3f2`); `bottom_base.png` (1017×289, SHA256 `98a149bb1709bea5dd21c8f60ff8296fe081294972d0c8a9871cecc09652c486`).
- A local integrity manifest and composition images exist in the ZIP, including `empty_five_part_assembly.png` and phone-shaped 320/360/412px previews. They were checked with Pillow. Source wood grain has been lightly stretched for a continuous recessed backboard; **phone-size appearance still must be approved**.
- The ZIP includes a `shelf_placement_proof_only.png`; those preview-only shelf cutouts are NOT to be packaged. Use the already working painted Home shelf layers in live Compose as specified by owner.
- The two earlier attempted concept renders still had books embedded in them; they must NOT be integrated as architectural art. Only the final empty source and its five extracted modules are candidates.

## Engineering integration contract
1. Transfer real source and all five exact binaries into the draft development branch in a versioned art folder and QA Android assets. Do not mark complete from this documentation alone. Verify hashes from source and manifest.
2. Full-bleed Compose: painted backboard, separate existing working shelf ledges and **real** separate catalog spines/book sprites, then artist-made left/right rail, top crest and lower base in front. Wood can extend behind system status/nav bars, while all interactive controls stay in safe insets and maintain at least 48dp targets.
3. Keep existing Home screen, saved puzzles, book IDs, spine → detail → View pages, catalog-only horizontal pager, non-clickable blank books, actual count, LAN, device signing, and previous rollback intact. No static book pixels in the new assets.
4. Responsive strategy: preserve crown/base silhouette/aspect, allow the middle back wood to expand, maintain expressive carved side rails with protected caps rather than a full-height distorting stretch. Test 320/360/412dp, tall/landscape, larger font scale, TalkBack and 0/3/7/13/24+ catalog fixtures.
5. Run source-pinned standard Android CI, image/alpha/hash tests, independent release isolation and APK byte/hash verification. Take phone screenshots, check clicks and owner acceptance BEFORE merging or claiming approved release artwork.

## Status at this checkpoint
Art kit locally created and assembled. **No new APK, no on-device visual proof, no GitHub image binary import and no Compose activation yet.** Keep draft PR #131 and known-good WS16AO / WS16AK rollback. This entry replaces the prior WS16AP art direction; it does not rewrite old QA evidence or delete the source for rollback.
