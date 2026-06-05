package game.grounds;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.actions.DepositAction;
import game.actions.PurchaseAction;
import game.actions.SellAction;
import game.capabilities.Depositable;
import game.capabilities.Purchasable;
import game.capabilities.Sellable;
import game.enums.AccessLevel;
import game.items.AccessCard;
import game.items.FirstAidKit;
import game.items.PlasmaCutter;
import game.items.SterilisationBox;
import game.enums.Ability;
import game.managers.QuotaManager;

/**
 * A Supercomputer terminal that allows workers to buy and sell items.
 *
 * @author Suchir
 * @author Victoria Tay (modified by)
 * @version 1.0
 */
public class SuperComputer extends Ground {
    private final QuotaManager quotaManager;
    /**
     * Constructor for Supercomputer.
     */
    public SuperComputer(QuotaManager quotaManager) {
        super('≡', "Supercomputer");
        this.quotaManager = quotaManager;
    }

    /**
     * Returns economy actions available to workers.
     * It will rever all the facility access towards SuperComputer when the quota is not met.
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

        if (!quotaManager.isFacilityAccessActive()) {
            return actions;
        }

        for (Item item : actor.getInventory().getItems()) {
            Sellable sellable = item.asCapability(Sellable.class).orElse(null);
            if (sellable != null) {
                actions.add(new SellAction(item, sellable));
            }
            // This is to check for deposit features
            Depositable depositable = item.asCapability(Depositable.class).orElse(null);
            if (depositable != null) {
                actions.add(new DepositAction(item, depositable, this.quotaManager));
            }
        }

        addPurchaseOption(actions, new FirstAidKit());
        addPurchaseOption(actions, new SterilisationBox());
        addPurchaseOption(actions, new AccessCard(AccessLevel.LEVEL_ONE));
        addPurchaseOption(actions, new AccessCard(AccessLevel.LEVEL_TWO));
        addPurchaseOption(actions, new AccessCard(AccessLevel.LEVEL_THREE));

        PlasmaCutter plasmaCutter = new PlasmaCutter(quotaManager);
        addPurchaseOption(actions, plasmaCutter);

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