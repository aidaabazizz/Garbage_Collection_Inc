package game.stages;

import edu.monash.fit2099.engine.positions.Location;

/**
 * An interface representing a specific stage in the lifecycle of a tree.
 * This interface follows the State Pattern, allowing a tree to delegate its
 * per-turn behavior and visual representation to its current stage. Stages can
 * transition to new states (growth stages) by returning a different TreeStage
 * in the execute method.
 *
 * @author Jewell Gomes
 */
public interface TreeStage {
    /**
     * Performs the primary logic for the current stage.
     * This method is responsible for handling aging, spawning behaviors,
     * and determining if the tree should mature into a new stage.
     *
     * @param location The current location of the tree ground object.
     * @return The next stage of the tree.
     */
    TreeStage execute(Location location);
    /**
     * Provides the visual representation of the tree for its current lifecycle stage.
     *
     * @return The character used to represent this specific stage on the map.
     */
    char getDisplayChar();
}
