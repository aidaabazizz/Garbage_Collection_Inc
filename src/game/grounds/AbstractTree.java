package game.grounds;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.stages.TreeStage;

/**
 * This class serves as the foundation for all mutated trees found on
 * the moon. It acts as a ground object that actors cannot walk
 * through. It delegates its behavior and visual appearance to a
 * specific stage object to handle growth over time.
 *
 * @author Jewell Gomes
 */
public class AbstractTree extends Ground {
    private TreeStage stage;

    /**
     * This method initializes the tree with a starting character and
     * name and sets the first stage of its lifecycle.
     */
    public AbstractTree(String name, TreeStage initialStage) {
        super(initialStage.getDisplayChar(), name);
        this.stage = initialStage;
    }

    /**
     * This method asks the current stage for the correct character to
     * show on the game map.
     */
    @Override
    public char getDisplayChar() {
        return stage.getDisplayChar();
    }

    /**
     * This method runs every turn to update the state of the tree by
     * executing the logic of the current stage.
     */
    @Override
    public void tick(Location location) {
        this.stage = stage.execute(location, this);
    }

    /**
     * This method ensures that actors are blocked from entering the
     * tile where the tree is growing.
     */
    @Override
    public boolean canActorEnter(Actor actor) {
        return false;
    }
}
