package game.stages;

import edu.monash.fit2099.engine.positions.Location;
import game.grounds.AbstractTree;

public class FleshySproutStage extends AbstractTreeStage {
    private int age = 0;

    @Override
    public TreeStage execute(Location location, AbstractTree tree) {
        if (getNearbyWorker(location) != null) {
            spawner.spawnSlime(location);
            return this;
        }
        age++;
        if (age >= 20) {
            // reset the counter here
            // if the 25% fails, we start counting another 20 turns.
            age = 0;

            if (random.nextDouble() <= 0.25) {
                return new FleshySaplingStage();
            }
        }
        return this;
    }

    @Override
    public char getDisplayChar(){
        return 'y';
    }
}
