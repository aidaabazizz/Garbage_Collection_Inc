package game.items;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.statistics.BaseStatistic;
import game.actions.CutAction;
import game.capabilities.BurningStatus;
import game.capabilities.CreditHolder;
import game.capabilities.Purchasable;
import game.enums.Ability;
import game.capabilities.Cuttable;
import game.enums.ItemStatistics;
import game.managers.QuotaManager;

/**
 * A purchasable Plasma Cutter that allows actors to cut cuttable
 * items and grounds.
 * Purchasing the Plasma Cutter causes the buyer to suffer a burning
 * effect due to the high temperatures when buying from Super Computer.
 *
 * @author Victoria Tay Wen Xie
 * @version 1.0
 */
public class PlasmaCutter extends Item implements Purchasable {

    /**
     * The quota manager associated with the game.
     */
    private final QuotaManager quotaManager;
    /**
     * Purchase price of the Plasma Cutter.
     */
    private static final int BUY_PRICE = 50;
    /**
     * Weight of the Plasma Cutter.
     */
    private static final int WEIGHT = 7;

    /**
     * Damage associated with the burning effect.
     */
    private static final int BURN_DAMAGE = 1;

    /**
     * Damage when buying plasma cutter.
     */
    private static final int BUY_DAMAGE = 5;

    /**
     * Duration of the burning effect.
     */
    private static final int BURN_DURATION = 5;

    /**
     * Creates a Plasma Cutter.
     * @param quotaManager the quota manager used by the game
     */
    public PlasmaCutter(QuotaManager quotaManager) {
        super("Plasma Cutter", '>');
        this.addNewStatistic(ItemStatistics.WEIGHT, new BaseStatistic(WEIGHT));
        this.makePortable();
        this.quotaManager = quotaManager;
        this.enableAbility(Ability.HAS_PLASMA_CUTTER);
    }

    /**
     * Returns the purchase price of the Plasma Cutter.
     * @return the purchase price
     */
    @Override
    public int getPurchasePrice() {
        return BUY_PRICE;
    }

    /**
     * Applies the effects of purchasing the Plasma Cutter which is
     * buyer takes 5 damage and is burned for 5 turns.
     * @param buyer the actor purchasing the item
     * @param map the map the actor is on
     * @param wallet the wallet used for the purchase
     * @return a description of the purchase effect
     */
    @Override
    public String purchasedBy(Actor buyer, GameMap map, CreditHolder wallet) {
        buyer.hurt(BUY_DAMAGE);
        buyer.addStatus(new BurningStatus(BURN_DURATION));
        return buyer + " suffers " + BURN_DAMAGE + " damage and burn for " + BURN_DURATION + " turns due to the searing chute temperatures!";
    }

    /**
     * This is to handle the scenario where purchase fails.
     * It will return the meaningful message when the purchase fails.
     * @param buyer the actor attempting the purchase
     * @param map the map the actor is on
     * @param wallet the wallet used for the purchase
     * @return a failure message
     */
    @Override
    public String failedPurchaseBy(Actor buyer, GameMap map, CreditHolder wallet) {
        return buyer + " lacks sufficient Worker Credits to purchase a Plasma Cutter.";
    }

    /**
     * Returns cut actions that can be performed on a cuttable ground
     * at the specified location.
     *
     * @param location the location being inspected
     * @return a list of allowable cut actions
     */
    @Override
    public ActionList allowableActions(Location location) {
        ActionList actions = new ActionList();
        if (location.getGround().hasAbility(Ability.CUTTABLE)) {
            Cuttable cuttableGround = (Cuttable) location.getGround();
            if (cuttableGround.canBeCut(null)) {
                actions.add(new CutAction(cuttableGround, location.getGround().toString(), location));
            }
        }
        return actions;
    }

    /**
     * Returns cut actions that can be performed on cuttable items
     * in the owner's inventory.
     *
     * @param owner the actor carrying the Plasma Cutter
     * @param map the map the actor is on
     * @return a list of allowable cut actions
     */
    @Override
    public ActionList allowableActions(Actor owner, GameMap map) {
        ActionList actions = new ActionList();
        for (Item item : owner.getInventory().getItems()) {
            if (item.hasAbility(Ability.CUTTABLE)) {
                Cuttable cuttableItem = (Cuttable) item;
                if (cuttableItem.canBeCut(owner)) {
                    // Inventory items do not sit on a map coordinate tiles, so pass null as the third argument
                    actions.add(new CutAction(cuttableItem, item.toString(), null));
                }
            }
        }
        return actions;
    }
}
