package game.capabilities;

import edu.monash.fit2099.engine.positions.Location;

/**
 * A contract defining the behavior of entities and items that can be host to an infection.
 * This interface facilitates Requirement 4, allowing varied targets (Workers, Undead,
 * Lanterns, and Cookies) to define unique responses both at the moment of infection
 * and as recurring effects during the infection's lifecycle.
 *
 * @author Chathya Attanayake
 * @version 1.0
 */
public interface Infectable {
    /**
     * Defines the immediate reaction of the target at the exact moment of contact with a parasite.
     * For example, an Undead might perish instantly, while a Worker might gain a new status.
     *
     * @param location The map location where the infection occurs.
     */
    default void reactToInfection(Location location){

    }

    /**
     * Defines the recurring logic that executes every game turn while the infection is active.
     * For example, a Worker might spawn a parasite every few turns, or a Lantern might drain oil.
     *
     * @param location The map location where the host is currently residing.
     */
    default void updateInfection(Location location){

    }

}
