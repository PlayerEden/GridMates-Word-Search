#!/usr/bin/env python3
"""WS16BF: clean anti-aliased physical spine WITHOUT a second backing plate.

Source is the preserved source-pinned WS16AI 104x542 artwork. This QA-only
render removes detached edge-colored specks and paints a single rolled-light
leather surface. Book text and theme illustrations are never stretched.
"""
from __future__ import annotations
import hashlib
import io
import json
import math
from pathlib import Path
from PIL import Image, ImageDraw, ImageFilter

ROOT = Path(__file__).resolve().parents[2]
SRC = ROOT / "app/src/debug/assets/ws16ai/spines"
PRIOR = ROOT / "app/src/debug/assets/ws16ai/manifest.json"
DEST = ROOT / "app/src/debug/assets/ws16bf"
REAL = ("woodland", "space", "garden")
BASE = {
    "woodland": (4, 31, 17),
    "space": (4, 17, 45),
    "garden": (6, 36, 11),
}
W, H = 104, 542

def sha(raw: bytes) -> str:
    return hashlib.sha256(raw).hexdigest()

def make(name: str, source: Image.Image) -> Image.Image:
    assert name in REAL and source.size == (W, H)
    original = source.convert("RGBA")
    output = Image.new("RGBA", (W, H), (0, 0, 0, 0))

    # One rounded silhouette, NOT a wider rectangular leather underlay.
    mask = Image.new("L", (W, H), 0)
    ImageDraw.Draw(mask).rounded_rectangle((3, 2, 100, 539), radius=12, fill=255)
    mask = mask.filter(ImageFilter.GaussianBlur(0.5))
    alphas, pixels, target = mask.load(), original.load(), output.load()

    for y in range(2, 540):
        cap = max(0.0, min(1.0, (y - 6) / 5.0)) * max(
            0.0, min(1.0, (535 - y) / 5.0)
        )
        for x in range(3, 101):
            alpha = alphas[x, y]
            if alpha < 8:
                continue
            r, g, b, src_alpha = pixels[x, y]
            if src_alpha < 128:
                r, g, b = BASE[name]
            # A consistent dark leather rim replaces noisy semi-transparent
            # source fringes. The lettered center still uses original pixels.
            left = max(0.0, min(1.0, (x - 4) / 8.0))
            right = max(0.0, min(1.0, (99 - x) / 8.0))
            blend = left * right * cap if src_alpha >= 128 else 0.0
            base = BASE[name]
            colors = [
                base[i] * (1.0 - blend) + rgb * blend
                for i, rgb in enumerate((r, g, b))
            ]
            # Soft convex roll-off across this SAME silhouette: stronger left
            # rim, warm specular shoulder and a shaded right edge.
            left_roll = max(0.0, min(1.0, (x - 3) / 17.0))
            right_roll = max(0.0, min(1.0, (100 - x) / 18.0))
            curvature = (0.62 + 0.41 * left_roll) * (0.69 + 0.31 * right_roll)
            highlight = 0.11 * math.exp(-((x - 18) / 6.0) ** 2)
            factor = curvature + highlight
            target[x, y] = (
                *[max(0, min(255, int(c * factor))) for c in colors],
                alpha,
            )
    return output

def generate() -> dict:
    prior = json.loads(PRIOR.read_text())
    assert prior["qa_only"] is True and prior["release_approved"] is False
    assets = []
    for name in REAL:
        path = SRC / (name + ".png")
        raw = path.read_bytes()
        assert sha(raw) == prior["spines"][name]["sha256"], name
        art = make(name, Image.open(io.BytesIO(raw)))
        output = DEST / "spines" / (name + ".png")
        output.parent.mkdir(parents=True, exist_ok=True)
        buf = io.BytesIO()
        art.save(buf, format="PNG", optimize=True, compress_level=9)
        png = buf.getvalue()
        output.write_bytes(png)
        assets.append({
            "name": name, "role": "playable-spine", "width": W, "height": H,
            "source": str(path.relative_to(ROOT)), "source_sha256": sha(raw),
            "output": str(output.relative_to(ROOT)), "sha256": sha(png),
        })
    manifest = {
        "schema_version": 1, "id": "ws16bf.single-curved-binding.v1",
        "qa_only": True, "release_approved": False, "owner_approved": False,
        "source_unchanged": True, "new_backing_panel": False,
        "assets": assets,
    }
    DEST.mkdir(parents=True, exist_ok=True)
    (DEST / "manifest.json").write_text(
        json.dumps(manifest, sort_keys=True, indent=2) + "\n"
    )
    print("WS16BF: cleaned 3 source-pinned curved spines")
    return manifest

if __name__ == "__main__":
    generate()
