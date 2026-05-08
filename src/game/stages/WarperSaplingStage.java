package game.stages;

import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.positions.Location;
import game.grounds.AbstractTree;

public class WarperSaplingStage extends AbstractTreeStage {
    private static final int GROWTH_THRESHOLD = 20;
    private static final double GROWTH_CHANCE = 0.25;
    private int age = 0;

    @Override
    public TreeStage execute(Location location, AbstractTree tree) {
        age++;
        if (age >= GROWTH_THRESHOLD) {
            age = 0;

            if (random.nextDouble() <= GROWTH_CHANCE) {
                display.println(String.format(
                        "Warper Tree Sapling ('%s') at %s matures into a Warper Mature Tree ('W')!",
                        getDisplayChar(), location));
                return new WarperMatureStage();
            }
        }
        return this;
    }

    @Override
    public char getDisplayChar() { return 'w'; }
}
