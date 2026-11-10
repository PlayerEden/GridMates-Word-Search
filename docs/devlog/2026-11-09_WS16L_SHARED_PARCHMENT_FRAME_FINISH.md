# WS-16L — Shared parchment and illustrated frame material pass

**Purpose:** Resume finishing the existing Word Search Home visuals after inspecting all PlayerEden repository artifact-retention rules. The owner has not provided the Standard/Fullscreen comparison screenshots for WS-16K and has not chosen a default, so **both modes remain available and unchanged**.

## Scope and exact source
- Production-code commit [`871c03941a29a47f0603b95ac02b28050b497955`](https://github.com/PlayerEden/GridMates-Word-Search/commit/871c03941a29a47f0603b95ac02b28050b497955), continuing the unmerged draft [PR #131](https://github.com/PlayerEden/GridMates-Word-Search/pull/131).
- `GridMatesParchmentBackground.kt`: made the already local, scalable parchment drawing warmer and less synthetic with translucent horizontal pigment pooling, two very soft radial watercolor washes and a deterministic, sparse fiber pass. All marks are generated from local integer coordinates; no random flickering, image files, tiling seams, screen-size assumptions, text, or input interception. The existing global-paper styling remains calm enough for dark green native text.
- `GridMatesIllustratedFrames.kt`: enriched reusable gold-and-paper frames with slightly warm inner-side glazing, narrow upper highlight/lower material edge, and a few extremely faint perimeter pigment flecks. Geometry, native fonts, buttons, 48dp Play target, click/ripple/semantics, content sizing and labels remain intact. Primary green pill retains its existing design.
- No changes to layouts, main menu heights, top/bottom floral placement, sidebars, fullscreen safe insets, system bar controls, stored `DisplayMode`, book titles/content, Local Solo/Daily/Together logic, local save files, QA artwork or release art isolation.

## Reuse and asset discipline
- The **existing** independent source SVG masters remain in `design/visual-system/modular/masters/shared/`. This is a **native Compose material-drawing iteration**, not a claim to have made new painterly PNG sprites or rasterized a whole-screen reference.
- The two native components are shared design primitives and therefore can later be copied deliberately across GridMates puzzle titles; this commit changes only Word Search.
- This pass adds **zero new APK bitmap assets** and no new non-candidate release images. This is deliberate given the account’s recent 0.5GB Actions storage warning. Existing 2–3-day routine retention and safe manual cleanup workflow remain.

## Verification and acceptance
- Exact-source [Android workflow #42761082558](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42761082558) and [QA art/release isolation #42761082413](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42761082413) started; **not yet green at initial log**.
- Last verified installed owner-comparison build remains WS-16K [artifact #13736265925](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42756573168/artifacts/13736265925).
- Once workflows finish, inspect the exact new APK and check that the 8 shelf, 6 illustrated action and 5 floral PNGs remain hash-identical to the previously pinned assets; compare button geometry and navigation as well.
- Owner phone acceptance must still compare **Standard vs Fullscreen**, and then confirm the new paper/frame warmth reads richly but does not reduce label contrast or slow the menu. Do not make Standard/Fullscreen default decision on behalf of the owner. If mobile screenshots reveal crowding or excessive parchment texture, revise the material—not the working game or bookcase.

**Next:** Share one verified QA APK only if CI succeeds; owner compares modes on the same build, then choose default and resume the remaining concrete Home art refinements. No extra puzzle books at this stage.
