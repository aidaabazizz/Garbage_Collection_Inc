package game.highvoltage;

import edu.monash.fit2099.engine.positions.Location;

/**
 * A functional interface representing any object in the game world that can
 * respond to a high-voltage galvanic charge.
 *
 * This interface acts as the "Resonator" component in the High-Voltage Galvanic
 * System (Requirement 3). It is designed using the Dependency Inversion Principle (DIP),
 * allowing energy sources (Emitters) to trigger complex reactions in Grounds,
 * Actors, or Items without being coupled to their concrete classes.
 *
 * Implementations of this interface define a wide variety of effects, including:
 * 1. Structural Morphing: Turning standard terrain (Puddles) into hazardous states.
 * 2. Biological Evolution: Triggering metamorphosis in dormant creatures.
 * 3. Physical Induction: Powering magnetic tools to harvest items remotely.
 * 4. Temporal Refreshment: Resetting the lifespan of temporary electrical hazards.
 *
 * @author Jewell Gomes
 */
public interface ChargeReactive {
    /**
     * Defines the specific structural, biological, or behavioral change that
     * occurs when this object absorbs a galvanic charge.
     *
     * @param location The coordinate where the reaction is taking place. This allows
     *                 the object to modify the map or interact with its surroundings.
     * @param charge   The GalvanicCharge context containing the energy's source name,
     *                 the terminal display for logging, the damage payload, and
     *                 the visited set for recursion prevention.
     */
    void reactToCharge(Location location, GalvanicCharge charge);
}