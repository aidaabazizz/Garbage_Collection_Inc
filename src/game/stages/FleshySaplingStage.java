package game.stages;

import edu.monash.fit2099.engine.positions.Location;
import game.grounds.AbstractTree;

/**
 * This class represents the fleshy sapling stage which is the second phase
 * of growth for the mutated fleshy tree. It acts as a middle transition
 * between a sprout and a mature tree and is responsible for managing
 * its own aging process.
 *
 * @author Jewell Gomes
 */
public class FleshySaplingStage extends AbstractTreeStage {
    private static final int GROWTH_THRESHOLD = 25;
    private static final double GROWTH_CHANCE = 0.50;
    private int age = 0;

    /**
     * The execute method is called every turn to process the behavior of
     * the sapling. It adds one to the age and checks if the maturation
     * threshold has been reached. If it has there is a fifty percent
     * chance to mature into a fleshy mature tree.
     */
    @Override
    public TreeStage execute(Location location, AbstractTree tree) {
        age++;
        if (age >= GROWTH_THRESHOLD) {
            // reset the age here when the 50% fails, we wait another 25 turns before trying again
            age = 0;

            if (random.nextDouble() <= GROWTH_CHANCE) {
                display.println(String.format(
                        "Fleshy Tree Sapling ('%s') at %s matures into a Fleshy Mature Tree ('Y')!",
                        getDisplayChar(),
                        location.toString()
                ));
                return new FleshyMatureStage();
            }
        }
        return this;
    }

    /**
     * This method returns the character v which is used to represent the
     * fleshy sapling on the game world map.
     */
    @Override
    public char getDisplayChar() { return 'v'; }
}
