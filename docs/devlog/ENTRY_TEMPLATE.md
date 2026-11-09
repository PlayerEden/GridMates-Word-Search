# [DATE] — [Milestone / pass title]

> **Quick status:** [PASSED AUTOMATED / OWNER-TESTING / PARTIAL / BLOCKED / FAILED]. Do not call a CI build “phone verified.”

## Who, what, where, why

- **Owner request / feedback:** [exact decision or paraphrase; owner approval is limited to what they actually reviewed]
- **Work performed by:** [assistant / developer / CI / owner tester, separate roles]
- **What we were trying to accomplish:** [concrete player-facing objective]
- **Why now:** [problem, screenshot feedback, prior blocker]
- **Where in app/repository:** [screens, files, exact PR/branch]

## Where we came from

- **Previous journal entry:** [relative link]
- **Starting code SHA + branch/PR:** [exact SHA and PR]
- **Previously verified APK:** [version, checksum if known; GitHub run]
- **Important decisions/constraints inherited:** [saves, signing, offline-first, art direction, open issues]

## What changed in this pass

- **Files/assets:** [names + relevant SHA, sizes, origins]
- **Implementation and decisions:** [what was chosen and why, any A/B alternatives]
- **Intentionally untouched:** [important game states, collections, saved progress]
- **Commit(s):** [exact implemented code SHA; do not invent]

## Build and QA receipt

- **Build attempted?** [yes / no, reason]
- **Source SHA tested:** [full SHA]
- **GitHub Actions run links and conclusions:** [Android QA, art QA, tests, status; don't infer pass from start]
- **APK:** [artifact URL, artifact name, size, SHA-256 if verified, package ID, signing/compatibility]
- **Checks that actually ran:** [unit / lint / compile / APK ZIP assets / emulator / device / two phones]
- **Failures/blockers:** [exact observed error; root cause only if proven]
- **Rollback/fallback:** [last owner-verified build, backup/install cautions]

## Outcome and follow-through

- **We completed:** [durable results]
- **Not complete:** [anything waiting on owner/CI, release gates, rights]
- **Next single action:** [specific action the next agent can start immediately]
- **Then:** [direction of the subsequent milestone]
- **New-chat one-liner:** Continue from [branch/PR], read [entry and handoff], and do [next step].

**Rule:** Every meaningful change or APK build gets a new dated journal entry and a row in docs/DEVELOPMENT_JOURNAL.md. Update active handoff at the same time.
