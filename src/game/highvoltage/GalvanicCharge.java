package game.highvoltage;

import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.positions.Location;

import java.util.HashSet;
import java.util.Set;

/**
 * A data-carrier and state-tracking object representing a single high-voltage surge event.
 *
 * In the context of the High-Voltage Galvanic System (Requirement 3), this class serves
 * as the "Carrier" that transports energy metadata (source name, damage, and display interface)
 * from an Emitter to various Resonators.
 *
 * Crucially, it maintains a "Memory" (visited set) of the current wave. This ensures
 * physical consistency by:
 * 1. Preventing infinite recursion loops between adjacent conductive tiles (e.g., PoweredFloors).
 * 2. Ensuring that a single entity (Actor or Item) is only processed once per surge event,
 *    preventing duplicate damage from the same lightning strike or coil pulse.
 *
 * @author Jewell Gomes
 */
public class GalvanicCharge {
    /** The descriptive name of the energy source (e.g., "a massive Lightning Bolt"). */
    private final String sourceName;
    /** The terminal interface used for logging wave-related events to the player. */
    private final Display display;
    /** The base damage payload carried by this specific surge. */
    private final int damage;
    /** The set of coordinates already reached by this wave during propagation. */
    private final Set<Location> visited = new HashSet<>();

    /**
     * Constructor for a GalvanicCharge.
     *
     * @param sourceName The name of the emitter triggering the surge.
     * @param display    The Display object used to print electrical feedback.
     * @param damage     The damage value to be applied to reactive entities.
     */
    public GalvanicCharge(String sourceName, Display display, int damage) {
        this.sourceName = sourceName;
        this.display = display;
        this.damage = damage;
    }

    /**
     * Attempts to mark a specific location as "zapped" by this wave.
     *
     * This method is the primary safety mechanism for the galvanic system. It uses
     * a HashSet to determine if the location has already been processed by the
     * current propagation chain.
     *
     * @param location The map coordinate to check and mark.
     * @return true if the location was NOT previously visited (successful visit);
     *         false if the wave has already reached this tile.
     */
    public boolean visit(Location location) {
        return visited.add(location); // false = already visited
    }

    /**
     * Gets the name of the source that generated this charge.
     *
     * @return A string representing the source (e.g., "Tesla Coil Pulse").
     */
    public String getSourceName() { return sourceName; }
    /**
     * Gets the display interface associated with this charge.
     *
     * @return The Display instance for terminal output.
     */
    public Display getDisplay() { return display; }
    /**
     * Gets the damage value carried by this charge.
     *
     * @return The integer damage payload.
     */
    public int getDamage() { return damage; }
    /**
     * Returns the set of all locations currently visited by this wave.
     * This is used by conductive resonators (like PoweredFloors) to perform
     * recursion checks before propagating the wave further.
     *
     * @return A set of visited Location objects.
     */
    public Set<Location> getVisited() { return visited; }
}
