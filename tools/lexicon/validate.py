#!/usr/bin/env python3
"""WS-10 draft word library validation. Python stdlib only; NO Android ingestion.

Run:
  python3 tools/lexicon/validate.py --data-dir data/lexicon
  python3 tools/lexicon/validate.py --data-dir data/lexicon --json-out /tmp/lexicon-report.json

Global words and topic assignments are separate to count UNIQUE words honestly.
No candidate is approved merely because it passes this validator.
"""
import argparse
import csv
from datetime import date
import json
from pathlib import Path
import re
import sys
import unicodedata
from collections import Counter

STATES = {"candidate", "reviewed", "approved", "hold", "rejected"}
TIERS = {"familiar", "intermediate", "advanced"}
ID = re.compile(r"[a-z][a-z0-9_-]{4,39}\Z")
SLUG = re.compile(r"[a-z][a-z0-9-]{2,40}\Z")
ANSWER = re.compile(r"[A-Z]{3,20}\Z")
REVIEW_DATE = re.compile(r"\d{4}-\d{2}-\d{2}\Z")
EXPECTED_WORD_COLS = {
    "word_id", "answer", "display", "locale", "review_status",
    "source_id", "editor_notes", "reviewed_by", "reviewed_on"
}
EXPECTED_TOPIC_COLS = {
    "word_id", "topic_id", "subtopic_id", "tier",
    "review_status", "editor_notes", "reviewed_by", "reviewed_on"
}


def csv_rows(path: Path, required: set[str]):
    with path.open(encoding="utf-8-sig", newline="") as f:
        reader = csv.DictReader(f)
        if reader.fieldnames is None or len(reader.fieldnames) != len(set(reader.fieldnames)):
            raise ValueError(f"{path}: missing/duplicate header")
        if set(reader.fieldnames) != required:
            raise ValueError(
                f"{path}: header must be {sorted(required)}; got {reader.fieldnames}"
            )
        rows = list(reader)
        if any(None in row or any(value is None for value in row.values()) for row in rows):
            raise ValueError(f"{path}: malformed CSV row")
        return rows


def normalized(display: str) -> str:
    # This FIRST English version accepts ordinary ASCII labels, spaces,
    # hyphens and apostrophes. Locale-specific rules need a NEW schema.
    if not re.fullmatch(r"[A-Za-z][A-Za-z '’-]*[A-Za-z]|[A-Za-z]", display):
        raise ValueError(f"Invalid display label {display!r}")
    return "".join(
        c for c in unicodedata.normalize("NFKD", display.upper())
        if "A" <= c <= "Z"
    )


def approval_signed(row, context):
    state = row["review_status"]
    if state not in STATES:
        raise ValueError(f"{context}: unknown review state {state}")
    reviewer = row["reviewed_by"].strip()
    when = row["reviewed_on"].strip()
    if state in {"reviewed", "approved"}:
        if len(reviewer) < 2 or not REVIEW_DATE.fullmatch(when):
            raise ValueError(f"{context}: reviewed/approved requires named editor and ISO date")
        try:
            date.fromisoformat(when)
        except ValueError as exc:
            raise ValueError(f"{context}: invalid review date") from exc
    elif reviewer or when:
        raise ValueError(f"{context}: nonreviewed row cannot claim a reviewer/date")


def audit(base: Path) -> dict:
    source_file = json.loads((base / "sources.json").read_text(encoding="utf8"))
    topic_file = json.loads((base / "topics.json").read_text(encoding="utf8"))
    if source_file["schema_version"] != 1 or topic_file["schema_version"] != 1:
        raise ValueError("Unsupported lexicon schema; migrate explicitly")
    sources = {}
    for x in source_file["sources"]:
        key = x["id"]
        if key in sources or not ID.fullmatch(key):
            raise ValueError(f"Bad or duplicate source ID {key}")
        if not x.get("license") or not x.get("attribution"):
            raise ValueError(f"Source {key} requires license and attribution ledger")
        if x.get("rights_review") not in {"not-cleared", "cleared", "internal-existing"}:
            raise ValueError(f"Source {key} has unknown rights review status")
        sources[key] = x
    topics = {}
    for x in topic_file["topics"]:
        key = x["id"]
        if key in topics or not SLUG.fullmatch(key):
            raise ValueError(f"Bad or duplicate topic ID {key}")
        if x.get("status") not in {"proposed", "active", "retired"}:
            raise ValueError(f"Unknown topic status {key}")
        subs = x.get("subtopics", [])
        if not subs or len(subs) != len(set(subs)) or any(
            not SLUG.fullmatch(s) for s in subs
        ):
            raise ValueError(f"Bad subtopics in {key}")
        topics[key] = x

    word_rows = csv_rows(base / "words_candidate.csv", EXPECTED_WORD_COLS)
    topic_rows = csv_rows(base / "word_topics_candidate.csv", EXPECTED_TOPIC_COLS)
    if not word_rows:
        raise ValueError("Lexicon must have at least one word")
    words = {}
    answers = {}
    for row in word_rows:
        key = row["word_id"]
        answer = row["answer"]
        context = f"word {key}/{answer}"
        if not ID.fullmatch(key) or key in words:
            raise ValueError(f"{context}: invalid or duplicate word ID")
        if not ANSWER.fullmatch(answer):
            raise ValueError(f"{context}: answer must be uppercase A-Z, 3..20")
        if row["locale"] != "en-US":
            raise ValueError(f"{context}: unsupported locale; use versioned locale schema")
        if (row["locale"], answer) in answers:
            raise ValueError(f"{context}: duplicate canonical answer")
        if normalized(row["display"]) != answer:
            raise ValueError(f"{context}: displayed word doesn't normalize to answer")
        if not row["editor_notes"].strip():
            raise ValueError(f"{context}: editorial provenance/reason required")
        approval_signed(row, context)
        source = sources.get(row["source_id"])
        if source is None:
            raise ValueError(f"{context}: unknown source {row['source_id']}")
        if row["review_status"] == "approved" and source["rights_review"] not in {
            "cleared", "internal-existing"
        }:
            raise ValueError(f"{context}: unlicensed or uncleared source cannot be approved")
        words[key] = row
        answers[row["locale"], answer] = key

    links = set()
    tier_by_topic = Counter()
    approved_word_ids = set()
    reviewed_word_ids = set()
    for row in word_rows:
        if row["review_status"] in {"reviewed", "approved"}:
            reviewed_word_ids.add(row["word_id"])
    for row in topic_rows:
        word_id, topic, sub = row["word_id"], row["topic_id"], row["subtopic_id"]
        context = f"assignment {word_id}/{topic}/{sub}"
        if word_id not in words:
            raise ValueError(f"{context}: orphaned word")
        if topic not in topics or sub not in topics[topic]["subtopics"]:
            raise ValueError(f"{context}: missing topic/subtopic")
        if row["tier"] not in TIERS:
            raise ValueError(f"{context}: unknown familiarity tier")
        if not row["editor_notes"].strip():
            raise ValueError(f"{context}: rationale required")
        approval_signed(row, context)
        lookup = (word_id, topic, sub)
        if lookup in links:
            raise ValueError(f"{context}: duplicate association")
        links.add(lookup)
        if row["review_status"] == "approved":
            if words[word_id]["review_status"] != "approved":
                raise ValueError(f"{context}: association cannot be approved before word")
            if topics[topic]["status"] != "active":
                raise ValueError(f"{context}: association cannot be approved on inactive topic")
            tier_by_topic[topic, row["tier"]] += 1
            approved_word_ids.add(word_id)
    if set(words) - {row["word_id"] for row in topic_rows}:
        raise ValueError("Every candidate word needs at least one topic assignment")
    return {
        "status": "VALIDATED_CANDIDATES_NOT_RELEASED",
        "schema_version": 1,
        "global_candidate_unique": len(words),
        "global_reviewed_unique": len(reviewed_word_ids),
        "global_approved_playable_unique": len(approved_word_ids),
        "candidate_topic_assignments": len(links),
        "approved_topic_assignments": sum(tier_by_topic.values()),
        "topic_families_proposed_or_active": len(topics),
        "topics_active": sum(t["status"] == "active" for t in topics.values()),
        "by_review_status": dict(sorted(Counter(row["review_status"] for row in word_rows).items())),
        "eligible_approved_assignments_by_topic_tier": {
            f"{topic}/{tier}": value for (topic, tier), value in sorted(tier_by_topic.items())
        },
        "release_goal_unique_approved_words": 10000,
        "gap_to_10000": max(0, 10000-len(approved_word_ids)),
        "notice": "No Android input or previously frozen generator changed."
    }


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--data-dir", type=Path, default=Path("data/lexicon"))
    parser.add_argument("--json-out", type=Path)
    args = parser.parse_args()
    try:
        report = audit(args.data_dir)
    except (ValueError, KeyError, OSError, json.JSONDecodeError) as exc:
        print(f"FAIL lexicon audit: {exc}", file=sys.stderr)
        raise SystemExit(1)
    print(json.dumps(report, sort_keys=True, indent=2))
    if args.json_out:
        args.json_out.parent.mkdir(parents=True, exist_ok=True)
        args.json_out.write_text(json.dumps(report, indent=2) + "\n", encoding="utf8")


if __name__ == "__main__":
    main()
