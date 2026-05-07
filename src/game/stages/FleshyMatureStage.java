package game.stages;

import edu.monash.fit2099.engine.positions.Location;
import game.grounds.AbstractTree;

public class FleshyMatureStage extends AbstractTreeStage {
    @Override
    public TreeStage execute(Location location, AbstractTree tree) {
        if (getNearbyWorker(location) != null) {
            spawner.spawnUndead(location);
        }
        return this;
    }

    @Override
    public char getDisplayChar() { return 'Y'; }
}
