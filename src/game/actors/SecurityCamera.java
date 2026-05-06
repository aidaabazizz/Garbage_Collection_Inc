package game.actors;

import game.inventory.BasicInventory;
import game.behaviours.ScanningBehaviour;

/**
 * A stationary monitoring entity designed to trigger the facility security system.
 * This class serves as a trigger mechanism for Requirement 4. It remains fixed
 * at a specific location and continuously monitors its immediate surroundings
 * for unauthorized personnel possessing specific worker capabilities.
 *
 * @author Jewell Gomes
 */
public class SecurityCamera extends NonPlayerCharacter {
    private static final int INITIAL_HEALTH = 999;
    private static final int SCAN_PRIORITY = 1;
    /**
     * Constructor to initialize the security camera with high durability and a monitoring character.
     * The camera is initialized with an empty inventory and a designated display character.
     */
    public SecurityCamera() {
        super("Security Camera", '◉', INITIAL_HEALTH, new BasicInventory());
        this.behaviours.put(SCAN_PRIORITY, new ScanningBehaviour());
    }
}
