package game.actions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import game.capabilities.Consumable;

/**
 * Action for stealing an item from a worker and immediately consuming it.
 * The item disappears from the game entirely after consumption.
 *
 * @author Aida
 */
public class StealAndConsumeAction extends Action {
    private final Actor target;
    private final Item item;
    private final Consumable consumable;
    private final String direction;

    public StealAndConsumeAction(Actor target, Item item, Consumable consumable, String direction) {
        this.target = target;
        this.item = item;
        this.consumable = consumable;
        this.direction = direction;
    }

    @Override
    public String execute(Actor actor, GameMap map) {
        StringBuilder result = new StringBuilder();

        // 1. Remove item from worker's inventory
        target.getInventory().remove(item);
        result.append(actor).append(" steals ").append(item).append(" from ").append(target).append(" at ").append(direction).append("!\n");

        // 2. Immediately consume the item (apply its effect)
        String consumptionResult = consumable.consumedBy(actor);
        result.append(actor).append(" consumes the stolen ").append(item).append(". ").append(consumptionResult);

        // 3. Clean up the item (it's gone from the game)
        if (consumable.isFinished()) {
            consumable.cleanUp(actor, map.locationOf(actor));
        }

        return result.toString();
    }

    @Override
    public String menuDescription(Actor actor) {
        return actor + " steals and consumes " + item + " from " + target;
    }
}