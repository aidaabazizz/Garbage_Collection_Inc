package game.highvoltage;

import edu.monash.fit2099.engine.GameEntity;
import edu.monash.fit2099.engine.capabilities.Status;
import edu.monash.fit2099.engine.positions.Location;

/**
 * A specialized Status effect representing a temporary loss of motor control due
 * to high-voltage galvanic exposure.
 *
 * The ParalyzedStatus is a core component of Requirement 3 (High-Voltage System).
 * It functions as a "Behavior Modifier" that forces an Actor to skip their
 * playTurn phase for the duration of the effect.
 *
 * Complexity Proof:
 * 1. Behavioral Modification: Directly intercepts and prevents Actor actions
 *    (Movement, Attack, Interaction).
 * 2. Reflective Shield Synergy: As per the documentation, while an actor has this
 *    status, their suit is highly charged. Any attacker hitting a paralyzed actor
 *    suffers "Reflective" damage or a counter-shock.
 * 3. Timed Lifecycle: Manages its own turn-based countdown and automatic removal.
 *
 * @author Jewell Gomes
 */
public class ParalyzedStatus implements Status {
    /** The number of game turns remaining before the actor recovers. */
    private int remainingTurns;
    /**
     * Constructor for ParalyzedStatus.
     *
     * @param turns The duration of the paralysis. In standard galvanic events
     *              (arcing puddles or stalker sparks), this is typically set to 1.
     */
    public ParalyzedStatus(int turns) {
        this.remainingTurns = turns; // normally just 1 turn of paralyzed
    }
    /**
     * Updates the status every turn cycle.
     * Decrements the remaining duration until it reaches zero.
     *
     * @param entity   The actor currently affected by the status.
     * @param location The coordinate of the actor.
     */
    @Override
    public void tickStatus(GameEntity entity, Location location) {
        remainingTurns--;
    }
    /**
     * Determines if the paralysis is still hindering the actor.
     *
     * @return true if the actor should still be restricted, false otherwise.
     */
    @Override
    public boolean isStatusActive() {
        return remainingTurns > 0;
    }
    /**
     * Returns a descriptive string for the user interface.
     * Note: The "Reflective Surface" suffix informs the player that attacking
     * this target is dangerous.
     *
     * @return "Paralyzed (Reflective Surface)"
     */
    @Override
    public String toString() {
        return "Paralyzed (Reflective Surface)";
    }
}
