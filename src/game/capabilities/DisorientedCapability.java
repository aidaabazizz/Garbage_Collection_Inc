package game.capabilities;

/**
 * Interface for actors that can be disoriented.
 * Used with asCapability() pattern - NO instanceof!
 *
 * @author Aida
 */
public interface DisorientedCapability {
    boolean isDisoriented();
}