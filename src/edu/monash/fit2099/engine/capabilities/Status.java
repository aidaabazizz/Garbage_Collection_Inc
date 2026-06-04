package edu.monash.fit2099.engine.capabilities;

import edu.monash.fit2099.engine.GameEntity;
import edu.monash.fit2099.engine.positions.Location;

public interface Status {

    /**
     * Called once per tick to update the status of the current entity. Default
     * implementation does nothing.
     *
     * @param currEntity the entity this status is attached to
     */
    default void tickStatus(GameEntity currEntity, Location location) {
        // No action by default; override in subclasses if needed
    }

    /**
     * Indicates whether this status is still active.
     *
     * @return true if active, false otherwise
     */
    boolean isStatusActive();
}
