// game/behaviours/SingingBehaviour.java (updated)
package game.behaviours;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.behaviours.Behaviour;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.Location;
import game.actions.ConsumePlayerAction;
import game.capabilities.HypnotizedStatus;
import game.enums.Ability;

import java.util.Optional;

/**
 * Behaviour for slimes that are hypnotized by Elsa's singing.
 * Causes slimes to consume adjacent workers.
 * Uses asCapability() pattern - NO instanceof!
 *
 * @author Aida
 */
public class SingingBehaviour implements Behaviour<Actor, Action> {

    @Override
    public Action operate(Actor actor, Location location) {
        // Use asCapability() pattern - consistent with your codebase!
        Optional<HypnotizedStatus> hypnotizedCap = actor.asCapability(HypnotizedStatus.class);

        if (!hypnotizedCap.isPresent() || !hypnotizedCap.get().isHypnotized()) {
            return null;
        }

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