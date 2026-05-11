/**
 * Immutable (row, col) coordinate pair on the simulation grid.
 *
 * <p>Used as a value object so positions can be safely shared between agents,
 * collections, and the grid without aliasing concerns. The record also gives
 * us correct {@code equals} / {@code hashCode} for free, which is required
 * because positions are stored in lists/sets when tracking empty patches.</p>
 */
public record Position(int row, int col) {
}
