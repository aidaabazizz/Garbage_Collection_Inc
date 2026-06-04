package game.items;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.statistics.BaseStatistic;
import game.enums.ItemStatistics;
import game.capabilities.FireStackable;
import game.capabilities.Sellable;
import game.capabilities.CreditHolder;
import game.grounds.Fire;
import game.highvoltage.MaterialCapability;

import java.util.Random;

/**
 * A heavy piece of archaic technology representing scrap material.
 * This item serves as a high-weight burden for workers collecting assets.
 *
 * @author Suchir
 * @version 1.0
 */
public class CRTMonitor extends Item implements Sellable {
    private static final int SELL_PRICE = 25;
    private static final int HEAL_AMOUNT = 5;
    private static final int SHORT_CHANCE = 20;
    private static final int SHORT_DAMAGE = 2;

    private final Random random = new Random();

    /**
     * Constructor for the CRT Monitor.
     * Assigns a substantial weight of thirty units and makes the item portable.
     */
    public CRTMonitor() {
        super("CRT Monitor", '◙');
        this.addNewStatistic(ItemStatistics.WEIGHT, new BaseStatistic(30));
        this.makePortable();
        this.enableAbility(MaterialCapability.MAGNETIC); // Added for magnetic item
    }

    /**
     * Gets the selling price of the CRT monitor.
     *
     * @return selling price
     */
    @Override
    public int getSellPrice() {
        return SELL_PRICE;
    }

    /**
     * Applies the effect after selling the CRT monitor.
     *
     * @param seller the actor selling the item
     * @param map the current game map
     * @param wallet the seller's wallet
     * @return selling effect description
     */
    @Override
    public String soldBy(Actor seller, GameMap map, CreditHolder wallet) {
        seller.heal(HEAL_AMOUNT);
        String result = seller + " feels relieved and heals " + HEAL_AMOUNT + " health points.";

        if (random.nextInt(100) < SHORT_CHANCE) {
            seller.hurt(SHORT_DAMAGE);
            spawnFireAround(map.locationOf(seller));
            result += " The CRT Monitor shorts out, dealing " + SHORT_DAMAGE
                    + " damage and spawning fire around the seller.";
        }

        return result;
    }

    /**
     * Spawns fire around a location.
     *
     * @param location the centre location
     */
    private void spawnFireAround(Location location) {
        for (Exit exit : location.getExits()) {
            Location destination = exit.getDestination();
            Ground currentGround = destination.getGround();

            FireStackable existingFire = destination.getGroundAs(FireStackable.class);
            if (existingFire != null) {
                existingFire.addStack();
            } else {
                destination.setGround(new Fire(currentGround));
            }
        }
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