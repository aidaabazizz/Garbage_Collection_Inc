package game.capabilities;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.actors.ContractedWorker;

import java.util.List;

public interface TeleportStrategy {
    Location getDestination(Actor actor, GameMap map);
    void applySideEffects(Actor actor, Location source, Location destination, GameMap map);
    String menuDescription(Actor actor);
}
