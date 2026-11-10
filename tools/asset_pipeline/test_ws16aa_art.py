#!/usr/bin/env python3
"""Tests source-hash-pinned WS16AA shelf and paper art; does not claim owner approval."""
import hashlib
import json
import struct
import unittest
from pathlib import Path

ROOT = Path(__file__).resolve().parents[2]
REGISTER = ROOT / "design/visual-system/ws16aa-art-register.json"
OUTPUT = ROOT / "app/src/debug/assets/ws16aa"

def git_oid(raw):
    return hashlib.sha1(b"blob " + str(len(raw)).encode() + b"\x00" + raw).hexdigest()

class Ws16AaArtTest(unittest.TestCase):
    def test_real_separate_original_masters_and_debug_exports(self):
        reg = json.loads(REGISTER.read_text())
        receipt = json.loads((OUTPUT / "manifest.json").read_text())
        self.assertFalse(reg["owner_approved"])
        self.assertFalse(reg["release_approved"])
        self.assertTrue(receipt["debug_only"])
        self.assertEqual(len(reg["assets"]), 6)
        self.assertEqual(len(receipt["items"]), 6)
        for item in reg["assets"]:
            raw = (ROOT / item["path"]).read_bytes()
            self.assertEqual(git_oid(raw), item["source_git_blob_sha1"])
            self.assertIn("<svg", raw.decode("utf-8"))
            export = item["export_filename"]
            row = receipt["items"][export]
            png = (OUTPUT / export).read_bytes()
            self.assertEqual(hashlib.sha256(raw).hexdigest(), row["source_sha256"])
            self.assertEqual(hashlib.sha256(png).hexdigest(), row["export_sha256"])
            self.assertEqual((item["export_width"], item["export_height"]),
                             struct.unpack(">II", png[16:24]))
    def test_debug_only_integration_does_not_replace_puzzle_ui(self):
        src = ROOT / "app/src/main/java/com/playereden/gridmates/wordsearch"
        shelf = (src / "Ws16GShelfArtwork.kt").read_text()
        paper = (src / "GridMatesParchmentBackground.kt").read_text()
        pager = (src / "Ws16BookCoverPager.kt").read_text()
        self.assertIn('context.assets.open("ws16aa/" + key + ".png")', shelf)
        self.assertIn('ws16aa/parchment.png', paper)
        self.assertIn('Ws16AaShelf(', shelf)
        self.assertIn('Ws16ECanvasShelfFallback()', shelf)
        self.assertIn('HorizontalPager(', pager)
        self.assertNotIn('art.getValue("contact-shadow")', shelf)
if __name__ == "__main__":
    unittest.main()
