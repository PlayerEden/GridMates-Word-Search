"""WS-10B2 human review queue tests: no auto-approval and explicit source evidence."""
import csv
from pathlib import Path
import tempfile
import unittest

from prepare_theme_review import generate, REVIEW_FIELDS


def write_csv(path, headers, rows):
    with path.open("w", newline="", encoding="utf8") as f:
        writer = csv.DictWriter(f, fieldnames=headers)
        writer.writeheader()
        writer.writerows(rows)


class ReviewQueueTests(unittest.TestCase):
    def setUp(self):
        self.temp = tempfile.TemporaryDirectory()
        self.dir = Path(self.temp.name)
        self.source = self.dir / "quarantine.csv"
        self.seeds = self.dir / "seeds.csv"
        self.notice = self.dir / "copyright.txt"
        self.output = self.dir / "review"
        self.notice.write_text("Copyright 2000-2026 by Kevin Atkinson")
        source_headers = [
            "answer", "display_proposal", "source_id", "source_release",
            "source_line", "review_status", "topic_status", "human_review_required"
        ]
        self.source_rows = []
        for answer in ("FOREST", "GALAXY", "FLOWER", "RADAR"):
            self.source_rows.append({
                "answer": answer, "display_proposal": answer.lower(),
                "source_id": "esdb-us-2026-02-25", "source_release": "rel-2026.02.25",
                "source_line": "6", "review_status": "candidate",
                "topic_status": "unclassified", "human_review_required": "true"
            })
        write_csv(self.source, source_headers, self.source_rows)
        self.seed_rows = [
            {"answer": "FOREST", "topic_suggestion": "woodland",
             "source_tier_status": "relaxed-proposal", "source_family": "lab",
             "source_blob_sha": "abc123"},
            {"answer": "GALAXY", "topic_suggestion": "space",
             "source_tier_status": "relaxed-proposal", "source_family": "lab",
             "source_blob_sha": "abc123"},
            {"answer": "FLOWER", "topic_suggestion": "garden",
             "source_tier_status": "advanced-proposal", "source_family": "lab",
             "source_blob_sha": "abc123"},
            {"answer": "RADAR", "topic_suggestion": "space",
             "source_tier_status": "hold", "source_family": "lab",
             "source_blob_sha": "abc123"},
            {"answer": "BROWNDWARF", "topic_suggestion": "space",
             "source_tier_status": "unreviewed-extra", "source_family": "lab",
             "source_blob_sha": "abc123"}
        ]
        self.seed_fields = list(self.seed_rows[0])
        self.write_seeds()

    def tearDown(self):
        self.temp.cleanup()

    def write_seeds(self):
        write_csv(self.seeds, self.seed_fields, self.seed_rows)

    def run_queue(self):
        return generate(self.source, self.seeds, self.output, self.notice)

    def test_three_theme_queues_keep_holds_and_unverified_terms_separate(self):
        summary = self.run_queue()
        self.assertEqual(summary["topic_review_queues"],
                         {"woodland": 1, "space": 2, "garden": 1})
        self.assertEqual(summary["research_holds"], 1)
        self.assertEqual(summary["missing_esdb_source_by_theme"], {"space": 1})
        self.assertEqual(summary["approved_unique_words"], 0)
        for name in ("woodland", "garden", "space"):
            with (self.output / f"{name}_editor_review_queue.csv").open() as f:
                data = list(csv.DictReader(f))
            self.assertTrue(all(r["requires_human_review"] == "true" for r in data))
            self.assertTrue(all(not r["reviewed_by"] and not r["editorial_decision"] for r in data))
        with (self.output / "original_research_holds.csv").open() as f:
            holds = list(csv.DictReader(f))
        self.assertEqual(holds[0]["answer"], "RADAR")
        self.assertEqual((self.output / "ESDB_SCOWL_COPYRIGHT_2026_02_25.txt").read_text(),
                         self.notice.read_text())

    def test_source_spelling_root_match_not_a_human_approval(self):
        self.run_queue()
        with (self.output / "space_editor_review_queue.csv").open() as f:
            data = list(csv.DictReader(f))
        present = next(x for x in data if x["answer"] == "GALAXY")
        missing = next(x for x in data if x["answer"] == "BROWNDWARF")
        self.assertEqual(present["source_lexicon_match"], "verified-esdb-root")
        self.assertEqual(missing["source_lexicon_match"], "needs-independent-word-check")
        self.assertEqual(missing["source_line"], "")
        self.assertTrue(all(x["approved_familiarity"] == "" for x in data))

    def test_duplicate_theme_word_rejected(self):
        self.seed_rows.append(self.seed_rows[0].copy())
        self.write_seeds()
        with self.assertRaisesRegex(ValueError, "Duplicate theme/answer"):
            self.run_queue()

    def test_approved_source_data_rejected(self):
        self.source_rows[0]["review_status"] = "approved"
        write_csv(self.source, list(self.source_rows[0]), self.source_rows)
        with self.assertRaisesRegex(ValueError, "unreviewed"):
            self.run_queue()

    def test_missing_rights_notice_rejected(self):
        self.notice.unlink()
        with self.assertRaisesRegex(ValueError, "copyright notice"):
            self.run_queue()


if __name__ == "__main__":
    unittest.main()
