package game.actions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import game.capabilities.CreditHolder;
import game.capabilities.Purchasable;

/**
 * Action for purchasing an item from the Supercomputer.
 *
 * @author Suchir
 * @version 1.0
 */
public class PurchaseAction extends Action {
    private final Item item;
    private final Purchasable purchasable;

    /**
     * Constructor for PurchaseAction.
     *
     * @param item the item being purchased
     * @param purchasable the purchasable behaviour of the item
     */
    public PurchaseAction(Item item, Purchasable purchasable) {
        this.item = item;
        this.purchasable = purchasable;
    }

    /**
     * Executes the purchase transaction.
     *
     * @param actor the actor buying the item
     * @param map the current map
     * @return description of the transaction
     */
    @Override
    public String execute(Actor actor, GameMap map) {
        CreditHolder wallet = getWallet(actor);

        if (wallet == null) {
            return actor + " does not have a wallet.";
        }

        int price = purchasable.getPurchasePrice();

        if (!wallet.hasEnough(price)) {
            return purchasable.failedPurchaseBy(actor, map, wallet);
        }

        wallet.deductCredits(price);

        boolean added = actor.getInventory().add(item);
        if (!added) {
            wallet.addCredits(price);
            return actor + " cannot carry " + item + ". The purchase is cancelled.";
        }

        String effect = purchasable.purchasedBy(actor, map, wallet);

        return String.format(
                "%s buys %s for %d credits. %s. %s",
                actor,
                item,
                price,
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
     * Describes the purchase option in the menu.
     *
     * @param actor the actor performing the action
     * @return menu description
     */
    @Override
    public String menuDescription(Actor actor) {
        return actor + " buys " + item + " for " + purchasable.getPurchasePrice() + " credits";
    }
}