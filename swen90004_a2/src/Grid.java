
public class Grid {
    private Agent[][] neighbourhood;
    private int gridX, gridY;
    private int numAgents = 0;

    public Grid(int gridX, int gridY) {
        this.gridX = gridX;
        this.gridY = gridY;
        this.neighbourhood = new Agent[gridX][gridY];
    }

    public void initialize(int density, int percentWanted) {
        double percentHappy = 0;
        for (int i = 0; i < this.gridX; i++) {
            for (int j = 0; j < this.gridY; j++) {
                int shouldGenerate = (int) (Math.random() * 100) + 1;
                if (shouldGenerate <= density) {
                    this.neighbourhood[i][j] = new Agent(percentWanted);
                    this.numAgents++;
                    System.out.print(this.neighbourhood[i][j].getAgentColour() + ", ");
                } else {
                    System.out.print("null, ");
                }
            }
            System.out.print("\n");
        }
        // check if agents happy
        percentHappy = this.updateAgents();
        System.out.println("\n%Happy = " + percentHappy + ";");
    }

    public double updateAgents() {
        double percentHappy = 0;
        int numAgents = 0;
        int numHappy =0;
        for (int i = 0; i < this.gridX; i++) {
            System.out.print("\n");
            for (int j = 0; j < this.gridY; j++) {
                if (this.neighbourhood[i][j] != null) {
                    numAgents ++;
                    int nearbyAgents = 0;
                    int similarAgents = 0;
                    //TO BE FINISHED; this needs to be able to handle if there is not an agent
                    System.out.print("(" + i + "," + j + ") ");
                    if (i > 0) {
                        if (j > 0) {
                            if (this.neighbourhood[i - 1][j - 1] != null) {
                                nearbyAgents++;
                                if (this.neighbourhood[i][j].getAgentColour() ==
                                        this.neighbourhood[i - 1][j - 1].getAgentColour()) {
                                    similarAgents++;
                                }
                            }
                        }
                        if (this.neighbourhood[i - 1][j] != null) {
                            nearbyAgents++;
                            if (this.neighbourhood[i][j].getAgentColour() ==
                                    this.neighbourhood[i - 1][j].getAgentColour()) {
                                similarAgents++;
                            }
                        }
                        if (j < this.gridY - 1) {
                            if (this.neighbourhood[i - 1][j + 1] != null) {
                                nearbyAgents++;
                                if (this.neighbourhood[i][j].getAgentColour() ==
                                        this.neighbourhood[i - 1][j + 1].getAgentColour()) {
                                    similarAgents++;
                                }
                            }
                        }
                    }
                    if (j > 0) {
                        if (this.neighbourhood[i][j - 1] != null) {
                            nearbyAgents++;
                            if (this.neighbourhood[i][j].getAgentColour() ==
                                    this.neighbourhood[i][j - 1].getAgentColour()) {
                                similarAgents++;
                            }
                        }
                    }
                    if (j < this.gridY - 1) {
                        if (this.neighbourhood[i][j + 1] != null) {
                            nearbyAgents++;
                            if (this.neighbourhood[i][j].getAgentColour() ==
                                    this.neighbourhood[i][j + 1].getAgentColour()) {
                                similarAgents++;
                            }
                        }
                    }
                    if (i < this.gridX-1) {

                        if (j > 0) {
                            if (this.neighbourhood[i + 1][j - 1] != null) {
                                nearbyAgents++;
                                if (this.neighbourhood[i][j].getAgentColour() ==
                                        this.neighbourhood[i + 1][j - 1].getAgentColour()) {
                                    similarAgents++;
                                }
                            }
                        }
                        if (this.neighbourhood[i + 1][j] != null) {
                            nearbyAgents++;
                            if (this.neighbourhood[i][j].getAgentColour() == this.neighbourhood[i + 1][j].getAgentColour()) {
                                similarAgents++;
                            }
                        }
                        if (j < this.gridY - 1) {
                            if (this.neighbourhood[i + 1][j + 1] != null) {
                                nearbyAgents++;
                                if (this.neighbourhood[i][j].getAgentColour() == this.neighbourhood[i + 1][j + 1].getAgentColour()) {
                                    similarAgents++;
                                }
                            }
                        }
                    }
                    if (nearbyAgents > 8) {
                        System.out.println("ERROR: Impossible number of nearby");
                    }
                    if (similarAgents> nearbyAgents){
                        System.out.println("ERROR: Impossible number of similar");
                    }
                    double percentSimilar = (double)similarAgents/(double)nearbyAgents;
                    this.neighbourhood[i][j].updateHappy(percentSimilar);
                    //System.out.print(similarAgents + " / " + nearbyAgents + "->");
                    System.out.print(this.neighbourhood[i][j].isHappy() + ", ");
                    if (this.neighbourhood[i][j].isHappy()){
                        numHappy++;
                    }
                } else {
                    System.out.print("NULL, ");
                }
            }
        }
        percentHappy = ((double)numHappy)/((double)numAgents) *100;
        return percentHappy;
    }

    public void step(int steps){
        System.out.println("STEPPING");
        for (int iter = 0; iter<steps; iter++){
            for (int i = 0; i < this.gridX; i++) {
                System.out.print("\n");
                for (int j = 0; j < this.gridY; j++) {

                }

            }
        }
    }
}


