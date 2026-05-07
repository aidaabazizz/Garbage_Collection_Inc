package game.stages;

import edu.monash.fit2099.engine.positions.Location;
import game.grounds.AbstractTree;

public class FleshySaplingStage extends AbstractTreeStage {
    private int age = 0;

    @Override
    public TreeStage execute(Location location, AbstractTree tree) {
        age++;
        if (age >= 25) {
            // reset the age here when the 50% fails, we wait another 25 turns before trying again
            age = 0;

            if (random.nextDouble() <= 0.50) {
                System.out.println("Fleshy Tree Sapling ('v') at " + location + " matures into a Fleshy Mature Tree ('Y')!");
                return new FleshyMatureStage();
            }
        }
        return this;
    }

    @Override
    public char getDisplayChar() { return 'v'; }
}
