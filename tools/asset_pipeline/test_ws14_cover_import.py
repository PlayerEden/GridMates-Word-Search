"""WS-14B2 original-cover QA import regression tests; stdlib only."""
import hashlib
import json
from pathlib import Path
import struct
import tempfile
import unittest
import zlib

from import_ws14_cover import prepare


def make_png(w=384, h=512):
    def chunk(tag, data):
        return struct.pack(">I", len(data)) + tag + data + struct.pack(">I", zlib.crc32(tag + data))
    ihdr = struct.pack(">IIBBBBB", w, h, 8, 6, 0, 0, 0)
    pixels = zlib.compress((b"\x00" + b"\xef\xd9\xb8\xff" * w) * h)
    return b"\x89PNG\r\n\x1a\n" + chunk(b"IHDR", ihdr) + chunk(b"IDAT", pixels) + chunk(b"IEND", b"")


class CoverImportTests(unittest.TestCase):
    def setUp(self):
        self.temp = tempfile.TemporaryDirectory()
        self.root = Path(self.temp.name)
        for path in ("design/visual-system/masters", "design/visual-system/exports",
                     "tools/asset_pipeline"):
            (self.root / path).mkdir(parents=True)
        self.registry = self.root / "design/visual-system/ws14-art-register.json"
        self.registry.write_text(json.dumps({
            "schema_version": 1,
            "visual_system": "gridmates-word-search-cozy-paperback",
            "assets": []
        }))
        validator = Path(__file__).with_name("validate_ws14_assets.py")
        (self.root / "tools/asset_pipeline/validate_ws14_assets.py").write_bytes(validator.read_bytes())
        self.master_rel = "design/visual-system/masters/woodland.png"
        self.export_rel = "design/visual-system/exports/woodland.png"
        self.master = self.root / self.master_rel
        self.export = self.root / self.export_rel
        self.master.write_bytes(make_png())
        self.export.write_bytes(make_png())

    def tearDown(self):
        self.temp.cleanup()

    def run_import(self, **changes):
        args = dict(root=self.root, theme="woodland",
                    master_rel=self.master_rel, export_rel=self.export_rel,
                    master_sha=hashlib.sha256(self.master.read_bytes()).hexdigest(),
                    export_sha=hashlib.sha256(self.export.read_bytes()).hexdigest(),
                    provenance="Original generation; commercial rights still awaiting review")
        args.update(changes)
        return prepare(**args)

    def test_actual_source_registers_only_as_debug_qa(self):
        art = self.run_import()
        self.assertEqual(art["status"], "QA_INTEGRATED")
        self.assertEqual(art["exports"][0]["android_source_set"], "debug")
        self.assertEqual(art["source"]["rights_status"], "PENDING_REVIEW")
        self.assertTrue((self.root / "app/src/debug/assets/ws14/books/woodland.png").is_file())

    def test_real_spine_integrates_only_as_debug_qa(self):
        self.export.write_bytes(make_png(96, 512))
        art = self.run_import(kind="spine")
        self.assertEqual("BOOK_WOODLAND_SPINE_V1", art["asset_id"])
        self.assertEqual("spine-ornament-art", art["purpose"])
        self.assertEqual("QA_INTEGRATED", art["status"])
        self.assertTrue((self.root / "app/src/debug/assets/ws14/spines/woodland.png").is_file())

    def test_bad_spine_proportions_denied(self):
        with self.assertRaisesRegex(ValueError, "spine must be narrow"):
            self.run_import(kind="spine")

    def test_mismatched_hash_does_not_write(self):
        with self.assertRaisesRegex(ValueError, "SHA-256"):
            self.run_import(export_sha="0" * 64)
        self.assertFalse((self.root / "app").exists())

    def test_watermarked_thumbnail_path_denied(self):
        with self.assertRaisesRegex(ValueError, "watermarked or thumbnail"):
            self.run_import(master_rel="design/visual-system/masters/thumbnail.png")

    def test_wrong_aspect_ratio_denied(self):
        self.export.write_bytes(make_png(600, 500))
        with self.assertRaisesRegex(ValueError, "near 3:4"):
            self.run_import()

    def test_corrupt_png_crc_denied(self):
        bad = bytearray(make_png())
        bad[30] ^= 0x01
        self.export.write_bytes(bad)
        with self.assertRaisesRegex(ValueError, "CRC"):
            self.run_import()

    def test_duplicate_asset_rejected(self):
        self.run_import()
        with self.assertRaisesRegex(ValueError, "already registered"):
            self.run_import()

    def test_missing_provenance_denied(self):
        with self.assertRaisesRegex(ValueError, "provenance"):
            self.run_import(provenance="")


if __name__ == "__main__":
    unittest.main()
