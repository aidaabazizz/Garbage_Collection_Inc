package game.grounds;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.*;
import game.capabilities.BlackHoleStatus;
import game.enums.DistortionCapability;
import game.sanctuary.DistortionSource;

public class BlackHolePortal  extends Ground implements DistortionSource {

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

    @Override public String releaseDistortion(Actor a, GameMap m, Location l) { return ""; }

    @Override
    public String stabilise(Location location) {
        location.setGround(new Floor());
        return "The Black Hole has been stabilized and collapsed.";
    }
}