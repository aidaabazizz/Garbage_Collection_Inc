package game.behaviours;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.behaviours.Behaviour;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.Location;
import game.actions.AttackAction;
import game.enums.Ability;

/**
 * A behavior that allows an actor to automatically attack adjacent targets.
 * The behavior scans all neighboring locations and identifies actors that
 * possess the worker ability. If a valid target is found, it returns an
 * action to perform an attack.
 *
 * @author Jewell Gomes
 */
public class AttackBehaviour implements Behaviour<Actor, Action> {

    /**
     * Scans the surroundings of the actor and returns an attack action if a worker is nearby.
     *
     * @param actor The actor performing the behavior.
     * @param location The current location of the actor.
     * @return An attack action if a valid target is adjacent, or null if no target is found.
     */
    @Override
    public Action operate(Actor actor, Location location) {
        for (Exit exit : location.getExits()) {
            Location destination = exit.getDestination();
            if (destination.containsAnActor()) {
                Actor target = destination.getActor();
                if (target.hasAbility(Ability.WORKER)) {
                    return new AttackAction(target, exit.getName(), actor.getIntrinsicWeapon());
                }
            }
        }
        return null;
    }
}
