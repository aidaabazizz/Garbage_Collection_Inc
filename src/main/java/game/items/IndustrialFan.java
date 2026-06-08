package game.items;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.statistics.BaseStatistic;
import game.capabilities.CreditHolder;
import game.capabilities.Depositable;
import game.capabilities.Sellable;
import game.enums.FacilityCapability;
import game.enums.ItemStatistics;
import game.managers.QuotaManager;
import game.managers.Spawner;

/**
 * An Industrial Fan that can be sold for credits or deposited for
 * Company Credits.
 * Selling the fan may cause a Slime to emerge near the Super Computer,
 * while depositing it rewards the worker with healing.
 *
 * @author Victoria Tay Wen Xie
 * @version 1.0
 */
public class IndustrialFan extends Item implements Sellable, Depositable {

    /**
     * Weight of the fan.
     */
    private static final int WEIGHT = 5;

    /**
     * Credits earned when the fan is sold.
     */
    private static final int SELL_PRICE = 150;

    /**
     * Company Credits earned when the fan is deposited.
     */
    private static final int COMPANY_CREDITS = 10;
    /**
     * Amount of health restored when the fan is deposited.
     */
    private static final int HEAL_AMOUNT = 10;

    /**
     * Spawner used to create creatures.
     */
    private final Spawner spawner;

    /**
     * Creates an Industrial Fan.
     * @param spawner the spawner used to create creatures
     */
    public IndustrialFan(Spawner spawner) {
        super("Industrial Fan", '@');
        this.addNewStatistic(ItemStatistics.WEIGHT, new BaseStatistic(WEIGHT));
        this.makePortable();
        this.spawner = spawner;
    }

    /**
     * Returns the selling price of the fan.
     * @return the selling price
     */
    @Override
    public int getSellPrice() { return SELL_PRICE; }

    /**
     * Returns the Company Credit value of the fan.
     * @return the Company Credit value
     */
    @Override
    public int getCompanyCreditValue() { return COMPANY_CREDITS; }

    /**
     * Sells the fan.
     * The fan is removed from the seller's inventory.
     * A Slime instantly spawns on an empty tile adjacent to the Supercomputer.
     * @param seller the actor selling the fan
     * @param map the map the actor is on
     * @param wallet the wallet receiving the credits
     * @return the result of the sale
     */
    @Override
    public String soldBy(Actor seller, GameMap map, CreditHolder wallet) {
        Location sellerLoc = map.locationOf(seller);
        seller.getInventory().remove(this);

        for (Exit exit : sellerLoc.getExits()) {
            Location adj = exit.getDestination();
            if (adj.getGround().hasAbility(FacilityCapability.FACILITY_TERMINAL)) {
                for (Exit comExit : adj.getExits()) {
                    Location slimeSpawnTile = comExit.getDestination();
                    if (!slimeSpawnTile.containsAnActor() && slimeSpawnTile.canActorEnter(seller)) {
                        spawner.spawnSlime(slimeSpawnTile);
                        return "Stripping the facility's colling system! A Slime immediately crawls out next to the Supercomputer!";
                    }
                }
            }
        }
        return "Ventilation system broken.";
    }

    /**
     * Deposits the fan into the Super Computer.
     * The depositor is healed as a reward for compliance.
     * @param depositor the actor depositing the fan
     * @param map the map the actor is on
     * @param quotaManager the quota manager handling Company Credits
     * @return the result of the deposit
     */
    @Override
    public String depositBy(Actor depositor, GameMap map, QuotaManager quotaManager) {
        depositor.heal(HEAL_AMOUNT);
        return "Company rewards your compliance with a burst of fresh oxygen, healing " + depositor + " for " + HEAL_AMOUNT + " HP!";
    }
}
