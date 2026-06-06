package game.grounds;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.capabilities.BurningStatus;
import game.capabilities.FireStackable;
import game.enums.Ability;
import game.enums.DistortionCapability;
import game.managers.QuotaManager;
import game.sanctuary.DamageInterceptor;
import game.sanctuary.DistortionSource;
import game.sanctuary.Extinguishable;

/**
 * A timed hazard ground spawned by CorruptedSafeHouse.
 * Damages non-worker actors each turn.
 * Workers are immune — BlueFire is meant to deter enemies, not harm the worker it protects.
 * Restores the original ground when extinguished.
 */
public class BlueFire extends Ground implements Extinguishable, DistortionSource, FireStackable {

    private int lifespan;
    private final Ground originalGround;

    private static final int MAX_LIFESPAN = 5;
    private static final int BURN_DURATION = 3;
    private static final int FLARE_DAMAGE = 3;
    private static final int AUDIT_CREDITS = 10;
    private static final int FLARE_RADIUS = 1;

    private final Display display = new Display();


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
            Actor actor = location.getActor();
            if (!actor.hasAbility(DamageInterceptor.PROTECTED) && actor.isConscious()) {
                actor.addStatus(new BurningStatus(BURN_DURATION));
                actor.addStatus(new BurningStatus(BURN_DURATION));
                display.println("\u001B[34m" + actor + " is scorched by the Blue Fire for 2 damage!\u001B[0m");

            }
        }

        lifespan--;
        if (lifespan <= 0) {
            extinguish(location);
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
    public String audit(QuotaManager manager, Location location) {
        manager.addCompanyCredits(AUDIT_CREDITS);

        // Final Effect: Flare damage
        for (Location adj : location.getNearbyLocations(FLARE_RADIUS)) {
            if (adj.containsAnActor()) adj.getActor().hurt(FLARE_DAMAGE);
        }

        this.extinguish(location); // Burns out immediately
        return "The Blue Fire flares violently and is consumed by the audit scan. [Quota +10]";
    }

    @Override
    public void extinguish(Location location) {
        location.setGround(originalGround);
    }

    @Override
    public void addStack() {
        if (this.lifespan < MAX_LIFESPAN) {
            this.lifespan = MAX_LIFESPAN; // refresh, don't stack infinitely
        }
    }

    // DistortionSource required overrides
    @Override public String releaseDistortion(Actor actor, GameMap map, Location location) { return ""; }

    @Override public String stabilise(Location location) { extinguish(location); return "Extinguished"; }
}