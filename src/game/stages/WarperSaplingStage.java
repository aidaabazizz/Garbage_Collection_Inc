package game.stages;

import edu.monash.fit2099.engine.positions.Location;
import game.grounds.AbstractTree;

public class WarperSaplingStage extends AbstractTreeStage {
    private int age = 0;

    @Override
    public TreeStage execute(Location location, AbstractTree tree) {
        age++;
        if (age >= 20) {
            age = 0;

            if (random.nextDouble() <= 0.25) {
                System.out.println("Warper Tree Sapling ('w') at " + location + " matures into a Warper Mature Tree ('W')!");
                return new WarperMatureStage();
            }
        }
        return this;
    }

    @Override
    public char getDisplayChar() { return 'w'; }
}
