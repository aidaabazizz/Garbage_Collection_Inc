package game.grounds;

import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Ground;
import game.enums.DistortionCapability;
import game.capabilities.ReversedMovementStatus;
import game.sanctuary.DistortionSource;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Location;


public class UpsideDownPortal extends Ground implements DistortionSource{

    public UpsideDownPortal() {
        super('Ω', "Upside Down Portal");
        this.enableAbility(DistortionCapability.CORRUPTED);
    }

    @Override
    public void tick(Location location) {
        if (location.containsAnActor()) {
            releaseDistortion(location.getActor(), location.map(), location);
        }
    }

    @Override
    public String releaseDistortion(Actor actor, GameMap map, Location location) {
        if (!actor.hasStatus(ReversedMovementStatus.class)) {
            actor.addStatus(new ReversedMovementStatus(3));
            return actor + " is disoriented by the Upside Down Portal!";
        }
        return "";
    }

    @Override
    public String stabilise(Location location) {
        // Complex Effect: Permanent terrain mutation
        location.setGround(new Floor());
        return "The portal's spatial energy collapses into a normal floor.";
    }
}
