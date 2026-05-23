/**
 * The ExperimentRunner sets up and automates the execution of multiple
 * simulation scenarios, spanning both Phase 1 (baseline) and Phase 2 (extension).
 */
public class ExperimentRunner {
    public ExperimentRunner(){

    }

    public void runExperiment(){
        int pMaxRepeat = 3;      // Number of iterations per configuration
        int pDensity = 95;       // Default density for %wanted sweep
        int pPercentWanted = 30; // Default %wanted for density sweep

        //Simulation placeholder = new Simulation(pDensity, pPercentWanted, pMaxRepeat);
        //placeholder.runSimulation();

        // =================================================================
        // PHASE 1 EXPERIMENTS (Baseline)
        // isPhase2 = false, dummy values (0.0) for threshold and jump distances
        // =================================================================
        System.out.println("=== Starting Phase 1 Experiments ===");

        for (int i = 0; i <= 80; i = i+10) {
            Simulation placeholder = new Simulation(pDensity, i, pMaxRepeat,
                                            false, 0.0, 0.0, 0.0);
            placeholder.runSimulation();
        }

        for (int i = 50; i <= 95; i = i+5) {
            Simulation placeholder = new Simulation(i, pPercentWanted, pMaxRepeat,
                                            false, 0.0, 0.0, 0.0);
            placeholder.runSimulation();
        }

        // =================================================================
        // PHASE 2 EXPERIMENTS (Extension)
        // isPhase2 = true, threshold = 25% (0.25), jump distance = 10 to 20
        // =================================================================
        System.out.println("=== Starting Phase 2 Experiments ===");

        double pSevereThreshold = 0.25; // 25% similarity triggers jump
        double pMinJump = 10.0;         // Jump starts at 10 units away
        double pMaxJump = 20.0;         // Jump ends at up to 20 units away

        // Run the exact same parameter sweeps for Phase 2 to allow direct comparison
        for (int i = 0; i <= 80; i = i + 10) {
            Simulation sim = new Simulation(pDensity, i, pMaxRepeat, true,
                                            pSevereThreshold, pMinJump, pMaxJump);
            sim.runSimulation();
        }

        for (int i = 50; i <= 95; i = i + 5) {
            Simulation sim = new Simulation(i, pPercentWanted, pMaxRepeat, true,
                                            pSevereThreshold, pMinJump, pMaxJump);
            sim.runSimulation();
        }
    }
}
