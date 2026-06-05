package game.highvoltage;

import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.positions.Location;
import java.util.Set;

/**
 * A high-level abstraction representing the metadata and state-tracking of a
 * high-voltage galvanic surge event.
 *
 * This interface is the cornerstone of the Dependency Inversion Principle (DIP)
 * implementation for the moon facility's electrical ecosystem. It serves as a
 * "Carrier" that decouples energy sources (Emitters) from the entities that
 * absorb or respond to energy (Resonators).
 *
 * By depending on this abstraction, the game's world-altering logic (Requirement 3)
 * and API-driven weather effects (Requirement 5) can trigger complex terrain
 * morphing and biological metamorphosis without being coupled to specific
 * concrete charge classes.
 *
 * @author Jewell Gomes
 */
public interface ChargeContext {

    /**
     * Returns the descriptive name of the energy source triggering the surge.
     * This is used to provide dynamic console feedback to the player (e.g.,
     * "a massive Lightning Bolt" or "conductive moisture").
     *
     * @return A string representing the display name of the surge's origin.
     */
    String getSourceName();

    /**
     * Provides access to the terminal interface used for logging electrical events.
     * This ensures that all components in a cascading propagation chain can output
     * feedback to the user interface consistently.
     *
     * @return The Display instance associated with the current surge event.
     */
    Display getDisplay();

    /**
     * Returns the base damage payload carried by this specific surge.
     * Different sources (e.g., a small battery vs. a lightning bolt) may carry
     * different damage values.
     *
     * @return The integer damage value to be applied to reactive entities.
     */
    int getDamage();

    /**
     * Attempts to mark a specific map coordinate as "visited" by this energy wave.
     *
     * This is the primary safety mechanism for the galvanic system. It prevents
     * infinite recursion loops between adjacent conductive tiles (e.g., Powered Floors)
     * and ensures that a single entity is only processed once per surge event,
     * preventing duplicate damage from the same pulse.
     *
     * @param location The map coordinate to check and mark.
     * @return true if the location was NOT previously visited; false if the wave
     *         has already reached this tile in the current propagation chain.
     */
    boolean visit(Location location);

    /**
     * Returns the set of all locations currently reached by this propagation chain.
     * This is utilized by complex resonators to perform "Look-Ahead" checks
     * or historical analysis of the surge's path across the facility.
     *
     * @return A set of Location objects representing the wave's visited history.
     */
    Set<Location> getVisited();
}