# WS-14 — GridMates: Word Search, Full-App Visual Identity and UI Modernization

**Planning checkpoint: November 8, 2026. Status: design specification, NOT implemented or production-approved.**

## North-star reference

The owner supplied a three-screen concept illustration showing: **Play/Home** (illustrated Solo, Daily, Play Together, Puzzle Books cards), **Puzzle Books** (real collectible volumes on beautiful shelves), and **Collection/Profile** (matching portrait frames, badges, category tabs and tile layout).

**Design goal:** an intentional, polished, cozy storybook/paperback puzzle game that feels like a real downloadable Google Play title, *not* an HTML demo with code-drawn ornaments. The **whole app** must share one consistent visual and interaction system, not only the shelf.

**Crucial:** the uploaded mockup is a visual target, **not shippable UI artwork**. Its embedded words, static status, phone frame and simulated puzzle progress cannot be used as actual buttons or data. Produce separate, source-tracked production sprites/illustrations and put readable native Android Compose text, controls and live game data above them.

### Owner priorities and guardrails

- More eye-catching, richer illustrations than existing Study A/B source-generated SVG prototypes; avoid generic, unrelated "AI slop."
- A real bookcase: illustrated physical book objects, accessible navigation and the feeling of pulling a volume from the shelf.
- One consistent art/typography/component grammar on **every** user-facing page.
- Very approachable for older and nontechnical players; no mandatory login or developer jargon in public UI.
- Offline/local-first, easy Solo/Daily/LAN; never compromise puzzle mechanics, score, saves, progress, Collection or existing unlocked portraits for a reskin.

## Source-of-truth and relationship to existing work

- Last verified functional bookcase: **WS-13A PR #122 / signed QA v56**. It implements actual standing book selection, but uses deterministic *prototype* artwork and native code-drawn shelf planks, **not** the finished illustrated production vision.
- Existing Book QA art studies A/B are **not approved final art** and remain as visual rollback/source controls.
- WS-13 epic **#117** owns bookshelf functionality; **#118** book-opening/reading; **#119** generated book illustrations; **#120** actual published library; **#121** release QA. **#91** art governance and **#90** editorial content remain active. This WS-14 umbrella owns *full-app visual cohesion and the asset production system* across these.
- All changes stacked against existing functioning branch, not silently merged into main. Production asset approval is independent from passing CI.

## Exact screen inventory

| Surface | Visual treatment | Functional behavior to preserve |
| --- | --- | --- |
| Play/Home | Illustrated header, mode icons and cohesive card/button hierarchy for Solo, Daily, Together, Puzzle Books | Continue, New game, My Puzzles, Daily, Wi-Fi lobby, book entry |
| Bookcase browse | Real illustrated shelf planks and book covers/spines, small plaques with genuine volume title/progress | Catalog order, stable book IDs, scrolling, tap-to-open, continue |
| Book detail/chapters | Larger pulled-out book, paper table-of-contents, bookmark ribbon and earned seals | Page IDs, valid completion, Resume/Replay, immutable board hashes |
| Solo/setup/My Puzzles | Same cards, headings, preview/selector motifs; minimize steps | All paused games, current run, grid sizes, zoom, hints |
| Puzzle gameplay | Paper-pencil board chrome, compact HUD/hint and polished results, illustrated but restrained | Trace/touch, board legibility, solution geometry, stats |
| Daily | Illustrated calendar / gold-star cue, compact honest daily state | Puzzle seed, schedule/progress, actual stats |
| Nearby/Play Together | Illustrated Wi-Fi/group icon and compatible host/join/status cards | LAN discovery, matching, team/duel mechanics |
| Collection/profile | Portrait frame, real selected/locked tiles, font and achievement tabs, tasteful trim | Original 12 Woodland portraits, IDs/unlocks, name, fonts, achievements |
| Records/results | Same milestone motifs, badges, stats and native chart/number treatment | Real numbers, score, penalties, wins/completions |
| Settings/help/dialogs | One consistent paper panel, icon grammar, empty/loading/error treatment | Existing settings, Android Back, help |
| App identity | Unified launcher icon, splash and eventual Play Store listing | Signed install paths, version history and data |

## Development sequence / gates

### WS-14A — Style bible, asset manifest and native component foundations (START HERE)

1. Define a unified **style bible**: mood, paper texture, brush/outline, cover framing, selected illustration detail density, green/cream/gold system, wood materials, native typography, shadows, spacing, focus/disabled states, motion.
2. Define **asset IDs and provenance** (theme, purpose, source prompt/model where known, source image, owner review, master/export resolution, transparent alpha, safe crop, checksum, license/rights, build flavor). Never pretend an unreviewed source has been approved.
3. Author a small **Compose visual kit** (theme tokens, IllustratedHeader, PaperPanel, IllustratedModeCard, BookCover, ShelfRow, PortraitTile, action buttons, Progress, DecorativeCorners, NavIcons); real text must remain native. Use scalable Compose/9-patch where content size varies.
4. Freeze a visual acceptance matrix for the three flagship screens and one representative phone size before bulk generation.

### WS-14B — Generate actual illustrated artwork and reusable production sprites

1. Generate **2–4 actual Woodland illustration masters** with one art brief (nostalgic illustrated paperback, owl/forest/log/mushrooms motif, no baked title) and compare *both* full-size and shelf-thumbnail appearances.
2. Select a visual anchor before creating corresponding **Space** (ringed planet/night sky) and **Garden** (flowers/watering can/butterfly) masters; lock book-border treatment, palette separation, brush style, detail and perspective.
3. Derive **cover + spine + chapter badge + bookmark + emblem + completion seal** for each book family using matched approved source motifs, not six random independent generations.
4. Build shared decorative family: 2–3 shelf plank components, corner foliage and scenery, divider ornaments, background layers, Solo owl scene, Daily star/calendar, Together Wi-Fi/group, book-stack entry, achievement/collection frames.
5. Review individual sprites **at actual Android dp sizes**; clean AI artifacts by editing/regenerating and export separately with alpha/crop/overdraw-safe bounds. Reusable raw assets should have transparent backgrounds, not include labels or phone chrome.
6. Sprite masters under versioned source directory with exported WebP/PNG under Android resources; no runtime generated images/network service. Art production may be assisted by image generation, but every asset is human curated.

### WS-14C — Vertical slice: Play + Bookshelf + Collection in one install-over QA APK

- Migrate **all three flagship screens together** to the same component system and asset family rather than letting only the bookshelf look finished.
- Play's Solo/Daily/Together/Books entry actions must remain obvious; bookcase must still act like real shelves; Collection preserves original portrait selections and unlocks. Use native live title/progress text, buttons and dynamic tabs.
- Preview on 320dp and normal owner-size portrait phones, at **200% text**, tablet and landscape when supported. No cropped buttons or art obscuring controls.
- Signed test APK installs over previous QA without clearing files. The owner reviews actual screenshots side-by-side against the reference, not just a style-sheet render.

### WS-14D — Finish the remaining game screens and all states

- Apply exactly the same design tokens/components to book-open/chapters/puzzle, Solo screens, Daily, Nearby, results, Records, Settings, achievements, loading/empty/locked/error dialogs and app icon/splash.
- Design **empty/disabled/loading/paused/completed/locked** states, not only ideal screenshots.
- Add only subtle animations if useful; respect reduced motion, time-to-first-action, offline function and battery.

### WS-14E — Quality, release promotion and rollback

- All art rights/provenance, exact master/export SHAs and explicit **owner approval** recorded. Unknown terms stay unknown, not guessed.
- Zero unapproved test books/prototype art inside release APK. No baked dynamic labels or fake progress.
- Run unit/lint/Android instrumentation **execution** as well as compiled tests; screenshots and functional checks on smallest/widest devices, high font scale, TalkBack, portrait/landscape, network-off, app restart, low memory.
- Verify existing Solo/Daily/Book journals and portrait/font unlock state on **install-over**, and consistent 4-tab navigation.
- Optimize PNG/WebP sizes, startup image decode, scroll stability; document Play Store assets, QA tag and known-good rollback.

## Acceptance gates

- At a glance, **Play, Bookshelf and Collection** look like the SAME game and the concept's visual quality—not three differently themed demos.
- Native titles/actions/progress stay readable and correct, with **48dp+ touch targets**, 200% text and a useful screen on 320dp.
- Dynamic book/portrait/score states reflect actual persisted game data and remain correct after relaunch, replay and upgrade.
- No drawing/sprites cover word-grid letters or interfere with gestures/zoom.
- No invented locked book inventory or fake earned badges.
- Independent owner review of *actual* art files and on-phone screen layouts. QA green does not mean visual approval.
- Last known-working APK and original A/B book art remain available as rollback.

## First implementation PR deliverables

**Do not generate 100 unrelated sprites in one shot.** First: one locked style bible + asset register + the initial Woodland illustration master + a reusable native mode-card/shelf/collection tile component specimen. Then integrate the first cohesive Play/Bookcase/Collection vertical slice in a signed QA build. Iterate the art family *before* scaling to every screen.

**Status labels**: PLANNED, GENERATED_UNREVIEWED, QA_INTEGRATED, OWNER_APPROVED, RELEASE_APPROVED. Never conflate them.
