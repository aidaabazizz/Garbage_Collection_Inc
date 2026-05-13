package game.behaviours;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.behaviours.Behaviour;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.Location;
import game.actions.StealAndConsumeAction;
import game.capabilities.Consumable;
import game.enums.Ability;

import java.util.Optional;

/**
 * Causes the actor to steal consumable items from nearby workers' inventories.
 * The stolen item is immediately consumed and disappears from the game.
 *
 * @author Aida
 */
public class StealFromInventoryBehaviour implements Behaviour<Actor, Action> {

    @Override
    public Action operate(Actor actor, Location location) {
        // Check all adjacent tiles for workers
        for (Exit exit : location.getExits()) {
            Location destination = exit.getDestination();
            if (destination.containsAnActor()) {
                Actor target = destination.getActor();
                if (target.hasAbility(Ability.WORKER)) {
                    // Find consumable items in target's inventory
                    for (Item item : target.getInventory().getItems()) {
                        Optional<Consumable> consumable = item.asCapability(Consumable.class);
                        if (consumable.isPresent()) {
                            // Steal AND consume the item in one action
                            return new StealAndConsumeAction(target, item, consumable.get(), exit.getName());
                        }
                    }
                }
            }
        }
        return null;
    }
}