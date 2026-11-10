#!/usr/bin/env python3
import unittest, json, hashlib
from pathlib import Path

ROOT = Path(__file__).resolve().parents[2]
DEST = ROOT / "app/src/debug/assets/ws16g/shelf"
class Ws16GShelfTest(unittest.TestCase):
    def test_eight_pinned_debug_exports(self):
        manifest=json.loads((DEST/"manifest.json").read_text())
        self.assertTrue(manifest["debug_only"])
        self.assertFalse(manifest["release_approved"])
        self.assertEqual(8,len(manifest["items"]))
        for name,detail in manifest["items"].items():
            data=(DEST/name).read_bytes()
            self.assertEqual(detail["export_sha256"],hashlib.sha256(data).hexdigest())
            self.assertTrue(data.startswith(b"\x89PNG\r\n\x1a\n"))
