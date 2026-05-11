import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Orchestrates Phase 1 replication experiments for the Schelling model.
 *
 * <p>Three named experiments are supported, all matching the proposal:
 * <ul>
 *   <li><b>baseline</b>: a single parameter setting
 *       (density = 95, %-similar-wanted = 30) replicated several times to
 *       characterise run-to-run variability.</li>
 *   <li><b>sweep-similar</b>: hold density fixed and vary %-similar-wanted
 *       across a configurable range.</li>
 *   <li><b>sweep-density</b>: hold %-similar-wanted fixed and vary density
 *       across a configurable range.</li>
 * </ul>
 *
 * <p>Each experiment produces:
 * <ul>
 *   <li>One per-tick CSV per individual run, written under the experiment's
 *       output directory.</li>
 *   <li>A {@code summary.csv} with one row per run aggregating the final
 *       state (final tick, %similar, %unhappy, total moves, converged).</li>
 * </ul>
 *
 * <p>Random seeds for individual runs are derived deterministically from a
 * single {@code baseSeed} so an entire sweep is reproducible.</p>
 */
public class ExperimentRunner {

    /** Default number of replications per parameter setting. */
    public static final int DEFAULT_REPLICATIONS = 10;
    /** Default base seed used to derive per-run seeds. */
    public static final long DEFAULT_BASE_SEED = 20260511L;

    /** Where all experiment outputs (per-run CSV + summary.csv) are written. */
    private final Path outputRoot;
    /** Number of replications per parameter setting. */
    private final int replications;
    /** Seed used to derive per-run seeds. */
    private final long baseSeed;

    /**
     * Construct an experiment runner.
     *
     * @param outputRoot   directory under which all CSVs will be created
     * @param replications number of runs per parameter setting (&gt;= 1)
     * @param baseSeed     base seed for deterministic per-run seed derivation
     */
    public ExperimentRunner(Path outputRoot, int replications, long baseSeed) {
        this.outputRoot = outputRoot;
        this.replications = replications;
        this.baseSeed = baseSeed;
    }

    /**
     * Run the baseline scenario: density = 95, %-similar-wanted = 30,
     * replicated {@link #replications} times.
     */
    public void runBaseline() throws IOException {
        runScenario("baseline", List.of(95), List.of(30));
    }

    /**
     * Sweep %-similar-wanted from 0 to 80 in steps of 10 at fixed density 95.
     * This matches Phase 1 experiment 2 from the proposal.
     */
    public void runSweepSimilar() throws IOException {
        List<Integer> similarValues = new ArrayList<>();
        for (int s = 0; s <= 80; s += 10) {
            similarValues.add(s);
        }
        runScenario("sweep_similar", List.of(95), similarValues);
    }

    /**
     * Sweep density from 50 to 95 in steps of 5 at fixed %-similar-wanted 30.
     * This matches Phase 1 experiment 3 from the proposal.
     */
    public void runSweepDensity() throws IOException {
        List<Integer> densityValues = new ArrayList<>();
        for (int d = 50; d <= 95; d += 5) {
            densityValues.add(d);
        }
        runScenario("sweep_density", densityValues, List.of(30));
    }

    /**
     * Run a scenario specified by a cross product of {@code densities} and
     * {@code similarValues}. Each (density, similar) cell is replicated
     * {@link #replications} times.
     *
     * @param name           sub-directory name under {@link #outputRoot}
     * @param densities      density values (percent, 0..100) to iterate over
     * @param similarValues  %-similar-wanted values (0..100) to iterate over
     */
    public void runScenario(String name,
                            List<Integer> densities,
                            List<Integer> similarValues) throws IOException {
        Path scenarioDir = outputRoot.resolve(name);
        Files.createDirectories(scenarioDir);
        Path summaryPath = scenarioDir.resolve("summary.csv");
        try (BufferedWriter summary = Files.newBufferedWriter(summaryPath)) {
            summary.write("experiment,density,percent_similar_wanted,run_id,seed,"
                    + "final_tick,final_percent_similar,final_percent_unhappy,"
                    + "final_num_unhappy,total_moves,converged,num_agents\n");
            for (int density : densities) {
                for (int similar : similarValues) {
                    for (int rep = 0; rep < replications; rep++) {
                        long seed = deriveSeed(density, similar, rep);
                        Simulation sim = new Simulation(density, similar, seed);
                        Path csvPath = scenarioDir.resolve(
                                String.format("run_d%02d_s%02d_r%02d.csv",
                                        density, similar, rep));
                        Simulation.RunResult result = sim.runWithCsv(csvPath);
                        appendSummaryRow(summary, name, density, similar, rep, seed, result);
                    }
                }
            }
        }
    }

    // -- helpers ------------------------------------------------------------

    /**
     * Deterministic per-run seed derivation. Mixes baseSeed with the three
     * loop indices so different (density, similar, rep) combinations get
     * statistically independent RNG sequences. The mixing constants are
     * arbitrary large primes; the exact values are not load-bearing.
     */
    private long deriveSeed(int density, int similar, int rep) {
        long mixed = baseSeed;
        mixed = mixed * 1_000_003L + density;
        mixed = mixed * 1_000_033L + similar;
        mixed = mixed * 1_000_037L + rep;
        return mixed;
    }

    /** Append a single summary row for one completed run. */
    private void appendSummaryRow(BufferedWriter summary,
                                  String name,
                                  int density,
                                  int similar,
                                  int rep,
                                  long seed,
                                  Simulation.RunResult result) throws IOException {
        summary.write(String.format(
                "%s,%d,%d,%d,%d,%d,%.4f,%.4f,%d,%d,%s,%d%n",
                name, density, similar, rep, seed,
                result.finalTick(),
                result.finalPercentSimilar(),
                result.finalPercentUnhappy(),
                result.finalNumUnhappy(),
                result.totalMoves(),
                result.converged(),
                result.numAgents()));
    }

    /**
     * Convenience for callers that want all three Phase 1 experiments at
     * once.
     */
    public void runAllPhase1() throws IOException {
        runBaseline();
        runSweepSimilar();
        runSweepDensity();
    }
}
