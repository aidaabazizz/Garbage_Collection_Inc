package game.stages;

import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.positions.Location;
import game.grounds.AbstractTree;

public class FleshySproutStage extends AbstractTreeStage {
    private static final int GROWTH_THRESHOLD = 20;
    private static final double GROWTH_CHANCE = 0.25;
    private int age = 0;

    @Override
    public TreeStage execute(Location location, AbstractTree tree) {
        if (getNearbyWorker(location) != null) {
            if (spawner.spawnSlime(location)) {
                return this; // Action performed, skip aging/growth
            }
        }
        age++;
        if (age >= GROWTH_THRESHOLD) {
            // reset the counter here
            // if the 25% fails, we start counting another 20 turns.
            age = 0;

            if (random.nextDouble() <= GROWTH_CHANCE) {
                display.println(String.format(
                        "Fleshy Tree Sprout ('%s') at %s grows into a Fleshy Sapling ('v')!",
                        getDisplayChar(), location));
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
