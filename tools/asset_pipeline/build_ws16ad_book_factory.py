#!/usr/bin/env python3
"""WS-16AD reproducible *candidate* book factory; no approval or save changes.

Reads original generated/debug WS-14 covers and spines after their pinned-sheet
import. Produces v2 geometry proofs with shared physical bounds and consistent
alpha handling; generates three visually distinct NON-PLAYABLE blank volumes.

IMPORTANT: This is a geometry/alpha proof only; historic V1 titles may still
contain the unregistered mark. Those must be corrected in revised art masters,
not scrubbed using a heuristic or represented as finished V2 cover artwork.
"""
from __future__ import annotations
from PIL import Image,ImageFilter,ImageChops
import hashlib,json,io,sys,os
from pathlib import Path
from book_spine_policy import spine_name_profile

ROOT=Path(__file__).resolve().parents[2]
OLD=ROOT/'app/src/debug/assets/ws14'
OUT=ROOT/'app/src/debug/assets/ws16ad'
GEOMETRY={'cover': {'canvas':[480,640],'visual_bbox':[12,65,468,574]},
          'spine': {'canvas':[128,640],'visual_bbox':[14,51,114,589]}}
THEMES=('woodland','space','garden')
BLANKS=(('linen','', '#607B56','#1D4638','#A6C18F'),
        ('night','', '#45507D','#192A56','#839ACB'),
        ('burgundy','', '#87554B','#542D37','#C69583'))


def digest(raw: bytes)->str:
    return hashlib.sha256(raw).hexdigest()


def alpha_bbox(src: Image.Image, cutoff:int=40):
    a=src.getchannel('A').point(lambda v:255 if v>=cutoff else 0)
    bbox=a.getbbox()
    if not bbox:raise ValueError('empty or transparent art')
    return bbox


def clean_alpha(im: Image.Image)->Image.Image:
    """Remove 1px anti-aliased matte at *silhouette*, keep internal painted highlights."""
    im=im.convert('RGBA')
    a=im.getchannel('A')
    # A narrow erosion eliminates semi-white matte at exterior perimeter.
    inside=a.filter(ImageFilter.MinFilter(3))
    # Combine original AA with eroded silhouette to avoid moving book geometry.
    a=ImageChops.darker(a,Image.blend(a,inside,0.65))
    im.putalpha(a)
    return im


def normalize(src: Image.Image,kind:str, template: Image.Image|None=None):
    spec=GEOMETRY[kind]
    W,H=spec['canvas']; x0,y0,x1,y1=spec['visual_bbox']
    src=src.convert('RGBA'); bbox=alpha_bbox(src)
    crop=src.crop(bbox)
    # Explicit, locked geometry of source book fronts. Image content may be
    # stretched only a few percent to reach the agreed publisher silhouette.
    crop=crop.resize((x1-x0,y1-y0),Image.Resampling.LANCZOS)
    crop=clean_alpha(crop)
    out=Image.new('RGBA',(W,H),(0,0,0,0))
    out.alpha_composite(crop,(x0,y0))
    if template is not None:
        # Use same reference cover shape, while not inventing white pixels outside
        # supplied artwork. A geometry target is not a replacement painted book.
        mask=template.getchannel('A')
        out.putalpha(ImageChops.darker(mask,out.getchannel('A')))
    # Nothing outside the canonical rectangle may be nontransparent.
    box=alpha_bbox(out)
    assert box[0]>=x0 and box[1]>=y0 and box[2]<=x1 and box[3]<=y1,box
    return out, bbox, box


def blank_svg(kind:str, palette:tuple[str,str,str],variant:int):
    """Editable source masters: 1:1 matching cover/spine visual frames, no lettering."""
    main,dark,lit=palette
    if kind=='cover':
        # Canvas 480×640; same visual bounds as canonical illustrated cover.
        motifs=[
            '<g fill="none" stroke="#F1D784" stroke-width="4" opacity=".85"><path d="M227 269Q247 225 266 269Q279 298 245 316Q210 291 227 269Z"/><path d="M245 314V342M245 332Q227 328 216 316M245 327Q268 323 278 311"/></g>',
            '<path d="M240 238C252 269 285 278 301 307C273 310 260 325 240 357C214 319 199 311 173 307C206 284 226 265 240 238Z" fill="none" stroke="#F2D78B" stroke-width="5"/><circle cx="240" cy="304" r="11" fill="#F1DEAC" opacity=".8"/>',
            '<g fill="none" stroke="#F1D784" stroke-width="5"><path d="M190 327L240 268L290 327M210 326V299H270V326"/><path d="M181 350H299"/></g>'
        ]
        # Used as a source asset, not a user-facing book with readable fake pages.
        return f'''<svg xmlns="http://www.w3.org/2000/svg" width="480" height="640" viewBox="0 0 480 640"><defs>
 <linearGradient id="a" x1="0" y1="0" x2="1" y2="1"><stop stop-color="{lit}"/><stop offset=".15" stop-color="{main}"/><stop offset=".72" stop-color="{dark}"/><stop offset="1" stop-color="#181F19"/></linearGradient>
 <linearGradient id="sp" x1="0" y1="0" x2="1" y2="0"><stop stop-color="#0D261E"/><stop offset=".33" stop-color="{main}"/><stop offset=".72" stop-color="{dark}"/><stop offset="1" stop-color="#171F16"/></linearGradient>
 <linearGradient id="pages" x1="0" y1="0" x2="0" y2="1"><stop stop-color="#FFF0C8"/><stop offset=".6" stop-color="#B59A6E"/><stop offset="1" stop-color="#6D4F2A"/></linearGradient></defs>
 <path d="M40 555Q49 566 84 570H454Q464 568 466 561L450 574H55Q42 567 40 555Z" fill="url(#pages)" stroke="#422B17" stroke-width="3"/>
 <path d="M34 82Q37 65 64 65H464V555Q460 565 446 563H66Q36 563 34 549Z" fill="url(#a)" stroke="#2B2316" stroke-width="5"/>
 <path d="M34 82Q37 65 64 65H80V555Q50 572 34 549Z" fill="url(#sp)" stroke="#C79843" stroke-width="3"/>
 <path d="M82 78H451V544H82Z" fill="none" stroke="#F4D88C" stroke-width="6"/>
 <path d="M95 92H438V530H95Z" fill="none" stroke="#7E5B28" stroke-width="2" opacity=".9"/>
 <path d="M88 101Q114 97 114 78M451 101Q424 97 424 78M88 520Q114 524 114 544M451 520Q424 524 424 544" fill="none" stroke="#FFE9A7" stroke-width="7"/>
 <path d="M45 104H78M45 516H78" stroke="#F2C96A" stroke-width="8"/>
 <path d="M105 421Q268 439 430 421" stroke="#ECD3A5" opacity=".22" fill="none" stroke-width="4"/>
 {motifs[variant]}
 <path d="M106 461Q268 451 428 461" fill="none" stroke="#E8C678" stroke-width="3" opacity=".65"/>
 <rect x="137" y="464" width="264" height="52" rx="24" fill="#E7CB92" stroke="#76531E" stroke-width="5" opacity=".85"/>
 <path d="M156 488H378" stroke="#AC8342" stroke-width="2" opacity=".45"/>
 </svg>'''
    return f'''<svg xmlns="http://www.w3.org/2000/svg" width="128" height="640" viewBox="0 0 128 640"><defs>
 <linearGradient id="sp" x1="0" y1="0" x2="1" y2="0"><stop stop-color="#0D231B"/><stop offset=".20" stop-color="{lit}"/><stop offset=".47" stop-color="{main}"/><stop offset=".78" stop-color="{dark}"/><stop offset="1" stop-color="#141A18"/></linearGradient></defs>
 <path d="M19 59Q24 52 36 51H100Q114 55 114 73V571Q112 587 100 589H35Q14 588 14 574V75Q14 63 19 59Z" fill="url(#sp)" stroke="#322113" stroke-width="3"/>
 <path d="M19 75H108M19 97H108M19 541H108M19 565H108" stroke="#F0C66A" stroke-width="7"/>
 <path d="M30 107H100V531H30Z" fill="none" stroke="#E4BD65" opacity=".62" stroke-width="2"/>
 <path d="M44 314Q64 277 84 314Q64 352 44 314Z" fill="none" stroke="#F0D48D" stroke-width="5"/>
 </svg>'''


def svg2png(raw:str)->bytes:
    import cairosvg
    return cairosvg.svg2png(bytestring=raw.encode('utf-8'))


def save_png(path:Path,img:Image.Image)->bytes:
    path.parent.mkdir(parents=True,exist_ok=True)
    b=io.BytesIO();img.save(b,format='PNG',optimize=True,compress_level=9)
    raw=b.getvalue();path.write_bytes(raw)
    return raw


def generate(root:Path=ROOT)->dict:
    global OLD,OUT
    OLD=root/'app/src/debug/assets/ws14';OUT=root/'app/src/debug/assets/ws16ad'
    folder=root/'design/visual-system/masters/ws16ad'
    folder.mkdir(parents=True,exist_ok=True)
    entries=[];canonical=None
    for theme in THEMES:
        for kind in ('cover','spine'):
            source=OLD/('books' if kind=='cover' else 'spines')/(theme+'.png')
            data=source.read_bytes();im=Image.open(io.BytesIO(data)).convert('RGBA')
            img,bbox,result=normalize(im,kind,canonical if kind=='cover' and canonical else None)
            if theme=='woodland' and kind=='cover':canonical=img
            rel=f'{kind}s/{theme}.png'
            output=OUT/rel
            raw=save_png(output,img)
            entries.append({'key':theme+'/'+kind,'role':'geometry-proof',
               'input':str(source.relative_to(root)),'input_sha256':digest(data),
               'source_bbox':bbox,'output':str(output.relative_to(root)),
               'sha256':digest(raw),'output_bbox':result,'dimensions':list(img.size),
               'contains_unreviewed_original_baked_lettering':True,
               **({'spine_name_profile':spine_name_profile(theme.capitalize())}
                  if kind=='spine' else {})})
    for index,(name,_,main,dark,lit) in enumerate(BLANKS):
        for kind in ('cover','spine'):
            source=blank_svg(kind,(main,dark,lit),index)
            srcPath=folder/f'placeholder-{name}-{kind}.svg'
            if srcPath.exists():
                if srcPath.read_text(encoding='utf-8') != source + '\n':
                    raise ValueError('Source SVG master has drifted: ' + str(srcPath))
            else:
                raise ValueError('Missing pinned placeholder master: ' + str(srcPath))
            b=svg2png(source)
            original=Image.open(io.BytesIO(b)).convert('RGBA')
            im,unused,bbox=normalize(original,kind)
            rel=f'placeholders/{name}-{kind}.png'
            output=OUT/rel;raw=save_png(output,im)
            entries.append({'key':name+'/'+kind,'role':'non-playable-placeholder',
              'input':str(srcPath.relative_to(root)),
              'input_sha256':digest(srcPath.read_bytes()),
              'output':str(output.relative_to(root)),
              'sha256':digest(raw),'output_bbox':alpha_bbox(im),'dimensions':list(im.size),
              'playable':False,'has_catalog_id':False})
    manifest={'schema_version':1,'name':'GridMates Word Search WS16AD book factory geometry proof',
     'status':'QA_GEOMETRY_PROOF_NOT_PRODUCTION_ART',
     'owner_approved':False,'release_approved':False,'qa_only':True,
     'source_sheet':'design/visual-system/masters/ws14-generated-asset-sheet-original.png',
     'source_sheet_sha256':'9c452ba81352b9a3bd08496de5e8cf8d8ea9ed188af540d4db2b2d558c41808e',
     'geometry':GEOMETRY,
     'spine_naming_policy':{
         'master_geometry':'spine-128x640-v1',
         'preferred_display_name':'one word, ideally 4-8 letters',
         'standard':'1-7 letters; future proportional library view 140dp high',
         'tall':'8-11 letters; future proportional library view 148dp high',
         'editorial_review':'long or multiword name; typeset manually, no silent shortening',
         'touch_target':'minimum 48dp independently of painted spine width',
         'applies_in_runtime_now':False,
     },'assets':entries,
     'open_gates':['Replace in-image unregistered ® in source painting',
       'Approve clean V2 book lettering and alpha edges on cream and dark backgrounds',
       'Verify hand-held tappable spines and native accessible labels',
       'No new playable book IDs for placeholders']}
    (OUT/'manifest.json').write_text(json.dumps(manifest,indent=2)+'\n')
    return manifest

if __name__=='__main__':
    data=generate()
    print('WS16AD_BOOK_FACTORY_OK',len(data['assets']))
