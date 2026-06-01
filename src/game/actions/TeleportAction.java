package game.actions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.teleportstrategies.BaseTeleportStrategy;

/**
 * This teleport action delegates its behaviour to the teleport strategies.
 * This action follows the Strategy pattern, allowing different teleportation behaviours
 *
 * @author Victoria Tay Wen Xie
 * @version 2.0
 */
public class TeleportAction extends Action {

    /**
     * The teleportation strategy used to perform the action.
     */
    private final BaseTeleportStrategy teleportStrategy;

    /**
     * Constructs a TeleportAction with a given teleport strategy.
     *
     * @param teleportStrategy the strategy that defines teleport behaviour
     */
    public TeleportAction(BaseTeleportStrategy teleportStrategy) {
        this.teleportStrategy = teleportStrategy;
    }

    /**
     * Executes the teleport action.
     * Retrieves the actor's current location and delegates the teleport logic
     * to the selected strategy.
     *
     * @param actor the actor performing the action
     * @param map the game map the actor is currently on
     * @return a string describing the result of the teleportation
     */
    @Override
    public String execute(Actor actor, GameMap map) {
        Location sourceLocation = map.locationOf(actor);
        if (sourceLocation == null) {
            return actor + " cannot determine its baseline coordinates.";
        }
        teleportStrategy.teleport(actor, sourceLocation);
        return teleportStrategy.getActionDescription(actor);
    }

    /**
     * Returns the menu description shown to the player.
     *
     * @param actor the actor performing the action
     * @return a string describing the teleport action
     */
    @Override
    public String menuDescription(Actor actor) {
        return teleportStrategy.getActionDescription(actor);
    }
}