package game.grounds;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.enums.DistortionCapability;
import game.capabilities.KillerInstinctStatus;
import game.sanctuary.DistortionSource;

public class RageGround extends Ground implements DistortionSource {
    public RageGround() {
        super('╬', "Rage Ground");
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
        actor.addStatus(new KillerInstinctStatus(3));
        return actor + " is consumed by rage!";
    }

    public String stabilise(Location location) {
        location.setGround(new Floor());
        return "The Rage Ground has been neutralized and restored to normal floor.";
    }

}
