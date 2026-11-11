#!/usr/bin/env python3
"""WS16AK: extract the owner-selected illustrated empty bookcase into reversible components.

The full-resolution raster is an immutable master. Each module is a TRUE crop of
that painting; resizing happens only to the relatively plain vertical bay strips.
Book sprites, text and touch targets are not baked into the resulting background.
Usage: python build_ws16ak_background.py --source path/to/approved_empty_bookcase.png --output /path/to/dir
"""
from __future__ import annotations
import argparse, hashlib, json, math
from pathlib import Path
from PIL import Image, ImageDraw, ImageFont

W,H=1024,1536
X0,X1=122,901
CUTS = [0,246,696,824,1188,1300,1536]
ROW_NAMES=['crown','upper_bay','middle_shelf','lower_bay','lower_shelf','base']


def sha256(p):return hashlib.sha256(p.read_bytes()).hexdigest()
def make_assets(source: Path, output: Path):
    im=Image.open(source).convert('RGBA'); assert im.size==(W,H),f'Expected {(W,H)}, got {im.size}'
    output.mkdir(parents=True,exist_ok=True)
    records=[]
    def save(name, region, bounds, group):
        file=output/(name+'.png'); region.save(file,optimize=True)
        records.append({'name':name,'file':file.name,'sha256':sha256(file),'size':list(region.size),'bbox':list(bounds),'group':group})
    for i,(y0,y1) in enumerate(zip(CUTS[:-1],CUTS[1:])):
        row=ROW_NAMES[i]; crop=im.crop((0,y0,W,y1)); save(row,crop,(0,y0,W,y1),'horizontal-band')
        if row.endswith('_bay'):
            for label,x0,x1 in [('left_pilaster',0,X0),('recessed_backboard',X0,X1),('right_pilaster',X1,W)]:
                sub=im.crop((x0,y0,x1,y1))
                save(row+'__'+label,sub,(x0,y0,x1,y1),'bay-component')
    # Optional neutral backboard sample for optional future new tiers, not a seamless claim.
    sample=im.crop((X0+70,920,X1-65,1120))
    save('backboard_extended_sample',sample,(X0+70,920,X1-65,1120),'repeat-optional-reference')
    # reconstruction @ source size must preserve every pixel
    rebuilt=Image.new('RGBA',(W,H),(0,0,0,0))
    for name,y0,y1 in zip(ROW_NAMES,CUTS[:-1],CUTS[1:]):
        rebuilt.alpha_composite(Image.open(output/(name+'.png')).convert('RGBA'),(0,y0))
    assert rebuilt.tobytes()==im.tobytes(),'A module seam or dropped pixel changed the approved artwork'
    rebuilt.save(output/'assembled_native_identical.png',optimize=True)
    source_copy=output/'approved_bookcase_master_1024x1536.png'
    source_copy.write_bytes(source.read_bytes())
    metadata={
      'id':'WS16AK-cabinet-only-painterly-master-v1',
      'status':'source-extracted-background-QA-only',
      'source_name':source_copy.name,'source_sha256':sha256(source_copy),
      'pixel_identical_reconstruction':True,
      'source_size':[W,H],'x_bay_seams':[X0,X1],
      'row_y_cuts':CUTS,'row_order':ROW_NAMES,
      'draw_order':'Six row bands for perfect native layout, or separate bay backing/pilasters for responsive vertical stretching. Keep actual book art/taps/native text outside this painting.',
      'layer_notes':{
        'crown':'Top carved header with ivy and open-book emblem',
        'upper_bay':'First recessed shelf interior; contains no books',
        'middle_shelf':'Carved central divider/shelf ledge, fixed aspect ratio',
        'lower_bay':'Second recessed shelf interior; contains no books',
        'lower_shelf':'Lower book contact plane; fixed aspect ratio',
        'base':'Decorative base and plinth, not an additional playable shelf',
      },
      'modules':records,
      'acceptance':{'on_phone':False,'android_integrated':False,'book_spines_rendered':False,'artwork_owner_approved':False},
      'rollback':'WS16AI full modular library stage-1 and the previous verified APK',
    }
    (output/'manifest.json').write_text(json.dumps(metadata,indent=2)+'\n')
    return metadata

def render_target(modules:Path,out:Path,width:int,height:int):
    """Responsive vertical 6-slice preview, preserving ornate bands' shape."""
    imgs={name:Image.open(modules/(name+'.png')).convert('RGBA') for name in ROW_NAMES}
    # Scale fixed wood carving parts proportionally to device width. Bay height absorbs flexibility.
    scale=width/W
    fixed={key:max(1,round(imgs[key].height*scale)) for key in ['crown','middle_shelf','lower_shelf','base']}
    free=height-sum(fixed.values())
    if free<90:raise ValueError('Too short to display two recessed bays with readable books')
    h_upper=round(free*(450/(450+364))); h_lower=free-h_upper
    heights={**fixed,'upper_bay':h_upper,'lower_bay':h_lower}
    canvas=Image.new('RGBA',(width,height),(0,0,0,0)); y=0
    positions={}
    for name in ROW_NAMES:
        hh=heights[name];im=imgs[name].resize((width,hh),Image.Resampling.LANCZOS)
        canvas.alpha_composite(im,(0,y));positions[name]={'top':y,'bottom':y+hh}
        y+=hh
    assert y==height
    canvas.convert('RGB').save(out,quality=93,subsampling=0)
    return positions


def render_phone_gallery(modules:Path,out:Path):
    from PIL import ImageDraw
    devices=[('Compact · 320dp',320,610),('Typical · 360dp',360,655),('Large · 412dp',412,725),('Tall · 360dp',360,760)]
    margin=20; title=50; footer=58; w_total=sum(w for _,w,h in devices)+margin*(len(devices)+1)
    h_total=max(h for _,w,h in devices)+title+footer+margin*2
    canvas=Image.new('RGB',(w_total,h_total),(242,236,222)); draw=ImageDraw.Draw(canvas)
    x=margin; results=[]
    for label,w,h in devices:
        output=out.parent/f'empty_{w}x{h}.jpg'
        pos=render_target(modules,output,w,h)
        art=Image.open(output)
        yy=title+margin
        canvas.paste(art,(x,yy))
        draw.text((x,margin),label,fill=(39,79,66))
        draw.text((x,yy+h+12),'EMPTY — books remain native overlays',fill=(71,65,56))
        results.append({'device':label,'width_px':w,'cabinet_height_px':h,'parts':pos})
        x+=w+margin
    canvas.save(out,quality=94)
    (modules/'layout_sizing_matrix.json').write_text(json.dumps(results,indent=2)+'\n')
    return results

if __name__=='__main__':
    ap=argparse.ArgumentParser();ap.add_argument('--source',type=Path,required=True);ap.add_argument('--output',type=Path,required=True)
    args=ap.parse_args()
    data=make_assets(args.source,args.output)
    r=render_phone_gallery(args.output,args.output/'empty_phone_sizes.jpg')
    print(json.dumps({'modules':len(data['modules']),'source':data['source_sha256'],'viewport_count':len(r),'preview':str(args.output/'empty_phone_sizes.jpg')},indent=2))
