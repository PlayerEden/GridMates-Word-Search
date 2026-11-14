#!/usr/bin/env python3
"""WS16BI: deterministic, QA-only mounted walnut shelf and unlettered blank bindings.

All painted pixels are generated from existing repo-pinned GridMates sources.
Do not overwrite any WS16BB/WS16BF artwork. No new words or book titles are
baked into the blank books, and the generated set is never release-approved.
"""
from __future__ import annotations
import hashlib
import io
import json
import math
import random
from pathlib import Path
from PIL import Image, ImageDraw, ImageFilter, ImageEnhance, ImageOps

ROOT = Path(__file__).resolve().parents[2]
DEST = ROOT / "app/src/debug/assets/ws16bi"
WOOD = ROOT / "app/src/debug/assets/ws16ba/shell/recessed_backboard.png"
OLD = ROOT / "app/src/debug/assets/ws16bb/shelf.png"
WOOD_SHA = "719b9b2673986d961902977e8178e99ebf92d2e1eb57f1796464b008f5071cd5"
OLD_SHA = "691496fae9340fa4131d235abd11589cd2e9155384773d0887848bfde11c4c1e"
GOLD = (223, 166, 76, 235)
W, H = 1172, 250
BW, BH = 104, 542
VARIANTS = {
    "fern": (22, 57, 43),
    "dusk": (31, 42, 76),
    "clay": (86, 40, 38),
}

def sha(path: Path) -> str:
    return hashlib.sha256(path.read_bytes()).hexdigest()

def png_write(im: Image.Image, path: Path) -> None:
    path.parent.mkdir(parents=True, exist_ok=True)
    im.save(path, format="PNG", optimize=True, compress_level=9)

def mounted_shelf() -> Image.Image:
    source = Image.open(WOOD).convert("RGB")
    old = Image.open(OLD).convert("RGBA")
    assert source.size == (606, 1777) and old.size == (586, 125)
    im = Image.new("RGBA", (W, H), (0, 0, 0, 0))

    shadow = Image.new("RGBA", (W, H))
    sd = ImageDraw.Draw(shadow)
    sd.rounded_rectangle((18, 35, W-18, 116), radius=20, fill=(25, 8, 4, 163))
    sd.ellipse((88, 92, 226, 232), fill=(20, 7, 2, 122))
    sd.ellipse((W-226, 92, W-88, 232), fill=(20, 7, 2, 122))
    im.alpha_composite(shadow.filter(ImageFilter.GaussianBlur(19)))

    # A real, textured walnut plank, not a single painted horizontal line.
    plank_src = source.crop((40, 170, 590, 750)).rotate(90, expand=True)
    grain = plank_src.resize((W-30, 125), Image.Resampling.LANCZOS)
    grain = ImageEnhance.Color(grain).enhance(0.78)
    grain = ImageEnhance.Brightness(grain).enhance(0.62)
    walnut = Image.new("RGBA", (W, H))
    walnut.paste(grain.convert("RGBA"), (15, 12))
    mask = Image.new("L", (W,H))
    m = ImageDraw.Draw(mask)
    m.rounded_rectangle((17, 12, W-17, 115), radius=14, fill=255)
    walnut.putalpha(mask)
    im.alpha_composite(walnut)
    d = ImageDraw.Draw(im, "RGBA")
    # Deep upper platform with an obvious contact/landing surface.
    d.polygon([(38, 10), (W-38, 10), (W-14, 34), (14, 34)],
              fill=(143, 65, 24, 245))
    d.line((42, 11, W-42, 11), fill=(247, 161, 65, 238), width=6)
    d.line((19, 34, W-19, 34), fill=(54, 19, 9, 240), width=8)
    d.line((24, 41, W-24, 41), fill=(205, 100, 36, 230), width=5)
    d.line((28, 60, W-28, 60), fill=(55, 21, 11, 203), width=6)
    # Inlaid brass trim follows the wood's structure, not an attached leaf.
    d.line((56, 70, W-56, 70), fill=(239, 177, 79, 185), width=3)
    d.line((56, 75, W-56, 75), fill=(80, 42, 17, 180), width=2)
    d.line((39, 96, W-39, 96), fill=(217, 115, 42, 175), width=4)
    d.line((40, 113, W-40, 113), fill=(52, 19, 9, 206), width=6)
    # Small chamfered joint plates, not massive floating corbels.
    for cx in (132, W-132):
        d.polygon([(cx-44, 104),(cx+44,104),(cx+34,132),(cx+17,165),
                   (cx+6,183),(cx-8,183),(cx-20,161),(cx-35,132)],
                  fill=(74, 30, 12, 241))
        d.line([(cx-39,108),(cx-27,137),(cx-8,173)],
               fill=(218, 131, 48, 174),width=5)
        d.line([(cx+34,110),(cx+22,143),(cx+6,177)],
               fill=(23, 11, 8, 220),width=5)
        d.ellipse((cx-10,154,cx+8,180),
                  fill=(116,48,16,230),outline=(214,144,65,176),width=3)
        d.ellipse((cx-7,78,cx+7,92),
                  fill=(73,32,11,240),outline=(232,178,78,220),width=3)
    # Short center engraved ornament, scaled to the shelf's visual weight.
    cx=W//2
    d.polygon([(cx,87),(cx+12,101),(cx,114),(cx-12,101)],
              fill=(137,77,33,211),outline=(223,160,77,204))
    for side in (-1,1):
        x=cx+side*18
        d.arc((x-42,89,x+42,124), 180 if side>0 else 0,
              340 if side>0 else 160, fill=(222,150,63,132),width=3)
    # The soft shelf shadow should feather inside the asset, not leave alpha
    # dust along the raster outermost pixels (old binding edge failure).
    alpha=im.getchannel("A")
    edge=ImageDraw.Draw(alpha)
    edge.rectangle((0,0,W-1,2),fill=0)
    edge.rectangle((0,H-3,W-1,H-1),fill=0)
    edge.rectangle((0,0,2,H-1),fill=0)
    edge.rectangle((W-3,0,W-1,H-1),fill=0)
    im.putalpha(alpha)
    return im

def blank(name: str, rgb: tuple[int,int,int]) -> Image.Image:
    rng = random.Random(731 + sum(map(ord,name)))
    im = Image.new("RGBA", (BW,BH), (0,0,0,0))
    m = Image.new("L", (BW,BH), 0)
    ImageDraw.Draw(m).rounded_rectangle((3,2,100,539),radius=12,fill=255)
    md = m.load()
    pixels = im.load()
    for y in range(2,540):
        for x in range(3,101):
            alpha=md[x,y]
            if not alpha: continue
            left=min(1,(x-3)/15)
            right=min(1,(100-x)/20)
            side=0.58+0.45*left*right
            warm=0.10*math.exp(-((x-19)/8)**2)
            grain=1+0.026*math.sin(y*0.16+x*.08)+rng.uniform(-.023,.023)
            color=tuple(max(0,min(255,int(v*(side+warm)*grain))) for v in rgb)
            pixels[x,y]=(*color,alpha)
    d=ImageDraw.Draw(im, "RGBA")
    # Closed fine-gold double bands and embossing: deliberately NO WORDS.
    for y in (36,40,62,66,471,475,499,503):
        d.arc((7,y-1,97,y+12),185,355,fill=(225,168,81,235),width=2)
    for y in (30,510):
        d.line([(18,y),(86,y)], fill=(236,187,98,172),width=2)
    cx,cy=52,272
    d.ellipse((26,cy-31,78,cy+31),outline=(229,178,89,155),width=3)
    d.ellipse((31,cy-25,73,cy+25),outline=(90,48,23,210),width=2)
    # Tiny motif variations: compass / diamond / frond, no fake theme icons.
    if name=="dusk":
        for a in range(8):
            angle=a*math.pi/4
            r1,r2=8,21
            d.line([(cx+r1*math.cos(angle),cy+r1*math.sin(angle)),
                    (cx+r2*math.cos(angle),cy+r2*math.sin(angle))],
                   fill=(235,181,91,175), width=3)
        d.ellipse((cx-5,cy-5,cx+5,cy+5),fill=(236,189,98,215))
    elif name=="clay":
        for t in (-1,1):
            d.arc((cx-17+t*8,cy-22,cx+17+t*8,cy+20),
                  55 if t>0 else 125,305 if t>0 else 235,
                  fill=(238,178,91,176),width=3)
        d.polygon([(cx,cy-12),(cx+11,cy),(cx,cy+12),(cx-11,cy)],
                  fill=(191,127,56,173),outline=(238,188,97,184))
    else:
        d.line([(cx,cy+20),(cx,cy-20)],fill=(232,181,87,170),width=3)
        for dy in (-11,0,11):
            d.ellipse((cx-16,cy+dy-4,cx-1,cy+dy+3),
                      outline=(226,167,79,171),width=2)
            d.ellipse((cx+2,cy+dy-3,cx+16,cy+dy+4),
                      outline=(226,167,79,171),width=2)
    # Mask again so ornament never creates dust outside the real binding edge.
    alpha=im.getchannel("A")
    from PIL import ImageChops
    im.putalpha(ImageChops.multiply(alpha,m))
    return im

def generate() -> None:
    assert sha(WOOD)==WOOD_SHA and sha(OLD)==OLD_SHA, "Source asset drift"
    shelf=DEST/"shelf.png"
    png_write(mounted_shelf(),shelf)
    records=[{"name":"shelf","path":"ws16bi/shelf.png","sha256":sha(shelf),
              "size":[W,H],"kind":"shelf"}]
    for name,rgb in VARIANTS.items():
        out=DEST/"blanks"/f"{name}.png"
        png_write(blank(name,rgb),out)
        records.append({"name":name,"path":f"ws16bi/blanks/{name}.png",
                        "sha256":sha(out),"size":[BW,BH],"kind":"unlettered-blank"})
    manifest={"schema":1,"qa_only":True,"release_approved":False,
              "owner_approved":False,"text_on_blanks":False,
              "real_book_ids_in_blanks":False,"books_or_text_on_shelf":False,
              "wood_source_sha256":WOOD_SHA,"prior_shelf_sha256":OLD_SHA,
              "assets":records}
    DEST.mkdir(parents=True,exist_ok=True)
    (DEST/"manifest.json").write_text(json.dumps(manifest,sort_keys=True,indent=2)+"\n")
    print("WS16BI generated detailed shelf + 3 wordless decorative blanks")

if __name__=="__main__":
    generate()
