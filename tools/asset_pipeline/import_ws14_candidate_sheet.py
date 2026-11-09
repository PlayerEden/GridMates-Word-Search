#!/usr/bin/env python3
"""Import six QA sprites from one SHA-pinned original PNG, like Woodland portraits.

Upload design/visual-system/masters/ws14-generated-asset-sheet-original.png
to the WS-14 draft branch to enable this deterministic, offline build step.
No watermark URLs, network artwork services, or production approvals.
"""
from collections import deque
import hashlib
from pathlib import Path

SOURCE = "design/visual-system/masters/ws14-generated-asset-sheet-original.png"
SHA256 = "9c452ba81352b9a3bd08496de5e8cf8d8ea9ed188af540d4db2b2d558c41808e"
REGIONS = {
    "woodland": {"cover": (11, 7, 420, 458), "spine": (428, 7, 519, 455)},
    "space": {"cover": (528, 7, 922, 459), "spine": (928, 7, 1014, 453)},
    "garden": {"cover": (1025, 7, 1436, 458), "spine": (1442, 8, 1527, 455)},
}


def hash_file(path):
    return hashlib.sha256(path.read_bytes()).hexdigest()


def remove_connected_white(image):
    pixels = image.load()
    width, height = image.size
    queue = deque()
    seen = set()
    for x in range(width):
        for y in (0, height-1):
            if sum(pixels[x, y][:3]) > 690:
                queue.append((x, y))
    for y in range(height):
        for x in (0, width-1):
            if sum(pixels[x, y][:3]) > 690:
                queue.append((x, y))
    while queue:
        x, y = queue.popleft()
        if (x, y) in seen:
            continue
        seen.add((x, y))
        r, g, b, a = pixels[x, y]
        if min(r, g, b) < 215 or max(r, g, b) - min(r, g, b) > 23:
            continue
        pixels[x, y] = (r, g, b, 0)
        for xx, yy in ((x-1,y), (x+1,y), (x,y-1), (x,y+1)):
            if 0 <= xx < width and 0 <= yy < height and (xx, yy) not in seen:
                queue.append((xx, yy))
    return image


def import_sheet(root):
    from PIL import Image
    from import_ws14_cover import prepare
    path = root / SOURCE
    if not path.is_file():
        print("WS-14 original candidate sheet absent: existing book art unchanged.")
        return 0
    if hash_file(path) != SHA256:
        raise ValueError("source SHA-256 mismatch: refusing unknown art")
    with Image.open(path) as im:
        if im.format != "PNG" or im.size != (1536, 1024):
            raise ValueError("unexpected source image dimensions or encoding")
        source = im.convert("RGBA")
    generated = []
    for theme, kinds in REGIONS.items():
        for kind, region in kinds.items():
            art = remove_connected_white(source.crop(region))
            if kind == "cover":
                w, h = art.size
                canvas_h = max(h, round(w * 4/3))
                canvas_w = max(w, round(canvas_h * 3/4))
                canvas_h = round(canvas_w * 4/3)
                canvas = Image.new("RGBA", (canvas_w, canvas_h), (255,255,255,0))
                canvas.alpha_composite(art, ((canvas_w-w)//2, (canvas_h-h)//2))
                art = canvas
            dest = "design/visual-system/exports/" + theme + "-" + kind + ".png"
            generated.append((theme, kind, dest, art))

    if any((root / rel).exists() for _, _, rel, _ in generated):
        raise ValueError("candidate export already exists; no overwriting")
    for _, _, rel, image in generated:
        path = root / rel
        path.parent.mkdir(parents=True, exist_ok=True)
        image.save(path, "PNG", optimize=True, compress_level=9)

    for theme, kind, rel, image in generated:
        entry = prepare(
            root=root, theme=theme, kind=kind,
            master_rel=SOURCE, export_rel=rel,
            master_sha=SHA256, export_sha=hash_file(root / rel),
            provenance=("Original ChatGPT-generated, unwatermarked WS-14C art sheet "
                        "as debug-only test artwork. Commercial and exact-pixel "
                        "approval not yet complete."),
        )
        print("QA_SPRITE_OK", entry["asset_id"])
    return len(generated)


if __name__ == "__main__":
    n = import_sheet(Path(__file__).resolve().parents[2])
    print("WS-14 QA artwork sprites:", n)
