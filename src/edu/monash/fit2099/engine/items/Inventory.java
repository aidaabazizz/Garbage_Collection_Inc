package edu.monash.fit2099.engine.items;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A class that manages a collection of items, allowing for clean abstraction
 * and the implementation of inventory mechanics like stacking.
 *
 * @author Adrian Kristanto
 */
public abstract class Inventory {
    /**
     * A bag of items
     */
    protected final List<Item> items;

    public Inventory() {
        this.items = new ArrayList<>();
    }

    /**
     * Add an item to the inventory.
     * Adding an item may fail due to certain conditions, e.g., the item to be added doesn't have the required statistic
     *
     * @param item The Item to add.
     * @return true if the item is successfully added, false otherwise
     */
    public abstract boolean add(Item item);

    /**
     * Remove an item from the inventory.
     * Removing an item may fail due to certain conditions, e.g., the item to be removed doesn't have the required statistic
     *
     * @param item The Item to remove.
     * @return true if the item is successfully removed, false otherwise
     */
    public abstract boolean remove(Item item);

    /**
     * Get a copy of the inventory.
     *
     * @return An unmodifiable wrapper of the inventory.
     */
    public List<Item> getItems() {
        return Collections.unmodifiableList(items);
    }

    /**
     * Returns a list of all capabilities of the specified type that are present
     * in the item inventory. For each item, attempts to retrieve the capability
     * using {@code asCapability}. If the capability is present, it is included
     * in the returned list.
     *
     * @param <T> the type of capability to retrieve
     * @param capability the class object representing the capability type
     * @return a list containing all capabilities of the specified type found in the inventory;
     *         the list is empty if no items provide the capability
     */
    public <T> List<T> getItemsAs(Class<T> capability) {
        List<T> result = new ArrayList<>();
        for (Item item : items) {
            if(item != null){
                item.asCapability(capability).ifPresent(result::add);
            }
        }
        return result;
    }

}
