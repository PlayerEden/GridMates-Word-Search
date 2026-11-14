#!/usr/bin/env python3
"""QA source-level guard: original 3D illustration is actually used, with rollback."""
import unittest
from pathlib import Path
ROOT=Path(__file__).resolve().parents[2]
SRC=ROOT/"app/src/main/java/com/playereden/gridmates/wordsearch"
class Ws16AbTest(unittest.TestCase):
 def test_dedicated_art_is_loaded_before_older_fallbacks(self):
    from subprocess import run
    import sys
    run([sys.executable,str(ROOT/"tools/asset_pipeline/verify_ws16ab_apk.py")],check=True)
    shelf=(SRC/"Ws16GShelfArtwork.kt").read_text()
    first=shelf.index('val ws16ab = remember(context)')
    old=shelf.index('val ws16aa = remember(context)')
    self.assertLess(first,old)
    self.assertIn('readWs16AbShelf(context)',shelf)
    self.assertIn('Ws16AbShelf(ws16ab, modifier)',shelf)
    self.assertIn('painted-shelf-v1.webp',shelf)
    self.assertIn('Ws16AaShelf(ws16aa, modifier)',shelf)
    self.assertIn('Ws16ECanvasShelfFallback()',shelf)
    self.assertIn('50.dp',shelf)
    self.assertIn('offset(y = 8.dp)',shelf)
 def test_background_is_still_optional_and_carousel_still_moving(self):
    parchment=(SRC/"GridMatesParchmentBackground.kt").read_text()
    carousel=(SRC/"Ws16BookCoverPager.kt").read_text()
    self.assertIn('alpha = .86f',parchment)
    self.assertIn('paperTooth?.let',parchment)
    self.assertIn('HorizontalPager(',carousel)
    self.assertIn('pageSize = PageSize.Fixed(coverWidth)',carousel)
    self.assertIn('onCoverTap(page, focused)',carousel)
if __name__=="__main__": unittest.main()
