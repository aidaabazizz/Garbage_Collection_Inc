package game.capabilities;

/**
 * Interface for actors that are currently frozen.
 * Used with asCapability() to check frozen state.
 *
 * @author Aida
 */
public interface FreezableCapability {
    boolean isFrozen();
}