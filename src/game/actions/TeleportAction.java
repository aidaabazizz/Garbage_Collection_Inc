package game.actions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.actors.ContractedWorker;
import game.capabilities.TeleportStrategy;
import game.enums.Ability;

/**
 * This teleport action delegates its behaviour to the teleport strategies.
 * This action follows the Strategy pattern, allowing different teleportation behaviours
 *
 * @author Victoria Tay Wen Xie
 * @version 1.0
 */
public class TeleportAction extends Action {

    /**
     * Teleportation strategy that defines specific behaviour for each teleportable
     */
    private final TeleportStrategy strategy;

    /**
     * Constructor for TeleportAction with the specified teleportation strategt
     * @param strategy this defines how teleportation works
     */
    public TeleportAction(TeleportStrategy strategy) {
        this.strategy = strategy;
    }

    /**
     * This executes the teleportation process
     * @param actor The actor performing the action.
     * @param map The map the actor is on.
     * @return A string that describes the result of the teleportation
     */
    @Override
    public String execute(Actor actor, GameMap map) {
        Location source = map.locationOf(actor);
        Location destination = strategy.getDestination(actor, map);

        if (destination == null) {
            return actor + " cancels teleportation.";
        }
        map.moveActor(actor, destination);
        strategy.applySideEffects(actor, source, destination, map);
        return actor + " teleported from " + source + " to " + destination;
    }

    /**
     * Returns the menu description for this teleportation action.
     * @param actor The actor performing the action.
     * @return a string that describes the action
     */
    @Override
    public String menuDescription(Actor actor) {
        if (actor.hasAbility(Ability.WORKER)) {
            return strategy.menuDescription(actor);
        }
        return null;
    };
}
