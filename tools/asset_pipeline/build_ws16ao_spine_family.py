#!/usr/bin/env python3
"""WS16AO: six source-pinned painted physical bindings, tall and standard.
Lettering and emblems remain original-size copied pixels; new width comes
from separately authored natural leather shoulders, not stretched printing.
"""
from __future__ import annotations
import hashlib, io, json, math, random
from pathlib import Path
from PIL import Image, ImageDraw, ImageChops, ImageFilter

ROOT=Path(__file__).resolve().parents[2]
SOURCE=ROOT/"app/src/debug/assets/ws16ai/spines"
ORIGINAL_MANIFEST=ROOT/"app/src/debug/assets/ws16ai/manifest.json"
DEST=ROOT/"app/src/debug/assets/ws16ao"
REAL=("woodland","space","garden")
BLANK=("linen","night","burgundy")
NAMES=REAL+BLANK
SEEDS={n:(i+1)*17 for i,n in enumerate(NAMES)}
PALETTE={"woodland":(19,77,48),"space":(17,47,91),"garden":(20,87,49),
         "linen":(23,91,68),"night":(26,58,106),"burgundy":(99,42,58)}
W,H=166,542
CUT,ADDED=216,120

def sha(raw):return hashlib.sha256(raw).hexdigest()

def save(path,img):
    path.parent.mkdir(parents=True,exist_ok=True)
    b=io.BytesIO();img.save(b,"PNG",optimize=True,compress_level=9)
    raw=b.getvalue();path.write_bytes(raw);return raw

def make_standard(name,source,donor):
    assert source.size==donor.size==(104,542)
    out=Image.new("RGBA",(W,H),(0,0,0,0));o=out.load();p=donor.load()
    palette=PALETTE[name];seed=SEEDS[name]
    for y in range(2,H-2):
        tip=min(y-2,H-3-y)
        inset=max(0,9-min(9,tip//2))
        for x in range(5+inset,W-5-inset):
            t=(x-5)/(W-10)
            bevel=(.77+.26*math.sin(min(1,t/.15)*math.pi/2)) if t<.15 else 1.0
            if t>=.85:bevel=.91-.16*(t-.85)/.15
            if t<.08:bevel*=.77
            sample=(12+(x+y//15)%7) if x<35 else (93+(x+y//16)%6)
            rgb=p[sample,y][:3]
            noise=(math.sin(x*.28+y*.039+seed)*1.2+
                   math.sin(x*.092-y*.057+seed)*.8+
                   math.sin(x*.49+y*.075)*.35+
                   (sum(rgb)/3-65)*.085)
            o[x,y]=tuple(max(0,min(255,int((c+noise)*bevel)))
                         for c in palette)+(255,)

    # Feather only the old 4-14px and 90-100px silhouette edges.
    # All interior authored words/emblems remain identical source pixels.
    center=source.crop((4,0,100,H))
    alpha=center.getchannel("A");fade=Image.new("L",center.size,255)
    fm=fade.load()
    for y in range(H):
        for x in range(10):
            fm[x,y]=int(255*x/10)
            fm[95-x,y]=int(255*x/10)
    center.putalpha(ImageChops.multiply(alpha,fade))
    out.alpha_composite(center,(35,0))
    d=ImageDraw.Draw(out,"RGBA")
    for y,width in ((20,2),(33,2),(54,3),(485,3),(501,2),(516,2)):
        for x in range(13,W-13):
            if 45<=x<125:continue
            light=math.sin((x-5)/(W-10)*math.pi)
            gold=(218+int(23*light),178+int(30*light),92+int(22*light),210)
            d.line(((x,y),(x,y+width)),fill=gold,width=1)
            d.point((x,y+width+1),fill=(91,52,20,130))
    for x in (38,130):
        d.line(((x,73),(x,470)),fill=(226,178,89,28),width=1)

    if name in BLANK:
        rng=random.Random(seed*53)
        overlay=Image.new("RGBA",(W,H),(0,0,0,0))
        brush=ImageDraw.Draw(overlay,"RGBA")
        for _ in range(1300):
            x=rng.randrange(42,126);y=rng.randrange(100,462)
            pigment=((232,217,169,rng.randint(2,10))
                     if rng.random()<.28 else (5,17,13,rng.randint(3,12)))
            brush.line(((x,y),(x,y+rng.randint(1,3))),fill=pigment,width=1)
        out=Image.alpha_composite(out,overlay)

    mask=Image.new("L",(W,H),0)
    ImageDraw.Draw(mask).rounded_rectangle((5,2,160,539),radius=15,fill=255)
    mask=mask.filter(ImageFilter.GaussianBlur(.55))
    out.putalpha(ImageChops.darker(out.getchannel("A"),mask))
    return out

def make_tall(short):
    """Insert UNPRINTED leather; don't squash or stretch any existing art."""
    assert short.size==(W,H)
    new=Image.new("RGBA",(W,H+ADDED),(0,0,0,0))
    new.alpha_composite(short.crop((0,0,W,CUT)),(0,0))
    new.alpha_composite(short.crop((0,CUT,W,H)),(0,CUT+ADDED))
    source=short.load();dest=new.load()
    for y in range(ADDED):
        sy=213+y%3 # Confirmed bare leather in each source
        shade=.5*math.sin(y*.17)+.3*math.sin(y*.37)
        for x in range(W):
            r,g,b,a=source[x,sy]
            dest[x,CUT+y]=(max(0,min(255,int(r+shade))),
                           max(0,min(255,int(g+shade))),
                           max(0,min(255,int(b+shade))),a)
    return new

def generate():
    prior=json.loads(ORIGINAL_MANIFEST.read_text())
    source_set={}
    for name in NAMES:
        p=SOURCE/(name+".png");raw=p.read_bytes()
        assert sha(raw)==prior["spines"][name]["sha256"],("Source drift",name)
        source_set[name]=(Image.open(io.BytesIO(raw)).convert("RGBA"),raw,p)
    assets=[]
    for name in NAMES:
        source,raw,path=source_set[name]
        donor=source_set[{"linen":"woodland","night":"space",
                          "burgundy":"garden"}.get(name,name)][0]
        standard=make_standard(name,source,donor)
        tall=make_tall(standard)
        for profile,art in (("standard",standard),("tall",tall)):
            dst=DEST/"spines"/profile/(name+".png")
            bytes_out=save(dst,art)
            bbox=art.getchannel("A").point(lambda a:255 if a>=40 else 0).getbbox()
            assert bbox and bbox[0]>=5 and bbox[2]<=161
            assets.append({"name":name,"profile":profile,
                "role":"real" if name in REAL else "nonplayable-blank",
                "source":str(path.relative_to(ROOT)),
                "source_sha256":sha(raw),"output":str(dst.relative_to(ROOT)),
                "sha256":sha(bytes_out),"dimensions":list(art.size),
                "alpha_bounds":list(bbox),"playable":name in REAL})
    manifest={"schema_version":1,"id":"WS16AO thicker physical publisher spine family",
       "qa_only":True,"release_approved":False,"owner_approved":False,
       "construction":"Original printed art and words 1:1; separately rendered leather shoulders",
       "profiles":{"standard":[166,542],"tall":[166,662]},
       "covers_unchanged":True,"assets":assets}
    DEST.mkdir(parents=True,exist_ok=True)
    (DEST/"manifest.json").write_text(json.dumps(manifest,indent=2,sort_keys=True)+"\n")
    print("WS16AO generated",len(assets),"separate debug spines")
    return manifest

if __name__=="__main__":generate()
