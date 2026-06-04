package game.teleportstrategies;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Location;
import game.grounds.ToxicWaste;

/**
 * This will support the teleportation of using alien cube.
 * It will help teleport worker to a chosen random destination, corrupts source
 * location into Toxic Waste and consumes the cube upon use.
 *
 * @author Victoria Tay Wen Xie
 * @version 2.0
 */
public class AlienCubeStrategy extends BaseTeleportStrategy {

    /**
     * Constructs an AlienCubeStrategy with a fixed target destination.
     *
     * @param target the destination location for teleportation
     * @param destinationName the display name of the destination
     */
    public AlienCubeStrategy(Location target, String destinationName) {
        super(target, destinationName);
    }

    /**
     * Determines the actual destination of the teleportation.
     * In this case, the destination is fixed and already precomputed.
     *
     * @param target the intended destination location
     * @return the same target location
     */
    @Override
    protected Location determineActualDestination(Location target) {
        return target;
    }

    /**
     * Applies effects at the source location before teleportation.
     * Converts all nearby tiles into Toxic Waste.
     *
     * @param source the location the actor is teleporting from
     */
    @Override
    public void applySourceEffects(Location source) {
        for (Location adjacent : source.getNearbyLocations(1)) {
            adjacent.setGround(new ToxicWaste());
        }
    }

    /**
     * Applies effects at the destination location after teleportation.
     * No additional effects are applied.
     *
     * @param destination the location the actor arrives at
     */
    @Override
    public void applyDestinationEffects(Location destination) {
        // No destination effects
    }

    /**
     * Returns a description of the teleportation action.
     *
     * @param actor the actor performing the teleportation
     * @return a string describing the teleportation action
     */
    @Override
    public String getActionDescription(Actor actor) {
        return actor + " warps to " + getDestinationName() + " using Alien Cube";
    }
}