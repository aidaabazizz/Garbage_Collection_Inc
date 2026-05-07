package game.teleportstrategies;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.capabilities.TeleportStrategy;
import game.grounds.Fire;

import java.util.Random;

public class TeleportTubeStrategy implements TeleportStrategy {

    private final static int ADJACENT_TILE_DISTANCE = 1;
    private final static int FIRE_DURATION = 2;
    private final Location destination;
    private final Random random = new Random();

    public TeleportTubeStrategy(Location destination) {
        this.destination = destination;
    }

    @Override
    public Location getDestination(Actor actor, GameMap map) {
        if (random.nextBoolean()){
            return getRandomValidLocation(destination.map(), actor);
        }
        return destination;
    }

    private Location getRandomValidLocation(GameMap destMap, Actor actor) {
        int maxX = destMap.getXRange().max();
        int maxY = destMap.getYRange().max();

        Location randomLocation;
        do {
            int x = random.nextInt(maxX + 1);
            int y = random.nextInt(maxY + 1);
            randomLocation = destMap.at(x,y);
        } while (!randomLocation.canActorEnter(actor));
        return randomLocation;
    }

    @Override
    public void applySideEffects(Actor actor, Location source, Location destination, GameMap map) {
        for (Location adjacent: destination.getNearbyLocations(ADJACENT_TILE_DISTANCE)) {
            if (adjacent.getGround() instanceof Fire fire) {
                fire.addStack(FIRE_DURATION);
            }
            else {
                adjacent.setGround(new Fire(adjacent.getGround(), FIRE_DURATION));
            }
        }
    }

    @Override
    public String menuDescription(Actor actor) {
        return "Teleport " + actor + " to " + destination.map().toString() + "(" + destination.x() + "," + destination.y() + ")";
    }
}
