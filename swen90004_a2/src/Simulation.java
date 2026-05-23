import java.io.FileWriter;
import java.util.ArrayList;
import java.io.File;
import java.io.IOException;

/**
 * The Simulation class manages a single experimental run of the grid,
 * including configuration, execution, and data exportation.
 */
public class Simulation {
    private int GRID_X = 50;        // Grid height
    private int GRID_Y = 50;        // Grid width

    private Grid grid;              // The grid instance for this simulation
    private int density;            // Percentage of populated cells
    private int percentWanted;      // Required similarity percentage for agents
    private int MAX_TICKS = 100;    // Hard limit for simulation duration
    private int numRepeat = 0;      // Current iteration number
    private int maxRepeat = 1;      // Maximum iterations to perform

    // Phase 2 parameters
    private boolean isPhase2;       // Toggle switch for Phase 2 mechanics.
    private double severeThreshold; // The ratio below which an agent takes drastic action.
    private double minJump;         // The minimum distance for a severe jump.
    private double maxJump;         // The maximum distance for a severe jump.

    /**
     * Constructs a Simulation controller with full Phase 1 and 2 configurations.
     * @param density Initial population density percentage.
     * @param percentWanted The desired similarity threshold for agents.
     * @param maxRepeat Number of times to run this specific configuration.
     * @param isPhase2 Toggle switch for Phase 2 mechanics.
     * @param severeThreshold The ratio below which an agent takes drastic action.
     * @param minJump The minimum distance for a severe jump.
     * @param maxJump The maximum distance for a severe jump.
     */
    public Simulation(int density, int percentWanted, int maxRepeat,
                      boolean isPhase2, double severeThreshold,
                      double minJump, double maxJump){
        this.density = density;
        this.percentWanted = percentWanted;
        this.maxRepeat = maxRepeat;
        this.isPhase2 = isPhase2;
        this.severeThreshold = severeThreshold;
        this.minJump = minJump;
        this.maxJump = maxJump;
    }

    /**
     * Executes the simulation for the configured number of repetitions.
     * Upon completion of each repeat, data is written to a CSV file.
     */
    public void runSimulation(){
        while (numRepeat<maxRepeat) {
            this.numRepeat++;
            grid = new Grid(GRID_X , GRID_Y);
            // Inject Phase 2 configurations
            grid.setPhase2Params(isPhase2, severeThreshold, minJump, maxJump);
            // Populate grid with agents and initialize their attributes
            grid.initialize(density, percentWanted);
            grid.step(this.MAX_TICKS);
            ArrayList<DataEntry> gridData = grid.getGridData();

            outputData(gridData);

        }
    }

    /**
     * Creates an output directory if missing, and exports simulation metrics
     * to a uniquely identified CSV file.
     * @param gridData Collection of metrics recorded during the simulation.
     */
    private void outputData(ArrayList<DataEntry> gridData){
        File myObj = new File("experiment_output/");
        // Create File object
        if (myObj.mkdir()) {
            // Try to create the file
            System.out.println("Folder created: " + myObj.getName());
        } else {
            System.out.println("Folder already exists.");
        }

        try {
            // Add a prefix to separate Phase 1 and Phase 2 data files
            String prefix = isPhase2 ? "phase2_" : "phase1_";
            String fileName = "experiment_output/" + prefix + density + "_"
                              + percentWanted + "_" + numRepeat + ".csv";

            FileWriter writer = new FileWriter(fileName);
            writer.write("tick, percent_similar, percent_unhappy, num_unhappy, total_moves\n");

            // Write each tick's state as a new row
            for (int i = 0; i < gridData.size(); i++) {
                writer.write(gridData.get(i).getTick() + "," +
                        String.format("%.1f", gridData.get(i).getPercentSimilar()) + "," +
                        String.format("%.1f", gridData.get(i).getPercentUnhappy()) + "," +
                        gridData.get(i).getNumUnhappy() + "," +
                        gridData.get(i).getTotalMoves() + "\n"
                );
            }
            writer.close();  // must close manually
            System.out.println("Successfully wrote to " + fileName);
        } catch (IOException e) {
            System.out.println("An IO error occurred during file writing.");
            e.printStackTrace();
        }
    }
}
