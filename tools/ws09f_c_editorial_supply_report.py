#!/usr/bin/env python3
"""Fail-closed source consistency check, not an approval of V4 vocabulary.

Propose which candidates need expert review or advanced-mode-only placement;
compute how much relaxed-mode supply remains for future 105-word puzzles.
This does NOT modify existing Python/Kotlin labs or shipping dictionaries.
"""
import json
from pathlib import Path

from ws09f_c_content_audit import (
    SOURCE, CANDIDATES, extract_packs, audit_draft_candidates,
)

FIXTURE = Path("labs/ws09f_c_kotlin/candidates_test_only.txt")
PROPOSAL = Path("design/marathon/v4_editorial_triage_proposal.json")


def main() -> None:
    raw = SOURCE.read_bytes()
    source = raw.decode("utf-8")
    draft = json.loads(CANDIDATES.read_text(encoding="utf-8"))
    audit_draft_candidates(source, raw, draft)
    proposal = json.loads(PROPOSAL.read_text(encoding="utf-8"))
    assert proposal["status"] == "UNAPPROVED_EDITORIAL_TRIAGE_PROPOSAL"
    assert proposal["generation_impact"] .startswith("NONE")
    assert proposal["provisional_relaxed_size50_target"] == 105
    goal = proposal["planning_goal_familiar_pool_minimum"]
    assert goal >= 125
    fixture = {}
    for line in FIXTURE.read_text(encoding="utf-8").splitlines():
        if not line.strip() or line.startswith("#"):
            continue
        theme, raw_words = line.split("|", 1)
        assert theme not in fixture
        fixture[theme] = raw_words.split(" ")
    packs = extract_packs(source)
    assert set(proposal["themes"]) == set(packs) == set(fixture)
    needs_additions = []
    for theme, tiers in packs.items():
        whole = fixture[theme]
        added = draft["themes"][theme]["unclassified_candidates"]
        advanced = proposal["themes"][theme]["provisional_advanced_only"]
        holds = proposal["themes"][theme]["hold_for_editor_review"]
        assert isinstance(advanced, list) and isinstance(holds, dict)
        assert len(set(advanced)) == len(advanced)
        assert set(advanced).isdisjoint(holds)
        assert set(advanced).issubset(added)
        assert set(holds).issubset(added)
        assert set(advanced).issubset(whole) and set(holds).issubset(whole)
        assert all(isinstance(reason, str) and len(reason) >= 8
                   for reason in holds.values())
        v3_advanced = set(tiers[2])
        assert v3_advanced.isdisjoint(advanced)
        possible_relaxed = [word for word in whole
                            if word not in v3_advanced and word not in advanced
                            and word not in holds]
        assert len(possible_relaxed) >= 105, (
            theme + " has too few unblocked words for a 105-word trial")
        buffer = len(possible_relaxed) - proposal["provisional_relaxed_size50_target"]
        gap = max(0, goal - len(possible_relaxed))
        print(f"{theme}: lab_pool={len(whole)} v3_advanced={len(v3_advanced)} "
              f"proposed_advanced={len(advanced)} editorial_holds={len(holds)} "
              f"tentative_relaxed={len(possible_relaxed)} spare={buffer} "
              f"planning_goal={goal} gap_to_goal={gap}")
        if gap:
            needs_additions.append((theme, gap))
    if needs_additions:
        print("EDITORIAL_WORK_REMAINS: need additional reviewed, accessible candidate words:")
        for theme, gap in needs_additions:
            print(f"  {theme}: at least {gap} more for current planning buffer, "
                  "before real editorial / generator placement approval")
    print("PASS: exact-source triage consistency. This does NOT approve any word.")
    print("No v4 content has been published and no user save or puzzle ID changed.")


if __name__ == "__main__":
    main()
