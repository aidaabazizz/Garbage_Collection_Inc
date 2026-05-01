package game.capabilities;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Location;

/**
 * A contract defining the behavior of objects that can be ingested or utilized by actors.
 * This interface manages the effect applied to the consumer and the subsequent
 * lifecycle/cleanup of the object (REQ2/REQ3).
 *
 * @author Jewell Gomes
 */
public interface Consumable {

    /**
     * Executes the logic associated with consuming the object.
     * @param actor The entity performing the consumption.
     * @return A description of the resulting effect on the actor.
     */
    public String consumedBy(Actor actor);

    /**
     * Determines if the object has been completely depleted.
     * @return True if the object should be removed or deactivated, false otherwise.
     */
    default boolean isFinished() {
        return true;
    }

    /**
     * Handles the removal or state transition of the object after consumption.
     * @param actor The actor involved in the consumption.
     * @param location The map location where the object resides.
     */
    default void cleanUp(Actor actor, Location location) {}
}
