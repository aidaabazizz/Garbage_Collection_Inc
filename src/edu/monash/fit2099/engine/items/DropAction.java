package edu.monash.fit2099.engine.items;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;

/**
 * An action for dropping an item from the actor's inventory.
 * @author Riordan Alfredo
 */
public class DropAction extends Action {
    /**
     * Current item
     */
    private final Item item;

    /**
     * Constructor.
     *
     * @param item the item to drop
     */
    public DropAction(Item item) {
        this.item = item;
    }

    /**
     * When an item is dropped, remove the item from the actor's inventory and add it to the current location of the actor.
     *
     * @param actor The actor performing the action.
     * @param map The map the actor is on.
     * @return a string describing who has dropped which item.
     */
    @Override
    public String execute(Actor actor, GameMap map) {
        if (actor.getInventory().remove(item)) {
            map.locationOf(actor).addItem(item);
            return menuDescription(actor);
        }
        return String.format("%s fails to drop %s", actor, item);
    }

    /**
     * A string describing the action suitable for displaying in the UI menu.
     *
     * @param actor The actor performing the action.
     * @return a String, e.g. "Player drops the potato"
     */
    @Override
    public String menuDescription(Actor actor) {
        return actor + " drops the " + item;
    }
}
