#!/usr/bin/env python3
"""WS-12K: controlled, source-proven, *optional* illustrated book art variant.

This is deliberately a different ART LAYER, not a new random composition per
theme. Existing paperback source geometry/motifs, sources, IDs and SHA receipts
remain untouched. One expressive but constrained botanical/field-notes detail
layer is inserted into each theme's exact motif on ALL SIX art types.

The original WS-12F 18 images must remain byte-identical. The alternate
source/preview is explicitly UNAPPROVED and isolated in debug assets.

We use the same six-piece art grammar: core emblem plus one coherent set of
background and foreground vector marks, NO embedded lettering, no external
assets, no face or trademark designs, no online generation.
"""
import argparse
import hashlib
import html
import json
from pathlib import Path
import xml.etree.ElementTree as ET

from render_family_review import SHAPES
from render_paperback import motif_markup, validate

SVG_NS = "{http://www.w3.org/2000/svg}"
BANNED = ("<text", "<script", "<foreignObject", "href=", "®", "™")
THEMES = ("woodland", "space", "garden")


def expressive_details(theme):
    """One authored layer per theme, reused at all six book sizes.

    The motifs are intentionally subtle on little badges but expressive on
    book covers: field-guide woodcut marks, small ink textures, quiet depth.
    Geometry is in the shared master 1200x1600 motif coordinate system.
    """
    a = theme["accent"]
    d = theme["accent_dark"]
    h = theme["highlight"]
    light = theme["accent_light"]
    if theme["id"] == "woodland":
        # A layered hillside behind the central pines, plus botanical ground.
        back = (
            f'<path d="M246 884L414 665L543 813L713 603L987 882V1055H246Z" '
            f'fill="{h}" opacity=".29"/>'
            f'<path d="M245 925Q410 815 564 908Q744 796 981 922" '
            f'fill="none" stroke="{d}" stroke-width="13" opacity=".32"/>'
            f'<path d="M310 875L347 769L384 875ZM814 891L863 748L910 891Z" '
            f'fill="{a}" opacity=".38"/>'
            f'<g fill="{d}" opacity=".58"><circle cx="356" cy="657" r="7"/>'
            f'<circle cx="824" cy="628" r="5"/><circle cx="425" cy="604" r="4"/></g>'
        )
        front = (
            f'<g stroke="{d}" stroke-linecap="round" fill="none" opacity=".82">'
            f'<path d="M289 1038Q364 992 443 1034M769 1049Q862 976 936 1031" stroke-width="10"/>'
            f'<path d="M340 1018l-18-35m18 35l27-43m500 35l-13-38m13 38l24-32" stroke-width="9"/>'
            f'</g><g fill="{h}" stroke="{d}" stroke-width="7">'
            f'<path d="M397 1030q-15-41 17-49q32 13 13 48Z"/>'
            f'<path d="M855 1035q-14-31 12-42q31 14 17 42Z"/>'
            f'</g><path d="M296 1068q329 23 642 0" fill="none" stroke="{a}" '
            f'opacity=".48" stroke-width="9"/>'
        )
    elif theme["id"] == "space":
        back = (
            f'<path d="M243 966Q444 723 979 561" fill="none" stroke="{h}" '
            f'stroke-width="43" opacity=".16" stroke-linecap="round"/>'
            f'<circle cx="388" cy="747" r="90" fill="{h}" opacity=".28"/>'
            f'<circle cx="916" cy="850" r="64" fill="{a}" opacity=".18"/>'
            f'<g stroke="{d}" stroke-width="7" fill="none" opacity=".48">'
            f'<path d="M293 713L367 639L432 679L502 619"/>'
            f'<path d="M778 1009L858 945L937 984"/></g>'
            f'<g fill="{d}" opacity=".68">'
            f'<circle cx="293" cy="713" r="8"/><circle cx="367" cy="639" r="8"/>'
            f'<circle cx="432" cy="679" r="8"/><circle cx="502" cy="619" r="8"/>'
            f'<circle cx="778" cy="1009" r="8"/><circle cx="858" cy="945" r="8"/>'
            f'<circle cx="937" cy="984" r="8"/></g>'
        )
        front = (
            f'<g stroke="{light}" opacity=".72" stroke-linecap="round" fill="none">'
            f'<path d="M482 844Q650 936 758 807" stroke-width="12"/>'
            f'<path d="M527 875Q635 903 709 851" stroke-width="6"/></g>'
            f'<g fill="{h}" stroke="{d}" stroke-width="5">'
            f'<circle cx="870" cy="550" r="16"/><circle cx="345" cy="940" r="13"/></g>'
            f'<g stroke="{d}" stroke-width="9" stroke-linecap="round" opacity=".8">'
            f'<path d="M911 690v58m-29-29h58M330 500v43m-21-21h42"/></g>'
        )
    elif theme["id"] == "garden":
        back = (
            f'<path d="M287 1049Q306 761 438 630M945 1032Q912 784 766 588" '
            f'fill="none" stroke="{a}" stroke-width="14" opacity=".42"/>'
            f'<g fill="{h}" opacity=".68">'
            f'<path d="M334 823q-126-53-93-132q108 14 112 121Z"/>'
            f'<path d="M380 748q-19-131 71-156q38 90-63 161Z"/>'
            f'<path d="M899 816q129-78 74-157q-103 28-95 147Z"/>'
            f'<path d="M843 741q-7-123-91-148q-39 83 95 163Z"/></g>'
            f'<g fill="{d}" opacity=".46">'
            f'<circle cx="368" cy="546" r="9"/><circle cx="818" cy="517" r="7"/>'
            f'<circle cx="888" cy="900" r="9"/></g>'
        )
        front = (
            f'<g stroke="{d}" stroke-width="8" stroke-linecap="round" '
            f'fill="none" opacity=".7">'
            f'<path d="M270 1052Q413 997 503 1038M698 1058Q850 988 958 1028"/>'
            f'<path d="M376 1033q-45-84-84-48m84 48q27-93 86-64"/>'
            f'<path d="M828 1034q-27-87-77-55m77 55q45-81 93-56"/></g>'
            f'<g fill="{h}" stroke="{d}" stroke-width="6">'
            f'<circle cx="440" cy="975" r="14"/><circle cx="774" cy="966" r="11"/>'
            f'</g><g stroke="{a}" stroke-width="7" opacity=".58">'
            f'<path d="M321 609l48-26m-22 53l44-28M828 581l-38-27"/></g>'
        )
    else:
        raise ValueError("No deliberately approved prototype detail geometry for theme")
    return back, front


def candidate_svg(original: str, theme: dict) -> str:
    a, light, dark, h = (theme[k] for k in (
        "accent", "accent_light", "accent_dark", "highlight"))
    original_motif = motif_markup(theme["motif"], a, light, dark, h)
    sentinel = "<g>" + original_motif + "</g>"
    if original.count(sentinel) != 1:
        raise ValueError("Source motif occurrence is not unique; fail closed")
    before, after = expressive_details(theme)
    candidate = original.replace(
        sentinel,
        f'<g data-study="field-notes-back">{before}</g>'
        + sentinel +
        f'<g data-study="field-notes-foreground">{after}</g>'
    )
    if any(x in candidate for x in BANNED):
        raise ValueError("Decorative SVG contains disallowed script/text/reference")
    root = ET.fromstring(candidate)
    if root.tag != SVG_NS + "svg" or list(root.iter(SVG_NS + "text")):
        raise ValueError("The study art is not a text-free SVG")
    return candidate


def compare_html(themes):
    """Two distinct six-piece families shown using actual files, not mock art."""
    panels = []
    for t in themes:
        tid = t["id"]
        title = html.escape(t["label"])
        for variant, prefix in (("Original · Quiet", ""), ("Study B · Field Notes", "alt/field-notes/")):
            icons = "".join(
                f'<figure class="{kind}"><img src="{prefix}{kind}/{tid}.svg" alt="">'
                f'<figcaption>{kind[:-1].title() if kind.endswith("s") else kind.title()}</figcaption></figure>'
                for kind in SHAPES
            )
            panels.append(
                f'<article class="panel"><p class="eyebrow">{title} · {variant}</p>'
                f'<div class="gallery">{icons}</div>'
                '<p class="foot">Unapproved · review only · native UI labels stay outside the art</p>'
                '</article>'
            )
    return """<!DOCTYPE html><html lang="en"><head><meta charset="utf-8">
<meta name="viewport" content="width=device-width,initial-scale=1">
<title>GridMates · Controlled Book Art Comparison</title>
<style>*{box-sizing:border-box}
body{margin:0;background:#F4F3EB;color:#263D36;font:15px system-ui,sans-serif}
main{max-width:1100px;margin:auto;padding:16px}
h1,h2{font-family:Georgia,serif;color:#214F3F}
h1{font-size:clamp(25px,4vw,39px);margin:4px 0}
.lead{color:#55665C;line-height:1.5}
.duo{display:grid;grid-template-columns:repeat(2,minmax(0,1fr));gap:12px}
.panel{background:#FFFDF8;border:1px solid #DFE0D6;border-radius:16px;padding:13px;min-width:0}
.eyebrow{color:#755C48;font-size:13px;font-weight:700}
.gallery{display:grid;grid-template-columns:repeat(4,minmax(0,1fr));gap:7px;align-items:end}
figure{margin:0;text-align:center;min-width:0}
figure.books{grid-column:span 3}figure.spines{grid-column:span 1}
figure img{display:block;width:100%;max-height:215px;object-fit:contain}
figure.books img{aspect-ratio:3/4}figure.spines img{height:160px}
figure.emblems img,figure.chapters img,figure.seals img{height:74px}
figure.bookmarks img{height:95px}
figcaption{color:#55665C;font-size:11px;margin-top:4px}
.foot{color:#55665C;font-size:11px}
.note{border-top:1px solid #DFE0D6;margin-top:24px;padding-top:12px;line-height:1.5}
@media(max-width:650px){.duo{grid-template-columns:1fr}}
@media(max-width:360px){main{padding:9px}.panel{padding:9px}}
</style></head><body><main>
<p class="eyebrow">WS-12K · SOURCE-CONTROLLED ART STUDY · NOT APPROVED</p>
<h1>Two ways to tell the same story</h1>
<p class="lead">Compare the exact existing quiet paperback art to an alternative
with restrained botanical or astronomical linework. Every theme uses the same
six book art locations and the same source color palette. The original files
and saved puzzle data have not been changed.</p>
<section class="duo">""" + "".join(panels) + """</section>
<p class="note">Study B only adds a controlled vector detail layer to the original
illustration. No text, real pages or buttons are baked into images.
This comparison does not approve or publish either treatment.</p>
</main></body></html>
"""


def render_study(tokens: Path, original_root: Path, destination: Path) -> dict:
    recipe = validate(json.loads(Path(tokens).read_text(encoding="utf-8")))
    original_root, destination = Path(original_root), Path(destination)
    receipt = json.loads((original_root / "render_sha256.json").read_text(encoding="utf-8"))
    if receipt.get("art_file_count") != 18 or receipt.get("owner_approved") is not False:
        raise ValueError("Requires exactly 18 original, unapproved source assets")
    files = {}
    originals = {}
    for theme in recipe["themes"]:
        if theme["id"] not in THEMES:
            raise ValueError("Art study must not silently invent new themes")
        for kind in SHAPES:
            name = f"{kind}/{theme['id']}.svg"
            original_bytes = (original_root / name).read_bytes()
            old = hashlib.sha256(original_bytes).hexdigest()
            if receipt["files"].get(name) != old:
                raise ValueError("Original image SHA changed unexpectedly: " + name)
            original = original_bytes.decode("utf-8")
            candidate = candidate_svg(original, theme)
            asset = destination / name
            asset.parent.mkdir(parents=True, exist_ok=True)
            asset.write_text(candidate, encoding="utf-8")
            files[name] = hashlib.sha256(candidate.encode("utf-8")).hexdigest()
            originals[name] = old
    report = {
        "schema_version": 1,
        "art_system": recipe["system_id"] + ".field-notes-study-b",
        "style_system": recipe["system_id"] + ".field-notes-study-b",
        "status": "PROTOTYPE_NOT_OWNER_APPROVED",
        "art_file_count": 18,
        "theme_count": 3,
        "owner_approved": False,
        "production_assets_modified": False,
        "source_original_files_unchanged": True,
        "text_baked_into_art": False,
        "files": dict(sorted(files.items())),
        "original_source_hashes": dict(sorted(originals.items()))
    }
    (destination / "render_sha256.json").write_text(
        json.dumps(report, indent=2, sort_keys=True) + "\n", encoding="utf-8"
    )
    page = compare_html(recipe["themes"])
    (original_root / "compare-studies.html").write_text(page, encoding="utf-8")
    return report


def main():
    ap = argparse.ArgumentParser(description=__doc__)
    ap.add_argument("--tokens", default="design/asset-pipeline/paperback-v1-tokens.json")
    ap.add_argument("--original-dir", required=True)
    ap.add_argument("--output-dir", required=True)
    args = ap.parse_args()
    result = render_study(
        Path(args.tokens), Path(args.original_dir), Path(args.output_dir)
    )
    print("WS-12K: preserved 18 original SVGs, created",
          result["art_file_count"], "isolated Field Notes SVG study assets")


if __name__ == "__main__":
    main()
