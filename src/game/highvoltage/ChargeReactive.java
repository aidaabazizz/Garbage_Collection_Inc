package game.highvoltage;

import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.positions.Location;

/**
 * A functional interface representing any object in the game world that can
 * respond to a high-voltage galvanic charge.
 *
 * This interface acts as the "Resonator" in the High-Voltage Galvanic System (REQ3).
 * It is designed using the Dependency Inversion Principle (DIP), allowing energy
 * sources (Emitters) to trigger complex reactions in Grounds, Actors, or Items
 * without knowing their concrete classes.
 *
 * Implementations of this interface define structural map changes (morphing),
 * biological transitions (evolution), or physical disruptions (magnetism).
 *
 * @author Jewell Gomes
 */
public interface ChargeReactive {
    /**
     * Defines the specific structural or behavioral change that occurs when
     * this object absorbs a galvanic charge.
     *
     * @param location   The coordinate where the reaction is taking place.
     * @param display    The terminal interface for outputting reaction logs.
     * @param sourceName The name of the energy source (e.g., "Lightning", "Tesla Coil")
     *                   triggering the reaction.
     */
    void reactToCharge(Location location, Display display, String sourceName);
}