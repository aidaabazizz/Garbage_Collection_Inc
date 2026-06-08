package game.teleportstrategies;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;

import java.util.Random;


/**
 * An abstract base class that serves as a foundation for all teleportation behaviors
 * within the Eclipse Nebula. This class centralizes shared utility logic to fulfill
 * the DRY principle and provides a stable framework for
 * both Requirement 2 and Requirement 3 features.
 *
 * @author Jewell Gomes
 * @author Victoria Tay Wen Xie (modified by)
 */
public abstract class BaseTeleportStrategy {

    private Actor teleportingActor;

    /**
     * The target location for teleportation.
     */
    protected Location targetLocation;

    /**
     * The name of the teleportation destination.
     */
    private final String destinationName;

    /**
     * Random number generator used by teleportation strategies.
     */
    protected static final Random RANDOM = new Random();

    /**
     * Constructs a teleportation strategy with a fixed destination.
     *
     * @param targetLocation the target location for teleportation
     * @param destinationName the name of the destination
     */
    public BaseTeleportStrategy(Location targetLocation, String destinationName) {
        this.targetLocation = targetLocation;
        this.destinationName = destinationName;
    }

    /**
     * Constructs a teleportation strategy without a fixed destination.
     *
     * @param destinationName the name of the destination
     */
    public BaseTeleportStrategy(String destinationName) {
        this.targetLocation = null;
        this.destinationName = destinationName;
    }

    /**
     * Finds a random valid location on the given map that can be occupied
     * by the specified actor.
     *
     * @param map the map to search
     * @param actor the actor to be teleported
     * @return a random valid location, or null if none is found
     */
    public static Location findRandomValidLocation(GameMap map, Actor actor) {
        Random rand = new Random();
        int xMin = map.getXRange().min();
        int xMax = map.getXRange().max();
        int yMin = map.getYRange().min();
        int yMax = map.getYRange().max();

        for (int i = 0; i < 100; i++) {
            int x = xMin + rand.nextInt((xMax - xMin) + 1);
            int y = yMin + rand.nextInt((yMax - yMin) + 1);
            Location candidate = map.at(x, y);

            if (candidate.getGround().canActorEnter(actor) && !candidate.containsAnActor()) {
                return candidate;
            }
        }
        return null;
    }

    /**
     * Executes the teleportation process.
     * Applies source effects, determines the destination, moves the actor,
     * and applies destination effects.
     *
     * @param actor the actor being teleported
     * @param source the location the actor is teleporting from
     */
    public final void teleport(Actor actor, Location source) {
        if (actor == null || source == null) return;
        this.teleportingActor = actor; // store before use
        applySourceEffects(source);
        Location destination = determineActualDestination(
                this.targetLocation != null ? this.targetLocation : source
        );
        if (destination == null) return;
        destination.map().moveActor(actor, destination);
        applyDestinationEffects(destination);
    }

    protected Actor getTeleportingActor() {
        return teleportingActor;
    }

    /**
     * Returns the name of the teleportation destination.
     *
     * @return the destination name
     */
    public String getDestinationName() {
        return this.destinationName;
    }

    /**
     * Determines the actual destination location.
     * Subclasses may override this method to implement custom destination
     * selection logic.
     *
     * @param target the intended destination location
     * @return the actual destination location
     */
    protected Location determineActualDestination(Location target) {
        return target;
    }

    /**
     * Returns a description of the teleportation action.
     *
     * @param actor the actor performing the teleportation
     * @return a string describing the teleportation action
     */
    public String getActionDescription(Actor actor) {
        return actor + " teleports to " + getDestinationName();
    }

    /**
     * Applies effects at the source location before teleportation.
     *
     * @param source the source location
     */
    protected abstract void applySourceEffects(Location source);

    /**
     * Applies effects at the destination location after teleportation.
     *
     * @param destination the destination location
     */
    protected abstract void applyDestinationEffects(Location destination);
}