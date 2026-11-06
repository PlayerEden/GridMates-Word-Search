# GridMates: Word Search — Open Font Library Research and Commercial-Licensing Intake

**Prepared:** November 6, 2026  
**Stage:** Verified **license-source research**, not final font-binary intake or UI approval.  
**Product:** GridMates: Word Search (Android), with a future family of GridMates puzzle apps.  
**Related:** [B5 typography and unlockables issue #29](https://github.com/PlayerEden/GridMates-Word-Search/issues/29) · [Game Design roadmap](DEVELOPMENT_ROADMAP.md).

## Executive decision

**Build a reusable, legally tracked library of bundled fonts before introducing paid cosmetics.** The license and a font's visual quality are separate gates. A font can be legally distributable yet unsuitable for a 20×20 word-search grid. Keep the default readable and free; unlocking another look must never hide letters, change generator output or affect a player's accuracy.

- **Preferred intake license:** SIL Open Font License 1.1 (`OFL-1.1`) from the verified original publisher or Google Fonts repository, with full copyright, license and reserved-font-name review.
- **Copyright reality:** Open-source/open-license does not mean public domain. The font remains protected under the copyright and license supplied by its author.
- **Commercial games and IAP:** The OFL expressly permits original and modified fonts to be bundled with commercial software, including games and mobile apps, and does not require the application's code to be open-source. **Do not sell the font file by itself** or represent a paid in-game cosmetic as exclusive ownership of the underlying font. An unlock should be a feature of a larger game or cosmetic set; users retain their OFL rights, even if the app uses DRM or otherwise gates access to a feature. For ambiguity around a paywalled font-only entitlement or unique sale structure, have release counsel check the implementation before selling it.
- **License obligations:** Retain each font's copyright notice and full applicable license text in the app distribution and accessible third-party notices. Under OFL, modified font derivatives must remain OFL, and any Reserved Font Name used by the project may need changing if modifying the actual font; do not imply authors endorse the game.
- **Google Fonts:** Files are organized under `ofl/`, `apache/` and `ufl/` by license. Verify each font's **own** family license; do not assume the whole repository is OFL. Pin the exact source commit, font binary SHA-256 and license SHA-256 when accepting files.

## Primary legal references

1. SIL Open Font License FAQ — bundled commercial games and apps, app need not be open source, font file cannot be sold alone: https://software.sil.org/oflt/ 
2. Current OFL instructions for mobile apps — preserve at least copyright, license notice and license text: https://openfontlicense.org/how-to-use-ofl-fonts
3. Exact OFL-1.1 text: https://spdx.org/licenses/OFL-1.1.html
4. Google Fonts repository, per-family license and provenance: https://github.com/google/fonts/blob/main/README.md
5. SIL project FAQ confirms commercial app bundling: https://software.sil.org/fonts/faq/

**Important distinction:** These references support bundling fonts *with* commercial games. They are not a grant of exclusive control over any typeface or permission to sell unmodified raw font downloads as a standalone product.

## First researched shortlist (official upstream license files located)

**License review here means:** the family has a directly inspectable publisher/Google Fonts OFL-1.1 notice. **It does not mean:** the exact current TTF/OTF binaries were downloaded, hashed, checked for required glyphs, or packaged. Every entry has `packaged: false`, `asset_verified: false` until intake passes.

| Family | Candidate role | Official license evidence | Observations to test |
| --- | --- | --- | --- |
| **Atkinson Hyperlegible** | Free accessible grid default candidate | [Google Fonts OFL.txt](https://github.com/google/fonts/blob/main/ofl/atkinsonhyperlegible/OFL.txt) · [upstream design](https://github.com/googlefonts/atkinson-hyperlegible/blob/main/README.md) | Deliberately distinct letter shapes, especially I/l/1 and O/0; **strong first board test**. Seriflike irregular widths still require measured center |
| **IBM Plex Mono** | Clean typewriter/technical board cosmetic | [Google Fonts OFL.txt](https://github.com/google/fonts/blob/main/ofl/ibmplexmono/OFL.txt) | Monospace makes spacing predictable; licensed with **Reserved Font Name “Plex”**—review before modification |
| **DM Mono** | Minimal contemporary board cosmetic | [Google Fonts OFL.txt](https://github.com/google/fonts/blob/main/ofl/dmmono/OFL.txt) | Open, simple monospaced forms; ensure clarity at 20×20 / 100% |
| **JetBrains Mono** | Alternate technical/typewriter cosmetic | [Google Fonts OFL.txt](https://github.com/google/fonts/blob/main/ofl/jetbrainsmono/OFL.txt) · [upstream README](https://github.com/JetBrains/JetBrainsMono) | OFL font (project source may carry a different license); disable discretionary ligatures for single-cell puzzle letters |
| **Lexend** | Rounded, readability-oriented board/UI candidate | [Google Fonts OFL.txt](https://github.com/google/fonts/blob/main/ofl/lexend/OFL.txt) | Distinctive width/spacing; variable font needs verified Android behavior; notice contains RFN **“RevReading Lexend”** |
| **Nunito** | Friendly rounded board or UI candidate | [Google Fonts OFL.txt](https://github.com/google/fonts/blob/main/ofl/nunito/OFL.txt) | Good warm GridMates feel; test I/W/M spacing when fitted into equal cells |
| **Literata** | Paperback/bookish UI and possibly board cosmetic | [Google Fonts OFL.txt](https://github.com/google/fonts/blob/main/ofl/literata/OFL.txt) | Strong “real puzzle-book” look but serifs may crowd 20×20; test board glyph metrics before approving for grid |
| **Patrick Hand** | Pencil/handwritten cosmetic, primarily larger-board or UI | [Google Fonts OFL.txt](https://github.com/google/fonts/blob/main/ofl/patrickhand/OFL.txt) | Letter slant and irregular silhouette are intentional; may fail compact legibility audit |

Other candidates for later **license-and-legibility review**: Caveat (script/handwritten), Special Elite (aged typewriter), Alegreya, B612 Mono, Source Code Pro and Noto Sans for extended language coverage. **Not approved or verified for this intake**; do not package them based on a name alone.

## Visual acceptance matrix

1. **Actual puzzle rendering:** Grid sizes 12×12, 15×15 and 20×20 at 100%, 145%, 185% and 250%. Test small/narrow Android portrait, landscape, Android font 200%, light/dark/contrast modes where applicable.
2. **Glyph samples:** `I J L M N O Q V W X`; `MILK`, `MUSHROOM`, `RIVER`, `WILLOW`, `ROBIN`; Spanish preview `Á É Í Ó Ú Ü Ñ` for future language packs. Look for ink bounds, vertical baseline and line collisions.
3. **Metrics:** measure **actual ink bounds** of the chosen face, center glyph inside cell, apply one consistent safety inset and dynamically clamp optical font size. Do not hardcode an assumption that all faces are monospace or that `I` and `W` share width. A legible but narrower glyph is fine if its **center alignment** is visually consistent.
4. **Game invariants:** Changing fonts cannot change grid letter content, selected target/path, tap coordinate, progress, hint allowance, completion record or seeded puzzle ID. Keep fonts a cosmetic profile preference rather than part of generator seeds.
5. **Cross-device:** Bundle approved .ttf/.otf in Android resources (or an approved, pinned signed delivery pack), **do not rely on system/OEM-installed fonts** or the phone's internet. Compare exact equipped preview on two phones and an emulator.
6. **Accessibility:** Always provide an unmissable readable default/reset and forbid highly decorative fonts at compact 20×20 if needed; offer a graceful fallback to default without taking away ownership. Avoid implying a “readability upgrade” is paid.

## Font intake ledger: nonnegotiable steps

For **each** candidate font, keep a machine-readable ledger row including:

- Stable `font_id` that is independent of display name, original family name, UI title, and unlock entitlement.
- Font family, author/copyright holder, upstream URL, exact upstream revision/tag and direct binary file path.
- Exact **binary SHA-256**, file size, format (TTF/OTF), weight/style and font name table data.
- SPDX license ID, full original copyright/license file in the release APK, exact license-file SHA-256, any Reserved Font Names and modifications made (if any).
- Android resource filename and bundled size (avoid shipping enormous unused variable-family weights).
- Glyph coverage `A–Z` and future Latin accents; baseline/spacing QA per size/zoom, two-phone comparisons and status `candidate → license-reviewed → asset-verified → visually-approved → packaged`.
- Paid entitlement classification and disclosures: `free_default`, `earned`, `cosmetic_bundle`; never store the proprietary *font file license* as something the player “owns exclusively.”
- Final link to automated build/asset QA receipt and third-party notices screen where users can access font license terms.

## Recommended shipping architecture

- `design/font-library-candidates.json`: source-backed research intake; **nothing listed here should be bundled or portrayed as already licensed/installed content merely because it's a candidate**.
- `app/src/main/res/font/`: only selected, approved and license-audited typefaces added after B5 rendering QA. Keep a separate matching `licenses/fonts/` notice directory and include license/copyright notices in an accessible in-app third-party notices screen.
- Core persisted cosmetic profile: `boardFontId` and optional `uiFontId` saved independently from puzzle generator. Unknown font IDs fallback to accessible default. Free/earned/purchased states defined without faking Play Billing support.
- Grid-aware preview: at least a real 4×4 sample, plus optional 15×15 stress test using letters `I`, `M`, `W`, `Q`.
- Future collections use a small first batch of 4–6 approved styles before considering dozens. Cosmetic pricing and Google Play Billing are a separate planning/review gate.

## Concrete next tasks

1. **B5 typography renderer:** implement font-neutral measured-center cell painter with a basic default; add separate board-font registry/equip-ready plumbing without starting storefront or granting fake entitlements.
2. **Asset research lab:** fetch selected official **font binary plus full license** at a pinned source commit; record file sizes/SHA-256, check name table, license notice, glyph coverage and Android performance. **No font binaries included in the Word Search APK yet.**
3. **Design review:** compare Atkinson Hyperlegible, IBM Plex Mono, DM Mono, Nunito and Literata on real puzzle screenshots. Promote only faces passing readability.
4. **Legal/commercial review:** check each final notice and paid cosmetic presentation. If an unlock is effectively reselling a bare font, redesign the offer or request legal advice before monetizing.
5. **Only then** import approved assets into Android and expose collectible states in Collection. Preserve original 12 Woodland portraits; don't delay main word-search/nearby multiplayer parity simply to build a vast font catalog.

## Research limitations

- The eight families above have directly verifiable official OFL-1.1 notices, but exact built binaries have **not yet been retrieved and audited** or copied into the app.
- License notice provenance needs to be matched to the **exact approved binary revision**, not inferred from a live GitHub `main` URL at a future release date.
- This is engineering/legal-license research, not a substitute for a lawyer's review of a final paid catalog or consumer disclosures.

## Google Play monetization compliance (separate from font copyright)

When the **Google Play-distributed** version charges money to unlock a **digital in-app cosmetic**, Google Play's payments policy generally requires the Play Billing system, subject to defined alternative-billing exceptions/programs. This is **a different legal requirement** from OFL font redistribution rights. We should not activate paid font entitlements until the billing, purchase acknowledgment, restore and consumer disclosure paths have been implemented and tested. A game may use earned/free font cosmetics without Play Billing.

- Official [Google Play Payments policy](https://support.google.com/googleplay/android-developer/answer/9858738?hl=en) and [explanation for digital goods](https://support.google.com/googleplay/android-developer/answer/10281818?hl=en-en).
- Advertise the item honestly as an **in-game appearance option**, not sale of the underlying font file or exclusive rights to its copyright. Keep a license notice reachable even while a cosmetic is locked.
- Avoid misleading “rare/exclusive font ownership” claims for an openly licensed typeface. Exclusivity can refer only to the game's own presentation/reward event, if applicable, not the open font's redistribution rights.
