package game.actions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import game.managers.AlarmManager;

/**
 * REQ4:
 * An Action that triggers the global security alarm system.
 * This action is typically used by security cameras or automated surveillance systems
 * to alert the map when an unauthorized actor (like a worker or player) is detected.
 *
 * It interacts with the AlarmManager singleton to manage the alarm's state and duration.
 *
 * @author Jewell Gomes
 */
public class TriggerAlarmAction extends Action {
    /**
     * Executes the alarm trigger logic.
     * Checks if an alarm is currently active; if not, it activates a new alarm
     * for the predefined duration.
     *
     * @param actor The actor performing the action (e.g., a Security Camera).
     * @param map The map the actor is currently on.
     * @return A string describing the result of the action (e.g., alarm activation or monitoring status).
     */
    @Override
    public String execute(Actor actor, GameMap map) {
        if (!AlarmManager.getInstance().isActive()) {
            AlarmManager.getInstance().activate(AlarmManager.ALARM_DURATION);
            return "!!! SECURITY CAMERA DETECTED WORKER - ALARM ACTIVATED !!!";
        }
        return actor + " continues monitoring.";
    }

    /**
     * Describes the action in the player's menu.
     * Since this action is automated (triggered by the environment/NPCs),
     * it returns an empty string so it does not appear in the player's command menu.
     *
     * @param actor The actor performing the action.
     * @return An empty string.
     */
    @Override
    public String menuDescription(Actor actor) {
        return "";
    }
}