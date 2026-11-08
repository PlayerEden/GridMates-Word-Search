# WS-12G — Installable Word Search Art Lab: Reusable Book Artwork Preview (QA v51)

**November 8, 2026 · DEBUG/QA-ONLY TEST APK. No owner-approved production book art and no changes to puzzle/book/Collection data.**

## Goal: let the owner judge actual artwork on their real phone

The owner already tested QA versionCode **50** and liked the improved book UI. WS-12F added a reproducible 18-piece Woodland/Space/Garden art family with a true same-style spine, and a native-text HTML contact sheet. Rather than incorrectly marking that art as final or overwriting the existing book cards, this milestone adds a **separate QA-only Art Lab**.

**Flow:** Home → **View Library Artwork** → offline Art Lab → Back to Play. The existing **Test Puzzle Books** button, three playable QA books, Solo/Daily/Nearby and collectible Collection remain where they were. Art Lab does not unlock cosmetics, generate words or modify any save file.

### What is implemented

- Art source is **copied exactly from WS-12F**: pinned Quiet Illustrated Paperbacks tokens and the three deterministic Python SVG scripts, in \`design/asset-pipeline/\` and \`tools/asset_pipeline/\`. Keeping the source versioned in this stack makes the APK reproducible without pulling unrelated art/roadmap branches.
- Debug Gradle task \`generateWs12QaArt\` produces **18 real SVG prototype art files** plus native-text \`review.html\` and a SHA-256 manifest into ignored \`app/src/debug/assets/ws12/\` **before** \`mergeDebugAssets\`. Only debug assets are built; release does not carry them. CI is Linux and has Python3; local development builds need Python3 when requesting \`:app:assembleDebug\`.
- \`QaArtLabScreen\` uses the phone's Android WebView to display the source-backed local preview HTML and 18 SVGs. **JavaScript and networking are disabled**, external navigation blocked, no live web requests or art generation on a user's device. All book labels remain HTML/native text rather than baked letters in the SVG.
- Art Lab is a separate screen, gated on \`BookQaPilot.enabled\` (true in debug, false in release). The Home entry clearly states **ART STUDY · NOT FINAL ARTWORK**. It adds no fifth bottom tab. Back returns to Play, and all production books/screens retain the current paper UI without prototype art.
- QA versionCode **51**, versionName \`0.9.7-qa-book-art-lab-qa\`, same existing \`com.playereden.gridmates.wordsearch.qa\` and QA signing. Intended install-over update **without uninstalling v50**.
- Android test source \`QaArtLabAndroidTest\` checks 18 SVGs are actually in the APK, labels and provenance manifest exist, and route opens/returns safely. **Compiling instrumentation source alone does not mean this test executed on an emulator/phone.**
- Separate release artifact verification ensures **no** prototype \`ws12/\` assets appear in a release APK. Existing original 12 Woodland portraits and saved-game schemas aren't rewritten.

### Your first phone review

1. Install the newest v51 **Word Search TEST** APK **over v50** without uninstalling.
2. On Home, scroll to **Library Art Lab — TEST ONLY** and tap **View Library Artwork**.
3. Compare all three covers and spines side by side, then the emblem, chapter marker, bookmark and seal. At normal phone width, does one consistent paperback family come through?
4. Open actual **Test Puzzle Books** separately to compare against real Android navigation (the art has **not** been injected into the actual book cards).
5. Share screenshots and a short reaction: **Keep / Make changes / Reject** for the family, plus details like "too sparse", "too busy", "colors don't match", "spine too narrow", or "art too plain".
6. If you also test gameplay, keep testing word selections, hint/save/resume and page completion—these were intentionally not modified in the art branch.

**Release gates remain:** art aesthetic choice, rights signoff, targeted native Android book-card integration, accessibility at 320dp and 200%-text, actual device/portrait/landscape tests, original avatar/save compatibility and owner approval. This QA screen is the *visual selection checkpoint*, not an authorized art release.

**Dependency stacks:** gameplay QA PR #101 → this Art Lab PR; separately, art source PRs #92→#93→#94→#109→#110. All art used by this QA feature is copied directly from the latter's source renderers, without a potentially conflicting repository-wide merge.
