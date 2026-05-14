import java.util.ArrayList;
import java.util.Collections;

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

    public Grid(int gridX, int gridY) {
        this.gridX = gridX;
        this.gridY = gridY;
        this.neighbourhood = new Agent[gridX][gridY];
    }

    public void initialize(int density, int percentWanted) {

        for (int i = 0; i < this.gridX; i++) {
            for (int j = 0; j < this.gridY; j++) {
                int shouldGenerate = (int) (Math.random() * 100) + 1;
                if (shouldGenerate <= density) {
                    Agent createdAgent = new Agent(i, j, percentWanted);
                    this.neighbourhood[i][j] = createdAgent;
                    this.numAgents++;
                    allAgents.add(createdAgent);
                    //System.out.print(this.neighbourhood[i][j].getAgentColour() + ", ");
                } else {
                    //System.out.print("null, ");
                }
            }
            //System.out.print("\n");
        }
        // check if agents happy
        this.updateAgents();
    }

    public double updateAgents() {
        double percentSimilarAggregate = 0;
        //int numAgents = 0;
        int numHappy =0;
        this.numUnhappy = 0;
        for (int i = 0; i < this.gridX; i++) {
            //System.out.print("\n");
            for (int j = 0; j < this.gridY; j++) {
                if (this.neighbourhood[i][j] != null) {
                    this.neighbourhood[i][j].updateCoordinates(i, j);
                    //numAgents ++;
                    int nearbyAgents = 0;
                    int similarAgents = 0;
                    //System.out.print("(" + i + "," + j + ") ");
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
                    //System.out.print(similarAgents + " / " + nearbyAgents + "->");
                    //System.out.print(this.neighbourhood[i][j].isHappy() + ", ");
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
        //System.out.println("\nUpdated Grid");
        //System.out.println("NumUnhappy = " + this.numUnhappy + ";");
        //System.out.println("%Similar = " + this.percentSimilar + ";");
        //System.out.println("%Unhappy = " + (100-this.percentHappy) + ";");
        //System.out.println("totalMoves = " + this.totalMoves + ";");
        dataEntry tickEntry = new dataEntry(this.tick,this.percentSimilar, (100-this.percentHappy), this.numUnhappy, this.totalMoves);
        this.gridData.add(tickEntry);
        return percentHappy;
    }

    public ArrayList step(int steps){
        this.tick++;
        while ( this.tick < steps) {

            //System.out.println("STEP " + iter);
            Collections.shuffle(allAgents);
            for (int a = 0; a < allAgents.size(); a++) {
                moveAgent(allAgents.get(a));
            }
            updateAgents();
            if (this.numUnhappy == 0){
                break;
            }
            this.tick++;
        }
        return this.gridData;
    }

    public void moveAgent (Agent mover){
        if (mover.isHappy()){
            return;
        } else {
            this.neighbourhood[mover.getX()][mover.getY()] = null;
            while (true) {
                //System.out.println(mover.getX() + "," + mover.getY());
                double rotate = Math.random() * 360;
                double move = (Math.random() * this.MAX_MOVE )+1;
                int moveX = (int) Math.round(Math.sin(rotate) * move);
                int moveY = (int) Math.round(Math.cos(rotate) * move);
                //System.out.println(mover.getX() + "," + mover.getY() + ": " + rotate + ", move " + move + "(" + moveX + "," + moveY + ")");
                int newX = mover.getX() + moveX;
                while (newX >= this.gridX) {
                    newX = newX - this.gridX;
                }
                if (newX < 0) {
                    newX = this.gridX + newX;
                }

                int newY = mover.getY() + moveY;
                while (newY >= this.gridY) {
                    newY = newY - this.gridY;
                }
                if (newY < 0) {
                    newY = this.gridY + newY;
                }

                mover.updateCoordinates(newX, newY);
                if (this.neighbourhood[newX][newY] == null){
                    this.totalMoves++;
                    //this.neighbourhood[mover.getX()][mover.getY()] = null;
                    this.neighbourhood[newX][newY] = mover;
                    //mover.updateCoordinates(newX, newY);
                    break;
                }

            }

        }

    }

    public ArrayList<dataEntry> getGridData() {
        return gridData;
    }
}


