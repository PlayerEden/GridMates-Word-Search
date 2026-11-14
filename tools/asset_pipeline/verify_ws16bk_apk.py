#!/usr/bin/env python3
"""Exact-source WS16BK QA asset and release-isolation verification."""
import hashlib
import json
import sys
import zipfile
from pathlib import Path

ROOT=Path(__file__).resolve().parents[2]
FOLDER=ROOT/"app/src/debug/assets/ws16bk"
manifest=json.loads((FOLDER/"manifest.json").read_text())
assert manifest["qa_only"] is True
assert manifest["release_approved"] is False
assert manifest["owner_approved"] is False
assert manifest["no_words_on_blanks"] is True
entries={"assets/"+a["path"]:a["sha256"] for a in manifest["assets"]}
entries["assets/ws16bk/manifest.json"]=hashlib.sha256(
    (FOLDER/"manifest.json").read_bytes()).hexdigest()
assert len(entries)==10, entries
with zipfile.ZipFile(sys.argv[1]) as qa:
    actual={x for x in qa.namelist() if x.startswith("assets/ws16bk/")}
    assert actual==set(entries), (actual,set(entries))
    for name,digest in entries.items():
        assert hashlib.sha256(qa.read(name)).hexdigest()==digest,name
if len(sys.argv)>2:
    with zipfile.ZipFile(sys.argv[2]) as rel:
        assert not any(x.startswith("assets/ws16bk/")
                       for x in rel.namelist()),"Unapproved WS16BK art in release"
print("WS16BK PASS: eight wordless source-pinned book spines and pager rail; release isolated")
