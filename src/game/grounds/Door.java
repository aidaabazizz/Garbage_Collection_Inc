package game.grounds;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Ground;
import game.capabilities.AlarmListener;
import game.capabilities.Unlockable;
import game.managers.AlarmManager;

/**
 * A security barrier that restricts movement within the facility.
 * The door can be transitioned from a locked to an unlocked state.
 * It implements the alarm listener interface to automatically lock
 * and prevent entry when the facility alarm system is active (REQ4),
 * regardless of its prior authorization status.
 *
 * @author Jewell Gomes
 */
public class Door extends Ground implements Unlockable, AlarmListener {
    private boolean isUnlocked = false;
    private boolean alarmLock = false;

    /**
     * Constructs a new Door instance.
     * Initializes the door with a display character of '=' and the display name "Door".
     * The door automatically subscribes itself to the AlarmManager to receive
     * notifications about facility alarm state changes.
     */
    public Door() {
        super('=', "Door");
        AlarmManager.getInstance().subscribe(this);
    }

    /**
     * Transitions the door to an unlocked state.
     */
    @Override
    public void unlock() {
        this.isUnlocked = true;
    }

    /**
     * Provides the current lock status of the door.
     * @return True if the door is authorized for entry, false otherwise.
     */
    @Override
    public boolean isUnlocked() {
        return isUnlocked;
    }

    /**
     * Triggered when the facility alarm is activated.
     * Forces the door into a locked state for security purposes.
     */
    @Override
    public void onAlarmActivated() { this.alarmLock = true; }

    /**
     * Triggered when the facility alarm is deactivated.
     * Releases the emergency lock on the door.
     */
    @Override
    public void onAlarmDeactivated() { this.alarmLock = false; }

    /**
     * Determines the visual representation of the door based on
     * the alarm status and the unlock status.
     * @return The character representing a locked or open door.
     */
    @Override
    public char getDisplayChar() {
        // If the alarm is on, it always looks like a locked door even if worker swiped card
        if (alarmLock) {
            return '=';
        }
        return isUnlocked ? '_' : '=';
    }

    /**
     * if the door is unlocked, any actor can step into the door
     * @param actor the Actor to check
     * @return true if the door is unlocked, false otherwise.
     */
    @Override
    public boolean canActorEnter(Actor actor) {
        return isUnlocked && !alarmLock;
    }
}
