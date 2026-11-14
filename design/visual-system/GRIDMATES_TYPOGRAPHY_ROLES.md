# GridMates typography roles — family design contract

**Applies to:** GridMates puzzle series, starting with the Word Search Android prototype. Documented 2026-11-10. A future Sudoku or Crossword implementation must adopt this deliberately and independently; this page alone doesn't change those apps.

## The permanent identity typography

| Role | Font | Scope | Player can unlock/equip as a cosmetic? |
| --- | --- | --- | --- |
| Storybook / display | **Literata** | App headings, section headers, bookish navigation | **No** |
| UI / controls | **Atkinson Hyperlegible** | Controls, captions, instructions, status and tiny text | **No** |

These are **embedded offline** under the SIL Open Font License 1.1, with the original complete license notices in the APK. Typography source files are tied to pinned Google Fonts Git blobs in `design/font-library-approved-b5.json`. Both fonts are open-licensed, **not exclusive GridMates-owned fonts**. Never sell raw font binaries as standalone merchandise or strip required notices. Custom logos, art and application code have separate ownership/licensing decisions.

## Board-letter cosmetics — different system

Board glyph fonts affect **only puzzle letters**, and may be earned/customized. Do not use equipped font to replace brand headlines, menu buttons, settings text or progress labels. This separation must be enforced in core data, not only the Collection copy.

The initial Word Search styles are:

| Board style | ID | Availability |
| --- | --- | --- |
| Readable Print — Atkinson | `atkinson` | **Always-free starter**, not an unlock. Historical default ID preserved for saved data. |
| Classic Typewriter — IBM Plex Mono | `plex` | Achievement after first completed puzzle |
| Modern Print — DM Mono | `dmmono` | Achievement after five completed puzzles |
| Pencil Notes — Patrick Hand | `patrick` | Achievement after first completed Daily puzzle |

The historical baseline happens to use the same Atkinson font as the UI, but **it cannot be earned as an achievement**. This is intentional backwards compatibility. If we later want even the free board baseline to have a visually distinct bundled face, migrate as a separate reviewed change and preserve historical selections. Do not claim this baseline is a separately licensed or exclusive typeface.

`GridMatesTypographyRoles` supplies stable identity IDs; `BoardFontCollection` derives `unlockableFontIds` and fails startup for collisions against brand roles. Core unit tests check these boundaries and keep existing achievement/unlock IDs intact.

## Rules for future unlockable fonts

1. Prefer distinct optical styles (handwriting, vintage typewriter, clean print, block print) over minor lookalike variations.
2. Check OFL or other actual **commercial redistribution rights** and bundle complete notices before enabling; pin source revision, binary hash, A–Z glyph coverage and accent coverage.
3. Test all 26 uppercase letters at common 20–72px cells and on small/large grids: no overlap or missing glyphs; maintain adequate contrast and touch independence.
4. Attach to transparent progression or explicitly priced cosmetics; preserve the game's accessible, always-free starter.
5. Retain saved ID semantics across versions; introduce migrations explicitly for changed/removed fonts.
6. Keep player selections separate from storybook branding and Android system back/navigation settings.
7. Avoid copying bitmap text into card artwork; keep live text legible/scalable for accessibility.

## Related Word Search QA

See `docs/devlog/2026-11-10_WS16V_SHELF_SHADOW_AND_FONT_ROLES.md`. The same pass removed a distracting external shadow rectangle from the layered bookshelf. Before adopting any shared style in a second GridMates app, review a phone screenshot and verify its own font assets/licenses and screen constraints.
