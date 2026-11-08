#!/usr/bin/env python3
"""Deterministic bounded 40/50 word-search GENERATOR LAB, not the Android engine.

Evaluates provisional V4 candidate supply, overlap and exact 8-way
occurrence uniqueness using Python. NO Kotlin source, saves, game modes,
scoring, production signing, or user-visible sizes are modified.
"""
from __future__ import annotations

from dataclasses import dataclass
from hashlib import sha256
from pathlib import Path
from random import Random
import json
import time

from ws09f_c_content_audit import (
    SOURCE, CANDIDATES, EXPERIMENT_TARGETS, extract_packs,
    substring_safe_pool, audit_draft_candidates,
)

FILLER = "EEEEAAAOOOIINNRRTTSSLLDDCCUUMMPPGGBBFFHHVVWWYYJKQXZ"
ALL = [(dy, dx) for dy in (-1, 0, 1) for dx in (-1, 0, 1) if dy or dx]
DIRECTIONS = {
    "relaxed": [(0, 1), (1, 0)],
    "classic": [(0, 1), (1, 0), (1, 1), (1, -1), (0, -1), (-1, 0)],
    "expert": ALL,
}
SEEDS = (11, 42, 2026)
MAX_ATTEMPTS = 24
SAMPLES_PER_WORD = 160


@dataclass
class LabPuzzle:
    grid: list[list[str]]
    words: list[str]
    paths: list[list[tuple[int, int]]]
    attempts: int
    placement_coverage: float

    def digest(self) -> str:
        raw = "\n".join("".join(row) for row in self.grid)
        return sha256(raw.encode("ascii")).hexdigest()[:16]

    def crossing_words(self) -> int:
        count = 0
        for path in self.paths:
            first = path[0]
            if any(y // 10 != first[0] // 10 or x // 10 != first[1] // 10
                   for y, x in path[1:]):
                count += 1
        return count


def count_occurrences(grid: list[list[str]], word: str) -> int:
    size = len(grid)
    count = 0
    for y in range(size):
        for x in range(size):
            if grid[y][x] != word[0]:
                continue
            for dy, dx in ALL:
                end_y, end_x = y + (len(word)-1)*dy, x + (len(word)-1)*dx
                if not (0 <= end_y < size and 0 <= end_x < size):
                    continue
                if all(grid[y+i*dy][x+i*dx] == c
                       for i, c in enumerate(word)):
                    count += 1
                    if count > 1:
                        return count
    return count


def seed_rng(theme: str, size: int, mode: str, seed: int, attempt: int) -> Random:
    label = f"ws09fc-lab-v1:{theme}:{size}:{mode}:{seed}:{attempt}"
    digest = sha256(label.encode("ascii")).digest()
    return Random(int.from_bytes(digest[:8], "big"))


def try_generate(pool: list[str], size: int, target: int, mode: str,
                 rng: Random) -> tuple[LabPuzzle | None, str]:
    options = list(pool)
    rng.shuffle(options)
    words = sorted(options[:target], key=lambda w: -len(w))
    if len(words) != target:
        return None, "short-target-supply"
    grid = [[" "] * size for _ in range(size)]
    paths: list[list[tuple[int, int]]] = []

    for word in words:
        length = len(word)
        first = None
        crossing = None
        for _ in range(SAMPLES_PER_WORD):
            dy, dx = rng.choice(DIRECTIONS[mode])
            first_y = max(0, -(length - 1) * dy)
            last_y = size - max(0, (length - 1) * dy)
            first_x = max(0, -(length - 1) * dx)
            last_x = size - max(0, (length - 1) * dx)
            if first_y >= last_y or first_x >= last_x:
                continue
            y = rng.randrange(first_y, last_y)
            x = rng.randrange(first_x, last_x)
            overlaps = 0
            fits = True
            for i, char in enumerate(word):
                existing = grid[y + i*dy][x + i*dx]
                if existing != " " and existing != char:
                    fits = False
                    break
                if existing == char:
                    overlaps += 1
            if not fits:
                continue
            path = [(y+i*dy, x+i*dx) for i in range(length)]
            if first is None:
                first = path
            if overlaps and crossing is None:
                crossing = path

        chosen = first if mode == "relaxed" else crossing or first
        if chosen is None:
            return None, f"placement-failed-{word}"
        for (y, x), letter in zip(chosen, word):
            grid[y][x] = letter
        paths.append(chosen)

    coverage = sum(letter != " " for row in grid for letter in row) / (size * size)
    for y in range(size):
        for x in range(size):
            if grid[y][x] == " ":
                grid[y][x] = rng.choice(FILLER)
    for word in words:
        if count_occurrences(grid, word) != 1:
            return None, f"nonunique-occurrence-{word}"
    return LabPuzzle(grid, words, paths, 1, coverage), "ok"


def generate(theme: str, size: int, mode: str, seed: int,
             pool: list[str]) -> LabPuzzle:
    target = EXPERIMENT_TARGETS[f"{size}x{size}"]
    failures = []
    for attempt in range(MAX_ATTEMPTS):
        rng = seed_rng(theme, size, mode, seed, attempt)
        puzzle, reason = try_generate(pool, size, target, mode, rng)
        if puzzle is not None:
            puzzle.attempts = attempt + 1
            return puzzle
        failures.append(reason)
    raise RuntimeError(f"{theme} {size}x{size} {mode} seed={seed}: "
                       f"failed after {MAX_ATTEMPTS} bounded attempts, "
                       f"last={failures[-3:]}")


def main() -> None:
    source_bytes = SOURCE.read_bytes()
    source = source_bytes.decode("utf-8")
    manifest = json.loads(CANDIDATES.read_text(encoding="utf-8"))
    audit_draft_candidates(source, source_bytes, manifest)
    original = extract_packs(source)
    print("WS-09F-C PYTHON GENERATOR LAB — NOT AN ANDROID PUZZLE")
    print("Working sizes: 40x40=70 targets, 50x50=105 targets.")
    print("Direction rules modeled; word familiarity tiers NOT yet assigned.")
    checks = []
    start = time.perf_counter()
    for theme in sorted(original):
        base_words = [word for tier in original[theme] for word in tier]
        additions = manifest["themes"][theme]["unclassified_candidates"]
        pool = substring_safe_pool(base_words + additions)
        for size in (40, 50):
            for mode in DIRECTIONS:
                puzzles = [generate(theme, size, mode, seed, pool) for seed in SEEDS]
                for puzzle in puzzles:
                    assert len(puzzle.words) == EXPERIMENT_TARGETS[f"{size}x{size}"]
                    assert len(set(puzzle.words)) == len(puzzle.words)
                    assert all(len(row) == size for row in puzzle.grid)
                    assert all(count_occurrences(puzzle.grid, w) == 1
                               for w in puzzle.words)
                first = puzzles[0]
                checks.append((theme, size, mode, first.digest()))
                print(f"{theme:<8} {size}x{size} {mode:<7}: "
                      f"{len(puzzles)}/{len(puzzles)} seeds valid, "
                      f"max_attempts={max(p.attempts for p in puzzles)}, "
                      f"placement_coverage={sum(p.placement_coverage for p in puzzles)/len(puzzles):.1%}, "
                      f"crossing_words_first={first.crossing_words()}, "
                      f"first_grid_sha={first.digest()}")
    elapsed = time.perf_counter() - start
    print(f"PASS: {len(checks)*len(SEEDS)} experimental grids, each has "
          f"the required number of uniquely occurring targets, in {elapsed:.2f}s.")
    print("No Android frame-rate, UI usability, vetted vocabulary, save, signing, "
          "or Kotlin-equivalent seeded results are claimed.")


if __name__ == "__main__":
    main()
