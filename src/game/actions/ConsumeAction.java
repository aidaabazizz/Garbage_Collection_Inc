package game.actions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import game.capabilities.Consumable;

/**
 * A generalized action representing the consumption of an item or environmental feature.
 * This class facilitates interactions where an actor receives effects from a consumable
 * object and manages the lifecycle of that object post-consumption.
 *
 * @author Jewell Gomes
 */
public class ConsumeAction extends Action {
    private final Consumable target;
    private final String name;

    /**
     * Constructor to initialize the consumption action with a target and its name.
     *
     * @param target The object providing the consumption logic.
     * @param name   The name of the object to be displayed in logs and menus.
     */
    public ConsumeAction(Consumable target, String name){
        this.target = target;
        this.name = name;
    }

    /**
     * Executes the consumption logic and handles item depletion.
     * This method triggers the specific effects of the consumable on the actor.
     * It further checks if the consumable is exhausted; if so, it triggers the
     * cleanup routine to remove the object from the actor's inventory or the map location.
     *
     * @param actor The Actor performing the consumption.
     * @param map   The GameMap where the action occurs.
     * @return A formatted string describing the actor, the item consumed, and the resulting effect.
     */
    @Override
    public String execute(Actor actor, GameMap map) {
        String result = target.consumedBy(actor);
        if (target.isFinished()) {
            target.cleanUp(actor, map.locationOf(actor));
        }
        return String.format("%s consumes %s. %s", actor, name, result);
    }

    /**
     * Describes the action for display in the player's command menu.
     *
     * @param actor The Actor performing the action.
     * @return A string describing the available consumption choice.
     */
    @Override
    public String menuDescription(Actor actor) {
        return actor + " consumes " + name;
    }
}
