# WS-12 — Bookshelf, Collections & Android Visual Integration Plan

**Design-system rollout plan, not permission to ship unapproved art.**
Existing proofs: [WS-12C covers/emblems](WS12C_QUIET_PAPERBACK_STYLE_SPEC_2026-11-08.md), [WS-12D accents](WS12D_MATCHING_PAPERBACK_ACCENTS_PLAN_2026-11-08.md). **Existing game test**: [WS-11E owner playable Bookshelf #100](https://github.com/PlayerEden/GridMates-Word-Search/pull/100), [WS-11E2 layout refinement #101](https://github.com/PlayerEden/GridMates-Word-Search/pull/101). Art is on a **separate stacked design branch**; do not merge conflicting bases directly.

## Practical release order — finish families, not 30 disconnected images

| Wave | Work and deliverable | Owner can actually test | Gate |
| --- | --- | --- | --- |
| A / system | Style Bible, AI playbook, production queue, approval test/CI | side-by-side concept/style kit review | source tests, original-art protection |
| B / Woodland | Use existing Woodland cover+emblem+chapter+bookmark+seal prototype and add a derived spine **without changing older cover hashes** | consistent Woodland cover/card/chapter/reward/contact sheet | owner's explicit visual shortlist and rights |
| C / Space + Garden | Apply **same geometry** and single-motif rules to matching books, no random independent generation | 3 books on one shelf / all stages compared | cross-theme consistency review |
| D / Collections | First choose new portrait model sheet *separate from approved 12 Woodland originals*, achievement badges, native font-card presentation | actual earned/cosmetic cards, stable locked/unlocked | icon scale, rights, portrait/ID compatibility |
| E / in-app prototype | Add *only reviewed* static SVG/vector assets to a separate QA build while native text/progress stays intact | home bookshelf → book → page → collection screens | 320dp, 200% font, offline, landscape, no image-only action |
| F / publication | Freeze approved art master+export hashes, fallback mapping, package size and acceptance screenshots | install-over release-candidate QA | approved registry + licensing + performance + rollback |

## Layout guidance from actual owner screenshots

The QA Bookshelf already has readable cream paper cards, green buttons, accessible book/chapter labels, and a functional letter board. **Do not replace those with a generated full-screen painted library.** Add small images to enhance identity while maintaining existing visual hierarchy.

- **Bookshelf:** cover miniature (consistent 3:4) as a leading image on each book card; title/theme/progress/action real Compose text. Consider a spine strip only if it improves the browse experience without shrinking tappable areas.
- **Book detail:** a single small emblem under or beside the book title; chapter badges beside native headings; optional bookmark ribbon beside saved page. Never obscure first page's Play action.
- **Book gameplay:** only a small, unobtrusive cover motif/header accent; **no artwork behind the actual letter grid**, no changes to touch coordinates, zoom, hint marks or word-list scroll.
- **Results:** one short completion seal in native result dialog; native announcement of completed page/next page, not a seal-only success message.
- **Collection:** keep four current areas coherent — profiles, portraits, fonts, achievements. New portraits get reviewed as a family and unlock visuals remain familiar. Native font preview renders an actual licensed typeface. **Do not use book/chapter completion counts to unlock original portrait IDs without explicit gameplay design approval**.

## Artwork families versus code assets

- Decorative SVG covers and emblems can derive from same vector tokens and source-controlled motif functions.
- Use no custom rendered image if a native small icon or text would communicate more clearly.
- Keep existing original Woodland portrait binaries untouched. Later collections may introduce new themes, but must be additive and not retroactively alter unlock requirements, collected IDs, player profiles or test saves.
- Art source path: \`design/asset-pipeline/\` tokens/renderers and future source masters. *Approved* exports: opt-in \`app/src/main\` resources with exact manifest SHA-256. Preview outputs stay in temporary QA artifacts, not production resources.
- Accessibility: decorative images should be marked decorative; information-bearing assets have meaningful native equivalents. Book/progress/completion never represented solely in color or art.

## Design reviews: hard requirements

**Visual:** show all 3 books side-by-side; then 32dp emblem, 48dp reward, 140dp book thumbnail and full phone screens. Look for silhouette confusion, inconsistent stroke weight, cramped text, over-detailed AI artifacts and mismatched palette.

**Technical:** rerun token-based SVG byte-determinism tests; original covers/emblems unchanged when adding new theme; production registry still fail-closed for unapproved sources; scan Android package for no prototype art; Android test on narrow+large text, landscape and offline. Count loading impact and verify old Solo/Daily/Collection and install-over saves.

**Legal/rights:** generated art provenance recorded; no implied copyright clearance; third-party assets/fonts reviewed with distribution licenses and notices. Adobe step optional, subject to available connection and editor; no phantom project links.

## No-go criteria

Don't release if any art is unreviewed; two themes have obviously different visual language; a status is baked into an image; a portrait/achievement ID changed silently; Android clipping/contrast occurs; original portrait hashes changed; new assets drive up APK/memory unnecessarily; rights are unknown; or old saved game/phone upgrade breaks.

## First owner approval checkpoint

Ask for **one coherent Woodland cover+emblem+chapter/bookmark/seal composition** beside the current actual Bookshelf screenshot. Request choice of "keep", "small changes" or "reject" with 1–3 concrete notes. Only then expand the exact motif system to the other books. This is the decision that prevents AI-style drift.
