package game.items;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.statistics.BaseStatistic;
import game.enums.ItemStatistics;
import game.enums.Ability;
import game.capabilities.Purchasable;
import game.capabilities.CreditHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * A specialized piece of corporate equipment used to purify consumables.
 * While in a worker's inventory, it grants the ability to safely
 * consume spoiled food and toxic water.
 *
 * @author Suchir
 * @version 1.0
 */
public class SterilisationBox extends Item implements Purchasable {
    private static final int WEIGHT = 7;
    private static final int PURCHASE_PRICE = 750;

    private final Random random = new Random();

    /**
     * Constructor for the Sterilisation Box.
     * Sets the weight to seven units and enables the sterilizer ability
     * for the carrier.
     */
    public SterilisationBox() {
        super("Sterilisation Box", '▣');
        this.addNewStatistic(ItemStatistics.WEIGHT, new BaseStatistic(WEIGHT));
        this.makePortable();
        this.enableAbility(Ability.STERILIZER);
    }

    /**
     * Gets the purchase price of the sterilisation box.
     *
     * @return purchase price
     */
    @Override
    public int getPurchasePrice() {
        return PURCHASE_PRICE;
    }

    /**
     * Applies the effect after purchasing the sterilisation box.
     * One random non-essential item in the buyer's inventory is permanently erased.
     *
     * @param buyer the actor buying the item
     * @param map the current game map
     * @param wallet the buyer's wallet
     * @return purchase effect description
     */
    @Override
    public String purchasedBy(Actor buyer, GameMap map, CreditHolder wallet) {
        List<Item> removableItems = new ArrayList<>();

        for (Item item : buyer.getInventory().getItems()) {
            if (item != this && !item.hasAbility(Ability.ESSENTIAL)) {
                removableItems.add(item);
            }
        }

        if (removableItems.isEmpty()) {
            return "The radiation finds no other item to erase.";
        }

        Item removedItem = removableItems.get(random.nextInt(removableItems.size()));
        buyer.getInventory().remove(removedItem);

        return "The radiation permanently erases " + removedItem + " from the inventory.";
    }
}