package game.sanctuary;

/**
 * An enum representing capabilities used to intercept, modify, or negate damage
 * applied to actors.
 *
 * These constants are intended to be used as capabilities
 * to signal to the damage system that an actor is currently under a specific
 * defensive state. This allows for clean interaction between defensive grounds
 * (like Safe Houses or Sanctuary Fields) and damage-dealing logic without
 * tight coupling.
 *
 * @author Chathya Attanayake
 * @version 1.0
 */
public enum DamageInterceptor {
    /**
     * Signals that the actor is currently protected by a sanctuary effect.
     *
     * Actors with this capability typically receive reduced damage from
     * hazards or are entirely immune to environmental effects like BlueFire.
     * This flag is automatically managed by SanctuaryStatus and SanctuaryField.
     */
    PROTECTED
}