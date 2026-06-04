package game.enums;

/**
 * A collection of capabilities (tags) used to identify the physical properties of
 * objects within the High-Voltage Galvanic System (REQ3).
 *
 * This enum is the cornerstone of the "Capability System" designed to replace
 * brittle 'instanceof' checks. Instead of checking for a concrete class name
 * (e.g., instanceof PoweredFloor), the system asks if an object possesses a
 * specific MaterialCapability.
 *
 * Design Proof:
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
    MAGNETIC,
    /**
     * Identifies an Item that is currently pinned to the floor by intense
     * high-voltage induction (e.g., from an Ionized Barrier).
     * While an item has this tag, it resists manual pick-up attempts by actors.
     */
    MAGNETICALLY_LOCKED,
    /**
     * Identifies an Actor or Item as capable of conducting electricity.
     *
     * In combat, if a target is CONDUCTIVE, it creates an electrical feedback loop,
     * arcing energy back at the attacker (as seen in the Reflective Surge logic).
     */
    CONDUCTIVE,
    /**
     * Identifies an Actor that is physically incapacitated by an electrical current.
     *
     * Any NPC with this capability will automatically skip its turn in the
     * turn-processing loop, simulating neuromuscular paralysis caused by
     * high-voltage exposure.
     */
    PARALYZED,
    /**
     * Identifies an entity capable of redirecting incoming energy back to its source.
     *
     * When an object with this capability is hit by an electrical attack, it
     * triggers the "Reflective Surge" logic, causing energy to arc back at the
     * attacker and dealing a portion of the incoming damage to them.
     */
    REFLECTIVE
}
