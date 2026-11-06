#!/usr/bin/env python3
"""Byte-verified, pinned Google Fonts import for B5 Android builds.

Only selected OFL font binaries and matching complete license notices enter
the APK. Noninteractive: rejects drift and unknown/unlicensed content.
Requires fontTools for glyph cmap checks (installed by CI).
"""
import argparse
import hashlib
import json
import pathlib
import shutil

ALPHABET = set("ABCDEFGHIJKLMNOPQRSTUVWXYZ")
OUTPUT_FONT_DIR = pathlib.Path("app/src/main/res/font")
OUTPUT_LICENSE_DIR = pathlib.Path("app/src/main/assets/font_licenses")
def git_blob(data):
    return hashlib.sha1(b"blob " + str(len(data)).encode() + b"\0" + data).hexdigest()

def main():
    p=argparse.ArgumentParser()
    p.add_argument("--source",required=True)
    p.add_argument("--manifest",default="design/font-library-approved-b5.json")
    args=p.parse_args()
    from fontTools.ttLib import TTFont
    root=pathlib.Path(args.source).resolve()
    manifest=json.loads(pathlib.Path(args.manifest).read_text(encoding="utf-8"))
    assert len(manifest["entries"]) == 4
    assert manifest["sourceCommit"] == "24ddc1f324cac5d9f5aedca126d298f56e3f1640"
    assert manifest["license"] == "OFL-1.1"
    OUTPUT_FONT_DIR.mkdir(parents=True,exist_ok=True)
    OUTPUT_LICENSE_DIR.mkdir(parents=True,exist_ok=True)
    for e in manifest["entries"]:
        assert e["id"] in {"atkinson", "plex", "dmmono", "patrick"}
        assert e["sourcePath"].endswith(".ttf") and e["licensePath"].endswith("/OFL.txt")
        assert e["androidFile"].startswith("gm_") and e["androidFile"].endswith(".ttf")
        assets=[]
        for path, sha, n, expected_sha256 in (
            ("sourcePath","sourceGitBlobSha1","sourceBytes","sourceSha256"),
            ("licensePath","licenseGitBlobSha1","licenseBytes","licenseSha256")
        ):
            target=(root / e[path]).resolve()
            assert target.is_relative_to(root), "Source path escaped pinned root"
            raw=target.read_bytes()
            assert len(raw)==e[n], f"Unexpected file size: {target}"
            actual_git=git_blob(raw)
            assert actual_git == e[sha], f"Git blob hash mismatch: {target}"
            digest=hashlib.sha256(raw).hexdigest()
            if e[expected_sha256] is not None:
                assert digest == e[expected_sha256], f"SHA-256 mismatch: {target}"
            print("FONT_INTAKE_SHA256",e["id"],path,digest,"bytes",len(raw))
            assets.append(raw)
        font_data,license_data=assets
        license_text=license_data.decode("utf-8-sig")
        assert "SIL OPEN FONT LICENSE" in license_text.upper()
        assert "Copyright" in license_text or "copyright" in license_text
        outfile=OUTPUT_FONT_DIR / e["androidFile"]
        outfile.write_bytes(font_data)
        (OUTPUT_LICENSE_DIR / (e["id"]+"_ofl.txt")).write_bytes(license_data)
        with TTFont(outfile,lazy=True) as font:
            glyph_map=font.getBestCmap() or {}
            missing=ALPHABET - {chr(code) for code in glyph_map}
            assert not missing, f"Required Latin uppercase absent in {e['id']}: {missing}"
            spanish="ÁÉÍÓÚÜÑ"
            covered="".join(c for c in spanish if ord(c) in glyph_map)
            family=font["name"].getDebugName(1)
            print("FONT_GLYPHS",e["id"],"A-Z=PASS","Spanish-supported="+covered,
                  "family="+str(family))
        print("FONT_APPROVED_PACKAGE",e["id"],e["androidFile"])
    assert len(list(OUTPUT_FONT_DIR.glob("gm_*.ttf"))) >= 4
    print("APPROVED_FONT_COUNT=4")

if __name__=="__main__":
    main()
