#!/usr/bin/env python3
"""Reassemble exact original WS16AS cabinet source bytes from GitHub-pinned assets.

Only repacks the original master, its five independently verified source crops
and source manifest. No artwork is generated or edited, and no release assets
are written. Full original conversation kit SHA stays in the source manifest.
"""
import hashlib
import io
import json
from pathlib import Path
from zipfile import ZIP_DEFLATED, ZipFile
from PIL import Image

ROOT = Path(__file__).resolve().parents[2]
SOURCE = ROOT / "design/visual-system/masters/ws16as"
KIT = SOURCE / "WS16AS_Modular_Library_Art_Kit.zip"
MASTER_SHA = "c1e42abaaef55b05aba8493637e3bc702376fd4078bd07699c504f1a21941264"
ORIGINAL_KIT_SHA = "db91d99ed5e2f628dcd1e7244f64f1c30e891f57b891324a5021d797a258d8ee"
EXPECTED = {
    "top_crown": ((602, 198), (0, 0, 602, 198), "c4365f20bf839e504cd5828fdacf2f89187a4770c72911e955de1a7b3ccebc63"),
    "left_pilaster": ((126, 814), (0, 198, 126, 1012), "caea09cae3e4114b99fa6fd7c8b9a3d736471b389864b9efb28b8c2f58d21dd9"),
    "recessed_backboard": ((356, 814), (126, 198, 482, 1012), "6d63a53556cbc8ae7059418d8210ca79796becfa7e8642b217debca696c17c64"),
    "right_pilaster": ((120, 814), (482, 198, 602, 1012), "843346dca3c6f711bd5d2d55076e664bcece16131a17efbffb27d996434a9e98"),
    "bottom_base": ((602, 108), (0, 1012, 602, 1120), "1192023c1f02efd042a4e75fc5d3ec56a45bdff6f573c741a0a0f0b0112578a2"),
}

def digest(value: bytes) -> str:
    return hashlib.sha256(value).hexdigest()

def verify_original_sources():
    raw_manifest = (SOURCE / "manifest.json").read_bytes()
    manifest = json.loads(raw_manifest)
    if manifest.get("schema") != "ws16as.modular-art.v1":
        raise ValueError("Unsupported source manifest")
    if manifest.get("original_kit_sha256") != ORIGINAL_KIT_SHA:
        raise ValueError("Wrong conversation art-kit provenance")
    if manifest.get("on_device_tested") is not False:
        raise ValueError("Unapproved phone acceptance claim")
    for flag in ("books_baked_into_shell", "shelves_baked_into_shell",
                 "plaques_baked_into_shell", "ui_text_baked_into_shell"):
        if manifest.get(flag) is not False:
            raise ValueError("Original cabinet must be empty: " + flag)
    master = (SOURCE / "original_candidate_sheet.png").read_bytes()
    if digest(master) != MASTER_SHA or manifest.get("source_sheet_sha256") != MASTER_SHA:
        raise ValueError("The original sheet bytes differ from the verified user upload")
    source = Image.open(io.BytesIO(master)).convert("RGBA")
    if source.size != (1017, 1547):
        raise ValueError("Master geometry changed")
    assembly = Image.new("RGBA", (602, 1120))
    original = {}
    for name, (dimensions, bounds, expected_sha) in EXPECTED.items():
        item = manifest["shell_parts"][name]
        filename = "assets/shell/" + name + ".png"
        if item["filename"] != filename or item["source_crop"] != list(bounds):
            raise ValueError("Invalid source crop/path: " + name)
        if (item["width"], item["height"]) != dimensions or item["sha256"] != expected_sha:
            raise ValueError("Original manifest hash/geometry modified: " + name)
        data = (SOURCE / "shell" / (name + ".png")).read_bytes()
        if digest(data) != expected_sha:
            raise ValueError("Original cabinet image hash mismatch: " + name)
        image = Image.open(io.BytesIO(data)).convert("RGBA")
        if image.size != dimensions:
            raise ValueError("Original cabinet image size mismatch: " + name)
        assembly.paste(image, (bounds[0], bounds[1]))
        original[filename] = data
    if assembly.tobytes() != source.crop((0, 0, 602, 1120)).tobytes():
        raise ValueError("The five shell pieces no longer reconstruct the original")
    return raw_manifest, master, original

def assemble():
    raw_manifest, master, original = verify_original_sources()
    if KIT.exists():
        print("Existing source ZIP retained; original PNGs independently verified")
        return
    root = "WS16AS_Modular_Library_Art_Kit/"
    with ZipFile(KIT, "w", ZIP_DEFLATED, compresslevel=9) as archive:
        archive.writestr(root + "manifest.json", raw_manifest)
        archive.writestr(root + "original_candidate_sheet.png", master)
        for filename, content in sorted(original.items()):
            archive.writestr(root + filename, content)
    with ZipFile(KIT) as archive:
        for filename, content in original.items():
            assert archive.read(root + filename) == content
    print("WS16AS: reassembled exact five original SHA-pinned cabinet sprites",
          "into a source-only QA kit; no rights/phone approval inferred")

if __name__ == "__main__":
    assemble()
