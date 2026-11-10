#!/usr/bin/env python3
"""Source and transparent PNG integrity; does not imply on-device visual approval."""
import unittest,json,hashlib,struct
from pathlib import Path
ROOT=Path(__file__).resolve().parents[2]
OUT=ROOT/"app/src/debug/assets/ws16h/actions"
class WS16HHomeArtTests(unittest.TestCase):
    def test_six_real_source_matched_icons(self):
        m=json.loads((OUT/"manifest.json").read_text())
        self.assertTrue(m["debug_only"])
        self.assertFalse(m["release_approved"])
        self.assertEqual(len(m["sprites"]),6)
        for name,row in m["sprites"].items():
            source=(ROOT/row["source"]).read_bytes()
            png=(ROOT/row["debug_asset"]).read_bytes()
            self.assertEqual(hashlib.sha256(source).hexdigest(),row["source_sha256"])
            self.assertEqual(hashlib.sha256(png).hexdigest(),row["export_sha256"])
            self.assertTrue(png.startswith(b"\x89PNG\r\n\x1a\n"))
            self.assertEqual(struct.unpack(">IIBB",png[16:26]),(256,256,8,6))
            self.assertGreater(len(png),2000)
if __name__=="__main__":
    unittest.main()
