package game.actions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import game.managers.QuotaManager;
import game.capabilities.Depositable;

/**
 * An action that allows a depositable item to be deposited into the Super Computer
 * in exchange for Company Credits.
 * The deposited item is removed from the actor's inventory and the quota progress
 * is updated based on the Company Credits earned.
 *
 * @author Victoria Tay Wen Xie
 * @version 1.0
 */
public class DepositAction extends Action {
    /**
     * This is the item that is being deposited
     */
    private final Item item;
    /**
     * This is the depositable behavior of the item
     */
    private final Depositable depositable;
    /**
     * This manages company credit quota
     */
    private final QuotaManager quotaManager;

    /**
     * This will create a deposit action
     * @param item item that is being deposited
     * @param depositable depositable behavior of the item
     * @param quotaManager manages company credit quota
     */
    public DepositAction(Item item, Depositable depositable, QuotaManager quotaManager) {
        this.item = item;
        this.depositable = depositable;
        this.quotaManager = quotaManager;
    }

    /**
     * Deposits the item, removes it from the actor's inventory and updates the company credit quota.
     * @param actor the actor performing the action
     * @param map the map the actor is on
     * @return a description of the deposit result
     */
    @Override
    public String execute(Actor actor, GameMap map) {
        if (!actor.getInventory().remove(item)) {
            return actor + " failed to deposit " + item + " (item missing from inventory).";
        }
        int creditsEarned = depositable.getCompanyCreditValue();
        String progressMsg = quotaManager.addCompanyCredits(creditsEarned);
        String resultMsg = depositable.depositBy(actor, map, quotaManager);
        return actor + " deposits " + item + " to the SuperComputer for "
                + creditsEarned + " Company Credits.\n"
                + progressMsg + "\n"
                + resultMsg;
    }

    /**
     * This returns the description shown in the menu.
     *
     * @param actor the actor performing the action
     * @return the menu description
     */
    @Override
    public String menuDescription(Actor actor) {
        return actor + " deposits " + item + " for Company Credits";
    }
}
