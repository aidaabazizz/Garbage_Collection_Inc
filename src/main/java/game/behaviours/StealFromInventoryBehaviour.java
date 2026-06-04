package game.behaviours;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.behaviours.Behaviour;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.Location;
import game.actions.StealConsumableAction;
import game.capabilities.Consumable;
import game.enums.Ability;

/**
 * Behaviour that allows CrazyChicken to steal and consume an adjacent worker's consumable item.
 *
 * @author Aida
 * @version 1.0
 */
public class StealFromInventoryBehaviour implements Behaviour<Actor, Action> {

    @Override
    public Action operate(Actor actor, Location location) {
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

                if (consumable != null) {
                    return new StealConsumableAction(target, item, consumable, exit.getName());
                }
            }
        }

        return null;
    }
}