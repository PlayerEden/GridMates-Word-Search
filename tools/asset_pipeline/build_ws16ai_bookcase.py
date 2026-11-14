#!/usr/bin/env python3
"""WS16AI: modular illustrated woodwork derived from the approved walnut shelf.

Real painted pixels stay source-pinned. Crown, pilasters, rear planking and base
are separate Android assets; all six spine sprites are individually trimmed.
No book/lettering is embedded in the painted cabinet parts.
"""
from pathlib import Path
from PIL import Image, ImageDraw, ImageEnhance, ImageOps
import hashlib
import json

ROOT = Path(__file__).resolve().parents[2]
SHELF = ROOT / "app/src/debug/assets/ws16ab/painted-shelf-v1.webp"
BACK = ROOT / "app/src/debug/assets/ws16g/shelf/backboard.png"
OUT = ROOT / "app/src/debug/assets/ws16ai"
SHELF_SHA = "c7680df007c7fa9bab3d2ccbeefd5b12a727229db64c41ddb7dee7bc529d7895"
BACK_SHA = "89a64633d24999f233078c3da74b5b2838dbb5a0d065543c8b7b4ff0f41af3c5"

def digest(raw):
    return hashlib.sha256(raw).hexdigest()

def generate():
    assert digest(SHELF.read_bytes()) == SHELF_SHA, "Painted shelf source changed"
    assert digest(BACK.read_bytes()) == BACK_SHA, "Backboard source changed"
    shelf = Image.open(SHELF).convert("RGBA")
    back = Image.open(BACK).convert("RGBA")
    assert shelf.size == (720, 149) and back.size == (480, 360)
    OUT.mkdir(parents=True, exist_ok=True)

    # Carved walnut relief is genuine approved painted artwork, not a mockup.
    crown = ImageOps.flip(shelf).resize((720, 138), Image.Resampling.LANCZOS)
    grain = shelf.crop((132, 10, 187, 41)).rotate(90, expand=True)
    pillar = Image.new("RGBA", (72, 670), (0, 0, 0, 0))
    strip = ImageEnhance.Contrast(grain.resize((50, 595))).enhance(1.24)
    pillar.alpha_composite(strip, (11, 37))
    d = ImageDraw.Draw(pillar, "RGBA")
    d.rounded_rectangle((5, 27, 66, 641), radius=8,
                        outline=(50, 24, 12, 235), width=5)
    for x, color, width in [
        (13, (253, 181, 84, 165), 3), (19, (82, 37, 18, 125), 2),
        (57, (39, 17, 10, 205), 4), (52, (233, 148, 68, 180), 2)
    ]:
        d.line([(x, 44), (x, 628)], fill=color, width=width)
    cap = shelf.crop((1, 1, 84, 148)).resize((70, 109),
                     Image.Resampling.LANCZOS)
    pillar.alpha_composite(cap, (1, 0))
    pillar.alpha_composite(ImageOps.flip(cap), (1, 559))

    back = ImageEnhance.Brightness(back).enhance(.81)
    back = ImageEnhance.Contrast(back).enhance(1.07)
    shade = Image.new("RGBA", back.size, (0, 0, 0, 0))
    alpha = Image.new("L", (1, back.height))
    for y in range(back.height):
        alpha.putpixel((0, y), int(
            74 * max(0.0, 1 - y / 110) ** 2 +
            16 * max(0.0, (y - 290) / 70)))
    shade.putalpha(alpha.resize(back.size))
    back = Image.alpha_composite(back, shade)
    base = shelf.crop((0, 0, 720, 87)).resize((720, 78),
                       Image.Resampling.LANCZOS)
    pictures = {
        "crown.png": crown,
        "pillar-left.png": pillar,
        "pillar-right.png": ImageOps.mirror(pillar),
        "backboard.png": back,
        "base.png": base,
    }
    parts = {}
    for name, picture in pictures.items():
        p = OUT / name
        picture.save(p, "PNG", optimize=True)
        parts[name] = {"sha256": digest(p.read_bytes()), "width": picture.width,
                       "height": picture.height, "bytes": p.stat().st_size}

    spines = {}
    for name in ("woodland", "space", "garden", "linen", "night", "burgundy"):
        p = (ROOT / "app/src/debug/assets/ws16af/spines" / (name + ".png")
             if name in ("woodland", "space", "garden") else
             ROOT / "app/src/debug/assets/ws16ad/placeholders" /
                 (name + "-spine.png"))
        source = Image.open(p).convert("RGBA")
        bbox = source.getchannel("A").getbbox()
        assert bbox == (14, 51, 114, 589), (name, bbox)
        trimmed = source.crop((bbox[0]-2, bbox[1]-2, bbox[2]+2, bbox[3]+2))
        dest = OUT / "spines" / (name + ".png")
        dest.parent.mkdir(parents=True, exist_ok=True)
        trimmed.save(dest, "PNG", optimize=True)
        spines[name] = {
            "source_sha256": digest(p.read_bytes()),
            "sha256": digest(dest.read_bytes()),
            "width": trimmed.width, "height": trimmed.height,
            "playable": name in ("woodland", "space", "garden"),
        }

    manifest = {
        "schema_version": 1, "qa_only": True, "release_approved": False,
        "books_baked_into_art": False, "labels_baked_into_art": False,
        "source_approved_painted_shelf_sha256": SHELF_SHA,
        "source_original_backboard_sha256": BACK_SHA,
        "parts": parts, "spines": spines,
    }
    (OUT / "manifest.json").write_text(
        json.dumps(manifest, indent=2, sort_keys=True) + "\n")
    return manifest

if __name__ == "__main__":
    output = generate()
    print("WS16AI source-pinned cabinet:", len(output["parts"]),
          "wood parts and", len(output["spines"]), "spine images")
