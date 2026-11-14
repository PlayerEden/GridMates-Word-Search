#!/usr/bin/env python3
"""WS16BK — reproducible, QA-only gallery of wordless book bindings + pager rail.

The existing pinned WS16BI artwork pipeline is the source of truth. This pass
does not change its old PNGs, and introduces no text, fake catalog data or
production-approved assets. Every image is generated deterministically.
"""
from __future__ import annotations
import hashlib
import json
import math
import random
from pathlib import Path
from PIL import Image, ImageDraw, ImageFilter, ImageEnhance
from build_ws16bi_library_art import ROOT, blank, WOOD, WOOD_SHA

DEST = ROOT / "app/src/debug/assets/ws16bk"
PRIOR = ROOT / "app/src/debug/assets/ws16bi/manifest.json"
VARIANTS = {
    "fern": (22, 57, 43),
    "dusk": (31, 42, 76),
    "clay": (86, 40, 38),
    "garnet": (74, 29, 45),
    "ochre": (90, 59, 27),
    "teal": (22, 66, 66),
    "violet": (48, 35, 72),
    "slate": (51, 60, 70),
}

def sha(path: Path) -> str:
    return hashlib.sha256(path.read_bytes()).hexdigest()

def write_png(im: Image.Image, path: Path):
    path.parent.mkdir(parents=True, exist_ok=True)
    im.save(path, format="PNG", optimize=True, compress_level=9)

def decorative_binding(name: str, rgb: tuple[int,int,int]) -> Image.Image:
    im = blank(name, rgb)
    # The five new colors get distinct emblems, all geometric/foliage motifs
    # with NO glyphs. Reuse the real leather silhouette, never wide backing.
    if name in ("fern", "dusk", "clay"):
        return im
    d = ImageDraw.Draw(im, "RGBA")
    gold = (225, 180, 98, 176)
    cx, cy = 52, 272
    if name == "garnet":
        for scale in (20, 12, 5):
            d.polygon((cx,cy-scale,cx+scale*.7,cy,cx,cy+scale,cx-scale*.7,cy),
                      outline=gold)
    elif name == "ochre":
        for r in (12, 21):
            d.arc((cx-r,cy-r,cx+r,cy+r),30,330,fill=gold,width=3)
        for v in (-1,1):
            d.line((cx+v*4,cy-12,cx+v*11,cy+13),fill=gold,width=2)
    elif name == "teal":
        for angle in range(0,360,45):
            rad=angle*math.pi/180
            x=cx+17*math.cos(rad);y=cy+17*math.sin(rad)
            d.ellipse((x-4,y-4,x+4,y+4),fill=(201,152,77,154))
    elif name == "violet":
        for s in (-1,1):
            d.arc((cx-19+s*4,cy-22,cx+19+s*4,cy+22),
                  50 if s>0 else 130,300 if s>0 else 230,
                  fill=gold,width=3)
        d.ellipse((cx-4,cy-4,cx+4,cy+4),fill=gold)
    else: # slate
        d.polygon((cx-16,cy+11,cx,cy-18,cx+16,cy+11),
                  outline=gold,width=3)
        d.line((cx-10,cy+18,cx+10,cy+18),fill=gold,width=2)
    # Clip ornament again to rounded binding body; never introduce alpha dust.
    mask=Image.new("L",(104,542),0)
    ImageDraw.Draw(mask).rounded_rectangle((3,2,100,539),radius=12,fill=255)
    from PIL import ImageChops
    im.putalpha(ImageChops.multiply(im.getchannel("A"),mask))
    return im

def pager_rail() -> Image.Image:
    assert sha(WOOD)==WOOD_SHA, "cabinet texture source changed"
    w,h=760,166
    source=Image.open(WOOD).convert("RGB")
    texture=source.crop((53,230,560,620)).resize((w-18,h-28),Image.Resampling.LANCZOS)
    texture=ImageEnhance.Color(texture).enhance(.78)
    texture=ImageEnhance.Brightness(texture).enhance(.60)
    im=Image.new("RGBA",(w,h),(0,0,0,0))
    shadow=Image.new("RGBA",(w,h),(0,0,0,0))
    sd=ImageDraw.Draw(shadow)
    sd.rounded_rectangle((14,24,w-14,h-9),radius=22,fill=(15,7,3,145))
    im.alpha_composite(shadow.filter(ImageFilter.GaussianBlur(9)))
    layer=Image.new("RGBA",(w,h),(0,0,0,0))
    layer.paste(texture,(9,8))
    mask=Image.new("L",(w,h),0)
    ImageDraw.Draw(mask).rounded_rectangle((9,8,w-9,h-20),radius=19,fill=255)
    layer.putalpha(mask)
    im.alpha_composite(layer)
    d=ImageDraw.Draw(im,"RGBA")
    # Beveled walnut plate with inset aged-brass lining. Arrows/dots are
    # native Compose and sit ON this image; no text, icons or labels baked in.
    d.rounded_rectangle((12,10,w-12,h-22),radius=18,
                        outline=(232,177,86,230),width=5)
    d.rounded_rectangle((23,19,w-23,h-34),radius=14,
                        outline=(63,28,13,213),width=4)
    d.line((38,21,w-38,21),fill=(250,195,111,146),width=3)
    d.line((38,h-35,w-38,h-35),fill=(36,18,10,192),width=3)
    for x in (37,w-37):
        for y in (45,h-58):
            d.ellipse((x-7,y-7,x+7,y+7),fill=(54,23,12,224),
                      outline=(236,184,101,197),width=3)
    alpha=im.getchannel("A")
    edge=ImageDraw.Draw(alpha)
    edge.rectangle((0,0,w-1,2),fill=0)
    edge.rectangle((0,h-3,w-1,h-1),fill=0)
    edge.rectangle((0,0,2,h-1),fill=0)
    edge.rectangle((w-3,0,w-1,h-1),fill=0)
    im.putalpha(alpha)
    return im

def generate():
    prior=json.loads(PRIOR.read_text())
    assert prior["qa_only"] and not prior["release_approved"]
    assert set(x["name"] for x in prior["assets"]) == {"shelf","fern","dusk","clay"}
    records=[]
    for name,rgb in VARIANTS.items():
        path=DEST/"blanks"/(name+".png")
        write_png(decorative_binding(name,rgb),path)
        records.append({"name":name,"kind":"unlettered-spine",
            "path":f"ws16bk/blanks/{name}.png","dimensions":[104,542],
            "sha256":sha(path)})
    rail=DEST/"pager_rail.png"
    write_png(pager_rail(),rail)
    records.append({"name":"pager_rail","kind":"cabinet-control-asset",
                    "path":"ws16bk/pager_rail.png",
                    "dimensions":[760,166],"sha256":sha(rail)})
    manifest={"schema":1,"qa_only":True,"release_approved":False,
       "owner_approved":False,"no_words_on_blanks":True,
       "decorations_do_not_represent_owned_books":True,
       "source_backboard_sha256":WOOD_SHA,"assets":records}
    DEST.mkdir(parents=True,exist_ok=True)
    (DEST/"manifest.json").write_text(json.dumps(manifest,indent=2,sort_keys=True)+"\n")
    print("WS16BK: generated 8 wordless bindings + 1 walnut/brass pager rail")

if __name__=="__main__":
    generate()
