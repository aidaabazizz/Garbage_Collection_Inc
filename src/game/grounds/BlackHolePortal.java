package game.grounds;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.*;
import game.capabilities.BlackHoleStatus;
import game.enums.DistortionCapability;
import game.sanctuary.DistortionSource;
import game.utils.SpatialSearch;

import java.util.List;

public class BlackHolePortal  extends Ground implements DistortionSource {


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
                actor.addStatus(new BlackHoleStatus(10));
            }
        }
    }

    @Override
    public String audit(QuotaManager manager, Location location) {
        manager.addCompanyCredits(25);
        this.auditCooldown = 5; // Weakened for 5 turns

        // Final Effect: Warp one nearby actor instantly
        List<Actor> nearby = SpatialSearch.getActorsWithinDistance(location, 2);
        String effect = "The Black Hole flares! ";
        if (!nearby.isEmpty()) {
            Actor target = nearby.get(0);
            location.map().moveActor(target, location.map().at(0,0)); // Warp to safety/start
            effect += target + " was sucked into a gravity spike and warped away!";
        } else {
            effect += "Gravity ripples outward, but no signatures were caught.";
        }

        return effect + " [Quota +25]";
    }

    @Override public String releaseDistortion(Actor a, GameMap m, Location l) { return ""; }

    @Override
    public String stabilise(Location location) {
        location.setGround(new Floor());
        return "The Black Hole has been stabilized and collapsed.";
    }


}