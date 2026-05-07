package game.actions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;

/**
 * Action for stealing items from another actor.
 *
 * @author Aida
 */
public class StealAction extends Action {
    private final Actor target;
    private final Item item;
    private final String direction;

    public StealAction(Actor target, Item item, String direction) {
        this.target = target;
        this.item = item;
        this.direction = direction;
    }

    @Override
    public String execute(Actor actor, GameMap map) {
        return actor + " steals " + item + " from " + target + " at " + direction + "!";
    }

    @Override
    public String menuDescription(Actor actor) {
        return actor + " steals from " + target;
    }
}