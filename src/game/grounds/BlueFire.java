package game.grounds;

import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.enums.Ability;
import game.enums.DistortionCapability;
import game.sanctuary.Extinguishable;

/**
 * A timed hazard ground spawned by CorruptedSafeHouse.
 * Damages non-worker actors each turn.
 * Workers are immune — BlueFire is meant to deter enemies, not harm the worker it protects.
 * Restores the original ground when extinguished.
 */
public class BlueFire extends Ground implements Extinguishable {

    private int lifespan;
    private final Ground originalGround;

    public BlueFire(int lifespan, Ground originalGround) {
        super('^', "Blue Fire");
        this.lifespan = lifespan;
        this.originalGround = originalGround;
        this.enableAbility(DistortionCapability.ACTIVE_HAZARD);
    }

    @Override
    public void tick(Location location) {
        // Self-extinguish if the safe house that spawned us no longer has a worker
        if (!isSafeHouseOccupied(location)) {
            extinguish(location);
            return;
        }

        if (location.containsAnActor()) {
            var actor = location.getActor();
            if (!actor.hasAbility(Ability.WORKER)) {
                actor.hurt(2);
                System.out.println("\u001B[34m" + actor + " is scorched by the Blue Fire for 2 damage!\u001B[0m");
            }
        }

        if (lifespan <= 1) {
            extinguish(location);
        } else {
            lifespan--;
        }
    }

    /**
     * Checks if any adjacent tile is a CorruptedSafeHouse with a worker on it.
     * BlueFire only persists while a worker is in the safe house that spawned it.
     * Uses capability check — no instanceof (DIP).
     */
    private boolean isSafeHouseOccupied(Location location) {
        for (Exit exit : location.getExits()) {
            Location adj = exit.getDestination();
            if (adj.getGround().hasAbility(DistortionCapability.CORRUPTED)
                    && adj.containsAnActor()
                    && adj.getActor().hasAbility(Ability.WORKER)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void extinguish(Location location) {
        location.setGround(originalGround);
    }
}