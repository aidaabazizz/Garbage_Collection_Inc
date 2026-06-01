package game.actions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import game.capabilities.CreditHolder;
import game.capabilities.Sellable;

/**
 * Action for selling an item to the Supercomputer.
 *
 * @author Suchir
 * @version 1.0
 */
public class SellAction extends Action {
    private final Item item;
    private final Sellable sellable;

    /**
     * Constructor for SellAction.
     *
     * @param item the item being sold
     * @param sellable the sellable behaviour of the item
     */
    public SellAction(Item item, Sellable sellable) {
        this.item = item;
        this.sellable = sellable;
    }

    /**
     * Executes the sale transaction.
     *
     * @param actor the actor selling the item
     * @param map the current map
     * @return description of the transaction
     */
    @Override
    public String execute(Actor actor, GameMap map) {
        CreditHolder wallet = getWallet(actor);

        if (wallet == null) {
            return actor + " does not have a wallet.";
        }

        int price = sellable.getSellPrice();
        int added = wallet.addCredits(price);

        String effect = sellable.soldBy(actor, map, wallet);
        actor.getInventory().remove(item);

        return String.format(
                "%s sells %s for %d credits. %d credits added. %s. %s",
                actor,
                item,
                price,
                added,
                wallet,
                effect
        );
    }

    /**
     * Finds the actor's wallet through the CreditHolder contract.
     *
     * @param actor the actor whose inventory is checked
     * @return the credit holder, or null if none exists
     */
    private CreditHolder getWallet(Actor actor) {
        if (actor.getInventory().getItemsAs(CreditHolder.class).isEmpty()) {
            return null;
        }
        return actor.getInventory().getItemsAs(CreditHolder.class).get(0);
    }

    /**
     * Describes the sale option in the menu.
     *
     * @param actor the actor performing the action
     * @return menu description
     */
    @Override
    public String menuDescription(Actor actor) {
        return actor + " sells " + item + " for " + sellable.getSellPrice() + " credits";
    }
}