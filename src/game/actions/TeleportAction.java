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
 * @version 1.0
 */
public class TeleportAction extends Action {
    private final BaseTeleportStrategy teleportStrategy;

    public TeleportAction(BaseTeleportStrategy teleportStrategy) {
        this.teleportStrategy = teleportStrategy;
    }

    @Override
    public String execute(Actor actor, GameMap map) {
        Location sourceLocation = map.locationOf(actor);
        if (sourceLocation == null) {
            return actor + " cannot determine its baseline coordinates.";
        }

        teleportStrategy.teleport(actor, sourceLocation);
        return actor + " travels to " + teleportStrategy.getDestinationName();
    }

    @Override
    public String menuDescription(Actor actor) {
        return teleportStrategy.getActionDescription(actor);
    }
}