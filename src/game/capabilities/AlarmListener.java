package game.capabilities;

/**
 * REQ4:
 * A contract for objects that must respond to changes in the facility alarm state.
 * This interface facilitates the Observer pattern, allowing the AlarmManager
 * to notify subscribed entities of lockdown events (REQ4).
 *
 * @author Jewell Gomes
 */
public interface AlarmListener {
    /** Routine executed when the facility-wide alarm is engaged. */
    public void onAlarmActivated();
    /** Routine executed when the facility-wide alarm is disengaged. */
    public void onAlarmDeactivated();
}
