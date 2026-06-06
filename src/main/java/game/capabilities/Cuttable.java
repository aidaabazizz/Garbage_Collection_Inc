package game.capabilities;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.enums.Ability;

/**
 * Interface for objects that can be cut using a Plasma Cutter.
 * Implementing classes define whether they can be cut and what they become
 * after being cut.
 *
 * @author Victoria Tay Wen Xie
 * @version 1.0
 */
public interface Cuttable {
    /**
     * Checks whether this object can be cut by the given actor.
     * @param actor the actor attempting to cut the object
     * @return true if the object can be cut, otherwise false
     */
    default boolean canBeCut(Actor actor) {
        if (actor == null) {
            return false;
        }

        // Globally look through the actor's inventory for the cutter capability
        for (Item item : actor.getInventory().getItems()) {
            if (item.hasAbility(Ability.HAS_PLASMA_CUTTER)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Executes the cutting behaviour of this object.
     * @param actor the actor performing the cut
     * @param map the current game map
     * @return the description of the result of the cut action
     */
    String executeCut(Actor actor, GameMap map, Location target);
}
