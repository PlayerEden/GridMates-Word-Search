# GridMates: Word Search — Master Game Design Document

**Version:** Living master design — WS-07 owner-playtest research addendum, November 7, 2026  
**Status:** [WS-07E v0.8.1 automated QA receipt](WS07E_BUILD_QA_2026-11-07.md): **90/90 API35 tests PASS**, signed install-over preserves Solo/Daily/identity/Race/Duel. The first WS-07E pass reduces Daily Journal scrolling and lets players review already purchased clues without spending more. **Two actual phones' Co-op P0 and human accessibility checks remain untested/deferred**. [Roadmap](DEVELOPMENT_ROADMAP.md) tracks remaining UX, stacked draft branches are unmerged, no production release.  
**Repository:** https://github.com/PlayerEden/GridMates-Word-Search  
**Companion plan:** [Development Roadmap](DEVELOPMENT_ROADMAP.md)  
**Planning rule:** Preserve accepted decisions, clearly label proposals, and revise this document when playtesting changes our mind.

> **The product:** A polished, standalone, offline-first Android word-search game in the GridMates puzzle family. Traditional word-search enjoyment, meaningful solo play, and delightful nearby cooperative and competitive play on two phones. **Good puzzles. Good company.** is a favored tagline candidate, not yet locked marketing copy.

---

## WS-14H2 — Regular illustrated book size ceiling (Nov 9, 2026)

**Owner-directed product decision:** Regular Puzzle Books use **12×12, 15×15 and 20×20 maximum**; screenshots of the compact bulb header on 12/15/20 support this layout. Boards **25×25 and 30×30** are intentional **optional large-board Free Play** choices, not required paperback progression. The existing V2 `bookPresets` and V3 `largePresets` / Solo `soloPresets` support exactly this without engine changes. Future UI may group these large options under **Large Puzzles**, but they are not a new generator or new save format. Keep existing published IDs/hashes/saves and any historical content intact. 100×100 Marathon remains a separate future R&D concept.

**WS-14I source follow-up (Nov 9):** The original total 4-step hint quota on 20×20 was removed in favor of **3 progressive clues per remaining word** (maximum 3 × published targets). [Green exact-source Android/art QA](devlog/2026-11-09_WS14I_HINT_RELIABILITY_AND_20X20_TRACE_TEST_RECEIPT.md) confirms core tests, compiled 20×20 gesture test and a QA APK; **physical word tracing, 5-tap hint use and save/reopen are still pending owner confirmation**. No hint-schema or saved puzzle ID migration. Earlier observation below is historical.

**Important unresolved gameplay QA:** the new 20×20 Hint bulb displays “No hints available” at 0/24 in one owner screenshot. Current rule allows four total 20×20 hint steps before returning null, and storage failures can also produce null; investigate actual reason, then improve feedback and consider hint accessibility without silently changing scoring. Positive visual layout reaction does not prove successful word tracing, precision selection, zoom or persistence. Source/playtest detail: [WS-14H2 device review](devlog/2026-11-09_WS14H2_PHONE_SPACING_AND_BOOK_SIZE_CEILING.md).

## WS-09 owner playtest direction — foundation before expansion (November 7, 2026)

The owner and a family player reviewed **15 actual v0.8.5 Android phone screenshots**. Agreed direction: zero-radius **square actual grid** without clipped corner letters, responsive book-style side margins, calm/readable paperback presentation, and simpler pre-game mode/theme/board/challenge selection instead of configuring the puzzle after entering it. Support multiple unfinished Solo puzzles and eventually one **continuous** 100×100 Marathon board with optional regional navigation (regions are not separate puzzles). Improve giant-board panning, 2-tap selection, word-list accessibility, Home, Records, Collections, Fonts and Settings. Preserve small puzzles, content/difficulty independence, and readable typography. **Do not treat 100×100 as already feasible/implemented.**

**Critical release priority:** A family tester is actively playing an older APK; identify exact version/signer and verify portable export before any destructive migration. Freeze legacy puzzle seed-to-layout behavior, make new generators versioned and verify genuine signed upgrade/restore of partial found words, hints, timers, Daily/profile/match journals. Default QA only; all drafts unmerged. Existing WS-08 beta gate and physical Co-op P0 stay open. Work and exact acceptance criteria live in **[WS-09 foundation plan](WS09_FOUNDATIONS_GAMEPLAY_SAVE_MARATHON_2026-11-07.md)** and the [master roadmap](DEVELOPMENT_ROADMAP.md).

---

## 0. Status key and decision authority

- **DECIDED** = directly agreed in the planning conversation or inherited as an established GridMates series direction.
- **BASELINE PROPOSAL** = recommended starting behavior; implementation and playtesting may change it.
- **FUTURE / OPEN** = deliberately deferred or requires an explicit later design decision.
- **IMPLEMENTED / TESTED** = use only when a verified Word Search source/build/QA receipt supports it. See the current linked receipts and dated implementation appendix rather than assuming historical 2026-11-05 design descriptions are current implementation claims.

**Decided on November 5, 2026:**
1. Word Search is a **separate Android app and separate GitHub repository**, part of the GridMates family.
2. Reuse as much of the proven GridMates: Sudoku experience as practical: native UI patterns, profile concept, existing original animal portrait assets, collections/cosmetics, settings, local achievements, record patterns, local networking/room joining, daily puzzle patterns, and disciplined testing.
3. **Versus has two intended modes:** **Shared Board Race** (race to claim words on one board) and **Speed Duel** (identical private boards; first to finish).
4. Preserve the welcoming puzzle-book-inspired visual identity; work toward a complete, real mobile app rather than a bare puzzle grid.
5. A master plan is useful, but design is iterative. Do not treat untested numbers, layouts, technical choices, or future features as immutable promises.

**Established series direction being carried forward:** offline-capable Solo, deliberate same-Wi-Fi nearby play without required sign-in, easy hosting/joining designed for less technical players, respectful customization, accessible UX, and independent app identity/saves.

**Not yet decided:** exact gameplay grid sizes and scoring balance; whether daily rewards use a currency; cross-game inventory or login; exact accent color/art; release date; online play; paid cosmetic randomization.

---

## 1. Product pillars

1. **Puzzle first.** Finding words must feel accurate, tactile, satisfying, and readable on actual Android phones.
2. **Good company, never mandatory.** Standalone Solo and Daily should be complete experiences; Nearby multiplayer is a reason to return, not a barrier to play.
3. **Familiar GridMates, unmistakably Word Search.** Reuse navigation, character art, style, and profile conventions; replace Sudoku-specific board, solver, modes, and terminology.
4. **Welcoming to older and less technical players.** Big understandable controls, low-friction discovery, optional help, and minimal setup. Avoid busy menus and computer jargon.
5. **Progress you can trust.** Never lose puzzle progress or earned items because of updates, reconnection, app restarts, or an unusual screen size.
6. **Low-pressure reward loop.** Celebrate completion and collections without grind, punitive streaks, aggressive ads, or paid advantages.
7. **Evidence before claims.** Build/test in separate checkpoints; don't call multiplayer or release ready based on an emulator alone.

**Initial audience:** casual word-search players, puzzle-book fans, adults and older users, partners/family on the same Wi-Fi, and people who enjoy friendly races. Children can enjoy appropriate themes; do not assume a child-directed store classification without separate policy/age design review.

**Initial platform:** native Android / Kotlin / Jetpack Compose, matching the current Sudoku stack. Android 8/API 26 as an initial compatibility candidate, subject to build and current Play requirement verification. Sideloadable signed test APK during development; Google Play AAB only after release readiness.

---

## 2. Player modes and scope

| Mode | Experience | Default outcome | Scope |
| --- | --- | --- | --- |
| **Solo** | Classic themed word search; find all listed words at your pace | Completed puzzle, time, word count, help usage | Core |
| **Daily** | One stable puzzle per local calendar date; resume/review history | Dated completion, honest stats, optional future reward | Core |
| **Co-op: Find Together** | Two players on separate phones solve one shared board; identify who found each word | Shared success with individual contributions | Core multiplayer |
| **Versus: Shared Board Race** | Same grid and word list; first valid claim owns each target | Most claimed words; equal count is a tie | Decided |
| **Versus: Speed Duel** | Same puzzle seed, separate boards and claimed-word state | First verified full completion wins; agreed tie/disconnect policy | Decided |
| **Custom Match** | Host chooses theme/difficulty and available mode options | Rules clearly visible before starting | Later polish |
| **Split Word Lists** | Optional co-op variant: each partner initially sees a different subset of targets | Shared puzzle; must communicate | Future / prototype only |

**Launch intent:** all five main experiences (Solo, Daily, Co-op, both Versus types) if each passes required real-device and fairness tests. The first playable milestone is Solo; multiplayer and optional polish arrive in later stages. Do not publish a disabled mode as if available.

### 2.1 General play loop

Home → choose Solo / Daily / Play Together / Versus → choose available options → find words → finish → clear results → Play Another / Review / Home, with an optional Share Result action once supported.

The player must be able to start a normal Solo puzzle without configuring a profile, signing in, or reading a lengthy tutorial. Resuming unfinished games is obvious and safe; starting over does not silently overwrite prior progress.

---

## 3. Word Search puzzle rules and interaction

### 3.1 Board format

- Rectangular grid of uppercase letters, initially English A–Z. Word list is visible and grouped below or adjacent to the board depending on available space.
- Target words may run horizontally, vertically, and diagonally in either direction. Direction sets expand with difficulty.
- A found word is crossed off in the word list and visibly highlighted along its grid path; overlapping discoveries remain distinguishable.
- A player selects a word by tracing a straight line from its first to last letter (or reverse direction). No loose bends, jumps, or approximate dictionary acceptance.
- Accept either endpoint direction for the same legitimate target. A valid traced path must exactly match a pre-generated target location. If the same target is present in multiple locations, behavior must be explicitly defined or, preferably, the generator must avoid ambiguity.
- Final touch confirmation and a short satisfying reaction should be immediate; no distracting celebratory overlay on every word.
- Completing every required target triggers one durable completion result. Merely finding words in filler letters does not complete the objective.

### 3.2 Touch feel and accessibility

**Baseline proposal:** smooth drag selection with live path preview and haptic tick when a valid word is confirmed. The engine snaps to one of eight straight directions once dragging passes a sensible threshold; it does **not** claim arbitrary curved gestures. Snap and cancel must be reliable with either thumb, stylus, or finger.

- Prevent accidental page scrolling while actively dragging on the letter grid, without trapping normal navigation elsewhere.
- Don't require precisely tapping a tiny letter corner. Favor adequate effective target areas, forgiving start/end hit detection, and a deliberate tap-start/tap-end alternative if testing shows drag is hard for some users.
- The board has enough usable room in portrait and landscape; dynamically size cells, word list, and margins. Allow an accessible zoom/large-letter option if a larger grid cannot fit legibly.
- Do not hide completion, partner, or mode indicators beneath notches/gesture bars or keyboard overlays.
- For screen readers, provide cell position/letter labels and an alternate selection method rather than assuming a drag-only board is accessible.
- Cosmetic highlighters must preserve letter contrast and selected/found differentiation; do not communicate ownership by color alone.

### 3.3 Difficulty — working targets, not final calibration

| Tier | Example candidate grid | Typical word directions | Design intent |
| --- | --- | --- | --- |
| Easy | ~8×8 | Horizontal / vertical, mostly forward | New and casual players |
| Medium | ~10×10 | Adds diagonal and selected reverse | Familiar word-search challenge |
| Hard | ~12×12 (or adaptive) | All eight directions; more overlap/distractors | Stronger challenge without microscopic print |

Grid size, word count, word length, overlap, and decoy density are **calibration variables**, not proof of difficulty. Test readability on small phones before locking sizes. Longer boards may benefit from zoom/scrolling and should not be introduced casually.

**Avoid:** artificially rare vocabulary as the only way to make Hard more difficult; tiny letter fonts; a time limit as the definition of Hard.

### 3.4 Generator and validation

Build Word Search's own deterministic puzzle engine. Do **not** reuse Sudoku's solver or board serialization.

- Input: versioned content pack, target words, difficulty policy, optional seed/date, grid dimensions.
- Normalize case and whitespace consistently; define policy for multi-word phrases, hyphens, accent marks, plurals, abbreviations, and language changes.
- Place all words in permitted directions with a bounded deterministic search and retry policy; ensure every target has a legal non-ambiguous location.
- Prefer natural intersections where letters match; ensure crossings do not produce broken target words.
- Fill remaining cells using a controlled alphabet strategy; scan the final board in all accepted directions for unintended duplicate target placements. Regenerate or explicitly handle ambiguous placements.
- Filter accidental offensive/problematic strings in fillers and content. No filter is perfect; curated QA remains required.
- Validate all target paths and lengths; completion checks are based on target IDs/paths, not a guess from a visual highlight.
- Every puzzle has a stable unique ID / content hash, generator version, content-pack version, difficulty, seed, and answer mapping.
- Preserve puzzle identity in saves and LAN messages; future generator updates must not silently mutate a saved board.
- Bound retries and provide curated fallback puzzles instead of a frozen loading screen or an impossible puzzle.
- Use a reusable core engine test suite: deterministic seeds; edge/corner/diagonal placements; reverse selection; crossings; duplicate words; invalid spans; content version drift; impossible placement fallback; repeat/Undo behavior.

### 3.5 Themes/content library

Initial themed pack candidates: Animals, Nature, Food, Places, Seasons, Space, Hobbies, Music, Everyday Life. These are suggestions to pilot, not a promise of an enormous word library.

- Start with a **small, edited, high-quality English content set**. Each category needs enough varied words for the chosen difficulty without excessive repetition.
- Keep source/provenance/licensing records; don't copy a publisher's complete proprietary puzzle book or list wholesale.
- Distinguish family-friendly themes from material requiring age/content consideration. Avoid accidentally offensive filler strings.
- Expand content packs incrementally; deduplicate recent puzzles locally where practical, but do not claim globally unique endless puzzles.
- Future localization requires its own alphabet, word segmentation, input, clue display, and content-quality review; do not assume English A–Z works for every language.

---

## 4. Solo, Daily, hints, and results

### 4.1 Solo

- Choose a theme and difficulty, with accessible defaults / Quick Play.
- Find all listed target words; pause or leave and resume exact progress (same board and claimed paths).
- Optional Undo only for a locally selected incorrect/in-progress highlight, **not** revoking a correctly found target by default. The design may instead use a quick clear/cancel action; decide based on actual gesture testing.
- The timer may show elapsed active time but should not end an ordinary game. Stop/restore reliably across app background, restart, and local clock changes.
- Completed puzzles remain reviewable with their highlighted answers; next puzzle is a clear action rather than a dead end.

### 4.2 Daily

Reuse Sudoku's successful **offline deterministic date-based** pattern, but implement a Word Search-specific seed and content version. Same date + content version should produce the same puzzle for devices with the same pack; do not promise one universal board if app/content versions differ.

- One Daily session per device-local date, resumed rather than rerolled for that date.
- Durable daily history with distinct complete/incomplete state. Previously saved unfinished days remain accessible under explicit bounded retention.
- Completion record is idempotent: reopening/resharing/restarting cannot grant a second completion or reward for that date.
- Hint-assisted completion still counts as completing the puzzle; track the assistance honestly.
- Explain behavior if date/time zone changes. Do not claim online anti-cheat or global timed competition without a backend.

### 4.3 Word Search help ladder — proposal

Hints should be unlimited and helpful even when only one word remains. **Initial levels to test:**
1. **Nudge** — suggest a target to look for or a portion of the grid.
2. **Location** — emphasize a row/region or reveal the starting letter's position.
3. **Reveal** — display an actual target path (or optionally mark that word as found, with explicit confirmation).

Do not silently complete a word or pretend an unvalidated guess is a hint. Repeatedly reopening the same level for the same word should not inflate assistance counts. Completion remains positive; result may be labeled **Assisted** with count and types. Controls shouldn't shame players, run out of uses, or require ads.

For Co-op: initial policy is no hints unless both partners see an explicit shared hint event or host-selected agreement. For Versus: **hints disabled by default** for fair competition. If later allowed in custom matches, use identical transparent terms and separate records.

### 4.4 Results and scoring

Lead with **Puzzle Complete**, **Solved Together**, **Victory**, **Defeat**, or **Tie**; mode, theme, difficulty, actual time, words found, individual contributions, and assistance facts where applicable.

- **Core metric:** found required words / total targets; verified by game state.
- **Solo and Daily:** record time, theme, difficulty, hints, completed date and mode. No numeric score is required for initial release.
- **Co-op:** celebrate shared finish, then show player contributions. Contributions are descriptive, not a measure of intelligence.
- **Shared Board Race:** 1 claimed word = 1 point initially, irrespective of word length. Most claims wins; ties are valid. Weighted points are a later experiment only.
- **Speed Duel:** first verified full-board completion wins; display finishing times/progress and fair disconnect handling. Do not mix this result with Shared Board Race records.
- **Future score formula:** only after calibrated difficulty and assistance penalties exist; never invent rankings, claim global competition, or overwrite legacy result semantics.
- **Share Result:** approved cross-series UX direction — optional secondary button on result screen, local card preview then Android Sharesheet, no automatic posting; respect name/privacy and spoiler rules.

---

## 5. Nearby multiplayer design

### 5.1 Common user experience

- **Two phones, same Wi-Fi**, no account or internet requirement after installation. Same game build/protocol compatibility is required when the current version cannot safely interoperate with older builds.
- Host selects **Play Together** or **Versus**, sets a puzzle/difficulty, and opens a room.
- Joiner taps a discovered room under **Nearby Games**; **open Nearby joining is the easiest default**. Host can explicitly enable **Require a Code**, which is remembered for newly created rooms. Manual address is under help/advanced fallback, not the primary route.
- Show friendly player names and actual original portrait assets. Compact in-game partner strip and collapsible chat; clear **You** and partner name labels. Unread indicator must not squeeze mode title or grid.
- Rejoin preserves the original player's reserved seat/token; a random nearby person may not silently replace a reserved opponent/partner.
- Host owns canonical state; every client claim is validated, saved, and broadcast. No guest-origin authority over claimed words or outcomes.
- Open local joining is explicitly a **trusted-LAN convenience**, not a hostile-network security guarantee. Do not advertise protected codes, tokens, full word-answer coordinates, or private profile data in discovery metadata.
- Give understandable errors for no Wi-Fi, isolation, missing permissions, incompatible versions, full room, wrong code, host loss, and reconnect.

**Design variant:** Word Search must use its own discovery service name / protocol identity and its own application ID so it never offers to join a Sudoku room. The Sudoku app currently has LAN protocol v6 in its v0.11C draft branch; **Word Search should begin its own independently versioned protocol**, not impersonate Sudoku v6.

### 5.2 Co-op — Find Together

- One shared grid, one shared list, both players free to find words simultaneously.
- Each accepted word is credited to the discoverer; partner sees the confirmed highlighted path and name/portrait color.
- If both submit the same word, host accepts exactly one claim; rejected duplicate gets friendly feedback, no double score.
- No winner/loser in Co-op. A shared completion card leads with the team achievement, then contributions.
- Partner's active drag may be shown as a lightweight temporary presence cue; it is not a permanent claim or network authority.
- The optional **Split Word Lists** concept can be explored after plain Co-op works. Never leak hidden target lists through snapshots, chat references, hints, history, or accessibility semantics.

### 5.3 Versus A — Shared Board Race (**decided**)

- Both see the identical grid and public target list.
- Each target is claimed by the first **host-accepted valid claim**. Claimed words show discoverer ownership, confirmed across both phones.
- Every target can score only once. When all targets are claimed: winner = most words claimed; equal totals = tie. A timer is informational by default, not a sudden ending.
- Host's own local input must go through the same validation/order path as guest input. Test and document host/guest latency advantage; don't claim fair ranked play based solely on receipt order. If close-race fairness proves inadequate, use a consistent server-side grace/tie policy in a documented later revision.
- A brief transient line animation can make the rival feel present; never hide the legible letter grid beneath animations.

### 5.4 Versus B — Speed Duel (**decided**)

- Both receive the same puzzle seed/content and start from a synchronized ready signal.
- Each has **private word-found state**; one player's finds do not cross out or reveal the rival's word coordinates.
- Show each player's portrait, current found-count/progress, and a gentle "opponent found a word" activity cue without giving away the word or position.
- First valid full completion received and confirmed by the authoritative host wins; explicitly handle near-simultaneous finishes as possible ties after playtesting. Do not pretend sub-second timestamps are globally precise on local phones.
- Both participants must have a real opportunity to enter the match; agree on what happens when one player disconnects before or during the race. Recommended default: pause match for reconnection within a bounded interval, then provide an explicit host decision or neutral unresolved match rather than silently awarding a win to the connected device.

### 5.5 Host state, fairness, and reconnection

**Core message concepts:** session ID, game/epoch ID, protocol version, mode, player/rejoin identity, puzzle ID + public grid, per-mode target visibility, claim requests with path/target reference and unique operation ID, host-sequenced confirmed events, room/chat/presence messages, completion result.

- Host rejects invalid, duplicate, replayed, stale-session, wrong-mode, and out-of-bounds claims.
- Save validated durable changes **before acknowledging** them; persist enough operation IDs/revisions to prevent duplicate awards after reconnect.
- Never send guest-only/private Speed Duel progress as if it were shared solved paths. **Don't serialize hidden answers to a guest just because they're hidden in rendering**; the public grid and word list are visible by design, but internal answer mapping should be minimized and only mode-appropriate information shared.
- A disconnect pauses or explicitly suspends competitive outcomes; no empty-map victory or silent timer-based match end.
- On reconnect: fetch canonical state, restore valid ownership and completed result, and discard unacknowledged cosmetic previews or tentative claims.
- Chat is local/session-scoped, bounded, safe against oversized inputs, attributable to the authenticated room participant, and removable/reset when a new participant replaces the old one.
- No background hosting, WAN matchmaking, automatic cloud backups, online accounts, or server guarantees at initial launch.

**Physical QA is mandatory** for room discovery, open/password joining, reconnection, two simultaneous word claims, complete Co-op, both Versus outcomes, lifecycle interruption, varied phone sizes and language/font settings.

---

## 6. Screens, navigation, and visual system

### 6.1 Information architecture

1. **Home:** GridMates branding, Continue, Solo/New Puzzle, Daily, Play Together, Versus (with mode chooser), modest profile/avatar access.
2. **Puzzle setup:** theme, difficulty, and any relevant multiplayer option; Quick Play default avoids extra steps.
3. **Playing:** board first, readable word list, progress and timer, hint/help/settings affordances, compact partner strip when applicable.
4. **Nearby/room:** large room choices, host/join, optional code, understandable connection state; no required IP addresses.
5. **Result:** completion, concise stats, Play Another, Review, Home, eventually Share Result.
6. **Journal/Profile:** Records, Achievements, Collections, Settings in a familiar GridMates organization.
7. **Help:** how to find/trace words, reverse/diagonal directions, multiplayer explanation, hint levels, room troubleshooting.

**Navigation principle:** minimize competing equal-weight actions. Important active-play controls must remain thumb-reachable; the puzzle grid should never shrink merely to show oversized decorative panels. Menus and collection browsing should function at narrow sizes and 200% font scale.

### 6.2 Existing GridMates styling to reuse

The accepted Sudoku v0.11C visual direction is warm, native, restrained: cream and paper backgrounds, forest green ink, muted gold accents, purposeful cards, familiar connected-tile mark, strong text hierarchy.

**Existing Sudoku source colors for reference (not mandatory exact Word Search palette):**
- Cream #F4F3EB
- Paper #FFFDF8
- Forest #214F3F
- Ink #263D36
- Gold #8A6729

Word Search should keep a recognizable family identity and may introduce a distinct accent for highlighting discovered words, e.g. muted sea-glass/teal or warm amber. **Exact accent and illustrations remain open to actual device previews.** Avoid flashy gradients, faux aged-paper textures that reduce legibility, or a generic ad-filled word-game aesthetic.

### 6.3 Puzzle board visual details

- Stable clean letters, clear interaction states: idle, tracing, valid-finish, already claimed, hinted, and partner-owned.
- Word-list items become checked or crossed out when found. In Shared Board Race, show ownership alongside the checkmark or color; don't rely on color alone.
- Multiple overlapping highlights must remain readable; draw translucent strokes or fine outlines behind letters rather than covering glyphs.
- Adjustable sound/haptic and reduced-motion respect; celebration effects are brief and nonblocking.
- Portrait and landscape reflow; no fixed-pixel design dependent on one phone.
- On-screen host/join controls don't compete for space with the word grid during active gameplay.

### 6.4 Existing art and brand assets

**Reuse the actual original 12 Woodland portraits** from Sudoku's v0.11C feature branch (not regenerated approximations):

Owl, Fox, Rabbit, Bear, Badger, Hedgehog, Robin, Deer, Squirrel, Otter, Tortoise, Cat.

Source of truth at design-baseline review:
- [Sudoku draft PR #28](https://github.com/PlayerEden/SudokuTogether/pull/28) / branch feat/gridmates-nearby-animals
- portrait mapping in app/.../AnimalPortraits.kt
- source art and provenance manifest in design/woodland/packaged-assets.json
- packaged app resources in app/src/main/res/drawable-nodpi/

Preserve **existing IDs** such as woodland_owl and woodland_fox, original colors, crop/silhouette and asset file hashes. Avoid creating subtly different versions in separate apps. Keep local art/resources and a copied source/provenance/manifest in Word Search when added; later source common assets from a versioned canonical art pack if that becomes worthwhile.

**Known separate proposals:** Ocean portraits and Bug/Insect portraits, designed as small style-matched pilot packs before any large expansion; the complete lineups/unlock rules remain open. In Word Search, possible unique cosmetic types are pencil/marker strokes, highlight colors, line endings, page-border styles and celebratory stamps. Cosmetics must never obscure the word-search letters.

### 6.5 Fonts, consistent rendering, and profiles

Sudoku's current font cosmetics on the v0.11C branch use generic device font families; two different Android phones may therefore not show the same deliberate variation. **For Word Search, prefer fixed, properly licensed, bundled fonts**, with tested clear fallback and contrast. Protect character legibility; avoid stylized faces for default grid letters.

Shared presentation means the same portrait names/IDs and collection UX. **It does not mean cross-app unlock synchronization exists.** Initially, Word Search owns its own local profile/achievements/unlocks. We should not imply that a purchase, stamp, earned portrait or ad-removal entitlement in Sudoku automatically transfers into Word Search. A cross-app profile/entitlement system requires a separately approved secure design.

---

## 7. Profiles, achievements, collections, progression

### 7.1 Core local profile

- Chosen name and original animal portrait or emblem, selected visual style, settings preferences, and honest local records.
- Profile display feels populated and enjoyable to browse without interrupting a new puzzle.
- Separate counts for Solo completed, Daily completed, Co-op completed, Shared Board Race played/won, Speed Duel played/won; optional per-tier personal bests.
- Friend system/remote invites are **not** a first-release requirement; nearby partner identity can remain room-local.
- If an unknown asset ID arrives from an older/newer LAN peer, render a safe fallback instead of crashing or changing saved ownership.

### 7.2 Achievements — suggested initial examples

- **First Find** — complete first Solo.
- **Daily Page** — complete first Daily.
- **Word Collector** — complete N total puzzles.
- **Better Together** — finish first Co-op.
- **First Claim** — win first Shared Board Race.
- **Quick Thinking** — win first Speed Duel.
- **Theme Explorer** — complete puzzles in several distinct themes.

Targets/names are placeholders. Derive achievements from durable, unique game records where feasible; never award duplicates from reconnect/replay/reopening. Avoid mandatory timed goals that disadvantage accessibility settings.

### 7.3 Daily gifts/rewards — proposal, not approved economy

Our Sudoku discussions suggest **Puzzle Stamps** (working name): complete a distinct Daily date, earn a small non-cash reward, and spend it on recurring/rotating curated cosmetics. Another simpler launch approach is fixed milestones at 3/7/14/30 distinct Daily completions with no currency.

For Word Search:
- **Do not** create new exclusive art every day; use repeatable themed packs or modest milestones.
- Missed days should not permanently lock items. No punitive streak reset, forced watch-an-ad reward or mandatory attendance.
- Assisted Daily still counts as a completion, while results track assistance.
- Rewards, if implemented, must be atomic, idempotent per dated challenge, and persisted safely.
- Decide later whether GridMates titles share a common wallet; **assume they do not** for first release.

---

## 8. Monetization and publication boundaries

Carry forward the Sudoku family's decided business direction as the **Word Search starting proposal**, subject to this game's release approval:
- Free-to-install app with a complete playable experience.
- Rare respectful interstitial ads **only after a player has completed and viewed a puzzle, at a later eligible navigation**; no banners or mid-puzzle ads.
- No ads during drawing, help, result celebrations, room join/reconnect, or real-time multiplayer.
- Optional one-time Remove Ads purchase through supported Google Play Billing, not a subscription.
- Ads unavailable/offline → continue normally. No ads to access hints, standard themes, or normal multiplayer.
- Cosmetic purchases/randomized packs are speculative future ideas; do not add paid randomized rewards without separate design, disclosure, legal and platform review.
- Treat each app's purchase entitlement independently unless cross-title commerce is explicitly designed and verified.

**Release is a separate gate:** production Android identity, signing keys stored outside public repo, stable update path, signed AAB, Play test requirements, accurate store screenshots, privacy/data safety and advertising declarations, consent and Billing tests, copyright/brand checks, target API verification, and real-device QA.

---

## 9. Architecture and repo strategy

### 9.1 Separation of concerns

Recommended target layout (subject to early prototype feedback):
- **:app** — Android application, Compose navigation, theme/word-grid UI, Android permissions/services, Android profile/settings.
- **:core** — Word Search puzzle rules, deterministic generator, targets/paths, validity, difficulty, hint strategy, game-session semantics, result computation. Prefer JVM-testable code without Android SDK dependencies.
- **:lan or adapter** — independent Word Search protocol types and host-authoritative sessions; carefully port relevant existing socket/discovery lessons rather than importing Sudoku data models.
- **:storage or adapter** — versioned Word Search saves and recovery, profile/catalog/achievement stores; reusable mechanics, game-specific schema.
- **art/content** — approved original portraits, manifests, fonts/licenses, curated word lists and generator fixtures.
- **tests/docs/scripts/.github** — reproducible QA, docs, manual GitHub Actions, verification.

Start small; an explosion of modules before the second playable game proves reuse is not a goal. Keep a **Word Search-specific engine** rather than an oversized imaginary universal game framework.

### 9.2 Sudoku reuse inventory and guardrails

**Safe to inspect/adapt first:** GameTheme.kt, GameWidgets.kt, HomeScreen.kt, JoinScreen.kt, JournalScreen.kt, AnimalPortraits.kt, CosmeticUi.kt, RoomDiscovery.kt, LocalPlayerStore.kt, DailyChallenge.kt, Achievements.kt, original art manifest and Android/Compose QA workflows.

**Requires substantial decoupling:** GameViewModel.kt, GameSession.kt, HostServer.kt, GuestClient.kt, Protocol.kt, saved Snapshot/Operation models, mode-specific results.

**Do not import as Word Search rules:** Sudoku.kt, HumanSudoku.kt, SudokuAssistance.kt, Sudoku-specific PersonalPlay edit/cell constraints, 9×9 number board, clue solver.

**Source fidelity:** As of November 5, 2026, Sudoku's default main contains mostly docs/workflows, development contains a working Android app, and newer v0.11C UX/Woodland work is in **draft PR #28 on feat/gridmates-nearby-animals**. Don't copy an older branch and assume it includes the accepted newer Nearby/portrait behavior. Use exact verified refs and record the source commit when reusing assets.

**Independence requirements:**
- Separate Android application ID (proposed com.playereden.gridmates.wordsearch) and distinct .qa/debug variants; final ID confirmed before publication.
- Independent icon/title, version codes, save paths, backup/export rules, protocol version/discovery identifier, store listing, signing, and QA evidence.
- Never copy Sudoku's private signing material, saved profiles, cached guest tokens, QA credentials, or non-redistributable fonts.
- Never change Sudoku's package, existing signing identity or save schema as a side effect of starting Word Search.
- The first release cannot join Sudoku games over LAN. A shared library later may reuse code **without merging the apps**.

### 9.3 Versioning and data safety

- Every persisted Word Search data model includes schema version and safe defaults/migrations. Keep saved puzzle content immutable across generator/content updates.
- Persist accepted finds, session identity, mode, result, record ID, time, hints, owned cosmetics, room admission policy and rejoin identity as applicable.
- Save atomically with a recoverable last-good copy. On corrupted/unreadable data, preserve evidence and show actionable recovery; do not silently wipe player history.
- Test upgrade paths using a **real prior Word Search APK signed with the same test identity** once a prior version exists.
- Separate QA install from an eventual production install. APK downloads/artifacts expire; source commits/checkpoints/QA receipts in GitHub must remain the durable backup.
- No scheduled CI that burns GitHub Actions minutes without need; manual or narrowly scoped workflow triggers preferred during early development.

---

## 10. Accessibility, privacy, performance, and quality

### Accessibility

- Clear large text, appropriate contrast, screen-reader support for controls/word list/selected letters, meaningful focus, accessible status announcement of found words.
- Effective ~48dp controls where practical; dense grid cells receive alternate accessible selection/zoom, not a blanket assertion that every letter can be 48dp on a 12×12 board.
- Portrait/landscape, short screens, tall screens, fold/rotation safety, 200% system font, color-vision differences, optional reduced motion, haptics and audio preferences.
- Include introductory How to Play and mode help without forced tutorial walls.

### Privacy and local networking

- No mandatory GridMates account, cloud identity or analytics network to play offline Solo/Daily.
- LAN is for nearby session traffic; be transparent that an open nearby room is discoverable by devices on the same LAN.
- Never send hidden match answers/codes/rejoin tokens in discovery metadata, exported results, debug logs or screenshots.
- Chat and profile names may be shared with nearby peers inside a room; don't upload them elsewhere by default.
- Future analytics/crash reporting/ads require separate inventory, transparent consent where required, privacy disclosures and store compliance.
- Share cards are local on-device outputs and optional; preview identifying details before the OS sharing screen.

### QA definition of done

For every meaningful milestone:
1. Core game unit tests green; determinism and generator validation explicit.
2. Android build/lint/instrumentation evidence; test the actual relevant code commit.
3. Native UI captures at multiple phone sizes, orientation and large system text; review, don't merely count screenshots.
4. Save/restart/update regression, with old builds where relevant.
5. Local multiplayer requires **two actual phones** in addition to socket/emulator tests before declaring reliable.
6. APK checksum and install identity are recorded, plus known-good rollback commit and open issues.
7. No claimed release/monetization/online feature without direct verified evidence.

**Product-level acceptance examples:** a first-time player can discover how to mark a word unaided; all target paths work in forward/reverse directions; the game never awards a false word; reopening retains the identical board; two players never both own one Shared Board Race word; Speed Duel never reveals a partner's private paths; temporary disconnection doesn't fabricate results; portraits/fonts are consistent across devices; ads never interrupt a puzzle.

---

## 11. Milestones and release gates

The [separate roadmap](DEVELOPMENT_ROADMAP.md) is the execution checklist; the master design controls intent.

- **WS-00 Plan & reference audit:** capture this spec, source refs, approvals/unknowns.
- **WS-01 Native foundation:** independent app identity, theme/navigation, asset-pipeline pilot, tests/build.
- **WS-02 Core word engine:** curated packs, deterministic generator, validator and usable grid.
- **WS-03 Great Solo:** theme/difficulty, saved play, tactile highlighting, clear results/help.
- **WS-04 Daily & profile:** deterministic offline Daily, history, collection, local achievements and settings.
- **WS-05 Nearby Co-op:** simple host/join and reliable shared claims; real-device smoke.
- **WS-06 Both Versus modes:** Shared Board Race and Speed Duel, fairness, results and LAN lifecycle.
- **WS-07 Polish/content/rewards:** accessibility, consistent fonts, unique cosmetics, optional sharing and calibrated scoring/rewards only after decisions.
- **WS-08 Beta / release preparation:** full QA, art/content/legal checks, Play setup, ads/Remove Ads only after stable beta and explicit approval.

Each milestone needs an evidence-backed checkpoint before the next larger change; no fixed launch date is imposed.

---

## 12. Open decisions / future discussions

These are intentionally **not** silently decided by this document:

1. Exact initial word list/theme roster and content source/licensing process.
2. Difficulty tiers, actual screen-fit board size, number of targets, and whether Hard needs zoom.
3. Final Word Search accent color, launcher art and highlighter styles.
4. Gesture alternate (tap start/end), diagonal snap threshold and screen-reader selection UX.
5. How to handle a practically simultaneous Speed Duel finish; competitive disconnect/no-show and resume timer thresholds.
6. Whether Co-op hints are available and how both players consent/see them.
7. First-launch Daily reward system: fixed milestones vs Puzzle Stamps vs simply achievements.
8. Initially independent portraits/unlocks vs future shared GridMates identity and ownership.
9. Exact score formula (if any), fastest records, and visibility of assistance in competition.
10. Whether Split Word Lists, timed custom matches, additional boards or more than two players belong in a post-launch update.
11. Whether Share Result is first-beta scope or a follow-up polish milestone.
12. Exact ad cooldown / one-time purchase price and per-title entitlements.
13. Google Play account setup, launch tester pool, release signing, policy disclosures and current Android SDK requirements.

**Change-management rule:** proposed decisions can change in playtesting. Record what changed, why, which milestone it impacts, and whether already-created player saves/LAN protocol need migration. Never retroactively describe a proposal as a tested feature.

---

## 13. References / source trail

### GridMates and current Sudoku
- [Sudoku repository](https://github.com/PlayerEden/SudokuTogether)
- [Current v0.11C draft PR #28](https://github.com/PlayerEden/SudokuTogether/pull/28) — improved Nearby, room code option, 12 Woodland portraits, tests
- [v0.11C QA evidence](https://github.com/PlayerEden/SudokuTogether/blob/feat/gridmates-nearby-animals/docs/V0.11C_NEARBY_WOODLAND_QA.md)
- [GridMates series vision](https://github.com/PlayerEden/SudokuTogether/blob/development/docs/GRIDMATES_BRAND_AND_SERIES.md)
- [GridMates visual identity](https://github.com/PlayerEden/SudokuTogether/blob/development/docs/GRIDMATES_VISUAL_IDENTITY_BRIEF.md)
- [Reusable Android puzzle foundation](https://github.com/PlayerEden/SudokuTogether/blob/development/docs/REUSABLE_ANDROID_PUZZLE_FOUNDATION.md)
- [Original approved LAN co-op design](https://github.com/PlayerEden/SudokuTogether/blob/development/docs/superpowers/specs/2026-10-25-sudoku-together-design.md)
- [Puzzle coach and scoring proposal](https://github.com/PlayerEden/SudokuTogether/blob/development/docs/GRIDMATES_PUZZLE_COACH_AND_SCORING_PROPOSAL.md)
- [Share results UX brief](https://github.com/PlayerEden/SudokuTogether/blob/development/docs/GRIDMATES_SHARE_RESULTS_UX_DESIGN.md)
- [Collections/daily rewards/fonts design](https://github.com/PlayerEden/SudokuTogether/blob/development/docs/GRIDMATES_COLLECTIONS_DAILY_REWARDS_AND_FONTS_DESIGN.md)
- [Monetization and play-testing direction](https://github.com/PlayerEden/SudokuTogether/blob/development/docs/GRIDMATES_MONETIZATION_AND_PLAY_TESTING_PLAN.md)
- [Latest staged Sudoku delivery plan](https://github.com/PlayerEden/SudokuTogether/blob/feat/gridmates-nearby-animals/docs/GRIDMATES_DELIVERY_PLAN.md)

**Important:** These references are evolving documents. Before transplanting code, inspect the current source/PR and verify actual implementation status, rather than assuming a dated planning brief is the final build.

---

## 14. Working agreement for future Codex / ChatGPT sessions

1. Begin with [START_HERE](START_HERE.md), **this master design**, and the roadmap. Report whether the currently requested behavior is DECIDED, PROPOSED, or IMPLEMENTED.
2. Inspect existing Word Search code and current GitHub source status before implementing. Refer to Sudoku's latest verified relevant branch for reuse, not default main alone.
3. Make **one bounded development milestone at a time**, with its own branch/PR/QA; avoid a massive unreviewed port of Sudoku.
4. Before altering app IDs, save schema, networking protocol, ownership, or art IDs, explain compatibility and update safety.
5. Deliver verifiable result: commit/branch, what changed, tests and Android install QA, unresolved issues, artifact/checksum if built, rollback point.
6. Update roadmap status and this document when decisions change. Do not replace player feedback with speculative polish.
7. Never edit Sudoku's repo to complete Word Search work without explicit separate authorization.


---

## November 7, 2026 — implementation and QA status (supersedes original planning-only header)

The original master design is preserved as a decision/reference document; it is not a current list of unimplemented features. For source-matched implementation evidence, start at [START HERE](START_HERE.md), the [roadmap](DEVELOPMENT_ROADMAP.md), and the [WS-06A full QA receipt](WS06A_BUILD_QA_2026-11-07.md).

**Implemented in stacked draft code:** actual Solo/Daily word-search gameplay, active puzzle saves, profiles/achievements, free earned Woodland portraits and collectible board fonts, manual JSON backup/restore, same-Wi-Fi Nearby Co-op with discovery/admission/reconnect and partner chat, screenshot-driven visual/accessibility polish, and **Shared Board Race** (single shared board, first host-accepted word earns a point, match ends after every word, winner/tie). LAN protocol is v5; the QA build is v0.6.0 versionCode 22 with stable public test-only signing.

**Automated evidence:** [49 real API35 Android emulator tests passed](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42343699693); original stable-signed v0.5.9→v0.6.0 APK update preserved existing local Solo/profile JSON. Exact SHA, artifact, checksum and rollback branch are in the [receipt](WS06A_BUILD_QA_2026-11-07.md). Physical two-phone Wi-Fi/race/rotation/TalkBack acceptance still deferred; an emulator cannot replace real devices.

**Still future:** separately playable Speed Duel with coordinated ready/start and private boards; idempotently persisted Versus records and post-match comparison; production Play release/signing; online accounts/servers. Do not imply these are already implemented merely because they are in this master design.

All PRs #30–#42 are still **DRAFT and UNMERGED** while the owner defers physical testing. The live feature stack is PR #42 on #41 on #40 on #39 and older; never develop from the tiny default main tree without reading the current handoff.


## November 7, 2026 — WS-06B local Versus journal update

Shared Board Race completed outcomes are now stored once per room in the existing local atomic profile (schema 4 optional races field) and in exported/restored backup JSON. Nearby Versus Stats show win/loss/tie and recent results without changing Solo/Daily; after each verified finish there is a player-relative comparison of points and word ownership. [Exact source and 52-test Android verification](WS06B_BUILD_QA_2026-11-07.md). A real two-phone Wi-Fi race and actual-device recovery test remain open. Speed Duel still needs a separate private-board authority and coordinated ready/start.


## November 7, 2026 — WS-06C independent Speed Duel

Speed Duel is now implemented separately from Shared Board Race in cumulative [draft PR #44](https://github.com/PlayerEden/GridMates-Word-Search/pull/44), with its own host-owned private word ledgers, per-player readiness and simultaneous GO broadcast, personal word highlights only, public opponent progress counts, and first host-accepted complete board deciding the winner. Near-simultaneous finishes have a deterministic order rather than a forced tie. Original Co-op/Race modes and Solo/Daily/Versus journal remain intact. LAN protocol increases from v5 to v6. [Verified 57-test API35 QA receipt](WS06C_BUILD_QA_2026-11-07.md) includes an install-over from real signed v0.6.1 preserving prior local race journal. Both real phones and anti-cheat security remain unverified; the opponent cannot see a list of discovered words through ordinary client state, but deterministic puzzle seed and unencrypted LAN are not adversarially secure. A distinct Speed Duel persistent result journal is deferred to WS-06D.


## November 7, 2026 — WS-06D Speed Duel records

Speed Duel now has a **distinct persistent winner/loser journal**, not a subset of Shared Board Race history. Only completed, recipient-scoped, host-verified outcomes are recorded. The first record for each hosted room remains immutable across retries and reconnections. Player Stats show separate Duel Played/Wins/Losses and recent matches; backup Export/preview/Restore include Duel history as an optional schema-4 array, with older backups supported and invalid/duplicate awards rejected. No opponent word IDs are exposed or stored as history. The final Duel card shows both counts and save status. [Source-matched 60-test QA receipt](WS06D_BUILD_QA_2026-11-07.md) proves an install-over from original v0.7.0 preserving Solo/Daily/profile and prior Race history. Owner two-device fairness, Wi-Fi loss and actual handset acceptance remain open; LAN traffic is not cryptographically secure. Next is UX/presence polish and potential sharing in WS-06E.


## November 7, 2026 — WS-06E friendly presence and optional sharing

Cumulative [draft PR #46](https://github.com/PlayerEden/GridMates-Word-Search/pull/46) adds a compact, accessible "Playing with someone nearby" panel using real friend connection, local display name, personal contributions and mode-specific stage, with **Speed Duel showing only the opponent count**. Completed, host-verified Co-op, Race and Duel have a player-relative, spoiler-safe optional Share result text preview; competitive result must be persisted before sharing. Solo/Daily's pre-existing share action now also previews exact text before Android Sharesheet. There are no room codes, IP addresses, chat logs, private word IDs, online services or invented Play URL in the exported text. This is **text sharing, not PNG card export**. [63-test exact-source Android QA](WS06E_BUILD_QA_2026-11-07.md) verified a v0.7.1→v0.7.2 install-over retaining Race and Duel journals. The latest Nov 6–7 [Sudoku design briefs](WS06E_NEARBY_PRESENCE_SHARE_QA_PLAN_2026-11-07.md) helped set privacy, preview and co-op cosmetic future design boundaries. Shared cosmetic tiers and no-router/Bluetooth transport remain future proposals. Real two-phone LAN fairness, large text and actual share targets still require owner acceptance.


## November 7, 2026 — WS-06F image result postcards

Cumulative [draft PR #47](https://github.com/PlayerEden/GridMates-Word-Search/pull/47) adds an optional, fully offline **1080×1350 (4:5) result postcard** for completed Solo/Daily/Co-op/Shared Board Race/Speed Duel, based only on already validated spoiler-safe share text. A visible preview gives independent **Share text / Share image / Cancel** choices; image sending uses a read-only scoped Android FileProvider content URI, not permission to other app-private data. The paper/forest/gold styling, bundled Atkinson/Plex fonts, and abstract empty grid match the Word Search aesthetic. No usernames, room credentials, private found-word lists, real puzzle answers or fake stats enter a shared card. [Exact-source 67-test Android QA](WS06F_BUILD_QA_2026-11-07.md) passed including actual signed v0.7.2→v0.7.3 update preserving both Race and Duel histories. The owner's physical Android Sharesheet target-app/scale testing remains OPEN. A future shared Co-op board-theme feature needs its own catalog/selection and accessibility policy; Sudoku's technical display-rank proposal does not establish Word Search entitlements.


## November 7, 2026 — WS-06G Shared Co-op Board Appearance (free preview)

Cumulative [draft PR #48](https://github.com/PlayerEden/GridMates-Word-Search/pull/48) now offers three baked-in free **Word Search** grid looks: Classic Paper, Linen Sketch and Blueprint Study. These have an explicit technical display priority **but no rarity, achievement requirement, paid item or unlock entitlement**. During Nearby **Co-op only**, host and guest can each choose before joining; a host-authoritative common look is resolved at the first acknowledged guest admission and frozen for the whole room, including reconnect. Per-phone board font and personal **Use Classic on my phone** visual override remain independent, as do public green/blue claim markers. Solo/Daily/Shared Board Race/Speed Duel remain Classic and do not borrow temporary looks. The change introduces allowlisted room `look` with **LAN v7**, preserving schema-4 saves. [Exact-source 71-test Android QA receipt](WS06G_BUILD_QA_2026-11-07.md) includes a real signed v0.7.3→v0.7.4 update retaining both competitive journals. The original inspiration was [Sudoku's Co-op themes design proposal](https://github.com/PlayerEden/SudokuTogether/blob/development/docs/GRIDMATES_SHARED_COOP_BOARD_THEMES_DESIGN.md), not a copy of Sudoku's cosmetic IDs or ownership. Real two-phone room-theme synchronization, accessibility at 200% font and hardware network testing remain OPEN.

---

## WS-07 — Owner playtest-driven design correction (November 7, 2026)

**Supersedes earlier provisional touch/layout patterns when they conflict.** Nine real-phone screenshots and a two-phone Nearby session revealed a severe puzzle interaction failure: both phones could connect, yet neither player could mark a word. The pictures additionally showed cropped default zoom, tiny Expert 20×20 touch targets and heavy scrolling through gameplay information. Do not conflate session connectivity with game playability.

### Revised locked UX outcomes

- **Play must feel as natural as drawing a line through a paper book.** Default word drag uses one finger, accurately registers the first pressed cell and the final released cell, previews a straight/diagonal/reversed path and offers understandable invalid/accepted feedback. A second tap to select the last letter is a non-dragging alternative. Never rely on an undiscoverable Find/Move switch.
- **Make zoom optional rather than compulsory.** On opening any 12×12/15×15/20×20 puzzle, show the entire board. Two-finger pinch and two-finger pan are navigation gestures and never submit a word; +/− and Fit are visible, single-pointer alternatives. At 20×20, the fit view is an overview rather than a claim of comfortable precision: research/try a loupe, center crosshair, or non-drag cell navigator. Selected-cell previews, labels and lines must match validated board coordinates.
- **No whole-page gameplay scrolling.** Active Solo/Daily/Co-op/Race/Duel screen uses a stable grid, minimal progress/connection/partner HUD, small visible target preview and accessible actions for full word list, chat, room details and results. Word-list/chat/history/collection/settings sheets or separate views can scroll when deliberately opened; active gameplay never disappears above/below a scrolling page. Adaptive landscape and 200%-font screens must not squeeze a board into unreadable cells.
- **Multiplayer trust remains grounded.** Co-op first valid shared claim, Race one host-confirmed owner and word-by-word final comparison, Duel independent private boards and Ready/GO. Both players must be able to mark a word on real phones before any mode is labeled accepted. Friendly presence uses truthful connection state. Do not reveal partner's private Duel word IDs or paths.
- **Accessibility is not merely enlarged text.** Standard controls should have Android-recommended 48dp touch areas; the 20×20 grid itself is a special spatial input surface and needs alternatives. Avoid requiring screen reader users to navigate 400 ungrouped cells as the sole selection path. Test TalkBack, Switch Access, 200% text, contrast, keyboard access and motor-accessible non-drag equivalents. Celebrate completed words without requiring color-only identification.
- **Ease over additional options.** Keep simple Home > Continue Solo, Daily, Host/Join; optional room codes and styles under secondary options. Settings, profiles, collections, achievements, sharing and saved records remain reachable but do not interrupt a puzzle.
- **Hint and content review remains planned, not an accidental redesign.** The master’s intended hint ladder is generous and should help at the last word; actual shipped Solo/Daily budgets are stricter and must be evaluated. Curated themes, replay variety, and reward/cosmetic presentation should follow usability measurements. Avoid purchasing/ads/online services until independently approved.

### Technical acceptance gates (MUST pass before release candidacy)

1. Finger-down/move/up/cancel, direction lock, coordinate mapping, clipping/scaling, pan/zoom matrix and 2-finger interruption pass deterministic tests on multiple board sizes and orientations; test via actual Compose pointer injection, not only core word validation.
2. **Owner two-phone P0:** each phone finds at least a horizontal, diagonal/reversed, edge and two-tap target; both receive the correct host-approved updated board; no accidental claims on pinch/pan.
3. Whole gameplay view fits in the available viewport without scroll and exposes progress, board and words; mode setup, word list, chat and information use clearly summoned subordinate surfaces.
4. Check width/height adaptation, 320dp and common screen sizes, orientation, 200% system text, TalkBack/Switch Access, feedback, failure/reconnect and back navigation.
5. Full standard CI + API35 emulator + actual same-signer v0.7.6→candidate signed install-over; preserve Solo/Daily/profile/Race/Duel and recorded finished results; real owner acceptance still required.
6. Rollback to verified source and signing identities, review stacked drafts before any merge, and do not publish QA app as a store production release.

### Research references / evidence

The complete evidence, Android and W3C source links, proposed metrics and execution order are in [WS-07 Playtest Research & UX Spec](WS07_PLAYTEST_RESEARCH_AND_UX_SPEC_2026-11-07.md) and the [WS-07A physical checklist](WS07A_REAL_PHONE_TOUCH_ACCEPTANCE_2026-11-07.md). The original WS-06G and WS-06I source records remain historical facts, not evidence the current P0 is fixed.


### WS-07A automated QA result, November 7 (does not supersede physical gate)

The [complete v0.7.7 receipt](WS07A_BUILD_QA_2026-11-07.md) records **78/78 emulator tests passing** following touch geometry/direction correction, same-signer v0.7.6→v0.7.7 preserved save, APK checksum and pinned rollback. The initially failed 15×15/20×20 host/guest swipe tests are green in this distinct run. **No human-operated two-phone Co-op success is claimed.** Continue [WS-07B–G](DEVELOPMENT_ROADMAP.md) only with that distinction and current physical acceptance checklist.


### WS-07B compact first-time entry, automated gate (November 7)

[Verified WS-07B QA receipt](WS07B_BUILD_QA_2026-11-07.md): new compact paperback Home keeps saved Solo/Daily/Together actions readily visible; Nearby places Host and Join before optional look/theme/room code, with manual join disclosed on request. The actual API35 suite passed **80/80 tests** including the 360×720dp primary CTA visibility tests and all previous gesture/regression checks. Live active gameplay remains a fixed board scene; advanced sheets and secondary history screens may still scroll. **Do not claim the owner's two-phone P0 is fixed until the real pair succeeds**. No gameplay rules, LAN v7, schema4, package ID or signer changed.
