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

/**
 * A behavioral strategy that enables an actor to identify and infect valid hosts in its vicinity.
 * This class facilitates Requirement 4, directing the actor to scan all adjacent tiles
 * for any entity or item that implements the Infectable interface. If a host is
 * detected, it initiates an infection sequence.
 *
 * @author Chathya Attanayake
 * @version 1.0
 */
public class InfectBehaviour implements Behaviour<Actor, Action> {

    /**
     * Scans the surrounding exits of the actor's current location to find valid hosts.
     * The behavior prioritizes infecting actors before checking for infectable items.
     *
     * @param actor    The actor performing the scanning behavior (e.g., a Parasite).
     * @param location The current location of the scanning actor.
     * @return An InfectAction if a valid host is detected nearby; null otherwise.
     */
    @Override
    public Action operate(Actor actor, Location location) {
        // Scan all adjacent tiles through the exits
        for (Exit exit : location.getExits()) {
            Location adj = exit.getDestination();

            // 1. Check if there is an actor at the adjacent location
            if (adj.containsAnActor()) {
                Actor targetActor = adj.getActor();
                // Check for Infectable capability (e.g., Workers or Undead)
                Optional<Infectable> maybeHost = targetActor.asCapability(Infectable.class);

                if (maybeHost.isPresent()) {
                    return new InfectAction(maybeHost.get());
                }
            }

            // 2. Check if there are infectable items at the adjacent location
            // REQ4: Specifically targets inorganic things like Lanterns or organic Cookies
            List<Infectable> infectableItems = adj.getItemsAs(Infectable.class);
            if (!infectableItems.isEmpty()) {
                // Get the first infectable item found
                return new InfectAction(infectableItems.get(0));
            }
        }
        //No valid target found in immediate surroundings
        return null;
    }
}
