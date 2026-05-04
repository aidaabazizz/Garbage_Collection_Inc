package game.items;

import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.statistics.BaseStatistic;
import game.enums.ItemStatistics;

/**
 * A lightweight piece of ancient data storage scrap.
 * Primarily used as a collectible corporate asset with minimal weight.
 *
 * @author Jewell Gomes
 */
public class FloppyDisk extends Item {
    /**
     * Constructor for the Floppy Disk.
     * Assigns a weight of one unit and makes the item portable.
     */
    public FloppyDisk() {
        super("Floppy Disk", '⊟');
        this.addNewStatistic(ItemStatistics.WEIGHT, new BaseStatistic(1));
        this.makePortable();
    }
}
