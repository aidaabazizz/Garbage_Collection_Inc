package game.sanctuary;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;

public interface SanctuaryTool {
    String activateSanctuaryEffect(Actor actor, GameMap map, Location location);
}
