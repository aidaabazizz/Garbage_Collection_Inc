package game.highvoltage;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.positions.Location;

/**
 * An interface representing any object or system capable of emitting a
 * high-voltage galvanic charge.
 *
 * This interface acts as the "Emitter" in the High-Voltage Galvanic System (REQ3).
 * It provides the contract for releasing energy into the map and managing
 * the lifecycle of the energy source itself.
 *
 * SOLID Design Proof:
 * This interface utilizes a default method for lifecycle management to avoid
 * 'instanceof' checks. Stationary sources (Grounds) inherit a no-op, while
 * portable sources (Items) override it to handle their own consumption.
 *
 * @author Jewell Gomes
 */
public interface ChargeSource {
    /**
     * Triggers the release of high-voltage energy at a specific map coordinate.
     * Implementations typically perform Area-of-Effect (AoE) scans to discover
     * and trigger {@link ChargeReactive} objects.
     *
     * @param location   The origin point of the energy release.
     * @param display    The terminal interface for outputting emission logs.
     * @param sourceName The display name of the energy source.
     */
    void releaseCharge(Location location, Display display, String sourceName);

    /**
     * Handles the cleanup or consumption of the energy source after a discharge.
     *
     * Interface Lifecycle Logic:
     * This is a default method to maintain the Single Responsibility Principle (SRP).
     * 1. Ground objects (like Tesla Coils) inherit the default empty body because
     *    they are permanent map features.
     * 2. Item objects (like Portable Batteries) override this to remove themselves
     *    from the Actor's inventory after use.
     *
     * @param actor The Actor who triggered the source (could be null for autonomous sources).
     */
    default void consumeSource(Actor actor) {}
}
