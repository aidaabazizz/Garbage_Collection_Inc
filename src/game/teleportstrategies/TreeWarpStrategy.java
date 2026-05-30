package game.teleportstrategies;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import java.util.Random;

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
     * Constructs a new TreeWarpStrategy with a default destination name.
     */
    public TreeWarpStrategy() {
        super("Random Location");
    }

    /**
     * Determines a random valid destination on the current map.
     * Retrieves the actor at the source location and delegates to the
     * shared {@link BaseTeleportStrategy#findRandomValidLocation} utility.
     * Falls back to the original location if no valid destination is found.
     *
     * @param target The source location from which the actor is being warped.
     * @return A random valid {@link Location}, or the original target as a fallback.
     */
    @Override
    protected Location determineActualDestination(Location target) {
        Actor actor = target.getActor();
        Location result = BaseTeleportStrategy.findRandomValidLocation(target.map(), actor);
        return result != null ? result : target;
    }

    /**
     * No source effects are applied when the tree warps a worker.
     *
     * @param source The location the actor is departing from.
     */
    @Override
    protected void applySourceEffects(Location source) {}

    /**
     * No destination effects are applied when the tree warps a worker.
     *
     * @param destination The location the actor arrives at.
     */
    @Override
    protected void applyDestinationEffects(Location destination) {}
}