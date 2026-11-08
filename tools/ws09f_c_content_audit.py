#!/usr/bin/env python3
"""WS-09F-C pre-generator content audit. No Android, Gradle or dependencies.

Reads the immutable v3 source as DATA; it never edits vocabulary or app content.
The 40x40/50x50 target counts are laboratory *hypotheses*, not release presets.
"""
from __future__ import annotations

import argparse
import hashlib
import json
import re
from pathlib import Path

SOURCE = Path("core/src/main/kotlin/com/playereden/gridmates/wordsearch/core/WordSearchV3.kt")
CANDIDATES = Path("design/marathon/v4_candidate_additions.json")
# Frozen baseline observed in the source-verified WS-09F-B code47 checkpoint.
EXPECTED_TIERS = {
    "WOODLAND": (28, 38, 15),
    "SPACE": (30, 37, 14),
    "GARDEN": (29, 36, 14),
}
EXPERIMENT_TARGETS = {"40x40": 70, "50x50": 105}


def extract_packs(source: str) -> dict[str, list[list[str]]]:
    packs = {}
    for theme in EXPECTED_TIERS:
        key = f"WordTheme.{theme} to pack("
        if source.count(key) != 1:
            raise ValueError(f"Expected exactly one version-3 pack for {theme}")
        section = source.split(key, 1)[1]
        tiers = re.findall(r'"""([\s\S]*?)"""', section)[:3]
        if len(tiers) != 3:
            raise ValueError(f"Missing content tiers for {theme}")
        packs[theme] = [re.findall(r"\S+", tier) for tier in tiers]
    return packs


def substring_safe_pool(words: list[str]) -> list[str]:
    """Conservative greedy pack; matches v3's refusal to pick substring pairs.

    This is not an exact maximum independent set. Its size is a practical
    baseline for planning, not a guarantee of generator placement success.
    """
    chosen = []
    for word in sorted(words, key=lambda item: -len(item)):
        if not any(word in old or old in word for old in chosen):
            chosen.append(word)
    return chosen


def audit(source: str) -> dict[str, dict]:
    report = {}
    for theme, tiers in extract_packs(source).items():
        counts = tuple(map(len, tiers))
        if counts != EXPECTED_TIERS[theme]:
            raise ValueError(f"{theme}: v3 tier counts changed: {counts}")
        words = [word for tier in tiers for word in tier]
        if len(set(words)) != len(words):
            raise ValueError(f"{theme}: duplicate words in frozen source")
        invalid = [w for w in words if not re.fullmatch(r"[A-Z]{5,16}", w)]
        if invalid:
            raise ValueError(f"{theme}: invalid words: {invalid}")
        safe = substring_safe_pool(words)
        report[theme] = {
            "tiers": counts,
            "existing": len(words),
            "conservative_substring_safe": len(safe),
            "experiments": {
                size: {
                    "target": target,
                    "gap": max(0, target - len(safe)),
                    "supply_headroom": len(safe) - target,
                }
                for size, target in EXPERIMENT_TARGETS.items()
            },
        }
    return report


def git_blob_sha1(raw: bytes) -> str:
    # The exact GitHub V3 blob SHA is a source-compatibility gate, not a
    # claim that v4 is part of the v3 executable.
    header = f"blob {len(raw)}\\0".encode("ascii")
    return hashlib.sha1(header + raw).hexdigest()


def audit_draft_candidates(source: str, raw_source: bytes, manifest: dict) -> dict:
    if manifest.get("status") != "EDITORIAL_REVIEW_REQUIRED_NOT_FOR_APP_GENERATION":
        raise ValueError("A research manifest must not be presented as approved content")
    if manifest.get("source_v3_blob_sha") != git_blob_sha1(raw_source):
        raise ValueError("The v4 candidate manifest does not match the frozen v3 blob")
    themes = manifest.get("themes")
    if not isinstance(themes, dict) or set(themes) != set(EXPECTED_TIERS):
        raise ValueError("Missing or extra theme in candidate manifest")
    report = {}
    for theme, tiers in extract_packs(source).items():
        frozen = [word for tier in tiers for word in tier]
        entry = themes[theme]
        additions = entry.get("unclassified_candidates") if isinstance(entry, dict) else None
        if not isinstance(additions, list) or not additions:
            raise ValueError(f"{theme}: missing candidate list")
        seen = list(frozen)
        for word in additions:
            if not isinstance(word, str) or not re.fullmatch(r"[A-Z]{5,16}", word):
                raise ValueError(f"{theme}: invalid candidate {word!r}")
            conflict = next((old for old in seen
                             if old == word or old in word or word in old), None)
            if conflict:
                raise ValueError(f"{theme}: candidate {word} conflicts with {conflict}")
            seen.append(word)
        safe = substring_safe_pool(seen)
        result = {
            "frozen_unique": len(frozen),
            "unreviewed_additions": len(additions),
            "combined_unique": len(seen),
            "conservative_substring_safe": len(safe),
            "50x50_headroom": len(safe) - EXPERIMENT_TARGETS["50x50"],
        }
        if result["conservative_substring_safe"] < 160:
            raise ValueError(f"{theme}: not yet enough draft candidates for research")
        report[theme] = result
    return report


def main() -> None:
    parser = argparse.ArgumentParser()
    parser.add_argument("--source", type=Path, default=SOURCE)
    parser.add_argument("--candidates", type=Path, default=CANDIDATES)
    args = parser.parse_args()
    raw = args.source.read_bytes()
    report = audit(raw.decode("utf-8"))
    print("WS-09F-C CONTENT SUPPLY AUDIT — NOT A GAMEPLAY GENERATOR")
    print(f"V3 source SHA-256: {hashlib.sha256(raw).hexdigest()}")
    print("Provisional 40x40=70 and 50x50=105 targets; neither size is enabled.")
    for theme, stats in report.items():
        print(f"{theme}: v3 tiers={stats['tiers']}, unique={stats['existing']}, "
              f"substring-safe baseline={stats['conservative_substring_safe']}")
        for size, result in stats["experiments"].items():
            label = "SHORTAGE" if result["gap"] else "NOMINAL SUPPLY ONLY"
            print(f"  {size}: {label}, target={result['target']}, "
                  f"headroom={result['supply_headroom']}, gap={result['gap']}")
    draft = json.loads(args.candidates.read_text(encoding="utf-8"))
    results = audit_draft_candidates(raw.decode("utf-8"), raw, draft)
    print("UNAPPROVED VERSION-4 RESEARCH CANDIDATES (NOT AN APP DICTIONARY)")
    for theme, stats in results.items():
        print(f"  {theme}: new={stats['unreviewed_additions']}, "
              f"combined={stats['combined_unique']}, "
              f"substring-safe={stats['conservative_substring_safe']}, "
              f"50x50 nominal headroom={stats['50x50_headroom']}")
    print("Candidates require semantic, spelling, familiarity-tier and gameplay review.")
    print("No generator placement, distinct hidden occurrences, FPS or device proof inferred.")


if __name__ == "__main__":
    main()
