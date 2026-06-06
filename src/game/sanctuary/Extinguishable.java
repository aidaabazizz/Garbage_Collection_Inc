package game.sanctuary;

import edu.monash.fit2099.engine.positions.Location;

public interface Extinguishable {
    /**
     * Interface for grounds that can be actively removed from the map.
     */
     void extinguish(Location location);
}
