package game.items;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.statistics.BaseStatistic;
import game.capabilities.CreditHolder;
import game.capabilities.Depositable;
import game.capabilities.Sellable;
import game.enums.ItemStatistics;
import game.managers.QuotaManager;
import game.teleportstrategies.BaseTeleportStrategy;

import java.util.Random;

/**
 * This is the item that Alien Cube become after being destroyed by Plasma Cutter.
 * It will handle the behaviour when being sold and being deposited to Super Computer.
 *
 * @author Victoria Tay Wen Xie
 * @version 1.0
 */
public class AlienArtifact extends Item implements Sellable, Depositable {
    /**
     * Weight of the alien cube.
     */
    private static final int WEIGHT = 1;
    /**
     * Selling price to the Super Computer.
     */
    private static final int SELL_PRICE = 200;
    /**
     * Company credits that can be earned after depositing this to the Super Computer.
     */
    private static final int COMPANY_CREDITS = 100;
    /**
     * Initial number of teleportation attempts.
     */
    private static final int TELEPORT_STARTING_ATTEMPTS = 0;
    /**
     * Maximum number of teleportation attempts.
     */
    private static final int MAX_ATTEMPTS = 100;
    /**
     * Random number generator used for artifact effects.
     */
    private final Random rand = new Random();

    /**
     * Creates an Alien Artifact.
     */
    public AlienArtifact() {
        super("Alien Artifact", '?');
        this.addNewStatistic(ItemStatistics.WEIGHT, new BaseStatistic(WEIGHT));
        this.makePortable();
    }

    /**
     * Returns the selling price of the artifact.
     * @return the selling price
     */
    @Override
    public int getSellPrice() { return SELL_PRICE; }

    /**
     * Returns the Company Credit value of the artifact.
     * @return the Company Credit value
     */
    @Override
    public int getCompanyCreditValue() {return COMPANY_CREDITS;}

    /**
     * Sells the artifact.
     * The artifact is removed from the seller's inventory. There is a 50%
     * chance that the seller becomes poisoned for 5 turns.
     *
     * @param seller the actor selling the artifact
     * @param map the map the actor is on
     * @param wallet the wallet receiving the credits
     * @return the result of the sale
     */
    @Override
    public String soldBy(Actor seller, GameMap map, CreditHolder wallet) {
        seller.getInventory().remove(this);
        if (rand.nextBoolean()) {
            seller.addStatus(new game.capabilities.PoisonStatus(5));
            return  seller + " becomes poisoned for 5 turns due to handling of the unstable artifact!";
        }
        return "Item safely handled, no side effect.";
    }

    /**
     * Deposits the artifact into the Super Computer.
     * The depositor is teleported to a random valid location on the map.
     *
     * @param depositor the actor depositing the artifact
     * @param map the map the actor is on
     * @param quotaManager the quota manager handling Company Credits
     * @return the result of the deposit
     */
    @Override
    public String depositBy(Actor depositor, GameMap map, QuotaManager quotaManager) {
        Location destination = null;
        int attempts = TELEPORT_STARTING_ATTEMPTS;
        while (destination == null && attempts < MAX_ATTEMPTS) {
            attempts++;
            destination = BaseTeleportStrategy.findRandomValidLocation(map, depositor);
        }
        if (destination != null) {
            map.moveActor(depositor, destination);
            return depositor + " is immediately teleported to sector ("
                    + destination.x() + ", " + destination.y() + ") to get them back to work faster!";
        }
        return "Teleport arrays are currently occupied, teleportation fails.";
    }
}
