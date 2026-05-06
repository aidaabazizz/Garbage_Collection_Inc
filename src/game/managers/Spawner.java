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
    public void spawnSlime(Location location);
    /**
     * Creates an Undead and handles its health-buff side effects.
     * @param location The map location for the spawn.
     */
    public void spawnUndead(Location location);


    //req4
    void spawnParasite(Location location);
}
