"""WS16AD-B: short readable names, controlled proportional heights, no ®."""
import json
from pathlib import Path
import unittest
from book_spine_policy import spine_name_profile

ROOT = Path(__file__).resolve().parents[2]


class SpineNamingPolicyTests(unittest.TestCase):
    def test_existing_names_have_correct_unmodified_identity(self):
        expected = {"Woodland": "tall", "Space": "standard", "Garden": "standard"}
        for name, size_class in expected.items():
            data = spine_name_profile(name)
            self.assertEqual(name, data["display_name"])
            self.assertEqual(size_class, data["height_class"])
            self.assertEqual("spine-128x640-v1", data["master_geometry"])
            self.assertFalse(data["editorial_review_required"])

    def test_new_names_have_guidance_not_silent_abbreviation(self):
        self.assertEqual("standard", spine_name_profile("Ocean")["height_class"])
        self.assertEqual("standard", spine_name_profile("Winter")["height_class"])
        self.assertEqual("tall", spine_name_profile("Halloween")["height_class"])
        self.assertEqual("tall", spine_name_profile("Underwater")["height_class"])
        x = spine_name_profile("Northern Lights")
        self.assertEqual("Northern Lights", x["display_name"])
        self.assertEqual("editorial-review", x["height_class"])
        self.assertIsNone(x["library_display_height_dp"])
        self.assertTrue(x["editorial_review_required"])

    def test_whitespace_marks_and_empty_input(self):
        self.assertEqual("Deep Forest", spine_name_profile(" Deep   Forest ")["display_name"])
        for name in ("", "   ", "GridMates®", "Space™", "®"):
            with self.subTest(name=name), self.assertRaises(ValueError):
                spine_name_profile(name)

    def test_factory_manifest_carries_name_profiles_without_changing_geometry(self):
        manifest = ROOT / "app/src/debug/assets/ws16ad/manifest.json"
        data = json.loads(manifest.read_text(encoding="utf-8"))
        self.assertEqual("spine-128x640-v1", data["spine_naming_policy"]["master_geometry"])
        spines = [entry for entry in data["assets"] if entry["role"] == "geometry-proof"
                  and entry["key"].endswith("/spine")]
        self.assertEqual(3, len(spines))
        for entry in spines:
            theme = entry["key"].split("/")[0]
            expected = spine_name_profile(theme.capitalize())
            self.assertEqual(expected, entry["spine_name_profile"])
            self.assertEqual([128, 640], entry["dimensions"])
        placeholders = [entry for entry in data["assets"]
                        if entry["role"] == "non-playable-placeholder"]
        self.assertEqual(6, len(placeholders))
        self.assertTrue(all("spine_name_profile" not in x for x in placeholders))


if __name__ == "__main__":
    unittest.main()
