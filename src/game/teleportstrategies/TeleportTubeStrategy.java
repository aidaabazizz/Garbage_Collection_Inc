package game.teleportstrategies;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.grounds.Fire;
public class TeleportTubeStrategy extends BaseTeleportStrategy {
    private static final int ADJACENT_TILE_DISTANCE = 1;
    private static final int FIRE_DURATION = 2;
    private Location destination;

    public TeleportTubeStrategy(Location destination) {
        this.destination = destination;
    }

    @Override
    public Location getDestination(Actor actor, GameMap map) {
        if (random.nextBoolean()) {
            return getRandomValidLocation(destination.map(), actor);
        }
        return destination;
    }

    @Override
    public void applySideEffects(Actor actor, Location source, Location destination, GameMap map) {
        // Burn adjacent tiles at destination (2 turns)
        for (Location adjacent : destination.getNearbyLocations(ADJACENT_TILE_DISTANCE)) {
            Ground ground = adjacent.getGround();
            if (ground.canActorEnter(actor)) {
                adjacent.setGround(new Fire(ground, FIRE_DURATION));
            }
        }
    }
    @Override
    public String menuDescription(Actor actor) {
        return "Teleport " + actor + " to " + destination.map() + "(" + destination.x() + "," + destination.y() + ") using teleportation tube.";
    }
}
