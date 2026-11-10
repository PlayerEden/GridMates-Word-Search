#!/usr/bin/env python3
"""WS16AD-B: editorial spine-name sizing policy, not a runtime art resizer.

Book masters stay the same source geometry. The profile is only a future
proportional Full Library display choice; a longer label must be re-typeset
within the spine design safe area, NEVER squeezed into existing bitmap pixels.
"""
from __future__ import annotations
import unicodedata

# Owner-facing guidance, not a new limit on saved/catalog book identities.
PREFERRED_NAME_LETTERS = (4, 8)
STANDARD_MAX_LETTERS = 7
TALL_MAX_LETTERS = 11
LIBRARY_HEIGHT_DP = {"standard": 140, "tall": 148}


def spine_name_profile(name: str) -> dict:
    if not isinstance(name, str):
        raise TypeError("Spine name must be text")
    display = " ".join(unicodedata.normalize("NFC", name).split())
    if not display or any(mark in display for mark in ("®", "™", "℠")):
        raise ValueError("Empty name or unverified trademark symbol")
    letters = sum(char.isalpha() for char in display)
    word_count = len(display.split())
    if letters == 0:
        raise ValueError("Spine name must contain letters")
    if word_count == 1 and letters <= STANDARD_MAX_LETTERS:
        size_class = "standard"
    elif word_count == 1 and letters <= TALL_MAX_LETTERS:
        size_class = "tall"
    else:
        size_class = "editorial-review"
    return {
        "display_name": display,
        "letter_count": letters,
        "word_count": word_count,
        "height_class": size_class,
        "library_display_height_dp": LIBRARY_HEIGHT_DP.get(size_class),
        "editorial_review_required": size_class == "editorial-review",
        "master_geometry": "spine-128x640-v1",
    }
