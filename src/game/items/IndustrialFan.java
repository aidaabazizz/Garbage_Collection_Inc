package game.items;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.statistics.BaseStatistic;
import game.capabilities.CreditHolder;
import game.capabilities.Sellable;
import game.enums.ItemStatistics;

/**
 * Heavy industrial equipment that can be sold to the Supercomputer.
 * Value: 30 credits
 *
 * @author Aida
 */
public class IndustrialFan extends Item implements Sellable {
    private static final int SELL_PRICE = 30;
    private static final int WEIGHT = 10;

    public IndustrialFan() {
        super("Industrial Fan", '≋');
        this.addNewStatistic(ItemStatistics.WEIGHT, new BaseStatistic(WEIGHT));
        this.makePortable();
    }

    @Override
    public int getSellPrice() {
        return SELL_PRICE;
    }

    @Override
    public String soldBy(Actor seller, GameMap map, CreditHolder wallet) {
        return seller + " sells Industrial Fan for " + SELL_PRICE + " credits.";
    }
}