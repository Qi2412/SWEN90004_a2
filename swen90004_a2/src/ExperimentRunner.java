public class ExperimentRunner {
    public ExperimentRunner(){

    }

    public void runExperiment(){
        int pMaxRepeat = 3; // how many times each parameter should be used for a simulation
        int pDensity = 95;
        int pPercentWanted = 30;

        //Simulation placeholder = new Simulation(pDensity, pPercentWanted, pMaxRepeat);
        //placeholder.runSimulation();

        for (int i = 0; i <= 80; i = i+10) {
            Simulation placeholder = new Simulation(pDensity, i, pMaxRepeat);
            placeholder.runSimulation();
        }

        for (int i = 50; i <= 95; i = i+5) {
            Simulation placeholder = new Simulation(i, pPercentWanted, pMaxRepeat);
            placeholder.runSimulation();
        }
    }
}
