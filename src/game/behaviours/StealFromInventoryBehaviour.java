// game.behaviours/StealFromInventoryBehaviour.java
package game.behaviours;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.behaviours.Behaviour;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.Location;
import game.actions.StealAction;
import game.enums.Ability;

/**
 * Causes the actor to steal consumable items from nearby workers' inventories.
 * The stolen item is dropped on the ground at the actor's location.
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
                        // Check if this is a consumable item (Apple, Cookies, Flask, FirstAidKit)
                        String itemName = item.toString().toLowerCase();
                        if (itemName.contains("apple") || itemName.contains("cookie") ||
                                itemName.contains("flask") || itemName.contains("first aid")) {
                            // Steal the item (remove from target, add to ground)
                            target.getInventory().remove(item);
                            location.addItem(item);
                            return new StealAction(target, item, exit.getName());
                        }
                    }
                }
            }
        }
        return null;
    }
}