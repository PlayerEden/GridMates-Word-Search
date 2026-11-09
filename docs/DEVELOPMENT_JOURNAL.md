# DEVELOPMENT JOURNAL — GridMates: Word Search

**Start here when a chat fills up.** This is a *chronological, evidence-backed* development journal, not a replacement for the current source tree. It tracks the **who, what, where, why, previous state, build outcome and next objective** for each meaningful development pass. Older milestones stay available for history; newer entries supersede them explicitly.

**Current workstream:** [WS-14 illustrated paperback UI](WS14_ACTIVE_HANDOFF_2026-11-09.md).  
**Full asset pipeline:** [Art Creation and GitHub Integration Playbook](ASSET_CREATION_GITHUB_INTEGRATION_PLAYBOOK.md).  
**Latest build source-matched evidence:** [Android QA #42576785096](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42576785096) and [6-sprite art QA #42576785170](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/42576785170), both **success** on source commit `00d2a36d12c0410a5f04b780ff8c3fe83f69c841`. See [APK field test](WS14C_ILLUSTRATED_APK_PHONE_REVIEW_2026-11-09.md).

## Latest development passes (newest first)

| Date (UTC) | Pass | Previous → new state | Outcome | Next |
| --- | --- | --- | --- | --- |
| 2026-11-09 | [WS-14D — first on-phone visual review and horizontal-library proposal](devlog/2026-11-09_WS14D_FIRST_PHONE_VISUAL_REVIEW_AND_LIBRARY_DIRECTION.md) | Illustrated QA APK only CI-verified → owner shares 5 real handset screenshots, says style works, requests grounded art and better browsing | Actual bookshelf + book detail visually reviewed; vertical shelf/detached spine issues documented; **design-only**, no new APK | Prototype a **QA-only horizontal featured-book browser** with attached spine/shadow and accessible nav; test against original layout |
| 2026-11-09 | [WS-14C-LOG — APK handoff recovery & persistent journal](devlog/2026-11-09_WS14C_APK_LINK_AND_PERSISTENT_JOURNAL.md) | CI-green illustrated APK → user reports "Library file not found"; process had no unified session log | Verified exact APK bytes locally; identified GitHub-hosted artifact URL; introduced mandatory per-pass journal structure; **on-phone installation not confirmed** | Have owner use GitHub artifact and report installation/screenshots; review six covers/spines before more art |
| 2026-11-09 | [WS-14B5 — Binary art import & QA evidence](WS14B5_PINNED_SHEET_IMPORT_2026-11-09.md) | Text-only art/import scaffolding → actual 3,129,981-byte PNG source committed and six sprites imported to debug APK | Source `00d2a36`; Android QA + art isolation **both passed**; zero WS-14 sprites in release | Owner physical visual/style review; don't confuse candidate replacement art with earlier OpenArt concepts |
| 2026-11-09 | [WS-14C — illustrated phone review plan](WS14C_ILLUSTRATED_APK_PHONE_REVIEW_2026-11-09.md) | Approved creative direction and initial component kit → real A/B preview route and dedicated test APK | Real 3 covers and 3 spines; functional bookshelf and native Art Lab preview; **handset QA pending** | Test A/B in actual Bookshelf, opened page, Collection and Play |

The last two entries summarize earlier work from source-backed docs and runs; they are **not** an invented full history of every previous conversation. Future entries should be added as the work happens.

## Mandatory development pass protocol

**Create/update a journal entry for every development pass that changes code, assets, tests, UI, plans, or release decisions—even if the pass fails or becomes blocked. Every APK build or test artifact must also get an entry.** Keep this human-readable; concise is fine for a tiny change. Use [the journal template](devlog/ENTRY_TEMPLATE.md) and place an immutable pass file under `docs/devlog/YYYY-MM-DD_<MILESTONE>_<SHORT_SLUG>.md`; add a newest-first row here.

For each pass, record:

1. **Who** — owner direction vs assistant implementation vs automated CI verification (never imply owner accepted what they haven't seen).
2. **What and why** — objective, problem or feedback, desired player-facing outcome.
3. **Where we came from** — parent PR/branch/head commit, last confirmed build, unresolved blocker, prior entry.
4. **What changed** — exact files/assets, implementation approach, decisions/tradeoffs, what wasn't touched.
5. **Build evidence** — precise commit SHA + workflow run links/status; APK artifact link + version/package/signing context and SHA-256 *when verified*. If no build, say **no build run** and why.
6. **Outcome and remaining risks** — automated vs device vs owner-approved separate, failed links and root cause (if proven), regression/save concerns, fallback.
7. **What's next** — one *immediate actionable step* and a short longer-term direction. Include new-chat restart instructions.

**Update order:** write the pass entry with code/art in the same PR whenever feasible; link it in this index and update the current [WS-14 handoff](WS14_ACTIVE_HANDOFF_2026-11-09.md). For long coding sessions, update incrementally rather than only at the end. Do not produce a user-facing "ready" build without an actual build receipt or explicit "unverified" label.

**Avoid circular commit SHA requirements:** the pass log can cite the **feature source SHA being tested** and the relevant CI run; the final log-only commit SHA can be confirmed from GitHub afterward. Do not invent SHA values to satisfy formatting.

**GitHub Actions minutes:** keep the log in the same source/PR branch; documentation-only logs generally should not trigger expensive Android builds. Don't rerun the emulator merely to create a journal entry.

## New-chat restart (copy/paste)

> Continue GridMates: Word Search from GitHub. First read AGENTS.md, docs/DEVELOPMENT_JOURNAL.md, its latest entry, and docs/WS14_ACTIVE_HANDOFF_2026-11-09.md. Check actual latest PR #131/successor, head SHA and workflow statuses. Explain where the last pass came from, what worked, what's still blocked, and then continue the next objective. After your pass, commit a dated development-journal entry with the who/what/where/why, changes, build evidence, current fallback and next step. Keep v56 as last owner-verified handset fallback unless newer proof exists.
