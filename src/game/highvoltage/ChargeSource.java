package game.highvoltage;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.positions.Location;

/**
 * An interface representing any object or system capable of emitting a high-voltage galvanic charge.
 *
 * This interface acts as the "Emitter" component in the High-Voltage Galvanic System (Requirement 3).
 * It defines the contract for releasing energy into the environment and managing the
 * lifecycle of the energy source itself.
 *
 * SOLID Design Proof:
 * This interface utilizes a default method for lifecycle management to avoid 'instanceof'
 * checks or type-casting within the Action classes.
 * 1. Stationary Emitters (Grounds like Tesla Coils) inherit a no-op implementation
 *    as they are permanent features.
 * 2. Portable Emitters (Items like Batteries) override the method to handle their
 *    own depletion or removal from inventory.
 *
 * @author Jewell Gomes
 */
public interface ChargeSource {
    /**
     * Triggers the release of high-voltage energy at a specific map coordinate.
     * Implementations typically use this method to initiate Area-of-Effect (AoE)
     * scans and propagate energy to any {@link ChargeReactive} resonators in the vicinity.
     *
     * @param location The origin point of the energy release.
     * @param charge   The GalvanicCharge context containing source details, display
     *                 interface, and damage payload.
     */
    void releaseCharge(Location location, GalvanicCharge charge);

    /**
     * Handles the cleanup, depletion, or consumption of the energy source after a discharge.
     *
     * Interface Lifecycle Logic:
     * This is a default method designed to satisfy the Single Responsibility Principle (SRP).
     * 1. Ground objects: Inherit the empty body because they are stationary and permanent.
     * 2. Item objects: Override this to handle removal from the Actor's inventory or
     *    reducing a charge counter upon use.
     *
     * @param actor The Actor who triggered the source (may be null for autonomous
     *              sources like lightning).
     */
    default void consumeSource(Actor actor) {}
}
