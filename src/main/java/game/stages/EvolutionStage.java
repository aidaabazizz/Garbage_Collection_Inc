package game.stages;

import edu.monash.fit2099.engine.positions.Location;

/**
 * Based on A2 feedback:
 * A generic implementation of a growth stage within a tree's lifecycle.
 * This class facilitates the evolution of a tree from one stage to another based on
 * a temporal threshold and a success probability.
 * It is designed to be configurable via its constructor, allowing multiple tree
 * types (such as Fleshy or Warper trees) to share the same growth logic. This
 * simplifies the hierarchy by removing the need for specific subclass saplings.
 *
 * @author Jewell Gomes
 */
public class EvolutionStage extends AbstractTreeStage {
    /**
     * Constructor to initialize an EvolutionStage with specific growth parameters.
     *
     * @param name         The name of the growth stage (e.g., "Sapling").
     * @param displayChar  The character representing this stage on the map.
     * @param threshold    The age threshold required to attempt evolution.
     * @param chance       The success rate of evolution once the threshold is met.
     */
    public EvolutionStage(String name, char displayChar, int threshold, double chance) {
        super(name, displayChar, threshold, chance);
    }

    /**
     * Updates the stage's internal state during each game turn.
     * This method handles the aging process and checks if the evolution conditions are met.
     * If maturation is successful, it returns the next stage in the lifecycle which
     * was provided via dependency injection.
     *
     * @param location The current map location of the tree holding this stage.
     * @return The next TreeStage in the sequence if evolution occurs; otherwise, this instance.
     */
    @Override
    public TreeStage execute(Location location) {
        updateAge(location);
        if (checkGrowthThreshold(location) && nextStage != null) {
            display.println(name + " at " + location + " grows into " + nextStage + "!");
            return this.nextStage;
        }
        return this;
    }
}

