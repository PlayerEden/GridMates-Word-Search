#!/usr/bin/env python3
"""WS-10B2: crosswalk research theme proposals against pinned ESDB quarantine.

The 563 existing V4 research words are only EDITORIAL SUGGESTIONS, not approvals.
Outputs three review sheets, distinct holds, source-traceable counts, no app data.

No word is silently discarded for lacking a dictionary match: compounds,
regional words and proper terms should be discussed by the editor, not guessed.
"""
import argparse
from collections import Counter
import csv
import json
from pathlib import Path
import shutil
import sys

THEMES = ("woodland", "space", "garden")
SEED_FIELDS = {
    "answer", "topic_suggestion", "source_tier_status",
    "source_family", "source_blob_sha"
}
REVIEW_FIELDS = [
    "answer", "suggested_display", "suggested_theme", "original_tier_proposal",
    "source_lexicon_match", "source_release", "source_line",
    "research_proposal_source_sha", "requires_human_review",
    "editorial_decision", "approved_display", "approved_theme",
    "approved_subtopic", "approved_familiarity", "reviewed_by",
    "reviewed_on", "editor_notes"
]


def read_rows(path: Path):
    with path.open("r", newline="", encoding="utf-8-sig") as f:
        reader = csv.DictReader(f)
        if not reader.fieldnames or len(set(reader.fieldnames)) != len(reader.fieldnames):
            raise ValueError(f"{path}: invalid CSV header")
        rows = list(reader)
        if any(None in r or None in r.values() for r in rows):
            raise ValueError(f"{path}: malformed CSV row")
        return rows, set(reader.fieldnames)


def write_rows(path: Path, rows: list[dict]):
    with path.open("w", newline="", encoding="utf8") as f:
        w = csv.DictWriter(f, fieldnames=REVIEW_FIELDS, lineterminator="\n")
        w.writeheader()
        w.writerows(rows)


def generate(quarantine_csv: Path, seed_csv: Path, outdir: Path,
             license_notice: Path) -> dict:
    candidates, fields = read_rows(quarantine_csv)
    must = {"answer", "source_id", "source_line", "source_release",
            "review_status", "topic_status", "human_review_required"}
    if not fields.issuperset(must):
        raise ValueError("Quarantine columns missing")
    by_answer = {}
    for r in candidates:
        answer = r["answer"]
        if answer in by_answer or r["review_status"] != "candidate" or r["topic_status"] != "unclassified" or r["human_review_required"] != "true":
            raise ValueError("Source candidate not unique, quarantined, and unreviewed")
        by_answer[answer] = r

    source_seeds, seed_fields = read_rows(seed_csv)
    if seed_fields != SEED_FIELDS or not source_seeds:
        raise ValueError("Theme seed data missing required provenance columns")
    rows = {theme: [] for theme in THEMES}
    hold_rows = []
    seen = set()
    missing = Counter()
    by_stage = Counter()
    for s in source_seeds:
        answer = s["answer"]
        theme = s["topic_suggestion"]
        stage = s["source_tier_status"]
        if theme not in THEMES or stage not in {
            "relaxed-proposal", "advanced-proposal", "unreviewed-extra", "hold"
        } or not s["source_blob_sha"] or not s["source_family"]:
            raise ValueError("Invalid research seed provenance/category")
        if not answer.isascii() or not answer.isupper() or not answer.isalpha():
            raise ValueError("Invalid uppercase research answer")
        key = theme, answer
        if key in seen:
            raise ValueError("Duplicate theme/answer seed")
        seen.add(key)
        item = by_answer.get(answer)
        if item is None:
            missing[theme] += 1
        by_stage[theme, stage] += 1
        row = {
            "answer": answer,
            "suggested_display": item["display_proposal"] if item else answer.title(),
            "suggested_theme": theme,
            "original_tier_proposal": stage,
            "source_lexicon_match": "verified-esdb-root" if item else "needs-independent-word-check",
            "source_release": item["source_release"] if item else "",
            "source_line": item["source_line"] if item else "",
            "research_proposal_source_sha": s["source_blob_sha"],
            "requires_human_review": "true",
            "editorial_decision": "",
            "approved_display": "",
            "approved_theme": "",
            "approved_subtopic": "",
            "approved_familiarity": "",
            "reviewed_by": "",
            "reviewed_on": "",
            "editor_notes": ""
        }
        if stage == "hold":
            hold_rows.append(row)
        else:
            rows[theme].append(row)
    if not license_notice.is_file() or "Copyright 2000-2026 by Kevin Atkinson" not in license_notice.read_text(encoding="utf8"):
        raise ValueError("Candidate review material must carry pinned source copyright notice")
    outdir.mkdir(parents=True, exist_ok=True)
    for theme in THEMES:
        write_rows(outdir / f"{theme}_editor_review_queue.csv",
                   sorted(rows[theme], key=lambda x: x["answer"]))
    write_rows(outdir / "original_research_holds.csv",
               sorted(hold_rows, key=lambda x: (x["suggested_theme"], x["answer"])))
    shutil.copyfile(license_notice, outdir / "ESDB_SCOWL_COPYRIGHT_2026_02_25.txt")
    summary = {
        "stage": "WS-10B2_PRELIMINARY_RESEARCH_CROSSWALK_NOT_APPROVED",
        "quarantined_source_candidates": len(candidates),
        "priority_research_seed_theme_assignments": len(source_seeds),
        "topic_review_queues": {t: len(rows[t]) for t in THEMES},
        "research_holds": len(hold_rows),
        "missing_esdb_source_by_theme": dict(sorted(missing.items())),
        "research_proposal_tiers": {
            f"{theme}/{status}": n for (theme, status), n in sorted(by_stage.items())
        },
        "approved_unique_words": 0,
        "approved_topic_assignments": 0,
        "disclaimer": "Language-spelling root matches do not establish theme, familiarity or suitability. All review decisions remain blank."
    }
    (outdir / "review_queue_report.json").write_text(
        json.dumps(summary, indent=2, sort_keys=True) + "\n", encoding="utf8")
    return summary


def main():
    p = argparse.ArgumentParser(description=__doc__)
    p.add_argument("--quarantine", type=Path, required=True)
    p.add_argument("--seed-csv", type=Path, default=Path(
        "data/lexicon/intake/v4_three_theme_research_seed_suggestions.csv"))
    p.add_argument("--copyright", type=Path, default=Path(
        "data/lexicon/licenses/ESDB_SCOWL_COPYRIGHT_2026_02_25.txt"))
    p.add_argument("--output-dir", type=Path, required=True)
    args = p.parse_args()
    try:
        report = generate(args.quarantine, args.seed_csv, args.output_dir,
                          args.copyright)
    except (OSError, ValueError) as exc:
        print(f"FAIL WS-10B2 theme review queue: {exc}", file=sys.stderr)
        sys.exit(1)
    print(json.dumps(report, indent=2, sort_keys=True))
    print("PASS: three original-themed review queues and explicit hold sheet; 0 approved.")


if __name__ == "__main__":
    main()
