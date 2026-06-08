package game.grounds;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.*;
import game.capabilities.BlackHoleStatus;
import game.enums.DistortionCapability;
import game.managers.QuotaManager;
import game.sanctuary.DistortionSource;
import game.utils.SpatialSearch;
import java.util.List;

/**
 * A hazardous ground type that represents a spatial rift.
 *
 * The Black Hole Portal acts as a DistortionSource. It exerts a "warp"
 * effect on any actor standing on it and can be "audited" to generate company credits,
 * though doing so causes a gravity flare that displaces nearby entities.
 *
 * It is initialized with the CORRUPTED capability, allowing it to be stabilized by workers
 *
 * @author Chathya Attanayake
 * @version 1.0
 */
public class BlackHolePortal  extends Ground implements DistortionSource {
    /** Duration of the warp status applied to actors entering the portal. */
    private static final int WARP_DURATION = 10;

    /** Credits awarded to the company when this portal is audited. */
    private static final int AUDIT_CREDITS = 25;

    /** Number of turns this portal is weakened after an audit. */
    private static final int AUDIT_COOLDOWN = 5;

    /** Radius used to scan for nearby actors during an audit flare. */
    private static final int AUDIT_SCAN_RADIUS = 2;

    /** Safe warp destination X coordinate. */
    private static final int SAFE_X = 0;

    /** Safe warp destination Y coordinate. */
    private static final int SAFE_Y = 0;

    /** Cooldown turns after being audited. */
    private int auditCooldown = 0;

    /**
     * Constructor that initializes the portal with the 'Ω' symbol and identifies it as corrupted.
     */
    public BlackHolePortal() {
        super('Ω', "Black Hole Portal");
        this.enableAbility(DistortionCapability.CORRUPTED);
    }

    /**
     * Updates the portal's state every turn.
     *
     * If the portal is on an audit cooldown, it is considered "weakened" and
     * will not affect actors. Otherwise, if an actor is standing on the portal,
     * it applies a BlackHoleStatus to them.
     *
     * @param location The current location of the Black Hole Portal.
     */
    @Override
    public void tick(Location location) {

        // portal is weakened and won't apply warp
        if (auditCooldown > 0) {
            auditCooldown--;
            return;
        }

        if (location.containsAnActor()) {
            Actor actor = location.getActor();
            // If worker enters, he starts warping for 10 turns
            if (!actor.hasStatus(BlackHoleStatus.class)) {
                actor.addStatus(new BlackHoleStatus(WARP_DURATION));
            }
        }
    }

    /**
     * Performs an audit on the portal, generating resources at the risk of a gravity spike.
     *
     * This action awards credits to the QuotaManager and puts the portal into
     * a cooldown state. Additionally, it triggers a "flare" that warps the first
     * detected nearby actor to a safe destination (0, 0).
     *
     * @param manager  The QuotaManager to receive the credits.
     * @param location The location of the portal being audited.
     * @return A string describing the result of the audit and the flare effect.
     */
    @Override
    public String audit(QuotaManager manager, Location location) {
        manager.addCompanyCredits(AUDIT_CREDITS);
        this.auditCooldown = AUDIT_COOLDOWN; // Weakened for 5 turns

        // Final Effect: Warp one nearby actor instantly
        List<Actor> nearby = SpatialSearch.getActorsWithinDistance(location, AUDIT_SCAN_RADIUS);
        String effect = "The Black Hole flares! ";
        if (!nearby.isEmpty()) {
            Actor target = nearby.get(0);
            location.map().moveActor(target, location.map().at(SAFE_X,SAFE_Y)); // Warp to safety/start
            effect += target + " was sucked into a gravity spike and warped away!";
        } else {
            effect += "Gravity ripples outward, but no signatures were caught.";
        }

        return effect + " [Quota +" + AUDIT_CREDITS + "]";
    }


    /**
     * Stabilizes the spatial rift, removing the hazard from the map.
     *
     * @param location The location of the portal to be stabilized.
     */
    @Override
    public void stabilise(Location location) {
        location.setGround(new Floor());
    }

}