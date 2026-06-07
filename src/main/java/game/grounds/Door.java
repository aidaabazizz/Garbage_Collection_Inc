package game.grounds;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Ground;
import game.capabilities.Unlockable;
import game.enums.AccessLevel;

/**
 * A security barrier that restricts movement within the facility.
 * The door can be transitioned from a locked to an unlocked state.
 * It implements the alarm listener interface to automatically lock
 * and prevent entry when the facility alarm system is active.
 *
 * @author Suchir
 * @version 1.0
 */
public abstract class Door extends Ground implements Unlockable {

    private boolean isUnlocked = false;
    private boolean alarmLock = false;
    private final AccessLevel requiredAccessLevel;

    /**
     * Constructs a new Level 1 Door instance.
     */
    public Door() {
        this(AccessLevel.LEVEL_ONE);
    }

    /**
     * Constructs a new Door instance with a required access level.
     *
     * @param requiredAccessLevel the access level required to unlock this door
     */
    public Door(AccessLevel requiredAccessLevel) {
        super('=', "Door");
        this.requiredAccessLevel = requiredAccessLevel;
    }

    /**
     * Transitions the door from locked to unlocked.
     */
    @Override
    public void unlock() {
        this.isUnlocked = true;
    }

    /**
     * Queries the current access state of the door.
     *
     * @return true if the door is unlocked, false otherwise
     */
    @Override
    public boolean isUnlocked() {
        return isUnlocked;
    }

    /**
     * Gets the access level required to unlock this door.
     *
     * @return the required access level
     */
    @Override
    public AccessLevel getRequiredAccessLevel() {
        return requiredAccessLevel;
    }

    /**
     * Determines the visual representation of the door.
     *
     * @return the display character for the door
     */
    @Override
    public char getDisplayChar() {
        if (alarmLock) {
            return '=';
        }

        return isUnlocked ? '_' : '=';
    }

    /**
     * Checks whether an actor can enter the door tile.
     *
     * @param actor the actor trying to enter
     * @return true if the door is unlocked and no alarm lock is active
     */
    @Override
    public boolean canActorEnter(Actor actor) {
        return isUnlocked && !alarmLock;
    }

    @Override
    public String applyUnlockEffect(Actor actor, GameMap map) {
        return "";
    }
}