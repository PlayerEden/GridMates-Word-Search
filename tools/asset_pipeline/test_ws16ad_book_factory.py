"""WS16AD: test actual reproducible geometry & placeholder image bytes and isolation."""
import hashlib
import json
from pathlib import Path
import unittest
from PIL import Image

ROOT=Path(__file__).resolve().parents[2]
OUT=ROOT/'app/src/debug/assets/ws16ad'

class BookFactoryTests(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.m=json.loads((OUT/'manifest.json').read_text(encoding='utf-8'))
    def test_all_12_real_assets_are_pinned(self):
        m=self.m
        self.assertEqual(12,len(m['assets']))
        self.assertTrue(m['qa_only'])
        self.assertFalse(m['owner_approved'])
        self.assertFalse(m['release_approved'])
        self.assertIn('unregistered ®',m['open_gates'][0])
        for item in m['assets']:
            src=(ROOT/item['input']).read_bytes()
            export=(ROOT/item['output']).read_bytes()
            self.assertEqual(item['input_sha256'],hashlib.sha256(src).hexdigest())
            self.assertEqual(item['sha256'],hashlib.sha256(export).hexdigest())
            with Image.open(ROOT/item['output']) as im:
                self.assertEqual(im.mode,'RGBA')
                self.assertEqual(tuple(item['dimensions']),im.size)
                self.assertEqual(tuple(item['output_bbox']),
                    im.getchannel('A').point(lambda v:255 if v>=40 else 0).getbbox())
    def test_geometry_is_shared_to_two_pixels(self):
        for kind in ('cover','spine'):
            target=self.m['geometry'][kind]
            bounds=target['visual_bbox']
            for item in self.m['assets']:
                if not item['key'].endswith('/'+kind):continue
                self.assertEqual(item['dimensions'],target['canvas'])
                self.assertTrue(all(abs(actual-ref)<=2 for actual,ref in zip(item['output_bbox'],bounds)),item)
    def test_coming_soon_has_no_live_catalog_record_or_prohibited_mark(self):
        entries=[i for i in self.m['assets'] if i['role']=='non-playable-placeholder']
        self.assertEqual(6,len(entries))
        for x in entries:
            self.assertFalse(x['playable'])
            self.assertFalse(x['has_catalog_id'])
            src=(ROOT/x['input']).read_text()
            self.assertNotIn('®',src)
            self.assertNotIn('TM',src)
            self.assertNotIn('<text',src)
    def test_only_code_loads_geometry_qa_art(self):
        art=(ROOT/'app/src/main/java/com/playereden/gridmates/wordsearch/BookArt.kt').read_text()
        self.assertIn('BookArtKind.COVER -> "covers"',art)
        self.assertIn('BookArtKind.SPINE -> "spines"',art)
        self.assertIn('return "ws16ad/$folder/$slug.png"',art)
        self.assertIn('preferredBookFactory',art)
        self.assertIn('preferredNewPath',art)
        # WS16AG explicitly allows an *anonymous* debug-only library preview,
        # but no placeholder may become a catalog book or a tappable selection.
        self.assertIn('ws16ad/placeholders/',art)
        self.assertIn('if (style !in listOf("linen", "night", "burgundy")) return',art)
        lib=(ROOT/'app/src/main/java/com/playereden/gridmates/wordsearch/BookcaseBookshelfScreen.kt').read_text()
        self.assertIn('HorizontalPager(',lib)
        self.assertIn('BookcaseFutureSpineArt',lib)
        self.assertIn('for (slot in 0 until slotsPerRow)',lib)
        self.assertNotIn('onSelect(style',lib)
if __name__=='__main__':unittest.main()
