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

The source uses no features above Java 21, so it compiles cleanly on any
JDK ≥ 21. The assignment targets **Java SE 26**; to verify against the
exact target release explicitly, use a JDK 26 install:

```bash
JDK26=/path/to/jdk-26/Contents/Home
$JDK26/bin/javac --release 26 -d swen90004_a2/bin swen90004_a2/src/*.java
```

Seeded output is bit-identical between Java 21+ and Java 26.

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

- **Grid**: 51 × 51 torus (NetLogo default).
- **Neighbourhood**: Moore (8 cells), with wrap-around.
- **Happiness rule**: `similar >= (%-similar-wanted / 100) * total`. Agents
  with no occupied neighbours are happy (`0 >= 0`).
- **Aggregate metric**: `percent_similar = sum(similar_i) / sum(total_i) * 100`,
  matching NetLogo's `update-globals` (degree-weighted, not a mean of ratios).
- **Move policy**: NetLogo's recursive `find-new-spot` — each unhappy agent
  turns a uniformly random heading and steps forward `random-float 10`
  patches, repeating from the new continuous position until it lands on a
  patch with no other agent.
- **Update order per tick**: identify unhappy agents → relocate each (in
  random order) via the walk → recompute happiness for all agents → advance
  tick.
- **Termination**: every agent happy, or `maxTicks` (default 1000) reached.

Randomness is seeded; passing the same `baseSeed` reproduces every run in a
sweep bit-exactly.

## Phase 1 results summary

The numbers below come from `java Main all` with the default settings
(`reps=10`, `baseSeed=20260511`); see the three `summary.csv` files under
`out/` for the raw data.

### Baseline — density = 95, %-similar-wanted = 30, 10 reps

| metric                | value                       |
| --------------------- | --------------------------- |
| Final %similar (mean) | **75.38%** (SD 1.05)        |
| Convergence ticks     | 20.3 (range 13–32)          |
| Converged             | 10 / 10                     |

The mean lands squarely inside NetLogo's reference range (~74–76%),
confirming faithful replication at the canonical parameters.

### Sweep — %-similar-wanted ∈ {0, 10, …, 80} at density 95

| %-similar-wanted | mean final %similar | converged |
| ---------------: | ------------------: | :-------: |
| 0                | 49.90%              | 10/10     |
| 10               | 50.30%              | 10/10     |
| 20               | 55.67%              | 10/10     |
| **30**           | **75.38%**          | 10/10     |
| 40               | 83.71%              | 10/10     |
| 50               | 86.72%              | 10/10     |
| 60               | 98.88%              | 8/10      |
| 70               | 98.88%              | 2/10      |
| **80**           | **51.04%**          | **0/10**  |

Two qualitatively distinct features emerge:

- **Schelling's classic finding**: a modest preference (30%) yields strong
  segregation (~75%). Increasing tolerance past 30% rapidly drives the
  system toward near-complete segregation.
- **Thrashing regime at 80%**: no equilibrium exists, so agents keep moving
  for the full 1000-tick cap; ~94% remain unhappy at all times and the
  instantaneous %similar stays near the random baseline (~50%). This
  matches the "model never finishes" behaviour described in the NetLogo
  model documentation.

### Sweep — density ∈ {50, 55, …, 95} at %-similar-wanted = 30

| density | mean final %similar | mean conv. ticks | converged |
| ------: | ------------------: | ---------------: | :-------: |
| 50      | 74.39%              | 10.7             | 10/10     |
| 65      | 72.18%              | 11.6             | 10/10     |
| 80      | 72.30%              | 14.1             | 10/10     |
| 95      | 75.38%              | 20.3             | 10/10     |

At a moderate tolerance, density has almost no effect on the equilibrium
segregation level (range: 71.6%–75.4%, SD ≈ 1.2 at every density) but
roughly **doubles** the convergence time as density goes from 50% to 95%,
because each unhappy agent's local walk has fewer empty patches to land on.
