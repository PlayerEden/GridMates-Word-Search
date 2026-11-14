import hashlib,json,struct,unittest
from pathlib import Path
ROOT=Path(__file__).resolve().parents[2]
DEST=ROOT/"app/src/debug/assets/ws16i/decor"
class WS16IHomeDecorTest(unittest.TestCase):
    def test_five_real_transparent_assets(self):
        record=json.loads((DEST/"manifest.json").read_text())
        self.assertTrue(record["debug_only"])
        self.assertFalse(record["release_approved"])
        self.assertEqual(len(record["sprites"]),5)
        for name,meta in record["sprites"].items():
            raw=(ROOT/meta["source"]).read_bytes()
            png=(DEST/(name+".png")).read_bytes()
            self.assertEqual(hashlib.sha256(raw).hexdigest(),meta["source_sha256"])
            self.assertEqual(hashlib.sha256(png).hexdigest(),meta["export_sha256"])
            self.assertEqual(struct.unpack(">IIBB",png[16:26]),
                (meta["width"],meta["height"],8,6))
