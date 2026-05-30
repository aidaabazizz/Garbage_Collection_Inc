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
     * Constructor that locks in a specific pre-calculated target destination.
     * * @param target The target Location destination tile.
     * @param destinationName The custom menu string containing coordinates.
     */
    public AlienCubeStrategy(Location target, String destinationName) {
        super(target, destinationName);
    }

    @Override
    protected Location determineActualDestination(Location target) {
        // Because we pass the pre-calculated target to the super constructor,
        // targetLocation is already populated in BaseTeleportStrategy.
        return target;
    }

    @Override
    public void applySourceEffects(Location source) {
        // Ripping a hole in reality: Turn all 8 immediately adjacent tiles into Toxic Waste (≈)
        for (Location adjacent : source.getNearbyLocations(1)) {
            adjacent.setGround(new ToxicWaste());
        }
    }

    @Override
    public void applyDestinationEffects(Location destination) {
    }

    @Override
    public String getActionDescription(Actor actor) {
        return actor + " warps to " + getDestinationName() + " using Alien Cube";
    }
}