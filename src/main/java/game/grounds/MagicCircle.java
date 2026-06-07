package game.grounds;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.actions.TeleportAction;
import game.enums.Ability;
import game.teleportstrategies.MagicCircleStrategy;

/**
 * Magic Circle is markings found inside 20-overflow. It allows within-map teleportation.
 * It is used to randomly teleport the worker to one of the other magic circles on the map.
 *
 * @author Victoria Tay Wen Xie
 * @version 1.0
 */
public class MagicCircle extends Ground {

    /**
     * This constructor constructs a new Magic Circle ground tile
     */
    public MagicCircle() {
        super('◎', "Magic Circle");
        this.enableAbility(Ability.IS_MAGIC_CIRCLE);
    }

    /**
     * Returns the list of allowable actions that an actor can perform while standing on this ground.
     * @param actor the Actor acting
     * @param location the current Location
     * @param direction the direction of the Ground from the Actor
     * @return an action list containing the teleport action.
     */
    @Override
    public ActionList allowableActions(Actor actor, Location location, String direction) {
        ActionList actions = new ActionList();

        // If direction is empty, it means the actor is standing directly on top of the '◎' symbol
        if (direction.isEmpty()) {
            actions.add(new TeleportAction(new MagicCircleStrategy()));
        }

        return actions;
    }
}