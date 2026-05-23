/**
 * DataEntry records the global statistical information of the simulation
 * for a single time tick.
 */
public class DataEntry {
    private int tick;              // Current time step of the simulation
    private int numUnhappy;        // Number of unhappy agents at this tick
    private int totalMoves;        // Cumulative number of agent relocations up to this tick
    private double percentSimilar; // Average percentage of similar neighbours across all agents
    private double percentUnhappy; // The percentage of the total agent population that is unhappy

    /**
     * Constructs a DataEntry object to store metrics for a specific tick.
     * @param tick The current time step.
     * @param percentSimilar The average similarity percentage across all agents.
     * @param percentUnhappy The percentage of total agents that are unhappy.
     * @param numUnhappy The absolute count of unhappy agents.
     * @param totalMoves The cumulative count of agent movements up to this tick.
     */
    public DataEntry(int tick, double percentSimilar,
                     double percentUnhappy, int numUnhappy, int totalMoves){
        this.tick = tick;
        this.percentSimilar=percentSimilar;
        this.percentUnhappy=percentUnhappy;
        this.numUnhappy = numUnhappy;
        this.totalMoves = totalMoves;
    }

    public int getTick() {
        return tick;
    }

    public int getNumUnhappy() {
        return numUnhappy;
    }

    public int getTotalMoves() {
        return totalMoves;
    }

    public double getPercentSimilar() {
        return percentSimilar;
    }

    public double getPercentUnhappy() {
        return percentUnhappy;
    }
}
