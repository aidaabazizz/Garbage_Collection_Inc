package game.capabilities;

/**
 * Interface for actors that are currently hypnotized.
 * Used with asCapability() to check hypnotized state.
 *
 * @author Aida
 */
public interface HypnotizedCapability {
    boolean isHypnotized();
}