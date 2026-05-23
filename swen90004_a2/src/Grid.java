import java.util.ArrayList;
import java.util.Collections;


// the Grid class handles the world/neighbourhood where Agents exist in;
public class Grid {
    private Agent[][] neighbourhood;
    private ArrayList<Agent> allAgents = new ArrayList<>();
    private int gridX, gridY;
    private int numAgents = 0;
    private int numUnhappy = 0;
    private double percentHappy = 0;
    private double percentSimilar = 0;
    private int MAX_MOVE = 10;
    private ArrayList<dataEntry> gridData = new ArrayList<>();
    private int totalMoves = 0;
    private int tick = 0;

    // Phase 2 parameters
    private boolean isPhase2 = false;
    private double severeThreshold = 0.25;
    private double minJump = 10.0;
    private double maxJump = 20.0;

    public Grid(int gridX, int gridY) {
        // the grid is initialized with its dimensions, x is height, y is width
        this.gridX = gridX;
        this.gridY = gridY;
        this.neighbourhood = new Agent[gridX][gridY];
    }

    public void setPhase2Params(boolean isPhase2, double severeThreshold, double minJump, double maxJump) {
        this.isPhase2 = isPhase2;
        this.severeThreshold = severeThreshold;
        this.minJump = minJump;
        this.maxJump = maxJump;
    }

    // the grid is populated once given the initial conditions
    public void initialize(int density, int percentWanted) {

        // for every cell of the grid, determine if an agent should be generated, and keep track of all agents
        for (int i = 0; i < this.gridX; i++) {
            for (int j = 0; j < this.gridY; j++) {
                int shouldGenerate = (int) (Math.random() * 100) + 1;
                if (shouldGenerate <= density) {
                    Agent createdAgent = new Agent(i, j, percentWanted);
                    this.neighbourhood[i][j] = createdAgent;
                    this.numAgents++;
                    allAgents.add(createdAgent);
                }
            }
        }
        // check if agents happy
        this.updateAgents();
    }

    // updateAgents checks if the agent is happy based on its neighbours
    public double updateAgents() {
        double percentSimilarAggregate = 0;
        int numHappy =0;
        this.numUnhappy = 0;
        for (int i = 0; i < this.gridX; i++) {
            //System.out.print("\n");
            for (int j = 0; j < this.gridY; j++) {
                if (this.neighbourhood[i][j] != null) {
                    this.neighbourhood[i][j].updateCoordinates(i, j);

                    int nearbyAgents = 0;
                    int similarAgents = 0;

                    /*this block of code goes through the eight neighbours of the Agent and if the Agent exists on an
                        edge, finds the neighbour on the wrap around
                        */
                    if (i > 0) {
                        if (j > 0) {
                            if (this.neighbourhood[i - 1][j - 1] != null) {
                                nearbyAgents++;
                                if (this.neighbourhood[i][j].getAgentColour() ==
                                        this.neighbourhood[i - 1][j - 1].getAgentColour()) {
                                    similarAgents++;
                                }
                            }
                        } else if (j == 0){
                            if (this.neighbourhood[i - 1][this.gridY-1] != null) {
                                nearbyAgents++;
                                if (this.neighbourhood[i][j].getAgentColour() ==
                                        this.neighbourhood[i - 1][this.gridY-1].getAgentColour()) {
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
                        } else if (j == this.gridY - 1) {
                            if (this.neighbourhood[i - 1][0] != null) {
                                nearbyAgents++;
                                if (this.neighbourhood[i][j].getAgentColour() ==
                                        this.neighbourhood[i - 1][0].getAgentColour()) {
                                    similarAgents++;
                                }
                            }
                        }
                    } else if (i == 0) {
                        if (j > 0) {
                            if (this.neighbourhood[this.gridX-1][j - 1] != null) {
                                nearbyAgents++;
                                if (this.neighbourhood[i][j].getAgentColour() ==
                                        this.neighbourhood[this.gridX-1][j - 1].getAgentColour()) {
                                    similarAgents++;
                                }
                            }
                        } else if (j == 0){
                            if (this.neighbourhood[this.gridX-1][this.gridY-1] != null) {
                                nearbyAgents++;
                                if (this.neighbourhood[i][j].getAgentColour() ==
                                        this.neighbourhood[this.gridX-1][this.gridY-1].getAgentColour()) {
                                    similarAgents++;
                                }
                            }
                        }
                        if (this.neighbourhood[this.gridX-1][j] != null) {
                            nearbyAgents++;
                            if (this.neighbourhood[i][j].getAgentColour() ==
                                    this.neighbourhood[this.gridX-1][j].getAgentColour()) {
                                similarAgents++;
                            }
                        }
                        if (j < this.gridY - 1) {
                            if (this.neighbourhood[this.gridX-1][j + 1] != null) {
                                nearbyAgents++;
                                if (this.neighbourhood[i][j].getAgentColour() ==
                                        this.neighbourhood[this.gridX-1][j + 1].getAgentColour()) {
                                    similarAgents++;
                                }
                            }
                        } else if (j == this.gridY - 1) {
                            if (this.neighbourhood[this.gridX-1][0] != null) {
                                nearbyAgents++;
                                if (this.neighbourhood[i][j].getAgentColour() ==
                                        this.neighbourhood[this.gridX-1][0].getAgentColour()) {
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
                    } else if (j == 0) {
                        if (this.neighbourhood[i][this.gridY-1] != null) {
                            nearbyAgents++;
                            if (this.neighbourhood[i][j].getAgentColour() ==
                                    this.neighbourhood[i][this.gridY-1].getAgentColour()) {
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
                    } else if (j == this.gridY - 1) {
                        if (this.neighbourhood[i][0] != null) {
                            nearbyAgents++;
                            if (this.neighbourhood[i][j].getAgentColour() ==
                                    this.neighbourhood[i][0].getAgentColour()) {
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
                        } else if (j == 0) {
                            if (this.neighbourhood[i + 1][this.gridY-1] != null) {
                                nearbyAgents++;
                                if (this.neighbourhood[i][j].getAgentColour() ==
                                        this.neighbourhood[i + 1][this.gridY-1].getAgentColour()) {
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
                        } else if (j == this.gridY - 1) {
                            if (this.neighbourhood[i + 1][0] != null) {
                                nearbyAgents++;
                                if (this.neighbourhood[i][j].getAgentColour() == this.neighbourhood[i + 1][0].getAgentColour()) {
                                    similarAgents++;
                                }
                            }
                        }
                    } else if (i == this.gridX-1) {
                        if (j > 0) {
                            if (this.neighbourhood[0][j - 1] != null) {
                                nearbyAgents++;
                                if (this.neighbourhood[i][j].getAgentColour() ==
                                        this.neighbourhood[0][j - 1].getAgentColour()) {
                                    similarAgents++;
                                }
                            }
                        } else if (j == 0) {
                            if (this.neighbourhood[0][this.gridY-1] != null) {
                                nearbyAgents++;
                                if (this.neighbourhood[i][j].getAgentColour() ==
                                        this.neighbourhood[0][this.gridY-1].getAgentColour()) {
                                    similarAgents++;
                                }
                            }
                        }
                        if (this.neighbourhood[0][j] != null) {
                            nearbyAgents++;
                            if (this.neighbourhood[i][j].getAgentColour() == this.neighbourhood[0][j].getAgentColour()) {
                                similarAgents++;
                            }
                        }
                        if (j < this.gridY - 1) {
                            if (this.neighbourhood[0][j + 1] != null) {
                                nearbyAgents++;
                                if (this.neighbourhood[i][j].getAgentColour() == this.neighbourhood[0][j + 1].getAgentColour()) {
                                    similarAgents++;
                                }
                            }
                        } else if (j == this.gridY - 1) {
                            if (this.neighbourhood[0][0] != null) {
                                nearbyAgents++;
                                if (this.neighbourhood[i][j].getAgentColour() == this.neighbourhood[0][0].getAgentColour()) {
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
                    double percentSimilarIndividual = 0;
                    if (nearbyAgents != 0) {
                        percentSimilarIndividual = (double) similarAgents / (double) nearbyAgents;
                    }
                    percentSimilarAggregate = percentSimilarAggregate + percentSimilarIndividual;

                    if (this.neighbourhood[i][j] !=null) {
                        this.neighbourhood[i][j].updateHappy(percentSimilarIndividual);
                    }

                    if (this.neighbourhood[i][j].isHappy()){
                        numHappy++;
                    } else {
                        this.numUnhappy++;
                    }
                } else {
                    //System.out.print("NULL, ");
                }
            }
        }

        if (this.numAgents != 0) {
            this.percentHappy = ((double) numHappy) / ((double) this.numAgents) * 100;
        } else {
            this.percentHappy = 0;
        }
        this.percentSimilar = (percentSimilarAggregate/(double)this.numAgents)*100;

        dataEntry tickEntry = new dataEntry(this.tick,this.percentSimilar, (100-this.percentHappy), this.numUnhappy, this.totalMoves);
        this.gridData.add(tickEntry);
        return percentHappy;
    }

    // run the world for either until 0 unhappy agents or a given number of maximum ticks to halt infinite loops
    public ArrayList step(int steps){

        while ( this.tick < steps) {
            this.tick++;
            Collections.shuffle(allAgents);
            for (int a = 0; a < allAgents.size(); a++) {
                moveAgent(allAgents.get(a));
            }
            updateAgents();
            if (this.numUnhappy == 0){
                break;
            }

        }
        return this.gridData;
    }

    // =========================
    // UPDATED moveAgent method
    // =========================
    // moveAgent checks if the agent is unhappy, and if it is, allows it to move
    public void moveAgent (Agent mover){
        int oldX = mover.getX();
        int oldY = mover.getY();
        this.neighbourhood[oldX][oldY] = null;

        while (true) {
            double moveDistance;

            // A jump is triggered only when Phase 2 is enabled and the similarity is below a threshold
            if (isPhase2 && mover.getCurrentSimilarity() < severeThreshold) {
                moveDistance = minJump + (Math.random() * (maxJump - minJump));
            } else {
                moveDistance = (Math.random() * this.MAX_MOVE) + 1;
            }

            double rotate = Math.random() * 360;
            double radians = Math.toRadians(rotate);

            int moveX = (int) Math.round(Math.sin(radians) * moveDistance);
            int moveY = (int) Math.round(Math.cos(radians) * moveDistance);

            int newX = (mover.getX() + moveX % gridX + gridX) % gridX;
            int newY = (mover.getY() + moveY % gridY + gridY) % gridY;

            if (this.neighbourhood[newX][newY] == null) {
                mover.updateCoordinates(newX, newY);
                this.neighbourhood[newX][newY] = mover;
                this.totalMoves++;
                break;
            }
        }


//        if (mover.isHappy()){
//            return;
//        } else {
//            this.neighbourhood[mover.getX()][mover.getY()] = null;
//            // the Agent continues to move until it finds an unoccupied cell
//            while (true) {
//                //the agent turns to a random direction and moves forward a random amount to the designated maximum
//                double rotate = Math.random() * 360;
//                double move = (Math.random() * this.MAX_MOVE )+1;
//                int moveX = (int) Math.round(Math.sin(rotate) * move);
//                int moveY = (int) Math.round(Math.cos(rotate) * move);
//
//                // this block of code enforces the wrap around on both x and y axis
//                int newX = mover.getX() + moveX;
//                while (newX >= this.gridX) {
//                    newX = newX - this.gridX;
//                }
//                if (newX < 0) {
//                    newX = this.gridX + newX;
//                }
//                int newY = mover.getY() + moveY;
//                while (newY >= this.gridY) {
//                    newY = newY - this.gridY;
//                }
//                if (newY < 0) {
//                    newY = this.gridY + newY;
//                }
//
//                mover.updateCoordinates(newX, newY);
//                if (this.neighbourhood[newX][newY] == null){
//                    this.totalMoves++;
//                    this.neighbourhood[newX][newY] = mover;
//                    break;
//                }
//
//            }
//
//        }

    }

    public ArrayList<dataEntry> getGridData() {
        return gridData;
    }
}


