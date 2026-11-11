#!/usr/bin/env python3
"""Verify new independent high-res artwork appears in debug and never release."""
import json,sys
from pathlib import Path
from zipfile import ZipFile
from build_ws16ba_hi_res import OUTPUT,SOURCE,SOURCE_SHA,CROPS,sha
def verify(qa:Path,release:Path|None=None):
 assert sha(SOURCE.read_bytes())==SOURCE_SHA
 meta=json.loads((OUTPUT/"manifest.json").read_text())
 assert meta["qa_only"] and not meta["release_approved"] and len(meta["assets"])==5
 prefix="assets/ws16ba/shell/"
 with ZipFile(qa) as z:
  packaged={x.removeprefix(prefix) for x in z.namelist() if x.startswith(prefix) and x.endswith(".png")}
  assert packaged=={x+".png" for x in CROPS},packaged
  assert prefix+"manifest.json" in z.namelist()
  for filename,info in meta["assets"].items():
   assert sha((OUTPUT/filename).read_bytes())==info["sha256"]
   assert sha(z.read(prefix+filename))==info["sha256"],filename
 if release:
  with ZipFile(release) as z:
   leak=[x for x in z.namelist() if x.startswith("assets/ws16ba/")]
   assert not leak,("WS16BA QA art leaked into release",leak)
 print("WS16BA five high-res PNGs source-verified in debug; release clean" if release else "WS16BA five high-res PNGs source-verified in debug")
if __name__=="__main__":
 if len(sys.argv) not in (2,3):raise SystemExit("usage: verify_ws16ba_apk.py DEBUG.apk [RELEASE.apk]")
 verify(Path(sys.argv[1]),Path(sys.argv[2]) if len(sys.argv)==3 else None)
