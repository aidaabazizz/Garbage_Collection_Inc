package game.services;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;

/**
 * Service class that handles pushing an actor away from a source location.
 * This keeps movement displacement logic separate from state classes.
 *
 * @author Aida
 * @version 1.0
 */
public class PushBackService {

    /**
     * Pushes a target actor away from a source location by a fixed number of tiles.
     * If a tile is blocked or outside the map, the push stops early.
     *
     * @param source the location pushing the target away
     * @param target the actor being pushed
     * @param targetLocation the current location of the target
     * @param tiles the maximum number of tiles to push
     */
    public void pushAwayFrom(Location source, Actor target, Location targetLocation, int tiles) {
        GameMap map = source.map();

        int dx = targetLocation.x() - source.x();
        int dy = targetLocation.y() - source.y();

        if (dx != 0) {
            dx = dx > 0 ? 1 : -1;
        }

        if (dy != 0) {
            dy = dy > 0 ? 1 : -1;
        }

        Location current = targetLocation;

        for (int step = 0; step < tiles; step++) {
            int newX = current.x() + dx;
            int newY = current.y() + dy;

            if (!map.getXRange().contains(newX) || !map.getYRange().contains(newY)) {
                return;
            }

            Location next = map.at(newX, newY);

            if (next.containsAnActor() || !next.canActorEnter(target)) {
                return;
            }

            map.moveActor(target, next);
            current = next;
        }
    }
}