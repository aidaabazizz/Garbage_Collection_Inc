package game.capabilities;

/**
 * A contract for environmental objects that can support multiple layers of fire.
 * This interface allows ground types to manage intensifying fire effects,
 * typically used when multiple lantern leaks occur on a single tile (REQ2).
 *
 * @author Jewell Gomes
 */
public interface FireStackable {
    /** Increases the intensity or duration of the fire at the implementing location. */
    public void addStack();
}
