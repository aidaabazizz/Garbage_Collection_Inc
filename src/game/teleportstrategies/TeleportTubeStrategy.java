package game.teleportstrategies;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.grounds.Fire;

/**
 * Teleportation Tube strategy.
 * It will teleport worker to fixed destinations with 50% malfunction chancs.
 * The effect after teleportation is burning adjacent tiles on arrival.
 *
 * @author Victoria Tay Wen Xie
 * @version 1.0
 */
public class TeleportTubeStrategy extends BaseTeleportStrategy {
    /** Adjacent tile search radius. */
    private static final int ADJACENT_TILE_DISTANCE = 1;
    /** Fire duration in turns */
    private static final int FIRE_DURATION = 2;
    /** Fixed destination location */
    private Location destination;

    /**
     * This constructor help creates a strategy for a specifc destination
     * @param destination the fixed teleport destination
     */
    public TeleportTubeStrategy(Location destination) {
        this.destination = destination;
    }

    /**
     * Returns destination with 50% malfunction chance.
     * @param actor the teleporting actor
     * @param map the current map
     * @return predetermined locations or random destinations
     */
    @Override
    public Location getDestination(Actor actor, GameMap map) {
        if (random.nextBoolean()) {
            return getRandomValidLocation(destination.map(), actor);
        }
        return destination;
    }

    /**
     * Sets adjacent tiles on fire at the destination
     * @param actor       The actor being moved.
     * @param source      The location where the teleportation started.
     * @param destination The location where the actor arrived.
     * @param map         The map where the side effects should be applied.
     */
    @Override
    public void applySideEffects(Actor actor, Location source, Location destination, GameMap map) {
        for (Location adjacent : destination.getNearbyLocations(ADJACENT_TILE_DISTANCE)) {
            Ground ground = adjacent.getGround();
            if (ground.canActorEnter(actor)) {
                adjacent.setGround(new Fire(ground, FIRE_DURATION));
            }
        }
    }

    /**
     * This return menu description
     * @param actor the teleporting actor
     * @return menu description string
     */
    @Override
    public String menuDescription(Actor actor) {
        return "Teleport " + actor + " to " + destination.map() + "(" + destination.x() + "," + destination.y() + ") using teleportation tube.";
    }
}
