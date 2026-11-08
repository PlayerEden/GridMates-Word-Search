#!/usr/bin/env python3
"""Reproducible, SOURCE-PINNED, ZERO-APPROVAL English lexicon quarantine import.

This intentionally does NOT write data/lexicon/words_candidate.csv, any Kotlin
file, or shipped assets. It reads a pinned Hunspell en_US zip and produces
candidate CSV and report in a caller-specified output directory.

The zip must first be acquired from the EXACT URL pinned by the manifest.
CI downloads the archive, then verifies byte length and SHA-256 before this
script inspects its contents. No network is used by this script itself.
"""
import argparse
import csv
import hashlib
import json
from collections import Counter
from pathlib import Path, PurePosixPath
import re
import sys
import zipfile


MAX_ARCHIVE_BYTES = 15 * 1024 * 1024
MAX_DICTIONARY_BYTES = 20 * 1024 * 1024
ALLOWED_WORD = re.compile(r"[a-z]{3,20}\Z")
DIGITS = re.compile(r"[0-9]{1,9}\Z")
OUT_FIELDS = [
    "answer", "display_proposal", "source_id", "source_release",
    "source_member", "source_line", "review_status", "topic_status",
    "human_review_required", "potential_palindrome", "already_in_starter_catalog",
    "editor_notes"
]


def sha256(data: bytes) -> str:
    return hashlib.sha256(data).hexdigest()


def read_csv_answers(path: Path) -> set[str]:
    with path.open(encoding="utf-8", newline="") as stream:
        return {line["answer"] for line in csv.DictReader(stream)}


def ingest(manifest_path: Path, archive_path: Path, output_dir: Path,
           starter_path: Path, rights_notice_path: Path) -> dict:
    m = json.loads(manifest_path.read_text(encoding="utf8"))
    if m.get("schema_version") != 1 or m.get("status") != "SOURCE_PINNED_CANDIDATE_ONLY_NOT_GAME_CONTENT":
        raise ValueError("Unrecognized or non-quarantine source manifest")
    if m.get("source_id") != "esdb-us-2026-02-25":
        raise ValueError("This importer only authorizes the exactly pinned ESDB US source")
    if m.get("upstream_release_tag") != "rel-2026.02.25":
        raise ValueError("Upstream release tag mismatch")
    if m.get("extraction", {}).get("output") != "quarantine-only" or m["extraction"].get("allow_approval") is not False:
        raise ValueError("Import must be quarantine-only with auto-approval disabled")
    asset = m["artifact"]
    if asset["name"] != "hunspell-en_US-2026.02.25.zip" or asset["expected_dictionary_member"] != "en_US.dic":
        raise ValueError("Unexpected pinned archive/member")
    if not re.fullmatch("[0-9a-f]{64}", asset["sha256"]):
        raise ValueError("Source hash missing")
    if not rights_notice_path.is_file():
        raise ValueError("Mandatory pinned copyright notice is missing")
    notice = rights_notice_path.read_text(encoding="utf8")
    for phrase in ("Copyright 2000-2026 by Kevin Atkinson",
                   "Permission to use, copy, modify, distribute, and sell any part of SCOWLv2",
                   "provided that the"):
        if phrase not in notice:
            raise ValueError("Pinned copyright text truncated or invalid")
    if m["copyright_notice"]["pinned_repo_path"] != str(rights_notice_path).replace("\\", "/"):
        raise ValueError("Copyright path does not match pinned source record")
    if not starter_path.is_file():
        raise ValueError("Existing candidate baseline is required to flag duplicates")
    existing = read_csv_answers(starter_path)
    if not archive_path.is_file() or archive_path.stat().st_size > MAX_ARCHIVE_BYTES:
        raise ValueError("Missing or oversized source archive")
    raw = archive_path.read_bytes()
    if len(raw) != asset["bytes"] or sha256(raw) != asset["sha256"]:
        raise ValueError("Source zip byte-size or SHA-256 mismatch; import blocked")
    with zipfile.ZipFile(archive_path) as zipped:
        members = zipped.infolist()
        if len(members) > 100 or any(
            "\\" in inf.filename or inf.filename.startswith("/")
            or ".." in PurePosixPath(inf.filename).parts
            or inf.file_size > MAX_DICTIONARY_BYTES for inf in members
        ):
            raise ValueError("Unsafe ZIP member/path/size")
        selected = [inf for inf in members
                    if PurePosixPath(inf.filename).name == asset["expected_dictionary_member"]]
        if len(selected) != 1:
            raise ValueError("ZIP must contain precisely one en_US.dic")
        content = zipped.read(selected[0])
    try:
        lines = content.decode("utf-8-sig").splitlines()
    except UnicodeError as err:
        raise ValueError("Source dictionary not UTF-8") from err
    if not lines or not DIGITS.fullmatch(lines[0].strip()):
        raise ValueError("Hunspell first line must be a declared dictionary count")
    declared = int(lines[0].strip())
    # Some Hunspell exports contain feature flags and morphology after a tab.
    # No affix expansion is attempted: root candidates ONLY.
    items = {}
    skipped = Counter()
    total_nonblank = 0
    for line_no, line in enumerate(lines[1:], 2):
        if not line.strip():
            skipped["blank_line"] += 1
            continue
        total_nonblank += 1
        token = line.split("\t", 1)[0].strip()
        if "/" in token:
            token = token.split("/", 1)[0]
        if not ALLOWED_WORD.fullmatch(token):
            skipped["non_ascii_or_non_lowercase_or_length"] += 1
            continue
        answer = token.upper()
        if answer in items:
            skipped["duplicate_root"] += 1
            continue
        items[answer] = {
            "answer": answer,
            "display_proposal": token,  # NOT reviewed/final label
            "source_id": m["source_id"],
            "source_release": m["upstream_release_tag"],
            "source_member": selected[0].filename,
            "source_line": line_no,
            "review_status": "candidate",
            "topic_status": "unclassified",
            "human_review_required": "true",
            "potential_palindrome": str(answer == answer[::-1]).lower(),
            "already_in_starter_catalog": str(answer in existing).lower(),
            "editor_notes": "Unreviewed spelling root; NOT themed, family-rated, or playable."
        }
    if total_nonblank == 0 or not items:
        raise ValueError("Dictionary contains no candidate roots")
    output_dir.mkdir(parents=True, exist_ok=True)
    with (output_dir / "quarantine_candidates.csv").open("w", encoding="utf8", newline="") as stream:
        writer = csv.DictWriter(stream, fieldnames=OUT_FIELDS, lineterminator="\n")
        writer.writeheader()
        writer.writerows(items[word] for word in sorted(items))
    # Preserve entire upstream notice alongside any distributed candidate artifact.
    (output_dir / "ESDB_SCOWL_COPYRIGHT_2026_02_25.txt").write_text(notice, encoding="utf8")
    report = {
        "stage": "WS-10B1_SOURCE_PINNED_QUARANTINE_ONLY",
        "source_id": m["source_id"],
        "source_release_tag": m["upstream_release_tag"],
        "archive_sha256_verified": sha256(raw),
        "archive_bytes_verified": len(raw),
        "source_declared_hunspell_entries": declared,
        "source_actual_nonblank_lines": total_nonblank,
        "candidate_unique_unreviewed": len(items),
        "candidate_already_in_starter_catalog": sum(
            x["already_in_starter_catalog"] == "true" for x in items.values()),
        "candidate_palindrome_flags": sum(
            x["potential_palindrome"] == "true" for x in items.values()),
        "excluded_rows_by_reason": dict(sorted(skipped.items())),
        "approved_unique_added_to_game": 0,
        "thematically_assigned_to_game": 0,
        "android_code_or_saves_modified": False,
        "license_notice_included": True,
        "rights_compliance_review_status": "publisher_permission_not_final_product_notice_signoff",
        "warning": "Speller roots include obscure, sensitive, and context-inappropriate words. Do not publish or auto-approve."
    }
    (output_dir / "intake_report.json").write_text(
        json.dumps(report, indent=2, sort_keys=True) + "\n", encoding="utf8")
    return report


def main() -> None:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--manifest", type=Path,
        default=Path("data/lexicon/intake/esdb_us_2026_02_25.json"))
    parser.add_argument("--archive", type=Path, required=True)
    parser.add_argument("--output-dir", type=Path, required=True)
    parser.add_argument("--starter", type=Path, default=Path("data/lexicon/words_candidate.csv"))
    parser.add_argument("--notice", type=Path, default=Path(
        "data/lexicon/licenses/ESDB_SCOWL_COPYRIGHT_2026_02_25.txt"))
    a = parser.parse_args()
    try:
        result = ingest(a.manifest, a.archive, a.output_dir, a.starter, a.notice)
    except (ValueError, OSError, zipfile.BadZipFile, KeyError, json.JSONDecodeError) as exc:
        print(f"FAIL WS-10B1 quarantine import: {exc}", file=sys.stderr)
        sys.exit(1)
    print(json.dumps(result, indent=2, sort_keys=True))
    print("PASS: pinned ESDB US source, review-only candidate extraction, zero auto-approvals.")


if __name__ == "__main__":
    main()
