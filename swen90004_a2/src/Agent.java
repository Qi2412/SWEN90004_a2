/**
 * A single resident in the Schelling segregation model.
 *
 * <p>Each agent has a fixed {@link Colour}, a mutable {@link Position} on the
 * grid, a personal {@code similarThresholdFraction} (the minimum proportion of
 * same-coloured neighbours required to be happy), and a cached {@code happy}
 * flag that is recomputed by the simulation each tick.</p>
 *
 * <p>Happiness is computed using the exact NetLogo Models Library rule:
 * an agent is happy iff
 * <pre>similar &gt;= threshold * total</pre>
 * where {@code similar} counts same-colour neighbours, {@code total} counts
 * occupied neighbours (out of 8 Moore neighbours), and {@code threshold} is
 * the fractional form of the {@code %-similar-wanted} parameter. An agent
 * with no occupied neighbours (total == 0) is considered happy, matching the
 * NetLogo convention (0 &gt;= 0 is true).</p>
 */
public class Agent {

    /** Group membership; does not change after construction. */
    private final Colour colour;

    /**
     * Personal tolerance, expressed as a fraction in {@code [0.0, 1.0]} (e.g.
     * 0.30 means the agent wants at least 30% of its occupied neighbours to
     * share its colour). Held as a fraction rather than a percentage so the
     * happiness check is a single multiplication.
     */
    private final double similarThresholdFraction;

    /** Current grid cell. Replaced (not mutated) when the agent relocates. */
    private Position position;

    /** Last computed happiness state; refreshed by {@link #updateHappiness}. */
    private boolean happy;

    /**
     * Construct an agent at a given position with a fixed colour and tolerance.
     *
     * @param colour                   the agent's group
     * @param position                 initial grid cell
     * @param similarThresholdFraction tolerance in {@code [0.0, 1.0]}; values
     *                                 outside this range are accepted but no
     *                                 longer correspond to the NetLogo slider
     */
    public Agent(Colour colour, Position position, double similarThresholdFraction) {
        this.colour = colour;
        this.position = position;
        this.similarThresholdFraction = similarThresholdFraction;
        this.happy = false;
    }

    /** @return this agent's colour group. */
    public Colour getColour() {
        return colour;
    }

    /** @return this agent's current grid cell. */
    public Position getPosition() {
        return position;
    }

    /**
     * Update this agent's grid cell. Called by {@link Grid#moveAgent}; do not
     * call directly, as the {@code Grid}'s internal cell array must stay in
     * sync.
     */
    void setPosition(Position newPosition) {
        this.position = newPosition;
    }

    /** @return cached happiness from the most recent {@link #updateHappiness}. */
    public boolean isHappy() {
        return happy;
    }

    /**
     * Recompute and cache this agent's happiness given the latest neighbour
     * counts. Uses the NetLogo rule {@code similar >= threshold * total}; an
     * agent with no occupied neighbours is happy by convention.
     *
     * @param similarNeighbours number of occupied Moore neighbours sharing
     *                          this agent's colour
     * @param totalNeighbours   number of occupied Moore neighbours (0..8)
     */
    public void updateHappiness(int similarNeighbours, int totalNeighbours) {
        // NetLogo: happy? = similar-nearby >= (%-similar-wanted * total-nearby / 100)
        // With totalNeighbours == 0 this collapses to 0 >= 0, i.e. happy.
        this.happy = similarNeighbours >= similarThresholdFraction * totalNeighbours;
    }
}
