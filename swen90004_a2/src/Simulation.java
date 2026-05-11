import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Random;

/**
 * One run of the Schelling segregation model.
 *
 * <p>A {@code Simulation} owns a {@link Grid}, a seeded random source, and
 * the model parameters (grid size, density, %-similar-wanted, torus flag,
 * tick cap). Calling {@link #run()} performs a complete run from a fresh
 * setup until either every agent is happy (convergence) or {@code maxTicks}
 * is reached. Per-tick metrics can optionally be streamed to a CSV file.</p>
 *
 * <p>The tick procedure mirrors the NetLogo Models Library
 * <em>Segregation</em> model:
 * <ol>
 *   <li>If every agent is already happy, stop.</li>
 *   <li>Take a randomly-ordered snapshot of the unhappy agents and move each
 *       to a uniformly-random empty cell (matching the statistical effect of
 *       NetLogo's recursive {@code find-new-spot}).</li>
 *   <li>Recompute the happiness of every agent.</li>
 *   <li>Update the global metrics and advance the tick counter.</li>
 * </ol>
 * </p>
 */
public class Simulation {

    /** Default world dimensions matching NetLogo's segregation model (51 x 51). */
    public static final int DEFAULT_GRID_SIZE = 51;
    /** Default safety cap on the number of ticks per run. */
    public static final int DEFAULT_MAX_TICKS = 1000;
    /** Default torus flag matching NetLogo's wrap-around world. */
    public static final boolean DEFAULT_TORUS = true;

    // -- configuration ------------------------------------------------------

    /** Grid width and height in cells. */
    private final int gridWidth;
    private final int gridHeight;
    /** Target occupancy percentage in [0, 100]. */
    private final int densityPercent;
    /** Minimum %-similar-wanted in [0, 100]. */
    private final int percentSimilarWanted;
    /** Fractional form of {@link #percentSimilarWanted}, cached for speed. */
    private final double thresholdFraction;
    /** Safety cap on the number of ticks. */
    private final int maxTicks;
    /** Seed used to construct the random source; recorded for reproducibility. */
    private final long seed;
    /** Random source; all randomness in the simulation flows from this. */
    private final Random rng;

    // -- state --------------------------------------------------------------

    /** The world. */
    private final Grid grid;
    /** Number of ticks completed so far. */
    private int currentTick;
    /** Cumulative number of relocations performed. */
    private long totalMoves;
    /** Number of agents currently flagged unhappy (cached, updated each tick). */
    private int numUnhappy;

    /**
     * Construct a simulation with all parameters specified explicitly.
     *
     * @param gridWidth            grid width in cells (must be &gt; 0)
     * @param gridHeight           grid height in cells (must be &gt; 0)
     * @param densityPercent       target occupancy percentage in {@code [0, 100]}
     * @param percentSimilarWanted intolerance threshold in {@code [0, 100]}
     * @param torus                whether the grid is a torus (NetLogo default)
     * @param maxTicks             cap on number of ticks for non-converging runs
     * @param seed                 seed for the random source
     */
    public Simulation(int gridWidth,
                      int gridHeight,
                      int densityPercent,
                      int percentSimilarWanted,
                      boolean torus,
                      int maxTicks,
                      long seed) {
        this.gridWidth = gridWidth;
        this.gridHeight = gridHeight;
        this.densityPercent = densityPercent;
        this.percentSimilarWanted = percentSimilarWanted;
        this.thresholdFraction = percentSimilarWanted / 100.0;
        this.maxTicks = maxTicks;
        this.seed = seed;
        this.rng = new Random(seed);
        this.grid = new Grid(gridWidth, gridHeight, torus);
        this.currentTick = 0;
        this.totalMoves = 0L;
        this.numUnhappy = 0;
    }

    /**
     * Convenience constructor that uses the NetLogo defaults for grid size,
     * torus, and tick cap.
     */
    public Simulation(int densityPercent, int percentSimilarWanted, long seed) {
        this(DEFAULT_GRID_SIZE, DEFAULT_GRID_SIZE, densityPercent, percentSimilarWanted,
                DEFAULT_TORUS, DEFAULT_MAX_TICKS, seed);
    }

    // -- accessors ----------------------------------------------------------

    public int getDensityPercent() {
        return densityPercent;
    }

    public int getPercentSimilarWanted() {
        return percentSimilarWanted;
    }

    public int getCurrentTick() {
        return currentTick;
    }

    public long getTotalMoves() {
        return totalMoves;
    }

    public int getNumUnhappy() {
        return numUnhappy;
    }

    public int getNumAgents() {
        return grid.getAgents().size();
    }

    public long getSeed() {
        return seed;
    }

    /** @return true if every agent is currently happy. */
    public boolean hasConverged() {
        return numUnhappy == 0;
    }

    // -- model procedure ----------------------------------------------------

    /**
     * Populate the grid: visit every cell and, with probability
     * {@code density/100}, place an agent of uniformly random colour. After
     * setup the happiness of every agent is computed, mirroring NetLogo's
     * initial {@code update-turtles} call.
     */
    public void setup() {
        grid.clear();
        currentTick = 0;
        totalMoves = 0L;
        for (int r = 0; r < gridHeight; r++) {
            for (int c = 0; c < gridWidth; c++) {
                if (rng.nextInt(100) < densityPercent) {
                    Colour colour = rng.nextBoolean() ? Colour.ORANGE : Colour.BLUE;
                    Agent agent = new Agent(colour, new Position(r, c), thresholdFraction);
                    grid.placeAgent(agent);
                }
            }
        }
        updateAllHappiness();
    }

    /**
     * Perform one tick of the model, matching NetLogo's {@code go}
     * procedure:
     * <pre>
     *   if all? turtles [happy?] [stop]
     *   move-unhappy-turtles      ; each unhappy turtle calls find-new-spot
     *   update-turtles            ; recompute happiness
     *   update-globals            ; recompute %similar, %unhappy
     *   tick
     * </pre>
     *
     * <p>Unhappy agents are visited in random order ({@code ask} semantics)
     * and relocated via the recursive random walk in
     * {@link Grid#findNewSpotByRandomWalk(Agent, Random)}. An agent whose
     * walk wanders back to its own patch does not count as a move (NetLogo:
     * {@code move-to patch-here} is a no-op in that case).</p>
     *
     * @return {@code true} if any unhappy agents existed at the start of
     *         the tick; {@code false} if the model is already at
     *         equilibrium and nothing was done
     */
    public boolean step() {
        if (hasConverged()) {
            return false;
        }
        List<Agent> shuffled = grid.shuffledAgents(rng);
        int movesThisTick = 0;
        for (Agent agent : shuffled) {
            if (agent.isHappy()) {
                continue;
            }
            Position destination = grid.findNewSpotByRandomWalk(agent, rng);
            if (destination == null) {
                // No empty patch found within the iteration cap; skip this
                // agent (extreme density / pathological setup).
                continue;
            }
            if (!destination.equals(agent.getPosition())) {
                grid.moveAgent(agent, destination);
                movesThisTick++;
            }
        }
        totalMoves += movesThisTick;
        updateAllHappiness();
        currentTick++;
        return true;
    }

    /**
     * Run from setup until convergence or {@code maxTicks}. Returns a record
     * of the final metrics so callers can use the simulation as a one-shot
     * function.
     */
    public RunResult run() {
        setup();
        while (currentTick < maxTicks && !hasConverged()) {
            step();
        }
        return new RunResult(currentTick, percentSimilar(), percentUnhappy(),
                numUnhappy, totalMoves, hasConverged(), getNumAgents(), seed);
    }

    /**
     * Run the simulation and stream per-tick metrics (tick, %similar,
     * %unhappy, num_unhappy, total_moves) as CSV to {@code csvOut}. The
     * parent directory is created if missing. Tick 0 captures the post-setup
     * state.
     */
    public RunResult runWithCsv(Path csvOut) throws IOException {
        Files.createDirectories(csvOut.toAbsolutePath().getParent());
        try (BufferedWriter writer = Files.newBufferedWriter(csvOut)) {
            writer.write("tick,percent_similar,percent_unhappy,num_unhappy,total_moves\n");
            setup();
            writeMetricsRow(writer);
            while (currentTick < maxTicks && !hasConverged()) {
                step();
                writeMetricsRow(writer);
            }
        }
        return new RunResult(currentTick, percentSimilar(), percentUnhappy(),
                numUnhappy, totalMoves, hasConverged(), getNumAgents(), seed);
    }

    // -- global metrics -----------------------------------------------------

    /**
     * Aggregate similar-neighbour percentage across all agents, computed
     * exactly as in NetLogo's {@code update-globals}:
     * <pre>
     *   percent-similar = (sum of similar-nearby) / (sum of total-nearby) * 100
     * </pre>
     * This is a degree-weighted aggregate (agents with more occupied
     * neighbours contribute proportionally more), not a simple mean of
     * per-agent ratios. Isolated agents contribute 0 to both sums and
     * therefore do not need special handling.
     *
     * @return similar-neighbour percentage in {@code [0, 100]}, or 0 if no
     *         agent has any neighbours yet
     */
    public double percentSimilar() {
        long sumSimilar = 0;
        long sumTotal = 0;
        for (Agent agent : grid.getAgents()) {
            int[] counts = grid.countNeighbours(agent.getPosition(), agent.getColour());
            sumSimilar += counts[0];
            sumTotal += counts[1];
        }
        return sumTotal == 0 ? 0.0 : 100.0 * sumSimilar / sumTotal;
    }

    /** @return percentage of agents currently flagged unhappy in {@code [0, 100]}. */
    public double percentUnhappy() {
        int n = getNumAgents();
        return n == 0 ? 0.0 : 100.0 * numUnhappy / n;
    }

    // -- internals ----------------------------------------------------------

    /**
     * Recompute the happiness flag of every agent and update {@link #numUnhappy}.
     * Called after setup and after every move phase, so {@link #step} can rely
     * on cached flags.
     */
    private void updateAllHappiness() {
        int unhappy = 0;
        for (Agent agent : grid.getAgents()) {
            int[] counts = grid.countNeighbours(agent.getPosition(), agent.getColour());
            agent.updateHappiness(counts[0], counts[1]);
            if (!agent.isHappy()) {
                unhappy++;
            }
        }
        this.numUnhappy = unhappy;
    }

    /**
     * Write one row of (tick, percent_similar, percent_unhappy, num_unhappy,
     * total_moves) to {@code writer}.
     */
    private void writeMetricsRow(BufferedWriter writer) throws IOException {
        writer.write(String.format(
                "%d,%.4f,%.4f,%d,%d%n",
                currentTick, percentSimilar(), percentUnhappy(), numUnhappy, totalMoves));
    }

    /**
     * Summary of a completed run, returned by {@link #run()} and
     * {@link #runWithCsv(Path)}.
     */
    public record RunResult(int finalTick,
                            double finalPercentSimilar,
                            double finalPercentUnhappy,
                            int finalNumUnhappy,
                            long totalMoves,
                            boolean converged,
                            int numAgents,
                            long seed) {
    }
}
