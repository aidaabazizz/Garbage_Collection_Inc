package game.stages;

import edu.monash.fit2099.engine.positions.Location;
import game.grounds.AbstractTree;

/**
 * A class representing the Sapling stage of a Warper Tree.
 * This stage tracks its age and has a chance to mature into a Warper Mature Tree
 * once a specific age threshold is reached.
 *
 * @author Jewell Gomes
 */
public class WarperSaplingStage extends AbstractTreeStage {
    private static final int GROWTH_THRESHOLD = 20;
    private static final double GROWTH_CHANCE = 0.25;
    private int age = 0;

    /**
     * Executes the lifecycle logic for the Warper Sapling.
     * Increments the age and, if the threshold is met, performs a random check
     * to determine if the tree matures into the next stage.
     *
     * @param location The current location of the tree on the map.
     * @param tree The AbstractTree object this stage belongs to.
     * @return A new {@link WarperMatureStage} if maturation occurs; otherwise, this instance.
     */
    @Override
    public TreeStage execute(Location location, AbstractTree tree) {
        age++;
        if (age >= GROWTH_THRESHOLD) {
            age = 0;

            if (random.nextDouble() <= GROWTH_CHANCE) {
                display.println(String.format(
                        "Warper Tree Sapling ('%s') at %s matures into a Warper Mature Tree ('W')!",
                        getDisplayChar(), location));
                return new WarperMatureStage();
            }
        }
        return this;
    }

    /**
     * Returns the display character for the Warper Sapling.
     *
     * @return The character 'w'.
     */
    @Override
    public char getDisplayChar() { return 'w'; }
}
