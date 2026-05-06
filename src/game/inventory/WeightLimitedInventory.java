package game.inventory;

import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.items.Inventory;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.items.ItemAbility;
import game.enums.ItemStatistics;

/**
 * At its core, this is just an oversized {@code ArrayList}.
 * It prevents the player from simply carrying the entire {@code EclipseNebula}
 * with them by enforcing an arbitrary capacity limit.
 *
 * @author Adrian Kristanto
 */
public class WeightLimitedInventory extends Inventory {
    private int weightLimit;
    private int weight;

    /**
     * Constructor for WeightLimitedInventory.
     *
     * @param weightLimit The maximum weight capacity.
     */
    public WeightLimitedInventory(int weightLimit) {
        this.weightLimit = weightLimit;
        this.weight = 0;
    }

    /**
     * Checks the weight of an item before adding it to the inventory.
     * Increments the current total weight if the item is successfully added.
     *
     * @param item The Item to be added.
     * @return True if the item was added within capacity, false otherwise.
     * @throws IllegalArgumentException If the item lacks a weight statistic.
     */
    @Override
    public boolean add(Item item) {
        Display display = new Display();
        if (item.hasStatistic(ItemStatistics.WEIGHT)) {
            int itemWeight =  item.getStatistic(ItemStatistics.WEIGHT);
            if (weight + itemWeight <= this.weightLimit) {
                items.add(item);
                this.weight += itemWeight;
                display.println(String.format("%s added successfully. Current inventory weight (%d/%d)", item, weight, weightLimit));
                return true;
            } else {
                display.println(String.format("Fails to add %s with weight %d. Weight limit will be exceeded (%d/%d)", item, itemWeight, weight + itemWeight, weightLimit));
                return false;
            }
        } else {
            String msg = String.format("%s does not have a weight statistic", item);
            throw new IllegalArgumentException(msg);
        }
    }

    /**
     * Removes an item from the inventory and decrements the total weight.
     *
     * @param item The Item to be removed.
     * @return True if the item was successfully removed.
     * @throws IllegalArgumentException If the item lacks a weight statistic.
     */
    @Override
    public boolean remove(Item item) {

        if (!item.hasAbility(ItemAbility.PORTABLE)) {
            return false;
        }
        if (item.hasStatistic(ItemStatistics.WEIGHT)) {
            int itemWeight =  item.getStatistic(ItemStatistics.WEIGHT);
            if (items.remove(item)) {
                this.weight -= itemWeight;
                return true;
            }
            return false;
        } else {
            String msg = String.format("%s does not have a weight statistic", item);
            throw new IllegalArgumentException(msg);
        }
    }
}
