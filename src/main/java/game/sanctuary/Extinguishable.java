package game.sanctuary;

import edu.monash.fit2099.engine.positions.Location;

/**
 * An interface for ground types or environmental effects that can be actively
 * suppressed or removed.
 *
 * The extinguish method provides a standardized way for external systems to
 * remove a hazard and restore the map to a neutral or original state.
 *
 * @author Chathya Attanayake
 * @version 1.0
 */
public interface Extinguishable {
    /**
     * Triggers the logic to remove or "put out" the implementation at the specified location.
     *
     * Implementations typically use this method to revert the  Ground at the
     * given Location back to a neutral state or to a stored "original"
     * ground type
     *
     * @param location the location where the effect should be extinguished.
     */
     void extinguish(Location location);
}
