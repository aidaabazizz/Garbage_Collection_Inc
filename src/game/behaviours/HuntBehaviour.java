package game.behaviours;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.behaviours.Behaviour;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.capabilities.Ability;
import game.managers.AlarmManager;

/**
 * REQ4:
 * A behavior that directs an actor to track and move toward the nearest conscious worker.
 * This behavior is only active when the facility alarm system is triggered.
 * It calculates the shortest path to a target based on Manhattan distance
 * and selects an exit that brings the actor closer to that target.
 *
 * @author Jewell Gomes
 */
public class HuntBehaviour implements Behaviour<Actor, Action> {

    /**
     * Determines the movement required to pursue the closest conscious worker.
     *
     * @param actor The actor performing the behavior.
     * @param location The current location of the actor.
     * @return A movement action toward the nearest target if the alarm is active, or null if no target exists.
     */
    @Override
    public Action operate(Actor actor, Location location) {
        if (!AlarmManager.getInstance().isActive()) {
            return null;
        }

        Actor target = findNearestWorker(location.map(), location);
        if (target == null) return null;

        Location targetLocation = location.map().locationOf(target);
        int currentDistance = distance(location, targetLocation);

        for (Exit exit : location.getExits()) {
            Location destination = exit.getDestination();
            if (destination.canActorEnter(actor)) {
                int newDistance = distance(destination, targetLocation);
                if (newDistance < currentDistance) {
                    return destination.getMoveAction(actor, "toward " + target + " ("  + exit.getName() + ")", exit.getHotKey());
                }
            }
        }
        return null;
    }

    /**
     * Searches the entire map to locate the nearest conscious worker actor.
     *
     * @param map The game map to search.
     * @param myLoc The starting location for distance calculations.
     * @return The closest conscious worker actor found on the map, or null if none are present.
     */
    private Actor findNearestWorker(GameMap map, Location myLoc) {
        Actor closest = null;
        int minDistance = Integer.MAX_VALUE;

        for (int y : map.getYRange()) {
            for (int x : map.getXRange()) {
                Location loc = map.at(x, y);
                if (loc.containsAnActor()) {
                    Actor target = loc.getActor();
                    if (target.hasAbility(Ability.WORKER) && target.isConscious()) {
                        int dist = distance(myLoc, loc);
                        if (dist < minDistance) {
                            minDistance = dist;
                            closest = target;
                        }
                    }
                }
            }
        }
        return closest;
    }

    /**
     * Calculates the Manhattan distance between two map locations.
     *
     * @param a The first location.
     * @param b The second location.
     * @return The absolute distance between location a and location b.
     */
    private int distance(Location a, Location b) {
        return Math.abs(a.x() - b.x()) + Math.abs(a.y() - b.y());
    }
}