#!/usr/bin/env python3
"""Guarantee experimental core TEST files are exact mirrors of verified lab.

No V4 generator or unapproved content enters core/src/main or app/src/main.
"""
from pathlib import Path
import hashlib

PAIRS = [
    ("labs/ws09f_c_kotlin/V4EngineLab.kt",
     "core/src/test/kotlin/ws09fc/lab/V4EngineLab.kt"),
    ("labs/ws09f_c_kotlin/candidates_test_only.txt",
     "core/src/test/resources/ws09fc_v4_test_only_candidates.txt"),
]
def main() -> None:
    for source, mirror in PAIRS:
        a, b = Path(source).read_bytes(), Path(mirror).read_bytes()
        if a != b:
            raise ValueError(f"STOP: frozen core TEST mirror drifted: {source} vs {mirror}")
        sha = hashlib.sha256(a).hexdigest()
        print(f"PASS byte-identical lab/core-test fixture: {mirror}; sha256={sha}")
    main_path = Path("core/src/main/kotlin/com/playereden/gridmates/wordsearch/core")
    assert not list(main_path.glob("*V4*")), "V4 content/engine must remain outside production core"
    assert not list(Path("app/src/main").rglob("*V4*")), "V4 must not enter the Android runtime"
    print("PASS V4 engine/candidates remain TEST-ONLY; no V4 production source added")

if __name__ == "__main__":
    main()
