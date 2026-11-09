"""WS-14A tests: no fake art approvals, export leaks, or broken hash receipts."""
import hashlib
import json
from pathlib import Path
import tempfile
import unittest

from validate_ws14_assets import verify_registry


class AssetRegisterTests(unittest.TestCase):
    def setUp(self):
        self.temp = tempfile.TemporaryDirectory()
        self.root = Path(self.temp.name)
        self.file = self.root / "register.json"
        self.source = self.root / "illustration.png"
        self.source.write_bytes(b"fake illustrative fixture, not a production image")
        self.export = self.root / "illustration.webp"
        self.export.write_bytes(b"fake Android fixture")
        self.base = {
            "schema_version": 1,
            "visual_system": "gridmates-word-search-cozy-paperback",
            "assets": []
        }

    def tearDown(self):
        self.temp.cleanup()

    def verify(self, asset=None):
        data = dict(self.base)
        data["assets"] = [] if asset is None else [asset]
        self.file.write_text(json.dumps(data), encoding="utf-8")
        return verify_registry(self.root, self.file)

    def item(self, state="QA_INTEGRATED"):
        def receipt(path):
            return {
                "path": path.name,
                "sha256": hashlib.sha256(path.read_bytes()).hexdigest(),
            }
        return {
            "asset_id": "BOOK_WOODLAND_COVER_V1",
            "family": "woodland",
            "purpose": "front-cover-art",
            "status": state,
            "source": dict(receipt(self.source), rights_status="PENDING_REVIEW"),
            "exports": [dict(receipt(self.export),
                android_source_set="debug", format="webp",
                pixel_width=512, pixel_height=683, alpha=True)]
        }

    def test_empty_registry_is_honest_and_valid(self):
        self.assertEqual(self.verify(), 0)

    def test_source_backed_qa_art_is_valid(self):
        self.assertEqual(self.verify(self.item()), 1)

    def test_unreviewed_art_cannot_leak_into_release(self):
        item = self.item()
        item["exports"][0]["android_source_set"] = "release"
        with self.assertRaisesRegex(ValueError, "unapproved art"):
            self.verify(item)

    def test_owner_approval_requires_actual_evidence(self):
        with self.assertRaisesRegex(ValueError, "owner approval evidence"):
            self.verify(self.item("OWNER_APPROVED"))

    def test_release_requires_documented_rights(self):
        item = self.item("RELEASE_APPROVED")
        item["owner_review"] = {
            "reviewed_by": "test-human", "reviewed_date": "2026-11-08",
            "approval_reference": "fixture",
        }
        item["exports"][0]["android_source_set"] = "release"
        with self.assertRaisesRegex(ValueError, "rights not documented"):
            self.verify(item)
        item["source"]["rights_status"] = "DOCUMENTED"
        self.assertEqual(self.verify(item), 1)

    def test_asset_mutation_fails_closed(self):
        item = self.item()
        self.source.write_bytes(b"different content")
        with self.assertRaisesRegex(ValueError, "SHA-256 mismatch"):
            self.verify(item)

    def test_path_traversal_rejected(self):
        item = self.item()
        item["source"]["path"] = "../private-image.png"
        with self.assertRaisesRegex(ValueError, "escapes repository"):
            self.verify(item)

    def test_duplicate_ids_rejected(self):
        item = self.item()
        d = dict(self.base)
        d["assets"] = [item, item]
        self.file.write_text(json.dumps(d))
        with self.assertRaisesRegex(ValueError, "duplicate asset ID"):
            verify_registry(self.root, self.file)


if __name__ == "__main__":
    unittest.main()
