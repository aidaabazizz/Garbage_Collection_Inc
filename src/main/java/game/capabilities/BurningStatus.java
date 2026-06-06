package game.capabilities;

/**
 * A specific damage-over-time effect resulting from exposure to fire.
 * This status tracks the duration of a burn effect on an actor, typically
 * originating from lantern oil leaks or ignited ground (REQ2).
 *
 * @author Jewell Gomes
 */
public class BurningStatus extends DamageOverTimeStatus {
    /**
     * Constructor to initialize the burning effect with a specific duration.
     * @param turns The number of game rounds the effect remains active.
     */
    public BurningStatus(int turns) {
        super("Burning", turns);
    }
}