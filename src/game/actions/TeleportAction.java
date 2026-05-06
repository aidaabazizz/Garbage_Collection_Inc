package game.actions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.teleport.TeleportStrategy;

/**
 * Generic teleport action that delegates behaviour to a TeleportStrategy.
 */
public class TeleportAction extends Action {

    private final TeleportStrategy strategy;

    public TeleportAction(TeleportStrategy strategy) {
        this.strategy = strategy;
    }

    @Override
    public String execute(Actor actor, GameMap map) {

        // Get actor current location
        Location source = map.locationOf(actor);

        // Strategy decides destination (and may include randomness / menu)
        Location destination = strategy.getDestination(actor, map);

        if (destination == null) {
            return actor + " cancels teleportation.";
        }

        // Move actor
        map.moveActor(actor, destination);

        // Apply side effects (burn, toxic, spawn, etc.)
        strategy.applySideEffects(actor, source, destination, map);

        return actor + " teleported from " + source + " to " + destination;
    }

    @Override
    public String menuDescription(Actor actor) {
        return strategy.menuDescription(actor);
    }
}
