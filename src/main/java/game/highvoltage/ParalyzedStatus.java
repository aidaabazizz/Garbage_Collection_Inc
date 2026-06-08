
package game.highvoltage;

import edu.monash.fit2099.engine.GameEntity;
import edu.monash.fit2099.engine.capabilities.Status;
import edu.monash.fit2099.engine.positions.Location;
import game.enums.MaterialCapability;

/**
 * A specialized Status effect representing a temporary loss of motor control and
 * high-voltage surface ionization.
 *
 * The ParalyzedStatus is a core component of the High-Voltage Galvanic System (Requirement 3).
 * It functions as a dual-phase modifier:
 * 1. Behavioral Modification: Forces the Actor to skip their turn processing phase
 *    for the duration of the effect.
 * 2. Material Synergy (Reflective Shield): While active, the status grants the
 *    {@link MaterialCapability#REFLECTIVE} capability to the Actor. This simulates
 *    a "Reflective Surface" where anyone attacking the paralyzed entity suffers
 *    electrical feedback (Reflective Surge).
 *
 * Complexity Proof (Requirement 3):
 * 1. Cross-Component Interaction: Connects Actor behaviors (skipping turns) with
 *    Combat logic (reflective damage) and the Capability system (Conductive tag).
 * 2. Timed Lifecycle: Manages a turn-based countdown to automatically revert the
 *    Actor's state once the electrical charge dissipates.
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
     * Updates the status every turn cycle to manage the lifecycle of the effect.
     *
     * This method handles the "Reflective Surface" logic:
     * 1. While the status is active, it enables the {@code CONDUCTIVE} capability.
     * 2. Decrements the remaining turns.
     * 3. Once expired, it disables the {@code CONDUCTIVE} capability, reverting
     *     the Actor to their standard material state.
     *
     * @param entity   The actor currently affected by the status.
     * @param location The coordinate of the actor.
     */
    @Override
    public void tickStatus(GameEntity entity, Location location) {
        if (isStatusActive()) {
            entity.enableAbility(MaterialCapability.REFLECTIVE);
            entity.enableAbility(MaterialCapability.PARALYZED);

            remainingTurns--;
        } else {
            // only disable if it is NO LONGER active
            // this ensures it stays on for at least one full cycle
            entity.disableAbility(MaterialCapability.REFLECTIVE);
            entity.disableAbility(MaterialCapability.PARALYZED);
        }
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
