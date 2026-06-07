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
 * A timed hazard ground representing mystical blue flames spawned by a Corrupted Safe House.
 *
 * Blue Fire it damages and applies BurningStatus to non-worker actors and workers, only remains harmless to protected workers.
 * The fire is lit to the presence of a worker in the Corrupted Safe House; if the worker leaves,
 * the fire extinguishes immediately.
 *
 * When the fire's lifespan ends or it is extinguished, it restores the originalGround
 * that existed before the fire was spawned.
 *
 * @author Chathya Attanayake
 * @version 1.0
 */
public class BlueFire extends Ground implements Extinguishable, DistortionSource, FireStackable {
    /** Current turns remaining before the fire self-extinguishes. */
    private int lifespan;
    /** The ground type to restore once the fire is extinguished. */
    private final Ground originalGround;
    /** The maximum turns a single instance of Blue Fire can last. */
    private static final int MAX_LIFESPAN = 5;
    /** The duration of the burning status applied to actors caught in the fire. */
    private static final int BURN_DURATION = 3;
    /** Damage dealt to all nearby actors when the fire is audited (flares). */
    private static final int FLARE_DAMAGE = 3;
    /** Credits awarded to the company for auditing this hazard. */
    private static final int AUDIT_CREDITS = 10;
    /** The radius of the flare effect during an audit. */
    private static final int FLARE_RADIUS = 1;
    /** The radius of the flare effect during an audit. */
    private final Display display = new Display();

    /**
     * Constructs a  BlueFire hazard with a fixed lifespan and a restoration target.
     *
     * @param lifespan       The initial duration of the fire.
     * @param originalGround The ground that will replace the fire once it expires.
     */
    public BlueFire(int lifespan, Ground originalGround) {
        super('^', "Blue Fire");
        this.lifespan = lifespan;
        this.originalGround = originalGround;
        this.enableAbility(DistortionCapability.ACTIVE_HAZARD);
    }

    /**
     * Updates the fire state each turn.
     *
     * The fire will extinguish if the safe house that spawned it is no longer
     * occupied by a worker. If an actor is standing on the fire and is not
     * protected, they receive double  BurningStatus and a notification is displayed.
     *
     * @param location The current location of the Blue Fire.
     */
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
     * Uses capability check
     *
     *  @param location The location of the fire.
     *  @return true if an occupied safe house is adjacent, false otherwise.
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

    /**
     * Audits the Blue Fire, providing credits but causing a violent flare.
     *
     * The flare deals damage to all actors within the FLARE_RADIUS.
     * The fire is consumed (extinguished) immediately after the audit.
     *
     * @param manager  The QuotaManager to receive the credits.
     * @param location The location of the fire.
     * @return A description of the audit result and flare damage.
     */
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

    /**
     * Reverts the ground at this location back to the originalGround.
     *
     * @param location The location to extinguish.
     */
    @Override
    public void extinguish(Location location) {
        location.setGround(originalGround);
    }

    /**
     * Refreshes the lifespan of the fire if another fire instance attempts to stack.
     * This prevents infinite stacking while maintaining the hazard's presence.
     */
    @Override
    public void addStack() {
        if (this.lifespan < MAX_LIFESPAN) {
            this.lifespan = MAX_LIFESPAN; // refresh, don't stack infinitely
        }
    }

    /**
     * Stabilizes the fire, causing it to be extinguished immediately.
     *
     * @param location The location of the fire.
     */
    @Override public void stabilise(Location location) { extinguish(location); }
}