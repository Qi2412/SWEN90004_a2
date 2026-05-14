import java.io.FileWriter;
import java.util.ArrayList;
import java.io.File;
import java.io.IOException;

public class Simulation {
    private int GRID_X = 50; //grid height
    private int GRID_Y = 50; //grid width

    private Grid grid; // empty grid for this simulation
    private int density;
    private int percentWanted;
    private int MAX_TICKS = 100;
    private int numRepeat = 0;
    private int maxRepeat = 1;


    public Simulation(int density, int percentWanted, int maxRepeat){
        this.density = density;
        this.percentWanted = percentWanted;
        this.maxRepeat = maxRepeat;
    }

    public void runSimulation(){
        while (numRepeat<maxRepeat) {
            this.numRepeat++;
            grid = new Grid(GRID_X , GRID_Y);
            // populate grid with agents and initialize their attributes
            grid.initialize(density, percentWanted);
            grid.step(this.MAX_TICKS);
            ArrayList<dataEntry> gridData = grid.getGridData();

            outputData(gridData);


        }


    }

    private void outputData(ArrayList<dataEntry> gridData){


        File myObj = new File("experiment_output/");
        // Create File object
        if (myObj.mkdir()) {
            // Try to create the file
            System.out.println("Folder created: " + myObj.getName());
        } else {
            System.out.println("Folder already exists.");
        }


        try {
            FileWriter myWriter = new FileWriter("experiment_output/" + density + "_" + percentWanted + "_" + numRepeat + ".csv");
            myWriter.write("tick, percent_similar, percent_unhappy, num_unhappy, total_moves\n");
            for (int i = 0; i < gridData.size(); i++) {
                myWriter.write(gridData.get(i).getTick() + "," +
                        String.format("%.1f", gridData.get(i).getPercentSimilar()) + "," +
                        String.format("%.1f", gridData.get(i).getPercentUnhappy()) + "," +
                        gridData.get(i).getNumUnhappy() + "," +
                        gridData.get(i).getTotalMoves() + "\n"
                );
            }
            myWriter.close();  // must close manually
            System.out.println("Successfully wrote to the file.");
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }
}
