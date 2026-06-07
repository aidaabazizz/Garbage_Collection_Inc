package game.capabilities;

import edu.monash.fit2099.engine.GameEntity;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.capabilities.Status;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.Location;
import game.enums.Ability;

/**
 * A status applied to a friendly actor (or the whistle-user) by a
 * CommandWhistle.
 * On the first tick it fires an AoE knockback pulse,
 * pushing non-worker actors 2 tiles away from the whistle-user's original
 * position. Blocked paths deal impact damage instead.
 *
 *
 * @author Chathya Attanayake
 * @version 1.0
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

    private final Display display = new Display();

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
        if (hasPulsed) return;
        hasPulsed = true;

        display.println("\u001B[33m>>> A tactical pulse erupts from " + entity + "!\u001B[0m");

        for (Exit exit : origin.getExits()) {
            Location adj = exit.getDestination();
            if (adj.containsAnActor() && !adj.getActor().hasAbility(Ability.WORKER)) {
                Actor enemy = adj.getActor();
                Location dest = findKnockbackDest(adj, exit.getName());

                if (dest != null && dest.canActorEnter(enemy)) {
                    origin.map().moveActor(enemy, dest);
                    display.println(">>> " + enemy + " is blasted away!");
                } else {
                    enemy.hurt(IMPACT_DAMAGE);

                    String obstacleName = "an obstacle";
                    if (dest == null) {
                        obstacleName = "the facility boundary";
                    } else if (dest.containsAnActor()) {
                        obstacleName = dest.getActor().toString();
                    } else {
                        obstacleName = dest.getGround().toString();
                    }

                    display.println(String.format(">>> %s slams into %s and takes %d impact damage!",
                            enemy, obstacleName, IMPACT_DAMAGE));
                }
            }
        }
    }

    /**
     * Finds the tile one step further in the same direction from a given location.
     *
     * @param from the starting adjacent location
     * @param dir  the direction name (exit name)
     * @return the next location in that direction, or null if none exists
     */
    private Location findKnockbackDest(Location from, String dir) {
        for (Exit e : from.getExits()) if (e.getName().equals(dir)) return e.getDestination();
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

    /**
     * Returns a readable description of this status.
     *
     * @return status label
     */
    @Override
    public String toString() {
        return "Motivation (AoE pulse pending)";
    }

}