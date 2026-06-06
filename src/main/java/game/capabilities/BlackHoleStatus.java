package game.capabilities;

import edu.monash.fit2099.engine.GameEntity;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import java.util.Random;

/**
 * Rule 2: Complex Effect (Terrain-Induced Spatiotemporal Warping).
 * Reuses A2 DamageOverTimeStatus to handle duration and crushing damage.
 */
public class BlackHoleStatus extends DamageOverTimeStatus {
    private final Random random = new Random();
    /** Maximum number of attempts to find a safe warp destination. */
    private static final int MAX_WARP_ATTEMPTS = 20;
    private final Display display = new Display();

    public BlackHoleStatus(int turns) {
        super("Event Horizon Warp", turns);
    }

    @Override
    public void tickStatus(GameEntity entity, Location location) {
        // 1. Handle A2 Logic: Decrement turns and deal 1 crushing damage
        super.tickStatus(entity, location);

        // 2. Rule 2 Logic: Random Teleportation
        entity.asCapability(Actor.class).ifPresent(actor -> {
            GameMap map = location.map();
            Location randomDest = findSafeWarpLocation(map);

            if (randomDest != null) {
                map.moveActor(actor, randomDest);
                display.println("\u001B[35m>>> " + actor + " is warped by the black hole's gravity!\u001B[0m");
            }
        });
    }

    private Location findSafeWarpLocation(GameMap map) {
        int x, y;
        Location loc;
        // Try to find a safe spot (not a wall, no other actor)
        for (int i = 0; i <MAX_WARP_ATTEMPTS ; i++) {
            x = random.nextInt(map.getXRange().max());
            y = random.nextInt(map.getYRange().max());
            loc = map.at(x, y);
            if (loc.getGround().canActorEnter(null) && !loc.containsAnActor()) {
                return loc;
            }
        }
        return null; // Fallback
    }
}