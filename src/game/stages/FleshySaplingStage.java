package game.stages;

import edu.monash.fit2099.engine.positions.Location;
import game.managers.Spawner;

/**
 * This class represents the fleshy sapling stage which is the second phase
 * of growth for the mutated fleshy tree. It acts as a middle transition
 * between a sprout and a mature tree and is responsible for managing
 * its own aging process.
 *
 * @author Jewell Gomes
 */
public class FleshySaplingStage extends FleshyTreeStage {
    private static final int GROWTH_THRESHOLD = 25;
    private static final double GROWTH_CHANCE = 0.50;
    /**
     * Constructor for the Sapling stage.
     * @param spawner The spawning manager to be passed forward to the mature stage.
     */
    public FleshySaplingStage(Spawner spawner) { super(spawner); }
    /**
     * The execute method is called every turn to process the behavior of
     * the sapling. It adds one to the age and checks if the maturation
     * threshold has been reached. If it has there is a fifty percent
     * chance to mature into a fleshy mature tree.
     */
    @Override
    public TreeStage execute(Location location) {
        updateAge(location, GROWTH_THRESHOLD, "Fleshy Sapling");
        if (checkGrowthThreshold(GROWTH_THRESHOLD, GROWTH_CHANCE, location, "Fleshy Sapling")) {
            display.println(String.format(
                    "Fleshy Tree Sapling ('%s') at %s matures into a Fleshy Mature Tree ('Y')!",
                    getDisplayChar(),
                    location.toString()
            ));
            return new FleshyMatureStage(spawner);
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
