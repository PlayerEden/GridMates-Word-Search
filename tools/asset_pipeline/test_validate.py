"""WS-12 source-hash / no-fake-approval checks; no commercial art files involved."""
import copy
from hashlib import sha256
import json
from pathlib import Path
import shutil
import tempfile
import unittest

from validate import audit

REPO_ROOT = Path(__file__).resolve().parents[2]


class AssetReviewTests(unittest.TestCase):
    def setUp(self):
        self.tmp = tempfile.TemporaryDirectory()
        self.root = Path(self.tmp.name)
        self.location = self.root / "design" / "asset-pipeline"
        self.location.mkdir(parents=True)
        for p in ("pilot-concept-register.json", "approved-assets.json"):
            shutil.copy(REPO_ROOT / "design" / "asset-pipeline" / p, self.location / p)
        self.concepts = json.loads((self.location / "pilot-concept-register.json").read_text())
        self.production = json.loads((self.location / "approved-assets.json").read_text())

    def tearDown(self):
        self.tmp.cleanup()

    def save(self):
        (self.location / "pilot-concept-register.json").write_text(
            json.dumps(self.concepts))
        (self.location / "approved-assets.json").write_text(
            json.dumps(self.production))

    def test_baseline_registers_three_concepts_and_zero_approved(self):
        self.save()
        report = audit(self.root)
        self.assertEqual(report["creative_directions"], 2)
        self.assertEqual(report["pilot_concepts"], 3)
        self.assertEqual(report["adobe_refinements"], 0)
        self.assertEqual(report["production_approved_assets"], 0)
        self.assertFalse(report["binary_sources_verified"])

    def test_cannot_pretend_adobe_refinement_or_approval_completed(self):
        for field in ("adobe_refined", "owner_approved", "exported_to_android"):
            self.concepts["book_concept_candidates"][0][field] = True
            self.save()
            with self.assertRaisesRegex(ValueError, "claim"):
                audit(self.root)
            self.concepts["book_concept_candidates"][0][field] = False

    def test_cannot_forge_concept_hash_or_repeat_same_id(self):
        self.concepts["book_concept_candidates"][0]["crop_sha256"] = "not-a-sha"
        self.save()
        with self.assertRaisesRegex(ValueError, "hash"):
            audit(self.root)
        self.concepts["book_concept_candidates"][0]["crop_sha256"] = (
            json.loads((REPO_ROOT / "design/asset-pipeline/pilot-concept-register.json").read_text())
            ["book_concept_candidates"][0]["crop_sha256"]
        )
        self.concepts["book_concept_candidates"][1]["asset_id"] = (
            self.concepts["book_concept_candidates"][0]["asset_id"])
        self.save()
        with self.assertRaisesRegex(ValueError, "Duplicate"):
            audit(self.root)

    def test_reject_unapproved_asset_in_android_registry(self):
        self.production["assets"].append({"asset_id": "gmws-test-art", "review_status": "concept"})
        self.save()
        with self.assertRaisesRegex(ValueError, "unapproved"):
            audit(self.root)

    def make_approved(self, licensed=False):
        master = self.root / "design" / "approved-art" / "test_master.bin"
        delivery = self.root / "app" / "src" / "main" / "assets" / "test_art.bin"
        master.parent.mkdir(parents=True, exist_ok=True)
        delivery.parent.mkdir(parents=True, exist_ok=True)
        master.write_bytes(b"fictional test source file")
        delivery.write_bytes(b"fictional test Android export")
        return {
            "asset_id": "gmws-approved-test-art",
            "kind": "book-cover", "theme_id": "woodland",
            "review_status": "approved", "source_type": (
                "licensed-third-party" if licensed else "human-original"),
            "source_file": "design/approved-art/test_master.bin",
            "source_sha256": sha256(master.read_bytes()).hexdigest(),
            "export_file": "app/src/main/assets/test_art.bin",
            "export_sha256": sha256(delivery.read_bytes()).hexdigest(),
            "license_id": "test-license",
            "license_notice_path": None,
            "fallback_asset_id": "native-fallback",
            "accessible_label": "Woodland book cover",
            "editor_name": "Review Editor",
            "approval_date": "2026-11-08",
            "approval_evidence": "test-evidence-id",
            "dimensions_px": {"width": 320, "height": 460},
            "history": [{"operation":"create","tool":"unit-test","notes":"temporary dummy"}]
        }

    def test_fully_signed_and_hashed_demo_entry_can_pass(self):
        self.production["assets"].append(self.make_approved())
        self.save()
        self.assertEqual(audit(self.root)["production_approved_assets"], 1)

    def test_missing_signoff_and_changed_binary_fail(self):
        asset = self.make_approved()
        asset["editor_name"] = None
        self.production["assets"].append(asset)
        self.save()
        with self.assertRaisesRegex(ValueError, "human signoff"):
            audit(self.root)
        asset["editor_name"] = "Editor"
        (self.root / asset["export_file"]).write_bytes(b"tampered export")
        self.save()
        with self.assertRaisesRegex(ValueError, "hash mismatch"):
            audit(self.root)

    def test_licensed_art_cannot_omit_notices(self):
        self.production["assets"].append(self.make_approved(licensed=True))
        self.save()
        with self.assertRaisesRegex(ValueError, "license notice"):
            audit(self.root)

    def test_approved_art_cannot_reference_files_outside_repository(self):
        asset = self.make_approved()
        asset["export_file"] = "../secrets.png"
        self.production["assets"].append(asset)
        self.save()
        with self.assertRaisesRegex(ValueError, "traversal|relative"):
            audit(self.root)


if __name__ == "__main__":
    unittest.main()
