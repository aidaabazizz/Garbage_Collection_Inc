package game.teleportstrategies;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.capabilities.TeleportStrategy;
import game.grounds.Fire;

import java.util.List;
import java.util.Random;

public class FixedTeleportStrategy implements TeleportStrategy {

    private final static int ADJACENT_TILE_DISTANCE = 1;
    private final static int FIRE_DURATION = 2;
    private final List<Location> destinations;
    private final Random random = new Random();

    public FixedTeleportStrategy(List<Location> destinations) {
        this.destinations = destinations;
    }

    @Override
    public Location getDestination(Actor actor, GameMap map) {
        Location chosenDestination = destinations.get(random.nextInt(destinations.size()));
        if (random.nextBoolean()){
            return getRandomValidLocation(chosenDestination.map(), actor);
        }
        return chosenDestination;
    }

    private Location getRandomValidLocation(GameMap destMap, Actor actor) {
        int maxX = destMap.getXRange().max();
        int maxY = destMap.getYRange().max();

        Location randomLocation;
        do {
            int x = random.nextInt(maxX);
            int y = random.nextInt(maxY);
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
        return "Teleport " + actor + " through Teleportation Tube to other location.";
    }
}
