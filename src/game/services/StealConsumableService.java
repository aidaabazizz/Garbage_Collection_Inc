package game.services;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.Location;
import game.capabilities.Consumable;
import game.enums.Ability;

/**
 * Service class that handles stealing and consuming items from adjacent workers.
 * This keeps stealing logic separate from state transition logic.
 *
 * @author Aida
 * @version 1.0
 */
public class StealConsumableService {

    /**
     * Finds the first adjacent worker carrying a consumable item, removes that item
     * from the worker's inventory, and immediately consumes it by the stealing actor.
     *
     * @param actor the actor stealing and consuming the item
     * @param location the current location of the actor
     * @return a description of the result
     */
    public String stealAndConsumeFromAdjacentWorker(Actor actor, Location location) {
        for (Exit exit : location.getExits()) {
            Location destination = exit.getDestination();

            if (!destination.containsAnActor()) {
                continue;
            }

            Actor target = destination.getActor();

            if (!target.hasAbility(Ability.WORKER)) {
                continue;
            }

            for (Item item : target.getInventory().getItems()) {
                Consumable consumable = item.asCapability(Consumable.class).orElse(null);

                if (consumable == null) {
                    continue;
                }

                target.getInventory().remove(item);

                String result = consumable.consumedBy(actor);

                if (consumable.isFinished()) {
                    consumable.cleanUp(actor, location);
                }

                return "\u001B[33m" + actor + " steals " + item + " from " + target
                        + " at " + exit.getName()
                        + " and immediately eats it.\u001B[0m " + result;
            }
        }

        return actor + " looks hungry, but no adjacent worker has a consumable item.";
    }
}