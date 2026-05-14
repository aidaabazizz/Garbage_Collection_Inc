package game.capabilities;

/**
 * Interface for actors that are currently frozen.
 * Used with asCapability() pattern - NO instanceof!
 *
 * @author Aida
 */
public interface FreezableCapability {
    /**
     * Checks if the freeze is still active.
     * @return true if still frozen, false otherwise
     */
    boolean isActive();
}