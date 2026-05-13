public class ExperimentRunner {
    public ExperimentRunner(){

    }

    public void runExperiment(){
        int pDensity = 95;
        int pPercentWanted = 30;
        int pMaxRepeat = 3;
        Simulation placeholder = new Simulation(pDensity, pPercentWanted, pMaxRepeat);
        placeholder.runSimulation();
    }
}
