#!/usr/bin/env python3
"""WS-12F — build a REAL, cohesive 3-theme book-art review pack.

Reuses exactly the original WS-12C cover/emblem renderer and WS-12D
chapter/bookmark/seal renderer, then adds only the absent 3 spine accents.
No AI model is invoked at runtime, no Android resources or approved assets
are modified, and all displayed words are real HTML, never baked SVG text.
"""
import argparse
import hashlib
import html
import json
from pathlib import Path
import shutil
import xml.etree.ElementTree as ET

from render_paperback import PALETTE, motif_markup, render, validate
from render_paperback_accents import render_accents

SHAPES = ("books", "spines", "emblems", "chapters", "bookmarks", "seals")
SVG_NS = "{http://www.w3.org/2000/svg}"
BANNED = ("<text", "<script", "<foreignObject", "href=", "®", "™")


def spine_svg(theme):
    """A new slim paperback spine derived from the very same motif geometry."""
    a, light, dark, highlight = (
        theme["accent"], theme["accent_light"],
        theme["accent_dark"], theme["highlight"]
    )
    motif = motif_markup(theme["motif"], a, light, dark, highlight)
    return (
        '<svg xmlns="http://www.w3.org/2000/svg" width="160" height="920" '
        'viewBox="0 0 160 920" aria-hidden="true">'
        f'<rect x="4" y="4" width="152" height="912" rx="22" '
        f'fill="{PALETTE["paper"]}" stroke="{PALETTE["rule"]}" stroke-width="6"/>'
        f'<rect x="15" y="15" width="130" height="890" rx="16" fill="{light}"/>'
        f'<path d="M28 47V873M132 47V873" stroke="{a}" stroke-width="5" '
        'stroke-linecap="round"/>'
        f'<path d="M45 88H115M45 117H115M45 804H115M45 833H115" '
        f'stroke="{dark}" stroke-width="6" stroke-linecap="round"/>'
        '<svg x="21" y="322" width="118" height="266" '
        'viewBox="240 440 730 680" preserveAspectRatio="xMidYMid meet">'
        f'<g>{motif}</g></svg>'
        f'<circle cx="80" cy="228" r="16" fill="{highlight}" '
        f'stroke="{a}" stroke-width="4"/>'
        f'<circle cx="80" cy="697" r="16" fill="{highlight}" '
        f'stroke="{a}" stroke-width="4"/>'
        '</svg>'
    )


def review_html(themes):
    """Compact art-first phone gallery; deliberately not a fake playable UI."""
    cards = []
    for t in themes:
        theme_id, title = t["id"], html.escape(t["label"])
        assets = [
            ("Cover", f"books/{theme_id}.svg", "cover"),
            ("Spine", f"spines/{theme_id}.svg", "spine"),
            ("Emblem", f"emblems/{theme_id}.svg", "small"),
            ("Chapter", f"chapters/{theme_id}.svg", "small"),
            ("Bookmark", f"bookmarks/{theme_id}.svg", "spine"),
            ("Completion", f"seals/{theme_id}.svg", "small"),
        ]
        cells = "".join(
            f'<figure class="asset {cls}"><img src="{path}" '
            f'alt="{title} {label.lower()} ornament, prototype only">'
            f'<figcaption>{label}</figcaption></figure>'
            for label, path, cls in assets
        )
        cards.append(
            f'<article class="volume">'
            f'<p class="eyebrow">PUZZLE BOOK · ART STUDY</p>'
            f'<h2>{title}</h2>'
            f'<p class="subtitle">Volume I · Six matching pieces</p>'
            f'<div class="art">{cells}</div>'
            f'<div class="card-bottom"><span>{title} · Volume I</span>'
            f'<span class="state">Prototype artwork</span></div>'
            '</article>'
        )
    return """<!doctype html><html lang="en"><head>
<meta charset="utf-8"><meta name="viewport" content="width=device-width, initial-scale=1">
<title>GridMates Word Search · Three-Family Art Study</title>
<style>
*{box-sizing:border-box}
body{margin:0;background:#F4F3EB;color:#263D36;font:15px/1.38 system-ui,sans-serif}
main{max-width:1180px;margin:0 auto;padding:16px}
h1,h2{font-family:Georgia,serif;color:#214F3F}
h1{font-size:clamp(23px,3vw,33px);margin:5px 0 6px}
h2{font-size:25px;margin:5px 0 3px}
.eyebrow{color:#55665C;font-size:11px;font-weight:700;letter-spacing:.09em;margin:0}
.intro{margin:0 0 16px;color:#55665C;max-width:720px}
.shelf{display:grid;grid-template-columns:repeat(3,minmax(0,1fr));gap:15px}
.volume{min-width:0;background:#FFFDF8;border:1px solid #DFE0D6;
border-radius:18px;padding:16px;box-shadow:0 2px 6px #263d3612}
.subtitle{color:#55665C;font-size:13px;margin:2px 0}
.art{display:grid;grid-template-columns:repeat(5,minmax(0,1fr));
gap:6px;align-items:end;margin:14px 0}
figure{margin:0;text-align:center;min-width:0}
.asset.cover{grid-column:span 3}
.asset.spine{grid-column:span 1}
.asset img{width:100%;display:block;max-height:212px;object-fit:contain}
.asset.cover img{aspect-ratio:3/4}.asset.spine img{height:160px}
.asset.small img{height:62px}
.asset figcaption{font-size:11px;margin-top:4px;color:#55665C}
.card-bottom{border-top:1px solid #DFE0D6;padding-top:12px;
display:flex;gap:8px;justify-content:space-between;align-items:center;
color:#263D36;font-size:13px;font-weight:600;flex-wrap:wrap}
.state{font-size:11px;color:#55665C;font-weight:500}
.note{margin-top:18px;border-top:1px solid #DFE0D6;padding:12px 2px;
font-size:13px;color:#55665C;line-height:1.5}
.note summary{cursor:pointer;color:#214F3F;font-weight:600}
.note p{max-width:750px}
@media(max-width:900px){.shelf{grid-template-columns:1fr}
.volume{max-width:600px;margin:auto;width:100%}
.asset img{max-height:245px}.asset.cover img{max-height:245px}}
@media(max-width:420px){main{padding:12px}
.shelf{grid-template-columns:1fr;gap:13px}
.volume{padding:14px}.art{gap:4px;margin:11px 0}
.asset figcaption{font-size:10px}
h1{font-size:25px}h2{font-size:24px}}
@media(prefers-reduced-motion:reduce){*{animation:none!important;transition:none!important}}
</style></head><body><main>
<p class="eyebrow">GRIDMATES · WORD SEARCH · ART LAB</p>
<h1>Library artwork</h1>
<p class="intro"><strong>Unapproved concept preview.</strong>
Compare Woodland, Space and Garden together. This is a visual study, not a playable book.</p>
<section class="shelf">""" + "".join(cards) + """</section>
<details class="note"><summary>About these artwork prototypes</summary>
<p>These six-piece book families use one shared paperback geometry, existing
source-controlled motifs and native paper/ink colors. All labels and progress
remain editable UI text, not lettering baked into artwork. The illustrations
do not replace any existing game screens or original Woodland portraits.
Only an explicit owner review can approve an asset for the finished app.</p>
</details>
</main></body></html>
"""


def render_family(tokens, output):
    """Generates a review pack with all 18 actual SVG asset files + receipts."""
    recipe = validate(json.loads(Path(tokens).read_text(encoding="utf-8")))
    output = Path(output)
    output.mkdir(parents=True, exist_ok=True)
    old_books = render(tokens, output / "_original_books")
    old_accents = render_accents(tokens, output / "_original_accents")
    hashes = {}
    for t in recipe["themes"]:
        tid = t["id"]
        for typ in SHAPES:
            src_dir = (output / "_original_books" if typ in ("books", "emblems")
                       else output / "_original_accents")
            dest = output / typ / f"{tid}.svg"
            dest.parent.mkdir(parents=True, exist_ok=True)
            if typ == "spines":
                source = spine_svg(t)
                dest.write_text(source, encoding="utf-8")
            else:
                shutil.copyfile(src_dir / typ / f"{tid}.svg", dest)
                source = dest.read_text(encoding="utf-8")
            if any(pattern in source for pattern in BANNED):
                raise ValueError(f"Disallowed text or external reference in {typ}/{tid}")
            tree = ET.fromstring(source)
            if tree.tag != SVG_NS + "svg" or list(tree.iter(SVG_NS + "text")):
                raise ValueError(f"{typ}/{tid} contains wrong or embedded UI content")
            hashes[f"{typ}/{tid}.svg"] = hashlib.sha256(source.encode("utf-8")).hexdigest()
    # Identity receipts for old assets must be identical, not 'almost the same'.
    for name, digest in old_books["files"].items():
        if name != "index.html" and hashes[name] != digest:
            raise ValueError(f"Existing WS-12C source mutated: {name}")
    for name, digest in old_accents["files"].items():
        if hashes[name] != digest:
            raise ValueError(f"Existing WS-12D source mutated: {name}")
    page = review_html(recipe["themes"])
    (output / "review.html").write_text(page, encoding="utf-8")
    hashes["review.html"] = hashlib.sha256(page.encode("utf-8")).hexdigest()
    report = {
        "style_system": recipe["system_id"],
        "status": "PROTOTYPE_NOT_OWNER_APPROVED",
        "theme_count": len(recipe["themes"]),
        "book_asset_types": list(SHAPES),
        "art_file_count": len(hashes) - 1,
        "text_baked_into_art": False,
        "owner_approved": False,
        "adobe_refined": False,
        "android_resources_modified": False,
        "existing_cover_hashes_unchanged": True,
        "existing_companion_hashes_unchanged": True,
        "files": dict(sorted(hashes.items())),
    }
    (output / "render_sha256.json").write_text(
        json.dumps(report, sort_keys=True, indent=2) + "\n", encoding="utf-8"
    )
    # Do not accidentally pack internal second copies in the review artifact.
    shutil.rmtree(output / "_original_books")
    shutil.rmtree(output / "_original_accents")
    return report


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--tokens", default="design/asset-pipeline/paperback-v1-tokens.json")
    parser.add_argument("--output-dir", required=True)
    args = parser.parse_args()
    print(json.dumps(render_family(args.tokens, args.output_dir), indent=2, sort_keys=True))


if __name__ == "__main__":
    main()
