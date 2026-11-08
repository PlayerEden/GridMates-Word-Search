"""WS-12E: art-family completeness, anti-slop metadata and approval guard."""
import copy
import json
from pathlib import Path
import shutil
import tempfile
import unittest

from validate_production_queue import audit_queue

ROOT = Path(__file__).resolve().parents[2]
FILE = "design/asset-pipeline/production-queue-v1.json"


class ProductionQueueTests(unittest.TestCase):
    def setUp(self):
        self.temp = tempfile.TemporaryDirectory()
        self.root = Path(self.temp.name)
        files = [
            FILE,
            "design/asset-pipeline/approved-assets.json",
            "design/asset-pipeline/paperback-v1-tokens.json",
            "docs/WS12_ART_DIRECTION_STYLE_BIBLE.md",
            "docs/WS12_AI_ASSET_PRODUCTION_PLAYBOOK.md",
            "tools/asset_pipeline/render_paperback.py",
            "tools/asset_pipeline/render_paperback_accents.py",
        ]
        for relative in files:
            path = self.root / relative
            path.parent.mkdir(parents=True, exist_ok=True)
            shutil.copy(ROOT / relative, path)
        self.data = json.loads((self.root / FILE).read_text())

    def tearDown(self):
        self.temp.cleanup()

    def save(self):
        (self.root / FILE).write_text(json.dumps(self.data), encoding="utf-8")

    def assert_rejected(self, expression):
        self.save()
        with self.assertRaisesRegex(ValueError, expression):
            audit_queue(self.root)

    def test_real_queue_accounts_for_three_complete_books_and_collections(self):
        self.save()
        report = audit_queue(self.root)
        self.assertEqual(report["book_families"], 3)
        self.assertEqual(report["asset_slots"], 18)
        self.assertEqual(report["collections"], 7)
        self.assertEqual(report["slot_statuses"]["prototype"], 18)
        self.assertEqual(report["slot_statuses"]["planned"], 0)
        self.assertEqual(report["slot_statuses"]["approved"], 0)
        self.assertEqual(report["status"], "QUEUE_ONLY_NO_OWNER_APPROVAL")

    def test_cannot_rebrand_as_approved_without_real_evidence(self):
        self.data["status"] = "APPROVED_ALL_ART"
        self.assert_rejected("may not claim")
        self.data["status"] = "PLANNED_AND_PROTOTYPE_ONLY_NO_RELEASE_APPROVAL"
        self.data["books"][0]["assets"][0]["status"] = "approved"
        self.data["books"][0]["assets"][0]["owner_approved"] = True
        self.assert_rejected("real signed manifest")

    def test_cannot_mix_styles_between_books(self):
        self.data["books"][1]["motif"] = "random-dragon"
        self.assert_rejected("motif")
        self.data["books"][1]["motif"] = "orbit"
        self.data["art_system_id"] = "totally-different-cover-style"
        self.assert_rejected("recipe")

    def test_cannot_omit_bookmark_or_duplicate_chapter_marker(self):
        self.data["books"][0]["assets"].pop()
        self.assert_rejected("missing required")
        self.data["books"][0]["assets"].append(
            copy.deepcopy(self.data["books"][0]["assets"][0])
        )
        self.assert_rejected("duplicate")

    def test_cannot_change_frozen_asset_ids(self):
        self.data["books"][2]["assets"][0]["asset_id"] = "surprise-different-cover"
        self.assert_rejected("identity changed")

    def test_no_fake_prototype_source_or_fabricated_review_status(self):
        self.data["books"][1]["assets"][0]["source_renderer"] = "tools/asset_pipeline/gone.py"
        self.assert_rejected("missing prototype")
        self.data["books"][1]["assets"][0]["source_renderer"] = (
            "tools/asset_pipeline/render_paperback.py"
        )
        self.data["books"][1]["review_status"] = "approved"
        self.assert_rejected("claim final approval")

    def test_no_baked_titles_or_bogus_reviewers(self):
        self.data["books"][0]["assets"][3]["native_text_required"] = False
        self.assert_rejected("native UI")
        self.data["books"][0]["assets"][3]["native_text_required"] = True
        self.data["books"][0]["approval_evidence"] = "some-review-id"
        self.assert_rejected("fabricated")

    def test_original_woodland_portraits_cannot_be_turned_into_new_generations(self):
        item = next(x for x in self.data["collections"]
                    if x["family_id"] == "woodland-original-portraits")
        item["status"] = "planned"
        self.assert_rejected("frozen")

    def test_cannot_remove_owner_rights_device_or_old_save_guards(self):
        self.data["release_gates"].remove("owner-visual-approval")
        self.assert_rejected("mandatory")

    def test_cannot_reference_files_outside_repo(self):
        self.data["style_bible"] = "../private-cover.png"
        self.assert_rejected("Unsafe source path")


if __name__ == "__main__":
    unittest.main()
