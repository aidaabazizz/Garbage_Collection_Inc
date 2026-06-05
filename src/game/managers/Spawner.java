package game.managers;

import edu.monash.fit2099.engine.positions.Location;

/**
 * A contract for classes that handle the creation and deployment of creatures.
 * Facilitates the Dependency Inversion Principle between flora and environmental reactions.
 *
 * @author Jewell Gomes
 * @author Chathya Attanayake
 */
public interface Spawner {
    /**
     * Creates a Slime and handles its associated environmental triggers.
     * @param location The map location for the spawn.
     */
    boolean spawnSlime(Location location);
    /**
     * Creates an Undead and handles its health-buff side effects.
     * @param location The map location for the spawn.
     */
    boolean spawnUndead(Location location);

    /**
     * Creates a Parasite at the specified location and handles its unique side effects.
     * Requirement 4: All workers within its adjacent tiles immediately take 2 points
     * of damage upon the Parasite's emergence.
     *
     * @param location The map location where the Parasite emergence is initiated.
     * @return true if the Parasite was successfully added to the map; false otherwise.
     */
    boolean spawnParasite(Location location);

    /**
     * REQ5: Spawns a CrazyChicken at or near the center location.
     * @param center The preferred spawn location
     * @return true if spawn was successful, false otherwise
     */
    boolean spawnCrazyChicken(Location center);
    
    boolean spawnElsa(Location center);


/**
 * Spawns a ScrapSnatcher and triggers loot explosion on adjacent tiles.
 *
 * @param location The map location where the ScrapSnatcher emerges
 * @return true if spawn was successful, false otherwise
 */
boolean spawnScrapSnatcher(Location location);
}


