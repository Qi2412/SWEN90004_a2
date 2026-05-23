import java.util.ArrayList;
import java.util.Collections;


/**
 * The Grid class handles the world/neighbourhood where Agents exist.
 * It manages the initialisation, updating and movement of all agents.
 */
public class Grid {
    private Agent[][] neighbourhood; // A 2D array representing the spatial grid
    private ArrayList<Agent> allAgents = new ArrayList<>(); // A list tracking all active agents
    private int gridX, gridY; // The height (x) and width (y) of the grid
    private int numAgents = 0; // Total number of agents spawned in the grid
    private int numUnhappy = 0; // Current count of unhappy agents
    private double percentHappy = 0; // Overall percentage of satisfied agents
    private double percentSimilar = 0; // Global average similarity percentage
    private int MAX_MOVE = 10; // Maximum jump distance for normal relocation
    private ArrayList<DataEntry> gridData = new ArrayList<>(); // Collection of metrics per tick
    private int totalMoves = 0; // Cumulative count of all agent jumps
    private int tick = 0; // The current time step of the simulation

    // Phase 2 parameters
    private boolean isPhase2 = false; // Flag to toggle Phase 2 extension mechanics
    private double severeThreshold = 0.25; // Similarity ratio triggering a jump
    private double minJump = 10.0; // Minimum distance for a jump
    private double maxJump = 20.0; // Maximum distance for a jump

    /**
     * Constructs an empty Grid with the specified dimensions.
     * @param gridX The grid x-coordinate
     * @param gridY The grid y-coordinate
     */
    public Grid(int gridX, int gridY) {
        // the grid is initialized with its dimensions, x is height, y is width
        this.gridX = gridX;
        this.gridY = gridY;
        this.neighbourhood = new Agent[gridX][gridY];
    }

    /**
     * Injects the specific Phase 2 parameters into the grid configuration.
     */
    public void setPhase2Params(boolean isPhase2, double severeThreshold,
                                double minJump, double maxJump) {
        this.isPhase2 = isPhase2;
        this.severeThreshold = severeThreshold;
        this.minJump = minJump;
        this.maxJump = maxJump;
    }

    /**
     * Populates the grid with agents based on a defined density.
     * @param density Probability (1-100) of an agent spawning in a given cell.
     * @param percentWanted The desired similarity percentage for each agent.
     */
    public void initialize(int density, int percentWanted) {

        // for every cell of the grid, determine if an agent should be generated,
        // and keep track of all agents
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

    /**
     * Evaluates all agents to update their similarity calculations and happiness.
     */
    public void updateAgents() {
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

                    /* this block of code goes through the eight neighbours of the Agent
                    and if the Agent exists on an edge, finds the neighbour on the wrap around
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
                    double percentSimilarIndividual = 1.0;
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

//        DataEntry tickEntry = new DataEntry(this.tick,this.percentSimilar, (100-this.percentHappy),
//                                            this.numUnhappy, this.totalMoves);
//        this.gridData.add(tickEntry);
    }

    // run the world for either until 0 unhappy agents or a given number of maximum ticks to halt infinite loops
//    public ArrayList step(int steps){
//
//        while ( this.tick < steps) {
//            this.tick++;
//            Collections.shuffle(allAgents);
//            for (int a = 0; a < allAgents.size(); a++) {
//                moveAgent(allAgents.get(a));
//            }
//            updateAgents();
//            if (this.numUnhappy == 0){
//                break;
//            }
//
//        }
//        return this.gridData;
//    }

    /**
     * Executes the simulation loop for a set number of ticks.
     * @param maxTicks The maximum number of time steps to simulate.
     */
    public void step(int maxTicks) {
        for (int t = 0; t < maxTicks; t++) {
            this.tick = t;
            updateAgents();

            // Record the current state
            gridData.add(new DataEntry(tick, percentSimilar, (100-percentHappy),
                                       numUnhappy, totalMoves));

            // If no one in the system is unhappy, the simulation will terminate early.
            if (numUnhappy == 0) {
                break;
            }

            // Disrupt the order to maintain system concurrency fairness
            Collections.shuffle(allAgents);

            // Add filtering, only unhappy agent can move.
            for (Agent agent : allAgents) {
                if (!agent.isHappy()) {
                    moveAgent(agent);
                }
            }
        }
    }

    // =========================
    // UPDATED moveAgent method
    // =========================

    /**
     * Calculates a new location for an unhappy agent and moves it there.
     * In Phase 2, severely unhappy agents leap further away.
     * @param mover The agent that intends to relocate.
     */
    public void moveAgent (Agent mover){
        int oldX = mover.getX();
        int oldY = mover.getY();
        this.neighbourhood[oldX][oldY] = null;

        int searchX = oldX;
        int searchY = oldY;

        // Continuously probe for a new empty cell
        while (true) {
            double moveDistance;

            // Phase 2 extension: A jump is triggered when the similarity is below a threshold
            if (isPhase2 && mover.getCurrentSimilarity() < severeThreshold) {
                moveDistance = minJump + (Math.random() * (maxJump - minJump));
            } else {
                moveDistance = (Math.random() * this.MAX_MOVE) + 1;
            }

            // Calculate directional offsets using radians
            double rotate = Math.random() * 360;
            double radians = Math.toRadians(rotate);

            int moveX = (int) Math.round(Math.sin(radians) * moveDistance);
            int moveY = (int) Math.round(Math.cos(radians) * moveDistance);

//            // Apply modulo arithmetic to handle toroidal boundary wrap-around cleanly
//            int newX = (mover.getX() + moveX % gridX + gridX) % gridX;
//            int newY = (mover.getY() + moveY % gridY + gridY) % gridY;

            searchX = (searchX + moveX % gridX + gridX) % gridX;
            searchY = (searchY + moveY % gridY + gridY) % gridY;

            // Finalise move if the destination is empty
            if (this.neighbourhood[searchX][searchY] == null) {
                mover.updateCoordinates(searchX, searchY);
                this.neighbourhood[searchX][searchY] = mover;
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

    public ArrayList<DataEntry> getGridData() {
        return gridData;
    }
}


