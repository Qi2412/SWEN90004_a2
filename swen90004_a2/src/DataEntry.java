/**
 * DataEntry records the global statistical information of the simulation
 * for a single time tick.
 */
public class DataEntry {
    private int tick, numUnhappy, totalMoves;
    private double percentSimilar, percentUnhappy;

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
