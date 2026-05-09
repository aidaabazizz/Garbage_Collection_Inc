package game.teleportstrategies;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.grounds.ToxicWaste;
import java.util.ArrayList;
import java.util.List;

public class AlienCubeStrategy extends BaseTeleportStrategy {
    private final Location destination;
    private final static int ADJACENT_TILE = 1;

    public AlienCubeStrategy(Location destination) {
        this.destination = destination;
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

    @Override
    public Location getDestination(Actor actor, GameMap map) {
        return destination;
    }

    @Override
    public void applySideEffects(Actor actor, Location source, Location destination, GameMap map) {
        for (Location adj : source.getNearbyLocations(ADJACENT_TILE)) {
            adj.setGround(new ToxicWaste());
        }
    }

    @Override
    public String menuDescription(Actor actor) {
        return "Warp to (" + destination.x() + ", " + destination.y() + ")";
    }
}