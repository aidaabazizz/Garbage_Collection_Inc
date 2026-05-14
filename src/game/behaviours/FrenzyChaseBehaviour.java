package game.behaviours;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.behaviours.Behaviour;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.utils.SpatialSearch;

/**
 * Behaviour used by CrazyChicken in FRENZY state.
 * It moves CrazyChicken toward the nearest conscious worker within range.
 *
 * @author Aida
 * @version 1.0
 */
public class FrenzyChaseBehaviour implements Behaviour<Actor, Action> {
    private static final int CHASE_DISTANCE = 8;

    @Override
    public Action operate(Actor actor, Location location) {
        GameMap map = location.map();
        Actor target = SpatialSearch.findNearestWorkerWithinDistance(map, location, CHASE_DISTANCE);

        if (target == null) {
            return null;
        }

        Location targetLocation = map.locationOf(target);
        return moveToward(actor, location, targetLocation);
    }

    private Action moveToward(Actor actor, Location currentLocation, Location targetLocation) {
        int currentDistance = distance(currentLocation, targetLocation);

        for (Exit exit : currentLocation.getExits()) {
            Location destination = exit.getDestination();

            if (!destination.canActorEnter(actor)) {
                continue;
            }

            int newDistance = distance(destination, targetLocation);

            if (newDistance < currentDistance) {
                return destination.getMoveAction(
                        actor,
                        "toward nearest worker (" + exit.getName() + ")",
                        exit.getHotKey()
                );
            }
        }

        return null;
    }

    private int distance(Location first, Location second) {
        return Math.abs(first.x() - second.x()) + Math.abs(first.y() - second.y());
    }
}