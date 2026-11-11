# WS16AP — Library reset to five independent painterly bookcase assets
**Date:** November 10, 2026 (Kentucky local) · **Status:** original art candidate created and locally verified; **not yet imported into GitHub or Android**, no new QA APK, no physical phone approval.

## Owner-approved direction
The user explicitly requested a Library overhaul with the **existing functional Home shelf reused**, while the surrounding bookcase is represented by only five separate decorative images: **rear/backboard, left, right, top crown and bottom base**. Preserve the real live books, spines and book actions; do not paint these into the background. Stop treating an entire fake bookshelf screenshot as a functional asset.

## Source-made art candidate, now available in the ChatGPT conversation
A new full-resolution painted concept/asset-sheet source `bookcase_asset_sheet_master.png` (1536×1024; SHA256 `7deb40cf0e165cd6a7ca5a6b57672a5ff53b1902674cffc452eb1bc10c27456e`) was generated; **only its clean right-hand modular art pieces**, not the decorated reference bookcase on the left, are extracted for the app. A reproducible Pillow extraction script, manifest, five real PNGs, local SHA test and empty-case phone-size composition proofs are packaged as `WS16AP_Bookcase_Reset_Kit.zip` in this ChatGPT conversation. **The ZIP is not uploaded to GitHub and is not a permanent GitHub asset source.**

Independent exports:
- `backboard.png` 527×646, SHA256 `cd7d3b2de874b016f9b6628dacc0f82252e3853f95b0ede2f2d3ad9996c5744b`.
- `pilaster_left.png` 93×697, SHA256 `e679943ab4317dd4e43eb4be29e646150fbd4ddfa9b7c3ac3a664e04f1afc49e`.
- `pilaster_right.png` 94×697, SHA256 `fcd2d2b7893decb125621225556e68e566ea752d499421a0a4229a0b688d7a8a`.
- `crown.png` 721×102, SHA256 `df8030730ccf8278ed3948d028c0a7727b3c7c61c31d46bc9daa7949188819bf`.
- `base.png` 656×153, SHA256 `0a3f2827d318753b45aca5da92bddca8071eca45a8d96a8f992b40370ec04e61`.

The local candidate test passed five component existence/dimensions/RGBA/transparency/source and export hash checks. The source sheet and all exports are **candidate art**, not approved release assets; a few alpha edges and actual mobile composition still need visual scrutiny.

## Implementation gate — do not fake completion
1. Import the actual five PNG files, original sheet and extraction script into a versioned `design/visual-system/` source folder and `app/src/debug/assets/ws16ap/`; validate hash/provenance. Do **not** invent download paths or rely on the conversation sandbox in CI.
2. Add a Library-only optional native Compose renderer in front of the current real `BookcaseBookshelfScreen` / `WS16AMInteractiveBookcaseScene` behavior: dark backboard → existing working Home wooden shelves and live book sprites → left/right pilasters → genuine separate top/bottom art; native heading and live counts remain as text.
3. Keep untouched: real 3-theme book art, catalog/pagination, ≥48dp per-book hit targets, 0/1/3/7/13/24+ fixture semantics, noninteractive decorative fillers, book preview/detail/page click paths, saved progress, Home, LAN, signing and fallback screen.
4. Fit to 320/360/412dp/tall/landscape/large fonts without flattening/stretching wood relief; do phone-sized layout captures, exact-source Android + independent art/release isolation CI and then owner phone screenshots/click testing.
5. Keep PR #131 **draft**, keep WS16AO/AK/AI verified rollback, and do not claim APK or Play release before source-matched CI and actual acceptance.

## What changed in this pass
A real five-piece candidate art kit was made and validated **outside GitHub**. This entry is only a durable integration brief. No Gradle/build/source/QA claims.
