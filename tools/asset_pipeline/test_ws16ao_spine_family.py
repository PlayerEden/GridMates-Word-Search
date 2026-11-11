"""WS16AO 12-image physical publisher family QA and live UI constraints."""
import json,unittest
from PIL import Image
from build_ws16ao_spine_family import ROOT,DEST,SOURCE,NAMES,REAL,BLANK,sha

class PublisherSpines(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.manifest=json.loads((DEST/"manifest.json").read_text())

    def test_both_profiles_every_real_and_blank_volume(self):
        m=self.manifest
        self.assertTrue(m["qa_only"])
        self.assertFalse(m["release_approved"])
        self.assertFalse(m["owner_approved"])
        self.assertEqual(12,len(m["assets"]))
        self.assertEqual(set(NAMES),{a["name"] for a in m["assets"]})
        for art in m["assets"]:
            with self.subTest(book=art["name"],profile=art["profile"]):
                src=ROOT/art["source"];out=ROOT/art["output"]
                self.assertEqual(sha(src.read_bytes()),art["source_sha256"])
                self.assertEqual(sha(out.read_bytes()),art["sha256"])
                with Image.open(out) as im:
                    self.assertEqual("RGBA",im.mode)
                    self.assertEqual((166,662) if art["profile"]=="tall" else (166,542),im.size)
                    self.assertLessEqual(im.getchannel("A").getbbox()[2],161)
                    self.assertGreaterEqual(im.getchannel("A").getbbox()[0],5)
                self.assertEqual(art["name"] in REAL,art["playable"])
                self.assertNotIn("cover",out.name)

    def test_original_letter_and_theme_pixels_never_stretched(self):
        for name in REAL:
            with self.subTest(name=name):
                im=Image.open(SOURCE/(name+".png")).convert("RGBA")
                out=Image.open(DEST/"spines/standard"/(name+".png")).convert("RGBA")
                for y in (105,130,162,184,273,318,355,420,451):
                    for x in range(19,84):
                        if im.getpixel((x,y))[3]>250:
                            self.assertEqual(im.getpixel((x,y))[:3],
                                out.getpixel((x+31,y))[:3],(name,x,y))

    def test_tall_inserts_only_unprinted_leather(self):
        for name in NAMES:
            with self.subTest(name=name):
                standard=Image.open(DEST/"spines/standard"/(name+".png")).convert("RGBA")
                tall=Image.open(DEST/"spines/tall"/(name+".png")).convert("RGBA")
                self.assertEqual(standard.crop((0,0,166,216)).tobytes(),
                                 tall.crop((0,0,166,216)).tobytes())
                self.assertEqual(standard.crop((0,216,166,542)).tobytes(),
                                 tall.crop((0,336,166,662)).tobytes())

    def test_no_opaque_matte_or_fake_progress(self):
        for art in self.manifest["assets"]:
            im=Image.open(ROOT/art["output"]).convert("RGBA")
            leaks=sum(1 for r,g,b,a in im.getdata()
                        if a==0 and max(r,g,b)>245)
            self.assertLess(leaks,25,art["name"])
        source=(ROOT/"app/src/main/java/com/playereden/gridmates/wordsearch"
                /"BookcaseWoodArtwork.kt").read_text()
        scene=(ROOT/"app/src/main/java/com/playereden/gridmates/wordsearch"
                /"WS16AMInteractiveBookcaseScene.kt").read_text()
        self.assertIn('"ws16ao/spines/$profile/$name.png"',source)
        self.assertIn("ContentScale.Fit",source)
        self.assertIn("profile = profile",scene)
        self.assertIn('val bindingRatio = if (profile == "tall")',scene)
        self.assertIn('val artWidth = minOf(slot - 2.dp',scene)
        self.assertIn("onBookSelected(book.id)",scene)
        self.assertNotIn("onBookSelected(style)",scene)
        self.assertIn('val hitSlot = if (fontScale >= 1.65f) 54.dp else 48.dp',scene)

if __name__=="__main__":unittest.main()
