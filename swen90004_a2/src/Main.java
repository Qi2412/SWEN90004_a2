/**
 * The Main class serves as the entry point for the simulation program.
 * It instantiates the ExperimentRunner and triggers the experimental suite.
 */
public class Main {

    public static void main(String[] args) {
        ExperimentRunner experiment = new ExperimentRunner();
        experiment.runExperiment();
    }
}