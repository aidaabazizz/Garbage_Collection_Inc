package game.grounds;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.actions.PurchaseAction;
import game.actions.SellAction;
import game.capabilities.Purchasable;
import game.capabilities.Sellable;
import game.enums.AccessLevel;
import game.items.AccessCard;
import game.items.FirstAidKit;
import game.items.SterilisationBox;
import game.enums.Ability;

/**
 * A Supercomputer terminal that allows workers to buy and sell items.
 *
 * @author Suchir
 * @version 1.0
 */
public class SuperComputer extends Ground {

    /**
     * Constructor for Supercomputer.
     */
    public SuperComputer() {
        super('≡', "Supercomputer");
    }

    /**
     * Returns economy actions available to workers.
     *
     * @param actor the actor interacting with the Supercomputer
     * @param location the location of the Supercomputer
     * @param direction the direction from the actor
     * @return available economy actions
     */
    @Override
    public ActionList allowableActions(Actor actor, Location location, String direction) {
        ActionList actions = new ActionList();

        if (!actor.hasAbility(Ability.WORKER)) {
            return actions;
        }

        for (Item item : actor.getInventory().getItems()) {
            Sellable sellable = item.asCapability(Sellable.class).orElse(null);
            if (sellable != null) {
                actions.add(new SellAction(item, sellable));
            }
        }

        addPurchaseOption(actions, new FirstAidKit());
        addPurchaseOption(actions, new SterilisationBox());
        addPurchaseOption(actions, new AccessCard(AccessLevel.LEVEL_ONE));
        addPurchaseOption(actions, new AccessCard(AccessLevel.LEVEL_TWO));
        addPurchaseOption(actions, new AccessCard(AccessLevel.LEVEL_THREE));

        return actions;
    }

    /**
     * Adds a purchase option if the item is purchasable.
     *
     * @param actions the action list
     * @param item the item offered for purchase
     */
    private void addPurchaseOption(ActionList actions, Item item) {
        Purchasable purchasable = item.asCapability(Purchasable.class).orElse(null);
        if (purchasable != null) {
            actions.add(new PurchaseAction(item, purchasable));
        }
    }
}