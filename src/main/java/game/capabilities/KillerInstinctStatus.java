package game.capabilities;

import edu.monash.fit2099.engine.GameEntity;
import edu.monash.fit2099.engine.capabilities.Status;
import edu.monash.fit2099.engine.positions.Location;

/**
 * A temporary status effect that grants the actor a "Killer Instinct" state.
 * While active, this status enables enhanced combat behaviour
 * (e.g., triggering Rage Strike actions in the worker's turn logic).
 * The status persists for a limited number of turns and expires automatically
 * once the counter reaches zero.
 *
 * @author Chathya Attanayake
 * @version 1.0
 */
public class KillerInstinctStatus implements Status {
    /** Number of turns remaining before this status expires. */
    private int remainingTurns;

    /**
     * Constructs a new Killer Instinct status effect.
     *
     * @param turns number of turns the effect remains active
     */
    public KillerInstinctStatus(int turns) {
        this.remainingTurns = turns;
    }

    /**
     * Updates the status each turn, reducing its remaining duration.
     *
     * @param entity  the entity affected by this status
     * @param location the entity's current location
     */
    @Override
    public void tickStatus(GameEntity entity, Location location) {
        remainingTurns--;
    }

    /**
     * Checks whether this status is still active.
     *
     * @return true if remaining turns is greater than zero
     */
    @Override
    public boolean isStatusActive() {
        return remainingTurns > 0;
    }

    /**
     * Returns a readable description of the status.
     *
     * @return formatted status string with remaining duration
     */
    @Override
    public String toString() {
        return "Killer Instinct (" + remainingTurns + " turns left)";
    }
}
