package game.stages;

import edu.monash.fit2099.engine.positions.Location;
import game.grounds.AbstractTree;

public class FleshySproutStage extends AbstractTreeStage {
    private int age = 0;

    @Override
    public TreeStage execute(Location location, AbstractTree tree) {
        age++;
        if (getNearbyWorker(location) != null) {
            Location targetLocation = getSpawnLocation(location);
            if (targetLocation != null) {
                spawner.spawnSlime(targetLocation);
                return this;
            }
        }
        if (age >= 20) {
            // reset the counter here
            // if the 25% fails, we start counting another 20 turns.
            age = 0;

            if (random.nextDouble() <= 0.25) {
                System.out.println("Fleshy Tree Sprout ('y') at " + location + " grows into a Fleshy Sapling ('v')!");
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
