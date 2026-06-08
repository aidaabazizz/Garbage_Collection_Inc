package game.capabilities;

import edu.monash.fit2099.engine.GameEntity;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import java.util.Random;

/**
 * A status effect representing a black hole "event horizon" anomaly.
 *This effect combines two mechanics: Damage-over-time (inherited from DamageOverTimeStatus)
 * and random spatial warping  of affected entities
 * The entities affected by this status may be periodically relocated to random safe locations on the map.
 *
 * @author  Chathya Attanayake
 * @version  1.0
 */
public class BlackHoleStatus extends DamageOverTimeStatus {
    /** Random generator used for selecting warp destinations. */
    private final Random random = new Random();
    /** Maximum number of attempts to find a safe warp destination. */
    private static final int MAX_WARP_ATTEMPTS = 20;
    /** Display used to print warp events to the game console. */
    private final Display display = new Display();

    /**
     * Constructs a new  BlackHoleStatus.
     *
     * @param turns number of turns this status remains active
     */
    public BlackHoleStatus(int turns) {
        super("Event Horizon Warp", turns);
    }


    /**
     * Applies both damage-over-time effects and black hole spatial warping.
     * Each tick:
     * Applies base Damage Over Time damage and duration reduction. If the entity is an Actor,
     * attempts to teleport it to a safe tile
     *
     * @param entity  the affected game entity
     * @param location the entity's current location
     */
    @Override
    public void tickStatus(GameEntity entity, Location location) {
        // Decrement turns and deal 1 crushing damage
        super.tickStatus(entity, location);

        // Random Teleportation
        entity.asCapability(Actor.class).ifPresent(actor -> {
            GameMap map = location.map();
            Location randomDest = findSafeWarpLocation(map);

            if (randomDest != null) {
                map.moveActor(actor, randomDest);
                display.println("\u001B[35m>>> " + actor + " is warped by the black hole's gravity!\u001B[0m");
            }
        });
    }

    /**
     * Attempts to find a random valid location on the map that is safe
     * for actor movement (no blocking terrain and no other actor present).
     *
     * @param map the game map to search
     * @return a valid Location, or  null if none found
     */
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