/**
 * The two agent colours used in the Schelling segregation model.
 *
 * <p>The NetLogo Models Library reference implementation uses the colours
 * "red" and "green", but the model UI labels them blue and orange. We keep
 * the UI-facing names here. The exact pair of colours is unimportant; what
 * matters is that there are exactly two distinguishable types of agent.</p>
 */
public enum Colour {
    /** First group — UI colour orange in the NetLogo model. */
    ORANGE,
    /** Second group — UI colour blue in the NetLogo model. */
    BLUE
}
