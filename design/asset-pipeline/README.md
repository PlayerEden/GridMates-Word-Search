# GridMates: Word Search — Art pipeline source of truth

The art workflow has **one house style** and two very different places to keep records:

- \`paperback-v1-tokens.json\` and \`tools/asset_pipeline/render_paperback*.py\`: deterministic **PROTOTYPE** cover/emblem/chapter/bookmark/seal source.
- \`production-queue-v1.json\`: complete **planned/prototype** asset-family roadmap, including six slots for each of Woodland, Space and Garden, plus seven cosmetic Collection workstreams.
- \`pilot-concept-register.json\`: earlier AI cover moodboards/receipts **not** imported Android resources or owner-approved artwork.
- \`approved-assets.json\` + \`asset-manifest.schema.json\`: guarded registry for **production-approved NEW resources ONLY**. Currently empty. This does not mean the twelve original approved Woodland portraits are absent from the existing Android app; they have their own preexisting packaging history.
- \`tools/asset_pipeline/validate.py\`: original source/rights/hash approval guard.
- \`tools/asset_pipeline/validate_production_queue.py\`: new no-fake-approvals, complete asset-family, native-text and portrait-protection planning gate.
- \`docs/WS12_ART_DIRECTION_STYLE_BIBLE.md\`: fixed color, geometry, texture and anti-slop visual grammar.
- \`docs/WS12_AI_ASSET_PRODUCTION_PLAYBOOK.md\`: AI concept → select → controlled edits → rights → explicit approval and final Android handoff.
- \`docs/WS12_ANCHOR_ASSET_PLAN.md\`: Woodland first, then compare Space/Garden as one family.
- \`docs/WS12_BOOKS_COLLECTIONS_INTEGRATION_PLAN.md\`: screen placement and collection design plan.
- \`docs/WS12_VISUAL_QA_AND_APPROVAL_CHECKLIST.md\`: exact human and device acceptance.

### Local verification (no image-generation service needed)

\`\`\`bash
python3 -m unittest discover -s tools/asset_pipeline -p 'test_*.py' -v
python3 tools/asset_pipeline/validate.py --repo-root .
python3 tools/asset_pipeline/validate_production_queue.py --repo-root .
\`\`\`

**Do not** add concept assets directly to \`app/src/main\`, invent approval metadata, alter original Woodland portraits, change the existing saved-game schemas or claim a generated image passed legal/accessibility review. The initial owner QA APK in WS-11E contains nine *test word-search puzzles*; it does not yet contain approved WS-12 book art.
