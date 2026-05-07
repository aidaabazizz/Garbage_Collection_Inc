package game.teleportstrategies;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.capabilities.TeleportStrategy;
import game.grounds.ToxicWaste;

public class AlienCubeStrategy implements TeleportStrategy {

    private final static int ADJACENT_TILE_DISTANCE = 1;
    private final Location destination;

    public AlienCubeStrategy(Location destination) {
        this.destination = destination;
    }

    public Location getDestination(Actor actor, GameMap map) {
        return destination;
    }

    @Override
    public void applySideEffects(Actor actor, Location source, Location destination, GameMap map) {
        for (Location adjacent : source.getNearbyLocations(ADJACENT_TILE_DISTANCE)) {
            adjacent.setGround(new ToxicWaste());
        }
    }

    @Override
    public String menuDescription(Actor actor) {
        return "Teleport " + actor + "to (" + destination.x() + ", " + destination.y() + ") via Alien Cube";
    }

}
