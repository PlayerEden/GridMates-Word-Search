#!/usr/bin/env python3
"""WS16AF: source-pinned no-registered-symbol spine art, all existing themes.

Use six source/master checks from previous factory, but fix *only* the detached
superscript after GridMates in each original physical spine. Keep the original
books and prior WS16AE covers, IDs and saves exactly as-is. Debug QA only.
"""
from __future__ import annotations
import io, json
from statistics import median
from PIL import Image
from build_ws16ad_book_factory import ROOT, normalize, alpha_bbox, digest, save_png
from build_ws16ae_books_v2 import SOURCE_SHA256, decontaminate_exterior_rgb

THEMES = ("woodland", "space", "garden")
SRC = ROOT / "app/src/debug/assets/ws14/spines"
OUT = ROOT / "app/src/debug/assets/ws16af/spines"

# Exact original 85-91px master-image coordinates, no broad rectangle or blur.
# x inclusive; the actual adjacent 's' letterform is intentionally excluded.
MARK_PIXEL_ROWS = {
    "woodland": {97:(75,77), 98:(75,77), 99:(75,77), 100:(75,77), 101:(76,76)},
    "space": {96:(77,78), 97:(76,79), 98:(76,79), 99:(76,79),
              100:(76,79), 101:(76,79)},
    "garden": {96:(75,77), 97:(75,77), 98:(75,77), 99:(75,77),
               100:(76,77), 101:(76,76)},
}

def clean_spine_symbol(source:Image.Image, theme:str)->tuple[Image.Image,int]:
    assert theme in THEMES, theme
    image=source.convert("RGBA")
    result=image.copy()
    changed=0
    for y,(x0,x1) in MARK_PIXEL_ROWS[theme].items():
        for x in range(x0,x1+1):
            # Sample genuine unlettered leather texture just above the mark.
            pixels=[image.getpixel((sx,sy))[:3] for sx,sy in (
                (x,y-12), (x-1,y-13), (x,y-10), (x+1,y-12), (x-2,y-10))]
            rgb=tuple(int(median(p[c] for p in pixels)) for c in range(3))
            result.putpixel((x,y), (*rgb,image.getpixel((x,y))[3]))
            changed+=1
    return result,changed

def generate()->dict:
    OUT.mkdir(parents=True,exist_ok=True)
    records=[]
    for theme in THEMES:
        source_path=SRC/(theme+".png")
        raw=source_path.read_bytes()
        assert digest(raw)==SOURCE_SHA256[theme]["spine"],("Unexpected unpinned original spine",theme)
        source=Image.open(io.BytesIO(raw)).convert("RGBA")
        retouched,changed=clean_spine_symbol(source,theme)
        assert source.getchannel("A").tobytes()==retouched.getchannel("A").tobytes()
        normalized,source_bounds,_=normalize(retouched,"spine",None)
        finished=decontaminate_exterior_rgb(normalized)
        output=OUT/(theme+".png")
        data=save_png(output,finished)
        assert list(finished.size)==[128,640]
        records.append({
            "theme":theme, "role":"spine", "source":str(source_path.relative_to(ROOT)),
            "source_sha256":digest(raw), "corrected_source_pixels":changed,
            "output":str(output.relative_to(ROOT)), "output_sha256":digest(data),
            "dimensions":list(finished.size), "bounds":list(alpha_bbox(finished)),
            "source_bounds":list(source_bounds)
        })
    manifest={
       "schema_version":1, "name":"GridMates Word Search WS16AF V3 corrected physical spines",
       "status":"QA_PENDING_OWNER_VISUAL_REVIEW","qa_only":True,"release_approved":False,
       "cover_fallback":"ws16ae/covers/", "assets":records
    }
    (OUT.parent/"manifest.json").write_text(json.dumps(manifest,indent=2,sort_keys=True)+"\n")
    return manifest

if __name__=="__main__":
    print("WS16AF corrected spines:",generate()["assets"])
