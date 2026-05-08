package game.stages;

import edu.monash.fit2099.engine.positions.Location;
import game.grounds.AbstractTree;

public interface TreeStage {
    TreeStage execute(Location location, AbstractTree tree);
    char getDisplayChar();
}
