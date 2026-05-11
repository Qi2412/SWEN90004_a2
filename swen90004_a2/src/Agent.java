
public class Agent {
    private Colour agentColour;
    private boolean happy = false;
    private double percentWanted = 0;
    public Agent(int percentWanted){
        this.percentWanted = percentWanted;
        this.randomize();
    }

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

    public void updateHappy (double percentSimilar){
        if (percentSimilar > ((double)this.percentWanted/100)){
            this.happy = true;
        } else {
            this.happy = false;
        }
    }
}
