
// Agent is either a blue or orange agent
public class Agent {
    private Colour agentColour;
    private boolean happy = false;
    private double percentWanted = 0;
    private int x;
    private int y;

    private double wealth = 100.0; // Phase 2: Initial wealth value of each resident

    public Agent(int x, int y, int percentWanted){
        this.percentWanted = percentWanted;
        this.randomize();
        this.x = x;
        this.y = y;
    }

    // randomly assigns a colour by coin flip
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

    /* this method compares a given percentage similar with the Agent's desired percentage and determines whether
    it is happy
     */
    public void updateHappy (double percentSimilar){
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
