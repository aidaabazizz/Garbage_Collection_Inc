package game.actions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;

/**
 * Action for ScrapSnatcher to steal a depositable item from the ground.
 *
 * @author Aida
 * @version 1.0
 */
public class StealResourceAction extends Action {
    private final Item targetItem;

    public StealResourceAction(Item targetItem) {
        this.targetItem = targetItem;
    }

    @Override
    public String execute(Actor actor, GameMap map) {
        Location currentLocation = map.locationOf(actor);

        if (currentLocation.getItems().contains(targetItem)) {
            currentLocation.removeItem(targetItem);
            actor.getInventory().add(targetItem);
            return actor + " snatches " + targetItem + " from the ground!";
        }

        return actor + " tries to snatch but " + targetItem + " is no longer there.";
    }

    @Override
    public String menuDescription(Actor actor) {
        return actor + " snatches " + targetItem;
    }
}