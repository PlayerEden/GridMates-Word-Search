#!/usr/bin/env python3
"""Fail-closed parity/word-quality preflight for a NON-SHIPPING V4 preview.

Proof of consistent source derivation is not a dictionary, content approval,
literacy study, word-frequency ranking or actual Android usability test.
"""
import json
import re
from pathlib import Path

THEMES = ("WOODLAND", "SPACE", "GARDEN")
V3 = Path("core/src/main/kotlin/com/playereden/gridmates/wordsearch/core/WordSearchV3.kt")
BASE = Path("core/src/test/resources/ws09fc_v4_test_only_candidates.txt")
TRIAGE = Path("design/marathon/v4_editorial_triage_proposal.json")
DRAFT = Path("design/marathon/v4_candidate_tiers_workbench_2026-11-08.json")
FIXTURE = Path("core/src/test/resources/ws09f_e_v4_provisional_tiers.txt")


def v3_advanced(source, name):
    start = source.index("WordTheme." + name + " to pack(")
    blocks = re.findall(r'"""(.*?)"""', source[start:], flags=re.DOTALL)[:3]
    assert len(blocks) == 3
    return blocks[2].split()


def tagged_words(lines, separator="|"):
    values = {}
    for line in lines:
        if not line.strip() or line.startswith("#"):
            continue
        keys = line.split(separator)
        assert len(keys) >= 2
        identity = tuple(keys[:-1])
        assert identity not in values, f"Duplicate test fixture key {identity}"
        values[identity] = keys[-1].split()
    return values


def fail_if_ambiguous(name, words):
    assert len(words) == len(set(words)), f"{name} duplicate word"
    assert all(5 <= len(w) <= 16 and re.fullmatch("[A-Z]+", w) for w in words)
    for i, a in enumerate(words):
        for b in words[i + 1:]:
            assert a not in b and b not in a, f"{name} substring conflict: {a} / {b}"


def main():
    source = V3.read_text(encoding="utf8")
    original = tagged_words(BASE.read_text(encoding="utf8").splitlines())
    triage = json.loads(TRIAGE.read_text(encoding="utf8"))
    draft = json.loads(DRAFT.read_text(encoding="utf8"))
    staged = tagged_words(FIXTURE.read_text(encoding="utf8").splitlines())
    assert draft["status"] == "EXPERIMENTAL_TEST_ONLY_AWAITING_HUMAN_EDITORIAL_REVIEW"
    assert triage["status"] == "UNAPPROVED_EDITORIAL_TRIAGE_PROPOSAL"
    assert draft["threshold"] == 125
    assert set(draft["themes"]) == set(triage["themes"]) == set(THEMES)
    assert set(original) == {(name,) for name in THEMES}
    assert set(staged) == {(name, category) for name in THEMES for category in (
        "TRIAL_RELAXED", "TRIAL_ADVANCED", "EDITOR_HOLD", "UNREVIEWED_ADDITION")}
    for name in THEMES:
        source_pool = original[name,]
        review = triage["themes"][name]
        v3_rare = v3_advanced(source, name)
        proposed_rare = review["provisional_advanced_only"]
        holds = list(review["hold_for_editor_review"])
        extra = [item["answer"] for item in
                 review.get("possible_friendly_additions_for_review", [])]
        for item in review.get("possible_friendly_additions_for_review", []):
            assert item["editor_status"] == "NOT_REVIEWED_NOT_IN_GENERATOR_FIXTURE"
        rare = list(dict.fromkeys(v3_rare + proposed_rare))
        common = [w for w in source_pool if w not in rare and w not in holds] + extra
        assert set(source_pool) == set(common + rare + holds) - set(extra)
        assert not set(extra).intersection(source_pool)
        assert staged[name, "TRIAL_RELAXED"] == common
        assert staged[name, "TRIAL_ADVANCED"] == rare
        assert staged[name, "EDITOR_HOLD"] == holds
        assert staged[name, "UNREVIEWED_ADDITION"] == extra
        entry = draft["themes"][name]
        assert entry["relaxed_candidates"] == common
        assert entry["advanced_only_candidates"] == rare
        assert entry["unreviewed_extra_concepts"] == extra
        assert entry["original_candidate_pool"] == len(source_pool)
        assert entry["manual_hold_count"] == len(holds)
        assert entry["proposed_advanced_count"] == len(rare)
        assert entry["provisional_relaxed_eligible_count"] == len(common)
        assert entry["unreviewed_extra_count"] == len(extra)
        assert len(common) >= 125
        fail_if_ambiguous(name, common + rare)
        for word, label in entry["display_label_proposals"].items():
            assert word in source_pool or word in extra
            normalized = re.sub("[^A-Za-z]", "", label).upper()
            assert normalized == word, f"{name}: label does not match answer {word}: {label}"
        print(f"{name}: preview_relaxed={len(common)} (unreviewed_extra={len(extra)}), "
              f"advanced={len(rare)}, editor_hold={len(holds)}; "
              f"105-of-{len(common)} supply possible, substring-free")
    root = Path("core/src/main/kotlin/com/playereden/gridmates/wordsearch/core")
    selector = (root / "V4PreviewTierSelection.kt").read_text(encoding="utf8")
    assert "internal object V4PreviewTierSelection" in selector
    assert "fun previewPuzzle(" in selector
    assert not any(word in selector for word in ("FULLMOON", "BROWNDWARF", "GARDEN|", "SPACE|"))
    solo = (root / "SoloProgress.kt").read_text(encoding="utf8")
    assert "V4PreviewTierSelection" not in solo
    assert "V4PreviewTierSelection" not in Path(
        "app/src/main/java/com/playereden/gridmates/wordsearch/WordSearchScreen.kt"
    ).read_text(encoding="utf8")
    print("PASS exact V3 + frozen experimental corpus + original triage source parity.")
    print("NO editorial approval, public V4 board, saved V4 ID, or APK is implied.")


if __name__ == "__main__":
    main()
