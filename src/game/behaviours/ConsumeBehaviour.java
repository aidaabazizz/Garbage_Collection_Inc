package game.behaviours;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.behaviours.Behaviour;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.Location;
import game.actions.ConsumeAction;
import game.capabilities.Consumable;

/**
 * A behavior that enables an actor to consume items located on its current tile.
 * The behavior checks all items present at the actor's current location to
 * determine if any of them possess the consumable capability.
 *
 * @author Jewell Gomes
 */
public class ConsumeBehaviour implements Behaviour<Actor, Action> {

    /**
     * Evaluates the items at the current location and returns a consumption action
     * for the first valid consumable item found.
     *
     * @param actor The actor performing the behavior.
     * @param location The current location of the actor.
     * @return A consume action if a consumable item is present on the ground, or null otherwise.
     */
    @Override
    public Action operate(Actor actor, Location location) {
        for (Item item : location.getItems()) {
            Consumable consumable = item.asCapability(Consumable.class).orElse(null);

            if (consumable != null) {
                if (!item.allowableActions(actor, location.map()).getUnmodifiableActionList().isEmpty()) {
                    return new ConsumeAction(consumable, item.toString());
                } else {
                    System.out.println(actor + " found " + item + " but it's not functional!");
                }
            }
        }
        return null;
    }
}