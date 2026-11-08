#!/usr/bin/env python3
"""WS-12C: deterministic, offline, scalable GridMates book-art prototyper.

All decorations use simple human-controlled SVG geometry and an immutable layout
recipe, so adding a theme only changes token values and its approved motif.
The PNGs/PDFs/UI are not auto-created or published, and no approved Android art
or existing collectible is modified. Generated title text is NOT baked into art.

Run:
  python3 tools/asset_pipeline/render_paperback.py \
    --tokens design/asset-pipeline/paperback-v1-tokens.json \
    --output-dir /tmp/ws12-paperback-preview

Open /tmp/ws12-paperback-preview/index.html to review the actual semantic HTML
book titles over decorative SVG images. Not a production Android screen.
"""
import argparse
import hashlib
import html
import json
from pathlib import Path
import re

HEX = re.compile(r"#[0-9A-Fa-f]{6}\Z")
SAFE_ID = re.compile(r"[a-z][a-z0-9-]{2,32}\Z")
PALETTE = {
    "cream": "#F4F3EB", "paper": "#FFFDF8",
    "forest": "#214F3F", "ink": "#263D36",
    "gold": "#8A6729", "muted": "#55665C",
    "rule": "#DFE0D6"
}
MOTIFS = {"pine-grove", "orbit", "flower-stem"}


def validate(recipe):
    if recipe.get("schema_version") != 1 or recipe.get("system_id") != "gridmates-ws.paperback-illustrated.v1":
        raise ValueError("Unsupported style system; create a new version for changes")
    if recipe.get("status") != "PROTOTYPE_STYLE_NOT_APPROVED_FOR_ANDROID":
        raise ValueError("Prototype recipe cannot claim app approval")
    if recipe.get("brand") != PALETTE:
        raise ValueError("Brand palette changed relative to the actual Compose theme")
    cover = recipe.get("cover", {})
    if (cover.get("width_px"), cover.get("height_px")) != (1200, 1600):
        raise ValueError("Paperbacks must share the 3:4 1200x1600 layout")
    if cover.get("rendered_words_baked_into_art") is not False or cover.get("text_is_native_ui") is not True:
        raise ValueError("Book titles and game progress must remain native text")
    rules = recipe.get("style_rules", {})
    if not rules or not all(rules.get(k) is True for k in
            ("no_ornate_gold_frames", "no_3d_embossed_or_fake_leather",
             "no_text_in_decorative_art", "all_book_ui_text_rendered_in_app",
             "reuse_same_composition_for_each_theme",
             "vector_native_fallback_for_missing_art")):
        raise ValueError("Required accessibility/reproducibility principles missing")
    themes = recipe.get("themes", [])
    if not isinstance(themes, list) or len(themes) < 3:
        raise ValueError("Need three source-tracked pilot themes")
    seen = set()
    for x in themes:
        if not SAFE_ID.fullmatch(x.get("id", "")) or x["id"] in seen:
            raise ValueError("Duplicate/invalid theme ID")
        seen.add(x["id"])
        if x.get("motif") not in MOTIFS:
            raise ValueError("Unknown drawing motif. Explicitly author/review it.")
        if not x.get("label", "").strip() or any(ch in x["label"] for ch in "<>&®™"):
            raise ValueError("Invalid or legal-claim-containing book title")
        if x.get("volume") != "I":
            raise ValueError("Pilot is volume I only; version other titles separately")
        for color in ("accent", "accent_light", "accent_dark", "highlight"):
            if not HEX.fullmatch(x.get(color, "")):
                raise ValueError("Invalid theme color")
    if {x["id"] for x in themes[:3]} != {"woodland", "space", "garden"}:
        raise ValueError("Three pilot theme identities must be preserved")
    return recipe


def motif_markup(motif, accent, light, dark, highlight):
    """A small editable motif catalogue rather than a new unpredictable generated image."""
    if motif == "pine-grove":
        return f"""<circle cx="871" cy="544" r="78" fill="{highlight}" opacity=".55"/>
<path d="M218 1030 Q422 923 618 1022 Q810 910 1010 1017 V1125 H218Z" fill="{highlight}" opacity=".45"/>
<path d="M494 955V1090 M708 984V1090 M595 909V1092" stroke="{dark}" stroke-width="24" stroke-linecap="round"/>
<path d="M492 555L353 927H631Z" fill="{accent}" opacity=".85"/>
<path d="M706 627L578 963H834Z" fill="{accent}" opacity=".70"/>
<path d="M597 463L422 919H773Z" fill="{dark}"/>
<path d="M597 548L473 871H722Z" fill="{accent}"/>
<path d="M300 982Q601 942 914 982" fill="none" stroke="{dark}" stroke-width="9" stroke-linecap="round"/>"""
    if motif == "orbit":
        return f"""<circle cx="604" cy="766" r="235" fill="{accent}"/>
<circle cx="538" cy="677" r="112" fill="{highlight}" opacity=".40"/>
<path d="M418 655Q516 555 667 568" fill="none" stroke="{light}" opacity=".6" stroke-width="20" stroke-linecap="round"/>
<ellipse cx="604" cy="766" rx="387" ry="115" transform="rotate(-23 604 766)"
 fill="none" stroke="{dark}" stroke-width="26"/>
<ellipse cx="604" cy="766" rx="390" ry="121" transform="rotate(-23 604 766)"
 fill="none" stroke="{highlight}" stroke-width="9"/>
<g stroke="{dark}" stroke-width="13" stroke-linecap="round">
 <path d="M288 554v69m-34-35h68M880 925v69m-34-35h68M804 475v46m-23-23h46"/>
</g>
<circle cx="325" cy="933" r="11" fill="{accent}"/>
<circle cx="930" cy="626" r="10" fill="{accent}"/>"""
    if motif == "flower-stem":
        return f"""<path d="M602 835Q548 975 598 1076" fill="none" stroke="{dark}" stroke-width="24" stroke-linecap="round"/>
<path d="M576 925Q424 838 416 944Q473 1014 584 973Z" fill="{accent}"/>
<path d="M598 979Q733 853 790 944Q743 1040 595 1016Z" fill="{accent}"/>
<path d="M515 727Q446 650 509 592Q577 600 598 681
 Q603 563 683 587Q729 653 650 724
 Q770 707 789 786Q758 864 647 811
 Q695 935 616 957Q539 933 566 814
 Q456 860 422 786Q427 711 515 727Z"
 fill="{highlight}" stroke="{dark}" stroke-width="8" stroke-linejoin="round"/>
<circle cx="601" cy="764" r="92" fill="{accent}"/>
<circle cx="601" cy="764" r="54" fill="{dark}"/>
<g fill="{accent}" opacity=".45"><circle cx="321" cy="557" r="26"/><circle cx="860" cy="563" r="34"/><circle cx="880" cy="985" r="21"/></g>"""
    raise ValueError("Unregistered motif")


def cover_svg(theme):
    a, light, dark, highlight = (theme[k] for k in
        ("accent", "accent_light", "accent_dark", "highlight"))
    motif = motif_markup(theme["motif"], a, light, dark, highlight)
    # Shared card template is immutable. No text: HTML/Compose supplies it.
    return f"""<svg xmlns="http://www.w3.org/2000/svg" width="1200" height="1600"
 viewBox="0 0 1200 1600" role="img">
<rect x="22" y="22" width="1156" height="1556" rx="48" fill="{PALETTE['paper']}"
 stroke="{PALETTE['rule']}" stroke-width="12"/>
<path d="M98 22V1578" stroke="{a}" stroke-width="58"/>
<path d="M105 74V1530" stroke="{light}" stroke-width="4" opacity=".8"/>
<rect x="169" y="388" width="880" height="760" rx="36" fill="{light}"/>
<g>{motif}</g>
<path d="M183 352H1030 M183 1196H1030" stroke="{PALETTE['rule']}"
 stroke-width="9" stroke-linecap="round"/>
<path d="M183 1444H1030" stroke="{a}" stroke-width="13" stroke-linecap="round" opacity=".65"/>
<circle cx="189" cy="128" r="12" fill="{PALETTE['gold']}"/>
<circle cx="1026" cy="128" r="12" fill="{PALETTE['gold']}"/>
</svg>
"""


def emblem_svg(theme):
    a,light,dark,highlight = (theme[k] for k in
        ("accent", "accent_light", "accent_dark", "highlight"))
    motif=motif_markup(theme["motif"],a,light,dark,highlight)
    # Crop symbol to an abstract one-motif circle. No text or fixed badge words.
    return f"""<svg xmlns="http://www.w3.org/2000/svg" width="300" height="300"
 viewBox="0 0 300 300" role="img">
<circle cx="150" cy="150" r="143" fill="{light}" stroke="{a}" stroke-width="12"/>
<svg x="42" y="48" width="216" height="208" viewBox="240 440 730 680"
 preserveAspectRatio="xMidYMid meet"><g>{motif}</g></svg>
</svg>
"""


def page(themes):
    books=[]
    for t in themes:
        id_=t["id"]
        title=html.escape(t["label"])
        books.append(f"""<article class="book" role="group" aria-label="{title}, Volume I">
  <img src="books/{id_}.svg" alt="" aria-hidden="true">
  <div class="series"><div>GridMates</div><span>WORD SEARCH</span></div>
  <h2>{title}</h2>
  <p class="volume">VOLUME I</p>
</article>""")
    return """<!doctype html><html lang="en"><meta charset="utf-8">
<meta name="viewport" content="width=device-width,initial-scale=1">
<title>GridMates — Paperback System v1 design proof</title>
<style>
:root{--paper:#FFFDF8;--cream:#F4F3EB;--forest:#214F3F;--ink:#263D36;--gold:#8A6729}
*{box-sizing:border-box}
body{margin:0;background:var(--cream);color:var(--ink);font-family:system-ui,sans-serif}
main{max-width:1120px;margin:0 auto;padding:24px}
h1{font-family:Georgia,serif;font-size:clamp(24px,4vw,34px);color:var(--forest);margin:0 0 8px}
p.intro{font-size:14px;line-height:1.6;color:#55665C;max-width:62ch}
.shelf{display:grid;grid-template-columns:repeat(3,minmax(0,1fr));gap:clamp(10px,3vw,26px);padding:26px 0}
.book{aspect-ratio:3/4;position:relative;min-width:0;filter:drop-shadow(0 6px 7px #263D3624)}
.book img{width:100%;height:100%;display:block}
.series{position:absolute;left:16%;right:7%;top:8%;text-align:center;color:var(--forest)}
.series div{font-family:Georgia,serif;font-weight:700;font-size:clamp(12px,2.2vw,27px)}
.series span{display:block;font-size:clamp(5px,.9vw,11px);letter-spacing:.12em;font-weight:700}
.book h2{position:absolute;left:15%;right:7%;top:77%;text-align:center;
 margin:0;font-family:Georgia,serif;font-size:clamp(12px,2.1vw,30px);color:var(--forest)}
.volume{position:absolute;left:15%;right:7%;bottom:5%;text-align:center;
 margin:0;font-size:clamp(7px,1.1vw,14px);letter-spacing:.09em;color:var(--gold);font-weight:700}
@media(max-width:420px){main{padding:12px}.shelf{gap:6px}.book h2{font-size:clamp(10px,3vw,13px)}}
.note{border-top:1px solid #DFE0D6;padding-top:16px;font-size:13px;color:#55665C}
</style>
<main><h1>GridMates — Quiet Paperbacks v1</h1>
<p class="intro">Prototype only. Same paper, proportions, lettering zones and motifs;
the theme changes through controlled vector artwork and four color tokens.
Titles are real editable HTML text, not baked into generated art.</p>
<section class="shelf">"""+"".join(books)+"""</section>
<p class="note">Not approved, not Adobe-refined, not shipped. This HTML is a
visual style proof; Android should use native Compose titles and status labels.</p>
</main></html>"""


def render(tokens, output):
    data=validate(json.loads(Path(tokens).read_text(encoding="utf8")))
    output=Path(output)
    (output/"books").mkdir(parents=True,exist_ok=True)
    (output/"emblems").mkdir(parents=True,exist_ok=True)
    files={}
    for t in data["themes"]:
        for prefix, name, fn in (
            ("books", t["id"]+".svg", cover_svg),
            ("emblems", t["id"]+".svg", emblem_svg)
        ):
            text=fn(t)
            if "<text" in text or "®" in text or "™" in text:
                raise ValueError("Decorative SVG must never embed game titles/legal marks")
            target=output/prefix/name
            target.write_text(text,encoding="utf8")
            files[str(target.relative_to(output))]=hashlib.sha256(text.encode("utf8")).hexdigest()
    html_text=page(data["themes"])
    (output/"index.html").write_text(html_text,encoding="utf8")
    files["index.html"]=hashlib.sha256(html_text.encode("utf8")).hexdigest()
    report={
        "system_id":data["system_id"],"status":data["status"],
        "theme_count":len(data["themes"]),"all_cover_geometry_same":True,
        "all_art_vector_only":True,"baked_text_images":0,
        "adobe_refined":False,"owner_approved":False,
        "production_app_assets_modified":False,
        "files":dict(sorted(files.items()))
    }
    (output/"render_sha256.json").write_text(
        json.dumps(report,indent=2,sort_keys=True)+"\n",encoding="utf8")
    return report


def main():
    parser=argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--tokens",default="design/asset-pipeline/paperback-v1-tokens.json")
    parser.add_argument("--output-dir",required=True)
    a=parser.parse_args()
    print(json.dumps(render(a.tokens,a.output_dir),indent=2,sort_keys=True))


if __name__=="__main__":
    main()
