package game.items;

import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.Location;
import game.managers.AlarmManager;

/**
 * REQ4:
 * A stationary timing device used to manage the facility alarm system.
 * This item acts as the primary clock for the AlarmManager, ensuring that
 * alarm durations and states are updated once per game round.
 */
public class AlarmTimer extends Item {

    /**
     * Constructor for the Alarm Timer.
     * Initializes the timer with a unique display character and ensures it
     * remains fixed to its spawn location.
     */
    public AlarmTimer() {
        super("Alarm Timer", '⏱'); // REQ4
        this.makeNonPortable();
    }

    /**
     * Updates the global alarm state.
     * This method is called by the game engine once per round to advance
     * the internal logic of the AlarmManager.
     * @param currentLocation The map location where the timer is located.
     */
    @Override
    public void tick(Location currentLocation) {
        // This method is important for REQ4 as it is called by engine exactly once per game round
        AlarmManager.getInstance().tick();
    }
}
