package game.actions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import game.capabilities.Consumable;

/**
 * Action for CrazyChicken to steal and immediately consume/destroy
 * a consumable item from a worker's inventory.
 *
 * @author Aida
 * @version 1.0
 */
public class StealConsumableAction extends Action {
    private final Actor target;
    private final Item item;
    private final Consumable consumable;
    private final String direction;

    public StealConsumableAction(Actor target, Item item, Consumable consumable, String direction) {
        this.target = target;
        this.item = item;
        this.consumable = consumable;
        this.direction = direction;
    }

    @Override
    public String execute(Actor actor, GameMap map) {
        if (!target.getInventory().getItems().contains(item)) {
            return target + " no longer has " + item + ".";
        }

        String stolenItemName = item.toString();

        target.getInventory().remove(item);

        return actor + " steals " + stolenItemName + " from " + target
                + " at " + direction
                + " and consumes and has no effect";
    }

    @Override
    public String menuDescription(Actor actor) {
        return actor + " steals and devours " + item + " from " + target;
    }
}