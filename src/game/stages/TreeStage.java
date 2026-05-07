package game.stages;

import edu.monash.fit2099.engine.positions.Location;
import game.grounds.AbstractTree;

public interface TreeStage {
    public TreeStage execute(Location location, AbstractTree tree);
    public char getDisplayChar();
}
