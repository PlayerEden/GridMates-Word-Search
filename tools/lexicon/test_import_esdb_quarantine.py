"""WS-10B1 synthetic archive fixtures. Never download/import real corpus in unit tests."""
import csv
import hashlib
import json
from pathlib import Path
import tempfile
import unittest
import zipfile

from import_esdb_quarantine import ingest


LICENSE = """Copyright 2000-2026 by Kevin Atkinson

Permission to use, copy, modify, distribute, and sell any part of SCOWLv2, or
word lists created from it, is hereby granted without fee, provided that the
above copyright notice appears in all copies.
"""


class PinnedESDBIntakeTests(unittest.TestCase):
    def setUp(self):
        self.tmp = tempfile.TemporaryDirectory()
        self.root = Path(self.tmp.name)
        self.notice = self.root / "ESDB.txt"
        self.notice.write_text(LICENSE)
        self.archive = self.root / "source.zip"
        self.output = self.root / "quarantine"
        self.starter = self.root / "existing.csv"
        self.starter.write_text("answer\nFOREST\n")
        self.manifest = self.root / "source.json"
        self.make_archive("6\nforest/AB\nflower\nradar\nRiver\nspace-craft\nbookcase\n")

    def tearDown(self):
        self.tmp.cleanup()

    def make_archive(self, text, member="en_US.dic", extra=None):
        with zipfile.ZipFile(self.archive, "w", zipfile.ZIP_DEFLATED) as archive:
            archive.writestr(member, text)
            if extra:
                for name, value in extra.items():
                    archive.writestr(name, value)
        blob = self.archive.read_bytes()
        manifest = {
            "schema_version": 1,
            "status": "SOURCE_PINNED_CANDIDATE_ONLY_NOT_GAME_CONTENT",
            "source_id": "esdb-us-2026-02-25",
            "upstream_release_tag": "rel-2026.02.25",
            "artifact": {
                "name": "hunspell-en_US-2026.02.25.zip",
                "expected_dictionary_member": "en_US.dic",
                "bytes": len(blob),
                "sha256": hashlib.sha256(blob).hexdigest()
            },
            "extraction": {"output": "quarantine-only", "allow_approval": False},
            "copyright_notice": {"pinned_repo_path": str(self.notice)}
        }
        self.manifest.write_text(json.dumps(manifest))

    def load(self):
        return ingest(self.manifest, self.archive, self.output,
                      self.starter, self.notice)

    def test_explicit_source_pin_and_true_candidate_counts(self):
        report = self.load()
        self.assertEqual(report["candidate_unique_unreviewed"], 4)
        self.assertEqual(report["approved_unique_added_to_game"], 0)
        self.assertEqual(report["candidate_palindrome_flags"], 1)
        self.assertEqual(report["candidate_already_in_starter_catalog"], 1)
        with (self.output / "quarantine_candidates.csv").open() as f:
            rows = list(csv.DictReader(f))
        self.assertEqual({r["answer"] for r in rows},
                         {"FOREST", "FLOWER", "RADAR", "BOOKCASE"})
        self.assertTrue(all(r["review_status"] == "candidate" for r in rows))
        self.assertTrue(all(r["topic_status"] == "unclassified" for r in rows))
        self.assertEqual((self.output / "ESDB_SCOWL_COPYRIGHT_2026_02_25.txt")
                         .read_text(), LICENSE)
        self.assertEqual(report["excluded_rows_by_reason"]
                         ["non_ascii_or_non_lowercase_or_length"], 2)

    def test_changed_source_hash_fails_before_writing_output(self):
        with self.archive.open("ab") as f:
            f.write(b"tamper")
        with self.assertRaisesRegex(ValueError, "SHA-256 mismatch"):
            self.load()
        self.assertFalse(self.output.exists())

    def test_wrong_release_or_autoapproval_fails(self):
        manifest = json.loads(self.manifest.read_text())
        manifest["upstream_release_tag"] = "latest"
        self.manifest.write_text(json.dumps(manifest))
        with self.assertRaisesRegex(ValueError, "release tag"):
            self.load()
        manifest["upstream_release_tag"] = "rel-2026.02.25"
        manifest["extraction"]["allow_approval"] = True
        self.manifest.write_text(json.dumps(manifest))
        with self.assertRaisesRegex(ValueError, "quarantine-only"):
            self.load()

    def test_archive_with_traversal_or_duplicate_dictionary_fails(self):
        self.make_archive("1\nforest\n", extra={"../unsafe.txt": "x"})
        with self.assertRaisesRegex(ValueError, "Unsafe ZIP"):
            self.load()
        self.make_archive("1\nforest\n", extra={"extra/en_US.dic": "1\nforest\n"})
        with self.assertRaisesRegex(ValueError, "precisely one en_US.dic"):
            self.load()

    def test_missing_copyright_notice_is_rejected(self):
        self.notice.unlink()
        with self.assertRaisesRegex(ValueError, "copyright notice"):
            self.load()

    def test_invalid_dictionary_header_and_bad_encoding_rejected(self):
        self.make_archive("not-a-count\nforest\n")
        with self.assertRaisesRegex(ValueError, "first line"):
            self.load()
        self.make_archive("2\nforest\nmoon\n")
        with zipfile.ZipFile(self.archive, "w") as archive:
            archive.writestr("en_US.dic", b"2\nforest\n\xfe\xff")
        # Amend hash to test actual decode rather than pinned-archive gate.
        m = json.loads(self.manifest.read_text())
        blob = self.archive.read_bytes()
        m["artifact"]["bytes"] = len(blob)
        m["artifact"]["sha256"] = hashlib.sha256(blob).hexdigest()
        self.manifest.write_text(json.dumps(m))
        with self.assertRaisesRegex(ValueError, "not UTF-8"):
            self.load()


if __name__ == "__main__":
    unittest.main()
