#!/usr/bin/env python3
"""Self-contained WS-10 validation and 10K-row capacity tests."""
import csv
import json
from pathlib import Path
import shutil
import tempfile
import unittest

from validate import audit

ROOT = Path(__file__).resolve().parents[2]
SOURCE = ROOT / "data" / "lexicon"


def rows(path):
    with path.open(encoding="utf8", newline="") as f:
        r = csv.DictReader(f)
        return list(r), list(r.fieldnames)


def write_rows(path, records, headers):
    with path.open("w", encoding="utf8", newline="") as f:
        writer = csv.DictWriter(f, fieldnames=headers, lineterminator="\n")
        writer.writeheader()
        writer.writerows(records)


class LibraryValidationTests(unittest.TestCase):
    def setUp(self):
        self.temp = tempfile.TemporaryDirectory()
        self.base = Path(self.temp.name) / "lexicon"
        shutil.copytree(SOURCE, self.base)

    def tearDown(self):
        self.temp.cleanup()

    def edit_csv(self, name, editor):
        path = self.base / name
        data, headings = rows(path)
        editor(data)
        write_rows(path, data, headings)

    def test_baseline_is_honest_and_all_draft(self):
        report = audit(self.base)
        self.assertEqual(report["global_candidate_unique"], 40)
        self.assertEqual(report["global_reviewed_unique"], 0)
        self.assertEqual(report["global_approved_playable_unique"], 0)
        self.assertEqual(report["candidate_topic_assignments"], 76)
        self.assertEqual(report["approved_topic_assignments"], 0)
        self.assertEqual(report["topic_families_proposed_or_active"], 33)
        self.assertEqual(report["gap_to_10000"], 10000)

    def test_multi_topic_mappings_do_not_inflate_unique_words(self):
        entries, _ = rows(self.base / "word_topics_candidate.csv")
        self.assertGreater(sum(r["word_id"] == "gm000005" for r in entries), 1)
        self.assertEqual(audit(self.base)["global_candidate_unique"], 40)

    def test_substring_related_words_are_allowed_at_library_scope(self):
        entries, _ = rows(self.base / "words_candidate.csv")
        found = {r["answer"] for r in entries}
        self.assertIn("FLOWER", found)
        self.assertIn("SUNFLOWER", found)
        self.assertEqual(audit(self.base)["global_candidate_unique"], 40)

    def test_duplicate_answer_rejected_even_with_new_id(self):
        def editor(data):
            d = data[0].copy()
            d["word_id"] = "gm900001"
            data.append(d)
        self.edit_csv("words_candidate.csv", editor)
        with self.assertRaisesRegex(ValueError, "duplicate canonical answer"):
            audit(self.base)

    def test_bad_display_and_orphaned_topic_are_rejected(self):
        self.edit_csv("words_candidate.csv",
                      lambda data: data[0].update(display="Completely different"))
        with self.assertRaisesRegex(ValueError, "normalize"):
            audit(self.base)
        shutil.copytree(SOURCE, self.base, dirs_exist_ok=True)
        self.edit_csv("word_topics_candidate.csv",
                      lambda data: data[0].update(word_id="missing999"))
        with self.assertRaisesRegex(ValueError, "orphaned"):
            audit(self.base)

    def test_approval_requires_editor_and_approved_rights(self):
        self.edit_csv("words_candidate.csv",
                      lambda data: data[0].update(review_status="approved"))
        with self.assertRaisesRegex(ValueError, "named editor"):
            audit(self.base)
        def ready(data):
            data[0].update(review_status="approved", reviewed_by="Lexicon reviewer",
                           reviewed_on="2026-11-08", source_id="wordfreq-research")
        self.edit_csv("words_candidate.csv", ready)
        with self.assertRaisesRegex(ValueError, "uncleared source"):
            audit(self.base)

    def test_approved_words_need_approved_active_topic_assignment(self):
        def approve_word(data):
            data[0].update(review_status="approved", reviewed_by="Editor A",
                           reviewed_on="2026-11-08")
        self.edit_csv("words_candidate.csv", approve_word)
        self.assertEqual(audit(self.base)["global_approved_playable_unique"], 0)
        def approve_assignment(data):
            data[0].update(review_status="approved", reviewed_by="Editor B",
                           reviewed_on="2026-11-08")
        self.edit_csv("word_topics_candidate.csv", approve_assignment)
        with self.assertRaisesRegex(ValueError, "inactive topic"):
            audit(self.base)
        topics_path = self.base / "topics.json"
        d = json.loads(topics_path.read_text())
        d["topics"][0]["status"] = "active"
        topics_path.write_text(json.dumps(d))
        report = audit(self.base)
        self.assertEqual(report["global_approved_playable_unique"], 1)
        self.assertEqual(report["approved_topic_assignments"], 1)

    def test_10000_extra_synthetic_records_validate_without_counting_approval(self):
        # Transient fake strings are only for algorithmic capacity testing.
        # They are NEVER saved in the repository's actual candidate CSV.
        def label(i):
            digits = []
            for _ in range(5):
                digits.append(chr(65 + i % 26))
                i //= 26
            return "QZ" + "".join(reversed(digits))
        p = self.base / "words_candidate.csv"
        vals, header = rows(p)
        topic_path = self.base / "word_topics_candidate.csv"
        topic_rows, topic_header = rows(topic_path)
        for i in range(10000):
            answer = label(i)
            id_ = f"gm{800000+i}"
            vals.append({
                "word_id": id_, "answer": answer, "display": answer, "locale": "en-US",
                "review_status": "candidate", "source_id": "gridmates-internal",
                "editor_notes": "SYNTHETIC CAPACITY TEST ONLY", "reviewed_by": "",
                "reviewed_on": ""
            })
            topic_rows.append({
                "word_id": id_, "topic_id": "everyday", "subtopic_id": "objects",
                "tier": "familiar", "review_status": "candidate",
                "editor_notes": "SYNTHETIC CAPACITY TEST ONLY",
                "reviewed_by": "", "reviewed_on": ""
            })
        write_rows(p, vals, header)
        write_rows(topic_path, topic_rows, topic_header)
        report = audit(self.base)
        self.assertEqual(report["global_candidate_unique"], 10040)
        self.assertEqual(report["candidate_topic_assignments"], 10076)
        self.assertEqual(report["global_approved_playable_unique"], 0)


if __name__ == "__main__":
    unittest.main()
