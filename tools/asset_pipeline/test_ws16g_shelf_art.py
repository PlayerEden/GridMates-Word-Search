#!/usr/bin/env python3
import unittest, json, hashlib
from pathlib import Path

ROOT = Path(__file__).resolve().parents[2]
DEST = ROOT / "app/src/debug/assets/ws16g/shelf"
class Ws16GShelfTest(unittest.TestCase):
    def test_home_shadow_is_confined_to_the_wood(self):
        runtime = (ROOT / "app/src/main/java/com/playereden/gridmates/wordsearch"
            / "Ws16GShelfArtwork.kt").read_text()
        patina = (ROOT / "app/src/main/java/com/playereden/gridmates/wordsearch"
            / "Ws16TShelfPatina.kt").read_text()
        fallback = (ROOT / "app/src/main/java/com/playereden/gridmates/wordsearch"
            / "Ws16EStorybookShelf.kt").read_text()

        # Keep legacy art in the audit registry but never DRAW its diffuse
        # screen-wide shadow. Regression check for owner's real-device halo.
        self.assertNotIn('art.getValue("contact-shadow")', runtime)
        self.assertIn("ShelfBookContact(", runtime)
        self.assertNotIn("Brush.horizontalGradient", patina)
        self.assertNotIn("Color(0x29492D1B)", patina)
        self.assertNotIn("w*.80f, 7*u", fallback)

    def test_eight_pinned_debug_exports(self):
        manifest=json.loads((DEST/"manifest.json").read_text())
        self.assertTrue(manifest["debug_only"])
        self.assertFalse(manifest["release_approved"])
        self.assertEqual(8,len(manifest["items"]))
        for name,detail in manifest["items"].items():
            data=(DEST/name).read_bytes()
            self.assertEqual(detail["export_sha256"],hashlib.sha256(data).hexdigest())
            self.assertTrue(data.startswith(b"\x89PNG\r\n\x1a\n"))
