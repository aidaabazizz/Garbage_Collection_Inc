package game.teleportstrategies;


import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;

/**
 * A teleportation strategy specific to the Mature Warper Tree.
 * This class implements the logic for finding a random destination on the map
 * when a worker is grabbed by the tree's roots. It integrates with the
 * Requirement 2 teleportation system by extending from the BaseTeleportStrategy
 * which implements the TeleportStrategy.
 *
 * @author Jewell Gomes
 */
public class TreeWarpStrategy extends BaseTeleportStrategy {

    /**
     * Determines a random valid destination on the current map for the warp event.
     * The method searches for a location that is both passable by the actor
     * and currently unoccupied.
     *
     * @param actor The actor that is being warped by the tree.
     * @param map   The GameMap where the warp is occurring.
     * @return A random valid Location for the actor to be moved to.
     */
    @Override
    public Location getDestination(Actor actor, GameMap map) {
        return getRandomValidLocation(map, actor);
    }

    /**
     * Provides a description of the action for the player menu.
     * Since the Warper Tree is an automatic environmental hazard and not a
     * player-initiated action, this returns an empty string to avoid appearing in menus.
     *
     * @param actor The actor performing the action.
     * @return An empty string.
     */
    @Override
    public String menuDescription(Actor actor) {
        return "";
    }
}
