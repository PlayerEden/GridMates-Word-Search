#!/usr/bin/env python3
"""Copy the twelve APPROVED Sudoku Woodland PNGs, verifying exact original bytes.

This script intentionally does not generate/modify artwork. Run after checking
out SudokuTogether at the pinned commit (see CI). Its outputs are ignored by Git
but packaged into the QA APK. Local offline builds retain monogram fallback.
"""
import argparse
import hashlib
import json
import pathlib
import shutil
import sys


def main() -> int:
    parser = argparse.ArgumentParser()
    parser.add_argument("--source", required=True, type=pathlib.Path)
    parser.add_argument("--manifest", type=pathlib.Path,
                        default=pathlib.Path("design/woodland/approved-portrait-reference.json"))
    parser.add_argument("--destination", type=pathlib.Path,
                        default=pathlib.Path("app/src/main/res/drawable-nodpi"))
    args = parser.parse_args()

    ref = json.loads(args.manifest.read_text(encoding="utf-8"))
    assets = ref["assets"]
    if len(assets) != 12 or len({x["id"] for x in assets}) != 12:
        raise ValueError("Expected exactly twelve unique original Woodland portraits")

    source = args.source.resolve()
    output = args.destination.resolve()
    expected = {f"woodland_{x}.png" for x in (
        "owl", "fox", "rabbit", "bear", "badger", "hedgehog",
        "robin", "deer", "squirrel", "otter", "tortoise", "cat")}
    actual = {pathlib.Path(x["resource"]).name for x in assets}
    if actual != expected:
        raise ValueError("Manifest resource names don't match approved set")

    # Verify all assets before writing any resource: fail closed on a mismatch.
    verified = []
    for entry in assets:
        relative = pathlib.Path(entry["resource"])
        if relative.is_absolute() or ".." in relative.parts:
            raise ValueError(f"Invalid path {relative}")
        original = (source / relative).resolve()
        if not original.is_relative_to(source):
            raise ValueError(f"Source escaped checkout: {original}")
        raw = original.read_bytes()
        if not raw.startswith(b"\x89PNG\r\n\x1a\n"):
            raise ValueError(f"Not an actual PNG: {relative}")
        digest = hashlib.sha256(raw).hexdigest()
        if digest != entry["resourceSha256"] or len(raw) != entry["bytes"]:
            raise ValueError(f"Approved source mismatch: {entry['id']}")
        verified.append((original, relative.name, digest, len(raw)))

    output.mkdir(parents=True, exist_ok=True)
    for source_file, filename, digest, size in verified:
        target = output / filename
        shutil.copyfile(source_file, target)
        if hashlib.sha256(target.read_bytes()).hexdigest() != digest:
            raise ValueError(f"Copy corrupted {filename}")
        print(f"APPROVED_ART_OK {filename} {size} SHA256={digest}")
    print(f"VERIFIED_WOODLAND_COUNT={len(verified)}")
    return 0


if __name__ == "__main__":
    sys.exit(main())
