package game.utils;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.capabilities.Consumable;
import game.capabilities.Hypnotizable;
import game.enums.Ability;

import java.util.ArrayList;
import java.util.List;

/**
 * A utility class for performing spatial queries on the game map.
 * This class centralizes logic for detecting actors in surrounding tiles to
 * satisfy the DRY (Don't Repeat Yourself) principle across Requirement 3 (Mutated Flora),
 * Requirement 4 (Environmental Spawning Reactions), and Requirement 5 (Stateful Creatures).
 * By isolating spatial queries, this utility ensures that changes to detection logic
 * only need to be modified in a single location, enhancing system maintainability.
 *
 * @author Jewell Gomes
 * @author Aida (Extended with additional helper methods for REQ5)
 */
public class SpatialSearch {

    // ==================== EXISTING METHODS ====================

    /**
     * Identifies all actors possessing the {@code Ability.WORKER} capability within
     * the eight adjacent tiles surrounding a specified center location.
     *
     * @param center the location around which to search for workers
     * @return a list of all adjacent actors with the WORKER capability;
     *         returns an empty list if no workers are found
     */
    public static List<Actor> getNearbyWorkers(Location center) {
        List<Actor> workers = new ArrayList<>();
        for (Actor actor : getNearbyActors(center)) {
            if (actor.hasAbility(Ability.WORKER)) {
                workers.add(actor);
            }
        }
        return workers;
    }

    /**
     * Collects all actors, regardless of capabilities, currently standing in the
     * eight tiles surrounding a center location.
     *
     * @param center the location around which to search for actors
     * @return a list of all actors found in the immediate surrounding tiles;
     *         returns an empty list if no actors are found
     */
    public static List<Actor> getNearbyActors(Location center) {
        List<Actor> actors = new ArrayList<>();
        for (Exit exit : center.getExits()) {
            Location adj = exit.getDestination();
            if (adj.containsAnActor()) {
                actors.add(adj.getActor());
            }
        }
        return actors;
    }

    // ==================== NEW HELPER METHODS FOR REQ5 ====================

    /**
     * Checks if there is any worker within a specified Manhattan distance.
     * Uses capability pattern - no instanceof.
     *
     * @param map The game map
     * @param center The center location
     * @param maxDistance The maximum Manhattan distance to check
     * @return true if at least one worker is within distance
     */
    public static boolean hasWorkerWithinDistance(GameMap map, Location center, int maxDistance) {
        return findNearestWorker(map, center) != null &&
                getDistanceToWorker(map, center, findNearestWorker(map, center)) <= maxDistance;
    }

    /**
     * Finds the nearest conscious worker to a given location.
     * Uses capability pattern - no instanceof.
     *
     * @param map The game map
     * @param center The reference location
     * @return The nearest worker, or null if none found
     */
    public static Actor findNearestWorker(GameMap map, Location center) {
        Actor closest = null;
        int minDistance = Integer.MAX_VALUE;

        for (int y : map.getYRange()) {
            for (int x : map.getXRange()) {
                Location loc = map.at(x, y);
                if (loc.containsAnActor()) {
                    Actor target = loc.getActor();
                    if (target.hasAbility(Ability.WORKER) && target.isConscious()) {
                        int dist = calculateDistance(loc, center);
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
     * Counts all workers on the entire map.
     * Uses capability pattern - no instanceof.
     *
     * @param map The game map
     * @return Total number of workers
     */
    public static int countAllWorkers(GameMap map) {
        int count = 0;
        for (int y : map.getYRange()) {
            for (int x : map.getXRange()) {
                Location loc = map.at(x, y);
                if (loc.containsAnActor()) {
                    Actor target = loc.getActor();
                    if (target.hasAbility(Ability.WORKER)) {
                        count++;
                    }
                }
            }
        }
        return count;
    }

    /**
     * Checks if an actor has any consumable items in their inventory.
     * Uses asCapability() pattern - no instanceof.
     *
     * @param actor The actor to check
     * @return true if the actor has at least one consumable item
     */
    public static boolean hasConsumableInInventory(Actor actor) {
        for (Item item : actor.getInventory().getItems()) {
            if (item.asCapability(Consumable.class).isPresent()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if there is a worker with consumable items within a specified distance.
     * Uses capability pattern - no instanceof.
     *
     * @param map The game map
     * @param center The center location
     * @param maxDistance The maximum Manhattan distance to check
     * @return true if a worker with consumable items is within distance
     */
    public static boolean hasWorkerWithConsumableWithinDistance(GameMap map, Location center, int maxDistance) {
        for (int y : map.getYRange()) {
            for (int x : map.getXRange()) {
                Location loc = map.at(x, y);
                if (loc.containsAnActor()) {
                    Actor target = loc.getActor();
                    if (target.hasAbility(Ability.WORKER)) {
                        int dist = calculateDistance(loc, center);
                        if (dist <= maxDistance && hasConsumableInInventory(target)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }


    /**
     * Checks if there is any hypnotizable actor (slime) within a specified distance.
     *
     * @param map The game map
     * @param center The center location
     * @param maxDistance The maximum Manhattan distance to check
     * @return true if a hypnotizable actor is within distance
     */
    public static boolean hasHypnotizableWithinDistance(GameMap map, Location center, int maxDistance) {
        for (int y : map.getYRange()) {
            for (int x : map.getXRange()) {
                Location loc = map.at(x, y);
                if (loc.containsAnActor()) {
                    Actor target = loc.getActor();
                    if (target.asCapability(Hypnotizable.class).isPresent()) {
                        int dist = calculateDistance(loc, center);
                        if (dist <= maxDistance) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * Calculates Manhattan distance between two locations.
     *
     * @param a First location
     * @param b Second location
     * @return Manhattan distance
     */
    private static int calculateDistance(Location a, Location b) {
        return Math.abs(a.x() - b.x()) + Math.abs(a.y() - b.y());
    }

    /**
     * Gets the distance from a center to a specific worker.
     *
     * @param map The game map
     * @param center The center location
     * @param worker The worker to measure distance to
     * @return Manhattan distance, or Integer.MAX_VALUE if worker not on map
     */
    private static int getDistanceToWorker(GameMap map, Location center, Actor worker) {
        if (worker == null) return Integer.MAX_VALUE;
        Location workerLoc = map.locationOf(worker);
        return calculateDistance(center, workerLoc);
    }
}