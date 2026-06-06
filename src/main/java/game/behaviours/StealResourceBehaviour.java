package game.behaviours;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.behaviours.Behaviour;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.Location;
import game.actions.StealResourceAction;
import game.capabilities.Sellable;

/**
 * Behaviour that causes an actor to steal any Sellable item from the ground.
 *
 * @author Aida
 */
public class StealResourceBehaviour implements Behaviour<Actor, Action> {

    @Override
    public Action operate(Actor actor, Location location) {
        for (Item item : location.getItems()) {
            if (item.asCapability(Sellable.class).isPresent()) {
                return new StealResourceAction(item);
            }
        }
        return null;
    }
}