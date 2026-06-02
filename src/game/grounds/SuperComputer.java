package game.grounds;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.actions.PurchaseAction;
import game.actions.SellAction;
import game.actions.StabiliseDistortionAction;
import game.enums.DistortionCapability;
import game.capabilities.Purchasable;
import game.capabilities.Sellable;
import game.enums.AccessLevel;
import game.items.AccessCard;
import game.items.FirstAidKit;
import game.items.SterilisationBox;
import game.enums.Ability;
import game.sanctuary.SanctuaryTool;

import java.util.List;

/**
 * A Supercomputer terminal that allows workers to buy and sell items.
 *
 * @author Suchir
 * @author Chathya Attanayake (modified by)
 * @version 2.0
 */
public class SuperComputer extends Ground implements SanctuaryTool {

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

        // --- REQ4: COMPLEX INTERACTION (Scanning for Distortions) ---
        // Scan adjacent tiles for Corrupted grounds using Capabilities
        List<Location> corruptedSites = game.utils.SpatialSearch.getAdjacentLocationsWithCapability(
                location,
                game.enums.DistortionCapability.CORRUPTED
        );
        for (Location site : corruptedSites) {
            actions.add(new game.actions.StabiliseDistortionAction(site));
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

    @Override
    public String activateSanctuaryEffect(Actor actor, GameMap map, Location location) {
        return "Supercomputer emits a low-frequency stabilization hum throughout the sector.";
    }
}