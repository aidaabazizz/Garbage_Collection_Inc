package game.managers;

import edu.monash.fit2099.engine.displays.Display;
import game.capabilities.AlarmListener;

import java.util.ArrayList;
import java.util.List;

/**
 * REQ4:
 * A singleton manager class that handles the facility-wide alarm system.
 * It maintains the state of the alarm and notifies subscribed listeners
 * when the alarm is activated or deactivated.
 *
 * @author Jewell Gomes
 */
public class AlarmManager {
    private static AlarmManager instance;
    /** The default duration for an alarm sequence. */
    public static final int ALARM_DURATION = 5;
    /** ANSI escape code to change text color to green. */
    private static final String GREEN_TEXT = "\u001B[32m";
    /** ANSI escape code to reset text color to default. */
    private static final String RESET_COLOR = "\u001B[0m";
    private static final String DEACTIVATION_MESSAGE = "!!! ALARM DEACTIVATED - FACILITY RETURNING TO NORMAL STATE !!!";
    /** The number of turns remaining in the current alarm state. */
    private int activeTurns = 0;
    /** The list of objects listening for alarm state changes. */
    private final List<AlarmListener> listeners = new ArrayList<>();

    /**
     * Private constructor to enforce the singleton pattern.
     */
    private AlarmManager() {}

    /**
     * Provides access to the singleton instance of the AlarmManager.
     *
     * @return The singleton AlarmManager instance.
     */
    public static AlarmManager getInstance() {
        if (instance == null) instance = new AlarmManager();
        return instance;
    }

    /**
     * Registers an object to receive notifications regarding alarm state changes.
     *
     * @param listener The AlarmListener to be subscribed.
     */
    public void subscribe(AlarmListener listener) { listeners.add(listener); }

    /**
     * Triggers the facility alarm for a specified number of turns.
     * Notifies all subscribed listeners that the alarm has been activated.
     *
     * @param duration The number of game turns the alarm will remain active.
     */
    public void activate(int duration) {
        this.activeTurns = duration;
        for (AlarmListener listener : listeners) {
            listener.onAlarmActivated();
        }
    }

    /**
     * Decrements the alarm timer by one turn.
     * If the timer reaches zero, the alarm is deactivated and all listeners are notified.
     */
    public void tick() {
        if (activeTurns > 0) {
            activeTurns--;
            if (activeTurns == 0) {
                new Display().println(GREEN_TEXT + DEACTIVATION_MESSAGE + RESET_COLOR);
                for (AlarmListener listener : listeners) listener.onAlarmDeactivated();
            }
        }
    }

    /**
     * Checks whether the facility alarm is currently active.
     *
     * @return True if the alarm is active, false otherwise.
     */
    public boolean isActive() { return activeTurns > 0; }
}
