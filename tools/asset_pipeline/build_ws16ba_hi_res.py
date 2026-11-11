#!/usr/bin/env python3
"""WS16BA: reproducible five-piece QA cabinet from a high-resolution master."""
import hashlib, io, json
from pathlib import Path
from PIL import Image
ROOT=Path(__file__).resolve().parents[2]
SOURCE=ROOT/"design/visual-system/masters/ws16ba/cabinet_tall_master_q90.jpg"
OUTPUT=ROOT/"app/src/debug/assets/ws16ba/shell"
SOURCE_SHA="d599f7e7510e2b5aaa28fdc20bf33186c5fb245f2ad31922d4e2ad50ffc44bc3"
DIMENSIONS=(1024,2298)
CROPS={
 "top_crown":(0,0,1024,337),
 "left_pilaster":(0,337,214,2114),
 "recessed_backboard":(214,337,820,2114),
 "right_pilaster":(820,337,1024,2114),
 "bottom_base":(0,2114,1024,2298)}
def sha(raw): return hashlib.sha256(raw).hexdigest()
def build():
 raw=SOURCE.read_bytes()
 assert sha(raw)==SOURCE_SHA,"Source master SHA mismatch"
 source=Image.open(io.BytesIO(raw)).convert("RGB")
 assert source.size==DIMENSIONS,"Source master geometry mismatch"
 OUTPUT.mkdir(parents=True,exist_ok=True)
 assembled=Image.new("RGB",DIMENSIONS)
 assets={}
 for name,bounds in CROPS.items():
  part=source.crop(bounds)
  assembled.paste(part,(bounds[0],bounds[1]))
  stream=io.BytesIO();part.save(stream,format="PNG",optimize=True)
  data=stream.getvalue();(OUTPUT/(name+".png")).write_bytes(data)
  assets[name+".png"]={"sha256":sha(data),"dimensions":list(part.size),"bounds":list(bounds)}
 assert assembled.tobytes()==source.tobytes(),"Cabinet pieces do not reconstruct master"
 meta={"schema":"ws16ba.highres-qa.v1","source_sha256":SOURCE_SHA,
       "source_dimensions":list(DIMENSIONS),"qa_only":True,
       "release_approved":False,"on_device_approved":False,
       "books_shelves_text_plaques_baked_in":False,"assets":assets}
 (OUTPUT/"manifest.json").write_text(json.dumps(meta,indent=2,sort_keys=True)+"\n")
 return meta
if __name__=="__main__":
 print("WS16BA generated 5 source-matched high-resolution QA PNGs:",len(build()["assets"]))
