#!/usr/bin/env python3
"""Pin real candidate bytes in QA APK; ensure absent from release APK."""
import json,hashlib,sys,zipfile
from pathlib import Path
root=Path(__file__).resolve().parents[2]
base=root/'app/src/debug/assets/ws16ad'
manifest=json.loads((base/'manifest.json').read_text())
assert manifest['qa_only'] and not manifest['release_approved']
files={}
for item in manifest['assets']:
    rel=Path(item['output']).relative_to(base.relative_to(root)).as_posix()
    files['assets/ws16ad/'+rel]=item['sha256']
files['assets/ws16ad/manifest.json']=hashlib.sha256((base/'manifest.json').read_bytes()).hexdigest()
assert len(files)==13
if len(sys.argv)>1:
    with zipfile.ZipFile(sys.argv[1]) as z:
        seen={n for n in z.namelist() if n.startswith('assets/ws16ad/')}
        assert seen==set(files),('Unexpected/missing WS16AD sprites',seen,set(files))
        for n,sha in files.items():
            assert hashlib.sha256(z.read(n)).hexdigest()==sha,n
if len(sys.argv)>2:
    with zipfile.ZipFile(sys.argv[2]) as z:
        assert not [n for n in z.namelist() if n.startswith('assets/ws16ad/')], 'Book candidates leaked to release'
print('WS16AD_BOOK_FACTORY_SHA_OK',len(files),'release checked:',len(sys.argv)>2)
