package game.items;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.statistics.BaseStatistic;
import game.enums.ItemStatistics;
import game.capabilities.Sellable;
import game.capabilities.CreditHolder;
import game.highvoltage.MaterialCapability;

import java.util.Random;

/**
 * A lightweight piece of ancient data storage scrap.
 * Primarily used as a collectible corporate asset with minimal weight.
 *
 * @author Suchir
 * @version 1.0
 */
public class FloppyDisk extends Item implements Sellable {
    private static final int SELL_PRICE = 1;
    private static final int GLITCH_CHANCE = 50;
    private static final int GLITCH_FEE = 50;

    private final Random random = new Random();

    /**
     * Constructor for the Floppy Disk.
     * Assigns a weight of one unit and makes the item portable.
     */
    public FloppyDisk() {
        super("Floppy Disk", '⊟');
        this.addNewStatistic(ItemStatistics.WEIGHT, new BaseStatistic(1));
        this.makePortable();
        this.enableAbility(MaterialCapability.MAGNETIC); // Added for magnetic item
    }

    /**
     * Gets the selling price of the floppy disk.
     *
     * @return selling price
     */
    @Override
    public int getSellPrice() {
        return SELL_PRICE;
    }

    /**
     * Applies the effect after selling the floppy disk.
     *
     * @param seller the actor selling the item
     * @param map the current game map
     * @param wallet the seller's wallet
     * @return selling effect description
     */
    @Override
    public String soldBy(Actor seller, GameMap map, CreditHolder wallet) {
        if (random.nextInt(100) < GLITCH_CHANCE) {
            int deducted = wallet.forceDeductCredits(GLITCH_FEE);
            return "The Supercomputer glitches and deducts " + deducted + " credits.";
        }

        return "The floppy disk is sold without a glitch.";
    }

    /**
     * Determines the interactions available for this item while it is on the ground.
     *
     * Logic (REQ 3 - High Voltage System):
     * If the item possesses the MAGNETICALLY_LOCKED capability, it indicates that
     * the local high-voltage induction is too strong for manual retrieval.
     * In this state, an empty ActionList is returned, effectively disabling the
     * 'Pick Up' interaction until the energy dissipates.
     *
     * @param location The current location of the item on the GameMap.
     * @return A list of allowable actions; empty if the item is magnetically locked.
     */
    @Override
    public ActionList allowableActions(Location location) {
        if (this.hasAbility(MaterialCapability.MAGNETICALLY_LOCKED)) {
            return new ActionList(); // Cannot be picked up by hand
        }
        return super.allowableActions(location);
    }
}