package game.teleportstrategies;


import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.capabilities.TeleportStrategy;

import java.util.Random;

/**
 * A teleportation strategy specific to the Mature Warper Tree.
 * This class implements the logic for finding a random destination on the map
 * when a worker is grabbed by the tree's roots. It integrates with the
 * Requirement 2 teleportation system by implementing the TeleportStrategy interface.
 *
 * @author Jewell Gomes
 */
public class TreeWarpStrategy implements TeleportStrategy {
    private final Random random = new Random();

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
        Location dest;
        do {
            int x = random.nextInt(map.getXRange().max() + 1);
            int y = random.nextInt(map.getYRange().max() + 1);
            dest = map.at(x, y);
        } while (dest.containsAnActor() || !dest.canActorEnter(actor));
        return dest;
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
