package game.items;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.Location;
import game.enums.MaterialCapability;

/**
 * Base class for all items that interact with the High-Voltage Galvanic System's
 * magnetic mechanics (Requirement 3).
 *
 * This abstract class centralizes the induction-based locking logic to avoid
 * code duplication across various scrap items (e.g., CRTMonitor, FloppyDisk).
 * It ensures that any item extending this class automatically participates
 * in the magnetic harvesting and locking ecosystem.
 *
 * @author Jewell Gomes
 */
public abstract class MagneticItem extends Item {

    /**
     * Constructor for the MagneticItem.
     * Initializes the item with its basic attributes and automatically enables the
     * {@link MaterialCapability#MAGNETIC} capability, allowing it to be
     * manipulated by electromagnetic induction (e.g., a powered Wallet).
     *
     * @param name        The display name of the item.
     * @param displayChar The character used to represent the item on the map.
     * @param portable    True if the item can be picked up and carried; false otherwise.
     */
    public MagneticItem(String name, char displayChar, boolean portable) {
        super(name, displayChar);
        if (portable) {
            this.makePortable();
        }
        // all magnetic items should have this capability by default
        this.enableAbility(MaterialCapability.MAGNETIC);
    }

    /**
     * Determines the interactions available for this item while it is on the ground.
     *
     * Logic (Requirement 3 - High-Voltage System):
     * If the item possesses the {@link MaterialCapability#MAGNETICALLY_LOCKED} capability,
     * it indicates that the local high-voltage induction (typically from an Ionized Barrier)
     * is too strong for manual retrieval. In this state, an empty ActionList is returned,
     * effectively disabling the 'Pick Up' interaction until the energy dissipates.
     *
     * @param location The current coordinate of the item on the GameMap.
     * @return A list of allowable actions; empty if the item is currently magnetically locked.
     */
    @Override
    public ActionList allowableActions(Location location) {
        if (this.hasAbility(MaterialCapability.MAGNETICALLY_LOCKED)) {
            return new ActionList(); // returns empty list, disabling PickUpAction
        }
        return super.allowableActions(location);
    }
}

