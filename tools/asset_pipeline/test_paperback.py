"""WS-12C reproducibility and existing Compose-theme consistency tests."""
import copy
import hashlib
import json
from pathlib import Path
import tempfile
import unittest
import xml.etree.ElementTree as ET

from render_paperback import render, validate, cover_svg, emblem_svg, PALETTE

ROOT=Path(__file__).resolve().parents[2]
TOKENS=ROOT/"design/asset-pipeline/paperback-v1-tokens.json"


class DeterministicPaperbackTests(unittest.TestCase):
    def setUp(self):
        self.source=json.loads(TOKENS.read_text(encoding="utf8"))
        self.temp=tempfile.TemporaryDirectory()
        self.root=Path(self.temp.name)

    def tearDown(self):
        self.temp.cleanup()

    def write_recipe(self, data):
        file=self.root/"tokens.json"
        file.write_text(json.dumps(data),encoding="utf8")
        return file

    def test_palette_is_parity_matched_to_current_compose_theme(self):
        self.assertEqual(PALETTE["cream"],"#F4F3EB")
        self.assertEqual(PALETTE["paper"],"#FFFDF8")
        self.assertEqual(PALETTE["forest"],"#214F3F")
        self.assertEqual(PALETTE["ink"],"#263D36")
        self.assertEqual(PALETTE["gold"],"#8A6729")
        self.assertEqual(validate(self.source)["brand"],PALETTE)

    def test_render_is_identical_on_two_clean_rebuilds(self):
        a=render(TOKENS,self.root/"a")
        b=render(TOKENS,self.root/"b")
        self.assertEqual(a,b)
        self.assertEqual(a["theme_count"],3)
        self.assertEqual(len(a["files"]),7)
        self.assertFalse(a["adobe_refined"])
        self.assertFalse(a["owner_approved"])
        self.assertFalse(a["production_app_assets_modified"])
        self.assertEqual(a["baked_text_images"],0)

    def test_svg_art_is_scalable_and_has_no_titles(self):
        for theme in self.source["themes"]:
            for text in (cover_svg(theme),emblem_svg(theme)):
                root=ET.fromstring(text)
                self.assertEqual(root.tag,"{http://www.w3.org/2000/svg}svg")
                self.assertEqual(len(list(root.iter("{http://www.w3.org/2000/svg}text"))),0)
                self.assertNotIn("WOODLAND",text)
                self.assertNotIn("GridMates",text)
                self.assertNotIn("®",text)
                self.assertNotIn("™",text)
                self.assertIn('viewBox=',text)
            self.assertIn('width="1200" height="1600"',cover_svg(theme))

    def test_semantic_preview_uses_editable_real_text(self):
        render(TOKENS,self.root/"preview")
        html=(self.root/"preview/index.html").read_text()
        self.assertIn("<h2>Woodland</h2>",html)
        self.assertIn("<h2>Space</h2>",html)
        self.assertIn("<h2>Garden</h2>",html)
        self.assertIn('aria-hidden="true"',html)
        self.assertIn("Not approved",html)
        self.assertIn("Georgia",html)
        self.assertNotIn("®,",html)

    def test_narrow_phone_preview_is_one_column_and_volume_clears_rule(self):
        render(TOKENS,self.root/"small")
        html=(self.root/"small/index.html").read_text()
        self.assertIn("@media(max-width:420px)",html)
        self.assertIn(".shelf{grid-template-columns:1fr",html)
        self.assertIn(".volume{position:absolute;left:15%;right:7%;bottom:12%",html)

    def test_adding_a_new_theme_does_not_modify_older_cover_bytes(self):
        original=render(TOKENS,self.root/"base")
        altered=copy.deepcopy(self.source)
        altered["themes"].append({
            "id":"ocean","label":"Ocean","volume":"I","motif":"orbit",
            "accent":"#365D71","accent_light":"#E0EDF3",
            "accent_dark":"#233E51","highlight":"#9ABCCD","mark":"shell"})
        extended=render(self.write_recipe(altered),self.root/"extended")
        for path in original["files"]:
            if path!="index.html":
                self.assertEqual(original["files"][path],extended["files"][path])
        self.assertIn("books/ocean.svg",extended["files"])
        self.assertEqual(extended["theme_count"],4)

    def test_invalid_palette_does_not_pass_as_original_app(self):
        altered=copy.deepcopy(self.source)
        altered["brand"]["forest"]="#000000"
        with self.assertRaisesRegex(ValueError,"palette"):
            validate(altered)

    def test_new_motif_requires_explicit_review_not_random_generation(self):
        altered=copy.deepcopy(self.source)
        altered["themes"][1]["motif"]="invent-a-new-planet-with-ai"
        with self.assertRaisesRegex(ValueError,"Unknown drawing motif"):
            validate(altered)

    def test_illegal_or_proprietary_book_titles_rejected(self):
        altered=copy.deepcopy(self.source)
        altered["themes"][0]["label"]="Woodland®"
        with self.assertRaisesRegex(ValueError,"legal-claim"):
            validate(altered)

    def test_no_runtime_generated_book_title_or_game_progress(self):
        altered=copy.deepcopy(self.source)
        altered["cover"]["text_is_native_ui"]=False
        with self.assertRaisesRegex(ValueError,"native text"):
            validate(altered)
        altered=copy.deepcopy(self.source)
        altered["style_rules"]["no_text_in_decorative_art"]=False
        with self.assertRaisesRegex(ValueError,"principles"):
            validate(altered)


if __name__=="__main__":
    unittest.main()
