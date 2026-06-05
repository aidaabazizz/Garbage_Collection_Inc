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

public class PlasmaCutter extends Item implements Purchasable {

    private final QuotaManager quotaManager;
    private static final int BUY_PRICE = 50;
    private static final int WEIGHT = 7;
    private static final int BURN_DAMAGE = 1;
    private static final int BURN_DURATION = 5;

    public PlasmaCutter(QuotaManager quotaManager) {
        super("Plasma Cutter", '>');
        this.addNewStatistic(ItemStatistics.WEIGHT, new BaseStatistic(WEIGHT));
        this.makePortable();
        this.quotaManager = quotaManager;
        this.enableAbility(Ability.HAS_PLASMA_CUTTER);
    }

    @Override
    public int getPurchasePrice() {
        return BUY_PRICE;
    }

    @Override
    public String purchasedBy(Actor buyer, GameMap map, CreditHolder wallet) {
        buyer.addStatus(new BurningStatus(BURN_DURATION));
        return buyer + " suffers " + BURN_DAMAGE + " damage and burn for " + BURN_DURATION + " turns due to the searing chute temperatures!";
    }

    @Override
    public String failedPurchaseBy(Actor buyer, GameMap map, CreditHolder wallet) {
        return buyer + " lacks sufficient Worker Credits to purchase a Plasma Cutter.";
    }

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
