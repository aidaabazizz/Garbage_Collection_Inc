package game.grounds;

import game.stages.*;
import game.teleportstrategies.TreeWarpStrategy;

/**
 * This class represents a tree tied to the teleportation grid of the
 * moon. It is a specific type of tree that begins its life as
 * a warper sapling.
 *
 * @author Jewell Gomes
 */
public class WarperTree extends AbstractTree {
    private static final int SAPLING_THRESHOLD = 20;
    private static final double SAPLING_CHANCE = 0.25;
    private static final char SAPLING_CHAR = 'w';
    private static final char MATURE_CHAR = 'W';
    private static final String SAPLING_NAME = "Warper Sapling";

    /**
     * This constructor sets up the initial properties of the plant.
     * It assigns the display character and name and starts the tree
     * directly at the sapling stage.
     */
    public WarperTree() {
        super("Warper Tree", createLifecycle());
    }

    /**
     * Configures the growth stages for the Warper Tree.
     * Uses Dependency Injection to connect the Sapling stage to the Mature stage.
     * By using the generic EvolutionStage class, the hierarchy remains simplified
     * and strictly follows the "No Magic Numbers" policy.
     *
     * @return The starting growth stage (Sapling) for the warper tree.
     */
    private static TreeStage createLifecycle() {
        TreeStage mature = new WarperMatureStage(new TreeWarpStrategy(), MATURE_CHAR);

        AbstractTreeStage sapling = new EvolutionStage(
                SAPLING_NAME, SAPLING_CHAR, SAPLING_THRESHOLD, SAPLING_CHANCE
        );

        sapling.setNextStage(mature);
        return sapling;
    }
}
