"""WS-14K2 verification of individual source-backed transparent gameplay motifs."""
import json
import struct
import unittest
from pathlib import Path
from hashlib import sha256
from xml.etree import ElementTree

ROOT = Path(__file__).resolve().parents[2]
BASE = ROOT / "app/src/debug/assets/ws14k2/motifs"


class WS14K2GameplayMotifsTest(unittest.TestCase):
    def test_all_three_sources_and_exports_are_present_and_distinct(self):
        manifest = json.loads((BASE / "manifest.json").read_text())
        self.assertTrue(manifest["debug_only"])
        self.assertFalse(manifest["release_approved"])
        assets = manifest["assets"]
        self.assertEqual(set(assets), {"woodland", "space", "garden"})
        hashes = []
        for theme, entry in assets.items():
            raw = (ROOT / entry["source_path"]).read_bytes()
            image = (ROOT / entry["export_path"]).read_bytes()
            self.assertEqual(sha256(raw).hexdigest(), entry["source_sha256"])
            self.assertEqual(sha256(image).hexdigest(), entry["export_sha256"])
            self.assertEqual(entry["bytes"], len(image))
            self.assertEqual(entry["approval"], "QA_INTEGRATED_UNREVIEWED")
            self.assertEqual(struct.unpack(">IIBB", image[16:26]), (256, 256, 8, 6))
            self.assertEqual(ElementTree.fromstring(raw).attrib["viewBox"], "0 0 256 256")
            hashes.append(entry["export_sha256"])
        self.assertEqual(len(set(hashes)), 3)


if __name__ == "__main__":
    unittest.main()
