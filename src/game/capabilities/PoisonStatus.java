package game.capabilities;

/**
 * A recurring damage effect resulting from toxic ingestion or exposure.
 * This status implements the poison logic associated with spoiled scraps
 * and contaminated water sources (REQ2).
 *
 * @author Jewell Gomes
 */
public class PoisonStatus extends DamageOverTimeStatus {
    /**
     * Constructor to initialize the poisoning with a specific duration.
     * @param turns The duration of the toxic effect.
     */
    public PoisonStatus(int turns) {
        super("Poisoned", turns);
    }
}