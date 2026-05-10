package game.stages;

import edu.monash.fit2099.engine.positions.Location;
import game.actions.TeleportAction;
import game.grounds.AbstractTree;
import game.teleportstrategies.TreeWarpStrategy;

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
     * @return A new WarperMatureStage if maturation occurs; otherwise, this instance.
     */
    @Override
    public TreeStage execute(Location location, AbstractTree tree) {
        age++;
        display.println(String.format("Warper Sapling at %s current age: (%d/%d)",
                location, age, GROWTH_THRESHOLD));
        if (age >= GROWTH_THRESHOLD) {
            age = 0;

            if (random.nextDouble() <= GROWTH_CHANCE) {
                display.println(String.format(
                        "Warper Tree Sapling ('%s') at %s matures into a Warper Mature Tree ('W')!",
                        getDisplayChar(), location));
                var action = new TeleportAction(new TreeWarpStrategy());
                return new WarperMatureStage(action);
            } else {
                display.println("Warper Sapling at " + location + " failed the 25% maturation roll. Resetting counter.");
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
