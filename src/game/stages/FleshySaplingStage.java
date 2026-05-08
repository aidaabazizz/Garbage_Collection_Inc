package game.stages;

import edu.monash.fit2099.engine.positions.Location;
import game.grounds.AbstractTree;

public class FleshySaplingStage extends AbstractTreeStage {
    private static final int GROWTH_THRESHOLD = 25;
    private static final double GROWTH_CHANCE = 0.50;
    private int age = 0;

    @Override
    public TreeStage execute(Location location, AbstractTree tree) {
        age++;
        if (age >= GROWTH_THRESHOLD) {
            // reset the age here when the 50% fails, we wait another 25 turns before trying again
            age = 0;

            if (random.nextDouble() <= GROWTH_CHANCE) {
                display.println(String.format(
                        "Fleshy Tree Sapling ('%s') at %s matures into a Fleshy Mature Tree ('Y')!",
                        getDisplayChar(),
                        location.toString()
                ));
                return new FleshyMatureStage();
            }
        }
        return this;
    }

    @Override
    public char getDisplayChar() { return 'v'; }
}
