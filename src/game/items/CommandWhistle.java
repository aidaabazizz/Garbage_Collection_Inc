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
import game.capabilities.Sellable;
import game.enums.Ability;
import game.enums.ItemStatistics;
import game.sanctuary.SanctuaryTool;
import game.utils.SpatialSearch;

public class CommandWhistle extends Item implements SanctuaryTool, Purchasable {

    private static final int PURCHASE_PRICE = 100;
    private static final int WEIGHT = 1;


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
        return new UseCommandWhistleAction(this).execute(actor, map);
    }

    // --- Purchasable Implementation ---
    @Override
    public int getPurchasePrice() {
        return PURCHASE_PRICE; // Rare item price
    }

    @Override
    public String purchasedBy(Actor buyer, GameMap map, CreditHolder wallet) {
        wallet.deductCredits(this.getPurchasePrice());
        buyer.getInventory().add(this);
        return buyer + " bought a Command Whistle for " + PURCHASE_PRICE + " credits.";
    }

}