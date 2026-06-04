package game.grounds;

import game.managers.Spawner;
import game.stages.*;

/**
 * This class represents a mutated tree that has been altered by toxic
 * waste. It is a specific type of tree that begins its life as
 * a fleshy sprout.
 *
 * @author Jewell Gomes
 */
public class FleshyTree extends AbstractTree {
    private static final int SPROUT_THRESHOLD = 20;
    private static final double SPROUT_CHANCE = 0.25;
    private static final int SAPLING_THRESHOLD = 25;
    private static final double SAPLING_CHANCE = 0.50;
    private static final String SAPLING_NAME = "Fleshy Sapling";
    private static final String SPROUT_NAME = "Fleshy Sprout";
    private static final char SPROUT_CHAR = 'y';
    private static final char SAPLING_CHAR = 'v';
    private static final char MATURE_CHAR = 'Y';

    /**
     * The constructor initializes a new instance of a fleshy tree.
     * It sets the starting display character to y and assigns the
     * name fleshy tree to the object. It sets the initial growth
     * state to the sprout stage so the tree can begin its lifecycle
     * from the very beginning.
     */
    public FleshyTree(Spawner spawner) {
        super("Fleshy Tree", createLifecycle(spawner));
    }

    /**
     * Centralizes the assembly of the tree's lifecycle stages.
     * This method fulfills the requirement for a flexible hierarchy by manually
     * "wiring" the stages together using Dependency Injection. This prevents
     * individual stage classes from being tightly coupled to their successors.
     *
     * @param spawner The spawner required by the biological growth stages.
     * @return The initial growth stage (Sprout) of the fleshy tree lifecycle.
     */
    private static TreeStage createLifecycle(Spawner spawner) {
        TreeStage mature = new FleshyMatureStage(spawner, MATURE_CHAR);

        AbstractTreeStage sapling = new EvolutionStage(SAPLING_NAME, SAPLING_CHAR, SAPLING_THRESHOLD, SAPLING_CHANCE);

        AbstractTreeStage sprout = new FleshySproutStage(spawner, SPROUT_NAME, SPROUT_CHAR, SPROUT_THRESHOLD, SPROUT_CHANCE);
        sprout.setNextStage(sapling);
        sapling.setNextStage(mature);

        return sprout;
    }
}
