package game.grounds;

import game.stages.FleshySproutStage;

public class FleshyTree extends AbstractTree {
    public FleshyTree() {
        super('y', "Fleshy Tree", new FleshySproutStage());
    }
}
