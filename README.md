# SWEN90004 Assignment 2 — Schelling Segregation (Phase 1 replication)

Group: Jinzheng Xiang (1698063), Qi He (1314559), Tong Zhao (1463847).

Java SE 26-compatible replication of the NetLogo Models Library *Segregation*
model. Standard library only, no third-party dependencies.

## Layout

```
swen90004_a2/
  src/
    Main.java               # CLI dispatcher
    Simulation.java         # one model run
    ExperimentRunner.java   # parameter sweeps
    Grid.java               # 2D world (torus, Moore neighbours)
    Agent.java              # resident with colour + happiness
    Colour.java             # ORANGE / BLUE
    Position.java           # immutable (row, col) record
```

CSV output goes under `out/` by default (git-ignored).

## Build

From the repository root:

```bash
mkdir -p swen90004_a2/bin
javac -d swen90004_a2/bin swen90004_a2/src/*.java
```

This produces `.class` files in `swen90004_a2/bin/`.

## Run

All commands assume you have already built (above) and are at the repo root.
Set the classpath to the build directory:

```bash
java -cp swen90004_a2/bin Main <command> [args...]
```

### Commands

| Command           | Purpose                                    | Default args                 |
| ----------------- | ------------------------------------------ | ---------------------------- |
| `single`          | One run, writes one per-tick CSV           | `95 30 42`                   |
| `baseline`        | density=95, similar-wanted=30, replicated  | `reps=10 baseSeed=20260511`  |
| `sweep-similar`   | similar-wanted from 0 to 80 (step 10)      | `reps=10 baseSeed=20260511`  |
| `sweep-density`   | density from 50 to 95 (step 5)             | `reps=10 baseSeed=20260511`  |
| `all`             | baseline + sweep-similar + sweep-density   | `reps=10 baseSeed=20260511`  |

Examples:

```bash
# Smoke test: one run, default parameters
java -cp swen90004_a2/bin Main single

# Baseline with 30 reps, custom seed, custom output dir
java -cp swen90004_a2/bin Main baseline 30 12345 out_baseline

# Full Phase 1 sweep with default reps (10)
java -cp swen90004_a2/bin Main all
```

### Output files

Each experiment is written under `<outputDir>/<scenarioName>/`:

- `run_d<density>_s<similar>_r<rep>.csv` — per-tick metrics for one run
  (`tick, percent_similar, percent_unhappy, num_unhappy, total_moves`).
- `summary.csv` — one row per run, aggregating final state across the sweep.

## Model semantics (NetLogo conformance)

- Grid: 51 × 51 torus (NetLogo default).
- Neighbourhood: Moore (8 cells), with wrap-around.
- Happiness rule: `similar >= (%-similar-wanted / 100) * total`. Agents with
  no occupied neighbours are happy (`0 >= 0`).
- Update order per tick: identify unhappy agents → relocate each to a
  uniformly random empty cell → recompute happiness → advance tick.
- Termination: every agent happy, or `maxTicks` (default 1000) reached.

Randomness is seeded; passing the same `baseSeed` reproduces every run in a
sweep bit-exactly.
