## Update — owner phone screenshots received; WS-14K1 first small pass implemented (November 9)

**This document started as a deferred plan; the owner has now supplied six real handset screenshots.** See [the evidence-backed WS-14K1 review and change receipt](devlog/2026-11-09_WS14K1_OWNER_SCREENSHOTS_AND_COMPACT_BOOK_HEADER.md). First small UI source code is committed as [794f096e](https://github.com/PlayerEden/GridMates-Word-Search/commit/794f096e387e7d7b65b4de170e41480ec5e5d448): one-row Back/title/Page/Hint header, reduced bookmark decoration, per-book hidden instructional zoom copy, temporary invalid-tap feedback. Solo/Daily and gameplay/save logic were left unchanged.

**Initial WS-14K1 build status:** Android [#42679021745](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42679021745) and art/release-isolation [#42679021766](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42679021766) started on that code; inspect their live conclusions before claiming a new APK. Latest previously dual-green **WS-14J2** remains [APK artifact #13699334694](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42675610221/artifacts/13699334694).

**Screenshot priorities:** The normal Woodland page was *bookmarked*, so it showed bookmark artwork rather than the new standalone fern asset. The 12×12 grid fills the phone width, but top rows crowd the word list; reclaiming header height primarily increases visible word-list space. Screenshot status `Try selecting a word from the list.` is an invalid claim message, *not proof that word tracing is broken*. There is still no explicit 20×20 drag, five-hint, and saved-reopen report.

**Next user-facing validation:** Compare a new WS-14K1 phone screenshot, check tactile word select/zoom, and decide on the next small adjustment. Do not prematurely remove real utility controls, add fake settings actions, replace graphics without review, or collapse the whole library and Solo flows into an untested redesign.

---

# WS-14K — Compact Gameplay UI and Icon Kit (after handset screenshots)

**State: DESIGN BACKLOG / DO NOT IMPLEMENT BEFORE PHONE REVIEW.** Recorded November 9, 2026 from owner preference for a compact, icon-driven book-page UI. It is deliberately postponed until the owner installs the exact WS-14J2 QA build and shares real screenshots. This is not approval of a mockup or a request to replace the whole existing interface.

## Testable checkpoint first

- **Exact tested code:** `01c20de43f4ee157cf9aa7a5b6cac7ea09d830e3` (draft PR #131).
- **Android QA:** [#42675610221](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42675610221), **SUCCESS**. [ZIP artifact #13699334694](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42675610221/artifacts/13699334694) contains `app-debug.apk`. Standard Android tests/lint, `compileDebugAndroidTestKotlin`, debug packaging, source-matched original Woodland candidate sprite verification completed.
- **Art/debug-release isolation:** [#42675610184](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42675610184), **SUCCESS**; the exact Woodland `ws14j` PNG is present only in debug; zero `assets/ws14j/` in release.
- **APK SHA-256 from Android QA job:** `1df127b0fb655f5c9786729e10ca29440e5f1f40e8607c505fa956aa9260f8c5`.
- **Package/version:** `com.playereden.gridmates.wordsearch.qa`, `versionCode=57`, `versionName=0.9.13-visual-kit-foundation-qa` (these are the build's real metadata, not user-facing milestone branding).
- **Unverified:** owner finger tracing on 20×20, repeated progressive hints, saved resume after back/reopen, actual UI look/feel at phone size, installation/signature compatibility, 32dp Woodland accent acceptance. CI success is not phone acceptance. Preserve the last owner-reviewed fallback, export local progress before reinstall, never insist on uninstall without a verified backup.

## What to photograph on the actual phone

1. **Book library and a book's detail page** — library swipe, navigation, cover proportions and labels.
2. **Woodland 12×12 or 15×15 gameplay at first opening** — entire screen including top bar, board, word list.
3. **Woodland 20×20 gameplay** — before hint, after five successive bulb taps, after tracing at least one real word; try Fit/zoom/Select, and back/reopen to check persistence. Record failures in plain language; do not infer pass from screenshots alone.
4. **Space or Garden gameplay page** — confirm existing fallback emblem and no accidental art mixing.
5. If easy, **landscape, smallest phone width or larger text** — note any wrapping, clipping, hidden action or difficult touch target.

These screenshots are design evidence, not proof of functionality without the user's description of actions and saved results. Avoid asking the owner to redo already-confirmed actions.

## Proposed layout direction — subject to screenshots

- **Single compact header row:** Back arrow on the left; a **native short book title + page identifier** centered/flexibly placed; Hint bulb and a small settings/overflow control at the right **only if needed and wired to an existing destination**. Do not introduce inert settings icons.
- **One meaningful secondary line at most:** word progress (e.g. `3/24` with accessible semantics) near the word list or header, without duplicating the same information in title and status. Critical error/saving/recovery messages remain legible in text.
- **Prioritize the board footprint and scrollable word list.** Maintain the 12/15/20 book presets and optional 25/30 Free Play separation; preserve native grid touches and zoom/Select.
- **Icon-only candidates:** back, Hint, bookmark, page navigation, settings, library, restart/replay if safe. **Do not remove labels for difficult or destructive actions:** reserve native text/tooltips/accessibility descriptions and confirmation where needed.
- **Theme:** keep the verified Woodland tiny header accent subtle and optional; Space and Garden use existing emblems until independent candidates pass their own QA. Never overlay illustration on letter cells.
- **Important engineering trade-off:** an icon can reduce the *visible label width*, but must **not shrink the accessible touch target** (typically ~48dp) or force cramped actions into the same line. A reusable vector icon can be more robust than a detailed raster painting for tiny functional controls. Decorative artwork remains a separate small transparent asset family.

## Asset backlog, in order (not generated or implemented by this document)

| Priority | Assets/UI components | Gate |
| --- | --- | --- |
| P0 after screenshots | Reuse/normalize simple native/vector Back, Hint, Settings/More icons; one adaptive header component | Compare screenshots first, preserve 48dp hit areas, accessible labels, keep settings actually functional |
| P0 after screenshots | Clean responsive word-list spacing and progress display; selective status text | Measure available board area at 320/360dp, portrait/landscape and enlarged text; found-word contrast |
| P1 | Bookmark, previous/next page, library and completion utility icons | Only where real navigation/status actions exist |
| P1 | Matching Woodland/Space/Garden transparent page accents at 24–36dp | Independently generated sprites, SHA-256 provenance and owner visual acceptance |
| P2 | Reusable neutral tabs/dividers/badge trims and results flourishes | After functional UI fits; no panel bloat or text baked into graphics |
| Later | Shared GridMates icon/chrome library | Pin IDs/hashes independently for Word Search/Sudoku/Crossword; do not auto-alter other repositories |

## Acceptance / implementation checklist

- Compare **before and after** screen captures with measurable board diameter/area and visible word-list count, not only aesthetics.
- Preserve existing 48dp targets, screen-reader semantics, dynamic text and landscape/large-text fallbacks.
- Check no overlapping actions, status disappearance, clipped 20×20, lost history, or accidental gesture hijack.
- Never swap in an AI status-board screenshot as real UI or import lettering baked into a generated graphic.
- Keep this as a distinct **WS-14K pass after owner screenshot feedback**. Until then the next development step is **owner phone testing of WS-14J2**, not more sprite production.
