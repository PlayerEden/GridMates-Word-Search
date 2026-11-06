# GridMates: Word Search — Multilingual Research & Future Development Plan

**Research baseline:** November 6, 2026  
**Status:** APPROVED FOR FUTURE PLANNING, **NOT AUTHORIZED FOR CURRENT FEATURE BUILD**  
**First additional language candidate:** Spanish (es), after English gameplay/quality and nearby multiplayer parity with GridMates: Sudoku.  
**Source of truth:** Word Search GitHub docs, [Parity-First Quality Plan](PARITY_FIRST_QUALITY_PLAN.md), [Development Roadmap](DEVELOPMENT_ROADMAP.md), [Word Library research issue #18](https://github.com/PlayerEden/GridMates-Word-Search/issues/18).  
**Decision principle:** Make future multilingual implementation cheap and safe; do not delay high-priority gameplay, real Android QA, stable signing, and local co-op/versus now.

## 1. Product vision

Build a multilingual word-search platform without turning a cozy, simple game into a language-settings maze.

- A player can choose **App Language** (menus, help, profile interface, accessibility text) separately from **Puzzle Language** (target vocabulary, word books, clues, definitions).
- An English UI + Spanish puzzle is valid; a Spanish UI + English puzzle is also valid.
- Default remains English until translated, tested content is ready. Show only languages that can genuinely be played, not an empty language selector.
- Languages should be separate, versioned offline **content packs**, with region-sensitive vocabulary and localized topics as needed; not one gigantic English dictionary translated automatically.
- A language-learning or bilingual puzzle book is a *future optional format*, never a prerequisite for enjoying ordinary Spanish word searches.
- Preserve accessible Quick Play: last-used choices, no forced login/network, no modal language decision every launch.
- Daily challenges can be deterministic within a selected puzzle language; both phones in local multiplayer must use identical, validated content.

**Non-goal now:** Shipping Spanish APKs, a translated menu, a bilingual board, new cosmetics, ads, live dictionary APIs, cloud translations, or a language-learning subscription.

## 2. Current implementation audit — what we cannot casually change

Checked the current Word Search WS-04C Kotlin source:

- `core/.../WordSearch.kt` has `WordTheme` enum with three **English-only 14-word** packs: Woodland, Outer Space, Garden.
- `WordSearchGenerator.CONTENT_VERSION = 1`; it shuffles a theme's words, places them on a seeded board, fills from literal `ABCDEFGHIJKLMNOPQRSTUVWXYZ`, and asserts every word uses `'A'..'Z'`.
- `WordSearchPuzzle.rows: List<String>`, `letter(...): Char?`, and the `CharArray` placement/grid logic assume single English ASCII letters per cell.
- `WordDifficulty` currently links Easy/Medium/Hard to 8×8 / 10×10 / 12×12. The separate board-size/challenge design is planned but **not implemented**.
- `SoloRun` stores theme/difficulty/seed/found IDs/time/hints. Existing puzzle identifiers resemble `wordsearch:v1:WOODLAND:EASY:<seed>` and are reconstructed during loading.
- `DailyChallenge` hashes a local calendar date with version 1 and uses fixed English themes; it currently has **no puzzle language or pack hash**.
- Android Compose UI contains hard-coded English copy in multiple screens. A future translation cannot be a simple resource-folder addition.
- The current QA source has passed automated Android compilation and artifacts, but Word Search has not yet reached full executed-device QA parity.

**Risk:** Editing v1 word lists, normalizing saved target IDs, or changing its seeded generator in place could corrupt valid old saves or alter old Daily boards. Treat v1 as frozen legacy behavior. Multilingual support belongs in an explicitly **new v2 generator/content contract** with a migration and old-version dispatch strategy.

## 3. Separate language dimensions

| Dimension | Example | Scope | Rule |
| --- | --- | --- | --- |
| UI language | `en`, `es` | Menus, settings, results, onboarding, accessibility | Independent of puzzle; follow Android app-locale conventions where supported |
| Puzzle language | `en`, `es` | Target words and packs | Chosen per puzzle/book; saved in puzzle identity |
| Optional locale/region | `es-419`, `es-ES` | Appropriate terms, spelling, definitions | Start with broadly neutral Spanish; review locale variants rather than assuming one global set |
| Learning/translation language | `en` for a Spanish vocabulary book | Optional educational explanations | Explicit selection; never secretly translate or change target word spellings |
| Calendar/format locale | user's device preference | Dates, time and number formatting | Do not equate region with puzzle language |

**Future UX sketch:**
- On first launch, UI follows Android language/default English (only locales genuinely supported).
- Free Play uses the saved **Puzzle Language**, **Theme/Book**, **Board Size** and **Challenge** settings; Quick Play remains one tap.
- Book details show available language editions; switch languages without changing the entire app UI.
- In a LAN lobby, host chooses the shared puzzle language and pack version; guest can join only with a compatible pack, with a friendly explanation otherwise.
- A future system UI language switch never silently resets an active puzzle.

## 4. Spanish linguistic design — do this correctly

### 4.1 Characters, case, normalization

**Proposed default:** preserve accents and `Ñ` in both the displayed target and the playable grid. The alphabet for an initial Spanish word-search policy includes `A–Z`, `Ñ`, and accented vowels where selected words require them (`Á É Í Ó Ú Ü`). Do not assume every letter needs equal filler probability.

- `AÑO` and `ANO` are *not interchangeable*. **Never strip the tilde from Ñ**.
- `PAPÁ` / `PAPA` and `SÍ` / `SI` illustrate why dropping vowel accents can change meanings, not just typography.
- Canonically equivalent composed/decomposed Unicode input must compare equal after **NFC normalization**, without erasing meaningful differences. Test `Á` versus `A + combining acute` and `Ñ` versus `N + combining tilde`.
- Use locale-aware uppercase/normalization policy, then **grapheme-cluster-aware** word length and board cells. Current Kotlin `Char` and `CharArray` are *not* a general multilingual cell model; don't extend them by simply adding a few Unicode literals and claiming all-language support.
- For initial Spanish, a reviewed fixed set of allowable single-cell graphemes can make the engine manageable. Future languages may have multi-code-point graphemes and other writing-direction requirements.
- Use explicit per-pack normalization rules and a pack version. Changing accent treatment later is a content/version migration.
- Do not automatically filter or infer target words from random filler on screen; word claims are accepted only for verified placed targets. Detect unintended duplicate full target occurrences under the pack's exact matching policy.

**Open product decision, not preapproved:** An optional accent-simplified puzzle mode may improve small-screen legibility, but must first undergo native-speaker review, collision checks, and accessibility tests. Do not introduce it in the first Spanish pilot.

### 4.2 Vocabulary, spelling and culture

- Avoid word-for-word machine translation of Woodland/Space/Garden. A translated list might be too long for the board, unfamiliar, region-specific or a different part of speech.
- Begin with broadly understandable Spanish items, then add `es-419` and `es-ES` variants if warranted. Examples of regional differences to review include `COCHE/CARRO` and `ORDENADOR/COMPUTADORA`; don't assume they have identical usage everywhere.
- Maintain regional/semantic tags and a native-speaker editorial review, especially for animals/plants/proper nouns and potentially offensive or ambiguous terms.
- For game-only puzzles, a visible word list is enough. For optional learning books, store **short human-reviewed definitions**, language tags and sense links; definitions may have different licenses than word spellings.
- Difficulty is primarily **direction permissions/reversals**, followed by crossings, length mix, clue/word familiarity, filler similarity and board size chosen separately. Rarity alone does not prove search difficulty; unfamiliar spelling sometimes sticks out and is easier to spot.

### 4.3 Phrases

- Display e.g. `ARCO IRIS` naturally in the target list, and map it to an explicitly approved no-space grid token `ARCOIRIS`.
- Do not strip every hyphen/apostrophe/diacritic automatically. Reject ambiguous/unsupported phrases in editorial validation.
- Count grid **cells**, not UTF-16 code units or display characters, for word length, placement and hit testing.

## 5. Word library and licensing research

**Research findings are leads, not a legal green light to redistribute datasets.** Each source's exact release and licensing terms must be reviewed before including words, definitions or frequency data in an APK or exporting a public derived list.

| Source | What it could help with | Verified caution / next research |
| --- | --- | --- |
| [wordfreq](https://github.com/rspeer/wordfreq) | Multi-language frequency estimates, including potential Spanish familiarity guidance | Its code is Apache-licensed, but its data files include **CC BY-SA 4.0** material and the maintainers explicitly warn against a naive CSV redistribution. Use as an *internal candidate research tool only* until a documented compliant-use decision is made |
| [wooorm/dictionaries](https://github.com/wooorm/dictionaries) / [Spanish license](https://github.com/wooorm/dictionaries/blob/main/dictionaries/es/license) | Spanish Hunspell spelling and morphology candidate checks | Repo tooling is MIT, **individual dictionaries retain separate licenses**; Spanish data advertises GPL-3+/LGPL-3+/MPL-1.1+ choices. Legal/redistribution review required |
| [Universal Dependencies Spanish-AnCora](https://universaldependencies.org/treebanks/es_ancora/index.html) | Lemmas, part-of-speech and Spanish text examples | Listed under **CC BY 4.0**; corpus text and publication provenance need review |
| [Universal Dependencies Spanish-GSD](https://universaldependencies.org/treebanks/es_gsd/index.html) | Complementary Spanish lemmas and corpus patterns | Listed under **CC BY-SA 4.0**; verify any embedded-text rights and packaging obligations |
| [ICU/CLDR locale guidance](https://unicode-org.github.io/icu/userguide/locale/) | Locale IDs, regional variants, culturally appropriate formatting | Reference standards; not a vocabulary pack |
| [Unicode Normalization UAX #15](https://www.unicode.org/reports/tr15/) | NFC canonical-equivalence rule | Follow the standard; do not invent normalization for each language |
| [Unicode Text Segmentation UAX #29](https://www.unicode.org/reports/tr29/) | Grapheme/word segmentation | Board representation must not depend only on JVM `Char` |
| [Android per-app languages](https://developer.android.com/guide/topics/resources/app-languages) | Actual native app-language support | LocaleConfig availability and AndroidX compatibility need a proof-of-concept on our Compose/Activity stack |

**Source policy:**
1. Maintain an offline **master authoring database** with source URL, license, release/date/hash, attribution obligations and review status per imported data set.
2. Keep vocabulary **candidate** words separate from **editor-approved game** words. Human review is essential for thematic fit, age appropriateness and regional spellings.
3. Do not scrape commercial dictionaries, RAE pages, online translator interfaces or search result text into the shipped library without permission and usage-right verification.
4. Prefer shipping only small reviewed content packs with original definitions and necessary attribution/NOTICES; don't embed a full scraped dictionary or unreviewed corpora by default.
5. The team must document where any derived frequency/familiarity bands came from and whether their redistribution is permitted. If unclear, use independently edited qualitative bands without copying restricted raw datasets.

## 6. Proposed future content and save contracts

**Illustrative schema/design, NOT IMPLEMENTED.**

### 6.1 Language pack
- `pack_id` (e.g. `woodland-es`), `language` (BCP-47, e.g. `es`), optional regional variant, `pack_version`, `normalization_policy`, `generator_version`, and `sha256` of canonical packed content.
- Each word entry: stable `word_id`, `display` text, `grid_graphemes`, theme and semantic tags, optional definition/translation, editorial status, regional suitability, difficulty/familiarity provenance, source/license.
- The same semantic concept may have distinct entries in English and Spanish; do not reuse the English spelling as the stable word ID.
- Explicit review/validity rules: unique grid tokens, no semantic collision created by normalization, valid allowable glyphs, lengths fitting boards, no accidental second target occurrence, age-safe vocabulary, bounded generator performance.

### 6.2 Puzzle identity and compatibility
- Future IDs should encode **engine version, language, content pack ID+version/hash, theme/book/chapter, challenge, board dimensions, seed, and any relevant normalization policy**.
- On loading older v1 saves or Daily entries, **dispatch to the preserved English v1 generator**. Never reinterpret old v1 IDs using the newer pack.
- New saved games preserve the puzzle's pinned content identity, selected target IDs/positions or immutable manifest when needed. Updates must not mutate previously issued Daily or chapter boards.
- Migrating persisted schema beyond the current version 3 needs an explicit read-migrate-write plan, backups and Android upgrade tests. An unknown future save schema must stay protected.
- Changing **UI language** must not modify active puzzle IDs, words, elapsed time or unlocked portraits. Changing **Puzzle Language** applies to the next new puzzle; never silently translate/reskin an unfinished grid.
- Save local records by language without discarding existing lifetime totals: allow English/Spanish filters, difficulty/mode filters and language-specific achievement descriptions.

### 6.3 Daily and multiplayer
- Proposed Daily key: stable date (existing local-date policy), **puzzle language**, content pack digest and generator-policy version. Define whether region edition participates. No hidden server-dependent generation.
- Two players choosing `es` should agree on the exact **same canonical word list, glyph rules, seed, board, target IDs, and SHA-256 pack hash**; validate in room join handshake before any claims.
- Host-authoritative claims transmit **path coordinates + target ID + revision**, not a guest-supplied word string as proof. Keep replay/duplicate-claim tests and mismatch error UI.
- Local chat stays user text; do not silently auto-translate private chat or use an external translation service.
- For Shared Board Race, accept first host-validated matching claim, regardless of UI language. Co-op partner presence and results can be translated per-player without altering shared mechanics.

## 7. Android localization implementation research

Android 13+ can offer per-app language preferences; official Android guidance recommends automatic LocaleConfig generation when the translated resource set is genuinely ready. For older versions, AndroidX/AppCompat provides compatibility APIs, but **our app currently extends ComponentActivity**; evaluate the required Activity/AppCompat integration and lifecycle behavior first rather than copying snippets into production. [Official Android guide](https://developer.android.com/guide/topics/resources/app-languages).

Implementation requirements when scheduled:
- Externalize existing hard-coded English Compose strings into Android string/plural resources: Home, Daily, Results, Hint/Feel, Collection, Settings, Backup/Restore, Error states, accessibility labels, onboarding, achievements, multiplayer rooms and notifications.
- Translate and **proofread** human-facing Spanish strings; use real plural/quantity resources, not English concatenation or naïve `"1 words"`.
- Do not advertise Spanish in Android settings before all supported critical screens are localized.
- Verify Android 13+ per-app languages and an Android 12/earlier fallback; test English-device/Spanish-UI and Spanish-device/English-UI combinations.
- Layout needs flexible text sizing (Spanish translations may be longer), dynamic type/200% fonts, TalkBack, narrow/landscape support, icon-only controls with accessible descriptions.
- Confirm settings/history, name, and Daily are unaffected when the app locale changes during backgrounding/resumption. Localized dates/numbers should not alter canonical save IDs.

## 8. Future development stages and concrete gates

**Scheduling principle:** Research/specification now; implementation only after quality parity and roadmap reprioritization. Each phase can create its own draft PR and known-green rollback checkpoint.

### ML-00 — Research and decisions (THIS DOCUMENT; no APK)
- Catalog sources, licenses, data-safety issues, Unicode rules and initial regional vocabulary policies.
- Define English/Spanish UI-versus-puzzle separation; preserve v1 English generation.
- Write a test fixture set for accent, Ñ, string length, Daily and save compatibility.
- **Done when:** documented decisions/issues exist, and no current gameplay/release work is blocked.

### ML-01 — Engine language-readiness (later, before distributing Spanish words)
- Refactor *new* generation to grapheme-aware cells and per-language alphabet/filler profiles, independent board size and difficulty profiles.
- Keep legacy English generator/IDs/save behavior intact. Add per-language pack loader and constraints.
- Add deterministic seeded replay, collision detection, bounds, load-perf and snapshot tests across both language packs; benchmark on budget Android hardware.
- **Done when:** existing v1 saves/Daily still reconstruct exactly; new v2 fixtures are deterministic and no Unicode word length/case collisions are observed.

### ML-02 — Small, curated Spanish proof pack
- Pilot around **150–300 reviewed words** across three to five themes, not a 100,000-word dump. Native speaker/editor checks spelling, accenting, region neutrality, theming, content age suitability and definitions.
- Test Relaxed/Classic/Expert settings at Compact/Standard/Large boards; quantify word acceptance rates, generator retry/failure, incidental target duplicates, average repetition and readability.
- Introduce language choice only in the **new-puzzle** flow. Existing players continue English by default.
- **Done when:** all reviewed Spanish words are placed validly, 0 mismatched published snapshots and a phone playtest approves difficulty/legibility.

### ML-03 — Full Spanish UI localization and release polish
- Localize player-visible Compose strings, tutorial, hints, results, achievements and error/backup flows; verify grammar with fluent Spanish reviewers.
- Implement system per-app language integration once Android setup proof passes, plus older-Android fallback.
- Test accessibility, high font scale, narrow phones, TalkBack and mixed UI/puzzle language combinations.
- **Done when:** no critical untranslated screen, loss of local save, wrong quantity/string, or silent UI-language board reset.

### ML-04 — Daily, Nearby and Versus multilingual QA
- Language-specific repeatable Daily with pinned policy; deterministic cross-device fixture hashes.
- Host/client pack matching for Co-op, Shared Board Race, and Speed Duel, including intentionally mismatched client versions and clear fallback errors.
- Real two-phone English/Spanish UI combinations and matching Spanish puzzle sessions.
- **Done when:** two Android devices get identical board/claims/results; no version exploit or duplicate award; reconnect and save migration work.

### ML-05 — Puzzle books and broader library scale
- Ship carefully edited Spanish chapters and optional bilingual/learning books with original licensed descriptions. Grow from hundreds to thousands after testing, not from raw corpus size claims.
- Add regional additions (`es-419`/`es-ES`) only when the distinctions materially improve play and editorial support exists.
- Evaluate more languages (French/German/Portuguese/Swedish) via the same architecture and source-rights check, not by copy/pasting Spanish rules.
- **Done when:** topic quality, accessibility, performance, license notices and app-size goals are demonstrated for each new pack.

## 9. Test matrix and must-pass fixtures

| Fixture / situation | Expected outcome |
| --- | --- |
| `AÑO` vs `ANO` | Remain distinct; no automatic Ñ→N conversion |
| `PAPÁ` vs `PAPA`; `SÍ` vs `SI` | No accidental collision in accent-preserving policy |
| Precomposed `Á` vs `A`+combining acute | NFC-normalized equivalent same playable glyph; one board cell |
| Precomposed `Ñ` vs `N`+combining tilde | Canonical equivalent, distinct from plain N |
| `ARCO IRIS` display vs `ARCOIRIS` board | Explicit phrase mapping and exact number of cells |
| 8×8/10×10/12×12 with long accented words | Bounded generator rejects unfittable entries, never clips grid or loops infinitely |
| Same (pack/version, language, settings, seed) on two devices | Same target IDs, board glyphs and accepted path coordinates |
| Same date but English vs Spanish Daily | Distinct language identities, reproducible inside each language |
| Switch UI Spanish while English Solo active | Board, IDs, progress, time and results unchanged |
| Switch puzzle language with unfinished Solo game | Explicit new-puzzle choice; no silent loss of current progress |
| Load existing English v1 save/old Daily | Exact legacy puzzle snapshot restored, records preserved |
| Import schema-1/2/3 backup after new schema migration | Save-safe migration, no fake reset/no overwritten unknown schema |
| Multiplayer pack mismatch | Host/guest cannot falsely play same room; clear compatible-pack message |
| Spanish long text with 200% font/landscape | Navigable, readable, no invisible buttons or overflow |
| Spanish audio/TalkBack announcements | Natural spoken words, sensible descriptions; Ñ/accent characters announced correctly where possible |
| Unsupported language/dialect pack | Clear fallback and no advertised but unplayable option |

**Additional property-based QA:** sample thousands of deterministic seeds per board/challenge/language, collect generation success rate, unintended duplicates, placement density, timing and output snapshots. Native Android *execution* is mandatory; compiling instrumentation sources is insufficient.

## 10. Risk register

| Risk | Severity | Mitigation |
| --- | --- | --- |
| Existing save/Daily break due to changed words or generator | Critical | Legacy v1 dispatch, pinned pack hashes, before/after snapshot and real Android upgrade tests |
| Accent/Ñ stripping creates collisions or incorrect vocabulary | Critical | NFC with distinct letters; phrase and collision validators; editor-reviewed test fixtures |
| Corpus redistributions violate licenses | High | Source audit and clear license notices; restricted data stays authoring-only pending legal review |
| Spanish UI but English-only unlocalized workflows | High | Translation completion gate before advertising Spanish app locale |
| LAN clients have different content versions | High | Version/hash handshake and host-authoritative claim validation |
| Huge library bloats APK or creates poor puzzles | Medium | Offline master DB separate from curated shipped packs; measure pack size and performance |
| Machine translation introduces unnatural or offensive entries | Medium | Native-speaker editorial acceptance, age tags, region-neutral pilot |
| Internationalization work delays game completeness | High | Defer build to after Sudoku-parity goals; only nonblocking design guardrails now |

## 11. What developers should keep in mind **right now**

While implementing current English Solo, Daily and Nearby systems, choose boundaries that won't fight multilingual support later:

1. **Do not modify the v1 English puzzle generator or existing v1 word lists in place**; any new vocabulary/difficulty engine gets a versioned contract.
2. Keep puzzle **content IDs, UI presentation text and locale-independent game state separate**. Avoid deriving state identity from translated labels.
3. When touching screens anyway, prefer Android string resources rather than adding more untranslatable English literals; don't translate incomplete screens just to tick a box.
4. Put content-pack version/hash slots into *new* network/save protocol designs before freezing those formats, but avoid an unnecessary destructive schema migration today.
5. Write testable generation rules independent of Android view state; do not assume all languages use `A–Z` or one UTF-16 `Char` per visible glyph.
6. Save the selected language per *future puzzle*, not as a global replacement for unfinished boards.
7. Keep the Spanish project as a separate later milestone with visible gates; prioritize **Android QA, stable signing, puzzle feel, English library quality, nearby Co-op and Versus** first.

## 12. Open decisions for owner/editor when the milestone is activated

- Spanish edition positioning: simple Spanish-language play, optional language learning, or both?
- Preferred first Spanish editorial audience: broadly neutral `es` with region tags vs an explicitly US/Latin-American-oriented `es-419` set?
- Whether the first Spanish puzzles preserve all vowel accents (recommended) or offer a carefully validated simplification option.
- Which 3–5 themes and whether players can freely mix word languages in a session (recommend **no mixing** for the first edition).
- Whether Daily offers one puzzle per language or a single globally promoted language; content-key policy must be frozen before publication.
- Attribution/rights review outcome for frequency, morphology and definitions.
- Native Spanish review staffing and acceptable error/quality thresholds.

## 13. Researched references

1. Android Developers, **Per-app language preferences** — https://developer.android.com/guide/topics/resources/app-languages
2. Android Developers, **Support different languages and cultures** — https://developer.android.com/training/basics/supporting-devices/languages
3. ICU/Unicode, **Locales and Resources** — https://unicode-org.github.io/icu/userguide/locale/
4. Unicode, **Normalization Forms (UAX #15)** — https://www.unicode.org/reports/tr15/
5. Unicode, **Text Segmentation (UAX #29)** — https://www.unicode.org/reports/tr29/
6. ICU, **Case Mappings** — https://unicode-org.github.io/icu/userguide/transforms/casemappings.html
7. rspeer, **wordfreq readme/license/corpus cautions** — https://github.com/rspeer/wordfreq
8. wooorm, **Hunspell dictionaries and individual Spanish license** — https://github.com/wooorm/dictionaries and https://github.com/wooorm/dictionaries/blob/main/dictionaries/es/license
9. Universal Dependencies, **Spanish AnCora** — https://universaldependencies.org/treebanks/es_ancora/index.html
10. Universal Dependencies, **Spanish GSD** — https://universaldependencies.org/treebanks/es_gsd/index.html
11. GridMates: Word Search, **current WordSearch.kt and DailyChallenge.kt** — repo source branch `feature/ws04c-verified-woodland-assets-2026-11-05` (reviewed November 6, 2026).

**Research disclaimer:** The links support engineering and preliminary source/license facts. They do not establish a legal license for a packaged commercial vocabulary dataset or that any multilingual functionality is already in Word Search.

---
**Roadmap relationship:** This is a future specification and research handoff. Current authoritative shipping focus remains **Sudoku-level quality parity, genuine Android runtime QA, reliable offline data, and nearby multiplayer**, as documented in `docs/PARITY_FIRST_QUALITY_PLAN.md`.
