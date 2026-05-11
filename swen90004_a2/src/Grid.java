import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * The 2D world that holds the agents of the Schelling segregation model.
 *
 * <p>The grid is a rectangular array of cells; each cell either holds one
 * {@link Agent} or is empty (null). The grid is configured at construction
 * time to use either a torus (wrap-around, matching NetLogo's default world
 * topology) or a bounded plane. Moore neighbourhoods (8 surrounding cells)
 * are used throughout.</p>
 *
 * <p>For efficiency the grid also maintains a list of empty cells so that
 * {@link #pickRandomEmptyPosition(Random)} runs in O(1) and
 * {@link #moveAgent(Agent, Position)} runs in amortised O(W*H / occupancy)
 * (we use a swap-and-pop trick to keep the empty-list contiguous).</p>
 */
public class Grid {

    /** Width of the grid in cells. */
    private final int width;
    /** Height of the grid in cells. */
    private final int height;
    /** If true, neighbour lookups wrap around the edges (torus topology). */
    private final boolean torus;
    /** Cell storage; cells[row][col] is either an Agent or null. */
    private final Agent[][] cells;
    /**
     * Live list of currently empty cells. Kept in sync by every mutating
     * operation. Order is not meaningful; we exploit that to remove entries
     * in O(1) by swapping with the last element before popping.
     */
    private final List<Position> emptyPositions;
    /** Snapshot of all agents currently on the grid. */
    private final List<Agent> agents;

    /**
     * Construct an empty grid.
     *
     * @param width  number of columns (must be &gt; 0)
     * @param height number of rows (must be &gt; 0)
     * @param torus  whether to wrap around the edges (NetLogo default: true)
     */
    public Grid(int width, int height, boolean torus) {
        this.width = width;
        this.height = height;
        this.torus = torus;
        this.cells = new Agent[height][width];
        this.emptyPositions = new ArrayList<>(width * height);
        this.agents = new ArrayList<>();
        // Initially every cell is empty.
        for (int r = 0; r < height; r++) {
            for (int c = 0; c < width; c++) {
                emptyPositions.add(new Position(r, c));
            }
        }
    }

    /** @return number of columns. */
    public int getWidth() {
        return width;
    }

    /** @return number of rows. */
    public int getHeight() {
        return height;
    }

    /** @return total number of cells (width * height). */
    public int getCellCount() {
        return width * height;
    }

    /** @return live list of agents currently on the grid; do not mutate. */
    public List<Agent> getAgents() {
        return agents;
    }

    /** @return number of currently empty cells. */
    public int getEmptyCount() {
        return emptyPositions.size();
    }

    /**
     * Place an existing agent on its current position. The cell must be
     * empty; otherwise an {@link IllegalStateException} is thrown to surface
     * the bug in the calling code.
     */
    public void placeAgent(Agent agent) {
        Position p = agent.getPosition();
        if (cells[p.row()][p.col()] != null) {
            throw new IllegalStateException("Cell already occupied at " + p);
        }
        cells[p.row()][p.col()] = agent;
        agents.add(agent);
        removeFromEmpty(p);
    }

    /**
     * Move an agent from its current cell to {@code destination}, which must
     * be currently empty. Updates the agent's stored position and the
     * empty-cell bookkeeping.
     */
    public void moveAgent(Agent agent, Position destination) {
        Position from = agent.getPosition();
        if (cells[destination.row()][destination.col()] != null) {
            throw new IllegalStateException("Destination not empty: " + destination);
        }
        cells[from.row()][from.col()] = null;
        cells[destination.row()][destination.col()] = agent;
        agent.setPosition(destination);
        removeFromEmpty(destination);
        emptyPositions.add(from);
    }

    /**
     * Pick a uniformly random empty cell. Returns {@code null} if the grid
     * is completely full (no empty cells exist).
     */
    public Position pickRandomEmptyPosition(Random rng) {
        if (emptyPositions.isEmpty()) {
            return null;
        }
        return emptyPositions.get(rng.nextInt(emptyPositions.size()));
    }

    /**
     * Return the agent currently at the given cell, or {@code null} if the
     * cell is empty. Coordinates are wrapped if the grid is a torus.
     */
    public Agent getAgentAt(int row, int col) {
        int r = wrapRow(row);
        int c = wrapCol(col);
        if (r < 0 || c < 0) {
            return null;
        }
        return cells[r][c];
    }

    /**
     * Count occupied Moore neighbours of the given cell, split by colour.
     *
     * @param centre        the cell to look around
     * @param matchColour   the colour to count as "similar"
     * @return a two-element int array: [similarNeighbours, totalNeighbours]
     */
    public int[] countNeighbours(Position centre, Colour matchColour) {
        int similar = 0;
        int total = 0;
        // 8 Moore neighbours: all (dr, dc) where not both zero, |dr| <= 1, |dc| <= 1.
        for (int dr = -1; dr <= 1; dr++) {
            for (int dc = -1; dc <= 1; dc++) {
                if (dr == 0 && dc == 0) {
                    continue;
                }
                Agent neighbour = getAgentAt(centre.row() + dr, centre.col() + dc);
                if (neighbour != null) {
                    total++;
                    if (neighbour.getColour() == matchColour) {
                        similar++;
                    }
                }
            }
        }
        return new int[]{similar, total};
    }

    /**
     * Produce a randomly-ordered copy of the agent list, used to schedule
     * agents asynchronously within a tick (matching NetLogo's {@code ask}).
     */
    public List<Agent> shuffledAgents(Random rng) {
        List<Agent> shuffled = new ArrayList<>(agents);
        Collections.shuffle(shuffled, rng);
        return shuffled;
    }

    // -- internal helpers ---------------------------------------------------

    /**
     * Remove a single empty position from the bookkeeping list in amortised
     * O(1) by overwriting it with the last entry and popping.
     */
    private void removeFromEmpty(Position p) {
        int idx = emptyPositions.indexOf(p);
        if (idx < 0) {
            throw new IllegalStateException("Position not in empty list: " + p);
        }
        int last = emptyPositions.size() - 1;
        if (idx != last) {
            emptyPositions.set(idx, emptyPositions.get(last));
        }
        emptyPositions.remove(last);
    }

    /**
     * Map a possibly-out-of-range row index onto a valid one. On a torus we
     * wrap with {@link Math#floorMod}; on a bounded grid we return -1 to
     * signal "off-grid", which {@link #getAgentAt} treats as empty.
     */
    private int wrapRow(int row) {
        if (torus) {
            return Math.floorMod(row, height);
        }
        return (row >= 0 && row < height) ? row : -1;
    }

    /**
     * Map a possibly-out-of-range column index onto a valid one. See
     * {@link #wrapRow}.
     */
    private int wrapCol(int col) {
        if (torus) {
            return Math.floorMod(col, width);
        }
        return (col >= 0 && col < width) ? col : -1;
    }

    /**
     * Internal: clear all cells. Useful for tests or for re-running a fresh
     * setup on the same grid instance.
     */
    void clear() {
        for (Agent[] row : cells) {
            Arrays.fill(row, null);
        }
        agents.clear();
        emptyPositions.clear();
        for (int r = 0; r < height; r++) {
            for (int c = 0; c < width; c++) {
                emptyPositions.add(new Position(r, c));
            }
        }
    }
}
