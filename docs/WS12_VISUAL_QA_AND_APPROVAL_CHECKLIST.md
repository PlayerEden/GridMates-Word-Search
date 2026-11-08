# WS-12 — Art Release Review, Accessibility and Acceptance Protocol

Art can pass a Python unit test yet look terrible at phone size. **Never claim technical CI is owner aesthetic approval or device QA.**

## Review matrix (every new family, not just first theme)

| Test surface | Sizes / scenario | Reviewer checks |
| --- | --- | --- |
| Contact sheet | Woodland, Space, Garden shown in the same layout and zoom | motif/line weight, color roles, background, framing and spacing agree |
| Book thumbnail | 120–140dp, 320dp-wide screen | identifiable theme, title native/legible, no crowded buttons |
| Emblem/badge | 24dp, 32dp, 48dp | instantly understandable; no extra tiny details or text |
| Chapter/page | 320dp & 200% font scale | title wraps normally, chapter badge doesn't cover Play/Continue |
| Gameplay | 12×12, 15×15, 20×20 portrait and landscape | art never obscures board, word list, hints, touch targets or selection trails |
| Collections | profile / portraits / licensed fonts / achievements, first & last page | cohesive portrait framing; locks and reward progress are native, not image-only |
| Offline | airplane mode + restart | all art renders locally, fallback if absent |
| Performance | one modest older Android and a narrow phone | no visible stutter, excess APK growth or bitmap OOM |
| Upgrade | install over signed QA build, old/partial saves present | original art and unlock IDs/records not changed; same test/production package rules |
| Accessibility | TalkBack, large fonts, touch navigation | useful descriptions for information; decorative art skipped |

**Capture evidence:** screenshot filename, app source SHA, device/resolution, font scale, Android version, asset ID/hash, reviewer, notes and pass/hold decision.

## Approval gates

- [ ] Reviewed brief + reference motifs/art system version
- [ ] Clear original/AI/third-party source and rights/licensing evidence
- [ ] Same palette/geometry/lineweight as siblings on side-by-side contact sheet
- [ ] Reviewed at icon/book thumbnail sizes and inside the real Android app
- [ ] No baked title, button, score or fake text
- [ ] Native fallback and accessible non-image status present
- [ ] Owner explicitly approves **specific asset ID + source/export hashes**, not "all art"
- [ ] Validator green; original 12 Woodland portraits and older approved assets unchanged
- [ ] Android lint/build/packaging + actual device checks; install-over does not erase data
- [ ] Rollback path and archived source masters/notice files documented

**Fail any required gate → keep asset as concept/prototype or rejected, do not add to \`approved-assets.json\` and do not bundle in release.**

## Practical scoring (human, not auto-generated)

Give each asset 0 (fails), 1 (needs work) or 2 (passes) for *subject accuracy, consistent family grammar, small-scale clarity, screen fit, traceable rights/source and emotional tone*. All six must score **2** for final candidate approval; the score is a review aid, not a machine-substitute for owner judgement. A single bogus word/watermark or inappropriate copyrighted likeness is immediate rejection.

Use project issue comments to record decisions. The automation is allowed to verify **file presence, allowed IDs, SHA, format, status and missing signoff**; it must not fabricate a reviewer identity, rights grant or aesthetic decision.
