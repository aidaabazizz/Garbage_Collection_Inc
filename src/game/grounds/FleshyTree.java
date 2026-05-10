package game.grounds;

import game.managers.Spawner;
import game.stages.FleshySproutStage;

/**
 * This class represents a mutated tree that has been altered by toxic
 * waste. It is a specific type of tree that begins its life as
 * a fleshy sprout.
 *
 * @author Jewell Gomes
 */
public class FleshyTree extends AbstractTree {
    /**
     * The constructor initializes a new instance of a fleshy tree.
     * It sets the starting display character to y and assigns the
     * name fleshy tree to the object. It sets the initial growth
     * state to the sprout stage so the tree can begin its lifecycle
     * from the very beginning.
     */
    public FleshyTree(Spawner spawner) {
        super("Fleshy Tree", new FleshySproutStage(spawner));
    }
}
