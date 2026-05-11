package game.utils;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.Location;
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
 */
public class SpatialSearch {

    /**
     * Identifies all actors possessing the {@code Ability.WORKER} capability within
     * the eight adjacent tiles (exits) surrounding a specified center location.
     *
     * This method is specifically used for triggers that only target player-controlled
     * workers, such as flora spawning slimes or slimes forcing item drops.
     *
     * @param center the Location on the map to search around.
     * @return a List of Actor instances that represent nearby workers.
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
     * This method is used for generic environmental effects that involve any type
     * of creature, such as the Undead's health bonus based on nearby lifeforms
     * or Elsa's cold-air effects.
     *
     * @param center the Location on the map to search around.
     * @return a List of all Actor instances found in adjacent tiles.
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
}