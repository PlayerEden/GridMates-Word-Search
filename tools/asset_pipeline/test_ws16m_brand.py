"""WS16M original wordmark-emblem source and alpha/raster SHA tests."""
import hashlib
import json
import struct
import unittest
from pathlib import Path
ROOT=Path(__file__).resolve().parents[2]
OUT=ROOT/"app/src/debug/assets/ws16m/brand"

class WS16MBrandArtTest(unittest.TestCase):
    def test_independent_emblems_match_sources(self):
        data=json.loads((OUT/"manifest.json").read_text())
        self.assertEqual(set(data["sprites"]),{"gridmates","wordsearch"})
        self.assertTrue(data["debug_only"])
        self.assertFalse(data["release_approved"])
        for name,meta in data["sprites"].items():
            src=(ROOT/meta["master"]).read_bytes()
            png=(OUT/(name+".png")).read_bytes()
            self.assertEqual(hashlib.sha256(src).hexdigest(),meta["source_sha256"])
            self.assertEqual(hashlib.sha256(png).hexdigest(),meta["export_sha256"])
            self.assertEqual(struct.unpack(">IIBB",png[16:26]),(256,256,8,6))
            self.assertGreater(len(png),2000)
if __name__ == "__main__":
    unittest.main()
