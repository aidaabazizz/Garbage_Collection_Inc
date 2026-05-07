package game.capabilities;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;

/**
 * This is a strategy interface for teleportation behaviours and it defines the core
 * operations of different teleportation mechanism. It serves as the template for all the
 * teleportable grounds or items.
 *
 * @author Victoria Tay Wen Xie
 * @version 1.0
 */
public interface TeleportStrategy {
    Location getDestination(Actor actor, GameMap map);
    default void applySideEffects(Actor actor, Location source, Location destination, GameMap map) {}
    String menuDescription(Actor actor);
}
