#!/usr/bin/env python3
"""WS16AE V2: source-pinned complete Woodland, Space, Garden cover/spine family.

Creates actual corrected art bytes using the approved original paintings, an
individually reviewed local paper retouch on the three erroneous registered
symbols, standardized physical geometry and alpha-edge RGB decontamination.
No playable catalog IDs, save metadata or published rights are changed.
"""
from __future__ import annotations
from pathlib import Path
import io, json
from PIL import Image
from build_ws16ad_book_factory import ROOT, normalize, alpha_bbox, digest, save_png

SRC = ROOT / "app/src/debug/assets/ws14"
DEST = ROOT / "app/src/debug/assets/ws16ae"
THEMES = ("woodland", "space", "garden")

# Original PNG coordinates, determined from 1:1 manual review of each banner.
# Only these exact tiny regions are altered; not the adjacent 's' or leaves.
REGISTERED_MARK_BOX = {
    "woodland": (350, 106, 358, 117),
    "space": (326, 91, 334, 103),
    "garden": (340, 106, 348, 117),
}
SOURCE_SHA256 = {
    "woodland": {
        "cover": "1acc54d2b0f7871a855c7dc00292a02b3185e5146c22ade2da464ae31f854d59",
        "spine": "734ea2516e56c41f0bd30bc13e795971e0fa2b3733171e208300743011ddd22c"},
    "space": {
        "cover": "e0b29b725b2456e5115c6c692ceba68484893f8cd4666cdfbcceaeeb38319381",
        "spine": "1101d3ea02e48336a1e6d217dad0700c4328758955ad86967d6f1edfd115414b"},
    "garden": {
        "cover": "d90e32ecfbc3460fe3d0257d054ac494fae2da53ff1993ad10a21d4e696c52ec",
        "spine": "f8a170a897f3af59ba1909a49bb548ac67e9609d7ef1f8a2c0050e4d768a237a"},
}

def repair_banner(im: Image.Image, box: tuple[int, int, int, int]) -> Image.Image:
    """Manual registered-mark pixel repair from neighboring bare parchment.

    No Gaussian blur, global erasure, changes to letterforms or source overwrite.
    """
    source = im.convert("RGBA")
    output = source.copy()
    x0, y0, x1, y1 = box
    palette = []
    for dy in range(-16, 18):
        for dx in range(-17, 19):
            x, y = x0 + dx, y0 + dy
            if not (0 <= x < source.width and 0 <= y < source.height):
                continue
            if x0 - 3 <= x < x1 + 3 and y0 - 3 <= y < y1 + 3:
                continue
            r, g, b, a = source.getpixel((x, y))
            if a < 210 or min(r, g, b) < 130 or max(r, g, b) - min(r, g, b) > 95:
                continue
            palette.append((x, y, (r, g, b)))
    assert len(palette) > 90, ("No clean parchment around retouch mask", box)
    for y in range(y0, y1):
        for x in range(x0, x1):
            candidates = [(1.0 / ((sx - x) ** 2 + (sy - y) ** 2 + 2), color)
                          for sx, sy, color in palette]
            candidates.sort(key=lambda pair: pair[0], reverse=True)
            nearest = candidates[:90]
            normalizer = sum(weight for weight, _ in nearest)
            rgb = tuple(int(round(sum(weight * color[c] for weight, color in nearest)
                                  / normalizer)) for c in range(3))
            output.putpixel((x, y), (*rgb, source.getpixel((x, y))[3]))
    return output

def decontaminate_exterior_rgb(image: Image.Image) -> Image.Image:
    """Fix RGB only in translucent outer edges; preserve alpha and real painting."""
    result = image.convert("RGBA")
    old_img = result.copy()
    old = old_img.load()
    new = result.load()
    width, height = result.size
    for y in range(height):
        for x in range(width):
            r, g, b, a = old[x, y]
            if a == 0:
                if r or g or b:
                    new[x, y] = (0, 0, 0, 0)
                continue
            if a >= 245:
                continue
            if not any(old[nx, ny][3] == 0 for nx, ny in
                       ((x+dx, y+dy) for dx, dy in
                        ((0, 1), (0, -1), (1, 0), (-1, 0))
                        if 0 <= x+dx < width and 0 <= y+dy < height)):
                continue
            closest = None
            for dy in range(-3, 4):
                for dx in range(-3, 4):
                    xx, yy = x + dx, y + dy
                    if not (0 <= xx < width and 0 <= yy < height):
                        continue
                    rr, gg, bb, aa = old[xx, yy]
                    if aa >= 245:
                        distance = dx*dx + dy*dy
                        if closest is None or distance < closest[0]:
                            closest = (distance, (rr, gg, bb))
            if closest:
                new[x, y] = (*closest[1], a)
    return result

def generate() -> dict:
    DEST.mkdir(parents=True, exist_ok=True)
    records = []
    geometry_reference = None
    for theme in THEMES:
        for role in ("cover", "spine"):
            directory = "books" if role == "cover" else "spines"
            original = SRC / directory / (theme + ".png")
            raw = original.read_bytes()
            assert digest(raw) == SOURCE_SHA256[theme][role], (
                "Existing approved-direction painting changed", original)
            source = Image.open(io.BytesIO(raw)).convert("RGBA")
            if role == "cover":
                source = repair_banner(source, REGISTERED_MARK_BOX[theme])
            scaled, source_bbox, _ = normalize(
                source, role, geometry_reference if role == "cover" else None)
            if theme == "woodland" and role == "cover":
                geometry_reference = scaled
            finished = decontaminate_exterior_rgb(scaled)
            target = DEST / ("covers" if role == "cover" else "spines") / (theme + ".png")
            output = save_png(target, finished)
            records.append({
                "theme": theme, "role": role,
                "input": str(original.relative_to(ROOT)),
                "source_sha256": digest(raw),
                "output": str(target.relative_to(ROOT)),
                "output_sha256": digest(output),
                "size": list(finished.size),
                "visual_bounds": list(alpha_bbox(finished)),
                "original_printed_symbol_mask": list(REGISTERED_MARK_BOX[theme])
                    if role == "cover" else None,
                "owner_approved": False, "release_approved": False,
            })
    document = {
        "schema_version": 2,
        "id": "GridMates-WordSearch-WS16AE-V2-book-painting-family",
        "status": "QA_RETOUCHED_V2_OWNER_REVIEW_PENDING",
        "source_policy": "Keep original owl, planet, garden illustrations; correct header glyph and clean exterior RGB",
        "qa_only": True, "owner_approved": False, "release_approved": False,
        "source_sheet": "design/visual-system/masters/ws14-generated-asset-sheet-original.png",
        "includes_inaccurate_registered_mark": False,
        "themes": list(THEMES), "assets": records
    }
    (DEST / "manifest.json").write_text(json.dumps(document, indent=2, sort_keys=True) + "\n")
    return document

if __name__ == "__main__":
    generated = generate()
    print("WS16AE V2 painted book family:", len(generated["assets"]), "real sprites")
