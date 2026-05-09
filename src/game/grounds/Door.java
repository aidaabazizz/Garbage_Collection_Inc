package game.grounds;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Ground;
import game.capabilities.AlarmListener;
import game.capabilities.Unlockable;
import game.enums.AccessLevel;
import game.managers.AlarmManager;

/**
 * A security barrier that restricts movement within the facility.
 * The door can be transitioned from a locked to an unlocked state.
 * It implements the alarm listener interface to automatically lock
 * and prevent entry when the facility alarm system is active.
 *
 * @author Suchir
 * @version 1.0
 */
public class Door extends Ground implements Unlockable, AlarmListener {

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
        AlarmManager.getInstance().subscribe(this);
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
     * Triggered when the facility alarm is activated.
     * Forces the door into emergency locked state.
     */
    @Override
    public void onAlarmActivated() {
        this.alarmLock = true;
    }

    /**
     * Triggered when the facility alarm is deactivated.
     * Releases the emergency lock on the door.
     */
    @Override
    public void onAlarmDeactivated() {
        this.alarmLock = false;
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
}