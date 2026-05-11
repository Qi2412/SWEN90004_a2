import java.io.IOException;
import java.nio.file.Paths;

/**
 * Command-line entry point for the Schelling segregation replication.
 *
 * <p>The class exposes four subcommands so the same compiled JAR can drive
 * a single ad-hoc run, the Phase 1 baseline replication, the Phase 1
 * %-similar-wanted sweep, the Phase 1 density sweep, or all three.</p>
 *
 * <p>Usage:
 * <pre>
 *   java Main single        [density] [%-similar-wanted] [seed] [outputCsv]
 *   java Main baseline      [reps] [baseSeed] [outputDir]
 *   java Main sweep-similar [reps] [baseSeed] [outputDir]
 *   java Main sweep-density [reps] [baseSeed] [outputDir]
 *   java Main all           [reps] [baseSeed] [outputDir]
 * </pre>
 * All arguments are positional and optional; sensible defaults are applied
 * for any that are omitted.</p>
 */
public class Main {

    /** Default density used when {@code single} is called with no arguments. */
    private static final int DEFAULT_DENSITY = 95;
    /** Default %-similar-wanted used by {@code single}. */
    private static final int DEFAULT_PERCENT_WANTED = 30;
    /** Default seed for one-off single runs. */
    private static final long DEFAULT_SINGLE_SEED = 42L;
    /** Default output directory under which experiment CSVs are written. */
    private static final String DEFAULT_OUTPUT_DIR = "out";

    /**
     * CLI entry point. Dispatches to one of the experiment helpers based on
     * the first argument. Errors result in a non-zero exit and a one-line
     * message on stderr.
     */
    public static void main(String[] args) {
        if (args.length == 0) {
            printUsage();
            return;
        }
        String command = args[0].toLowerCase();
        try {
            switch (command) {
                case "single" -> runSingle(args);
                case "baseline" -> runBaseline(args);
                case "sweep-similar" -> runSweepSimilar(args);
                case "sweep-density" -> runSweepDensity(args);
                case "all" -> runAll(args);
                case "-h", "--help", "help" -> printUsage();
                default -> {
                    System.err.println("Unknown command: " + command);
                    printUsage();
                    System.exit(1);
                }
            }
        } catch (IOException ex) {
            System.err.println("I/O error: " + ex.getMessage());
            System.exit(2);
        } catch (NumberFormatException ex) {
            System.err.println("Could not parse numeric argument: " + ex.getMessage());
            printUsage();
            System.exit(1);
        }
    }

    /**
     * Run a single simulation with optional density, %-similar-wanted, seed,
     * and output CSV path. Useful for quick sanity checks and for producing
     * a single illustrative trace for the report.
     */
    private static void runSingle(String[] args) throws IOException {
        int density = argInt(args, 1, DEFAULT_DENSITY);
        int similar = argInt(args, 2, DEFAULT_PERCENT_WANTED);
        long seed = argLong(args, 3, DEFAULT_SINGLE_SEED);
        String csvPath = args.length >= 5
                ? args[4]
                : String.format("%s/single/run_d%02d_s%02d.csv",
                        DEFAULT_OUTPUT_DIR, density, similar);
        Simulation sim = new Simulation(density, similar, seed);
        Simulation.RunResult result = sim.runWithCsv(Paths.get(csvPath));
        System.out.printf(
                "single: density=%d similar=%d seed=%d -> tick=%d %%similar=%.2f "
                + "%%unhappy=%.2f moves=%d converged=%s csv=%s%n",
                density, similar, seed,
                result.finalTick(), result.finalPercentSimilar(),
                result.finalPercentUnhappy(), result.totalMoves(),
                result.converged(), csvPath);
    }

    /** Run the baseline experiment with optional replications/seed/outDir. */
    private static void runBaseline(String[] args) throws IOException {
        runnerFromArgs(args).runBaseline();
        System.out.println("baseline experiment complete.");
    }

    /** Run the %-similar-wanted sweep with optional replications/seed/outDir. */
    private static void runSweepSimilar(String[] args) throws IOException {
        runnerFromArgs(args).runSweepSimilar();
        System.out.println("sweep-similar experiment complete.");
    }

    /** Run the density sweep with optional replications/seed/outDir. */
    private static void runSweepDensity(String[] args) throws IOException {
        runnerFromArgs(args).runSweepDensity();
        System.out.println("sweep-density experiment complete.");
    }

    /** Run all three Phase 1 experiments back-to-back. */
    private static void runAll(String[] args) throws IOException {
        runnerFromArgs(args).runAllPhase1();
        System.out.println("all Phase 1 experiments complete.");
    }

    /**
     * Build an {@link ExperimentRunner} from the standard argument layout:
     * {@code args[1]=reps args[2]=baseSeed args[3]=outputDir}.
     */
    private static ExperimentRunner runnerFromArgs(String[] args) {
        int reps = argInt(args, 1, ExperimentRunner.DEFAULT_REPLICATIONS);
        long baseSeed = argLong(args, 2, ExperimentRunner.DEFAULT_BASE_SEED);
        String outDir = args.length >= 4 ? args[3] : DEFAULT_OUTPUT_DIR;
        return new ExperimentRunner(Paths.get(outDir), reps, baseSeed);
    }

    /** Parse an int argument with a default if missing. */
    private static int argInt(String[] args, int index, int defaultValue) {
        return args.length > index ? Integer.parseInt(args[index]) : defaultValue;
    }

    /** Parse a long argument with a default if missing. */
    private static long argLong(String[] args, int index, long defaultValue) {
        return args.length > index ? Long.parseLong(args[index]) : defaultValue;
    }

    /** Print usage on stdout. */
    private static void printUsage() {
        System.out.println("""
                Schelling segregation replication (Phase 1).

                Usage:
                  java Main single        [density] [%-similar-wanted] [seed] [outputCsv]
                  java Main baseline      [reps] [baseSeed] [outputDir]
                  java Main sweep-similar [reps] [baseSeed] [outputDir]
                  java Main sweep-density [reps] [baseSeed] [outputDir]
                  java Main all           [reps] [baseSeed] [outputDir]

                Defaults:
                  density            = 95
                  %-similar-wanted   = 30
                  single seed        = 42
                  reps               = 10
                  baseSeed           = 20260511
                  outputDir          = out""");
    }
}
