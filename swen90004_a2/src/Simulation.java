public class Simulation {
    private int GRID_X = 50; //grid height
    private int GRID_Y = 50; //grid width

    private Grid grid = new Grid(GRID_X , GRID_Y); // empty grid for this simulation
    private int density;
    private int percentWanted;

    public Simulation(int density, int percentWanted){
        this.density = density;
        this.percentWanted = percentWanted;
    }

    public void runSimulation(){
        // populate grid with agents and initialize their attributes
        grid.initialize(density, percentWanted);
        grid.step(10);
    }
}
