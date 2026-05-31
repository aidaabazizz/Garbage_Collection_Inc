package game.capabilities;

import edu.monash.fit2099.engine.GameEntity;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.capabilities.Status;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.Location;
import game.enums.Ability;

/**
 * A status applied to a friendly actor (or the whistle-user) by a
 * {@link game.items.CommandWhistle}. On the first tick it fires an AoE knockback
 * pulse, pushing non-worker actors 2 tiles away from the whistle-user's original
 * position. Blocked paths deal impact damage instead.
 *
 * <p>Complex effect chain (Rule 2):
 * <pre>Status.tickStatus() → AoE scan from origin → map.moveActor() per enemy
 *     OR actor.hurt() on wall collision</pre>
 * </p>
 *
 * <p>Design: MotivationStatus owns the knockback physics (SRP).
 * The origin is stored at construction time so the pulse always radiates
 * from the whistle-user's tile, not from wherever the motivated actor is standing.
 * No instanceof checks — uses Ability.WORKER capability (DIP).</p>
 *
 * @author Your Name
 */
public class MotivationStatus implements Status {

    /** Damage dealt when an enemy is blocked by a wall. */
    private static final int IMPACT_DAMAGE = 5;

    /** Whether the pulse has already fired. */
    private boolean hasPulsed = false;

    /**
     * The location of the whistle-user at the moment of activation.
     * The pulse radiates outward from here, NOT from the motivated actor.
     */
    private final Location origin;

    /**
     * Creates a MotivationStatus anchored to the whistle-user's location.
     *
     * @param whistleUserLocation the tile the whistle-user was standing on
     */
    public MotivationStatus(Location whistleUserLocation) {
        this.origin = whistleUserLocation;
    }

    /**
     * On the first tick, scans all exits from the pulse origin. Any non-worker
     * actor in an adjacent tile is pushed 2 tiles in the same direction.
     * If the destination is blocked, the enemy stops at the last valid tile and
     * takes {@value IMPACT_DAMAGE} impact damage.
     *
     * @param entity   the actor holding this status (ignored — pulse uses origin)
     * @param location the actor's current location (ignored — pulse uses origin)
     */
    @Override
    public void tickStatus(GameEntity entity, Location location) {
        if (hasPulsed) {
            return;
        }
        hasPulsed = true;

        // AoE scan: examine every exit from the pulse origin
        for (Exit exit : origin.getExits()) {
            Location adjacent = exit.getDestination();

            if (!adjacent.containsAnActor()) {
                continue;
            }

            Actor target = adjacent.getActor();

            // Only knock back non-worker actors (enemies/creatures)
            // Uses capability check — no instanceof (DIP)
            if (target.hasAbility(Ability.WORKER)) {
                continue;
            }

            // Find the tile 2 steps further in the same direction
            Location knockbackDest = findKnockbackDestination(adjacent, exit.getName());

            if (knockbackDest != null && knockbackDest.canActorEnter(target)) {
                // Clear path — reposition the actor
                origin.map().moveActor(target, knockbackDest);
            } else {
                // Blocked — apply impact damage
                target.hurt(IMPACT_DAMAGE);
            }
        }
    }

    /**
     * Searches the exits of {@code from} to find the tile in the same named
     * direction, giving us the "2 tiles away" destination.
     *
     * @param from          the adjacent tile the enemy currently occupies
     * @param directionName the exit name (e.g. "North") to follow
     * @return the destination 2 tiles from origin, or null if not found
     */
    private Location findKnockbackDestination(Location from, String directionName) {
        for (Exit pushExit : from.getExits()) {
            if (pushExit.getName().equals(directionName)) {
                return pushExit.getDestination();
            }
        }
        return null;
    }

    /**
     * This status is active for exactly one turn — until the pulse fires.
     *
     * @return true until the pulse has fired
     */
    @Override
    public boolean isStatusActive() {
        return !hasPulsed;
    }
}