package game.items;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.statistics.BaseStatistic;
import game.actions.ActivateHeavenTokenAction;
import game.capabilities.CreditHolder;
import game.capabilities.Purchasable;
import game.grounds.SanctuaryField;
import game.enums.ItemStatistics;
import game.sanctuary.SanctuaryTool;

/**
 * A consumable artifact that creates a temporary zone of divine protection.
 *
 * When activated via the ActivateHeavenTokenAction, the token replaces the
 * ground the actor is currently standing on with a SanctuaryField. This field
 * persists for a set duration before reverting to the original ground type.
 *
 * The token is consumed upon use and can be purchased from facility terminals.
 * It is primarily used to create safe zones in hazardous environments.
 *
 * @author Chathya Attanayake
 * @version 1.0
 */
public class HeavenToken extends Item implements SanctuaryTool, Purchasable {
    /** The weight of the token in the actor's inventory. */
    private static final int WEIGHT = 1;

    /** The number of turns the created Sanctuary Field will persist. */
    private static final int FIELD_DURATION = 10;

    /** The price of the token when purchased from a store. */
    private static final int PURCHASE_PRICE = 50;

    /**
     * Constructor.
     * Initializes the token with the 'ε' symbol, makes it portable, and sets its weight.
     */
    public HeavenToken() {
        super("Heaven Token", 'ε');
        this.makePortable();
        this.addNewStatistic(ItemStatistics.WEIGHT, new BaseStatistic(WEIGHT));
    }

    /**
     * Returns the action to activate this token, available when the actor holds it.
     *
     * @param actor    the actor holding the token
     * @param map      the game map
     * @return list containing the activation action
     */
    @Override
    public ActionList allowableActions(Actor actor, GameMap map) {
        ActionList actions = new ActionList();
        actions.add(new ActivateHeavenTokenAction(this));
        return actions;
    }

    /**
     * Activates the token's primary effect: manifesting a Sanctuary Field.
     *
     * This method performs the following:
     *
     *     Captures the current ground type at the actor's location.
     *     Replaces that ground with a new SanctuaryField, passing the
     *     previous ground as a parameter so it can be restored later.
     *     Removes the token from the actor's inventory.

     *
     * @param actor    the actor activating the token.
     * @param map      the current game map.
     * @param location the location where the field is being manifested.
     * @return a description of the activation and the duration of the field.
     */
    @Override
    public String activateSanctuaryEffect(Actor actor, GameMap map, Location location) {
        Ground previousGround = location.getGround();

        // Replace the floor worker is standing on with a Sanctuary Field
        location.setGround(new SanctuaryField(FIELD_DURATION, previousGround));

        // Remove from inventory
        actor.getInventory().remove(this);

        // 4. Return the result string to be displayed in the console
        return String.format("\u001B[35m%s activates the Heaven Token! A holy field manifests for %d turns!\u001B[0m",
                actor, FIELD_DURATION);

    }

    /**
     * Returns the purchase price of the token.
     *
     * @return the cost in credits.
     */
    @Override
    public int getPurchasePrice() {
        return PURCHASE_PRICE; // Rare item price
    }

    /**
     * Logic for purchasing the token from a vendor.
     * Deducts the price from the actor's wallet and adds the item to their inventory.
     *
     * @param buyer  the actor buying the token.
     * @param map    the map where the transaction occurs.
     * @param wallet the credit holder to deduct funds from.
     * @return a summary of the purchase transaction.
     */
    @Override
    public String purchasedBy(Actor buyer, GameMap map, CreditHolder wallet) {
        wallet.deductCredits(this.getPurchasePrice());
        buyer.getInventory().add(this);
        return buyer + " bought a Heaven Token for " + PURCHASE_PRICE + " credits.";
    }

}
