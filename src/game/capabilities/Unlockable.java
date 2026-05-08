package game.capabilities;

import game.enums.AccessLevel;

/**
 * A contract for environmental obstacles that restrict movement based on a lock state.
 * This interface facilitates the security bypass mechanics used by access cards
 * and facility doors (REQ1).
 *
 * @author Suchir
 *
 */
public interface Unlockable {
    /** Transitions the object from a locked state to an unlocked state. */
    void unlock();

    /**
     * Queries the current access state of the object.
     * @return True if the object is passable, false if it is locked.
     */
    boolean isUnlocked();

    /**
     * Gets the access level required to unlock this object.
     *
     * @return required access level
     */
    AccessLevel getRequiredAccessLevel();
}
