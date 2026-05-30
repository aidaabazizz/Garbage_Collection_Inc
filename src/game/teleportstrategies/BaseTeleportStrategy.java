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
 */
public abstract class BaseTeleportStrategy {
    protected Location targetLocation;
    private final String destinationName;
    protected final Random random = new Random();

    public BaseTeleportStrategy(Location targetLocation, String destinationName) {
        this.targetLocation = targetLocation;
        this.destinationName = destinationName;
    }

    public BaseTeleportStrategy(String destinationName) {
        this.targetLocation = null;
        this.destinationName = destinationName;
    }

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
     * Template method that handles the core execution loop.
     */
    public final void teleport(Actor actor, Location source) {
        if (actor == null || source == null) {
            return;
        }

        // 1. Trigger source-side effects (e.g., Alien Cube turning ground to Toxic Waste)
        applySourceEffects(source);

        // 2. Determine actual destination map and location
        Location destination = determineActualDestination(this.targetLocation != null ? this.targetLocation : source);
        if (destination == null) return;

        // 3. Move the actor safely
        GameMap targetMap = destination.map();
        targetMap.moveActor(actor, destination);

        // 4. Trigger destination side-effects
        applyDestinationEffects(destination);
    }

    public String getDestinationName() {
        return this.destinationName;
    }

    protected Location determineActualDestination(Location target) {
        return target;
    }

    public String getActionDescription(Actor actor) {
        return actor + " teleports to " + getDestinationName();
    }

    protected abstract void applySourceEffects(Location source);
    protected abstract void applyDestinationEffects(Location destination);
}