// this object records global information in a single tick
public class dataEntry {
    private int tick, numUnhappy, totalMoves;
    private double percentSimilar, percentUnhappy;

    public dataEntry(int tick, double percentSimilar, double percentUnhappy, int numUnhappy, int totalMoves){
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
