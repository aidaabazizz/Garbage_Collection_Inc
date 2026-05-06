package game.items;

import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.statistics.BaseStatistic;
import game.enums.ItemStatistics;

/**
 * A heavy piece of archaic technology representing scrap material.
 * This item serves as a high-weight burden for workers collecting assets.
 *
 * @author Jewell Gomes
 */
public class CRTMonitor extends Item {

    /**
     * Constructor for the CRT Monitor.
     * Assigns a substantial weight of thirty units and makes the item portable.
     */
    public CRTMonitor() {
        super("CRT Monitor", '◙');
        this.addNewStatistic(ItemStatistics.WEIGHT, new BaseStatistic(30));
        this.makePortable();
    }
}
