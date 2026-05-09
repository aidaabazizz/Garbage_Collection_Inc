package game.capabilities;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import game.enums.AccessLevel;

/**
 * A contract for environmental obstacles that restrict movement based on a lock state.
 * This interface facilitates the security bypass mechanics used by access cards
 * and facility doors (REQ1).
 *
 * @author Jewell Gomes
 * @author Suchir
 * @author Victoria Tay Wen Xie
 */
public interface Unlockable {
    /** Transitions the object from a locked state to an unlocked state. */
    public void unlock();

    /**
     * Queries the current access state of the object.
     * @return True if the object is passable, false if it is locked.
     */
    public boolean isUnlocked();

    /**
     * Gets the access level required to unlock this object.
     *
     * @return required access level
     */
    AccessLevel getRequiredAccessLevel();

    /**
     *Applies specific effect that occurs when the door is unlocked
     * @param actor the actor performing the unlock action
     * @param map the current game map containing the door
     * @return a descriptive message of the effect that occured
     */
    String applyUnlockEffect(Actor actor, GameMap map);
}
