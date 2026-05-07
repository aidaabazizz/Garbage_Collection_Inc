package game.grounds;

import game.stages.WarperSaplingStage;

public class WarperTree extends AbstractTree {
    public WarperTree() {
        super('w', "Warper Tree", new WarperSaplingStage());
    }
}
