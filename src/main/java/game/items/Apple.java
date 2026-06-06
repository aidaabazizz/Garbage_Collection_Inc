package game.items;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.statistics.BaseStatistic;
import game.enums.ItemStatistics;
import game.actions.ConsumeAction;
import game.enums.Ability;
import game.capabilities.Consumable;
import game.capabilities.PoisonStatus;
import game.capabilities.Sellable;
import game.capabilities.CreditHolder;

/**
 * A spoiled food item found within the facility.
 * Consuming this item results in toxic effects unless the consumer possesses
 * a sterilization box.
 *
 * @author Suchir
 * @version 1.0
 */
public class Apple extends Item implements Consumable, Sellable {
    private static final int HEAL_POINTS = 3;
    private static final int POISON_DURATION = 5;

    private static final int SELL_PRICE = 1;
    private static final int SELL_POISON_DURATION = 2;

    /**
     * Constructor for the Apple.
     * Initializes the apple with a weight of one unit and sets it as portable.
     */
    public Apple() {
        super("Apple", 'ó');
        this.addNewStatistic(ItemStatistics.WEIGHT, new BaseStatistic(1));
        this.makePortable();
    }

    /**
     * Processes the consumption logic for the apple.
     * Provides healing if the actor has sterilization capabilities; otherwise,
     * inflicts a poison status that lasts for five turns.
     *
     * @param actor The actor consuming the apple.
     * @return A description of the consumption outcome.
     */
    @Override
    public String consumedBy(Actor actor) {
        if (actor.hasAbility(Ability.STERILIZER)) {
            actor.heal(HEAL_POINTS);
            return String.format("%s eats a sterilized apple and heals %d HP.", actor, HEAL_POINTS);
        }
        actor.addStatus(new PoisonStatus(POISON_DURATION));
        return String.format("%s eats a rotten apple and is poisoned for %d turns!", actor, POISON_DURATION);
    }

    /**
     * Removes the apple from the game world after use.
     * Deletes the item from both the actor's inventory and the map location.
     *
     * @param actor The actor who consumed the item.
     * @param location The map location where the item was consumed.
     */
    @Override
    public void cleanUp(Actor actor, Location location) {
        actor.getInventory().remove(this);
        location.removeItem(this);
    }

    /**
     * Provides the action to consume the apple.
     *
     * @param owner The actor who can interact with the apple.
     * @param map the current game map
     * @return A list containing a single consume action.
     */
    @Override
    public ActionList allowableActions(Actor owner, GameMap map) {
        ActionList actions = new ActionList();
        actions.add(new ConsumeAction(this, "Apple"));
        return actions;
    }

    /**
     * Gets the selling price of the apple.
     *
     * @return selling price
     */
    @Override
    public int getSellPrice() {
        return SELL_PRICE;
    }

    /**
     * Applies the effect after selling the apple.
     *
     * @param seller the actor selling the item
     * @param map the current game map
     * @param wallet the seller's wallet
     * @return selling effect description
     */
    @Override
    public String soldBy(Actor seller, GameMap map, CreditHolder wallet) {
        if (seller.hasAbility(Ability.STERILIZER)) {
            return seller + " is protected by a Sterilisation Box and avoids poisoning.";
        }

        seller.addStatus(new PoisonStatus(SELL_POISON_DURATION));
        seller.addStatus(new PoisonStatus(SELL_POISON_DURATION));

        return seller + " is poisoned for " + SELL_POISON_DURATION
                + " turns, taking 2 damage per turn.";
    }
}