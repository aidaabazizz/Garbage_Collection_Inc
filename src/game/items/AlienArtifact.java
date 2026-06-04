package game.items;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.statistics.BaseStatistic;
import game.capabilities.CreditHolder;
import game.capabilities.Sellable;
import game.enums.ItemStatistics;

/**
 * Mysterious alien technology - the most valuable depositable resource.
 * Value: 50 credits
 *
 * @author Aida
 */
public class AlienArtifact extends Item implements Sellable {
    private static final int SELL_PRICE = 50;
    private static final int WEIGHT = 8;

    public AlienArtifact() {
        super("Alien Artifact", '⨀');
        this.addNewStatistic(ItemStatistics.WEIGHT, new BaseStatistic(WEIGHT));
        this.makePortable();
    }

    @Override
    public int getSellPrice() {
        return SELL_PRICE;
    }

    @Override
    public String soldBy(Actor seller, GameMap map, CreditHolder wallet) {
        return seller + " sells Alien Artifact for " + SELL_PRICE + " credits.";
    }
}