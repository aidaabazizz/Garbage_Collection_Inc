package game.sanctuary;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;

public interface DistortionSource {
    String releaseDistortion(Actor actor, GameMap map, Location location);

    String stabilise(Location location);

    String audit(QuotaManager quotaManager, Location location);
}
