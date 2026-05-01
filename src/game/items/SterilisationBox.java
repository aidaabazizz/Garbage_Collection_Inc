package game.items;


import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.statistics.BaseStatistic;
import game.ItemStatistics;
import game.capabilities.Ability;

/**
 * A specialized piece of corporate equipment used to purify consumables.
 * While in a worker's inventory, it grants the ability to safely
 * consume spoiled food and toxic water.
 *
 * @author Jewell Gomes
 */
public class SterilisationBox extends Item {

    /**
     * Constructor for the Sterilisation Box.
     * Sets the weight to seven units and enables the sterilizer ability
     * for the carrier.
     */
    public SterilisationBox() {
        super("Sterilisation Box", '▣');
        this.addNewStatistic(ItemStatistics.WEIGHT, new BaseStatistic(7));
        this.makePortable();
        this.enableAbility(Ability.STERILIZER);
    }
}
