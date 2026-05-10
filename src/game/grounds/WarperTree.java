package game.grounds;

import game.stages.WarperSaplingStage;

/**
 * This class represents a tree tied to the teleportation grid of the
 * moon. It is a specific type of tree that begins its life as
 * a warper sapling.
 *
 * @author Jewell Gomes
 */
public class WarperTree extends AbstractTree {
    /**
     * This constructor sets up the initial properties of the plant.
     * It assigns the display character and name and starts the tree
     * directly at the sapling stage.
     */
    public WarperTree() {
        super("Warper Tree", new WarperSaplingStage());
    }
}
