package game.highvoltage;

/**
 * A collection of capabilities (tags) used to identify the physical properties of
 * objects within the High-Voltage Galvanic System (REQ3).
 *
 * This enum is the cornerstone of the "Capability System" designed to replace
 * brittle 'instanceof' checks. Instead of checking for a concrete class name
 * (e.g., instanceof PoweredFloor), the system asks if an object possesses a
 * specific MaterialCapability.
 *
 * Best Design Proof:
 * By using these tags, the game can identify what an object "is made of" or
 * "can do" rather than what it is named, strictly adhering to the
 * Dependency Inversion Principle (DIP).
 *
 * @author Jewell Gomes
 */
public enum MaterialCapability {
    /**
     * Identifies a Ground tile or Actor as a conductive power source.
     *
     * Objects with this capability (e.g., PoweredFloor, ElectrifiedPuddle, TeslaCoil)
     * act as "Outlets" that provide continuous power to items in an Actor's inventory
     * (such as the Wallet).
     */
    ENERGIZED,
    /**
     * Identifies an Item as being composed of metallic or ferrous materials.
     *
     * Items with this capability (e.g., Scrap Metal, CRTMonitor, FloppyDisk) are
     * susceptible to magnetic flux and can be physically pulled across the map
     * by a powered Wallet.
     */
    MAGNETIC
}
