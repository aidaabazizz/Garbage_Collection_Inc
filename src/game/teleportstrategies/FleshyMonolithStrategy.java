package game.teleportstrategies;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;

/**
 * Teleportation strategy for the Fleshy Monolith.
 * When a worker is adjacent, the Monolith violently warps them to a random valid location.
 *
 * @author REQ2 Implementation
 * @version 1.0
 */
public class FleshyMonolithStrategy extends BaseTeleportStrategy {

    private static final String DESTINATION_NAME = "random location";
    private final Display display;

    /**
     * Constructor for FleshyMonolithStrategy.
     * Uses the constructor without a fixed destination since destination is random.
     */
    public FleshyMonolithStrategy() {
        super(DESTINATION_NAME);
        this.display = new Display();
    }

    @Override
    protected Location determineActualDestination(Location target) {
        // Override to find a random valid location instead of using the target parameter
        Actor actor = getTeleportingActor();
        if (actor == null) return null;

        GameMap map = target.map();
        return BaseTeleportStrategy.findRandomValidLocation(map, actor);
    }

    @Override
    protected void applySourceEffects(Location source) {
        // No source effects for Monolith teleportation
    }

    @Override
    protected void applyDestinationEffects(Location destination) {
        display.println("The worker arrives with a violent thud at " + destination + "!");
    }

    @Override
    public String getActionDescription(Actor actor) {
        return actor + " is violently warped to a random location!";
    }
}