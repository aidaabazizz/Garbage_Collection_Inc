package game.behaviours;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.behaviours.Behaviour;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.Location;
import game.actions.TriggerAlarmAction;
import game.capabilities.Ability;
import game.managers.AlarmManager;

/**
 * REQ4:
 * A class that represents a security scanning behavior for actors.
 * It allows an actor (such as a security camera) to scan its immediate surroundings
 * (adjacent exits) for specific unauthorized individuals, specifically those with
 * the WORKER ability.
 *
 * If a worker is detected in an adjacent square and the global alarm is not
 * already active, this behavior returns an action to trigger the alarm.
 *
 * @author Jewell Gomes
 */
public class ScanningBehaviour implements Behaviour<Actor, Action> {
    /**
     * Scans the adjacent exits of the actor's current location to detect actors with
     * the WORKER ability.
     *
     * If such an actor is found and the alarm manager indicates that no alarm is
     * currently active, it provides a TriggerAlarmAction.
     *
     * @param actor The actor performing the scanning behavior.
     * @param location The current location of the scanning actor.
     * @return A TriggerAlarmAction if a worker is detected nearby and the alarm is inactive;
     *         null otherwise.
     */
    @Override
    public Action operate(Actor actor, Location location) {
        for (Exit exit : location.getExits()) {
            Location destination = exit.getDestination();
            if (destination.containsAnActor() && destination.getActor().hasAbility(Ability.WORKER)) {
                if (!AlarmManager.getInstance().isActive()) {
                    return new TriggerAlarmAction();
                }
            }
        }
        return null;
    }
}