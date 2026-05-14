package game.behaviours;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.behaviours.Behaviour;
import edu.monash.fit2099.engine.capabilities.Status;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.Location;
import game.actions.ConsumePlayerAction;
import game.capabilities.HypnotizedStatus;
import game.enums.Ability;

/**
 * Behaviour for slimes that are hypnotized by Elsa's singing.
 * Causes slimes to consume adjacent workers.
 * Uses class comparison - NO instanceof!
 *
 * @author Aida
 * @version 1.0
 */
public class SingingBehaviour implements Behaviour<Actor, Action> {

    /**
     * Checks if the actor has an active HypnotizedStatus.
     * Uses class comparison - NOT instanceof (SOLID compliant).
     *
     * @param actor The actor to check
     * @return true if hypnotized and status is active, false otherwise
     */
    private boolean isHypnotized(Actor actor) {
        for (Status status : actor.statuses()) {
            // Class comparison - NOT instanceof!
            if (status.getClass() == HypnotizedStatus.class && status.isStatusActive()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Action operate(Actor actor, Location location) {
        if (!isHypnotized(actor)) {
            return null;
        }

        // Check adjacent tiles for workers
        for (Exit exit : location.getExits()) {
            Location destination = exit.getDestination();
            if (destination.containsAnActor()) {
                Actor target = destination.getActor();
                if (target.hasAbility(Ability.WORKER)) {
                    return new ConsumePlayerAction(target, exit.getName());
                }
            }
        }
        return null;
    }
}