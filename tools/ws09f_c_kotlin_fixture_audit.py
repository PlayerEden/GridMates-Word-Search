#!/usr/bin/env python3
"""Prove Kotlin lab's text fixture is EXACTLY sourced from unapproved v4 draft.

This is Python-only and needs no Gradle. It cannot prove Kotlin compilation.
"""
from __future__ import annotations

import json
from pathlib import Path
from ws09f_c_content_audit import (
    SOURCE, CANDIDATES, extract_packs, substring_safe_pool, audit_draft_candidates,
)

FIXTURE = Path("labs/ws09f_c_kotlin/candidates_test_only.txt")

def verify_fixture() -> None:
    raw = SOURCE.read_bytes()
    source = raw.decode("utf-8")
    manifest = json.loads(CANDIDATES.read_text(encoding="utf-8"))
    audit_draft_candidates(source, raw, manifest)
    expected = {}
    for theme, tiers in extract_packs(source).items():
        base = [word for tier in tiers for word in tier]
        draft = manifest["themes"][theme]["unclassified_candidates"]
        expected[theme] = substring_safe_pool(base + draft)
    actual = {}
    for line in FIXTURE.read_text(encoding="utf-8").splitlines():
        if not line or line.startswith("#"):
            continue
        if "|" not in line:
            raise ValueError("Lab fixture line is missing a theme separator")
        theme, words = line.split("|", 1)
        if theme in actual:
            raise ValueError(f"Duplicate theme: {theme}")
        actual[theme] = words.split(" ")
    if actual != expected:
        details = {theme: (len(actual.get(theme, [])), len(words))
                   for theme, words in expected.items()}
        raise ValueError(f"Lab fixture does not exactly match audited candidates: {details}")
    for theme, words in sorted(actual.items()):
        assert len(words) >= 105
        print(f"LAB-FIXTURE-PASS {theme}: {len(words)} unchanged candidate words")
    print("PASS: Kotlin fixture matches frozen v3 plus unapproved v4 candidates.")
    print("NO Kotlin compilation, approved content, playable boards or APK implied.")

if __name__ == "__main__":
    verify_fixture()
