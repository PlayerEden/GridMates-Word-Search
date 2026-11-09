# WS-16B — Asset-first Home actions and compact menu

**Date:** November 9, 2026. **Branch:** feature/ws14b-style-anchor-and-art-direction-2026-11-08, PR #131 draft.
**Status at creation:** UI/art source committed at 0fb918c4b967a5b5bfe888289ca03a771e8ea0ac; exact-source Android/art workflows underway, NOT phone-accepted.

## Owner direction
Continue from positively reviewed WS-16A carousel Home phone screenshots. Preserve big illustrated covers and functional routes. Improve Daily Puzzle vs Together truncation with a wider labeled Daily button and compact two-silhouette Together asset. Make Custom the user-facing name for the current customizable Solo mode; show a future Quick Play mixed-word mode's actual space without pretending it works. Compress the oversized Menu dialog and vertical padding. Evaluate on phone before shrinking covers.

## Implemented source and art
- New native Canvas art kit: Ws16BActionArt.kt. Original, separate editable transparent SVG masters in design/visual-system/masters: ws16b-daily-action.svg, ws16b-together-action.svg, ws16b-quick-action.svg, ws16b-continue-action.svg. They act as design-source counterparts to runtime vector drawing; Android does not render SVGs directly.
- Ws16BHomeActions.kt: Daily Puzzle action uses a wider share of the row (1.72:1). Together has a large two-person emblem and full visible text. Below 300dp *content* width or at 1.35+ font scale, actions stack rather than clip. Native 48dp+ actions with content descriptions.
- Ws16HomeScreen.kt uses shared actions and Continue bookmark emblem. Current Solo's Home entry is labeled Continue while unfinished and Custom when complete, with real custom setup reached by a small Custom action. My Puzzles persists. Hamburger Menu now uses compact left-aligned native Dialog with working Settings, Help, Records, My Puzzles, Full Shelf when available, and Custom.
- GridMatesShell.kt reduces Home padding/gaps to 12dp/10dp from 20dp/16dp without changing non-Home pages or altering cover carousel art/size. It passes the new Quick Play preview visibility only when QA is active.
- SoloSetupScreen.kt heading eyebrow says CUSTOM BEFORE YOU PLAY; underlying generator and saved IDs remain unchanged.
- Updated three instrumentation source test files for Continue, Daily/Together labels and new menu/Quick Play QA screen.

## Quick Play, intentionally not faked
QA shows an illustrated but NON-CLICKABLE Quick Play row explicitly marked "Mixed-word boards · coming soon". The user requested a new mixed-vocabulary generator (book/theme words plus separate general pool) that works offline even with zero released books. Current Solo only generates single-theme seeded boards, so repointing Quick Play at Solo would misrepresent it. Do NOT ship or make this QA placeholder actionable until the WS-17 versioned mixed-lexicon generator, safe saves, 12/15/20 two-tap selector and Play Another are implemented and validated. Production builds do not display the QA-only preview.

## Test and rollback
Verify Android and art/release-isolation workflows on exact gameplay/assets commit 0fb918c4b967a5b5bfe888289ca03a771e8ea0ac, then provide ONE verified QA APK artifact and checksum. Android test SOURCE compilation is not actual emulator or phone execution. Ask owner to check 320/360dp and large fonts, full Daily Puzzle and Together labels, Quick Play footprint, compact menu, Custom route, carousel persistence, Solo/Daily/LAN and backup/saves. Keep PR draft/unmerged.
Last verified owner baseline before this pass: WS-16A QA artifact https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42704988037/artifacts/13713432908, APK SHA-256 2367bc0e54d0bc11fba9c05cc704763e73c9ec0d4f8ba63a5ebdf81357105d11.

## Next
Address build failures if any, capture exact-source dual-green receipt, then owner handset screenshot test. Only afterward revisit potentially shrinking covers and improving Full Shelf spine arrangements. 
