package game.inventory;

import edu.monash.fit2099.engine.items.Inventory;
import edu.monash.fit2099.engine.items.Item;

/**
 * Primarily used as a temporary holding cell for legendary potions, rare
 * artifacts, and random shiny rocks the player will hoard "just in case,"
 * but ultimately never use during the final boss fight.
 *
 * @see edu.monash.fit2099.engine.actors.Actor
 * @see Inventory
 * @author Adrian Kristanto
 */
public class BasicInventory extends Inventory {

    /**
     * A simple implementation of the add method.
     * It simply adds the item into the items list without doing additional checks.
     *
     * @param item The Item to add.
     * @return true if the item is successfully added, false otherwise
     */
    @Override
    public boolean add(Item item) {
        return items.add(item);
    }

    /**
     * A simple implementation of the remove method.
     * It simply removes the item from the items list without doing additional checks.
     *
     * @param item The Item to remove.
     * @return true if the item is successfully removed, false otherwise
     */
    @Override
    public boolean remove(Item item) {
        return items.remove(item);
    }
}
