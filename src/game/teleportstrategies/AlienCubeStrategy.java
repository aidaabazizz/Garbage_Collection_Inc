package game.teleportstrategies;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.grounds.ToxicWaste;
import game.items.AlienCube;

import java.util.ArrayList;
import java.util.List;

/**
 * This will support the teleportation of using alien cube.
 * It will help teleport worker to a chosen random destination, corrupts source
 * location into Toxic Waste and consumes the cube upon use.
 *
 * @author Victoria Tay Wen Xie
 * @version 1.0
 */
public class AlienCubeStrategy extends BaseTeleportStrategy {
    /** Destination location */
    private final Location destination;
    /** Adjacent corruption radius */
    private final static int ADJACENT_TILE = 1;
    /** This will refer to the Alien Cube */
    private final AlienCube cube;

    /**
     * Creates a strategy for a chosen destination
     * @param destination the destination location
     * @param cube the Alien cube being used
     */
    public AlienCubeStrategy(Location destination, AlienCube cube) {
        this.destination = destination;
        this.cube = cube;
    }

    /**
     * This helper finds the 3 random spots required by the Alien Cube
     * using the inherited base utility.
     */
    public List<Location> getRandomDestinations(GameMap map, Actor actor, int count) {
        List<Location> targets = new ArrayList<>();
        while (targets.size() < count) {
            Location loc = getRandomValidLocation(map, actor); // Inherited from Base
            if (loc != null && !targets.contains(loc)) {
                targets.add(loc);
            }
        }
        return targets;
    }

    /**
     * Returns the chosen destination
     * @param actor the teleporting actor
     * @param map the current map
     * @return the destination location
     */
    @Override
    public Location getDestination(Actor actor, GameMap map) {
        return destination;
    }

    /**
     * This will corrupt source tiles to Toxic Waste and removes the alien cube from inventory
     * @param actor       The actor being moved.
     * @param source      The location where the teleportation started.
     * @param destination The location where the actor arrived.
     * @param map         The map where the side effects should be applied.
     */
    @Override
    public void applySideEffects(Actor actor, Location source, Location destination, GameMap map) {
        for (Location adj : source.getNearbyLocations(ADJACENT_TILE)) {
            adj.setGround(new ToxicWaste());
        }
        if (cube != null) {
            actor.getInventory().remove(cube);
        }
    }

    /**
     * Returns menu description
     * @param actor the teleporting actor
     * @return menu description string
     */
    @Override
    public String menuDescription(Actor actor) {
        return "Warp to (" + destination.x() + ", " + destination.y() + ")";
    }
}