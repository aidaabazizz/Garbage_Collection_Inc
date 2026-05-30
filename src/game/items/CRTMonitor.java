package game.items;

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
        this.enableAbility(MaterialCapability.MAGNETIC);
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
}