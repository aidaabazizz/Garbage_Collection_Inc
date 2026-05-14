package game.actions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import game.capabilities.Infectable;

/**
 * An Action representing the infection process initiated by an infectious entity.
 * This class facilitates Requirement 4, where a Parasite attempts to infect a host.
 * Upon execution, the target host triggers its unique reaction to the infection,
 * and the infectious actor (the Parasite) is immediately removed from the map
 * as it perishes during the process.
 *
 * @author Chathya Attanayake
 * @version 1.0
 */
public class InfectAction extends Action {
    /** The target host (Actor or Item) that can be infected. */
    private final Infectable target;

    /**
     * Constructor for InfectAction.
     *
     * @param target The host implementing the Infectable interface to be infected.
     */
    public InfectAction(Infectable target) {
        this.target = target;
    }

    /**
     * Executes the infection logic on the target host.
     * This method triggers the host's reaction at the moment of infection and
     * subsequently removes the infectious actor from the map, simulating its death.
     *
     * @param actor The Actor performing the infection (e.g., the Parasite).
     * @param map   The GameMap where the interaction is occurring.
     * @return A descriptive string detailing the infection and the death of the attacker.
     */
    @Override
    public String execute(Actor actor, GameMap map) {
        //Trigger the specific reaction for the host
        target.reactToInfection(map.locationOf(actor));
        map.removeActor(actor); // Parasite dies immediately
        return actor + " has infected " + target + " and perished.";
    }

    /**
     * Describes the action for the menu system.
     * Since this action is an automated NPC behavior for the Parasite, it returns
     * an empty string so it does not appear in the player's command menu.
     *
     * @param actor The Actor performing the action.
     * @return An empty string.
     */
    @Override
    public String menuDescription(Actor actor) { return ""; }
}


