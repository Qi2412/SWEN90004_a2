/**
 * The Agent class represents an individual resident (either BLUE or ORANGE)
 * within the Segregation Model.
 */
public class Agent {
    private Colour agentColour;
    private boolean happy = false;
    private double percentWanted = 0;
    private int x;
    private int y;

    private double currentSimilarity = 0.0; // Phase 2: Record current similarity
    private double wealth = 100.0; // Phase 2: Initial wealth value of each resident

    /**
     * Constructs a new Agent with a designated position and tolerance level.
     * @param x The initial x-coordinate.
     * @param y The initial y-coordinate.
     * @param percentWanted The threshold percentage for the agent to be happy.
     */
    public Agent(int x, int y, int percentWanted){
        this.percentWanted = percentWanted;
        this.randomize();
        this.x = x;
        this.y = y;
    }

    /**
     * Randomly assigns the agent's colour (ORANGE or BLUE) with equal probability.
     * Assumption: Math.random() provides a uniform distribution [0.0, 1.0).
     */
    public void randomize (){
        if (Math.random()<0.5){
            this.agentColour = Colour.ORANGE;
        } else {
            this.agentColour = Colour.BLUE;
        }
    }


    public Colour getAgentColour() {
        return agentColour;
    }

    public void setAgentColour(Colour agentColour) {
        this.agentColour = agentColour;
    }

    public boolean isHappy() {
        return happy;
    }

    public void setHappy(boolean happy) {
        this.happy = happy;
    }

    /**
     * Updates the happiness state based on the current neighbourhood similarity.
     * @param percentSimilar The actual percentage of similar neighbours (0 to 100).
     */
    public void updateHappy (double percentSimilar){
        this.currentSimilarity = percentSimilar;
        if (percentSimilar > ((double)this.percentWanted/100)){
            this.happy = true;
        } else {
            this.happy = false;
        }
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public double getCurrentSimilarity() {
        return currentSimilarity;
    }

    public void updateCoordinates (int x, int y){
        this.x = x;
        this.y = y;
    }

    // Phase 2: Check if Agent can afford the moving costs.
    public boolean canAfford(double cost) {
        return this.wealth >= cost;
    }

    // Phase 2: Deduct moving cost from wealth value
    public void deductWealth(double cost) {
        this.wealth -= cost;
    }
}
