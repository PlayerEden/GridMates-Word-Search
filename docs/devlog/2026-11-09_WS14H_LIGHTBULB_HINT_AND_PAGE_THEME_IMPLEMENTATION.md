# 2026-11-09 — WS-14H: bulb Hint, compact puzzle chrome and theme paper

**State:** SOURCE IMPLEMENTED and **source-matched Android + art CI PASSED** on commit `7856282`. Physical owner phone review still pending. NO new asset generation claimed. See [verified APK build receipt](2026-11-09_WS14H_GREEN_ANDROID_ART_QA_AND_PHONE_HANDOFF.md). See the [full WS-14H milestone roadmap](../WS14H_COMPACT_GAMEPLAY_HEADER_AND_THEME_ROADMAP_2026-11-09.md).

## Who / what / where / why

- **Owner decision:** Replace massive full-width Hint button with a small **light-bulb symbol only**; remove the permanent generic “Trace the words on your puzzle page” sentence; place Hint beside book/page title; tint the surrounding page subtly per book; keep actual word grid unthemed for future **unlockable board-theme collections**. Continue toward final layout and a small reusable GridMates art family.
- **Assistant work:** implemented a native outlined 48dp bulb button, compact status/header, QA-only paper tints and tests; fixed a pre-existing WS-14G Kotlin receiver error. Authored this roadmap, linked it into the full-app roadmap and journal. No new PNG/SVG assets generated.
- **Where:** `BookPageGameplayScreen.kt`, new `BookPageHintButton.kt`, `BookPageThemePaper.kt`, and associated Android/JVM tests on [draft PR #131](https://github.com/PlayerEden/GridMates-Word-Search/pull/131).

## Previous state and real build evidence

- Last owner-provided WS-14F screenshots: horizontal Woodland/Space illustrated library, improved Space book detail with Play/Bookmark, Space 12×12 and Woodland 20×20 board. Owner liked direction; no successful word claim or pinch result yet.
- Last green baseline before the header-emblem source: `12977f393cdbba42f4eafe96ae8501aef880a903`, [standard Android #42652363486](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42652363486) and [art #42652363702](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42652363702), both SUCCESS.
- Prior standalone emblem experiment `153cdab85ca0971e66774af0f1710e87f78eb63f` failed [standard #42654077279](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42654077279) and [art #42654077610](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42654077610) due to Kotlin error `BookPageGameplayScreen.kt:272`: `val maxWidth: Dp cannot be called in this context with implicit receiver`. This was **real compilation failure**, not an HTTP download 504.
- WS-14H first feature-source commit `09490ef3d63df58f39843d34f7122fd24bbb221c` corrected that receiver by capturing the enclosing width in `BoxWithConstraints` scope. [Standard Android #42657230403](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42657230403) failed a **different** compiler issue in the new bulb Canvas file: Compose uses `StrokeJoin`, not `PathJoin`. The follow-up source commit fixes this; do not call it green without reading its own runs.
- First art workflow [#42657230390](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42657230390) was still active at the moment the fix was prepared; examine its live conclusion and subsequent latest source runs, don't infer success.
- **Android APK evidence for this pass:** pending. Existing owner-installed QA and last broadly verified fallback must be preserved.

## What code changed (no gameplay engine rewrite)

- The visual Hint action is now **only an outlined light-bulb symbol** within a circular 48dp native IconButton beside the native book/page title. Accessibility content description = `Hint`; disabled while save/session isn't editable; the exact existing `sessionStore.hint(...)` and staged result messages were moved intact into the icon callback. Icon drawn by native Canvas (reusable across GridMates); no new binary or external font.
- Default `status` is now empty and **only rendered when nonblank**, so passive instruction filler no longer reserves height. Dynamic found-word/error/practice/hint feedback still appears.
- QA-active page background becomes Woodland `#F1F3EA`, Space `#F0F2F7`, Garden `#F6F2EA`, keyed directly by `book.theme`; the grid paint, board unlockables, input transform, hints/claims and saved data are left intact.
- Existing `BookArtKind.EMBLEM` display remains QA gated, hidden at narrow/large text sizes and when bookmarked/completed status art takes priority. Native title max two lines with ellipsis when required.
- New `Ws14HHintIconAndroidTest.kt` compiles native semantics/click/enabled behavior; new `Ws14HThemePaperTest.kt` guards three distinct tints. Actual instrumentation **execution** remains separate from CI compilation.

## QA acceptance and rollback

- Standard Android and art debug/release-isolation workflows must pass against the **same latest code SHA**; keep six existing original-derived book art sprites QA only and absent from release. Publish artifact ZIP with `app-debug.apk`, exact source SHA and archive/APK hashes once verified.
- Owner then tests title/bulb visibility in Woodland/Space/Garden at 320/360/430dp, large text and landscape, staged hints, word claim, +/Fit/pinch on 12×12 and 20×20, save/reopen and old book navigation. A screenshot with zero found words is not completion proof.
- Preserve Classic shelves A/B toggle, selected book IDs, 12 Woodland profile portraits, local saves and LAN. Don't install by uninstalling or clearing data. No merge or released-art approval implied.

**Next action:** inspect newest PR #131 code SHA and CI statuses, fix actual compiler errors if any, record a new source-matched build receipt, offer GitHub artifact and collect owner phone screenshots. After real layout acceptance, begin three small independently versioned theme accents per [WS-14H roadmap](../WS14H_COMPACT_GAMEPLAY_HEADER_AND_THEME_ROADMAP_2026-11-09.md). No bulk art production before.
