package game.grounds;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.*;
import game.capabilities.BlackHoleStatus;
import game.enums.DistortionCapability;
import game.managers.QuotaManager;
import game.sanctuary.DistortionSource;
import game.utils.SpatialSearch;

import java.util.List;

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

    public BlackHolePortal() {
        super('Ω', "Black Hole Portal");
        this.enableAbility(DistortionCapability.CORRUPTED);
    }

    @Override
    public void tick(Location location) {
        if (location.containsAnActor()) {
            Actor actor = location.getActor();
            // RULE: If Bob enters, he starts warping for 10 turns
            if (!actor.hasStatus(BlackHoleStatus.class)) {
                actor.addStatus(new BlackHoleStatus(WARP_DURATION));
            }
        }
    }

    @Override
    public String audit(QuotaManager manager, Location location) {
        manager.addCompanyCredits(AUDIT_CREDITS);
        this.auditCooldown = AUDIT_COOLDOWN; // Weakened for 5 turns

        // Final Effect: Warp one nearby actor instantly
        List<Actor> nearby = SpatialSearch.getActorsWithinDistance(location, 2);
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

    @Override public String releaseDistortion(Actor a, GameMap m, Location l) { return ""; }

    @Override
    public String stabilise(Location location) {
        location.setGround(new Floor());
        return "The Black Hole has been stabilized and collapsed.";
    }


}