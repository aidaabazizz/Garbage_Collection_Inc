package game.behaviours;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.behaviours.Behaviour;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.Location;
import game.actions.InfectAction;
import game.capabilities.Infectable;
import java.util.List;
import java.util.Optional;

//req4
public class InfectBehaviour implements Behaviour<Actor, Action> {

    @Override
    public Action operate(Actor actor, Location location) {
        for (Exit exit : location.getExits()) {
            Location adj = exit.getDestination();

            // 1. Check if there is an actor at the adjacent location
            if (adj.containsAnActor()) {
                Actor targetActor = adj.getActor();
                Optional<Infectable> maybeHost = targetActor.asCapability(Infectable.class);

                if (maybeHost.isPresent()) {
                    return new InfectAction(maybeHost.get());
                }
            }

            // 2. Check if there are infectable items at the adjacent location
            List<Infectable> infectableItems = adj.getItemsAs(Infectable.class);
            if (!infectableItems.isEmpty()) {
                // Get the first infectable item found
                return new InfectAction(infectableItems.get(0));
            }
        }
        return null;
    }
}
