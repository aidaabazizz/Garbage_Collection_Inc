package game.items;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.*;
import edu.monash.fit2099.engine.statistics.BaseStatistic;
import game.actions.UseCommandWhistleAction;
import game.capabilities.CreditHolder;
import game.capabilities.MotivationStatus;
import game.capabilities.Purchasable;
import game.enums.Ability;
import game.enums.ItemStatistics;
import game.sanctuary.SanctuaryTool;

/**
 * A rare consumable item that allows an actor to inspire and motivate nearby workers.
 *
 * When used, the whistle identifies the nearest friendly worker within a specific radius
 * and applies the MotivationStatus. This status causes the targeted worker
 * to emit a powerful Area of Effect (AoE) pulse in the following turn. If no other
 * workers are nearby, the user motivates themselves (provided they are a worker).
 *
 * The item is consumed upon use and can be purchased from the SuperComputer.
 *
 * @author Chathya Attanayake
 * @version 1.0
 */
public class CommandWhistle extends Item implements SanctuaryTool, Purchasable {
    /** The cost to purchase this item from a terminal. */
    private static final int PURCHASE_PRICE = 100;

    /** The weight of the whistle in the actor's inventory. */
    private static final int WEIGHT = 1;

    /** The maximum distance (Manhattan distance) to search for a friendly worker. */
    private static final int SEARCH_RADIUS = 5;

    /**
     * Constructor.
     * Initializes the whistle with the 'f' symbol and sets its weight statistic.
     */
    public CommandWhistle() {
        super("Command Whistle", 'f');
        this.makePortable();
        this.addNewStatistic(ItemStatistics.WEIGHT, new BaseStatistic(WEIGHT));
    }

    /**
     * Returns the activation action when this whistle is in the actor's inventory.
     *
     * @param actor    the actor holding the whistle
     * @param map      the game map
     * @return the list of available actions
     */
    @Override
    public ActionList allowableActions(Actor actor, GameMap map) {
        ActionList actions = new ActionList();
        actions.add(new UseCommandWhistleAction(this));
        return actions;
    }

    /**
     * Implements SanctuaryTool interface. Delegates to UseCommandWhistleAction.
     *
     * @param actor    the actor
     * @param map      the game map
     * @param location the actor's location
     * @return description string
     */
    @Override
    public String activateSanctuaryEffect(Actor actor, GameMap map, Location location) {
        Actor target = findNearestFriendly(actor, location);
        if (target == null) target = actor;

        // 2. Apply the effect
        target.addStatus(new MotivationStatus(location));

        // 3. Consume the item
        actor.getInventory().remove(this);

        return String.format("%s blows the Command Whistle! %s is motivated — an AoE pulse will fire next turn!",
                actor, target);
    }

    /**
     * Scans all exits from the whistle-user's location for a friendly (WORKER) actor.
     * Uses Ability.WORKER capability check — no instanceof (DIP).
     *
     * @param user the actor using the whistle (excluded from search)
     * @param here the whistle-user's location
     * @return the first friendly actor found, or null if none
     */
    private Actor findNearestFriendly(Actor user, Location here) {
        Actor closest = null;
        int minDist = Integer.MAX_VALUE;

        for (Location loc : here.getNearbyLocations(SEARCH_RADIUS)) {
            if (!loc.containsAnActor()) continue;
            Actor candidate = loc.getActor();
            if (candidate == user) continue;
            if (!candidate.hasAbility(Ability.WORKER)) continue;
            int dist = Math.abs(loc.x() - here.x()) + Math.abs(loc.y() - here.y());
            if (dist < minDist) {
                minDist = dist;
                closest = candidate;
            }
        }
        return closest;
    }


    /**
     * Returns the credits required to purchase the whistle.
     *
     * @return the purchase price as an integer.
     */
    @Override
    public int getPurchasePrice() {
        return PURCHASE_PRICE; // Rare item price
    }

    /**
     * Handles the purchase transaction for the whistle.
     * Deducts the price from the buyer's wallet and adds the whistle to their inventory.
     *
     * @param buyer  the actor purchasing the item.
     * @param map    the map where the transaction occurs.
     * @param wallet the credit holder from which funds are deducted.
     * @return a message describing the successful purchase.
     */
    @Override
    public String purchasedBy(Actor buyer, GameMap map, CreditHolder wallet) {
        wallet.deductCredits(this.getPurchasePrice());
        buyer.getInventory().add(this);
        return buyer + " bought a Command Whistle for " + PURCHASE_PRICE + " credits.";
    }

}