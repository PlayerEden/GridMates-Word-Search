# WS-13 — GridMates: Word Search, The Real Puzzle Bookshelf

**Owner intent clarified November 8, 2026.**
**Status:** Product direction **decided**. WS-13A physical shelf browse and selected-volume hero are implemented in a stacked, debug-only QA branch; the remaining phases below are *not yet completed*. Actual finished generated illustrations, owner final art approval and production book content are still future work.

## The product promise

**The player enters their own little puzzle library, sees real illustrated paperbacks resting on shelves, taps a physical-looking volume, opens it, chooses a chapter or their saved bookmark, and plays the actual word search printed on that page.**

This is **not** another card list decorated with thumbnails. The shelf itself is the primary browsing surface. Keep the GridMates feeling: **Good puzzles. Good company.** The experience should be calm, appealing, and legible for older players who simply want to open a book and play.

## Player journey, with honest progress

1. **Play → Puzzle Books.** The entry remains in Play. Do **not** add a fifth bottom-nav tab or hide Solo, Daily or Nearby.
2. **Library / physical shelves.** Warm paper backdrop, subtle shelf planks, neat upright book covers with narrow physical spines. A brief shelf label under each displays its real title, progress and one native Open/Continue/Read-again control. The cover itself is also tappable and screen-reader navigable.
3. **Take a book off the shelf.** A prominent full-cover-and-spine hero replaces the dense tiny-emblem-only heading. The real volume title, chapters, progress, Continue and saved bookmark remain native accessible text/actions.
4. **Read the book.** Chapter sections and immutable book-wide Page 1, 2, 3, etc.; chapter artwork as an accent, not a wall of icons. Pages have real Play, Bookmark, Restart/Revisit states. Keep existing accurate hints, trace selection and page generation; never cover the grid with decoration.
5. **Return to library.** Book and page progress survives exit, app restart and install-over; a completed volume has an earned completion seal, never a fake achievement. No double-credit for replay. The same book returns to its place on the shelf.
6. **Grow the library.** As publication-ready volumes are added, the shelf gains real books (no random fake locked volumes). When the collection becomes large, use lazy shelf rows with optional category labels from actual catalog metadata. No forced sign-in, no online content dependency.

## Screen design and behavior contract

| Surface | Desired experience | Technical/UX guard |
| --- | --- | --- |
| Play / Home | Easy **Puzzle Books** entry next to Solo, Daily, Nearby | No fifth tab |
| Library | **Actual upright covers** arranged on illustrated wooden planks | 2 covers/shelf on normal phone; 1 on 320dp+200% text; 3 on wide tablet; lazy rows, no horizontal scroll trap |
| Shelf object | Cover + slight spine visible; tap cover to open; short title/progress label beneath | Cover is a native accessible button with text alternative; separate 48dp text action |
| Shelf ordering | Real catalog order; stable book IDs; position deterministic | No fictional "coming soon" book covers or new save IDs |
| Book selected | Large cover/spine and real title, followed by Continue and chapter/page list | Correct saved bookmark and completed-page status; back returns to shelf |
| Chapter/page | Familiar paper/pencil page layout, restrained chapter art | Book-wide page numbers, 48dp targets, clear progress, real playback |
| Completion | Native completion status, coordinated seal/stamp only when genuinely earned | Replays don't award again, content checksum confirms page solved |
| Collections | Profiles, portraits and fonts remain **separate** from the book library | Original 12 Woodland portraits and unlocks must remain unchanged |
| Art preferences (QA) | Compare original quiet Study A vs richer Field Notes Study B | Owner currently prefers **the extra details of Study B**; original art stays available, choice affects visuals only |

### Visual language

A cozy **illustrated paperback library**, not photorealistic 3D, a generic videogame inventory, or a pile of UI cards. Use purposeful small shelves, modest paper labels, disciplined forest/ink/cream colors, and restrained wood accents. The big illustrations provide character; the native UI must remain quiet enough to read.

Avoid extraneous faux buttons, giant repeated action blocks, gratuitous shine, illegible spine text, low-contrast wood panels behind labels, loud particle effects, or stock-looking mismatched picture styles. The user prefers slightly **more eye-catching details**, **not** more visual clutter. The style direction is **Study B for QA exploration**, but neither A nor B has been approved as finished book art.

## Actual image generation: separate from deterministic vector prototypes

The previous pipeline-generated SVGs were a *system layout proof*, not the finished AI-created illustrated covers the owner originally requested. **Do not call them full AI illustrations.**

**WS-13C editorial art workflow:**

1. Start with **one** owner-selected visual anchor for Woodland: genuine image-generation concepts with a consistent illustrated paperback / nostalgic puzzle-book appearance, no typography baked into the picture. Generate a small controlled set (2–4), compare at true shelf thumbnail size, reject AI artifacts, choose or revise with specific feedback.
2. Reuse fixed composition/line thickness/lighting/material/paper rules for Space and Garden. Variation is in motif/color only: each cover is part of **the same series**.
3. Use that one approved theme image as the cover master; derive matching spine, chapter emblem, bookmark, completion seal and achievement family from controlled crops/shape language rather than inventing six unrelated AI prompts per book.
4. Record model and provenance when known, original prompts, rights/terms, source/export dimensions, versioned image hashes, previews and license notices. If unavailable, mark as unknown; **never fabricate approval or rights evidence**.
5. Review entire 3-book shelf and each opened book in *actual Android QA screenshots*. Human approval must reference exact files and version. Test 48dp thumb, 320dp portrait, 200% font, landscape and TalkBack.
6. Only then replace placeholder resources in QA and, separately, prepare production-approved resources. **The existing prototypes remain an instant rollback path.**

Artwork generation may use an illustration model as part of the creative workflow; at runtime the Android app uses only bundled offline PNG/WebP assets, never an image-generation service.

## Roadmap — order of implementation

### WS-13A · Real shelves / first playable QA checkpoint (IMPLEMENTED IN SOURCE)

- [x] Physical native two-book shelf layout and restrained layered wood planks, not rectangular cards.
- [x] Tap covers or their own labeled native Open action; immutable book IDs and progress displayed beneath books.
- [x] Lazy rows, true catalog inventory, one-column large-type / tablet three-column responsive layout.
- [x] Selected-book larger cover/spine hero, same real chapter/page flow.
- [x] Preserve original Study A, start QA on owner-preferred more detailed Study B.
- [x] Add Android test sources for shelf arrangement, actual cover selection, no invented books and large-type fallback.
- [ ] GitHub Android CI must pass; **real phone review pending**. QA build versionCode 56 is not a production release.

### WS-13B · Make opening/reading feel like a book (PLANNED)

- [ ] Refine the selected-book screen as a clean open paperback / table of contents, not another dense card stack; preserve accessible native text.
- [ ] Lightweight optional open/close page transitions only when they do not complicate navigation or disorient older players; respect reduced motion.
- [ ] Ensure proper chapter marker, real page number, bookmark ribbon and completion seal with verified save/resume/replay.
- [ ] Add persistent navigation hints to get back to shelf; one-handed Android navigation and device-back consistency.
- [ ] Test 320dp, landscape, long titles, 200% font, TalkBack and memory.

### WS-13C · Real curated image-generated book artwork (PLANNED)

- [ ] Produce a genuinely illustrative Woodland anchor set for real visual review (distinct from programmatic SVG geometry).
- [ ] Fix common art system, test Space/Garden siblings at actual shelf size.
- [ ] Derive consistent complete six-asset families, preserve native editable text.
- [ ] Approval/rights/source manifests with exact image hashes; keep prototype fallback until owner decision.
- [ ] Additive Collection art modeled from the final approved style, **not** an uncurated batch of random portraits.

### WS-13D · Library growth and practical organization (PLANNED)

- [ ] Replace clearly labeled QA books with real editorially approved, checksum-stable published pages (WS-10/WS-11); no fake release pages.
- [ ] Expand actual book library incrementally; when enough books exist add native shelf/category organization based on catalog data.
- [ ] Continue last book/page, optionally identify bookmarked volume; do not add accounts, cloud locks or complicated menus.
- [ ] Validate progress migration without renaming book IDs, and ensure scrolling remains performant with many volumes.

### WS-13E · Polish, release QA and maintenance (PLANNED)

- [ ] No clipped covers, controls, labels or navigation at smallest screens/high font scale.
- [ ] Verify physical-phone install-over and multiple reopens across actual book completion, Solo, Daily, profile and Nearby.
- [ ] Zero unapproved prototype assets in release, no image-generation networking, source rights manifest passes.
- [ ] Include user-facing onboarding in one short sentence at most; avoid long development warnings.
- [ ] QA evidence, release notes, version/tag, rollback plan, Play testing when ready.

## Explicit non-goals for the first shelf

No 3D camera, dynamic light simulation, hidden paywall, gacha book-opening animation, internet book downloads, fake locked book covers, mandatory Google login, arbitrary shelves of empty placeholders, fifth bottom tab or rewrite of the word-search engine.

## Exit criteria for first owner playtest

From the signed **Word Search TEST** v56 APK installed **over** v55:
- User can immediately recognize a physical shelf **without** reading an explanation.
- Woodland and Space stand on the first shelf; Garden on the next at normal portrait width.
- Tap a cover to pull out the right real book; Continue/bookmark/page flow still works.
- Space and Garden remain accessible by vertical scrolling; page numbers and real progress stay intact.
- Existing A/B art control and saved word/puzzle/profile data survive.
- Larger text creates one book per shelf; all controls remain accessible.
- User can say what feels right/wrong visually. **No final art approval inferred from technical success.**

**Related:** [Book content epic #90](https://github.com/PlayerEden/GridMates-Word-Search/issues/90), [visual art epic #91](https://github.com/PlayerEden/GridMates-Word-Search/issues/91), [previous card refinement #114](https://github.com/PlayerEden/GridMates-Word-Search/issues/114). The new WS-13 epic should own all future *shelf experience* decisions, separate from source art and lexicon publication.
